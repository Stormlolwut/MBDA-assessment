package com.stormwitziers.pokedex;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.stormwitziers.pokedex.FileWriters.Writer;
import com.stormwitziers.pokedex.Fragments.DetailFragment;
import com.stormwitziers.pokedex.Fragments.OverviewFragment;
import com.stormwitziers.pokedex.PokemonList.PokemonLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import static com.stormwitziers.pokedex.PokemonService.POKEMON_NOTIFICATION_CHANNEL;


public class MainActivity extends AppCompatActivity implements OverviewFragment.OnPokemonSelected, RateMyPokemonDialogFragment.OnPokemonRatingDialogListener, Serializable {

    private Spinner mSpinner;

    private static SettingsActivity settingInstance;

    private OverviewFragment.OnPokemonSelected mOnPokemonSelected;
    public ArrayAdapter<String> SpinnerAdapter;

    private final CharSequence NAME = "The pokedex!";
    private final String DESCRIPTION = "For all your pokemon updates!";

    private final String OVERVIEW_FRAGMENT_TAG = "fragment_list";
    private final String DETAIL_VIEW_FRAGMENT_TAG = "fragment_details";

    private final int EDIT_POKEMON_RESULT = 1;
    private final int SETTINGS_RESULT = 2;

    private FragmentManager mFragmentManager;

    private OverviewFragment mOverviewFragment;
    private DetailFragment mDetailFragment;

    private PokemonLoader mPokemonLoader;
    private Intent mPokemonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        mPokemonLoader = new PokemonLoader(this);
        mOverviewFragment = new OverviewFragment();
        mOverviewFragment.initialize(this, mPokemonLoader);
        mPokemonLoader.setHandler(mOverviewFragment);

        for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); i++) {
            mFragmentManager.popBackStack();
        }
        Fragment overviewFragment = mFragmentManager.findFragmentByTag(OVERVIEW_FRAGMENT_TAG);
        if (overviewFragment != null) {
            fragmentTransaction.remove(overviewFragment);
        }

        fragmentTransaction.add(R.id.FragmentLayout, mOverviewFragment, OVERVIEW_FRAGMENT_TAG);
        fragmentTransaction.commit();

        mOnPokemonSelected = this;
        mPokemonLoader.loadPokemons();

        initializeSpinner();
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
        mDetailFragment = new DetailFragment();
        mDetailFragment.initialize(this, pokemon);

        fragmentTransaction.addToBackStack(DETAIL_VIEW_FRAGMENT_TAG);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            fragmentTransaction.replace(mFragmentManager.findFragmentByTag(OVERVIEW_FRAGMENT_TAG).getId(), mDetailFragment, DETAIL_VIEW_FRAGMENT_TAG);
        } else if (mFragmentManager.findFragmentByTag(DETAIL_VIEW_FRAGMENT_TAG) == null) {
            fragmentTransaction.add(R.id.FragmentLayout, mDetailFragment, DETAIL_VIEW_FRAGMENT_TAG);
        } else {
            fragmentTransaction.remove(mFragmentManager.findFragmentByTag(DETAIL_VIEW_FRAGMENT_TAG));
            fragmentTransaction.add(R.id.FragmentLayout, mDetailFragment, DETAIL_VIEW_FRAGMENT_TAG);
        }
        fragmentTransaction.commitAllowingStateLoss();

        UpdateSpinner(pokemon.getName());
    }

    @Override
    public void onPositiveButtonClick(float rating) {
        mDetailFragment.updatePokemonRating(rating);
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(POKEMON_NOTIFICATION_CHANNEL, NAME, NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription(DESCRIPTION);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
    }

    public void initializeSpinner() {
        ArrayList<String> pokemonNames = new ArrayList<>();
        pokemonNames.add("Home");
        if (mPokemonLoader.FavoriteList != null) {
            for (Pokemon pokemon : mPokemonLoader.FavoriteList) {
                pokemonNames.add(pokemon.getName());
            }
        }

        mSpinner = findViewById(R.id.toolbar_favorite_spinner);
        SpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pokemonNames);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(SpinnerAdapter);
        mSpinner.setSelection(0, false);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // position 0 is the name favorites.
                if (position != 0) {
                    Pokemon pokemon = mPokemonLoader.FavoriteList.get(position - 1);
                    mOnPokemonSelected.onItemSelected(pokemon);
                } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); i++) {
                        mFragmentManager.popBackStack();
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("preferences", 0);

        if(pref.getBoolean("notif", false)){
            mPokemonService = new Intent(this, PokemonService.class);
            mPokemonService.putExtra("favorites", mPokemonLoader);
            startService(mPokemonService);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPokemonService == null) return;
        this.getApplicationContext().stopService(mPokemonService);
    }

    private void ResetActivity() {
        if(mPokemonService != null){
            this.getApplicationContext().stopService(mPokemonService);
        }

        Intent i = new Intent(this.getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

    public void CreateNewPokemon(View v) {
        Intent pokemonCreation = new Intent(this, com.stormwitziers.pokedex.PokemonCreationActivity.class);
        pokemonCreation.putExtra("PokemonLoader", mPokemonLoader);
        startActivity(pokemonCreation);
    }

    public void deletePokemon(View v) {
        TextView name = findViewById(R.id.details_name);
        Pokemon p = getCustomPokemon(name.getText().toString());

        if (p == null) return;

        mPokemonLoader.CustomPokemonList.remove(p);

        Writer writer = new Writer(this.getApplicationContext(), p);
        writer.delete();

        ResetActivity();
    }

    public void editPokemon(View v) {
        TextView name = findViewById(R.id.details_name);
        Pokemon p = getCustomPokemon(name.getText().toString());

        Intent pokemonCreation = new Intent(this, com.stormwitziers.pokedex.PokemonCreationActivity.class);
        pokemonCreation.putExtra("PokemonLoader", mPokemonLoader);
        pokemonCreation.putExtra("Pokemon", p);
        startActivityForResult(pokemonCreation, EDIT_POKEMON_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_POKEMON_RESULT) {
            if (resultCode == RESULT_OK) {
                if(mPokemonService != null){
                    this.getApplicationContext().stopService(mPokemonService);
                }

                Intent i = new Intent(this.getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }
    }

    private Pokemon getCustomPokemon(String name) {
        for (int i = 0; i < mPokemonLoader.CustomPokemonList.size(); i++) {
            Pokemon p = mPokemonLoader.CustomPokemonList.get(i);
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public PokemonLoader getPokemonLoader() {
        return this.mPokemonLoader;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment detailFragment = mFragmentManager.findFragmentByTag(DETAIL_VIEW_FRAGMENT_TAG);
        if (detailFragment != null) {

            String pokemonName = ((TextView) findViewById(R.id.details_name)).getText().toString();
            UpdateSpinner(pokemonName);
        } else {
            UpdateSpinner("Home");
        }
    }

    private void UpdateSpinner(String pokemonName) {
        for (int i = 0; i < SpinnerAdapter.getCount(); i++) {
            String name = SpinnerAdapter.getItem(i);
            if (name.equals(pokemonName)) {
                mSpinner.setSelection(i, false);
                return;
            }
        }

        mSpinner.setSelection(0, false);
    }

    public void PopBackStackFragment() {
        mFragmentManager.popBackStack();
    }

    public void UpdateSpinnerPosition() {
        mSpinner.setSelection(SpinnerAdapter.getCount() - 1, false);
    }

    public void OpenSettings() {
        Intent intent = new Intent(this, com.stormwitziers.pokedex.SettingsAppActivity.class);
        startActivity(intent);
    }
}
