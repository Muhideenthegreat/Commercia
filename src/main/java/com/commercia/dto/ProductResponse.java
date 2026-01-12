package com.commercia.dto;

import java.math.BigDecimal;

public class ProductResponse {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private int stockQty;
  private String imageUrl;
  private Long categoryId;
  private String categoryName;

  public ProductResponse(Long id, String name, String description, BigDecimal price, int stockQty,
                         String imageUrl, Long categoryId, String categoryName) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stockQty = stockQty;
    this.imageUrl = imageUrl;
    this.categoryId = categoryId;
    this.categoryName = categoryName;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public int getStockQty() {
    return stockQty;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }
}
