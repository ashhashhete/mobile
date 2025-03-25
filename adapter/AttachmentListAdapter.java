package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

//import com.techaidsolution.gdc_app.R;
//import com.techaidsolution.gdc_app.databinding.RvListItemAttachmentNameBinding;
//import com.techaidsolution.gdc_app.model.AttachmentListImageDetails;

import com.igenesys.R;
import com.igenesys.databinding.RvListItemAttachmentNameBinding;
import com.igenesys.model.AttachmentListImageDetails;
//import com.techaidsolution.igenesys.databinding.RvListItemAttachmentNameBinding;

import java.util.ArrayList;

public class AttachmentListAdapter extends RecyclerView.Adapter {

    private OnAttachmentItemClickListner onAttachmentItemClickListner;
    private Activity activity;
    private ArrayList<AttachmentListImageDetails> attachmentListImageDetails;
    private String attachmentType;

    public AttachmentListAdapter(Activity activity, ArrayList<AttachmentListImageDetails> attachmentListImageDetails, String attachmentType, OnAttachmentItemClickListner onAttachmentItemClickListner) {
        this.activity = activity;
        this.attachmentListImageDetails = attachmentListImageDetails;
        this.attachmentType = attachmentType;
        this.onAttachmentItemClickListner = onAttachmentItemClickListner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AttachmentNameVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_item_attachment_name, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AttachmentListImageDetails attachmentListImageDetails1 = attachmentListImageDetails.get(position);

        ((AttachmentNameVH) holder).binding.txtElectricityConnectionImage.setText(attachmentListImageDetails1.getFileName());

        ((AttachmentNameVH) holder).binding.txtElectricityConnectionImage.setOnClickListener(view -> {
            onAttachmentItemClickListner.onAttachmentNameTextClicked(attachmentListImageDetails1);
        });

        if (attachmentListImageDetails1.getFilePath().startsWith("https:/")) {
            ((AttachmentNameVH) holder).binding.ivCloseElectricityConnection.setVisibility(View.GONE);
        }

        ((AttachmentNameVH) holder).binding.ivCloseElectricityConnection.setOnClickListener(view -> {
            onAttachmentItemClickListner.onAttachmentCancelBtnClicked(attachmentType, attachmentListImageDetails1);
        });
    }

    @Override
    public int getItemCount() {
        return attachmentListImageDetails == null ? 0 : attachmentListImageDetails.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAttachmentListImageDetails(ArrayList<AttachmentListImageDetails> attachmentListImageDetails) {
        this.attachmentListImageDetails = attachmentListImageDetails;
        notifyDataSetChanged();
    }


    public interface OnAttachmentItemClickListner {
        void onAttachmentNameTextClicked(AttachmentListImageDetails attachmentListImageDetails);

        void onAttachmentCancelBtnClicked(String attachmentType, AttachmentListImageDetails attachmentListImageDetails);
    }

    static class AttachmentNameVH extends RecyclerView.ViewHolder {

        RvListItemAttachmentNameBinding binding;

        public AttachmentNameVH(@NonNull View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }

}
