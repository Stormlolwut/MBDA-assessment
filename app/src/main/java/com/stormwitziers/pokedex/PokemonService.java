package com.stormwitziers.pokedex;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import com.stormwitziers.pokedex.FileWriters.Writer;
import com.stormwitziers.pokedex.PokemonList.PokemonLoader;

import java.util.ArrayList;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PokemonService extends Service {

    private final int POKEMON_NOTIFICATION = 1;

    public static String POKEMON_NOTIFICATION_CHANNEL = "ThePokemonChannel";
    public static int POKEMON_PENDING_INTENT_RESULT = 3;

    private int mRandomIndex;

    PokemonLoader mPokemonLoader;
    ArrayList<Pokemon> mFavoriteList;

    private int mNotificationDelay;

    public void onCreate(){
        mNotificationDelay = 5000;
        mFavoriteList = new ArrayList<Pokemon>();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId){

        final Handler handler = new Handler();

        mPokemonLoader = (PokemonLoader) intent.getExtras().get("favorites");
        startForeground(POKEMON_NOTIFICATION, getNotification());

        handler.post(new Runnable() {
            @Override
            public void run() {
                mFavoriteList = mPokemonLoader.FavoriteList;
                handler.postDelayed(this, mNotificationDelay);

                if(mFavoriteList.size() < 1) return;

                //TODO: add the pokemon from the list instead
                mRandomIndex = (mRandomIndex + 1 + new Random().nextInt(2)) % mFavoriteList.size();
                sendNotification();
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
        if(mFavoriteList.size() < 1) {
            return new Notification.Builder(this.getApplicationContext(), POKEMON_NOTIFICATION_CHANNEL)
                    .setContentTitle("Welcome to the pokedex!")
                    .setContentText("We hope you enjoy your stay!")
                    .setAutoCancel(true)
                    .build();
        }

        //TODO: voeg pokemon plaatje toe

        Bitmap icon = null;

        try{
            Writer writer = new Writer(this.getApplicationContext(), mFavoriteList.get(mRandomIndex));
            icon = writer.getPokemonBitmap(getResources()).getBitmap();
        } catch (Exception e){

        }

        if(icon == null) icon = BitmapFactory.decodeResource(getResources(), R.drawable.pokemon_bulbasaur);
        Icon smallcon = Icon.createWithBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pokemon_bulbasaur));

        Intent activity = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent mainActivityIntent = PendingIntent.getActivity(this.getApplicationContext(), POKEMON_PENDING_INTENT_RESULT, activity, PendingIntent.FLAG_UPDATE_CURRENT);


        return new Notification.Builder(this.getApplicationContext(), POKEMON_NOTIFICATION_CHANNEL)
                .setContentTitle(mFavoriteList.get(mRandomIndex).getName() + " misses you!")
                .setSmallIcon(smallcon)
                .setLargeIcon(icon)
                .setContentText("Come back soon or your " + mFavoriteList.get(mRandomIndex).getName() + " gets it \uD83D\uDD2A \n" +
                        "\uD83D\uDC80\n")
                .setContentIntent(mainActivityIntent)
                .setAutoCancel(true)
                .build();
    }
}
