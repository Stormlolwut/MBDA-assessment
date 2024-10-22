package com.stormwitziers.pokedex.PokemonList;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.stormwitziers.pokedex.Filters.PokemonSearchFilter;
import com.stormwitziers.pokedex.Pokemon;
import com.stormwitziers.pokedex.R;

import java.util.ArrayList;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonViewHolder> implements Filterable {

    private ArrayList<Pokemon> mData;
    private PokemonLoader mPokemonLoader;
    private OnPokemonListener mOnPokemonListener;

    private PokemonSearchFilter mPokemonSearchFilter;

    public PokemonAdapter(PokemonLoader pokemonLoader, OnPokemonListener onPokemonListener) {
        this.mPokemonLoader = pokemonLoader;
        this.mData = pokemonLoader.PokemonList;
        this.mOnPokemonListener = onPokemonListener;

        this.mPokemonSearchFilter = new PokemonSearchFilter(this, mPokemonLoader);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setPokemonArrayList(ArrayList<Pokemon> pokemons) {
        mData = pokemons;
        notifyDataSetChanged();
    }

    public Pokemon getItemAtPosition(int position) {
        if (position < 0 || position > mData.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return mData.get(position);
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout pokemonItem = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_item, parent, false);
        return new PokemonViewHolder(pokemonItem, mOnPokemonListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = mData.get(position);
        ((ImageView) holder.pokemonItem.getChildAt(0)).setImageDrawable(pokemon.getPicture());
        ((TextView) holder.pokemonItem.getChildAt(1)).setText(pokemon.getName());
    }

    public void addPokemons(ArrayList<Pokemon> pokemons){

        addNewPokemons(pokemons);
        notifyDataSetChanged();
    }

    private void addNewPokemons(ArrayList<Pokemon> newList){

        for (int i =0; i< newList.size(); i++){
            boolean constains = false;

            for (int j =0; j< mData.size(); j++){
                if(mData.get(j).equals(newList.get(i))){
                    constains = true;
                }
            }

            if(!constains) { mData.add(newList.get(i)); }
        }
    }

    @Override
    public Filter getFilter() {
        return mPokemonSearchFilter;
    }

    public interface OnPokemonListener {
        void onPokemonClick(int position);
    }
}
