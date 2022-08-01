package com.frio.neptune;

import com.frio.neptune.utils.*;
import com.frio.neptune.shapes.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class GLRenderer implements GLSurfaceView.Renderer {

  private final float[] PROJECTION_MATRIX = new float[16];
  private float mRatio;

  private List<Object2D> mObjectsList = new LinkedList<Object2D>();

  private Camera mCamera;
  private Vector3 mCameraPosition;

  public void addNewObject(String uid, String type, float[] color) {
    this.mObjectsList.add(new Object2D(uid, type, color));
  }

  public Camera getCamera() {
    return this.mCamera;
  }

  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    this.mCamera = new Camera();
    this.mCameraPosition = this.mCamera.getTransform().getPosition();
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

  public static int loadShader(int type, String shaderCode) {
    int shader = GLES32.glCreateShader(type);

    GLES32.glShaderSource(shader, shaderCode);
    GLES32.glCompileShader(shader);

    return shader;
  }
}
