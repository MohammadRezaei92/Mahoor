package com.alium.orin;

import android.app.Application;
import android.os.Build;

import com.alium.orin.appshortcuts.DynamicShortcutManager;


/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Set up dynamic shortcuts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            new DynamicShortcutManager(this).initDynamicShortcuts();
        }
    }
}
