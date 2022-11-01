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

package com.frio.neptune.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.frio.neptune.R;
import com.frio.neptune.adapters.*;
import com.frio.neptune.databinding.ActivityEditorBinding;
import com.frio.neptune.opengl.*;
import com.frio.neptune.utils.*;
import com.frio.neptune.utils.Object;
import com.frio.neptune.utils.app.*;
import com.frio.neptune.world.World;
import com.itsaky.androidide.logsender.LogSender;
import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends AppCompatActivity {

  private ActivityEditorBinding binding;
  private GLRenderer renderer;
  private String mode = null;

  private Project mProject;
  private ProjectUtil mProjectUtil;
  private ObjectAdapter mObjectsAdapter;
  private List<Object> mObjectsList;

  private ScaleGestureDetector mScaleDetector;
  private float mScaleFactor = 1.0f;

  private float mLastX = 0f;
  private float mLastY = 0f;

  private MenuItem mTrashItem;

  @Override
  protected void onCreate(Bundle bundle) {
    LogSender.startLogging(this);
    super.onCreate(bundle);

    this.binding = ActivityEditorBinding.inflate(getLayoutInflater());
    this.setContentView(binding.getRoot());

    this.setSupportActionBar(binding.toolbar);

    this.main();
  }

  protected void main() {
    observer();

    mObjectsList = new ArrayList<Object>();

    mProject = getIntent().getParcelableExtra("project");
    getSupportActionBar().setTitle(mProject.getName());

    binding.surface.setEGLContextClientVersion(2);

    renderer = new GLRenderer(getApplicationContext());
    renderer.loadScene(mProject.getWorldPath());

    binding.surface.setRenderer(renderer);
    
    mProjectUtil = new ProjectUtil(renderer);
    mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
    mObjectsAdapter = new ObjectAdapter(mObjectsList);

    mObjectsAdapter.setOnClickListener(
        (view, pos) -> {
          mTrashItem.setVisible(true);
          binding.propertyLayout.setVisibility(0);

          Object object = mObjectsAdapter.get(pos);

          EditText etName = binding.objectName;
          etName.setText(object.getName());
          etName.setSelection(etName.getSelectionEnd());

          etName.setOnEditorActionListener(
              (v, actionId, event) -> {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                  String text = etName.getText().toString().trim();

                  if (text.isEmpty()) {
                    etName.setText(object.getName());
                    etName.setSelection(etName.getSelectionEnd());
                  } else {
                    object.setName(text);
                    renderer.getObjectsList().set(pos, object);
                    mObjectsAdapter.notifyDataSetChanged();
                  }
                }

                return handled;
              });
        });

    binding.objects.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    binding.objects.setAdapter(mObjectsAdapter);

    mProjectUtil.updateObjects(mObjectsList);
    mObjectsAdapter.notifyDataSetChanged();
    binding.objectsCount.setText(String.valueOf(renderer.getObjectsCount()));

    new Thread(
            () -> {
              while (true) {
                try {
                  runOnUiThread(
                      () -> {
                        getSupportActionBar().setSubtitle(renderer.getFPS() + " FPS");
                      });
                  Thread.sleep(100l);
                } catch (InterruptedException exception) {
                  ExceptionUtil.throwsException(exception);
                  break;
                }
              }
            })
        .start();
  }

  protected void observer() {
    binding.surface.setOnTouchListener(
        (view, event) -> {
          switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
              {
                mLastX = event.getRawX();
                mLastY = event.getRawY();

                mode = "DOWN";
                break;
              }

            case MotionEvent.ACTION_MOVE:
              {
                if (mode != "ZOOM") {
                  int dpi = getResources().getDisplayMetrics().densityDpi;
                  float zoom = 1f / renderer.getCamera().getZoom();

                  float vx = (mLastX - event.getRawX()) / dpi * zoom;
                  float vy = (mLastY - event.getRawY()) / dpi * zoom;

                  Vector3 vector3 = renderer.getCamera().getPosition();
                  vector3.set(vector3.getX() - vx, vector3.getY() + vy, 0);

                  mLastX = event.getRawX();
                  mLastY = event.getRawY();

                  mode = "MOVE";
                }

                break;
              }

            case MotionEvent.ACTION_UP:
              {
                mode = null;
                break;
              }
          }

          mScaleDetector.onTouchEvent(event);
          return true;
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    mTrashItem = menu.findItem(R.id.trash);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.trash:
        mTrashItem.setVisible(false);

        mObjectsAdapter.remove(mObjectsAdapter.getSelectedPosition());
        renderer.removeObject(mObjectsAdapter.getSelectedPosition());

        mProjectUtil.updateObjects(mObjectsList);
        mObjectsAdapter.resetSelection();
        binding.propertyLayout.setVisibility(8);
        mObjectsAdapter.notifyDataSetChanged();

        binding.objectsCount.setText(String.valueOf(renderer.getObjectsCount()));

        return true;
      case R.id.square:
        mProjectUtil.createNewSquare();
        binding.surface.requestRender();

        mProjectUtil.updateObjects(mObjectsList);
        mObjectsAdapter.notifyDataSetChanged();

        binding.objectsCount.setText(String.valueOf(renderer.getObjectsCount()));

        return true;
      case R.id.save:
        World.saveWorld(mProject, renderer);
        AndroidUtil.showToast(getApplicationContext(), getString(R.string.saved_successfully));

        return true;
      case R.id.centralize_camera:
        renderer.getCamera().resetPosition();

        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      mScaleFactor *= detector.getScaleFactor();
      renderer.getCamera().setZoom(mScaleFactor);

      return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
      mode = "ZOOM";

      return true;
    }
  }
}