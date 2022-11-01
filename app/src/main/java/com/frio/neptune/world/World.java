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
package com.frio.neptune.world;

import android.content.Context;
import com.frio.neptune.opengl.GLRenderer;
import com.frio.neptune.utils.Object;
import com.frio.neptune.utils.Project;
import com.frio.neptune.utils.app.FileUtil;
import com.frio.neptune.utils.app.ProjectUtil;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class World {

  private Project project;
  private Set<Object> objectList;

  public World(Project project) {
    this.objectList = new ConcurrentSkipListSet<>();
    // TODO
  }

  public static World loadWorldFrom(Project project) {
    return null; // TODO
  }

  public static String createNewWorld(String version, String date) {
    JSONObject main = new JSONObject();

    JSONObject tempObject = new JSONObject();
    JSONObject tempWorld = new JSONObject();
    JSONObject objects = new JSONObject();
    JSONObject world = new JSONObject();

    JSONArray objectsArray = new JSONArray();
    JSONArray worldArray = new JSONArray();

    try {
      String name = ProjectUtil.defaultObjectName;
      String uid = ProjectUtil.generateUUID();
      String position = ProjectUtil.convertArray(ProjectUtil.defaultObjectPosition);
      String color = ProjectUtil.convertArray(ProjectUtil.defaultObjectColor);

      tempObject.put("name", name);
      tempObject.put("position", position);
      tempObject.put("color", color);

      objects.put(uid, tempObject);
      objectsArray.put(objects);

      tempWorld.put("date", date);
      tempWorld.put("version", version);

      worldArray.put(tempWorld);

      main.put("settings", worldArray);
      main.put("objects", objectsArray);

      return main.toString(2);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void saveWorld(Project project, GLRenderer renderer) {
    JSONObject objects = new JSONObject();
    JSONObject world = new JSONObject();

    try {
      for (int i = 0; i < renderer.getObjectsList().size(); i++) {
        try {
          Object obj = renderer.getObjectsList().get(i);
          JSONObject object = new JSONObject();
          object.put("name", obj.getName());
          object.put("position", obj.getPositionString());
          object.put("color", obj.getColorString());

          objects.put(obj.getUUID(), object);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      JSONArray objectsArray = new JSONArray();
      JSONArray worldArray = new JSONArray();

      objectsArray.put(objects);
      world.put("date", project.getDate());
      world.put("version", project.getVersion());

      worldArray.put(world);

      JSONObject main = new JSONObject();

      main.put("settings", worldArray);
      main.accumulate("objects", objectsArray);

      FileUtil.writeFile(project.getWorldPath(), main.toString(2));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}