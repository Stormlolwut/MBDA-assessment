package com.stormwitziers.pokedex.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stormwitziers.pokedex.Pokemon;
import com.stormwitziers.pokedex.R;
import com.stormwitziers.pokedex.RateMyPokemonDialogFragment;

import java.util.ArrayList;
import java.util.Objects;

public class DetailFragment extends Fragment  {

    private Spinner mSpinner;
    private boolean mSpinnerFirstCall;
    private ArrayAdapter<String> mSpinnerAdapter;

    private Pokemon mCurrentPokemon;
    private ArrayList<Pokemon> mFavoriteList;

    private OverviewFragment.OnPokemonSelected mOnPokemonSelected;



    public DetailFragment(Pokemon pokemon, ArrayList<Pokemon> favoriteList){
        mFavoriteList = favoriteList;
        this.mCurrentPokemon = pokemon;
        mSpinnerFirstCall = true;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mOnPokemonSelected = (OverviewFragment.OnPokemonSelected) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.detail_fragment, container, false);

        if(mCurrentPokemon != null){
            setPokemon(currentView, mCurrentPokemon);
        }

        return currentView;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_favorite:
                return favoriteCurrentPokemon();
            default:
                return false;
        }

    }

    public void updatePokemonRating(float rating){
        mCurrentPokemon.setRating(rating);
        RatingBar ratingBar = Objects.requireNonNull(getActivity()).findViewById(R.id.details_ratingbar);
        ratingBar.setRating(rating);
    }

    private void setPokemon(View currentView, Pokemon pokemon){

        TextView pokeName = currentView.findViewById(R.id.details_name);
        ImageView pokeImage = currentView.findViewById(R.id.details_picture);
        RatingBar ratingBar = currentView.findViewById(R.id.details_ratingbar);

        pokeName.setText(pokemon.getName());
        pokeImage.setImageDrawable(pokemon.getPicture());

        ratingBar.setRating(pokemon.getRating());
    }

    private boolean favoriteCurrentPokemon() {
        if (mCurrentPokemon == null) return false;



        boolean contains = false;
        for (Pokemon pokemon : mFavoriteList){
            if(pokemon.getName().equals(mCurrentPokemon.getName())){
                contains = true;
                break;
            }
        }

        if(!contains){
            initializeSpinner();
            mFavoriteList.add(mCurrentPokemon);
            mSpinnerAdapter.add(mCurrentPokemon.getName());
        }

        RateMyPokemonDialogFragment dialogFragment = new RateMyPokemonDialogFragment();
        dialogFragment.show(getFragmentManager(), "rating_dialog");

        return true;
    }

    private void initializeSpinner(){
        ArrayList<String> pokemonNames = new ArrayList<>();
        for (Pokemon pokemon: mFavoriteList) {
            pokemonNames.add(pokemon.getName());
        }

        mSpinner = this.getActivity().findViewById(R.id.toolbar_favorite_spinner);
        mSpinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, pokemonNames);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setSelection(0, false);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!mSpinnerFirstCall){
                    Pokemon pokemon = mFavoriteList.get(position);
                    mOnPokemonSelected.onItemSelected(pokemon);
                }

                mSpinnerFirstCall = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
