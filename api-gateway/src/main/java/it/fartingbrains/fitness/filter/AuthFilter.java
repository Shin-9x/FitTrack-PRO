package it.fartingbrains.fitness.filter;

import it.fartingbrains.fitness.common.constant.AuthConstants;
import it.fartingbrains.fitness.common.feign.AuthServiceFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter {
    private final Logger _log = LoggerFactory.getLogger(AuthFilter.class);

    private final AuthServiceFeignClient _authServiceFeignClient;

    public AuthFilter(@Lazy AuthServiceFeignClient authServiceFeignClient) {
        this._authServiceFeignClient = authServiceFeignClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(_log.isDebugEnabled()) { _log.debug("[filter] Global filter Executed"); }

        String requestPath = exchange.getRequest().getURI().getPath();

        if(_log.isDebugEnabled()) { _log.debug("[filter] RequestPath: {}", requestPath); }

        if(requestPath == null) {
            if(_log.isErrorEnabled()) { _log.error("[filter] RequestPath NULL"); }
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }

        if (AuthConstants.NO_AUTH_API_PATHS.stream().noneMatch(requestPath::contains)) {
            return _validateToken(exchange, chain);
        }

        if (_log.isDebugEnabled()) { _log.debug("[filter] Skipping authentication for no-auth endpoint."); }
        return chain.filter(exchange);
    }

    private Mono<Void> _validateToken(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(_log.isDebugEnabled()) { _log.debug("[validateToken] Start Token Validation"); }

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.startsWith(AuthConstants.BEARER_PLUS_SPACE)) {
            String token = authorization.substring(AuthConstants.BEARER_PLUS_SPACE.length());

            if (_log.isDebugEnabled()) { _log.debug("[validateToken] Bearer token received: {}", token); }

            return _authServiceFeignClient.validateToken(token)
                    .flatMap(isValid -> {
                        if (_log.isDebugEnabled()) { _log.debug("[validateToken] Is Token Valid: {}", isValid); }

                        if (Boolean.TRUE.equals(isValid)) {
                            return chain.filter(exchange);
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }
                    })
                    .onErrorResume(throwable -> {
                        if (_log.isErrorEnabled()) {
                            _log.error("[validateToken] Token Validation Service UNAVAILABLE", throwable);
                        }
                        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        return exchange.getResponse().setComplete();
                    });
        }

        if (_log.isWarnEnabled()) { _log.warn("[validateToken] No Bearer token found in request."); }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
