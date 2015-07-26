package com.welove.activity;
import com.welove.app.R;
import com.welove.broadcast.UpdateInfoService;
import com.welove.database.MessageTable;
import com.welove.view.FragmentConversation;
import com.welove.view.FragmentFind;
import com.welove.view.FragmentFriends;
import com.welove.view.FragmentProfile;

import appLogic.AppNotification;
import appLogic.Message;
import chat.ConversationProxy;
import chat.MessageEvent;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import appLogic.AppConstant;
import appLogic.ConversationManager;
import appLogic.UserInfo;
import appLogic.UserManager;
import common.ImageLoaderManager;
import common.Util;
import de.greenrobot.event.EventBus;

@SuppressLint("DefaultLocale")
public class MainActivity extends BroadcastActivity {
    public enum FragmentType{
        Love,
        Conversation,
        Friends
    }

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

    public static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMeInfo();

        AppConstant.conversationProxy = new ConversationProxy();

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

        AppNotification.initialize((NotificationManager) getSystemService(NOTIFICATION_SERVICE), this);

        initBroadcastService();

        EventBus.getDefault().register(this);

        instance = this;
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
        AppConstant.userManager = UserManager.getInstance(this);

        if(AppConstant.userManager.friends.size() == 0)
            AppConstant.userManager.refresh(Util.getAssertInputStream(this.getResources().getAssets(), "friends.json"));

        AppConstant.meInfo = AppConstant.userManager.getUser(AppConstant.id);

        if(AppConstant.meInfo.gender == UserInfo.Gender.Female){
            AppConstant.it = "他";
        }

        AppConstant.defaultImageDrawable = getResources().getDrawable(R.drawable.default_boy_drawable);

        AppConstant.inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        AppConstant.conversationManager = new ConversationManager(this);

        AppConstant.imageLoaderManager = new ImageLoaderManager();

        AppConstant.messageTable = MessageTable.getInstance(this);
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

        currentTabIndex = 0;
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

    public void onEventBackgroundThread(MessageEvent messageEvent) {
        Message message = AppConstant.conversationProxy.getMessageByEvent(messageEvent);

        if (message == null)
            return;

        UserInfo user = AppConstant.userManager.getUser(message.friendId);
        if (user == null)
            return;

        if (ChatActivity.instance != null && ChatActivity.instance.friend.id == message.friendId) {
            ChatActivity.instance.addChatMessage(message);
        } else {
            AppConstant.messageTable.insertMessage(message);
            if (message.direction == Message.Direction.RECEIVE) {

                AppConstant.conversationManager.addOrReplaceConversation(message);
                AppNotification.getInstance().sendNotification(user.nickName, message.body, FragmentType.Conversation);

                runOnUiThread(new Runnable() {
                    public void run() {
                        AppConstant.conversationManager.refreshView();
                        updateUnreadMessageLabel();
                    }
                });
            }
        }
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

    @Override
    public void onResume() {
        super.onResume();
        if (!isConflict || !isCurrentAccountRemoved) {
            // initView();

            updateUnreadMessageLabel();
            updateNewFriendsLabel();
        }
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

    @Override
    public void onDestroy(){
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        instance = null;
    }

    public void updateNewFriendsLabel() {
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

    public void updateUnreadMessageLabel() {
        int count = AppConstant.conversationManager.getUnreadCount();
        if (count > 0) {
            unreadLabel.setText(String.valueOf(count));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }
    }
}
