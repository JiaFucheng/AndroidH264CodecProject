package com.example.androidh264codecproject.encoder;

public class BitrateTool {

    public static int getAdaptiveBitrate(int w, int h) {
        if (w < 640 && h < 360)
            return 576 * 1000;
        else if (w < 848 && h < 480)
            return 896 * 1000;
        else if (w < 1280 && h < 720)
            return 1216 * 1000;
        else if (w < 1920 && h < 1080)
            return 2496 * 1000;
        else
            return 4992 * 1000;
    }

}
