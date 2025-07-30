package com.luppol.life_balance.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record AuthUser (
        Long personId,
        String email,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {
    public AuthUser(Long personId, String email) {
        this(personId, email, List.of()); // List.of(new SimpleGrantedAuthority("ROLE_USER"))
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() { return ""; }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
