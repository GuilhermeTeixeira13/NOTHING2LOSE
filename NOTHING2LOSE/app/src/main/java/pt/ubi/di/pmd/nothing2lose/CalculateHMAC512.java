package pt.ubi.di.pmd.nothing2lose;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CalculateHMAC512 {

    public static byte[] calculateHMAC(SecretKey key, Award award) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");

            // Create a SecretKeySpec object using the key
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "HmacSHA512");

            // Initialize the Mac object with the key
            mac.init(keySpec);

            // Calculate the HMAC for the Award object
            byte[] hmac = mac.doFinal(award.toByteArray());

            return hmac;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // Handle exceptions here
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Converts a byte array to a hexadecimal string
     * @param bytes the byte array to convert
     * @return a hexadecimal string representing the byte array
     */
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
