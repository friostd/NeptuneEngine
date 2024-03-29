/*
 * Copyright (c) 2022-2023 friostd.
 *
 * This file is part of Neptune Engine
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
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

public class Project implements Parcelable {

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

    private final String name;
    private final String date;
    private String version;
    private String path;

    public Project(String name, String version, String path, String date) {
        this.name = name;
        this.version = version;
        this.path = path;
        this.date = date;
    }

    protected Project(Parcel parcel) {
        this.name = parcel.readString();
        this.version = parcel.readString();
        this.path = parcel.readString();
        this.date = parcel.readString();
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWorldPath() {
        return this.path + "/scene.world";
    }

    public String getDate() {
        return this.date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.name);
        parcel.writeString(this.version);
        parcel.writeString(this.path);
        parcel.writeString(this.date);
    }
}