package com.naumen.anticafe.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Date;
@Data
@Entity
@NoArgsConstructor
@Table(name = "guests")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ordr")
    private Order order;
    private String name;
    @ManyToOne
    private Client client;
    private Date timeBegin;
    private Date timeEnd;
    private int total;
}
