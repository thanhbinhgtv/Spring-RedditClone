package com.example.springredditclone.service;

import com.example.springredditclone.exceptions.SpringRedditException;
import com.example.springredditclone.model.RefreshToken;
import com.example.springredditclone.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Tạo mới chuỗi ngẫu nhiên RefreshToken và lưu vào csdl
    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());
        return refreshTokenRepository.save(refreshToken);
    }

    // Tìm kiếm, xác thực mã Token
    // Tiếp theo, chúng ta có validateRefreshToken () truy vấn DB với mã thông báo đã cho.
    // Nếu không tìm thấy mã thông báo, nó sẽ ném ra một ngoại lệ với thông báo - "Mã làm mới không hợp lệ"
    void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).orElseThrow(() -> new SpringRedditException("Invalid refresh Token"));
    }

    // Xóa mã Token làm mới khỏi csdl khi logout
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}