package pt.ubi.di.pmd.nothing2lose;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class KeyGen {
    private static final int SIMPLE_KEY_BITS = 20;
    private static final int MEDIUM_KEY_BITS = 21;
    private static final int RARE_KEY_BITS = 22;
    private static final int LEGENDARY_KEY_BITS = 23;
    private int tamanhoChave;
    private int tamanhoAleatorio;
    private byte[] chave;
    private byte[] chaveSimples;
    private byte[] chaveMedia;
    private byte[] chaveRara;
    private byte[] chaveLendaria;

    public byte[] ChaveCifra(int tamanhoAleatorio, int tamanhoZeros) {
        this.tamanhoAleatorio = tamanhoAleatorio;
        this.tamanhoChave = tamanhoAleatorio + tamanhoZeros;
        this.chave = new byte[tamanhoChave / 8];
        return this.chave;
    }

    public byte[] gerarChave(byte[] chave) {
        SecureRandom random = new SecureRandom();
        byte[] aleatorio = new byte[tamanhoAleatorio / 8];
        random.nextBytes(aleatorio);
        System.arraycopy(aleatorio, 0, chave, 0, aleatorio.length);
        return chave;
    }

    public List<byte[]> generateKeys() {
        SecureRandom random = new SecureRandom();
        List<byte[]> keys = new ArrayList<>();

        // Gerar chave Simples
        chaveSimples = ChaveCifra(SIMPLE_KEY_BITS, 108);
        byte[] chaveSimplesGerada = gerarChave(chaveSimples);
        keys.add(chaveSimplesGerada);

        // Gerar chave Media
        chaveMedia = ChaveCifra(MEDIUM_KEY_BITS, 107);
        byte[] chaveMedioGerada = gerarChave(chaveMedia);
        keys.add(chaveMedioGerada);

        // Gerar chave Rara
        chaveRara = ChaveCifra(RARE_KEY_BITS, 106);
        byte[] chaveRaroGerada = gerarChave(chaveRara);
        keys.add(chaveRaroGerada);

        // Gerar chave Lendaria
        chaveLendaria = ChaveCifra(LEGENDARY_KEY_BITS, 105);
        byte[] chaveLendarioGerada = gerarChave(chaveLendaria);
        keys.add(chaveLendarioGerada);



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

