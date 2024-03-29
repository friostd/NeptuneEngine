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

package com.frio.neptune.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frio.neptune.R;
import com.frio.neptune.utils.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.Holder> {

    public ImageView more;
    private final List<Project> mProjectsList;
    private ClickListener clickListener;
    private MenuClickListener menuClickListener;

    public ProjectAdapter(List<Project> list) {
        mProjectsList = list;
    }

    @NonNull
    @Override
    public ProjectAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_project, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectAdapter.Holder holder, int position) {
        Project project = mProjectsList.get(position);

        String name = project.getName();

        more.setOnClickListener(
                (vi) -> {
                    if (menuClickListener != null) {
                        menuClickListener.menuClickListener(vi, position);
                    }
                });

        holder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return mProjectsList.size();
    }

    public void setOnClickListener(ClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnMenuClickListener(MenuClickListener listener) {
        this.menuClickListener = listener;
    }

    public interface ClickListener {
        void clickListener(int position);
    }

    public interface MenuClickListener {
        void menuClickListener(View view, int position);
    }

    public class Holder extends RecyclerView.ViewHolder {

        private final TextView name;

        public Holder(final View view) {
            super(view);

            more = view.findViewById(R.id.more);
            name = view.findViewById(R.id.name);

            view.setTag(view);
            view.setOnClickListener(
                    v -> {
                        if (clickListener != null) {
                            clickListener.clickListener(getAdapterPosition());
                        }
                    });
        }
    }
}