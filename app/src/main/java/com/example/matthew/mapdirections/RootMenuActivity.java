package com.example.matthew.mapdirections;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

public class RootMenuActivity extends AppCompatActivity {

    //TODO: What if login expires while the app is open

    private int SAVED_TRIPS_REQUEST_CODE = 0;

    private RequestQueue queue;
    private IntentFilter intentFilter;
    private LogoutReceiver logoutReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_menu);
        queue = Volley.newRequestQueue(this);

        intentFilter = new IntentFilter("com.example.Logout");
        logoutReceiver = new LogoutReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(logoutReceiver, intentFilter);
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

    public void onClickRootLogout(View view) {
        ((MyApplication)getApplication()).setLoginToken("");
        StringRequest request = new StringRequest(Request.Method.GET, "http://http://178.62.46.132/app_login?req_type=Logout" +
                "&key=" + ((MyApplication) getApplication()).getLoginToken() + "&app_id=" + ((MyApplication) getApplication()).getUnique_id(),
                new ListenerExtended<String>(this) {

                    @Override
                    public void onResponse(String response) {
                        ((Activity)c).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(c, LoginActivity.class);
                                startActivity(intent);
                                ((Activity)c).finish();
                            }
                        });
                    }

                },
                new ErrorListenerExtended(this) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((Activity)c).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(c, LoginActivity.class);
                                startActivity(intent);
                                ((Activity)c).finish();
                            }
                        });
                    }
                });
        queue.add(request);

    }

    private class LogoutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(logoutReceiver);
            finish();
        }
    }
}
