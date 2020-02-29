package com.stormwitziers.pokedex.FileWriters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.stormwitziers.pokedex.Pokemon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Writer {
    private static final String FOLDER_NAME = "Pokemon";

    private final com.stormwitziers.pokedex.Pokemon mPokemon;
    private final File parent;

    public Writer(Context context, com.stormwitziers.pokedex.Pokemon pokemon) {
        this.mPokemon = pokemon;

        parent = new File(context.getFilesDir(), FOLDER_NAME);
        if(!parent.exists())
        {
            parent.mkdir();
        }
    }

    public void save() {
        File fileBitmap = new File(parent, mPokemon.getName() + ".BMP");
        File filePokemon = new File(parent, mPokemon.getName() + ".json");

        BitmapDrawable bitmapDrawable = (BitmapDrawable) mPokemon.getPicture();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        JSONObject obj = new JSONObject();


        if (!fileBitmap.exists()) {
            try {
                // BITMAP
                FileOutputStream fileOutputStreamBMP = new FileOutputStream(fileBitmap);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStreamBMP);
                fileOutputStreamBMP.flush();
                fileOutputStreamBMP.close();

                // POKEMON
                obj.put("name", mPokemon.getName());
                obj.put("rating", mPokemon.getRating());
                obj.put("picture", fileBitmap.getAbsolutePath());
                obj.put("type", mPokemon.getType());
                obj.put("favorite", mPokemon.isFavorite());
                obj.put("custom", mPokemon.isCustom());
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePokemon));
                bufferedWriter.write(obj.toString());
                bufferedWriter.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean delete() {
        boolean deleteSucces = false;

        File fileBitmap = new File(parent, mPokemon.getName() + ".BMP");
        File filePokemon = new File(parent, mPokemon.getName() + ".json");

        fileBitmap.delete();
        deleteSucces = filePokemon.delete();
        return deleteSucces;
    }

    public void update(Pokemon previousPokemon){
        File fileBitmap = new File(parent, previousPokemon.getName() + ".BMP");
        File filePokemon = new File(parent, previousPokemon.getName() + ".json");

        fileBitmap.delete();
        filePokemon.delete();

        save();
    }

    public static ArrayList<com.stormwitziers.pokedex.Pokemon> LoadAllPokemons(Context context) {
        ArrayList<com.stormwitziers.pokedex.Pokemon> pokemons = new ArrayList<>();
        File parent = new File(context.getFilesDir(), FOLDER_NAME);
        File[] files = parent.listFiles();

        if(files == null) files = new File[0];

        for(File f: files)
        {
            String name = f.getName();
            String extension = name.substring(name.lastIndexOf("."));

            if(!extension.equals(".json"))
            {
                continue;
            }

            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(f.getAbsolutePath()));
                String content = fileReader.readLine();

                JSONObject pokemonJson = new JSONObject(content);

                // Pokemon properties.
                Drawable drawable = Drawable.createFromPath(pokemonJson.getString("picture"));
                com.stormwitziers.pokedex.Pokemon pokemon = new com.stormwitziers.pokedex.Pokemon(pokemonJson.getString("name"), drawable, pokemonJson.getString("type"), pokemonJson.getBoolean("custom"));
                pokemon.setRating((float)pokemonJson.getDouble("rating"));
                pokemon.isFavorite(pokemonJson.getBoolean("favorite"));

                pokemons.add(pokemon);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        return pokemons;
    }

    public BitmapDrawable getPokemonBitmap(Resources resources){
        File fileBitmap = new File(parent, mPokemon.getName() + ".BMP");
        Bitmap bitmap = BitmapFactory.decodeFile(fileBitmap.getAbsolutePath());
        return new BitmapDrawable(resources, bitmap);
    }
}
