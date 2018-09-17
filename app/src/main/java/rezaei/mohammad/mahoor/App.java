package rezaei.mohammad.mahoor;

import android.app.Application;
import android.os.Build;

import rezaei.mohammad.mahoor.appshortcuts.DynamicShortcutManager;


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
