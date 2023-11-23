package com.naumen.anticafe.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public interface ReserveServiceProperties {
    public int getDaysToReserve();

    public int getOpeningHour();

    public int getClosingHour();
}
