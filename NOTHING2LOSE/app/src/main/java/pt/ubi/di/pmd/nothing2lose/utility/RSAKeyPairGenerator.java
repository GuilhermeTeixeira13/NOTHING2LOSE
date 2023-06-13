package pt.ubi.di.pmd.nothing2lose.utility;

import java.util.Base64;
import java.security.*;

/**

 The RSAKeyPairGenerator class is responsible for generating RSA key pairs and manipulating the keys.
 */
public class RSAKeyPairGenerator {

    /**
     * Generates an RSA key pair with a key size of 2048 bits.
     *
     * @return The generated KeyPair object.
     * @throws NoSuchAlgorithmException If the RSA algorithm is not available.
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Retrieves the private key from a KeyPair object and returns it as a Base64-encoded string.
     *
     * @param keyPair The KeyPair object containing the private key.
     * @return The private key as a Base64-encoded string.
     */
    public static String getPrivateKeyBase64(KeyPair keyPair) {
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    /**
     * Retrieves the public key from a KeyPair object and returns it as a Base64-encoded string.
     *
     * @param keyPair The KeyPair object containing the public key.
     * @return The public key as a Base64-encoded string.
     */
    public static String getPublicKeyBase64(KeyPair keyPair) {
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }
}



