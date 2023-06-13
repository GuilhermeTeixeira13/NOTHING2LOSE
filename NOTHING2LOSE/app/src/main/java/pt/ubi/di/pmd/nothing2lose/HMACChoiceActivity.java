package pt.ubi.di.pmd.nothing2lose;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The HMACChoiceActivity class represents an activity that allows the user to choose the HMAC algorithm.
 *
 * It provides buttons for HMAC256, HMAC512, and logout functionality.
 */
public class HMACChoiceActivity extends AppCompatActivity {

    Button HMACSHA256Btn;
    Button HMACSHA512Btn;
    Button logoutBtn;

    /**
     * This method is called when the activity is created.
     * It sets the layout for the activity and initializes the buttons.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hmac_choice);

        // Initialize the buttons by finding their corresponding views.
        HMACSHA256Btn = (Button) findViewById(R.id.hash256);
        HMACSHA512Btn = (Button) findViewById(R.id.hash512);
        logoutBtn = (Button) findViewById(R.id.logoutButton);
    }

    /**
     * This method is triggered when the user clicks on the HMAC256 button.
     * It navigates to the AESChoiceActivity and passes the chosen HMAC algorithm as an extra.
     *
     * @param view The View object representing the clicked HMAC256 button.
     */
    public void onHMAC256Clicked(View view){
        Intent goToAESChoiceActivity = new Intent(this, AESChoiceActivity.class);
        goToAESChoiceActivity.putExtra("HMAC", "HMAC256");
        startActivity(goToAESChoiceActivity);
    }

    /**
     * This method is triggered when the user clicks on the HMAC512 button.
     * It navigates to the AESChoiceActivity and passes the chosen HMAC algorithm as an extra.
     *
     * @param view The View object representing the clicked HMAC512 button.
     */
    public void onHMAC512Clicked(View view){
        Intent intent = new Intent(this, AESChoiceActivity.class);
        intent.putExtra("HMAC", "HMAC512");
        startActivity(intent);

    }

    /**
     * This method is triggered when the user clicks on the logout button.
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
