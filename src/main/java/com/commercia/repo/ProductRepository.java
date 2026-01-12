package com.commercia.repo;

import com.commercia.domain.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByActiveTrue();
  List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
}
