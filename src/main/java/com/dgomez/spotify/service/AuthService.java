package com.dgomez.spotify.service;

import com.dgomez.spotify.dto.AuthResponse;

public interface AuthService {

    AuthResponse getAuthInformation(String userID);
}
