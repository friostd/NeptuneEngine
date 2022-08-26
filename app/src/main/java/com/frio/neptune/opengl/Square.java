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

package com.frio.neptune.opengl;

import android.opengl.GLES32;
import android.opengl.Matrix;
import com.frio.neptune.utils.Vector3;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

  private FloatBuffer mVertexBuffer;
  private ShortBuffer mDrawListBuffer;

  private final String VERTEX_SHADER_CODE =
      "uniform mat4 projectionMatrix;"
          + "attribute vec4 vPosition;"
          + "uniform mat4 model;"
          + "void main() {"
          + "  gl_Position = projectionMatrix * model * vPosition;"
          + "}";

  private final String FRAGMENT_SHADER_CODE =
      "precision mediump float;"
          + "uniform vec4 vColor;"
          + "void main() {"
          + "  gl_FragColor = vColor;"
          + "}";

  private int mProjectionMatrixHandle;

  private static final float SQUARE_COORDS[] = {
    -0.25f, 0.25f, 0.0f,
    -0.25f, -0.25f, 0.0f,
    0.25f, -0.25f, 0.0f,
    0.25f, 0.25f, 0.0f
  };

  private final short DRAW_ORDER[] = {0, 1, 2, 0, 2, 3};

  private final int mProgram;

  private int mPositionHandle;
  private int mColorHandle;
  private int mModelHandle;

  private final int VERTEX_COUNT = SQUARE_COORDS.length / 3;

  private Vector3 mPosition = new Vector3(0, 0, 0);
  private Vector3 mScale = new Vector3(1, 1, 1);
  private Vector3 mRotation = new Vector3(0, 0, 0);

  public Square() {
    ByteBuffer bb = ByteBuffer.allocateDirect(SQUARE_COORDS.length * 4);

    bb.order(ByteOrder.nativeOrder());
    mVertexBuffer = bb.asFloatBuffer();
    mVertexBuffer.put(SQUARE_COORDS);
    mVertexBuffer.position(0);

    ByteBuffer dlb = ByteBuffer.allocateDirect(DRAW_ORDER.length * 2);

    dlb.order(ByteOrder.nativeOrder());
    mDrawListBuffer = dlb.asShortBuffer();
    mDrawListBuffer.put(DRAW_ORDER);
    mDrawListBuffer.position(0);

    int vertexShader = GLRenderer.loadShader(GLES32.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
    int fragmentShader = GLRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);

    mProgram = GLES32.glCreateProgram();

    GLES32.glAttachShader(mProgram, vertexShader);
    GLES32.glAttachShader(mProgram, fragmentShader);

    GLES32.glLinkProgram(mProgram);
  }

  public void draw(float[] projectionMatrix, float[] color) {
    float positionM[] = new float[16];
    Matrix.setIdentityM(positionM, 0);
    Matrix.translateM(positionM, 0, mPosition.getX(), mPosition.getY(), mPosition.getZ());

    float rotationM[] = new float[16];
    Matrix.setIdentityM(rotationM, 0);
    Matrix.setRotateM(rotationM, 0, mRotation.getX(), 1, 0, 0);
    Matrix.setRotateM(rotationM, 0, mRotation.getY(), 0, 1, 0);
    Matrix.setRotateM(rotationM, 0, mRotation.getZ(), 0, 0, 1);

    float scaleM[] = new float[16];
    Matrix.setIdentityM(scaleM, 0);
    Matrix.scaleM(scaleM, 0, mScale.getX(), mScale.getY(), mScale.getZ());

    float model[] = new float[16];
    Matrix.multiplyMM(model, 0, scaleM, 0, rotationM, 0);
    Matrix.multiplyMM(model, 0, model, 0, positionM, 0);

    GLES32.glUseProgram(mProgram);

    mPositionHandle = GLES32.glGetAttribLocation(mProgram, "vPosition");

    GLES32.glEnableVertexAttribArray(mPositionHandle);

    GLES32.glVertexAttribPointer(mPositionHandle, 3, GLES32.GL_FLOAT, false, 12, mVertexBuffer);

    mColorHandle = GLES32.glGetUniformLocation(mProgram, "vColor");

    GLES32.glUniform4fv(mColorHandle, 1, color, 0);

    mProjectionMatrixHandle = GLES32.glGetUniformLocation(mProgram, "projectionMatrix");

    GLES32.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, projectionMatrix, 0);

    mModelHandle = GLES32.glGetUniformLocation(mProgram, "model");
    GLES32.glUniformMatrix4fv(mModelHandle, 1, false, model, 0);

    GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, 0, VERTEX_COUNT);

    GLES32.glDisableVertexAttribArray(mPositionHandle);
  }
}
