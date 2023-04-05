package pt.ubi.di.pmd.nothing2lose;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

    public ArrayList<Award> createAwards(double lambda, double minPrize, double maxPrize){
        ArrayList<Award> awards = new ArrayList<>();
        ArrayList<Integer> prizes = new ArrayList<>();
        int category = 1;

        for (int i = 0; i < 4; i++) {
            int prize = (int) generateRandomPrize(lambda, minPrize, maxPrize);
            prizes.add(prize);
            Log.d("MyApp", "Ticket Prize: " + prize);
        }

        Collections.sort(prizes);

        for(int prize : prizes){
            Award award = new Award(prize, category);
            awards.add(award);
            category++;
        }

        return awards;
    }

    public void AwardAChoosen(View v){
        Log.d("MyApp", "Clicked on A award.");
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