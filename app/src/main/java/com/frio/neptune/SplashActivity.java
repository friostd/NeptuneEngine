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

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.frio.neptune.databinding.ActSplashBinding;
import com.frio.neptune.utils.app.FilesUtil;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

  private ActSplashBinding binding;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    this.binding = ActSplashBinding.inflate(getLayoutInflater());
    this.setContentView(binding.getRoot());

    this.main();
  }

  protected void main() {
    FilesUtil.createDir(getExternalFilesDir("projects").toString());

    // TODO: Remove this line when the application doesn't need logs
    FilesUtil.createDir(getExternalFilesDir("logs").toString());

    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
              }
            },
            2500);
  }
}

