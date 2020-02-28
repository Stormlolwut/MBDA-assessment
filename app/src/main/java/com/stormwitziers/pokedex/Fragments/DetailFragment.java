package com.stormwitziers.pokedex.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.stormwitziers.pokedex.FileWriters.FavoritePokemon;
import com.stormwitziers.pokedex.MainActivity;
import com.stormwitziers.pokedex.Pokemon;
import com.stormwitziers.pokedex.PokemonList.PokemonLoader;
import com.stormwitziers.pokedex.R;
import com.stormwitziers.pokedex.RateMyPokemonDialogFragment;

import java.util.ArrayList;
import java.util.Objects;

public class DetailFragment extends Fragment  {
    private MainActivity mMainActivity;

    private Pokemon mCurrentPokemon;

    private MenuItem favoriteItem = null;
    private RatingBar ratingBar;

    public DetailFragment(MainActivity mainActivity, Pokemon pokemon){
        this.mCurrentPokemon = pokemon;
        this.mMainActivity = mainActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStop() {
        super.onStop();

        favoriteItem.setVisible(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View currentView = inflater.inflate(R.layout.detail_fragment, container, false);

        if(mCurrentPokemon != null){
            setPokemon(currentView, mCurrentPokemon);
        }


        final SwipeRefreshLayout layout = currentView.findViewById(R.id.detail_refresh_layout);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ImageView pokeImage = currentView.findViewById(R.id.details_picture);

                pokeImage.setImageDrawable(mCurrentPokemon.getPicture());

                layout.setRefreshing(false);
            }
        });

        View view = ((AppCompatActivity) getActivity()).findViewById(R.id.toolbar_favorite);
        return currentView;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_favorite:
                if (mCurrentPokemon == null) return false;
                if(mCurrentPokemon.isFavorite())
                {
                    setFavoriteCurrentPokemon(false);
                }
                else
                {
                    ShowDialog();
                }
                return true;
            default:
                return false;
        }

    }

    public void updatePokemonRating(float rating){
        mCurrentPokemon.setRating(rating);
        RatingBar ratingBar = Objects.requireNonNull(getActivity()).findViewById(R.id.details_ratingbar);
        ratingBar.setRating(rating);
    }

    private void setPokemon(View currentView, Pokemon pokemon){

        TextView pokeName = currentView.findViewById(R.id.details_name);
        ImageView pokeImage = currentView.findViewById(R.id.details_picture);
        ratingBar = currentView.findViewById(R.id.details_ratingbar);

        pokeName.setText(pokemon.getName());
        pokeImage.setImageDrawable(pokemon.getPicture());

        ratingBar.setRating(pokemon.getRating());
    }

    public void setFavoriteCurrentPokemon(boolean setFavorite) {
        FavoritePokemon favoritePokemon = new FavoritePokemon(getContext(), mCurrentPokemon);
        ArrayList<Pokemon> favoriteList = mMainActivity.getPokemonLoader().FavoriteList;

        if(setFavorite)
        {
            boolean contains = false;
            for (Pokemon pokemon : favoriteList){
                if(pokemon.getName().equals(mCurrentPokemon.getName())){
                    contains = true;
                    break;
                }
            }

            if(!contains){
                favoriteList.add(mCurrentPokemon);
                mMainActivity.initializeSpinner();


                favoritePokemon.Save();
                mCurrentPokemon.isFavorite(true);
                SetFavIconOn(true);
            }
        }
        else
        {
            favoriteList.remove(mCurrentPokemon);
            mMainActivity.initializeSpinner();

            favoritePokemon.Delete();
            mCurrentPokemon.isFavorite(false);
            mCurrentPokemon.setRating(0);
            ratingBar.setRating(0);
            SetFavIconOn(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        favoriteItem = menu.findItem(R.id.toolbar_favorite);
        favoriteItem.setVisible(true);

        SetFavIconOn(mCurrentPokemon.isFavorite());
    }

    public void ShowDialog()
    {
        RateMyPokemonDialogFragment dialogFragment = new RateMyPokemonDialogFragment(this);
        dialogFragment.show(getFragmentManager(), "rating_dialog");
    }

    private void SetFavIconOn(boolean value)
    {
        if(value)
        {
            favoriteItem.setIcon(android.R.drawable.star_big_on);
        }
        else
        {
            favoriteItem.setIcon(android.R.drawable.star_big_off);
        }
    }
}
