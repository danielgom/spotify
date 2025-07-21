package com.dgomez.spotify.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Entity
@Setter
@Getter
@Table(name = "user_tokens")
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userID;

    @Column(unique = true, nullable = false)
    private String accessToken;

    @Column(unique = true, nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserToken userToken = (UserToken) o;
        return Objects.equals(id, userToken.id) &&
                Objects.equals(userID, userToken.userID) &&
                Objects.equals(accessToken, userToken.accessToken) &&
                Objects.equals(refreshToken, userToken.refreshToken) &&
                Objects.equals(expiresAt, userToken.expiresAt) && Objects.equals(createdAt, userToken.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userID, accessToken, refreshToken, expiresAt, createdAt);
    }
}
