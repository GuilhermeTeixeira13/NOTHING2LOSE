package pt.ubi.di.pmd.nothing2lose;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import java.sql.Connection;
import java.sql.DriverManager;
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

public class MainActivity extends AppCompatActivity {

    Button ButtonAwardA;
    Button ButtonAwardB;
    Button ButtonAwardC;
    Button ButtonAwardD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButtonAwardA = (Button) findViewById(R.id.awardA);
        ButtonAwardB = (Button) findViewById(R.id.awardB);
        ButtonAwardC = (Button) findViewById(R.id.awardC);
        ButtonAwardD = (Button) findViewById(R.id.awardD);

        Log.d("MyApp", "App started.");

        double lambda = 5;
        double minPrize = 0.0;
        double maxPrize = 1000.0;

        ArrayList<Award> awards = createAwards(lambda, minPrize, maxPrize);
        Log.d("MyApp", "Awards sorted: " + awards.toString());

        Collections.shuffle(awards);
        Log.d("MyApp", "Awards shuffled: " + awards.toString());
    }

    public static double generateRandomPrize(double lambda, double min, double max) {
        Random random = new Random();
        double prize = Math.exp(-lambda * random.nextDouble());
        return (max - min) * prize + min;
    }

    public ArrayList<Award> createAwards(double lambda, double minPrize, double maxPrize) {
        ArrayList<Award> awards = new ArrayList<>();
        ArrayList<Integer> prizes = new ArrayList<>();
        int category = 1;

        for (int i = 0; i < 4; i++) {
            int prize = (int) generateRandomPrize(lambda, minPrize, maxPrize);
            prizes.add(prize);
            Log.d("MyApp", "Ticket Prize: " + prize);
        }

        Collections.sort(prizes);

        for (int prize : prizes) {
            Award award = new Award(prize, category);
            awards.add(award);
            category++;
        }

        return awards;
    }

    public void AwardAChoosen(View v) throws SQLException {
        Log.d("MyApp", "Clicked on A award.");

        new DatabaseQueryTask().execute();
    }

    private class DatabaseQueryTask extends AsyncTask<Void, Void, List<String>> {
        private Exception exception;

        protected List<String> doInBackground(Void... voids) {
            List<String> results = new ArrayList<>();

            String url = "jdbc:postgresql://nothing2lose-db.carkfyqrpaoi.eu-north-1.rds.amazonaws.com:5432/NOTHING2LOSEDB";
            String username = "postgres";
            String password = "8iy5df232";

            try {
                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection(url, username, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users");

                while (rs.next()) {
                    // Process the data retrieved from the database
                    // For example, you can add the values to a list
                    String id = String.valueOf(rs.getInt("ID"));
                    String user = rs.getString("username");
                    String pass = rs.getString("password");
                    results.add("ID: " + id + ", Username: " + user + ", Password: " + pass);
                }

                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                Log.e("MyApp", "Error executing query", e);
                exception = e;
            }

            return results;
        }

        protected void onPostExecute(List<String> results) {
            if (exception != null) {
                Toast.makeText(MainActivity.this, "Error executing query: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                // Update UI with the database query results
                for (String result : results) {
                    Log.d("MyApp", result);
                }
            }
        }
    }

    public void AwardBChoosen(View v){
        Log.d("MyApp", "Clicked on B award.");
    }

    public void AwardCChoosen(View v){
        Log.d("MyApp", "Clicked on C award.");
    }

    public void AwardDChoosen(View v){
        Log.d("MyApp", "Clicked on D award.");
    }
}