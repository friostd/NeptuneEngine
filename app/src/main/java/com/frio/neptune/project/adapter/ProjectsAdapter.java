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

// Util

package com.frio.neptune.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.frio.neptune.R;
import com.frio.neptune.project.Project;
import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.Holder> {

  private List<Project> mProjectsList;
  private ClickListener clickListener;
  private LongClickListener longClickListener;

  public ProjectsAdapter(List<Project> list) {
    mProjectsList = list;
  }

  public void setOnClickListener(ClickListener listener) {
    this.clickListener = listener;
  }

  public boolean setOnLongClickListener(LongClickListener listener) {
    this.longClickListener = listener;

    return true;
  }

  public interface ClickListener {
    void clickListener(int position);
  }

  public interface LongClickListener {
    boolean longClickListener(View view, int position);
  }

  public class Holder extends RecyclerView.ViewHolder {

    private ImageView icon;
    private TextView name;

    public Holder(final View view) {
      super(view);

      icon = view.findViewById(R.id.icon);
      name = view.findViewById(R.id.name);

      view.setTag(view);
      view.setOnClickListener(
          v -> {
            if (clickListener != null) {
              clickListener.clickListener(getAdapterPosition());
            }
          });

      view.setOnLongClickListener(
          vi -> {
            if (longClickListener != null) {
              longClickListener.longClickListener(vi, getAdapterPosition());
            }

            return true;
          });
    }
  }

  @NonNull
  @Override
  public ProjectsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.ln_project, parent, false);

    return new Holder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ProjectsAdapter.Holder holder, int position) {
    Project project = mProjectsList.get(position);

    String name = project.getName();
    String path = project.getPath();

    holder.name.setText(name);
  }

  @Override
  public int getItemCount() {
    return mProjectsList.size();
  }
}
