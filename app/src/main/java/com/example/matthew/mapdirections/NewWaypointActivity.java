package com.example.matthew.mapdirections;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;


public class NewWaypointActivity extends AppCompatActivity {

    /*
    * Activity used to find location and add time to waypoint
     */

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String[] waypoint;
    public final static String NEW_WAYPOINT_WAYPOINT = "com.example.matthew.NEW_WAYPOINT_WAYPOINT";

    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_waypoint);

        queue = Volley.newRequestQueue(this);

        waypoint = new String[4];
    }

    @Override
    protected void onStart() {
        super.onStart();
        set_up_maps();
    }

    private void set_up_maps()
    {
        WebView webView = (WebView) findViewById(R.id.webAddWaypoint);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://178.62.46.132/map");
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
                waypoint[0] = place.getName().toString();
                waypoint[1] = place.getId().toString();
                waypoint[2] = place.getLatLng().toString().substring(10,
                        place.getLatLng().toString().length() - 2);

                NewWaypointActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WebView webView = ((WebView) findViewById(R.id.webAddWaypoint));
                        webView.evaluateJavascript("(function() {create_markers([\"" + waypoint[1] + "\"], 1); })();", null);
                    }
                });

                StringRequest request = new StringRequest(Request.Method.GET, "http://178.62.46.132/time?place_type=" +
                        place.getPlaceTypes().get(0).toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                ((EditText)findViewById(R.id.txtAddWaypointHours)).setText(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((EditText)findViewById(R.id.txtAddWaypointHours)).setText("Error");
                    }
                });
                queue.add(request);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    public void onClickNewWaypointResult(View view) {
        Intent intent = new Intent();
        intent.putExtra(NEW_WAYPOINT_WAYPOINT, waypoint);
        waypoint[3] = ((TextView)findViewById(R.id.txtAddWaypointHours)).getText().toString();
        setResult(RESULT_OK, intent);
        finish();
    }
}
