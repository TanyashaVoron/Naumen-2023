package com.naumen.anticafe.config;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.repository.EmployeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(EmployeeRepository employeeRepository){
        return username -> {
            Employee employee = employeeRepository.findByUsername(username);
            if(employee!=null) return employee;
            throw new UsernameNotFoundException("Employee '"+username+"' not found");
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request.anyRequest().permitAll()).formLogin().loginPage("/login");
        return httpSecurity.build();
    }
}
