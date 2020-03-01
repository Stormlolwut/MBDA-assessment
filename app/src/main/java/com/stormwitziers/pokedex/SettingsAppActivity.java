package com.stormwitziers.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.io.Serializable;

public class SettingsAppActivity extends AppCompatActivity implements Serializable {
    private static SettingsActivity instance;

    public static SettingsActivity getInstance() {
        if (instance == null) {
            instance = new SettingsActivity();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_app);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.setting_frame_layout, getInstance());
        transaction.commit();
    }
}
