package pt.ubi.di.pmd.nothing2lose;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.mindrot.jbcrypt.BCrypt;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextNickname;
    TextInputEditText editTextPassword;
    TextInputEditText editTextPasswordRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

       editTextNickname = findViewById(R.id.editTextNickname);
       editTextPassword = findViewById(R.id.editTextPassword);
       editTextPasswordRepeat = findViewById(R.id.editTextPasswordRepeat);
    }

    public void onRegisterClicked(View view) {
        String nickname = editTextNickname.getText().toString().trim();
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

        new UserRegistrationTask().execute(nickname, password);
    }

    private class UserRegistrationTask extends AsyncTask<String, Void, Boolean> {
        private Exception exception;

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Processing, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String nickname = params[0];
            String password = params[1];
            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(password, salt);

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
                        if (count == 0) {
                            // User does not exist, insert into database
                            String insertQuery = "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)";
                            try (PreparedStatement pstmt2 = conn.prepareStatement(insertQuery)) {
                                pstmt2.setString(1, nickname);
                                pstmt2.setString(2, hashedPassword);
                                pstmt2.setString(3, salt);
                                pstmt2.executeUpdate();
                            }
                            return true;
                        }
                        return false;
                    }
                }
            } catch (Exception e) {
                Log.e("MyApp", "Error executing query", e);
                exception = e;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();

            if (result) {
                Toast.makeText(getApplicationContext(), "Successful registration!", Toast.LENGTH_SHORT).show();
                saveUserInSharedPreferences();
                goToGamePage();
            } else {
                Toast.makeText(RegisterActivity.this, "Sorry, the nickname is already taken!", Toast.LENGTH_LONG).show();
            }
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

        editor.putString("username", editTextNickname.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.commit();
    }
}