package pt.ubi.di.pmd.nothing2lose;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AES_Choice extends AppCompatActivity {

    Button Button_CBC;
    Button Button_CTR;

    String hmac_choice;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aes_choice);
        Bundle extras = getIntent().getExtras();
        hmac_choice = extras.getString("HMAC");

        Button_CBC = (Button) findViewById(R.id.ButtonCBC);
        Button_CTR = (Button) findViewById(R.id.ButtonCTR);

    }

    public void onaescbcClicked(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("AES", "CBC");
        intent.putExtra("HMAC", hmac_choice);
        startActivity(intent);
    }
    public void onaesctrClicked(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("AES", "CTR");
        intent.putExtra("HMAC", hmac_choice);
        startActivity(intent);

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
