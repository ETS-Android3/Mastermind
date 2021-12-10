package com.example.mastermind;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Create popup dialogue when game ends. Displays win dialogue if user won, and lose dialogue if
 * user lost.
 *
 * @author Kania Gandasetiawan
 * @version 1.0
 */
public class GameEndDialog extends AppCompatDialogFragment {

    private GameEndDialogListener listener;
    private Boolean gameWon;

    /**
     * Class constructor
     *
     * @param gameWon   true, if game won
     *                  false, otherwise
     */
    public GameEndDialog(Boolean gameWon) {
        this.gameWon = gameWon;
    }


    /**
     * Build game end dialog. If game won, display win dialogue. Otherwise, display lose dialogue.
     * Dialog is set to not cancellable. User must click "Escape Again" or "Try Again" to
     * close dialog.
     *
     * @param savedInstanceState    application instance state when dialog called
     * @return                      popup dialog created
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

    /**
     * Represents required implementation of GameEndDialogListener
     */
    public interface GameEndDialogListener {
        void onTryAgainClicked();
    }

    /**
     * Attach dialog to context. Throws ClassCastException if EndGameDialogListener is not
     * implemented.
     *
     * @param context   context to attach dialog to
     */
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
