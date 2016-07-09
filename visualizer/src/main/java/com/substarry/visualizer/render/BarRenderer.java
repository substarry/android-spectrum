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

import com.substarry.visualizer.AudioData;
import com.substarry.visualizer.FFTData;

public class BarRenderer extends Renderer {
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
    public BarRenderer(int columnCount,
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
        if(mFFTPoints != null){
            for(int i = 0; i < mFFTPoints.length; i++){
                mFFTPoints[i] = 0;
            }
        }

        this.mColumnCount = columnCount;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setMutiple(float mutiple) {
        this.mMutiple = mutiple;
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
        mPaint.setStrokeWidth(columnWidth * 0.8f);

        for (int i = 0; i < mColumnCount; i++) {
            mFFTPoints[i * 4] = i * columnWidth + columnWidth / 2;
            mFFTPoints[i * 4 + 2] = i * columnWidth + columnWidth / 2;
            byte rfk = data.bytes[divisions * i];
            byte ifk = data.bytes[divisions * i + 1];
            byte dbValue = (byte) Math.hypot(rfk, ifk);

            if (dbValue < 0) {
                dbValue = 127;
            }

            float columnHeight = (dbValue) * (maxHeight / 128);
            if(columnHeight == 0){
                columnHeight = columnWidth * 0.1f;
            }

            if (mPosition == POSITION_TOP) {
                mFFTPoints[i * 4 + 1] = 0;
                mFFTPoints[i * 4 + 3] = columnHeight;
            } else if (mPosition == POSITION_BOTTOM) {
                mFFTPoints[i * 4 + 1] = rect.height();
                mFFTPoints[i * 4 + 3] = rect.height() - columnHeight;
            } else if (mPosition == POSITION_MIDDLE) {
                mFFTPoints[i * 4 + 1] = rect.height() / 2 + columnHeight / 2;
                mFFTPoints[i * 4 + 3] = rect.height() / 2 - columnHeight / 2;
            } else {
                return;
            }
        }

        canvas.drawLines(mFFTPoints, mPaint);
    }

}
