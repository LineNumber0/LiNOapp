package ma.oprojet.ln0wp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ma.oprojet.ln0wp.Models.Media;
import ma.oprojet.ln0wp.Models.Post;
import ma.oprojet.ln0wp.Services.FtiarService;
import ma.oprojet.ln0wp.utils.Logger;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ArchivePostAdaptater extends RecyclerView.Adapter<ArchivePostAdaptater.ArchivePostsViewHolder> {

    // Best practice to display date-time. {B.A}
    public static String formatPostDate(Context context, String postDate) {
        try {
            SimpleDateFormat parser =
                    new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            Locale.US
                    );
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date date = parser.parse(postDate);

            DateFormat formatter =
                    DateFormat.getDateTimeInstance(
                            DateFormat.FULL,
                            DateFormat.SHORT
                    );

            return formatter.format(date);

        } catch (Exception e) {
            return "";
        }
    }

    public static int getAverageColor(Bitmap bitmap) {
        int r = 0, g = 0, b = 0;
        int count = 0;

        for (int y = 0; y < bitmap.getHeight(); y += 10) {
            for (int x = 0; x < bitmap.getWidth(); x += 10) {
                int c = bitmap.getPixel(x, y);
                r += Color.red(c);
                g += Color.green(c);
                b += Color.blue(c);
                count++;
            }
        }

        return Color.rgb(r / count, g / count, b / count);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ArchivePostsViewHolder extends RecyclerView.ViewHolder {
        public Target target;
        // each data item is just a string in this case
        CardView cv;
        TextView postTitle;
        TextView postDate;
        ImageView postPhoto;
        Button postReadMoreBtn;

        ArchivePostsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.post_card);
            postTitle = (TextView)itemView.findViewById(R.id.post_title);
            postDate = (TextView)itemView.findViewById(R.id.post_date);
            postPhoto = (ImageView)itemView.findViewById(R.id.post_photo);
            postReadMoreBtn = (Button)itemView.findViewById(R.id.readMoreBtn);
        }
    }

    private List<Post> posts;

    private FtiarService ftiarService = new RestAdapter.Builder()
            .setEndpoint(FtiarService.ENDPOINT)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .build()
            .create(FtiarService.class);

    private Context context;

    public ArchivePostAdaptater(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArchivePostsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_posts, viewGroup, false);
        return new ArchivePostsViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder( final ArchivePostsViewHolder holder, int i ) {
        final Context ctxt = this.context; // Save the context locally
        final Post currentPost = this.posts.get(i);

        if ( i == 0 ) {
            // Restore preferences
            SharedPreferences settings = ctxt.getSharedPreferences("myShare", ctxt.MODE_PRIVATE);
            int last_post_id = settings.getInt("last_post_id", 0);

            if ( last_post_id != currentPost.getId() ) {
                // We need an Editor object to make preference changes.
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("last_post_id", currentPost.getId());

                // Commit the edits!
                editor.apply();

                if ( last_post_id != 0 ) {
                    // Push notification goes here
                    NotificationCompat.Builder mBuilder =
                            (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                                .setContentTitle(context.getString(R.string.NewPost))
                                    .setSmallIcon(R.drawable.ic_notification_foreground)
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setContentText(currentPost.getTitle().getRendered())
                                    .setPriority(NotificationCompat.PRIORITY_HIGH);
                    // Sets an ID for the notification
                    int mNotificationId = 001;
                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());
                }

            }
        }

        holder.postTitle.setText( currentPost.getTitle().getRendered() );

        // Format the post date
        String postDate = currentPost.getDate();
        //holder.postDate.setText( currentPost.getI18nFormatedDate(postDate, "dd MMMM yyyy", Locale.FRANCE) );
        // {B.A}
        holder.postDate.setText( formatPostDate(ctxt, postDate) );
        // {B.A} Set the Background color of the ImageView and CardView from the average color of the post image.
        holder.target = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.postPhoto.setImageBitmap(bitmap);

                int avgColor = getAverageColor(bitmap);
                holder.cv.setCardBackgroundColor(avgColor);
                holder.postPhoto.setBackgroundColor(avgColor);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Logger.d("onBitmapFailed error : " + errorDrawable + e);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Logger.d("onPrepareLoad : " + placeHolderDrawable);
            }
        };


        // Get Media Object with a call to the REST API
        this.ftiarService.getMediaAsync(currentPost.getFeatured_media(), new Callback<Media>() {

            @Override
            public void success(Media media, Response response) {
                // Use Picasso lib to display an Image based on an URL
            try {
                Picasso.get().load(media.getMedia_details().getSizes().getLarge().getSource_url()).into(holder.target);
                Picasso.get().load(media.getMedia_details().getSizes().getLarge().getSource_url()).into(holder.postPhoto);
            } catch (NullPointerException e) {
                Picasso.get().load(R.drawable.kbd_bw).into(holder.postPhoto);
            }

        }

            @Override
            public void failure(RetrofitError error) {
                Logger.d("RETROFIT_ERROR", error.toString() );
            }
        });

        // Handle read more click
        holder.postReadMoreBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Launch a single post activity
                Intent intentSinglePost = new Intent( ctxt, SinglePostActivity.class );
                intentSinglePost.putExtra("post_id", currentPost.getId() );
                intentSinglePost.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctxt.startActivity(intentSinglePost);
                Toast.makeText(ctxt, ctxt.getString(R.string.enjoyRead), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.posts.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}