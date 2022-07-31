package com.frio.neptune;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.frio.neptune.utils.Math;
import java.util.Random;
import java.util.UUID;
import javax.microedition.khronos.egl.EGL;

public class MainActivity extends AppCompatActivity {

  private GLSurfaceView mGLSurface;
  private GLRenderer mRenderer;

  private ScaleGestureDetector mScaleDetector;
  private float mScaleFactor = 1.0f;

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
          mScaleDetector.onTouchEvent(event);
          mGLSurface.requestRender();
          return true;
        });
  }

  protected void initializeViews() {
    this.mGLSurface = this.findViewById(R.id.mGLSurface);
  }

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      mScaleFactor *= detector.getScaleFactor();

      // Don't let the object get too small or too large.
      mScaleFactor = Math.clamp(1, mScaleFactor, 10);
      mRenderer.setCameraZoom(mScaleFactor);

      mGLSurface.requestRender();
      
      getSupportActionBar().setTitle(mRenderer.getCameraZoom() + "");
      return true;
    }
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
}
