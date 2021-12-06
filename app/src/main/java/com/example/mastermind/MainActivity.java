package com.example.mastermind;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity
        implements PopupMenu.OnMenuItemClickListener, GameEndDialog.GameEndDialogListener {

    public static final String INTEGER_GENERATOR_API = "https://www.random.org/integers";
    public static final String TAG = "MainActivity";

    private RecyclerView rvPastGuesses;
    protected PastGuessAdapter pastGuessAdapter;
    private LinearLayout llContainerNumbers;
    private TextView tvGuessBox1;
    private TextView tvGuessBox2;
    private TextView tvGuessRemaining;
    private Button btnResetGuess;
    private Button btnSubmitGuess;

    private String[] secretNumber;
    private int[] frequencyOfSecretNumbers;
    private int secretNumberLength;
    private int numberMin;
    private int numberMax;
    private int guessAllotted;
    private int numberOfGuessesUsed;
    private int guessRemaining;
    private Boolean gameWon;

    private TextView listGuessBoxes[];
    private int currentGuessPosition = 0;
    private ArrayList<PastGuess> pastGuesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvPastGuesses = findViewById(R.id.rvPastGuesses);
        llContainerNumbers = findViewById(R.id.llContainerNumbers);

        tvGuessBox1 = findViewById(R.id.tvGuessBox1);
        tvGuessBox2 = findViewById(R.id.tvGuessBox2);
        tvGuessRemaining = findViewById(R.id.tvGuessRemaining);
        btnResetGuess = findViewById(R.id.btnResetGuess);
        btnSubmitGuess = findViewById(R.id.btnSubmitGuess);

        // Set game mode: easy, medium, hard
        secretNumberLength = 2;
        numberMin = 1;
        numberMax = 5;
        guessAllotted = 3;
        numberOfGuessesUsed = 0;

        // Set up recycler view for past guesses
        pastGuesses = new ArrayList<>();
        pastGuessAdapter = new PastGuessAdapter(this, pastGuesses);
        rvPastGuesses.setAdapter(pastGuessAdapter);
        rvPastGuesses.setLayoutManager(new LinearLayoutManager(this));

        listGuessBoxes = new TextView[secretNumberLength];
        listGuessBoxes[0] = tvGuessBox1;
        listGuessBoxes[1] = tvGuessBox2;

        createNumberButtons();
        updateGuessRemaining();
        querySecretNumber();

        btnSubmitGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validGuess()) {
                    submitGuess();
                }
                else {
                    Toast.makeText(MainActivity.this,
                            "Choose a number for each position", Toast.LENGTH_SHORT).show();
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
                frequencyOfSecretNumbers = new int[numberMax + 1];
                for (String number : secretNumber) {
                    frequencyOfSecretNumbers[Integer.parseInt(number)] += 1;
                }
                Log.i(TAG, "Numbers in secret number: "
                        + Arrays.toString(frequencyOfSecretNumbers));
            }

            @Override
            public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse,
                                  @Nullable Throwable throwable) {
                Log.d(TAG, "Integer Generator API request failure.");
                // !! Pop up window to notify error and generate new number
            }
        });
    }

    // Create pop up menu for levels: easy, normal, challenge
    public void showPopupLevels(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu_levels);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.easy:
                Toast.makeText(MainActivity.this, "Easy",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.normal:
                Toast.makeText(MainActivity.this, "Normal",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.challenge:
                Toast.makeText(MainActivity.this, "Challenge",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
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
                        TextView currentGuessBox = listGuessBoxes[currentGuessPosition];
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
            if (listGuessBoxes[i].getText().toString().equals("?")) {
                return false;
            }
        }

        return true;
    }

    // Submit valid guess
    private void submitGuess() {

        // Add each value from guess box into guess
        String[] guess = new String[secretNumberLength];
        for (int i = 0; i < secretNumberLength; ++i) {
            guess[i] = listGuessBoxes[i].getText().toString();
        }
        Log.i(TAG, "Current guess: " + Arrays.toString(guess));

        // Check if guess matches secret number
        Boolean matchSecretNumber = true;
        for (int i = 0; i < secretNumberLength; ++i) {
            if (!guess[i].equals(secretNumber[i])) {
                matchSecretNumber = false;
            }
        }

        if (matchSecretNumber) {
            Log.i(TAG, "Game won!");
            gameWon();
        }
        else {
            /* Record how many value OR value and location
               matches exist between guess and secret number
                    1: value match in secret code
                    2: value and location match in secret code
               Index does not matter, there is no specific order
            */
            ArrayList<Integer> matchedGuess = new ArrayList<>();
            int[] frequencyOfSecretNumbersCopy =
                    Arrays.copyOf(frequencyOfSecretNumbers, numberMax + 1);

            for (int i = 0; i < secretNumberLength; ++i) {
                if (guess[i].equals(secretNumber[i])
                        && frequencyOfSecretNumbersCopy[Integer.parseInt(guess[i])] > 0) {
                    matchedGuess.add(2);
                    frequencyOfSecretNumbersCopy[Integer.parseInt(guess[i])] -= 1;
                } else if (frequencyOfSecretNumbersCopy[Integer.parseInt(guess[i])] > 0) {
                    matchedGuess.add(1);
                    frequencyOfSecretNumbersCopy[Integer.parseInt(guess[i])] -= 1;
                }
            }
            Log.i(TAG, "Matched numbers: " + matchedGuess.toString());

            // Add new guess into PastGuesses
            int positionAdded = 0;
            pastGuesses.add(positionAdded, new PastGuess(guess, matchedGuess));
            pastGuessAdapter.notifyItemInserted(positionAdded);
            Log.i(TAG, "Guess added to pastGuesses: "
                    + Arrays.toString(pastGuesses.get(positionAdded).getGuess()));

            guessUsed();
            updateGuessRemaining();

            if (remainingGuessExists()) {
                resetGuessBoxes();
            } else {
                Log.i(TAG, "Game over!");
                gameOver();
            }
        }
    }

    private Boolean remainingGuessExists() {
        return numberOfGuessesUsed < guessAllotted;
    }

    // Increment guesses used !! add guesses left
    private void guessUsed() {
        ++numberOfGuessesUsed;
    }

    private void updateGuessRemaining() {
        tvGuessRemaining.setText(Integer.toString(guessAllotted - numberOfGuessesUsed));
    }

    // Reset all guess boxes and current guess position
    private void resetGuessBoxes() {

        for (TextView guessBox : listGuessBoxes) {
            guessBox.setText("?");
        }
        currentGuessPosition = 0;
    }

    // Reset game: get new secret number, clear past guesses, reset guesses used
    private void resetGame() {
//        querySecretNumber();
//        numberOfGuessesUsed = 0;
//
//        updateGuessRemaining();
//        resetGuessBoxes();
//        pastGuesses.clear();
//        pastGuessAdapter.notifyDataSetChanged();

        finish();
        startActivity(getIntent());
        overridePendingTransition(0,0);
    }

    // Open win dialog when user wins
    private void gameWon() {
        gameWon = true;
        openGameEndDialog();
    }

    // Open lose dialog when user wins
    private void gameOver() {
        gameWon = false;
        openGameEndDialog();
    }

    // Call game end dialog when user wins or loses
    private void openGameEndDialog() {
        GameEndDialog gameEndDialog = new GameEndDialog(gameWon);
        gameEndDialog.show(getSupportFragmentManager(), "GameEndDialogue");
    }

    // Reset game when user clicks "Try Again" at game end
    @Override
    public void onTryAgainClicked() {
        resetGame();
    }
}