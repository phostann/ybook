package com.example.ybook.service.impl;

import com.example.ybook.common.ApiCode;
import com.example.ybook.dto.ChangePasswordRequestDTO;
import com.example.ybook.dto.LoginRequestDTO;
import com.example.ybook.dto.LoginResponse;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.exception.BizException;
import com.example.ybook.security.JwtService;
import com.example.ybook.service.AuthService;
import com.example.ybook.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 认证服务实现
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           UserService userService,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequestDTO request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            if (ex instanceof BadCredentialsException) {
                throw new BizException(ApiCode.BAD_REQUEST, "用户名或密码错误");
            } else if (ex instanceof DisabledException) {
                throw new BizException(ApiCode.USER_DISABLED);
            } else if (ex instanceof LockedException) {
                throw new BizException(ApiCode.FORBIDDEN, "账户已锁定");
            } else if (ex instanceof AccountExpiredException) {
                throw new BizException(ApiCode.FORBIDDEN, "账户已过期");
            } else if (ex instanceof CredentialsExpiredException) {
                throw new BizException(ApiCode.UNAUTHORIZED, "凭证已过期");
            }
            throw new BizException(ApiCode.UNAUTHORIZED, "认证失败");
        }
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        UserEntity entity = userService.lambdaQuery().eq(UserEntity::getUsername, principal.getUsername()).one();
        if (entity != null && "0".equals(entity.getStatus())) {
            throw new BizException(ApiCode.USER_DISABLED);
        }
        String token = jwtService.generateToken(principal.getUsername());
        return new LoginResponse(token);
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails user)) {
            throw new BizException(ApiCode.UNAUTHORIZED);
        }
        UserEntity entity = userService.lambdaQuery().eq(UserEntity::getUsername, user.getUsername()).one();
        if (entity == null) {
            throw new BizException(ApiCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(request.getOldPassword(), entity.getPassword())) {
            throw new BizException(ApiCode.BAD_REQUEST, "旧密码不正确");
        }
        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.updateById(entity);
    }
}
