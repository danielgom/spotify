package com.dgomez.spotify.service.impl;

import com.dgomez.spotify.dto.AuthResponse;
import com.dgomez.spotify.dto.ex.UserException;
import com.dgomez.spotify.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public AuthResponse getAuthInformation(String userID) {
        log.info("Retrieving auth information for user {}", userID);
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient("spotify", userID);

        if (client == null) {
            throw new UserException("No authorized client found, no OAUTH2 token found", HttpStatus.BAD_REQUEST);
        }

        String accessToken = client.getAccessToken().getTokenValue();
        String refreshToken = client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null;
        String scope = String.join(" ", client.getAccessToken().getScopes());
        long expiresIn = client.getAccessToken().getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();

        log.info("Retrieved auth information for user {}", userID);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .scope(scope)
                .build();
    }
}
