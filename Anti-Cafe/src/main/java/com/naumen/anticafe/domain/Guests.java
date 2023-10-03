package com.naumen.anticafe.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Date;
@Data
@Entity
@NoArgsConstructor
public class Guests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Orders orders;
    private String name;
    @ManyToOne
    private Clients clients;
    private Date timeBegin;
    private Date timeEnd;
    private int total;
}
