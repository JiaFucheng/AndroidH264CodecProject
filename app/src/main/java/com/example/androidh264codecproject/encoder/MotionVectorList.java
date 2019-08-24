package com.example.androidh264codecproject.encoder;

public class MotionVectorList {

    private int count;
    private int[] data;

    public MotionVectorList(int[] data) {
        this.count = data.length / 6;
        this.data  = data;
    }

    public int getCount() {
        return this.count;
    }

    public MotionVectorListItem getItem(int i) {

        int off = i * 6;

        int mvX   = data[off];
        int mvY   = data[off+1];
        int posX  = data[off+2];
        int posY  = data[off+3];
        int sizeX = data[off+4];
        int sizeY = data[off+5];

        return new MotionVectorListItem(mvX, mvY, posX, posY, sizeX, sizeY);
    }

}
