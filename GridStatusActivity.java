package com.igenesys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
//
//import com.techaidsolution.gdc_app.App;
//import com.techaidsolution.gdc_app.R;
//import com.techaidsolution.gdc_app.database.LocalSurveyDbViewModel;
//import com.techaidsolution.gdc_app.databinding.ActivityChangeClusterStatusBinding;
//import com.techaidsolution.gdc_app.model.UpdateFeatureToLayer;
//import com.techaidsolution.gdc_app.model.UpdateWorkAreaStatusModel;
//import com.techaidsolution.gdc_app.model.UserModel;
//import com.techaidsolution.gdc_app.model.WorkAreaModel;
//import com.techaidsolution.gdc_app.networks.GetFormModel;
//import com.techaidsolution.gdc_app.networks.QueryResultRepoViewModel;
//import com.techaidsolution.gdc_app.ui.login.LoginActivity;
//import com.techaidsolution.gdc_app.utils.Constants;
//import com.techaidsolution.gdc_app.utils.Utils;

import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.databinding.ActivityGridStatusBinding;
import com.igenesys.model.UpdateFeatureToLayer;
import com.igenesys.model.UpdateWorkAreaStatusModel;
import com.igenesys.model.UserModel;
import com.igenesys.model.WorkAreaModel;
import com.igenesys.networks.GetFormModel;
import com.igenesys.networks.QueryResultRepoViewModel;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GridStatusActivity extends AppCompatActivity {

    ActivityGridStatusBinding binding;
    Activity activity;
    ArrayList<WorkAreaModel> workAreaList;
    WorkAreaModel selectedWorkArea;
    String statusType = "";
    LocalSurveyDbViewModel localSurveyDbViewModel;
    boolean isTotalCountZero = false;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGridStatusBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        activity = GridStatusActivity.this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        workAreaList = new ArrayList<>();

        binding.commonHeader.txtPageHeader.setText("Change Work Area Status");

        try {
            userModel = App.getInstance().getUserModel();
            if (userModel != null) {
                intiView();
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

    private void intiView() {

        try {

//        localSurveyDbViewModel = ViewModelProviders.of(this).get(LocalSurveyDbViewModel.class);
        localSurveyDbViewModel = new ViewModelProvider(this).get(LocalSurveyDbViewModel.class);
        binding.radioInProgress.setChecked(false);
        binding.radioHold.setChecked(false);
        binding.radioComplete.setChecked(false);
        binding.radioReOpen.setChecked(false);

        getWorkArea(userModel.getUser_name());

        binding.autoCompleteGridLayout.setOnItemClickListener((parent, view, i, l) -> {
            selectedWorkArea = (WorkAreaModel) parent.getAdapter().getItem(i);
            if (selectedWorkArea.getWork_area_status().equals(Constants.InProgress_statusLayer)) {
                binding.radioInProgress.setChecked(true);
                statusType = Constants.InProgress_statusLayer;
            } else if (selectedWorkArea.getWork_area_status().equals(Constants.OnHold_statusLayer)) {
                binding.radioHold.setChecked(true);
                statusType = Constants.OnHold_statusLayer;
            } else if (selectedWorkArea.getWork_area_status().equals(Constants.completed_statusLayer)) {
                binding.radioComplete.setChecked(true);
                statusType = Constants.completed_statusLayer;
            } else if (selectedWorkArea.getWork_area_status().equals(Constants.NotStarted_statusLayer)) {
                binding.radioReOpen.setChecked(true);
                statusType = Constants.NotStarted_statusLayer;
            }
        });

        binding.radioInProgress.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                statusType = Constants.InProgress_statusLayer;
            }
        });

        binding.radioHold.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                statusType = Constants.OnHold_statusLayer;
            }
        });

        binding.radioComplete.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                statusType = Constants.completed_statusLayer;
            }
        });

        binding.radioReOpen.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                statusType = Constants.NotStarted_statusLayer;
            }
        });

        binding.commonHeader.imgBack.setOnClickListener(view -> {
            finish();
        });

        binding.materialCVSubmit.setOnClickListener(view -> {
            if (Utils.isNullOrEmpty(binding.autoCompleteGridLayout.getText().toString())) {
                Utils.shortToast("Select Work Area name", activity);
                return;
            } else if (Utils.isNullOrEmpty(statusType)) {
                Utils.shortToast("Select status", activity);
                return;
            }

            if (statusType.equals(Constants.completed_statusLayer)) {
                getStructureStatus(selectedWorkArea.getWork_area_name());
            } else {
                updateWorkArea(statusType);
            }
        });

        binding.materialCVCancel.setOnClickListener(view -> {
            finish();
        });
    } catch (Exception exception) {
            AppLog.e(exception.getMessage());
    }
    }

    private void getStructureStatus(String workAreaName) {

        if (!Utils.checkinterne(activity))
            return;
        String structureWhereClause = "(work_area_name = '" + workAreaName + "') AND (structure_status <> 'Completed') AND (structure_status IS NOT NULL)";

        Utils.updateProgressMsg("Getting structure status, please wait", activity);
        try {

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(this).get(QueryResultRepoViewModel.class);

        queryResultRepoViewModel.getQueryCountResult(Constants.StructureInfo_MS_BASE_URL_ARC_GIS,
                Constants.StructureInfo_ENDPOINT,
                GetFormModel.getInstance().getCountQueryBuilderForm(structureWhereClause));

        queryResultRepoViewModel.getCountModelMutableLiveData().observe(this, resultQueryModel -> {
            Utils.dismissProgress();
            if (resultQueryModel != null) {
                try {
                    if (Integer.parseInt(resultQueryModel.getCount()) == 0)
                        isTotalCountZero = true;
                    else isTotalCountZero = false;
                } catch (Exception e) {
                    isTotalCountZero = false;
                }
            } else {
                isTotalCountZero = false;
            }

            if (validateWorkAreaStatus(selectedWorkArea)) {
                updateWorkArea(statusType);
            }

        });

    } catch (Exception exception) {
            AppLog.e(exception.getMessage());
        }
    }


    private boolean validateWorkAreaStatus(WorkAreaModel selectedWorkArea) {
        boolean localStructureCompl = true;
        String workAreaStatus = "";
        try {
            workAreaStatus = localSurveyDbViewModel.getWorkAreaStatus(selectedWorkArea.getWork_area_name(), userModel.getUser_name());
        } catch (Exception e) {
            AppLog.e(e.getMessage());
        }

        if (!Utils.isNullOrEmpty(workAreaStatus)) {
            localStructureCompl = workAreaStatus.equals(Constants.completed_statusLayer);
        }

        if (!localStructureCompl) {
            Utils.shortToast("Update the workarea status after completing all the structures within it.",activity);
           // Utils.shortToast("Local structure status is not " + Constants.completed + "", activity);
            return false;
        } else if (!isTotalCountZero) {
            Utils.shortToast("Update the workarea status after completing all the structures within it.",activity);
            //Utils.shortToast("All survey structure is not " + Constants.completed + "", activity);
            return false;
        } else {
            return true;
        }

    }

    private void getWorkArea(String userName) {
        if (!Utils.checkinterne(activity))
            return;

        Utils.updateProgressMsg("Getting Work Area Names, please wait", activity);
        try {

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(this).get(QueryResultRepoViewModel.class);

//        queryResultRepoViewModel.initWorkAreaQueryResult(Constants.WorkArea_BASE_URL_ARC_GIS.concat(Constants.MapServerEndPoint).concat(Constants.StructureInfo_ENDPOINT),
//                GetFormModel.getInstance().getQueryBuilderForm(
//                        String.format("user_name = '%s' AND (work_area_status = '%s' OR work_area_status = '%s' OR work_area_status = '%s')",
//                        userName, Constants.NotStarted, Constants.InProgress, Constants.OnHold), "*", true, "work_area_name", true, false));

        queryResultRepoViewModel.getQueryResult(Constants.WorkArea_MS_BASE_URL_ARC_GIS,
                Constants.WorkArea_ENDPOINT,
                GetFormModel.getInstance().getQueryBuilderForm(
                        String.format("user_name = '%s' AND (work_area_status = '%s' OR work_area_status = '%s' OR work_area_status = '%s' OR work_area_status = '%s')",
                                userName, Constants.NotStarted, Constants.InProgress, Constants.OnHold_statusLayer, Constants.completed), "*", false, "work_area_name", true, true));

        workAreaList = new ArrayList<>();
        queryResultRepoViewModel.getMutableLiveData().observe(this, resultQueryModel -> {
            Utils.dismissProgress();
            if (resultQueryModel != null) {
                if (resultQueryModel.getFeatures().size() > 0) {
                    for (int i = 0; i < resultQueryModel.getFeatures().size(); i++) {
                        Map<String, Object> map = (Map<String, Object>) resultQueryModel.getFeatures().get(i);
                        Map<String, Object> mapAttributeValue = (Map<String, Object>) map.get("attributes");
                        Map<String, Object> mapGeometryValue = (Map<String, Object>) map.get("geometry");
                        if (mapAttributeValue != null) {

                            try {

                                if (mapAttributeValue.containsKey(Constants.WorkArea_work_survey_start_date) &&
                                        mapAttributeValue.containsKey(Constants.WorkArea_work_survey_end_date) &&
                                        !Utils.getString(mapAttributeValue.get(Constants.WorkArea_work_survey_start_date)).isEmpty() &&
                                        !Utils.getString(mapAttributeValue.get(Constants.WorkArea_work_survey_end_date)).isEmpty()) {

                                    WorkAreaModel workAreaModel = new WorkAreaModel(
                                            Utils.getString(mapAttributeValue.get(Constants.objectid)),
                                            Utils.getString(mapAttributeValue.get(Constants.WorkArea_work_area_name)),
                                            Utils.getString(mapAttributeValue.get(Constants.WorkArea_work_area_status)),
                                            Utils.getString(mapAttributeValue.get(Constants.WorkArea_work_user_name)),
                                            Utils.getString(mapAttributeValue.get(Constants.globalid)),
                                            Utils.convertDateToString(mapAttributeValue.get(Constants.WorkArea_work_last_edited_date)),
                                            Utils.getGson().toJson(mapGeometryValue),
                                            Utils.convertExponentialToDate(Utils.getString(mapAttributeValue.get(Constants.WorkArea_work_survey_start_date))),
                                            Utils.convertExponentialToDate(Utils.getString(mapAttributeValue.get(Constants.WorkArea_work_survey_end_date)))
                                    );

                                    if (workAreaModel.getWork_area_name().equalsIgnoreCase(App.getSharedPreferencesHandler().getString(Constants.workAreaName, ""))) {
                                        workAreaList.add(workAreaModel);
                                    }
                                }

                            } catch (Exception e) {
                                AppLog.e(e.getMessage());
                            }
                        }
                    }
//                    binding.autoCompleteGridLayout.setText(App.getSharedPreferencesHandler().getString(Constants.workAreaName, ""));

                    if (workAreaList != null && !workAreaList.isEmpty()) {
                        App.getInstance().setWorkAreaModels(workAreaList);
                        binding.autoCompleteGridLayout.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, workAreaList));
                    }

                }
            }
        });
        } catch (Exception exception) {
            AppLog.e(exception.getMessage());
        }
    }

    private void updateWorkArea(String status) {

        try {
            UpdateWorkAreaStatusModel updateWorkAreaStatusModel = new UpdateWorkAreaStatusModel();

            updateWorkAreaStatusModel.setWork_area_status(status);
            updateWorkAreaStatusModel.setObjectid((int) Double.parseDouble(selectedWorkArea.getObjectid()));
            selectedWorkArea.setWork_area_status(status);

            UpdateFeatureToLayer.UpdateForm updateForm = new UpdateFeatureToLayer.UpdateForm();
            updateForm.setAttributes(updateWorkAreaStatusModel);

            Utils.updateProgressMsg("Updating work area status.", activity);
            List<UpdateFeatureToLayer.UpdateForm> array = new ArrayList<>();
            array.add(updateForm);

            QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(this).get(QueryResultRepoViewModel.class);
            queryResultRepoViewModel.initUpdateFeatureResult(Constants.WorkArea_FS_BASE_URL_ARC_GIS, Constants.WorkArea_ENDPOINT,
                    GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

            queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(this, addedRecordResponse -> {

                Utils.dismissProgress();
                if (addedRecordResponse != null) {
                    Utils.shortToast("Status updated successfully", activity);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.INTENT_SelectedChangWorkArea, selectedWorkArea);
                    activity.setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Utils.shortToast("Status not updated successfully", activity);
                }
            });
        } catch (Exception e) {
            AppLog.e(e.getMessage());
        }
    }
}