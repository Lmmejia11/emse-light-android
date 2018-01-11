package com.linamaria.light_android.external_communication;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by LinaMaria on 12/13/2017.
 */

public class SingleHtmlQueue {

    private static SingleHtmlQueue instance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private SingleHtmlQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized SingleHtmlQueue getInstance(Context context) {
        if (instance == null) {
            instance = new SingleHtmlQueue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
    
}
