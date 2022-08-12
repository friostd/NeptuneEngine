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

import android.content.Context;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.frio.neptune.shapes.*;
import com.frio.neptune.utils.*;
import com.frio.neptune.utils.app.*;
import java.util.LinkedList;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GLRenderer implements GLSurfaceView.Renderer {

  private final float[] PROJECTION_MATRIX = new float[16];
  private float mRatio;

  private List<Object2D> mObjectsList = new LinkedList<Object2D>();

  private Camera mCamera;
  private Vector3 mCameraPosition;

  private Context context;

  public GLRenderer(Context context) {
    this.context = context;
  }

  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    mCamera = new Camera();
    mCameraPosition = mCamera.getTransform().getPosition();
  }

  public void onDrawFrame(GL10 unused) {
    GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);

    Matrix.orthoM(
        PROJECTION_MATRIX,
        0,
        -mRatio / mCamera.getZoom(),
        mRatio / mCamera.getZoom(),
        -1 / mCamera.getZoom(),
        1 / mCamera.getZoom(),
        -1,
        50);

    Matrix.translateM(
        PROJECTION_MATRIX,
        0,
        mCameraPosition.getX(),
        mCameraPosition.getY(),
        mCameraPosition.getZ());

    // Draw objects

    for (Object2D object : mObjectsList) {
      switch (object.getType()) {
        case "Square":
          Square square = new Square();
          square.draw(PROJECTION_MATRIX, object.getColor());
          break;
        default:
          break;
      }
    }

    // AndroidUtil.showToast(context, mObjectsList.size() + " Objetos(s)");
  }

  public void loadObjects(String path) {
    try {
      JSONObject json = new JSONObject(FilesUtil.readFile(path));
      JSONArray array = json.getJSONArray("objects");

      JSONObject objects = array.getJSONObject(0);

      for (int i = 0; i < objects.length(); i++) {
        JSONObject object = objects.getJSONObject(objects.names().getString(i));

        float[] color =
            AndroidUtil.toArray(object.getString("color").replace("[", "").replace("]", ""));

        addNewObject(objects.names().getString(i), object.getString("type"), color);
      }
    } catch (JSONException e) {
      AndroidUtil.throwsException(context, e.getMessage());
    }
  }

  public void onSurfaceChanged(GL10 unused, int width, int height) {
    GLES32.glViewport(0, 0, width, height);
    mRatio = (float) width / height;

    Matrix.orthoM(
        PROJECTION_MATRIX,
        0,
        -mRatio * mCamera.getZoom(),
        mRatio * mCamera.getZoom(),
        -mCamera.getZoom(),
        mCamera.getZoom(),
        -1,
        50);
  }

  public List<Object2D> getObjectsList() {
    return this.mObjectsList;
  }

  public void addNewObject(String uid, String type, float[] color) {
    mObjectsList.add(new Object2D(uid, type, color));
  }

  public Camera getCamera() {
    return mCamera;
  }

  public static int loadShader(int type, String shaderCode) {
    int shader = GLES32.glCreateShader(type);

    GLES32.glShaderSource(shader, shaderCode);
    GLES32.glCompileShader(shader);

    return shader;
  }
}
