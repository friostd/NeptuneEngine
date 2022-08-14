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
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.frio.neptune.databinding.ActEditorBinding;
import com.frio.neptune.project.Project;
import com.frio.neptune.project.adapter.ObjectsAdapter;
import com.frio.neptune.utils.*;
import com.frio.neptune.utils.app.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditorActivity extends AppCompatActivity {

  private ActEditorBinding binding;
  private GLRenderer renderer;
  private Project mProject;

  private ObjectsAdapter mAdapter;
  private List<Object2D> mObjectsList = new LinkedList<>();

  private ScaleGestureDetector mScaleDetector;
  private float mScaleFactor = 1.0f;

  private String mode = "NONE";

  private float mLastX = 0f;
  private float mLastY = 0f;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    this.binding = ActEditorBinding.inflate(getLayoutInflater());
    this.setContentView(binding.getRoot());

    this.main();
  }

  protected void main() {
    observer();

    mProject = getIntent().getParcelableExtra("project");
    getSupportActionBar().setTitle(mProject.getName());

    binding.surface.setEGLContextClientVersion(3);
    renderer = new GLRenderer(this);

    binding.surface.setRenderer(renderer);
    binding.surface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    renderer.loadObjects(mProject.getPath() + "/scene.world");
    binding.surface.requestRender();

    mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());

    mAdapter = new ObjectsAdapter(mObjectsList);
    mAdapter.setOnLongClickListener(
        (view, pos) -> {
          PopupMenu popup = new PopupMenu(this, view);
          Menu menu = popup.getMenu();

          menu.add(0, 0, 0, "Deletar");

          popup.setOnMenuItemClickListener(
              (item) -> {
                switch (item.getItemId()) {
                  case 0:
                    {
                      mAdapter.remove(pos);
                      renderer.removeObject(pos);

                      binding.surface.requestRender();
                      loadObjects();
                      break;
                    }
                  default:
                    break;
                }

                return true;
              });

          popup.show();
          return true;
        });

    binding.objects.setAdapter(mAdapter);
    binding.objects.setLayoutManager(new LinearLayoutManager(this));

    loadObjects();
  }

  protected void observer() {
    binding.surface.setOnTouchListener(
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
                if (mode != "ZOOM") {
                  final float x = event.getX();
                  final float y = event.getY();

                  dx = x - mLastX;
                  dy = y - mLastY;

                  Vector3 vector3 = renderer.getCamera().getTransform().getPosition();
                  vector3.set(vector3.getX() + dx / x, vector3.getY() - dy / y, 0);

                  binding.surface.requestRender();

                  mLastX = x;
                  mLastY = y;

                  mode = "MOVE";
                }

                break;
              }

            case MotionEvent.ACTION_UP:
              {
                mode = "NONE";
                break;
              }
          }

          mScaleDetector.onTouchEvent(event);
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
            binding.surface.requestRender();

            loadObjects();
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

              objects.put(obj.getUID(), object);
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
          binding.surface.requestRender();
          return true;
        }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void loadObjects() {
    List<Object2D> objects = renderer.getObjectsList();
    if (objects.size() <= 0) {
      binding.line.setVisibility(8);
      return;
    }

    binding.line.setVisibility(0);
    mObjectsList.clear();

    for (Object2D object : objects) {
      mObjectsList.add(new Object2D(object.getUID(), object.getType(), object.getColor()));
    }

    mObjectsList.sort((p1, p2) -> p1.getType().compareTo(p2.getType()));
    mAdapter.notifyDataSetChanged();
  }

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      mScaleFactor *= detector.getScaleFactor();
      renderer.getCamera().setZoom(mScaleFactor);

      binding.surface.requestRender();
      return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
      detector = null;
      mode = "ZOOM";

      System.gc();
      return true;
    }
  }
}
