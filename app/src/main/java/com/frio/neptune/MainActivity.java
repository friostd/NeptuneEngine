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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.frio.neptune.databinding.ActivityMainBinding;
import com.frio.neptune.project.Project;
import com.frio.neptune.project.adapter.ProjectsAdapter;
import com.frio.neptune.utils.app.*;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;

  private ProjectsAdapter mProjectsAdapter;
  private List<Project> mProjectsList = new LinkedList<>();

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    this.binding = ActivityMainBinding.inflate(getLayoutInflater());
    this.setContentView(binding.getRoot());

    this.setSupportActionBar(binding.toolbar);

    this.main();
  }

  protected void main() {
    observer();

    mProjectsAdapter = new ProjectsAdapter(mProjectsList);
    mProjectsAdapter.setOnClickListener(
        (pos) -> {
          Project project = mProjectsList.get(pos);

          Intent intent = new Intent(MainActivity.this, EditorActivity.class);
          intent.putExtra("project", project);

          startActivity(intent);
        });

    mProjectsAdapter.setOnLongClickListener(
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
                      FilesUtil.delete(project.getPath());
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

    binding.projects.setAdapter(mProjectsAdapter);
    binding.projects.setLayoutManager(new LinearLayoutManager(this));
  }

  protected void observer() {
    binding.fab.setOnClickListener(
        (view) -> {
          AlertDialog dialog = new AlertDialog.Builder(this).create();
          View inflater = dialog.getLayoutInflater().inflate(R.layout.layout_create_project, null);

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
              });

          cancel.setOnClickListener(
              (someView) -> {
                someView = null;

                AndroidUtil.closeKeyboard(this);
                dialog.dismiss();
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

    mProjectsAdapter.notifyDataSetChanged();
  }

  private void createNewProject(String name) {
    if (FilesUtil.exists(getExternalFilesDir("projects") + "/" + name)) {
      AndroidUtil.showToast(this, "Projeto já existente");
      AndroidUtil.closeKeyboard(this);
      return;
    }

    String projectPath = getExternalFilesDir("projects") + "/" + name;
    String date = LocalDate.now(ZoneId.of("America/Sao_Paulo")).toString();

    FilesUtil.createDir(projectPath);
    FilesUtil.writeFile(this, projectPath + "/scene.world", setupProject(name, date));

    refreshProjects();
  }

  private String setupProject(String name, String date) {
    JSONObject main = new JSONObject();

    JSONObject tempObj = new JSONObject();
    JSONObject tempWorld = new JSONObject();
    JSONObject objects = new JSONObject();
    JSONObject world = new JSONObject();

    JSONArray objectsArray = new JSONArray();
    JSONArray worldArray = new JSONArray();

    try {
      String uid = UUID.randomUUID().toString().replace("-", "");
      String type = "Square";
      float[] color = new float[] {1f, 0f, 0f, 1f};

      tempObj.put("type", type);
      tempObj.put("color", Arrays.toString(color));

      objects.put(uid, tempObj);
      objectsArray.put(objects);

      tempWorld.put("name", name);
      tempWorld.put("creationDate", date);

      worldArray.put(tempWorld);

      main.put("worldSettings", worldArray);
      main.put("objects", objectsArray);

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