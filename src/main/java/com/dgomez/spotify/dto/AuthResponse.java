package com.dgomez.spotify.dto;

import lombok.Builder;

@Builder
public record AuthResponse(String accessToken,
                           String refreshToken,
                           long expiresIn,
                           String tokenType,
                           String scope
) {
}
