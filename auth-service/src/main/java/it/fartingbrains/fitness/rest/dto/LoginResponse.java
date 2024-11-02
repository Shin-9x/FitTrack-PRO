package it.fartingbrains.fitness.rest.dto;

import it.fartingbrains.fitness.pojo.Token;

public class LoginResponse {
    private final Token accessToken;
    private final Token refreshToken;

    public LoginResponse(Token accessToken, Token refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Token getAccessToken() { return accessToken; }
    public Token getRefreshToken() { return refreshToken; }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "accessToken=" + accessToken +
                ", refreshToken=" + refreshToken +
                '}';
    }
}
