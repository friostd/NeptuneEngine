package com.frio.neptune.shapes;

import android.opengl.GLES32;
import com.frio.neptune.GLRenderer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

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

  // number of coordinates per vertex in this array
  static final int COORDS_PER_VERTEX = 3;
  static float triangleCoords[] = { // in counterclockwise order:
    0.0f, 0.25f, 0.0f, // top
    -0.25f, -0.25f, 0.0f, // bottom left
    0.25f, -0.25f, 0.0f // bottom right
  };

  private final int mProgram;

  private int positionHandle;
  private int colorHandle;

  private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
  private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

  public Triangle() {
    // initialize vertex byte buffer for shape coordinates
    ByteBuffer bb =
        ByteBuffer.allocateDirect(
            // (number of coordinate values * 4 bytes per float)
            triangleCoords.length * 4);
    // use the device hardware's native byte order
    bb.order(ByteOrder.nativeOrder());

    // create a floating point buffer from the ByteBuffer
    vertexBuffer = bb.asFloatBuffer();
    // add the coordinates to the FloatBuffer
    vertexBuffer.put(triangleCoords);
    // set the buffer to read the first coordinate
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
    GLES32.glVertexAttribPointer(
        positionHandle, COORDS_PER_VERTEX, GLES32.GL_FLOAT, false, vertexStride, vertexBuffer);

    // get handle to fragment shader's vColor member
    colorHandle = GLES32.glGetUniformLocation(mProgram, "vColor");

    // Set color for drawing the triangle
    GLES32.glUniform4fv(colorHandle, 1, color, 0);

    // get handle to shape's transformation matrix
    vPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix");

    // Pass the projection and view transformation to the shader
    GLES32.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

    // Draw the triangle
    GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount);

    // Disable vertex array
    GLES32.glDisableVertexAttribArray(positionHandle);
  }
}
