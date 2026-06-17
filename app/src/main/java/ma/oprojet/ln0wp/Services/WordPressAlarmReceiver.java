package ma.oprojet.ln0wp.Services;
// WordPressAlarmReceiver.java

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WordPressAlarmReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        context.startService(serviceIntent);
    }
}
