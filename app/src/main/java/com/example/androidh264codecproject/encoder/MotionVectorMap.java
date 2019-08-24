package com.example.androidh264codecproject.encoder;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class MotionVectorMap {

    public static final String TAG = "MotionVectorMap";

    public static final String DEFAULT_SAVE_FILEPATH =
            "/storage/emulated/0/coviar_opencl/results/mv_map_output.txt";

    private int width;
    private int height;
    private int[] data;

    public MotionVectorMap(int w, int h, int[] data) {
        this.width  = w;
        this.height = h;
        this.data   = data;
    }

    public int[] getData() {
        return this.data;
    }

    public static void deleteMotionVectorMapFileIfExist() {
        new File(DEFAULT_SAVE_FILEPATH).deleteOnExit();
    }

    public static void saveMotionVectorMap(MotionVectorMap map, String filename, int frameId) {
        try {
            File file = new File(filename);
            if (!file.exists())
                file.createNewFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            bw.write(String.format(Locale.CHINA, "===== %d =====\n", frameId));

            int h, w;

            for (h = 0; h < map.height; h ++) {
                for (w = 0; w < map.width; w ++) {
                    int mvid = (h * map.width + w) << 1;

                    int mvx = map.data[mvid];
                    int mvy = map.data[mvid+1];

                    bw.write(String.format(Locale.CHINA, "(%d,%d)\t", mvx, mvy));
                }
                bw.write('\n');
                //bw.flush();
                Log.d(TAG, String.format(Locale.CHINA, "Height=%d, Width=%d", h, w));
            }

            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
