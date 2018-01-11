package com.linamaria.light_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.linamaria.light_android.external_communication.HttpManager;

/**
 * Created by LinaMaria on 12/20/2017.
 */

public class RoomContextAlarmReceiver extends BroadcastReceiver{

    private HttpManager httpm;
    private ContextManagementActivity cm;

    public RoomContextAlarmReceiver(HttpManager httpm, ContextManagementActivity cm) {
        this.httpm = httpm;
        this.cm = cm;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (cm.room != null)
            httpm.retrieveRoomContextState(cm.room.getRoom());
    }

}
