package pt.ubi.di.pmd.nothing2lose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KeyGen {
    public static List<byte []> generateKeys() {
        List<byte []> keys = new ArrayList<>();

        // Define the number of bits for different types of awards
        int simplePremiumBits = 17; // 30 sec
        int avgPrizeBits = 18; // 1min
        int rarePremiumBits = 19; // 2.5min
        int legendaryPremiumBits = 20; // 5min

        // Generate keys for different types of awards
        byte[] simplePremiumKey = generateKey(128, simplePremiumBits);
        byte[] avgPrizeKey = generateKey(128, avgPrizeBits);
        byte[] rarePremiumKey = generateKey(128, rarePremiumBits);
        byte[] legendaryPremiumKey = generateKey(128, legendaryPremiumBits);

        // Add the keys to the list
        keys.add(simplePremiumKey);
        keys.add(avgPrizeKey);
        keys.add(rarePremiumKey);
        keys.add(legendaryPremiumKey);

        return keys; // Return the list of generated keys
    }

    public static byte[] convertStringToByteArray(String binaryString) {
        int stringLength = binaryString.length();
        int byteArrayLength = (stringLength + 7) / 8;
        byte[] byteArray = new byte[byteArrayLength];

        for (int i = 0; i < byteArrayLength; i++) {
            int startIndex = i * 8;
            int endIndex = Math.min(startIndex + 8, stringLength);
            String byteString = binaryString.substring(startIndex, endIndex);
            int value = Integer.parseInt(byteString, 2);
            byteArray[i] = (byte) value;
        }

        return byteArray;
    }

    public static byte[] generateKey(int size, int randomBits) {
        StringBuilder sb = new StringBuilder(size);
        Random random = new Random();

        // Append zeros
        for (int i = 0; i < size - randomBits; i++) {
            sb.append('0');
        }

        // Append random bits
        for (int i = 0; i < randomBits; i++) {
            sb.append(random.nextInt(2));
        }

        return convertStringToByteArray(sb.toString());
    }

    public static String byteArrayToString(byte[] bytes) {
        String ret = "";

        for (byte b : bytes) {
            ret += (String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        return ret;
    }
}


