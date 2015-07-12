package com.welove.activity;

import com.welove.app.R;
import com.welove.broadcast.UpdateInfoService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import appLogic.AppConstant;
import appLogic.UserInfo;

public class UpdateNickNameActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick);

        final String userId = this.getIntent().getStringExtra("id");
        final UserInfo user = AppConstant.userManager.getUser(userId);

        if (user == null)
            return;

        final EditText et_nick = (EditText) this.findViewById(R.id.et_nick);
        final TextView tv_save = (TextView) this.findViewById(R.id.tv_save);

        if(userId.equals(AppConstant.meInfo.id)) {
            et_nick.setText(user.name);
        }else{
            et_nick.setText(user.nickName);
        }

        if(user.nickName != null)
            et_nick.setSelection(user.nickName.length());

        et_nick.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    tv_save.setClickable(false);
                } else {
                    tv_save.setClickable(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = et_nick.getText().toString().trim();
                if(name == null || name.isEmpty()) {
                    finish();
                    return;
                }

                Intent intent = new Intent(UpdateInfoService.ServiceName);
                intent.putExtra(UpdateInfoService.UpdateUserDetail, true);
                intent.putExtra("id", userId);

                if(userId.equals(AppConstant.meInfo.id)){
                    user.nickName = user.name = name;
                    intent.putExtra(UpdateInfoService.UpdateProfile, true);
                    intent.putExtra(UpdateInfoService.UpdateContactList, true);
                } else{
                    user.nickName = name;
                    intent.putExtra(UpdateInfoService.UpdateContactList, true);
                    intent.putExtra(UpdateInfoService.UpdateConversationList, true);
                }

                AppConstant.userManager.updateUserInfo(userId);
                AppConstant.conversationManager.userInfoUpdated(userId);

                sendBroadcast(intent);

                finish();
            }
        });
    }

    public void back(View view) {
        finish();
    }
}
