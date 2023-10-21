package es.upm.miw.bantumi;

import android.app.Application;

public class BantumiApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.Theme_Bantumi);
        // DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
