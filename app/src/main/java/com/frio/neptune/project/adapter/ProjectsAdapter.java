package com.frio.neptune.project.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frio.neptune.project.Project;
import com.frio.neptune.R;

import java.util.LinkedList;
import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.Holder> {

  private List<Project> mProjectsList;

  private TouchListener listener;

  public ProjectsAdapter(List<Project> list) {
    mProjectsList = list;
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
            if (listener != null) {
              listener.setOnClickListener(v, getAdapterPosition());
            }
          });
    }
  }

  public void setOnClickListener(TouchListener listener) {
    this.listener = listener;
  }

  public interface TouchListener {
    void setOnClickListener(View view, int position);
  }

  @NonNull
  @Override
  public ProjectsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ln_project, parent, false);

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
