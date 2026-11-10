package com.david.auth.services;

import com.david.auth.dto.LoginRequest;
import com.david.auth.dto.TokenResponse;

public interface AuthService {
    TokenResponse autenticar(LoginRequest request) throws Exception;
}
