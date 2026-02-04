package com.delivery.ms.controller;

import com.delivery.ms.dto.CustomerOrder;
import com.delivery.ms.dto.DeliveryEvent;
import com.delivery.ms.entity.Delivery;
import com.delivery.ms.entity.DeliveryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.ConcreteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class DeliveryController {
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private KafkaTemplate<String, DeliveryEvent> kafkaTemplate;

    @KafkaListener(topics = "new-stock", groupId="stock-group")
    public void deliveryOrder(String event) throws JsonMappingException, JsonProcessingException{
        System.out.println("Inside the ship order for order " + event);

        Delivery shipment = new Delivery();
        DeliveryEvent inventoryEvent = new ObjectMapper().readValue(event, DeliveryEvent.class);
        CustomerOrder order = inventoryEvent.getOrder();

        try{
            if(order.getAddress() == null){
                throw new Exception("Address not found");
            }
            shipment.setAddress(order.getAddress());
            shipment.setOrderId(order.getOrderId());
            shipment.setStatus("SUCCESS");
            deliveryRepository.save(shipment);
        } catch (Exception e){
            shipment.setOrderId(order.getOrderId());
            shipment.setStatus("FAILED");
            deliveryRepository.save(shipment);

            System.out.println(order);

            DeliveryEvent reverseEvent = new DeliveryEvent();
            reverseEvent.setType("STOCK_REVERSED");
            reverseEvent.setOrder(order);
            kafkaTemplate.send("reversed-stock", reverseEvent);
        }
    }
}
