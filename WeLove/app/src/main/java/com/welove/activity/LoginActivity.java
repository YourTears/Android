
package com.welove.activity;

import java.util.HashMap;
import java.util.Map;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.welove.app.R;
import com.welove.database.LoginTable;

import appLogic.AppConstant;
import common.LoadDataFromServer;
import common.LoadDataFromServer.DataCallBack;

/**
 * 登陆页面
 *
 */
public class LoginActivity extends Activity {
    private EditText et_usertel;
    private EditText et_password;
    private Button btn_login;
    private Button btn_qtlogin;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new ProgressDialog(LoginActivity.this);
        et_usertel = (EditText) findViewById(R.id.et_usertel);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_qtlogin = (Button) findViewById(R.id.btn_qtlogin);

        et_usertel.addTextChangedListener(new TextChange());
        et_password.addTextChangedListener(new TextChange());

        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.setMessage("正在登录...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();

                String phone = et_usertel.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                String id = getIdByPhone(phone);
                dialog.dismiss();

                if(id == null) {
                    Toast.makeText(LoginActivity.this, "账号或密码错误...", Toast.LENGTH_SHORT).show();
                } else {
                    AppConstant.id = id;
                    LoginTable.getInstance(LoginActivity.this).addOrReplaceLogin(id, password);

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }

        });
        btn_qtlogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,
                        RegisterActivity.class));
            }

        });
    }

    private String getIdByPhone(String phone)
    {
        if(phone.equals("13818085246"))
            return "longztc";

        if(phone.equals("13818085247"))
            return "dede";

        if(phone.equals("13512169026"))
            return "mingyang";

        return null;
    }

    // EditText监听器
    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {

            boolean Sign2 = et_usertel.getText().length() > 0;
            boolean Sign3 = et_password.getText().length() > 0;

            if (Sign2 & Sign3) {
                btn_login.setTextColor(0xFFFFFFFF);
                btn_login.setEnabled(true);
            }
            // 在layout文件中，对Button的text属性应预先设置默认值，否则刚打开程序的时候Button是无显示的
            else {
                btn_login.setTextColor(0xFFD0EFC6);
                btn_login.setEnabled(false);
            }
        }

    }
}