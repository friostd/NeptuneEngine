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

// Shapes

package com.frio.neptune.shapes;

import android.opengl.GLES32;
import android.opengl.Matrix;
import com.frio.neptune.GLRenderer;
import com.frio.neptune.utils.Vector3;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

  private FloatBuffer vertexBuffer;

  private final String vertexShaderCode =
      "uniform mat4 uMVPMatrix;"
          + "attribute vec4 vPosition;"
          + "uniform mat4 model;"
          + "void main() {"
          + "  gl_Position = uMVPMatrix * vPosition * model;"
          + "}";

  private final String fragmentShaderCode =
      "precision mediump float;"
          + "uniform vec4 vColor;"
          + "void main() {"
          + "  gl_FragColor = vColor;"
          + "}";

  private int vPMatrixHandle;

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
  private final int vertexStride = COORDS_PER_VERTEX * 4;

  private Vector3 position = new Vector3(0, 0, 0);
  private Vector3 scale = new Vector3(1, 1, 1);
  private Vector3 rotation = new Vector3(0, 0, 0);

  public Triangle() {
    ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);

    bb.order(ByteOrder.nativeOrder());
    vertexBuffer = bb.asFloatBuffer();
    vertexBuffer.put(triangleCoords);
    vertexBuffer.position(0);

    int vertexShader = GLRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = GLRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode);

    mProgram = GLES32.glCreateProgram();

    GLES32.glAttachShader(mProgram, vertexShader);

    GLES32.glAttachShader(mProgram, fragmentShader);

    GLES32.glLinkProgram(mProgram);
    
    position.setX(2);
  }

  public void draw(float[] mvpMatrix, float[] color) {
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

    vPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix");

    GLES32.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

    int modelHandle = GLES32.glGetUniformLocation(mProgram, "model");
    GLES32.glUniformMatrix4fv(modelHandle, 1, false, model, 0);

    GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount);

    GLES32.glDisableVertexAttribArray(positionHandle);
  }
}