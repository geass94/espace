package ge.boxwood.espace.controllers;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ge.boxwood.espace.models.CreditCard;
import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.Payment;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.georgiancard.*;
import ge.boxwood.espace.repositories.OrderRepository;
import ge.boxwood.espace.repositories.PaymentRepository;
import ge.boxwood.espace.repositories.UserRepository;
import ge.boxwood.espace.services.CommunicationService;
import ge.boxwood.espace.services.CreditCardService;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.text.DecimalFormat;

@Controller
public class PaymentController {

    private static String primaryTrxId = "";
    private static String orderId = "1111122222";
    private String merchId = "19446110895BF705B1BE0F83FC420564";
    private String gelAccountId = "BF504CA75DB31020A1B35EDD3F9DBDBF";

    @Autowired
    private CreditCardService creditCardService;
    @Autowired
    private CommunicationService communicationService;
    @Autowired
    private PaymentRepository paymentsRepo;
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private UserRepository userRepo;

    @GetMapping(path = "/payment/initiatePayment")
    public void initiatePayment(@RequestParam String orderId, HttpServletResponse response) throws IOException {
        String serviceUrl = "https://3dacq.georgiancard.ge/payment/start.wsm";
        String lang = "KA";

        PaymentController.orderId = orderId.trim();
        String back_url_s = "https://www.api.e-space.ge/checkoutComplete?store=undefined&region=1";
        String back_url_f = "https://www.api.e-space.ge/checkoutFailed?store=undefined&region=1";

        //TODO register order here and get unique id
        // merTrxId = addOrder(Order) /returns transaction id (unique in merchant system)

        String redirectUrl = "";
        try {
            redirectUrl = String.format("%1s?lang=%2s&merch_id=%3s&o.mer_trx_id=%4s&back_url_s=%5s&back_url_f=%6s", serviceUrl, lang, merchId, String.valueOf(orderId).trim(),
                    URLEncoder.encode(back_url_s, "UTF-8"), URLEncoder.encode(back_url_f, "UTF-8"));
        } catch (Exception ex) {

        }
        System.out.println("Redirecting to payment page!");
        System.out.println(redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping(path = "/payments/check", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String checkAvailable(@RequestParam Map<String, String> params, HttpServletRequest request) {
        PaymentAvailRequest paymentAvailRequest = mapPaymentAvailRequest(params);
        OrderParam orderParam = paymentAvailRequest.params.stream().filter(param -> param.name.equals("mer_trx_id")).findFirst().orElse(null);
        if (orderParam == null) {
            return "orderParam is null:";
        }
        String paymentId = orderParam.value.trim();
        System.out.println("Enetered payement check!");

        // get Transaction using id here
        // if transaction exists and payment is available then return OK and all the other stuff
        // innerTrnId also goes to response.merchantTrx
        //  if order has attached CreditCard then get that credit card's trxId(i think) and pass to primaryTrxPcid (reccurent)
        //

        Payment payment = paymentsRepo.findByUuid(paymentId);

        PaymentAvailResponse paymentAvailResponse = new PaymentAvailResponse();
        if (payment == null) {
            paymentAvailResponse.result.code = 2L;
        } else if (!payment.isConfirmed()) {
            Order order = payment.getOrder();
            User user = order.getUser();

            paymentAvailResponse.result.code = 1L;
            paymentAvailResponse.result.desc = "OK";
            paymentAvailResponse.merchantTrx = String.valueOf(payment.getUuid());
            paymentAvailResponse.purchase.shortDesc = String.valueOf(order.getId());
            paymentAvailResponse.purchase.longDesc = "პროდუქტები პროდუქტები";
            paymentAvailResponse.purchase.accountAmount.id = gelAccountId;
            DecimalFormat df = new DecimalFormat("#.#");

            paymentAvailResponse.purchase.accountAmount.amount = BigDecimal.valueOf(Long.parseLong(df.format( payment.getPrice() * 100 )));
            paymentAvailResponse.purchase.accountAmount.currencyCode = "981";
            paymentAvailResponse.purchase.accountAmount.exponent = "2";
            //paymentAvailResponse.purchase.accountAmount.fee = new BigDecimal(order.);
            paymentAvailResponse.card = null;
            if (payment.getCreditCard() != null) {
                paymentAvailResponse.primaryTrxPcid = payment.getCreditCard().getTrxId();
                //paymentAvailResponse.transactionType = "CardRegister";
            } else {
                //paymentAvailResponse.transactionType = "CardRegister";
            }

            //paymentAvailResponse.card.ref = "Nothing Now";
            //paymentAvailResponse.card.present = ""; // Y or N

            OrderParam phoneParam = new OrderParam();
            phoneParam.name = "phone";
            phoneParam.value = user.getPhoneNumber();

            OrderParam amountParam = new OrderParam();
            amountParam.name = "amount";
            amountParam.value = String.valueOf(payment.getPrice());

//            OrderParam extendParam = new OrderParam();
//            amountParam.name = "extended3DSResults";
//            amountParam.value = "True";

            paymentAvailResponse.orderParams.add(phoneParam);
            paymentAvailResponse.orderParams.add(amountParam);
            //paymentAvailResponse.orderParams.add(extendParam);
        }

        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(true);
        ObjectMapper xmlMapper = new XmlMapper(module);
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String xmlString = "";
        try {
            xmlString = xmlMapper.writeValueAsString(paymentAvailResponse);
        } catch (Exception ex) {

        }

        System.out.println(xmlString);

        return xmlString;
    }

    @GetMapping(path = "/payments/register", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String registerPayment(@RequestParam Map<String, String> params, HttpServletRequest httpRequest) {
        RegisterPaymentRequest request = mapRegisterPaymentRequest(params);
        RegisterPaymentResponse response = new RegisterPaymentResponse();

        ObjectMapper xmlMapper = new XmlMapper();
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            if (request == null) {
                response.result.desc = "Merchant error";
                System.out.println("Request Mapping error");
                throw new RuntimeException();
            }
            System.out.println("Signature started");
            StringBuffer requestURL = httpRequest.getRequestURL();
            String queryString = httpRequest.getQueryString();
            System.out.println(requestURL.append("?"+queryString));
            ResourceLoader resourceLoader = new DefaultResourceLoader();
//            try {
//                System.out.println("Signature try block entered");
//                KeyStore trustStore = KeyStore.getInstance("JKS");
//                InputStream certInStream = resourceLoader.getClassLoader().getResourceAsStream("certs/truststore.jks");
//                trustStore.load(certInStream, "moitanetrust123@".toCharArray());
//
//                StringBuffer requestURL = httpRequest.getRequestURL();
//                String queryString = httpRequest.getQueryString();
//                System.out.println(requestURL.append(queryString));
//
//                String message = removeQueryParameter(requestURL.append('?').append(queryString).toString(), "signature");
//                byte[] messageBytes = message.getBytes(Charset.forName("utf-8"));
//                System.out.println(message);
//
//
//                String signature = request.signature;
//                byte[] signatureBytes = Base64.decodeBase64(signature.getBytes("utf-8"));
//
//                X509Certificate certGc = (X509Certificate) trustStore.getCertificate("gc");
//
//                Signature sig = Signature.getInstance("SHA512withRSA");
//                System.out.println("Signature instance created");
//                sig.initVerify(certGc.getPublicKey());
//                System.out.println("Signature initVerify done");
//                sig.update(messageBytes);
//                System.out.println("Signature update");
//                boolean verified = sig.verify(signatureBytes);
//                System.out.println("Signature verified");
//
//                if (!verified) {
//                    System.out.println("Signature Unauthorized entered");
//                    response.result.desc = "Unauthorized";
//                    throw new RuntimeException();
//                }
//
//            } catch (Exception ex) {
//                System.out.println(ex.getCause().getMessage());
//                ex.printStackTrace();
//                System.out.println("Signature catch block entered");
//                if (!response.result.desc.equals("Unauthorized")){
//                    response.result.desc = "Athorization error";
//                    System.out.println("Signature Unauthorized entered");
//                }
//                throw new RuntimeException();
//            }

            System.out.println("Signature passed");

            if (request.merchId.equals(merchId) && request.resultCode.equals("1")) {
                if (!StringUtils.isEmpty(request.merchantTrx)) {
                    Payment payment = paymentsRepo.findByUuid(request.merchantTrx);
                    if (payment != null) {
                        Order order = payment.getOrder();
                        User user = order.getUser();

                        String acquireBankTrnId = request.pRrn; //transaction id in acquirer bank

                        Date paymentDate = request.pTransmissionDateTime;
                        payment.setConfirmDate(paymentDate);
                        payment.setConfirmed(true);
                        payment.confirm();
                        paymentsRepo.save(payment);
                        order.setActive(true);
                        orderRepo.save(order);



                        CreditCard creditCard = creditCardService.findByUserAndMaskedPan(user, request.pMaskedPan);
                        if (creditCard == null) {
                            creditCard = new CreditCard();
                            creditCard.setUser(user);
                            creditCard.setCardHolder(request.pCardHolder);
                            creditCard.setExpDate(request.pStorageCardExpDt);
                            creditCard.setMaskedPan(request.pMaskedPan);
                            creditCard.setTrxId(request.trxId);
                            creditCard.setCardRef(request.pStorageCardRef);
                            creditCard = creditCardService.create(creditCard);

                            payment.setCreditCard(creditCard);
                            paymentsRepo.save(payment);
                        }
                    } else {
                        response.result.desc = "No such payment";
                        throw new RuntimeException();
                    }
                } else {
                    response.result.desc = "No such payment";
                    throw new RuntimeException();
                }
            } else if (request.merchId.equals(merchId) && request.resultCode.equals("2")) {
                System.out.println("Payment error block entered");
                response.result.desc = "Unsuccessful";
                throw new RuntimeException();
            }

            response.result.code = 1L;
            response.result.desc = "OK";

        } catch (Exception ex) {
            System.out.println("Main catch: " + ex.getMessage());
            response.result.code = 2L;
            response.result.desc = StringUtils.isEmpty(response.result.desc) ? "Merchant Error" : response.result.desc;
        }
        String responseXml = "";
        try {
            responseXml = xmlMapper.writeValueAsString(response);
        } catch (Exception ex) {
            System.out.println("Response Mapping error");

            responseXml =
                    "<register-payment-response>\n" +
                            " <result>\n" +
                            " <code>2</code>\n" +
                            " <desc>Merchant error</desc>\n" +
                            " </result>\n" +
                            "</register-payment-response>";
        }

        System.out.println(responseXml);
        return responseXml;
    }

    private String removeQueryParameter(String url, String parameterName) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        List<NameValuePair> queryParameters = uriBuilder.getQueryParams();
        for (Iterator<NameValuePair> queryParameterItr = queryParameters.iterator(); queryParameterItr.hasNext(); ) {
            NameValuePair queryParameter = queryParameterItr.next();
            if (queryParameter.getName().equals(parameterName)) {
                queryParameterItr.remove();
            }
        }
        uriBuilder.setParameters(queryParameters);
        return uriBuilder.build().toString();
    }


    private RegisterPaymentRequest mapRegisterPaymentRequest(Map<String, String> params) {
        DateFormat df = new SimpleDateFormat("MMddHHmmss");
        DateFormat expDF = new SimpleDateFormat("YYMM");
        DateFormat tsDF = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        RegisterPaymentRequest request = new RegisterPaymentRequest();
        request.merchId = params.get("merch_id");
        request.trxId = params.get("trx_id");
        request.merchantTrx = params.get("merchant_trx");
        request.resultCode = params.get("result_code");
        try {
            request.amount = new BigDecimal(params.get("amount"));
        } catch (Exception ex) {
            request.amount = BigDecimal.ZERO;
        }
        request.accountId = params.get("account_id");
        request.pRrn = params.get("p.rrn");
        try {
            Date date = df.parse(params.get("p.transmissionDateTime"));
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            c.setTime(date);
            c.set(Calendar.YEAR, year);
            request.pTransmissionDateTime = c.getTime();

            date = tsDF.parse(params.get("ts"));
            request.ts = date;
        } catch (Exception ex) {

        }

        try {
            request.signature = params.get("signature");
            //request.signature = request.signature.replaceAll("\r", "").replaceAll("\n", "");
        } catch (Exception ex) {

        }
        request.pCardHolder = params.get("p.cardholder");
        request.pAuthCode = params.get("p.authcode");
        request.pMaskedPan = params.get("p.maskedPan");
        request.pIsFullyAuthenticated = params.get("p.isFullyAuthenticated");
        request.p3DSPAResTXcavv = params.get("p.3DS.PARes.TX.cavv");
        request.p3DSPAResTXeci = params.get("p.3DS.PARes.TX.eci");
        request.p3DSPAResPurchasexid = params.get("p.3DS.PARes.Purchase.xid");
        request.pStorageCardRef = params.get("p.storage.card.ref");
        request.pStorageCardExpDt = params.get("p.storage.card.expDt");
        request.pStorageCardRecurrent = params.get("p.storage.card.recurrent");
        request.pStorageCardRegistered = params.get("p.storage.card.registered");
        request.extResultCode = params.get("ext_result_code");

        request.initStageParams = params.entrySet().stream().filter(entry -> entry.getKey().startsWith("o.")).map(entry -> {
            OrderParam orderParam = new OrderParam();
            orderParam.name = entry.getKey().substring(2);
            orderParam.value = entry.getValue();
            return orderParam;
        }).collect(Collectors.toList());

        request.firstStageParams = params.entrySet().stream().filter(entry -> entry.getKey().startsWith("m.")).map(entry -> {
            OrderParam orderParam = new OrderParam();
            orderParam.name = entry.getKey().substring(2);
            orderParam.value = entry.getValue();
            return orderParam;
        }).collect(Collectors.toList());

        return request;
    }

    private PaymentAvailRequest mapPaymentAvailRequest(Map<String, String> params) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

        PaymentAvailRequest request = new PaymentAvailRequest();
        request.merchId = params.get("merch_id");
        request.trxId = params.get("trx_id");
        request.langCode = params.get("lang_code");
        try {
            request.ts = df.parse(params.get("ts"));
        } catch (Exception ex) {

        }
        request.params = params.entrySet().stream().filter(entry -> entry.getKey().startsWith("o.")).map(entry -> {
            OrderParam orderParam = new OrderParam();
            orderParam.name = entry.getKey().substring(2);
            orderParam.value = entry.getValue();
            return orderParam;
        }).collect(Collectors.toList());

        return request;
    }


    @RequestMapping("/confirmpayment")
    @ResponseBody
    public boolean confirmPayment(@RequestParam(value = "token", required = true, defaultValue = "") String token,
                                  @RequestParam(value = "id", required = true, defaultValue = "") String id) {
        Payment payment = paymentsRepo.findByUuid(id);
        payment.confirm();
        paymentsRepo.save(payment);
        return true;
    }
}