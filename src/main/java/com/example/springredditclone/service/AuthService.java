package com.example.springredditclone.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.example.springredditclone.dto.AuthenticationResponese;
import com.example.springredditclone.dto.LoginRequest;
import com.example.springredditclone.dto.RefreshTokenRequest;
import com.example.springredditclone.exceptions.SpringRedditException;
import com.example.springredditclone.security.JwtProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springredditclone.dto.RegisterRequest;
import com.example.springredditclone.model.NotificationEmail;
import com.example.springredditclone.model.User;
import com.example.springredditclone.model.VerificationToken;
import com.example.springredditclone.repository.UserRepository;
import com.example.springredditclone.repository.VerificationTokenRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final MailService mailService;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RefreshTokenService refreshTokenService;

	@Transactional
	public void signup(RegisterRequest registerRequest) {	//Ánh xạ đối tượng RegisterRequest tới đối tượng User
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));  //Mã hóa password trước khi lưu vào csdl
		user.setCreated(Instant.now());
		user.setEnabled(false);		//Vô hiệu hóa người dùng ngay sau khi đăng ký, kích hoạt khi xác minh địa chỉ email của người dùng
		userRepository.save(user); 	//Lưu đối tượng người dùng vào csdl

		//Gọi hàm Tạo token và lưu vào csdl và nhận về mã token truyền vào biến String
		String token = generateVerificationToken(user);

		mailService.sendMail(new NotificationEmail("Please Activate Your Account",		//Tiêu đề email (subject)
				user.getEmail(),														//Người nhận (recipient)
				"Thank you for signing up to Spring Reddit, " +							//Nội dung (body)
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
	}

	// Tạo ngẫu nhiên mã token và lưu vào csdl với foreign key là user
	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();	//Tạo chuỗi ngẫu nhiên UUID làm mã Token
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationTokenRepository.save(verificationToken);  //lưu đối tượng mã xác minh(token) vào csdl
		return token;
	}

	// Nhận về mã token bên controller, tìm token tương ứng với user nào và xử lý
	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);		//Tìm mã Token
		verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));	//Nếu mã token không tồn tại sẽ ném ngoại lệ
		fetchUserAndEnable(verificationToken.get());	//Truyền token cho method(fetchUserAndEnable) để kích hoạt user
	}

	// Private ?
	// Tìm User thông qua mã token liên kết và kích hoạt User đó, update user
	@Transactional
	public void fetchUserAndEnable(VerificationToken verificationToken) {
		String username = verificationToken.getUser().getUsername();	//Nhận được username liên kết với mã token này
		User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name - "+ username));
		user.setEnabled(true);
		userRepository.save(user);
	}

	// Login ???
    public AuthenticationResponese login(LoginRequest loginRequest) {
		// Xác thực username và password xem user nào đăng login
		// AuthenticationManager đảm nhiệm phần xác thực khi sử dụng Spring Security
		Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		// Set thông tin authentication vào Security Context, Nếu không xảy ra exception tức là thông tin hợp lệ
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		String jwt = jwtProvider.generateToken(authenticate);			//Tạo Jwt cho User đăng login
		return AuthenticationResponese.builder()
				.authenticationToken(jwt)
				.refreshToken(refreshTokenService.generateRefreshToken().getToken())	//Tạo mới RefreshToken - Truyền vào mã Refresh token mới
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))	//Set time hết hạn
				.username(loginRequest.getUsername())
				.build();
    }

    // PostService ??
	// Tim userName người Đăng bài (Post) ??
	// Tìm User đăng login
	@Transactional(readOnly = true)
	public User getCurrentUser() {
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
				getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
	}

	//	Tạo Jwt mới mỗi khi hết hạn
	public AuthenticationResponese refreshToken(RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
		String jwt = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
		return AuthenticationResponese.builder()
				.authenticationToken(jwt)
				.refreshToken(refreshTokenRequest.getRefreshToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
				.username(refreshTokenRequest.getUsername())
				.build();
	}
	// Vote (PostMapper)
	// Spring Security kiểm tra người dùng đã login? xác thực? hay chưa
	// Method này true nếu đã login và ngược lại
	public boolean isLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
	}
}
