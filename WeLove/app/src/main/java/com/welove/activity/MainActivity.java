package com.welove.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.welove.app.R;
import com.welove.broadcast.UpdateInfoService;
import com.welove.view.FragmentConversation;
import com.welove.view.FragmentFind;
import com.welove.view.FragmentFriends;
import com.welove.view.FragmentProfile;

import common.LoadDataFromServer;
import common.LoadDataFromServer.DataCallBack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import appLogic.AppConstant;
import appLogic.ConversationManager;
import appLogic.LoginInfo;
import appLogic.UserInfo;
import appLogic.UserManager;
import common.ImageLoaderManager;
import common.Util;

@SuppressLint("DefaultLocale")
public class MainActivity extends BroadcastActivity {
    // 未读消息textview
    private TextView unreadLabel;
    // 未读通讯录textview
    TextView unreadAddressLable;
    protected static final String TAG = "MainActivity";

    private Fragment[] fragments;
    public FragmentConversation conversationfragment;
    private FragmentFriends contactlistfragment;
    private FragmentFind findfragment;
    private FragmentProfile profilefragment;
    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private int index;
    // 当前fragment的index
    private int currentTabIndex;
    private NewMessageBroadcastReceiver msgReceiver;
    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    // 账号在别处登录
    public boolean isConflict = false;
    // 账号被移除
    private boolean isCurrentAccountRemoved = false;
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    private UpdateInfoService updateInfoService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMeInfo();

        if (savedInstanceState != null
                && savedInstanceState.getBoolean("",
                        false)) {
            // 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setContentView(R.layout.activity_main);
        initView();

        if (getIntent().getBooleanExtra("conflict", false)
                && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra("", false)
                && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }

        Intent updateInfoIntent = new Intent(MainActivity.this, UpdateInfoService.class);
        bindService(updateInfoIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                updateInfoService = ((UpdateInfoService.UpdateInfoBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                updateInfoService = null;

            }
        }, Context.BIND_AUTO_CREATE);

        initBroadcastService();
    }

