
## Prepare YUV Video File

1. Put your YUV video file in SD card directory.
2. Set YUV video width, height, frame rate and directory in ***com.example.androidh264codecproject.AVCEncoderTestThread***.

## Get Motion Vectors

See ***com.example.androidh264codecproject.decoder.FFmpegAVCDecoderCallback*** for more details. You can get motion vector list by calling ***decoder.getMotionVectorList*** and get motion vector values from list item.

Here is the key code of getting motion vectors.

```java
MotionVectorList mvList = decoder.getMotionVectorList();
if (mvList != null) {
    Log.d(TAG, String.format(Locale.CHINA, "MV List Count: %d", mvList.getCount()));
    for (int i = 0; i < mvList.getCount(); i++) {
        MotionVectorListItem item = mvList.getItem(i);
        int mvX = item.getMvX();      // Motion Vector = Current Block Position - Last Block Position
        int mvY = item.getMvY();
        int posX = item.getPosX();    // Current Block Position
        int posY = item.getPosY();
        int sizeX = item.getSizeX();  // Block Size. Most are 16x16 but some of blocks are 8x8
        int sizeY = item.getSizeY();
    }
}
```

## Setting of MediaCodec
|Setting|Value|
|:-:|:-:|
|MIME|video/avc|
|Width|340|
|Height|256|
|Color Format|YUV420Flexible|
|Bit Rate|576 kbps [[1]](#ref)|
|Frame Rate|25|
|I Frame Interval|0.48(s), i.e. GOP Size is 12|

## <span id="ref">Reference</span>

[1] [Video Encoding Settings for H.264 Excellence](http://www.lighterra.com/papers/videoencodingh264/). Aristotle.
