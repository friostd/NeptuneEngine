/*
 * Copyright (c) 2022-2023 friostd.
 *
 * This file is part of Neptune Engine
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.frio.neptune.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.frio.neptune.R;
import com.frio.neptune.adapters.ObjectAdapter;
import com.frio.neptune.databinding.ActivityEditorBinding;
import com.frio.neptune.opengl.GLRenderer;
import com.frio.neptune.utils.Object;
import com.frio.neptune.utils.Project;
import com.frio.neptune.utils.Vector3;
import com.frio.neptune.utils.app.AndroidUtil;
import com.frio.neptune.utils.app.ExceptionUtil;
import com.frio.neptune.utils.app.ProjectUtil;
import com.frio.neptune.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditorActivity extends AppCompatActivity {

    private ActivityEditorBinding binding;
    private GLRenderer renderer;
    private String mode = null;

    private Project mProject;
    private ObjectAdapter mObjectsAdapter;
    private List<Object> mObjectsList;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;

    private float mLastX = 0f;
    private float mLastY = 0f;

    private MenuItem mTrashItem;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.binding = ActivityEditorBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        this.setSupportActionBar(binding.toolbar);

        this.main();
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void main() {
        observer();

        mObjectsList = new ArrayList<>();

        mProject = getIntent().getParcelableExtra("project");
        Objects.requireNonNull(getSupportActionBar()).setTitle(mProject.getName());

        binding.surface.setEGLContextClientVersion(2);

        renderer = new GLRenderer(getApplicationContext());
        renderer.loadScene(mProject.getWorldPath());

        binding.surface.setRenderer(renderer);

        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        mObjectsAdapter = new ObjectAdapter(mObjectsList);

        mObjectsAdapter.setOnClickListener((view, pos) -> mTrashItem.setVisible(true));

        binding.objects.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.objects.setAdapter(mObjectsAdapter);

        ProjectUtil.updateObjects(renderer, mObjectsList);
        mObjectsAdapter.notifyDataSetChanged();
        binding.objectsCount.setText(String.valueOf(renderer.getObjectsCount()));

        new Thread(
                () -> {
                    while (true) {
                        try {
                            runOnUiThread(
                                    () -> getSupportActionBar().setSubtitle(renderer.getFPS() + " FPS"));
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            ExceptionUtil.throwsException(getApplicationContext(), Objects.requireNonNull(e.getCause()));
                            break;
                        }
                    }
                })
                .start();
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void observer() {
        binding.surface.setOnTouchListener(
                (view, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            mLastX = event.getRawX();
                            mLastY = event.getRawY();

                            mode = "DOWN";
                            break;
                        }

                        case MotionEvent.ACTION_MOVE: {
                            if (!Objects.equals(mode, "ZOOM")) {
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

                        case MotionEvent.ACTION_UP: {
                            mode = null;
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
        getMenuInflater().inflate(R.menu.menu, menu);
        mTrashItem = menu.findItem(R.id.trash);

        return true;
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trash:
                mTrashItem.setVisible(false);

                mObjectsAdapter.remove(mObjectsAdapter.getSelectedPosition());
                renderer.removeObject(mObjectsAdapter.getSelectedPosition());

                ProjectUtil.updateObjects(renderer, mObjectsList);
                mObjectsAdapter.resetSelection();
                mObjectsAdapter.notifyDataSetChanged();

                binding.objectsCount.setText(String.valueOf(renderer.getObjectsCount()));

                return true;
            case R.id.square:
                ProjectUtil.createNewSquare(renderer);
                binding.surface.requestRender();

                ProjectUtil.updateObjects(renderer, mObjectsList);
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