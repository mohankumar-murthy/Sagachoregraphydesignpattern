package com.delivery.ms.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CustomerOrder {
    private String item;
    private int quantity;
    private double amount;
    private String paymentMode;
    private Long orderId;
    private String address;
}
