/*
 * MIT License
 * Copyright (c) 2022 FrioGitHub

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

package com.frio.neptune;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import androidx.appcompat.app.AppCompatActivity;
import com.frio.neptune.project.Project;
import com.frio.neptune.utils.*;
import com.frio.neptune.utils.app.*;
import java.util.Random;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

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
        {
          float red = 0 + new Random().nextFloat() * (1 - 0);
          float green = 0 + new Random().nextFloat() * (1 - 0);
          float blue = 0 + new Random().nextFloat() * (1 - 0);

          String uid = UUID.randomUUID().toString().replace("-", "");
          String type = "Square";

          mRenderer.addNewObject(uid, type, new float[] {red, green, blue, 1.0f});
          mGLSurface.requestRender();

          return true;
        }
      case R.id.save:
        float red = 0 + new Random().nextFloat() * (1 - 0);
        float green = 0 + new Random().nextFloat() * (1 - 0);
        float blue = 0 + new Random().nextFloat() * (1 - 0);

        String uid = UUID.randomUUID().toString().replace("-", "");
        String type = "Square";
        String color = String.valueOf(red + "," + green + "," + blue + ",1.0f");

        try {
          JSONObject json = new JSONObject(FilesUtil.readFile(mProject.getPath() + "/scene.world"));

          JSONObject objects = new JSONObject();
          objects.put("uid", uid);
          objects.put("type", type);
          objects.put("color", color);

          json.accumulate("objects", objects);

          FilesUtil.writeFile(this, mProject.getPath() + "/scene.world", json.toString(2));
        } catch (JSONException e) {
          AndroidUtil.throwsException(this, e.getMessage());
        } finally {
          AndroidUtil.showToast(this, "Mundo salvo com sucesso!");
        }

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
