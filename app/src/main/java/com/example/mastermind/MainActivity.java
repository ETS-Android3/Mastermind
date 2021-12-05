package com.example.mastermind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String INTEGER_GENERATOR_API = "https://www.random.org/integers";
    public static final String TAG = "MainActivity";

    private LinearLayout llContainerNumbers;
    private TextView tvGuessBox1;
    private TextView tvGuessBox2;
    private Button btnResetGuess;
    private Button btnSubmitGuess;

    private String[] secretNumber;
    private int[] numbersInSecretNumber;
    private int secretNumberLength;
    private int numberMin;
    private int numberMax;
    private ArrayList<TextView> listGuessBoxes;
    private int currentGuessPosition = 0;
    private ArrayList<PastGuess> pastGuesses;

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
        numberMin = 1;
        numberMax = 2;
        listGuessBoxes = new ArrayList<>(Arrays.asList(tvGuessBox1, tvGuessBox2));

        querySecretNumber();
        createNumberButtons();

        btnSubmitGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validGuess()) {
                    submitGuess();
                }
                else {
                    Toast.makeText(MainActivity.this, "Invalid guess. Choose a number for each position.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnResetGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGuessBoxes();
            }
        });
    }

    // Get random secret number from Integer Generator API
    private void querySecretNumber() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("num", secretNumberLength);
        params.put("min", numberMin);
        params.put("max", numberMax);
        params.put("col", 1);               // Return response arranged by 1 column per line
        params.put("base", 10);             // Use base 10 number system
        params.put("format", "plain");      // Get return response in plain text
        params.put("rnd", "new");           // Generate new random number

        client.get(INTEGER_GENERATOR_API, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, String response) {
                Log.d(TAG, "Integer Generator API request success!");

                // Store secret number's number value and index location
                secretNumber = response.split("\n");
                Log.i(TAG, "Secret number: " + Arrays.toString(secretNumber));

                // Store secret number's number value
                numbersInSecretNumber = new int[numberMax + 1];
                for (String number : secretNumber) {
                    numbersInSecretNumber[Integer.parseInt(number)] = 1;
                }
                Log.i(TAG, "Numbers in secret number: "
                        + Arrays.toString(numbersInSecretNumber));
            }

            @Override
            public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse,
                                  @Nullable Throwable throwable) {
                Log.d(TAG, "Integer Generator API request failure.");
                // !! Pop up window to notify error and generate new number
            }
        });
    }

    // Create button for each possible number
    private void createNumberButtons() {

        // Set button params
        for (int i = numberMin; i <= numberMax; ++i) {
            final Button button = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            params.setMargins(30, 0, 30, 0);
            button.setLayoutParams(params);
            button.setTextSize(24);
            button.setBackgroundColor(Color.CYAN);
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
                        Toast.makeText(MainActivity.this, "Submit or reset guess",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            llContainerNumbers.addView(button);
        }
    }

    // Check if every position in guess has valid number
    private Boolean validGuess() {

        for (int i = 0; i < secretNumberLength; ++i) {
            if (listGuessBoxes.get(i).getText().toString().equals("?")) {
                return false;
            }
        }

        return true;
    }

    // Submit valid guess
    private void submitGuess() {

        /* Records how many value OR value and location
           matches exist between guess and secret number
                1: value match in secret code
                2: value and location match in secret code
           Index does not matter, there is no specific order
        */
        ArrayList<Integer> matchedGuess = new ArrayList<>();

        for (int i = 0; i < secretNumberLength; ++i) {
            String currentNumber = listGuessBoxes.get(i).getText().toString();
            if (currentNumber.equals(secretNumber[i])
                    && numbersInSecretNumber[Integer.parseInt(currentNumber)] == 1) {
                matchedGuess.add(2);
            }
            else if (numbersInSecretNumber[Integer.parseInt(currentNumber)] == 1) {
                matchedGuess.add(1);
            }
        }
        Log.i(TAG, "Matched numbers in guess: " + matchedGuess.toString());


        int numCorrectValueAndLocation = Collections.frequency(matchedGuess, 2);
        int numCorrectValue = Collections.frequency(matchedGuess, 1);

        if (numCorrectValueAndLocation == secretNumberLength) {
            Toast.makeText(MainActivity.this, "Correct!", Toast.LENGTH_LONG).show();
        }
        else if (numCorrectValueAndLocation > 0
                && numCorrectValue > 0) {
            Toast.makeText(MainActivity.this, numCorrectValueAndLocation + " correct value and location + " + numCorrectValue + " correct value", Toast.LENGTH_LONG).show();
        }
        else if (numCorrectValueAndLocation > 0) {
            Toast.makeText(MainActivity.this, numCorrectValueAndLocation + " correct value and location + " , Toast.LENGTH_LONG).show();
        }
        else if (numCorrectValue > 0) {
            Toast.makeText(MainActivity.this, numCorrectValue + " correct value but incorrect location", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(MainActivity.this, "Wrong numbers!", Toast.LENGTH_LONG).show();
        }

        resetGuessBoxes();
    }

    // Reset all guess boxes
    private void resetGuessBoxes() {

        for (TextView guessBox : listGuessBoxes) {
            guessBox.setText("?");
        }
        currentGuessPosition = 0;
    }
}