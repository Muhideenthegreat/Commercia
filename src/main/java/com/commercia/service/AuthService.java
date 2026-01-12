package com.commercia.service;

import com.commercia.domain.UserAccount;
import com.commercia.dto.AuthResponse;
import com.commercia.dto.LoginRequest;
import com.commercia.dto.UserSummary;
import com.commercia.security.JwtService;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;

  public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userService = userService;
  }

  public AuthResponse authenticate(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    UserAccount user = userService.getByEmail(request.getEmail());

    String token = jwtService.generateToken(user.getEmail(),
        user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList()));

    UserSummary summary = new UserSummary(user.getId(), user.getFullName(), user.getEmail(),
        user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));

    return new AuthResponse(token, summary);
  }
}
