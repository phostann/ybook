package com.example.ybook.security;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * 当前登录用户的精简信息。
 */
public class CurrentUser implements Serializable {
    private final Long id;
    private final String username;
    private final Set<String> roles;

    public CurrentUser(Long id, String username, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(roles);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }
}

