package com.naumen.anticafe.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "guest")
    private List<GuestCart> Cart;
}
