package it.fartingbrains.fitness.rest;

import it.fartingbrains.fitness.common.annotation.Loggable;
import it.fartingbrains.fitness.common.constant.AuthConstants;
import it.fartingbrains.fitness.common.enums.CustomErrorCodes;
import it.fartingbrains.fitness.common.util.CommonUtils;
import it.fartingbrains.fitness.entity.User;
import it.fartingbrains.fitness.pojo.Token;
import it.fartingbrains.fitness.rest.dto.LoginRequest;
import it.fartingbrains.fitness.rest.dto.LoginResponse;
import it.fartingbrains.fitness.service.TokenService;
import it.fartingbrains.fitness.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AuthConstants.BASE_PATH)
public class AuthController {
    private static final Logger _log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(AuthenticationManager authManager, TokenService tokenService, UserService userService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Loggable
    @PostMapping(AuthConstants.REFRESH_ACCESS_TOKEN_PATH)
    public Mono<ResponseEntity<?>> refresh(@RequestBody String refreshToken) {
        return Mono.defer(() -> {
            String errorMessage;

            if (tokenService.validateToken(refreshToken)) {
                Token newAccessToken = tokenService.refreshAccessToken(refreshToken);

                if(newAccessToken == null) {
                    errorMessage = "[refresh] Unable to obtain new Access Token";
                    _log.error(errorMessage);
                    return CommonUtils.createErrorResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return Mono.just(ResponseEntity.ok(newAccessToken));
            } else {
                errorMessage = String.format("[refresh] RefreshToken [%s] invalid or expired", refreshToken);
                _log.error(errorMessage);
                return CommonUtils.createErrorResponse(errorMessage, HttpStatus.UNAUTHORIZED);
            }
        });
    }

    @Loggable
    @PostMapping(AuthConstants.LOGIN_PATH)
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest loginRequest) {
        return Mono.defer(() -> {
            String errorMessage;

            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            if(username == null || password == null) {
                errorMessage = "[login] Username or password null.";
                _log.error(errorMessage);
                return CommonUtils.createErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
            }

            if(userService.findByUsername(username) == null) {
                errorMessage = String.format("[login] Username [%s] not found.", username);
                _log.error(errorMessage);
                return CommonUtils.createErrorResponse(errorMessage, HttpStatus.NOT_FOUND);
            }

            try {
                Authentication auth = authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );

                if (auth.isAuthenticated()) {
                    _log.info("[login] User {} authenticated.", username);

                    Token accessToken = tokenService.generateAccessToken(auth);
                    Token refreshToken = tokenService.generateRefreshToken(username);

                    return Mono.just(ResponseEntity.ok(new LoginResponse(accessToken, refreshToken)));
                } else {
                    errorMessage = "[login] Password not recognized.";
                    _log.error(errorMessage);
                    return CommonUtils.createErrorResponse(errorMessage, HttpStatus.UNAUTHORIZED);
                }
            } catch (AuthenticationException ex) {
                errorMessage = String.format("[login] Error during authenticate user [%s]", username);
                _log.error(errorMessage, ex);
                return CommonUtils.createErrorResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @Loggable
    @PostMapping(AuthConstants.REGISTER_PATH)
    public Mono<ResponseEntity<?>> register(@RequestBody User user) {
        return Mono.defer(() -> {
            String errorMessage;

            String username = user.getUsername();
            String password = user.getPassword();
            String email = user.getEmail();

            if(username == null || password == null || email == null) {
                errorMessage = "[register] Username, or password, or email null.";
                _log.error(errorMessage);
                return CommonUtils.createErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
            }

            if(userService.findByUsername(username) != null) {
                errorMessage = String.format("[register] Username [%s] already taken.", username);
                _log.error(errorMessage);
                return CommonUtils.createErrorResponse(
                        errorMessage, CustomErrorCodes.USERNAME_ALREADY_EXISTS.getCode(), HttpStatus.CONFLICT
                );
            }

            if(userService.findByEmail(email) != null) {
                errorMessage = String.format("[register] Email [%s] already taken.", email);
                _log.error(errorMessage);
                return CommonUtils.createErrorResponse(
                        errorMessage, CustomErrorCodes.EMAIL_ALREADY_EXISTS.getCode(), HttpStatus.CONFLICT
                );
            }

            try {
                userService.saveUser(user);
                return Mono.just(ResponseEntity.ok("Registration Successfully!"));
            } catch (Exception e) {
                errorMessage = String.format("[register] Error saving user [%s]", user);
                _log.error(errorMessage, e);
                return CommonUtils.createErrorResponse(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @GetMapping(AuthConstants.VALIDATE_PATH)
    public Mono<Boolean> validateToken(@RequestParam String token) {
        return Mono.just(tokenService.validateToken(token));
    }

    @Loggable
    @GetMapping(AuthConstants.FETCH_USER_PATH)
    public Mono<ResponseEntity<?>> getUser(@RequestHeader HttpHeaders headers) {
        return Mono.defer(() -> {
            String username = tokenService.getUsernameFromHeader(headers);

            String errorMessage;

            if(username == null) {
                errorMessage = "[getUser] Unable to retrieve username from header";
                _log.error(errorMessage);
                return CommonUtils.createErrorResponse(errorMessage, HttpStatus.NOT_FOUND);
            }

            User user = userService.findByUsername(username);

            if(user == null) {
                errorMessage = String.format("[getUser] User [%s] not found.", username);
                _log.error(errorMessage);
                return CommonUtils.createErrorResponse(errorMessage, HttpStatus.NOT_FOUND);
            }

            return Mono.just(ResponseEntity.ok(User.toBE(user)));
        });
    }
}
