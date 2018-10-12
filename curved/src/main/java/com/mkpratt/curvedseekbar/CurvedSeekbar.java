package com.mkpratt.curvedseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkpratt.generalseekbar.Thumb;

import java.util.ArrayList;

public class CurvedSeekbar extends View {

    ArrayList<Thumb> thumbs = new ArrayList<>();
    final TextView[] seekbarValues;

    private float padding = 50f;

    private PointF startingPoint;
    private PointF endingPoint;

    private int startingValue;
    private int endingValue;

    private boolean firstDraw = true;

    Paint linePaint;
    Paint fillPaint;
    Paint clickPaint;

    float ovalTop;
    float ovalRight;
    float ovalBottom;
    float ovalLeft;

    float radiusX;
    float radiusY;

    PointF origin;

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
    public CurvedSeekbar(Context context, LinearLayout layout, int sliderWidth, int numThumbs, float thumbWidth, float thumbPadding, int rangeStart, int rangeEnd) {
        super(context);

        //this.primaryArea = (this.getRight() - padding) - (this.getLeft() + padding);

        this.startingPoint = new PointF(this.getLeft() + 50f, this.getBottom() - 50f);
        this.endingPoint = new PointF(this.getRight() - 50f, this.getTop() + 50f);

        seekbarValues = new TextView[numThumbs];

        for (int i = 0; i < numThumbs; i++) {
            PointF thumbCenter = new PointF();
            thumbCenter.set(this.startingPoint);

            float thumbPosition = 0; // position percentage along the line from 0 - 100
            int thumbValue = rangeStart; // default, will be updated in onDraw

            Thumb thumb = new Thumb(thumbWidth, thumbCenter, thumbPadding, thumbValue, thumbPosition);
            thumbs.add(thumb);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);

            final TextView seekbarValue = new TextView(context);
            seekbarValue.setText("Thumb " + (i + 1) + ": " + rangeStart);
            seekbarValue.setLayoutParams(params);
            seekbarValue.setTextSize(30);
            layout.addView(seekbarValue);
            seekbarValues[i] = seekbarValue;
        }

        //this.sliderWidth = sliderWidth;

        this.startingValue = rangeStart;
        this.endingValue = rangeEnd;

        this.linePaint = new Paint();
        this.linePaint.setColor(Color.BLACK);
        this.linePaint.setAntiAlias(true);
        this.linePaint.setTextSize(90f);
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setStrokeWidth(3f);

        this.clickPaint = new Paint();
        this.clickPaint.setColor(Color.RED);
        this.clickPaint.setAntiAlias(true);
        this.clickPaint.setTextSize(90f);

        this.fillPaint = new Paint();
        this.fillPaint.setColor(Color.BLACK);
        this.fillPaint.setAntiAlias(true);
        this.fillPaint.setTextSize(90f);

        invalidate();
    }


    @Override
    protected synchronized void onDraw (Canvas canvas)
    {
        Log.d ("seek","on draw");
        super.onDraw(canvas);

        this.startingPoint = new PointF(this.getLeft() + 50f, this.getBottom() - 50f);
        this.endingPoint = new PointF(this.getRight() - 50f, this.getTop() + 50f);

        this.origin = new PointF(this.getRight(), this.getBottom());

        int idx = 0;
        for (Thumb t : thumbs) {
            // Draw the initial values
            if (this.firstDraw) {
                setOvalBounds();
                t.setThumbCenter(new PointF(this.ovalLeft, this.ovalBottom));

                seekbarValues[idx].setText("Thumb " + (idx + 1) + ": " + t.getThumbValue());
            }
            // draw thumbs
            canvas.drawCircle(t.getThumbCenter().x, t.getThumbCenter().y, t.getThumbWidth(), t.isThumbHit() || t.isThumbMoving() ? clickPaint : fillPaint);

            idx++;
        }
        this.firstDraw = false;
        drawSliderCurve(canvas);
    }

    private void drawLineFromPoints(PointF start, PointF end, Canvas canvas, Paint myPaint) {
        canvas.drawLine(start.x, start.y, end.x, end.y, myPaint);
    }

    private void drawSliderCurve(Canvas canvas) {
        RectF oval = new RectF();
        oval.top = this.ovalTop;
        oval.bottom = this.ovalBottom + (this.ovalBottom - this.ovalTop);
        oval.left = this.ovalLeft;
        oval.right = this.ovalRight + (this.ovalRight - this.ovalLeft);

        this.radiusX = this.ovalRight;
        this.radiusY = this.ovalBottom;

        canvas.drawArc(oval, 180f, 90f, false, linePaint);
    }

    private void setOvalBounds() {
        this.ovalTop = this.getTop() + 50f;
        this.ovalRight = this.getRight() - 50f;
        this.ovalBottom = this.getBottom() - 50f;
        this.ovalLeft = this.getLeft() + 50f;
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

                        // height
                        double height = origin.y - fingerLocation.y;
                        double width = origin.x - fingerLocation.x;

                        // angle between origin and finger location
                        double theta = Math.atan(Math.abs(height)/Math.abs(width));

                        // vector and magnitude
                        PointF vector = new PointF(fingerLocation.x - origin.x, fingerLocation.y - origin.y);
                        double mag = getVectorMagnitude(vector);

                        // normalize the vector
                        vector.x /= mag; vector.y /= mag;

                        // new radius length
                        double newRadius = (radiusX * radiusY) /
                                (Math.sqrt((Math.pow(radiusX, 2) * Math.pow(Math.sin(theta), 2)) +
                                        (Math.pow(radiusY, 2) * Math.pow(Math.cos(theta), 2))));

                        PointF newThumbCenter = new PointF();
                        newThumbCenter.x = (float) (origin.x - 3 + (vector.x * newRadius));
                        newThumbCenter.y = (float) (origin.y - 3 + (vector.y * newRadius));

                        // set thumb value
                        int newThumbValue = (int) Math.round(startingValue + (endingValue - startingValue) * theta * 180f / (Math.PI * 90f));

                        // Keep within widget bounds on slider
                        if (newThumbCenter.x > this.endingPoint.x) {
                            newThumbCenter.x = this.endingPoint.x;
                            newThumbCenter.y = this.endingPoint.y;
                            newThumbValue = this.endingValue;
                        }
                        if (newThumbCenter.x < this.startingPoint.x) {
                            newThumbCenter.x = this.startingPoint.x;
                        }
                        if (newThumbCenter.y > this.startingPoint.y) {
                            newThumbCenter.y = this.startingPoint.y;
                            newThumbCenter.x = this.startingPoint.x;
                            newThumbValue = this.startingValue;
                        }
                        if (newThumbCenter.y < this.endingPoint.y) {
                            newThumbCenter.y = this.startingPoint.y;
                        }

                        t.setThumbValue(newThumbValue);
                        t.setThumbCenter(newThumbCenter);

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

    // Return the magnitude of a given vector
    private double getVectorMagnitude(PointF vector) {
        return Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2));
    }

    // Distance between finger location and the given seekbar thumb
    private double getDistance(PointF fingerLocation, Thumb thumb) {
        return Math.sqrt(Math.pow(fingerLocation.x - thumb.getThumbCenter().x, 2) + Math.pow(fingerLocation.y - thumb.getThumbCenter().y, 2));
    }
}