package com.commercia.dto;

public class AuthResponse {
  private String token;
  private UserSummary user;

  public AuthResponse(String token, UserSummary user) {
    this.token = token;
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public UserSummary getUser() {
    return user;
  }
}
