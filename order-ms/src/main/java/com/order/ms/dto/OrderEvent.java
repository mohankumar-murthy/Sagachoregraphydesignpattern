package com.order.ms.dto;

import lombok.Data;

@Data
public class OrderEvent {

    private String type;
    private CustomerOrder order;
}

