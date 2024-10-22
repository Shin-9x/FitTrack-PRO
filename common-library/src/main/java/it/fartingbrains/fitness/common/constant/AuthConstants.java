package it.fartingbrains.fitness.common.constant;

import java.util.List;

public class AuthConstants {
    public static final String RSA_PREFIX = "rsa";

    public static final String BEARER = "Bearer";
    public static final String SPACE = " ";
    public static final String BEARER_PLUS_SPACE = BEARER + SPACE;

    public static final String BASE_PATH = "/auth";
    public static final String LOGIN_PATH = "/login";
    public static final String REGISTER_PATH = "/register";
    public static final String VALIDATE_PATH = "/validate";

    public static final List<String> NO_AUTH_API_PATHS = List.of(
            BASE_PATH + LOGIN_PATH,
            BASE_PATH + REGISTER_PATH,
            BASE_PATH + VALIDATE_PATH
    );
}
