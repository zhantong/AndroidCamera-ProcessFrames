package com.polarxiong.camerademo;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by zhantong on 16/6/15.
 */
public class ProcessWithAsyncTask extends AsyncTask<byte[], Void, String> {
    private static final String TAG = "AsyncTask";

    @Override
    protected String doInBackground(byte[]... params) {
        processFrame(params[0]);
        return "test";
    }

    private void processFrame(byte[] frameData) {
        Log.i(TAG, "test");
    }
}
