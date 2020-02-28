package com.stormwitziers.pokedex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PokemonCreationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String mCurrentPhotoPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_creation);

        Spinner pokemonType = findViewById(R.id.pokemon_creation_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.string_pokemon_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pokemonType.setAdapter(adapter);
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
