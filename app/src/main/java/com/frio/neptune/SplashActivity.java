package com.frio.neptune;

import com.frio.neptune.utils.app.FilesUtil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
