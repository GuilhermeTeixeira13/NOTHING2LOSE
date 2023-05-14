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

    Button ButtonAwardA;
    Button ButtonAwardB;
    Button ButtonAwardC;
    Button ButtonAwardD;

    ArrayList<Award> awards;
    List<byte[]> keys;
    List<byte[]> hmacs;
    List<byte[]> cifrados;
    byte[] iv;
    SecretKey HMACkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        ButtonAwardA = (Button) findViewById(R.id.awardA);
        ButtonAwardB = (Button) findViewById(R.id.awardB);
        ButtonAwardC = (Button) findViewById(R.id.awardC);
        ButtonAwardD = (Button) findViewById(R.id.awardD);

        Log.d("MyApp", "App started.");

        double lambda = 5;
        double minPrize = 0.0;
        double maxPrize = 1000.0;

        // Ordenados do menor para o maior prémio
        awards = createAwards(lambda, minPrize, maxPrize);
        Log.d("MyApp", "Awards sorted: " + awards.toString());

        // Calculam-se as chaves, ordenadas da menos complexa à mais complexa
        KeyGen keyAlgorithm = new KeyGen();
        keys = keyAlgorithm.generateKeys();
        for(byte[] key : keys) {
            String keyString = KeyGen.byteArrayToHexString(key);
            Log.d("MyApp", keyString);
        }

        // Calcula uma key para o HMAC
        HMACkey = generateSecretKey();
        Log.d("MyApp", "Chave para o HMAC: " + Base64.getEncoder().encodeToString(HMACkey.getEncoded()));

        // Calcula HMAC para cada award
        hmacs = new ArrayList<>();
        for (Award award : awards){
            byte [] hmac = HMAC.calculateHMAC(HMACkey, award);
            hmacs.add(hmac);
            String hmacString = HMAC.byteArrayToHexString(hmac);
            Log.d("MyApp", hmacString);
        }

        // Cifra
        cifrados = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            try {
                byte[] encAward = EncryptDecrypt.encryptAward(keys.get(i), awards.get(i));
                Log.d("MyApp", "Bilhete cifrado: " + Base64.getEncoder().encodeToString(encAward));
                cifrados.add(encAward);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static SecretKey generateSecretKey() {
        SecretKey secretKey = null;
        try {
            // Generate a key to use in hmac
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
        int category = 1;

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

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Award award = awards.get(randomNumber);
        byte [] encKey = keys.get(randomNumber);
        byte [] hmac = hmacs.get(randomNumber);
        byte [] encAward = cifrados.get(randomNumber);

        Log.d("MyApp", "Escolheu o award: " + award);
        Log.d("MyApp", "Chave usada para cifrar: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "Cifrado: " + Base64.getEncoder().encodeToString(encAward));

        goToDecipher(hmac, HMACkey, encAward);
    }

    public void AwardBChoosen(View v){
        Log.d("MyApp", "Clicked on B award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Award award = awards.get(randomNumber);
        byte [] encKey = keys.get(randomNumber);
        byte [] hmac = hmacs.get(randomNumber);
        byte [] encAward = cifrados.get(randomNumber);

        Log.d("MyApp", "Escolheu o award: " + award);
        Log.d("MyApp", "Chave usada para cifrar: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "Cifrado: " + Base64.getEncoder().encodeToString(encAward));

        goToDecipher(hmac, HMACkey, encAward);
    }

    public void AwardCChoosen(View v){
        Log.d("MyApp", "Clicked on C award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Award award = awards.get(randomNumber);
        byte [] encKey = keys.get(randomNumber);
        byte [] hmac = hmacs.get(randomNumber);
        byte [] encAward = cifrados.get(randomNumber);

        Log.d("MyApp", "Escolheu o award: " + award);
        Log.d("MyApp", "Chave usada para cifrar: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "Cifrado: " + Base64.getEncoder().encodeToString(encAward));

        goToDecipher(hmac, HMACkey, encAward);
    }

    public void AwardDChoosen(View v){
        Log.d("MyApp", "Clicked on D award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Award award = awards.get(randomNumber);
        byte [] encKey = keys.get(randomNumber);
        byte [] hmac = hmacs.get(randomNumber);
        byte [] encAward = cifrados.get(randomNumber);

        Log.d("MyApp", "Escolheu o award: " + award);
        Log.d("MyApp", "Chave usada para cifrar: " + KeyGen.byteArrayToHexString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "Cifrado: " + Base64.getEncoder().encodeToString(encAward));

        goToDecipher(hmac, HMACkey, encAward);
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
