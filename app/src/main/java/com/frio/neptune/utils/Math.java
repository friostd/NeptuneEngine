package com.frio.neptune.utils;

public class Math {

  public static float clamp(float min, float value, float max) {
    return (value < min ? min : (value > max ? max : value));
  }
}
