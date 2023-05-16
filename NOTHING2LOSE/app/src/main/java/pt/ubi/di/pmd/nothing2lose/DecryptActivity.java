package pt.ubi.di.pmd.nothing2lose;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import javax.crypto.SecretKey;


public class DecryptActivity extends AppCompatActivity {
    Button cancelBtn;
    Button playAgainBtn;
    TextView titleTxt;
    TextView congratzTxt;
    TextView categoryTxt;

    byte[] hmac;
    SecretKey hmacKey;
    byte[] encAward;

    byte[] signature;

    String publicKey;
    String hmac_choice;

    private DecryptionTask decryptionTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decrypt);

        cancelBtn = (Button) findViewById(R.id.cancel_button);
        playAgainBtn = (Button) findViewById(R.id.play_again_button);
        playAgainBtn.setVisibility(View.INVISIBLE);
        titleTxt = (TextView) findViewById(R.id.titleText);
        congratzTxt = (TextView) findViewById(R.id.congratulations);
        congratzTxt.setVisibility(View.INVISIBLE);
        categoryTxt = (TextView) findViewById(R.id.award_category);
        categoryTxt.setVisibility(View.INVISIBLE);

        // Getting the flag from the intent that he came from
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        // Check flag and initialize objects
        if(checkFlag.equals("FROM_GAME")){
            hmac = (byte[]) getIntent().getSerializableExtra("HMAC");
            hmacKey = (SecretKey) getIntent().getSerializableExtra("HMAC_KEY");
            encAward = (byte[]) getIntent().getSerializableExtra("ENC_AWARD");
            signature = (byte[]) getIntent().getSerializableExtra("SIGNATURE");
            publicKey = (String) getIntent().getSerializableExtra("PUBLIC_KEY");
            hmac_choice = (String) getIntent().getSerializableExtra("HMAC_Choice");
        }

        decryptionTask = new DecryptionTask();
        decryptionTask.execute();
    }

    @Override
    public void onBackPressed() {

    }

    public void CancelButtonClicked(View v) {
        super.onBackPressed();

        // Stop decryption task
        if (decryptionTask != null && decryptionTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.d("MyApp", "STOPPED");
            decryptionTask.cancel(true);
            return;
        }
    }

    private class DecryptionTask extends AsyncTask<Void, Void, Award> {

        protected Award doInBackground(Void... voids) {
            Award DecryptedAward = null;
            byte[] key = new byte[16];

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < (1 << 30); i++) {

                for (int j = 0; j < 4; j++) {
                    key[j] = (byte) ((i >> (j * 8)) & 0xFF);
                }

                // Log.d("MyApp", "key = " + KeyGen.bytesToHex(key));

                if (isCancelled()) {
                    return null; // terminate the task
                }

                try {
                    DecryptedAward = EncryptDecrypt.decryptAward(key, encAward);
                    // Successful decryption
                    break;
                } catch (Exception e) {
                    // Decryption failed
                    continue;
                }
            }

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            Log.d("MyApp", "Decryption time: " + elapsedTime / 1000 + " seconds");

            if (hmac_choice.equals("HMAC256")) {
                byte[] hmacNew = HMAC.calculateHMAC(hmacKey, DecryptedAward);
                if (Arrays.equals(hmac, hmacNew)) {
                    Log.d("MyApp", "SUCCESS! HMAC's match.");

                    RSASignatureVerification rsaVerification = new RSASignatureVerification();

                    boolean verificationSignature = false;
                    try {
                        verificationSignature = rsaVerification.verifyDigitalSignature(DecryptedAward.toString(), publicKey, signature);
                    } catch (Exception e) {
                        Log.e("MyApp", e.toString());
                    }
                    if (verificationSignature) {
                        Log.d("MyApp", "SUCCESS! Signature's match.");

                        Log.d("MyApp", "SUCCESS! Decrypted Award: " + DecryptedAward);

                        return DecryptedAward;
                    } else {
                        Log.d("MyApp", "Signature's do not match.");
                    }
                } else {
                    Log.d("MyApp", "HMAC's do not match.");
                }
            } else if (hmac_choice.equals("HMAC512")){
                byte[] hmacNew = CalculateHMAC512.calculateHMAC(hmacKey, DecryptedAward);
                if (Arrays.equals(hmac, hmacNew)) {
                    Log.d("MyApp", "SUCCESS! HMAC's match.");

                    RSASignatureVerification rsaVerification = new RSASignatureVerification();

                    boolean verificationSignature = false;
                    try {
                        verificationSignature = rsaVerification.verifyDigitalSignature(DecryptedAward.toString(), publicKey, signature);
                    } catch (Exception e) {
                        Log.e("MyApp", e.toString());
                    }
                    if (verificationSignature) {
                        Log.d("MyApp", "SUCCESS! Signature's match.");

                        Log.d("MyApp", "SUCCESS! Decrypted Award: " + DecryptedAward);

                        return DecryptedAward;
                    } else {
                        Log.d("MyApp", "Signature's do not match.");
                    }
                } else {
                    Log.d("MyApp", "HMAC's do not match.");
                }
            }
            return null;
        }


        protected void onPostExecute(Award decryptedAward) {
            super.onPostExecute(decryptedAward);
            cancelBtn.setVisibility(View.INVISIBLE);

            playAgainBtn.setVisibility(View.VISIBLE);

            congratzTxt.setVisibility(View.VISIBLE);

            titleTxt.setText("You won: " + decryptedAward.getPrice() + "$!");

            categoryTxt.setVisibility(View.VISIBLE);
            categoryTxt.setText(String.valueOf(decryptedAward.getCategoryString()));
            categoryTxt.setTextColor(Color.parseColor(decryptedAward.getCategoryColor()));
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

    public void PlayAgainButtonClicked (View v) {
        Intent goToGameIntent = new Intent(this, GameActivity.class);
        goToGameIntent.putExtra("HMAC", hmac_choice);
        startActivity(goToGameIntent);
    }
}
