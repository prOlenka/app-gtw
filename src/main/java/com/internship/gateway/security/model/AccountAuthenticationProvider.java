package com.internship.gateway.security.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;


@Slf4j
@Component
public class AccountAuthenticationProvider implements ReactiveAuthenticationManager {
    private static final String USERNAME_CLAIM = "preferred_username";
    private static final String ROLES_CLAIM = "roles";
    private static final String REAL_ACCESS_CLAIM = "realm_access";
    private static final String USER_ID_CLAIM = "sub";
    private final ReactiveJwtDecoder jwtDecoder;

    public AccountAuthenticationProvider(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
        BearerTokenAuthenticationToken authenticationToken = (BearerTokenAuthenticationToken) authentication;

        return getJwt(authenticationToken).map(jwt -> {
            String userId = jwt.getClaimAsString(USER_ID_CLAIM);
            String username = jwt.getClaimAsString(USERNAME_CLAIM);
            return new UserDetailsImpl(userId, username, getRolesFromJwt(jwt));
        })
                .map(userDetails -> (Authentication) new UsernamePasswordAuthenticationToken(
                        userDetails,
                        authenticationToken.getCredentials(),
                        userDetails.getAuthorities()
                ))
                .doOnError(throwable -> {
//                    throw new CommonHttpStatusExeption(HttpStatusCode.valueOf(401), throwable.getMassage()); //TODO
                });
    }


    private Set<String> getRolesFromJwt(Jwt jwt) {
        Map< String, Object > claim = Optional.ofNullable(jwt.getClaimAsMap(REAL_ACCESS_CLAIM))
                .orElse(new HashMap<>());
        List<String> rolesList = (List<String>) Optional.ofNullable(claim.get(ROLES_CLAIM))
                .orElse(new ArrayList<>());
        return new HashSet<>(rolesList);
    }

    private Mono<Jwt> getJwt(BearerTokenAuthenticationToken bearer) {
        try{
            return this.jwtDecoder.decode(bearer.getToken());
        } catch(JwtException failed){
            throw new AuthenticationServiceException(failed.getMessage(), failed);

        }
    }
}
