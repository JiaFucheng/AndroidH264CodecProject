package com.example.androidh264codecproject.yuvtools;

public class YUVFormatTransformTool {

    public static byte[] I420ToYV12(byte[] data, int width, int height) {
        if (data != null) {
            final int lumaSize = width * height;
            final int chromaSize = lumaSize>>2;
            final int frameSize = lumaSize + (chromaSize<<1);
            final int YOffset  = 0;
            final int CbOffset = lumaSize;
            final int CrOffset = CbOffset + chromaSize;
            byte[] outputData = new byte[frameSize];

            System.arraycopy(data, YOffset,  outputData, YOffset,  lumaSize);   // Y to Y
            System.arraycopy(data, CrOffset, outputData, CbOffset, chromaSize); // Cr to Cb
            System.arraycopy(data, CbOffset, outputData, CrOffset, chromaSize); // Cb to Cr

            return outputData;
        } else {
            return null;
        }
    }

}
