package com.example.androidh264codecproject.decoder;

import com.example.androidh264codecproject.encoder.MotionVectorList;
import com.example.androidh264codecproject.encoder.MotionVectorMap;
import com.example.androidh264codecproject.encoder.ResidualMap;

public class FFmpegAVCDecoder {

    private long handle;
    private int videoWidth;
    private int videoHeight;
    private int yuvFrameDataSize;
    private int mvMapDataSize;
    private int resMapDataSize;
    private int[] mvMapData;
    private byte[] resMapData;

    public FFmpegAVCDecoder(int videoWidth, int videoHeight) {
        this.videoWidth  = videoWidth;
        this.videoHeight = videoHeight;
        this.yuvFrameDataSize = videoWidth * videoHeight * 3 / 2;
        this.mvMapDataSize    = videoWidth * videoHeight * 2;
        this.resMapDataSize   = videoWidth * videoHeight * 3 / 2;
        this.mvMapData     = new int[mvMapDataSize];
        this.resMapData    = new byte[resMapDataSize];
        this.handle        = nativeInit(videoWidth, videoHeight);
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

    public MotionVectorList getMotionVectorList() {
        int count = nativeGetMotionVectorListCount(this.handle);
        if (count > 0) {
            int[] mvListData = new int[count * 6];
            nativeGetMotionVectorList(this.handle, mvListData);
            return new MotionVectorList(mvListData);
        } else {
            return null;
        }
    }

    public ResidualMap getResidualMap() {
        if (nativeGetResidualMapData(this.handle, resMapData)) {
            return new ResidualMap(videoWidth, videoHeight, resMapData);
        } else {
            return null;
        }
    }

    public byte[] getFrameData() {
        byte[] yuvFrameData = new byte[yuvFrameDataSize];
        if (nativeGetYUVFrameData(this.handle, yuvFrameData)) {
            return yuvFrameData;
        } else {
            return null;
        }
    }

    private native long nativeInit(int videoWidth, int videoHeight);
    private native void nativeFree(long handle);
    private native boolean nativeDecodeFrame(long handle, byte[] packetData);
    private native boolean nativeGetMotionVectorMapData(long handle, int[] mvMapData);
    private native int nativeGetMotionVectorListCount(long handle);
    private native void nativeGetMotionVectorList(long handle, int[] mvListData);
    private native boolean nativeGetResidualMapData(long handle, byte[] resArrayData);
    private native boolean nativeGetYUVFrameData(long handle, byte[] yuvFrameData);
}
