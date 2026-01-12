package com.commercia.service;

import com.commercia.domain.Cart;
import com.commercia.domain.CartItem;
import com.commercia.domain.Product;
import com.commercia.domain.UserAccount;
import com.commercia.dto.CartItemResponse;
import com.commercia.dto.CartResponse;
import com.commercia.repo.CartRepository;
import com.commercia.repo.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;

  public CartService(CartRepository cartRepository, ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.productRepository = productRepository;
  }

  @Transactional(readOnly = true)
  public CartResponse getCart(UserAccount user) {
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    return toCartResponse(cart);
  }

  @Transactional
  public CartResponse addItem(UserAccount user, Long productId, int quantity) {
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    if (!product.isActive() || product.getStockQty() < quantity) {
      throw new IllegalArgumentException("Insufficient stock for this product");
    }

    Optional<CartItem> existing = cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst();

    if (existing.isPresent()) {
      CartItem item = existing.get();
      int newQty = item.getQuantity() + quantity;
      if (product.getStockQty() < newQty) {
        throw new IllegalArgumentException("Insufficient stock for this quantity");
      }
      item.setQuantity(newQty);
    } else {
      cart.getItems().add(new CartItem(cart, product, quantity));
    }

    return toCartResponse(cart);
  }

  @Transactional
  public CartResponse updateItem(UserAccount user, Long itemId, int quantity) {
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    CartItem item = cart.getItems().stream()
        .filter(cartItem -> cartItem.getId().equals(itemId))
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

    if (quantity <= 0) {
      cart.getItems().remove(item);
    } else {
      if (item.getProduct().getStockQty() < quantity) {
        throw new IllegalArgumentException("Insufficient stock for this quantity");
      }
      item.setQuantity(quantity);
    }

    return toCartResponse(cart);
  }

  @Transactional
  public CartResponse removeItem(UserAccount user, Long itemId) {
    Cart cart = cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    cart.getItems().removeIf(item -> item.getId().equals(itemId));
    return toCartResponse(cart);
  }

  @Transactional(readOnly = true)
  public Cart getCartEntity(UserAccount user) {
    return cartRepository.findByUserId(user.getId())
        .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
  }

  private CartResponse toCartResponse(Cart cart) {
    List<CartItemResponse> items = cart.getItems().stream()
        .map(item -> new CartItemResponse(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getProduct().getPrice(),
            item.getQuantity(),
            item.getProduct().getImageUrl()))
        .collect(Collectors.toList());

    BigDecimal subtotal = items.stream()
        .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new CartResponse(items, subtotal);
  }
}
