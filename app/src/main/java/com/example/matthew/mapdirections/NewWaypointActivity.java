package com.example.matthew.mapdirections;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import net.sf.json.JSONArray;

import org.json.JSONObject;

public class NewWaypointActivity extends AppCompatActivity {

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Place waypoint;
    public final static String NEW_WAYPOINT_ID = "com.example.matthew.NEW_WAYPOINT_ID";
    public final static String NEW_WAYPOINT_HOURS = "com.example.matthew.NEW_WAYPOINT_HOURS";
    public final static String NEW_WAYPOINT_NAME = "com.example.matthew.NEW_WAYPOINT_NAME";
    public final static String NEW_WAYPOINT_LATLNG = "com.example.matthew.NEW_WAYPOINT_LATLNG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_waypoint);
        set_up_maps();
    }

    private void set_up_maps()
    {
        WebView webView = (WebView) findViewById(R.id.webAddWaypoint);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/simplemap.html");
    }

    public void onClickNewWaypointTxtWaypoint(View view)
    {
        try {
            Intent searchIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(searchIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        }
        catch(GooglePlayServicesRepairableException e) {

        }
        catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                ((TextView)findViewById(R.id.txtAddWaypointWaypoint)).setText(place.getName());
                waypoint = place;

                /*WebView webView = ((WebView) findViewById(R.id.webAddWaypoint));
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        WebView webView = (WebView) findViewById(R.id.webAddWaypoint);
                        webView.evaluateJavascript("create_markers(" + waypoint.getAddress() + ", 1);", null);
                    }
                });*/

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    public void onClickNewWaypointResult(View view) {
        Intent intent = new Intent();
        intent.putExtra(NEW_WAYPOINT_ID, waypoint.getId());
        intent.putExtra(NEW_WAYPOINT_HOURS, ((EditText) findViewById(R.id.txtAddWaypointHours)).getText());
        intent.putExtra(NEW_WAYPOINT_NAME, waypoint.getName());
        intent.putExtra(NEW_WAYPOINT_LATLNG, waypoint.getLatLng().toString());

        setResult(RESULT_OK, intent);
        finish();
    }
}
