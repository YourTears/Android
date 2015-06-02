package appLogic;

import android.content.Context;
import android.util.Log;

import com.fanxin.adapter.ContactAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Long on 4/29/2015.
 */
public class FriendManager {
    public List<FriendInfo> friends;
    public List<FriendInfo> pendingFriends;
    public ContactAdapter adapter;

    private HashSet<String> blockIds;
    private HashMap<String, FriendInfo> friendMapping;
    private HashMap<String, FriendInfo> pendingFriendMapping;

    private FriendManager(Context context) {
        friends = new ArrayList<FriendInfo>();
        pendingFriends = new ArrayList<FriendInfo>();
        blockIds = new HashSet<String>();
        friendMapping = new HashMap<String, FriendInfo>();
        pendingFriendMapping = new HashMap<String, FriendInfo>();

        adapter = new ContactAdapter(context, friends, pendingFriends);
    }

    private static FriendManager instance = null;

    public static FriendManager getInstance(Context context) {
        if (instance == null)
            instance = new FriendManager(context);

        return instance;
    }

    public FriendInfo getFriend(String id) {
        if (friendMapping.containsKey(id)) {
            return friendMapping.get(id);
        }

        if (pendingFriendMapping.containsKey(id)) {
            return pendingFriendMapping.get(id);
        }

        return null;
    }

    public int getFriendCount() {
        return friends.size() + pendingFriends.size();
    }

    public boolean isBlocked(String id) {
        return blockIds.contains(id);
    }

    public synchronized void acceptFriendInvitation(String friendId) {
        FriendInfo friend = getFriend(friendId);
        if (friend != null && friend.friendStatus == FriendInfo.FriendStatus.ToAccept) {
            try {
                pendingFriends.remove(friend);
                pendingFriendMapping.remove(friendId);

                friends.add(friend);
                friendMapping.put(friendId, friend);

                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("加好友失败", e.getMessage());
            }
        }
    }

    public boolean refresh(InputStream is) {
        String s = common.Util.convertToString(is);
        if (s == null)
            return false;

        try {
            JSONObject json = new JSONObject(s);

            if (json.has("users")) {
                JSONArray array = json.getJSONArray("users");

                for (int idx = 0; idx < array.length(); idx++) {
                    FriendInfo friend = getFriendInfo(array.getJSONObject(idx));

                    if (friend != null) {
                        if (friend.friendStatus != FriendInfo.FriendStatus.Blocked) {
                            if (friend.friendStatus == FriendInfo.FriendStatus.Friend) {
                                friends.add(friend);
                                friendMapping.put(friend.id, friend);
                            } else {
                                pendingFriends.add(friend);
                                pendingFriendMapping.put(friend.id, friend);
                            }
                        } else {
                            blockIds.add(friend.id);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            return false;
        } finally {
            adapter.notifyDataSetChanged();
        }

        return true;
    }

    private FriendInfo getFriendInfo(JSONObject json) {
        FriendInfo friend = new FriendInfo();

        try {
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