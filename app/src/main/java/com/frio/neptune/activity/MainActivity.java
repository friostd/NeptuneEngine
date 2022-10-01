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

package com.frio.neptune.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.frio.neptune.R;
import com.frio.neptune.adapters.*;
import com.frio.neptune.databinding.ActivityMainBinding;
import com.frio.neptune.utils.*;
import com.frio.neptune.utils.app.*;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;

  private ProjectAdapter mProjectsAdapter;
  private List<Project> mProjectsList = new LinkedList<>();

  private String version = "0.2.0-alpha";

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

    mProjectsAdapter = new ProjectAdapter(mProjectsList);
    mProjectsAdapter.setOnClickListener(
        (pos) -> {
          Project project = mProjectsList.get(pos);

          Intent intent = new Intent(MainActivity.this, EditorActivity.class);
          intent.putExtra("project", project);

          startActivity(intent);
        });

    mProjectsAdapter.setOnMenuClickListener(
        (view, pos) -> {
          PopupMenu popup = new PopupMenu(this, view);
          Menu menu = popup.getMenu();
          menu.add(0, 0, 0, getString(R.string.delete));

          popup.setOnMenuItemClickListener(
              (item) -> {
                switch (item.getItemId()) {
                  case 0:
                    {
                      FilesUtil.delete(mProjectsList.get(pos).getPath());
                      refreshProjects();
                      break;
                    }
                  default:
                    break;
                }

                return true;
              });

          popup.show();
        });

    binding.projects.setAdapter(mProjectsAdapter);
    binding.projects.setLayoutManager(new GridLayoutManager(this, 3));
  }

  protected void observer() {
    binding.fab.setOnClickListener(
        (view) -> {
          MaterialAlertDialogBuilder dialog =
              new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog);
          dialog.setTitle(R.string.create_project);

          View inflater = this.getLayoutInflater().inflate(R.layout.layout_create_project, null);
          dialog.setView(inflater);

          final EditText input = inflater.findViewById(R.id.text);
          dialog.setPositiveButton(
              R.string.ok,
              (d, i) -> {
                final String name = AndroidUtil.removeDiacritics(input.getText().toString().trim());

                if (name == null || name.isEmpty()) {
                  AndroidUtil.showToast(this, "Digite um nome válido");
                  return;
                }

                if (ProjectUtils.isExists(this, name)) {
                  AndroidUtil.showToast(this, "Projeto já existente");
                  return;
                }

                ProjectUtils.createNewProject(this, name, version, ProjectUtils.getDateNow());
                AndroidUtil.closeKeyboard(this);

                refreshProjects();
              });

          dialog.setNegativeButton(
              R.string.cancel,
              (d, i) -> {
                AndroidUtil.closeKeyboard(this);
              });

          dialog.show();
        });
  }

  public void refreshProjects() {
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
        mProjectsList.add(
            new Project(
                file.getName(), "0.1.0-Alpha", file.getAbsolutePath(), ProjectUtils.getDateNow()));
      }
    }

    Collections.sort(mProjectsList, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
    mProjectsAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onStart() {
    refreshProjects();

    super.onStart();
  }
}