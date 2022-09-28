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

package com.frio.neptune.opengl;

import static android.opengl.GLES32.*;

import android.content.Context;
import android.opengl.Matrix;
import com.frio.neptune.utils.Vector3;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {

  private FloatBuffer mVertexBuffer;
  private ShortBuffer mDrawListBuffer;

  private int mProjectionMatrixHandle;

  private static final float SQUARE_COORDS[] = {
    -0.25f, 0.25f, 0.0f,
    -0.25f, -0.25f, 0.0f,
    0.25f, -0.25f, 0.0f,
    0.25f, 0.25f, 0.0f
  };

  private final String FRAGMENT_SHADER =
      "precision mediump float;\n"
          + "uniform vec4 vColor;\n"
          + "void main() {\n"
          + "gl_FragColor = vColor;\n"
          + "}";

  private final String VERTEX_SHADER =
      "uniform mat4 projectionMatrix;\n"
          + "attribute vec4 vPosition;\n"
          + "uniform mat4 model;\n"
          + "uniform vec4 vColor;\n"
          + "void main() {\n"
          + "gl_Position = projectionMatrix * model * vPosition;\n"
          + "}";

  private final short DRAW_ORDER[] = {0, 1, 2, 0, 2, 3};

  private final int mProgram;

  private int mPositionHandle;
  private int mColorHandle;
  private int mModelHandle;

  private final int VERTEX_COUNT = SQUARE_COORDS.length / 3;

  private Vector3 mPosition = new Vector3(0, 0, 0);
  private Vector3 mScale = new Vector3(1, 1, 1);
  private Vector3 mRotation = new Vector3(0, 0, 0);

  private Context context;

  public Square(Context context) {
    this.context = context;

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

    int vertexShader = glCreateShader(GL_VERTEX_SHADER);
    int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

    glShaderSource(vertexShader, VERTEX_SHADER);
    glShaderSource(fragmentShader, FRAGMENT_SHADER);

    glCompileShader(vertexShader);
    glCompileShader(fragmentShader);

    mProgram = glCreateProgram();
    glAttachShader(mProgram, vertexShader);
    glAttachShader(mProgram, fragmentShader);

    glLinkProgram(mProgram);
  }

  public void draw(float[] projectionMatrix, float[] color) {
    int buffers[] = new int[2];
    glGenBuffers(2, buffers, 0);
    int vbo = buffers[0];
    int ibo = buffers[1];

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

    glUseProgram(mProgram);

    mPositionHandle = glGetAttribLocation(mProgram, "vPosition");

    glEnableVertexAttribArray(mPositionHandle);
    glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 12, mVertexBuffer);
    mColorHandle = glGetUniformLocation(mProgram, "vColor");

    glUniform4fv(mColorHandle, 1, color, 0);
    mProjectionMatrixHandle = glGetUniformLocation(mProgram, "projectionMatrix");

    glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, projectionMatrix, 0);
    mModelHandle = glGetUniformLocation(mProgram, "model");
    glUniformMatrix4fv(mModelHandle, 1, false, model, 0);

    glDrawArrays(GL_TRIANGLE_FAN, 0, VERTEX_COUNT);
    glDisableVertexAttribArray(mPositionHandle);

    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glVertexAttribPointer(
        mPositionHandle, 3, GL_FLOAT, false, 0, 0); // <----- 0, because "vbo" is bound
    glEnableVertexAttribArray(mPositionHandle);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
    glDrawElements(
        GL_TRIANGLE_FAN,
        DRAW_ORDER.length,
        GL_UNSIGNED_SHORT,
        0); // <----- 0, because "ibo" is bound

    glDisableVertexAttribArray(mPositionHandle);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
  }
}