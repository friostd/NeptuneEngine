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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.frio.neptune.databinding.ActMainBinding;
import com.frio.neptune.project.Project;
import com.frio.neptune.project.adapter.ProjectsAdapter;
import com.frio.neptune.utils.app.*;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  // Used to load the 'ndk' library on application startup.
  static {
    System.loadLibrary("ndk");
  }

  private ActMainBinding binding;
  private ProjectsAdapter mAdapter;
  private List<Project> mProjectsList = new LinkedList<>();

  private boolean ignore = false;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    this.binding = ActMainBinding.inflate(getLayoutInflater());
    this.setContentView(binding.getRoot());

    this.setSupportActionBar(binding.toolbar);

    this.main();
  }

  protected void main() {
    observer();

    mAdapter = new ProjectsAdapter(mProjectsList);
    mAdapter.setOnClickListener(
        (pos) -> {
          Project project = mProjectsList.get(pos);

          Intent intent = new Intent(MainActivity.this, EditorActivity.class);
          intent.putExtra("project", project);

          startActivity(intent);
        });

    mAdapter.setOnLongClickListener(
        (view, pos) -> {
          Project project = mProjectsList.get(pos);

          PopupMenu popup = new PopupMenu(this, view);
          Menu menu = popup.getMenu();
          menu.add(0, 0, 0, getString(R.string.delete));

          popup.setOnMenuItemClickListener(
              (item) -> {
                switch (item.getItemId()) {
                  case 0:
                    {
                      FilesUtil.delete(this, project.getPath());
                      refreshProjects();
                      break;
                    }
                  default:
                    break;
                }

                return true;
              });

          popup.show();
          return true;
        });

    binding.projects.setAdapter(mAdapter);
    binding.projects.setLayoutManager(new LinearLayoutManager(this));
  }

  protected void observer() {
    binding.fab.setOnClickListener(
        (view) -> {
          AlertDialog dialog = new AlertDialog.Builder(this).create();
          View inflater = dialog.getLayoutInflater().inflate(R.layout.ln_new_project, null);

          dialog.setView(inflater);
          dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

          EditText input = inflater.findViewById(R.id.text);
          Button ok = inflater.findViewById(R.id.ok);
          Button cancel = inflater.findViewById(R.id.cancel);

          input.setFocusableInTouchMode(true);
          ok.setOnClickListener(
              (otherView) -> {
                otherView = null;
                final String name = AndroidUtil.removeDiacritics(input.getText().toString().trim());

                if (name == null || name.isEmpty()) {
                  AndroidUtil.showToast(this, "Digite um nome válido");
                  return;
                }

                createNewProject(name);
                AndroidUtil.closeKeyboard(this);
                dialog.dismiss();

                System.gc();
              });

          cancel.setOnClickListener(
              (someView) -> {
                someView = null;
                AndroidUtil.closeKeyboard(this);
                dialog.dismiss();

                System.gc();
              });

          dialog.show();
        });
  }

  // Util

  private void refreshProjects() {
    mProjectsList.clear();

    File root = new File(getExternalFilesDir("projects").toString());
    File[] listFiles = root.listFiles();

    if (listFiles == null || listFiles.length <= 0) {
      binding.projects.setVisibility(8);
      binding.noProjects.setVisibility(0);
      return;
    }

    binding.projects.setVisibility(0);
    binding.noProjects.setVisibility(8);

    for (File file : listFiles) {
      if (file.isDirectory()) {
        mProjectsList.add(new Project(file.getName(), file.getAbsolutePath()));
        Collections.sort(mProjectsList, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
      }
    }

    mAdapter.notifyDataSetChanged();
  }

  private void createNewProject(String name) {
    if (FilesUtil.exists(getExternalFilesDir("projects") + "/" + name)) {
      AndroidUtil.showToast(this, "Projeto já existente");
      AndroidUtil.closeKeyboard(this);
      return;
    }

    String projectPath = getExternalFilesDir("projects") + "/" + name;

    FilesUtil.createDir(projectPath);
    FilesUtil.writeFile(this, projectPath + "/scene.world", setupProject());

    refreshProjects();
  }

  private String setupProject() {
    JSONObject main = new JSONObject();

    JSONObject object = new JSONObject();
    JSONObject objects = new JSONObject();
    JSONArray array = new JSONArray();

    try {
      String uid = UUID.randomUUID().toString().replace("-", "");
      String type = "Square";
      float[] color = new float[] {1f, 0f, 0f, 1f};

      object.put("type", type);
      object.put("color", Arrays.toString(color));

      objects.put(uid, object);
      array.put(objects);

      main.put("objects", array);
      return main.toString(2);
    } catch (JSONException e) {
      AndroidUtil.showToast(this, e.getMessage());
      return null;
    }
  }

  // @Override methods

  @Override
  protected void onStart() {
    refreshProjects();

    super.onStart();
  }
}
