package com.frio.neptune.utils.app;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionUtils {

  public static void throwsException(Context context, Throwable throwable) {
    try {
      File file = new File(context.getExternalFilesDir("logs") + "/logs.txt");
      PrintWriter pw = new PrintWriter(file);
      throwable.printStackTrace(pw);

      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}