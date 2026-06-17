package ma.oprojet.ln0wp;

import static ma.oprojet.ln0wp.ArchivePostAdaptater.formatPostDate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import ma.oprojet.ln0wp.Models.Media;
import ma.oprojet.ln0wp.Models.Post;
import ma.oprojet.ln0wp.Models.User;
import ma.oprojet.ln0wp.Services.FtiarService;
import ma.oprojet.ln0wp.Services.NotificationService;
import ma.oprojet.ln0wp.Services.PicassoImageGetter;
import ma.oprojet.ln0wp.utils.Logger;
import ma.oprojet.ln0wp.utils.WordPressNotificationScheduler;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SinglePostActivity extends BaseActivity {

    private ShareActionProvider mShareActionProvider;
    // {B.A}
    private String cleanElementorCss(String htmlStr) {
        // Remove <style>...</style>
        htmlStr = htmlStr.replaceAll("(?s)<style.*?>.*?</style>", "");
        // Remove empty paragraphs and divs
        htmlStr = htmlStr.replaceAll("(?i)<p>\\s*(?:&nbsp;|<br\\s*/?>|\\s)*\\s*</p>", "");
        htmlStr = htmlStr.replaceAll("(?i)<div[^>]*>\\s*</div>", "");
        // Remove leftover &nbsp;
        htmlStr = htmlStr.replace("&nbsp;", "");
        // Replace multiple <br> with single
        htmlStr = htmlStr.replaceAll("(?i)(<br\\s*/?>\\s*){2,}", "<br>");
        // Convert div to p (TextView-friendly)
        htmlStr = htmlStr.replaceAll("(?i)<div[^>]*>", "<p>");
        htmlStr = htmlStr.replaceAll("(?i)</div>", "</p>");
        return htmlStr.trim();
    }

    // {B.A} The following code delays the loading until the TextView is fully loaded, otherwise we will see the post image pushing the post content.
    private Handler handler = new Handler(Looper.getMainLooper());

    private void loadDataDelay(FrameLayout progressBar, TextView contentView) {
        // Delay showing progress for fast loads
        handler.postDelayed(() -> {
            if (progressBar.getVisibility() == View.VISIBLE) {
                // Still loading, keep progress visible
            }
        }, 300); // 300ms delay

        // Simulate data loading on background thread
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate loading

                runOnUiThread(() -> {
                    // Data loaded, now ensure view is ready

                    // Force layout pass
                    contentView.post(() -> {
                        // Now hide progress and show content
                        progressBar.setVisibility(View.GONE);

                        contentView.setVisibility(View.VISIBLE);
                        contentView.setAlpha(0f);
                        contentView.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .start();
                    });
                });
            } catch (InterruptedException e) {
                Logger.e("UI_THREAD", e.toString(), e);
            }
        }).start();

}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);

        ViewCompat.setTransitionName(findViewById(R.id.appBarLayout), "Name");

        // {B.A} Rewrite this Activity for Network handling.
        setupViews();
        loadData();

    }

    private void setupViews() {
        // Find views
        noInternetLayout = findViewById(R.id.no_internet_layout);
        contentLayout = findViewById(R.id.content_layout);
        loadingLayout = (FrameLayout) findViewById(R.id.progressBar4);

        // Setup retry button
        Button retryButton = (Button) findViewById(R.id.btn_retry);
        retryButton.setOnClickListener(v -> {
            if (networkMonitor.isNetworkAvailable()) {
                hideFullScreenNoInternet();
                retryAction();
            } else {
                Toast.makeText(SinglePostActivity.this, R.string.noconn, Toast.LENGTH_SHORT).show();
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
        protected void loadData () {
            if (!networkMonitor.isNetworkAvailable()) {
                showFullScreenNoInternet();
                return;
            }

            showLoading();


            // get view widgets
            final ImageView post_image = (ImageView) findViewById(R.id.post_photo);
            final TextView post_title = (TextView) findViewById(R.id.post_title);
            final TextView post_date = (TextView) findViewById(R.id.post_date);
            final TextView post_author = (TextView) findViewById(R.id.post_author);
            final TextView post_content = (TextView) findViewById(R.id.post_content);
            // {B.A}
            FrameLayout progressBar2;
            progressBar2 = (FrameLayout) findViewById(R.id.progressBar4);

            // Get the post_id
            Intent lastIntent = getIntent();
            int post_id = lastIntent.getIntExtra("post_id", 0);


            final FtiarService ftiarService = new RestAdapter.Builder()
                    .setEndpoint(FtiarService.ENDPOINT)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build()
                    .create(FtiarService.class);


            // Retrieve the post by passing the post_id
            ftiarService.getPostAsync(post_id, new Callback<Post>() {

                @Override
                public void success(Post post, Response response) {

                    String title = post.getTitle().getRendered();

                    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                    collapsingToolbarLayout.setTitle(title); // Set Toolbar title : Post title
                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

                    setTitle(title);
                    post_title.setText(title);

                    // Create share provider intent
                    setShareIntent(createShareIntent(title, post.getLink()));
                    // {B.A} Set a better TextView with displayed images instead of the commented code after this.
                    Picasso picasso = Picasso.get();
                    android.content.res.Resources res = getResources();
                    String htmlContent = cleanElementorCss(post.getContent().getRendered());
                    PicassoImageGetter imageGetter = new PicassoImageGetter(post_content, res, picasso, SinglePostActivity.this);
                    // {B.A} Fix links before rendering.
                    post_content.setMovementMethod(LinkMovementMethod.getInstance());
                    post_content.setLinksClickable(true);

                    post_content.setText(Html.fromHtml(
                            // {B.A} Cleaning Elementor Wordpress plugin style code that get printed in the TextView.
                            cleanElementorCss(htmlContent),
                            Html.FROM_HTML_MODE_LEGACY,
                            imageGetter,
                            null
                    ));

                    //String content = Html.fromHtml(post.getContent().getRendered()).toString();
                    //post_content.setText(content);

                    //post_date.setText(post.getI18nFormatedDate(post.getDate(), "dd MMMM yyyy", Locale.FRANCE));
                    // {B.A}
                    String postDate = post.getDate();
                    post_date.setText(formatPostDate(SinglePostActivity.this, postDate));

                    ftiarService.getUserAsync(post.getAuthor(), new Callback<User>() {

                        @Override
                        public void success(User author, Response response) {
                            // Use Picasso lib to display an Image based on an URL
                            post_author.setText(getString(R.string.author) + " " + author.getName());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Logger.d("RETROFIT_ERROR", error.toString());
                        }
                    });

                    ftiarService.getMediaAsync(post.getFeatured_media(), new Callback<Media>() {

                        @Override
                        public void success(Media media, Response response) {
                            // Use Picasso lib to display an Image based on an URL
                            try {
                                Picasso.get().load(media.getMedia_details().getSizes().getLarge().getSource_url()).into(post_image);
                            } catch (NullPointerException e) {
                                Picasso.get().load(R.drawable.kbd_bw).into(post_image);
                            }
                            hideLoading();

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Logger.d("RETROFIT_ERROR", error.toString());
                            hideLoading();
                            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                                showFullScreenNoInternet();
                            } else {
                                LinearLayout postErrorLayout = (LinearLayout) findViewById(R.id.post_server_error_layout);
                                TextView postErrorMessge = (TextView) findViewById(R.id.post_errorMsg);
                                //Toast.makeText(SinglePostActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                postErrorLayout.setVisibility(View.VISIBLE);
                                postErrorMessge.setText(error.getMessage());
                            }
                        }
                    });

                    loadDataDelay(progressBar2, post_content);

                }

                @Override
                public void failure(RetrofitError error) {
                    Logger.d("RETROFIT_ERROR", error.toString());
                }
            });

            // Start notification service
            startNotificationService();

            // Schedule Notification periodic checks.
            WordPressNotificationScheduler.scheduleNotifications(this, 15); // Check every 15 minutes


        }

    private void startNotificationService() {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);
    }

    @Override
    protected void retryAction() {
        loadData();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_post, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent( String post_title, String post_url ) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        String sharedText = getString(R.string.readMore) + " \"" + post_title + "\" " + getString(R.string.wrtby) + " " + getString(R.string.developer_name) + " " + getString(R.string.postLink) + " " + post_url;
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharedText);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
