package com.commercia.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {
  private List<CartItemResponse> items;
  private BigDecimal subtotal;

  public CartResponse(List<CartItemResponse> items, BigDecimal subtotal) {
    this.items = items;
    this.subtotal = subtotal;
  }

  public List<CartItemResponse> getItems() {
    return items;
  }

  public BigDecimal getSubtotal() {
    return subtotal;
  }
}
