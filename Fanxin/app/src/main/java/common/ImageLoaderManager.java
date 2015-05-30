package common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import appLogic.AppConstant;

/**
 * Created by Long on 5/28/2015.
 */
public class ImageLoaderManager {
    private final int MaxThreadCount = 10;
    private ExecutorService threadPools = null;
    private BitmapMemoryCache bitmapMemoryCache;
    private String imageCacheFolder = null;

    public ImageLoaderManager() {
        threadPools = Executors.newFixedThreadPool(MaxThreadCount);
        bitmapMemoryCache = new BitmapMemoryCache();
        imageCacheFolder = AppConstant.cacheFolder + "/images/";
    }

    public void loadImage(ImageView imageView, String cacheId, String imageUrl) {
        loadImage(imageView, cacheId, imageUrl, CacheMode.No);
    }

    public void loadImage(ImageView imageView, String cacheId, String imageUrl, boolean forceRefresh) {
        loadImage(imageView, cacheId, imageUrl, CacheMode.No, forceRefresh);
    }

    public void loadImage(ImageView imageView, String cacheId, String imageUrl, CacheMode cacheMode) {
        loadImage(imageView, cacheId, imageUrl, cacheMode, false);
    }

    public void loadImage(ImageView imageView, String cacheId, String imageUrl, CacheMode cacheMode, boolean forceRefresh) {
        loadImage(imageView, cacheId, imageUrl, cacheMode, forceRefresh, new ImageDownloadedCallBack() {

            @Override
            public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
                if (imageView != null && bitmap != null)
                    imageView.setImageBitmap(bitmap);
            }
        });
    }

    private void loadImage(final ImageView imageView, final String cacheId, final String imageUrl, final CacheMode cacheMode, final boolean forceRefresh,
                          final ImageDownloadedCallBack imageDownloadedCallBack) {
        Bitmap bitmap = null;

        if(!forceRefresh) {
            if (cacheMode == CacheMode.Memory) {
                bitmap = bitmapMemoryCache.getBitmap(cacheId);
            }

            if(bitmap == null && (cacheMode == CacheMode.Memory || cacheMode == CacheMode.File)) {
                File file = new File(AppConstant.imageFolder + cacheId + AppConstant.imageExtension);
                if (file.exists()) {
                    bitmap = BitmapFactory.decodeFile(file.getPath());
                }
            }

            if(cacheMode == CacheMode.Cache){
                File file = new File(imageCacheFolder + cacheId + AppConstant.imageExtension);
                if (file.exists()) {
                    bitmap = BitmapFactory.decodeFile(file.getPath());
                }
            }

            if (bitmap != null) {
                imageDownloadedCallBack.onImageDownloaded(imageView, bitmap);
                return;
            }
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    imageDownloadedCallBack.onImageDownloaded(imageView, (Bitmap) msg.obj);
                }
            };

            Thread thread = new Thread() {
                @Override
                public void run() {
                    Bitmap bitmap = Util.downloadImage(imageUrl);
                    Bitmap compressedBitmap = Util.compressBitmap(bitmap);
                    Message msg = new Message();
                    msg.obj = compressedBitmap;
                    handler.sendMessage(msg);

                    if(cacheMode == CacheMode.Memory || cacheMode == CacheMode.CacheAndSave) {
                        bitmapMemoryCache.addBitmap(cacheId, compressedBitmap);
                    }

                    if(cacheMode == CacheMode.Memory || cacheMode == CacheMode.File || cacheMode == CacheMode.CacheAndSave) {
                        String filePath = AppConstant.imageFolder + cacheId + AppConstant.imageExtension;
                        Util.saveBitmapToFile(compressedBitmap, filePath);
                    }

                    if(cacheMode == CacheMode.Cache || cacheMode == CacheMode.CacheAndSave){
                        String filePath = imageCacheFolder + cacheId + AppConstant.imageExtension;
                        Util.saveBitmapToFile(bitmap, filePath);
                    }
                }
            };

            threadPools.execute(thread);
        }

        return;
    }

    public interface ImageDownloadedCallBack {
        void onImageDownloaded(ImageView imageView, Bitmap bitmap);
    }

    public enum CacheMode {
        Memory,
        File,
        Cache,
        CacheAndSave,
        No
    }
}