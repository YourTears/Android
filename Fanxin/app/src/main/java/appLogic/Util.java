package appLogic;

import appLogic.enums.FriendStatus;
import appLogic.enums.Gender;

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

    public static FriendStatus parseFriendStatus(int status){
        if(status == 0)
            return FriendStatus.pendingAccept;
        else if(status == 1)
            return FriendStatus.pendingAccepted;
        else if(status == 2)
            return FriendStatus.blocked;
        else
            return FriendStatus.friend;
    }
}
