package com.example.matthew.mapdirections;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

public class RunningTripActivity extends AppCompatActivity {

    public static String RUNNING_TRIP_HOTEL = "com.example.matthew.RUNNING_TRIP_HOTEL";
    public static String RUNNING_TRIP_WAYPOINTS = "com.example.matthew.RUNNING_TRIP_WAYPOINTS";
    public static String RUNNING_TRIP_TIMES = "com.example.matthew.RUNNING_TRIP_TIMES";
    public static String RUNNING_TRIP_TIME_TO_STAY = "com.example.matthew.RUNNING_TRIP_TIME_TO_STAY";

    private testService boundService;
    private ServiceConnection connection;
    private boolean isBound = false;

    private boolean isMap = true;

    private ScrollView directionsScroll;
    private WebView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_trip);

        mapView = new WebView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                0.95f);
        layoutParams.height = 1000;
        mapView.setLayoutParams(layoutParams);
        ((LinearLayout) findViewById(R.id.layoutRunningTrip)).addView(mapView, 0);
        mapView.getSettings().setJavaScriptEnabled(true);
        mapView.getSettings().setDomStorageEnabled(true);
        mapView.loadUrl("http://www.doc.ic.ac.uk/~mwc112/map.php");

        /*webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.evaluateJavascript("initMap();",null);
            }
        });*/

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                boundService = ((testService.TestBinder)service).getService();
                boundService.setRunningTripActivity(RunningTripActivity.this);
                boundService.setIsShowing(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        //bindService(new Intent(this, testService.class), connection, BIND_AUTO_CREATE);
        //isBound = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound) {
            boundService.repushNotification();
            boundService.setIsShowing(false);
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, testService.class), connection, BIND_AUTO_CREATE);
        isBound = true;

        mapView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mapView.evaluateJavascript("initMap();", null);
                //TODO: Does not actually draw route
                mapView.evaluateJavascript("draw_route(" + boundService.getRoute() + ");", null);
            }
        });
    }



    /*@Override
    protected void onRestart() {
        super.onRestart();
        bindService(new Intent(this, testService.class), connection, BIND_AUTO_CREATE);
        isBound = true;
    }*/

    public void onClickRunningTripStop(View view) {
        Intent intent = new Intent(this, testService.class);
        stopService(intent);
        unbindService(connection);
        isBound = false;
        Intent main_intent = new Intent(this, RootMenuActivity.class);
        finish();
        startActivity(main_intent);
    }

    public void onClickRunningTripSwap(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutRunningTrip);
        if(isMap) {
            linearLayout.removeView(mapView);
            directionsScroll = new ScrollView(this);
            directionsScroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    0.95f));
            linearLayout.addView(directionsScroll, 0);

            LinearLayout newLinearLayout = new LinearLayout(this);
            newLinearLayout.setOrientation(LinearLayout.VERTICAL);
            newLinearLayout.setClickable(true);
            directionsScroll.addView(newLinearLayout);
            populateDirectionsList(newLinearLayout);
            isMap = false;
        }
        else {
            linearLayout.removeView(directionsScroll);
            linearLayout.addView(mapView, 0);
            isMap = true;
        }
    }

    private void populateDirectionsList(LinearLayout linearLayout) {
        String route = boundService.getRoute();

        try {
            JSONObject jsonObject = new JSONObject(route);
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            JSONObject leg = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
            textView.setText("Travel for " + leg.getJSONObject("duration").getString("text") + " to arrive at " +
                    leg.getJSONObject("arrival_time").getString("text"));
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextSize(22);
            linearLayout.addView(textView, 0);

            JSONArray steps = leg.getJSONArray("steps");

            for(int i = 0; i < steps.length(); i++) {
                TextView textViewStep = new TextView(this);
                textViewStep.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if(!steps.getJSONObject(i).getString("travel_mode").equals("TRANSIT")) {
                    textViewStep.setText(i + ". Travel for " + steps.getJSONObject(i).getJSONObject("duration").getString("text"));
                }
                else {
                    textViewStep.setText(i + ". Travel for " + steps.getJSONObject(i).getJSONObject("duration").getString("text") +
                            " to arrive at " + steps.getJSONObject(i).getJSONObject("transit_details").getJSONObject("departure_stop")
                            .getString("name"));
                }
                linearLayout.addView(textViewStep);
            }
        }
        catch (Exception e) {}

    }

}
