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

public class ProjectUtils {

  public static final String defaultObjectName = "Square";
  public static final float[] defaultObjectColor = new float[] {1, 0, 0, 1};
  public static final float[] newObjectColor = new float[] {1, 1, 1, 1};

  public static void createNewProject(Context context, String name, String date) {
    File file = new File(context.getExternalFilesDir("projects").getAbsolutePath() + "/" + name);

    if (file.exists()) return;

    FilesUtil.createDir(file.getAbsolutePath());
    FilesUtil.writeFile(
        context, file.getAbsolutePath() + "/scene.world", World.createNewWorld(name, date));
  }

  public static boolean isExists(Context context, String name) {
    File file = new File(context.getExternalFilesDir("projects").getAbsolutePath() + "/" + name);

    return file.exists();
  }

  public static String generateUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static String convertColor(float[] color) {
    return Arrays.toString(color);
  }

  public static String getDateNow() {
    return LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString();
  }

  public static void createNewSquare(GLRenderer renderer) {
    String uid = generateUID();
    String type = defaultObjectName;
    float[] color = newObjectColor;

    renderer.addNewObject(uid, type, color);
  }

  public static int getObjectsCount(GLRenderer renderer) {
    return renderer.getObjectsList().size();
  }

  public static void updateObjects(List<Object> list) {
    list.clear();

    for (int x = 0; x < list.size(); x++) {
      list.add(new Object(list.get(x).getUID(), list.get(x).getType(), list.get(x).getColor()));
    }
  }
}