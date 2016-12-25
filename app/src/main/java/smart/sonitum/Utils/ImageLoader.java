package smart.sonitum.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import smart.sonitum.R;

public class ImageLoader extends AsyncTask<String, Integer, Bitmap> {
    private final WeakReference<ImageView> viewWeakReference;
    private Context ctx;

    public ImageLoader(ImageView imageView, Context ctx) {
        viewWeakReference = new WeakReference<>(imageView);
        this.ctx = ctx;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return loadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
        ImageView imageView = viewWeakReference.get();
        if (imageView != null)
            imageView.setImageBitmap(bmp);
    }

    private Bitmap loadBitmap(String path) {
        Bitmap bmp = BitmapFactory.decodeFile(path);
        if (bmp == null)
            bmp = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_album);
        return bmp;
    }
}
