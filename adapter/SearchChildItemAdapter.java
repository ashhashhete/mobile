package com.igenesys.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;
import com.igenesys.databinding.RvItemSummaryChildBinding;
import com.igenesys.model.SearchChildItemModel;


import java.util.ArrayList;

public class SearchChildItemAdapter extends RecyclerView.Adapter<SearchChildItemAdapter.ChildViewHolder> {

    Activity activity;
    private ArrayList<SearchChildItemModel> searchChildItemModels;

    // Constructor
    SearchChildItemAdapter(Activity activity, ArrayList<SearchChildItemModel> searchChildItemModels) {
        this.activity = activity;
        this.searchChildItemModels = searchChildItemModels;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // Here we inflate the corresponding
        // layout of the child item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_item_summary_child, viewGroup, false);

        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int position) {

        // Create an instance of the ChildItem
        // class for the given position
        SearchChildItemModel childItem = searchChildItemModels.get(position);

        childViewHolder.binding.txtHeader.setText(childItem.getHeader());
        childViewHolder.binding.txtValue.setText(childItem.getValue());
        childViewHolder.binding.txtValue.setVisibility(View.VISIBLE);
        childViewHolder.binding.rvList.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {

        return searchChildItemModels.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {

        RvItemSummaryChildBinding binding;

        ChildViewHolder(View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }
}