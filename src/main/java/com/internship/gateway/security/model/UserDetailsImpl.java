package com.internship.gateway.security.model;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toList;


@RequiredArgsConstructor
public class UserDetailsImpl implements UserDataContainer {
    private final String userId;
    private final String username;
    private final Set<String> roles;

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    public String getUserId() {
        return this.userId;
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
    }

    @Override
    public String getPassword() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
