package com.commercia.service;

import com.commercia.domain.Cart;
import com.commercia.domain.CartItem;
import com.commercia.domain.Order;
import com.commercia.domain.OrderItem;
import com.commercia.domain.UserAccount;
import com.commercia.dto.CheckoutRequest;
import com.commercia.dto.OrderItemResponse;
import com.commercia.dto.OrderResponse;
import com.commercia.repo.OrderRepository;
import com.commercia.repo.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
  private final OrderRepository orderRepository;
  private final CartService cartService;
  private final ProductRepository productRepository;

  public OrderService(OrderRepository orderRepository, CartService cartService, ProductRepository productRepository) {
    this.orderRepository = orderRepository;
    this.cartService = cartService;
    this.productRepository = productRepository;
  }

  @Transactional
  public OrderResponse checkout(UserAccount user, CheckoutRequest request) {
    Cart cart = cartService.getCartEntity(user);
    if (cart.getItems().isEmpty()) {
      throw new IllegalArgumentException("Cart is empty");
    }

    BigDecimal total = cart.getItems().stream()
        .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    Order order = new Order(user, total);
    order.setShippingName(request.getShippingName());
    order.setShippingAddress(request.getShippingAddress());
    order.setShippingCity(request.getShippingCity());
    order.setShippingState(request.getShippingState());
    order.setShippingZip(request.getShippingZip());

    for (CartItem item : cart.getItems()) {
      if (item.getProduct().getStockQty() < item.getQuantity()) {
        throw new IllegalArgumentException("Insufficient stock for " + item.getProduct().getName());
      }

      item.getProduct().setStockQty(item.getProduct().getStockQty() - item.getQuantity());
      productRepository.save(item.getProduct());

      OrderItem orderItem = new OrderItem(order, item.getProduct().getName(), item.getProduct().getPrice(),
          item.getQuantity());
      order.getItems().add(orderItem);
    }

    Order saved = orderRepository.save(order);
    cart.getItems().clear();

    return toOrderResponse(saved);
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> listOrders(UserAccount user) {
    return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
        .map(this::toOrderResponse)
        .collect(Collectors.toList());
  }

  private OrderResponse toOrderResponse(Order order) {
    List<OrderItemResponse> items = order.getItems().stream()
        .map(item -> new OrderItemResponse(item.getId(), item.getProductName(), item.getUnitPrice(), item.getQuantity()))
        .collect(Collectors.toList());
    return new OrderResponse(order.getId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt(), items);
  }
}
