package com.stormwitziers.pokedex;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.Serializable;

public class Pokemon implements Serializable {

    private int position;
    private String name;
    private transient Drawable picture;
    private String type;
    private float rating;

    private boolean isFavorite;
    private boolean isCustom;

    public int getPosition() { return position; }

    public String getType() { return type; }

    public String getName(){ return name; }

    public Drawable getPicture() { return picture; }

    public void setPicture(Drawable drawable) { picture = drawable; }

    public void isFavorite(boolean value) { isFavorite = value; }

    public boolean isFavorite() { return isFavorite; }

    public boolean isCustom() { return isCustom; }

    public void setRating(float rating) { this.rating = rating; }

    public float getRating(){ return rating; }

    public Pokemon(String name, Drawable picture, String type, boolean isCustom){
        this.name = name;
        this.picture = picture;
        this.type = type;
        this.isCustom = isCustom;
    }
    public Pokemon(int position, String name)
    {
        this.name = name;
        this.position = position;
    }

    public boolean equals(Pokemon pokemon){
        return this.name.equals(pokemon.getName());
    }
}
