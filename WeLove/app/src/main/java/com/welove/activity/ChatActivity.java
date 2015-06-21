package com.welove.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.welove.adapter.ExpressionAdapter;
import com.welove.app.R;
import com.welove.adapter.ExpressionPagerAdapter;
import com.welove.view.ExpandGridView;
import common.PasteEditText;

import appLogic.AppConstant;
import appLogic.UserInfo;
import appLogic.Message;
import appLogic.MessageManager;
import common.ExpressionUtils;

public class ChatActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    private static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;

    public static final String COPY_IMAGE = "EASEMOBIMG";
    private View recordingContainer;
    private ImageView micImage;
    private TextView recordingHint;
    private ListView conversationListView;
    private PasteEditText messageEditText;
    private View keyboardModeButton;
    private View voiceModeButton;
    private View sendMessageButton;
    private View pressToSpeakButton;
    // private ViewPager expressionView;
    private LinearLayout expressionLayout;
    private LinearLayout moreButtonsLayout;

    private View textModeLayout;
    private ClipboardManager clipboard;
    private ViewPager expressionView;
    private Drawable[] micImages;
    private NewMessageBroadcastReceiver receiver;
    public static ChatActivity activityInstance = null;
    // 给谁发送消息
    private UserInfo friend;

    private File cameraFile;
    public static int resendPos;

    private ImageView normalExpressionView;
    private ImageView checkedExpressionView;
    private RelativeLayout messageLayout;
    private ProgressBar loadingMessageProcessBar;
    private boolean isLoading = false;
    private final int pagesize = 20;
    private Button moreButton;
    public String playMsgId;

    private MessageManager messageManager;

    private boolean sentMessage = false;

    // 分享的照片
    String iamge_path = null;
    // 设置按钮
    private ImageView iv_setting;
    private ImageView iv_setting_group;
    @SuppressLint("HandlerLeak")
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String friendId = this.getIntent().getStringExtra("id");
        friend = AppConstant.userManager.getUser(friendId);

        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ForPressToTalk");

        initView();
        setUpView();

        readingMessageMode();
        conversationListView.requestFocus();
    }

    /**
     * initView
     */
    protected void initView() {
        loadingMessageProcessBar = (ProgressBar) findViewById(R.id.pb_loading_message);
        recordingContainer = findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        conversationListView = (ListView) findViewById(R.id.listview_conversations);

        voiceModeButton = findViewById(R.id.button_voice_mode);
        keyboardModeButton = findViewById(R.id.button_keyboard_mode);

        messageLayout = (RelativeLayout) findViewById(R.id.layout_message);
        messageEditText = (PasteEditText) findViewById(R.id.et_message);

        sendMessageButton = findViewById(R.id.button_send_message);
        pressToSpeakButton = findViewById(R.id.button_press_to_speak);

        expressionLayout = (LinearLayout) findViewById(R.id.layout_expressions);
        expressionView = (ViewPager) findViewById(R.id.view_expressions);

        normalExpressionView = (ImageView) findViewById(R.id.iv_expression_normal);
        checkedExpressionView = (ImageView) findViewById(R.id.iv_expression_checked);

        moreButtonsLayout = (LinearLayout) findViewById(R.id.layout_more_buttons);
        moreButton = (Button) findViewById(R.id.button_more);

        textModeLayout = findViewById(R.id.layout_text_mode);
        messageLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);

        messageManager = new MessageManager(this, friend.id);

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{
                getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08),
                getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10),
                getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12),
                getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14),};

        // 初始化表情viewpager
        List<View> views = getExpressionViews();

        expressionView.setAdapter(new ExpressionPagerAdapter(views));
        messageEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextMode();
                } else {
                    messageLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }
            }
        });

        // 监听文字框
        messageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    moreButton.setVisibility(View.GONE);
                    sendMessageButton.setVisibility(View.VISIBLE);
                } else {
                    moreButton.setVisibility(View.VISIBLE);
                    sendMessageButton.setVisibility(View.GONE);
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
    }

    private void readingMessageMode() {
        hideKeyboard();
        textModeLayout.setVisibility(View.GONE);
    }

    private void editTextMode() {
        keyboardMode();

        messageLayout.setBackgroundResource(R.drawable.input_bar_bg_active);

        textModeLayout.setVisibility(View.GONE);

        normalExpressionView.setVisibility(View.VISIBLE);
        checkedExpressionView.setVisibility(View.INVISIBLE);

        if (TextUtils.isEmpty(messageEditText.getText())) {
            moreButton.setVisibility(View.VISIBLE);
            sendMessageButton.setVisibility(View.GONE);
        } else {
            moreButton.setVisibility(View.GONE);
            sendMessageButton.setVisibility(View.VISIBLE);
        }

        moreButtonsLayout.setVisibility(View.GONE);
        expressionLayout.setVisibility(View.GONE);
    }

    private void expressionMode() {
        hideKeyboard();
        keyboardMode();

        messageLayout.setBackgroundResource(R.drawable.input_bar_bg_active);

        moreButtonsLayout.setVisibility(View.GONE);

        textModeLayout.setVisibility(View.VISIBLE);

        normalExpressionView.setVisibility(View.INVISIBLE);
        checkedExpressionView.setVisibility(View.VISIBLE);

        expressionLayout.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(messageEditText.getText())) {
            moreButton.setVisibility(View.VISIBLE);
            sendMessageButton.setVisibility(View.GONE);
        } else {
            moreButton.setVisibility(View.GONE);
            sendMessageButton.setVisibility(View.VISIBLE);
        }
    }

    private void moreButtonsMode() {
        hideKeyboard();
        keyboardMode();

        expressionLayout.setVisibility(View.GONE);

        messageLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
        pressToSpeakButton.setVisibility(View.GONE);

        voiceModeButton.setVisibility(View.VISIBLE);
        keyboardModeButton.setVisibility(View.GONE);

        textModeLayout.setVisibility(View.VISIBLE);

        normalExpressionView.setVisibility(View.VISIBLE);
        checkedExpressionView.setVisibility(View.INVISIBLE);

        moreButtonsLayout.setVisibility(View.VISIBLE);
    }

    private void keyboardMode() {
        voiceModeButton.setVisibility(View.VISIBLE);
        keyboardModeButton.setVisibility(View.GONE);

        messageLayout.setVisibility(View.VISIBLE);
        pressToSpeakButton.setVisibility(View.GONE);
    }

    private void speakMode() {
        hideKeyboard();
        messageLayout.setVisibility(View.GONE);

        textModeLayout.setVisibility(View.GONE);

        voiceModeButton.setVisibility(View.GONE);
        keyboardModeButton.setVisibility(View.VISIBLE);

        pressToSpeakButton.setVisibility(View.VISIBLE);

        sendMessageButton.setVisibility(View.GONE);
        moreButton.setVisibility(View.VISIBLE);

        moreButtonsLayout.setVisibility(View.GONE);
        expressionLayout.setVisibility(View.GONE);
    }

    private void setUpView() {
        activityInstance = this;
        normalExpressionView.setOnClickListener(this);
        checkedExpressionView.setOnClickListener(this);
        voiceModeButton.setOnClickListener(this);
        keyboardModeButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);

        // position = getIntent().getIntExtra("position", -1);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        ((TextView) findViewById(R.id.textview_name)).setText(friend.nickName);


        // 显示消息
        conversationListView.setAdapter(messageManager.adapter);
        conversationListView.setOnScrollListener(new ListScrollListener());
        int count = conversationListView.getCount();
        if (count > 0) {
            conversationListView.setSelection(count - 1);
        }

        conversationListView.setOnTouchListener(new OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                readingMessageMode();
                return false;
            }
        });
        // 注册接收消息广播
        receiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter();
        ackMessageIntentFilter.setPriority(5);

        // 注册一个消息送达的BroadcastReceiver
        IntentFilter deliveryAckMessageIntentFilter = new IntentFilter();
        deliveryAckMessageIntentFilter.setPriority(5);

        // show forward message if the message is not null
        String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
        if (forward_msg_id != null) {
            // 显示发送要转发的消息
            //forwardMessage(forward_msg_id);
        }
        iv_setting = (ImageView) this.findViewById(R.id.iv_setting);
        iv_setting_group = (ImageView) this.findViewById(R.id.iv_setting_group);
        iv_setting.setVisibility(View.VISIBLE);
        iv_setting_group.setVisibility(View.GONE);
        iv_setting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }

        });
    }

    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                AppConstant.inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            finish();
        }

        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case RESULT_CODE_COPY: // 复制消息
                    break;
                case RESULT_CODE_DELETE: // 删除消息
                    messageManager.adapter.notifyDataSetChanged();
                    conversationListView.setSelection(data.getIntExtra("position", messageManager.adapter.getCount()) - 1);
                    break;

                case RESULT_CODE_FORWARD: // 转发消息
                    // EMMessage forwardMsg = (EMMessage) adapter.getItem(data
                    // .getIntExtra("position", 0));
                    // Intent intent = new Intent(this,
                    // ForwardMessageActivity.class);
                    // intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
                    // startActivity(intent);

                    break;

                default:
                    break;
            }
        }
        if (resultCode == RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // 清空会话
                messageManager.adapter.notifyDataSetChanged();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists())
                    Log.e("cameraFile.getAbsolutePath()------>>>>", cameraFile.getAbsolutePath());
                sendPicture(cameraFile.getAbsolutePath(), false);
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

                int duration = data.getIntExtra("dur", 0);
                String videoPath = data.getStringExtra("path");

                Bitmap bitmap = null;
                FileOutputStream fos = null;
                try {

                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                    if (bitmap == null) {
                        bitmap = BitmapFactory.decodeResource(getResources(),
                                R.drawable.app_panel_video_icon);
                    }

                    bitmap.compress(CompressFormat.JPEG, 100, fos);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fos = null;
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    }

                }

            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    //more(textModeLayout);
                    sendLocationMsg(latitude, longitude, "", locationAddress);
                } else {
                    Toast.makeText(this, "无法获取到您的位置信息！", Toast.LENGTH_SHORT)
                            .show();
                }
                // 重发消息
            } else if (requestCode == REQUEST_CODE_TEXT
                    || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE
                    || requestCode == REQUEST_CODE_LOCATION
                    || requestCode == REQUEST_CODE_VIDEO
                    || requestCode == REQUEST_CODE_FILE) {
                resendMessage();
            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
                // 粘贴
                if (!TextUtils.isEmpty(clipboard.getText())) {
                    String pasteText = clipboard.getText().toString();
                    if (pasteText.startsWith(COPY_IMAGE)) {
                        // 把图片前缀去掉，还原成正常的path
                        sendPicture(pasteText.replace(COPY_IMAGE, ""), false);
                    }

                }
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单

            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                messageManager.adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 消息图标点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.button_send_message) {// 点击发送按钮(发文字和表情)
            String content = messageEditText.getText().toString().trim();
            if (!content.isEmpty()) {
                sendText(content);
                sentMessage = true;
            }
        } else if (id == R.id.btn_take_picture) {
            selectPicFromCamera();// 点击照相图标
        } else if (id == R.id.btn_picture) {
            selectPicFromLocal(); // 点击图片图标
        } else if (id == R.id.iv_expression_normal) { // 点击显示表情框
            expressionMode();
        } else if (id == R.id.iv_expression_checked) { // 点击隐藏表情框
            editTextMode();
        } else if (id == R.id.btn_video) {
        } else if (id == R.id.btn_voice_call) { // 点击语音电话图标
        } else if (id == R.id.button_voice_mode) {
            speakMode();
        } else if (id == R.id.button_keyboard_mode) {
            editTextMode();
        } else if (id == R.id.button_more) {
            moreButtonsMode();
        } else if (id == R.id.et_message) {
            editTextMode();
        }
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    private void sendText(String content) {
        if (content.length() > 0) {
            long time = (new Date()).getTime();
            if (time == messageManager.lastMessageTime)
                time++;

            Message message = new Message(UUID.randomUUID(), friend.id, Message.Direction.SEND,
                    content, time, Message.MessageType.TEXT);

            messageManager.addOrReplaceMessage(message);

            conversationListView.setSelection(conversationListView.getCount() - 1);
            messageEditText.setText("");

            sendMessageInBackground(message);
            setResult(RESULT_OK);
        }
    }

    private void sendMessageInBackground(Message message){
        message.status = Message.MessageStatus.SUCCEED;

        messageManager.addOrReplaceMessage(message);
    }

    /**
     * 发送语音
     *
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(String filePath, String fileName, String length,
                           boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        try {
            conversationListView.setSelection(conversationListView.getCount() - 1);
            setResult(RESULT_OK);
            // send file
            // sendVoiceSub(filePath, fileName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPicture(final String filePath, boolean is_share) {
        Log.e("filePath------>>>>", filePath);
        String to = friend.id;

        conversationListView.setSelection(conversationListView.getCount() - 1);
        setResult(RESULT_OK);
        // textModeLayout(textModeLayout);
    }

    /**
     * 发送视频消息
     */
    private void sendVideo(final String filePath, final String thumbPath,
                           final int length) {
        final File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            return;
        }
        try {
            conversationListView.setSelection(conversationListView.getCount() - 1);
            setResult(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath, false);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendPicture(file.getAbsolutePath(), false);
        }

    }

    /**
     * 发送位置信息
     *
     * @param latitude
     * @param longitude
     * @param imagePath
     * @param locationAddress
     */
    private void sendLocationMsg(double latitude, double longitude,
                                 String imagePath, String locationAddress) {
        conversationListView.setSelection(conversationListView.getCount() - 1);
        setResult(RESULT_OK);

    }

    /**
     * 重发消息
     */
    private void resendMessage() {
        conversationListView.setSelection(resendPos);
    }

    /**
     * 获取表情的gridview的子view
     */
    private List<View> getExpressionViews() {
        List<String> expressionNames = ExpressionUtils.getExpressionNames();
        List<View> views = new ArrayList<>();

        for (int i = 0; i < expressionNames.size(); i += 20) {

            View view = View.inflate(this, R.layout.expression_gridview, null);
            ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
            List<String> list = new ArrayList<>();
            list.addAll(expressionNames.subList(i, Math.min(i + 20, expressionNames.size())));
            list.add("delete_expression");

            final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, R.layout.row_expression, list);
            gv.setAdapter(expressionAdapter);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String filename = expressionAdapter.getItem(position);
                    try {
                        // 文字输入框可见时，才可输入表情
                        // 按住说话可见，不让输入表情
                        if (keyboardModeButton.getVisibility() != View.VISIBLE) {

                            if (filename != "delete_expression") { // 不是删除键，显示表情
                                // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类

                                Class expressionClass = Class.forName(ExpressionUtils.class.getName());
                                Field field = expressionClass.getField(filename);
                                messageEditText.append(ExpressionUtils.getSmiledText(
                                        ChatActivity.this, (String) field.get(null)));
                            } else { // 删除文字或者表情
                                if (!TextUtils.isEmpty(messageEditText.getText())) {

                                    int selectionStart = messageEditText
                                            .getSelectionStart();// 获取光标的位置
                                    if (selectionStart > 0) {
                                        String body = messageEditText.getText()
                                                .toString();
                                        String tempStr = body.substring(0,
                                                selectionStart);
                                        int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                        if (i != -1) {
                                            CharSequence cs = tempStr.substring(i,
                                                    selectionStart);
                                            if (ExpressionUtils.containsKey(cs
                                                    .toString()))
                                                messageEditText.getEditableText()
                                                        .delete(i, selectionStart);
                                            else
                                                messageEditText.getEditableText()
                                                        .delete(selectionStart - 1,
                                                                selectionStart);
                                        } else {
                                            messageEditText.getEditableText()
                                                    .delete(selectionStart - 1,
                                                            selectionStart);
                                        }
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                    }

                }
            });
            views.add(view);
        }

        return views;
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragment", "conversation");
        startActivity(intent);
    }

    /**
     * 消息广播接收者
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 记得把广播给终结掉
            abortBroadcast();

            String username = intent.getStringExtra("from");

            String msgid = intent.getStringExtra("msgid");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象

        }
    }

    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");

        }
    };

    /**
     * 消息送达BroadcastReceiver
     */
    private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
        }
    };
    private PowerManager.WakeLock wakeLock;

    /**
     * 按住说话listener
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @SuppressLint({"ClickableViewAccessibility", "Wakelock"})
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(ChatActivity.this, R.string.recoding_fail,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint
                                .setText(getString(R.string.release_to_cancel));
                        recordingHint
                                .setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                    } else {
                        // stop recording and send voice file
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, "发送失败，请检测服务器是否连接",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);

                    return false;
            }
        }
    }

    private class ListScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    if (view.getFirstVisiblePosition() == 0 && !isLoading) {
                        loadingMessageProcessBar.setVisibility(View.VISIBLE);
                        isLoading = false;
                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

        }

    }

    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;

        // 注销广播
        try {
            unregisterReceiver(receiver);
            receiver = null;
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(ackMessageReceiver);
            ackMessageReceiver = null;
            unregisterReceiver(deliveryAckMessageReceiver);
            deliveryAckMessageReceiver = null;
        } catch (Exception e) {
        }
    }

    protected void onPause() {
        super.onPause();
        if (wakeLock.isHeld())
            wakeLock.release();
    }

    /**
     * 加入到黑名单
     *
     * @param username
     */
    private void addUserToBlacklist(String username) {
    }

    /**
     * 覆盖手机返回键
     */
    public void onBackPressed() {
        if (textModeLayout.getVisibility() == View.VISIBLE) {
            textModeLayout.setVisibility(View.GONE);
            normalExpressionView.setVisibility(View.VISIBLE);
            checkedExpressionView.setVisibility(View.INVISIBLE);
        } else {
            // if(type==3){
            // startActivity(new
            // Intent(getApplicationContext(),MyGroupActivity.class));
            // finish();
            // }else if(type==2){
            // startActivity(new
            // Intent(getApplicationContext(),ContactListActivity.class));
            // finish();
            // }else if(type==1){
            // startActivity(new
            // Intent(getApplicationContext(),MainActivity.class));
            // finish();
            // }else{
            finish();
            // }
        }
    }

    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (friend.id.equals(username)) {

        } else {
            finish();
            startActivity(intent);
        }
    }

    /**
     * 转发消息
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (sentMessage) {
            sentMessage = false;
            AppConstant.conversationManager.addOrReplaceConversation(messageManager.getLastMessage());
        }
    }
}