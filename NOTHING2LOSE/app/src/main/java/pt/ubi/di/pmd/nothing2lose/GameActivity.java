package pt.ubi.di.pmd.nothing2lose;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import pt.ubi.di.pmd.nothing2lose.utility.Award;
import pt.ubi.di.pmd.nothing2lose.utility.EncryptDecrypt;
import pt.ubi.di.pmd.nothing2lose.utility.HMAC;
import pt.ubi.di.pmd.nothing2lose.utility.KeyGenerator;
import pt.ubi.di.pmd.nothing2lose.utility.RSAKeyPairGenerator;
import pt.ubi.di.pmd.nothing2lose.utility.RSASignature;

/**
 * GameActivity
 * This activity class represents the main game screen of the application.
 * It handles the generation and encryption of awards, generation of HMACs and digital signatures,
 * shuffling and displaying awards, and navigation to the decryption screen.
 */
public class GameActivity extends AppCompatActivity {

    Button AwardABtn;
    Button AwardBBtn;
    Button AwardCBtn;
    Button AwardDBtn;

    ArrayList<Award> awards;
    List<byte[]> encKeys;
    List<byte[]> encAwards;
    List<SecretKey> hmacKeys;
    List<byte[]> hmacs;
    List<String> publicKeys;
    List<String> privateKeys;
    List<byte[]> digitalSignatures;

    ArrayList<Integer> awardsShuffled;

    String hmacChoice;
    String aesChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        // Retrieve HMAC and AES choices from the previous screen
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

        // Generate encryption keys using KeyGenerator class.
        encKeys = KeyGenerator.generateKeys();
        for (byte[] key : encKeys) {
            String keyString = KeyGenerator.byteArrayToString(key);
            Log.d("MyApp", "ENC KEY = " + keyString);
        }

        // Generate secret keys for HMACs.
        hmacKeys = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            SecretKey HMACkey = null;
            if (hmacChoice.equals("HMAC256"))
                HMACkey = HMAC.generateSecretKeyHmacSHA256();
            else if (hmacChoice.equals("HMAC512"))
                HMACkey = HMAC.generateSecretKeyHmacSHA512();

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

