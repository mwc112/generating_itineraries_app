package com.example.matthew.mapdirections;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;

public class testService extends Service {

    private int NOTIFICATION_ID = 1;
    private String NOTIFICATION_TITLE = "Time to leave";
    private String NOTIFICATION_CONTENT = "";
    private String[] dests;
    private int[][] startEndtimes;
    private int[] timeToLeave;
    private boolean travelling = true;
    private int waypoint = 1;
    private String hotel;

    private boolean isShowing = true;

    private String route;

    private RunningTripActivity runningTripActivity;
    private RunningTripAtLocActivity runningTripAtLocActivity;

    protected RequestQueue queue;

    private LocationListener locationListener;

    private PendingIntent pendingIntent;

    private double[] latLng;

    private int[] timesToStay;

    private Receiver receiver;
    private Receiver2 receiver2;
    private EveryMinuteReceiver everyMinuteReceiver;

    private int timeUntilTravel;

    private String nextWaypointName;

    public testService() {
    }

    @Override
    public void onCreate() {
        latLng = new double[2];
        queue = Volley.newRequestQueue(this);

        locationListener = new LocationListenerExtended(this) {
            @Override
            public void onLocationChanged(Location location) {
                handleLocChange(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        handleCommand(intent);
        return START_STICKY;
    }

    private void handleCommand(Intent intent) {
        this.dests = intent.getStringArrayExtra(RunningTripActivity.RUNNING_TRIP_WAYPOINTS);
        this.startEndtimes = (int[][])intent.getSerializableExtra(RunningTripActivity.RUNNING_TRIP_TIMES);
        this.hotel = intent.getStringExtra(RunningTripActivity.RUNNING_TRIP_HOTEL);
        this.timeToLeave = new int[2];
        this.timeToLeave[0] = startEndtimes[0][0];
        this.timeToLeave[1] = startEndtimes[0][1];
        this.timesToStay = intent.getIntArrayExtra(RunningTripActivity.RUNNING_TRIP_TIME_TO_STAY);
        setTimeToLeave(getTimeToLeaveNiceFormat());

        StringRequest request1 = new StringRequest(Request.Method.GET, "http://www.doc.ic.ac.uk/~mwc112/get_route.php" +
                "?origin=" + dests[1] + "&destination=" + Uri.parse(dests[0]).toString(), new ListenerExtended<String>(this) {
            @Override
            public void onResponse(String response) {
                route = response.substring(1);

                LocationManager locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);

                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
                }
                catch(SecurityException e) {}

                StringRequest request = new StringRequest(Request.Method.POST, "http://www.doc.ic.ac.uk/~mwc112/place_info.php" +
                        "?place_id=" + dests[waypoint], new ListenerExtended<String>(c) {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            setPlaceToGo(jsonObject.getString("name"));
                            nextWaypointName = jsonObject.getString("name");
                            latLng[0] = jsonObject.getDouble("latitude");
                            latLng[1] = jsonObject.getDouble("longitude");

                            Intent actIntent = new Intent(c, RunningTripActivity.class);
                            actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(actIntent);

                            Intent savedIntent = new Intent();
                            savedIntent.setAction("com.example.SAVEDTRIPSACTIVITY");
                            sendBroadcast(savedIntent);
                        }
                        catch (Exception e) {}
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                queue.add(request);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request1);

        Intent runningIntent = new Intent(this, RunningTripActivity.class);
        pendingIntent = PendingIntent.getActivity(this,0,runningIntent,0);
    }

    public String getTimeToLeaveNiceFormat() {
        StringBuilder sb = new StringBuilder();
        if(timeToLeave[0] < 10) {
            sb.append("0");
        }
        sb.append(timeToLeave[0]).append(":");

        if(timeToLeave[1] < 10) {
            sb.append("0");
        }
        sb.append(timeToLeave[1]);
        return sb.toString();
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
                .setContentText(NOTIFICATION_CONTENT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager notman = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notman.notify(NOTIFICATION_ID, mBuilder.build());
    }

    protected void handleLocChange(Location location) {
                try {
                    double dist = computeDistance(location.getLatitude(), location.getLongitude());
                        if(dist < 0.25) {
                            if(waypoint == dests.length - 1) {
                                disableLocUpdates();
                                if(isShowing) {
                                    runningTripActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            runningTripActivity.finish();
                                        }
                                    });
                                }
                                else {
                                    NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.cancel(NOTIFICATION_ID);
                                }
                                Intent mainIntent = new Intent(this, RootMenuActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainIntent);
                                stopSelf();
                            }
                            else {
                                disableLocUpdates();
                                setAlarmForIn(timesToStay[waypoint]);
                                //setMinuteNotifAlarm();
                                //setTimeToLeave(timesToStay);
                                travelling = false;
                                if(isShowing)
                                    switchToAtLoc();
                                else
                                    switchNotDetails();
                            }
                        }
                }
                catch(Exception e) {}
    }

    public String getRoute() {
        return route;
    }

    private void disableLocUpdates() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        try {
            locationManager.removeUpdates(locationListener);
        }
        catch(SecurityException e) {}
    }

    public void setRunningTripActivity(RunningTripActivity activity) {
        this.runningTripActivity = activity;
    }

    public void setRunningTripAtLocActivity(RunningTripAtLocActivity activity) {
        this.runningTripAtLocActivity = activity;
    }

    private void switchToAtLoc() {
        setPlaceToGo("ARRIVED AT LOCATION");
        setTimeToLeave(Integer.toString(timesToStay[waypoint]) + " minutes remaining");
        Intent runningIntent = new Intent(this, RunningTripAtLocActivity.class);
        pendingIntent = PendingIntent.getActivity(this,0,runningIntent,0);
        Intent intent = new Intent(this, RunningTripAtLocActivity.class);
        startActivity(intent);
        runningTripActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                runningTripActivity.finish();
            }
        });
    }

    public void setIsShowing(boolean showing) {
        this.isShowing = showing;
    }

    private void switchNotDetails() {
        setPlaceToGo("ARRIVED AT LOCATION");
        setTimeToLeave(Integer.toString(timesToStay[waypoint]) + " minutes remaining");
        Intent runningIntent = new Intent(this, RunningTripAtLocActivity.class);
        pendingIntent = PendingIntent.getActivity(this,0,runningIntent,0);
        //TODO: change pending intent once arrived
        //Intent runningIntent = new Intent(this, RunningTripActivity.class);
        //pendingIntent = PendingIntent.getActivity(this,0,runningIntent,0);
        repushNotification();
    }

    private double computeDistance(double lat, double lng) {
        int R = 6371;
        double dLat = degToRad(lat - latLng[0]);
        double dLng = degToRad(lng - latLng[1]);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(degToRad(latLng[0])) * Math.cos(degToRad(lat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = R * c; //Distance in km

        return d;
    }

    private double degToRad(double deg) {
        return deg * (Math.PI / 180.0);
    }

    private void setAlarmForIn(int hours) {
        IntentFilter intentFilter = new IntentFilter("com.example.Receiver");
        receiver = new Receiver();
        if(receiver2 != null) {
            unregisterReceiver(receiver2);
        }
        registerReceiver(receiver, intentFilter);

        Intent intent = new Intent();
        intent.setAction("com.example.Receiver");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,intent, 0);

        //TODO: Change to be minutes rather than hours
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                //(timesToStay[waypoint] * 3600 * 1000) - (300000), pendingIntent);
                5000, pendingIntent);

    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long[] pattern = {0,400,400,400,400,400,400,400};

            Vibrator v = (Vibrator) testService.this.getSystemService(testService.this.VIBRATOR_SERVICE);
            v.vibrate(pattern, -1);

            Intent intent2 = new Intent();
            intent2.setAction("com.example.Receiver");
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(testService.this, 0,intent2, 0);

            IntentFilter intentFilter = new IntentFilter("com.example.Receiver");
            receiver2 = new Receiver2();
            unregisterReceiver(receiver);
            registerReceiver(receiver2, intentFilter);

            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                    //300000, pendingIntent2);
                    5000, pendingIntent2);
        }
    }

    private class Receiver2 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Vibrator v = (Vibrator) testService.this.getSystemService(testService.this.VIBRATOR_SERVICE);
            long[] pattern = {0,400,400,400,400,400,400,400,400,400,400,400,400,400,400};
            v.vibrate(pattern, -1);

            travelling = true;
            waypoint++;
            reactToTimer();
        }
    }

    private void startLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
        }
        catch(SecurityException e) {}
    }

    private void reactToTimer() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://www.doc.ic.ac.uk/~mwc112/place_info.php" +
                "?place_id=" + dests[waypoint], new ListenerExtended<String>(this) {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    setPlaceToGo(jsonObject.getString("name"));
                    latLng[0] = jsonObject.getDouble("latitude");
                    latLng[1] = jsonObject.getDouble("longitude");

                    StringRequest request = new StringRequest(Request.Method.GET, "http://www.doc.ic.ac.uk/~mwc112/get_route.php" +
                            "?origin=" + dests[waypoint - 1] + "&destination=" + Uri.parse(dests[waypoint]).toString(), new ListenerExtended<String>(c) {
                        @Override
                        public void onResponse(String response) {
                            startLocation();
                            //TODO: set time to leave as current time + time to stay
                            setTimeToLeave("");

                            route = response.substring(1);

                            Intent runningIntent = new Intent(c, RunningTripActivity.class);
                            pendingIntent = PendingIntent.getActivity(testService.this, 0, runningIntent, 0);

                            if(!isShowing) {
                                repushNotification();
                            }
                            else {
                                runningTripAtLocActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        runningTripAtLocActivity.finish();
                                    }
                                });
                                Intent actIntent = new Intent(c, RunningTripActivity.class);
                                actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(actIntent);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    queue.add(request);
                }
                catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);
    }

    private void setMinuteNotifAlarm() {
        timeUntilTravel = timesToStay[waypoint];
        everyMinuteReceiver = new EveryMinuteReceiver();

        IntentFilter intentFilter = new IntentFilter("com.example.testServiceMinuteNotif");
        registerReceiver(everyMinuteReceiver, intentFilter);

        Intent intent = new Intent();
        intent.setAction("com.example.testServiceMinuteNotif");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(testService.this, 0,intent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                60000, pendingIntent);
    }

    private class EveryMinuteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            timeUntilTravel -= 1;
            NOTIFICATION_CONTENT = Integer.toString(timeUntilTravel) + " minutes remaining";

            if(!isShowing)
                repushNotification();
            else {
                runningTripAtLocActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)testService.this.runningTripAtLocActivity.findViewById(R.id.txtRunningTripAtLocTime)).setText(Integer.toString(timeUntilTravel) +
                                " minutes reamining");
                    }
                });
            }


            if(timeUntilTravel == 1) {
                unregisterReceiver(everyMinuteReceiver);
                return;
            }
            Intent intent2 = new Intent();
            intent2.setAction("com.example.testServiceMinuteNotif");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(testService.this, 0,intent2, 0);

            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                    60000, pendingIntent);
        }
    }

    public String getNextWaypointName() {
        return nextWaypointName;
    }

    public int getTimeUntilTravel() {
        return timeUntilTravel;
    }

}
