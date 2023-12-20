package com.naumen.anticafe.serviceImpl.role;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.RoleRepository;
import com.naumen.anticafe.service.Role.RoleService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @InjectMocks
    private RoleServiceImpl roleService;
    @Mock
    private RoleRepository roleRepository;

    @Test
    void getAllRole() {
        Role role = new Role(1,"role");
        List<Role> roleList1 = new ArrayList<>(List.of(role));
        List<Role> roleList2 = new ArrayList<>(List.of(role));
        Mockito.when(roleRepository.findAll()).thenReturn(roleList1);
        Assertions.assertEquals(roleService.getAllRole(),roleList2);
        Mockito.verify(roleRepository,Mockito.times(1)).findAll();
    }

    @SneakyThrows
    @Test
    void getRole_found_andException() {
        Role role = new Role(1, "Role");
        Integer id = 1;
        Integer exceptionId = 2;
        //найден
        Mockito.when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        Assertions.assertEquals(roleService.getRole(id), role);
        Mockito.verify(roleRepository, Mockito.times(1)).findById(id);
        //ненайден
        Mockito.when(roleRepository.findById(exceptionId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> roleService.getRole(exceptionId));
        Mockito.verify(roleRepository, Mockito.times(1)).findById(exceptionId);
    }
}