package ma.oprojet.ln0wp.utils;

// ThemeUtils.java
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

public class ThemeUtils {
    
    public static final String THEME_PREF_KEY = "AppTheme";
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;
    public static final int THEME_SYSTEM = 0;
    
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    public static int getSavedTheme(Context context) {
        return getPreferences(context).getInt(THEME_PREF_KEY, THEME_SYSTEM);
    }
    
    public static void saveTheme(Context context, int theme) {
        getPreferences(context).edit().putInt(THEME_PREF_KEY, theme).apply();
    }
    
    public static void applyTheme(int themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
    
    public static void initTheme(Context context) {
        int savedTheme = getSavedTheme(context);
        applyTheme(savedTheme);
    }
}
