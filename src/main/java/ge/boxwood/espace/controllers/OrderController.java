package ge.boxwood.espace.controllers;

import ge.boxwood.espace.models.*;
import ge.boxwood.espace.models.enums.PaymentType;
import ge.boxwood.espace.models.enums.Status;
import ge.boxwood.espace.repositories.CounterRepository;
import ge.boxwood.espace.repositories.CreditCardRepository;
import ge.boxwood.espace.repositories.OrderRepository;
import ge.boxwood.espace.repositories.PaymentRepository;
import ge.boxwood.espace.security.TokenHelper;
import ge.boxwood.espace.services.ChargerService;
import ge.boxwood.espace.services.CreditCardService;
import ge.boxwood.espace.services.PricingService;
import ge.boxwood.espace.services.UserService;
import ge.boxwood.espace.services.gc.GCPaymentService;
import ge.boxwood.espace.services.smsservice.GeoSms.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CreditCardService creditCardService;
    @Autowired
    private GCPaymentService gcPaymentService;
    @Autowired
    private CounterRepository counterRepository;

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
        return ResponseEntity.ok(creditCardService.getByUser(user));
    }

    @PutMapping("/cards/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> arrangeCards(@PathVariable("id")Long id, @RequestBody CreditCard creditCard){
        return ResponseEntity.ok(creditCardService.update(creditCard, id));
    }

    @DeleteMapping("/cards/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> deleteCard(@PathVariable("id")Long id){
        creditCardService.delete(id);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/addUserCreditCard")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> confirmCreditCard(HttpServletRequest request){
        String authToken = tokenHelper.getToken( request );
        String username = tokenHelper.getUsernameFromToken(authToken);
        User user = userService.getByUsername(username);
        Order order = new Order(user);
        order.setPaymentType(PaymentType.CREDITCARD);
        order.setPrice(1f);
        orderRepository.save(order);
        Payment payment = new Payment(1f, order);
        paymentRepository.save(payment);
        orderRepository.flush();
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/makerefund/{uuid}")
    public ResponseEntity<?> makeRefund(@PathVariable("uuid")String uuid){
        Payment payment = paymentRepository.findByUuid(uuid);
        return ResponseEntity.ok(gcPaymentService.makeRefund(uuid, 0.5f, payment.getTrxId(), payment.getPrrn()));
    }

//    @PostMapping("/charger/gettransaction")
//    void getTransaction(@RequestBody Map<String, String> data){
//        String trId = data.getOrDefault("transactionId", "0");
//        String chargeTime = data.getOrDefault("chargeTime", "0");
//        String chargePower = data.getOrDefault("chargePower", "0");
//        String consumedPower = data.getOrDefault("consumedPower", "0");
//        Order order = orderRepository.findByChargerTransactionId(Long.valueOf(trId));
//        Charger charger = order.getCharger();
//        List<Counter> counterList = counterRepository.findAllByChargerIdAndChargerTrId(charger.getChargerId(), trId);
//        Counter counter = new Counter();
//        counter.setChargerId(charger.getChargerId());
//        counter.setChargerTrId(trId);
//        counter.setLastUpdate(Calendar.getInstance().getTimeInMillis());
//        counter.setStartTime(Calendar.getInstance().getTimeInMillis());
//        counter.setChargeTime(Long.valueOf(chargeTime));
//        counter.setChargePower(Double.valueOf(chargePower));
//        counter.setConsumedPower(Long.valueOf(consumedPower));
//        counter.setPricing(counterList.get( counterList.size() - 1 ).getCurrentPrice());
//        counterRepository.save(counter);
//    }

    @GetMapping("/charger/gettransaction/{trid}")
    void getTransaction(@PathVariable("trid")String trId){
        chargerService.transaction(Long.valueOf(trId));
    }
}
