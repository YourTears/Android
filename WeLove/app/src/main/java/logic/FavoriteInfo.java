package logic;

import android.util.Xml;

import com.example.blind.welove.Constant;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import common.Gender;

/**
 * Created by tiazh on 4/10/2015.
 */
public class FavoriteInfo {
    List<String> favoriteId = null;

    public FavoriteInfo()
    {
        favoriteId = new ArrayList<String>();
        favoriteId.add(Constant.meInfo.id);
    }

    public void addToFavorite(String id)
    {
        if(favoriteId.contains(id))
            return;

        favoriteId.add(id);
    }

    public void removeFromFavorite(String id)
    {
        if(id == Constant.meInfo.id || !favoriteId.contains(id))
            return;

        favoriteId.remove(id);
    }

    public boolean isFavorite(String id)
    {
        return favoriteId.contains(id);
    }

    public static FavoriteInfo getFavoriteInfo(InputStream is)
    {
        FavoriteInfo info = new FavoriteInfo();

        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("id"))
                        info.addToFavorite(parser.nextText());
                }
            }
        } catch (Exception e) {

        }

        return info;
    }
}
