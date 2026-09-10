package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.AuthUtil;
import com.ecommerce.sb_ecom.payload.OrderDTO;
import com.ecommerce.sb_ecom.payload.OrderRequestDTO;
import com.ecommerce.sb_ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthUtil authUtil;


    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod,
                                                  @RequestBody OrderRequestDTO orderRequest) {
        String emailId = authUtil.loggedInEmail();
        OrderDTO response = orderService.placeOrder(emailId,
                orderRequest.getAddressId(),
                paymentMethod,
                orderRequest.getPgName(),
                orderRequest.getPgPaymentId(),
                orderRequest.getPgStatus(),
                orderRequest.getPgResponseMessage());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
