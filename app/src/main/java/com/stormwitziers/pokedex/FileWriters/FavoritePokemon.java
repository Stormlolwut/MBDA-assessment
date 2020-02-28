package com.stormwitziers.pokedex.FileWriters;

import android.content.Context;

import com.stormwitziers.pokedex.Pokemon;

public class FavoritePokemon extends Writer {
    public FavoritePokemon(Context context, Pokemon pokemon) {
        super(context, pokemon, FAVORITE);
    }
}
