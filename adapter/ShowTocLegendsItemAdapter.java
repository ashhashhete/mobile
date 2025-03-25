package com.igenesys.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;
import com.igenesys.databinding.ViewShowTocItemBinding;
import com.igenesys.model.TocLegendsListModel;

import java.util.List;


public class ShowTocLegendsItemAdapter extends RecyclerView.Adapter<ShowTocLegendsItemAdapter.RowHolder> {
    private Activity parentActivity;
    private List<TocLegendsListModel> tocLegendsListModels;
    int tabType;


    public ShowTocLegendsItemAdapter(Activity activity, List<TocLegendsListModel> tocLegendsListModels) {
        this.parentActivity = activity;
        this.tocLegendsListModels = tocLegendsListModels;
    }

    public List<TocLegendsListModel> getTocLegendsListModels() {
        return tocLegendsListModels;
    }

    public void setTocLegendsListModels(int tabType, List<TocLegendsListModel> tocLegendsListModels) {
        this.tocLegendsListModels = tocLegendsListModels;
        this.tabType = tabType;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_show_toc_item, viewGroup, false);
        return new RowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        TocLegendsListModel tocLegendsListModel = tocLegendsListModels.get(position);
        holder.binding.txtLegendName.setText(String.format("%s", tocLegendsListModel.getLegendsName()));

        if (tabType == 0) {
            holder.binding.ivLegendIcon.setVisibility(View.GONE);
            holder.binding.checkbox.setVisibility(View.VISIBLE);
            holder.binding.checkbox.setChecked(tocLegendsListModel.isChecked());

            holder.binding.checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
                tocLegendsListModel.setChecked(b);
//                ShowTocLegendsItemAdapter.this.notifyDataSetChanged();
            });
        } else {
            holder.binding.checkbox.setVisibility(View.GONE);
            holder.binding.ivLegendIcon.setVisibility(View.VISIBLE);
            holder.binding.ivLegendIcon.setImageResource(tocLegendsListModel.getIcon());
        }

    }

    @Override
    public int getItemCount() {
        return (null != tocLegendsListModels ? tocLegendsListModels.size() : 0);
    }

    static class RowHolder extends RecyclerView.ViewHolder {
        ViewShowTocItemBinding binding;

        RowHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }
}