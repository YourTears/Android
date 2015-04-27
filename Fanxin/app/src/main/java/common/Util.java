package common;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by long on 4/27/2015.
 */
public class Util {
    final static int BUFFER_SIZE = 4096;

    public static String convertToString(InputStream is)
    {
        String s = null;
        if(is == null)
            return s;

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int count = -1;
            while ((count = is.read(data, 0, BUFFER_SIZE)) != -1)
                outStream.write(data, 0, count);

            data = null;
            s =  new String(outStream.toByteArray());
        }
        catch (IOException e) {
            Log.v("", e.getMessage());
        }

        return s;
    }

    public static InputStream getAssertInputStream(AssetManager assertManager, String relativeFilePath)
    {
        try {
            return assertManager.open(relativeFilePath);
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
