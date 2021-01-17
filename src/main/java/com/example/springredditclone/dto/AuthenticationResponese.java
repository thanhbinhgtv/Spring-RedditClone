package com.example.springredditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponese {
    private String authenticationToken;
    private String refreshToken;    // New for Refresh token
    private Instant expiresAt;      // New for time hết hạn token
    private String username;
}
