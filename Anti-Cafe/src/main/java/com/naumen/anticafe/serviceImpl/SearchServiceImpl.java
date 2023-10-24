package com.naumen.anticafe.serviceImpl;

import com.naumen.anticafe.domain.Employee;
import com.naumen.anticafe.domain.GameZone;
import com.naumen.anticafe.domain.Order;
import com.naumen.anticafe.repository.EmployeeRepository;
import com.naumen.anticafe.repository.GameZoneRepository;
import com.naumen.anticafe.repository.OrderRepository;
import com.naumen.anticafe.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public class SearchServiceImpl implements SearchService {

    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final GameZoneRepository gameZoneRepository;
    @Autowired
    public SearchServiceImpl(OrderRepository orderRepository,
                             EmployeeRepository employeeRepository,
                             GameZoneRepository gameZoneRepository) {
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
        this.gameZoneRepository = gameZoneRepository;
    }

    public List<Order> getOrderByIdOrGameZoneOrPayment(Long orderId, Long gameZoneId, Boolean payment, LocalDate reserveDate,Long employeeId){
        GameZone gameZone = null;
        if(gameZoneId!=null) {
            Optional<GameZone> optionalGameZone = gameZoneRepository.findById(gameZoneId);
            if (optionalGameZone.isPresent()) gameZone = optionalGameZone.get();
        }
        Employee employee = null;
        if(employeeId!=null) {
            Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
            if (optionalEmployee.isPresent()) employee = optionalEmployee.get();
        }
        List<Order> orders = orderRepository.findAllByIdAndGameZoneAndPaymentAndReserveDateAndManager(orderId,gameZone,payment,reserveDate,employee);
        return orders;
    }
    public Iterable<Employee> getEmployees(){
        return employeeRepository.findAll();
    }
}
