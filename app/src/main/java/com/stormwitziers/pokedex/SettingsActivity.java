package com.stormwitziers.pokedex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
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
        switchPreference = (SwitchPreferenceCompat)findPreference("autoFav");
        seekBarPreference = (SeekBarPreference)findPreference("setRating");

        notificationBarPreference = (SwitchPreferenceCompat)findPreference("notif");
        notificationDelayPreference = (SeekBarPreference)findPreference("notifDelay");
    }

    @Override
    public void onStop() {
        super.onStop();

        UpdateSummary(getPreferenceScreen().getContext().getSharedPreferences("preferences", 0));
    }

    private void UpdateSummary(SharedPreferences sharedPreferences)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("autoFav", switchPreference.isChecked());
        editor.putInt("setRating", seekBarPreference.getValue());

        editor.putBoolean("notif", notificationBarPreference.isChecked());
        editor.putInt("notifDelay", notificationDelayPreference.getValue());

        editor.apply();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
