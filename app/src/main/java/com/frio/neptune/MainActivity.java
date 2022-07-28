package com.frio.neptune;

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
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

  private GLSurfaceView mGLSurface;
  private GLRenderer mRenderer;

  private float previousX;
  private float previousY;

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

    this.mGLSurface.setOnTouchListener(
        (view, event) -> {
          // MotionEvent reports input details from the touch screen
          // and other input controls. In this case, you are only
          // interested in events where the touch position changed.

          float x = event.getX();
          float y = event.getY();

          switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
              float dx = x - previousX;
              float dy = y - previousY;

              this.mRenderer.setPositionX(this.mRenderer.getPositionX() + (dx / x));
              this.mRenderer.setPositionY(this.mRenderer.getPositionY() + (dy / y));

              this.mGLSurface.requestRender();
          }

          previousX = x;
          previousY = y;

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

        this.mRenderer.addObject(
            UUID.randomUUID().toString(), "Square", new float[] {rs, gs, bs, 1.0f});
        this.mGLSurface.requestRender();
        return true;
      case R.id.triangle:
        float rt = 0 + new Random().nextFloat() * (1 - 0);
        float gt = 0 + new Random().nextFloat() * (1 - 0);
        float bt = 0 + new Random().nextFloat() * (1 - 0);

        this.mRenderer.addObject(
            UUID.randomUUID().toString(), "Triangle", new float[] {rt, gt, bt, 1.0f});
        this.mGLSurface.requestRender();
        return true;
      case R.id.circle:
        float rc = 0 + new Random().nextFloat() * (1 - 0);
        float gc = 0 + new Random().nextFloat() * (1 - 0);
        float bc = 0 + new Random().nextFloat() * (1 - 0);

        this.mRenderer.addObject(
            UUID.randomUUID().toString(), "Circle", new float[] {rc, gc, bc, 1.0f});
        this.mGLSurface.requestRender();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
