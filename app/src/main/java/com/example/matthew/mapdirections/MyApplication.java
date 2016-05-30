package com.example.matthew.mapdirections;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created by matthew on 30/05/16.
 */
public class MyApplication extends Application  {

    private String loginToken;
    private String unique_id;

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        if(loginToken == null) {
            loginToken = new String();
        }
        this.loginToken = loginToken;
    }

    public String getUnique_id() {
        return unique_id;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String filename = "unique_id";
        File id = new File(filename);

        if(!id.exists()) {
            try {
                unique_id = UUID.randomUUID().toString();

                FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(unique_id.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                FileInputStream inputStream = openFileInput(filename);
                byte[] stream = new byte[255];
                inputStream.read(stream);
                unique_id = stream.toString();
                inputStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
