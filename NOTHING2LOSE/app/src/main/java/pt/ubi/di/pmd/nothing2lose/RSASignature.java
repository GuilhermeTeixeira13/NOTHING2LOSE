package pt.ubi.di.pmd.nothing2lose;

import java.util.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

public class RSASignature {

    public byte[] generateDigitalSignature(String data, String privateKeyBase64) throws Exception {
        // Decodifica a chave privada de Base64 para bytes
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

        // Cria uma instância da chave privada a partir dos bytes
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // Dados a serem assinados
        byte[] dataBytes = data.getBytes();

        // Criação de uma instância do objeto Signature para assinatura digital
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Inicializa o objeto Signature com a chave privada
        signature.initSign(privateKey);

        // Adiciona os dados para assinatura
        signature.update(dataBytes);

        // Gera a assinatura digital
        byte[] digitalSignature = signature.sign();

        return digitalSignature;
    }
}
