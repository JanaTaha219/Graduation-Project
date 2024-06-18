package com.example.zh2t.service;

import com.example.zh2t.DTOs.UserDTOP;
import com.example.zh2t.models.SecurityUser;
import com.example.zh2t.models.Token2;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {
    private final String SECRET_KEY = "aeaa12f7a9be0225f4dbb418ba835b0b623143b2c8765523d5d7f0b742e917c5";

    @Resource(name="redisTemplate")
    private HashOperations<String, String, Token2> hashOperations;



    @Value("${session.time}")
    private long sessionTime;

    public Token2 getToken(String key){
        return hashOperations.get("Token2", key);
    }
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token){return extractClaim(token, claims -> claims.get("role", String.class));}

    public boolean isValid(String token, SecurityUser user){
        String username = extractUsername(token);
        boolean isLoggedOut = getToken(token).isBlacklisted();
        boolean tokenExist = getToken(token)!=null;
        return (user.getUsername().equals(username) && !isTokenExpire(token) && !isLoggedOut && tokenExist);
    }

    public boolean isTokenExpire(String token) {
        return extractExpirationTime(token).before(new Date());
    }

    private Date extractExpirationTime(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(UserDTOP user){
        return Jwts
                .builder()
                .subject(user.getUniqueName())
                .claim("roles", user.getRole())
                .issuedAt(new Date(System.currentTimeMillis()))  //Unique Mon Apr 15 12:10:55 IDT 2024
                .expiration(new Date(System.currentTimeMillis() + sessionTime))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}