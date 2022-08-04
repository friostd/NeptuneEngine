package com.frio.neptune.utils;

public class Camera {

  private Vector3 mPosition;
  private Transform mTransform;

  private float mCameraZoom = 1.0f;

  public Camera() {
    this.mPosition = new Vector3(0, 0, 0);
    this.mTransform = new Transform(mPosition);
    this.mCameraZoom = 1.f;
  }

  public Transform getTransform() {
    return this.mTransform;
  }

  public void setZoom(float value) {
    this.mCameraZoom = Math.clamp(1.f / 10.f, value, 1.f);
  }

  public float getZoom() {
    return this.mCameraZoom;
  }
}
