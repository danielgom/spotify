package com.dgomez.spotify.service.impl;

import com.dgomez.spotify.dto.AuthResponse;
import com.dgomez.spotify.service.AuthService;
import com.dgomez.spotify.service.SpotifyTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final OAuth2AuthorizedClientService authorizedClientService;

    private final SpotifyTokenService spotifyTokenService;

    @Override
    public AuthResponse getAuthInformation(String userID) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient("spotify", userID);

        if (client == null) {
            log.warn("No authorized client found for user {}. Triggering re-authentication", userID);
            throw new InsufficientAuthenticationException("No authorized client found, re-authentication required");
        }

        spotifyTokenService.saveUserToken(userID, client);

        if (client.getRefreshToken() != null && client.getAccessToken() != null) {
            return AuthResponse.builder()
                    .message("Authentication successful")
                    .userID(userID)
                    .authenticated(true)
                    .build();
        }

        throw new InsufficientAuthenticationException("No access token or refresh token found");
    }
}
