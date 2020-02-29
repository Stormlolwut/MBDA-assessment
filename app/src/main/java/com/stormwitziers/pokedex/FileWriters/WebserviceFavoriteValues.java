package com.stormwitziers.pokedex.FileWriters;

public class WebserviceFavoriteValues {
    public String PokemonName;
    public float PokemonRating;

    public WebserviceFavoriteValues(String name, float rating)
    {
        this.PokemonName = name;
        this.PokemonRating = rating;
    }
}
