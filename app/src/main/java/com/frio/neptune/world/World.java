package com.frio.neptune.world;

import com.frio.neptune.project.Project;
import com.frio.neptune.utils.Object2D;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class World {

  private Project project;

  private Set<Object2D> objectList;

  public World(Project project) {
    this.objectList = new ConcurrentSkipListSet<>();
    // TODO
  }

  public static World loadWorldFrom(Project project) {
    return null; // TODO
  }

  public void saveWorld() {
    // TODO
  }
}
