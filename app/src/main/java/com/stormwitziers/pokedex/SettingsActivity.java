package com.stormwitziers.pokedex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsActivity extends PreferenceFragmentCompat {
    public SeekBarPreference seekBarPreference;
    public SwitchPreferenceCompat switchPreference;

    public SwitchPreferenceCompat notificationBarPreference;
    public SeekBarPreference notificationDelayPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        seekBarPreference = (SeekBarPreference)findPreference("setRating");
        switchPreference = (SwitchPreferenceCompat)findPreference("autoFav");

        notificationBarPreference = (SwitchPreferenceCompat)findPreference("notif");
        notificationDelayPreference = (SeekBarPreference)findPreference("notifDelay");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
