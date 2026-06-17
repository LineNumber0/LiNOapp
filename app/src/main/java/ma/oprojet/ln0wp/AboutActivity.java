package ma.oprojet.ln0wp;

import static ma.oprojet.ln0wp.Services.FtiarService.BLOG;
import static ma.oprojet.ln0wp.Services.FtiarService.WEBSITE;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.about_title);
        }

        // Populate version number programmatically (optional)
        TextView versionText = (TextView) findViewById(R.id.app_version);
        String version = getVersionName();
        versionText.setText(String.format(getString(R.string.version_format), version));

        String blogLink = WEBSITE + BLOG;

         TextView websiteLink = (TextView) findViewById(R.id.developer_website);
         websiteLink.setText(blogLink);

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void retryAction() {

    }

    /**
     * Helper method to get the app version name from package manager.
     */
    private String getVersionName() {
        try {
            return getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "1.0"; // fallback
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}