package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.fx.others.TopUser;
import com.fanxin.app.fx.others.TopUserDao;

import appLogic.FriendInfo;

@SuppressLint({ "SimpleDateFormat", "SdCardPath" })
public class ChatSingleSettingActivity extends BaseActivity implements
        OnClickListener {
    // 、置顶、、、、
    private RelativeLayout rl_switch_chattotop;
    private RelativeLayout rl_switch_block_groupmsg;
    private RelativeLayout re_clear;

    // 状态变化
    private ImageView iv_switch_chattotop;
    private ImageView iv_switch_unchattotop;
    private ImageView iv_switch_block_groupmsg;
    private ImageView iv_switch_unblock_groupmsg;

    private String userId;
    private String userNick;
    private String avatar;
    String sex;
    private List<String> blackList;
    // 置顶列表
    Map<String, TopUser> topMap = new HashMap<String, TopUser>();
    private ProgressDialog progressDialog;
    public static ChatSingleSettingActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlechat_setting);

        instance = this;
        // 获取传过来的userId
        userId = getIntent().getStringExtra("userId");
        FriendInfo user = null;
        // 资料错误则不显示
        if (user == null) {
            return;
        }
        userNick = user.nickName;
        avatar = user.imageUrl;
        sex = user.gender.toString();

        //
        progressDialog = new ProgressDialog(this);
        initView();
        initData();

    }

    private void initView() {

        rl_switch_chattotop = (RelativeLayout) findViewById(R.id.rl_switch_chattotop);
        rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        re_clear = (RelativeLayout) findViewById(R.id.re_clear);

        iv_switch_chattotop = (ImageView) findViewById(R.id.iv_switch_chattotop);
        iv_switch_unchattotop = (ImageView) findViewById(R.id.iv_switch_unchattotop);
        iv_switch_block_groupmsg = (ImageView) findViewById(R.id.iv_switch_block_groupmsg);
        iv_switch_unblock_groupmsg = (ImageView) findViewById(R.id.iv_switch_unblock_groupmsg);

        // 初始化置顶和免打扰的状态
        if (!blackList.contains(userId)) {
            iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);

        } else {

            iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);

        }
        if (!topMap.containsKey(userId)) {
            // 当前状态是w未置顶
            iv_switch_chattotop.setVisibility(View.INVISIBLE);
            iv_switch_unchattotop.setVisibility(View.VISIBLE);
        } else {
            // 当前状态是置顶
            iv_switch_chattotop.setVisibility(View.VISIBLE);
            iv_switch_unchattotop.setVisibility(View.INVISIBLE);
        }

    }

    private void initData() {

        rl_switch_chattotop.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);
        re_clear.setOnClickListener(this);

        ImageView iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        TextView tv_username = (TextView) this.findViewById(R.id.tv_username);
        tv_username.setText(userNick);
        iv_avatar.setImageResource(R.drawable.default_useravatar);
        iv_avatar.setTag(avatar);
        if (avatar != null && !avatar.equals("")) {

        }
        iv_avatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(ChatSingleSettingActivity.this,
                        UserInfoActivity.class).putExtra("hxid", userId)
                        .putExtra("nick", userNick).putExtra("avatar", avatar)
                        .putExtra("sex", sex));

            }

        });
        ImageView iv_avatar2 = (ImageView) this.findViewById(R.id.iv_avatar2);
        iv_avatar2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatSingleSettingActivity.this,
                        CreatChatRoomActivity.class).putExtra("userId", userId));

            }

        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_switch_block_groupmsg: // 设置免打扰
            progressDialog.setMessage("正在设置免打扰...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            if (iv_switch_block_groupmsg.getVisibility() == View.VISIBLE) {
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        removeOutBlacklist(userId);
                        progressDialog.dismiss();

                    }

                }, 2000);

            } else {
                moveToBlacklist(userId);
            }
            break;

        case R.id.re_clear: // 清空聊天记录
            progressDialog.setMessage("正在清空消息...");
            progressDialog.show();
            // 按照你们要求必须有个提示，防止记录太少，删得太快，不提示
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    progressDialog.dismiss();

                }

            }, 2000);

            break;

        case R.id.rl_switch_chattotop:
            // 当前状态是已经置顶,点击后取消置顶
            if (iv_switch_chattotop.getVisibility() == View.VISIBLE) {

                iv_switch_chattotop.setVisibility(View.INVISIBLE);
                iv_switch_unchattotop.setVisibility(View.VISIBLE);

                if (topMap.containsKey(userId)) {

                    topMap.remove(userId);
                    TopUserDao topUserDao = new TopUserDao(
                            ChatSingleSettingActivity.this);

                    topUserDao.deleteTopUser(userId);

                }

            } else {

                // 当前状态是未置顶点击后置顶

                iv_switch_chattotop.setVisibility(View.VISIBLE);
                iv_switch_unchattotop.setVisibility(View.INVISIBLE);

                if (!topMap.containsKey(userId)) {
                    TopUser topUser = new TopUser();
                    topUser.setTime(System.currentTimeMillis());
                    // 1---表示是群组0----个人
                    topUser.setType(0);
                    topUser.setUserName(userId);
                    Map<String, TopUser> map = new HashMap<String, TopUser>();
                    map.put(userId, topUser);
                    topMap.putAll(map);
                    TopUserDao topUserDao = new TopUserDao(
                            ChatSingleSettingActivity.this);
                    topUserDao.saveTopUser(topUser);

                }

            }

            break;

        default:
            break;
        }

    }

    /**
     * 把user移入到免打扰
     */
    private void moveToBlacklist(final String username) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // 加入到黑名单
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            iv_switch_block_groupmsg
                                    .setVisibility(View.VISIBLE);
                            iv_switch_unblock_groupmsg
                                    .setVisibility(View.INVISIBLE);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 移出免打扰
     * 
     * @param tobeRemoveUser
     */
    private void removeOutBlacklist(final String tobeRemoveUser) {

        try {
            iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
            iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(getApplicationContext(), "设置失败",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void back(View v) {
        finish();
    }

}
