package pt.ubi.di.pmd.nothing2lose;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecrypt {
    private static final String AES_CTR_ALGORITHM = "AES/CTR/NoPadding";
    private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Encrypts an Award object with the specified encryption key AES CTR ALGORITHM.
     *
     * @param key the encryption key as a byte array
     * @param award the Award object to be encrypted
     * @return a byte array that represents the encrypted Award object
     * @throws Exception if there is an error during encryption
     */
    public static byte[] encryptAwardCTR(byte[] key, Award award) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CTR_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(key, AES_CTR_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(sks.getEncoded()));
        return cipher.doFinal(award.toByteArray());
    }

    /**
     * Decrypts an encrypted Award object with the specified decryption key using AES CTR ALGORITHM.
     *
     * @param EncKey the decryption key as a byte array
     * @param encryptedAward a byte array that represents the encrypted Award object
     * @return an Award object that represents the decrypted Award object
     * @throws Exception if there is an error during decryption or deserialization
     */
    public static Award decryptAwardCTR(byte[] EncKey, byte[] encryptedAward) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CTR_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(EncKey, AES_CTR_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(sks.getEncoded()));
        byte[] decryptedBytes = cipher.doFinal(encryptedAward);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decryptedBytes));
        return (Award) ois.readObject();
    }

    /**
     * Encrypts an Award object with the specified encryption key AES CBC ALGORITHM.
     *
     * @param key the encryption key as a byte array
     * @param award the Award object to be encrypted
     * @return a byte array that represents the encrypted Award object
     * @throws Exception if there is an error during encryption
     */
    public static byte[] encryptAwardCBC(byte[] key, Award award) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(key, AES_CBC_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(sks.getEncoded()));
        return cipher.doFinal(award.toByteArray());
    }

    /**
     * Decrypts an encrypted Award object with the specified decryption key using AES CBC ALGORITHM.
     *
     * @param EncKey the decryption key as a byte array
     * @param encryptedAward a byte array that represents the encrypted Award object
     * @return an Award object that represents the decrypted Award object
     * @throws Exception if there is an error during decryption or deserialization
     */
    public static Award decryptAwardCBC(byte[] EncKey, byte[] encryptedAward) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(EncKey, AES_CBC_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(sks.getEncoded()));
        byte[] decryptedBytes = cipher.doFinal(encryptedAward);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decryptedBytes));
        return (Award) ois.readObject();
    }
}


