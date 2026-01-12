package com.commercia.dto;

import java.math.BigDecimal;

public class CartItemResponse {
  private Long id;
  private Long productId;
  private String productName;
  private BigDecimal unitPrice;
  private int quantity;
  private String imageUrl;

  public CartItemResponse(Long id, Long productId, String productName, BigDecimal unitPrice, int quantity,
                          String imageUrl) {
    this.id = id;
    this.productId = productId;
    this.productName = productName;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
    this.imageUrl = imageUrl;
  }

  public Long getId() {
    return id;
  }

  public Long getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
