package common;

import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tiazh on 4/1/2015.
 */
public class Util {
    public static Uri getImageUri(File imageFolder, String imagePath)
    {
        String imageName = MD5.getMD5(imagePath) + imagePath.substring(imagePath.lastIndexOf('.'));

        File file = new File(imageFolder, imageName);

        if(file.exists())
            return Uri.fromFile(file);
        else
        {
            try {
                URL url = new URL(imagePath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");

                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();

                return Uri.fromFile(file);
            }
            catch (Exception e)
            {
                return null;
            }
        }
    }
}