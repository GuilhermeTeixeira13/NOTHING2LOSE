package pt.ubi.di.pmd.nothing2lose;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecrypt {

    // The algorithm used for encryption and decryption
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Encrypts an Award object with the specified encryption key.
     *
     * @param key the encryption key as a byte array
     * @param award the Award object to be encrypted
     * @return a byte array that represents the encrypted Award object
     * @throws Exception if there is an error during encryption
     */
    public static byte[] encryptAward(byte[] key, Award award) throws Exception {
        // Create a Cipher object with the specified algorithm
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // Create a SecretKeySpec object from the encryption key
        SecretKeySpec sks = new SecretKeySpec(key, CIPHER_ALGORITHM);
        // Initialize the Cipher object with the encryption key and an initialization vector generated from the encryption key
        cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(sks.getEncoded()));
        // Encrypt the Award object and return the encrypted data as a byte array
        return cipher.doFinal(award.toByteArray());
    }

    /**
     * Decrypts an encrypted Award object with the specified decryption key.
     *
     * @param EncKey the decryption key as a byte array
     * @param encryptedAward a byte array that represents the encrypted Award object
     * @return an Award object that represents the decrypted Award object
     * @throws Exception if there is an error during decryption or deserialization
     */
    public static Award decryptAward(byte[] EncKey, byte[] encryptedAward) throws Exception {
        // Create a Cipher object with the specified algorithm
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // Create a SecretKeySpec object from the decryption key
        SecretKeySpec sks = new SecretKeySpec(EncKey, CIPHER_ALGORITHM);
        // Initialize the Cipher object with the decryption key and an initialization vector generated from the decryption key
        cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(sks.getEncoded()));
        // Decrypt the encrypted Award object and deserialize it to an Award object
        byte[] decryptedBytes = cipher.doFinal(encryptedAward);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decryptedBytes));
        Award DecryptedAward = (Award) ois.readObject();
        // Return the decrypted Award object
        return DecryptedAward;
    }
}


