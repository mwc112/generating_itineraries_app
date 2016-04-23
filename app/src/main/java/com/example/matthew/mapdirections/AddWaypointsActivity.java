package com.example.matthew.mapdirections;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;

//TODO: Use offset instead of shuffling all values
//TODO: Finish removing elements - either tell map number of elements or physically remove from arraylist

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
    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final static int HOTEL_AUTOCOMPLETE_REQUEST_CODE = 2;

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

    public void onClickAdd(View view)
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

    public void onClickMap(View view)
    {
        Intent intent = new Intent(this, WaypointsMapActivity.class);
        intent.putExtra(MAP_WAYPOINTS, waypoints);
        intent.putExtra(NUM_MAP_WAYPOINTS, num_waypoints);
        startActivity(intent);
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
            num_waypoints--;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                TextView textView = new TextView(this);
                textView.setText(place.getName());
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
                waypoints.add(place.getName().toString());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

}
