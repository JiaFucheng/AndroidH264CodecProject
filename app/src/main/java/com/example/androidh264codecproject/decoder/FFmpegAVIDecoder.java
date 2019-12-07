package com.example.androidh264codecproject.decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FFmpegAVIDecoder {

    private final int DEFAULT_IN_BUF_SIZE = 4096;

    private long mNativeHandle;

    private String mFilename;
    private FileInputStream mFileInputStream;

    private int mVideoWidth;
    private int mVideoHeight;

    private byte[] mInBuffer;
    private byte[] mFrameData;

    public FFmpegAVIDecoder() {
        mVideoWidth  = 0;
        mVideoHeight = 0;
        mFileInputStream = null;
    }

    public void setInputFilename(String filename) {
        mFilename = filename;
    }

    public void setVideoInfo(int width, int height) {
        mVideoWidth  = width;
        mVideoHeight = height;
    }

    public void prepare() throws IOException {
        // Create frame data buffer
        // NOTE: Only for YUV420 format
        int frameSize = mVideoWidth * mVideoHeight * 3 / 2;
        mFrameData = new byte[frameSize];

        // Create input data buffer
        //mInBuffer = new byte[DEFAULT_IN_BUF_SIZE];

        // Open file input stream
        //mFileInputStream = new FileInputStream(new File(mFilename));

        // Create native ffmpeg avi decoder
        mNativeHandle = nativeCreateFFmpegAVIDecoder(mFilename);
    }

    public byte[] getFrameData() throws IOException {

        for (;;) {
            /*if (nativeGetInBufferLength(mNativeHandle) <= 0) {
                // Read input buffer data
                int readSize = mFileInputStream.read(mInBuffer);
                if (readSize > 0) {
                    // Copy to decoder input buffer
                    nativeCopyToNativeInBuffer(mNativeHandle, mInBuffer, readSize);
                } else if (readSize == -1) {
                    // EOF
                    return null;
                }
            }*/

            // Decode a frame
            boolean gotFrame = nativeDecodeFrame(mNativeHandle, mFrameData);
            if (gotFrame) {
                return mFrameData;
            } else {
                return null;
            }
        }
    }

    public void close() throws IOException {
        if (mFileInputStream != null)
            mFileInputStream.close();
        nativeFreeFFmpegAVIDecoder(mNativeHandle);
    }

    private native long nativeCreateFFmpegAVIDecoder(String filename);
    private native void nativeSetVideoInfo(long handle, int width, int height);
    //private native int  nativeGetInBufferLength(long handle);
    //private native void nativeCopyToNativeInBuffer(long handle, byte[] inBuffer, int bufSize);
    private native boolean nativeDecodeFrame(long handle, byte[] outFrameBuffer);
    private native void    nativeFreeFFmpegAVIDecoder(long handle);

}
