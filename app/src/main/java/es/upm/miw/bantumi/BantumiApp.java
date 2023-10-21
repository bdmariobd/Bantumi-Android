package es.upm.miw.bantumi;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class BantumiApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
