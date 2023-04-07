package pt.ubi.di.pmd.nothing2lose;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.mindrot.jbcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {

    TextInputEditText editTextNickname;
    TextInputEditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        editTextNickname = findViewById(R.id.editTextNickname);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Retrieve user's login credentials
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        // Check if the username and password are not empty before using them to log the user in
        if (!username.isEmpty() && !password.isEmpty()) {
            new MainActivity.LoginTask().execute(username, password);
        }
    }

    public void onLoginClicked(View view){
        String nickname = editTextNickname.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String hashedPassword = BCrypt.hashpw(password, RegisterActivity.SALT);

        if (nickname.isEmpty() || password.isEmpty()) {
            // Empty fields
            Toast.makeText(MainActivity.this, "Hey there! It seems like you left some fields empty!", Toast.LENGTH_LONG).show();
            return;
        }

        new MainActivity.LoginTask().execute(nickname, hashedPassword);
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        private Exception exception;

        @Override
        protected Boolean doInBackground(String... params) {
            String nickname = params[0];
            String password = params[1];

            String svurl = "jdbc:postgresql://nothing2lose-db.carkfyqrpaoi.eu-north-1.rds.amazonaws.com:5432/NOTHING2LOSEDB";
            String svusername = "postgres";
            String svpassword = "8iy5df232";

            try (Connection conn = DriverManager.getConnection(svurl, svusername, svpassword)) {
                // Check if there is a user with the given username and password
                String selectQuery = "SELECT COUNT(*) FROM users WHERE username=? AND password=?";
                try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                    pstmt.setString(1, nickname);
                    pstmt.setString(2, password);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        rs.next();
                        int count = rs.getInt(1);
                        return count > 0;
                    }
                }
            } catch (Exception e) {
                Log.e("MyApp", "Error executing query", e);
                exception = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Successful Login!", Toast.LENGTH_LONG).show();
                saveUserInSharedPreferences();
                goToGamePage();
            } else {
                Toast.makeText(MainActivity.this, "We're sorry, but we couldn't log you in.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goToGamePage(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void onRegisterLinkClicked(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void saveUserInSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String hashedPassword = BCrypt.hashpw(editTextPassword.getText().toString(), RegisterActivity.SALT);

        editor.putString("username", editTextNickname.getText().toString());
        editor.putString("password", hashedPassword);
        editor.commit();
    }
}