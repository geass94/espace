package ge.boxwood.espace.controllers;

import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.Payment;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.enums.PaymentType;
import ge.boxwood.espace.repositories.OrderRepository;
import ge.boxwood.espace.repositories.PaymentRepository;
import ge.boxwood.espace.security.TokenHelper;
import ge.boxwood.espace.services.ChargerService;
import ge.boxwood.espace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenHelper tokenHelper;
    @Autowired
    private ChargerService chargerService;
    @PostMapping("/giveOrder")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> giveOrder(HttpServletRequest request){
        String authToken = tokenHelper.getToken( request );
        String username = tokenHelper.getUsernameFromToken(authToken);
        User user = userService.getByUsername(username);
        Order order = new Order(user);
        order.setCharger(chargerService.getOneByCID(7L));
        order.setPaymentType(PaymentType.CASH);
        order = orderRepository.save(order);
        Payment payment = new Payment((float) 123.32, order);
        paymentRepository.save(payment);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/addUserCreditCard")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> confirmCreditCard(HttpServletRequest request){
        String authToken = tokenHelper.getToken( request );
        String username = tokenHelper.getUsernameFromToken(authToken);
        User user = userService.getByUsername(username);
        Order order = new Order(user);
        order.setPaymentType(PaymentType.CREDITCARD);
        order.setCashPayment(false);
        order.setPrice(1f);
        orderRepository.save(order);
        Payment payment = new Payment(1f, order);
        paymentRepository.save(payment);
        orderRepository.flush();
        return ResponseEntity.ok(payment);
    }
}
