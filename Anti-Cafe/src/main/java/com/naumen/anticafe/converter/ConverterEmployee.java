package com.naumen.anticafe.converter;

import com.naumen.anticafe.DTO.receive.employee.ShowAddDTO;
import com.naumen.anticafe.DTO.receive.employee.ShowEditDTO;
import com.naumen.anticafe.DTO.send.employee.EmployeeSendDTO;
import com.naumen.anticafe.DTO.send.employee.ShowAddSendDTO;
import com.naumen.anticafe.DTO.send.employee.ShowEditSendDTO;
import com.naumen.anticafe.DTO.send.employee.ShowSendDTO;
import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConverterEmployee {
    public static ShowAddSendDTO convertToShowAddSendDTO(ShowAddDTO dto, String employee, List<Role> roles) {
        return new ShowAddSendDTO(
                Optional.ofNullable(dto.nameError()),
                Optional.ofNullable(dto.usernameError()),
                Optional.ofNullable(dto.usernameDuplicateError()),
                Optional.ofNullable(dto.passwordError()),
                dto.name() == null ? "" : dto.name(),
                dto.username() == null ? "" : dto.username(),
                dto.roleId() == null ? 1 : dto.roleId(),
                employee,
                roles
        );
    }

    public static EmployeeSendDTO convertToEmployeeSendDTO(Employee employee) {
        return new EmployeeSendDTO(
                employee.getId(),
                employee.getName(),
                employee.getUsername(),
                employee.getRole().getRole(),
                employee.isEnabled()
        );
    }

    public static ShowEditSendDTO convertToShowEditSendDTO(ShowEditDTO dto, String employee, Employee employeeEdit, List<Role> roles) {
        return new ShowEditSendDTO(
                Optional.ofNullable(dto.nameError()),
                Optional.ofNullable(dto.username()),
                Optional.ofNullable(dto.usernameDuplicateError()),
                Optional.ofNullable(dto.passwordError()),
                employee,
                employeeEdit.getId(),
                employeeEdit.getUsername(),
                employeeEdit.getRole().getId(),
                employeeEdit.getName(),
                roles
        );
    }

    public static List<EmployeeSendDTO> convertToListEmployeeSendDTO(Page<Employee> employeeList) {
        List<EmployeeSendDTO> employeeSendDTOList = new ArrayList<>();
        for (Employee e : employeeList) {
            employeeSendDTOList.add(
                    ConverterEmployee.convertToEmployeeSendDTO(e)
            );
        }
        return employeeSendDTOList;
    }

    public static ShowSendDTO convertToShowSendDTO(String employeeName, Page<Employee> employeePage, String username) {
        List<EmployeeSendDTO> employeeSendDTOList = convertToListEmployeeSendDTO(employeePage);
        return new ShowSendDTO(
                employeeName,
                employeeSendDTOList,
                employeePage.getTotalPages(),
                employeePage.getTotalElements(),
                employeePage.isLast(),
                employeePage.isFirst(),
                employeePage.getNumber(),
                username
        );
    }
}
