package com.polarxiong.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private static final int PROCESS_WITH_HANDLER_THREAD = 1;

    private int processType = PROCESS_WITH_HANDLER_THREAD;

    private ProcessWithHandlerThread processFrameHandlerThread;
    private Handler processFrameHandler;

    public CameraPreview(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        if (processType == PROCESS_WITH_HANDLER_THREAD) {
            processFrameHandlerThread = new ProcessWithHandlerThread("process frame");
            processFrameHandler = new Handler(processFrameHandlerThread.getLooper(), processFrameHandlerThread);
        }
    }

    private void openCameraOriginal() {
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "camera is not available");
        }
    }

    public Camera getCameraInstance() {
        if (mCamera == null) {
            CameraHandlerThread mThread = new CameraHandlerThread("camera thread");
            synchronized (mThread) {
                mThread.openCamera();
            }
        }
        return mCamera;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        getCameraInstance();
        mCamera.setPreviewCallback(this);
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (processType == PROCESS_WITH_HANDLER_THREAD) {
            processFrameHandler.obtainMessage(ProcessWithHandlerThread.WHAT_PROCESS_FRAME, data).sendToTarget();
        }
    }

    private class CameraHandlerThread extends HandlerThread {
        Handler mHandler;

        public CameraHandlerThread(String name) {
            super(name);
            start();
            mHandler = new Handler(getLooper());
        }

        synchronized void notifyCameraOpened() {
            notify();
        }

        void openCamera() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    openCameraOriginal();
                    notifyCameraOpened();
                }
            });
            try {
                wait();
            } catch (InterruptedException e) {
                Log.w(TAG, "wait was interrupted");
            }
        }
    }
}
