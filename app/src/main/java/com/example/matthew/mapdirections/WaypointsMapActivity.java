package com.example.matthew.mapdirections;

import android.content.Context;
import android.content.Intent;
import android.provider.DocumentsContract;
import android.sax.ElementListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//TODO: implement on button click methods

public class WaypointsMapActivity extends AppCompatActivity {

    private Intent intent;
    public static String WAYPOINTS_MAP_WAYPOINTS = "com.example.matthew.WAYPOINTS_MAP_WAYPOINTS";
    public static String WAYPOINTS_MAP_NUM_WAYPOINTS = "com.example.matthew.WAYPOINTS_MAP_NUM_WAYPOINTS";
    public static String WAYPOINTS_MAP_HOTEL = "com.example.matthew.WAYPOINTS_MAP_HOTEL";

    public String routes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waypoints_map);
        intent = getIntent();

        set_up_maps();
        plot_waypoints();
    }

    private void set_up_maps()
    {
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/simplemap.html");
    }

    private void plot_waypoints()
    {
        String[][] waypoints = (String[][])intent.getSerializableExtra(WAYPOINTS_MAP_WAYPOINTS);
        final int numWaypoints = intent.getIntExtra(AddWaypointsActivity.NUM_MAP_WAYPOINTS, 0);
        ArrayList<String> waypoints_list = new ArrayList<String>();

        for(int i = 0; i < numWaypoints; i++) {
            waypoints_list.add(waypoints[i][1]);
        }
        final JSONArray jsonArray = new JSONArray(waypoints_list);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                WebView webView = (WebView) findViewById(R.id.webview);
                webView.evaluateJavascript("create_markers(" + jsonArray.toString() + "," + numWaypoints + ");", null);
            }
        });

    }

    private void route_waypoints() {
        String[][] waypoints = (String[][])intent.getSerializableExtra(WAYPOINTS_MAP_WAYPOINTS);
        final int num_waypoints = intent.getIntExtra(WAYPOINTS_MAP_NUM_WAYPOINTS, 0);
        final String hotel = intent.getStringExtra(WAYPOINTS_MAP_HOTEL);
        ArrayList<String> waypoints_list = new ArrayList<String>();

        for(int i = 0; i < num_waypoints; i++) {
            waypoints_list.add(waypoints[i][1]);
        }

        final JSONArray jsonArray = new JSONArray(waypoints_list);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url)
            {
                WebView webView = (WebView) findViewById(R.id.webview);
                webView.evaluateJavascript("get_dir("+jsonArray.toString()+","+num_waypoints+","+
                        '"'+hotel+'"'+");", null);
            }
        });
    }

    public void onClickMapsSave(View view) {
        final String filename = "trips.xml";
        try {
            File file = new File(this.getFilesDir(), filename);
            if(!file.exists()) {
                Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Element rootElement = d.createElement("trips");
                d.appendChild(rootElement);

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                transformer.transform(new DOMSource(d), result);

                FileOutputStream output = openFileOutput(filename, Context.MODE_PRIVATE);
                output.write(writer.toString().getBytes());
                output.close();
            }

            FileInputStream fis = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String input = new String(inputBuffer);
            isr.close();
            fis.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes("UTF-8"));
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document d = db.parse(bais);
            Node root = d.getFirstChild();
            Element trip = d.createElement("trip");
            root.appendChild(trip);
            //TODO: Create trip ids, different for online/offline?
            //trip.setAttribute("id", );
            Element hotel = d.createElement("hotel");
            hotel.setTextContent("Buckingham Palace, London");
            trip.appendChild(hotel);
            Element startTime = d.createElement("start_time");
            trip.appendChild(startTime);
            Element startTimeHour = d.createElement("hour");
            startTime.appendChild(startTimeHour);
            startTimeHour.setTextContent("17");
            Element startTimeMinute = d.createElement("minute");
            startTime.appendChild(startTimeMinute);
            startTimeMinute.setTextContent("0");
            Element endTime = d.createElement("end_time");
            trip.appendChild(endTime);
            Element endTimeHour = d.createElement("hour");
            endTime.appendChild(endTimeHour);
            endTimeHour.setTextContent("17");
            Element endTimeMinute = d.createElement("minute");
            endTime.appendChild(endTimeMinute);
            endTimeMinute.setTextContent("0");
            Element waypoints = d.createElement("waypoints");
            trip.appendChild(waypoints);

            String[][] waypoints_actual = (String[][])intent.getSerializableExtra(WAYPOINTS_MAP_WAYPOINTS);
            final int num_waypoints = intent.getIntExtra(WAYPOINTS_MAP_NUM_WAYPOINTS, 0);
            for(int i = 0; i < num_waypoints; i++) {
                Element waypoint = d.createElement("waypoint");
                waypoints.appendChild(waypoint);
                waypoint.setTextContent(waypoints_actual[i][1]);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(new DOMSource(d), result);

            FileOutputStream output = openFileOutput(filename, Context.MODE_PRIVATE);
            output.write(writer.toString().getBytes());
            output.close();

        }
        catch(FileNotFoundException e) {}
        catch (IOException e) {}
        catch (ParserConfigurationException e) {}
        catch (SAXException e) {}
        catch (TransformerConfigurationException e ){}
        catch (TransformerException e) {}

        setResult(RESULT_OK);
        finish();
    }
}
