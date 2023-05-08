package pt.ubi.di.pmd.nothing2lose;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherAwards {

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static List<byte[]> encrypt(List<byte[]> keys, ArrayList<Award> awards) throws Exception {
        List<byte[]> cifrados = new ArrayList<>();
        //IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        for (int i = 0; i < 4 ; i++) {
            Award award = awards.get(i);
            byte[] Key = keys.get(i);
            SecretKeySpec sks = new SecretKeySpec(Key, CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(sks.getEncoded()));
            byte[] cifradoValue = cipher.doFinal(award.toByteArray());
            cifrados.add(cifradoValue);
        }
        return cifrados;
    }
}

