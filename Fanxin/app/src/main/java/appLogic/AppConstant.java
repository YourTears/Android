package appLogic;

import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import common.ImageLoaderManager;

/**
 * Created by tiazh on 4/22/2015.
 */
public class AppConstant {
    public static boolean isLogin = true;

    public static FriendManager friendManager = null;
    public static MeInfo meInfo = null;

    public static String dataFolder = null;
    public static String imageFolder = null;

    public static Drawable defaultImageDrawable = null;

    public static InputMethodManager inputManager = null;

    public static ConversationManager conversationManager = null;

    public static View conversationView = null;

    public static ImageLoaderManager imageLoaderManager = null;

    public static String imageExtension = ".PNG";
}
