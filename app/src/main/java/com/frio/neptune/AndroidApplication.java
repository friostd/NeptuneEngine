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
import android.content.Context;
import android.os.Handler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class AndroidApplication extends Application {

  private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

  public static Context applicationContext;
  public static volatile Handler applicationHandler;

  @Override
  public void onCreate() {
    this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    Thread.setDefaultUncaughtExceptionHandler(
        (thread, ex) -> {
          String error = getStackTrace(ex);

          uncaughtExceptionHandler.uncaughtException(thread, ex);
        });

    super.onCreate();

    applicationContext = this;
    applicationHandler = new Handler(applicationContext.getMainLooper());
  }

  private String getStackTrace(Throwable th) {
    final Writer result = new StringWriter();

    final PrintWriter printWriter = new PrintWriter(result);
    Throwable cause = th;

    while (cause != null) {
      cause.printStackTrace(printWriter);
      cause = cause.getCause();
    }

    final String stacktraceAsString = result.toString();
    printWriter.close();

    return stacktraceAsString;
  }
}