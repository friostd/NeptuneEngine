package com.frio.neptune.utils;

public class Transform {

  private Vector3 mVector3;

  public Transform(Vector3 vector3) {
    this.mVector3 = vector3;
  }

  public Vector3 getPosition() {
    return this.mVector3;
  }
}
