package it.fartingbrains.fitness.rest;

import it.fartingbrains.fitness.common.annotation.Loggable;
import it.fartingbrains.fitness.common.constant.AuthConstants;
import it.fartingbrains.fitness.entity.User;
import it.fartingbrains.fitness.service.TokenService;
import it.fartingbrains.fitness.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @PostMapping(AuthConstants.LOGIN_PATH)
    public Mono<ResponseEntity<String>> login(@RequestBody it.fartingbrains.fitness.rest.dto.LoginRequest loginRequest) {
        return Mono.defer(() -> {
            try {
                Authentication auth = authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );

                if (auth.isAuthenticated()) {
                    String token = tokenService.generateToken(auth); // Genera il token in modo sincrono
                    return Mono.just(ResponseEntity.ok(token));
                } else {
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized"));
                }
            } catch (AuthenticationException ex) {
                // Log exception if needed
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized"));
            }
        });
    }


    @Loggable
    @PostMapping(AuthConstants.REGISTER_PATH)
    public Mono<ResponseEntity<?>> register(@RequestBody User user) {
        return Mono.defer(() -> {
            if (user.getUsername() != null && user.getPassword() != null && user.getEmail() != null) {
                userService.saveUser(user);
                return Mono.just(ResponseEntity.ok("Registration Successfully!"));
            }
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        });
    }

    @GetMapping(AuthConstants.VALIDATE_PATH)
    public Mono<Boolean> validateToken(@RequestParam String token) {
        return Mono.just(tokenService.validateToken(token));
    }

    @Loggable
    @GetMapping("/pippo")
    public Mono<String> getPippo() {
        _log.info("################## GET PIPPO #################");
        return Mono.just("DAJE ROMA DAJE");
    }
}
