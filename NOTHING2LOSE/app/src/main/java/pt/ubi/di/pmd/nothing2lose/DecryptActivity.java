package pt.ubi.di.pmd.nothing2lose;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    Button ButtonCancel;
    TextView txtFieldtitle;

    byte[] hmac;
    SecretKey hmacKey;
    byte[] encAward;

    private DecryptionTask decryptionTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decrypt);

        ButtonCancel = (Button) findViewById(R.id.cancel_button);
        txtFieldtitle = (TextView) findViewById(R.id.titleText);

        // Getting the flag from the intent that he came from
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        // Check flag and initialize objects
        if(checkFlag.equals("FROM_GAME")){
            hmac = (byte[]) getIntent().getSerializableExtra("HMAC");
            hmacKey = (SecretKey) getIntent().getSerializableExtra("HMAC_KEY");
            encAward = (byte[]) getIntent().getSerializableExtra("ENC_AWARD");
        }

        decryptionTask = new DecryptionTask();
        decryptionTask.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (decryptionTask != null && decryptionTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.d("MyApp", "STOPPED");
            decryptionTask.cancel(true);
            return;
        }
    }

    public void CancelButtonClicked(View v) {
        super.onBackPressed();
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

            for (int i = 0; i < (1 << 23); i++) {
                for (int j = 0; j < 3; j++) {
                    key[j] = (byte) ((i >> (j * 8)) & 0xFF);
                }

                if (isCancelled()) {
                    return null; // terminate the task
                }

                try {
                    DecryptedAward = EncryptDecrypt.decryptAward(key, encAward);
                    break;
                } catch (Exception e) {
                    continue;
                }
            }

            if (isCancelled()) {
                return null; // terminate the task
            }

            byte[] hmacNew = HMAC.calculateHMAC(hmacKey, DecryptedAward);

            if (Arrays.equals(hmac, hmacNew)) {
                Log.d("MyApp", "HMAC's match");
                Log.d("MyApp", "Decrypted Award: " + DecryptedAward);
            } else {
                Log.d("MyApp", "HMAC's do not match");
            }

            return DecryptedAward;
        }


        protected void onPostExecute(Award decryptedAward) {
            super.onPostExecute(decryptedAward);
            // TODO: Update UI with decryptedAward
            ButtonCancel.setVisibility(View.INVISIBLE);
            txtFieldtitle.setText(String.valueOf(decryptedAward.getPrice()));
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
