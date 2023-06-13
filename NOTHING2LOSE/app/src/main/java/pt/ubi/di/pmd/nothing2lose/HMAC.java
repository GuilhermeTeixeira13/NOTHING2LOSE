package pt.ubi.di.pmd.nothing2lose;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * The HMAC class provides methods for generating keys and calculate HMACS.
 * */
public class HMAC {

    /**
     * Calculates the HMAC256 for an Award using the provided key
     *
     * @param key the key to use for the HMAC calculation
     * @param award the Award object to be hashed
     * @return a byte array containing the HMAC for the Award object
     */
    public static byte[] calculateHMAC256(SecretKey key, Award award) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            // Create a SecretKeySpec object using the key
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "HmacSHA256");

            // Initialize the Mac object with the key
            mac.init(keySpec);

            // Calculate the HMAC for the Award object
            return mac.doFinal(award.toByteArray());
        } catch (Exception e) {
            // Handle exceptions here
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Calculates the HMAC256 for an Award using the provided key
     *
     * @param key the key to use for the HMAC calculation
     * @param award the Award object to be hashed
     * @return a byte array containing the HMAC for the Award object
     */
    public static byte[] calculateHMAC512(SecretKey key, Award award) {
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
     * Generates a secret key using the HmacSHA256 algorithm.
     *
     * @return SecretKey - the generated secret key.
     */
    public static SecretKey generateSecretKeyHmacSHA256() {
        SecretKey secretKey = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256);
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    /**
     * Generates a secret key using the HmacSHA512 algorithm.
     *
     * @return SecretKey - the generated secret key.
     */
    public static SecretKey generateSecretKeyHmacSHA512() {
        SecretKey secretKey = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA512");
            keyGen.init(512);
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    /**
     * Converts a byte array to a hexadecimal string
     *
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

