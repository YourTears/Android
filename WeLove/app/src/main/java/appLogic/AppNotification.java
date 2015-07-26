package appLogic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.welove.activity.MainActivity;
import com.welove.app.R;

/**
 * Created by Long on 7/26/2015.
 */
public class AppNotification {
    private static int NOTIFICATION_FLAG = 1;

    private NotificationManager manager = null;
    private Context context = null;
    private static AppNotification instance = null;

    private AppNotification(NotificationManager manager, Context context){
        this.manager = manager;
        this.context = context;
    }

    public static void initialize(NotificationManager manager, Context context) {
        instance = new AppNotification(manager, context);
    }

    public static AppNotification getInstance(){
        return instance;
    }

    public void sendNotification(String title, String content, MainActivity.FragmentType fragmentType){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("fragment", fragmentType.toString());

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notify = new Notification.Builder(context)
                .setSmallIcon(R.drawable.logo_welove)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setNumber(1)
                .getNotification();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;

        manager.notify(NOTIFICATION_FLAG ++, notify);
    }
}
