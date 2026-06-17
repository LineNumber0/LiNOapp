package ma.oprojet.ln0wp;
// From DeepSeek BaseActivity.java

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.FrameLayout;

import ma.oprojet.ln0wp.utils.NetworkMonitor;

public abstract class BaseActivity extends AppCompatActivity 
        implements NetworkMonitor.NetworkListener {
    
    protected NetworkMonitor networkMonitor;
    protected View noInternetLayout;
    protected View contentLayout;
    protected ProgressBar progressBar;
    protected FrameLayout loadingLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkMonitor = NetworkMonitor.getInstance(this);
        
        // Set content view in child activities
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        networkMonitor.addListener(this);
        networkMonitor.checkConnection();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        networkMonitor.removeListener(this);
    }
    
    // Network callbacks
    @Override
    public void onNetworkAvailable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideFullScreenNoInternet();
            }
        });
    }
    
    @Override
    public void onNetworkUnavailable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showFullScreenNoInternet();
            }
        });
    }
    
    // Abstract methods for child activities
    protected abstract void loadData();
    protected abstract void retryAction();
    
    // Full screen no internet methods for MainActivity
    protected void showFullScreenNoInternet() {
        // Override this in MainActivity only
    }
    
    protected void hideFullScreenNoInternet() {
        // Override this in MainActivity only
    }
    
    // Loading methods for all activities
    protected void showLoading() {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.VISIBLE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
    
    protected void hideLoading() {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
