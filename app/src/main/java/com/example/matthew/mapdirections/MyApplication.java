package com.example.matthew.mapdirections;

import android.app.Application;
import android.content.Context;
import android.renderscript.ScriptGroup;

import com.google.android.gms.iid.InstanceID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created by matthew on 30/05/16.
 */
public class MyApplication extends Application  {

    //Cache login token

    private String loginToken;
    private String unique_id;
    private String user_email;

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        try {
            this.loginToken = loginToken;
            FileOutputStream fileOutputStream = openFileOutput("login_token", Context.MODE_PRIVATE);
            fileOutputStream.write(loginToken.getBytes());
            fileOutputStream.close();
        }
        catch(Exception e){}
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String id) {
        this.unique_id = id;
    }

    public String getUserEmail() {
        return user_email;
    }

    public void setUserEmail(String email) {
        user_email = email;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        File id = getBaseContext().getFileStreamPath("unique_id");

        if(!id.exists()) {
            try {
                unique_id = InstanceID.getInstance(getApplicationContext()).getId();
                FileOutputStream outputStream = openFileOutput("unique_id", Context.MODE_PRIVATE);
                outputStream.write(unique_id.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                FileInputStream inputStream = openFileInput("unique_id");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                char[] stream = new char[36];
                inputStreamReader.read(stream);
                unique_id = new String(stream);
                inputStreamReader.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        loginToken = new String();
        try {
            FileInputStream tokenfileInputStream = openFileInput("login_token");
            InputStreamReader tokenInputStreamReader = new InputStreamReader(tokenfileInputStream);
            char[] rawTok = new char[32];
            tokenInputStreamReader.read(rawTok);
            tokenInputStreamReader.close();
            loginToken = new String(rawTok);
        }
        catch (Exception e) {}

        user_email = new String();
        try {
            FileInputStream tokenfileInputStream = openFileInput("user_email");
            InputStreamReader tokenInputStreamReader = new InputStreamReader(tokenfileInputStream);
            char[] rawTok = new char[32];
            tokenInputStreamReader.read(rawTok);
            tokenInputStreamReader.close();
            loginToken = new String(rawTok);
        }
        catch (Exception e) {}
    }


}
