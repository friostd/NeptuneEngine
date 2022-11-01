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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;

  private ProjectAdapter mProjectsAdapter;
  private Set<Project> mProjectsList = new LinkedHashSet<>();

  private ProjectUtil mProjectUtil;

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
          Project project = ProjectUtil.get(mProjectsList, pos);

          Intent intent = new Intent(MainActivity.this, EditorActivity.class);
          intent.putExtra("project", project);

          startActivity(intent);
        });

    mProjectsAdapter.setOnMenuClickListener(
        (view, pos) -> {
          PopupMenu popup = new PopupMenu(this, view);
          Menu menu = popup.getMenu();
          menu.add(0, 0, 0, getString(R.string.delete));
          menu.add(0, 1, 0, getString(R.string.edit));

          popup.setOnMenuItemClickListener(
              (item) -> {
                switch (item.getItemId()) {
                  case 0:
                    {
                      FileUtil.delete(ProjectUtil.get(mProjectsList, pos).getPath());
                      refreshProjects();
                      break;
                    }
                  case 1:
                    {
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

    mProjectUtil = new ProjectUtil();
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
                  AndroidUtil.showToast(this, getString(R.string.digits_valid_name));
                  return;
                }

                if (mProjectUtil.isExists(this, name)) {
                  AndroidUtil.showToast(this, getString(R.string.project_already_exists));
                  return;
                }

                mProjectUtil.createNewProject(this, name, version, ProjectUtil.getDateNow());
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
    String p = getExternalFilesDir("projects").toString();

    try {
      Set<File> set =
          Files.list(Paths.get(p))
              .filter(Files::isDirectory)
              .map(Path::toFile)
              .collect(Collectors.toSet());

      set.stream()
          .forEach(
              file -> {
                String name = file.getName();
                String path = file.getAbsolutePath();
                String date = ProjectUtil.getDateNow();

                mProjectsList.add(new Project(name, version, path, date));
              });

      if (mProjectsList.isEmpty()) {
        binding.projects.setVisibility(8);
        binding.noProjects.setVisibility(0);
        return;
      }

      binding.projects.setVisibility(0);
      binding.noProjects.setVisibility(8);

      mProjectsList.stream().sorted((p1, p2) -> p1.getDate().compareTo(p2.getDate()));
      mProjectsAdapter.notifyDataSetChanged();
    } catch (IOException exception) {
      ExceptionUtil.throwsException(exception);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    refreshProjects();
  }
}