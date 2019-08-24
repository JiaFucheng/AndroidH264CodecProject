
##Prepare YUV Video File

1. Put your YUV video file in SD card directory.
2. Set YUV video width, height, frame rate and directory in com.example.androidh264codecproject.AVCEncoderTestThread.

##Get Motion Vectors

See ***com.example.androidh264codecproject.decoder.FFmpegAVCDecoderCallback*** for more details. You can get motion vector list by calling ***decoder.getMotionVectorList*** and get motion vector values of list item.
