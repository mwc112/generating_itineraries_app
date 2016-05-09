package com.example.matthew.mapdirections;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
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

public class testService extends Service {

    private int NOTIFICATION_ID = 1;
    private String NOTIFICATION_TITLE = "Time to leave";
    private String NOTIFICATION_CONTENT = "";
    private String[] dests;
    private int[][] startEndtimes;
    private int[] timeToLeave;
    private boolean travelling = true;
    private int waypoint = 0;

    protected RequestQueue queue;

    public testService() {
    }

    @Override
    public void onCreate() {
        queue = Volley.newRequestQueue(this);

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListenerExtended(this) {
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

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        catch(SecurityException e) {}
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        handleCommand(intent);
        return START_STICKY;
    }

    private void handleCommand(Intent intent) {
        this.dests = intent.getStringArrayExtra(RunningTripActivity.RUNNING_TRIP_WAYPOINTS);
        this.startEndtimes = (int[][])intent.getSerializableExtra(RunningTripActivity.RUNNING_TRIP_TIMES);
        this.timeToLeave = new int[2];
        this.timeToLeave[0] = startEndtimes[0][0];
        this.timeToLeave[1] = startEndtimes[0][1];
        setTimeToLeave(getTimeToLeaveNiceFormat());

        StringRequest request = new StringRequest(Request.Method.POST, "http://www.doc.ic.ac.uk/~mwc112/place_info.php" +
                "?place_id=" + dests[waypoint], new ListenerExtended<String>(this) {
            @Override
            public void onResponse(String response) {
                setPlaceToGo(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);
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
        Intent intent = new Intent(this, RunningTripActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

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
        StringRequest request = new StringRequest(Request.Method.GET, Uri.parse("http://www.doc.ic.ac.uk/~mwc112/closest_places.php" +
                "?lat_lng=" + location.getLatitude() + "," + location.getLongitude()).toString(), new ListenerExtended<String>(this) {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray places = new JSONArray(response);
                    for(int i = 0; i < places.length(); i++) {
                        if(places.get(i) == dests[waypoint]) {
                            if(i == dests.length) {}
                                //TODO
                                //reachedFinalWaypoint();
                            else {
                                //disableLocationUpdates();
                                //enableTimerForNextDest();
                                travelling = false;
                            }
                            break;
                        }
                    }
                }
                catch(Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

}
