package com.example.matthew.mapdirections;

import android.app.Application;

/**
 * Created by matthew on 30/05/16.
 */
public class MyApplication extends Application  {

    private String loginToken;

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        if(loginToken == null) {
            loginToken = new String();
        }
        this.loginToken = loginToken;
    }

}
