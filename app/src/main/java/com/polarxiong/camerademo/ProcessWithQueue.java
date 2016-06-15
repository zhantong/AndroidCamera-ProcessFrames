package com.polarxiong.camerademo;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhantong on 16/6/15.
 */
public class ProcessWithQueue extends Thread {
    private static final String TAG = "Queue";
    private LinkedBlockingQueue<byte[]> mQueue;

    public ProcessWithQueue(LinkedBlockingQueue<byte[]> frameQueue) {
        mQueue = frameQueue;
        start();
    }

    @Override
    public void run() {
        while (true) {
            byte[] frameData = null;
            try {
                frameData = mQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (frameData.length == 1) {
                break;
            }
            processFrame(frameData);
        }
    }

    private void processFrame(byte[] frameData) {
        Log.i(TAG, "test");
    }
}
