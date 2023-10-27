package es.upm.miw.bantumi;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;

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

    protected void createSnackbar(String message) {
        Log.i("SettingsActivity", "Snackbar: " + message);
        Snackbar.make(findViewById(R.id.settings_container), message, Snackbar.LENGTH_LONG).show();
    }

    public static class MySettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            BantumiApp app = (BantumiApp) requireActivity().getApplication();
            SettingsActivity settingsActivity = (SettingsActivity) requireActivity();

            findPreference("theme").setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        app.setTheme(newValue.toString());
                        return true;
                    }
            );

            findPreference("useDinamicColor").setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        settingsActivity.createSnackbar(getResources().getString(R.string.restartApp));
                        if (newValue.toString().equals("true")) {
                            app.setDynamicTheme(true);
                            this.getActivity().recreate();
                        } else {
                            app.setDynamicTheme(false);
                            this.getActivity().recreate();
                        }
                        return true;
                    }
            );

            findPreference("useDinamicColor").setEnabled(app.isDynamicColorSupported());


            findPreference("initialSeedNumber").setSummaryProvider(
                    preference -> {
                        String value = preference.getSharedPreferences().getString("initialSeedNumber", String.valueOf(getResources().getInteger(R.integer.intNumInicialSemillas)));
                        return getResources().getString(R.string.initialSeedSummary) + ": " + value;
                    }
            );


            EditTextPreference seedNumber = findPreference("initialSeedNumber");

            seedNumber.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setSelection(editText.getText().length());
            });

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
