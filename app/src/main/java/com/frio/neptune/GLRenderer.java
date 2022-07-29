package com.frio.neptune;

import android.opengl.Matrix;
import com.frio.neptune.Object2D;
import com.frio.neptune.shapes.Circle;
import com.frio.neptune.shapes.Square;
import com.frio.neptune.shapes.Triangle;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES32;
import android.opengl.GLSurfaceView;

public class GLRenderer implements GLSurfaceView.Renderer {

  private final float[] vPMatrix = new float[16];
  private final float[] projectionMatrix = new float[16];
  private final float[] viewMatrix = new float[16];

  private float[] mCamForward = new float[3];
  private float[] mCamPosition = new float[3];

  private List<Object2D> objects = new LinkedList<>();

  public void addObject(String uid, String type, float[] color) {
    this.objects.add(new Object2D(uid, type, color));
  }

  // Override methods

  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    mCamForward[2] = 1;
    mCamPosition[2] = -3;
  }

  public void onDrawFrame(GL10 unused) {
    GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);

    Matrix.setLookAtM(
        viewMatrix,
        0,
        mCamPosition[0],
        mCamPosition[1],
        mCamPosition[2],
        mCamForward[0] + mCamPosition[0],
        mCamForward[1] + mCamPosition[1],
        mCamForward[2] + mCamPosition[2],
        0,
        1,
        0);

    Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

    for (Object2D list : objects) {
      switch (list.getType()) {
        case "Square":
          Square square = new Square();
          square.draw(vPMatrix, list.getColor());
          break;
        case "Triangle":
          Triangle triangle = new Triangle();
          triangle.draw(vPMatrix, list.getColor());
          break;
        case "Circle":
          Circle circle = new Circle();
          circle.draw(vPMatrix, list.getColor());
      }
    }
  }

  public void onSurfaceChanged(GL10 unused, int width, int height) {
    GLES32.glViewport(0, 0, width, height);

    float ratio = (float) width / height;

    // this projection matrix is applied to object coordinates
    // in the onDrawFrame() method
    Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, 0, 50);
  }

  public static int loadShader(int type, String shaderCode) {

    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    int shader = GLES32.glCreateShader(type);

    // add the source code to the shader and compile it
    GLES32.glShaderSource(shader, shaderCode);
    GLES32.glCompileShader(shader);

    return shader;
  }

  // Util methods

  public float getPositionX() {
    return this.mCamPosition[0];
  }

  public float getPositionY() {
    return this.mCamPosition[1];
  }

  public float getPositionZ() {
    return this.mCamPosition[2];
  }

  public void setPositionX(float x) {
    this.mCamPosition[0] = x;
  }

  public void setPositionY(float y) {
    this.mCamPosition[1] = y;
  }

  public void setPositionZ(float z) {
    this.mCamPosition[2] = z;
  }
}
