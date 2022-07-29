package com.frio.neptune.utils;

public class Vector3 {

  private float x, y, z;

  public Vector3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public float getX() {
    return this.x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return this.y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getZ() {
    return this.z;
  }

  public void setZ(float z) {
    this.z = z;
  }

  public void set(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void set(Vector3 vector3) {
    if (vector3 == null) return;

    this.x = vector3.getX();
    this.y = vector3.getY();
    this.z = vector3.getZ();
  }
}
