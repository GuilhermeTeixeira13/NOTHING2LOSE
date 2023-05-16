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

import com.google.android.material.textfield.TextInputEditText;

import org.mindrot.jbcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {

    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Retrieve user's login credentials
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        // Check if the username and password are not empty before using them to log the user in
        if (!email.isEmpty() && !password.isEmpty()) {
            new LoginTask().execute(email, password);
        }
    }

    public void onLoginClicked(View view) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            // Empty fields
            Toast.makeText(MainActivity.this, "Hey there! It seems like you left some fields empty!", Toast.LENGTH_LONG).show();
            return;
        }

        new LoginTask().execute(email, password);
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        private Exception exception;

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Processing, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            String svurl = "jdbc:postgresql://nothing2lose-db.carkfyqrpaoi.eu-north-1.rds.amazonaws.com:5432/NOTHING2LOSEDB";
            String svusername = "postgres";
            String svpassword = "8iy5df232";

            try (Connection conn = DriverManager.getConnection(svurl, svusername, svpassword)) {

                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String salt = rs.getString("salt");
                    String hashedPassword = BCrypt.hashpw(password, salt);

                    if (hashedPassword.equals(rs.getString("password"))) {
                        return true;
                    }
                }

                return false;
            } catch (Exception e) {
                Log.e("MyApp", "Error executing query", e);
                exception = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();

            if (result) {
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

        editor.putString("email", editTextEmail.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.commit();
    }
}