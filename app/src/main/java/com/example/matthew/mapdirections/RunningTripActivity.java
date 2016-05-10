package com.example.matthew.mapdirections;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class RunningTripActivity extends AppCompatActivity {

    public static String RUNNING_TRIP_HOTEL = "com.example.matthew.RUNNING_TRIP_HOTEL";
    public static String RUNNING_TRIP_WAYPOINTS = "com.example.matthew.RUNNING_TRIP_WAYPOINTS";
    public static String RUNNING_TRIP_TIMES = "com.example.matthew.RUNNING_TRIP_TIMES";

    private testService boundService;
    private ServiceConnection connection;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_trip);

        final WebView webView = (WebView) findViewById(R.id.webRunningTrip);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("http://www.doc.ic.ac.uk/~mwc112/map.php");

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
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, testService.class), connection, BIND_AUTO_CREATE);
        isBound = true;

        final WebView webView = (WebView) findViewById(R.id.webRunningTrip);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.evaluateJavascript("initMap();", null);
                //TODO: Does not actually draw route
                webView.evaluateJavascript("draw_route(" + boundService.getRoute() + ");",null);
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

}
