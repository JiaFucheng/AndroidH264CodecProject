package com.example.androidh264codecproject.yuvtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class YUVI420FileReader {

    private int mWidth;
    private int mHeight;
    private int mFrameSize;
    private FileInputStream mInputStream;

    public YUVI420FileReader(File file, int width, int height) throws IOException {
        mInputStream = new FileInputStream(file);
        mWidth     = width;
        mHeight    = height;
        mFrameSize = width * height * 3 / 2;
    }

    public byte[] readFrameData() throws IOException {
        byte[] bytes = new byte[mFrameSize];
        int ret = mInputStream.read(bytes, 0, mFrameSize);
        if (ret != -1)
            return bytes;
        else
            return null;
    }

    public void close() throws IOException {
        mInputStream.close();
    }

}
