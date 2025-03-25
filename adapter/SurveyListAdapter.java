package com.igenesys.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.SummaryActivity;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.databinding.RvItemSurveylistBinding;

//import com.igenesys.binding.SummaryPage;

import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;

import java.util.List;

public class SurveyListAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private List<UnitInfoDataModel> unitInfoDataModel;
    private LocalSurveyDbViewModel localSurveyDbViewModel;
    private OnItemClickListner onItemClickListner;

    public SurveyListAdapter(Activity activity,LocalSurveyDbViewModel localSurveyDbViewModel, List<UnitInfoDataModel> unitInfoDataModel, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.unitInfoDataModel = unitInfoDataModel;
        this.onItemClickListner = onItemClickListner;
        this.localSurveyDbViewModel=localSurveyDbViewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SummaryChildVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_surveylist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UnitInfoDataModel summaryItemModel = unitInfoDataModel.get(position);


        ((SummaryChildVH) holder).binding.txtUnitId.setText(summaryItemModel.getUnit_unique_id());
        ((SummaryChildVH) holder).binding.txtWorkArea.setText(localSurveyDbViewModel.getWorkAreaNameByStructureId(summaryItemModel.getUnitSampleGlobalid()));
        if(summaryItemModel.getHut_number()==null){
            ((SummaryChildVH) holder).binding.txtStructureId.setText("N/A");
        }else{
            ((SummaryChildVH) holder).binding.txtStructureId.setText(summaryItemModel.getHut_number());
        }
        ((SummaryChildVH) holder).binding.txtStructureId.setText(summaryItemModel.getHut_number());
        if(summaryItemModel.getLastEditedDate()==null){
            ((SummaryChildVH) holder).binding.txtLastEditDate.setText("Last modified: N/A");

        }else{
            ((SummaryChildVH) holder).binding.txtLastEditDate.setText("Last modified: " + Utils.convertDateToStringSurveyListFormat(summaryItemModel.getLast_edited_date()));

        }
        ((SummaryChildVH) holder).binding.txtVisitCount.setText(""+summaryItemModel.getVisit_count());


        String status = "N/A";
        int imgInt = R.color.main_color;

        if (summaryItemModel.getUnit_status().equals(Constants.InProgress_statusLayer)) {
            status = Constants.In_Progress;
//            imgInt = R.color.inProgressBoarderColor;
            imgInt = R.color.status_dark_blue;
        } else if (summaryItemModel.getUnit_status().equals(Constants.OnHold_statusLayer)) {
            status = Constants.On_Hold;
            imgInt = R.color.onHoldBoarderColor;
        } else if (summaryItemModel.getUnit_status().equals(Constants.NotStarted_statusLayer)) {
            status = Constants.Not_Started;
            imgInt = R.color.notStartedBoarderColor;
        } else if (summaryItemModel.getUnit_status().equals(Constants.completed_statusLayer)) {
            status = Constants.completed;
            imgInt = R.color.completeBoarderColor;
        } else if (summaryItemModel.getUnit_status().equals(Constants.completed_dispute)) {
            status = Constants.dispute;
            imgInt = R.color.lighter_red;
        }

        ((SummaryChildVH) holder).binding.txtProgress.setText(status);
        ((SummaryChildVH) holder).binding.imgProgress.setBackgroundTintList(ContextCompat.getColorStateList(activity, imgInt));

        ((SummaryChildVH) holder).binding.txtSurveyDate.setText(Utils.convertDateToStringSurveyListFormat(summaryItemModel.getDate()));

        ((SummaryChildVH) holder).binding.layoutCarvView.setOnClickListener(view -> {

            App.getSharedPreferencesHandler().putString(Constants.viewMode, Constants.offline);

            // #1339 - Resolved (Ajay)
            /*App.getSharedPreferencesHandler().putString(Constants.uniqueId, summaryItemModel.getHut_number());
            activity.startActivity(new Intent(activity, SummaryActivity.class)
                    .putExtra(Constants.uniqueId, summaryItemModel.getHut_number()));*/

            App.getSharedPreferencesHandler().putString(Constants.uniqueId, summaryItemModel.getUnitSampleGlobalid());
            activity.startActivity(new Intent(activity, SummaryActivity.class)
                    .putExtra(Constants.uniqueId, summaryItemModel.getUnitSampleGlobalid()));
        });

        ((SummaryChildVH) holder).binding.cvUpload.setOnClickListener(view -> {
            onItemClickListner.onUploadBtnClicked(summaryItemModel);
        });

        ((SummaryChildVH) holder).binding.cvEdit.setOnClickListener(view -> {

            App.getSharedPreferencesHandler().putString(Constants.uniqueId, summaryItemModel.getUnitSampleGlobalid());
            App.getSharedPreferencesHandler().putString(Constants.viewMode, Constants.offline);
            App.getSharedPreferencesHandler().putString("local","local");
            activity.startActivity(new Intent(activity, SummaryActivity.class)
                    .putExtra(Constants.uniqueId, summaryItemModel.getUnitSampleGlobalid()));

        });

        ((SummaryChildVH) holder).binding.cvDelete.setOnClickListener(view -> {
            onItemClickListner.onDeleteBtnClicked(summaryItemModel);

        });
    }

    @Override
    public int getItemCount() {
        return unitInfoDataModel == null ? 0 : unitInfoDataModel.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSummaryItemModels(List<UnitInfoDataModel> unitInfoDataModel) {
        this.unitInfoDataModel = unitInfoDataModel;
        notifyDataSetChanged();
    }


    public interface OnItemClickListner {
        void onEditBtnClicked(UnitInfoDataModel unitInfoDataModel);

        void onUploadBtnClicked(UnitInfoDataModel unitInfoDataModel);

        void onDeleteBtnClicked(UnitInfoDataModel unitInfoDataModel);
    }

    static class SummaryChildVH extends RecyclerView.ViewHolder {

        RvItemSurveylistBinding binding;

        public SummaryChildVH(@NonNull View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }


}
