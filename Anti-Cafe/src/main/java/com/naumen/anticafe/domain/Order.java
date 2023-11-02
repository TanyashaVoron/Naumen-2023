package com.naumen.anticafe.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private LocalDate reserveDate;
    private LocalTime reserveTime;
    private LocalTime endReserve;
    @ManyToOne
    private Employee manager;
    private Boolean payment;
    private LocalDate date;
    private int tariff;
    private int total;
    private Boolean taggedDelete;
}
