package com.cardboardmx.uberbita;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import net.sqlcipher.Cursor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Objects;

/**
 * Created by juanelo on 01/08/15.
 */
public class GenericTaskFragment extends Fragment {

    static public interface TaskCallBacks{
        void onPreExcute();
        void onProgressUpdate();
        void onCancelled();
        void onPostExcute(Cursor result);
    }


    public static final String TAG_TASK_FRAGMENT = "task_fragment";

    private TaskCallBacks mCallBacks;
    private GenericAsyncTask mTask;
    private boolean mRunning;

    private String mURL;
    private String mRequestMethod;
    private String mData;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if ( !(activity instanceof TaskCallBacks)){
            throw new IllegalStateException("Activity debera implementar interfaz taskCallBacks");
        }
        mCallBacks = (TaskCallBacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancel();
    }

    public void start(){
        if (!mRunning) {
            mTask  = new GenericAsyncTask();
            mTask.execute("");
            mRunning = true;
        }
    }

    public void cancel(){
        if (mRunning) {
            mTask.cancel(false);
            mTask = null;
            mRunning = false;
        }
    }

    public boolean isRunning(){
        return mRunning;
    }


    private class GenericAsyncTask extends AsyncTask<Object, Object, Cursor>{


        @Override
        protected void onPreExecute() {
            mCallBacks.onPreExcute();
            mRunning = true;
        }

        @Override
        protected Cursor doInBackground(Object... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mCallBacks.onPostExcute(cursor);
            mRunning = false;

        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            mCallBacks.onProgressUpdate();
        }

        @Override
        protected void onCancelled() {
            mCallBacks.onCancelled();
            mRunning = false;
        }
    }

    private void disposeConnection(HttpURLConnection conn) {

        if ( conn != null ){
            conn.disconnect();
        }

        conn = null;
    }
}
