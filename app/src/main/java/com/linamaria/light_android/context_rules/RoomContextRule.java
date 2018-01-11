package com.linamaria.light_android.context_rules;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;

import com.linamaria.light_android.ContextManagementActivity;
import com.linamaria.light_android.R;
import com.linamaria.light_android.RoomContextState;

/**
 * Created by LinaMaria on 12/20/2017.
 */

public abstract class RoomContextRule {

    private ContextManagementActivity cm;

    public RoomContextRule(ContextManagementActivity cm) {
        this.cm = cm;
    }

    public void apply(RoomContextState context) {
        if (condition(context)) {
            action();
            notify(cm.getApplicationContext(), this, cm.room.getRoom());
        }
    }

    protected abstract boolean condition(RoomContextState context);

    protected abstract void action();

    private void notify(Context context, RoomContextRule rule, String room) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.waiting) // TODO notification_icon
                .setContentTitle("AmI Context Rule Triggered")
                .setContentText(rule + " is applied for room " + room + "!");
        mBuilder.setContentIntent(null);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(cm.increaseAndGetCounter(), mBuilder.build());
    }


}
