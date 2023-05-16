package pt.ubi.di.pmd.nothing2lose;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class hmac_choice extends AppCompatActivity {

    Button Button_hash256;
    Button Button_hash512;

    Button Button_logout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hmac_choice);

        Button_hash256 = (Button) findViewById(R.id.hash256);
        Button_hash512 = (Button) findViewById(R.id.hash512);
        Button_logout = (Button) findViewById(R.id.logoutButton);

    }

    public void onhmac256Clicked(View view){
        Intent intent = new Intent(this, AES_Choice.class);
        intent.putExtra("HMAC", "HMAC256");
        startActivity(intent);
    }
    public void onhmac512Clicked(View view){
        Intent intent = new Intent(this, AES_Choice.class);
        intent.putExtra("HMAC", "HMAC512");
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
