package pt.ubi.di.pmd.nothing2lose.utility;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * The RSASignatureVerification class is responsible for verifying digital signatures using RSA public keys.
 */
public class RSASignatureVerification {

    /**
     * Verifies a digital signature using RSA.
     *
     * @param data              The original data as a string.
     * @param publicKeyBase64   The public key in Base64 format.
     * @param signatureBytes    The signature as an array of bytes.
     * @return                  True if the signature is valid, false otherwise.
     * @throws Exception        If an error occurs during the verification process.
     */
    public static boolean verifyDigitalSignature(String data, String publicKeyBase64, byte[] signatureBytes) throws Exception {
        // Decode the public key and signature from Base64 to bytes
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);

        // Create an instance of the public key from the bytes
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // Create an instance of the Signature object for signature verification
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Initialize the Signature object with the public key
        signature.initVerify(publicKey);

        // Add the original data
        byte[] dataBytes = data.getBytes();
        signature.update(dataBytes);

        // Verify the digital signature
        boolean signatureVerified = signature.verify(signatureBytes);

        return signatureVerified;
    }
}

