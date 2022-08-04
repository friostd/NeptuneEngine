package com.frio.neptune;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.frio.neptune.project.Project;
import com.frio.neptune.project.adapter.ProjectsAdapter;
import com.frio.neptune.utils.app.*;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.json.JSONArray;
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
      float red = 0 + new Random().nextFloat() * (1 - 0);
      float green = 0 + new Random().nextFloat() * (1 - 0);
      float blue = 0 + new Random().nextFloat() * (1 - 0);

      String uid = UUID.randomUUID().toString();
      String type = "Square";
      String color = String.valueOf(red + "," + green + "," + blue + "," + 1.0f);

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
