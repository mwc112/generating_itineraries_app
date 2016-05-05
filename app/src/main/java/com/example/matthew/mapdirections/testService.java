package com.example.matthew.mapdirections;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class testService extends Service {

    // TODO: ON HOLD UNTIL more useful information passed

    private int NOTIFICATION_ID = 1;
    private String NOTIFICATION_TITLE = "Time to leave";
    private String NOTIFICATION_CONTENT = "";
    private JSONArray legs;
    private String currentDest;
    private String currentDepTime;

    public testService() {
    }

    @Override
    public void onCreate() {

        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setContentTitle("Time to leave")
                .setContentText("New notification text");
        NotificationManager notman = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notman.notify(NOTIFICATION_ID, mBuilder.build());*/
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        handleCommand(intent);

        return START_STICKY;
    }

    private void handleCommand(Intent intent) {
        try {
            JSONObject routes = new JSONObject(intent.getStringExtra(RunningTripActivity.RUNNING_TRIP_ROUTES));
            legs = routes.getJSONArray("legs");
            JSONObject leg = legs.getJSONObject(0);
            JSONObject departureTime = leg.getJSONObject("departure_time");
            //currentDest =
        }
        catch(Exception e) {}
    }

    private final IBinder binder = new TestBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class TestBinder extends Binder {
        testService getService() {
            return testService.this;
        }
    }

    public void setTimeToLeave(String time) {
        NOTIFICATION_CONTENT = time;
    }

    public void setPlaceToGo(String place) {
        NOTIFICATION_TITLE = place;
    }

    public void repushNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_CONTENT);
        NotificationManager notman = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notman.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
