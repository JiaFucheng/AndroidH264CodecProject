package com.example.androidh264codecproject.process;

public class AccumulateDevice {

    protected int width;
    protected int height;
    protected int accuMVDataSize;

    public AccumulateDevice(int width, int height, int block_size) {
        init(width, height, block_size);
    }

    protected void init(int width, int height, int block_size) {
        accuMVDataSize = width * height * 2;
    }

    public void shutdown() {}

    public void resetAccumulatedMV() {}

    public void accumulateMV(int[] mvArrayData) {}

    public int[] getAccumulatedMV() {
        return null;
    }
}
