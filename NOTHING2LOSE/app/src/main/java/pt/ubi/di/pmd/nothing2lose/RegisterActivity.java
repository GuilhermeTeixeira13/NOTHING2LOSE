package pt.ubi.di.pmd.nothing2lose;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText nicknameEditText;
    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;
    TextInputEditText editTextPasswordRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

       nicknameEditText = findViewById(R.id.editTextNickname);
       editTextEmail = findViewById(R.id.editTextEmail);
       editTextPassword = findViewById(R.id.editTextPassword);
       editTextPasswordRepeat = findViewById(R.id.editTextPasswordRepeat);
    }

    public void onRegisterClicked(View view){
        Log.d("MyApp", "cliquei no registo");

        if(!nicknameEditText.getText().toString().equals("") && !editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("") && !editTextPasswordRepeat.getText().toString().equals("")){
            if(editTextPassword.getText().toString().equals(editTextPasswordRepeat.getText().toString())){
                // Passwords match
                new RegisterActivity.UserRegistration().execute();
            }
            else {
                // Passwords don't match
                Toast.makeText(RegisterActivity.this, "It seems like the passwords you entered don't match!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            // Empty Textfields
            Toast.makeText(RegisterActivity.this, "Hey there! It seems like you left some fields empty!" , Toast.LENGTH_LONG).show();
        }
    }

    public void onLoginLinkClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class UserRegistration extends AsyncTask<Void, Void, Void> {
        private Exception exception;

        protected Void doInBackground(Void... voids) {
            String url = "jdbc:postgresql://nothing2lose-db.carkfyqrpaoi.eu-north-1.rds.amazonaws.com:5432/NOTHING2LOSEDB";
            String username = "postgres";
            String password = "8iy5df232";

            // Passwords match
            try {
                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection(url, username, password);

                String insertQuery = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(insertQuery);

                // set values for the parameters
                pstmt.setString(1, nicknameEditText.getText().toString());
                pstmt.setString(2, editTextEmail.getText().toString());
                pstmt.setString(3, editTextPassword.getText().toString());

                // execute the insert statement
                pstmt.executeUpdate();

                pstmt.close();
                conn.close();

                goToGamePage();
            } catch (Exception e) {
                Log.e("MyApp", "Error executing query", e);
                exception = e;
            }
            return null;
        }
    }

    public void goToGamePage(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}