package com.internship.gateway.config;

import com.internship.gateway.security.model.AccountAuthenticationProvider;
import com.internship.gateway.security.model.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;



@Configuration
@RequiredArgsConstructor
public class ApplicationSecurityConfig {
    private final AccountAuthenticationProvider provider;

    @Bean
    protected SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http.httpBasic(Customizer.withDefaults())
                .headers(headerSpec ->
                        headerSpec.contentSecurityPolicy(contentSecurityPolicySpec ->
                                contentSecurityPolicySpec.policyDirectives("upgrade-insecure-requests")))
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(requests -> {
                    //actuator
                    requests.pathMatchers("/actuator/**").permitAll();
                    //admin
                    requests.pathMatchers("/admin/**").permitAll();
                    //keycloak
                    requests.pathMatchers("/openid-connect/**").permitAll();
                    requests.pathMatchers("/api/external/**").permitAll();
                    // portal
                    // Создание пользователя
                    requests.pathMatchers(HttpMethod.POST, "/portal/v1/user").permitAll();
                    // Восстановление пароля
                    requests.pathMatchers(HttpMethod.GET, "/portal/v1/password/reset-request/{login}").permitAll();
                    requests.pathMatchers(HttpMethod.POST, "/portal/v1/password").permitAll();
                    // Смена пароля
                    requests.pathMatchers(HttpMethod.PUT, "/portal/v1/password").authenticated();
                    // Создание своей организации
                    requests.pathMatchers(HttpMethod.POST, "/portal/v1/company/**").hasAnyAuthority(Authority.REGISTRATOR.name());
                    // Обновление своей организации
                    requests.pathMatchers(HttpMethod.PUT, "/portal/v1/company/**").hasAnyAuthority(Authority.REGISTRATOR.name());
                    // Удаление своей организации
                    requests.pathMatchers (HttpMethod.DELETE, "/portal/v1/company/{id}").hasAnyAuthority(Authority.REGISTRATOR.name());
                    requests.pathMatchers ("/portal/v1/**").hasAnyAuthority (Authority.LOGIST.name(), Authority.ADMIN.name(), Authority.REGISTRATOR.name());

                    // org
                    // Просмотр карточки организации
                    requests.pathMatchers (HttpMethod.GET, "/org/v1/company/**").hasAnyAuthority (Authority. REGISTRATOR.name(), Authority.ADMIN.name(), Authority.LOGIST.name());
                    // Просмотр карточки организации
                    requests.pathMatchers (HttpMethod.GET, "/org/v1/partner/{id}").hasAnyAuthority (Authority.ADMIN.name(), Authority.LOGIST.name());
                    // Удаление
                    requests.pathMatchers (HttpMethod.DELETE, "/org/v1/**").hasAnyAuthority (Authority.ADMIN.name());
                    requests.pathMatchers ("/org/v1/integration/***").denyAll();
                    requests.pathMatchers ("/org/v1/**").hasAnyAuthority(Authority.LOGIST.name(), Authority.ADMIN.name(), Authority.REGISTRATOR.name());
                    // dwh
                    requests.pathMatchers ("/dwh/v1/dashboard/**").hasAnyAuthority (Authority.ADMIN.name(), Authority.LOGIST.name());
                    // drv
                    requests.pathMatchers ("/drv/v1/**").hasAnyAuthority(Authority.DRIVER.name());
                    requests.pathMatchers ("/drv/test/**").hasAnyAuthority(Authority.ADMIN.name());
                    // zkz
                    // Удаление заявки организации
                    requests.pathMatchers ("/zkz/11/integration/**").denyAll();
                    requests.pathMatchers (HttpMethod.DELETE,"/zkz/v1/**").hasAnyAuthority (Authority.ADMIN.name());
                    requests.pathMatchers ("/zkz/v1/**"). hasAnyAuthority (Authority.LOGIST.name());
//                    // nsi
//                    requests.pathMatchers("/nsi/v1/**").authenticated();
//                    //swagger
//                    requests.pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**", "/favicon.ico").permitAll();
//                    requests.pathMatchers("/drv/v3/api-docs").permitAll();
//                    requests.pathMatchers("/portal/v3/api-docs").permitAll();
//                    requests.pathMatchers("/org/v3/api-docs").permitAll();
//                    requests.pathMatchers("/dwh/v3/api-docs").permitAll();
//                    requests.pathMatchers("/nsi/v3/api-docs").permitAll();
//                    requests.pathMatchers("/zkz/v3/api-docs").permitAll();
//                    requests.pathMatchers("/").permitAll();
                })

                .oauth2ResourceServer(resourceServerConfigurer -> resourceServerConfigurer
                        .authenticationManagerResolver(context -> Mono.just(provider))
                ).build();
    }
}
