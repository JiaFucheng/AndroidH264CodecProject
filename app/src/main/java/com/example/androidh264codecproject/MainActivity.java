package com.example.androidh264codecproject;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int STATE_PAUSED = 1;
    private static final int STATE_STARTED = 2;

    public static final int MSG_UPDATE_PROCESS = 1;
    public static final int MSG_FINISH_PROCESS = 2;

    private Spinner mDatasetSpinner;
    private EditText mBitrateEditText;
    private EditText mStartIndexEditText;
    private TextView mProcessTextView;
    private ProgressBar mProgressBar;
    private Button mStartPauseButton;

    private int buttonState = STATE_PAUSED;

    private AVCEncoderDataSetThread currentThread;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_PROCESS:
                    updateProcess();
                    break;
                case MSG_FINISH_PROCESS:
                    if (buttonState == STATE_STARTED)
                        onClickStartPauseButton();
            }
        }
    };

    static {
        System.loadLibrary("motion_search_jni");
        //System.loadLibrary("motion_search_opencl_jni");
        //System.loadLibrary("encoder_jni");
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
        setContentView(R.layout.activity_main);

        checkStoragePermission();

        initUI();

        // Transfer AVI to H264
        //new AVCEncoderDataSetThread().start();
    }

    private void checkStoragePermission() {
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (this.shouldShowRequestPermissionRationale(Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                //Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }

            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {
            //Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            //Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        }
    }

    private void updateUIWhenStarted() {
        mDatasetSpinner.setEnabled(false);
        mBitrateEditText.setEnabled(false);
        mStartIndexEditText.setEnabled(false);
        mStartPauseButton.setText("Pause");
    }

    private void updateUIWhenPaused() {
        mDatasetSpinner.setEnabled(true);
        mBitrateEditText.setEnabled(true);
        mStartIndexEditText.setEnabled(true);
        mStartPauseButton.setText("Start");
    }

    private void onClickStartPauseButton() {
        if (buttonState == STATE_PAUSED) {
            try {
                // Get value from UI
                int datasetIndex = mDatasetSpinner.getSelectedItemPosition();
                int bitrate = Integer.parseInt(mBitrateEditText.getEditableText().toString());
                int si = Integer.parseInt(mStartIndexEditText.getEditableText().toString());

                // Start a task thread
                currentThread = new AVCEncoderDataSetThread();
                currentThread.setDatasetIndex(datasetIndex);
                currentThread.setBitrate(bitrate);
                currentThread.setStartIndex(si);
                currentThread.setReportHandler(mHandler);
                currentThread.start();

                buttonState = STATE_STARTED;
                updateUIWhenStarted();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(this, "Input format wrong", Toast.LENGTH_SHORT).show();
            }
        } else if (buttonState == STATE_STARTED) {
            // Exit task thread
            currentThread.setExitFlag(true);

            buttonState = STATE_PAUSED;
            updateUIWhenPaused();
        }
    }

    private void initUI() {
        String[] datasetItems = new String[]{
                "Split1 Train Part1 (4991)",
                "Split1 Train Part2 (4546)",
                "Split1 Test (3783)",
                "Split YoYo (1)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, datasetItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mDatasetSpinner = (Spinner) findViewById(R.id.dataset_spinner);
        mDatasetSpinner.setAdapter(adapter);

        mBitrateEditText = (EditText) findViewById(R.id.bitrate_edittext);
        mStartIndexEditText = (EditText) findViewById(R.id.startindex_edittext);
        mProcessTextView = (TextView) findViewById(R.id.process_textview);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mStartPauseButton = (Button) findViewById(R.id.start_pause_button);

        mStartPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStartPauseButton();
            }
        });
    }

    private void updateProcess() {
        if (currentThread != null) {
            String process = currentThread.getCurrentProcessText();
            int progress = currentThread.getCurrentProgress();
            mProcessTextView.setText(process);
            mProgressBar.setProgress(progress);
        }
    }
}
