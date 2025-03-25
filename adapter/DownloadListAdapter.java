package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.App;
import com.igenesys.DashboardMapActivity;
import com.igenesys.R;
import com.igenesys.database.dabaseModel.newDbSModels.DownloadedWebMapModel;
import com.igenesys.databinding.RvItemDownloadedWebmapBinding;

import com.igenesys.model.UserModel;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;

import com.igenesys.DashboardActivity;
import com.igenesys.database.dabaseModel.newDbSModels.DownloadedWebMapModel;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;

import java.util.List;

public class DownloadListAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private List<DownloadedWebMapModel> downloadedWebMapModels;

    private OnItemClickListner onItemClickListner;


    public DownloadListAdapter(Activity activity, List<DownloadedWebMapModel> downloadedWebMapModels, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.downloadedWebMapModels = downloadedWebMapModels;
        this.onItemClickListner = onItemClickListner;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SummaryChildVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_downloaded_webmap, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DownloadedWebMapModel summaryItemModel = downloadedWebMapModels.get(position);

        ((SummaryChildVH) holder).binding.txtWorkAreaName.setText(summaryItemModel.getWorkAreaName());
        ((SummaryChildVH) holder).binding.txtZoomLevel.setText("Zoom Level: " + summaryItemModel.getZoomLevel());
        ((SummaryChildVH) holder).binding.txtLastUpdatedDate.setText("Last Updated: " + Utils.convertDateToStringDateOnlyFormat(summaryItemModel.getCreatedOn()));

        ((SummaryChildVH) holder).binding.layoutCarvView.setOnClickListener(view -> {
//            App.getSharedPreferencesHandler().putString(Constants.uniqueId, summaryItemModel.getStructure_id());
//            App.getSharedPreferencesHandler().putString(Constants.viewMode, Constants.offline);
            activity.startActivity(new Intent(activity, DashboardMapActivity.class)
                    .putExtra(Constants.IntentComingFrom, "download")
                    .putExtra(Constants.DownloadedWebMapModel, summaryItemModel));



        });

        ((SummaryChildVH) holder).binding.cvDelete.setOnClickListener(view -> {
            onItemClickListner.onDeleteBtnClicked(summaryItemModel);
        });
    }

    @Override
    public int getItemCount() {
        return downloadedWebMapModels == null ? 0 : downloadedWebMapModels.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDownloadedWebMapModels(List<DownloadedWebMapModel> downloadedWebMapModels) {
        this.downloadedWebMapModels = downloadedWebMapModels;
        notifyDataSetChanged();
    }


    public interface OnItemClickListner {
        void onItemClicked(DownloadedWebMapModel downloadedWebMapModel);

        void onDeleteBtnClicked(DownloadedWebMapModel downloadedWebMapModel);
    }

    static class SummaryChildVH extends RecyclerView.ViewHolder {

        RvItemDownloadedWebmapBinding binding;

        public SummaryChildVH(@NonNull View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }

}
