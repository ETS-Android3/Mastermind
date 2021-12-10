package com.example.mastermind;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Bind data from past guesses array list to recycler view in user interface to show display
 * past guesses and location or value matches.
 *
 * @author Kania Gandasetiawan
 * @version 1.0
 */
public class PastGuessAdapter extends RecyclerView.Adapter<PastGuessAdapter.ViewHolder> {

    public static final String TAG = "PastGuessAdapter";

    Context context;
    ArrayList<PastGuess> pastGuesses;

    /**
     * Class constructor.
     *
     * @param context       context used for adapter and recycler view
     * @param pastGuesses   array list of PastGuess class with past guess objects signifying
     *                      past guesses submitted by user
     */
    public PastGuessAdapter(Context context, ArrayList<PastGuess> pastGuesses) {

        this.context = context;
        this.pastGuesses = pastGuesses;
    }

    /**
     * Create view holder by inflating item past guess.
     *
     * @param parent    parent view group
     * @param viewType  past guess view
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View pastGuessView = LayoutInflater.from(context).inflate(R.layout.item_past_guess,
                parent, false);
        return new ViewHolder(pastGuessView);
    }

    /**
     * Bind past guess objects in past guesses to view holder.
     *
     * @param holder    past guess view holder
     * @param position  index of past guess object in past guesses array list
     */
    @Override
    public void onBindViewHolder(@NonNull PastGuessAdapter.ViewHolder holder, int position) {
        PastGuess pastGuess = pastGuesses.get(position);
        holder.bind(pastGuess);
    }

    /**
     * Get number of guesses in past guesses.
     *
     * @return  number of guesses in past guesses
     */
    @Override
    public int getItemCount() { return pastGuesses.size(); }

    /**
     * Represents view holder class used in recycler view.
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvPastGuessBox1;
        TextView tvPastGuessBox2;
        TextView tvPastGuessBox3;
        TextView tvPastGuessBox4;
        ImageView ivMatchedGuess1;
        ImageView ivMatchedGuess2;
        ImageView ivMatchedGuess3;
        ImageView ivMatchedGuess4;

        /**
         * Set views.
         *
         * @param itemView  item view of view holder
         */
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            tvPastGuessBox1 = itemView.findViewById(R.id.tvPastGuessBox1);
            tvPastGuessBox2 = itemView.findViewById(R.id.tvPastGuessBox2);
            tvPastGuessBox3 = itemView.findViewById(R.id.tvPastGuessBox3);
            tvPastGuessBox4 = itemView.findViewById(R.id.tvPastGuessBox4);
            ivMatchedGuess1 = itemView.findViewById(R.id.ivMatchedGuess1);
            ivMatchedGuess2 = itemView.findViewById(R.id.ivMatchedGuess2);
            ivMatchedGuess3 = itemView.findViewById(R.id.ivMatchedGuess3);
            ivMatchedGuess4 = itemView.findViewById(R.id.ivMatchedGuess4);
        }

        /**
         * Bind past guess object guess value and matched numbers to view holder. Bind guess
         * number value to guess box. For every "2" in guess matches, display location match icon.
         * For every "1", display value match icon.
         *
         * @param pastGuess past guess object fom past guesses array list signifying past guess
         *                  submitted by user
         */
        public void bind(PastGuess pastGuess) {

            int secretNumberLength = pastGuess.getGuess().length;

            TextView[] pastGuessBoxes = new TextView[secretNumberLength];
            pastGuessBoxes[0] = tvPastGuessBox1;
            pastGuessBoxes[1] = tvPastGuessBox2;
            pastGuessBoxes[2] = tvPastGuessBox3;
            pastGuessBoxes[3] = tvPastGuessBox4;

            ImageView[] matchedGuessHints = new ImageView[secretNumberLength];
            matchedGuessHints[0] = ivMatchedGuess1;
            matchedGuessHints[1] = ivMatchedGuess2;
            matchedGuessHints[2] = ivMatchedGuess3;
            matchedGuessHints[3] = ivMatchedGuess4;
            int guessHintPosition = 0;

            for (int i = 0; i < secretNumberLength; ++i) {
                pastGuessBoxes[i].setText(pastGuess.getGuess()[i]);
            }

            int valueAndLocationMatches =
                    Collections.frequency(pastGuess.getMatchedGuess(), 2);
            for (int i = 0; i < valueAndLocationMatches; ++i) {
                matchedGuessHints[guessHintPosition]
                        .setImageResource(R.drawable.ic_match_hint_location);   // Location match
                ++guessHintPosition;                                            // icon
            }

            int valueMatches =
                    Collections.frequency(pastGuess.getMatchedGuess(), 1);
            for (int i = 0; i < valueMatches; ++i) {
                matchedGuessHints[guessHintPosition]                            // Value match icon
                        .setImageResource(R.drawable.ic_match_hint_value);
                ++guessHintPosition;
            }

            Log.i(TAG, "From pastGuess in RV, "
                    + "guess: " + Arrays.toString(pastGuess.getGuess())
                    + ", matched numbers: " + pastGuess.getMatchedGuess().toString());
        }
    }
}
