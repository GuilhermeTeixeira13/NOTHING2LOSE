package pt.ubi.di.pmd.nothing2lose;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class KeyGen {
    private static final int SIMPLE_KEY_BITS = 20;
    private static final int MEDIUM_KEY_BITS = 21;
    private static final int RARE_KEY_BITS = 22;
    private static final int LEGENDARY_KEY_BITS = 23;

    public List<byte[]> generateKeys() {
        SecureRandom random = new SecureRandom();
        List<byte[]> keys = new ArrayList<>();

        // Gerar chave simples
        byte[] simpleKeyBytes = new byte[(SIMPLE_KEY_BITS + 7) / 8];
        random.nextBytes(simpleKeyBytes);
        byte[] simpleKey = new byte[SIMPLE_KEY_BITS / 8 + 13];
        System.arraycopy(simpleKeyBytes, 0, simpleKey, 0, simpleKeyBytes.length);
        keys.add(simpleKey);

        // Gerar chave média
        byte[] mediumKeyBytes = new byte[(MEDIUM_KEY_BITS + 7) / 8];
        random.nextBytes(mediumKeyBytes);
        byte[] mediumKey = new byte[MEDIUM_KEY_BITS / 8 + 13];
        System.arraycopy(mediumKeyBytes, 0, mediumKey, 0, mediumKeyBytes.length);
        keys.add(mediumKey);

        // Gerar chave rara
        byte[] rareKeyBytes = new byte[(RARE_KEY_BITS + 7) / 8];
        random.nextBytes(rareKeyBytes);
        byte[] rareKey = new byte[RARE_KEY_BITS / 8 + 13];
        System.arraycopy(rareKeyBytes, 0, rareKey, 0, rareKeyBytes.length);
        keys.add(rareKey);

        // Gerar chave lendária
        byte[] legendaryKeyBytes = new byte[(LEGENDARY_KEY_BITS + 7) / 8];
        random.nextBytes(legendaryKeyBytes);
        byte[] legendaryKey = new byte[LEGENDARY_KEY_BITS / 8 + 13];
        System.arraycopy(legendaryKeyBytes, 0, legendaryKey, 0, legendaryKeyBytes.length);
        keys.add(legendaryKey);

        return keys;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }
}

