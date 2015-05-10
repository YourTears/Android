package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.Map;

import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import appLogic.AppConstant;
import appLogic.FriendInfo;
import appLogic.FriendStatus;
import appLogic.Gender;

public class UserInfoActivity extends Activity {
    private FriendInfo friend = null;
     String hxid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        Button btn_sendmsg = (Button) this.findViewById(R.id.btn_sendmsg);
        ImageView iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
        TextView tv_name = (TextView) this.findViewById(R.id.tv_name);
        String id = this.getIntent().getStringExtra("id");
        friend = AppConstant.friendManager.getFriend(id);

        if (friend != null) {
            tv_name.setText(friend.name);
            if (friend.gender == Gender.male) {
                iv_sex.setImageResource(R.drawable.ic_sex_male);
            } else if (friend.gender == Gender.female) {
                iv_sex.setImageResource(R.drawable.ic_sex_female);
            } else {
                iv_sex.setVisibility(View.GONE);
            }

            if (friend.friendStatus == FriendStatus.friend) {
                btn_sendmsg.setText("发消息");
            }
        }

        btn_sendmsg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (friend.friendStatus == FriendStatus.friend) {
                    Intent intent = new Intent();
                    intent.putExtra("id", friend.id);
                    intent.setClass(UserInfoActivity.this, ChatActivity.class);

                    startActivity(intent);
                } else {

                    Intent intent = new Intent();
                    intent.putExtra("hxid", hxid);
                    // intent.putExtra("avatar", avatar);
                    // intent.putExtra("nick", nick);

                    intent.setClass(UserInfoActivity.this,
                            AddFriendsFinalActivity.class);
                    startActivity(intent);

                }
            }

        });
       refresh();
    }

    public void back(View view) {

        finish();
    }
    
    private void refresh(){
        Map<String, String> map = new HashMap<String, String>();

        map.put("uid", hxid);

        LoadDataFromServer task = new LoadDataFromServer(
                UserInfoActivity.this, Constant.URL_Search_User, map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    
//                    int code = data.getInteger("code");
//                    if (code == 1) {
//
//                        JSONObject json = data.getJSONObject("user");
//                        String hxid = json.getString("hxid");
//                        String fxid = json.getString("fxid");
//                        String nick = json.getString("nick");
//                        String avatar = json.getString("avatar");
//                        String sex = json.getString("sex");
//                        String region = json.getString("region");
//                        String sign = json.getString("sign");
//                        String tel = json.getString("tel");
//
//                        User user = new User();
//                        user.setFxid(fxid);
//                        user.setUsername(hxid);
//                        user.setBeizhu("");
//                        user.setNick(nick);
//                        user.setRegion(region);
//                        user.setSex(sex);
//                        user.setTel(tel);
//                        user.setSign(sign);
//                        user.setAvatar(avatar);
//                        setUserHearder(hxid, user);
//
//
//                        UserDao dao = new UserDao(UserInfoActivity.this);
//                        dao.saveContact(user);
//                        DemoApplication.getInstance().getContactList().put(hxid, user);
//
//                    }

                } catch (JSONException e) {
                     
                    e.printStackTrace();
                }
            }
        });
    }
    
}
