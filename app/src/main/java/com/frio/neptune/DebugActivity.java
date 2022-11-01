package com.frio.neptune;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DebugActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    String errorMessage = "";

    if (intent != null) {
      errorMessage = intent.getStringExtra("error");

      AlertDialog dialog =
          new AlertDialog.Builder(this)
              .setTitle("An error occurred")
              .setMessage(errorMessage)
              .setPositiveButton(
                  "End Application",
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      finish();
                    }
                  })
              .create();
      dialog.show();
      ((TextView) dialog.findViewById(android.R.id.message)).setTextIsSelectable(true);
    }
  }
}