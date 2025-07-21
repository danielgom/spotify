package com.dgomez.spotify.service;

import com.dgomez.spotify.dto.SpotifyRefreshTokenResponse;

public interface SpotifyApiService {

    SpotifyRefreshTokenResponse refreshToken(String refreshToken);
}
