package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.media.MediaPlayer;
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

/**
 * Main activity of Mastermind game.
 *
 * @author Kania Gandasetiawan
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity
        implements PopupMenu.OnMenuItemClickListener, GameEndDialog.GameEndDialogListener {

    public static final String INTEGER_GENERATOR_API = "https://www.random.org/integers";
    public static final String TAG = "MainActivity";

    private LinearLayout llContainerGuessBoxes;
    private LinearLayout llContainerNumbers1;
    private LinearLayout llContainerNumbers2;
    private TextView tvGuessRemaining;
    private TextView tvCountDownTimer;
    private Button btnResetGuess;
    private Button btnSubmitGuess;

    private RecyclerView rvPastGuesses;
    protected PastGuessAdapter pastGuessAdapter;
    private ArrayList<PastGuess> pastGuesses;

    private TextView listGuessBoxes[];
    private int currentGuessBoxPosition = 0;

    private String currentLevel;
    private String[] secretNumber;
    private int[] frequencyOfSecretNumbers;
    private int secretNumberLength;
    private int numberMin;
    private int numberMax;
    private int guessAllotted;
    private int numberOfGuessesUsed;
    private Boolean gameWon;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 120000;   // 2 min
    private Boolean timerRunning = false;

    private MediaPlayer mediaPlayer;
    private int currentBgm;
    private int bgmDefault;
    private int bgmChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        llContainerGuessBoxes = findViewById(R.id.llContainerGuessBoxes);
        llContainerNumbers1 = findViewById(R.id.llContainerNumbers1);
        llContainerNumbers2 = findViewById(R.id.llContainerNumbers2);
        rvPastGuesses = findViewById(R.id.rvPastGuesses);

        tvGuessRemaining = findViewById(R.id.tvGuessRemaining);
        tvCountDownTimer = findViewById(R.id.tvCountDownTimer);
        btnResetGuess = findViewById(R.id.btnResetGuess);
        btnSubmitGuess = findViewById(R.id.btnSubmitGuess);

        // Set up recycler view for past guesses
        pastGuesses = new ArrayList<>();
        pastGuessAdapter = new PastGuessAdapter(this, pastGuesses);
        rvPastGuesses.setAdapter(pastGuessAdapter);
        rvPastGuesses.setLayoutManager(new LinearLayoutManager(this));

        // Set default normal mode and start game
        secretNumberLength = 4;
        guessAllotted = 10;
        numberOfGuessesUsed = 0;

        currentLevel = "normal";
        numberMin = 0;
        numberMax = 7;
        querySecretNumber();
        createGuessBoxes();
        createNumberButtons();
        createSubmitAndResetButtons();
        updateGuessRemaining();

        bgmDefault = R.raw.bgm_default;
        bgmChallenge = R.raw.bgm_challenge;
        currentBgm = bgmDefault;
        startBackgroundMusic();
    }

    /**
     * Get random secret number from Integer Generator API. Stores each number value as string into
     * array. Also stores frequency of each number value into that value's index in an array. For
     * example, a secret number of "1 2 3 1" would have a freqyency array of [0, 2, 1, 1].
     */
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

                secretNumber = response.split("\n");
                Log.i(TAG, "Secret number: " + Arrays.toString(secretNumber));

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
            }
        });
    }

    /**
     * Create popup menu for levels: easy, normal, challenge.
     * @param   view    view for popup menu
     */
    public void showPopupLevels(View view) {

        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu_levels);
        popup.show();
    }

    /**
     * Update current level when specified level is clicked. Call update level function to
     * update level.
     *
     * @param   menuItem    menu item with level options
     * @return  boolean     if menu item clicked, return true
     */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.easy:
                currentLevel = "easy";
                updateLevel(currentLevel);
                return true;
            case R.id.normal:
                currentLevel = "normal";
                updateLevel(currentLevel);
                return true;
            case R.id.challenge:
                currentLevel = "challenge";
                updateLevel(currentLevel);
                return true;
            default:
                return false;
        }
    }

    /**
     * Update game based on specified level's settings. Update mininum and maximum integers
     * possible for secret code (ex. digits 0-7) and update background music for specified level.
     * Call create number buttons and reset game functions to set up game with specified level's
     * settings.
     *
     * @param currentLevel  current level mode
     */
    private void updateLevel(String currentLevel) {

        switch (currentLevel) {
             case "easy":
                numberMin = 0;
                numberMax = 5;
                currentBgm = bgmDefault;
                break;
            case "normal":
                numberMin = 0;
                numberMax = 7;
                currentBgm = bgmDefault;
                break;
            case "challenge":
                numberMin = 0;
                numberMax = 9;
                currentBgm = bgmChallenge;
                break;
        }

        createNumberButtons();
        resetGame();
    }

    /**
     * Reset game by querying for new secret number, resetting guess used count,
     * remove past guesses, and resets background music for current level and timer, if applicable,
     * for current level. If challenge level, set up timer.
     */
    private void resetGame() {

        querySecretNumber();
        numberOfGuessesUsed = 0;
        updateGuessRemaining();
        resetGuessBoxes();

        pastGuesses.clear();
        pastGuessAdapter.notifyDataSetChanged();

        if (currentLevel == "challenge") {
            tvCountDownTimer.setVisibility(View.VISIBLE);
            timeLeftInMilliseconds = 120000;                // 2 min
            startCountDownTimer();
        }
        else {
            hideCountDownTimer();
        }
        stopBackgroundMusic();
        startBackgroundMusic();
    }

    /**
     * Create text view guess boxes to display user's current guess.
     */
    private void createGuessBoxes() {

        listGuessBoxes = new TextView[secretNumberLength];

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
            Log.i(TAG, "Guess box id: " + textView.getId()
                    + ", Button text: " + textView.getText());

            listGuessBoxes[i] = textView;
            llContainerGuessBoxes.addView(textView);
        }
    }

    /**
     * Create button for possible number values in secret number.
     */
    private void createNumberButtons() {

        llContainerNumbers1.removeAllViews();
        llContainerNumbers2.removeAllViews();

        createNumberButtonsInContainer(
                llContainerNumbers1, numberMin, numberMax / 2);
        createNumberButtonsInContainer(
                llContainerNumbers2, (numberMax / 2) + 1, numberMax);
    }

    /**
     * Create buttons for range of number values and insert in a container. When buttons are
     * clicked, guess box is updated with the number value clicked. If button is clicked after
     * length of secret number is reached, a toast pops up notifying user to enter or reset guess.
     *
     * @param llContainerNumbers    container to generate number buttons in
     * @param numberStart           starting value for number buttons generated
     * @param numberEnd             last value for number buttons generated
     */
    private void createNumberButtonsInContainer(
            LinearLayout llContainerNumbers, int numberStart, int numberEnd) {

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
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentGuessBoxPosition < secretNumberLength) {
                        TextView currentGuessBox = listGuessBoxes[currentGuessBoxPosition];
                        currentGuessBox.setText(button.getText());
                        ++currentGuessBoxPosition;
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

    /**
     * Create submit and reset buttons. If submit button is clicked without invalid guess, a toast
     * will notify user to choose a number for each guess position.
     */
    private void createSubmitAndResetButtons() {

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

    /**
     * Check if guess is valid by checking length of user guess with length of secret number length.
     *
     * @return  true    if guess and secret number length match
     *          false   otherwise
     */
    private Boolean validGuess() {

        for (int i = 0; i < secretNumberLength; ++i) {
            if (listGuessBoxes[i].getText().toString().equals("?")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if user guess matches secret code, adds guess to past guesses array list, and updates
     * remaining guesses. If guess is correct, call game won function. If not, record whether each
     * digit has a location or value match into match guess string array list. Add "2" to match
     * guess array for each location match, and add 1 for each value match. Update remaining
     * guesses. If guesses remain, reset guess box. If not, call game over function.
     */
    private void submitGuess() {

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
            gameWon();
            Log.i(TAG, "Game won!");
        }
        else {
            ArrayList<Integer> matchedGuess = new ArrayList<>();
            String[] guessCopy = Arrays.copyOf(guess, secretNumberLength);

            int[] frequencyOfSecretNumbersCopy =
                    Arrays.copyOf(frequencyOfSecretNumbers, numberMax + 1);

            // Check location matches
            for (int i = 0; i < secretNumberLength; ++i) {
                if (guessCopy[i].equals(secretNumber[i])
                        && frequencyOfSecretNumbersCopy[Integer.parseInt(guess[i])] > 0) {
                    matchedGuess.add(2);
                    frequencyOfSecretNumbersCopy[Integer.parseInt(guess[i])] -= 1;
                    guessCopy[i] = "-1";
                }
            }

            // Check value matches
            for (int i = 0; i < secretNumberLength; ++i) {
                if (guessCopy[i] != "-1") {
                    int matchedValue = Integer.parseInt(guessCopy[i]);

                    if (frequencyOfSecretNumbersCopy[matchedValue] > 0) {
                        matchedGuess.add(1);
                        frequencyOfSecretNumbersCopy[matchedValue] -= 1;
                    }
                }
            }
            Log.i(TAG, "Matched numbers: " + matchedGuess.toString());

            // Add new guess into PastGuesses
            int positionAdded = 0;
            pastGuesses.add(positionAdded, new PastGuess(guess, matchedGuess));
            pastGuessAdapter.notifyItemInserted(positionAdded);
            Log.i(TAG, "Guess added to pastGuesses: "
                    + Arrays.toString(pastGuesses.get(positionAdded).getGuess()));

            // Update guesses used
            guessUsed();
            updateGuessRemaining();

            if (remainingGuessExists()) {
                resetGuessBoxes();
            } else {
                gameOver();
                Log.i(TAG, "Game over!");
            }
        }
    }

    /**
     * Checks if remaining guess exists
     * @return  true    if remaining guess exists
     *          false   otherwise
     */
    private Boolean remainingGuessExists() { return numberOfGuessesUsed < guessAllotted; }

    /**
     * Increment guesses used
     */
    private void guessUsed() { ++numberOfGuessesUsed; }

    /**
     * Update remaining guess in user interface
     */
    private void updateGuessRemaining() {

        tvGuessRemaining.setText(Integer.toString(guessAllotted - numberOfGuessesUsed));
    }

    /**
     * Reset all guess boxes and current guess position for guess box
     */
    private void resetGuessBoxes() {

        for (TextView guessBox : listGuessBoxes) {
            guessBox.setText("?");
        }
        currentGuessBoxPosition = 0;
    }

    /**
     * Open win dialog when user wins. For challenge level, calls stop countdown timer.
     */
    private void gameWon() {

        if (currentLevel == "challenge") {
            stopCountDownTimer();
        }

        gameWon = true;
        openGameEndDialog();
    }

    /**
     * Open lose dialog when user loses. For challenge level, calls stop countdown timer.
     */
    private void gameOver() {

        if (currentLevel == "challenge") {
            stopCountDownTimer();
        }

        gameWon = false;
        openGameEndDialog();
    }

    /**
     * Call popup dialog notifying game end when user wins or loses
     */
    private void openGameEndDialog() {

        GameEndDialog gameEndDialog = new GameEndDialog(gameWon);
        gameEndDialog.show(getSupportFragmentManager(), "GameEndDialogue");
    }

    /**
     * Reset game when user clicks "Try Again" button on game end dialog
     */
    @Override
    public void onTryAgainClicked() {

        resetGame();
    }

    /**
     * Start countdown timer for challenge mode. When timer runs out, call game over.
     */
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

    /**
     * Update timer on user interface.
     */
    private void updateCountDownTimer() {

        int minutes = (int) timeLeftInMilliseconds / 60000;         // 60000 milliseconds per second
        int seconds = (int) timeLeftInMilliseconds % 60000 / 1000;

        String timeLeft = minutes + ":" ;
        if (seconds < 10) {
            timeLeft += "0";
        }
        timeLeft += seconds;
        Log.i(TAG, timeLeft);

        tvCountDownTimer.setText(timeLeft);
    }

    /**
     * Stop count down timer.
     */
    private void stopCountDownTimer() {

        countDownTimer.cancel();
        timerRunning = false;
    }

    /**
     * Stop and hide count down timer.
     */
    private void hideCountDownTimer() {

        if (timerRunning) {
            stopCountDownTimer();
            tvCountDownTimer.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Play background music on loop.
     */
    public void startBackgroundMusic() {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, currentBgm);
        }
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    /**
     * Pause background music.
     */
    public void pauseBackgroundMusic() {

        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * Stop and release background music.
     */
    public void stopBackgroundMusic() {

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Start background music on resume.
     */
    @Override
    protected void onResume() {

        super.onResume();
        startBackgroundMusic();
    }

    /**
     * Pause background music on pause.
     */
    @Override
    protected void onPause() {

        super.onPause();
        pauseBackgroundMusic();
    }

    /**
     * Stop and release background music on stop.
     */
    @Override
    protected void onStop() {

        super.onStop();
        stopBackgroundMusic();
    }
}