package com.example.matthew.mapdirections;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class SavedTripsActivity extends AppCompatActivity {

    private int numTrips = 0;
    private View[] viewTrips;
    private String[] trips;
    private int selected = -1;
    private LinearLayout linearLayout;
    private ScrollView scrllTrips;
    private BroadcastReceiver receiver;
    private RequestQueue queue;
    private Button uploadButton;
    private ProgressBar uploadProgBar;
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_trips);

        rootLayout = (LinearLayout) findViewById(R.id.layoutSavedTripsRoot);

        uploadProgBar = new ProgressBar(this);
        uploadProgBar.setIndeterminate(true);

        uploadButton = new Button(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        uploadButton.setLayoutParams(layoutParams);
        uploadButton.setId(R.id.btnSavedTripsUpload);
        uploadButton.setText("Upload");
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSavedTripsUpload(v);
            }
        });
        uploadButton.setEnabled(false);
        rootLayout.addView(uploadButton, 1);

        receiver = new SavedBroadcastReceiver();

        scrllTrips = (ScrollView) findViewById(R.id.scrllSavedTripsTrips);
        scrllTrips.setBackgroundColor(Color.parseColor("#ffe3e3e3"));

        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setClickable(true);
        scrllTrips.addView(linearLayout);

        trips = new String[20];
        viewTrips = new View[20];

        queue = Volley.newRequestQueue(this);

        try {
            final String filename = "trips.xml";
            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String input = new String(inputBuffer);
            isr.close();
            fis.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes("UTF-8"));
            //File file = new File(this.getFilesDir(), filename);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document d = db.parse(bais);

            Node root = d.getFirstChild();
            NodeList children = root.getChildNodes();

            for(int i = 0; i < children.getLength(); i++) {
                TextView textView = new TextView(this);
                textView.setText(Integer.toString(i));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selected != -1)
                            linearLayout.getChildAt(selected).setBackgroundColor(Color.parseColor("#ffe3e3e3"));
                        selected = v.getId();
                        v.setBackgroundColor(Color.GREEN);
                        findViewById(R.id.btnSavedTripsUpload).setEnabled(true);
                    }
                });
                linearLayout.addView(textView);
                viewTrips[numTrips] = textView;
                textView.setId(numTrips);
                trips[numTrips] = Integer.toString(i);
                numTrips++;
            }
        }
        catch (Exception e) {}
    }

    public void onClickSavedTripsStart(View view) {
        Button editBtn = (Button) findViewById(R.id.btnSavedTripsEdit);
        Button startBtn = (Button) findViewById(R.id.btnSavedTripsStart);
        ((LinearLayout)findViewById(R.id.layoutSavedTripsBtns)).removeView(editBtn);
        ((LinearLayout)findViewById(R.id.layoutSavedTripsBtns)).removeView(startBtn);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        ((LinearLayout)findViewById(R.id.layoutSavedTripsBtns)).addView(progressBar);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        IntentFilter intentFilter = new IntentFilter("com.example.SAVEDTRIPSACTIVITY");
        registerReceiver(receiver, intentFilter);
        new StartServiceTask().execute();
    }

    private class StartServiceTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... v) {

            try {
                final String filename = "trips.xml";
                FileInputStream fis = SavedTripsActivity.this.openFileInput(filename);
                InputStreamReader isr = new InputStreamReader(fis);
                char[] inputBuffer = new char[fis.available()];
                isr.read(inputBuffer);
                String input = new String(inputBuffer);
                isr.close();
                fis.close();

                ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes("UTF-8"));
                //File file = new File(this.getFilesDir(), filename);
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document d = db.parse(bais);

                Node root = d.getFirstChild();
                NodeList children = root.getChildNodes();

                Node selectedChild = children.item(selected);
                String hotel = selectedChild.getChildNodes().item(0).getFirstChild().getNodeValue();
                NodeList waypoint_nodes = selectedChild.getChildNodes().item(3).getChildNodes();
                String[] waypoints = new String[waypoint_nodes.getLength()];
                for(int i = 0; i < waypoint_nodes.getLength(); i++) {
                    waypoints[i] = waypoint_nodes.item(i).getFirstChild().getNodeValue();
                }
                NodeList times_to_stay_nodes = selectedChild.getChildNodes().item(4).getChildNodes();
                int[] times_to_stay = new int[times_to_stay_nodes.getLength()];
                for(int i = 0; i < times_to_stay_nodes.getLength(); i++) {
                    times_to_stay[i] = Integer.parseInt(times_to_stay_nodes.item(i).getFirstChild().getNodeValue());
                }
                int[][] time = new int[2][2];
                Node startTime = selectedChild.getChildNodes().item(1);
                Node endTime = selectedChild.getChildNodes().item(2);
                time[0][0] = Integer.parseInt(startTime.getFirstChild().getFirstChild().getNodeValue());
                time[0][1] = Integer.parseInt(startTime.getLastChild().getFirstChild().getNodeValue());
                time[1][0] = Integer.parseInt(endTime.getFirstChild().getFirstChild().getNodeValue());
                time[1][1] = Integer.parseInt(endTime.getLastChild().getFirstChild().getNodeValue());
                Intent intent = new Intent(SavedTripsActivity.this, testService.class);
                intent.putExtra(RunningTripActivity.RUNNING_TRIP_HOTEL, hotel);
                intent.putExtra(RunningTripActivity.RUNNING_TRIP_WAYPOINTS, waypoints);
                intent.putExtra(RunningTripActivity.RUNNING_TRIP_TIMES, time);
                intent.putExtra(RunningTripActivity.RUNNING_TRIP_TIME_TO_STAY, times_to_stay);
                startService(intent);
            }
            catch (Exception e) {}

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }

    public BroadcastReceiver getReceiver() {
        return receiver;
    }

    private class SavedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SavedTripsActivity.this.unregisterReceiver(SavedTripsActivity.this.getReceiver());
            SavedTripsActivity.this.finish();
        }
    }

    public void onClickSavedTripsUpload(View view) {
        rootLayout.removeView(uploadButton);
        rootLayout.addView(uploadProgBar, 1);

        try {
            final String filename = "trips.xml";
            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String input = new String(inputBuffer);
            isr.close();
            fis.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes("UTF-8"));
            //File file = new File(this.getFilesDir(), filename);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document d = db.parse(bais);

            Node root = d.getFirstChild();
            NodeList children = root.getChildNodes();

            Node selectedChild = children.item(selected);
            String hotel = selectedChild.getChildNodes().item(0).getFirstChild().getNodeValue();
            NodeList waypoint_nodes = selectedChild.getChildNodes().item(3).getChildNodes();
            String[] waypoints = new String[waypoint_nodes.getLength()];
            for (int i = 0; i < waypoint_nodes.getLength(); i++) {
                waypoints[i] = waypoint_nodes.item(i).getFirstChild().getNodeValue();
            }
            NodeList times_to_stay_nodes = selectedChild.getChildNodes().item(4).getChildNodes();
            int[] times_to_stay = new int[times_to_stay_nodes.getLength()];
            for (int i = 0; i < times_to_stay_nodes.getLength(); i++) {
                times_to_stay[i] = Integer.parseInt(times_to_stay_nodes.item(i).getFirstChild().getNodeValue());
            }
            int[][] time = new int[2][2];
            int[] date_actual = new int[3];
            Node startTime = selectedChild.getChildNodes().item(1);
            //Node endTime = selectedChild.getChildNodes().item(2);
            Node date = selectedChild.getChildNodes().item(5);
            date_actual[2] = Integer.parseInt(date.getFirstChild().getFirstChild().getNodeValue());
            date_actual[1] = Integer.parseInt(date.getChildNodes().item(1).getFirstChild().getNodeValue());
            date_actual[0] = Integer.parseInt(date.getLastChild().getFirstChild().getNodeValue());
            time[0][0] = Integer.parseInt(startTime.getFirstChild().getFirstChild().getNodeValue());
            time[0][1] = Integer.parseInt(startTime.getLastChild().getFirstChild().getNodeValue());
            String start = Integer.toString(date_actual[2]).substring(2) + "-" +
                    Integer.toString(date_actual[1]) + "-" +
                    Integer.toString(date_actual[0]) + "%20" +
                    Integer.toString(time[0][0]) + ":" +
                    Integer.toString(time[0][1]) + ":00";
            //time[1][0] = Integer.parseInt(endTime.getFirstChild().getFirstChild().getNodeValue());
            //time[1][1] = Integer.parseInt(endTime.getLastChild().getFirstChild().getNodeValue());

            String route = "[]";

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://178.62.116.27/save_trip?hotel=");
            stringBuilder.append("hotel" + "&route=" + route + "&waypoints=");
            stringBuilder.append(new JSONArray(waypoints).toString() + "&times_to_stay=");
            stringBuilder.append(new JSONArray(times_to_stay).toString() + "&transport_method=public&creator=4&start_date_time=");
            stringBuilder.append(start);
            stringBuilder.append("&key=" + ((MyApplication)getApplication()).getLoginToken());
            stringBuilder.append("&app_id=" + ((MyApplication)getApplication()).getUnique_id());
            String s = stringBuilder.toString();
            String su = Uri.parse(s).toString();
            StringRequest request = new StringRequest(Request.Method.GET, su,
                    new ListenerExtended<String>(this) {
                        @Override
                        public void onResponse(String response) {
                            Activity a = (Activity) c;
                            if(response.equals("OK")) {
                                a.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rootLayout.removeView(uploadProgBar);
                                        rootLayout.addView(uploadButton, 1);
                                    }
                                });
                            }
                            else if(response.equals("Bad Request")) {
                                a.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rootLayout.removeView(uploadProgBar);
                                    }
                                });
                            }
                            else if(response.equals("Invalid Login")) {
                                //TODO: Callback method on new InstanceID
                                ((MyApplication)getApplication()).setLoginToken("");
                                ((MyApplication)getApplication()).setUserEmail("");
                                Intent intent = new Intent(SavedTripsActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    },
                    new ErrorListenerExtended(this) {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Activity a = (Activity) c;
                            a.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rootLayout.removeView(uploadProgBar);
                                }
                            });
                        }
                    });
            queue.add(request);
        }
        catch (Exception e){}
    }



}
