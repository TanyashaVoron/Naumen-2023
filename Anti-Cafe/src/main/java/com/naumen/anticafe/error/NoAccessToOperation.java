package com.naumen.anticafe.error;

import lombok.Getter;

@Getter
public class NoAccessToOperation extends Exception{
    String nameEmployeeNow;
    String ownerOrderEmployeeName;

    public NoAccessToOperation(String e,String nameEmployeeNow, String ownerOrderEmployeeName) {
        super(e);
        this.nameEmployeeNow = nameEmployeeNow;
        this.ownerOrderEmployeeName = ownerOrderEmployeeName;
    }
}
