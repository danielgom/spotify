package com.dgomez.spotify.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

public interface SpotifyTokenService {

    String getValidAccessToken(String userID);

    void saveUserToken(String userID, OAuth2AuthorizedClient client);
}
