package com.example.matthew.mapdirections;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class LoginActivity extends AppCompatActivity {

    private static char[] KEYSTORE_PASSWORD = "password".toCharArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutLogin);
        Button button = createLoginButton();
        linearLayout.addView(button);
    }

    public void onClickLogin(View view) {

        /*final HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv =
                        HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("localhost", session);
            }
        };

        HurlStack hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(newSslSocketFactory());
                    httpsURLConnection.setHostnameVerifier(hostnameVerifier);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };*/
    }


    /*private SSLSocketFactory newSslSocketFactory() {
        try {
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = this.getApplicationContext().getResources().openRawResource(R.raw.codeprojectssl);
            try {
                trusted.load(in, KEYSTORE_PASSWORD);
            } finally {
                in.close();
            }
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trusted);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            SSLSocketFactory sf = context.getSocketFactory();
            return sf;
        }
        catch (Exception e) {throw new AssertionError(e);}
    }*/

    protected void showProgressBar() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutLogin);
        Button button = (Button) findViewById(R.id.btnLogin);
        linearLayout.removeView(button);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setId(R.id.progressBarLogin);
        linearLayout.addView(progressBar, 3);
    }

    protected Button createLoginButton() {
        Button button = new Button(this);
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(layoutParams);
        button.setId(R.id.btnLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.showProgressBar();

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                final String email = ((EditText) findViewById(R.id.txtLoginEmail)).getText().toString();
                final String password = ((EditText) findViewById(R.id.txtLoginPassword)).getText().toString();

                StringRequest request = new StringRequest(Request.Method.GET, Uri.parse("http://178.62.116.27/app_login?email=" + email + "&password="
                                + password + "&app_id=" + ((MyApplication) getApplication()).getUnique_id()).toString(),
                        new ListenerExtended<String>(LoginActivity.this) {
                            @Override
                            public void onResponse(final String response) {
                                if (response.equals("Bad Request")) {
                                    ((Activity) c).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Button b = createLoginButton();
                                            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
                                            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutLogin);
                                            linearLayout.removeView(progressBar);
                                            linearLayout.addView(b, 3);
                                            //TODO: Show some kind of message when it fails
                                        }
                                    });
                                } else {
                                    ((MyApplication)getApplication()).setLoginToken(response);
                                    ((Activity) c).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(c, RootMenuActivity.class);
                                            startActivity(intent);
                                            ((Activity) c).finish();
                                        }
                                    });

                                }
                            }
                        },
                        new ErrorListenerExtended(LoginActivity.this) {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Activity a = (Activity) c;
                                a.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Button b = createLoginButton();
                                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
                                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutLogin);
                                        linearLayout.removeView(progressBar);
                                        linearLayout.addView(b, 3);
                                        //TODO: Show some kind of message when it fails
                                    }
                                });
                            }
                        });
                queue.add(request);
            }
        });
        return  button;
    }

}
