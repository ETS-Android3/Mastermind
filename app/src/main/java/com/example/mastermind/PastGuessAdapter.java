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

public class PastGuessAdapter extends RecyclerView.Adapter<PastGuessAdapter.ViewHolder> {

    Context context;
    ArrayList<PastGuess> pastGuesses;

    public PastGuessAdapter(Context context, ArrayList<PastGuess> pastGuesses) {
        this.context = context;
        this.pastGuesses = pastGuesses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View pastGuessView = LayoutInflater.from(context).inflate(R.layout.item_past_guess,
                parent, false);
        return new ViewHolder(pastGuessView);
    }

    @Override
    public void onBindViewHolder(@NonNull PastGuessAdapter.ViewHolder holder, int position) {
        PastGuess pastGuess = pastGuesses.get(position);
        holder.bind(pastGuess);
    }

    @Override
    public int getItemCount() {
        return pastGuesses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvPastGuessBox1;
        TextView tvPastGuessBox2;
        ImageView ivMatchedGuess1;
        ImageView ivMatchedGuess2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPastGuessBox1 = itemView.findViewById(R.id.tvPastGuessBox1);
            tvPastGuessBox2 = itemView.findViewById(R.id.tvPastGuessBox2);
            ivMatchedGuess1 = itemView.findViewById(R.id.ivMatchedGuess1);
            ivMatchedGuess2 = itemView.findViewById(R.id.ivMatchedGuess2);
        }

        public void bind(PastGuess pastGuess) {
            int secretNumberLength = pastGuess.getGuess().length;

            TextView[] pastGuessBoxes = new TextView[secretNumberLength];
            pastGuessBoxes[0] = tvPastGuessBox1;
            pastGuessBoxes[1] = tvPastGuessBox2;

            ImageView[] matchedGuessHints = new ImageView[secretNumberLength];
            matchedGuessHints[0] = ivMatchedGuess1;
            matchedGuessHints[1] = ivMatchedGuess2;
            int guessHintPosition = 0;

            Log.i("PastGuessAdapter", "Past guess to RV: " + Arrays.toString(pastGuess.getGuess()));

            // Bind past guess numbers to guess boxes
            for (int i = 0; i < secretNumberLength; ++i) {
                pastGuessBoxes[i].setText(pastGuess.getGuess()[i]);
            }

            int valueAndLocationMatches = Collections.frequency(pastGuess.getMatchedGuess(), 2);
            for (int i = 0; i < valueAndLocationMatches; ++i) {
                matchedGuessHints[guessHintPosition].setImageResource(R.drawable.ic_launcher_foreground);
                ++guessHintPosition;
            }

            int valueMatches = Collections.frequency(pastGuess.getMatchedGuess(), 1);
            for (int i = 0; i < valueMatches; ++i) {
                matchedGuessHints[guessHintPosition].setImageResource(R.drawable.ic_launcher_background);
                ++guessHintPosition;
            }
        }
    }
}
