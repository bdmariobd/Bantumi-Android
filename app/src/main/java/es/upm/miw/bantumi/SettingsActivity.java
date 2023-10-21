package es.upm.miw.bantumi;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.color.DynamicColors;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new MySettingsFragment())
                .commit();
    }

    public static class MySettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            findPreference("theme").setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        if (newValue.toString().equals("light")) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else if (newValue.toString().equals("dark")) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        }
                        return true;
                    }
            );

            findPreference("useDinamicColor").setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        BantumiApp app = (BantumiApp) requireActivity().getApplication();
                        if(newValue.toString().equals("true")){
                            app.setDynamicTheme(true);
                            this.getActivity().recreate();
                        }else{
                            app.setDynamicTheme(false);
                            this.getActivity().recreate();
                        }
                        return true;
                    }
            );


            findPreference("initialSeedNumber").setSummaryProvider(
                    preference -> {
                        String value = preference.getSharedPreferences().getString("initialSeedNumber", String.valueOf(getResources().getInteger(R.integer.intNumInicialSemillas)));
                        return getResources().getString(R.string.initialSeedSummary) + ": " + value;
                    }
            );

            findPreference("nickName").setSummaryProvider(
                    preference -> {
                        String value = preference.getSharedPreferences().getString("nickName", "Player");
                        return getResources().getString(R.string.nickNameSummary) + ": " + value;
                    }
            );

            findPreference("theme").setSummaryProvider(
                    preference -> {
                        String value = preference.getSharedPreferences().getString("theme", "system");
                        return getResources().getString(R.string.themeSummary) + ": " + value;
                    }
            );
        }
    }
}
