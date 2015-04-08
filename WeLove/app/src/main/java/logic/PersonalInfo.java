package logic;

import android.util.Xml;

import com.example.blind.welove.Constant;

import org.xmlpull.v1.XmlPullParser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import common.Gender;

/**
 * Created by tiazh on 4/5/2015.
 */
public class PersonalInfo {
    public String id;
    public String name;
    public String nickName;
    public String imageUrl;
    public Gender gender;

    public PersonalInfo() {
        id = null;
        name = null;
        nickName = null;
        imageUrl = null;
    }

    private String m_imageLocalPath = null;

    public String getImageLocalPath()
    {
        if(m_imageLocalPath != null)
            return m_imageLocalPath;

        if(id == null || imageUrl == null)
            return null;

        m_imageLocalPath = Constant.imageFolder + id + imageUrl.substring(imageUrl.lastIndexOf('.'));
        return m_imageLocalPath;
    }

    public static PersonalInfo getPersonalInfo(String filePath) {
        PersonalInfo info = null;

        FileInputStream is = null;

        try {
            is = new FileInputStream(filePath);
            info = getPersonalInfo(is);

            is.close();
        } catch (Exception e) {

        }

        return info;
    }

    public static PersonalInfo getPersonalInfo(InputStream is) {
        PersonalInfo info = null;

        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("info")) {
                        info = new PersonalInfo();
                        info.id = parser.getAttributeValue(0);
                    } else if (parser.getName().equals("name"))
                        info.name = parser.nextText();
                    else if (parser.getName().equals("imageUrl"))
                        info.imageUrl = parser.nextText();
                    else if(parser.getName().equals("gender"))
                        info.gender = Gender.valueOf(parser.nextText());
                }
            }
        } catch (Exception e) {

        }

        return info;
    }

    public static List<PersonalInfo> getPersonalInfos(String filePath) {
        List<PersonalInfo> infos = null;

        FileInputStream is = null;

        try {
            is = new FileInputStream(filePath);
            infos = getPersonalInfos(is);

            is.close();
        } catch (Exception e) {

        }

        return infos;
    }

    public static List<PersonalInfo> getPersonalInfos(InputStream is) {
        List<PersonalInfo> infos = new ArrayList<PersonalInfo>();
        PersonalInfo info = null;

        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("info")) {
                        info = new PersonalInfo();
                        info.id = parser.getAttributeValue(0);
                    } else if (parser.getName().equals("name"))
                        info.name = parser.nextText();
                    else if (parser.getName().equals("imageUrl"))
                        info.imageUrl = parser.nextText();
                    else if(parser.getName().equals("gender"))
                        info.gender = Gender.valueOf(parser.nextText());
                }
                else if(eventType == XmlPullParser.END_TAG)
                {
                    if(parser.getName().equals("info")) {
                        if(info != null)
                            infos.add(info);
                    }
                }
            }
        } catch (Exception e) {

        }

        return infos;
    }
}