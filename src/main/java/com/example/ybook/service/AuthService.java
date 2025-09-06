package com.example.ybook.service;

import com.example.ybook.dto.ChangePasswordRequestDTO;
import com.example.ybook.dto.LoginRequestDTO;
import com.example.ybook.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequestDTO request);
    void changePassword(ChangePasswordRequestDTO request);
}

