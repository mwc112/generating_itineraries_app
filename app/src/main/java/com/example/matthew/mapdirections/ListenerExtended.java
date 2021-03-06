package com.example.matthew.mapdirections;

import android.content.Context;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matthew on 26/04/16.
 */
public class ListenerExtended<T> implements Response.Listener<T> {

    /*
    * Extends listener so that ui can be changed in context (Activity)
     */

    protected Context c;

    public ListenerExtended(Context c) {
        this. c = c;
    }

    @Override
    public void onResponse(T response) {
    }
}
