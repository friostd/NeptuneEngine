package com.frio.neptune.shapes;

import android.opengl.GLES32;
import android.opengl.Matrix;
import com.frio.neptune.GLRenderer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.frio.neptune.utils.Vector3;

public class Square {

  private FloatBuffer vertexBuffer;
  private ShortBuffer drawListBuffer;

  private final String vertexShaderCode =
      "uniform mat4 projectionMatrix;"
          + "attribute vec4 vPosition;"
          + "uniform mat4 model;"
          + "void main() {"
          + "  gl_Position = projectionMatrix * model * vPosition;"
          + "}";

  private final String fragmentShaderCode =
      "precision mediump float;"
          + "uniform vec4 vColor;"
          + "void main() {"
          + "  gl_FragColor = vColor;"
          + "}";

  private int projectionMatrixHandle;

  static final int COORDS_PER_VERTEX = 3;
  static float squareCoords[] = {
    -0.25f, 0.25f, 0.0f,
    -0.25f, -0.25f, 0.0f,
    0.25f, -0.25f, 0.0f,
    0.25f, 0.25f, 0.0f
  };

  private short drawOrder[] = {0, 1, 2, 0, 2, 3};

  private final int mProgram;

  private int positionHandle;
  private int colorHandle;

  private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
  private final int vertexStride = COORDS_PER_VERTEX * 4;

  private Vector3 position = new Vector3(0, 0, 0);
  private Vector3 scale = new Vector3(1, 1, 1);
  private Vector3 rotation = new Vector3(0, 0, 0);

  public Square() {
    ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);

    bb.order(ByteOrder.nativeOrder());
    vertexBuffer = bb.asFloatBuffer();
    vertexBuffer.put(squareCoords);
    vertexBuffer.position(0);

    ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);

    dlb.order(ByteOrder.nativeOrder());
    drawListBuffer = dlb.asShortBuffer();
    drawListBuffer.put(drawOrder);
    drawListBuffer.position(0);

    int vertexShader = GLRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = GLRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode);

    mProgram = GLES32.glCreateProgram();

    GLES32.glAttachShader(mProgram, vertexShader);

    GLES32.glAttachShader(mProgram, fragmentShader);

    GLES32.glLinkProgram(mProgram);
  }

  public void draw(float[] projectionMatrix, float[] color) {
    float positionM[] = new float[16];
    Matrix.setIdentityM(positionM, 0);
    Matrix.translateM(positionM, 0, position.getX(), position.getY(), position.getZ());

    float rotationM[] = new float[16];
    Matrix.setIdentityM(rotationM, 0);
    Matrix.setRotateM(rotationM, 0, rotation.getX(), 1, 0, 0);
    Matrix.setRotateM(rotationM, 0, rotation.getY(), 0, 1, 0);
    Matrix.setRotateM(rotationM, 0, rotation.getZ(), 0, 0, 1);

    float scaleM[] = new float[16];
    Matrix.setIdentityM(scaleM, 0);
    Matrix.scaleM(scaleM, 0, scale.getX(), scale.getY(), scale.getZ());

    float model[] = new float[16];
    Matrix.multiplyMM(model, 0, scaleM, 0, rotationM, 0);
    Matrix.multiplyMM(model, 0, model, 0, positionM, 0);

    GLES32.glUseProgram(mProgram);

    positionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition");

    GLES32.glEnableVertexAttribArray(positionHandle);

    GLES32.glVertexAttribPointer(
        positionHandle, COORDS_PER_VERTEX, GLES32.GL_FLOAT, false, vertexStride, vertexBuffer);

    colorHandle = GLES32.glGetUniformLocation(mProgram, "vColor");

    GLES32.glUniform4fv(colorHandle, 1, color, 0);

    projectionMatrixHandle = GLES32.glGetUniformLocation(mProgram, "projectionMatrix");

    GLES32.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0);

    int modelHandle = GLES32.glGetUniformLocation(mProgram, "model");
    GLES32.glUniformMatrix4fv(modelHandle, 1, false, model, 0);

    GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, 0, vertexCount);

    GLES32.glDisableVertexAttribArray(positionHandle);
  }
}
