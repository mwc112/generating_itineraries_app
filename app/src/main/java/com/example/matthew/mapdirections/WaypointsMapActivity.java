package com.example.matthew.mapdirections;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

//TODO: implement on button click methods

public class WaypointsMapActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waypoints_map);
        intent = getIntent();

        set_up_maps();
        route_waypoints();
    }

    private void set_up_maps()
    {
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/simplemap.html");
    }

    private void plot_waypoints()
    {
        ArrayList<String> waypoints = intent.getStringArrayListExtra(AddWaypointsActivity.MAP_WAYPOINTS);
        final int numWaypoints = intent.getIntExtra(AddWaypointsActivity.NUM_MAP_WAYPOINTS, 0);
        final JSONArray jsonArray = new JSONArray(waypoints);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                WebView webView = (WebView) findViewById(R.id.webview);
                webView.evaluateJavascript("create_markers(" + jsonArray.toString() + "," + numWaypoints + ");", null);
            }
        });

    }

    private void route_waypoints() {
        ArrayList<String> waypoints = intent.getStringArrayListExtra(AddWaypointsActivity.MAP_WAYPOINTS);
        final int num_waypoints = intent.getIntExtra(AddWaypointsActivity.NUM_MAP_WAYPOINTS, 0);
        final String hotel = intent.getStringExtra(AddWaypointsActivity.MAP_HOTEL);
        final JSONArray jsonArray = new JSONArray(waypoints);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url)
            {
                WebView webView = (WebView) findViewById(R.id.webview);
                webView.evaluateJavascript("get_dir("+jsonArray.toString()+","+num_waypoints+","+
                        '"'+hotel+'"'+");", null);
            }
        });
    }

    public void onClickMapsConfirm(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
