package com.example.matthew.mapdirections;

import android.content.Context;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matthew on 26/04/16.
 */
public class ListenerExtended<String> implements Response.Listener<String> {

    protected Context c;

    public ListenerExtended(Context c) {
        this. c = c;
    }

    @Override
    public void onResponse(String response) {
    }
}
