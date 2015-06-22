package appLogic;

/**
 * Created by Long on 4/21/2015.
 */
public class UserInfo {
    public String sys_id;
    public String id;
    public String externalId;
    public String name;
    public String name_pinyin;
    public String nickName;
    public String imageUrl;
    public Gender gender;
    public FriendStatus friendStatus = FriendStatus.Friend;
    public String sign;
    public int regionProvinceId;
    public int regionCityId;
    public int homeProvinceId;
    public int homeCityId;
    public String region;
    public String home;

    public enum FriendStatus {
        Friend,
        PendingRequest,
        ToAccept,
        PendingAccepted,
        Blocked
    }

    public static enum Gender {
        Male,
        Female
    }

    public static Gender parseGender(int gender) {
        if (gender == 0)
            return Gender.Female;
        else
            return Gender.Male;
    }

    public static FriendStatus parseFriendStatus(int status){
        if(status == 0)
            return FriendStatus.Friend;
        else if(status == 1)
            return FriendStatus.PendingRequest;
        else if(status == 2)
            return FriendStatus.ToAccept;
        else if(status == 3)
            return FriendStatus.PendingAccepted;
        else
            return FriendStatus.Blocked;
    }

    public static int parseGender(Gender gender) {
        if(gender == Gender.Female)
            return 0;
        else
            return 1;
    }

    public static int parseFriendStatus(FriendStatus status){
        if(status == FriendStatus.Friend)
            return 0;
        else if(status == FriendStatus.PendingRequest)
            return 1;
        else if(status == FriendStatus.ToAccept)
            return 2;
        else if(status == FriendStatus.PendingAccepted)
            return 3;
        else
            return 4;
    }
}
