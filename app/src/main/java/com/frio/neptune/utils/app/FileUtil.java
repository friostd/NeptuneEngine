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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileUtil {

  public static boolean exists(String path) {
    File file = new File(path);
    if (file == null) return false;

    return file.exists();
  }

  public static void newDirectory(String path) {
    File file = new File(path);
    if (file.exists()) return;

    file.mkdirs();
  }

  public static void writeFile(String path, String text) {
    try {
      Files.write(Paths.get(path), text.getBytes());
    } catch (IOException exception) {
      ExceptionUtil.throwsException(exception);
    }
  }

  public static String readFile(String path) {
    try {
      BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
      String text = br.lines().collect(Collectors.joining("\n"));

      return text;
    } catch (IOException exception) {
      ExceptionUtil.throwsException(exception);
      return null;
    }
  }

  public static byte[] readRawFile(String path) {
    try {
      FileInputStream stream = new FileInputStream(path);
    } catch (Exception exception) {
      ExceptionUtil.throwsException(exception);
    }

    return new byte[0];
  }

  public static boolean delete(String path) {
    File file = new File(path);

    if (!file.exists()) return false;
    if (file.isFile()) return file.delete();

    File[] listFiles = file.listFiles();

    if (listFiles != null) {
      for (File file2 : listFiles) {
        if (file2.isDirectory()) {
          delete(file2.getAbsolutePath());
        }
        if (file2.isFile()) {
          file2.delete();
        }
      }
    }

    return file.delete();
  }
}