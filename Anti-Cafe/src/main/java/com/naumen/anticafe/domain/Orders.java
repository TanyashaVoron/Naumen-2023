package com.naumen.anticafe.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Entity
@NoArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private GameZones gameZone;

    private Date timeBegin;
    private Date timeEnd;
    @ManyToOne
    private Employees manager;
    private Boolean payment;
    private Date date;
    private int tariff;
    private int total;
}
