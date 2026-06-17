package ma.oprojet.ln0wp.utils;
// NetworkMonitor.java

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;

public class NetworkMonitor {
    
    public interface NetworkListener {
        void onNetworkAvailable();
        void onNetworkUnavailable();
    }
    
    private static NetworkMonitor instance;
    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<NetworkListener> listeners = new ArrayList<>();
    private boolean isConnected = true;
    
    private NetworkMonitor(Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) 
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkConnection();
    }
    
    public static synchronized NetworkMonitor getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkMonitor(context);
        }
        return instance;
    }
    
    public boolean isNetworkAvailable() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    
    public void checkConnection() {
        boolean wasConnected = isConnected;
        isConnected = isNetworkAvailable();
        
        if (wasConnected != isConnected) {
            notifyListeners();
        }
    }
    
    public void addListener(NetworkListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(NetworkListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (NetworkListener listener : listeners) {
                    if (isConnected) {
                        listener.onNetworkAvailable();
                    } else {
                        listener.onNetworkUnavailable();
                    }
                }
            }
        });
    }
    
    public boolean getCurrentStatus() {
        return isConnected;
    }
}
