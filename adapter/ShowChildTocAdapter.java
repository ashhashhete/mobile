package com.igenesys.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;
import com.igenesys.databinding.ViewShowChidTocBinding;
import com.igenesys.model.ChildTocModel;


import java.util.ArrayList;

public class ShowChildTocAdapter extends RecyclerView.Adapter<ShowChildTocAdapter.RowHolder> {

    private final Activity activity;
    private ArrayList<ChildTocModel> childTocModels;

    public ShowChildTocAdapter(Activity activity, ArrayList<ChildTocModel> childTocModels) {
        this.childTocModels = childTocModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_show_chid_toc, viewGroup, false);
        return new RowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        ChildTocModel childTocModel = childTocModels.get(position);
        holder.binding.gridItemIcomImg.setImageBitmap(childTocModel.getIcon());
        holder.binding.gridItemText.setText(childTocModel.getName());
    }

    @Override
    public int getItemCount() {
        return (null != childTocModels ? childTocModels.size() : 0);
    }

    static class RowHolder extends RecyclerView.ViewHolder {
        ViewShowChidTocBinding binding;

        RowHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }
}
