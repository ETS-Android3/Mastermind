package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nullable;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity
        implements PopupMenu.OnMenuItemClickListener, GameEndDialog.GameEndDialogListener {

    public static final String INTEGER_GENERATOR_API = "https://www.random.org/integers";
    public static final String TAG = "MainActivity";

    private RecyclerView rvPastGuesses;
    protected PastGuessAdapter pastGuessAdapter;

    private LinearLayout llContainerGuessBoxes;
    private LinearLayout llContainerNumbers1;
    private LinearLayout llContainerNumbers2;
    private TextView tvGuessRemaining;
    private TextView tvCountDownTimer;
    private Button btnResetGuess;
    private Button btnSubmitGuess;

    private String currentLevel;
    private String[] secretNumber;
    private int[] frequencyOfSecretNumbers;
    private int secretNumberLength;
    private int numberMin;
    private int numberMax;
    private int guessAllotted;
    private int numberOfGuessesUsed;
    private Boolean gameWon;

    private TextView listGuessBoxes[];
    private int currentGuessPosition = 0;
    private ArrayList<PastGuess> pastGuesses;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 120000;   // 2 min
    private Boolean timerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set containers
        llContainerGuessBoxes = findViewById(R.id.llContainerGuessBoxes);
        llContainerNumbers1 = findViewById(R.id.llContainerNumbers1);
        llContainerNumbers2 = findViewById(R.id.llContainerNumbers2);
        rvPastGuesses = findViewById(R.id.rvPastGuesses);

        // Set guess remaining, timer, reset, and submit  button
        tvGuessRemaining = findViewById(R.id.tvGuessRemaining);
        tvCountDownTimer = findViewById(R.id.tvCountDownTimer);
        btnResetGuess = findViewById(R.id.btnResetGuess);
        btnSubmitGuess = findViewById(R.id.btnSubmitGuess);

        // Set default game mode
        secretNumberLength = 4;
        guessAllotted = 10;
        numberOfGuessesUsed = 0;

        // Set up recycler view for past guesses
        pastGuesses = new ArrayList<>();
        pastGuessAdapter = new PastGuessAdapter(this, pastGuesses);
        rvPastGuesses.setAdapter(pastGuessAdapter);
        rvPastGuesses.setLayoutManager(new LinearLayoutManager(this));

        // Set and start game
        currentLevel = "normal";
        numberMin = 0;
        numberMax = 7;
        createGuessBoxes();
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
        secretNumber = ("1\n2\n3\n4").split("\n");
        frequencyOfSecretNumbers = new int[numberMax + 1];
        for (String number : secretNumber) {
            frequencyOfSecretNumbers[Integer.parseInt(number)] += 1;
        }

//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.put("num", secretNumberLength);
//        params.put("min", numberMin);
//        params.put("max", numberMax);
//        params.put("col", 1);               // Return response arranged by 1 column per line
//        params.put("base", 10);             // Use base 10 number system
//        params.put("format", "plain");      // Get return response in plain text
//        params.put("rnd", "new");           // Generate new random number
//
//        client.get(INTEGER_GENERATOR_API, params, new TextHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, String response) {
//                Log.d(TAG, "Integer Generator API request success!");
//
//                // Store secret number's number value and index location
//                secretNumber = response.split("\n");
//                Log.i(TAG, "Secret number: " + Arrays.toString(secretNumber));
//
//                // Store secret number's number value
//                frequencyOfSecretNumbers = new int[numberMax + 1];
//                for (String number : secretNumber) {
//                    frequencyOfSecretNumbers[Integer.parseInt(number)] += 1;
//                }
//                Log.i(TAG, "Numbers in secret number: "
//                        + Arrays.toString(frequencyOfSecretNumbers));
//            }
//
//            @Override
//            public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse,
//                                  @Nullable Throwable throwable) {
//                Log.d(TAG, "Integer Generator API request failure.");
//                // !! Pop up window to notify error and generate new number
//            }
//        });
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
                currentLevel = "easy";
                updateLevel(currentLevel);
                hideCountDownTimer();
                return true;

            case R.id.normal:
                currentLevel = "normal";
                updateLevel(currentLevel);
                hideCountDownTimer();
                return true;

            case R.id.challenge:
                currentLevel = "challenge";
                tvCountDownTimer.setVisibility(View.VISIBLE);
                updateLevel(currentLevel);
                return true;

            default:
                return false;
        }
    }



    private void updateLevel(String currentLevel) {
        switch (currentLevel) {
             case "easy":
                numberMin = 0;
                numberMax = 5;
                break;
            case "normal":
                numberMin = 0;
                numberMax = 7;
                break;
            case "challenge":
                numberMin = 0;
                numberMax = 9;
                break;
        }

        createNumberButtons();
        resetGame();
    }

    // Reset game: get new secret number, clear past guesses, reset guesses used
    private void resetGame() {
        querySecretNumber();
        numberOfGuessesUsed = 0;

        updateGuessRemaining();
        resetGuessBoxes();
        pastGuesses.clear();
        pastGuessAdapter.notifyDataSetChanged();

        if (currentLevel == "challenge") {
            timeLeftInMilliseconds = 120000;   // 2 min
            startCountDownTimer();
        }

//        finish();
//        startActivity(getIntent());
//        overridePendingTransition(0,0);
    }

    // Create guess boxes
    private void createGuessBoxes() {

        listGuessBoxes = new TextView[secretNumberLength];

        // Set guess box params
        for (int i = 0; i < secretNumberLength; ++i) {
            final TextView textView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            params.setMargins(30, 0, 30, 0);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(34);
            textView.setTextColor(getResources().getColor(R.color.text));
            textView.setBackground(getResources().getDrawable(R.drawable.background_guess_box));
            textView.setId(i);
            textView.setText("?");
            Log.i(TAG, "Guess box id: " + textView.getId() + ", Button text: " + textView.getText());

            // Add new guess box to listGuessBoxes and guess box container
            listGuessBoxes[i] = textView;
            llContainerGuessBoxes.addView(textView);
        }
    }

    // Create button for each possible number
    private void createNumberButtons() {

        // Remove buttons, if exists
        llContainerNumbers1.removeAllViews();
        llContainerNumbers2.removeAllViews();

        // Create number buttons in containers
        createNumberButtonsInContainer(
                llContainerNumbers1, numberMin, numberMax / 2);
        createNumberButtonsInContainer(
                llContainerNumbers2, (numberMax / 2) + 1, numberMax);
    }

    // Create number buttons in container
    private void createNumberButtonsInContainer(
            LinearLayout llContainerNumbers, int numberStart, int numberEnd) {

        // Set button params
        for (int i = numberStart; i <= numberEnd; ++i) {
            final Button button = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            params.setMargins(30, 0, 30, 0);
            button.setLayoutParams(params);
            button.setTextSize(24);
            button.setTextColor(getResources().getColor(R.color.text));
            button.setBackgroundColor(getResources().getColor(R.color.secondary_button));
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
                        Toast.makeText(MainActivity.this, "Enter or reset guess",
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

    // Open win dialog when user wins
    private void gameWon() {

        if (currentLevel == "challenge") {
            stopCountDownTimer();
        }

        gameWon = true;
        openGameEndDialog();
    }

    // Open lose dialog when user wins
    private void gameOver() {

        if (currentLevel == "challenge") {
            stopCountDownTimer();
        }

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

    // Start countdown timer
    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                updateCountDownTimer();
            }

            @Override
            public void onFinish() {
                gameOver();
            }
        }.start();

        timerRunning = true;
    }

    // Update seconds left in timer
    private void updateCountDownTimer() {
        int minutes = (int) timeLeftInMilliseconds / 60000;         // 60000 milliseconds per second
        int seconds = (int) timeLeftInMilliseconds % 60000 / 1000;

        // Build timer, ex. 2:08
        String timeLeft = minutes + ":" ;
        if (seconds < 10) {
            timeLeft += "0";
        }
        timeLeft += seconds;
        Log.i(TAG, timeLeft);

        tvCountDownTimer.setText(timeLeft);
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void hideCountDownTimer() {
        if (timerRunning) {
            stopCountDownTimer();
            tvCountDownTimer.setVisibility(View.INVISIBLE);
        }
    }

}