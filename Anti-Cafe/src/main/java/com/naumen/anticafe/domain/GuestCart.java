package com.naumen.anticafe.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "guest_carts")
public class GuestCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Guest guest;
    @ManyToOne
    private Product product;
    private int quantity;
}
