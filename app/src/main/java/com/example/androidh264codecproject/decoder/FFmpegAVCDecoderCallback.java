package com.example.androidh264codecproject.decoder;

import android.util.Log;

import com.example.androidh264codecproject.encoder.MotionVectorList;
import com.example.androidh264codecproject.encoder.MotionVectorListItem;
import com.example.androidh264codecproject.encoder.MotionVectorMap;
import com.example.androidh264codecproject.process.AccumulateCPU;
import com.example.androidh264codecproject.process.AccumulateMode;
import com.example.androidh264codecproject.process.ResidualCPU;

import java.util.Locale;

public class FFmpegAVCDecoderCallback extends DecoderCallback {

    private static final int FRAME_TYPE_I = 1;
    private static final int FRAME_TYPE_P = 2;

    private boolean getMVMapFlag = true;
    private boolean saveMVMapFlag = false;
    private boolean accuMVFlag = true;
    private boolean getMVListFlag = false;
    private boolean getResidualFlag = true;
    private boolean showLogFlag = false;

    private FFmpegAVCDecoder decoder;
    private AccumulateCPU    accuMV;
    private ResidualCPU      resProc;

    private final int GOP_SIZE = 12;
    private int pframeLimit;
    private int curPFrameIndex;
    private byte[] refIFrameData;

    public FFmpegAVCDecoderCallback(int w, int h) {
        decoder = new FFmpegAVCDecoder(w, h);
        accuMV  = new AccumulateCPU(w, h, 16);
        resProc = new ResidualCPU(w, h, 16);

        accuMV.resetAccumulatedMV();
    }

    public void setPFrameLimit(int value) {
        this.pframeLimit = value;
    }

    @Override
    public void call(byte[] encodedData, int size) {
        byte[] packetData = new byte[size];
        System.arraycopy(encodedData, 0, packetData, 0, size);
        long startMs = System.currentTimeMillis();
        if (decoder.decodeFrame(packetData)) {
            // NOTE: Exactly got a decoded frame
            if (showLogFlag) {
                Log.d(TAG, String.format("Decode time %d ms",
                        System.currentTimeMillis() - startMs));
            }

            int frameType = 0;
            MotionVectorMap mvMap;

            if (getMVMapFlag) {
                startMs = System.currentTimeMillis();
                mvMap = decoder.getMotionVectorMap();
                if (mvMap != null) {
                    if (showLogFlag) {
                        Log.d(TAG, String.format("Get mv time %d ms pframe-index %d",
                                System.currentTimeMillis() - startMs,
                                curPFrameIndex + 1));
                    }
                    frameType = FRAME_TYPE_P;
                    curPFrameIndex ++;

                    if (saveMVMapFlag)
                        MotionVectorMap.saveMotionVectorMap(
                                mvMap,
                                MotionVectorMap.DEFAULT_SAVE_FILEPATH,
                                0);

                    if (accuMVFlag) {
                        startMs = System.currentTimeMillis();
                        accuMV.accumulateMV(mvMap.getData(),
                                            AccumulateMode.PIXEL_LEVEL);
                        if (showLogFlag) {
                            Log.d(TAG, String.format(
                                    "Accu time %d ms",
                                    System.currentTimeMillis() - startMs));
                        }
                    }
                } else {
                    frameType = FRAME_TYPE_I;
                    curPFrameIndex = 0;
                    if (accuMVFlag)
                        accuMV.resetAccumulatedMV();
                    refIFrameData = decoder.getFrameData();
                }
            }

            MotionVectorList mvList = null;
            if (getMVListFlag) {
                mvList = decoder.getMotionVectorList();
                if (mvList != null) {
                    frameType = FRAME_TYPE_P;
                    //Log.d(TAG, String.format(Locale.CHINA, "MV List Count: %d", mvList.getCount()));
                    for (int i = 0; i < mvList.getCount(); i++) {
                        MotionVectorListItem item = mvList.getItem(i);
                        int mvX = item.getMvX();
                        int mvY = item.getMvY();
                        int posX = item.getPosX();
                        int posY = item.getPosY();
                        int sizeX = item.getSizeX();
                        int sizeY = item.getSizeY();
                        Log.d(TAG, String.format(Locale.CHINA, "MV (%d,%d) Pos (%d,%d) Size (%d,%d)",
                                mvX, mvY, posX, posY, sizeX, sizeY));
                    }
                } else {
                    frameType = FRAME_TYPE_I;
                }
            }

            if (getResidualFlag) {
                if (frameType == FRAME_TYPE_P) {
                    if (curPFrameIndex == GOP_SIZE - 1 ||
                        curPFrameIndex == pframeLimit) {
                        startMs = System.currentTimeMillis();
                        resProc.getResidual(decoder.getFrameData(),
                                            refIFrameData,
                                            accuMV.getAccumulatedMV());
                        if (showLogFlag) {
                            Log.d(TAG, String.format(Locale.CHINA,
                                    "Get residual time %d ms",
                                    System.currentTimeMillis() - startMs));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        decoder.free();
        accuMV.shutdown();
    }
}
