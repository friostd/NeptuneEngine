package com.frio.neptune.utils.app;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class FilesUtil {

  public static boolean exists(String path) {
    File file = new File(path);
    if (file == null) return false;

    return file.exists();
  }

  public static void createDir(String path) {
    File file = new File(path);
    if (file.exists()) return;

    file.mkdirs();
  }

  public static String writeFile(Context context, String path, String text) {
    File file = new File(path.substring(0, path.lastIndexOf('/')), path.substring(path.lastIndexOf('/') + 1, path.length()));
    
    try (Writer writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
      writer.write(text);
      return "";
    } catch (IOException e) {
      return e.getMessage();
    }
  }

  public static String readFile(String path) {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append(System.lineSeparator());
        line = br.readLine();
      }

      return sb.toString();
    } catch (FileNotFoundException e) {
      return null;
    } catch (IOException e) {
      return null;
    }
  }
}
