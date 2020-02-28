package com.stormwitziers.pokedex.FileWriters;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.stormwitziers.pokedex.Pokemon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FavoritePokemon  {


    private static final String FAVORITE = "Favorite";
    private static final String FILE_NAME = "Favorites";
    private static final String NAME = "name";
    private static final String RATING = "rating";

    private final Pokemon mPokemon;
    private final File mParent;

    public FavoritePokemon(Context context, Pokemon pokemon) {
        this.mPokemon = pokemon;

        this.mParent = new File(context.getFilesDir(), FAVORITE);
        if(!mParent.exists())
        {
            mParent.mkdir();
        }
    }

    public void Save()
    {
        File favoriteFile = new File(mParent, FILE_NAME + ".json");

        if(!favoriteFile.exists())
        {
            CreateEmptyFavoriteFile(favoriteFile);
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(favoriteFile.getAbsolutePath()));

            // READ
            String content = fileReader.readLine();
            JSONObject pokemonJson = new JSONObject(content);

            // JSON
            JSONObject obj = new JSONObject();
            obj.put(NAME, mPokemon.getName());
            obj.put(RATING, mPokemon.getRating());


            pokemonJson.put(mPokemon.getName(), obj);
            SaveJsonToFile(favoriteFile, pokemonJson);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public void Delete()
    {
        File favoriteFile = new File(mParent, FILE_NAME + ".json");
        if(!favoriteFile.exists())
        {
            return;
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(favoriteFile.getAbsolutePath()));

            // READ
            String content = fileReader.readLine();
            JSONObject pokemonJson = new JSONObject(content);

            // WRITE
            pokemonJson.remove(mPokemon.getName());

            SaveJsonToFile(favoriteFile, pokemonJson);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<WebserviceFavoriteValues> LoadAllFavorites(Context context)
    {
        File parent = new File(context.getFilesDir(), FAVORITE);
        if(!parent.exists())
        {
            parent.mkdir();
        }

        File favoriteFile = new File(parent, FILE_NAME + ".json");

        if(!favoriteFile.exists())
        {
            CreateEmptyFavoriteFile(favoriteFile);
        }

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(favoriteFile.getAbsolutePath()));

            // READ
            String content = fileReader.readLine();
            JSONObject pokemonJson = new JSONObject(content);

            ArrayList<WebserviceFavoriteValues> values = new ArrayList<>();
            for (Iterator<String> it = pokemonJson.keys(); it.hasNext(); ) {
                String key = it.next();

                JSONObject obj = pokemonJson.getJSONObject(key);
                String name = obj.getString(NAME);
                float rating = (float)obj.getDouble(RATING);

                values.add(new WebserviceFavoriteValues(name, rating));
            }

            return values;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void SaveJsonToFile(File file, JSONObject jsonObject) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

        bufferedWriter.write(jsonObject.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    private static void CreateEmptyFavoriteFile(File file)
    {
        try {
            file.createNewFile();
            JSONObject jsonObject = new JSONObject();
            SaveJsonToFile(file, jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
