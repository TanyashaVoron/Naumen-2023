package com.naumen.anticafe.helper;

import com.naumen.anticafe.DTO.receive.searchOrderManagment.ShowDTO;
import com.naumen.anticafe.DTO.send.searchOrderManagment.ShowSendDTO;
import com.naumen.anticafe.error.NotFoundException;

public interface SearchOrderManagementHelper {
    public ShowSendDTO searchOrder(ShowDTO dto, boolean orderMarker, String employeeUsername) throws NotFoundException;
}
