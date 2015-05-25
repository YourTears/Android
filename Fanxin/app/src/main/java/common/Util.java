package common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static void createFolder(String folderPath)
    {
        File file = new File(folderPath);
        if(!file.exists())
            file.mkdir();
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

    public static Bitmap downloadImage(String imageUrl, String imageLocalPath, boolean forceRefresh) {
        File file = new File(imageLocalPath);

        if (forceRefresh || !file.exists()) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    InputStream is = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                }
            } catch (Exception e) {
                Log.i("DownloadImage", e.getMessage());
                return null;
            }
        }

        try {
            return BitmapFactory.decodeFile(imageLocalPath);
        } catch (Exception e) {
            Log.i("DownloadImage", e.getMessage());
            return null;
        }
    }

//    private void hideKeyboard(Context context) {
//        if (context.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
//            if (getCurrentFocus() != null)
//                manager.hideSoftInputFromWindow(getCurrentFocus()
//                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }
}
