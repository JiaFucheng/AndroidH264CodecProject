package com.example.androidh264codecproject.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataSetLoader {

    public static final int TYPE_YUV  = 1;
    public static final int TYPE_AVI  = 2;
    public static final int TYPE_H264 = 3;

    private String mInFileRoot;
    private String mOutFileRoot;
    private String mCurrentFileName;
    private BufferedReader mBufferedReader;

    public DataSetLoader(String listFilename, String inFileRoot, String outFileRoot) throws IOException {
        File file = new File(listFilename);
        this.mBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        // Add separator if not
        if (!String.valueOf(inFileRoot.charAt(inFileRoot.length()-1)).equals(File.separator))
            inFileRoot = inFileRoot + File.separator;
        if (!String.valueOf(outFileRoot.charAt(outFileRoot.length()-1)).equals(File.separator))
            outFileRoot = outFileRoot + File.separator;
        this.mInFileRoot  = inFileRoot;
        this.mOutFileRoot = outFileRoot;
    }

    public void close() throws IOException {
        if (mBufferedReader != null)
            mBufferedReader.close();
    }

    private String getFileType(int type) {
        switch (type) {
            case TYPE_YUV:
                return ".yuv";
            case TYPE_AVI:
                return ".avi";
            case TYPE_H264:
                return ".h264";
            default:
                return null;
        }
    }

    public String getNextFileItemPath(int type) throws IOException {
        String line = mBufferedReader.readLine();
        if (line != null) {
            int ei = line.indexOf('.');
            mCurrentFileName = line.substring(0, ei);
            return mInFileRoot + mCurrentFileName + getFileType(type);
        } else {
            return null;
        }
    }

    public String getCurrentOutFileItemPath(int type) {
        if (mCurrentFileName != null)
            return mOutFileRoot + mCurrentFileName + getFileType(type);
            //return mOutFileRoot + mCurrentFileName;
        else
            return null;
    }

    public String getCurrentFileName() {
        return mCurrentFileName;
    }

}
