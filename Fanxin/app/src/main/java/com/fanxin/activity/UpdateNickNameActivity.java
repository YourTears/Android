package com.fanxin.activity;

import com.fanxin.app.R;
import android.app.Activity;
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

        et_nick.setText(user.nickName);

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
                if(userId.equals(AppConstant.meInfo.id)){
                    user.name = et_nick.getText().toString();
                } else{
                    user.nickName = et_nick.getText().toString();
                }

                AppConstant.userManager.updateUserInfo(userId);
                finish();
            }
        });
    }

    public void back(View view) {
        finish();
    }
}
