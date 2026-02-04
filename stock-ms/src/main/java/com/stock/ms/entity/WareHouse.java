package com.stock.ms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class WareHouse {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private int quantity;
    @Column
    private String item;

}
