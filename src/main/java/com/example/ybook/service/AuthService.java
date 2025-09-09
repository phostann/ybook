package com.example.ybook.service;

import com.example.ybook.dto.ChangePasswordRequestDTO;
import com.example.ybook.dto.LoginRequestDTO;
import com.example.ybook.dto.LoginResponse;
import com.example.ybook.dto.UserCreateDTO;
import com.example.ybook.vo.UserVO;

/**
 * <p>
 * 认证服务接口
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
public interface AuthService {
    LoginResponse login(LoginRequestDTO request);
    void changePassword(ChangePasswordRequestDTO request);
    UserVO register(UserCreateDTO request);
    UserVO getCurrentUserProfile();
}
