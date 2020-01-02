package com.example.androidh264codecproject.process;

public class ResidualCPU extends Residual {

    private long handle;

    public ResidualCPU(int width, int height, int blockSize) {
        super(width, height, blockSize);

        this.handle = nativeInit(width, height, blockSize);
    }

    public void shutdown() {
        nativeShutdown(this.handle);
    }

    public int[] getYUVResidual(byte[] curYUVData, byte[] refYUVData, int[] mvArrayData) {
        resArrayData = new int[resYUVArraySize];
        nativeGetYUVResidual(this.handle, curYUVData, refYUVData, mvArrayData, resArrayData);
        return resArrayData;
    }

    public int[] getResidual(byte[] curYUVData, byte[] refYUVData, int[] accuMVArrayData) {
        // Bug Point: If not resize resArrayData, it is YUV array size which is wrong.
        resArrayData = new int[resArraySize];
        nativeGetResidual(this.handle, curYUVData, refYUVData, accuMVArrayData, resArrayData);
        return resArrayData;
    }

    private static native long nativeInit(int width, int height, int blockSize);
    private static native void nativeShutdown(long handle);

    private static native void nativeGetYUVResidual(long handle,
                                                    byte[] curYUVData,  byte[] refYUVData,
                                                    int[]  mvArrayData, int[]  resArrayData);
    private static native void nativeGetResidual(long handle,
                                                 byte[] curYUVData,      byte[] refYUVData,
                                                 int[]  accuMVArrayData, int[]  resArrayData);

}
