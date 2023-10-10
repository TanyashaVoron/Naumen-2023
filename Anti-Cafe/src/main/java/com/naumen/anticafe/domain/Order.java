package com.naumen.anticafe.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private GameZone gameZone;

    private Date timeBegin;
    private Date timeEnd;
    @ManyToOne
    private Employee manager;
    private Boolean payment;
    private Date date;
    private int tariff;
    private int total;
}
