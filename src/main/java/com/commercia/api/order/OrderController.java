package com.commercia.api.order;

import com.commercia.dto.CheckoutRequest;
import com.commercia.dto.OrderResponse;
import com.commercia.security.AppUserDetails;
import com.commercia.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public OrderResponse checkout(@AuthenticationPrincipal AppUserDetails userDetails,
                                @Valid @RequestBody CheckoutRequest request) {
    return orderService.checkout(userDetails.getUser(), request);
  }

  @GetMapping
  public List<OrderResponse> listOrders(@AuthenticationPrincipal AppUserDetails userDetails) {
    return orderService.listOrders(userDetails.getUser());
  }
}
