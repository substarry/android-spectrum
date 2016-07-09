/**
 * Copyright 2011, Felix Palmer
 * <p/>
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.substarry.visualizer.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.substarry.visualizer.AudioData;
import com.substarry.visualizer.FFTData;

public class PixelRenderer extends Renderer {
    private int mColumnCount;
    private Paint mPaint;
    private int mPosition;
    private float mMutiple;

    private static final int POSITION_TOP = 0;
    private static final int POSITION_BOTTOM = 1;
    private static final int POSITION_MIDDLE = 2;

    /**
     * Renders the FFT data as a series of lines, in histogram form
     * @param columnCount - must be a power of 2. Controls how many lines to draw
     * @param paint - Paint to draw lines with
     * @param position - whether to draw the lines at the top of the canvas, or the bottom
     */
    public PixelRenderer(int columnCount,
                         Paint paint,
                         int position,
                         float mutiple) {
        super();
        mColumnCount = columnCount;
        mPaint = paint;
        mPosition = position;
        mMutiple = mutiple;
    }

    public void setColumnCount(int columnCount) {
        this.mColumnCount = columnCount;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setMutiple(float mutiple) {
        this.mMutiple = mutiple;
    }

    public void setPaint(Paint paint) {
        this.mPaint = paint;
    }

    @Override
    public void onRender(Canvas canvas, AudioData data, Rect rect) {
        // Do nothing, we only display FFT data
    }

    @Override
    public void onRender(Canvas canvas, FFTData data, Rect rect) {
        int divisions;
        if (mColumnCount <= data.bytes.length / 4) {
            //only show half frequency
            divisions = ((int) ((data.bytes.length / 4.0f) / mColumnCount)) * 2;
        } else if (mColumnCount <= data.bytes.length / 2) {
            divisions = 2;
        } else {
            mColumnCount = data.bytes.length / 2;
            divisions = 2;
        }

        float maxHeight = rect.height() * mMutiple;
        float columnWidth = rect.width() / (float) mColumnCount;
        float padding = columnWidth * 0.1f;
        mPaint.setStrokeWidth(columnWidth * 0.8f);

        for (int i = 0; i < mColumnCount; i++) {
            byte rfk = data.bytes[divisions * i];
            byte ifk = data.bytes[divisions * i + 1];
            byte dbValue = (byte) Math.hypot(rfk, ifk);

            if (dbValue < 0) {
                dbValue = 127;
            }

            float columnHeight = (dbValue) * (maxHeight / 128);
            float left = i * columnWidth + padding;
            float right = (i + 1) * columnWidth - padding;

            int drawCount = (int) (columnHeight / columnWidth);
            if (drawCount == 0) {
                drawCount = 1;
            }

            float pixelHeight = columnHeight / drawCount;

            // draw each pixel
            for (int j = 0; j < drawCount; j++) {

                float top, bottom;
                RectF pixRect;

                switch (mPosition) {
                    case POSITION_TOP:
                        top = pixelHeight * j + padding;
                        bottom = pixelHeight * (j + 1) - padding;
                        pixRect = new RectF(left, top, right, bottom);
                        break;

                    case POSITION_BOTTOM:
                        bottom = rect.height() - pixelHeight * j - padding;
                        top = rect.height() - pixelHeight * (j + 1) + padding;
                        pixRect = new RectF(left, top, right, bottom);
                        break;

                    case POSITION_MIDDLE:
                        bottom = rect.height() / 2 - (columnHeight / 2) + pixelHeight * j + padding;
                        top = rect.height() / 2 - (columnHeight / 2) + pixelHeight * (j + 1) - padding;
                        pixRect = new RectF(left, top, right, bottom);
                        break;

                    default:
                        return;
                }
                canvas.drawRect(pixRect, mPaint);
            }
        }
    }
}
