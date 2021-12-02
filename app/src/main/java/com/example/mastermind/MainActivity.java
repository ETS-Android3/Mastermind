package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    
    private TextView tvGuessBox1;
    private TextView tvGuessBox2;
    private Button btnNumber1;
    private Button btnNumber2;
    private Button btnResetGuess;
    private Button btnSubmitGuess;

    private ArrayList<String> secretNumber;
    private ArrayList<TextView> listGuessBoxes;
    private ArrayList<Button> listPossibleNumbers;
    private int secretNumberLength;
    private int currentGuessPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvGuessBox1 = findViewById(R.id.tvGuessBox1);
        tvGuessBox2 = findViewById(R.id.tvGuessBox2);
        btnNumber1 = findViewById(R.id.btnNumber1);
        btnNumber2 = findViewById(R.id.btnNumber2);
        btnResetGuess = findViewById(R.id.btnResetGuess);
        btnSubmitGuess = findViewById(R.id.btnSubmitGuess);

        secretNumberLength = 2;
        secretNumber = new ArrayList<>(Arrays.asList("2", "2"));
        listGuessBoxes = new ArrayList<>(Arrays.asList(tvGuessBox1, tvGuessBox2));
        listPossibleNumbers = new ArrayList<>(Arrays.asList(btnNumber1, btnNumber2));

        for (Button buttonNumber : listPossibleNumbers) {
            buttonNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentGuessPosition < secretNumberLength) {
                        TextView currentGuessBox = listGuessBoxes.get(currentGuessPosition);
                        currentGuessBox.setText(buttonNumber.getText());
                        ++currentGuessPosition;
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Submit or reset guess", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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

    private void resetGuessBoxes() {
        for (TextView guessBox : listGuessBoxes) {
            guessBox.setText("?");
        }
        currentGuessPosition = 0;
    }
}