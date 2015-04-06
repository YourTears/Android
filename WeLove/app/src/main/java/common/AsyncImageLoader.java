package common;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by tiazh on 4/1/2015.
 */
public class AsyncImageLoader extends AsyncTask<String, Integer, Uri> {
    private ImageView m_imageView;

    public AsyncImageLoader(ImageView imageView)
    {
        m_imageView = imageView;
    }

    @Override
    protected Uri doInBackground(String... params) {
        try {
            return Util.getImageUri(params[0], params[1]);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Uri result)
    {
        if(m_imageView != null && result != null)
            m_imageView.setImageURI(result);
    }
}
