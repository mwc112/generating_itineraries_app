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
    private int waypoint = 0;
    private String hotel;

    private boolean isShowing = true;

    private String route;

    private RunningTripActivity runningTripActivity;
    private RunningTripAtLocActivity runningTripAtLocActivity;

    protected RequestQueue queue;

    private LocationListener locationListener;

    private PendingIntent pendingIntent;

    private double[] latLng;

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
        setTimeToLeave(getTimeToLeaveNiceFormat());

        StringRequest request1 = new StringRequest(Request.Method.GET, "http://www.doc.ic.ac.uk/~mwc112/get_route.php" +
                "?origin=" + dests[1] + "&destination=" + Uri.parse(dests[0]).toString(), new ListenerExtended<String>(this) {
            @Override
            public void onResponse(String response) {
                route = response.substring(1);
                Intent actIntent = new Intent(c, RunningTripActivity.class);
                actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(actIntent);

                LocationManager locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);

                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
                }
                catch(SecurityException e) {}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request1);

        StringRequest request = new StringRequest(Request.Method.POST, "http://www.doc.ic.ac.uk/~mwc112/place_info.php" +
                "?place_id=" + dests[0], new ListenerExtended<String>(this) {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    setPlaceToGo(jsonObject.getString("name"));
                    latLng[0] = jsonObject.getDouble("latitude");
                    latLng[1] = jsonObject.getDouble("longitude");
                }
                catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);

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
                            if(waypoint == dests.length) {}
                                //TODO
                                //reachedFinalWaypoint();
                            else {
                                disableLocUpdates();
                                //enableTimerForNextDest();
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

    private void enableTimerForNextDest() {

    }

    public void setRunningTripActivity(RunningTripActivity activity) {
        this.runningTripActivity = activity;
    }

    public void setRunningTripAtLocActivity(RunningTripAtLocActivity activity) {
        this.runningTripAtLocActivity = activity;
    }

    private void switchToAtLoc() {
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

}
