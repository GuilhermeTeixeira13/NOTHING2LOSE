package pt.ubi.di.pmd.nothing2lose.utility;

import java.util.Base64;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * The RSASignature class is responsible for generating digital signatures using RSA private keys.
 */
public class RSASignature {
        /**
         * Generates a digital signature for the given data using the provided private key.
         *
         * @param data              The data to be signed.
         * @param privateKeyBase64  The private key in Base64 format.
         * @return                  The generated digital signature as an array of bytes.
         * @throws Exception        If an error occurs during the signature generation process.
         */
        public static byte[] generateDigitalSignature(String data, String privateKeyBase64) throws Exception {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            byte[] dataBytes = data.getBytes();

            Signature signature = Signature.getInstance("SHA256withRSA");

            signature.initSign(privateKey);

            signature.update(dataBytes);

            byte[] digitalSignature = signature.sign();

            return digitalSignature;
        }
}


