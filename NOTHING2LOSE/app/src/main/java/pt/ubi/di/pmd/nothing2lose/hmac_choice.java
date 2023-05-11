package pt.ubi.di.pmd.nothing2lose;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class hmac_choice extends AppCompatActivity {

    Button Button_hash256;
    Button Button_hash512;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hmac_choice);

        Button_hash256 = (Button) findViewById(R.id.hash256);
        Button_hash512 = (Button) findViewById(R.id.hash512);

    }

    public void onhmac256Clicked(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("HMAC", "HMAC256");
        startActivity(intent);
    }
    public void onhmac512Clicked(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("HMAC", "HMAC512");
        startActivity(intent);

    }
}
