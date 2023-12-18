package com.naumen.anticafe.exception;

import lombok.Getter;

@Getter
public class NoAccessToOperation extends Exception {
    String nameEmployeeNow;
    String ownerOrderEmployeeName;

    public NoAccessToOperation(String e, String ownerOrderEmployeeName, String nameEmployeeNow) {
        super(e);
        this.nameEmployeeNow = nameEmployeeNow;
        this.ownerOrderEmployeeName = ownerOrderEmployeeName;
    }
}
