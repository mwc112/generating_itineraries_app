package com.example.matthew.mapdirections;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
* Activity to list and store waypoints before
* A. The order of the waypoints has been decided
* B. The route has been generated
*
* TODO: Use generated order of waypoints rather than just order selected
 */

public class AddWaypointsToGenerateActivity extends AppCompatActivity {

    private ScrollView scrlWaypoints;
    private LinearLayout linearLayout;
    private View[] viewWaypoints;
    private String[][] waypoints;
    private int num_waypoints = 0;
    private int selected = -1;
    private int[] start_time;
    private int[] end_time;
    private int[] date;

    private final int NEW_WAYPOINT_REQUEST_CODE = 0;
    private final int WAYPOINT_MAP_REQUEST_CODE = 1;
    private final int SELECT_DATE_REQUEST_CODE = 2;

    private final static String TAG = "AddWptsToGenActivity";
    public final static String ADD_WAYPOINTS_GEN_WAYPOINTS = "com.example.matthew.ADD_WAYPOINTS_GEN_WAYPOINTS";
    public final static String ADD_WAYPOINTS_GEN_NUM_WAYPOINTS = "com.example.matthew.ADD_WAYPOINTS_GEN_NUM_WAYPOINTS";
    public final static String ADD_WAYPOINTS_GEN_HOTEL = "com.example.matthew.ADD_WAYPOINTS_GEN_HOTEL";
    public final static String ADD_WAYPOINTS_GEN_NUM = "com.example.matthew.ADD_WAYPOINTS_GEN_NUM";

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        * Sets up scroll view and arrays to store waypoints
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_waypoints_to_generate);

        scrlWaypoints = (ScrollView) findViewById(R.id.scrllAddWaypointsGenWaypoints);
        scrlWaypoints.setBackgroundColor(Color.parseColor("#ffe3e3e3"));

        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setClickable(true);
        scrlWaypoints.addView(linearLayout);

        waypoints = new String[20][];
        viewWaypoints = new View[20];
        start_time = new int[2];
        start_time[0] = 19;
        start_time[1] = 0;
        end_time = new int[2];
        end_time[0] = 19;
        end_time[1] = 0;

        queue = Volley.newRequestQueue(this);
    }

    public void onClickAddWaypointsGenStartTime(View view) {
        /*
        * Stores start time selected and formats printed time
         */
        DialogFragment dialogFragment = new OnTimeFragment();
        dialogFragment.show(getSupportFragmentManager(), "time_new_day_start");
    }


    public void onClickAddWaypointsGenEndTime(View view) {
        /*
        * Stores end time selected and formats printed time
         */
        DialogFragment dialogFragment = new OnTimeFragment();
        dialogFragment.show(getSupportFragmentManager(), "time_new_day_start");
    }

    public void onClickAddWaypointsGenAdd(View view)
    {
        Intent intent = new Intent(this, NewWaypointActivity.class);
        startActivityForResult(intent, NEW_WAYPOINT_REQUEST_CODE);
    }

    public void onClickAddWaypointsGenMap(View view) {
        /*
        * Builds request for shortest route and checks if too long for specified time
         */
        int sum = 0;
        for(int i = 0; i < num_waypoints; i++) {
            sum += Integer.parseInt(waypoints[i][3]);
        }
        final int sum2 = sum;
        if(sum * 60 > ((end_time[0] * 60 + end_time[1]) - (start_time[0] * 60 + start_time[1]))) {
            ((TextView) findViewById(R.id.txtAddWaypointsGenWarn)).setText(R.string.add_waypoints_activities_too_long);
            return;
        }

        ArrayList<String> to_pass = new ArrayList<>();

        for(int i = 0; i < num_waypoints; i++) {
            to_pass.add(waypoints[i][2]);
        }

        JSONArray jsonToPass = new JSONArray(to_pass);

        StringRequest request = new StringRequest(Request.Method.POST, Uri.parse("http://www.doc.ic.ac.uk/~mwc112/shortest_path.php" +
                "?waypoints="+jsonToPass).toString(), new ListenerExtended<String>(this) {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray a = new JSONArray(response.substring(1));

                    //Check if travel time and time at locations is too long
                    if ((int)a.get(a.length() - 1) + sum2 * 3600 >
                            ((end_time[0] * 3600 + end_time[1] * 60) - (start_time[0] * 3600 + start_time[1] * 60))) {
                        ((TextView) findViewById(R.id.txtAddWaypointsGenWarn)).setText(R.string.add_waypoints_day_too_long);
                        return;
                    }
                    else {
                        //Pass the information to the map activity to get directions and be displayed
                        ((TextView)findViewById(R.id.txtAddWaypointsGenEnd)).setText(R.string.add_waypoints_to_gen_temp);

                        Intent child_intent = new Intent(c, WaypointsMapActivity.class);
                        child_intent.putExtra(WaypointsMapActivity.WAYPOINTS_MAP_WAYPOINTS, waypoints);
                        child_intent.putExtra(WaypointsMapActivity.WAYPOINTS_MAP_NUM_WAYPOINTS, num_waypoints);
                        child_intent.putExtra(WaypointsMapActivity.WAYPOINTS_MAP_HOTEL, "Buckingham Palace, London");
                        child_intent.putExtra(WaypointsMapActivity.WAYPOINTS_MAP_DATE, date);
                        child_intent.putExtra(WaypointsMapActivity.WAYPOINTS_MAP_TIME, start_time);

                        startActivityForResult(child_intent, WAYPOINT_MAP_REQUEST_CODE);
                    }

                }
                catch (JSONException e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);

    }

    public void onClickAddWaypointsGenRM(View view)
    {
        if (selected == num_waypoints - 1) {
        } else {
            for (int i = selected; i < num_waypoints - 1; i++) {
                viewWaypoints[i + 1].setId(i);
                viewWaypoints[i] = viewWaypoints[i + 1];
                waypoints[i] = waypoints[i+1];
            }
        }

        num_waypoints--;
        selected = -1;
        linearLayout.removeAllViews();

        for (int i = 0; i < num_waypoints; i++) {
            linearLayout.addView(viewWaypoints[i]);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        * On receiving a new waypoint from activity store it
         */

        if (requestCode == NEW_WAYPOINT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Waypoint received from NewWaypointActivity");
                String[] result = data.getStringArrayExtra(NewWaypointActivity.NEW_WAYPOINT_WAYPOINT);

                TextView newWaypointTextView = new TextView(this);
                newWaypointTextView.setText(result[0]);
                newWaypointTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selected != -1)
                            linearLayout.getChildAt(selected).setBackgroundColor(Color.parseColor("#ffe3e3e3"));
                        selected = v.getId();
                        v.setBackgroundColor(Color.GREEN);
                    }
                });
                linearLayout.addView(newWaypointTextView);
                viewWaypoints[num_waypoints] = newWaypointTextView;
                newWaypointTextView.setId(num_waypoints);
                waypoints[num_waypoints] = result;
                num_waypoints++;
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.e(TAG, "Error: Waypoint not received from NewWaypointActivity");
            }
        }
        else if(requestCode == SELECT_DATE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Log.i(TAG, "Date received from SelectDateActivity");
                Toast.makeText(getBaseContext(), R.string.add_waypoints_date_success,
                        Toast.LENGTH_LONG).show();
                date = data.getIntArrayExtra(SelectDateActivity.SELECT_DATE_DATE);
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Log.e(TAG, "Error: error received from SelectDateActivity");
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), R.string.add_waypoints_date_cancel,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClickAddWaypointsGenDate(View view) {
        Intent intent = new Intent(this, SelectDateActivity.class);
        startActivityForResult(intent, SELECT_DATE_REQUEST_CODE);
    }

    public static class OnTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            return new TimePickerDialog(getActivity(), this, 19, 0, true);
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
        }

    }
}
