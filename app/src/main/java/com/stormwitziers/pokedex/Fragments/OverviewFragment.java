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
import com.stormwitziers.pokedex.PokemonList.PokemonLoader;
import com.stormwitziers.pokedex.R;


public class OverviewFragment extends Fragment implements PokemonAdapter.OnPokemonListener, PokemonLoader.IPokemonLoaderHandler {
    private PokemonLoader mPokemonLoader;
    private RecyclerView mPokemonRecycleView;
    private PokemonAdapter mAdapter;

    private OnPokemonSelected mOnPokemonSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mPokemonLoader = PokemonLoader.getInstance();
        mPokemonLoader.loadPokemons();
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
        mAdapter = new PokemonAdapter(mPokemonLoader, this);


        mPokemonRecycleView = view.findViewById(R.id.pokemon_recycle_view);
        //mPokemonRecycleView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mPokemonRecycleView.setLayoutManager(layoutManager);

        mPokemonRecycleView.setAdapter(mAdapter);
        mAdapter.addPokemons(mPokemonLoader.loadCustomPokemons());
    }

    public void onResume() {

        super.onResume();
        mAdapter.addPokemons(mPokemonLoader.loadCustomPokemons());
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

    @Override
    public void PokemonLoaded(int pokemonPosition) {
        mAdapter.notifyItemInserted(pokemonPosition);
    }

    @Override
    public void PokemonUpdated(int pokemonPosition) {
        mAdapter.notifyItemChanged(pokemonPosition);
    }

    public interface OnPokemonSelected{
        void onItemSelected(Pokemon pokemon);
    }
}

