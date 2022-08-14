package com.frio.neptune.world;

public final class WorldJNIBridge {

  static {
    System.loadLibrary("ndk");
  }

  public native String load_world();
}
