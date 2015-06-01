package appLogic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by tiazh on 4/29/2015.
 */
public class FriendManager {
    public List<FriendInfo> friends;
    public List<FriendInfo> pendingFriends;
    public HashSet<String> blockIds;

    private HashMap<String, FriendInfo> friendMapping;

    private FriendManager()
    {
        init();
    }

    private static FriendManager instance = null;

    public static FriendManager getInstance()
    {
        if(instance == null)
            instance = new FriendManager();

        return instance;
    }

    public FriendInfo getFriend(String id)
    {
        if(!friendMapping.containsKey(id))
            return null;

        return friendMapping.get(id);
    }

    public int getFriendCount()
    {
        return friends.size() + pendingFriends.size();
    }

    public boolean isBlocked(String id)
    {
        return blockIds.contains(id);
    }

    public boolean refresh(InputStream is)
    {
        init();

        String s = common.Util.convertToString(is);
        if (s == null)
            return false;

        try {
            JSONObject json = new JSONObject(s);

            if(json.has("users")) {
                JSONArray array = json.getJSONArray("users");

                for(int idx = 0; idx < array.length(); idx ++)
                {
                    FriendInfo friend = getFriendInfo(array.getJSONObject(idx));

                    if(friend != null) {
                        if(friend.friendStatus != FriendInfo.FriendStatus.Blocked){
                            if(friend.friendStatus == FriendInfo.FriendStatus.Friend)
                                friends.add(friend);
                            else
                                pendingFriends.add(friend);
                            friendMapping.put(friend.id, friend);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    private void init()
    {
        friends = new ArrayList<FriendInfo>();
        pendingFriends = new ArrayList<FriendInfo>();
        blockIds = new HashSet<String>();
        friendMapping = new HashMap<String, FriendInfo>();
    }

    private FriendInfo getFriendInfo(JSONObject json)
    {
        FriendInfo friend = new FriendInfo();

        try{
            friend.sys_id = json.getString("sys_id");
            friend.id = json.getString("id");
            friend.gender = appLogic.Util.parseGender(json.getInt("gender"));
            friend.name = json.getString("name");
            friend.imageUrl = json.getString("imageUrl");
            friend.friendStatus = appLogic.Util.parseFriendStatus(json.getInt("friendStatus"));

        } catch (JSONException e) {
            return null;
        }

        return friend;
    }
}
