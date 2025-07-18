package com.dgomez.spotify.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(String reason, String error, String message, LocalDateTime timestamp, String path, int status) {}
