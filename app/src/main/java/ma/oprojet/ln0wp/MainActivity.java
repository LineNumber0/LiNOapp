package ma.oprojet.ln0wp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ma.oprojet.ln0wp.Models.Post;
import ma.oprojet.ln0wp.Services.FtiarService;
import ma.oprojet.ln0wp.Services.NotificationService;
import ma.oprojet.ln0wp.ui.SettingsActivity;
import ma.oprojet.ln0wp.utils.Logger;
import ma.oprojet.ln0wp.utils.WordPressNotificationScheduler;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends BaseActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        // {B.A} Rewrite this Activity for Network handling.
        setupViews();
        loadData();
    }

    private void setupViews() {
        // Find views
        noInternetLayout = findViewById(R.id.no_internet_layout);
        contentLayout = findViewById(R.id.content_layout);
        loadingLayout = (FrameLayout) findViewById(R.id.progressBar3);

        ProgressBar loadingIcon = (ProgressBar) findViewById(R.id.loadCirc);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.4f, 1.0f);
        alphaAnimation.setDuration(800); // Duration for one cycle
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);

        loadingIcon.startAnimation(alphaAnimation);

        // Setup retry button
        Button retryButton = (Button) findViewById(R.id.btn_retry);
        retryButton.setOnClickListener(v -> {
            if (networkMonitor.isNetworkAvailable()) {
                hideFullScreenNoInternet();
                retryAction();
            } else {
                Toast.makeText(MainActivity.this, R.string.noconn, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void showFullScreenNoInternet() {
        if (noInternetLayout != null && contentLayout != null) {
            noInternetLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void hideFullScreenNoInternet() {
        if (noInternetLayout != null && contentLayout != null) {
            noInternetLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void loadData() {
        if (!networkMonitor.isNetworkAvailable()) {
            showFullScreenNoInternet();
            return;
        }

        showLoading();

        // Do BackgroundTask job to send a notification if there is a new post

        // Prepare API REST Calls
        FtiarService ftiarService = new RestAdapter.Builder()
                .setEndpoint(FtiarService.ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(FtiarService.class);




        ftiarService.getPostsAsync(new Callback<List<Post>>() {
            @Override
            public void success(List<Post> posts, Response response) {
                Logger.d("Retrofit Success");
                showPosts(posts);
                hideLoading();
            }

            @Override
            public void failure(RetrofitError error) {
                Logger.d("Retrofit Fails");
                Logger.d("RETROFIT_ERROR", error.toString());
                hideLoading();
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    showFullScreenNoInternet();
                } else {
                    LinearLayout errorLayout = (LinearLayout) findViewById(R.id.server_error_layout);
                    TextView errorMessge = (TextView) findViewById(R.id.errorMsg);
                        //Toast.makeText(MainActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    errorLayout.setVisibility(View.VISIBLE);
                    errorMessge.setText(error.getMessage());
                }
            }
        });

        // Start notification service
        startNotificationService();

        // Schedule Notification periodic checks.
        WordPressNotificationScheduler.scheduleNotifications(this, 15); // Check every 15 minutes

    }

    @Override
    protected void retryAction() {
        loadData();
    }


    public void showPosts(List<Post> posts) {

        //Toast.makeText(MainActivity.this, getString(R.string.postsNumber) + " " + posts.size(),Toast.LENGTH_LONG).show();
        // {B.A} Giving up on Toast to save time for fixing a crash (revealed on emulator API 25)
        Logger.d(getString(R.string.postsNumber) + " " + posts.size());

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        // {B.A} Fix the instability of the RecyclerView.
        mRecyclerView.setItemViewCacheSize(20);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        ArchivePostAdaptater adapter = new ArchivePostAdaptater(posts, getBaseContext());
        mRecyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startNotificationService() {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Optional: Cancel notifications when app closes
        WordPressNotificationScheduler.cancelNotifications(this);
    }
}

