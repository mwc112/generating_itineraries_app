package com.example.matthew.mapdirections;

import android.content.Context;

/**
 * Created by matthew on 29/04/16.
 */
public abstract class RunnableExtended implements Runnable {
    protected Context c;

    public RunnableExtended(Context c) {
        this.c = c;
    }
}
