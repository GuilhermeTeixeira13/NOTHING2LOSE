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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
    List<byte[]> enc_awards;



    SecretKey keyHmac;

    List<byte[]> digitalSignaturesList;
    List<String> publicKeysList;
    List<String> privateKeysList;

    ArrayList<Integer> listShuffled;

    String hmac_choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        Bundle extras = getIntent().getExtras();
        hmac_choice = extras.getString("HMAC");
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
        KeyGen keyAlgorithm = new KeyGen();
        encKeys = keyAlgorithm.generateKeys();

        // Log the encryption keys.
        for (byte[] key : encKeys) {
            String keyString = KeyGen.byteArrayToHexString(key);
            Log.d("MyApp", "ENC KEY = " + keyString);
        }

        // Generate a secret key for HMAC.
        keyHmac = generateSecretKey();

        // Log the HMAC key.
        Log.d("MyApp", "HMAC KEY = " + Base64.getEncoder().encodeToString(keyHmac.getEncoded()));

        // Calcula HMAC para cada award
        hmacs = new ArrayList<>();
        if (hmac_choice.equals("HMAC256")){
            CalculateHMAC calculateHMAC = new CalculateHMAC();
            for (Award award : awards) {
              byte[] hmac = calculateHMAC.calcHMAC(keyHmac, award);
              hmacs.add(hmac);
              String hmacString = HMAC.byteArrayToHexString(hmac);
              Log.d("MyApp", "HMAC = " + hmacString);
            }
        } else if (hmac_choice.equals("HMAC512")) {
            CalculateHMAC512 calculateHMAC512 = new CalculateHMAC512();
            for (Award award : awards) {
              byte[] hmac = calculateHMAC512.calcHMAC(keyHmac, award);
              hmacs.add(hmac);
              String hmacString = HMAC.byteArrayToHexString(hmac);
              Log.d("MyApp", "HMAC = " + hmacString);
            }

        enc_awards = new ArrayList<>();
        // Encrypt each award using a different encryption key and add it to a list of encrypted awards.
        for (int i = 0; i < 4; i++) {
            try {
                byte[] encAward = EncryptDecrypt.encryptAward(encKeys.get(i), awards.get(i));
                Log.d("MyApp", "AWARD CYPHER = " + Base64.getEncoder().encodeToString(encAward)); // Log the encrypted award.
                enc_awards.add(encAward);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        publicKeysList = new ArrayList<>();
        privateKeysList = new ArrayList<>();
        // Generate Signatures for each Prize, Order: key 1 -> award (category 1)
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
        listShuffled = new ArrayList<Integer>();
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


       /* Random random = new Random();
        int randomNumber = random.nextInt(4);

        Log.d("MyApp", "Escolheu o award: " + awards.get(randomNumber));
        Log.d("MyApp", "Chave usada para cifrar: " + KeyGen.byteArrayToHexString(keys.get(randomNumber)));
        if (hmac_choice.equals("HMAC256")){
            Log.d("MyApp", "HMAC: " + CalculateHMAC.byteArrayToHexString(hmacs.get(randomNumber)));
        } else if (hmac_choice.equals("HMAC512")) {
            Log.d("MyApp", "HMAC: " + CalculateHMAC512.byteArrayToHexString(hmacs.get(randomNumber)));
        } */

        Award award = awards.get(listShuffled.get(0));
        byte [] encKey = encKeys.get(listShuffled.get(0));
        byte [] hmac = hmacs.get(listShuffled.get(0));
        byte [] encAward = enc_awards.get(listShuffled.get(0));
        byte [] signature = digitalSignaturesList.get(listShuffled.get(0));
        String publicKey = publicKeysList.get(listShuffled.get(0));
        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);
        AwardABtn.setVisibility(View.INVISIBLE);

        goToDecipher(hmac, keyHmac, encAward, signature, publicKey);
    }

    public void AwardBChoosen(View v){
        Log.d("MyApp", "Clicked on B award.");

        Award award = awards.get(listShuffled.get(1));
        byte [] encKey = encKeys.get(listShuffled.get(1));
        byte [] hmac = hmacs.get(listShuffled.get(1));
        byte [] encAward = enc_awards.get(listShuffled.get(1));
        byte [] signature = digitalSignaturesList.get(listShuffled.get(1));
        String publicKey = publicKeysList.get(listShuffled.get(1));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);

        AwardBBtn.setVisibility(View.INVISIBLE);


        goToDecipher(hmac, keyHmac, encAward, signature, publicKey);
    }

    public void AwardCChoosen(View v){
        Log.d("MyApp", "Clicked on C award.");

        Award award = awards.get(listShuffled.get(2));
        byte [] encKey = encKeys.get(listShuffled.get(2));
        byte [] hmac = hmacs.get(listShuffled.get(2));
        byte [] encAward = enc_awards.get(listShuffled.get(2));
        byte [] signature = digitalSignaturesList.get(listShuffled.get(2));
        String publicKey = publicKeysList.get(listShuffled.get(2));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);


        AwardCBtn.setVisibility(View.INVISIBLE);
        goToDecipher(hmac, keyHmac, encAward, signature, publicKey);
    }

    public void AwardDChoosen(View v){
        Log.d("MyApp", "Clicked on D award.");

        Award award = awards.get(listShuffled.get(3));
        byte [] encKey = encKeys.get(listShuffled.get(3));
        byte [] hmac = hmacs.get(listShuffled.get(3));
        byte [] encAward = enc_awards.get(listShuffled.get(3));
        byte [] signature = digitalSignaturesList.get(listShuffled.get(3));
        String publicKey = publicKeysList.get(listShuffled.get(3));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);

        AwardDBtn.setVisibility(View.INVISIBLE);

        goToDecipher(hmac, keyHmac, encAward, signature, publicKey);
    }


    public void goToDecipher (byte[] hmac, SecretKey hmacKey, byte[] encAward, byte[] signature, String publicKey) {
        Intent goToDecryptIntent = new Intent(this, DecryptActivity.class);
        goToDecryptIntent.putExtra("flag","FROM_GAME");
        goToDecryptIntent.putExtra("HMAC", hmac);
        goToDecryptIntent.putExtra("HMAC_KEY", hmacKey);
        goToDecryptIntent.putExtra("ENC_AWARD", encAward);
        goToDecryptIntent.putExtra("SIGNATURE", signature);
        goToDecryptIntent.putExtra("PUBLIC_KEY", publicKey);
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
