package com.stormwitziers.pokedex;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Pokemon implements Serializable {

    private String name;
    private Drawable picture;
    private float rating;

    public String getName(){
        return name;
    }

    public Drawable getPicture(){
        return picture;
    }

    public void setRating(float rating){
        this.rating = rating;
    }

    public float getRating(){
        return rating;
    }

    public Pokemon(String name, Drawable picture){
        this.name = name;
        this.picture = picture;
    }
}
