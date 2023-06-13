package pt.ubi.di.pmd.nothing2lose;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import javax.crypto.SecretKey;

import pt.ubi.di.pmd.nothing2lose.utility.Award;
import pt.ubi.di.pmd.nothing2lose.utility.EncryptDecrypt;
import pt.ubi.di.pmd.nothing2lose.utility.HMAC;
import pt.ubi.di.pmd.nothing2lose.utility.KeyGenerator;
import pt.ubi.di.pmd.nothing2lose.utility.RSASignatureVerification;

/**
 * The DecryptActivity class represents the activity for decrypting an award.
 * It handles the decryption process, cancellation, and UI updates based on the decryption result.
 */
public class DecryptAndVerifyActivity extends AppCompatActivity {

    Button cancelBtn;
    Button playAgainBtn;
    TextView titleTxt;
    TextView congratsTxt;
    TextView categoryTxt;

    byte[] initialHMAC;
    byte[] currentHMAC;
    SecretKey hmacKey;
    byte[] encAward;
    byte[] awardSignature;
    String awardPublicKey;

    String hmacChoice;
    String aesChoice;

    private DecryptionTask decryptionTask;

    /**
     * Called when the activity is created. Initializes the UI elements, retrieves the decryption parameters from the intent,
     * and starts the decryption task.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decrypt);

        cancelBtn = (Button) findViewById(R.id.cancel_button);
        playAgainBtn = (Button) findViewById(R.id.play_again_button);
        playAgainBtn.setVisibility(View.INVISIBLE);
        titleTxt = (TextView) findViewById(R.id.titleText);
        congratsTxt = (TextView) findViewById(R.id.congratulations);
        congratsTxt.setVisibility(View.INVISIBLE);
        categoryTxt = (TextView) findViewById(R.id.award_category);
        categoryTxt.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        String checkFlag = intent.getStringExtra("flag");

        if (checkFlag.equals("FROM_GAME")) {
            initialHMAC = (byte[]) getIntent().getSerializableExtra("HMAC");
            hmacKey = (SecretKey) getIntent().getSerializableExtra("HMAC_KEY");
            encAward = (byte[]) getIntent().getSerializableExtra("ENC_AWARD");
            awardSignature = (byte[]) getIntent().getSerializableExtra("SIGNATURE");
            awardPublicKey = (String) getIntent().getSerializableExtra("PUBLIC_KEY");
            hmacChoice = (String) getIntent().getSerializableExtra("HMAC_Choice");
            aesChoice = (String) getIntent().getSerializableExtra("AES_Choice");
        }

        decryptionTask = new DecryptionTask();
        decryptionTask.execute();
    }

    /**
     * Overrides the onBackPressed method to prevent going back from the decryption screen.
     */
    @Override
    public void onBackPressed() {
        // Disable the back button functionality
    }

    /**
     * Handles the cancellation button click event. Stops the decryption task if it is running.
     *
     * @param v The view that triggered the event.
     */
    public void CancelButtonClicked(View v) {
        super.onBackPressed();

        // Stop decryption task
        if (decryptionTask != null && decryptionTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.d("MyApp", "STOPPED");
            decryptionTask.cancel(true);

            // Create an intent to pass data back to the previous activity, if needed
            Intent intent = new Intent();
            intent.putExtra("key", 1); // Example: passing a key-value pair
            setResult(RESULT_OK, intent); // Set the result code and intent

            return;
        }
    }


    // Helper method to convert a binary string to a byte array
    private static byte[] binaryStringToByteArray(String binaryString) {
        byte[] byteArray = new byte[binaryString.length() / 8];
        for (int i = 0; i < byteArray.length; i++) {
            String byteString = binaryString.substring(i * 8, (i + 1) * 8);
            byteArray[i] = (byte) Integer.parseInt(byteString, 2);
        }
        return byteArray;
    }


    /**
     * The DecryptionTask class represents an asynchronous task for performing the decryption process in the background.
     * It decrypts the award using different keys until a successful decryption or cancellation occurs.
     */
    private class DecryptionTask extends AsyncTask<Void, Void, Award> {

