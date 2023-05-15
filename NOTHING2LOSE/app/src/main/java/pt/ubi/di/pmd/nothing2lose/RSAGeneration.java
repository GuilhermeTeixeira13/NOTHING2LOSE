package pt.ubi.di.pmd.nothing2lose;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RSAGeneration {


    public String generatePrivateKeyBase64() throws NoSuchAlgorithmException {
        // Criação de uma instância do gerador de chaves RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        // Definir o tamanho da chave (por exemplo, 2048 bits)
        keyPairGenerator.initialize(2048);

        // Gerar o par de chaves RSA
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Obter a chave privada em bytes
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();

        // Converter a chave privada para Base64
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    public String generatePublicKeyBase64() throws NoSuchAlgorithmException {
        // Criação de uma instância do gerador de chaves RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        // Definir o tamanho da chave (por exemplo, 2048 bits)
        keyPairGenerator.initialize(2048);

        // Gerar o par de chaves RSA
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Obter a chave pública em bytes
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();

        // Converter a chave pública para Base64
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }
}

