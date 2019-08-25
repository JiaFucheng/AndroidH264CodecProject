package com.example.androidh264codecproject.decoder;

import android.util.Log;

import com.example.androidh264codecproject.encoder.MotionVectorList;
import com.example.androidh264codecproject.encoder.MotionVectorListItem;
import com.example.androidh264codecproject.encoder.MotionVectorMap;
import com.example.androidh264codecproject.process.AccumulateCPU;
import com.example.androidh264codecproject.process.AccumulateMode;

import java.util.Locale;

public class FFmpegAVCDecoderCallback extends DecoderCallback {

    private static final String TAG = "DecoderCallback";

    private boolean saveMV = false;
    private boolean accuMV = false;

    private FFmpegAVCDecoder decoder;
    private AccumulateCPU    accu;

    public FFmpegAVCDecoderCallback(int w, int h) {
        decoder = new FFmpegAVCDecoder(w, h);
        accu    = new AccumulateCPU(w, h, 16);

        accu.resetAccumulatedMV();
    }

    @Override
    public void call(byte[] encodedData, int size) {
        byte[] packetData = new byte[size];
        System.arraycopy(encodedData, 0, packetData, 0, size);
        if (decoder.decodeFrame(packetData)) {
            // NOTE: Exactly got a decoded frame
            MotionVectorMap mvMap = decoder.getMotionVectorMap();
            if (mvMap != null) {
                if (saveMV)
                    MotionVectorMap.saveMotionVectorMap(
                            mvMap,
                            MotionVectorMap.DEFAULT_SAVE_FILEPATH,
                            0);

                if (accuMV) {
                    long startMs = System.currentTimeMillis();
                    accu.accumulateMV(mvMap.getData(),
                            AccumulateMode.PIXEL_LEVEL);
                    Log.d(TAG, String.format(
                            "Accu time %d ms",
                            System.currentTimeMillis() - startMs));
                }
            }

            MotionVectorList mvList = decoder.getMotionVectorList();
            if (mvList != null) {
                Log.d(TAG, String.format(Locale.CHINA, "MV List Count: %d", mvList.getCount()));
                for (int i = 0; i < mvList.getCount(); i++) {
                    MotionVectorListItem item = mvList.getItem(i);
                    int mvX = item.getMvX();
                    int mvY = item.getMvY();
                    int posX = item.getPosX();
                    int posY = item.getPosY();
                    int sizeX = item.getSizeX();
                    int sizeY = item.getSizeY();
                }
            }
        }
    }

    @Override
    public void close() {
        decoder.free();
        accu.shutdown();
    }
}
