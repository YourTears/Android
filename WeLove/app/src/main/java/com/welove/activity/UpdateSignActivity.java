package com.welove.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.welove.app.R;
import com.welove.broadcast.UpdateInfoService;

import appLogic.AppConstant;

/**
 * Created by Long on 6/14/2015.
 */
public class UpdateSignActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sign);

        final EditText et_sign = (EditText) this.findViewById(R.id.et_sign);
        et_sign.setText(AppConstant.meInfo.sign);

        if(AppConstant.meInfo.sign != null)
            et_sign.setSelection(AppConstant.meInfo.sign.length());

        TextView saveView = (TextView) this.findViewById(R.id.tv_save);
        saveView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppConstant.meInfo.sign = et_sign.getText().toString().trim();
                AppConstant.userManager.updateUserInfo(AppConstant.meInfo.id);

                Intent intent = new Intent(UpdateInfoService.ServiceName);
                intent.putExtra(UpdateInfoService.UpdateUserDetail, true);
                intent.putExtra("id", AppConstant.meInfo.id);

                sendBroadcast(intent);

                finish();
            }
        });
    }

    public void back(View view) {
        finish();
    }
}