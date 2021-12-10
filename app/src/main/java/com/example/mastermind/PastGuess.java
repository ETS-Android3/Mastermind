package com.example.mastermind;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Represents a past guess submitted by user.
 *
 * @author Kania Gandasetiawan
 * @version 1.0
 */
public class PastGuess {

    private String[] guess;
    private ArrayList<Integer> matchedGuess;

    /**
     * Class constructor.
     *
     * @param guess         user guess
     * @param matchedGuess  array list of matched guesses. Each "2" in array list signifies a
     *                      location match between user guess and secret code. Each "1" signifies
     *                      value match.
     */
    public PastGuess(String[] guess, ArrayList<Integer> matchedGuess) {

        this.guess = guess;
        this.matchedGuess = matchedGuess;
    }

    /**
     * Get user guess as string array.
     *
     * @return guess    user guess
     */
    public String[] getGuess() { return guess; }

    /**
     * Get location and value matches for guess.
     *
     * @return  matchedGuess array list of matched guesses. Each "2" in array list signifies a
     *                       location match between user guess and secret code. Each "1" signifies
     *                       value match.
     */
    public ArrayList<Integer> getMatchedGuess() {
        return matchedGuess;
    }
}
