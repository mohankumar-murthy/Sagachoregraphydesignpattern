package com.order.ms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.ms.dto.OrderEvent;
import com.order.ms.entity.Orders;
import com.order.ms.entity.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReverseOrder {

    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = "reversed-orders", groupId = "orders-group")
    public void reverseOrder(String event){
        System.out.println("Inside reverse order for order " + event);
        try {
            OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);
            Optional<Orders> order = orderRepository.findById(orderEvent.getOrder().getOrderId());

            order.ifPresent(o -> {
                o.setStatus("FAILED");
                this.orderRepository.save(o);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
