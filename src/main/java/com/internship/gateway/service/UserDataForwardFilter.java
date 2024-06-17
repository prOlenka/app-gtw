package com.internship.gateway.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.gateway.security.model.UserDataContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataForwardFilter implements GlobalFilter {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .flatMap(c -> {
                    UserDataContainer user = (UserDataContainer) c.getAuthentication().getPrincipal();
                    ServerHttpRequest request = getModifiedRequest(exchange, user);
                    return chain.filter(exchange.mutate().request(request).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private ServerHttpRequest getModifiedRequest(ServerWebExchange exchange, UserDataContainer user){
        try {
            return exchange.getRequest().mutate()
                    .header("x-userName", user.getUsername())
                    .header("x-userId", user.getUserId())
                    .header("x-roles", objectMapper.writeValueAsString(user.getRoles()))
                    .build();
        }catch (JsonProcessingException e){
            log.error("roles serialization error");
            throw new RuntimeException();
        }
    }

}
