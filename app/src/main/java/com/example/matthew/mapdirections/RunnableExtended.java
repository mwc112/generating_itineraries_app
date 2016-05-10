package com.example.matthew.mapdirections;

import android.app.Activity;
import android.content.Context;

/**
 * Created by matthew on 29/04/16.
 */
public abstract class RunnableExtended implements Runnable {
    protected Activity c;

    public RunnableExtended(Activity c) {
        this.c = c;
    }
}
