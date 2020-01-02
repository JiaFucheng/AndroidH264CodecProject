package com.example.androidh264codecproject.process;

public class Accumulate {

    protected int width;
    protected int height;
    protected int accuMVDataSize;
    protected int accuResDataSize;

    public Accumulate(int width, int height, int block_size) {
        init(width, height, block_size);
    }

    protected void init(int width, int height, int block_size) {
        accuMVDataSize  = width * height * 2;
        accuResDataSize = width * height * 3;
    }

    public void shutdown() {}

    public void resetAccumulatedMV() {}

    public void accumulateMV(int[] mvArrayData) {}

    public int[] getAccumulatedMV() {
        return null;
    }
}
