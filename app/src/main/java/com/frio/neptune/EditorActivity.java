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
import androidx.appcompat.app.AppCompatActivity;
import com.frio.neptune.databinding.ActEditorBinding;
import com.frio.neptune.project.Project;
import com.frio.neptune.utils.*;
import com.frio.neptune.utils.app.*;
import java.util.Arrays;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditorActivity extends AppCompatActivity {

  private ActEditorBinding binding;
  private GLRenderer renderer;
  private Project mProject;

  /**
   * Temporarily removed (Doesn't work on some devices).
   *
   * <p>private ScaleGestureDetector mScaleDetector;
   */
  private float mLastX;

  private float mLastY;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    this.binding = ActEditorBinding.inflate(getLayoutInflater());
    this.setContentView(binding.getRoot());

    this.main();
  }

  protected void main() {
    mProject = getIntent().getParcelableExtra("project");

    binding.glSurface.setEGLContextClientVersion(3);
    renderer = new GLRenderer(this);

    binding.glSurface.setRenderer(renderer);
    binding.glSurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    renderer.loadObjects(mProject.getPath() + "/scene.world");
    binding.glSurface.requestRender();

    binding.glSurface.setOnTouchListener(
        (view, event) -> {
          float dx = 0;
          float dy = 0;

          switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
              {
                final float x = event.getX();
                final float y = event.getY();

                mLastX = x;
                mLastY = y;
                break;
              }
            case MotionEvent.ACTION_MOVE:
              {
                final float x = event.getX();
                final float y = event.getY();

                dx = x - mLastX;
                dy = y - mLastY;

                Vector3 vector3 = renderer.getCamera().getTransform().getPosition();
                vector3.set(vector3.getX() + dx / x, vector3.getY() - dy / y, 0);

                binding.glSurface.requestRender();

                mLastX = x;
                mLastY = y;
                break;
              }
          }

          return true;
        });
  }

  // @Override methods

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
          {
            String uid = UUID.randomUUID().toString().replace("-", "");
            String type = "Square";
            float[] color = new float[] {1f, 1f, 1f, 1f};

            renderer.addNewObject(uid, type, color);
            binding.glSurface.requestRender();
          }

          return true;
        }
      case R.id.save:
        {
          JSONObject main = new JSONObject();
          JSONArray array = new JSONArray();

          JSONObject objects = new JSONObject();
          JSONObject object = new JSONObject();

          try {
            for (Object2D obj : renderer.getObjectsList()) {
              object.put("type", obj.getType());
              object.put("color", Arrays.toString(obj.getColor()));

              objects.put(obj.getUid(), object);
            }

            array.put(objects);

            main.accumulate("objects", array);
            FilesUtil.writeFile(this, mProject.getPath() + "/scene.world", main.toString(2));
          } catch (JSONException e) {
            AndroidUtil.throwsException(this, e.getMessage());
          } finally {
            AndroidUtil.showToast(this, "Mundo salvo com sucesso!");
          }
          return true;
        }
      case R.id.resetCamera:
        {
          renderer.getCamera().getTransform().setPosition(0, 0, 0);
          binding.glSurface.requestRender();
          return true;
        }
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}

