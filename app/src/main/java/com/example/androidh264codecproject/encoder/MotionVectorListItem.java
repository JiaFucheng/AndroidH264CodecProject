package com.example.androidh264codecproject.encoder;

public class MotionVectorListItem {

    private int mvX, mvY;
    private int posX, posY;
    private int sizeX, sizeY;

    public MotionVectorListItem(
            int mvX, int mvY, int posX, int posY, int sizeX, int sizeY) {
        this.mvX = mvX;
        this.mvY = mvY;
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public int getMvX() { return this.mvX; }
    public int getMvY() { return this.mvY; }
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getSizeX() { return sizeX; }
    public int getSizeY() { return sizeY; }
}
