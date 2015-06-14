package com.fanxin.app.fx;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fanxin.activity.UpdateNickNameActivity;
import com.fanxin.activity.UpdateSignActivity;
import com.fanxin.app.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import appLogic.AppConstant;
import common.ImageLoaderManager;

@SuppressLint("SdCardPath")
public class MyUserInfoActivity extends Activity {

    private RelativeLayout re_image;
    private RelativeLayout re_name;
    private RelativeLayout re_sign;

    private ImageView iv_image;
    private TextView tv_name;
    private TextView tv_id;
    private TextView tv_sign;

    private String imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int UPDATE_SIGN = 4;// 结果
    private static final int UPDATE_NICK = 5;// 结果

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        initView();

    }

    private void initView() {
        re_image = (RelativeLayout) this.findViewById(R.id.re_image);
        re_name = (RelativeLayout) this.findViewById(R.id.re_name);
        re_sign = (RelativeLayout) this.findViewById(R.id.re_sign);

        re_image.setOnClickListener(new MyListener());
        re_name.setOnClickListener(new MyListener());
        re_sign.setOnClickListener(new MyListener());

        iv_image = (ImageView) this.findViewById(R.id.iv_image);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_id = (TextView) this.findViewById(R.id.tv_id);
        tv_sign = (TextView) this.findViewById(R.id.tv_sign);

        tv_name.setText(AppConstant.meInfo.name);
        tv_id.setText(AppConstant.meInfo.id);
        tv_sign.setText(AppConstant.meInfo.sign);

        AppConstant.imageLoaderManager.loadImage(iv_image, AppConstant.meInfo.id, AppConstant.meInfo.imageUrl, ImageLoaderManager.CacheMode.Memory);
    }

    class MyListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.re_image:
                    showPhotoDialog();
                    break;

                case R.id.re_name:
                    Intent intent = new Intent(MyUserInfoActivity.this, UpdateNickNameActivity.class);
                    intent.putExtra("id", AppConstant.meInfo.id);
                    startActivity(intent);

                    break;

                case R.id.re_sign:
                    startActivity(new Intent(MyUserInfoActivity.this, UpdateSignActivity.class));
                    break;
            }
        }
    }

    private void showPhotoDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        // 为确认按钮添加事件,执行退出应用操作
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("拍照");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {

                imageName = getNowTime() + ".png";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File("/sdcard/fanxin/", imageName)));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("相册");
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                getNowTime();
                imageName = getNowTime() + ".png";
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                dlg.cancel();
            }
        });

    }

    @SuppressLint("SdCardPath")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:

                startPhotoZoom(
                        Uri.fromFile(new File("/sdcard/fanxin/", imageName)),
                        480);
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    startPhotoZoom(data.getData(), 480);
                break;

            case PHOTO_REQUEST_CUT:
                // BitmapFactory.Options options = new BitmapFactory.Options();
                //
                // /**
                // * 最关键在此，把options.inJustDecodeBounds = true;
                // * 这里再decodeFile()，返回的bitmap为空
                // * ，但此时调用options.outHeight时，已经包含了图片的高了
                // */
                // options.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/fanxin/"
                        + imageName);
                iv_image.setImageBitmap(bitmap);
                updateAvatarInServer(imageName);
                break;

            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @SuppressLint("SdCardPath")
    private void startPhotoZoom(Uri uri1, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri1, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File("/sdcard/fanxin/", imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    public void back(View view) {
        finish();
    }

    private void updateAvatarInServer(final String image) {

    }
}
