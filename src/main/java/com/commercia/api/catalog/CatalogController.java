package com.commercia.api.catalog;

import com.commercia.dto.CategoryResponse;
import com.commercia.dto.ProductResponse;
import com.commercia.service.CatalogService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
  private final CatalogService catalogService;

  public CatalogController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping("/categories")
  public List<CategoryResponse> listCategories() {
    return catalogService.listCategories();
  }

  @GetMapping("/products")
  public List<ProductResponse> listProducts(@RequestParam(required = false) Long categoryId) {
    return catalogService.listProducts(categoryId);
  }

  @GetMapping("/products/{id}")
  public ProductResponse getProduct(@PathVariable Long id) {
    return catalogService.getProduct(id);
  }
}
