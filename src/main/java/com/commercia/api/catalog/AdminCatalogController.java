package com.commercia.api.catalog;

import com.commercia.domain.Product;
import com.commercia.dto.ProductCreateRequest;
import com.commercia.dto.ProductResponse;
import com.commercia.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/catalog")
public class AdminCatalogController {
  private final CatalogService catalogService;

  public AdminCatalogController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @PostMapping("/products")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest request) {
    Product product = new Product(request.getName(), request.getDescription(), request.getPrice(),
        request.getStockQty(), request.getImageUrl());
    Product saved = catalogService.createProduct(product, request.getCategoryName());
    return catalogService.getProduct(saved.getId());
  }
}
