package com.stormwitziers.pokedex.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stormwitziers.pokedex.Pokemon;
import com.stormwitziers.pokedex.PokemonList.PokemonAdapter;
import com.stormwitziers.pokedex.R;



public class OverviewFragment extends Fragment implements PokemonAdapter.OnPokemonListener {

    private RecyclerView mPokemonRecycleView;
    private PokemonAdapter mAdapter;

    private OnPokemonSelected mOnPokemonSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOnPokemonSelected = (OnPokemonSelected) getActivity();

        View view = inflater.inflate(R.layout.overview_fragment, container, false);
        addPokemonsToRecycleView(view);
        return view;
    }

    private void addPokemonsToRecycleView(View view){

        Pokemon[] pokemonArray = {
                new Pokemon("Bulbasaur", getResources().getDrawable(R.drawable.pokemon_bulbasaur)),
                new Pokemon("Dragonite", getResources().getDrawable(R.drawable.pokemon_dragonite)),
                new Pokemon("Pikachu", getResources().getDrawable(R.drawable.pokemon_pikachu)),
                new Pokemon("Sonja", getResources().getDrawable(R.drawable.pokemon_pikachu))
        };

        mAdapter = new PokemonAdapter(pokemonArray, this);


        mPokemonRecycleView = view.findViewById(R.id.pokemon_recycle_view);
        mPokemonRecycleView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mPokemonRecycleView.setLayoutManager(layoutManager);

        mPokemonRecycleView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        SearchView item = (SearchView) menu.findItem(R.id.toolbar_search).getActionView();
        item.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Filter filter = mAdapter.getFilter();
                filter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Filter filter = mAdapter.getFilter();
                filter.filter(newText);
                return false;
            }
        });

    }

    @Override
    public void onPokemonClick(int position) {
        mOnPokemonSelected.onItemSelected(mAdapter.getItemAtPosition(position));
    }

    public interface OnPokemonSelected{
        void onItemSelected(Pokemon pokemon);
    }
}

