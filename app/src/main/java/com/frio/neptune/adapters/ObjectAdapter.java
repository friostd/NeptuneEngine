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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frio.neptune.R;
import com.frio.neptune.utils.Object;

import java.util.List;

public class ObjectAdapter extends RecyclerView.Adapter<ObjectAdapter.Holder> {

    private final List<Object> mObjectsList;
    private Context mContext;

    private int selectedPosition = -1;

    private ClickListener onClickListener;

    public ObjectAdapter(List<Object> list) {
        this.mObjectsList = list;
    }

    public void setOnClickListener(ClickListener listener) {
        this.onClickListener = listener;
    }

    @NonNull
    @Override
    public ObjectAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_object, parent, false);

        mContext = parent.getContext();

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjectAdapter.Holder holder, int position) {
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(0xFF06B006);
        } else {
            holder.itemView.setBackgroundColor(0x00000000);
        }

        holder.name.setText(mContext.getString(R.string.square));
    }

    @Override
    public int getItemCount() {
        return mObjectsList.size();
    }

    public void remove(int position) {
        mObjectsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, mObjectsList.size());
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void resetSelection() {
        selectedPosition = -1;
    }

    public interface ClickListener {
        void notify(View view, int position);
    }

    public class Holder extends RecyclerView.ViewHolder {

        private final TextView name;

        @SuppressLint("NotifyDataSetChanged")
        public Holder(final View view) {
            super(view);

            name = view.findViewById(R.id.name);

            view.setTag(view);
            view.setOnClickListener(
                    v -> {
                        if (onClickListener != null) {
                            onClickListener.notify(view, getAdapterPosition());

                            selectedPosition = getAdapterPosition();

                            notifyDataSetChanged();
                        }
                    });
        }
    }
}