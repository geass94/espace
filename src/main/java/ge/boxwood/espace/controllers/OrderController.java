package ge.boxwood.espace.controllers;

import ge.boxwood.espace.models.CreditCard;
import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.Payment;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.enums.PaymentType;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.repositories.CreditCardRepository;
import ge.boxwood.espace.repositories.OrderRepository;
import ge.boxwood.espace.repositories.PaymentRepository;
import ge.boxwood.espace.security.TokenHelper;
import ge.boxwood.espace.services.ChargerService;
import ge.boxwood.espace.services.UserService;
import ge.boxwood.espace.services.smsservice.GeoSms.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private CreditCardRepository creditCardRepository;
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

    @GetMapping("/cards")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getCards(HttpServletRequest request){
        String authToken = tokenHelper.getToken( request );
        String username = tokenHelper.getUsernameFromToken(authToken);
        User user = userService.getByUsername(username);
        System.out.println(authToken);
        return ResponseEntity.ok(creditCardRepository.findAllByUserAndStatus(user, Status.ACTIVE));
    }

    @PutMapping("/cards/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> arrangeCards(){
        return null;
    }

    @DeleteMapping("/cards/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> deleteCard(@PathVariable("id")Long id){
        CreditCard creditCard = creditCardRepository.findOne(id);
        creditCard.setStatus(Status.DELETED);
        creditCard = creditCardRepository.save(creditCard);
        if(creditCard != null) {
            return ResponseEntity.ok(true);
        }
        else{
            throw new RuntimeException("Error while deleting");
        }

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
