package com.commercia.service;

import com.commercia.domain.Cart;
import com.commercia.domain.Role;
import com.commercia.domain.UserAccount;
import com.commercia.domain.enums.RoleName;
import com.commercia.dto.RegisterRequest;
import com.commercia.repo.RoleRepository;
import com.commercia.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public UserAccount registerUser(RegisterRequest request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email already registered");
    }

    UserAccount user = new UserAccount(request.getFullName(), request.getEmail(),
        passwordEncoder.encode(request.getPassword()));
    user.setPhone(request.getPhone());

    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
        .orElseThrow(() -> new EntityNotFoundException("Role not configured"));
    user.setRoles(Set.of(userRole));

    Cart cart = new Cart(user);
    user.setCart(cart);

    return userRepository.save(user);
  }

  public UserAccount getByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
  }
}
