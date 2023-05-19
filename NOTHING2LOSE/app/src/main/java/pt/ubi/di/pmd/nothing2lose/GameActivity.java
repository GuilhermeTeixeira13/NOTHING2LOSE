package pt.ubi.di.pmd.nothing2lose;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.util.Log;
import android.view.View;
import android.widget.Button;


public class GameActivity extends AppCompatActivity {

    Button AwardABtn;
    Button AwardBBtn;
    Button AwardCBtn;
    Button AwardDBtn;

    ArrayList<Award> awards;
    List<byte[]> encKeys;
    List<byte[]> hmacs;
    List<SecretKey> hmacKeys;
    List<byte[]> encAwards;
    List<byte[]> digitalSignaturesList;
    List<String> publicKeysList;
    List<String> privateKeysList;

    ArrayList<Integer> listShuffled;

    String hmacChoice;
    String aesChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        hmacChoice = (String) getIntent().getSerializableExtra("HMAC");
        aesChoice = (String) getIntent().getSerializableExtra("AES");

        // Find the award buttons in the layout.
        AwardABtn = (Button) findViewById(R.id.awardA);
        AwardBBtn = (Button) findViewById(R.id.awardB);
        AwardCBtn = (Button) findViewById(R.id.awardC);
        AwardDBtn = (Button) findViewById(R.id.awardD);

        Log.d("MyApp", "App started."); // Log that the app has started.

        // Initialize variables for the awards.
        double lambda = 5;
        double minPrize = 0.0;
        double maxPrize = 1000.0;

        // Generate a list of awards.
        awards = createAwards(lambda, minPrize, maxPrize);
        Log.d("MyApp", "Awards sorted: " + awards.toString()); // Log the sorted list of awards.

        // Generate encryption keys using KeyGen class.
        encKeys = KeyGen.generateKeys();
        for (byte[] key : encKeys) {
            String keyString = KeyGen.byteArrayToHexString(key);
            Log.d("MyApp", "ENC KEY = " + keyString);
        }

