package com.igenesys.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.igenesys.CrashReportActivity;
import com.igenesys.adapter.CrashReportListAdapter;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.DownloadedWebMapModel;
//import com.igenesys.databinding.ActivityDownloadOfflineWebmapBinding;
import com.igenesys.databinding.ActivityCrashReportBinding;
import com.igenesys.model.CrashReportsModel;
import com.igenesys.model.UserModel;
//import com.igenesys.ShowFullScreenAttachment;
import com.igenesys.adapter.DownloadListAdapter;
//import com.igenesys.DownloadOfflineWebmapActivity;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Utils;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CrashReportViewModel extends ActivityViewModel<CrashReportActivity> implements CrashReportListAdapter.OnItemClickListner {

    List<CrashReportsModel> crashReportsModelList;
    ActivityCrashReportBinding binding;
    Activity activity;
    CrashReportListAdapter crashReportListAdapter;
    CrashReportsModel crashReportsModel;

    public CrashReportViewModel(CrashReportActivity activity) {
        super(activity);
        this.activity = activity;
        binding = activity.getBinding();

        initView();
    }

    public static String readFromFile(String filePath) {
        StringBuilder text = new StringBuilder();
        String result = "Unable to get details.";
        try {
            File myFile = new File(filePath);//changed scope
            BufferedReader br = new BufferedReader(new FileReader(myFile));
            String line;
            while ((line = br.readLine()) != null) {
                text.append("\n");
                text.append(line);
                text.append("\n");
            }
            br.close();
            result = text.toString();
        } catch (Exception ignored) {
            AppLog.e(ignored.getMessage());
        }

        return result;
    }

    private void initView() {
        crashReportsModelList = new ArrayList<>();


        binding.commonHeader.txtPageHeader.setText("Crash Reports");
        binding.commonHeader.imgBack.setOnClickListener(view -> finish());

        crashReportListAdapter = new CrashReportListAdapter(activity, null, this);
        binding.rvCrashReportList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.rvCrashReportList.setAdapter(crashReportListAdapter);
        Utils.updateProgressMsg("Getting the crash reports list. Please wait...", activity);
        refreshList();
        binding.btnCrashMe.setVisibility(View.GONE);
        binding.btnCrashMe.setOnClickListener(view -> {
            String helloCrashed = null;
            int a = helloCrashed.length();
            AppLog.e(a + "");
        });
    }

    @Override
    public void onItemClicked(CrashReportsModel crashReportsModel) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.view_alert_crash_details, null);
        alertDialog.setView(customLayout);

        TextView txt_name1 = (TextView) customLayout.findViewById(R.id.txt_name);

        TextView txt_crashDetails1 = (TextView) customLayout.findViewById(R.id.txt_crashDetails);

        txt_crashDetails1.setText(readFromFile(crashReportsModel.getPath()));
        txt_name1.setText(crashReportsModel.getName());

        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            dialog.cancel();
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();

//        FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
//        ShowFullScreenCrashDetails showFullScreenCrashDetails = ShowFullScreenCrashDetails.newInstance(crashReportsModel.getName(), readFromFile(crashReportsModel.getPath()));
//        showFullScreenCrashDetails.show(fm, "");

    }

    private void refreshList() {
        crashReportsModelList = new ArrayList<>();

        File directory = new File(activity.getExternalFilesDir("") + "/Crash_Reports");

        if (directory.exists()) {
            try {

            File[] files = directory.listFiles();

            if (files != null) {
                    Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

                for (File file : files) {
                    Date newDate = new Date(file.lastModified());

                    if (file.getName().contains(".txt")) {
                            crashReportsModelList.add(new CrashReportsModel(file.getName(), file.getAbsolutePath(), Utils.convertDateToStringSurveyListFormat(newDate)));

                        }
                }
            }
            } catch (Exception exception) {
                AppLog.e(exception.getMessage());
            }

            crashReportListAdapter.setCrashReportsModelList(crashReportsModelList);
            if (crashReportsModelList.size() == 0)
                Utils.showMessagePopup("No crash report found.", activity);
        } else {
            Utils.showMessagePopup("No crash report found.", activity);
        }
        Utils.dismissProgress();
    }

}
