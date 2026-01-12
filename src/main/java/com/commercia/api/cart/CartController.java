package com.commercia.api.cart;

import com.commercia.dto.CartItemRequest;
import com.commercia.dto.CartResponse;
import com.commercia.security.AppUserDetails;
import com.commercia.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping
  public CartResponse getCart(@AuthenticationPrincipal AppUserDetails userDetails) {
    return cartService.getCart(userDetails.getUser());
  }

  @PostMapping("/items")
  public CartResponse addItem(@AuthenticationPrincipal AppUserDetails userDetails,
                              @Valid @RequestBody CartItemRequest request) {
    return cartService.addItem(userDetails.getUser(), request.getProductId(), request.getQuantity());
  }

  @PatchMapping("/items/{itemId}")
  public CartResponse updateItem(@AuthenticationPrincipal AppUserDetails userDetails,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CartItemRequest request) {
    return cartService.updateItem(userDetails.getUser(), itemId, request.getQuantity());
  }

  @DeleteMapping("/items/{itemId}")
  public CartResponse removeItem(@AuthenticationPrincipal AppUserDetails userDetails,
                                 @PathVariable Long itemId) {
    return cartService.removeItem(userDetails.getUser(), itemId);
  }
}
