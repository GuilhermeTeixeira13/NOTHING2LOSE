package pt.ubi.di.pmd.nothing2lose;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecrypt {

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static byte[] encryptAward(byte[] key, Award award) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(key, CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(sks.getEncoded()));

        return cipher.doFinal(award.toByteArray());
    }

    public static Award decryptAward(byte[] EncKey, byte[] encryptedAward) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(EncKey, CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(sks.getEncoded()));
        byte[] decryptedBytes = cipher.doFinal(encryptedAward);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decryptedBytes));
        Award DecryptedAward = (Award) ois.readObject();
        return DecryptedAward;
    }
}

