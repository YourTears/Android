package appLogic;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.welove.database.MessageTable;

import chat.ConversationProxy;
import common.ImageLoaderManager;

/**
 * Created by tiazh on 4/22/2015.
 */
public class AppConstant {
    public static String id = null;
    public static String it = "她";

    public static UserManager userManager = null;
    public static UserInfo meInfo = null;

    public static String dataFolder = null;
    public static String cacheFolder = null;
    public static String imageFolder = null;

    public static Drawable defaultImageDrawable = null;

    public static InputMethodManager inputManager = null;

    public static ConversationManager conversationManager = null;

    public static View conversationView = null;

    public static ImageLoaderManager imageLoaderManager = null;

    public static String imageExtension = ".PNG";

    public static ConversationProxy conversationProxy = null;
    public static MessageTable messageTable = null;
}
