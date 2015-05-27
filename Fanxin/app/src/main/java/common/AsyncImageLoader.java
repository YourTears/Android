package common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by Long on 5/10/2015.
 */
public class AsyncImageLoader extends AsyncTask<String, Integer, Bitmap> {
    private ImageView m_imageView;
    private boolean m_forceRefresh = false;

    public AsyncImageLoader(ImageView imageView)
    {
        m_imageView = imageView;
    }

    public AsyncImageLoader(ImageView imageView,boolean forceRefresh)
    {
        m_imageView = imageView;
        m_forceRefresh = forceRefresh;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return Util.downloadImage(params[0], params[1], m_forceRefresh);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result)
    {
        if(m_imageView != null && result != null) {
            m_imageView.setImageBitmap(result);
        }
    }
}

