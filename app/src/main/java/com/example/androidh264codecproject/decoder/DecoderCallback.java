package com.example.androidh264codecproject.decoder;

public abstract class DecoderCallback {

    protected static final String TAG = "DecoderCallback";

    public abstract void call(byte[] encodedData, int size);

    public abstract void close();
}
