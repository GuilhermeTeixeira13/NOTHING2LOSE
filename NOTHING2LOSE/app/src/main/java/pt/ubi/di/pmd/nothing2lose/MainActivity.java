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

import android.util.Log;
import android.view.View;
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
            new CheckIfUserExistsTask().execute(username, password);
        }
    }

    public void onLoginClicked(View view){
        String nickname = editTextNickname.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (nickname.isEmpty() || password.isEmpty()) {
            // Empty fields
            Toast.makeText(MainActivity.this, "Hey there! It seems like you left some fields empty!", Toast.LENGTH_LONG).show();
            return;
        }

        new CheckIfUserExistsTask().execute(nickname, password);
    }

    private class CheckIfUserExistsTask extends AsyncTask<String, Void, Boolean> {
        private Exception exception;

        String nickname;
        String password;


        @Override
        protected Boolean doInBackground(String... params) {
            nickname = params[0];
            password = params[1];

            String svurl = "jdbc:postgresql://nothing2lose-db.carkfyqrpaoi.eu-north-1.rds.amazonaws.com:5432/NOTHING2LOSEDB";
            String svusername = "postgres";
            String svpassword = "8iy5df232";

            try (Connection conn = DriverManager.getConnection(svurl, svusername, svpassword)) {
                // Check if there is a user with the given username and password
                String selectQuery = "SELECT COUNT(*) FROM users WHERE username=?";
                try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                    pstmt.setString(1, nickname);
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
                new MainActivity.LoginTask().execute(nickname, password);
            } else {
                Toast.makeText(MainActivity.this, "We're sorry, but we couldn't log you in. 1", Toast.LENGTH_LONG).show();
            }
        }
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
                String selectQuery = "SELECT * FROM users WHERE username = '" + nickname + "'";
                Log.d("MyApp", selectQuery);
                try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        String salt = rs.getString("salt");
                        String hashedPassword = BCrypt.hashpw(password, salt);

                        Log.d("MyApp", "HP LOGIN: "+ hashedPassword);
                        Log.d("MyApp", "HP DB: "+ rs.getString("password"));

                        if(hashedPassword.equals(rs.getString("password"))){
                            return true;
                        }
                    }
                    return false;
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
                saveUserInSharedPreferences();
                goToGamePage();
            } else {
                Toast.makeText(MainActivity.this, "We're sorry, but we couldn't log you in. 2", Toast.LENGTH_LONG).show();
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

        editor.putString("username", editTextNickname.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.commit();
    }
}