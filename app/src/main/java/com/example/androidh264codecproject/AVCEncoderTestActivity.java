package com.example.androidh264codecproject;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.example.androidh264codecproject.encoder.EncodeMode;

public class AVCEncoderTestActivity extends Activity {

    static {
        System.loadLibrary("motion_search_jni");
        System.loadLibrary("ffmpeg_h264_decoder_jni");
        System.loadLibrary("ffmpeg_avi_decoder_jni");

        /* FFmpeg Library */
        System.loadLibrary("avcodec");
        System.loadLibrary("avdevice");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("postproc");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avcencoder_test);

        initUI();
    }

    private void initUI() {
        Button syncButton = (Button) findViewById(R.id.sync_button);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVCEncoderTestThread t = new AVCEncoderTestThread();
                t.setEncodeMode(EncodeMode.SYNC_MODE);
                t.start();
            }
        });

        Button asyncButton = (Button) findViewById(R.id.async_button);
        asyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVCEncoderTestThread t = new AVCEncoderTestThread();
                t.setEncodeMode(EncodeMode.ASYNC_MODE);
                t.start();
            }
        });
    }

}
