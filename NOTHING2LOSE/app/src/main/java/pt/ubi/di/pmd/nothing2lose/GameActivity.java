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
    SecretKey HMACkey;

    String hmac_choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        Bundle extras = getIntent().getExtras();
        hmac_choice = extras.getString("HMAC");

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
        SecretKey HMACkey = generateSecretKey();
        Log.d("MyApp", "Chave para o HMAC: " + Base64.getEncoder().encodeToString(HMACkey.getEncoded()));

        // Calcula HMAC para cada award
        if (hmac_choice.equals("HMAC256")){
            CalculateHMAC calculateHMAC = new CalculateHMAC();
            hmacs = calculateHMAC.calcHMAC(HMACkey, awards);
            for (byte[] hmac : hmacs){
                String hmacString = CalculateHMAC.byteArrayToHexString(hmac);
                Log.d("MyApp", hmacString);
            }
        } else if (hmac_choice.equals("HMAC512")) {
            CalculateHMAC512 calculateHMAC512 = new CalculateHMAC512();
            hmacs = calculateHMAC512.calcHMAC(HMACkey, awards);
            for (byte[] hmac : hmacs){
                String hmacString = CalculateHMAC512.byteArrayToHexString(hmac);
                Log.d("MyApp", hmacString);
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

        Log.d("MyApp", "Escolheu o award: " + awards.get(randomNumber));
        Log.d("MyApp", "Chave usada para cifrar: " + KeyGen.byteArrayToHexString(keys.get(randomNumber)));
        if (hmac_choice.equals("HMAC256")){
            Log.d("MyApp", "HMAC: " + CalculateHMAC.byteArrayToHexString(hmacs.get(randomNumber)));
        } else if (hmac_choice.equals("HMAC512")) {
            Log.d("MyApp", "HMAC: " + CalculateHMAC512.byteArrayToHexString(hmacs.get(randomNumber)));
        }
    }

    public void AwardBChoosen(View v){
        Log.d("MyApp", "Clicked on B award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Log.d("MyApp", "Escolheu o award: " + awards.get(randomNumber));
        Log.d("MyApp", "Chave usada para cifrar: " + KeyGen.byteArrayToHexString(keys.get(randomNumber)));
        if (hmac_choice.equals("HMAC256")){
            Log.d("MyApp", "HMAC: " + CalculateHMAC.byteArrayToHexString(hmacs.get(randomNumber)));
        } else if (hmac_choice.equals("HMAC512")) {
            Log.d("MyApp", "HMAC: " + CalculateHMAC512.byteArrayToHexString(hmacs.get(randomNumber)));
        }
    }

    public void AwardCChoosen(View v){
        Log.d("MyApp", "Clicked on C award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Log.d("MyApp", "Escolheu o award: " + awards.get(randomNumber));
        Log.d("MyApp", "Chave usada para cifrar: " + KeyGen.byteArrayToHexString(keys.get(randomNumber)));
        if (hmac_choice.equals("HMAC256")){
            Log.d("MyApp", "HMAC: " + CalculateHMAC.byteArrayToHexString(hmacs.get(randomNumber)));
        } else if (hmac_choice.equals("HMAC512")) {
            Log.d("MyApp", "HMAC: " + CalculateHMAC512.byteArrayToHexString(hmacs.get(randomNumber)));
        }
    }

    public void AwardDChoosen(View v){
        Log.d("MyApp", "Clicked on D award.");

        Random random = new Random();
        int randomNumber = random.nextInt(4);

        Log.d("MyApp", "Escolheu o award: " + awards.get(randomNumber));
        Log.d("MyApp", "Chave usada para cifrar: " + KeyGen.byteArrayToHexString(keys.get(randomNumber)));
        if (hmac_choice.equals("HMAC256")){
            Log.d("MyApp", "HMAC: " + CalculateHMAC.byteArrayToHexString(hmacs.get(randomNumber)));
        } else if (hmac_choice.equals("HMAC512")) {
            Log.d("MyApp", "HMAC: " + CalculateHMAC512.byteArrayToHexString(hmacs.get(randomNumber)));
        }
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
