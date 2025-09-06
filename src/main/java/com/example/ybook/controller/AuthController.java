package com.example.ybook.controller;

import com.example.ybook.common.ApiCode;
import com.example.ybook.common.ApiResponse;
import com.example.ybook.dto.ChangePasswordRequest;
import com.example.ybook.dto.LoginRequest;
import com.example.ybook.dto.LoginResponse;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.exception.BizException;
import com.example.ybook.security.JwtService;
import com.example.ybook.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        // 检查状态
        UserEntity entity = userService.lambdaQuery().eq(UserEntity::getUsername, principal.getUsername()).one();
        if (entity != null && "0".equals(entity.getStatus())) {
            throw new BizException(ApiCode.USER_DISABLED);
        }
        String token = jwtService.generateToken(principal.getUsername());
        return ApiResponse.success(new LoginResponse(token));
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails user)) {
            return ApiResponse.error(ApiCode.UNAUTHORIZED);
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
        return ApiResponse.success();
    }
}

