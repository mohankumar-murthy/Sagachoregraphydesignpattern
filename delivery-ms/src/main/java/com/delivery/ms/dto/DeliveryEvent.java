package com.delivery.ms.dto;

import lombok.Data;

@Data
public class DeliveryEvent {
    private String type;
    private CustomerOrder order;
}
