package com.stormwitziers.pokedex.Filters;

import android.widget.Filter;

import com.stormwitziers.pokedex.Pokemon;
import com.stormwitziers.pokedex.PokemonList.PokemonAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class PokemonSearchFilter extends Filter {

    private ArrayList<Pokemon> mDataFullList;
    private PokemonAdapter mAdapter;

    public PokemonSearchFilter(PokemonAdapter adapter, Pokemon[] fullArray){
        mAdapter = adapter;
        mDataFullList = new ArrayList<>(Arrays.asList(fullArray));
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResult = new FilterResults();

        ArrayList<Pokemon> filteredList = new ArrayList<>();

        if(constraint == null || constraint.length() < 1) {
            filterResult.count = mDataFullList.size();
            filterResult.values = mDataFullList;
            return filterResult;
        }

        String constraintString = constraint.toString().toUpperCase();

        for (int i = 0; i < mDataFullList.size(); i++){

            if(mDataFullList.get(i).getName().toUpperCase().contains(constraintString)){
                filteredList.add(mDataFullList.get(i));
            }
        }

        filterResult.count = filteredList.size();
        filterResult.values = filteredList;

        return filterResult;

    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        Pokemon[] pokemons = (Pokemon[]) ((ArrayList) results.values).toArray(new Pokemon[0]);

        mAdapter.setPokemonArray(pokemons);
        mAdapter.notifyDataSetChanged();
    }
}
