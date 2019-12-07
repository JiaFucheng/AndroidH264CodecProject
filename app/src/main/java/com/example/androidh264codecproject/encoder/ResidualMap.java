package com.example.androidh264codecproject.encoder;

public class ResidualMap {

    private int width;
    private int height;
    private byte[] data;

    public ResidualMap(int w, int h, byte[] data) {
        this.width  = w;
        this.height = h;
        this.data   = data;
    }

    public byte[] getData() {
        return this.data;
    }

}
