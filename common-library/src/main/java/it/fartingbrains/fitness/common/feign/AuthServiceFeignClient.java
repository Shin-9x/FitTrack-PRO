package it.fartingbrains.fitness.common.feign;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "auth-service")
public interface AuthServiceFeignClient {

    @GetMapping("/auth/validate")
    Mono<Boolean> validateToken(@RequestParam("token") String token);
}
