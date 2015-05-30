package common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by long on 4/27/2015.
 */
public class Util {
    final static int BUFFER_SIZE = 4096;

    public static String convertToString(InputStream is) {
        String s = null;
        if (is == null)
            return s;

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int count = -1;
            while ((count = is.read(data, 0, BUFFER_SIZE)) != -1)
                outStream.write(data, 0, count);

            data = null;
            s = new String(outStream.toByteArray());
        } catch (IOException e) {
            Log.v("", e.getMessage());
        }

        return s;
    }

    public static InputStream getAssertInputStream(AssetManager assertManager, String relativeFilePath) {
        try {
            return assertManager.open(relativeFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists())
            file.mkdir();
    }

    public static void createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static String getAppFilePath(Context context) {
        String appFilePath = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            appFilePath = context.getExternalFilesDir("data").getPath();
        } else {
            appFilePath = context.getFilesDir().getPath();
        }

        return appFilePath;
    }

    public static String getAppCachePath(Context context) {
        String appCachePath = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            appCachePath = context.getExternalCacheDir().getPath();
        } else {
            appCachePath = context.getCacheDir().getPath();
        }

        return appCachePath;
    }

    public static Bitmap downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();

                return BitmapFactory.decodeStream(is);
            }
        } catch (Exception e) {
            Log.i("DownloadImage", e.getMessage());
            return null;
        }

        return null;
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, String filePath) {
        createFile(filePath);

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(filePath);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
                return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        deleteFile(filePath);
        return false;
    }

    public static Bitmap compressBitmap(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        while (stream.toByteArray().length / 1024 > 100) {
            stream.reset();
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, stream);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(stream.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }
}
