package com.order.ms.dto;

import lombok.Data;

@Data
public class Payment {
    private String paymentMode;
    private Long orderId;
    private double amount;
}
