package com.example.androidh264codecproject;

import android.os.Handler;
import android.util.Log;

import com.example.androidh264codecproject.dataset.DataSetLoader;
import com.example.androidh264codecproject.decoder.FFmpegAVIDecoder;
import com.example.androidh264codecproject.encoder.AVCEncoder;
import com.example.androidh264codecproject.encoder.BitrateTool;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static com.example.androidh264codecproject.MainActivity.MSG_FINISH_PROCESS;
import static com.example.androidh264codecproject.MainActivity.MSG_UPDATE_PROCESS;

public class AVCEncoderDataSetThread extends Thread {

    private static final String TAG = "AVCEncoderDataSet";

    private final String[] datalistPath = {
            "LocalAction/datalists/ucf101_split1_train.txt",
            "LocalAction/datalists/ucf101_split1_test.txt",
            "LocalAction/datalists/ucf101_split_jfc_test.txt"};

    private final String[] datasetPath = {
            "LocalAction/UCF-101-AVI-340-256/split1_train_part1",
            "LocalAction/UCF-101-AVI-340-256/split1_train_part2",
            "LocalAction/UCF-101-AVI-340-256/split1_test",
            "LocalAction/UCF-101-AVI-340-256/split1_test"};

    private final int[] datasetCount = {4991, 4546, 3783, 1};

    private boolean exitFlag;
    private int datasetIndex;
    private int videoWidth;
    private int videoHeight;
    private int frameRate;
    private int bitrate;
    private int startIndex;
    private int currentProgress;
    private String currentProcessText;
    private Handler uiHandler;

    public AVCEncoderDataSetThread() {
        this.videoWidth  = 340;
        this.videoHeight = 256;
        this.frameRate   = 25;
        this.bitrate     = BitrateTool.getAdaptiveBitrate(videoWidth, videoHeight);
        this.startIndex  = 0;
        this.exitFlag    = false;

    }

    public void setDatasetIndex(int i) {
        this.datasetIndex = i;
    }

    public void setBitrate(int rate) {
        this.bitrate = rate * 1000; // Kbps
    }

    public void setStartIndex(int i) {
        this.startIndex = i;
    }

    public void setExitFlag(boolean f) {
        this.exitFlag = f;
    }

    public void setReportHandler(Handler h) {
        this.uiHandler = h;
    }

    public int getCurrentProgress() {
        return this.currentProgress;
    }

    public String getCurrentProcessText() {
        return this.currentProcessText;
    }

    private void updateInitProgressInUI() {
        currentProgress = 0;
        currentProcessText = "";
        uiHandler.sendEmptyMessage(MSG_UPDATE_PROCESS);
    }

    private void updateCountDownInUI(int countDown) {
        currentProgress = countDown * 100 / 3;
        currentProcessText = String.format(Locale.CHINA, "Count down %d s", countDown);
        uiHandler.sendEmptyMessage(MSG_UPDATE_PROCESS);
    }

    private void updateProgressInUI(int videoIndex, String processText) {
        currentProgress = (videoIndex + 1) * 100 / datasetCount[datasetIndex];
        currentProcessText = processText;
        uiHandler.sendEmptyMessage(MSG_UPDATE_PROCESS);
    }

    private void updateFinishedProgressInUI() {
        uiHandler.sendEmptyMessage(MSG_FINISH_PROCESS);
    }

    @Override
    public void run() {
        super.run();

        try {
            // Print setting
            Log.i(TAG, String.format(Locale.CHINA,
                    "Current setting is\n" +
                            "Video Size:%d*%d\n" +
                            "Frame Rate:%d\n" +
                            "Bitrate:%d\n" +
                            "Start Index:%d", videoWidth, videoHeight, frameRate, bitrate, startIndex));

            // Count down for 3 seconds
            Log.i(TAG, "Task will start after 3s");
            int countDown = 3;
            while (countDown >= 0) {
                updateCountDownInUI(countDown);
                countDown --;
                Thread.sleep(1000);
                // Maybe pause during count down
                if (exitFlag) {
                    updateInitProgressInUI();
                    return;
                }
            }

            final String sdCardDirectory = "/storage/emulated/0/";

            String listPath;
            switch (datasetIndex) {
                case 0:
                case 1:
                    listPath = datalistPath[0];
                    break;
                case 2:
                    listPath = datalistPath[1];
                    break;
                case 3:
                    listPath = datalistPath[2];
                    break;
                default:
                    listPath = null;
            }

            DataSetLoader loader = new DataSetLoader(
                    // List File
                    sdCardDirectory + listPath,
                    // Input File Directory
                    sdCardDirectory + datasetPath[datasetIndex],
                    // Output File Directory
                    sdCardDirectory + "LocalAction/UCF-101-H264");
            Log.i(TAG, "Dataset loader created");

            int videoIndex = 0;
            //int videoCount = Integer.MAX_VALUE;
            //int endIndex = startIndex + videoCount;
            boolean ret;
            String filePath;
            File file;
            AVCEncoder encoder = null;
            long startMs = 0;

            while (!exitFlag && (filePath = loader.getNextFileItemPath(DataSetLoader.TYPE_AVI)) != null) {
                //Log.i("DataSetLoader", String.format(Locale.CHINA, "Load file %s", filePath));

                // Check input file path
                file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }
                // Skip video before start index
                if (videoIndex < startIndex) {
                    // Update process
                    updateProgressInUI(videoIndex, String.format(Locale.CHINA, "Skip %s", loader.getCurrentFileName()));
                    videoIndex ++;
                    continue;
                }

                String outFilePath = loader.getCurrentOutFileItemPath(DataSetLoader.TYPE_H264);

                // Check output file path
                file = new File(outFilePath).getParentFile();
                if (!file.exists()) {
                    ret = file.mkdirs();
                }

                String inputAVIFilePath = filePath;
                String outputH264Path   = outFilePath;

                // Create and initialize ffmpeg avi decoder
                FFmpegAVIDecoder decoder = new FFmpegAVIDecoder();
                decoder.setInputFilename(inputAVIFilePath);
                decoder.setVideoInfo(videoWidth, videoHeight);
                try {
                    decoder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (encoder != null) {
                    // Wait if last encoder has not finished
                    do {
                        Thread.sleep(100);
                    } while (encoder.isRunning());

                    Log.i(TAG, String.format(
                        "Encoded %d video %d ms", videoIndex, System.currentTimeMillis() - startMs));

                    videoIndex ++;

                    // Limit video index
                    //if (videoIndex >= endIndex)
                    //    break;
                }

                // Update process
                updateProgressInUI(videoIndex,
                        String.format(Locale.CHINA, "[%d%%] %d %s",
                                (videoIndex + 1) * 100/(datasetCount[datasetIndex]),
                                videoIndex, loader.getCurrentFileName()));

                // Wait 100ms for next encoder
                Thread.sleep(100);

                encoder = new AVCEncoder(videoWidth, videoHeight, frameRate, bitrate);
                encoder.setFFmpegAVIDecoder(decoder);
                encoder.setOutputH264Path(outputH264Path);
                encoder.startAsync();  // Async Mode

                startMs = System.currentTimeMillis();
            }

            // Last one video
            if (encoder != null) {
                do {
                    Thread.sleep(100);
                } while (encoder.isRunning());
                Log.i(TAG, String.format(
                        "Encoded %d video %d ms", videoIndex, System.currentTimeMillis() - startMs));
            }

            loader.close();
            Log.i(TAG, "Dataset loader closed");

            // Update process when finished
            updateFinishedProgressInUI();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
