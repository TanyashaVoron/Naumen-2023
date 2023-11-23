package com.naumen.anticafe.DTO.send.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class ShowSendDTO {
    private String nameEmployee;
    private List<EmployeeDTO> employees;

    public ShowSendDTO() {
        this.employees = new ArrayList<>();
    }

    public void setEmployeeDTO(Long id, String name, String username, String role, boolean enabled) {
        EmployeeDTO employeeDTO = new EmployeeDTO(id, name, username, role, enabled);
        employees.add(employeeDTO);
    }
    public void setNameEmployee(String nameEmployee) {
        this.nameEmployee = nameEmployee;
    }
    @Getter
    @AllArgsConstructor
    class EmployeeDTO{
        private Long id;
        private String name;
        private String username;
        private String role;
        private boolean enabled;
    }
}
