package com.frio.neptune.shapes;

import android.opengl.GLES32;
import android.opengl.Matrix;
import com.frio.neptune.GLRenderer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

  private FloatBuffer vertexBuffer;
  private ShortBuffer drawListBuffer;

  private final String vertexShaderCode =
      "uniform mat4 uMVPMatrix;"
          + "attribute vec4 vPosition;"
          + "uniform mat4 model;"
          + "void main() {"
          + "  gl_Position = uMVPMatrix * model * vPosition;"
          + "}";

  private final String fragmentShaderCode =
      "precision mediump float;"
          + "uniform vec4 vColor;"
          + "void main() {"
          + "  gl_FragColor = vColor;"
          + "}";

  private int vPMatrixHandle;

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

  private float position[] = new float[16];
  private float rotation[] = new float[16];
  private float scale[] = new float[16];

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

    Matrix.setIdentityM(position, 1);
    Matrix.setIdentityM(scale, 0);
    Matrix.scaleM(scale, 0, 1, 1, 1);

    Matrix.setRotateM(rotation, 0, 0, 0, 0, 1);
  }

  public void draw(float[] mvpMatrix, float[] color) {
    float model[] = new float[16];
    Matrix.multiplyMM(model, 0, scale, 0, rotation, 0);
    Matrix.multiplyMM(model, 0, model, 0, position, 0);

    GLES32.glUseProgram(mProgram);

    positionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition");

    GLES32.glEnableVertexAttribArray(positionHandle);

    GLES32.glVertexAttribPointer(
        positionHandle, COORDS_PER_VERTEX, GLES32.GL_FLOAT, false, vertexStride, vertexBuffer);

    colorHandle = GLES32.glGetUniformLocation(mProgram, "vColor");

    GLES32.glUniform4fv(colorHandle, 1, color, 0);

    vPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix");

    GLES32.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

    int modelHandle = GLES32.glGetUniformLocation(mProgram, "model");
    GLES32.glUniformMatrix4fv(modelHandle, 1, false, model, 0);

    GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, 0, vertexCount);

    GLES32.glDisableVertexAttribArray(positionHandle);
    
    position[0] = 1;
  }
}
