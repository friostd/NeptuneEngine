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

import android.os.Parcel;
import android.os.Parcelable;
import com.frio.neptune.utils.Object2D;

public class Object2D implements Parcelable {

  private String uid;
  private String type;
  private float[] color;

  public Object2D(String uid, String type, float[] color) {
    this.uid = uid;
    this.type = type;
    this.color = color;
  }

  protected Object2D(Parcel parcel) {
    this.uid = parcel.readString();
    this.type = parcel.readString();
    this.color = parcel.createFloatArray();
  }

  public static final Creator<Object2D> CREATOR =
      new Creator<Object2D>() {

        @Override
        public Object2D createFromParcel(Parcel parcel) {
          return new Object2D(parcel);
        }

        @Override
        public Object2D[] newArray(int size) {
          return new Object2D[size];
        }
      };

  public String getUID() {
    return this.uid;
  }

  public void setUID(String uid) {
    this.uid = uid;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public float[] getColor() {
    return this.color;
  }

  public void setColor(float[] color) {
    this.color = color;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeString(this.uid);
    parcel.writeString(this.type);
    parcel.writeFloatArray(this.color);
  }
}
