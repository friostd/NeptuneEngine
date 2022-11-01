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

package com.frio.neptune.utils;

import com.frio.neptune.utils.app.ProjectUtil;

public class Object {

  private String name;
  private String uuid;
  private float[] position;
  private float[] color;

  public Object(String name, String uuid, float[] position, float[] color) {
    this.name = name;
    this.uuid = uuid;
    this.position = position;
    this.color = color;
  }

  public Object(String uuid, float[] position, float[] color) {
    this.name = "Object";
    this.uuid = uuid;
    this.position = position;
    this.color = color;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUUID() {
    return this.uuid;
  }

  public void setUUID(String uid) {
    this.uuid = uid;
  }

  public float[] getColor() {
    return this.color;
  }

  public String getColorString() {
    return ProjectUtil.convertArray(color);
  }

  public void setColor(float[] color) {
    this.color = color;
  }

  public float[] getPosition() {
    return this.position;
  }

  public String getPositionString() {
    return ProjectUtil.convertArray(position);
  }

  public void setPosition(float[] position) {
    this.position = position;
  }
}