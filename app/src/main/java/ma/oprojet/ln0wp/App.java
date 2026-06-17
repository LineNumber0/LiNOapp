package ma.oprojet.ln0wp;

import android.app.Application;

import ma.oprojet.ln0wp.utils.ThemeUtils;

public class App extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize theme from preferences
        ThemeUtils.initTheme(this);
    }
}
