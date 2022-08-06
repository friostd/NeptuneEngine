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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.frio.neptune.project.Project;
import com.frio.neptune.project.adapter.ProjectsAdapter;
import com.frio.neptune.utils.app.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  private RecyclerView mProjectsView;
  private ProjectsAdapter mAdapter;

  private FloatingActionButton mFab;

  private List<Project> mProjectsList = new LinkedList<>();

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    this.setContentView(R.layout.act_main);

    main();
  }

  protected void main() {
    initializeViews();

    mAdapter = new ProjectsAdapter(mProjectsList);
    mAdapter.setOnClickListener(
        (view, pos) -> {
          Project project = mProjectsList.get(pos);

          Intent intent = new Intent(MainActivity.this, EditorActivity.class);
          intent.putExtra("project", project);

          startActivity(intent);
        });

    mProjectsView.setAdapter(mAdapter);
    mProjectsView.setLayoutManager(new LinearLayoutManager(this));
  }

  protected void initializeViews() {
    mProjectsView = findViewById(R.id.rv_projects);
    mFab = findViewById(R.id.fab);

    events();
  }

  protected void events() {
    mFab.setOnClickListener(
        (view) -> {
          AlertDialog dialog = new AlertDialog.Builder(this).create();
          View v = dialog.getLayoutInflater().inflate(R.layout.ln_new_project, null);

          dialog.setView(v);
          dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00FFFFFF));

          EditText input = v.findViewById(R.id.txt_name);
          TextView ok = v.findViewById(R.id.btn_ok);
          TextView cancel = v.findViewById(R.id.btn_cancel);

          input.setFocusableInTouchMode(true);
          ok.setOnClickListener(
              (view1) -> {
                final String name = input.getText().toString().trim();

                if (name == null || name.isEmpty()) {
                  AndroidUtil.showToast(this, "Digite um nome válido");
                  return;
                }

                createNewProject(name);
                closeKeyboard();
                dialog.dismiss();
              });

          cancel.setOnClickListener(
              (view2) -> {
                closeKeyboard();
                dialog.dismiss();
              });

          dialog.show();
        });
  }

  // Util

  private void refreshProjects() {
    mProjectsList.clear();

    File file = new File(getExternalFilesDir("projects").toString());
    for (File f : file.listFiles()) {
      if (f.isHidden() || f.isFile()) return;

      mProjectsList.add(new Project(f.getName(), f.getAbsolutePath()));
      Collections.sort(mProjectsList, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));

      mAdapter.notifyDataSetChanged();
    }
  }

  private void createNewProject(String name) {
    if (FilesUtil.exists(getExternalFilesDir("projects") + "/" + name)) {
      AndroidUtil.showToast(this, "Projeto já existente");
      closeKeyboard();
      return;
    }

    final String projectPath = getExternalFilesDir("projects") + "/" + name;

    FilesUtil.createDir(projectPath);
    FilesUtil.writeFile(this, projectPath + "/scene.world", setupProject());

    refreshProjects();
  }

  private String setupProject() {
    JSONObject json = new JSONObject();
    JSONObject objects = new JSONObject();

    try {
      String uid = UUID.randomUUID().toString().replace("-", "");
      String type = "Square";
      String color = "1.0f,0.0f,0.0f,1.0f";

      json.put("uid", uid);
      json.put("type", type);
      json.put("color", color);

      objects.put("objects", json);

      return objects.toString(2);
    } catch (JSONException e) {
      AndroidUtil.showToast(this, e.getMessage());
      return null;
    }
  }

  private void closeKeyboard() {
    if (this.getCurrentFocus() != null) {
      InputMethodManager imm =
          (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
  }

  // @Override methods

  @Override
  protected void onStart() {
    refreshProjects();

    super.onStart();
  }
}
