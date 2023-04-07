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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText nicknameEditText;
    TextInputEditText editTextPassword;
    TextInputEditText editTextPasswordRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

       nicknameEditText = findViewById(R.id.editTextNickname);
       editTextPassword = findViewById(R.id.editTextPassword);
       editTextPasswordRepeat = findViewById(R.id.editTextPasswordRepeat);
    }

    public void onRegisterClicked(View view) {
        String nickname = nicknameEditText.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordRepeat = editTextPasswordRepeat.getText().toString().trim();

        if (nickname.isEmpty() || password.isEmpty() || passwordRepeat.isEmpty()) {
            // Empty fields
            Toast.makeText(RegisterActivity.this, "Hey there! It seems like you left some fields empty!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordRepeat)) {
            // Passwords don't match
            Toast.makeText(RegisterActivity.this, "It seems like the passwords you entered don't match!", Toast.LENGTH_LONG).show();
            return;
        }

        new CheckIfUserExistsTask().execute(nickname, password);
    }

    private class CheckIfUserExistsTask extends AsyncTask<String, Void, Boolean> {
        private Exception exception;

        @Override
        protected Boolean doInBackground(String... params) {
            String nickname = params[0];
            String password = params[1];

            String svurl = "jdbc:postgresql://nothing2lose-db.carkfyqrpaoi.eu-north-1.rds.amazonaws.com:5432/NOTHING2LOSEDB";
            String svusername = "postgres";
            String svpassword = "8iy5df232";

            try (Connection conn = DriverManager.getConnection(svurl, svusername, svpassword)) {
                // Check if there is another user with the same username
                String selectQuery = "SELECT COUNT(*) FROM users WHERE username=?";
                try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                    pstmt.setString(1, nickname);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        rs.next();
                        int count = rs.getInt(1);
                        return count == 0;
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
                String nickname = nicknameEditText.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                new UserRegistrationTask().execute(nickname, password);
            } else {
                Toast.makeText(RegisterActivity.this, "Sorry, the nickname is already taken!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class UserRegistrationTask extends AsyncTask<String, Void, Void> {
        private Exception exception;

        @Override
        protected Void doInBackground(String... params) {
            String nickname = params[0];
            String password = params[1];

            String svurl = "jdbc:postgresql://nothing2lose-db.carkfyqrpaoi.eu-north-1.rds.amazonaws.com:5432/NOTHING2LOSEDB";
            String svusername = "postgres";
            String svpassword = "8iy5df232";

            try (Connection conn = DriverManager.getConnection(svurl, svusername, svpassword)) {
                String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    pstmt.setString(1, nickname);
                    pstmt.setString(2, password);
                    pstmt.executeUpdate();
                }
                saveUserInSharedPreferences();
                goToGamePage();
            } catch (Exception e) {
                Log.e("MyApp", "Error executing query", e);
                exception = e;
            }
            return null;
        }
    }
    
    public void onLoginLinkClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToGamePage(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void saveUserInSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", nicknameEditText.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.commit();
    }
}