package com.frio.neptune;

import com.frio.neptune.utils.*;

import android.os.Bundle;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.UUID;

import javax.microedition.khronos.egl.EGL;
import android.opengl.GLSurfaceView;

public class MainActivity extends AppCompatActivity {

  private GLSurfaceView mGLSurface;
  private GLRenderer mRenderer;

  private ScaleGestureDetector mScaleDetector;

  private float mScaleFactor = 1.0f;
  private float mPreviousX;
  private float mPreviousY;

  private String mode = "NONE";

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    this.setContentView(R.layout.act_main);

    this.main();
  }

  protected void main() {
    this.initializeViews();

    this.mGLSurface.setEGLContextClientVersion(3);
    this.mRenderer = new GLRenderer();

    this.mGLSurface.setRenderer(this.mRenderer);
    this.mGLSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    this.mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());

    this.mGLSurface.setOnTouchListener(
        (view, event) -> {
          float x = event.getX();
          float y = event.getY();

          switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
              if (mode != "ZOOM") {
                if ((x >= 0)
                    & (x <= mGLSurface.getWidth())
                    & (y >= 0)
                    & (y <= mGLSurface.getHeight())) {
                  float dx = x - mPreviousX;
                  float dy = y - mPreviousY;

                  Vector3 vector3 = mRenderer.getCamera().getTransform().getPosition();
                  vector3.set(vector3.getX() + (dx / x), vector3.getY() + (-dy / y), 0);

                  mGLSurface.requestRender();
                  mode = "MOVE";
                }
              }
              break;

            case MotionEvent.ACTION_UP:
              mode = "NONE";
              break;
          }

          mPreviousX = x;
          mPreviousY = y;

          mScaleDetector.onTouchEvent(event);
          return true;
        });
  }

  protected void initializeViews() {
    this.mGLSurface = this.findViewById(R.id.mGLSurface);
  }

  // Override methods

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = this.getMenuInflater();
    inflater.inflate(R.menu.menu, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.square:
        float rs = 0 + new Random().nextFloat() * (1 - 0);
        float gs = 0 + new Random().nextFloat() * (1 - 0);
        float bs = 0 + new Random().nextFloat() * (1 - 0);

        this.mRenderer.addNewObject(
            UUID.randomUUID().toString(), "Square", new float[] {rs, gs, bs, 1.0f});
        this.mGLSurface.requestRender();
        return true;
        /*case R.id.triangle:
          float rt = 0 + new Random().nextFloat() * (1 - 0);
          float gt = 0 + new Random().nextFloat() * (1 - 0);
          float bt = 0 + new Random().nextFloat() * (1 - 0);

          this.mRenderer.addNewObject(
              UUID.randomUUID().toString(), "Triangle", new float[] {rt, gt, bt, 1.0f});
          this.mGLSurface.requestRender();
          return true;
        case R.id.circle:
          float rc = 0 + new Random().nextFloat() * (1 - 0);
          float gc = 0 + new Random().nextFloat() * (1 - 0);
          float bc = 0 + new Random().nextFloat() * (1 - 0);

          this.mRenderer.addNewObject(
              UUID.randomUUID().toString(), "Circle", new float[] {rc, gc, bc, 1.0f});
          this.mGLSurface.requestRender();
          return true;*/
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  // Classes

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      mScaleFactor *= detector.getScaleFactor();
      mRenderer.getCamera().setZoom(mScaleFactor);

      mGLSurface.requestRender();

      getSupportActionBar().setTitle(mRenderer.getCamera().getZoom() + "");
      return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
      mode = "ZOOM";

      return true;
    }
  }
}
