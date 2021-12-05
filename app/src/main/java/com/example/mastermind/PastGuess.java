package com.example.mastermind;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PastGuess {

    private String[] guess;
    private ArrayList<Integer> matchedGuess;

    public PastGuess(String[] guess, ArrayList<Integer> matchedGuess) {
        this.guess = guess;
        this.matchedGuess = matchedGuess;
    }

    public String[] getGuess() {
        return guess;
    }

    public ArrayList<Integer> getMatchedGuess() {
        return matchedGuess;
    }
}
