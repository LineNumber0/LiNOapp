package ma.oprojet.ln0wp.utils;
// WordPressNotificationScheduler.java

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import ma.oprojet.ln0wp.Services.WordPressAlarmReceiver;

public class WordPressNotificationScheduler {
    
    public static void scheduleNotifications(Context context, int intervalMinutes) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WordPressAlarmReceiver.class);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        long intervalMillis = intervalMinutes * 60 * 1000L;
        long firstRun = System.currentTimeMillis() + intervalMillis;

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, firstRun, pendingIntent);
    }
    
    public static void cancelNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WordPressAlarmReceiver.class);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        alarmManager.cancel(pendingIntent);
    }
}
