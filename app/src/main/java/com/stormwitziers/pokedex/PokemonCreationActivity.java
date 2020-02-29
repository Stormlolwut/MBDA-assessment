package com.stormwitziers.pokedex;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.stormwitziers.pokedex.FileWriters.Writer;
import com.stormwitziers.pokedex.PokemonList.PokemonLoader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class PokemonCreationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String mCurrentPhotoPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_creation);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner pokemonType = findViewById(R.id.pokemon_creation_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.string_pokemon_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pokemonType.setAdapter(adapter);
    }

    public void savePokemon(View v){
        EditText name = findViewById(R.id.pokemon_creation_name);
        Spinner type = findViewById(R.id.pokemon_creation_spinner);
        ImageButton image = findViewById(R.id.pokemon_creation_profile_picture);

        // TODO: Check if it still works thx Storm <3
         PokemonLoader loader = ((MainActivity)getBaseContext()).getPokemonLoader();

        if(TextUtils.isEmpty(name.getText())) {
            name.setError("Please fill in a name for your pokemon!");
            return;
        }else if(!loader.isNameUnique(name.getText().toString())){
            name.setError("That name is already taken please us an other!");
            return;
        }

        if(image.getDrawable() == null) {
            name.setError("Please add an image for your pokemon!");
            return;
        }

        Pokemon pokemon = new Pokemon(name.getText().toString(), image.getDrawable(), type.getSelectedItem().toString());
        Writer writer = new Writer(this, pokemon);
        writer.Save();

        loader.CustomPokemonList.add(pokemon);
        this.finish();
    }

    //region Choose profile picture

    public void ChooseProfilePicture(View v){
        TakePicture();
    }

    private void TakePicture(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager()) != null){
            File image = null;
            try{
                image = createImageFile();
            } catch (IOException e){
                Log.println(Log.ERROR, "image creation", "an error occurred: " + e);
                return;
            }
            Uri imageURI = FileProvider.getUriForFile(this, "com.stormwitziers.pokedex.fileprovider", image);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);

            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                File image = new File(mCurrentPhotoPath);
                Uri uri = Uri.fromFile(image);

                ImageButton profilePicture = findViewById(R.id.pokemon_creation_profile_picture);
                profilePicture.setImageURI(uri);

                addImageToGallery();
            }
        }
    }

    private File createImageFile() throws IOException{
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

    private void addImageToGallery(){
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
