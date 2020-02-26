package com.stormwitziers.pokedex.Filters;

import android.widget.Filter;

import com.stormwitziers.pokedex.Pokemon;
import com.stormwitziers.pokedex.PokemonList.PokemonAdapter;
import com.stormwitziers.pokedex.PokemonList.PokemonLoader;

import java.util.ArrayList;

public class PokemonSearchFilter extends Filter {

    private PokemonLoader mPokemonLoader;
    private PokemonAdapter mAdapter;

    public PokemonSearchFilter(PokemonAdapter adapter, PokemonLoader pokemonLoader){
        mAdapter = adapter;
        mPokemonLoader = pokemonLoader;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResult = new FilterResults();

        ArrayList<Pokemon> filteredList = new ArrayList<>();

        if(constraint == null || constraint.length() < 1) {
            filterResult.count = mPokemonLoader.PokemonList.size();
            filterResult.values = mPokemonLoader.PokemonList;
            return filterResult;
        }

        String constraintString = constraint.toString().toUpperCase();

        for (int i = 0; i < mPokemonLoader.PokemonList.size(); i++){

            if(mPokemonLoader.PokemonList.get(i).getName().toUpperCase().contains(constraintString)){
                filteredList.add(mPokemonLoader.PokemonList.get(i));
            }
        }

        filterResult.count = filteredList.size();
        filterResult.values = filteredList;

        return filterResult;

    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        ArrayList<Pokemon> pokemons = (ArrayList<Pokemon>) results.values;

        mAdapter.setPokemonArrayList(pokemons);
        mAdapter.notifyDataSetChanged();
    }
}
