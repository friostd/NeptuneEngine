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

// Util

package com.frio.neptune.project;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {

  private String name;
  private String path;

  public Project(String name, String path) {
    this.name = name;
    this.path = path;
  }

  protected Project(Parcel parcel) {
    this.name = parcel.readString();
    this.path = parcel.readString();
  }

  public static final Creator<Project> CREATOR =
      new Creator<Project>() {

        @Override
        public Project createFromParcel(Parcel parcel) {
          return new Project(parcel);
        }

        @Override
        public Project[] newArray(int size) {
          return new Project[size];
        }
      };

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeString(this.name);
    parcel.writeString(this.path);
  }
}
