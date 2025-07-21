package com.dgomez.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthResponse(String message, @JsonProperty("user_id") String userID, boolean authenticated) {
}
