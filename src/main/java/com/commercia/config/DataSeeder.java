package com.commercia.config;

import com.commercia.domain.Cart;
import com.commercia.domain.Category;
import com.commercia.domain.Product;
import com.commercia.domain.Role;
import com.commercia.domain.UserAccount;
import com.commercia.domain.enums.RoleName;
import com.commercia.repo.CategoryRepository;
import com.commercia.repo.ProductRepository;
import com.commercia.repo.RoleRepository;
import com.commercia.repo.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
  @Bean
  CommandLineRunner seedData(RoleRepository roleRepository,
                             UserRepository userRepository,
                             CategoryRepository categoryRepository,
                             ProductRepository productRepository,
                             PasswordEncoder passwordEncoder) {
    return args -> {
      Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
          .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));
      Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
          .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));

      if (userRepository.findByEmail("admin@commercia.local").isEmpty()) {
        UserAccount admin = new UserAccount("Commercia Admin", "admin@commercia.local",
            passwordEncoder.encode("admin123"));
        admin.setRoles(Set.of(adminRole, userRole));
        Cart cart = new Cart(admin);
        admin.setCart(cart);
        userRepository.save(admin);
      }

      if (categoryRepository.count() == 0) {
        Category devices = new Category("Devices", "Premium hardware for modern work and play.");
        Category lifestyle = new Category("Lifestyle", "Everyday accessories that feel elevated.");
        Category studio = new Category("Studio", "Tools for creators and teams.");

        categoryRepository.saveAll(List.of(devices, lifestyle, studio));

        productRepository.saveAll(List.of(
            createProduct("Aurora Smart Hub", "A minimalist smart hub with ambient controls and voice presets.",
                new BigDecimal("179.00"), 40, devices,
                "https://images.unsplash.com/photo-1519389950473-47ba0277781c?auto=format&fit=crop&w=900&q=80"),
            createProduct("Nimbus Mechanical Keyboard", "Hot-swappable keys, quiet switches, and a soft glow.",
                new BigDecimal("129.00"), 65, devices,
                "https://images.unsplash.com/photo-1516251193007-45ef944ab0c6?auto=format&fit=crop&w=900&q=80"),
            createProduct("Ember Travel Mug", "Keeps drinks perfectly warm, with a soft-touch finish.",
                new BigDecimal("49.00"), 120, lifestyle,
                "https://images.unsplash.com/photo-1543332164-6e82f355badc?auto=format&fit=crop&w=900&q=80"),
            createProduct("Lumen Desk Lamp", "Adjustable lighting with focus modes for late nights.",
                new BigDecimal("89.00"), 80, lifestyle,
                "https://images.unsplash.com/photo-1501045661006-fcebe0257c3f?auto=format&fit=crop&w=900&q=80"),
            createProduct("Studio Field Notes", "A5 linen-bound notebook set for ideation and planning.",
                new BigDecimal("24.00"), 200, studio,
                "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=900&q=80"),
            createProduct("Atlas Backpack", "Weather-resistant backpack with device sleeves and hidden pockets.",
                new BigDecimal("139.00"), 55, studio,
                "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=900&q=80")
        ));
      }
    };
  }

  private Product createProduct(String name, String description, BigDecimal price, int stockQty,
                                Category category, String imageUrl) {
    Product product = new Product(name, description, price, stockQty, imageUrl);
    product.setCategory(category);
    return product;
  }
}
