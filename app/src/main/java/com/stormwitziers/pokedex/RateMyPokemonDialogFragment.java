package com.stormwitziers.pokedex;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class RateMyPokemonDialogFragment extends DialogFragment {

    public OnPokemonRatingDialogListener listener;

    public void onAttach(Context context){
        super.onAttach(context);

        try{
            listener = (OnPokemonRatingDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() + "must implement OnPokemonRatingDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflater.inflate(R.layout.dialog_pokemon_rating, null)).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RatingBar ratingbar = getDialog().findViewById(R.id.dialog_rating_bar);
                listener.onPositiveButtonClick(ratingbar.getRating());
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    public interface OnPokemonRatingDialogListener{
        void onPositiveButtonClick(float rating);
    }
}
