package com.example.mastermind;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class GameEndDialog extends AppCompatDialogFragment {

    private GameEndDialogListener listener;

    private Boolean gameWon;

    public GameEndDialog(Boolean gameWon) {
        this.gameWon = gameWon;
    }

    // Build dialog when game ends
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set win message if user won or lose message if use lost
        if (gameWon) {
            builder.setTitle(R.string.win_dialogue)
                    .setMessage(R.string.win_dialogue_detail)
                    .setPositiveButton("Escape Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            listener.onTryAgainClicked();
                        }
                    });
        }
        else {
            builder.setTitle(R.string.lose_dialogue)
                    .setMessage(R.string.lose_dialogue_detail)
                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            listener.onTryAgainClicked();
                        }
                    });
        }
        builder.setCancelable(false);

        return builder.create();
    }

    public interface GameEndDialogListener {
        void onTryAgainClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (GameEndDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement EndGameDialogListener");
        }
    }
}
