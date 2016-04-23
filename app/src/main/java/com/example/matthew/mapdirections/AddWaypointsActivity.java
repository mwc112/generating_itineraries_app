package com.example.matthew.mapdirections;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;

//TODO: Use offset instead of shuffling all values
//TODO: Finish removing elements - either tell map number of elements or physically remove from arraylist
//TODO: Need to know what day it is for when things are open
//TODO: Need to know when things are open

public class AddWaypointsActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private ArrayList<String> waypoints;
    private ScrollView scrlWaypoints;
    private View[] viewWaypoints;
    private int selected = 0;
    private int num_waypoints = 0;
    public final static String MAP_WAYPOINTS = "com.example.matthew.MAP_WAYPOINTS";
    public final static String NUM_MAP_WAYPOINTS = "com.example.matthew.NUM_MAP_WAYPOINTS";
    public final static String MAP_HOTEL = "com.example.matthew.MAP_HOTEL";
    public final static String RESULT_ADD_WAYPOINT_WAYPOINTS = "com.example.matthew.RESULT_ADD_WAYPOINT_WAYPOINTS";
    public final static String RESULT_ADD_WAYPOINT_NUM_WAYPOINTS = "com.example.matthew.RESULT_ADD_WAYPOINT_NUM_WAYPOINTS";
    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final static int NEW_WAYPOINT_REQUEST_CODE = 3;
    private final static int MAP_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_waypoints);

        scrlWaypoints = (ScrollView) findViewById(R.id.scrllWaypoints);
        scrlWaypoints.setBackgroundColor(Color.parseColor("#ffe3e3e3"));

        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setClickable(true);
        scrlWaypoints.addView(linearLayout);

        waypoints = new ArrayList<String>(20);
        viewWaypoints = new View[20];

    }

    public void onClickAddWaypointsStartTime(View view) {
        DialogFragment dialogFragment = new OnTimeFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                ((TextView) getActivity().findViewById(R.id.txtAddWaypointsStart)).setText(hour+":"+minute);
            }
        };
        dialogFragment.show(getSupportFragmentManager(), "time_new_day_start");
    }


    public void onClickAddWaypointsEndTime(View view) {
        DialogFragment dialogFragment = new OnTimeFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                ((TextView)getActivity().findViewById(R.id.txtAddWaypointsEnd)).setText(hour+":"+minute);
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
        Intent intent = new Intent(this, WaypointsMapActivity.class);
        intent.putExtra(MAP_WAYPOINTS, waypoints);
        intent.putExtra(NUM_MAP_WAYPOINTS, num_waypoints);
        intent.putExtra(MAP_HOTEL, "chicago, il");
        startActivityForResult(intent, MAP_REQUEST_CODE);
    }

    public void onClickUp(View view)
    {
        if(selected == 0 || selected == -1)
            return;
        View temp = viewWaypoints[selected];
        String wpt_temp = waypoints.get(selected);
        waypoints.set(selected, waypoints.get(selected - 1));
        waypoints.set(selected - 1, wpt_temp);
        viewWaypoints[selected] = viewWaypoints[selected - 1];
        viewWaypoints[selected - 1] = temp;
        viewWaypoints[selected].setId(selected);
        viewWaypoints[selected - 1].setId(selected - 1);
        selected -= 1;

        linearLayout.removeAllViews();

        for(int i = 0; i < num_waypoints; i++)
        {
            linearLayout.addView(viewWaypoints[i]);
        }
    }

    public void onClickDown(View view)
    {
        if(selected == num_waypoints - 1 || selected == -1)
            return;
        View temp = viewWaypoints[selected];
        String wptTemp = waypoints.get(selected);
        waypoints.set(selected, waypoints.get(selected + 1));
        waypoints.set(selected + 1, wptTemp);
        viewWaypoints[selected] = viewWaypoints[selected + 1];
        viewWaypoints[selected + 1] = temp;

        viewWaypoints[selected].setId(selected);
        viewWaypoints[selected + 1].setId(selected + 1);
        selected += 1;

        linearLayout.removeAllViews();

        for(int i = 0; i < num_waypoints; i++)
        {
            linearLayout.addView(viewWaypoints[i]);
        }
    }

    public void onClickRM(View view)
    {
        if(selected == num_waypoints - 1)
        {
        }
        else {
            for (int i = selected; i < num_waypoints - 1; i++) {
                viewWaypoints[i + 1].setId(i);
                viewWaypoints[i] = viewWaypoints[i + 1];
                waypoints.set(i, waypoints.get(i + 1));
            }
        }

        num_waypoints--;
        selected = -1;
        linearLayout.removeAllViews();

        for(int i= 0; i < num_waypoints; i++)
        {
            linearLayout.addView(viewWaypoints[i]);
        }
    }


    // TODO: Do something with number of hours that is also passed
    // TODO: Pass directions as well as just locations?  Directions may change?  But then time will change?
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MAP_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RESULT_ADD_WAYPOINT_WAYPOINTS, waypoints);
                resultIntent.putExtra(RESULT_ADD_WAYPOINT_NUM_WAYPOINTS, num_waypoints);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }

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
