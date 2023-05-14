package pt.ubi.di.pmd.nothing2lose;


import android.util.Log;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class KeyGen {
    public List<byte []> generateKeys() {
        List<byte []> keys = new ArrayList<>();

        int simplePremiumBits = 20;
        int avgPrizeBits = 21;
        int rarePremiumBits = 22;
        int legendaryPremiumBits = 23;

        byte[] simplePremiumKey = createKey(128, simplePremiumBits);

        byte[] avgPrizeKey = createKey(128, avgPrizeBits);

        byte[] rarePremiumKey = createKey(128, rarePremiumBits);

        byte[] legendaryPremiumKey = createKey(128, legendaryPremiumBits);

        keys.add(simplePremiumKey);
        keys.add(avgPrizeKey);
        keys.add(rarePremiumKey);
        keys.add(legendaryPremiumKey);

        return keys;
    }

    public static byte[] createKey(int keySize, int randomSize) {
        byte[] key = new byte[keySize/8]; // Create a byte array for the key
        SecureRandom random = new SecureRandom(); // Create a secure random number generator

        // Fill the first x bits with random data
        int randomBytes = randomSize/8;
        byte[] randomData = new byte[randomBytes];
        random.nextBytes(randomData);
        System.arraycopy(randomData, 0, key, 0, randomBytes);

        // Set the remaining bits to 0
        for (int i = randomBytes; i < key.length; i++) {
            key[i] = 0;
        }

        return key; // Return the generated key
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}


