package com.frio.neptune.utils;

public class Object2D {

  private String uid;
  private String type;
  private float[] color;

  public Object2D(String uid, String type, float[] color) {
    this.uid = uid;
    this.type = type;
    this.color = color;
  }

  public String getUid() {
    return this.uid;
  }

  public void setId(String uid) {
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
}
