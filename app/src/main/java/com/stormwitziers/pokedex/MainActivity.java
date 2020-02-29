package com.stormwitziers.pokedex;

import androidx.annotation.Nullable;
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

    private OverviewFragment.OnPokemonSelected mOnPokemonSelected;
    public ArrayAdapter<String> SpinnerAdapter;

    private final CharSequence name = "Pokemon channel!";
    private final String description = "For all your pokemon updates!";

    private final String OVERVIEW_FRAGMENT_TAG = "fragment_list";
    private final String DETAIL_VIEW_FRAGMENT_TAG = "fragment_details";

    private final int EDIT_POKEMON_RESULT = 1;

    private FragmentManager mFragmentManager;

    private OverviewFragment mOverviewFragment;
    private DetailFragment mDetailFragment;

    private PokemonLoader mPokemonLoader;
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
        mOverviewFragment = new OverviewFragment(this, mPokemonLoader);
        mPokemonLoader.setHandler(mOverviewFragment);

        fragmentTransaction.add(R.id.LinearLayout, mOverviewFragment, OVERVIEW_FRAGMENT_TAG);
        fragmentTransaction.commit();

        Intent pokemonServiceIntent = new Intent(this, PokemonService.class);

        startService(pokemonServiceIntent);

        mOnPokemonSelected = this;
        mPokemonLoader.loadPokemons();
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
        mDetailFragment = new DetailFragment(this, pokemon);

        fragmentTransaction.addToBackStack(DETAIL_VIEW_FRAGMENT_TAG);
        fragmentTransaction.replace(mFragmentManager.findFragmentByTag(OVERVIEW_FRAGMENT_TAG).getId(), mDetailFragment, DETAIL_VIEW_FRAGMENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onPositiveButtonClick(float rating) {
        mDetailFragment.updatePokemonRating(rating);
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(POKEMON_NOTIFICATION_CHANNEL, name, NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
    }


    // TODO: Maybe own class "FavoritePokemon"?
    public void initializeSpinner() {
        ArrayList<String> pokemonNames = new ArrayList<>();
        pokemonNames.add("Home");
        if(mPokemonLoader.FavoriteList != null)
        {
            for (Pokemon pokemon : mPokemonLoader.FavoriteList) {
                pokemonNames.add(pokemon.getName());
            }
        }
        
        mSpinner = findViewById(R.id.toolbar_favorite_spinner);
        SpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pokemonNames);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(SpinnerAdapter);
        mSpinner.setSelection(0,false);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // position 0 is the name favorites.
                if(position != 0){
                    Pokemon pokemon = mPokemonLoader.FavoriteList.get(position - 1);
                    mOnPokemonSelected.onItemSelected(pokemon);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void CreateNewPokemon(View v){
        Intent pokemonCreation = new Intent(this, com.stormwitziers.pokedex.PokemonCreationActivity.class);
        pokemonCreation.putExtra("PokemonLoader", mPokemonLoader);
        startActivity(pokemonCreation);
    }

    public void deletePokemon(View v){
        TextView name = findViewById(R.id.details_name);
        Pokemon p = getCustomPokemon(name.getText().toString());

        if(p == null) return;

        mPokemonLoader.CustomPokemonList.remove(p);

        Writer writer = new Writer(this.getApplicationContext(), p);
        writer.delete();

        Intent i = new Intent(this.getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void editPokemon(View v){
        TextView name = findViewById(R.id.details_name);
        Pokemon p = getCustomPokemon(name.getText().toString());

        Intent pokemonCreation = new Intent(this, com.stormwitziers.pokedex.PokemonCreationActivity.class);
        pokemonCreation.putExtra("PokemonLoader", mPokemonLoader);
        pokemonCreation.putExtra("Pokemon", p);
        startActivityForResult(pokemonCreation, EDIT_POKEMON_RESULT);

        //resetActivities();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_POKEMON_RESULT){
            if(resultCode == RESULT_OK){
                Intent i = new Intent(this.getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }
    }

    private Pokemon getCustomPokemon(String name){
        for (int i = 0; i < mPokemonLoader.CustomPokemonList.size(); i++){
            Pokemon p = mPokemonLoader.CustomPokemonList.get(i);
            if(p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }

    public PokemonLoader getPokemonLoader() {
        return this.mPokemonLoader;
    }
}
