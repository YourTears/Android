package appLogic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.welove.adapter.ContactAdapter;
import com.welove.database.UserTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import common.HanziToPinyin;

/**
 * Created by Long on 4/29/2015.
 */
public class UserManager {
    public List<UserInfo> friends;
    public List<UserInfo> pendingFriends;
    public ContactAdapter adapter;

    public int notificationCount = 0;

    private HashSet<String> blockIds;
    private HashMap<String, UserInfo> friendMapping;
    private HashMap<String, UserInfo> pendingFriendMapping;
    private UserTable userTable;

    private UserManager(Context context) {
        friends = new ArrayList<UserInfo>();
        pendingFriends = new ArrayList<UserInfo>();
        blockIds = new HashSet<String>();
        friendMapping = new HashMap<String, UserInfo>();
        pendingFriendMapping = new HashMap<String, UserInfo>();

        adapter = new ContactAdapter(context, friends, pendingFriends);

        userTable = UserTable.getInstance(context);

        for(UserInfo user : userTable.getFriends()){
            if(user.nickName == null || user.nickName.isEmpty()){
                user.nickName = user.name;
            }
            user.name_pinyin = HanziToPinyin.getPinYin(user.nickName);

            if (user.friendStatus != UserInfo.FriendStatus.Blocked) {
                if (user.friendStatus == UserInfo.FriendStatus.Friend) {
                    friends.add(user);
                    friendMapping.put(user.id, user);
                } else {
                    pendingFriends.add(user);
                    pendingFriendMapping.put(user.id, user);
                }
            } else {
                blockIds.add(user.id);
            }
        }

        Collections.sort(friends, new UserManager.PinyinComparator());
    }

    public void refreshListView(){
        adapter.notifyDataSetChanged();
    }

    private static UserManager instance = null;

    public static UserManager getInstance(Context context) {
        if (instance == null)
            instance = new UserManager(context);

        return instance;
    }

    public UserInfo getUser(String id) {
        if (friendMapping.containsKey(id)) {
            return friendMapping.get(id);
        }

        if (pendingFriendMapping.containsKey(id)) {
            return pendingFriendMapping.get(id);
        }

        return null;
    }

    public boolean containFriend(String id) {
        return friendMapping.containsKey(id);
    }

    public int getFriendCount() {
        return friends.size() + pendingFriends.size();
    }

    public boolean isBlocked(String id) {
        return blockIds.contains(id);
    }

    public synchronized void acceptFriendInvitation(String userId) {
        UserInfo user = getUser(userId);
        if (user != null && user.friendStatus == UserInfo.FriendStatus.ToAccept) {
            try {
                user.friendStatus = UserInfo.FriendStatus.Friend;

                pendingFriends.remove(user);
                pendingFriendMapping.remove(userId);

                friends.add(user);
                friendMapping.put(userId, user);
                Collections.sort(friends, new UserManager.PinyinComparator());

                adapter.notifyDataSetChanged();
                userTable.addOrReplaceFriend(user);
            } catch (Exception e) {
                Log.e("加好友失败", e.getMessage());
            }
        }
    }

    public synchronized void deleteUser(String userId) {
        UserInfo user = null;

        if (friendMapping.containsKey(userId)) {
            user = friendMapping.get(userId);
            friends.remove(user);

            AppConstant.conversationManager.deleteConversation(userId);
        }

        if (pendingFriendMapping.containsKey(userId)) {
            user = pendingFriendMapping.get(userId);
            pendingFriends.remove(user);
        }

        if (user != null) {
            adapter.notifyDataSetChanged();

            userTable.deleteFriend(userId);
        }
    }

    public synchronized void updateUserInfo(String userId){
        UserInfo user = getUser(userId);

        if(user != null){
            userTable.addOrReplaceFriend(user);
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
                    UserInfo user = getFriendInfo(array.getJSONObject(idx));
                    if(user.nickName == null || user.nickName.isEmpty()){
                        user.nickName = user.name;
                    }
                    user.name_pinyin = HanziToPinyin.getPinYin(user.nickName);

                    if (user != null) {
                        if (user.friendStatus != UserInfo.FriendStatus.Blocked) {
                            if (user.friendStatus == UserInfo.FriendStatus.Friend) {
                                friends.add(user);
                                friendMapping.put(user.id, user);
                            } else {
                                pendingFriends.add(user);
                                pendingFriendMapping.put(user.id, user);
                            }
                        } else {
                            blockIds.add(user.id);
                        }

                        userTable.addOrReplaceFriend(user);
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

    private UserInfo getFriendInfo(JSONObject json) {
        UserInfo friend = new UserInfo();

        try {
            friend.sys_id = json.getString("sys_id");
            friend.id = json.getString("id");
            friend.gender = UserInfo.parseGender(json.getInt("gender"));
            friend.name = json.getString("name");
            friend.imageUrl = json.getString("imageUrl");
            friend.friendStatus = UserInfo.parseFriendStatus(json.getInt("friendStatus"));

        } catch (JSONException e) {
            return null;
        }

        return friend;
    }

    @SuppressLint("DefaultLocale")
    public class PinyinComparator implements Comparator<UserInfo> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(UserInfo f1, UserInfo f2) {
            String py1 = f1.name_pinyin;
            String py2 = f2.name_pinyin;

            if (isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;

            return py1.compareTo(py2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }

    @SuppressLint("DefaultLocale")
    public class TimeComparator implements Comparator<UserInfo> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(UserInfo f1, UserInfo f2) {

            return 0;
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }
}