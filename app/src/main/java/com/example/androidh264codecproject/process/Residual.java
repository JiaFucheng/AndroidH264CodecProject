package com.example.androidh264codecproject.process;

public class Residual {

    protected long handle;

    protected int width;
    protected int height;
    protected int resArraySize;
    protected int resYUVArraySize;
    protected int[] resArrayData;

    public Residual(int width, int height, int blockSize) {
        init(width, height, blockSize);
    }

    public void init(int width, int height, int blockSize) {
        resArraySize    = width * height * 3;
        resYUVArraySize = width * height * 3 / 2;
    }

    public void shutdown() {}

    public int[] getYUVResidual(byte[] curYUVData, byte[] refYUVData, int[] mvArrayData) { return null; }

    public int[] getResidual(byte[] curYUVData, byte[] refYUVData, int[] accuMVArrayData) { return null; }

}
