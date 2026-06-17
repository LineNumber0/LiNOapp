package ma.oprojet.ln0wp.Services;

import static ma.oprojet.ln0wp.Services.FtiarService.ENDPOINT;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ma.oprojet.ln0wp.Models.Post;
import ma.oprojet.ln0wp.R;
import ma.oprojet.ln0wp.SinglePostActivity;
import ma.oprojet.ln0wp.utils.Logger;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class NotificationService extends IntentService {


	private static final String TAG = "BenAbdelWpNotificationService";
	private static final String PREF_NAME = "WordPressPrefs";
	private static final String PREF_LAST_CHECK = "last_check_time";
	private static final String PREF_LAST_POST_DATE = "last_post_date";

	private static final int NOTIFICATION_ID = 1001;
	private static final String WORDPRESS_BASE_URL = ENDPOINT;

	private FtiarService apiService;
	private SharedPreferences prefs;

	private static final String CHANNEL_ID = "linoapp_posts_channel";
	private static final CharSequence CHANNEL_NAME = "LineNumberO Posts";
	private static final String CHANNEL_DESCRIPTION = "Notifications for new LineNumberO posts.";
	int importance = NotificationManager.IMPORTANCE_HIGH;


	// Calls the parent IntentService constructor and names the worker thread for identification.
	// Improves debugging with meaningful thread names because IntentService has no default constructor. (DeepSeek)
	public NotificationService() {
		super("BenAbdelNotificationService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

		// Setup Retrofit
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint(WORDPRESS_BASE_URL)
				.setLogLevel(RestAdapter.LogLevel.FULL)
				.build();

		apiService = restAdapter.create(FtiarService.class);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		checkForNewPosts();
	}

	private void checkForNewPosts() {
		try {
			String lastPostDate = prefs.getString(PREF_LAST_POST_DATE, null);
			List<Post> posts;

			if (lastPostDate != null) {
				// Get posts after the last known post date
				posts = getPostsAfterDate(lastPostDate);
			} else {
				// First time, get latest post
				posts = getLatestPosts();
			}

			if (posts != null && !posts.isEmpty()) {
				// Save the latest post date
				String latestDate = posts.get(0).getDate();
				prefs.edit().putString(PREF_LAST_POST_DATE, latestDate).apply();

				// Show notification for new posts
				for (Post post : posts) {
					showNotification(post);
				}
			}

			// Update last check time
			String currentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
					.format(new Date());
			prefs.edit().putString(PREF_LAST_CHECK, currentTime).apply();

		} catch (Exception e) {
			Logger.e(TAG, "Error checking for new posts !", e);
		}
	}

	private List<Post> getLatestPosts() {
		final List<Post>[] result = new List[1];

		apiService.getLatestPosts(1, "date", true, new Callback<List<Post>>() {
			@Override
			public void success(List<Post> posts, Response response) {
				synchronized (this) {
					result[0] = posts;
					notifyAll();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				Logger.e(TAG, "Failed to get posts : " + error.getMessage());
				synchronized (this) {
					notifyAll();
				}
			}
		});

		synchronized (this) {
			try {
				wait(10000); // Wait up to 10 seconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		return result[0];
	}


	private List<Post> getPostsAfterDate(String date) {
		final List<Post>[] result = new List[1];

		apiService.getPostsAfterDate(date, 10, true, new Callback<List<Post>>() {
			@Override
			public void success(List<Post> posts, Response response) {
				synchronized (this) {
					result[0] = posts;
					notifyAll();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				Logger.e(TAG, "Failed to get posts : " + error.getMessage());
				synchronized (this) {
					notifyAll();
				}
			}
		});

		synchronized (this) {
			try {
				wait(10000); // Wait up to 10 seconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		return result[0];
	}


	private void showNotification(Post post) {
		// Create intent to open the post
		Intent intent = new Intent(this, SinglePostActivity.class);
		intent.putExtra("post_id", post.getId());
		intent.putExtra("post_title",  post.getTitle().getRendered());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


			createNotificationChannel();

		// Build notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_notification_foreground)
				.setContentTitle(getString(R.string.NewPost))
				.setContentText(post.getTitle().getRendered())
				.setStyle(new NotificationCompat.BigTextStyle()
						.bigText(stripHtml(post.getExcerpt().toString())))
				.setAutoCancel(true)
				.setContentIntent(pendingIntent)
				.setPriority(NotificationCompat.PRIORITY_HIGH);



		// Add large icon if available
		String imageUrl = post.getFeaturedImage();
		if (imageUrl != null) {
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
				builder.setLargeIcon(bitmap);

				// For Android 7.0+ use big picture style
				builder.setStyle(new NotificationCompat.BigPictureStyle()
						.bigPicture(bitmap)
						.setBigContentTitle(post.getTitle().getRendered())
						.setSummaryText(stripHtml(post.getExcerpt().getRendered())));
			} catch (Exception e) {
				Logger.e(TAG, "Failed to load notification image !", e);
			}
		}

		// Add actions
		builder.addAction(R.drawable.ic_open_foreground, "Read", pendingIntent);

		// Show notification
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationManagerCompat manager = NotificationManagerCompat.from(this);
		notificationManager.notify(NOTIFICATION_ID + post.getId(), builder.build());
		manager.notify(NOTIFICATION_ID, builder.build());
	}



	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					CHANNEL_ID,
					CHANNEL_NAME,
					importance
			);

			channel.setDescription(CHANNEL_DESCRIPTION);


			channel.enableVibration(true);
			channel.setVibrationPattern(new long[]{0, 300, 100, 300});
			channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

			//NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	private String stripHtml(String html) {
		if (html == null) return "";
		return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().trim();
	}

}
