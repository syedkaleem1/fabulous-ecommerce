package com.fabulous.orchestrator.service;

import com.fabulous.orchestrator.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MyCustomUserService implements UserDetails {

    private String email; // Changed from 'name' to 'username' for clarity
    private String password;
    private List<GrantedAuthority> authorities;

    public MyCustomUserService(User user) {
        this.email = user.getEmail(); // Assuming 'name' is used as 'username'
        this.password = user.getPasswordHash();
        this.authorities = List.of(user.getRole().name())
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement your logic if you need this
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement your logic if you need this
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement your logic if you need this
    }

    @Override
    public boolean isEnabled() {
        return true; // Implement your logic if you need this
    }
}
