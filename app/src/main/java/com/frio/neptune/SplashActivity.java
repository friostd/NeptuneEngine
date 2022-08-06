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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.frio.neptune.utils.app.FilesUtil;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

  private TextView mTxtProgress;
  private final String[] PERMISSIONS =
      new String[] {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
      };

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    this.setContentView(R.layout.act_splash);

    initializeViews();

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
      main();
    } else if (ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.READ_EXTERNAL_STORAGE)
        || ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        || ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
      mTxtProgress.setText(
          "Vá em Informações do aplicativo -> Permissões -> Arquivos e mídia -> Permitir gerenciamento de todos os arquivos.");
      findViewById(R.id.progress).setVisibility(View.GONE);
    } else {
      ActivityCompat.requestPermissions(this, PERMISSIONS, 1000);
    }
  }

  protected void main() {
    mTxtProgress.setText("Permissões aceitas. Iniciando aplicativo.");

    FilesUtil.createDir(getExternalFilesDir("projects").toString());
    FilesUtil.createDir(getExternalFilesDir("logs").toString());

    startActivity(new Intent(SplashActivity.this, MainActivity.class));
    finish();
  }

  protected void initializeViews() {
    mTxtProgress = findViewById(R.id.mTxtProgress);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == 1000) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        main();
      } else {
        mTxtProgress.setText(
            "Sem a permissão de Armazenamento o aplicativo não funciona corretamente. Caso haja dúvidas Leia a Política de Privacidade");
        findViewById(R.id.progress).setVisibility(View.GONE);
        new Timer()
            .schedule(
                new TimerTask() {
                  @Override
                  public void run() {
                    runOnUiThread(
                        new Runnable() {
                          @Override
                          public void run() {
                            mTxtProgress.setText(
                                "Vá em Informações do aplicativo -> Permissões -> Arquivos e mídia -> Permitir gerenciamento de todos os arquivos.");
                          }
                        });
                  }
                },
                7000);
      }
      return;
    }

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
}
