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

package com.frio.neptune.opengl;

import android.content.Context;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.frio.neptune.utils.Camera;
import com.frio.neptune.utils.Object;
import com.frio.neptune.utils.Vector3;
import com.frio.neptune.utils.app.*;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GLRenderer implements GLSurfaceView.Renderer {

  private final float[] PROJECTION_MATRIX = new float[16];
  private float mRatio;

  private Set<Object> mObjectsList;

  private Camera mCamera;
  private Vector3 mCameraPosition;

  private float x, y, z;
  private float zoom;

  private Context context;

  private int averageFPS;
  private int currentFrame;
  private long lastTime;

  public boolean isAllowedToRender = true;
  private Square square;

  public GLRenderer(Context context) {
    mObjectsList = new HashSet<>();
    context = context;
  }

  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    if (!isAllowedToRender) return;

    GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    mCamera = new Camera();
    mCameraPosition = mCamera.getPosition();

    square = new Square(context);
  }

  public void onDrawFrame(GL10 unused) {
    if (!isAllowedToRender) return;

    GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);

    x = mCameraPosition.getX();
    y = mCameraPosition.getY();
    z = mCameraPosition.getZ();
    zoom = mCamera.getZoom();

    Matrix.orthoM(PROJECTION_MATRIX, 0, -mRatio / zoom, mRatio / zoom, -1 / zoom, 1 / zoom, -1, 50);
    Matrix.translateM(PROJECTION_MATRIX, 0, x, y, z);

    mObjectsList.stream()
        .forEach(
            object -> {
              square.draw(PROJECTION_MATRIX, object.getColor());
            });

    if (lastTime + 1000 < System.currentTimeMillis()) {
      lastTime = System.currentTimeMillis();
      averageFPS = currentFrame;
      currentFrame = 0;
      return;
    }

    currentFrame++;
  }

  public void loadScene(String path) {
    isAllowedToRender = false;

    try {
      JSONObject json = new JSONObject(FilesUtil.readFile(path));
      JSONArray array = json.getJSONArray("objects");

      if (array == null) return;
      JSONObject objects = array.getJSONObject(0);

      for (int i = 0; i < objects.length(); i++) {
        JSONObject object = objects.getJSONObject(objects.names().getString(i));

        float[] color =
            AndroidUtil.toArray(object.getString("color").replace("[", "").replace("]", ""));

        addNewObject(objects.names().getString(i), object.getString("type"), color);
      }

      isAllowedToRender = true;
    } catch (JSONException e) {
      ExceptionUtils.throwsException(context, e);
      isAllowedToRender = false;

    } catch (ConcurrentModificationException e) {
      ExceptionUtils.throwsException(context, e);
      throw new RuntimeException();
    }
  }

  public void onSurfaceChanged(GL10 unused, int width, int height) {
    if (!isAllowedToRender) return;
    GLES32.glViewport(0, 0, width, height);
    mRatio = (float) width / height;

    Matrix.orthoM(PROJECTION_MATRIX, 0, -mRatio * zoom, mRatio * z, -zoom, zoom, -1, 50);
  }

  public Set<Object> getObjectsList() {
    return this.mObjectsList;
  }

  public int getObjectsCount() {
    return this.mObjectsList.size();
  }

  public void addNewObject(String uid, String type, float[] color) {
    mObjectsList.add(new Object(uid, type, color));
  }

  public void removeObject(int position) {
    mObjectsList.remove(position);
  }

  public Camera getCamera() {
    return mCamera;
  }

  public Context getContext() {
    return context;
  }

  public int getFPS() {
    return averageFPS;
  }

  public static int loadShader(int type, String shaderCode) {
    int shader = GLES32.glCreateShader(type);

    GLES32.glShaderSource(shader, shaderCode);
    GLES32.glCompileShader(shader);

    return shader;
  }
}