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

package com.frio.neptune;

import android.app.Application;
import com.frio.neptune.activity.MainActivity;
import com.frio.neptune.utils.app.AndroidUtil;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AndroidApplication extends Application implements Thread.UncaughtExceptionHandler {

  private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public void uncaughtException(Thread thread, Throwable ex) {
    new Thread(
            () -> {
              AndroidUtil.throwsException(getBaseContext(), throwableToString(ex));
            })
        .start();
  }

  private String throwableToString(Throwable t) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter, false);

    t.printStackTrace(printWriter);
    printWriter.flush();
    stringWriter.flush();
    return stringWriter.toString();
  }
}
