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
import com.stormwitziers.pokedex.Pokemon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PokemonLoader {
    public interface IPokemonLoaderHandler {
        void PokemonLoaded(int pokemonPosition);
        void PokemonUpdated(int pokemonPosition);
    }

    private static final String API_URL_POKEMON = "https://pokeapi.co/api/v2/pokemon/";
    private static final String API_URL_POKEMON_FORM = "https://pokeapi.co/api/v2/pokemon-form/";

    private RequestQueue mRequestQueue;
    private Context mContext;

    private IPokemonLoaderHandler mHandler;

    public ArrayList<Pokemon> PokemonMap;

    public PokemonLoader(Context context, IPokemonLoaderHandler handler) {
        mHandler = handler;
        mContext = context;

        mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.start();

        PokemonMap = new ArrayList<Pokemon>();
    }


    public Pokemon[] loadPokemons(int offset, int amount) {

        // TODO
        return null;
    }

    public Pokemon getPokemon(int pokemonId) {
        return PokemonMap.get(pokemonId);
    }

    public void loadPokemon(final int id) {
        // Pokemon information
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, API_URL_POKEMON_FORM + id, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            Pokemon pokemon = new Pokemon(PokemonMap.size(), response.getString("name"));

                            loadPokemonBitMap(pokemon, response.getJSONObject("sprites").getString("front_default"));

                            PokemonMap.add(pokemon);
                            mHandler.PokemonLoaded(pokemon.getPosition());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }

    private void loadPokemonBitMap(final Pokemon pokemon, String url) {
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
        );

        mRequestQueue.add(imageRequest);
    }
}
