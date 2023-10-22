package com.naumen.anticafe;

import com.naumen.anticafe.domain.*;
import com.naumen.anticafe.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class AntiCafeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AntiCafeApplication.class, args);
    }

/*
  @Bean
    public CommandLineRunner dataLoad(OrderRepository orderRepository,
                                      GuestRepository guestRepository,
                                      GuestCartRepository guestCartRepository,
                                      ProductRepository productRepository,
                                      EmployeeRepository employeeRepository,
                                      RoleRepository roleRepository,
                                      PasswordEncoder passwordEncoder,
                                      GameZoneRepository gameZoneRepository){
        return args ->{
                    Role role1 = new Role();
            role1.setRole("ROLE_ADMIN");
            roleRepository.save(role1);
            Role role2 = new Role();
            role2.setRole("ROLE_USER");
            roleRepository.save(role2);
                        Employee employee = new Employee();
            employee.setName("Piter p.");
            employee.setPassword(passwordEncoder.encode("admin"));
            employee.setUsername("admin");
            employee.getRole().add(role1);
            employee.setEnabled(true);
            employee.setAccountNonLocked(true);
            employee.setAccountNonExpired(true);
            employee.setCredentialsNonExpired(true);
            employeeRepository.save(employee);
                        Employee employee1 = new Employee();
            employee1.setName("Jon Sina");
            employee1.setPassword(passwordEncoder.encode("user"));
            employee1.setUsername("user");
            employee1.getRole().add(role2);
            employee1.setEnabled(true);
            employee1.setAccountNonLocked(true);
            employee1.setAccountNonExpired(true);
            employee1.setCredentialsNonExpired(true);
            employeeRepository.save(employee1);
            GameZone gameZone = new GameZone();
            gameZone.setName("a");
            gameZoneRepository.save(gameZone);
            GameZone gameZone1 = new GameZone();
            gameZone.setName("b");
            gameZoneRepository.save(gameZone1);
            GameZone gameZone2 = new GameZone();
            gameZone.setName("c");
            gameZoneRepository.save(gameZone2);
            GameZone gameZone3 = new GameZone();
            gameZone.setName("d");
            gameZoneRepository.save(gameZone3);
        Order order = new Order();
        order.setGameZone(gameZone1);
        order.setManager(employee1);
        Product product1 = new Product();
        product1.setName("ooo");
        Product product2 = new Product();
        product2.setName("aaa");
        productRepository.save(product1);
        productRepository.save(product2);
        for (int i = 1; i < 4; i++) {
            Guest guest = new Guest();
            guest.setOrder(order);
            guest.setName(i+" гость");
            guestRepository.save(guest);
            GuestCart guestCart = new GuestCart();
            guestCart.setGuest(guest);
            guestCart.setProduct(product1);
            guestCartRepository.save(guestCart);
            guestCart = new GuestCart();
            guestCart.setGuest(guest);
            guestCart.setProduct(product2);
            guestCartRepository.save(guestCart);
        }};
    }*/
}
