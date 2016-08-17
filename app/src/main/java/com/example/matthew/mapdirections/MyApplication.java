package com.example.matthew.mapdirections;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by matthew on 30/05/16.
 */
public class MyApplication extends Application{

    //TODO: Cache login token
    //TODO: Don't use email for login every time - use app_id and login_token
    //TODO: Exceptions

    private static final String TAG = "MyApplication";


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

    public void setUserEmail(String user_email) {
        try {
            this.user_email = user_email;
            FileOutputStream fileOutputStream = openFileOutput("user_email", Context.MODE_PRIVATE);
            fileOutputStream.write(user_email.getBytes());
            fileOutputStream.close();
        }
        catch(Exception e){}
    }

    @Override
    public void onCreate() {
        super.onCreate();

        File id = getBaseContext().getFileStreamPath("unique_id");

        if(!id.exists()) {
            try {
                unique_id = InstanceID.getInstance(getApplicationContext()).getId();
                //unique_id = UUID.randomUUID().toString();
                FileOutputStream outputStream = openFileOutput("unique_id", Context.MODE_PRIVATE);
                outputStream.write(unique_id.getBytes());
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error: writing new unique id");
            }
        }
        else {
            try {
                FileInputStream inputStream = openFileInput("unique_id");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                char[] stream = new char[11];
                inputStreamReader.read(stream);
                unique_id = new String(stream);
                inputStreamReader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error: Reading unique app id");
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
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.wtf(TAG, "Error: Login token not found despite file returning existing");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "Error: Reading login token");
        }

        user_email = new String();
        try {
            FileInputStream tokenfileInputStream = openFileInput("user_email");
            InputStreamReader tokenInputStreamReader = new InputStreamReader(tokenfileInputStream);
            char[] rawTok = new char[32];
            tokenInputStreamReader.read(rawTok);
            tokenInputStreamReader.close();
            loginToken = new String(rawTok);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Error: Saved email not found despite having login token");
        }
        catch (IOException e) {

        }
    }

}
