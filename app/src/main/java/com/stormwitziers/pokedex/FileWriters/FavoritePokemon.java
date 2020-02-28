package com.stormwitziers.pokedex.FileWriters;

import android.content.Context;

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
import java.lang.reflect.Array;
import java.util.ArrayList;

public class FavoritePokemon  {
    private static final String FAVORITE = "Favorite";
    private static final String FILE_NAME = "Favorites";
    private static final String OBJECT_NAME = "favorites";

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

            // WRITE
            JSONArray jsonArray = new JSONArray(pokemonJson.getString(OBJECT_NAME));
            jsonArray.put(mPokemon.getName());
            pokemonJson.put(OBJECT_NAME, jsonArray);


            AddFavoriteArray(favoriteFile, pokemonJson);
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
            JSONArray jsonArray = new JSONArray(pokemonJson.getString(OBJECT_NAME));
            for (int i = 0; i < jsonArray.length(); i++)
            {
                String name = jsonArray.getString(i);
                if(name == mPokemon.getName())
                {
                    jsonArray.remove(i);
                    break;
                }
            }

            pokemonJson.put(OBJECT_NAME, jsonArray);
            AddFavoriteArray(favoriteFile, pokemonJson);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> LoadAllFavorites(Context context)
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

            ArrayList<String> strings = new ArrayList<String>();
            JSONArray array = new JSONArray(pokemonJson.getString(OBJECT_NAME));
            for (int i = 0; i < array.length(); i++)
            {
                strings.add((String)array.get(i));
            }

            return strings;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void AddFavoriteArray(File file, JSONObject jsonObject) throws IOException {
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
            jsonObject.put(OBJECT_NAME, new ArrayList<String>());
            AddFavoriteArray(file, jsonObject);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
