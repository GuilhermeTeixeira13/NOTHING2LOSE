package pt.ubi.di.pmd.nothing2lose;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CalculateHMAC512 {
    Mac[] hmacs = new Mac[4];

    public List<byte[]> calcHMAC(SecretKey key, ArrayList<Award> awards) {
        List<byte[]> hmacS = new ArrayList<>();

        for (int i = 0; i < 4 ; i++){
            Award award = awards.get(i);

            try {
                // Create an HMAC instance with the SHA-512 algorithm
                Mac hmac = Mac.getInstance("HmacSHA512");

                // Create a SecretKeySpec with the current key and algorithm
                SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "HmacSHA512");

                // Initialize the HMAC with the key
                hmac.init(keySpec);

                // Calculate the HMAC of the award
                byte[] hmacValue = hmac.doFinal(award.toByteArray());

                // Add the HMAC to the result list
                hmacS.add(hmacValue);

            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                // Handle exceptions here
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return hmacS;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
