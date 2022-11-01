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

package com.frio.neptune.utils.app;

import android.content.Context;
import com.frio.neptune.opengl.GLRenderer;
import com.frio.neptune.utils.Object;
import com.frio.neptune.world.World;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ProjectUtil {

  public static final String defaultObjectName = "Object";
  public static final float[] defaultObjectColor = new float[] {1, 0, 0, 1};
  public static final float[] defaultObjectPosition = new float[2];
  public static final float[] newObjectColor = new float[] {1, 1, 1, 1};

  private GLRenderer renderer;

  public ProjectUtil() {}

  public ProjectUtil(GLRenderer renderer) {
    this.renderer = renderer;
  }

  public void createNewProject(Context context, String name, String version, String date) {
    File file = new File(context.getExternalFilesDir("projects").getAbsolutePath() + "/" + name);

    if (file.exists()) return;

    String path = file.getAbsolutePath();
    String world = World.createNewWorld(version, date);

    FileUtil.newDirectory(path);
    FileUtil.writeFile(path + "/scene.world", world);
  }

  public boolean isExists(Context context, String name) {
    File file = new File(context.getExternalFilesDir("projects").getAbsolutePath() + "/" + name);

    return file.exists();
  }

  public void createNewSquare() {
    String name = defaultObjectName;
    String uuid = generateUUID();
    float[] position = defaultObjectPosition;
    float[] color = newObjectColor;

    renderer.addNewObject(name, uuid, position, color);
  }

  public int getObjectsCount() {
    return renderer.getObjectsList().size();
  }

  public void updateObjects(List<Object> list) {
    if (renderer.getObjectsList().size() <= 0) return;
    list.clear();

    for (Object object : renderer.getObjectsList()) {
      list.add(new Object(object.getName(), object.getUUID(), object.getPosition(), object.getColor()));
    }
  }
  
  public static String generateUUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static String convertArray(float[] array) {
    return Arrays.toString(array).replace("[", "").replace("]", "");
  }
  
  public static String getDateNow() {
    return LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString();
  }

  public static <T> T get(Set<T> list, int index) {
    int n = 0;
    for (T any : list) {
      if (n == index) {
        return any;
      }

      n++;
    }

    return null;
  }
}