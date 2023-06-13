package pt.ubi.di.pmd.nothing2lose;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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


/**
 * The RegisterActivity class is responsible for handling user registration.
 * It allows users to enter their email and password to create an account.
 */
public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;
    TextInputEditText editTextPasswordRepeat;

    /**
     * Called when the activity is created.
     * Sets the layout and initializes the UI elements.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

       editTextEmail = findViewById(R.id.editTextEmail);
       editTextPassword = findViewById(R.id.editTextPassword);
       editTextPasswordRepeat = findViewById(R.id.editTextPasswordRepeat);
    }

    /**
     * Handles the click event of the register button.
     * Validates the input fields and starts the UserRegistrationTask to register the user.
     *
     * @param view The View object that was clicked.
     */
    public void onRegisterClicked(View view) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordRepeat = editTextPasswordRepeat.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || passwordRepeat.isEmpty()) {
            // Empty fields
            Toast.makeText(RegisterActivity.this, "Hey there! It seems like you left some fields empty!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordRepeat)) {
            // Passwords don't match
            Toast.makeText(RegisterActivity.this, "It seems like the passwords you entered don't match!", Toast.LENGTH_LONG).show();
            return;
        }

        new UserRegistrationTask().execute(email, password);
    }

    /**
     * The UserRegistrationTask class is an AsyncTask used to perform user registration in the background.
     * It connects to a PostgreSQL database and checks if the email is available before inserting the new user.
     */
    @SuppressLint("StaticFieldLeak")
    private class UserRegistrationTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progressDialog;

        /**
         * This method is called before the user registration task starts.
         * It displays a progress dialog to indicate that the registration process is in progress.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Processing, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * This method performs the user registration task in the background.
         * It connects to the PostgreSQL database and checks if the email is available before inserting the new user.
         *
         * @param params The email and password passed as parameters.
         * @return true if the registration is successful, false otherwise.
         */
        @Override
        protected Boolean doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(password, salt);

            String svurl = "jdbc:postgresql://nothing2lose-db.carkfyqrpaoi.eu-north-1.rds.amazonaws.com:5432/NOTHING2LOSEDB";
            String svusername = "postgres";
            String svpassword = "8iy5df232";

            try (Connection conn = DriverManager.getConnection(svurl, svusername, svpassword)) {
                // Check if there is another user with the same username
                String selectQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                    pstmt.setString(1, email);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        rs.next();
                        int count = rs.getInt(1);
                        if (count == 0) {
                            // User does not exist, insert into database
                            String insertQuery = "INSERT INTO users (email, password, salt) VALUES (?, ?, ?)";
                            try (PreparedStatement pstmt2 = conn.prepareStatement(insertQuery)) {
                                pstmt2.setString(1, email);
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
            }
            return false;
        }

        /**
         * This method is called after the user registration task is completed.
         * It dismisses the progress dialog and performs actions based on the registration result.
         * @param result The result of the user registration task (true if successful, false otherwise).
         */
        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();

            if (result) {
                Toast.makeText(getApplicationContext(), "Successful registration!", Toast.LENGTH_SHORT).show();
                saveUserInSharedPreferences();
                goToGamePage();
            } else {
                Toast.makeText(RegisterActivity.this, "Sorry, the email is already in use!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Handles the click event of the login link.
     * Navigates to the MainActivity.
     *
     * @param view The View object that was clicked.
     */
    public void onLoginLinkClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates to the GameActivity.
     */
    public void goToGamePage(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    /**
     * Saves the user's email and password in SharedPreferences.
     * This allows the data to be persisted and accessed later.
     */
    public void saveUserInSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("email", editTextEmail.getText().toString());
        editor.putString("password", editTextPassword.getText().toString());
        editor.apply();
    }
}