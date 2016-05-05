package com.example.matthew.mapdirections;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_trips);

        scrllTrips = (ScrollView) findViewById(R.id.scrllSavedTripsTrips);
        scrllTrips.setBackgroundColor(Color.parseColor("#ffe3e3e3"));

        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setClickable(true);
        scrllTrips.addView(linearLayout);

        trips = new String[20];
        viewTrips = new View[20];

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
        new Thread(new RunnableExtended(this) {
            @Override
            public void run() {

                try {
                    final String filename = "trips.xml";
                    FileInputStream fis = c.openFileInput(filename);
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
                    String hotel = selectedChild.getChildNodes().item(0).getNodeValue();
                    String routes = selectedChild.getChildNodes().item(4).getNodeValue();
                    Intent intent = new Intent(c, testService.class);
                    intent.putExtra(RunningTripActivity.RUNNING_TRIP_HOTEL, hotel);
                    intent.putExtra(RunningTripActivity.RUNNING_TRIP_ROUTES, routes);
                    startService(intent);
                }
                catch (Exception e) {}
            }
        }).start();

        Intent intent = new Intent(this, RunningTripActivity.class);
        startActivity(intent);
        finish();
    }
}
