package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.R;
import com.igenesys.databinding.RvItemCrashreportBinding;
import com.igenesys.model.CrashReportsModel;

import com.igenesys.model.CrashReportsModel;

import java.util.List;

public class CrashReportListAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private List<CrashReportsModel> crashReportsModelList;

    private OnItemClickListner onItemClickListner;

    public CrashReportListAdapter(Activity activity, List<CrashReportsModel> crashReportsModelList, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.crashReportsModelList = crashReportsModelList;
        this.onItemClickListner = onItemClickListner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SummaryChildVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_crashreport, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CrashReportsModel summaryItemModel = crashReportsModelList.get(position);

        ((SummaryChildVH) holder).binding.txtFileName.setText(summaryItemModel.getName());
        ((SummaryChildVH) holder).binding.txtLastUpdatedDate.setText(summaryItemModel.getLastModifiedOn());
        ((SummaryChildVH) holder).binding.layoutCarvView.setOnClickListener(view -> {
            onItemClickListner.onItemClicked(summaryItemModel);
        });

    }

    @Override
    public int getItemCount() {
        return crashReportsModelList == null ? 0 : crashReportsModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCrashReportsModelList(List<CrashReportsModel> crashReportsModelList) {
        this.crashReportsModelList = crashReportsModelList;
        notifyDataSetChanged();
    }


    public interface OnItemClickListner {
        void onItemClicked(CrashReportsModel downloadedWebMapModel);

    }

    static class SummaryChildVH extends RecyclerView.ViewHolder {

        RvItemCrashreportBinding binding;

        public SummaryChildVH(@NonNull View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }

}
