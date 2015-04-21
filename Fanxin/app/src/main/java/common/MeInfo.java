package common;

/**
 * Created by tiazh on 4/21/2015.
 */
public class MeInfo {
    private MeInfo()
    {

    }

    private static MeInfo m_instance = null;

    public static MeInfo getInstance()
    {
        if(m_instance == null)
            m_instance = new MeInfo();

        return m_instance;
    }

    public String sys_id;
    public String id;
    public String name;
    public String imageUrl;
    public Gender gender;
}
