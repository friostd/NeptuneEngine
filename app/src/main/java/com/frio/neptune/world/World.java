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
import com.frio.neptune.utils.app.FilesUtil;
import com.frio.neptune.utils.app.ProjectUtils;
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

  public static String createNewWorld(String name, String date) {
    JSONObject main = new JSONObject();

    JSONObject tempObject = new JSONObject();
    JSONObject tempWorld = new JSONObject();
    JSONObject objects = new JSONObject();
    JSONObject world = new JSONObject();

    JSONArray objectsArray = new JSONArray();
    JSONArray worldArray = new JSONArray();

    try {
      String uid = ProjectUtils.generateUID();
      String type = ProjectUtils.defaultObjectName;
      String color = ProjectUtils.convertColor(ProjectUtils.defaultObjectColor);

      tempObject.put("type", type);
      tempObject.put("color", color);

      objects.put(uid, tempObject);
      objectsArray.put(objects);

      tempWorld.put("name", name);
      tempWorld.put("date", date);

      worldArray.put(tempWorld);

      main.put("settings", worldArray);
      main.put("objects", objectsArray);

      return main.toString(2);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void saveWorld(Context context, Project project, GLRenderer renderer) {
    JSONObject objects = new JSONObject();
    JSONObject world = new JSONObject();

    try {
      renderer.getObjectsList().stream()
          .forEach(
              obj -> {
                try {
                  JSONObject object = new JSONObject();
                  object.put("type", obj.getType());
                  object.put("color", obj.getColorString());

                  objects.put(obj.getUID(), object);
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              });

      JSONArray objectsArray = new JSONArray();
      JSONArray worldArray = new JSONArray();

      objectsArray.put(objects);
      world.put("name", project.getName());
      world.put("date", project.getDate());

      worldArray.put(world);

      JSONObject main = new JSONObject();

      main.put("settings", worldArray);
      main.accumulate("objects", objectsArray);
      FilesUtil.writeFile(context, project.getWorldPath(), main.toString(2));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}