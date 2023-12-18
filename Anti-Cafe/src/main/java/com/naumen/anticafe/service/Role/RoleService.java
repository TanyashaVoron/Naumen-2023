package com.naumen.anticafe.service.Role;

import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.exception.NotFoundException;

import java.util.List;

public interface RoleService {
    List<Role> getAllRole();

    Role getRole(Integer roleId) throws NotFoundException;
}
