package ge.boxwood.espace.config.utils;

import org.apache.commons.codec.binary.Hex;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ShaUtils {


    public static String hashSHA256ToHex(String param) {
        String value = new String();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] result = digest.digest(param.getBytes(StandardCharsets.UTF_8));
            value = DatatypeConverter.printHexBinary(result);
            value = Hex.encodeHexString(result);
            //value = new BigInteger(1, result).toString(16);

        } catch (Exception ex) {

        }
        return value;
    }
}
