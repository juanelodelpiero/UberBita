package com.cardboardmx.uberbita.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by juanenrique_ramirez on 11/3/15.
 */
public class RegisterActivity extends IntentService{

    public RegisterActivity() {
        super("TripsIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        long endTime = System.currentTimeMillis() + 5*1000;

        while (System.currentTimeMillis() < endTime){
            synchronized (this){
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
