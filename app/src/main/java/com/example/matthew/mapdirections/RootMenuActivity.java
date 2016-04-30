package com.example.matthew.mapdirections;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RootMenuActivity extends AppCompatActivity {

    private int SAVED_TRIPS_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_menu);
    }

    public void onClickRootSaved(View view) {
        Intent intent = new Intent(this, SavedTripsActivity.class);
        startActivityForResult(intent, SAVED_TRIPS_REQUEST_CODE);
    }

    public void onClickRootNew(View view) {
        Intent intent = new Intent(this, AddWaypointsToGenerateActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if(requestCode == SAVED_TRIPS_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                finish();
            }
        }*/
    }
}
