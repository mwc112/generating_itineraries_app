package com.example.matthew.mapdirections;

import android.content.Context;

import com.android.volley.Response;

/**
 * Created by matthew on 30/05/16.
 */
public abstract class ErrorListenerExtended implements Response.ErrorListener {

    protected Context c;

    public ErrorListenerExtended(Context c) {
        this.c = c;
    }

}
