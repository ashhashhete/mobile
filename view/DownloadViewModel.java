package com.igenesys.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.igenesys.DownloadActivity;
import com.igenesys.adapter.DownloadListAdapter;
//import com.igenesys.databinding.ActivityDownloadBinding;
import com.igenesys.databinding.ActivityDownloadBinding;
import com.igenesys.utils.AppLog;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.DownloadedWebMapModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
//import com.igenesys.databinding.ActivityDownloadOfflineWebmapBinding;
import com.igenesys.databinding.ActivitySurveyLocalListBinding;
import com.igenesys.model.UserModel;
import com.igenesys.LoginActivity;
//import com.igenesys.SurveyListAdapter;
//import com.igenesys.SurveyLocalList;
import com.igenesys.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;

public class DownloadViewModel extends ActivityViewModel<DownloadActivity> implements DownloadListAdapter.OnItemClickListner {

    List<DownloadedWebMapModel> downloadedWebMapModelds;
    ActivityDownloadBinding binding;
    Activity activity;
    LocalSurveyDbViewModel localSurveyDbViewModel;

    UserModel userModel;
    DownloadListAdapter downloadListAdapter;
    DownloadedWebMapModel downloadedWebMapModel;

    public DownloadViewModel(DownloadActivity activity) {
        super(activity);
        this.activity = activity;
        binding = activity.getBinding();
//        localSurveyDbViewModel = ViewModelProviders.of(getActivity()).get(LocalSurveyDbViewModel.class);

        try {
            localSurveyDbViewModel = new ViewModelProvider(getActivity()).get(LocalSurveyDbViewModel.class);
            userModel = App.getInstance().getUserModel();
            if (userModel != null) {
                initView();
            } else {
                Utils.shortToast("User details not found. Kindly login again.", activity);
                activity.finishAffinity();
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        } catch (Exception e) {
            Utils.shortToast("User details not found. Kindly login again.", activity);
            activity.finishAffinity();
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }
    }

    private void initView() {
        try {
            downloadedWebMapModelds = new ArrayList<>();

            binding.commonHeader.txtPageHeader.setText("Download offline map");
            binding.commonHeader.imgBack.setOnClickListener(view -> finish());

            downloadListAdapter = new DownloadListAdapter(activity, null, this);
            binding.rvSurveyList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            binding.rvSurveyList.setAdapter(downloadListAdapter);
            Utils.updateProgressMsg("Getting the previous download list. Please wait...", activity);
            refreshList();
        }catch(Exception ex){
            AppLog.e("Exception in Init Download View:"+ex.getMessage());
        }
    }

    @Override
    public void onItemClicked(DownloadedWebMapModel downloadedWebMapModel) {
//        this.downloadedWebMapModel = downloadedWebMapModel;
//        showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete this record?",
//                "Yes", "No", false);

    }

    @Override
    public void onDeleteBtnClicked(DownloadedWebMapModel downloadedWebMapModel) {
        this.downloadedWebMapModel = downloadedWebMapModel;
        showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete this record?",
                "Yes", "No", false);
    }

    public void showActionAlertDialogButtonClicked(String header, String mssage, String btnYes, String btnNo, boolean toUplaod) {

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_action, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        TextView txt_mssage = customLayout.findViewById(R.id.txt_mssage);

        txt_header.setText(header);
        txt_mssage.setText(mssage);

        TextView txt_yes = customLayout.findViewById(R.id.txt_yes);
        TextView txt_no = customLayout.findViewById(R.id.txt_no);

        txt_yes.setText(btnYes);
        txt_no.setText(btnNo);

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);
        ImageView img_close = customLayout.findViewById(R.id.img_close);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);

        statusRadioGroup.setVisibility(View.GONE);

        btn_yes.setOnClickListener(view1 -> {

            deleteWorkAreaWebmapInfo();
            dialog.dismiss();
        });

        btn_no.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        img_close.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void deleteWorkAreaWebmapInfo() {
        try {
            Utils.updateProgressMsg("Deleting the previous download. Please wait...", activity);
            new Handler().postDelayed(() -> {
                Utils.deleteDirectory(new File(downloadedWebMapModel.getPathAddress()));
                localSurveyDbViewModel.deleteWebMapInfoData(downloadedWebMapModel.getWorkAreaName(), activity);
                Utils.updateProgressMsg("Getting the previous download list. Please wait...", activity);
                refreshList();
            }, 2000);
        }catch(Exception ex){
            AppLog.e("Exception:"+ex.getMessage());
        }
    }

    private void refreshList() {
        try{
            downloadedWebMapModelds = localSurveyDbViewModel.getDownloadedMapInfoData(userModel.getUser_name());
            downloadListAdapter.setDownloadedWebMapModels(downloadedWebMapModelds);

            if (downloadedWebMapModelds.size() == 0) {
                Utils.showMessagePopup("It appears that no base maps were downloaded for offline use.", activity);
            }

            Utils.dismissProgress();
        }catch(Exception ex){
            AppLog.e(""+ex.getMessage());
        }
    }

}
