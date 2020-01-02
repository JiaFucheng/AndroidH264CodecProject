package com.example.androidh264codecproject.process;

public class AccumulateCPU extends Accumulate {

    private long handle;

    public AccumulateCPU(int width, int height, int block_size) {
        super(width, height, block_size);

        this.handle = nativeInit(width, height, block_size);
    }

    public void shutdown() {
        super.shutdown();
        nativeShutdown(this.handle);
    }

    public void resetAccumulatedMV() {
        nativeResetAccumulatedMV(this.handle);
    }

    public void accumulateMV(int[] mvArrayData, int mode) {
        nativeAccumulateMV(this.handle, mvArrayData, mode);
    }

    public int[] getAccumulatedMV() {
        int[] accuMVArrayData = new int[accuMVDataSize];
        nativeGetAccumulatedMV(this.handle, accuMVArrayData);
        return accuMVArrayData;
    }

    public void resetAccumulatedResidual() {
        nativeResetAccumulatedResidual(this.handle);
    }

    public void accumulateResidual(byte[] resArrayData, int[] mvListData, int mvListCount) {
        nativeAccumulateResidual(this.handle, resArrayData, mvListData, mvListCount);
    }

    public int[] getAccumulatedResidual() {
        int[] accuResArrayData = new int[accuResDataSize];
        nativeGetAccumulatedResidual(this.handle, accuResArrayData);
        return accuResArrayData;
    }

    private static native long nativeInit(int width, int height, int block_size);
    private static native void nativeShutdown(long handle);

    private static native void nativeResetAccumulatedMV(long handle);
    private static native void nativeAccumulateMV(long handle, int[] mvArrayData, int mode);
    private static native void nativeGetAccumulatedMV(long handle, int[] accuMVArrayData);

    private static native void nativeResetAccumulatedResidual(long handle);
    private static native void nativeAccumulateResidual(
                                long handle, byte[] resArrayData, int[] mvListData, int mvListCount);
    private static native void nativeGetAccumulatedResidual(long handle, int[] accuResArrayData);
}
