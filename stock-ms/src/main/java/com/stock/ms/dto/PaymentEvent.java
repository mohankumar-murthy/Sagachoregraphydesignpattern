package com.stock.ms.dto;

import lombok.Data;

@Data
public class PaymentEvent {
    private String type;
    private CustomerOrder order;
}
