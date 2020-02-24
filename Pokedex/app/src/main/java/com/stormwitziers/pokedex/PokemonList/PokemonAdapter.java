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

public class PokemonAdapter extends RecyclerView.Adapter<PokemonViewHolder> implements Filterable {

    private Pokemon[] mData;
    private Pokemon[] mFullData;
    private OnPokemonListener mOnPokemonListener;

    public PokemonAdapter(Pokemon[] pokemons, OnPokemonListener onPokemonListener){
        this.mData = pokemons;
        this.mFullData = mData;
        this.mOnPokemonListener = onPokemonListener;
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    public void setPokemonArray(Pokemon[] pokemons){
        mData = pokemons;
        notifyDataSetChanged();
    }

    public Pokemon getItemAtPosition(int position){
        if(position < 0 || position > mData.length) { throw new ArrayIndexOutOfBoundsException(); }

        return mData[position];
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout pokemonItem = (ConstraintLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_item, parent, false);
        return new PokemonViewHolder(pokemonItem, mOnPokemonListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        ((ImageView) holder.pokemonItem.getChildAt(0)).setImageDrawable(mData[position].getPicture());
        ((TextView) holder.pokemonItem.getChildAt(1)).setText(mData[position].getName());
    }

    @Override
    public Filter getFilter() {
        return new PokemonSearchFilter(this, mFullData);
    }


    public interface OnPokemonListener{
        void onPokemonClick(int position);
    }
}
