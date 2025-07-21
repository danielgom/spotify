package com.dgomez.spotify.service.impl;

import com.dgomez.spotify.dto.SpotifyRefreshTokenResponse;
import com.dgomez.spotify.service.SpotifyApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Service
public class SpotifyApiServiceImpl implements SpotifyApiService {

    private RestClient apiRestClient;

    private RestClient refreshRestClient;

    @Value("${spotify.base-refresh-url}")
    private String refreshTokenPath;

    @Value("${spotify.base-url}")
    private String baseSpotifyUrl;

    @Value("${spring.security.oauth2.client.registration.spotify.client-id}")
    private String clientID;

    @Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
    private String clientSecret;

    @Override
    public SpotifyRefreshTokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        return this.refreshRestClient().post()
                .header("Authorization", this.getAuthorizationHeader())
                .body(body)
                .retrieve()
                .body(SpotifyRefreshTokenResponse.class);
    }

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes());
    }

    private RestClient refreshRestClient() {
        if (refreshRestClient == null) {
            refreshRestClient = RestClient.builder()
                    .defaultHeader("Content-Type", "application/x-www-form-urlencoded")
                    .baseUrl(this.refreshTokenPath)
                    .build();
        }

        return refreshRestClient;
    }

    private RestClient apiRestClient() {
        if (apiRestClient == null) {
            RestClient.builder()
                    .defaultHeader("Content-Type", "application/json")
                    .baseUrl(this.baseSpotifyUrl)
                    .build();
        }

        return apiRestClient;
    }
}
