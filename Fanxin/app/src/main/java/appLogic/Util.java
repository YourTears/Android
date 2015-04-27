package appLogic;

/**
 * Created by tiazh on 4/27/2015.
 */
public class Util {
    public static Gender parseGender(int gender) {
        if (gender == 0)
            return Gender.female;
        else
            return Gender.male;
    }
}
