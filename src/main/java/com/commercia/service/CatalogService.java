package com.commercia.service;

import com.commercia.domain.Category;
import com.commercia.domain.Product;
import com.commercia.dto.CategoryResponse;
import com.commercia.dto.ProductResponse;
import com.commercia.repo.CategoryRepository;
import com.commercia.repo.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  public CatalogService(CategoryRepository categoryRepository, ProductRepository productRepository) {
    this.categoryRepository = categoryRepository;
    this.productRepository = productRepository;
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public List<CategoryResponse> listCategories() {
    return categoryRepository.findAll().stream()
        .map(category -> new CategoryResponse(category.getId(), category.getName(), category.getDescription()))
        .collect(Collectors.toList());
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public List<ProductResponse> listProducts(Long categoryId) {
    List<Product> products = categoryId == null
        ? productRepository.findByActiveTrue()
        : productRepository.findByCategoryIdAndActiveTrue(categoryId);

    return products.stream().map(this::toProductResponse).collect(Collectors.toList());
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public ProductResponse getProduct(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    return toProductResponse(product);
  }

  private ProductResponse toProductResponse(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getStockQty(),
        product.getImageUrl(),
        product.getCategory() != null ? product.getCategory().getId() : null,
        product.getCategory() != null ? product.getCategory().getName() : null
    );
  }

  public Product createProduct(Product product, String categoryName) {
    Category category = categoryRepository.findByName(categoryName)
        .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    product.setCategory(category);
    return productRepository.save(product);
  }
}
