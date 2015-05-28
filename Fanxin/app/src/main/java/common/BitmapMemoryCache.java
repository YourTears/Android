package common;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Long on 5/28/2015.
 */
public class BitmapMemoryCache {
    private LruCache<String, Bitmap> bitmapCache;

    public BitmapMemoryCache(){
        int maxMemory = (int) (Runtime.getRuntime() .maxMemory() / 1024);
        bitmapCache = new LruCache<>(maxMemory / 8);
    }

    public synchronized void addBitmap(String key, Bitmap bitmap) {
        if(key != null && bitmap != null){
            if(getBitmap(key) != null)
                removeBitmap(key);

            bitmapCache.put(key, bitmap);
        }
    }

    public synchronized Bitmap getBitmap(String key){
        if(key == null)
            return null;

        return bitmapCache.get(key);
    }

    public synchronized void removeBitmap(String key) {
        if (key != null) {
            if (bitmapCache != null) {
                Bitmap bm = bitmapCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }
}
