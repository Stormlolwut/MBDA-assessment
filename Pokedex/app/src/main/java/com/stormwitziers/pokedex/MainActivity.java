package com.stormwitziers.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.stormwitziers.pokedex.Fragments.DetailFragment;
import com.stormwitziers.pokedex.Fragments.OverviewFragment;

import java.util.ArrayList;
import java.util.Objects;

import static com.stormwitziers.pokedex.PokemonService.POKEMON_NOTIFICATION_CHANNEL;


public class MainActivity extends AppCompatActivity implements OverviewFragment.OnPokemonSelected, RateMyPokemonDialogFragment.OnPokemonRatingDialogListener {

    private final CharSequence name = "Pokemon channel!";
    private final String description = "For all your pokemon updates!";

    private final String OVERVIEW_FRAGMENT_TAG = "fragment_list";
    private final String DETAIL_VIEW_FRAGMENT_TAG = "fragment_details";

    private FragmentManager mFragmentManager;
    private ArrayList<Pokemon> mFavoriteList;

    private OverviewFragment mOverviewFragment;
    private DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mFavoriteList = new ArrayList<Pokemon>();

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mOverviewFragment = new OverviewFragment();

        fragmentTransaction.add(R.id.LinearLayout, mOverviewFragment, OVERVIEW_FRAGMENT_TAG);
        fragmentTransaction.commit();

        Intent pokemonServiceIntent = new Intent(this, PokemonService.class);

        startService(pokemonServiceIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void onItemSelected(Pokemon pokemon) {

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mDetailFragment = new DetailFragment(pokemon, mFavoriteList);
        
        fragmentTransaction.addToBackStack(DETAIL_VIEW_FRAGMENT_TAG);
        fragmentTransaction.replace(mFragmentManager.findFragmentByTag(OVERVIEW_FRAGMENT_TAG).getId(), mDetailFragment, DETAIL_VIEW_FRAGMENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onPositiveButtonClick(float rating) {
        mDetailFragment.updatePokemonRating(rating);
    }

    private void createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(POKEMON_NOTIFICATION_CHANNEL, name, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);

        }
    }
}
