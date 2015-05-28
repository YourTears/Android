package common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.fanxin.app.fx.others.HTTPService;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Long on 5/28/2015.
 */
public class ImageLoaderManager {
    private final int MaxThreadCount = 10;
    private ExecutorService threadPools = null;
    private BitmapMemoryCache bitmapMemoryCache;

    public ImageLoaderManager(){
        threadPools = Executors.newFixedThreadPool(MaxThreadCount);
        bitmapMemoryCache = new BitmapMemoryCache();
    }

    public void loadImage(ImageView imageView, String cacheId, String imageUrl){
        loadImage(imageView, cacheId, imageUrl, CacheMode.No);
    }

    public void loadImage(ImageView imageView, String cacheId, String imageUrl, CacheMode cacheMode){
        loadImage(imageView, cacheId, imageUrl, cacheMode, new ImageDownloadedCallBack() {

            @Override
            public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
                if (imageView != null && bitmap != null)
                    imageView.setImageBitmap(bitmap);
            }
        });
    }

    private void loadImage(final ImageView imageView, final String cacheId, final String imageUrl, final CacheMode cacheMode,
                             final ImageDownloadedCallBack imageDownloadedCallBack){
        Bitmap bitmap = null;

        if(cacheMode == CacheMode.MEMORY){
            bitmap = bitmapMemoryCache.getBitmap(cacheId);

            if(bitmap != null){
                imageDownloadedCallBack.onImageDownloaded(imageView, bitmap);
                return;
            }
        }

        if(imageUrl != null && !imageUrl.isEmpty()){
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    imageDownloadedCallBack.onImageDownloaded(imageView, (Bitmap) msg.obj);
                }
            };

            Thread thread = new Thread() {
                @Override
                public void run() {
                    InputStream inputStream = HTTPService.getInstance().getStream(imageUrl);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 5;

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    bitmapMemoryCache.addBitmap(cacheId, bitmap);

                    imageDownloadedCallBack.onImageDownloaded(imageView, bitmap);
                }
            };

            threadPools.execute(thread);
        }

        return;
    }

    public interface ImageDownloadedCallBack {
        void onImageDownloaded(ImageView imageView, Bitmap bitmap);
    }

    public enum CacheMode{
        MEMORY,
        File,
        No
    }
}
