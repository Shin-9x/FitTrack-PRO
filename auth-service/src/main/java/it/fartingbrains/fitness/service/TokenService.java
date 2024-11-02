package it.fartingbrains.fitness.service;

import it.fartingbrains.fitness.pojo.Token;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;

    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserService userService) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
    }

    public Token generateAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(1, ChronoUnit.HOURS);

        String scope = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expirationTime)
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        String generatedToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new Token(generatedToken, now, expirationTime);
    }

    public Token generateRefreshToken(String username) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(30, ChronoUnit.DAYS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expirationTime)
                .subject(username)
                .claim("type", "refresh")
                .build();

        String generatedToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new Token(generatedToken, now, expirationTime);
    }

    public boolean validateToken(String token) {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(token);
        } catch (JwtException e) {
            return false;
        }

        Instant expiration = jwt.getExpiresAt();

        return expiration == null || !expiration.isBefore(Instant.now());
    }

    public Token refreshAccessToken(String refreshToken) {
        //TODO: make a better management
        Jwt jwt = jwtDecoder.decode(refreshToken);

        if (!jwt.getClaim("type").equals("refresh")) {
            return null;
        }

        String username = jwt.getSubject();

        if(username == null) {
            return null;
        }

        UserDetails user = userService.loadUserByUsername(username);

        if(user == null) {
            return null;
        }

        return generateAccessToken(new UsernamePasswordAuthenticationToken(username, user.getPassword()));
    }
}
