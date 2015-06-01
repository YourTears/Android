package appLogic;

/**
 * Created by Long on 4/27/2015.
 */
public class Util {
    public static FriendInfo.Gender parseGender(int gender) {
        if (gender == 0)
            return FriendInfo.Gender.Female;
        else
            return FriendInfo.Gender.Male;
    }

    public static FriendInfo.FriendStatus parseFriendStatus(int status){
        if(status == 0)
            return FriendInfo.FriendStatus.Friend;
        else if(status == 1)
            return FriendInfo.FriendStatus.PendingRequest;
        else if(status == 2)
            return FriendInfo.FriendStatus.ToAccept;
        else if(status == 3)
            return FriendInfo.FriendStatus.PendingAccepted;
        else
            return FriendInfo.FriendStatus.Blocked;
    }
}
