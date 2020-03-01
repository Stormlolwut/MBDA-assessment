package com.stormwitziers.pokedex.Fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stormwitziers.pokedex.MainActivity;
import com.stormwitziers.pokedex.Pokemon;
import com.stormwitziers.pokedex.PokemonList.PokemonAdapter;
import com.stormwitziers.pokedex.PokemonList.PokemonLoader;
import com.stormwitziers.pokedex.R;


public class OverviewFragment extends Fragment implements PokemonAdapter.OnPokemonListener, PokemonLoader.IPokemonLoaderHandler {
    private PokemonLoader mPokemonLoader;
    private RecyclerView mPokemonRecycleView;
    private PokemonAdapter mAdapter;

    private OnPokemonSelected mOnPokemonSelected;
    private MainActivity mMainActivity;

    public void initialize(MainActivity mainActivity, PokemonLoader pokemonLoader) {
        this.mMainActivity = mainActivity;
        this.mPokemonLoader = pokemonLoader;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_settings:
                mMainActivity.OpenSettings();
                return true;
            default:
                return false;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOnPokemonSelected = (OnPokemonSelected) getActivity();
        View view = inflater.inflate(R.layout.overview_fragment, container, false);
        addPokemonsToRecycleView(view);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        return view;
    }

    private void addPokemonsToRecycleView(View view) {
        mPokemonLoader = ((MainActivity) getActivity()).getPokemonLoader();
        mAdapter = new PokemonAdapter(mPokemonLoader, this);


        mPokemonRecycleView = view.findViewById(R.id.pokemon_recycle_view);
//        mPokemonRecycleView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        mPokemonRecycleView.setLayoutManager(layoutManager);

        mPokemonRecycleView.setAdapter(mAdapter);
    }

    public void onResume() {

        super.onResume();
        //TODO alleen pokemons toevoegen die niet in de lijst nog zitten
        mAdapter.addPokemons(mPokemonLoader.loadCustomPokemons());

        mMainActivity.initializeSpinner();
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
    public void RefreshFavorites() {
        mMainActivity.initializeSpinner();
    }

    @Override
    public void PokemonUpdated(int pokemonPosition) {
        mAdapter.notifyItemChanged(pokemonPosition);
    }

    public interface OnPokemonSelected {
        void onItemSelected(Pokemon pokemon);
    }
}

