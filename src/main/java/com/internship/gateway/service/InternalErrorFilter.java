package com.internship.gateway.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalErrorFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(()-> {
                    ServerHttpResponse response = exchange.getResponse();
                    if(Objects.equals(response.getStatusCode(), HttpStatusCode.valueOf(500))){
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            throw new RuntimeException(e); //TODO
                        }
                    }
                }));
    }
}
