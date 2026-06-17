package ma.oprojet.ln0wp.Services;

// Source - https://stackoverflow.com/a
// Posted by Thomas Moerman
// Retrieved 2026-01-08, License - CC BY-SA 3.0
// Modified by Benlamine Abdelmourhit

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.TypedValue;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ma.oprojet.ln0wp.utils.Logger;

public class PicassoImageGetter implements Html.ImageGetter {

    final Resources resources;
    final Picasso pablo;
    final TextView textView;
    final int imageMargin;
    final Context context;

    public PicassoImageGetter(final TextView textView, final Resources resources, final Picasso pablo, Context context) {
        this.textView = textView;
        this.resources = resources;
        this.pablo = pablo;
        this.context = context;
        this.imageMargin = dpToPx(12);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Drawable getDrawable(final String source) {
        final BitmapDrawablePlaceHolder result = new BitmapDrawablePlaceHolder();

        //noinspection deprecation
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(final Void... meh) {
                try {
                    return pablo.load(source).get();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final Bitmap bitmap) {

                try {
                    final BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);

                    int textWidth = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
                    float ratio = (float) bitmap.getHeight() / bitmap.getWidth();
                    int imageHeight = (int) (textWidth * ratio);

                    if (textWidth <= 0) {
                        textWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                    }

                    drawable.setBounds(imageMargin, imageMargin, textWidth - imageMargin, imageHeight);

                    result.setDrawable(drawable);
                    result.setBounds(imageMargin, imageMargin, textWidth - imageMargin, imageHeight);

                    textView.setText(textView.getText()); // invalidate() doesn't work correctly...
                } catch (Exception e) {
                    // {B.A}
                    Logger.e("DRAWABLE", e.toString(), e);
                    Logger.d("Something wrong with the drawable in the PicassoImageGetter class !");
                }
            }

        }.execute((Void) null);

        return result;
    }

    static class BitmapDrawablePlaceHolder extends BitmapDrawable {

        protected Drawable drawable;

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

    }
}
