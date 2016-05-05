package com.example.matthew.mapdirections;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RunningTripActivity extends AppCompatActivity {

    public static String RUNNING_TRIP_ROUTES = "com.example.matthew.RUNNING_TRIP_ROUTES";
    public static String RUNNING_TRIP_HOTEL = "com.example.matthew.RUNNING_TRIP_HOTEL";

    private testService boundService;
    private ServiceConnection connection;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_trip);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                boundService = ((testService.TestBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(new Intent(this, testService.class), connection, BIND_AUTO_CREATE);
        isBound = true;
    }


}
