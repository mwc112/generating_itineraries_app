package com.example.matthew.mapdirections;

import android.content.Context;
import android.location.LocationListener;

/**
 * Created by matthew on 06/05/16.
 */
public abstract class LocationListenerExtended implements LocationListener {

    protected Context c;

    public LocationListenerExtended(Context c) {
        this.c = c;
    }
}
