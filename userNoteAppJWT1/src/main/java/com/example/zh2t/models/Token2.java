package com.example.zh2t.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Token")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Token2 implements Serializable {
    @Id
    private String id;
    private boolean blacklisted;

    public Token2(String id) {
        this.id = id;
        this.blacklisted = false;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }
}
