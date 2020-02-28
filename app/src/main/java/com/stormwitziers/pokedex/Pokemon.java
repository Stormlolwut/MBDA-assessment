package com.stormwitziers.pokedex;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.Serializable;

public class Pokemon implements Serializable {

    private int position;
    private String name;
    private Drawable picture;
    private String type;
    private float rating;

    public int getPosition() { return position; }

    public String getType() { return type; }

    public String getName(){ return name; }

    public Drawable getPicture() { return picture; }

    public void setPicture(Drawable drawable) { picture = drawable; }

    public void setRating(float rating) { this.rating = rating; }

    public float getRating(){ return rating; }

    public Pokemon(String name, Drawable picture, String type){
        this.name = name;
        this.picture = picture;
        this.type = type;
    }
    public Pokemon(int position, String name)
    {
        this.name = name;
        this.position = position;
    }
}
