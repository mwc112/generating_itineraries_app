package com.example.matthew.mapdirections;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RunningTripAtLocActivity extends AppCompatActivity {

    private ServiceConnection connection;
    private testService boundService;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_trip_at_loc);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                boundService = ((testService.TestBinder)service).getService();
                boundService.setRunningTripAtLocActivity(RunningTripAtLocActivity.this);
                boundService.setIsShowing(true);
                ((TextView)findViewById(R.id.txtRunningTripAtLocInfo)).setText(boundService.getNextWaypointName());
                ((TextView)findViewById(R.id.txtRunningTripAtLocTime)).setText(boundService.getTimeUntilTravel() + " minutes remaining");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound) {
            boundService.repushNotification();
            boundService.setIsShowing(false);
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, testService.class), connection, BIND_AUTO_CREATE);
        isBound = true;
    }

    public void onClickRunningTripAtLocStop(View view) {
        Intent intent = new Intent(this, testService.class);
        stopService(intent);
        unbindService(connection);
        isBound = false;
        Intent main_intent = new Intent(this, RootMenuActivity.class);
        finish();
        startActivity(main_intent);
    }
}
