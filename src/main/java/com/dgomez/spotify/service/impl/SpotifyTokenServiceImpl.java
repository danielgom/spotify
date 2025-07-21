package com.dgomez.spotify.service.impl;

import com.dgomez.spotify.dto.SpotifyRefreshTokenResponse;
import com.dgomez.spotify.dto.ex.UserException;
import com.dgomez.spotify.model.UserToken;
import com.dgomez.spotify.repository.UserTokenRepository;
import com.dgomez.spotify.service.SpotifyApiService;
import com.dgomez.spotify.service.SpotifyTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class SpotifyTokenServiceImpl implements SpotifyTokenService {

    private final Logger log = LoggerFactory.getLogger(SpotifyTokenServiceImpl.class);

    private final UserTokenRepository userTokenRepository;

    private final SpotifyApiService spotifyApiService;

    @Override
    public String getValidAccessToken(String userID) {
        UserToken userToken = userTokenRepository.findUserTokenByUserID(userID)
                .orElseThrow(() -> new UserException("No token found for user", HttpStatus.UNAUTHORIZED));
        final int secondsBufferToExpire = 300;
        if (userToken.getExpiresAt().isBefore(Instant.now().plusSeconds(secondsBufferToExpire))) {
            return refreshAccessToken(userToken);
        }

        return userToken.getAccessToken();
    }


    @Override
    public void saveUserToken(String userID, OAuth2AuthorizedClient client) {
        UserToken userToken = userTokenRepository.findUserTokenByUserID(userID).orElse(new UserToken());
        userToken.setUserID(userID);
        userToken.setAccessToken(client.getAccessToken().getTokenValue());
        userToken.setRefreshToken(client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null);
        assert client.getAccessToken().getExpiresAt() != null;
        userToken.setExpiresAt(Instant.now().plusSeconds(client.getAccessToken().getExpiresAt().getEpochSecond()));
        userToken.setCreatedAt(Instant.now());

        userTokenRepository.save(userToken);
    }

    private String refreshAccessToken(UserToken userToken) {
        if (userToken.getRefreshToken() == null) {
            throw new InsufficientAuthenticationException("No refresh token - re-auth required");
        }
        try {
            SpotifyRefreshTokenResponse refreshTokenResponse =
                    spotifyApiService.refreshToken(userToken.getRefreshToken());

            this.updateTokensInDatabase(userToken, refreshTokenResponse);

            return refreshTokenResponse.getAccessToken();
        } catch (RestClientException rEx) {
            log.error("Error refreshing access token for user {}", userToken.getUserID(), rEx);
            throw new InsufficientAuthenticationException("Token refresh failed - re-auth required");
        }
    }

    private void updateTokensInDatabase(UserToken userToken, SpotifyRefreshTokenResponse refreshTokenResponse) {
        userToken.setAccessToken(refreshTokenResponse.getAccessToken());
        userToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenResponse.getExpiresIn()));

        if (refreshTokenResponse.getRefreshToken() != null) {
            log.info("Spotify provided new refresh token for user {}", userToken.getUserID());
            userToken.setRefreshToken(refreshTokenResponse.getRefreshToken());
        }

        userToken.setCreatedAt(Instant.now());

        userTokenRepository.save(userToken);

        log.info("Updated tokens for user {} - Access token expires at {}",
                userToken.getUserID(), userToken.getExpiresAt());
    }
}
