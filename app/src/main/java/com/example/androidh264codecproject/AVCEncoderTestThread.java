package com.example.androidh264codecproject;

import com.example.androidh264codecproject.decoder.FFmpegAVCDecoderCallback;
import com.example.androidh264codecproject.encoder.AVCEncoder;
import com.example.androidh264codecproject.encoder.BitrateTool;
import com.example.androidh264codecproject.encoder.MotionVectorMap;

import java.util.Locale;

public class AVCEncoderTestThread extends Thread {

    @Override
    public void run() {
        super.run();

        // 1280 / 848 / 640 / 340
        int videoWidth  = 340;
        // 720 / 480 / 360 / 256
        int videoHeight = 256;
        int frameRate   = 25;
        int bitrate     = BitrateTool.getAdaptiveBitrate(
                                        videoWidth, videoHeight);

        MotionVectorMap.deleteMotionVectorMapFileIfExist();

        AVCEncoder encoder = new AVCEncoder(videoWidth, videoHeight, frameRate, bitrate);

        /*
         * PATH 1: /storage/emulated/0/coviar_opencl/UCF-101/YoYo/v_YoYo_g01_c01.yuv
         * PATH 2: /storage/emulated/0/coviar_opencl/Video/basketballshoot/basketballshoot_%dp.yuv
         **/
        encoder.setInputYUVFile("/storage/emulated/0/"
                + String.format(Locale.CHINA,
                        "coviar_opencl/UCF-101/YoYo/v_YoYo_g01_c01.yuv", videoHeight));
        /*
         * PATH 1: UCF-101-H264/YoYo
         * PATH 2: Video-H264/BasketballShoot
         */
        encoder.setOutputH264Path("/storage/emulated/0/" + "coviar_opencl/UCF-101-H264/YoYo");

        encoder.setDecoderCallback(new FFmpegAVCDecoderCallback(videoWidth, videoHeight));

        //encoder.start();
        encoder.startAsync();
    }
}
