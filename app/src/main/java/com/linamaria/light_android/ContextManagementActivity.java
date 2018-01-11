package com.linamaria.light_android;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.linamaria.light_android.RoomContextState.Status;
import com.android.volley.RequestQueue;
import com.linamaria.light_android.context_rules.RoomContextRule;
import com.linamaria.light_android.context_rules.RuleCreator;
import com.linamaria.light_android.external_communication.HttpManager;
import com.linamaria.light_android.external_communication.Mqtt;
import com.linamaria.light_android.external_communication.SingleHtmlQueue;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ContextManagementActivity extends AppCompatActivity implements Response.Listener<JSONObject> {

    private HttpManager httpm;
    private Mqtt mqtt;

    public RoomContextState room;
    private List<RoomContextRule> rules;
    private int count_notification = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_management);

        // Queue for REST requests
        RequestQueue queue = SingleHtmlQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        httpm = new HttpManager(queue, this);
        try {
            mqtt = new Mqtt(this);
            mqtt.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        // Context rules
        rules = RuleCreator.createRules(this);

        // Alarm to update every 10 seconds
        Intent i = new Intent(this, RoomContextAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pi); // cancel any existing alarms
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 5000, 5000, pi);
    }

    // --------------------------------------------------------------------------------------------
    //                         CHANGING THE VIEW
    //---------------------------------------------------------------------------------------------

    public void displayMessage(String text){
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(getApplicationContext(), text, duration).show();
    }

    public void updateContextView(){
        TextView lightLevelView = (TextView) findViewById(R.id.textViewLightValue);
        TextView noiseLevelView = (TextView) findViewById(R.id.textViewNoiseValue);
        ImageView lightImageView = (ImageView) findViewById(R.id.imageView1);
        ImageView ringerImageView = (ImageView) findViewById(R.id.imageView2);

        int lightImage = room.getLightStatus() == Status.ON ? R.drawable.ic_bulb_on : R.drawable.ic_bulb_off;
        int ringerImage = room.getRingerStatus() == Status.ON ? R.drawable.ic_ringer_on : R.drawable.ic_ringer_off;
        lightLevelView.setText(Integer.toString(room.getLightLevel()));
        noiseLevelView.setText(Float.toString(room.getNoiseLevel()));
        lightImageView.setImageResource(lightImage);
        ringerImageView.setImageResource(ringerImage);

        checkRules();
    }

    public void waitingConxtextView(){
        TextView lightLevelView = (TextView) findViewById(R.id.textViewLightValue);
        TextView noiseLevelView = (TextView) findViewById(R.id.textViewNoiseValue);
        ImageView lightImageView = (ImageView) findViewById(R.id.imageView1);
        ImageView ringerImageView = (ImageView) findViewById(R.id.imageView2);

        lightLevelView.setText(R.string.waiting);
        noiseLevelView.setText(R.string.waiting);
        lightImageView.setImageResource(R.drawable.waiting);
        ringerImageView.setImageResource(R.drawable.waiting);
    }

    // --------------------------------------------------------------------------------------------
    //                         REST REQUESTS: LIGHT, RINGER AND ROOM
    //---------------------------------------------------------------------------------------------

    public void retrieveRoomContextState(View view) {
        String roomId = ((EditText) findViewById(R.id.editText1)).getText().toString();
        waitingConxtextView();
        httpm.retrieveRoomContextState(roomId);
    }

    public void switchLight(View view) throws MqttException {
        if (room == null) return;
        waitingConxtextView();

        String newState = room.getLightStatus() == Status.ON ? "OFF" : "ON";
        mqtt.sendMessage("rl/state/light/" + room.getRoom(), newState );
        httpm.switchLight(room.getRoom());
    }

    public void switchRinger(View view) throws MqttException {
        if (room == null) return;
        waitingConxtextView();

        String newState = room.getRingerStatus() == Status.ON ? "OFF" : "ON";
        mqtt.sendMessage("rl/state/noise/" + room.getRoom(), newState );
        httpm.switchRinger(room.getRoom());
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            this.room = jsonToRoom(response);
            updateContextView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public RoomContextState jsonToRoom(JSONObject response) throws JSONException {

        JSONObject light = response.getJSONObject("light");
        JSONObject noise = response.getJSONObject("noise");
        String id = response.getString("id").toString();
        int lightLevel = Integer.parseInt(light.get("level").toString());
        Status lightStatus =  Status.valueOf(light.get("status").toString());
        int noiseLevel = Integer.parseInt(noise.get("level").toString());
        Status ringerStatus =  Status.valueOf(noise.get("status").toString());
        return new RoomContextState(id,lightStatus,ringerStatus,lightLevel,noiseLevel);
    }

    // --------------------------------------------------------------------------------------------
    //                         ACTIONS: Mode AND RULES
    //---------------------------------------------------------------------------------------------

    public void switchMode(View view) {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int mode = audioManager.getRingerMode();
        if (mode == AudioManager.RINGER_MODE_SILENT) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            displayMessage("\"Do no disturbed\" was turned OFF");
        } else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            displayMessage("\"Do no disturbed\" was turned ON");
        }
    }

    public void checkRules(){
        for (RoomContextRule rule: rules){
            rule.apply(room);
        }
    }

    public int increaseAndGetCounter(){
        return ++count_notification;
    }


}
