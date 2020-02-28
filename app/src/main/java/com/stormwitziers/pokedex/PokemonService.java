package com.stormwitziers.pokedex;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PokemonService extends Service {

    private final int POKEMON_NOTIFICATION = 1;
    public static String POKEMON_NOTIFICATION_CHANNEL = "ThePokemonChannel";

    private int mFavorite;
    Pokemon[] mPokemonArray;

    public PokemonService() {
    }


    public void onCreate(){

        mPokemonArray = new Pokemon[] {
                new Pokemon("Bulbasaur", getResources().getDrawable(R.drawable.pokemon_bulbasaur), "wow"),
                new Pokemon("Dragonite", getResources().getDrawable(R.drawable.pokemon_dragonite), "wow"),
                new Pokemon("Pikachu", getResources().getDrawable(R.drawable.pokemon_pikachu), "wow"),
                new Pokemon("Sonja", getResources().getDrawable(R.drawable.pokemon_pikachu), "wow"),
                new Pokemon("André", getResources().getDrawable(R.drawable.pokemon_pikachu), "wow"),
                new Pokemon("Huseyin", getResources().getDrawable(R.drawable.pokemon_pikachu), "wow"),
                new Pokemon("Hakan", getResources().getDrawable(R.drawable.pokemon_peach), "wow"),
        };

        startForeground(POKEMON_NOTIFICATION, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId){


        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                //TODO: add the pokemon from the list instead
                mFavorite = (mFavorite + 1 + new Random().nextInt(2)) % mPokemonArray.length;
                sendNotification();
                int FIVE_SECONDS = 5000;
                handler.postDelayed(this, FIVE_SECONDS);
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void sendNotification(){
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(POKEMON_NOTIFICATION, getNotification());
        }
    }

    public Notification getNotification(){

        Bitmap icon = ((BitmapDrawable) mPokemonArray[mFavorite].getPicture()).getBitmap();

        if(icon == null) icon = BitmapFactory.decodeResource(getResources(), R.drawable.pokemon_pikachu);

        Icon smallcon = Icon.createWithBitmap(icon);

        return new Notification.Builder(this.getApplicationContext(), POKEMON_NOTIFICATION_CHANNEL)
                .setContentTitle("We just added " + mPokemonArray[mFavorite].getName() + " while you were gone!")
                .setSmallIcon(smallcon)
                .setLargeIcon(icon)
                .setContentText("We need you to to rate it so come back soon!")
                .setAutoCancel(true)
                .build();
    }
}
