package ge.boxwood.espace.services.gc.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jayway.jsonpath.JsonPath;
import ge.boxwood.espace.models.CreditCard;
import ge.boxwood.espace.models.Order;
import ge.boxwood.espace.models.Payment;
import ge.boxwood.espace.models.User;
import ge.boxwood.espace.models.georgiancard.*;
import ge.boxwood.espace.repositories.OrderRepository;
import ge.boxwood.espace.repositories.PaymentRepository;
import ge.boxwood.espace.services.CreditCardService;
import ge.boxwood.espace.services.gc.GCPaymentService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GCPaymentServiceImpl implements GCPaymentService {
    private static String primaryTrxId = "";
    private static String orderId = "1111122222";
    private static String merchId = "19446110895BF705B1BE0F83FC420564";
    private static String gelAccountId = "BF504CA75DB31020A1B35EDD3F9DBDBF";
    private static String serviceUrl = "https://3dacq.georgiancard.ge/payment/start.wsm";
    private static String lang = "KA";
    private static String back_url_s = "https://www.api.e-space.ge/checkoutComplete";
    private static String back_url_f = "https://www.api.e-space.ge/checkoutFailed";
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CreditCardService creditCardService;

    @Override
    public String initiatePayment(String orderId, String trxId) {
        String redirectUrl = "";
        try {
            redirectUrl = String.format("%1s?lang=%2s&merch_id=%3s&o.mer_trx_id=%4s&back_url_s=%5s&back_url_f=%6s", serviceUrl, lang, merchId, String.valueOf(orderId).trim(),
                    URLEncoder.encode(back_url_s, "UTF-8"), URLEncoder.encode(back_url_f, "UTF-8"));
        } catch (Exception ex) {

        }
        return redirectUrl;
    }

    @Override
    public String checkAvailable(Map<String, String> params) {
        System.out.println("Entered payemnet check");
        PaymentAvailRequest paymentAvailRequest = mapPaymentAvailRequest(params);
        OrderParam orderParam = paymentAvailRequest.params.stream().filter(param -> param.name.equals("mer_trx_id")).findFirst().orElse(null);
        if (orderParam == null) {
            return "orderParam is null:";
        }
        String paymentId = orderParam.value.trim();

        // get Transaction using id here
        // if transaction exists and payment is available then return OK and all the other stuff
        // innerTrnId also goes to response.merchantTrx
        //  if order has attached CreditCard then get that credit card's trxId(i think) and pass to primaryTrxPcid (reccurent)
        //

        Payment payment = paymentRepository.findByUuid(paymentId);

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
            paymentAvailResponse.purchase.longDesc = "Charger fee.";
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
        return xmlString;
    }

    @Override
    public String registerPayment(Map<String, String> params, HttpServletRequest httpRequest) {
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
                    Payment payment = paymentRepository.findByUuid(request.merchantTrx);
                    if (payment != null) {
                        Order order = payment.getOrder();
                        User user = order.getUser();

                        String acquireBankTrnId = request.pRrn; //transaction id in acquirer bank
//                        if ( order.getTargetPrice() - payment.getPrice() > 0  ){
//                            makeRefund(order.getTargetPrice(), payment.getPrice(), request.trxId, acquireBankTrnId);
//                        }
                        Date paymentDate = request.pTransmissionDateTime;
                        payment.setTrxId(request.trxId);
                        payment.setPrrn(acquireBankTrnId);
                        payment.setConfirmDate(paymentDate);
                        payment.setConfirmed(true);
                        payment.confirm();
                        paymentRepository.save(payment);
                        orderRepository.save(order);

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
                            paymentRepository.save(payment);
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
        String responseXml;
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
    @Override
    public String makeRefund(String paymentUUID, Float refundPrice, String trxId, String prnn){
        try{
            Payment payment = paymentRepository.findByUuid(paymentUUID);
            Order order = payment.getOrder();
            order.setRefunded(true);

            URIBuilder builder = new URIBuilder();
            builder.setScheme("https");
            builder.setHost(merchId+":Hep84Fvm83@3dacq.georgiancard.ge");
            builder.setPath("/merchantapi/refund");
            builder.addParameter("trx_id", trxId);
            builder.addParameter("p.rrn", prnn);
            builder.addParameter("amount", String.valueOf(refundPrice * 100));
            URL url = builder.build().toURL();
            System.out.println("REFUND URL:"+url.toString());
            URL obj = new URL(url.toString());

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Charset", "UTF-8");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer xmlResp = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                xmlResp.append(inputLine);
            }

            in.close();

            String xml = xmlResp.toString();
            JSONObject jsonObj = XML.toJSONObject(xml);
            jsonObj.put("responseCode", con.getResponseCode());
            System.out.println("REFUND METHOD");
            System.out.println(getAttribute(jsonObj, "code"));
            payment.confirm();
            paymentRepository.save(payment);
            orderRepository.save(order);
            return getAttribute(jsonObj, "code");
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private String getAttribute(JSONObject json, String path) {
        return JsonPath.read(json.toString(), path);
    }
}
