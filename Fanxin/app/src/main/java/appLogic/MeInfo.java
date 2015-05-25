package appLogic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import appLogic.enums.Gender;

/**
 * Created by long on 4/21/2015.
 */
public class MeInfo {
    public String sys_id;
    public String id;
    public String name;
    public String imageUrl;
    public String phone;
    public Gender gender;

    private static MeInfo m_instance = null;

    private MeInfo()
    {

    }

    public static MeInfo getInstance()
    {
        if(m_instance == null)
            m_instance = new MeInfo();

        return m_instance;
    }

    public static boolean getMeInfo(InputStream is) {
        if(is == null)
            return false;

        String s = common.Util.convertToString(is);
        if (s == null)
            return false;

        try {
            JSONObject json = new JSONObject(s);

            getInstance().sys_id = json.getString("sys_id");
            getInstance().id = json.getString("id");
            getInstance().name = json.getString("name");
            getInstance().phone = json.getString("phone");
            getInstance().gender = appLogic.Util.parseGender(json.getInt("gender"));
            getInstance().imageUrl = json.getString("imageUrl");

        } catch (JSONException e) {
            return false;
        }

        return true;
    }
}
