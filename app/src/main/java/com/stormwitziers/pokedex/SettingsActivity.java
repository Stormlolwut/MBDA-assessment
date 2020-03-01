package com.stormwitziers.pokedex;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import android.os.Bundle;

public class SettingsActivity extends PreferenceFragmentCompat {
    public SeekBarPreference seekBarPreference;
    public SwitchPreferenceCompat switchPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        seekBarPreference = (SeekBarPreference)findPreference("setRating");
        switchPreference = (SwitchPreferenceCompat)findPreference("autoFav");
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
