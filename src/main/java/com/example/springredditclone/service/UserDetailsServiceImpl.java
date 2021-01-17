package com.example.springredditclone.service;

import com.example.springredditclone.model.User;
import com.example.springredditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
// Lớp này lưu trữ thông tin người dùng khi họ login vào hệ thống
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    // Lớp này Override phương thức loadUserByUsername(), được Spring Security sử dụng để tìm nạp chi tiết người dùng
    public UserDetails loadUserByUsername(String username) {
        // Đầu tiên query xuống csdl xem có username này không
        // Lấy tên người dùng làm đầu vào, trả về đối tượng chi tiết người dùng
        Optional<User> userOptional = userRepository.findByUsername(username);
        // Nếu không có thì thông báo lỗi
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("No user " + "Found with username : " + username));
        // Cấp quyền cho người dùng
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                user.isEnabled(), true, true,
                true, getAuthorities("USER"));
    }
    // Cấp quyền cho người dùng ???
    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return singletonList(new SimpleGrantedAuthority(role));
    }
}