package com.frio.neptune;

import android.content.Context;
import android.widget.Toast;
import com.frio.neptune.utils.*;
import com.frio.neptune.utils.app.*;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GLRenderer implements GLSurfaceView.Renderer {

  private final float[] PROJECTION_MATRIX = new float[16];
  private float mRatio;

  private List<Object2D> mObjectsList = new LinkedList<Object2D>();

  private Camera mCamera;
  private Vector3 mCameraPosition;

  private Context mContext;

  public GLRenderer(Context mContext) {
    this.mContext = mContext;
  }

  public void addNewObject(String uid, String type, float[] color) {
    mObjectsList.add(new Object2D(uid, type, color));
  }

  public Camera getCamera() {
    return mCamera;
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
  }

  public void loadObjects(String path) {
    try {
      JSONObject jsonObject = new JSONObject(FilesUtil.readFile(path));
      JSONObject objects = jsonObject.optJSONObject("objects");

      if (objects != null) {
        for (int a = 0; a < objects.length(); a++) {
          String[] colorArray = objects.getString("color").split(",");
          float[] color = new float[colorArray.length];

          for (int b = 0; b < colorArray.length; b++) {
            color[b] = Float.parseFloat(colorArray[b].toString());
          }

          addNewObject(
              objects.getString("uid").toString(), objects.getString("type").toString(), color);
        }
      } else {
        JSONArray array = jsonObject.optJSONArray("objects");
        for (int a = 0; a < array.length(); a++) {
          JSONObject obj = array.optJSONObject(a);
          String[] colorArray = obj.getString("color").split(",");
          float[] color = new float[colorArray.length];

          for (int b = 0; b < colorArray.length; b++) {
            color[b] = Float.parseFloat(colorArray[b].toString());
          }

          addNewObject(obj.getString("uid").toString(), obj.getString("type").toString(), color);
        }
      }
    } catch (JSONException e) {
      FilesUtil.writeFile(
          mContext, mContext.getExternalFilesDir("logs") + "/log.txt", e.getMessage());
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
