package com.igenesys.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;
import com.igenesys.databinding.ViewShowIdentifyItemBinding;
import com.igenesys.model.IdentifyItemListModel;

import java.util.List;


public class ShowIdentifyItemAdapter extends RecyclerView.Adapter<ShowIdentifyItemAdapter.RowHolder> {
    private Activity parentActivity;
    private List<IdentifyItemListModel> identifyItemListModels;


    public ShowIdentifyItemAdapter(Activity activity, List<IdentifyItemListModel> identifyItemListModels) {
        this.parentActivity = activity;
        this.identifyItemListModels = identifyItemListModels;
    }

    public List<IdentifyItemListModel> getIdentifyItemListModels() {
        return identifyItemListModels;
    }

    public void setIdentifyItemListModels(List<IdentifyItemListModel> identifyItemListModels) {
        this.identifyItemListModels = identifyItemListModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_show_identify_item, viewGroup, false);
        return new RowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        IdentifyItemListModel identifyItemListModel = identifyItemListModels.get(position);
        if (identifyItemListModel.getHeader().equals("Number of Floors")||identifyItemListModel.getHeader().equals("Structure Status")||
                identifyItemListModel.getHeader().equals("Work Area Name")){
            holder.binding.lay.setVisibility(View.VISIBLE);
            holder.binding.txtHeader.setText(String.format("%s", identifyItemListModel.getHeader()));
            holder.binding.txtValue.setText(String.format("%s", identifyItemListModel.getValues()));
        }else if (identifyItemListModel.getHeader().equals("created date")){
            holder.binding.txtHeader.setText(String.format("%s", "Collected Date"));
            holder.binding.txtValue.setText(String.format("%s", identifyItemListModel.getValues()));
        }else if (identifyItemListModel.getHeader().equals("Hut Number")){
            holder.binding.txtHeader.setText(String.format("%s", "Hut ID"));
            holder.binding.txtValue.setText(String.format("%s", identifyItemListModel.getValues()));
        }else {
            holder.binding.lay.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return (null != identifyItemListModels ? identifyItemListModels.size() : 0);
    }

    static class RowHolder extends RecyclerView.ViewHolder {
        ViewShowIdentifyItemBinding binding;

        RowHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }
}