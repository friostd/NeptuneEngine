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
