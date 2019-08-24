package com.example.androidh264codecproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("motion_search_jni");
        // System.loadLibrary("motion_search_opencl_jni");
        // System.loadLibrary("encoder_jni");
        System.loadLibrary("ffmpeg_h264_decoder_jni");

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
        setContentView(R.layout.activity_main);

        checkStoragePermission();

        new AVCEncoderTestThread().start();
    }

    private void checkStoragePermission() {
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (this.shouldShowRequestPermissionRationale(Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                // Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }

            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {
            // Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            // Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        }
    }
}
