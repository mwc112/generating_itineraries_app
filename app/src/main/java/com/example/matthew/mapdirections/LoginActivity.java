package com.example.matthew.mapdirections;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {

    //TODO: Secure HTTP transmission
    //TODO: Implement logging

    //private static char[] KEYSTORE_PASSWORD = "password".toCharArray();
    private RequestQueue queue;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutLogin);
        createLoginButton();
        linearLayout.addView(loginButton);
        queue = Volley.newRequestQueue(LoginActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!((MyApplication)getApplication()).getLoginToken().equals(""))
            isLoginValid();
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

    private void switchButtonForBar() {
        LinearLayout rootLinearLayout = (LinearLayout) findViewById(R.id.layoutLogin);
        rootLinearLayout.removeView(loginButton);
        ProgressBar loginProg = new ProgressBar(this);
        loginProg.setIndeterminate(true);
        loginProg.setId(R.id.progressBarLogin);
        rootLinearLayout.addView(loginProg, 3);
    }

    private void createLoginButton() {
        loginButton = new Button(this.getBaseContext());
        ViewGroup.LayoutParams buttonlayout = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        loginButton.setText(R.string.login_button_text);
        loginButton.setLayoutParams(buttonlayout);
        loginButton.setId(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchButtonForBar();

                //TODO: Login does not work after closing app think because Instance ID can't be sent 400 error (fixed?)
                final String email = ((EditText) findViewById(R.id.txtLoginEmail)).getText().toString();
                final String password = ((EditText) findViewById(R.id.txtLoginPassword)).getText().toString();

                StringRequest request = new StringRequest(Request.Method.GET, Uri.parse("http://178.62.46.132/app_login?email=" + email + "&password="
                        + password + "&app_id=" + ((MyApplication) getApplication()).getUnique_id() + "&req_type=token").toString(),
                        new ListenerExtended<String>(LoginActivity.this) {
                            @Override
                            public void onResponse(final String response) {
                                if (response.equals("Bad Request")) {
                                    ((Activity) c).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
                                            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutLogin);
                                            linearLayout.removeView(progressBar);
                                            linearLayout.addView(loginButton, 3);
                                            //TODO: Show some kind of message when it fails
                                        }
                                    });
                                } else {
                                    //TODO: Clean up so activity is passed to ListenerExtended
                                    ((MyApplication) getApplication()).setLoginToken(response);
                                    ((MyApplication) getApplication()).setUserEmail(email);
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
                                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
                                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutLogin);
                                        linearLayout.removeView(progressBar);
                                        linearLayout.addView(loginButton, 3);
                                        //TODO: Show some kind of message when it fails
                                    }
                                });
                            }
                        });
                queue.add(request);
            }
        });
    }

    private void isLoginValid() {
        StringRequest request = new StringRequest(Request.Method.GET, "http://178.62.46.132/app_login?req_type=Validate&" +
                "key=" + ((MyApplication) getApplication()).getLoginToken() + "&app_id=" + ((MyApplication)getApplication()).getUnique_id(),
                new ListenerExtended<String>(this) {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Valid")) {
                            ((Activity)c).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(c, RootMenuActivity.class);
                                    startActivity(intent);
                                    ((Activity)c).finish();
                                }
                            });
                        }
                        if(response.equals("Invalid")) {
                            //TODO: Show error message
                        }
                        if(response.equals("Bad Request")) {
                            //TODO: Show error response
                        }
                    }

        },
                new ErrorListenerExtended(this) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: Show error response
                    }
                });

        queue.add(request);
    }

}
