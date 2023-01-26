/*
 * Copyright (c) 2022-2023 friostd.
 *
 * This file is part of Neptune Engine
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    public static void createDir(String path) {
        File file = new File(path);
        if (file.exists()) return;

        file.mkdirs();
    }

    public static void writeFile(String path, String text) {
        File file =
                new File(
                        path.substring(0, path.lastIndexOf('/')),
                        path.substring(path.lastIndexOf('/') + 1));

        try (Writer writer =
                     new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (IOException e) {
            return null;
        }
    }

    public static void delete(String str) {
        File file = new File(str);

        if (!file.exists()) return;
        if (file.isFile()) {
            file.delete();
            return;
        }

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

        file.delete();
    }
}
