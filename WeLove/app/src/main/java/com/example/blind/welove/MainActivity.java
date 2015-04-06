package com.example.blind.welove;

import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import java.io.File;
import java.io.IOException;

import common.Gender;
import common.Util;
import logic.PersonalInfo;


public class MainActivity extends ActionBarActivity {
    private FragmentLove fragmentLove = null;
    private FragmentChat fragmentChat = null;
    private FragmentContact fragmentContact = null;
    private FragmentMe fragmentMe = null;

    private FragmentTransaction fragmentTransaction = null;
    private boolean isMainFrameAdded = false;

    private RadioGroup mainRadioGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initConstant();

        initMeInfo();

        setContentView(R.layout.activity_main);

        setFragment(R.id.id_radio_love);

        initialization();
    }

    private void initConstant() {
        Constant.dataRootPath = this.getFilesDir().getPath() + "/cache/";
        Constant.imageFolder = Constant.dataRootPath + Constant.imageFolderName + "/";
        Constant.infoFolder = Constant.dataRootPath + Constant.infoFolderName + "/";

        Util.createFolder(Constant.dataRootPath);
        Util.createFolder(Constant.imageFolder);
        Util.createFolder(Constant.infoFolder);
    }

    private void initMeInfo() {
        try {
            Constant.meInfo = PersonalInfo.getPersonalInfo(this.getResources().getAssets().open("info/me.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(Constant.meInfo != null && Constant.meInfo.gender == Gender.female)
            Constant.it = "他";
    }

    private void initialization() {
        mainRadioGroup = (RadioGroup) findViewById(R.id.main_radio_group);
        mainRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setFragment(checkedId);
            }
        });
    }

    private void setFragment(int radioId) {
        fragmentTransaction = getFragmentManager().beginTransaction();

        switch (radioId) {
            case R.id.id_radio_love:
                if (fragmentLove == null)
                    fragmentLove = new FragmentLove();
                if (isMainFrameAdded)
                    fragmentTransaction.replace(R.id.main_content, fragmentLove);
                else {
                    fragmentTransaction.add(R.id.main_content, fragmentLove);
                    isMainFrameAdded = true;
                }
                break;

            case R.id.id_radio_chat:
                if (fragmentChat == null)
                    fragmentChat = new FragmentChat();
                fragmentTransaction.replace(R.id.main_content, fragmentChat);
                break;

            case R.id.id_radio_contact:
                if (fragmentContact == null)
                    fragmentContact = new FragmentContact();
                fragmentTransaction.replace(R.id.main_content, fragmentContact);
                break;

            case R.id.id_radio_me:
                if (fragmentMe == null)
                    fragmentMe = new FragmentMe();
                fragmentTransaction.replace(R.id.main_content, fragmentMe);
                break;

            default:
                break;
        }

        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
