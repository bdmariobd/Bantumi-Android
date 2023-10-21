package es.upm.miw.bantumi;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

public class BantumiApp extends Application {
    Boolean useDynamicTheme;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        useDynamicTheme = prefs.getBoolean("useDinamicColor", false);
        setDynamicTheme(useDynamicTheme);
    }

    public boolean isDynamicTheme() {
        return useDynamicTheme;
    }

    public void setDynamicTheme(Boolean useDynamicTheme) {
        if (useDynamicTheme) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        } else {
            DynamicColors.applyToActivitiesIfAvailable(this, R.style.Theme_Bantumi);
        }
    }
}
