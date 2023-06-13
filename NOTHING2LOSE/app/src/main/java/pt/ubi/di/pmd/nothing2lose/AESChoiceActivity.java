package pt.ubi.di.pmd.nothing2lose;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The AESChoiceActivity class represents an Android activity that displays a choice screen for AES encryption modes.
 * It allows the user to select between AES CBC and AES CTR encryption modes.
 * The selected mode is then passed to the GameActivity class for further processing.
 */
public class AESChoiceActivity extends AppCompatActivity {

    // Buttons for AES CBC and AES CTR encryption modes
    Button CBCbtn;
    Button CTRbtn;

    // Stores the HMAC value passed from the previous activity
    String hmacChoice;

    /**
     * Called when the activity is first created.
     * Initializes the activity, sets the layout, and retrieves the HMAC type from the intent.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aes_choice);

        // Retrieve the HMAC value passed from the previous activity
        hmacChoice = (String) getIntent().getSerializableExtra("HMAC");

        // Initialize the buttons by finding their respective views
        CBCbtn = (Button) findViewById(R.id.ButtonCBC);
        CTRbtn = (Button) findViewById(R.id.ButtonCTR);
    }

    /**
     * Handles the click event for the AES CBC button.
     * Starts the GameActivity class and passes the AES CBC encryption mode and the HMAC type as extras.
     *
     * @param view The view that triggered the click event.
     */
    public void onAESCBCClicked(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("AES", "CBC");
        intent.putExtra("HMAC", hmacChoice);
        startActivity(intent);
    }

    /**
     * Handles the click event for the AES CTR button.
     * Starts the GameActivity class and passes the AES CTR encryption mode and the HMAC type as extras.
     *
     * @param view The view that triggered the click event.
     */
    public void onAESCTRClicked(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("AES", "CTR");
        intent.putExtra("HMAC", hmacChoice);
        startActivity(intent);
    }

    /**
     * Handles the click event for the logout button.
     * Removes the stored username and password from shared preferences, effectively logging out the user.
     * Starts the MainActivity class.
     *
     * @param v The view that triggered the click event.
     */
    public void onLogoutClicked(View v) {
        // Access the shared preferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Remove the stored username and password
        editor.remove("username");
        editor.remove("password");
        editor.apply();

        // Start the MainActivity class to logout the user
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