    private void initBroadcastService(){
        broadServiceName = UpdateInfoService.ServiceName;
        broadcastIntent = new Intent(MainActivity.this, UpdateInfoService.class);
        broadcastReceiver = new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                boolean updateProfile = intent.getBooleanExtra(UpdateInfoService.UpdateProfile, false);
                if(updateProfile){
                    profilefragment.updateProfile();
                }

                boolean updateContactList = intent.getBooleanExtra(UpdateInfoService.UpdateContactList, false);
                if(updateContactList){
                    contactlistfragment.refreshListView();
                }

                boolean updateConversationList = intent.getBooleanExtra(UpdateInfoService.UpdateConversationList, false);
                if(updateConversationList){
                    conversationfragment.refreshListView();
                }
            }
        };
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                broadcastService = ((UpdateInfoService.UpdateInfoBinder) binder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                broadcastService = null;
            }
        };
    }

    private void initMeInfo()
    {
        AppConstant.dataFolder = Util.getAppFilePath(this);
        AppConstant.cacheFolder = Util.getAppCachePath(this);
        AppConstant.imageFolder = AppConstant.dataFolder + "/images/";

        Util.createFolder(AppConstant.dataFolder);
        Util.createFolder(AppConstant.imageFolder);
        Util.createFolder(AppConstant.cacheFolder + "/images");

        //DbOpenHelper.getInstance(this).deleteDatabase(this);

        AppConstant.userManager = UserManager.getInstance(this);

        if(AppConstant.userManager.friends.size() == 0)
            AppConstant.userManager.refresh(Util.getAssertInputStream(this.getResources().getAssets(), "friends.json"));

        AppConstant.meInfo = AppConstant.userManager.getUser(LoginInfo.getInstance().id);

        if(AppConstant.meInfo.gender == UserInfo.Gender.Female){
            AppConstant.it = "他";
        }

        AppConstant.defaultImageDrawable = getResources().getDrawable(R.drawable.default_boy_drawable);

        AppConstant.inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        AppConstant.conversationManager = new ConversationManager(this);

        AppConstant.imageLoaderManager = new ImageLoaderManager();
    }

    private void initView() {
        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
        unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);

        conversationfragment = new FragmentConversation();
        contactlistfragment = new FragmentFriends();
        findfragment = new FragmentFind();
        profilefragment = new FragmentProfile();
        fragments = new Fragment[] {findfragment, conversationfragment, contactlistfragment, profilefragment };
        imagebuttons = new ImageView[4];
        imagebuttons[0] = (ImageView) findViewById(R.id.ib_find);
        imagebuttons[1] = (ImageView) findViewById(R.id.ib_weixin);
        imagebuttons[2] = (ImageView) findViewById(R.id.ib_contact_list);
        imagebuttons[3] = (ImageView) findViewById(R.id.ib_profile);

        imagebuttons[0].setSelected(true);
        textviews = new TextView[4];
        textviews[0] = (TextView) findViewById(R.id.tv_find);
        textviews[1] = (TextView) findViewById(R.id.tv_weixin);
        textviews[2] = (TextView) findViewById(R.id.tv_contact_list);
        textviews[3] = (TextView) findViewById(R.id.tv_profile);
        textviews[0].setTextColor(0xFF45C01A);
        // 添加显示第一个fragment
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, conversationfragment)
                .add(R.id.fragment_container, contactlistfragment)
                .add(R.id.fragment_container, profilefragment)
                .add(R.id.fragment_container, findfragment)
                .hide(contactlistfragment).hide(profilefragment)
                .hide(conversationfragment).show(findfragment).commit();

        // 注册一个接收消息的BroadcastReceiver
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter();
        ackMessageIntentFilter.setPriority(3);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 注册一个透传消息的BroadcastReceiver
        IntentFilter cmdMessageIntentFilter = new IntentFilter();
        cmdMessageIntentFilter.setPriority(3);
        registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);



        // 注册一个离线消息的BroadcastReceiver
        // IntentFilter offlineMessageIntentFilter = new
        // IntentFilter(EMChatManager.getInstance()
        // .getOfflineMessageBroadcastAction());
        // registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
        case R.id.re_find:
            index = 0;
            break;
        case R.id.re_weixin:
            index = 1;
            break;
        case R.id.re_contact_list:
            index = 2;
            break;
        case R.id.re_profile:
            index = 3;
            break;

        }

        if (currentTabIndex != index) {
            FragmentTransaction trx = getFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(0xFF999999);
        textviews[index].setTextColor(0xFF45C01A);
        currentTabIndex = index;
    }

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;

        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(
                            MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                                conflictBuilder = null;
                                finish();
                                startActivity(new Intent(MainActivity.this,
                                        LoginActivity.class));
                            }
                        });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
            }

        }

    }

    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(
                            MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                                accountRemovedBuilder = null;
                                finish();
                                startActivity(new Intent(MainActivity.this,
                                        LoginActivity.class));
                            }
                        });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {

            }

        }

    }

    /**
     * 新消息广播接收者
     * 
     * 
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

        }
    }

    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
        }
    };

    /**
     * 透传消息BroadcastReceiver
     */
    private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            // EMLog.d(TAG, "收到透传消息");
            // // 获取cmd message对象
            //
            // EMMessage message = intent.getParcelableExtra("message");
            // // 获取消息body
            // CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
            // String action = cmdMsgBody.action;// 获取自定义action
            //
            // // 获取扩展属性 此处省略
            // // message.getStringAttribute("");
            // EMLog.d(TAG,
            // String.format("透传消息：action:%s,message:%s", action,
            // message.toString()));
            // String st9 = getResources().getString(
            // R.string.receive_the_passthrough);
            // Toast.makeText(MainActivity.this, st9 + action,
            // Toast.LENGTH_SHORT)
            // .show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!isConflict || !isCurrentAccountRemoved) {
            // initView();
            updateUnreadLabel();
            updateUnreadAddressLable();
        }

        IntentFilter updateInfoFilter = new IntentFilter(UpdateInfoService.ServiceName);
        registerReceiver(new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
//                int counter = intent.get
//                String text = String.valueOf(counter);
//                counterText.setText(text);

            }
        }, updateInfoFilter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean("", isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().getBooleanExtra("conflict", false)
                && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra("", false)
                && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = AppConstant.conversationManager.totalUnreadCount;
        if (count > 0) {
            unreadLabel.setText(String.valueOf(count));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = AppConstant.userManager.notificationCount;
                if (count > 0) {
                    unreadAddressLable.setText(String.valueOf(count));
                    unreadAddressLable.setVisibility(View.VISIBLE);
                } else {
                    unreadAddressLable.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    public void refreshFriendsList() {
        List<String> usernames = new ArrayList<String>();

        if (usernames != null && usernames.size() > 0) {
            String totaluser = usernames.get(0);
            for (int i = 1; i < usernames.size(); i++) {
                final String split = "66split88";
                totaluser += split + usernames.get(i);
            }
            totaluser = totaluser.replace("", "");
            totaluser = totaluser.replace("", "");

            Map<String, String> map = new HashMap<String, String>();

            map.put("uids", totaluser);

            LoadDataFromServer task = new LoadDataFromServer(MainActivity.this,
                    "", map);

            task.getData(new DataCallBack() {

                @Override
                public void onDataCallBack(JSONObject data) {
                    try {
                        int code = data.getInteger("code");
                        if (code == 1) {
                            JSONArray josnArray = data.getJSONArray("friends");

                            saveFriends(josnArray);

                        }

                    } catch (JSONException e) {
                        Log.e("MainActivity", "update friendsLiST ERROR");
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void saveFriends(JSONArray josnArray) {

//        Map<String, FriendInfo> map = new HashMap<String, FriendInfo>();
//
//        if (josnArray != null) {
//            for (int i = 0; i < josnArray.size(); i++) {
//                JSONObject json = (JSONObject) josnArray.getJSONObject(i);
//                try {
//                    String hxid = json.getString("hxid");
//                    String fxid = json.getString("fxid");
//                    String nick = json.getString("nick");
//                    String avatar = json.getString("avatar");
//                    String sex = json.getString("sex");
//                    String region = json.getString("region");
//                    String sign = json.getString("sign");
//                    String tel = json.getString("tel");
//
//                    User user = new User();
//                    user.setFxid(fxid);
//                    user.setUsername(hxid);
//                    user.setBeizhu("");
//                    user.setNick(nick);
//                    user.setRegion(region);
//                    user.setSex(sex);
//                    user.setTel(tel);
//                    user.setSign(sign);
//                    user.setAvatar(avatar);
//                    setUserHearder(hxid, user);
//                    map.put(hxid, user);
//
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            }
//        }
//        // 添加user"申请与通知"
//        User newFriends = new User();
//        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
//        String strChat = getResources().getString(
//                R.string.Application_and_notify);
//        newFriends.setNick(strChat);
//        newFriends.setBeizhu("");
//        newFriends.setFxid("");
//        newFriends.setHeader("");
//        newFriends.setRegion("");
//        newFriends.setSex("");
//        newFriends.setTel("");
//        newFriends.setSign("");
//        newFriends.setAvatar("");
//        map.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
//        // 添加"群聊"
//        User groupUser = new User();
//        String strGroup = getResources().getString(R.string.group_chat);
//        groupUser.setUsername(Constant.GROUP_USERNAME);
//        groupUser.setNick(strGroup);
//        groupUser.setHeader("");
//        groupUser.setNick(strChat);
//        groupUser.setBeizhu("");
//        groupUser.setFxid("");
//        groupUser.setHeader("");
//        groupUser.setRegion("");
//        groupUser.setSex("");
//        groupUser.setTel("");
//        groupUser.setSign("");
//        groupUser.setAvatar("");
//        map.put(Constant.GROUP_USERNAME, groupUser);
//
//        // 存入内存
//        DemoApplication.getInstance().setContactList(map);
//        // 存入db
//        UserDao dao = new UserDao(MainActivity.this);
//        List<User> users = new ArrayList<User>(map.values());
//        dao.saveContactList(users);

    }

    private void addFriendToList(final String hxid) {
        Map<String, String> map_uf = new HashMap<String, String>();
        map_uf.put("hxid", hxid);
        LoadDataFromServer task = new LoadDataFromServer(null,
                "", map_uf);
        task.getData(new DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                try {

//                    int code = data.getInteger("code");
//                    if (code == 1) {
//
//                        JSONObject json = data.getJSONObject("user");
//                        if (json != null && json.size() != 0) {
//
//                        }
//                        String nick = json.getString("nick");
//                        String avatar = json.getString("avatar");
//
//                        String hxid = json.getString("hxid");
//                        String fxid = json.getString("fxid");
//                        String region = json.getString("region");
//                        String sex = json.getString("sex");
//                        String sign = json.getString("sign");
//                        String tel = json.getString("tel");
//                        FriendInfo user = new FriendInfo();
//
//                        user.sys_id = hxid;
//                        user.
//                        user.setAvatar(avatar);
//                        user.setFxid(fxid);
//                        user.setRegion(region);
//                        user.setSex(sex);
//                        user.setSign(sign);
//                        user.setTel(tel);
//                        setUserHearder(hxid, user);
//                        Map<String, User> userlist = DemoApplication
//                                .getInstance().getContactList();
//                        Map<String, User> map_temp = new HashMap<String, User>();
//                        map_temp.put(hxid, user);
//                        userlist.putAll(map_temp);
//                        // 存入内存
//                        DemoApplication.getInstance().setContactList(userlist);
//                        // 存入db
//                        UserDao dao = new UserDao(MainActivity.this);
//
//                        dao.saveContact(user);
//
//                        // 自己封装的javabean
//                        InviteMessage msg = new InviteMessage();
//                        msg.setFrom(hxid);
//                        msg.setTime(System.currentTimeMillis());
//
//                        String reason_temp = nick + "66split88" + avatar
//                                + "66split88"
//                                + String.valueOf(System.currentTimeMillis())
//                                + "66split88" + "已经同意请求";
//                        msg.setReason(reason_temp);
//
//                        msg.setStatus(InviteMesageStatus.BEAGREED);
//                        User userTemp = DemoApplication.getInstance()
//                                .getContactList()
//                                .get(Constant.NEW_FRIENDS_USERNAME);
//                        if (userTemp != null
//                                && userTemp.getUnreadMsgCount() == 0) {
//                            userTemp.setUnreadMsgCount(userTemp
//                                    .getUnreadMsgCount() + 1);
//                        }
//                        notifyNewIviteMessage(msg);
//                    }
//
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

        });

    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                moveTaskToBack(false);
                finish();

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}