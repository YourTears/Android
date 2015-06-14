package com.fanxin.app.fx;

import com.fanxin.activity.MainActivity;
import com.fanxin.app.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import appLogic.AppConstant;
import appLogic.FriendInfo;


public class FriendPopupWindow extends PopupWindow {
    private FriendInfo friend = null;

	@SuppressLint("InflateParams")
	public FriendPopupWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.friendinfo_more, null);
 
        // 设置SelectPicPopupWindow的View
        this.setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        
        
        RelativeLayout remark =(RelativeLayout) view.findViewById(R.id.rl_remark);
        RelativeLayout report =(RelativeLayout) view.findViewById(R.id.rl_report);
        RelativeLayout deleteContact =(RelativeLayout) view.findViewById(R.id.rl_delete_contact);

        remark.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                FriendPopupWindow.this.dismiss();
          
            }
            
        } );
        report.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                FriendPopupWindow.this.dismiss();
            }

        } );
        deleteContact.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                if(friend != null) {
                    new AlertDialog.Builder(context)
                            .setTitle("确认删除 " + friend.name + " ?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener(){

                                public void onClick(DialogInterface dialog, int item) {
                                    AppConstant.friendManager.deleteFriend(friend.id);
                                    context.startActivity(new Intent(context, MainActivity.class));
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                }

                FriendPopupWindow.this.dismiss();
            }

        } );
    }

    /**
     * 显示popupWindow
     * 
     * @param parent
     */
    public void showPopupWindow(FriendInfo friend, View parent) {
        this.friend = friend;
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
