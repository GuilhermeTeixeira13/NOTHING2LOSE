package pt.ubi.di.pmd.nothing2lose;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSASignatureVerification {

    public boolean verifyDigitalSignature(String data, String publicKeyBase64, byte[] signatureBytes) throws Exception {
        // Decodifica a chave pública e a assinatura de Base64 para bytes
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);;

        // Cria uma instância da chave pública a partir dos bytes
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // Criação de uma instância do objeto Signature para verificação da assinatura
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Inicializa o objeto Signature com a chave pública
        signature.initVerify(publicKey);

        // Adiciona os dados originais
        byte[] dataBytes = data.getBytes();
        signature.update(dataBytes);

        // Verifica a assinatura digital
        boolean signatureVerified = signature.verify(signatureBytes);

        return signatureVerified;
    }
}
