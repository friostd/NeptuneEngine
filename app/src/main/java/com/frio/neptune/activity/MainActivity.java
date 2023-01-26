/*
 * Copyright (c) 2022-2023 friostd.
 *
 * This file is part of Neptune Engine
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.frio.neptune.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.frio.neptune.BuildConfig;
import com.frio.neptune.R;
import com.frio.neptune.adapters.ProjectAdapter;
import com.frio.neptune.databinding.ActivityMainBinding;
import com.frio.neptune.utils.Project;
import com.frio.neptune.utils.app.AndroidUtil;
import com.frio.neptune.utils.app.FileUtil;
import com.frio.neptune.utils.app.ProjectUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ProjectAdapter mProjectsAdapter;
    private final List<Project> mProjectsList = new LinkedList<>();

    private final String version = "0.2.0-alpha";

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
                    menu.add(0, 0, 0, R.string.backup);
                    menu.add(0, 1, 0, R.string.delete);
                    menu.add(0, 2, 0, R.string.rename);

                    popup.setOnMenuItemClickListener(
                            (item) -> {
                                switch (item.getItemId()) {
                                    case 0: {
                                        // Later
                                    }
                                    case 1: {
                                        FileUtil.delete(mProjectsList.get(pos).getPath());
                                        fetchProjects();
                                    }
                                    case 2: {
                                        // Late
                                    }
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

                                if (name.isEmpty()) {
                                    AndroidUtil.showToast(this, getString(R.string.enter_valid_name));
                                    return;
                                }

                                if (ProjectUtil.isExists(this, name)) {
                                    AndroidUtil.showToast(this, getString(R.string.project_exists));
                                    return;
                                }

                                ProjectUtil.createNewProject(this, name, version, ProjectUtil.getDateNow());
                                AndroidUtil.closeKeyboard(this);

                                fetchProjects();
                            });

                    dialog.setNegativeButton(
                            R.string.cancel,
                            (d, i) -> AndroidUtil.closeKeyboard(this));

                    dialog.show();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void fetchProjects() {
        mProjectsList.clear();

        File root = new File(getExternalFilesDir("projects").toString());
        File[] listFiles = root.listFiles();

        if (listFiles == null || listFiles.length <= 0) {
            binding.projects.setVisibility(View.GONE);
            binding.noProjects.setVisibility(View.VISIBLE);
            return;
        }

        binding.projects.setVisibility(View.VISIBLE);
        binding.noProjects.setVisibility(View.GONE);

        for (File file : listFiles) {
            if (file.isDirectory()) {
                mProjectsList.add(new Project(file.getName(), BuildConfig.VERSION_NAME, file.getAbsolutePath(), ProjectUtil.getDateNow()));
            }
        }

        mProjectsList.sort(Comparator.comparing(Project::getDate));
        mProjectsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        fetchProjects();

        super.onStart();
    }
}