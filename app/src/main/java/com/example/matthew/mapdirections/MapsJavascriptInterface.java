package com.example.matthew.mapdirections;

import android.content.Context;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;

/**
 * Created by matthew on 29/04/16.
 */
public class MapsJavascriptInterface {

    WaypointsMapActivity c;

    public MapsJavascriptInterface(WaypointsMapActivity c) {
        this.c = c;
    }

    @JavascriptInterface
    public void setDirections(String routes) {
        try {
            c.routes = routes;
        }
        catch (Exception e) {}
    }
}
