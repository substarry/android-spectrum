/**
 * Copyright 2011, Felix Palmer
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.substarry.spectrum;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.substarry.visualizer.VisualizerView;
import com.substarry.visualizer.render.BarRenderer;
import com.substarry.visualizer.render.PixelRenderer;
import com.substarry.visualizer.utils.TunnelPlayerWorkaround;

import java.io.IOException;

/**
 * Demo to show how to use VisualizerView
 */
public class MainActivity extends Activity {
  private MediaPlayer mPlayer;
  private MediaPlayer mSilentPlayer;  /* to avoid tunnel player issue */
  private VisualizerView mVisualizerView;
  private TextView mFadeAlphaTv,mColumnCountTv,mMutipleTv;
  private SeekBar mFadeAlphaSeekBar,mColumnCountSeekBar,mMutipleSeekBar;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    initView();
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    initTunnelPlayerWorkaround();
    init();
  }

  @Override
  protected void onPause()
  {
    cleanUp();
    super.onPause();
  }

  @Override
  protected void onDestroy()
  {
    cleanUp();
    super.onDestroy();
  }

  private void initView(){
    mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);

    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(Color.argb(200, 181, 111, 233));
    mPixelRenderer = new PixelRenderer(mColumnCount, paint, mPosition, mMutiple);
    mBarRenderer = new BarRenderer(mColumnCount, paint, mPosition, mMutiple);

    switchShape(false);

    mFadeAlphaTv = (TextView) findViewById(R.id.fadeAlphaTv);
    mColumnCountTv = (TextView) findViewById(R.id.columnCountTv);
    mMutipleTv = (TextView) findViewById(R.id.mutipleTv);
    mFadeAlphaSeekBar = (SeekBar) findViewById(R.id.fadeAlphaSeekBar);
    mColumnCountSeekBar = (SeekBar) findViewById(R.id.columnCountSeekBar);
    mMutipleSeekBar = (SeekBar) findViewById(R.id.mutipleSeekBar);

    mFadeAlphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        changeFadeAlpha(i/100f);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

    mColumnCountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        changeColumnCount(i);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

    mMutipleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        changeMutiple(i/20f);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

    mFadeAlphaSeekBar.setProgress((int) (mFadeAlpha * 100));
    mColumnCountSeekBar.setProgress(mColumnCount);
    mMutipleSeekBar.setProgress((int) mMutiple * 20);
  }

  private void init()
  {
    mPlayer = MediaPlayer.create(this, R.raw.music2);
    mPlayer.setLooping(true);
    mPlayer.start();

    // We need to link the visualizer view to the media player so that
    // it displays something
    mVisualizerView.link(mPlayer);
  }

  private void cleanUp()
  {
    if (mPlayer != null)
    {
      mVisualizerView.release();
      mPlayer.release();
      mPlayer = null;
    }
    
    if (mSilentPlayer != null)
    {
      mSilentPlayer.release();
      mSilentPlayer = null;
    }
  }
  
  // Workaround (for Galaxy S4)
  //
  // "Visualization does not work on the new Galaxy devices"
  //    https://github.com/felixpalmer/android-visualizer/issues/5
  //
  // NOTE: 
  //   This code is not required for visualizing default "test.mp3" file,
  //   because tunnel player is used when duration is longer than 1 minute.
  //   (default "test.mp3" file: 8 seconds)
  //
  private void initTunnelPlayerWorkaround() {
    // Read "tunnel.decode" system property to determine
    // the workaround is needed
    if (TunnelPlayerWorkaround.isTunnelDecodeEnabled(this)) {
      mSilentPlayer = TunnelPlayerWorkaround.createSilentMediaPlayer(this);
    }
  }



  private PixelRenderer mPixelRenderer;
  private BarRenderer mBarRenderer;
  private int mPosition = 2;
  private int mColumnCount = 128;
  private float mMutiple = 2.0f;
  private float mFadeAlpha = 0.5f;
  // Methods for adding renderers to visualizer
  private void setTop()
  {
    mPosition = 0;
    mBarRenderer.setPosition(mPosition);
    mPixelRenderer.setPosition(mPosition);
  }

  private void setBottom()
  {
    mPosition = 1;
    mBarRenderer.setPosition(mPosition);
    mPixelRenderer.setPosition(mPosition);
  }

  private void setMiddle()
  {
    mPosition = 2;
    mBarRenderer.setPosition(mPosition);
    mPixelRenderer.setPosition(mPosition);
  }

  private void switchShape(boolean pixel){
    mVisualizerView.clearRenderers();
    if(pixel){
      mVisualizerView.addRenderer(mPixelRenderer);
    }
    else {
      mVisualizerView.addRenderer(mBarRenderer);
    }
  }

  private void changeMutiple(float mutiple){
    mMutiple = mutiple;
    mBarRenderer.setMutiple(mMutiple);
    mPixelRenderer.setMutiple(mMutiple);
    mMutipleTv.setText("Mutiple: " + mutiple);
  }

  private void changeColumnCount(int count){
    mColumnCount = count;
    mBarRenderer.setColumnCount(mColumnCount);
    mPixelRenderer.setColumnCount(mColumnCount);
    mColumnCountTv.setText("Column Count: " + count);
  }

  private void changeFadeAlpha(float alpha)
  {
    mFadeAlpha = alpha;
    mVisualizerView.setFadeAlpha(mFadeAlpha);
    mFadeAlphaTv.setText("Fade Alpha: " + alpha);
  }

  // Actions for buttons defined in xml
  public void startPressed(View view) throws IllegalStateException, IOException
  {
    if(mPlayer.isPlaying())
    {
      return;
    }
    mPlayer.prepare();
    mPlayer.start();
  }

  public void stopPressed(View view)
  {
    mPlayer.stop();
  }

  public void shapePressed(View view)
  {
    if(view.isSelected()){
      view.setSelected(false);
      switchShape(false);
    }
    else{
      view.setSelected(true);
      switchShape(true);
    }
  }

  public void topPressed(View view)
  {
   setTop();
  }

  public void middlePressed(View view)
  {
    setMiddle();
  }

  public void bottomPressed(View view)
  {
    setBottom();
  }
}