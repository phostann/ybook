package com.example.ybook.security;

import com.example.ybook.entity.UserEntity;
import com.example.ybook.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 用户详情服务实现
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userService.lambdaQuery().eq(UserEntity::getUsername, username).one();
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        boolean enabled = entity.getStatus() == null || "1".equals(entity.getStatus());
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(entity.getUsername(), entity.getPassword(), enabled, true, true, true, authorities);
    }
}
