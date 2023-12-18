package com.naumen.anticafe.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
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
    private LocalDate timerTaggedDelete;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "compositeId.order")
    private List<Guest> guests;
}
