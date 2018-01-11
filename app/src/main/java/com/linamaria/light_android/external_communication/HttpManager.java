package com.linamaria.light_android.external_communication;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.linamaria.light_android.ContextManagementActivity;
import org.json.JSONObject;

/**
 * Created by LinaMaria on 12/13/2017.
 */

public class HttpManager {

    private RequestQueue queue;
    private final ContextManagementActivity cm;
    private String url ="https://glacial-plateau-99461.herokuapp.com/api/rooms";

    public HttpManager(RequestQueue queue,ContextManagementActivity cm) {
        this.queue = queue;
        this.cm = cm;
    }

    public void retrieveRoomContextState(String roomId){
        String url = this.url + "/" + roomId;
        sendHttpRequest(url, Request.Method.GET, cm);
    }

    public void switchLight(String roomId){
        String url = this.url + "/" + roomId + "/switch-light";
        sendHttpRequest(url, Request.Method.POST, cm);
    }

    public void switchRinger(String roomId){
        String url = this.url + "/" + roomId + "/switch-ringer";
        sendHttpRequest(url, Request.Method.POST, cm);
    }

    public void sendHttpRequest(String url, int method, Response.Listener<JSONObject> listener){

        JsonObjectRequest contextRequest = new JsonObjectRequest(
                method, url, null, listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Some error to access URL : Room may not exists...
                    }
                });

        queue.add(contextRequest);
    }




}
