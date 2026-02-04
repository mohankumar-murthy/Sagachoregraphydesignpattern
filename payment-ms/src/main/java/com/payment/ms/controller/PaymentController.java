package com.payment.ms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.ms.dto.CustomerOrder;
import com.payment.ms.dto.OrderEvent;
import com.payment.ms.dto.PaymentEvent;
import com.payment.ms.entity.Payment;
import com.payment.ms.entity.PaymentRepository;
import org.hibernate.annotations.ConcreteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaOrderTemplate;

    @KafkaListener(topics = "new-orders", groupId = "orders-group")
    public void processPayment(String event) throws JsonMappingException, JsonProcessingException{
        System.out.println("Received Event for payment" + event);
        OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);

        CustomerOrder order = orderEvent.getOrder();
        Payment payment = new Payment();

        try{
            payment.setAmount(order.getAmount());
            payment.setPaymentMode(order.getPaymentMode());
            payment.setOrderId(order.getOrderId());
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);

            PaymentEvent paymentEvent = new PaymentEvent();
            paymentEvent.setOrder(orderEvent.getOrder());
            paymentEvent.setType("PAYMENT_CREATED");
            kafkaTemplate.send("new-payments", paymentEvent);
        } catch (Exception e){
            payment.setOrderId(order.getOrderId());
            payment.setStatus("FAILED");
            paymentRepository.save(payment);

            OrderEvent oe = new OrderEvent();
            oe.setOrder(order);
            oe.setType("ORDER_REVERSED");
            kafkaOrderTemplate.send("reversed-orders", orderEvent);
        }
    }
}
