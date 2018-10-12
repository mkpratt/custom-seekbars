package com.mkpratt.generalseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GeneralSeekbar extends View {

    ArrayList<Thumb> thumbs = new ArrayList<>();
    final TextView[] seekbarValues;

    private float padding = 50f;
    private float newPadding;

    private int sliderWidth;

    private PointF startingPoint;
    private PointF endingPoint;
    private float primaryArea;
    private float useableArea;

    private int startingValue;
    private int endingValue;

    private boolean firstDraw = true;

    Paint linePaint;
    Paint clickPaint;

    /**
     * @param context Android Context
     * @param layout Parent layout for dynamically adding TextViews to report each thumb value
     * @param sliderWidth Integer between 0 and 100, which is a width percentage of the parent that the slider will fill
     * @param numThumbs Number of thumbs displayed on the slider
     * @param thumbWidth How wide each of the thumbs will be (not bulletproof)
     * @param thumbPadding Extra margin of padding to grab the thumb easier
     * @param rangeStart Starting value for the slider bar
     * @param rangeEnd Ending value for the slider bar
     */
    public GeneralSeekbar(Context context, LinearLayout layout, int sliderWidth, int numThumbs, float thumbWidth, float thumbPadding, int rangeStart, int rangeEnd) {
        super(context);

        this.primaryArea = (this.getRight() - padding) - (this.getLeft() + padding);

        seekbarValues = new TextView[numThumbs];

        for (int i = 0; i < numThumbs; i++) {
            PointF thumbPoint = new PointF();

            float thumbPosition = (i + 1) * (numThumbs/(float)(numThumbs + 1)/(float)numThumbs);
            int thumbValue = 0; // default, will be updated in onDraw

            Thumb thumb = new Thumb(thumbWidth, thumbPoint, thumbPadding, thumbValue, thumbPosition);
            thumbs.add(thumb);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);

            final TextView seekbarValue = new TextView(context);
            seekbarValue.setText("Thumb " + (i + 1) + ": 0");
            seekbarValue.setLayoutParams(params);
            seekbarValue.setTextSize(30);
            layout.addView(seekbarValue);
            seekbarValues[i] = seekbarValue;
        }

        this.sliderWidth = sliderWidth;

        this.startingPoint = new PointF(this.getLeft() + newPadding, this.getTop() + padding);
        this.endingPoint = new PointF(this.getRight() - newPadding, this.getTop() + padding);

        this.startingValue = rangeStart;
        this.endingValue = rangeEnd;

        this.linePaint = new Paint();
        this.linePaint.setColor(Color.BLACK);
        this.linePaint.setAntiAlias(true);
        this.linePaint.setTextSize(90f);

        this.clickPaint = new Paint();
        this.clickPaint.setColor(Color.RED);
        this.clickPaint.setAntiAlias(true);
        this.clickPaint.setTextSize(90f);

        invalidate();
    }


    @Override
    protected synchronized void onDraw (Canvas canvas)
    {
        Log.d ("seek","on draw");
        super.onDraw(canvas);

        this.primaryArea = (this.getRight() - padding) - (this.getLeft() + padding);

        this.useableArea = this.primaryArea * (sliderWidth/100.0f);
        this.newPadding = (this.primaryArea - this.useableArea)/2;

        this.startingPoint = new PointF(this.getLeft() + newPadding, this.getTop() + padding);
        this.endingPoint = new PointF(this.getRight() - newPadding, this.getTop() + padding);

        drawLineFromPoints (this.startingPoint, this.endingPoint, canvas, linePaint);

        int idx = 0;
        for (Thumb t : thumbs) {
            // Draw the initial values
            if (this.firstDraw) {
                float thumbX = startingPoint.x + (this.endingPoint.x - this.startingPoint.x) * t.getThumbPosition();
                t.setThumbCenter(new PointF(thumbX, padding));

                // This is a crazy line of calculations,
                // but this calculates thumb values based on a dynamic seekbar width as well as a dynamic range of values
                t.setThumbValue(Math.round((this.endingValue - this.startingValue) * (1 - ((this.endingPoint.x - thumbX)/(this.endingPoint.x - this.startingPoint.x)))) + this.startingValue);

                // Update a TextView with the thumb's value
                seekbarValues[idx].setText("Thumb " + (idx + 1) + ": " + t.getThumbValue());
            }
            if (t.isThumbHit() || t.isThumbMoving()) {
                canvas.drawCircle(t.getThumbCenter().x, t.getThumbWidth(), t.getThumbWidth(), clickPaint);
            } else {
                canvas.drawCircle(t.getThumbCenter().x, t.getThumbWidth(), t.getThumbWidth(), linePaint);
            }
            idx++;
        }

        this.firstDraw = false;
    }

    private void drawLineFromPoints(PointF start, PointF end, Canvas canvas, Paint myPaint) {
        canvas.drawLine(start.x, start.y, end.x, end.y, myPaint);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        PointF fingerLocation = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (Thumb t : thumbs) {
                    double dist = getDistance(fingerLocation, t);
                    if (dist < (t.getThumbWidth() + t.getThumbPadding())) {
                        t.setThumbHit(true);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int idx = 0;
                for (Thumb t : thumbs) {
                    if (t.isThumbHit()) {
                        t.setThumbMoving(true);

                        PointF newPoint = new PointF(event.getX(), padding);
                        if (newPoint.x > endingPoint.x) {
                            newPoint.x = endingPoint.x;
                        }
                        if (newPoint.x < startingPoint.x) {
                            newPoint.x = startingPoint.x;
                        }

                        // Same crazy calculation as in onDraw, except this get's the new thumb point x value
                        t.setThumbValue(Math.round((this.endingValue - this.startingValue) * (1 - ((this.endingPoint.x - newPoint.x)/(this.endingPoint.x - this.startingPoint.x)))) + this.startingValue);
                        t.setThumbCenter(newPoint);

                        seekbarValues[idx].setText("Thumb " + (idx + 1) + ": " + t.getThumbValue());
                    }
                    idx++;
                }
                break;
            case MotionEvent.ACTION_UP:
                for (Thumb t : thumbs) {
                    t.setThumbHit(false);
                    t.setThumbMoving(false);
                }
                break;
        }

        invalidate(); // make sure we force a redraw.
        return true;
    }

    // Distance between finger location and the given seekbar thumb
    private double getDistance(PointF fingerLocation, Thumb thumb) {
        return Math.sqrt(Math.pow(fingerLocation.x - thumb.getThumbCenter().x, 2) + Math.pow(fingerLocation.y - thumb.getThumbCenter().y, 2));
    }
}