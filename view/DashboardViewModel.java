package com.igenesys.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.igenesys.DashboardActivity;
import com.igenesys.utils.AppLog;
import com.igenesys.DashboardMapActivity;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.databinding.ActivityDashboardBinding;
import com.igenesys.model.UserModel;
import com.igenesys.model.WorkAreaModel;
import com.igenesys.networks.GetFormModel;
import com.igenesys.networks.QueryResultRepoViewModel;
import com.igenesys.model.DahboardListModel;
import com.igenesys.adapter.DashboardRvListAdapter;
//import com.igenesys.DashboardMapActivity;
import com.igenesys.LoginActivity;
import com.igenesys.utils.AppPermissions;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DashboardViewModel extends ActivityViewModel<DashboardActivity> {

    Activity activity;
    ActivityDashboardBinding binding;
    int inProgressCount = 0, completeCount = 0, notStartedCount = 0, onHoldCount = 0, totalCount = 0;
    WorkAreaModel selectedWorkArea;
    ArrayList<WorkAreaModel> workAreaList;
    ArrayList<String> nameList;
    boolean dateSelected = false, clusterSelected = false;
    String dateWhere = "", clusterWhere = "";
    String selectedFilter = "ALL";
    boolean progressClicked = false, completedClicked = false, notStartedClicked = false, holdClicked = false;
    UserModel userModel;
    private List<DahboardListModel> dahboardListModels;
    private DashboardRvListAdapter dashboardRvListAdapter;
    private int currentPage = 0, totalPage = 0;

    public DashboardViewModel(DashboardActivity activity) {
        super(activity);
        this.activity = activity;
        this.binding = activity.getBinding();

        try {
            userModel = App.getInstance().getUserModel();
            if (userModel != null) {
                initView();
            } else {
                Utils.shortToast("User details not found. Kindly login again.", activity);
                activity.finishAffinity();
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        } catch (Exception e) {
            AppLog.logData(activity,e.getMessage());
            Utils.shortToast("User details not found. Kindly login again.", activity);
            activity.finishAffinity();
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }

    }

    private void initView() {

        binding.commonHeader.txtPageHeader.setText("Dashboard");
        binding.commonHeader.imgBack.setOnClickListener(view -> activity.finish());
        workAreaList = new ArrayList<>();

        dashboardRvListAdapter = new DashboardRvListAdapter(dahboardListModels, activity);
        binding.rvFilterList.setVisibility(View.VISIBLE);
        binding.rvFilterList.setAdapter(dashboardRvListAdapter);
        setupList(userModel.getUser_name());
        selectDate();
        setupListner();
    }

    private void setCardWidth() {
        binding.cvInProgress.setStrokeWidth(0);
        binding.cvCompleted.setStrokeWidth(0);
        binding.cvNotStarted.setStrokeWidth(0);
        binding.cvOnHold.setStrokeWidth(0);

        progressClicked = false;
        completedClicked = false;
        notStartedClicked = false;
        holdClicked = false;
    }

    private void setupListner() {
        binding.cvInProgress.setOnClickListener(view -> {
            if (!progressClicked) {
                setCardWidth();
                progressClicked = true;
                binding.cvInProgress.setStrokeWidth(3);
                dashboardRvListAdapter.getFilter().filter(Constants.In_Progress);
                selectedFilter = Constants.In_Progress;

//              dashboardRvListAdapter.getFilter().filter(Constants.In_Progress);
            } else {
                setCardWidth();
                progressClicked = false;
                selectedFilter = "ALL";
                dashboardRvListAdapter.getFilter().filter("");
            }

        });

        binding.cvCompleted.setOnClickListener(view -> {
            if (!completedClicked) {
                setCardWidth();
                completedClicked = true;
                binding.cvCompleted.setStrokeWidth(3);
                dashboardRvListAdapter.getFilter().filter(Constants.completed);
                selectedFilter = Constants.completed;
            } else {
                setCardWidth();
                completedClicked = false;
                selectedFilter = "ALL";
                dashboardRvListAdapter.getFilter().filter("");
            }
        });

        binding.cvNotStarted.setOnClickListener(view -> {
            if (!notStartedClicked) {
                setCardWidth();
                notStartedClicked = true;
                binding.cvNotStarted.setStrokeWidth(3);
                dashboardRvListAdapter.getFilter().filter(Constants.Not_Started);
                selectedFilter = Constants.Not_Started;
            } else {
                setCardWidth();
                notStartedClicked = false;
                selectedFilter = "ALL";
                dashboardRvListAdapter.getFilter().filter("");
            }
        });

        binding.cvOnHold.setOnClickListener(view -> {
            if (!holdClicked) {
                setCardWidth();
                holdClicked = true;
                binding.cvOnHold.setStrokeWidth(3);
                dashboardRvListAdapter.getFilter().filter(Constants.On_Hold);
                selectedFilter = Constants.On_Hold;
            } else {
                setCardWidth();
                holdClicked = false;
                selectedFilter = "ALL";
                dashboardRvListAdapter.getFilter().filter("");
            }
        });

        binding.autoCompleteClusterWorkArea.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.autoCompleteClusterWorkArea.showDropDown();

        });

        binding.autoCompleteClusterWorkArea.setOnTouchListener((v, event) -> {
            binding.autoCompleteClusterWorkArea.showDropDown();
            return false;
        });

        binding.autoCompleteClusterWorkArea.setOnItemClickListener((parent, view, i, l) -> {

            if (!parent.getAdapter().getItem(i).toString().equalsIgnoreCase("All Work Area")) {
                selectedWorkArea = workAreaList.get(i - 1);
                clusterSelected = true;
                binding.txtRvHeader.setText("List of Surveys");
                binding.imgvIViewOnMap.setVisibility(View.VISIBLE);
            } else {
                binding.txtRvHeader.setText("List of Work Area");
                binding.imgvIViewOnMap.setVisibility(View.GONE);
                clusterSelected = false;
            }

            setupPreviousClusterRecord(getWhereClause());
        });

        binding.imgvIViewOnMap.setOnClickListener(view -> {
            if (!Utils.checkinterne(activity))
                return;

            totalCount = notStartedCount + completeCount + onHoldCount + inProgressCount;
            if (totalCount == 0) {
                Utils.shortToast("No record is available to view on the map.", activity);
                return;
            }
            if (!AppPermissions.checkGpsOn(activity)) {
                Utils.shortToast("GPS is disabled in your device or it may not be on 'High Accuracy'. Please enable it.", activity);
                return;
            }


            activity.startActivity(new Intent(activity, DashboardMapActivity.class)
                    .putExtra(Constants.IntentComingFrom, "dashboard")
                    .putExtra(Constants.INTENT_SelectedWhereClause, getWhereClause())
                    .putExtra(Constants.INTENT_SelectedWorkArea, selectedWorkArea)
                    .putExtra(Constants.INTENT_SelectedFilter, selectedFilter)
                    .putExtra(Constants.INTENT_SelectedWorkAreaNsCount, notStartedCount)
                    .putExtra(Constants.INTENT_SelectedWorkAreaIpCount, inProgressCount)
                    .putExtra(Constants.INTENT_SelectedWorkAreaC_Count, completeCount)
                    .putExtra(Constants.INTENT_SelectedWorkAreaOhCount, onHoldCount)
                    .putExtra(Constants.selectedCluster, binding.autoCompleteClusterWorkArea.getText().toString()));

        });
    }

    private void setupList(String userName) {

        if (!Utils.checkinterne(activity))
            return;

        nameList = new ArrayList<>();
        nameList.add("All Work Area");
        dahboardListModels = new ArrayList<>();

        Utils.updateProgressMsg("Getting work area names, please wait", activity);
        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);

        queryResultRepoViewModel.getQueryResult(Constants.WorkArea_MS_BASE_URL_ARC_GIS, Constants.WorkArea_ENDPOINT,
                GetFormModel.getInstance().getQueryBuilderForm(
                        String.format("user_name = '%s' AND (work_area_status = '%s' OR work_area_status = '%s' OR work_area_status = '%s' OR work_area_status = '%s')",
                                userName, Constants.NotStarted, Constants.InProgress, Constants.OnHold_statusLayer, Constants.completed, Constants.dispute),
                        "*", true, "work_area_name", true, false));

        workAreaList = new ArrayList<>();

        queryResultRepoViewModel.getMutableLiveData().observe(getActivity(), resultQueryModel -> {

            Utils.dismissProgress();

            if (resultQueryModel != null && !resultQueryModel.getFeatures().isEmpty()) {

                for (int i = 0; i < resultQueryModel.getFeatures().size(); i++) {

                    try {

                        Map<String, Object> map = (Map<String, Object>) resultQueryModel.getFeatures().get(i);
                        Map<String, Object> mapAttributeValue = (Map<String, Object>) map.get("attributes");
                        Map<String, Object> mapGeometryValue = (Map<String, Object>) map.get("geometry");

                        if (mapAttributeValue != null) {

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

                                workAreaList.add(workAreaModel);

                                String status = "";
                                int color = R.color.main_color;
                                nameList.add(workAreaModel.getWork_area_name());
                                switch (workAreaModel.getWork_area_status()) {
                                    case "Completed":
                                        completeCount++;
                                        status = Constants.completed;
                                        color = R.color.completeBoarderColor;
                                        break;
                                    case "On Hold":
                                        onHoldCount++;
                                        status = Constants.On_Hold;
                                        color = R.color.onHoldBoarderColor;
                                        break;
                                    case "In Progress":
                                        inProgressCount++;
                                        status = Constants.In_Progress;
//                                    color = R.color.inProgressBoarderColor;
                                        color = R.color.status_dark_blue;
                                        break;
                                    case "Not Started":
                                        notStartedCount++;
                                        status = Constants.Not_Started;
                                        color = R.color.notStartedBoarderColor;
                                        break;
                                }

                                dahboardListModels.add(new DahboardListModel(Constants.clusterList, workAreaModel.getWork_area_name(),
                                        workAreaModel.getLast_edited_date(), status, color));
                            }
                        }
                    } catch (Exception exception) {
                        AppLog.logData(activity, exception.getMessage());
                        AppLog.e(exception.getMessage());
                    }
                }

                binding.txtInProgressCount.setText(String.valueOf(inProgressCount));
                binding.txtCompleteCount.setText(String.valueOf(completeCount));
                binding.txtNotStartedCount.setText(String.valueOf(notStartedCount));
                binding.txtOnHoldCount.setText(String.valueOf(onHoldCount));
                // binding.autoCompleteClusterWorkArea.setText(App.getSharedPreferencesHandler().getString(Constants.workAreaName, ""));

                if (workAreaList != null && !workAreaList.isEmpty()) {
                    dashboardRvListAdapter.setDataList(dahboardListModels);
                    binding.rvFilterList.setLayoutManager(new LinearLayoutManager(activity));
                    binding.autoCompleteClusterWorkArea.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, nameList));
                    binding.autoCompleteClusterWorkArea.setText(nameList.get(0), false);
                }
            }
        });
    }

    private void selectDate() {
        binding.etFilterDate.setOnClickListener(view -> {

            if (!Utils.checkAutodateTimeValidation(activity)) {
                return;
            }

            CalendarConstraints constraints = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now())
                    .build();

            MaterialDatePicker picker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTheme(R.style.ThemMaterialCalendar)
                    .setCalendarConstraints(constraints)
                    .setTitleText("Select Date Range")
                    .setSelection(new Pair<>(null, null))
                    .build();

            picker.show(((AppCompatActivity) activity).getSupportFragmentManager(), "");
            picker.addOnPositiveButtonClickListener(selection -> {

                Pair pair = (Pair) selection;
                dateSelected = true;
                dateWhere = "(" + Constants.WorkArea_work_last_edited_date + " BETWEEN timestamp '" +
                        Utils.convertTimeToQueryString((Long) pair.first) + " 00:00:01' AND timestamp '" +
                        Utils.convertTimeToQueryString((Long) pair.second) + " 23:59:59')";
                binding.etFilterDate.setText(Utils.convertTimeToString((Long) pair.first) + "/" + Utils.convertTimeToString((Long) pair.second));
                setupPreviousClusterRecord(getWhereClause());
            });

            picker.addOnNegativeButtonClickListener(view1 -> {
                dateSelected = false;
                binding.etFilterDate.setText("");
                setupPreviousClusterRecord(getWhereClause());
                picker.dismiss();
            });
        });
    }

    private String getWhereClause() {
        String where = "user_name = '" + userModel.getUser_name() + "'";
        if (clusterSelected) {
            where = "(work_area_name = '" + binding.autoCompleteClusterWorkArea.getText().toString() + "')";
        } else {
            where = "(user_name = '" + userModel.getUser_name() + "')";
        }
        if (dateSelected)
            where = "(" + where + " AND " + dateWhere + ")";
        return where + " AND (1=1)";
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupPreviousClusterRecord(String where) {

        if (!Utils.checkinterne(activity))
            return;

        Utils.updateProgressMsg("Getting records.Please wait..", activity);
        ArrayList<DahboardListModel> dahboardListModels1 = new ArrayList<>();
        try {

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        resetCounter();
        dashboardRvListAdapter.setDataList(dahboardListModels1);

        if (clusterSelected) {
            queryResultRepoViewModel.getQueryResult(Constants.StructureInfo_MS_BASE_URL_ARC_GIS,
                    Constants.StructureInfo_ENDPOINT,
                    GetFormModel.getInstance().getQueryBuilderForm(where, "*", false, Constants.WorkArea_work_last_edited_date + " DESC", true, false));
        } else {
            queryResultRepoViewModel.getQueryResult(Constants.WorkArea_MS_BASE_URL_ARC_GIS,
                    Constants.WorkArea_ENDPOINT,
                    GetFormModel.getInstance().getQueryBuilderForm(where, "*", false, "work_area_name" + " DESC", true, false));

        }

        queryResultRepoViewModel.getMutableLiveData().observe(getActivity(), resultQueryModel -> {
            Utils.dismissProgress();
            if (resultQueryModel != null) {
                if (resultQueryModel.getFeatures().size() > 0) {
                    binding.rvFilterList.setVisibility(View.VISIBLE);
                    for (int i = 0; i < resultQueryModel.getFeatures().size(); i++) {
                        try {

                        Map<String, Object> map = (Map<String, Object>) resultQueryModel.getFeatures().get(i);
                        Map<String, Object> mapGeometryValue = (Map<String, Object>) map.get("geometry");
                        Map<String, Object> mapAttributeValue = (Map<String, Object>) map.get("attributes");
                        if (mapAttributeValue != null) {
                            int color = R.color.main_color;
                            String status = "", name = "", listType;
                            if (clusterSelected) {
                                status = Utils.getString(mapAttributeValue.get(Constants.StructureInfo_structure_status));
                                name = Utils.getString(mapAttributeValue.get(Constants.UnitInfo_hut_number));
                                listType = Constants.structureList;
                            } else {
                                status = Utils.getString(mapAttributeValue.get(Constants.WorkArea_work_area_status));
                                name = Utils.getString(mapAttributeValue.get(Constants.WorkArea_work_area_name));
                                listType = Constants.clusterList;
                            }

                            switch (status) {
                                case "Completed":
                                    completeCount++;
                                    status = Constants.completed;
                                    color = R.color.completeBoarderColor;
                                    break;
                                case "On Hold":
                                    onHoldCount++;
                                    status = Constants.On_Hold;
                                    color = R.color.onHoldBoarderColor;
                                    break;
                                case "In Progress":
                                    inProgressCount++;
                                    status = Constants.In_Progress;
//                                    color = R.color.inProgressBoarderColor;
                                    color = R.color.status_dark_blue;
                                    break;
                                case "Not Started":
                                    notStartedCount++;
                                    status = Constants.Not_Started;
                                    color = R.color.notStartedBoarderColor;
                                    break;
                            }
                            dahboardListModels1.add(new DahboardListModel(listType, name,
                                    Utils.convertDateToString(mapAttributeValue.get(Constants.WorkArea_work_last_edited_date)),
                                    status, color));
                        }
                        } catch(Exception exception) {
                            AppLog.e(exception.getMessage());
                        }
                    }

                    binding.txtInProgressCount.setText("" + inProgressCount);
                    binding.txtCompleteCount.setText("" + completeCount);
                    binding.txtNotStartedCount.setText("" + notStartedCount);
                    binding.txtOnHoldCount.setText("" + onHoldCount);

                    dashboardRvListAdapter.setDataList(dahboardListModels1);
                } else {
                    Utils.showMessagePopup("No record found.", activity);
                    binding.rvFilterList.setVisibility(View.GONE);
                    binding.noListLyt.setVisibility(View.VISIBLE);
                }
            } else {
                Utils.showMessagePopup("No record found.", activity);
                binding.rvFilterList.setVisibility(View.GONE);
                binding.noListLyt.setVisibility(View.VISIBLE);
            }
        });
        } catch (Exception exception) {
            AppLog.e(exception.getMessage());
        }
    }

    private void resetCounter() {
        inProgressCount = 0;
        completeCount = 0;
        notStartedCount = 0;
        onHoldCount = 0;
        totalCount = 0;

        binding.txtInProgressCount.setText("" + inProgressCount);
        binding.txtCompleteCount.setText("" + completeCount);
        binding.txtNotStartedCount.setText("" + notStartedCount);
        binding.txtOnHoldCount.setText("" + onHoldCount);
    }

}
