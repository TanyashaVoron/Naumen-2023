package com.naumen.anticafe.config;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.Role;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.repository.RoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        //Шифровальщик пароля
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(EmployeeRepository employeeRepository){
        //поиск сотрудника в бд если его не находит, выдает ошибку
        return username -> {
            Employee employee = employeeRepository.findByUsername(username);
            if(employee!=null) return employee;
            throw new UsernameNotFoundException("Employee '"+username+"' not found");
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //контроль доступа пользователям(на текущий момент все открыто)
        httpSecurity.authorizeHttpRequests(request -> request
                .anyRequest().permitAll()).formLogin().loginPage("/login");
        httpSecurity.logout()
                .logoutSuccessUrl("/login");
        httpSecurity.csrf().ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"));
        httpSecurity.headers().frameOptions().sameOrigin();
        return httpSecurity.build();
    }
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String str = "ROLE_ADMIN > ROLE_GENERAL_MANAGER > ROLE_MANAGER";
        roleHierarchy.setHierarchy(str);
        return roleHierarchy;
    }
}
