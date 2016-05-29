package com.example.matthew.mapdirections;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*public void onClickLogin(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String email = ((EditText)findViewById(R.id.txtLoginEmail)).getText().toString();
        final String password = ((EditText)findViewById(R.id.txtLoginPassword)).getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, "https://139.59.188.237/login",
                new ListenerExtended<String>(this) {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("OK")) {

                        }
                        else {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        queue.add(request);
    }*/

}
