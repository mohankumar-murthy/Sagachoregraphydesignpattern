package com.payment.ms.dto;

import lombok.Data;

@Data
public class PaymentEvent
{
    private String type;
    private CustomerOrder order;
}
