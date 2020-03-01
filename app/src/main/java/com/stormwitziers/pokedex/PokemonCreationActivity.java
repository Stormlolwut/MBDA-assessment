package com.stormwitziers.pokedex;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.stormwitziers.pokedex.FileWriters.Writer;
import com.stormwitziers.pokedex.PokemonList.PokemonLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PokemonCreationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String mCurrentPhotoPath;
    private PokemonLoader mPokemonLoader;
    private Pokemon mPreviousPokemon;

    private ArrayAdapter<CharSequence> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_creation);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPokemonLoader = (PokemonLoader) getIntent().getSerializableExtra("PokemonLoader");
        mPreviousPokemon = (Pokemon) getIntent().getSerializableExtra("Pokemon");

        Spinner pokemonType = findViewById(R.id.pokemon_creation_spinner);
        mAdapter = ArrayAdapter.createFromResource(this, R.array.string_pokemon_type_array, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pokemonType.setAdapter(mAdapter);

        if (mPreviousPokemon != null) {
            setPokemonData();
        }
    }

    private void setPokemonData() {
        //TODO: get image from json since you cant serialize a bitmap in the pokemon class
        EditText name = findViewById(R.id.pokemon_creation_name);
        Spinner type = findViewById(R.id.pokemon_creation_spinner);
        ImageButton image = findViewById(R.id.pokemon_creation_profile_picture);

        Writer writer = new Writer(this.getApplicationContext(), mPreviousPokemon);
        BitmapDrawable pokemonImage = writer.getPokemonBitmap(getResources());

        name.setText(mPreviousPokemon.getName());
        type.setSelection(mAdapter.getPosition(mPreviousPokemon.getType()));

        image.setImageDrawable(pokemonImage);
    }

    public void savePokemon(View v) {
        EditText name = findViewById(R.id.pokemon_creation_name);
        Spinner type = findViewById(R.id.pokemon_creation_spinner);
        ImageButton image = findViewById(R.id.pokemon_creation_profile_picture);


        if (TextUtils.isEmpty(name.getText())) {
            name.setError("Please fill in a name for your pokemon!");
            return;
        } else if (!mPokemonLoader.isNameUnique(name.getText().toString())) {
            if( mPreviousPokemon == null)
            {
                name.setError("That name is already taken please us an other!");
                return;
            }
            else if(!mPreviousPokemon.getName().equals(name.getText().toString()))
            {
                name.setError("That name is already taken please us an other!");
                return;
            }
        }

        if (image.getDrawable() == null) {
            name.setError("Please add an image for your pokemon!");
            return;
        }

        // TODO: Pokemon Rating and Favorite zijn beiden false en 0.0 wanneer het niet zo is.
        Pokemon pokemon = new Pokemon(name.getText().toString(), image.getDrawable(), type.getSelectedItem().toString(), true);
        Writer writer = new Writer(this, pokemon);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("preferences", 0);

        if (pref.getBoolean("autoFav", false)) {
            pokemon.isFavorite(true);
            pokemon.setRating(pref.getInt("setRating", 0));
        }

        if (mPreviousPokemon == null) writer.save();
        else {
            writer.update(mPreviousPokemon);
            setResult(RESULT_OK, null);
        }

        mPokemonLoader.CustomPokemonList.add(pokemon);
        this.finish();
    }

    //region Choose profile picture

    public void ChooseProfilePicture(View v) {
        TakePicture();
    }

    private void TakePicture() {


        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        } else {


            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
            gallIntent.setType("image/*");

            // Always use string resources for UI text.
            // This says something like "Share this photo with"
            String title = getResources().getString(R.string.chooser_title);

            if (takePicture.resolveActivity(getPackageManager()) != null) {
                File image = null;
                try {
                    image = createImageFile();
                } catch (IOException e) {
                    Log.println(Log.ERROR, "image creation", "an error occurred: " + e);
                    return;
                }

                Uri imageURI = FileProvider.getUriForFile(this, "com.stormwitziers.pokedex.fileprovider", image);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                gallIntent.putExtra(Intent.EXTRA_MIME_TYPES, imageURI);

                // Create intent to show the chooser dialog
                Intent chooser = Intent.createChooser(takePicture, title);
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{gallIntent});

                startActivityForResult(chooser, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                File image = new File(mCurrentPhotoPath);
                Uri uri = Uri.fromFile(image);

                ImageButton profilePicture = findViewById(R.id.pokemon_creation_profile_picture);
                profilePicture.setImageURI(uri);

                if(profilePicture.getDrawable() == null)
                {
                    image.delete();
                    uri = data.getData();

                    profilePicture.setImageURI(uri);
                }

                addImageToGallery();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addImageToGallery() {
        Intent mediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File image = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(image);

        mediaScan.setData(contentUri);
        this.sendBroadcast(mediaScan);
    }

    //endregion

    //region Pokemon type spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //endregion
}
