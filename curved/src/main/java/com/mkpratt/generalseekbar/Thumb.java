package com.mkpratt.generalseekbar;

import android.graphics.PointF;

public class Thumb {

    float thumbWidth;
    PointF thumbCenter;
    float thumbPadding;
    int thumbValue;
    float thumbPosition;

    boolean thumbHit;
    boolean thumbMoving;

    /**
     * Thumb for the GeneralSeekbar
     * @param thumbWidth Default is 50f (not bulletproof)
     * @param thumbCenter Center point for the thumb along slider
     * @param thumbPadding Extra margin of padding to grab the thumb easier
     * @param thumbValue Integer value representing the value of the thumb along the slider
     * @param thumbPosition Position of the thumb along the slider as a Float Percentage
     */
    public Thumb(float thumbWidth, PointF thumbCenter, float thumbPadding, int thumbValue, float thumbPosition) {
        this.setThumbWidth(thumbWidth);
        this.setThumbCenter(thumbCenter);
        this.setThumbPadding(thumbPadding);
        this.setThumbPosition(thumbPosition);

        this.setThumbHit(false);
        this.setThumbMoving(false);

        this.setThumbValue(thumbValue);
    }

    public float getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(float circleWidth) {
        this.thumbWidth = circleWidth;
    }

    public PointF getThumbCenter() {
        return thumbCenter;
    }

    public void setThumbCenter(PointF thumbCenter) {
        this.thumbCenter = thumbCenter;
    }

    public float getThumbPadding() {
        return thumbPadding;
    }

    public void setThumbPadding(float thumbPadding) {
        this.thumbPadding = thumbPadding;
    }

    public boolean isThumbHit() {
        return thumbHit;
    }

    public void setThumbHit(boolean thumbHit) {
        this.thumbHit = thumbHit;
    }

    public boolean isThumbMoving() {
        return thumbMoving;
    }

    public void setThumbMoving(boolean thumbMoving) {
        this.thumbMoving = thumbMoving;
    }

    public int getThumbValue() {
        return thumbValue;
    }

    public void setThumbValue(int thumbValue) {
        this.thumbValue = thumbValue;
    }

    public float getThumbPosition() {
        return thumbPosition;
    }

    public void setThumbPosition(float thumbPosition) {
        this.thumbPosition = thumbPosition;
    }
}
