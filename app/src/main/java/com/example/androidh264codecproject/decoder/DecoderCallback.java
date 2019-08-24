package com.example.androidh264codecproject.decoder;

public abstract class DecoderCallback {

    public abstract void call(byte[] encodedData, int size);

    public abstract void close();

}
