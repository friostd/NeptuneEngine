package com.frio.neptune;

import android.opengl.Matrix;
import com.frio.neptune.utils.Object2D;
// import com.frio.neptune.shapes.Circle;
import com.frio.neptune.shapes.Square;
// import com.frio.neptune.shapes.Triangle;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES32;
import android.opengl.GLSurfaceView;

public class GLRenderer implements GLSurfaceView.Renderer {

  private final float[] projectionMatrix = new float[16];
  private float cameraZoom = 1.0f;
  private float ratio;

  private List<Object2D> objects = new LinkedList<Object2D>();

  public void addNewObject(String uid, String type, float[] color) {
    this.objects.add(new Object2D(uid, type, color));
  }

  public void setCameraZoom(float cameraZoom) {
    this.cameraZoom = Math.min(1.f / 10, Math.max(cameraZoom, 10));;
  }
  
  public float getCameraZoom() {
    return this.cameraZoom;
  }

  public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
  }

  public void onDrawFrame(GL10 unused) {
    GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT);
    
    Matrix.orthoM(this.projectionMatrix, 0, -ratio / cameraZoom, ratio / cameraZoom, -1 / cameraZoom, 1 / cameraZoom, -1, 50);

    // Draw objects

    for (Object2D list : objects) {
      switch (list.getType()) {
        case "Square":
          Square square = new Square();
          square.draw(projectionMatrix, list.getColor());
          break;
        default:
          break;
          /*case "Triangle":
            Triangle triangle = new Triangle();
            triangle.draw(projectionMatrix, list.getColor());
            break;
          case "Circle":
            Circle circle = new Circle();
            circle.draw(projectionMatrix, list.getColor());*/
      }
    }
  }

  public void onSurfaceChanged(GL10 unused, int width, int height) {
    GLES32.glViewport(0, 0, width, height);
    ratio = (float) width / height;

    Matrix.orthoM(this.projectionMatrix, 0, -ratio * cameraZoom, ratio * cameraZoom, -cameraZoom, cameraZoom, -1, 50);
  }

  public static int loadShader(int type, String shaderCode) {
    int shader = GLES32.glCreateShader(type);

    GLES32.glShaderSource(shader, shaderCode);
    GLES32.glCompileShader(shader);

    return shader;
  }
}
