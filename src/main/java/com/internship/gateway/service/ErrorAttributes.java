package com.internship.gateway.service;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

public class ErrorAttributes extends DefaultErrorAttributes {

    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options){
        return super.getErrorAttributes((ServerRequest) request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));
    }
}
