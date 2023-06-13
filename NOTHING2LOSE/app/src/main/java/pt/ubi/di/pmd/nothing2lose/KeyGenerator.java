package pt.ubi.di.pmd.nothing2lose;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The KeyGenerator class provides methods for generating keys used in the application.
 * */
public class KeyGenerator {
    /**
     * Generates keys for different types of awards.
     *
     * @return A list of byte arrays representing the generated keys.
     */
    public static List<byte []> generateKeys() {
        List<byte []> keys = new ArrayList<>();

        // Define the number of bits for different types of awards
        int simplePremiumBits = 17; // Decryption time > 30 sec
        int avgPrizeBits = 18; // Decryption time > 1min
        int rarePremiumBits = 19; // Decryption time > 2.5min
        int legendaryPremiumBits = 20; // Decryption time > 5min

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

    /**
     * Converts a binary string to a byte array.
     *
     * @param binaryString The binary string to be converted.
     * @return The byte array representing the converted binary string.
     */
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

    /**
     * Generates a key of the specified size with a specified number of random bits.
     *
     * @param size        The total size of the key in bits.
     * @param randomBits  The number of random bits in the key.
     * @return The byte array representing the generated key.
     */
    public static byte[] generateKey(int size, int randomBits) {
        StringBuilder sb = new StringBuilder(size);
        Random random = new Random();

        // Append zeros
        for (int i = 0; i < size - randomBits; i++) { sb.append('0');}

        // Append random bits
        for (int i = 0; i < randomBits; i++) { sb.append(random.nextInt(2));}

        return convertStringToByteArray(sb.toString());
    }

    /**
     * Converts a byte array to a binary string representation.
     *
     * @param bytes The byte array to be converted.
     * @return The binary string representation of the byte array.
     */
    public static String byteArrayToString(byte[] bytes) {
        String ret = "";

        for (byte b : bytes) { ret += (String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));}

        return ret;
    }
}


