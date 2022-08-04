package com.frio.neptune;

import android.widget.Toast;
import com.frio.neptune.project.Project;
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

public class EditorActivity extends AppCompatActivity {

  private GLSurfaceView mGLSurface;
  private GLRenderer mRenderer;

  private ScaleGestureDetector mScaleDetector;

  private float mScaleFactor = 1.0f;
  private float mPreviousX;
  private float mPreviousY;

  private String mode = "NONE";
  private Project mProject;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    this.setContentView(R.layout.act_editor);

    main();
  }

  protected void main() {
    initializeViews();

    mProject = getIntent().getParcelableExtra("project");

    mGLSurface.setEGLContextClientVersion(3);
    mRenderer = new GLRenderer(this);

    mGLSurface.setRenderer(mRenderer);
    mGLSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    mRenderer.loadObjects(mProject.getPath() + "/scene.world");
    mGLSurface.requestRender();

    mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());

    mGLSurface.setOnTouchListener(
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
    mGLSurface = findViewById(R.id.glSurfaceView);
  }

  // Override methods

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.square:
        float red = 0 + new Random().nextFloat() * (1 - 0);
        float green = 0 + new Random().nextFloat() * (1 - 0);
        float blue = 0 + new Random().nextFloat() * (1 - 0);

        String uid = UUID.randomUUID().toString().replace("-", "");
        String type = "Square";

        mRenderer.addNewObject(uid, type, new float[] {red, green, blue, 1.0f});
        mGLSurface.requestRender();
        return true;
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
      return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
      mode = "ZOOM";

      return true;
    }
  }
}
