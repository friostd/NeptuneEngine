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

// App util

package com.frio.neptune.utils.app;

import android.content.Context;
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
    File file =
        new File(
            path.substring(0, path.lastIndexOf('/')),
            path.substring(path.lastIndexOf('/') + 1, path.length()));

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
