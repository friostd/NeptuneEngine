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

package com.frio.neptune.opengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.frio.neptune.utils.Camera;
import com.frio.neptune.utils.Object;
import com.frio.neptune.utils.Vector3;
import com.frio.neptune.utils.app.AndroidUtil;
import com.frio.neptune.utils.app.ExceptionUtil;
import com.frio.neptune.utils.app.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private final float[] PROJECTION_MATRIX = new float[16];
    private float mRatio;

    private final List<Object> mObjectsList;

    private Camera mCamera;
    private Vector3 mCameraPosition;

    private float zoom;

    private final Context context;

    private int averageFPS;
    private int currentFrame;
    private long lastTime;

    private Square square;

    public GLRenderer(Context context) {
        this.mObjectsList = new ArrayList<>();
        this.context = context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mCamera = new Camera();
        mCameraPosition = mCamera.getPosition();

        square = new Square();
    }

    public void onDrawFrame(GL10 unused) {
        glClear(GL_COLOR_BUFFER_BIT);

        float x1 = mCameraPosition.getX();
        float y = mCameraPosition.getY();
        float z = mCameraPosition.getZ();
        zoom = mCamera.getZoom();

        Matrix.orthoM(PROJECTION_MATRIX, 0, -mRatio / zoom, mRatio / zoom, -1 / zoom, 1 / zoom, -1, 50);
        Matrix.translateM(PROJECTION_MATRIX, 0, x1, y, z);

        for (int x = 0; x < mObjectsList.size(); x++) {
            square.draw(PROJECTION_MATRIX, mObjectsList.get(x).getColor());
        }

        if (lastTime + 1000 < System.currentTimeMillis()) {
            lastTime = System.currentTimeMillis();
            averageFPS = currentFrame;
            currentFrame = 0;
            return;
        }

        currentFrame++;
    }

    public void loadScene(String path) {
        try {
            JSONObject json = new JSONObject(Objects.requireNonNull(FileUtil.readFile(path)));
            JSONArray array = json.getJSONArray("objects");

            JSONObject objects = array.getJSONObject(0);

            for (int i = 0; i < objects.length(); i++) {
                JSONObject object = objects.getJSONObject(Objects.requireNonNull(objects.names()).getString(i));

                float[] color =
                        AndroidUtil.toArray(object.getString("color").replace("[", "").replace("]", ""));

                addNewObject(Objects.requireNonNull(objects.names()).getString(i), object.getString("type"), color);
            }
        } catch (JSONException e) {
            ExceptionUtil.throwsException(context, e);
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        glViewport(0, 0, width, height);
        mRatio = (float) width / height;

        Matrix.orthoM(PROJECTION_MATRIX, 0, -mRatio / zoom, mRatio / zoom, -1 / zoom, 1 / zoom, -1, 50);
    }

    public List<Object> getObjectsList() {
        return this.mObjectsList;
    }

    public int getObjectsCount() {
        return this.mObjectsList.size();
    }

    public void addNewObject(String uuid, String type, float[] color) {
        mObjectsList.add(new Object(uuid, type, color));
    }

    public void removeObject(int position) {
        mObjectsList.remove(position);
    }

    public Camera getCamera() {
        return mCamera;
    }

    public int getFPS() {
        return averageFPS;
    }
}