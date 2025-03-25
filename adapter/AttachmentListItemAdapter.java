package com.igenesys.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.databinding.RvItemAttachmentItemBinding;

import java.io.File;
import java.util.ArrayList;

public class AttachmentListItemAdapter extends RecyclerView.Adapter<AttachmentListItemAdapter.ChildViewHolder> {

    private ArrayList<MediaInfoDataModel> mediaInfoDataModels;

    private OnItemClickListner onItemClickListner;

    // Constructor
    AttachmentListItemAdapter(ArrayList<MediaInfoDataModel> mediaInfoDataModels, OnItemClickListner onItemClickListner) {
        this.mediaInfoDataModels = mediaInfoDataModels;
        this.onItemClickListner = onItemClickListner;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // Here we inflate the corresponding
        // layout of the child item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_item_attachment_item, viewGroup, false);

        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder childViewHolder, int position) {

        // Create an instance of the ChildItem
        // class for the given position
        MediaInfoDataModel childItem = mediaInfoDataModels.get(position);

        File file = new File(childItem.getFilename());

        Log.d("nameFile", file.getName());
        Log.d("urlFile", childItem.getItem_url());

        childViewHolder.binding.txtview.setText(file.getName());
        childViewHolder.binding.txtview.setPaintFlags(childViewHolder.binding.txtview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        childViewHolder.binding.txtview.setTextColor(Color.parseColor("#367CFF"));
        childViewHolder.binding.txtview.setOnClickListener(view -> onItemClickListner.onTextClick(childItem));
    }

    @Override
    public int getItemCount() {
        return mediaInfoDataModels.size();
    }

    public interface OnItemClickListner {

        void onTextClick(MediaInfoDataModel mediaInfoDataModel);
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {

        RvItemAttachmentItemBinding binding;

        ChildViewHolder(View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }
}