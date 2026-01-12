package com.commercia.api.auth;

import com.commercia.domain.UserAccount;
import com.commercia.dto.AuthResponse;
import com.commercia.dto.LoginRequest;
import com.commercia.dto.RegisterRequest;
import com.commercia.dto.UserSummary;
import com.commercia.security.JwtService;
import com.commercia.service.AuthService;
import com.commercia.service.UserService;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserService userService;
  private final AuthService authService;
  private final JwtService jwtService;

  public AuthController(UserService userService, AuthService authService, JwtService jwtService) {
    this.userService = userService;
    this.authService = authService;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    UserAccount user = userService.registerUser(request);
    String token = jwtService.generateToken(user.getEmail(),
        user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList()));
    UserSummary summary = new UserSummary(user.getId(), user.getFullName(), user.getEmail(),
        user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));
    return new AuthResponse(token, summary);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.authenticate(request);
  }
}