        /**
         * Performs the decryption process in the background.
         *
         * @param voids The input parameters (not used).
         * @return The decrypted award if successful, null otherwise.
         */
        protected Award doInBackground(Void... voids) {
            Award decryptedAward = null;

            long startTime = System.currentTimeMillis();

            int keyLength = 128;
            byte[] key;

            // Iterate through all possible combinations
            for (int i = 0; i < Math.pow(2, keyLength); i++) {
                String binaryString = String.format("%" + keyLength + "s", Integer.toBinaryString(i)).replace(' ', '0');
                key = binaryStringToByteArray(binaryString);

                if (isCancelled())
                    return null;

                try {
                    System.out.println("testing: " + KeyGenerator.byteArrayToString(key));

                    if (aesChoice.equals("CBC"))
                        decryptedAward = EncryptDecrypt.decryptAwardCBC(key, encAward);
                    else if (aesChoice.equals("CTR"))
                        decryptedAward = EncryptDecrypt.decryptAwardCTR(key, encAward);

                    // Successful decryption
                    break;
                } catch (Exception e) {
                    // Decryption failed
                }
            }

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            Log.d("MyApp", "Decryption time: " + elapsedTime / 1000 + " seconds");

            if (hmacChoice.equals("HMAC256"))
                currentHMAC = HMAC.calculateHMAC256(hmacKey, decryptedAward);
            else if (hmacChoice.equals("HMAC512"))
                currentHMAC = HMAC.calculateHMAC512(hmacKey, decryptedAward);

            if (Arrays.equals(initialHMAC, currentHMAC)) {
                Log.d("MyApp", "SUCCESS! HMAC's match.");

                boolean verificationSignature = false;

                try {
                    verificationSignature = RSASignatureVerification.verifyDigitalSignature(decryptedAward.toString(), awardPublicKey, awardSignature);
                } catch (Exception e) {
                    Log.e("MyApp", e.toString());
                }
                if (verificationSignature) {
                    Log.d("MyApp", "SUCCESS! Signature's match.");
                    Log.d("MyApp", "SUCCESS! Decrypted Award: " + decryptedAward);

                    return decryptedAward;
                } else {
                    decryptionOrVerificationFailedAlert("HMAC's do not match.");
                    Log.d("MyApp", "Signature's do not match.");
                }
            } else {
                decryptionOrVerificationFailedAlert("HMAC's do not match.");
                Log.d("MyApp", "HMAC's do not match.");
            }

            return null;
        }

        /**
         * Executes after the decryption task is completed. Updates the UI elements based on the decryption result.
         *
         * @param decryptedAward The decrypted award if successful, null otherwise.
         */
        protected void onPostExecute(Award decryptedAward) {
            super.onPostExecute(decryptedAward);

            if (decryptedAward != null){
                cancelBtn.setVisibility(View.INVISIBLE);
                playAgainBtn.setVisibility(View.VISIBLE);
                congratsTxt.setVisibility(View.VISIBLE);
                titleTxt.setText("You won: " + decryptedAward.getPrice() + "$!");
                categoryTxt.setVisibility(View.VISIBLE);
                categoryTxt.setText(String.valueOf(decryptedAward.getCategory()));
                categoryTxt.setTextColor(Color.parseColor(decryptedAward.getColor()));
            }
        }
    }

    /**
     * Handles the logout button click event. Removes the user's credentials from SharedPreferences and navigates to the login screen.
     *
     * @param v The view that triggered the event.
     */
    public void onLogoutClicked(View v) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove("username");
        editor.remove("password");
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the play again button click event. Navigates to the HMACChoiceActivity.
     *
     * @param v The view that triggered the event.
     */
    public void PlayAgainButtonClicked(View v) {
        Intent goToHmacChoiceIntent = new Intent(this, HMACChoiceActivity.class);
        startActivity(goToHmacChoiceIntent);
    }

    /**
     * Displays an alert indicating that decryption or verification failed, and performs necessary actions.
     *
     * @param extra Additional information about the failure.
     */
    public void decryptionOrVerificationFailedAlert(String extra) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(DecryptAndVerifyActivity.this, "Ups! Something happened to the chosen award: " + extra);
            }
        });

        Intent goToHmacChoiceIntent = new Intent(this, HMACChoiceActivity.class);
        startActivity(goToHmacChoiceIntent);
    }

    /**
     * Displays a toast message.
     *
     * @param context The context in which the toast should be displayed.
     * @param message The message to be shown in the toast.
     */
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}