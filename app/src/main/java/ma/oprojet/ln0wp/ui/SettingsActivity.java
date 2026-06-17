package ma.oprojet.ln0wp.ui;

// SettingsActivity.java

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ma.oprojet.ln0wp.MainActivity;
import ma.oprojet.ln0wp.R;
import ma.oprojet.ln0wp.utils.ThemeUtils;

public class SettingsActivity extends AppCompatActivity {
    
    private ListView themeListView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.action_settings);
        }
        
        themeListView = (ListView) findViewById(R.id.theme_list);
        
        setupThemeList();

    }

    private void setupThemeList() {
        // Get theme entries from arrays.xml
        String[] themeEntries = getResources().getStringArray(R.array.theme_entries);
        final String[] themeValues = getResources().getStringArray(R.array.theme_values);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_list_item_single_choice, 
            themeEntries
        );
        
        themeListView.setAdapter(adapter);
        themeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        // Set current selection based on saved theme
        int savedTheme = ThemeUtils.getSavedTheme(this);
        int selectedPosition = getPositionFromThemeValue(savedTheme, themeValues);
        themeListView.setItemChecked(selectedPosition, true);
        
        themeListView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedTheme = Integer.parseInt(themeValues[position]);

            // Save and apply theme
            ThemeUtils.saveTheme(SettingsActivity.this, selectedTheme);
            ThemeUtils.applyTheme(selectedTheme);
            if (selectedTheme == 0) {
                Toast.makeText(SettingsActivity.this, R.string.AutoOption, Toast.LENGTH_LONG).show();
            }

            // Restart activity to apply theme immediately
            recreateActivity();
            recreate();

        });
    }
    
    private int getPositionFromThemeValue(int themeValue, String[] themeValues) {
        for (int i = 0; i < themeValues.length; i++) {
            if (Integer.parseInt(themeValues[i]) == themeValue) {
                return i;
            }
        }
        return 0; // Default to first position
    }

    private void recreateActivity() {
        // To restart/recreate TargetActivity from SourceActivity:
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
