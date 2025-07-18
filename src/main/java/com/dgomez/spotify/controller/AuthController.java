package com.dgomez.spotify.controller;

import com.dgomez.spotify.dto.AuthResponse;
import com.dgomez.spotify.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/spotify")
    public ResponseEntity<AuthResponse> handleSpotifyCallback(Authentication authentication) {
        return ResponseEntity.ok(authService.getAuthInformation(authentication.getName()));
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }

}
