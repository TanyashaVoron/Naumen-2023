package com.naumen.anticafe.service.Role;

import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.error.NotFoundException;

import java.util.List;

public interface RoleService {
    List<Role> getAllRole();
    Role getRole(Long roleId) throws NotFoundException;
}
