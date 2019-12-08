package com.example.androidh264codecproject;

import android.util.Log;

import com.example.androidh264codecproject.dataset.DataSetLoader;
import com.example.androidh264codecproject.decoder.FFmpegAVCDecoderCallback;
import com.example.androidh264codecproject.decoder.FFmpegAVIDecoder;
import com.example.androidh264codecproject.encoder.AVCEncoder;
import com.example.androidh264codecproject.encoder.BitrateTool;
import com.example.androidh264codecproject.encoder.EncodeMode;
import com.example.androidh264codecproject.encoder.MotionVectorMap;

import java.io.IOException;
import java.util.Locale;

public class AVCEncoderTestThread extends Thread {

    private boolean enableAVIDecodeTest = false;
    private boolean enableH264OutputFile = false;
    private boolean enableCallBackTest = true;
    private int encodeMode = EncodeMode.SYNC_MODE;

    public void setEncodeMode(int encodeMode) {
        this.encodeMode = encodeMode;
    }

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

        final String sdCardDirectory = "/storage/emulated/0/";

        /*
         * PATH 1: /storage/emulated/0/coviar_opencl/UCF-101/YoYo/v_YoYo_g01_c01.yuv
         * PATH 2: /storage/emulated/0/coviar_opencl/Video/basketballshoot/basketballshoot_%dp.yuv
         **/
        String inputYUVFilePath = sdCardDirectory
                                    + String.format(Locale.CHINA,
                                        "coviar_opencl/UCF-101/YoYo/v_YoYo_g01_c01.yuv",
                                        videoHeight);

        /*
         * PATH 1: UCF-101-H264/YoYo
         * PATH 2: Video-H264/BasketballShoot
         **/
        String outputH264Path = sdCardDirectory + "coviar_opencl/UCF-101-H264/YoYo";

        // Delete MV map file if it exists
        MotionVectorMap.deleteMotionVectorMapFileIfExist();

        FFmpegAVIDecoder decoder = null;
        if (enableAVIDecodeTest) {
            String inputAVIFilePath = sdCardDirectory
                    + "coviar_opencl/UCF-101-AVI/YoYo/v_YoYo_g01_c01_340_256.avi";

            // Create and initialize ffmpeg avi decoder
            decoder = new FFmpegAVIDecoder();
            decoder.setInputFilename(inputAVIFilePath);
            decoder.setVideoInfo(videoWidth, videoHeight);
            try {
                decoder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create and initialize ffmpeg yuv encoder
        AVCEncoder encoder = new AVCEncoder(videoWidth, videoHeight, frameRate, bitrate);
        if (enableAVIDecodeTest) {
            encoder.setFFmpegAVIDecoder(decoder);
        } else {
            encoder.setInputYUVFile(inputYUVFilePath);
        }
        if (enableH264OutputFile) {
            encoder.setOutputH264Path(outputH264Path);
        }
        if (enableCallBackTest) {
            encoder.setDecoderCallback(
                    new FFmpegAVCDecoderCallback(videoWidth, videoHeight));
        }

        // Start encoder
        if (encodeMode == EncodeMode.SYNC_MODE)
            encoder.start();
        else if (encodeMode == EncodeMode.ASYNC_MODE)
            encoder.startAsync();
    }

    private void dataSetLoaderTest(final String sdCardDirectory) {
        try {
            DataSetLoader loader = new DataSetLoader(
                    // List File
                    sdCardDirectory + "coviar_opencl/datalists/ucf101_split_jfc_test.txt",
                    // Input File Directory
                    sdCardDirectory + "coviar_opencl/UCF-101",
                    // Output File Directory
                    sdCardDirectory + "coviar_opencl/UCF-101-H264");

            String filePath = loader.getNextFileItemPath(DataSetLoader.TYPE_YUV);

            Log.i("DataSetLoader", String.format(Locale.CHINA, "Load file %s", filePath));

            String outFilePath = loader.getCurrentOutFileItemPath(DataSetLoader.TYPE_H264);

            Log.i("DataSetLoader", String.format(Locale.CHINA, "Output file %s", outFilePath));

            loader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
