package com.naumen.anticafe.error;

import lombok.Getter;

@Getter
public class GuestsHaveGoodsException extends Exception{
    private final Long guestId;
    private final String message;
    public GuestsHaveGoodsException(String e, Long guestId) {
        super(e);
        this.guestId = guestId;
        message = e;
    }
}
