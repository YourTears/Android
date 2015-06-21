package com.welove.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.welove.app.R;
import com.welove.broadcast.UpdateInfoService;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import appLogic.AppConstant;
import common.ImageLoaderManager;

@SuppressLint("SdCardPath")
public class MeInfoActivity extends BroadcastActivity {

    private RelativeLayout re_image;
    private RelativeLayout re_name;
    private RelativeLayout re_sign;

    private ImageView imageView;
    private TextView nameView;
    private TextView idView;
    private TextView signView;

    private String imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_detail);
        initView();

        initBroadcastService();
    }

    private void initBroadcastService(){
        broadServiceName = UpdateInfoService.ServiceName;
        broadcastIntent = new Intent(MeInfoActivity.this, UpdateInfoService.class);
        broadcastReceiver = new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                boolean updateUserDetail = intent.getBooleanExtra(UpdateInfoService.UpdateUserDetail, false);

                if(updateUserDetail){
                    String userId = intent.getStringExtra("id");
                    if(userId.equals(AppConstant.meInfo.id)){
                        if(nameView != null){
                            nameView.setText(AppConstant.meInfo.name);
                        }

                        if(signView != null){
                            signView.setText(AppConstant.meInfo.sign);
                        }
                    }
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

    private void initView() {
        re_image = (RelativeLayout) this.findViewById(R.id.re_image);
        re_name = (RelativeLayout) this.findViewById(R.id.re_name);
        re_sign = (RelativeLayout) this.findViewById(R.id.re_sign);

        re_image.setOnClickListener(new MyListener());
        re_name.setOnClickListener(new MyListener());
        re_sign.setOnClickListener(new MyListener());

        imageView = (ImageView) this.findViewById(R.id.iv_image);
        nameView = (TextView) this.findViewById(R.id.tv_name);
        idView = (TextView) this.findViewById(R.id.tv_id);
        signView = (TextView) this.findViewById(R.id.tv_sign);

        nameView.setText(AppConstant.meInfo.name);
        idView.setText(AppConstant.meInfo.id);
        signView.setText(AppConstant.meInfo.sign);

        AppConstant.imageLoaderManager.loadImage(imageView, AppConstant.meInfo.id, AppConstant.meInfo.imageUrl, ImageLoaderManager.CacheMode.Memory);
    }

    class MyListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.re_image:
                    showPhotoDialog();
                    break;

                case R.id.re_name:
                    Intent intent = new Intent(MeInfoActivity.this, UpdateNickNameActivity.class);
                    intent.putExtra("id", AppConstant.meInfo.id);
                    startActivity(intent);

                    break;

                case R.id.re_sign:
                    startActivity(new Intent(MeInfoActivity.this, UpdateSignActivity.class));
                    break;
            }
        }
    }

    private void showPhotoDialog() {
        imageName = getNowTime() + ".png";
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
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
                imageView.setImageBitmap(bitmap);
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
