package com.example.matthew.mapdirections;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;

public class AddWaypointsToGenerateActivity extends AppCompatActivity {

    private ScrollView scrlWaypoints;
    private LinearLayout linearLayout;
    private ArrayList<String> waypoints;
    private View[] viewWaypoints;
    private int num_waypoints = 0;
    private int selected = -1;

    private final static int NEW_WAYPOINT_REQUEST_CODE = 0;
    private final static int MAP_REQUEST_CODE = 1;

    public final static String ADD_WAYPOINTS_GEN_WAYPOINTS = "com.example.matthew.ADD_WAYPOINTS_GEN_WAYPOINTS";
    public final static String ADD_WAYPOINTS_GEN_NUM_WAYPOINTS = "com.example.matthew.ADD_WAYPOINTS_GEN_NUM_WAYPOINTS";
    public final static String ADD_WAYPOINTS_GEN_HOTEL = "com.example.matthew.ADD_WAYPOINTS_GEN_HOTEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_waypoints_to_generate);

        scrlWaypoints = (ScrollView) findViewById(R.id.scrllWaypoints);
        scrlWaypoints.setBackgroundColor(Color.parseColor("#ffe3e3e3"));

        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setClickable(true);
        scrlWaypoints.addView(linearLayout);

        waypoints = new ArrayList<String>(20);
        viewWaypoints = new View[20];
    }

    public void onClickAddWaypointsGenStartTime(View view) {
        DialogFragment dialogFragment = new OnTimeFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                ((TextView) getActivity().findViewById(R.id.txtAddWaypointsGenStart)).setText(hour+":"+minute);
            }
        };
        dialogFragment.show(getSupportFragmentManager(), "time_new_day_start");
    }


    public void onClickAddWaypointsGenEndTime(View view) {
        DialogFragment dialogFragment = new OnTimeFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                ((TextView)getActivity().findViewById(R.id.txtAddWaypointsGenEnd)).setText(hour+":"+minute);
            }
        };
        dialogFragment.show(getSupportFragmentManager(), "time_new_day_start");
    }

    public void onClickAdd(View view)
    {
        Intent intent = new Intent(this, NewWaypointActivity.class);
        startActivityForResult(intent, NEW_WAYPOINT_REQUEST_CODE);
    }

    public void onClickMap(View view)
    {

    }

    public void onClickAddWaypointsGenRM(View view)
    {
        if (selected == num_waypoints - 1) {
        } else {
            for (int i = selected; i < num_waypoints - 1; i++) {
                viewWaypoints[i + 1].setId(i);
                viewWaypoints[i] = viewWaypoints[i + 1];
                waypoints.set(i, waypoints.get(i + 1));
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
        if (requestCode == NEW_WAYPOINT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                TextView textView = new TextView(this);
                textView.setText(data.getStringExtra(NewWaypointActivity.NEW_WAYPOINT_NAME));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selected != -1)
                            linearLayout.getChildAt(selected).setBackgroundColor(Color.parseColor("#ffe3e3e3"));
                        selected = v.getId();
                        v.setBackgroundColor(Color.GREEN);
                    }
                });
                linearLayout.addView(textView);
                viewWaypoints[num_waypoints] = textView;
                textView.setId(num_waypoints);
                num_waypoints++;
                waypoints.add(data.getStringExtra(NewWaypointActivity.NEW_WAYPOINT_ID));

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }
}