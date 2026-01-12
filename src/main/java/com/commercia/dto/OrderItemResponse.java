package com.commercia.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
  private Long id;
  private String productName;
  private BigDecimal unitPrice;
  private int quantity;

  public OrderItemResponse(Long id, String productName, BigDecimal unitPrice, int quantity) {
    this.id = id;
    this.productName = productName;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
  }

  public Long getId() {
    return id;
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
}
