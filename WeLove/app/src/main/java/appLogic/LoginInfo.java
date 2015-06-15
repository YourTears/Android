package appLogic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Long on 4/21/2015.
 */
public class LoginInfo {
    public String sys_id;
    public String id;
    public String name;
    public String phone;

    private static LoginInfo m_instance = null;

    private LoginInfo(){
        id = "longztc";
    }

    public static LoginInfo getInstance()
    {
        if(m_instance == null)
            m_instance = new LoginInfo();

        return m_instance;
    }
}