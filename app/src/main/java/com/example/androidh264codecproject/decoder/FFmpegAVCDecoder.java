package com.example.androidh264codecproject.decoder;

import com.example.androidh264codecproject.encoder.MotionVectorMap;

public class FFmpegAVCDecoder {

    private long handle;
    private int videoWidth;
    private int videoHeight;
    private int mvMapDataSize;
    private int[] mvMapData;

    public FFmpegAVCDecoder(int videoWidth, int videoHeight) {
        this.videoWidth  = videoWidth;
        this.videoHeight = videoHeight;
        this.mvMapDataSize = videoWidth * videoHeight * 2;
        this.mvMapData     = new int[mvMapDataSize];
        this.handle      = nativeInit(videoWidth, videoHeight);
    }

    public void free() {
        nativeFree(this.handle);
    }

    public boolean decodeFrame(byte[] packetData) {
        return nativeDecodeFrame(this.handle, packetData);
    }

    public MotionVectorMap getMotionVectorMap() {
        if (nativeGetMotionVectorMapData(this.handle, mvMapData)) {
            return new MotionVectorMap(videoWidth, videoHeight, mvMapData);
        } else {
            return null;
        }
    }

    private native long nativeInit(int videoWidth, int videoHeight);
    private native void nativeFree(long handle);
    private native boolean nativeDecodeFrame(long handle, byte[] packetData);
    private native boolean nativeGetMotionVectorMapData(long handle, int[] mvMapData);

}
