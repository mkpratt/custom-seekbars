package com.mkpratt.simpleseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SimpleSeekbar extends View
{

    private PointF circleCenter;
    private float circleRadius = 50f;
    private float lineY = 50f;

    private PointF startingPoint;
    private PointF endingPoint;

    private boolean thumbMoving = false;
    private boolean thumbHit = false;

    public int thumbValue;

    public TextView seekbarValue;

    Paint boringPaint;
    Paint clickPaint;

    public SimpleSeekbar(Context context, TextView seekbarValue) {
        super(context);
        this.circleCenter = new PointF(circleRadius, circleRadius);

        this.startingPoint = new PointF(this.getLeft() + circleRadius, this.getTop() + circleRadius);
        this.endingPoint = new PointF(this.getRight() - circleRadius, this.getTop() + circleRadius);

        this.thumbHit = false;
        this.thumbMoving = false;

        this.thumbValue = 0;

        this.boringPaint = new Paint();
        this.boringPaint.setColor(0xff101010);
        this.boringPaint.setAntiAlias(true);
        this.boringPaint.setTextSize(90f);

        this.clickPaint = new Paint();
        this.clickPaint.setColor(Color.RED);
        this.clickPaint.setAntiAlias(true);
        this.clickPaint.setTextSize(90f);

        this.seekbarValue = seekbarValue;

        invalidate();
    }


    @Override
    protected synchronized void onDraw (Canvas canvas)
    {
        startingPoint = new PointF(this.getLeft() + circleRadius, this.getTop() + lineY);
        endingPoint = new PointF(this.getRight() - circleRadius, this.getTop() + lineY);

        Log.d ("seek","on draw");
        super.onDraw(canvas);

        drawLineFromPoints (startingPoint, endingPoint, canvas, boringPaint);
        if (thumbHit) {
            canvas.drawCircle(circleCenter.x, circleRadius,circleRadius, clickPaint);
        } else {
            canvas.drawCircle(circleCenter.x, circleRadius, circleRadius, boringPaint);
        }

    }

    private void drawLineFromPoints(PointF start, PointF end, Canvas canvas, Paint myPaint) {
        canvas.drawLine(start.x, start.y, end.x, end.y, myPaint);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        PointF fingerLocation = new PointF(event.getX(), event.getY());
        double distance = Math.sqrt(Math.pow(fingerLocation.x - circleCenter.x, 2) + Math.pow(fingerLocation.y - circleCenter.y, 2));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (distance < circleRadius + 15f) {
                    thumbHit = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (thumbHit) {
                    thumbMoving = true;

                    circleCenter.x = event.getX();
                    circleCenter.y = lineY;
                    if (circleCenter.x > endingPoint.x) {
                        circleCenter.x = endingPoint.x;
                    }
                    if (circleCenter.x < startingPoint.x) {
                        circleCenter.x = startingPoint.x;
                    }

                    float barLen = endingPoint.x - startingPoint.x;
                    thumbValue = Math.round(circleCenter.x/barLen * 100) - 6;

                    seekbarValue.setText(Integer.toString(thumbValue));
                }
                break;
            case MotionEvent.ACTION_UP:
                thumbHit = false;
                thumbMoving = false;
                break;
        }


        invalidate(); // make sure we force a redraw.
        return true;
    }
}
