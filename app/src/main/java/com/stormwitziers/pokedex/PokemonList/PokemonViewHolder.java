package com.stormwitziers.pokedex.PokemonList;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

class PokemonViewHolder extends ViewHolder implements View.OnClickListener {

    ConstraintLayout pokemonItem;
    PokemonAdapter.OnPokemonListener onPokemonListener;

    PokemonViewHolder(ConstraintLayout pokemonItem, PokemonAdapter.OnPokemonListener onPokemonListener) {
        super(pokemonItem);
        this.pokemonItem = pokemonItem;

        this.onPokemonListener = onPokemonListener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onPokemonListener.onPokemonClick(getAdapterPosition());
    }
}
