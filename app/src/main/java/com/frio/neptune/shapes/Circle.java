package com.frio.neptune.shapes;

import android.opengl.GLES32;
import com.frio.neptune.GLRenderer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Circle {

  private FloatBuffer vertexBuffer;

  private final String vertexShaderCode =
      "uniform mat4 uMVPMatrix;"
          + "attribute vec4 vPosition;"
          + "void main() {"
          + "  gl_Position = uMVPMatrix * vPosition;"
          + "}";

  private final String fragmentShaderCode =
      "precision mediump float;"
          + "uniform vec4 vColor;"
          + "void main() {"
          + "  gl_FragColor = vColor;"
          + "}";

  // Use to access and set the view transformation
  private int vPMatrixHandle;

  static float circleCoords[] = new float[364 * 3];

  private final int mProgram;

  private int positionHandle;
  private int colorHandle;

  public Circle() {
    circleCoords[0] = 0;
    circleCoords[1] = 0;
    circleCoords[2] = 0;

    for (int i = 1; i < 364; i++) {
      circleCoords[(i * 3) + 0] =
          (float) (0.25 * Math.cos((3.14 / 180) * (float) i) + circleCoords[0]);
      circleCoords[(i * 3) + 1] =
          (float) (0.25 * Math.sin((3.14 / 180) * (float) i) + circleCoords[1]);
      circleCoords[(i * 3) + 2] = 0;
    }

    ByteBuffer bb = ByteBuffer.allocateDirect(circleCoords.length * 4);

    bb.order(ByteOrder.nativeOrder());
    vertexBuffer = bb.asFloatBuffer();
    vertexBuffer.put(circleCoords);
    vertexBuffer.position(0);

    int vertexShader = GLRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = GLRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode);

    // create empty OpenGL ES Program
    mProgram = GLES32.glCreateProgram();

    // add the vertex shader to program
    GLES32.glAttachShader(mProgram, vertexShader);

    // add the fragment shader to program
    GLES32.glAttachShader(mProgram, fragmentShader);

    // creates OpenGL ES program executables
    GLES32.glLinkProgram(mProgram);
  }

  public void draw(float[] mvpMatrix, float[] color) {
    // Add program to OpenGL ES environment
    GLES32.glUseProgram(mProgram);

    // get handle to vertex shader's vPosition member
    positionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition");

    // Enable a handle to the triangle vertices
    GLES32.glEnableVertexAttribArray(positionHandle);

    // Prepare the triangle coordinate data
    GLES32.glVertexAttribPointer(positionHandle, 3, GLES32.GL_FLOAT, false, 12, vertexBuffer);

    // get handle to fragment shader's vColor member
    colorHandle = GLES32.glGetUniformLocation(mProgram, "vColor");

    // Set color for drawing the triangle
    GLES32.glUniform4fv(colorHandle, 1, color, 0);

    // get handle to shape's transformation matrix
    vPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix");

    // Pass the projection and view transformation to the shader
    GLES32.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

    // Draw the triangle
    GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, 0, 364);

    // Disable vertex array
    GLES32.glDisableVertexAttribArray(positionHandle);
  }
}
