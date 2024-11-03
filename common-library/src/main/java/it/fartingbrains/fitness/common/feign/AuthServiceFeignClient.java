package it.fartingbrains.fitness.common.feign;

import it.fartingbrains.fitness.common.constant.AuthConstants;
import it.fartingbrains.fitness.common.dto.UserBE;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "auth-service")
public interface AuthServiceFeignClient {
    @GetMapping(AuthConstants.BASE_PATH + AuthConstants.VALIDATE_PATH)
    Mono<Boolean> validateToken(@RequestParam("token") String token);

    @GetMapping(AuthConstants.BASE_PATH + AuthConstants.FETCH_USER_PATH)
    Mono<UserBE> getUser(@RequestHeader HttpHeaders headers);
}
