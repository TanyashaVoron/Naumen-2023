package com.naumen.anticafe.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@Entity
@NoArgsConstructor
@Table(name = "guests")
public class Guest {

    @EmbeddedId
    private GuestId compositeId;
    private String name;
    @ManyToOne
    private Client client;
    private Date timeBegin;
    private Date timeEnd;
    private int total;
}
