package com.stormwitziers.pokedex.PokemonList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.stormwitziers.pokedex.FileWriters.FavoritePokemon;
import com.stormwitziers.pokedex.FileWriters.WebserviceFavoriteValues;
import com.stormwitziers.pokedex.FileWriters.Writer;
import com.stormwitziers.pokedex.Fragments.OverviewFragment;
import com.stormwitziers.pokedex.Pokemon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

public class PokemonLoader implements Serializable {
    public interface IPokemonLoaderHandler  {
        void PokemonLoaded(int pokemonPosition);

        void RefreshFavorites();

        void PokemonUpdated(int pokemonPosition);
    }

    private static final String API_URL_POKEMON_SPECIES = "https://pokeapi.co/api/v2/pokemon-species/";
    private static final String API_URL_POKEMON = "https://pokeapi.co/api/v2/pokemon/";
    private static final String API_URL_POKEMON_FORM = "https://pokeapi.co/api/v2/pokemon-form/";

    private transient RequestQueue mRequestQueue;
    private transient Context mContext;

    private transient IPokemonLoaderHandler mHandler;

    public ArrayList<Pokemon> PokemonList;
    public ArrayList<Pokemon> FavoriteList;
    public ArrayList<Pokemon> CustomPokemonList;

    public PokemonLoader(Context context) {
        mContext = context;

        mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.start();

        PokemonList = new ArrayList<Pokemon>();
        FavoriteList = new ArrayList<Pokemon>();
        CustomPokemonList = new ArrayList<Pokemon>();
    }

    public void setHandler(OverviewFragment mOverviewFragment) {
        mHandler = mOverviewFragment;
    }

    public boolean isNameUnique(final String name) {

        Optional<Pokemon> pokemon = PokemonList.stream().filter(p -> p.getName().equals(name.toLowerCase())).findFirst();
        return !pokemon.isPresent();
    }

    public void loadPokemons() {
        final ArrayList<WebserviceFavoriteValues> favoriteNames = FavoritePokemon.LoadAllFavorites(mContext);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL_POKEMON_SPECIES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int count = response.getInt("count");
                            for (int i = 1; i <= count; i++) {
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL_POKEMON_FORM + i, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    Pokemon pokemon = new Pokemon(PokemonList.size(), response.getString("name"));

                                                    // Add to favorites.
                                                    if (favoriteNames != null) {
                                                        for (WebserviceFavoriteValues fav :
                                                                favoriteNames) {
                                                            if (fav.PokemonName.equals(pokemon.getName())) {
                                                                FavoriteList.add(pokemon);
                                                                pokemon.isFavorite(true);
                                                                pokemon.setRating(fav.PokemonRating);
                                                                mHandler.RefreshFavorites();
                                                                break;
                                                            }
                                                        }
                                                    }

                                                    loadPokemonBitMap(pokemon, response.getJSONObject("sprites").getString("front_default"));

                                                    PokemonList.add(pokemon);
                                                    mHandler.PokemonLoaded(pokemon.getPosition());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                mRequestQueue.add(jsonObjectRequest);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }

    public ArrayList<Pokemon> loadCustomPokemons(){
       ArrayList<Pokemon> pokemons = Writer.LoadAllPokemons(this.mContext);
       for (Pokemon pokemon : pokemons)
       {
           boolean isUnique = true;
           for (Pokemon favPokemon : FavoriteList){
               isUnique = !favPokemon.getName().equals(pokemon.getName());
               if(!isUnique)break;
           }
            if(isUnique && pokemon.isFavorite()) { FavoriteList.add(pokemon); }

       }
       return CustomPokemonList = pokemons;
    }

    private void loadPokemonBitMap(final Pokemon pokemon, String url) {
        if (!url.equals("null")) {
            ImageRequest imageRequest = new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            int position = pokemon.getPosition();
                            Drawable d = new BitmapDrawable(mContext.getResources(), response);
                            pokemon.setPicture(d);


                            mHandler.PokemonUpdated(position);
                        }
                    },
                    0,
                    0,
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.RGB_565,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            ) {
                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }
            };

            mRequestQueue.add(imageRequest);
        }
    }
}
