package com.order.ms.controller;

import com.order.ms.dto.CustomerOrder;
import com.order.ms.dto.OrderEvent;
import com.order.ms.entity.OrderRepository;
import com.order.ms.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @PostMapping("/orders")
    private void createOrder(@RequestBody CustomerOrder customerOrder){
        Orders orders = new Orders();
        try{
            orders.setAmount(customerOrder.getAmount());
            orders.setItem(customerOrder.getItem());
            orders.setQuantity(customerOrder.getQuantity());
            orders.setStatus("CREATED");
            orders = orderRepository.save(orders);

            customerOrder.setOrderId(orders.getId());

            OrderEvent event = new OrderEvent();
            event.setOrder(customerOrder);
            event.setType("ORDER_CREATED");
            kafkaTemplate.send("new-orders", event);
        } catch (Exception e){
            e.printStackTrace();
            orders.setStatus("FAILED");
            orderRepository.save(orders);
        }
    }
}
