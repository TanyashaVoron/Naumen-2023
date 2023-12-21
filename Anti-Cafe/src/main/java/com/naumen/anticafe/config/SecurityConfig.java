package com.naumen.anticafe.config;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.repository.EmployeeRepository;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Optional;

@Configuration
public class SecurityConfig {
    /**
     * Шифровальщик пароля
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Служба сведений о пользователях
     */
    @Bean
    public UserDetailsService userDetailsService(EmployeeRepository employeeRepository) {
        //поиск сотрудника в бд если его не находит, выдает ошибку
        return username -> {
            Optional<Employee> employee = employeeRepository.findByUsername(username);
            if (employee.isPresent()) return employee.get();
            throw new UsernameNotFoundException("Employee '" + username + "' not found");
        };
    }

    /**
     * Цепочка фильтров безопасности
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //контроль доступа пользователям
        httpSecurity.authorizeHttpRequests(request -> request
                        .requestMatchers(
                                new AntPathRequestMatcher("/login")
                        ).permitAll()
                        .requestMatchers(
                                new AntPathRequestMatcher("/orderManagement/**")
                        ).hasAnyRole("GENERAL_MANAGER")
                        .requestMatchers(
                                new AntPathRequestMatcher("/employee/**")
                        ).hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
                ).formLogin(formLogin -> formLogin.loginPage("/login"))
                .logout(logout -> logout.logoutSuccessUrl("/login"))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return httpSecurity.build();
    }

    /**
     * иерархия ролей для цепочки
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String str = "ROLE_ADMIN > ROLE_GENERAL_MANAGER > ROLE_MANAGER";
        roleHierarchy.setHierarchy(str);
        return roleHierarchy;
    }
}
