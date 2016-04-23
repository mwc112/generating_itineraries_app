package com.example.matthew.mapdirections;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class ManualCreationTripActivity extends AppCompatActivity {

    private ArrayList<ArrayList<String>> days_waypoints;
    public final static String MANUAL_CREATION_HOTEL = "com.example.matthew.MANUAL_CREATION_HOTEL";
    private final static int NEW_WAYPOINTS_REQUEST_CODE = 1;
    private int num_days = 0;
    private ArrayList<Integer> days_num_waypoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_creation_trip);
        days_waypoints = new ArrayList<ArrayList<String>>(7);
        days_num_waypoints = new ArrayList<Integer>(7);
    }

    public void onClickManualCreationNewDay(View view) {
        Intent intent = new Intent(this, AddWaypointsActivity.class);
        intent.putExtra(MANUAL_CREATION_HOTEL, ((EditText) findViewById(R.id.txtManualTripHotel)).getText());
        startActivityForResult(intent, NEW_WAYPOINTS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == NEW_WAYPOINTS_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                days_waypoints.add(data.getStringArrayListExtra(AddWaypointsActivity.RESULT_ADD_WAYPOINT_WAYPOINTS));
                days_num_waypoints.add(data.getIntExtra(AddWaypointsActivity.RESULT_ADD_WAYPOINT_NUM_WAYPOINTS, 0));
                num_days++;
            }
        }
    }

}
