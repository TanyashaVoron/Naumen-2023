package com.naumen.anticafe.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GuestId implements Serializable {

    private Long guestId;

    @ManyToOne
    @JoinColumn
    private Order order;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GuestId)) return false;
        GuestId other = (GuestId) o;
        return Objects.equals(getGuestId(), other.getGuestId()) &&
                Objects.equals(getOrder(), other.getOrder());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getGuestId(), getOrder());
    }
}
