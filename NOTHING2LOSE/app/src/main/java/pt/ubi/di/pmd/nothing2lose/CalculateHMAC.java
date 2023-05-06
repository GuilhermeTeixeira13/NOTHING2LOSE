package pt.ubi.di.pmd.nothing2lose;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CalculateHMAC {
    Mac[] hmacs = new Mac[4];

    public List<byte[]> claculateHmac(List keys, ArrayList awards) {
        String[] prizes;
        List<byte[]> hmacS = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            prizes = (String[]) awards.get(i);
            try {
                hmacs[i] = Mac.getInstance("HmacSHA256");
                hmacs[i].init((Key) keys.get(i));
                byte[] hmacBytes = hmacs[i].doFinal(prizes[i].getBytes());
                hmacS.add(hmacBytes);

                String hmac = Base64.getEncoder().encodeToString(hmacBytes);
                System.out.println(prizes[i] + ": " + hmac);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return hmacS;
    }

    public String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}
