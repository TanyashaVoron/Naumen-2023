package com.naumen.anticafe.serviceImpl.role;

import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.exception.NotFoundException;
import com.naumen.anticafe.repository.RoleRepository;
import com.naumen.anticafe.service.Role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * возвращает все роли
     */
    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRole() {
        return (List<Role>) roleRepository.findAll();
    }

    /**
     * Ищет роль по id
     */
    @Override
    @Transactional(readOnly = true)
    public Role getRole(Integer roleId) throws NotFoundException {
        //если не находит роль то выбрасывает ошибку
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalRole.isEmpty()) throw new NotFoundException("Роль не найдена");
        return optionalRole.get();
    }
}