        // Generate secret keys for HMACs.
        hmacKeys = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            SecretKey HMACkey = generateSecretKey();
            hmacKeys.add(HMACkey);
            Log.d("MyApp", "HMAC KEY = " + Base64.getEncoder().encodeToString(HMACkey.getEncoded()));
        }

        // Generate HMAC for each award
        hmacs = new ArrayList<>();
        byte[] hmac;
        for (int i = 0; i < 4; i++) {
            if (hmacChoice.equals("HMAC256"))
                hmac = HMAC.calculateHMAC256(hmacKeys.get(i), awards.get(i));
            else
                hmac = HMAC.calculateHMAC512(hmacKeys.get(i), awards.get(i));
            hmacs.add(hmac);
            Log.d("MyApp", hmacChoice + " = " + HMAC.byteArrayToHexString(hmac));
        }

        // Encrypt each award
        encAwards = new ArrayList<>();
        byte[] encAward;
        for (int i = 0; i < 4; i++) {
            try {
                if (aesChoice.equals("CBC"))
                    encAward = EncryptDecrypt.encryptAwardCBC(encKeys.get(i), awards.get(i));
                else
                    encAward = EncryptDecrypt.encryptAwardCTR(encKeys.get(i), awards.get(i));
                Log.d("MyApp", aesChoice + " AWARD CYPHER = " + Base64.getEncoder().encodeToString(encAward)); // Log the encrypted award.
                encAwards.add(encAward);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Generate Signatures for each award, Order: key 0 -> award (category 0)

        publicKeysList = new ArrayList<>();
        privateKeysList = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            RSAKeyPairGenerator rsaG = new RSAKeyPairGenerator();
            try {
                KeyPair kp =  rsaG.generateKeyPair();
                String publicKey = rsaG.getPublicKeyBase64(kp);
                String privateKey = rsaG.getPrivateKeyBase64(kp);
                publicKeysList.add(publicKey);
                privateKeysList.add(privateKey);
            } catch (NoSuchAlgorithmException e) {
                Log.e("MyApp", e.toString());
            }
        }

        // Digital Signature
        digitalSignaturesList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            RSASignature rsaSign = new RSASignature();
            try {
                byte[] signature = rsaSign.generateDigitalSignature(awards.get(i).toString(), privateKeysList.get(i));
                Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature)); // Log the encrypted award.
                digitalSignaturesList.add(signature);
            } catch (Exception e) {
                Log.e("MyApp", e.toString());
            }
        }

        // Generate a list of numbers [0,3] shuffled
        // i = 0 -> A // i = 1 -> B // i = 2 -> C // i = 3 -> D
        listShuffled = new ArrayList<>();
        listShuffled.add(0);
        listShuffled.add(1);
        listShuffled.add(2);
        listShuffled.add(3);
        Collections.shuffle(listShuffled);
    }

    public static SecretKey generateSecretKey() {
        SecretKey secretKey = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256);
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    public static double generateRandomPrize(double lambda, double min, double max) {
        Random random = new Random();
        double prize = Math.exp(-lambda * random.nextDouble());
        return (max - min) * prize + min;
    }

    public ArrayList<Award> createAwards(double lambda, double minPrize, double maxPrize) {
        ArrayList<Award> awards = new ArrayList<>();
        ArrayList<Integer> prizes = new ArrayList<>();
        int category = 0;

        for (int i = 0; i < 4; i++) {
            int prize = (int) generateRandomPrize(lambda, minPrize, maxPrize);
            prizes.add(prize);
        }

        Collections.sort(prizes);

        for (int prize : prizes) {
            Award award = new Award(prize, category);
            awards.add(award);
            category++;
        }

        return awards;
    }

    public void AwardAChoosen(View v) {
        Log.d("MyApp", "Clicked on A award.");

        Award award = awards.get(listShuffled.get(0));
        byte [] encKey = encKeys.get(listShuffled.get(0));
        byte [] hmac = hmacs.get(listShuffled.get(0));
        SecretKey hmacKey = hmacKeys.get(listShuffled.get(0));
        byte [] encAward = encAwards.get(listShuffled.get(0));
        byte [] signature = digitalSignaturesList.get(listShuffled.get(0));
        String publicKey = publicKeysList.get(listShuffled.get(0));
        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);
        AwardABtn.setVisibility(View.INVISIBLE);

        goToDecipher(hmac, hmacKey, encAward, signature, publicKey, hmacChoice, aesChoice);
    }

    public void AwardBChoosen(View v){
        Log.d("MyApp", "Clicked on B award.");

        Award award = awards.get(listShuffled.get(1));
        byte [] encKey = encKeys.get(listShuffled.get(1));
        byte [] hmac = hmacs.get(listShuffled.get(1));
        SecretKey hmacKey = hmacKeys.get(listShuffled.get(1));
        byte [] encAward = encAwards.get(listShuffled.get(1));
        byte [] signature = digitalSignaturesList.get(listShuffled.get(1));
        String publicKey = publicKeysList.get(listShuffled.get(1));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);

        AwardBBtn.setVisibility(View.INVISIBLE);

        goToDecipher(hmac, hmacKey, encAward, signature, publicKey, hmacChoice, aesChoice);
    }

    public void AwardCChoosen(View v){
        Log.d("MyApp", "Clicked on C award.");

        Award award = awards.get(listShuffled.get(2));
        byte [] encKey = encKeys.get(listShuffled.get(2));
        byte [] hmac = hmacs.get(listShuffled.get(2));
        SecretKey hmacKey = hmacKeys.get(listShuffled.get(2));
        byte [] encAward = encAwards.get(listShuffled.get(2));
        byte [] signature = digitalSignaturesList.get(listShuffled.get(2));
        String publicKey = publicKeysList.get(listShuffled.get(2));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);

        AwardCBtn.setVisibility(View.INVISIBLE);
        goToDecipher(hmac, hmacKey, encAward, signature, publicKey, hmacChoice, aesChoice);
    }

    public void AwardDChoosen(View v){
        Log.d("MyApp", "Clicked on D award.");

        Award award = awards.get(listShuffled.get(3));
        byte [] encKey = encKeys.get(listShuffled.get(3));
        byte [] hmac = hmacs.get(listShuffled.get(3));
        SecretKey hmacKey = hmacKeys.get(listShuffled.get(3));
        byte [] encAward = encAwards.get(listShuffled.get(3));
        byte [] signature = digitalSignaturesList.get(listShuffled.get(3));
        String publicKey = publicKeysList.get(listShuffled.get(3));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);

        AwardDBtn.setVisibility(View.INVISIBLE);

        goToDecipher(hmac, hmacKey, encAward, signature, publicKey, hmacChoice, aesChoice);
    }


    public void goToDecipher (byte[] hmac, SecretKey hmacKey, byte[] encAward, byte[] signature, String publicKey, String hmac_choice,String aes_choice) {
        Intent goToDecryptIntent = new Intent(this, DecryptActivity.class);
        goToDecryptIntent.putExtra("flag","FROM_GAME");
        goToDecryptIntent.putExtra("HMAC", hmac);
        goToDecryptIntent.putExtra("HMAC_KEY", hmacKey);
        goToDecryptIntent.putExtra("ENC_AWARD", encAward);
        goToDecryptIntent.putExtra("SIGNATURE", signature);
        goToDecryptIntent.putExtra("PUBLIC_KEY", publicKey);
        goToDecryptIntent.putExtra("HMAC_Choice", hmac_choice);
        goToDecryptIntent.putExtra("AES_Choice", aes_choice);
        startActivity(goToDecryptIntent);
    }

    public void onLogoutClicked(View v){
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
