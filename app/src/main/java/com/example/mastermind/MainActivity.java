package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private LinearLayout llContainerNumbers;
    private TextView tvGuessBox1;
    private TextView tvGuessBox2;
    private Button btnResetGuess;
    private Button btnSubmitGuess;

    private ArrayList<String> secretNumber;
    private ArrayList<TextView> listGuessBoxes;
    private int secretNumberLength;
    private int numberMin;
    private int numberMax;
    private int currentGuessPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llContainerNumbers = findViewById(R.id.llContainerNumbers);

        tvGuessBox1 = findViewById(R.id.tvGuessBox1);
        tvGuessBox2 = findViewById(R.id.tvGuessBox2);
        btnResetGuess = findViewById(R.id.btnResetGuess);
        btnSubmitGuess = findViewById(R.id.btnSubmitGuess);

        secretNumberLength = 2;
        secretNumber = new ArrayList<>(Arrays.asList("2", "2"));

        numberMin = 1;
        numberMax = 2;
        listGuessBoxes = new ArrayList<>(Arrays.asList(tvGuessBox1, tvGuessBox2));

        createNumberButtons();

        btnResetGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGuessBoxes();
            }
        });

        btnSubmitGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean correctGuess = false;
                Boolean correctLocation = false;

                for (int i = 0; i < secretNumberLength; ++i) {
                    Log.i(TAG, "Checking guess");
                    for (int j = 0; j < secretNumberLength; ++j) {
                        Log.i(TAG, "Check: " + listGuessBoxes.get(i).getText() + secretNumber.get(j));
                        if (listGuessBoxes.get(i).getText().equals(secretNumber.get(j))) {
                            correctGuess = true;
                            if (i == j) {
                                correctLocation = true;
                            }
                        }
                    }
                }

                resetGuessBoxes();

                Log.i(TAG, "Correct Guess: " + correctGuess + ", Correct Location: " + correctLocation);
                if (correctGuess && correctLocation) {
                    Toast.makeText(MainActivity.this, "At least 1 correct number and location", Toast.LENGTH_SHORT).show();
                }
                else if (correctGuess) {
                    Toast.makeText(MainActivity.this, "At least 1 correct number", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Wrong numbers!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Create button for each possible number
    private void createNumberButtons() {
        for (int i = numberMin; i <= numberMax; ++i) {
            final Button button = new Button(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            button.setId(i);
            button.setText(Integer.toString(i));
            Log.i(TAG, "Button id: " + button.getId() + ", Button text: " + button.getText());

            // Update current guess box with number clicked
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentGuessPosition < secretNumberLength) {
                        TextView currentGuessBox = listGuessBoxes.get(currentGuessPosition);
                        currentGuessBox.setText(button.getText());
                        ++currentGuessPosition;
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Submit or reset guess", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            llContainerNumbers.addView(button);
        }
    }

    // Reset all guess boxes
    private void resetGuessBoxes() {
        for (TextView guessBox : listGuessBoxes) {
            guessBox.setText("?");
        }
        currentGuessPosition = 0;
    }
}