package pt.ubi.di.pmd.nothing2lose;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
    List<byte[]> enc_awards;;
    SecretKey keyHmac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

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

        hmacs = new ArrayList<>();
        // Generate an HMAC for each award and add it to a list of HMACs.
        for (Award award : awards) {
            byte[] hmac = HMAC.calculateHMAC(keyHmac, award);
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
        int category = 1;

        for (int i = 0; i < 4; i++) {
            int prize = (int) generateRandomPrize(lambda, minPrize, maxPrize);
            awards.add(new Award(prize, category));
            category++;
        }

        return awards;
    }

    public void AwardAChoosen(View v) {
        Log.d("MyApp", "Clicked on A award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Award award = awards.get(randomNumber);
        byte [] encKey = encKeys.get(randomNumber);
        byte [] hmac = hmacs.get(randomNumber);
        byte [] encAward = enc_awards.get(randomNumber);

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));

        AwardABtn.setVisibility(View.INVISIBLE);

        goToDecipher(hmac, keyHmac, encAward);
    }

    public void AwardBChoosen(View v){
        Log.d("MyApp", "Clicked on B award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Award award = awards.get(randomNumber);
        byte [] encKey = encKeys.get(randomNumber);
        byte [] hmac = hmacs.get(randomNumber);
        byte [] encAward = enc_awards.get(randomNumber);

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));

        AwardBBtn.setVisibility(View.INVISIBLE);

        goToDecipher(hmac, keyHmac, encAward);
    }

    public void AwardCChoosen(View v){
        Log.d("MyApp", "Clicked on C award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Award award = awards.get(randomNumber);
        byte [] encKey = encKeys.get(randomNumber);
        byte [] hmac = hmacs.get(randomNumber);
        byte [] encAward = enc_awards.get(randomNumber);

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));

        AwardCBtn.setVisibility(View.INVISIBLE);

        goToDecipher(hmac, keyHmac, encAward);
    }

    public void AwardDChoosen(View v){
        Log.d("MyApp", "Clicked on D award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Award award = awards.get(randomNumber);
        byte [] encKey = encKeys.get(randomNumber);
        byte [] hmac = hmacs.get(randomNumber);
        byte [] encAward = enc_awards.get(randomNumber);

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));

        AwardDBtn.setVisibility(View.INVISIBLE);

        goToDecipher(hmac, keyHmac, encAward);
    }

    public void goToDecipher (byte[] hmac, SecretKey hmacKey, byte[] encAward) {
        Intent goToDecryptIntent = new Intent(this, DecryptActivity.class);
        goToDecryptIntent.putExtra("flag","FROM_GAME");
        goToDecryptIntent.putExtra("HMAC", hmac);
        goToDecryptIntent.putExtra("HMAC_KEY", hmacKey);
        goToDecryptIntent.putExtra("ENC_AWARD", encAward);
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