        // Generate key pairs for each award
        publicKeys = new ArrayList<>();
        privateKeys = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            try {
                KeyPair kp =  RSAKeyPairGenerator.generateKeyPair();
                String publicKey = RSAKeyPairGenerator.getPublicKeyBase64(kp);
                String privateKey = RSAKeyPairGenerator.getPrivateKeyBase64(kp);
                publicKeys.add(publicKey);
                privateKeys.add(privateKey);
            } catch (NoSuchAlgorithmException e) {
                Log.e("MyApp", e.toString());
            }
        }

        // Digital Signature of each award
        digitalSignatures = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            try {
                byte[] signature = RSASignature.generateDigitalSignature(awards.get(i).toString(), privateKeys.get(i));
                Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature)); // Log the encrypted award.
                digitalSignatures.add(signature);
            } catch (Exception e) {
                Log.e("MyApp", e.toString());
            }
        }

        awardsShuffled = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(awardsShuffled);
    }

    /**
     * Called when the activity is resumed. Performs necessary actions when the activity resumes.
     * Checks the visibility of award buttons (Award A, Award B, Award C, and Award D), and if all of them are invisible,
     * navigates to the HMACChoiceActivity.
     */
    private boolean areAllButtonsInvisible() {
        return AwardABtn.getVisibility() == View.INVISIBLE &&
                AwardBBtn.getVisibility() == View.INVISIBLE &&
                AwardCBtn.getVisibility() == View.INVISIBLE &&
                AwardDBtn.getVisibility() == View.INVISIBLE;
    }

    /**
     * Called when the activity is resumed. Performs necessary actions when the activity resumes.
     * Checks the visibility of award buttons (Award A, Award B, Award C, and Award D), and if all of them are invisible,
     * navigates to the HMACChoiceActivity. It ensures a seamless user experience by mitigating navigational dead ends.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (areAllButtonsInvisible()) {
            Log.d("MyApp", "All buttons invisible!");
            Intent goToHmacChoiceIntent = new Intent(this, HMACChoiceActivity.class);
            startActivity(goToHmacChoiceIntent);
        }
    }

    /**
     * This code contains two methods: generateRandomPrize and createAwards.
     * The generateRandomPrize method generates a random prize value based on a given lambda, minimum value, and maximum value.
     * The createAwards method creates a list of Award objects with randomly generated prize values.
     *
     * @param lambda The lambda value used to calculate the random prize.
     * @param min The minimum value of the random prize.
     * @param max The maximum value of the random prize.
     * @return A list of Award objects with randomly generated prize values.

     */
    public static double generateRandomPrize(double lambda, double min, double max) {
        // Create a new instance of the Random class.
        Random random = new Random();

        // Generate a random prize value using the exponential distribution formula.
        double prize = Math.exp(-lambda * random.nextDouble());

        // Scale and shift the prize value based on the given minimum and maximum values.
        return (max - min) * prize + min;
    }

    /**
     * This method creates a list of Award objects with randomly generated prize values.
     *
     * @param lambda The lambda value used to calculate the random prize.
     * @param minPrize The minimum value of the random prize.
     * @param maxPrize The maximum value of the random prize.
     * @return A list of Award objects with randomly generated prize values.
     *
     */
    public ArrayList<Award> createAwards(double lambda, double minPrize, double maxPrize) {
        ArrayList<Award> awards = new ArrayList<>();
        ArrayList<Integer> prizes = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int prize = (int) generateRandomPrize(lambda, minPrize, maxPrize);
            prizes.add(prize);
        }

        Collections.sort(prizes);

        int category = 0;
        String[] categoryStrings = {"Simple", "Medium", "Rare", "Legendary!"};
        String[] categoryColors = {"#F0FFFF", "#0000FF", "#FFD700", "#FF4500"};

        for (int prize : prizes) {
            Award award = new Award(prize, categoryStrings[category], categoryColors[category]);
            awards.add(award);
            category++;
        }

        return awards;
    }

    /**
     * This method is triggered when the user clicks on an A award.
     * It retrieves the necessary data related to the selected award and logs the information.
     * It also hides the A award button and proceeds to the decryption process.
     *
     * @param v The View object representing the clicked award.
     */
    public void AwardAChoosen(View v) {
        Log.d("MyApp", "Clicked on A award.");

        Award award = awards.get(awardsShuffled.get(0));
        byte [] encKey = encKeys.get(awardsShuffled.get(0));
        byte [] hmac = hmacs.get(awardsShuffled.get(0));
        SecretKey hmacKey = hmacKeys.get(awardsShuffled.get(0));
        byte [] encAward = encAwards.get(awardsShuffled.get(0));
        byte [] signature = digitalSignatures.get(awardsShuffled.get(0));
        String publicKey = publicKeys.get(awardsShuffled.get(0));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGenerator.byteArrayToString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);

        AwardABtn.setVisibility(View.INVISIBLE);

        goToDecrypt(hmac, hmacKey, encAward, signature, publicKey, hmacChoice, aesChoice);
    }

    /**
     * This method is triggered when the user clicks on an B award.
     * It retrieves the necessary data related to the selected award and logs the information.
     * It also hides the B award button and proceeds to the decryption process.
     *
     * @param v The View object representing the clicked award.
     */
    public void AwardBChoosen(View v){
        Log.d("MyApp", "Clicked on B award.");

        Award award = awards.get(awardsShuffled.get(1));
        byte [] encKey = encKeys.get(awardsShuffled.get(1));
        byte [] hmac = hmacs.get(awardsShuffled.get(1));
        SecretKey hmacKey = hmacKeys.get(awardsShuffled.get(1));
        byte [] encAward = encAwards.get(awardsShuffled.get(1));
        byte [] signature = digitalSignatures.get(awardsShuffled.get(1));
        String publicKey = publicKeys.get(awardsShuffled.get(1));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGenerator.byteArrayToString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);

        AwardBBtn.setVisibility(View.INVISIBLE);

        goToDecrypt(hmac, hmacKey, encAward, signature, publicKey, hmacChoice, aesChoice);
    }

    /**
     * This method is triggered when the user clicks on an C award.
     * It retrieves the necessary data related to the selected award and logs the information.
     * It also hides the C award button and proceeds to the decryption process.
     *
     * @param v The View object representing the clicked award.
     */
    public void AwardCChoosen(View v){
        Log.d("MyApp", "Clicked on C award.");

        Award award = awards.get(awardsShuffled.get(2));
        byte [] encKey = encKeys.get(awardsShuffled.get(2));
        byte [] hmac = hmacs.get(awardsShuffled.get(2));
        SecretKey hmacKey = hmacKeys.get(awardsShuffled.get(2));
        byte [] encAward = encAwards.get(awardsShuffled.get(2));
        byte [] signature = digitalSignatures.get(awardsShuffled.get(2));
        String publicKey = publicKeys.get(awardsShuffled.get(2));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGenerator.byteArrayToString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);

        AwardCBtn.setVisibility(View.INVISIBLE);

        goToDecrypt(hmac, hmacKey, encAward, signature, publicKey, hmacChoice, aesChoice);
    }

    /**
     * This method is triggered when the user clicks on an D award.
     * It retrieves the necessary data related to the selected award and logs the information.
     * It also hides the D award button and proceeds to the decryption process.
     *
     * @param v The View object representing the clicked award.
     */
    public void AwardDChoosen(View v){
        Log.d("MyApp", "Clicked on D award.");

        Award award = awards.get(awardsShuffled.get(3));
        byte [] encKey = encKeys.get(awardsShuffled.get(3));
        byte [] hmac = hmacs.get(awardsShuffled.get(3));
        SecretKey hmacKey = hmacKeys.get(awardsShuffled.get(3));
        byte [] encAward = encAwards.get(awardsShuffled.get(3));
        byte [] signature = digitalSignatures.get(awardsShuffled.get(3));
        String publicKey = publicKeys.get(awardsShuffled.get(3));

        Log.d("MyApp", "CHOOSED AWARD: " + award);
        Log.d("MyApp", "ENC KEY: " + KeyGenerator.byteArrayToString(encKey));
        Log.d("MyApp", "HMAC: " + HMAC.byteArrayToHexString(hmac));
        Log.d("MyApp", "CIPHER: " + Base64.getEncoder().encodeToString(encAward));
        Log.d("MyApp", "AWARD SIGNATURE = " + Base64.getEncoder().encodeToString(signature));
        Log.d("MyApp", "PUBLIC_KEY = " + publicKey);

        AwardDBtn.setVisibility(View.INVISIBLE);

        goToDecrypt(hmac, hmacKey, encAward, signature, publicKey, hmacChoice, aesChoice);
    }

    /**
     * This method navigates to the DecryptActivity and passes the necessary data for decryption.
     *
     * @param hmac The HMAC value associated with the award.
     * @param hmacKey The HMAC key used for verification.
     * @param encAward The encrypted award.
     * @param signature The digital signature of the award.
     * @param publicKey The public key used for signature verification.
     * @param hmacChoice The choice of HMAC algorithm.
     * @param aesChoice The choice of AES algorithm.
     */
    public void goToDecrypt(byte[] hmac, SecretKey hmacKey, byte[] encAward, byte[] signature, String publicKey, String hmacChoice, String aesChoice) {
        Intent goToDecryptIntent = new Intent(this, DecryptAndVerifyActivity.class);
        goToDecryptIntent.putExtra("flag","FROM_GAME");
        goToDecryptIntent.putExtra("HMAC", hmac);
        goToDecryptIntent.putExtra("HMAC_KEY", hmacKey);
        goToDecryptIntent.putExtra("ENC_AWARD", encAward);
        goToDecryptIntent.putExtra("SIGNATURE", signature);
        goToDecryptIntent.putExtra("PUBLIC_KEY", publicKey);
        goToDecryptIntent.putExtra("HMAC_Choice", hmacChoice);
        goToDecryptIntent.putExtra("AES_Choice", aesChoice);
        startActivity(goToDecryptIntent);
    }

    /**
     * This method is triggered when the user clicks on the logout button.
     *
     * It removes the stored username and password from SharedPreferences, and navigates back to the MainActivity.
     *
     * @param v The View object representing the clicked logout button.
     */
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
