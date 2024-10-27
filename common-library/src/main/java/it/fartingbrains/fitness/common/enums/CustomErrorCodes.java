package it.fartingbrains.fitness.common.enums;

public enum CustomErrorCodes {
    USERNAME_ALREADY_EXISTS("ERR001"),
    EMAIL_ALREADY_EXISTS("ERR002");

    private final String code;

    CustomErrorCodes(String code) { this.code = code; }

    public String getCode() { return code; }
}
