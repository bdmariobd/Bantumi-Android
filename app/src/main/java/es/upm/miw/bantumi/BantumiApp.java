package es.upm.miw.bantumi;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

public class BantumiApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean useDynamicTheme = prefs.getBoolean("useDinamicColor", false);
        setDynamicTheme(useDynamicTheme);
        String theme = prefs.getString("theme", "system");
        setTheme(theme);
    }

    public void setDynamicTheme(Boolean useDynamicTheme) {
        if (useDynamicTheme) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        } else {
            DynamicColors.applyToActivitiesIfAvailable(this, R.style.Theme_Bantumi);
        }
    }

    public void setTheme(String theme) {
        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
