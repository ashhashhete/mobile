package com.igenesys.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.igenesys.App;
import com.igenesys.LoginActivity;
import com.igenesys.R;
import com.igenesys.StructureActivity;
import com.igenesys.UploadActivity;
import com.igenesys.adapter.SummaryListAdapter;
import com.igenesys.adapter.SurveyListAdapter;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.AadhaarVerificationData;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaDetailsDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StatusCount;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.databinding.ActivitySurveyLocalListBinding;
import com.igenesys.fragment.ShowFilterBottomSheetFragment;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.model.UpdateFeatureToLayer;
import com.igenesys.model.UpdateFeatures;
import com.igenesys.model.UpdateWorkAreaStatusModel;
import com.igenesys.model.UserModel;
import com.igenesys.model.WorkAreaModel;
import com.igenesys.networks.Api_Interface;
import com.igenesys.networks.GetFormModel;
import com.igenesys.networks.QueryResultRepoViewModel;
import com.igenesys.networks.RetrofitService;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.Utils;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import org.bouncycastle.crypto.CryptoException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyListPageViewModel extends ActivityViewModel<UploadActivity>
        implements SurveyListAdapter.OnItemClickListner, ShowFilterBottomSheetFragment.OnItemClickListner {

    private final int UPLOAD_INTERVAL_MIN = 3000;
    private final int UPLOAD_INTERVAL = 5000;

    private int uploadParentPosition = -1;
    private int uploadFilePosition = 0;

    private ProgressDialog progressBar;
    private Random r = new Random();
    private MutableLiveData<Integer> listenUploadCounter = new MutableLiveData<>();
    private int fileToUpload = 0;

    private List filePathArray = new ArrayList();
    private HashMap<String, String> hashMapGobalId;
    // private ArrayList<String> memberIdArrayList;
    private ArrayList<String> unitIdArrayList;
    // private ArrayList<HashMap<String, String>> hashMapRelativePathObjectId = new ArrayList<>();
    // private ArrayList<MediaInfoDataModel> mediaInfoDataModelArrayList;

    private ArrayList<String> mediaIdArrayListToDelete;

    private Activity activity;
    private ActivitySurveyLocalListBinding binding;
    public static boolean loadMap = false;
    private LocalSurveyDbViewModel localSurveyDbViewModel;
    private ServiceFeatureTable structureInfoFT, mediaInfoFT, unitInfoFT, hohInfoFT, memberInfoFT;
    private QueryResultRepoViewModel queryResultRepoViewModel;
    private UserModel userModel;

    private LinkedHashMap<String, List<MediaDetailsDataModel>> mapListMediaInfoToDelete;

    private SurveyListAdapter surveyListAdapter;

    private List<UnitInfoDataModel> unitInfoPointList, filteredUnitData, filterdUnitPointList, filteredUnitDataToUpload;

    private UnitInfoDataModel selectedUnitDataModel;

    private String tenementNumber = "", clusterNumber = "", status = "";

    private boolean isSingleUploadCall = true;

    private ArrayList<String> listHohIds;
    private ArrayList<String> listMembersIds;

    private String hohGlobalId;

    private ArrayList<MediaInfoDataModel> deleteObjList = new ArrayList<>();
    private String username="";

    // private LinkedHashMap<String, ArrayList<MediaInfoDataModel>> mapFailedUploadedUnits;

    public SurveyListPageViewModel(UploadActivity activity) {
        super(activity);

        this.activity = activity;
        binding = activity.getBinding();

        reloadLayers();

        queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);

        try {

            userModel = App.getInstance().getUserModel();

            if (userModel != null) {
                initView();
            } else {
                redirectToLogin();
            }
        } catch (Exception e) {
            redirectToLogin();
        }

        // generateODORequestToken();
    }

    private void refreshCount() {
        try {
            StatusCount statusCount = localSurveyDbViewModel.getUnitStatusCounts();
            binding.txtInProgressCount.setText(String.valueOf(statusCount.getCountInProgress()));
            binding.txtCompletedCount.setText(String.valueOf(statusCount.getCountCompleted()));
            binding.txtOnHoldCount.setText(String.valueOf(statusCount.getCountonHold()));
            binding.txtNotStartedCount.setText(String.valueOf(statusCount.getCountNotStarted()));
        } catch (Exception ignored) {
            Utils.shortToast("Unable to Refresh Count.", activity);
        }
    }

    private void refreshList() {
        localSurveyDbViewModel.getUnitInfoUploadData(userModel.getUser_name()).observe(getActivity(), unitInfoDataModelList -> {

            unitInfoPointList = unitInfoDataModelList;
            filterdUnitPointList = unitInfoPointList;

            refreshCount();

            if (unitInfoDataModelList.isEmpty())
                Utils.shortToast("No saved structure info.", activity);

            surveyListAdapter.setSummaryItemModels(filterdUnitPointList);
        });
    }

    private void initView() {
        try {
            username=App.getSharedPreferencesHandler().getString("id","");
        }catch (Exception ex){
            ex.getMessage();
        }
        progressBar = new ProgressDialog(activity);
        progressBar.setCancelable(false);
        progressBar.setMessage("Uploading Structure");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setMax(100);
        unitInfoPointList = new ArrayList<>();
        filterdUnitPointList = new ArrayList<>();
        filteredUnitData = new ArrayList<>();

        mediaIdArrayListToDelete = new ArrayList<>();
        // mapFailedUploadedUnits = new LinkedHashMap<>();

        binding.cvErrorView.setVisibility(View.GONE);
        localSurveyDbViewModel = ViewModelProviders.of(getActivity()).get(LocalSurveyDbViewModel.class);
        localSurveyDbViewModel = ViewModelProviders.of(getActivity()).get(LocalSurveyDbViewModel.class);
        binding.commonHeader.txtPageHeader.setText("Survey List");
        binding.commonHeader.imgBack.setOnClickListener(view -> finish());

        surveyListAdapter = new SurveyListAdapter(activity, localSurveyDbViewModel, null, this);
        binding.rvSurveyList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.rvSurveyList.setAdapter(surveyListAdapter);

        refreshList();
        setUpListener();
        // generateODORequestToken();
    }

    private void setUpListener() {

        binding.rvUploadAll.setOnClickListener(view -> {

            if (!Utils.isConnected(activity)) {
                stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
                return;
            }

            if (unitInfoPointList != null && !unitInfoPointList.isEmpty()) {
                showActionAlertDialogButtonClicked("Confirm the action",
                        "Do you want to upload all records (max first 10 at a time)?",
                        "Yes", "No", false, false);
            } else {
                stopProcessWithError("No pending units available to upload.");
            }
        });

        binding.rvFilterList.setOnClickListener(view -> {
            FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
            ShowFilterBottomSheetFragment showFilterBottomSheetFragment = ShowFilterBottomSheetFragment.geInstance(activity, tenementNumber,
                    clusterNumber, status, this);
            showFilterBottomSheetFragment.show(fm, "");
        });

        binding.rvZip.setOnClickListener(view -> generateUploadDataForZip());

        listenUploadCounter.observe((LifecycleOwner) activity, integer -> {
            try {
                if (fileToUpload < filteredUnitDataToUpload.size()) {
                    selectedUnitDataModel = filteredUnitDataToUpload.get(fileToUpload);
                    generateStructureData();
                } else {
                    Utils.shortToast("Data Upload Successfully", activity);
                    progressBar.setMessage("Completed Successfully");
                    progressBar.setProgress(100);
                    progressBar.dismiss();
                    fileToUpload = 0;
                    isSingleUploadCall = false;
                }
            } catch (Exception ex) {
                stopProcessWithError("Something went wrong while uploading unit data. Kindly retry!!");
            }
        });

        binding.rvStructure.setOnClickListener(view -> syncStructure());
    }

    public void showActionAlertDialogButtonClicked(String header, String message, String btnYes, String btnNo,
                                                   boolean isSingleUploadCall, boolean isDelete) {

        this.isSingleUploadCall = isSingleUploadCall;

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_action, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        txt_header.setText(header);

        TextView txt_message = customLayout.findViewById(R.id.txt_mssage);
        txt_message.setText(message);

        TextView txt_yes = customLayout.findViewById(R.id.txt_yes);
        txt_yes.setText(btnYes);

        TextView txt_no = customLayout.findViewById(R.id.txt_no);
        txt_no.setText(btnNo);

        ImageView img_close = customLayout.findViewById(R.id.img_close);
        img_close.setOnClickListener(view1 -> dialog.dismiss());

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);
        statusRadioGroup.setVisibility(View.GONE);

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(view1 -> {

            if (isDelete) {
                try {
                    // It will delete single selected unit
                    deleteUnitFromLocalDB(true);
                } catch (Exception ex) {
                    AppLog.e("" + ex.getMessage());
                    stopProcessWithError("Unable to Delete the Unit. Please Try Again!!");
                }
            } else if (isSingleUploadCall) {
                // It will upload single selected unit
                generateStructureData();
            } else {
                // It will upload all units
                generateMultipleUpload();
            }

            dialog.dismiss();
        });

        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(view1 -> dialog.dismiss());

        dialog.show();
    }

    public void showActionAlertDialogUpdateButtonOffline(String header, String yesBtn, String noBtn, String message) {

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_action, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        txt_header.setText(header);

        TextView txt_message = customLayout.findViewById(R.id.txt_mssage);
        txt_message.setText(message);

        TextView txt_yes = customLayout.findViewById(R.id.txt_yes);
        txt_yes.setText(yesBtn);

        TextView txt_no = customLayout.findViewById(R.id.txt_no);
        txt_no.setText(noBtn);

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);

        if (Utils.isNullOrEmpty(yesBtn))
            btn_yes.setVisibility(View.GONE);

        btn_yes.setOnClickListener(view1 -> dialog.dismiss());

        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);

        if (Utils.isNullOrEmpty(noBtn))
            btn_no.setVisibility(View.GONE);

        btn_no.setOnClickListener(view1 -> {
            dialog.dismiss();
            activity.finish();
        });

        ImageView img_close = customLayout.findViewById(R.id.img_close);
        img_close.setVisibility(View.GONE);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);
        statusRadioGroup.setVisibility(View.GONE);

        dialog.show();
    }

    @Override
    public void onEditBtnClicked(UnitInfoDataModel unitInfoDataModel) {
        activity.startActivity(new Intent(activity, StructureActivity.class)
                .putExtra(Constants.IS_EDITING, true)
                .putExtra(Constants.viewMode, Constants.offline)
                .putExtra(Constants.EDIT_TYPE, Constants.EDIT_StructureInfo)
                .putExtra(Constants.INTENT_DATA_StructureInfo, localSurveyDbViewModel.getStructureInfoPointDataModel(unitInfoDataModel.getUnitSampleGlobalid())));
    }

    @Override
    public void onUploadBtnClicked(UnitInfoDataModel unitInfoDataModel) {
        if (Utils.isConnected(activity)) {
            selectedUnitDataModel = unitInfoDataModel;
            showActionAlertDialogButtonClicked("Confirm the action", "Do you want to upload this record?", "Yes", "No", true, false);
        } else {
            showActionAlertDialogUpdateButtonOffline("DRP App Status", "OK", "", "Unable to proceed. You're currently offline.Please check internet connection");
        }
    }

    @Override
    public void onDeleteBtnClicked(UnitInfoDataModel unitInfoDataModel) {
        selectedUnitDataModel = unitInfoDataModel;
        showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete this record?", "Yes", "No", false, true);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            String pdfFilePath = App.getSharedPreferencesHandler().getString(Constants.pdfFilePath);
            App.getSharedPreferencesHandler().putString(Constants.pdfFilePath, "");
            File pdfFile = new File(pdfFilePath);
            if (pdfFile.exists())
                CryptoUtilsTest.encryptFileinAES(pdfFile, 1);
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResetBtnClicked() {

        tenementNumber = "";
        clusterNumber = "";
        status = "";

        filteredUnitData = unitInfoPointList;
        surveyListAdapter.setSummaryItemModels(filteredUnitData);
    }

    @Override
    public void onApplyClicked(String tenementNumber, String cluterNumber, String sta) {

        this.tenementNumber = tenementNumber;
        this.clusterNumber = cluterNumber;
        this.status = sta;

        String status = "";

        if (sta.equals(Constants.In_Progress)) {
            status = Constants.InProgress_statusLayer;
        } else if (sta.equals(Constants.On_Hold)) {
            status = Constants.OnHold_statusLayer;
        } else if (sta.equals(Constants.dispute)) { //Constants.Not_Started
            status = Constants.completed_dispute;//Constants.NotStarted_statusLayer;
        } else if (sta.equals(Constants.completed)) {
            status = Constants.completed_statusLayer;
        }

        filteredUnitData = multipleFilter(tenementNumber.trim(), cluterNumber.trim(), status.trim(), unitInfoPointList);

        if (filteredUnitData.isEmpty()) {
            Utils.showMessagePopup("Couldn't find any filtered value.", activity);
        }

        surveyListAdapter.setSummaryItemModels(filteredUnitData);
    }

    @Override
    public boolean onBackKeyPress() {
        loadMap = true;
        return super.onBackKeyPress();
    }

    public List<UnitInfoDataModel> multipleFilter(String tenementNumber, String clusterNumber,
                                                  String status, List<UnitInfoDataModel> listAllTasks) {

        ArrayList<UnitInfoDataModel> listTasksAfterFiltering = new ArrayList<>();

        for (UnitInfoDataModel task_obj : listAllTasks) {
            String tenementNumber1 = task_obj.getHut_number();
            String clusterNumber1 = clusterNumber;//task_obj.getCluster_name();
            String status1 = task_obj.getUnit_status();

            if (tenementNumber1.toUpperCase().trim().contains(tenementNumber.toUpperCase().trim()) || tenementNumber.isEmpty())
                if (clusterNumber1.toUpperCase().trim().contains(clusterNumber.toUpperCase().trim()) || clusterNumber.isEmpty())
                    if (status.equalsIgnoreCase(status1) || status.isEmpty())
                        if (!tenementNumber.isEmpty() || !clusterNumber.isEmpty() || status.equalsIgnoreCase(status1) || !status.isEmpty()) {
                            listTasksAfterFiltering.add(task_obj);
                        }
        }

        return listTasksAfterFiltering;
    }

    private void reloadLayers() {
        structureInfoFT = App.getInstance().getStructureInfoFT_FS();
        mediaInfoFT = App.getInstance().getMediaInfoFT_FS();
        unitInfoFT = App.getInstance().getUnitInfoFT_FS();
        hohInfoFT = App.getInstance().getHohInfoFT_FS();
        memberInfoFT = App.getInstance().getMemberInfoFT_FS();

        structureInfoFT.loadAsync();
        mediaInfoFT.loadAsync();
        unitInfoFT.loadAsync();
        hohInfoFT.loadAsync();
        memberInfoFT.loadAsync();
    }

    private void updateWorkArea(WorkAreaModel selectedWorkArea) {

        try {

            UpdateWorkAreaStatusModel updateWorkAreaStatusModel = new UpdateWorkAreaStatusModel();

            updateWorkAreaStatusModel.setWork_area_status(Constants.InProgress_statusLayer);
            updateWorkAreaStatusModel.setObjectid((int) Double.parseDouble(selectedWorkArea.getObjectid()));
            selectedWorkArea.setWork_area_status(Constants.InProgress_statusLayer);

            UpdateFeatureToLayer.UpdateForm updateForm = new UpdateFeatureToLayer.UpdateForm();
            updateForm.setAttributes(updateWorkAreaStatusModel);

            List<UpdateFeatureToLayer.UpdateForm> array = new ArrayList<>();
            array.add(updateForm);

            queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
            queryResultRepoViewModel.initUpdateFeatureResult(Constants.WorkArea_FS_BASE_URL_ARC_GIS, Constants.WorkArea_ENDPOINT,
                    GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));
            queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), addedRecordResponse ->
                    Utils.shortToast(addedRecordResponse != null ? "Status updated successfully" : "Status not updated successfully", activity));
        } catch (Exception e) {
            AppLog.e(e.getMessage());
            stopProcessWithError("Something Went Wrong while updating WorkArea Status");
        }
    }

    private void generateMultipleUpload() {

        try {

            isSingleUploadCall = false;

            if (structureInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                    mediaInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                    unitInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                    hohInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                    memberInfoFT.getLoadStatus() != LoadStatus.LOADED) {
                reloadLayers();
                stopProcessWithError("It seems like the layers are not loading properly at the moment. Please be patient and wait for a little while.");
                return;
            }

            filteredUnitDataToUpload = filterdUnitPointList;

            if (!filteredUnitDataToUpload.isEmpty()) {
                fileToUpload = 0;
                listenUploadCounter.setValue(fileToUpload);
            } else {
                stopProcessWithError("No Data To Upload");
            }
        } catch (Exception ex) {
            AppLog.e(String.valueOf(ex.getMessage()));
            stopProcessWithError("Something went wrong while uploading data. " + ex.getMessage());
        }
    }
    //----------Structure Sync-------------------

    private void syncStructure() {

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        if (structureInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                mediaInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                unitInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                hohInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                memberInfoFT.getLoadStatus() != LoadStatus.LOADED) {
            reloadLayers();
            stopProcessWithError("It seems like the layers are not loading properly at the moment. Please be patient and wait for a little while.");
            return;
        }

        progressBar.show();
        progressBar.setMessage("Uploading Structure");
        progressBar.setProgress(0);

        List<StructureInfoPointDataModel> structureInfoPointDataModelList = localSurveyDbViewModel.getStructureInfoPointDataModelAll();
        ArrayList<Map<String, Object>> attriutesStructureUpdateArray = new ArrayList();
        Map<String, Object> attributes;

        int objectId;
        for (StructureInfoPointDataModel structureInfoPointDataModel : structureInfoPointDataModelList) {

            try {
                objectId = (int) Double.parseDouble(structureInfoPointDataModel.getObejctId());
            } catch (Exception ignored) {
                objectId = 0;
            }

            attributes = Utils.getStructurePointAttributeV4(
                    structureInfoPointDataModel.getStructure_id(),
                    structureInfoPointDataModel.getGrid_number(),
                    structureInfoPointDataModel.getArea_name(),
                    structureInfoPointDataModel.getCluster_name(),
                    structureInfoPointDataModel.getPhase_name(),
                    structureInfoPointDataModel.getWork_area_name(),
                    structureInfoPointDataModel.getHut_number(),
                    structureInfoPointDataModel.getStructure_name(),
                    (short) structureInfoPointDataModel.getNo_of_floors(),
                    structureInfoPointDataModel.getAddress(),
                    structureInfoPointDataModel.getTenement_number(),
                    structureInfoPointDataModel.getStructure_status(),
                    structureInfoPointDataModel.getSurveyor_name());

            if (structureInfoPointDataModel.isUploaded() && objectId != 0) {
                attributes.put(Constants.objectid, objectId);
            }

            attriutesStructureUpdateArray.add(attributes);
        }

        new Handler().postDelayed(() -> uploadSyncStructureData(attriutesStructureUpdateArray, Constants.StructureInfo_ENDPOINT), 500);
    }

    private void uploadSyncStructureData(ArrayList<Map<String, Object>> attributes, String path) {

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        List<UpdateFeatures.UpdateForm> array = new ArrayList<>();

        for (Map<String, Object> attribute : attributes) {
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attribute);
            array.add(updateForm);
        }

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initUpdateFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, path,
                GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

        queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            try {

                if (updatedRecordResponse != null && updatedRecordResponse.getAddResults() != null
                        && !updatedRecordResponse.getAddResults().isEmpty()) {
                    if (updatedRecordResponse.getAddResults().get(0).getSuccess()) {
                        Utils.shortToast("Structure Info Updated Successfully.", activity);
                    } else if (updatedRecordResponse.getAddResults().get(0).getError() != null
                            && !Utils.isNullOrEmpty(updatedRecordResponse.getAddResults().get(0).getError().getDescription())
                            && updatedRecordResponse.getAddResults().get(0).getError().getDescription().equalsIgnoreCase("Object is missing.")) {
                        stopProcessWithError("Structure details not updated successfully. Error: " + updatedRecordResponse.getAddResults().get(0).getError().getDescription());
                    }
                } else {
                    stopProcessWithError("Error while sync upload Structure Data");
                }
            } catch (Exception ex) {
                stopProcessWithError("Exception sync uploadStructureData: " + ex.getMessage());
            }
        });
    }

    //---------------------------------

    private void generateStructureData() {

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        if (structureInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                mediaInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                unitInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                hohInfoFT.getLoadStatus() != LoadStatus.LOADED &&
                memberInfoFT.getLoadStatus() != LoadStatus.LOADED) {
            reloadLayers();
            stopProcessWithError("It seems like the layers are not loading properly at the moment. Please be patient and wait for a little while.");
            return;
        }

        if (App.getSharedPreferencesHandler().getEsriToken() == null || App.getSharedPreferencesHandler().getEsriToken().isEmpty()) {
            stopProcessWithError("Something went wrong while authentication. Kindly re-initiate the app to proceed!!");
            return;
        }

        progressBar.show();
        progressBar.setMessage("Uploading Structure");
        progressBar.setProgress(0);

        saveLogs("generateStructureData_", new Gson().toJson(selectedUnitDataModel).toString());

        StructureInfoPointDataModel structureInfoPointDataModel = localSurveyDbViewModel.getStructureInfoPointDataModel(selectedUnitDataModel.getUnitSampleGlobalid());

        if (structureInfoPointDataModel == null) {
            stopProcessWithError("Something went wrong while generating Structure Data. Kindly Retry!!");
            return;
        }

        Map<String, Object> attributes;

        int objectId = 0;
        try {
            objectId = (int) Double.parseDouble(structureInfoPointDataModel.getObejctId());
        } catch (Exception ignored) {

        }
        attributes = Utils.getStructurePointAttributeV4(
                structureInfoPointDataModel.getStructure_id(),
                structureInfoPointDataModel.getGrid_number(),
                structureInfoPointDataModel.getArea_name(),
                structureInfoPointDataModel.getCluster_name(),
                structureInfoPointDataModel.getPhase_name(),
                structureInfoPointDataModel.getWork_area_name(),
                structureInfoPointDataModel.getHut_number(),
                structureInfoPointDataModel.getStructure_name(),
                (short) structureInfoPointDataModel.getNo_of_floors(),
                structureInfoPointDataModel.getAddress(),
                structureInfoPointDataModel.getTenement_number(),
                structureInfoPointDataModel.getStructure_status(),
                structureInfoPointDataModel.getSurveyor_name());

        if (structureInfoPointDataModel.isUploaded() && objectId != 0) {
            attributes.put(Constants.objectid, objectId);
        }

        Log.w("Upload", "Attributes::generateStructureData" + new Gson().toJson(attributes));

        new Handler().postDelayed(() -> uploadStructureData(attributes, structureInfoPointDataModel), UPLOAD_INTERVAL_MIN);
    }

    private void uploadStructureData(Map<String, Object> attributes, StructureInfoPointDataModel structureInfoPointDataModel) {
        String s="structure_id = "+structureInfoPointDataModel.getStructure_id()+"\n"+"structure_hut_id = "+structureInfoPointDataModel.getHut_number()+"\n"+"global_id = "+structureInfoPointDataModel.getGlobalId()+"\n";
        saveParticularLogs("uploadStructureData_" + selectedUnitDataModel.getUnit_id(), s.toString());
        saveLogs("uploadStructureData_" + structureInfoPointDataModel.getStructure_id(), new Gson().toJson(attributes).toString());

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        Log.d("New Upload", "New Upload :: uploadStructureData");

        UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
        updateForm.setAttributes(attributes);

        List<UpdateFeatures.UpdateForm> array = new ArrayList<>();
        array.add(updateForm);

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initUpdateFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.StructureInfo_ENDPOINT,
                GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

        queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            try {

                if (updatedRecordResponse != null && !updatedRecordResponse.getAddResults().isEmpty()) {
                    if (updatedRecordResponse.getAddResults().get(0).getSuccess()) {
                        Utils.shortToast("Structure Info Updated Successfully.", activity);
//                        new Handler().postDelayed(() -> generateUnitData(structureInfoPointDataModel.getStructSampleGlobalid(),structureInfoPointDataModel), UPLOAD_INTERVAL_MIN);
                        new Handler().postDelayed(() -> getStructureInfo(structureInfoPointDataModel.getStructSampleGlobalid(),structureInfoPointDataModel), UPLOAD_INTERVAL_MIN);
                    } else if (updatedRecordResponse.getAddResults().get(0).getError() != null
                            && !Utils.isNullOrEmpty(updatedRecordResponse.getAddResults().get(0).getError().getDescription())
                            && updatedRecordResponse.getAddResults().get(0).getError().getDescription().equalsIgnoreCase("Object is missing.")) {
                        stopProcessWithError("Structure details not updated successfully. Error: " + updatedRecordResponse.getAddResults().get(0).getError().getDescription());
                    }
                } else {
                    stopProcessWithError("Error while upload Structure Data");
                }
            } catch (Exception ex) {
                stopProcessWithError("Exception uploadStructureData: " + ex.getMessage());
            }
        });
    }

    ////////////// UNIT INFORMATION

    private void generateUnitData(String structure_id,StructureInfoPointDataModel structureInfoPointDataModel,Map<String, Object> mapAttributeValue) {

        try {

            progressBar.setMessage("Uploading Unit");
            progressBar.setProgress(r.nextInt(20 - 10) + 10);

            Object structurePointGlobalId;
            String structurePointGlobalIdStr = structure_id.toUpperCase();

            if (structurePointGlobalIdStr.startsWith("{"))
                structurePointGlobalId = UUID.fromString(structurePointGlobalIdStr.substring(1, structurePointGlobalIdStr.length() - 1));
            else
                structurePointGlobalId = UUID.fromString(structurePointGlobalIdStr);

            try{

                if(!selectedUnitDataModel.getHut_number().equalsIgnoreCase(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_hut_number)))){
                    String s="unit_unique_id = "+selectedUnitDataModel.getUnit_id()+"\n"+"unit_hut_id = "+selectedUnitDataModel.getHut_number()+"\n"+"rel_global_id = "+selectedUnitDataModel.getRel_globalid()+"\n";
                    saveParticularLogs("uploadNewUnitData_" + selectedUnitDataModel.getUnit_id(), s.toString());
                    stopProcessWithError("Hut ID not matching for unit and hut polygon.");
                    return;
                }
                if(!selectedUnitDataModel.getRel_globalid().equalsIgnoreCase(Utils.getString(mapAttributeValue.get(Constants.globalid)))){
                    String s="unit_unique_id = "+selectedUnitDataModel.getUnit_id()+"\n"+"unit_hut_id = "+selectedUnitDataModel.getHut_number()+"\n"+"rel_global_id = "+selectedUnitDataModel.getRel_globalid()+"\n";
                    saveParticularLogs("uploadNewUnitData_" + selectedUnitDataModel.getUnit_id(), s.toString());
                    stopProcessWithError("Rel global ID and Global ID not matching for unit and hut polygon.");
                    return;
                }
                if(!selectedUnitDataModel.getUnit_unique_id().substring(0,Utils.getString(mapAttributeValue.get("hut_id")).length()).equalsIgnoreCase(Utils.getString(mapAttributeValue.get("hut_id")))){
                    String s="unit_unique_id = "+selectedUnitDataModel.getUnit_id()+"\n"+"unit_hut_id = "+selectedUnitDataModel.getHut_number()+"\n"+"rel_global_id = "+selectedUnitDataModel.getRel_globalid()+"\n";
                    saveParticularLogs("uploadNewUnitData_" + selectedUnitDataModel.getUnit_id(), s.toString());
                    stopProcessWithError("Unit unique ID not matching for unit and hut polygon id.");
                    return;
                }
            }catch (Exception ex){
                ex.getMessage();
            }




            if (!Utils.isConnected(activity)) {
                stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
                return;
            }

            Log.d("New Upload", "New Upload :: generateUnitData");

            ArrayList<Map<String, Object>> attriutesUnitNewArray = new ArrayList();
            ArrayList<Map<String, Object>> attriutesUnitUpdateArray = new ArrayList();

            GregorianCalendar cal;
            // String unit_area_mtrs = "", loft_area_mtrs = "";
            short visit_count = 0;
            short lockFlag = 0;

            try {

                int objectId = (int) Utils.doubleFormatter(selectedUnitDataModel.getObejctId());
                Map<String, Object> attributes;
                visit_count = (short) selectedUnitDataModel.getVisit_count();
                if (visit_count == 0) {
                    visit_count = 1;
                }
                if (visit_count >= 4) {
                    lockFlag = 1;
                }
                if (selectedUnitDataModel.getUnit_status().equals(Constants.completed_statusLayer)) {
                    lockFlag = 1;
                } else if (selectedUnitDataModel.getUnit_status().equals(Constants.completed_dispute)) {
                    lockFlag = 1;
                }

                // Visit Count issue. Commented as informed by Rohit Sinha
                // selectedUnitDataModel.setVisit_count(lockFlag);

                if (selectedUnitDataModel.isYesNo() && !selectedUnitDataModel.isMember_available()) {
                    try {
                        cal = Utils.getGregorianCalendarFromDate(selectedUnitDataModel.getExistence_since());
                    } catch (Exception e) {
                        cal = null;
                    }
                    String lfft = "YES";
                    if (selectedUnitDataModel.getLoft_present()) {
                        lfft = "YES";
                    } else {
                        lfft = "NO";
                    }

                    GregorianCalendar gc = null;
                    try {
                        Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(selectedUnitDataModel.getExistence_since() + " 00:00:00");
                        TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                        Locale loc = new Locale("en", "IN");
                        Calendar calendar = Calendar.getInstance(loc);
                        gc = (GregorianCalendar) calendar;
                        gc.setTimeZone(tz);
                        gc.setTime(date);
                    } catch (Exception ex) {
                        ex.getCause();
                    }
//RS
                    attributes = Utils.getUnitInfoDetailsAttributeV4(structurePointGlobalId,
                            selectedUnitDataModel.getUnit_id(),
                            selectedUnitDataModel.getUnit_unique_id(),
                            selectedUnitDataModel.getRelative_path(),
                            selectedUnitDataModel.getTenement_number(),
                            selectedUnitDataModel.getHut_number(),
                            selectedUnitDataModel.getFloor(),
                            selectedUnitDataModel.getUnit_no(),
                            selectedUnitDataModel.getUnit_usage(),
                            gc,
                            cal,
                            selectedUnitDataModel.getStructure_year(),
                            selectedUnitDataModel.getNature_of_activity(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            (selectedUnitDataModel.isElectric_bill_attached()) ? (short) 1 : (short) 0,
                            (short) 0,
                            (selectedUnitDataModel.isProperty_tax_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isRent_agreement_attached()) ? (short) 1 : (short) 0,
                            (short) 0,
                            (selectedUnitDataModel.isChain_document_attached()) ? (short) 1 : (short) 0,
                            (short) 0,
                            (selectedUnitDataModel.isOthers_attachment_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isNa_tax_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isElectoral_roll_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isPhoto_pass_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isShare_certificate_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isSchool_college_certificate_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isCertificate_issued_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isRestro_hotel_license_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isFactory_act_license_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isFood_drug_license_attached()) ? (short) 1 : (short) 0,
                            "NO",
                            selectedUnitDataModel.getUnit_status(),
                            selectedUnitDataModel.getSurveyor_name(),
                            selectedUnitDataModel.getRemarks(),
                            selectedUnitDataModel.getRemarks_other(),
                            selectedUnitDataModel.getMedia_captured_cnt(),
                            selectedUnitDataModel.getMedia_uploaded_cnt(),
                            selectedUnitDataModel.getSurveyor_desig(),
                            selectedUnitDataModel.getDrp_officer_name(),
                            selectedUnitDataModel.getDrp_officer_name_other(),
                            selectedUnitDataModel.getDrp_officer_desig(),
                            selectedUnitDataModel.getDrp_officer_desig_other(),
                            visit_count,
                            selectedUnitDataModel.getArea_name(),
                            selectedUnitDataModel.getWard_no(),
                            selectedUnitDataModel.getSector_no(),
                            selectedUnitDataModel.getZone_no(),
                            selectedUnitDataModel.getNagar_name(),
                            selectedUnitDataModel.getNagar_name_other(),
                            selectedUnitDataModel.getSociety_name(),
                            selectedUnitDataModel.getStreet_name(),
                            selectedUnitDataModel.getLandmark_name(),
                            selectedUnitDataModel.getRespondent_name(),
                            selectedUnitDataModel.getRespondent_dob(),
                            selectedUnitDataModel.getRespondent_age(),
                            selectedUnitDataModel.getRespondent_hoh_contact(),
                            selectedUnitDataModel.getRespondent_hoh_relationship(),
                            selectedUnitDataModel.getRespondent_hoh_relationship_other(),
                            selectedUnitDataModel.getTenement_document(),
                            selectedUnitDataModel.getMashal_survey_number(),
                            selectedUnitDataModel.getOwnership_status(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            lfft,
                            selectedUnitDataModel.getLoft_area_sqft(),
                            selectedUnitDataModel.getEmployees_count(),
                            selectedUnitDataModel.getPincode(),
                            selectedUnitDataModel.getRespondent_mobile(),
                            selectedUnitDataModel.getGhumasta_area_sqft(),
                            new GregorianCalendar(),
//                            unitInfoDataModel.getForm_lock(),
                            lockFlag,
                            selectedUnitDataModel.isUploaded(),
                            selectedUnitDataModel.getRespondent_hoh_name(),
                            selectedUnitDataModel.getSurvey_date(),
                            selectedUnitDataModel.getSurvey_time(),
                            selectedUnitDataModel.getType_of_other_structure(),
                            "",
                            selectedUnitDataModel.getRespondent_non_available_remark(),
                            selectedUnitDataModel.getSurvey_pavti_no(),
                            selectedUnitDataModel.getExistence_since_year(),
                            selectedUnitDataModel.getCountry_name(),
                            selectedUnitDataModel.getState_name(),
                            selectedUnitDataModel.getCity_name(),
                            selectedUnitDataModel.getAccess_to_unit(),
                            selectedUnitDataModel.getResidential_area_sqft(),
                            selectedUnitDataModel.getCommercial_area_sqft(),
                            selectedUnitDataModel.getMobile_no_for_otp(),
                            selectedUnitDataModel.getOtp_sent(),
                            selectedUnitDataModel.getOtp_received(),
                            selectedUnitDataModel.getOtp_verified(),
                            selectedUnitDataModel.getOtp_remarks(),
                            selectedUnitDataModel.getOtp_remarks_other(),
                            selectedUnitDataModel.getThumb_remarks(),

                            selectedUnitDataModel.getStructure_type_religious(),
                            selectedUnitDataModel.getStructure_religious_other(),
                            selectedUnitDataModel.getStructure_type_amenities(),
                            selectedUnitDataModel.getStructure_amenities_other(),
                            selectedUnitDataModel.getName_of_structure(),
                            selectedUnitDataModel.getType_of_diety(),
                            selectedUnitDataModel.getType_of_diety_other(),
                            selectedUnitDataModel.getName_of_diety(),
                            selectedUnitDataModel.getCategory_of_faith(),
                            selectedUnitDataModel.getCategory_of_faith_other(),
                            selectedUnitDataModel.getSub_category_of_faith(),
                            selectedUnitDataModel.getReligion_of_structure_belongs(),
                            selectedUnitDataModel.getStructure_ownership_status(),
                            selectedUnitDataModel.getName_of_trust_or_owner(),
                            selectedUnitDataModel.getNature_of_structure(),
                            selectedUnitDataModel.getConstruction_material(),
                            selectedUnitDataModel.getDaily_visited_people_count(),
                            selectedUnitDataModel.getTenement_number_rel_amenities(),
                            selectedUnitDataModel.getTenement_doc_used(),
                            selectedUnitDataModel.getTenement_doc_other(),
                            selectedUnitDataModel.getIs_structure_registered(),
                            selectedUnitDataModel.getStructure_registered_with(),
                            selectedUnitDataModel.getOther_religious_authority(),
                            selectedUnitDataModel.getName_of_trustee(),
                            selectedUnitDataModel.getName_of_landowner(),
                            selectedUnitDataModel.getNoc_from_landlord_or_govt(),
                            selectedUnitDataModel.getApproval_from_govt(),
                            selectedUnitDataModel.getYearly_festival_conducted(),
                            selectedUnitDataModel.getSurvey_pavti_no_rel_amenities(),
                            selectedUnitDataModel.getMashal_rel_amenities(),
                            selectedUnitDataModel.getRa_total_no_of_floors(),
                            selectedUnitDataModel.getLatitude(),
                            selectedUnitDataModel.getLongitude(),
                            selectedUnitDataModel.getGenesys_device_name(),
                            selectedUnitDataModel.getPrimary_imei_no(),
                            selectedUnitDataModel.getSecond_imei_no(),
                            selectedUnitDataModel.getIs_drppl_officer_available(),
                            selectedUnitDataModel.getDrppl_officer_name()
                    );
                } else if (!selectedUnitDataModel.isMember_available()) {
//                    visit_count= (short) unitInfoDataModel.getVisit_count();
//                    Date dtt=getDate(Long.parseLong(unitInfoDataModel.getVisit_date()));
                    //RS
                    attributes = Utils.getUnitInfoDetailsAttributeV4(structurePointGlobalId,
                            selectedUnitDataModel.getUnit_id(),
                            selectedUnitDataModel.getUnit_unique_id(),
                            selectedUnitDataModel.getRelative_path(),
                            selectedUnitDataModel.getHut_number(),
                            selectedUnitDataModel.getFloor(),
                            selectedUnitDataModel.getUnit_no(),
                            selectedUnitDataModel.getNature_of_activity(),
                            "NO",
                            selectedUnitDataModel.getUnit_status(),
                            selectedUnitDataModel.getSurveyor_name(),
                            selectedUnitDataModel.getSurveyor_desig(),
                            selectedUnitDataModel.getDrp_officer_name(),
                            selectedUnitDataModel.getDrp_officer_name_other(),
                            selectedUnitDataModel.getDrp_officer_desig(),
                            selectedUnitDataModel.getDrp_officer_desig_other(),
                            selectedUnitDataModel.getRemarks(),
                            selectedUnitDataModel.getRemarks_other(),
                            visit_count,
                            selectedUnitDataModel.getArea_name(),
                            selectedUnitDataModel.getWard_no(),
                            selectedUnitDataModel.getSector_no(),
                            selectedUnitDataModel.getZone_no(),
                            selectedUnitDataModel.getNagar_name(),
                            selectedUnitDataModel.getNagar_name_other(),
                            selectedUnitDataModel.getSociety_name(),
                            selectedUnitDataModel.getStreet_name(),
                            selectedUnitDataModel.getLandmark_name(),
                            selectedUnitDataModel.getPincode(),
                            new GregorianCalendar(),
                            lockFlag,
                            selectedUnitDataModel.getSurvey_date(),
                            selectedUnitDataModel.getSurvey_time(),
                            selectedUnitDataModel.isUploaded(),
                            selectedUnitDataModel.getCountry_name(),
                            selectedUnitDataModel.getState_name(),
                            selectedUnitDataModel.getCity_name(),
                            selectedUnitDataModel.getAccess_to_unit(),
                            selectedUnitDataModel.getResidential_area_sqft(),
                            selectedUnitDataModel.getCommercial_area_sqft(),
                            selectedUnitDataModel.getMobile_no_for_otp(),
                            selectedUnitDataModel.getOtp_sent(),
                            selectedUnitDataModel.getOtp_received(),
                            selectedUnitDataModel.getOtp_verified(),
                            selectedUnitDataModel.getOtp_remarks(),
                            selectedUnitDataModel.getOtp_remarks_other(),
                            selectedUnitDataModel.getThumb_remarks(),
                            selectedUnitDataModel.getLatitude(),
                            selectedUnitDataModel.getLongitude(),
                            selectedUnitDataModel.getGenesys_device_name(),
                            selectedUnitDataModel.getPrimary_imei_no(),
                            selectedUnitDataModel.getSecond_imei_no(),
                            selectedUnitDataModel.getIs_drppl_officer_available(),
                            selectedUnitDataModel.getDrppl_officer_name()
                    );
                    if(selectedUnitDataModel.getUnit_status().equalsIgnoreCase(Constants.NotApplicable_statusLayer)){
                        attributes.put("unit_usage", selectedUnitDataModel.getUnit_usage());
                    }
                } else {
                    try {
                        cal = Utils.getGregorianCalendarFromDate(selectedUnitDataModel.getExistence_since());
                        // unit_area_mtrs = Double.toString(selectedUnitDataModel.getUnit_area_sqft());
                        // loft_area_mtrs = Double.toString(selectedUnitDataModel.getLoft_area_sqft());
                        // visit_count= (short) unitInfoDataModel.getVisit_count();
                    } catch (Exception e) {
                        cal = null;
                    }
                    // all new values
                    String lfft = "YES";
                    if (selectedUnitDataModel.getLoft_present()) {
                        lfft = "YES";
                    } else {
                        lfft = "NO";
                    }
                    GregorianCalendar gc = null;
                    try {
                        Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(selectedUnitDataModel.getExistence_since() + " 00:00:00");
                        TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                        Locale loc = new Locale("en", "IN");
                        Calendar calendar = Calendar.getInstance(loc);
                        gc = (GregorianCalendar) calendar;
                        gc.setTimeZone(tz);
                        gc.setTime(date);
                    } catch (Exception ex) {
                        ex.getCause();
                    }
//RS
                    attributes = Utils.getUnitInfoDetailsAttributeV4(structurePointGlobalId,
                            selectedUnitDataModel.getUnit_id(),
                            selectedUnitDataModel.getUnit_unique_id(),
                            selectedUnitDataModel.getRelative_path(),
                            selectedUnitDataModel.getTenement_number(),
                            selectedUnitDataModel.getHut_number(),
                            selectedUnitDataModel.getFloor(),
                            selectedUnitDataModel.getUnit_no(),
                            selectedUnitDataModel.getUnit_usage(),
                            gc,
                            cal,
                            selectedUnitDataModel.getStructure_year(),
                            selectedUnitDataModel.getNature_of_activity(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            (selectedUnitDataModel.isElectric_bill_attached()) ? (short) 1 : (short) 0,
                            (short) 0,
                            (selectedUnitDataModel.isProperty_tax_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isRent_agreement_attached()) ? (short) 1 : (short) 0,
                            (short) 0,
                            (selectedUnitDataModel.isChain_document_attached()) ? (short) 1 : (short) 0,
                            (short) 0,
                            (selectedUnitDataModel.isOthers_attachment_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isNa_tax_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isElectoral_roll_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isPhoto_pass_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isShare_certificate_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isSchool_college_certificate_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isCertificate_issued_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isRestro_hotel_license_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isFactory_act_license_attached()) ? (short) 1 : (short) 0,
                            (selectedUnitDataModel.isFood_drug_license_attached()) ? (short) 1 : (short) 0,
                            "YES",
                            selectedUnitDataModel.getUnit_status(),
                            selectedUnitDataModel.getSurveyor_name(),
                            selectedUnitDataModel.getRemarks(),
                            selectedUnitDataModel.getRemarks_other(),
                            selectedUnitDataModel.getMedia_captured_cnt(),
                            selectedUnitDataModel.getMedia_uploaded_cnt(),
                            selectedUnitDataModel.getSurveyor_desig(),
                            selectedUnitDataModel.getDrp_officer_name(),
                            selectedUnitDataModel.getDrp_officer_name_other(),
                            selectedUnitDataModel.getDrp_officer_desig(),
                            selectedUnitDataModel.getDrp_officer_desig_other(),
                            visit_count,
                            selectedUnitDataModel.getArea_name(),
                            selectedUnitDataModel.getWard_no(),
                            selectedUnitDataModel.getSector_no(),
                            selectedUnitDataModel.getZone_no(),
                            selectedUnitDataModel.getNagar_name(),
                            selectedUnitDataModel.getNagar_name_other(),
                            selectedUnitDataModel.getSociety_name(),
                            selectedUnitDataModel.getStreet_name(),
                            selectedUnitDataModel.getLandmark_name(),
                            selectedUnitDataModel.getRespondent_name(),
                            selectedUnitDataModel.getRespondent_dob(),
                            selectedUnitDataModel.getRespondent_age(),
                            selectedUnitDataModel.getRespondent_hoh_contact(),
                            selectedUnitDataModel.getRespondent_hoh_relationship(),
                            selectedUnitDataModel.getRespondent_hoh_relationship_other(),
                            selectedUnitDataModel.getTenement_document(),
                            selectedUnitDataModel.getMashal_survey_number(),
                            selectedUnitDataModel.getOwnership_status(),
                            selectedUnitDataModel.getUnit_area_sqft(),
                            lfft,
                            selectedUnitDataModel.getLoft_area_sqft(),
                            selectedUnitDataModel.getEmployees_count(),
                            selectedUnitDataModel.getPincode(),
                            selectedUnitDataModel.getRespondent_mobile(),
                            selectedUnitDataModel.getGhumasta_area_sqft(),
                            new GregorianCalendar(),
                            // unitInfoDataModel.getForm_lock(),
                            lockFlag,
                            selectedUnitDataModel.isUploaded(),
                            selectedUnitDataModel.getRespondent_hoh_name(),
                            selectedUnitDataModel.getSurvey_date(),
                            selectedUnitDataModel.getSurvey_time(),
                            selectedUnitDataModel.getType_of_other_structure(),
                            selectedUnitDataModel.getRespondent_remarks(),
                            selectedUnitDataModel.getRespondent_non_available_remark(),
                            selectedUnitDataModel.getSurvey_pavti_no(),
                            selectedUnitDataModel.getExistence_since_year(),
                            selectedUnitDataModel.getCountry_name(),
                            selectedUnitDataModel.getState_name(),
                            selectedUnitDataModel.getCity_name(),
                            selectedUnitDataModel.getAccess_to_unit(),
                            selectedUnitDataModel.getResidential_area_sqft(),
                            selectedUnitDataModel.getCommercial_area_sqft(),
                            selectedUnitDataModel.getMobile_no_for_otp(),
                            selectedUnitDataModel.getOtp_sent(),
                            selectedUnitDataModel.getOtp_received(),
                            selectedUnitDataModel.getOtp_verified(),
                            selectedUnitDataModel.getOtp_remarks(),
                            selectedUnitDataModel.getOtp_remarks_other(),
                            selectedUnitDataModel.getThumb_remarks(),
                            selectedUnitDataModel.getStructure_type_religious(),
                            selectedUnitDataModel.getStructure_religious_other(),
                            selectedUnitDataModel.getStructure_type_amenities(),
                            selectedUnitDataModel.getStructure_amenities_other(),
                            selectedUnitDataModel.getName_of_structure(),
                            selectedUnitDataModel.getType_of_diety(),
                            selectedUnitDataModel.getType_of_diety_other(),
                            selectedUnitDataModel.getName_of_diety(),
                            selectedUnitDataModel.getCategory_of_faith(),
                            selectedUnitDataModel.getCategory_of_faith_other(),
                            selectedUnitDataModel.getSub_category_of_faith(),
                            selectedUnitDataModel.getReligion_of_structure_belongs(),
                            selectedUnitDataModel.getStructure_ownership_status(),
                            selectedUnitDataModel.getName_of_trust_or_owner(),
                            selectedUnitDataModel.getNature_of_structure(),
                            selectedUnitDataModel.getConstruction_material(),
                            selectedUnitDataModel.getDaily_visited_people_count(),
                            selectedUnitDataModel.getTenement_number_rel_amenities(),
                            selectedUnitDataModel.getTenement_doc_used(),
                            selectedUnitDataModel.getTenement_doc_other(),
                            selectedUnitDataModel.getIs_structure_registered(),
                            selectedUnitDataModel.getStructure_registered_with(),
                            selectedUnitDataModel.getOther_religious_authority(),
                            selectedUnitDataModel.getName_of_trustee(),
                            selectedUnitDataModel.getName_of_landowner(),
                            selectedUnitDataModel.getNoc_from_landlord_or_govt(),
                            selectedUnitDataModel.getApproval_from_govt(),
                            selectedUnitDataModel.getYearly_festival_conducted(),
                            selectedUnitDataModel.getSurvey_pavti_no_rel_amenities(),
                            selectedUnitDataModel.getMashal_rel_amenities(),
                            selectedUnitDataModel.getRa_total_no_of_floors(),
                            selectedUnitDataModel.getLatitude(),
                            selectedUnitDataModel.getLongitude(),
                            selectedUnitDataModel.getGenesys_device_name(),
                            selectedUnitDataModel.getPrimary_imei_no(),
                            selectedUnitDataModel.getSecond_imei_no(),
                            selectedUnitDataModel.getIs_drppl_officer_available(),
                            selectedUnitDataModel.getDrppl_officer_name()
                    );
                }

                if (selectedUnitDataModel.isUploaded()) {

                    if (selectedUnitDataModel.getUnit_status().equals(Constants.completed_statusLayer) || selectedUnitDataModel.getUnit_status().equals(Constants.completed_dispute)) {
                        attributes.put("hoh_availability", Short.parseShort("" + selectedUnitDataModel.getHoh_avaibility()));
                        attributes.put("annexure_remarks", selectedUnitDataModel.getAnnexure_remarks());
                        attributes.put("annexure_uploaded", selectedUnitDataModel.getAnnexure_uploaded());
                        GregorianCalendar gg = new GregorianCalendar();
                        // gg.add(Calendar.MINUTE,330);
                        attributes.put("annexure_upload_date", gg);
                        attributes.put("panchnama_form_remarks", selectedUnitDataModel.getPanchnama_form_remarks());
                    }

                    attributes.put(Constants.objectid, objectId);

                    if (!structurePointGlobalIdStr.startsWith("{")) {
                        structurePointGlobalIdStr = "{" + structurePointGlobalIdStr + "}";
                    }

                    attributes.put("rel_globalid", structurePointGlobalIdStr.toUpperCase());
                    attributes.put("activity_log","Mobile_Logged In "+username+"_UpdateNewUnit (Edit Unit)"+"_"+ Utils.getEpochDateStamp());
                    localSurveyDbViewModel.updateMediaInfo(objectId + "", selectedUnitDataModel.getGlobalId(), selectedUnitDataModel.getRelative_path());
                    attriutesUnitUpdateArray.add(attributes);

                } else {
                    if (selectedUnitDataModel.getUnit_status().equals(Constants.completed_statusLayer) || selectedUnitDataModel.getUnit_status().equals(Constants.completed_dispute)) {
                        attributes.put("hoh_availability", Short.parseShort("" + selectedUnitDataModel.getHoh_avaibility()));
                        attributes.put("annexure_remarks", selectedUnitDataModel.getAnnexure_remarks());
                        attributes.put("annexure_uploaded", selectedUnitDataModel.getAnnexure_uploaded());
                        GregorianCalendar gg = new GregorianCalendar();
                        attributes.put("annexure_upload_date", gg);
                        attributes.put("panchnama_form_remarks", selectedUnitDataModel.getPanchnama_form_remarks());
                    }
                    attributes.put("activity_log","Mobile_Logged In "+username+"_AddNewUnit"+"_"+ Utils.getEpochDateStamp());
                    selectedUnitDataModel.setNature_of_activity("add");
                    attriutesUnitNewArray.add(attributes);
                }
            } catch (Exception exception) {
                stopProcessWithError("Exception generateUnitData: " + exception.getMessage());
                return;
            }

            Log.w("Upload", "Attributes::attriutesUnitUpdateArray" + new Gson().toJson(attriutesUnitUpdateArray));
            Log.w("Upload", "Attributes::attriutesUnitNewArray" + new Gson().toJson(attriutesUnitNewArray));

            new Handler().postDelayed(() -> {
                if (!attriutesUnitUpdateArray.isEmpty()) {
                    App.getSharedPreferencesHandler().putString("activity_log_unit","Mobile_Logged In "+username+"_UpdateNewUnit (Edit Unit)"+"_"+ Utils.getEpochDateStamp());
                    uploadExistingUnitData(attriutesUnitUpdateArray, attriutesUnitNewArray);
                } else if (!attriutesUnitNewArray.isEmpty()) {
                    App.getSharedPreferencesHandler().putString("activity_log_unit","Mobile_Logged In "+username+"_AddNewUnit"+"_"+ Utils.getEpochDateStamp());
                    uploadNewUnitData(attriutesUnitNewArray);
                }
            }, UPLOAD_INTERVAL_MIN);
        } catch (Exception ex) {
            stopProcessWithError("Exception generateUnitData: " + ex.getMessage());
        }
    }

    private void uploadNewUnitData(ArrayList<Map<String, Object>> attriutesArray) {

        ServiceFeatureTable serviceFeatureTable = unitInfoFT;
        String s="unit_unique_id = "+selectedUnitDataModel.getUnit_id()+"\n"+"unit_hut_id = "+selectedUnitDataModel.getHut_number()+"\n"+"rel_global_id = "+selectedUnitDataModel.getRel_globalid()+"\n";
        saveParticularLogs("uploadNewUnitData_" + selectedUnitDataModel.getUnit_id(), s.toString());
        saveLogs("uploadNewUnitData_" + selectedUnitDataModel.getUnit_id(), new Gson().toJson(attriutesArray).toString());

        Log.d("New Upload", "New Upload :: uploadNewUnitData");

        serviceFeatureTable.addDoneLoadingListener(() -> {
            if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

                List<Feature> featureArrayList = new ArrayList<>();

                for (Map<String, Object> stringObjectMap : attriutesArray) {
                    Feature feature = serviceFeatureTable.createFeature();
                    feature.getAttributes().putAll(stringObjectMap);
                    featureArrayList.add(feature);
                }

                Log.i("uploadNewUnitDataFeatureData" + selectedUnitDataModel.getUnit_id(), new Gson().toJson(featureArrayList.toString()));
                // add the new feature to the feature table and to server

                if (serviceFeatureTable.canAdd()) {
                    ListenableFuture<Void> addFeatureFuture = serviceFeatureTable.addFeaturesAsync(featureArrayList);
                    addFeatureFuture.addDoneListener(() -> {

                        try {
                            addFeatureFuture.get();
                            if (addFeatureFuture.isDone()) {
                                ListenableFuture<List<FeatureEditResult>> applyEdits = serviceFeatureTable.applyEditsAsync();
                                applyEdits.addDoneListener(() -> {
                                    try {
                                        List<FeatureEditResult> editResults = applyEdits.get();
                                        if (editResults != null && !editResults.isEmpty()) {
                                            if (!editResults.get(0).hasCompletedWithErrors()) {
                                                for (int i = 0; i < editResults.size(); i++) {

                                                    String globalId = editResults.get(i).getGlobalId().substring(1, editResults.get(i).getGlobalId().length() - 1);

                                                    selectedUnitDataModel.setGlobalId(globalId);

                                                    localSurveyDbViewModel.updateUnitInfo("" + editResults.get(i).getObjectId(), globalId,
                                                            true, selectedUnitDataModel.getUnit_id());
                                                    localSurveyDbViewModel.updateMediaInfo("" + editResults.get(i).getObjectId(),
                                                            editResults.get(i).getGlobalId().substring(1, editResults.get(i).getGlobalId().length() - 1),
                                                            selectedUnitDataModel.getRelative_path());
                                                }

                                                Utils.shortToast("Unit Info Added Successfully.", activity);

                                                WorkAreaModel workAreaModel = App.getSharedPreferencesHandler().getWorkArea(Constants.selectedWorkAreaName);
                                                if (workAreaModel.getWork_area_status().equals(Constants.NotStarted_statusLayer)) {
                                                    updateWorkArea(workAreaModel);
                                                }
                                                new Handler().postDelayed(() -> generateMedia(Constants.unit_infoLayer), UPLOAD_INTERVAL_MIN);

                                            } else {
                                                stopProcessWithError("Unit Upload Completed With Error");
                                            }
                                        }
                                    } catch (ExecutionException | InterruptedException e) {
                                        stopProcessWithError("Exception:" + e.getMessage());
                                    }
                                });
                            } else {
                                stopProcessWithError("Unit Not Added Try Again");
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            stopProcessWithError("Exception:" + e.getMessage());
                        }
                    });
                } else {
                    stopProcessWithError("Error while adding record. Please try again later.");
                }
            }
        });
    }

    private void uploadExistingUnitData(ArrayList<Map<String, Object>> attriutesUnitUpdateArray, ArrayList<Map<String, Object>> attriutesNewUnitArray) {

        String s="unit_unique_id = "+selectedUnitDataModel.getUnit_id()+"\n"+"unit_hut_id = "+selectedUnitDataModel.getHut_number()+"\n"+"rel_global_id = "+selectedUnitDataModel.getRel_globalid()+"\n";
        saveParticularLogs("uploadNewUnitData_" + selectedUnitDataModel.getUnit_id(), s.toString());
        saveLogs("uploadExistingUnitData_" + selectedUnitDataModel.getUnit_id(), new Gson().toJson(attriutesUnitUpdateArray).toString());

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        List<UpdateFeatures.UpdateForm> array = new ArrayList<>();

        for (Map<String, Object> attributes : attriutesUnitUpdateArray) {
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attributes);
            array.add(updateForm);
        }

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initUpdateFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.Unit_info_ENDPOINT,
                GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

        queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            if (updatedRecordResponse != null && updatedRecordResponse.getAddResults() != null
                    && !updatedRecordResponse.getAddResults().isEmpty() && updatedRecordResponse.getAddResults().get(0).getSuccess()) {

                Utils.shortToast("Unit Info Updated Successfully.", activity);

                if (!attriutesNewUnitArray.isEmpty()) {
                    new Handler().postDelayed(() -> {
                        uploadNewUnitData(attriutesNewUnitArray);
                    }, UPLOAD_INTERVAL);
                } else {
                    new Handler().postDelayed(() -> {
                        WorkAreaModel workAreaModel = App.getSharedPreferencesHandler().getWorkArea(Constants.selectedWorkAreaName);
                        if (workAreaModel.getWork_area_status().equals(Constants.NotStarted_statusLayer)) {
                            updateWorkArea(workAreaModel);
                        }
                        generateMedia(Constants.unit_infoLayer);
                    }, UPLOAD_INTERVAL_MIN);
                }
            } else {
                updateSingleAttribute("Unit Not Uploaded Try Again");
//                stopProcessWithError("Unit Not Uploaded Try Again");
            }
        });
    }

    ////////////// HOH INFORMATION

    private void generateHOHData() {

        progressBar.setMessage("Uploading HOH");
        progressBar.setProgress(r.nextInt(50 - 35) + 35);
        listHohIds = new ArrayList<>();

        ArrayList<Map<String, Object>> attriutesHohAddArray = new ArrayList();
        ArrayList<Map<String, Object>> attriutesHohUpdateArray = new ArrayList();

        List<HohInfoDataModel> listHohInfoDataModel = localSurveyDbViewModel.getHohInfoData(selectedUnitDataModel.getUnit_id());

        String structurePointGlobalIdStr = selectedUnitDataModel.getGlobalId().toUpperCase();

        // Not getting GlobalId first Time.
        AppLog.d("structurePointGlobalIdStr: " + structurePointGlobalIdStr);
        if (!structurePointGlobalIdStr.startsWith("{")) {
            structurePointGlobalIdStr = "{" + selectedUnitDataModel.getGlobalId() + "}".toUpperCase();
        }

        AppLog.d("structurePointGlobalIdStr: " + structurePointGlobalIdStr);

        Object structurePointGlobalId = "";

        if (structurePointGlobalIdStr.startsWith("{"))
            structurePointGlobalId = UUID.fromString(structurePointGlobalIdStr.substring(1, structurePointGlobalIdStr.length() - 1));
        else
            structurePointGlobalId = UUID.fromString(structurePointGlobalIdStr);

        AppLog.d("structurePointGlobalId: " + structurePointGlobalId);

        // TODO: Commented loop and retrieving last index HOH data to avoid multiple HOH Entry..
        // for (HohInfoDataModel hohDetailsModel : hohInfoDataModel) {

        if (listHohInfoDataModel != null && !listHohInfoDataModel.isEmpty()) {

            HohInfoDataModel hohDetailsModel = listHohInfoDataModel.get(listHohInfoDataModel.size() - 1);

            listHohIds.add(hohDetailsModel.getHoh_id());

            AppLog.d("hohDetailsModel.getHoh_id(): " + hohDetailsModel.getHoh_id());

            int objectId = (int) Utils.doubleFormatter(hohDetailsModel.getObejctId());

            try {
                GregorianCalendar gc;
                Date date;
                if (hohDetailsModel.getHoh_dob().toString().equals("1/1/1970") || hohDetailsModel.getHoh_dob().toString().equals("01/01/1970")) {
                    gc = new GregorianCalendar();
                    gc.set(GregorianCalendar.YEAR, 1970);
                    gc.set(GregorianCalendar.MONTH, GregorianCalendar.JANUARY);
                    gc.set(GregorianCalendar.DAY_OF_MONTH, 1);
                } else {
                    date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(hohDetailsModel.getHoh_dob() + " 00:00:00");
                    TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                    Locale loc = new Locale("en", "IN");
                    Calendar calendar = Calendar.getInstance(loc);
                    gc = (GregorianCalendar) calendar;
                    gc.setTimeZone(tz);
                    gc.setTime(date);
                }
                //gc.add(Calendar.DATE, 1);

                Map<String, Object> attributes = Utils.getHohAttributeV4(
                        structurePointGlobalId,
                        hohDetailsModel.getHoh_id(),
                        hohDetailsModel.getRelative_path(),
                        hohDetailsModel.getHoh_name(),
                        hohDetailsModel.getMarital_status(),
                        hohDetailsModel.getMarital_status_other(),
                        (short) hohDetailsModel.getHoh_spouse_count(),
                        hohDetailsModel.getHoh_spouse_name(),
                        hohDetailsModel.getHoh_contact_no(),
                        gc,
                        (short) hohDetailsModel.getAge(),
                        hohDetailsModel.getGender(),
                        (short) hohDetailsModel.getStaying_since_year(),
                        hohDetailsModel.getAadhar_no(),
                        hohDetailsModel.getPan_no(),
                        hohDetailsModel.getRation_card_colour(),
                        hohDetailsModel.getRation_card_no(),
                        hohDetailsModel.getFrom_state(),
                        hohDetailsModel.getFrom_state_other(),
                        hohDetailsModel.getMother_tongue(),
                        hohDetailsModel.getMother_tongue_other(),
                        hohDetailsModel.getReligion(),
                        hohDetailsModel.getReligion_other(),
                        hohDetailsModel.getEducation(),
                        hohDetailsModel.getEducation_other(),
                        hohDetailsModel.getOccupation(),
                        hohDetailsModel.getOccupation_other(),
                        hohDetailsModel.getPlace_of_work(),
                        hohDetailsModel.getType_of_work(),
                        hohDetailsModel.getType_of_work_other(),
                        hohDetailsModel.getMonthly_income(),
                        hohDetailsModel.getMode_of_transport(),
                        hohDetailsModel.getMode_of_transport_other(),
                        hohDetailsModel.getSchool_college_name_location(),
                        hohDetailsModel.getHandicap_or_critical_disease(),
                        hohDetailsModel.getStaying_with(),
                        hohDetailsModel.getVehicle_owned_driven(),
                        hohDetailsModel.getVehicle_owned_driven_other(),
                        (short) hohDetailsModel.getDeath_certificate(),
                        (short) hohDetailsModel.getCount_of_other_members(),
                        hohDetailsModel.getAdhaar_verify_status(),
                        hohDetailsModel.getAdhaar_verify_remark(),
                        hohDetailsModel.getAdhaar_verify_date(),
                        hohDetailsModel.isUploaded());

                if (hohDetailsModel.isUploaded()) {

                    hohGlobalId = hohDetailsModel.getGlobalId();

                    localSurveyDbViewModel.updateHohIdForAadhaarVerification(hohGlobalId, selectedUnitDataModel.getUnit_unique_id());

                    attributes.put(Constants.objectid, objectId);
                    attributes.put("rel_globalid", structurePointGlobalIdStr.toUpperCase());
                    attributes.put("activity_log","Mobile_Logged In "+username+"_UpdateNewHoh (Edit Hoh)"+"_"+ Utils.getEpochDateStamp());
                    localSurveyDbViewModel.updateMediaInfo(objectId + "", hohDetailsModel.getGlobalId(), hohDetailsModel.getRelative_path());
                    attriutesHohUpdateArray.add(attributes);
                } else {
                    attributes.put("activity_log","Mobile_Logged In "+username+"_AddNewHoh"+"_"+ Utils.getEpochDateStamp());
                    attriutesHohAddArray.add(attributes);
                }
            } catch (Exception ex) {
                stopProcessWithError("Exception generateHOHData: " + ex.getMessage());
                return;
            }
        }

        Log.w("Upload", "Attributes::attriutesHohUpdateArray " + new Gson().toJson(attriutesHohUpdateArray));
        Log.w("Upload", "Attributes::attriutesHohAddArray " + new Gson().toJson(attriutesHohAddArray));

        new Handler().postDelayed(() -> {
            if (!attriutesHohUpdateArray.isEmpty()) {
                App.getSharedPreferencesHandler().putString("activity_log_hoh","Mobile_Logged In "+username+"_UpdateNewHoh (Edit Hoh)"+"_"+ Utils.getEpochDateStamp());
                uploadExistingHOHData(attriutesHohUpdateArray, attriutesHohAddArray);
            } else if (!attriutesHohAddArray.isEmpty()) {
                App.getSharedPreferencesHandler().putString("activity_log_hoh","Mobile_Logged In "+username+"_AddNewHoh"+"_"+ Utils.getEpochDateStamp());
                uploadNewHOHData(attriutesHohAddArray);
            } else {
                uploadAttachments(Constants.hoh_infoLayer, null, "generateHOHData");
            }
        }, UPLOAD_INTERVAL_MIN);
    }

    private void uploadNewHOHData(ArrayList<Map<String, Object>> attriutesHohAddArray) {

        saveLogs("uploadNewHOHData_" + listHohIds.get(0), new Gson().toJson(attriutesHohAddArray).toString());

        ServiceFeatureTable serviceFeatureTable = hohInfoFT;

        serviceFeatureTable.addDoneLoadingListener(() -> {
            if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

                List<Feature> featureArrayList = new ArrayList<>();

                for (Map<String, Object> stringObjectMap : attriutesHohAddArray) {
                    Feature feature = serviceFeatureTable.createFeature();
                    feature.getAttributes().putAll(stringObjectMap);
                    featureArrayList.add(feature);
                }

                Log.i("uploadNewHOHDataFeatureData" + selectedUnitDataModel.getUnit_id(), new Gson().toJson(featureArrayList.toString()));
                // add the new feature to the feature table and to server

                if (serviceFeatureTable.canAdd()) {
                    ListenableFuture<Void> addFeatureFuture = serviceFeatureTable.addFeaturesAsync(featureArrayList);
                    addFeatureFuture.addDoneListener(() -> {

                        try {
                            addFeatureFuture.get();
                            if (addFeatureFuture.isDone()) {
                                ListenableFuture<List<FeatureEditResult>> applyEdits = serviceFeatureTable.applyEditsAsync();
                                applyEdits.addDoneListener(() -> {
                                    try {
                                        List<FeatureEditResult> editResults = applyEdits.get();
                                        if (editResults != null && !editResults.isEmpty()) {
                                            if (!editResults.get(0).hasCompletedWithErrors()) {
                                                for (int i = 0; i < editResults.size(); i++) {

                                                    String globalId = editResults.get(i).getGlobalId().substring(1, editResults.get(i).getGlobalId().length() - 1);

                                                    hohGlobalId = globalId;

                                                    localSurveyDbViewModel.updateHohIdForAadhaarVerification(hohGlobalId, selectedUnitDataModel.getUnit_unique_id());

                                                    localSurveyDbViewModel.updateHOHInfo("" + editResults.get(i).getObjectId(), globalId, true,
                                                            attriutesHohAddArray.get(i).get("hoh_id").toString());

                                                    localSurveyDbViewModel.updateMediaInfo("" + editResults.get(i).getObjectId(),
                                                            editResults.get(i).getGlobalId().substring(1, editResults.get(i).getGlobalId().length() - 1),
                                                            attriutesHohAddArray.get(i).get("relative_path").toString());
                                                }

                                                Utils.shortToast("HOH Info Added Successfully.", activity);

                                                new Handler().postDelayed(() -> generateMedia(Constants.hoh_infoLayer), UPLOAD_INTERVAL_MIN);

                                            } else {
                                                updateSingleAttribute("HOH Upload Completed With Error");
//                                                stopProcessWithError("HOH Upload Completed With Error");
                                            }
                                        } else {
                                            updateSingleAttribute("HOH Upload Completed With Error");
//                                            stopProcessWithError("HOH Upload Completed With Error");
                                        }
                                    } catch (ExecutionException | InterruptedException e) {
                                        updateSingleAttribute("Exception:" + e.getMessage());
//                                        stopProcessWithError("Exception:" + e.getMessage());
                                    }
                                });
                            } else {
                                updateSingleAttribute("HOH Not Added Try Again");
//                                stopProcessWithError("HOH Not Added Try Again");
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            updateSingleAttribute("Exception:" + e.getMessage());
//                            stopProcessWithError("Exception:" + e.getMessage());
                        }
                    });
                } else {
                    updateSingleAttribute("Error while adding record. Please try again later.");
//                    stopProcessWithError("Error while adding record. Please try again later.");
                }
            }
        });
    }

    private void uploadExistingHOHData(ArrayList<Map<String, Object>> attriutesHohUpdateArray, ArrayList<Map<String, Object>> attriutesHohAddArray) {

        saveLogs("uploadExistingHOHData_" + listHohIds.get(0), new Gson().toJson(attriutesHohUpdateArray).toString());

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        List<UpdateFeatures.UpdateForm> array = new ArrayList<>();

        for (Map<String, Object> attributes : attriutesHohUpdateArray) {
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attributes);
            array.add(updateForm);
        }

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initUpdateFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.Hoh_info_ENDPOINT,
                GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

        queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            if (updatedRecordResponse != null && updatedRecordResponse.getAddResults() != null
                    && !updatedRecordResponse.getAddResults().isEmpty() && updatedRecordResponse.getAddResults().get(0).getSuccess()) {

                Utils.shortToast("HOH Info Updated Successfully.", activity);

                if (!attriutesHohAddArray.isEmpty()) {
                    new Handler().postDelayed(() -> {
                        uploadNewHOHData(attriutesHohAddArray);
                    }, UPLOAD_INTERVAL);
                } else {
                    new Handler().postDelayed(() -> {
                        generateMedia(Constants.hoh_infoLayer);
                    }, UPLOAD_INTERVAL_MIN);
                }
            } else {
                updateSingleAttribute("HOH Not Uploaded Try Again");
//                stopProcessWithError("HOH Not Uploaded Try Again");
            }
        });
    }

    ////////////// MEMBERS INFORMATION

    private void generateMembersData() {

        progressBar.setMessage("Uploading Members");
        progressBar.setProgress(r.nextInt(80 - 65) + 65);
        listMembersIds = new ArrayList<>();

        ArrayList<Map<String, Object>> attriutesMemberArray = new ArrayList();
        ArrayList<Map<String, Object>> attriutesMemberUpdateArray = new ArrayList();

        List<HohInfoDataModel> hohInfoDataModel = localSurveyDbViewModel.getHohInfoData(selectedUnitDataModel.getUnit_id());

        if (hohInfoDataModel == null || hohInfoDataModel.isEmpty()) {
            uploadAttachments(Constants.member_infoLayer, null, "generateMembersData");
            return;
        }

        HohInfoDataModel hohDetailsModel = hohInfoDataModel.get(hohInfoDataModel.size() - 1);

        // List<MemberInfoDataModel> memberInfoDataModels = localSurveyDbViewModel.getMemberInfoData(hohInfoDataModel.get(0).getHoh_id());
        List<MemberInfoDataModel> memberInfoDataModels = localSurveyDbViewModel.getMemberInfoData(hohDetailsModel.getHoh_id());

        Object globalId;

        if (!hohInfoDataModel.get(0).getGlobalId().isEmpty()) {
            hohGlobalId = hohInfoDataModel.get(0).getGlobalId();
        }

        String structurePointGlobalIdStr = hohGlobalId;

        if (!structurePointGlobalIdStr.startsWith("{"))
            structurePointGlobalIdStr = "{" + hohGlobalId + "}".toUpperCase();

        globalId = UUID.fromString(structurePointGlobalIdStr.substring(1, structurePointGlobalIdStr.length() - 1));

        for (MemberInfoDataModel memberDetailsModel : memberInfoDataModels) {

            listMembersIds.add(memberDetailsModel.getMember_id());

            int objectId = (int) Utils.doubleFormatter(memberDetailsModel.getObejctId());

            try {

                GregorianCalendar gc = null;
                Date date = null;

                if (memberDetailsModel.getMember_dob() != null && !memberDetailsModel.getMember_dob().isEmpty()) {

                    if (memberDetailsModel.getMember_dob().toString().equals("1/1/1970") || memberDetailsModel.getMember_dob().toString().equals("01/01/1970")) {
                        gc = new GregorianCalendar();
                        gc.set(GregorianCalendar.YEAR, 1970);
                        gc.set(GregorianCalendar.MONTH, GregorianCalendar.JANUARY);
                        gc.set(GregorianCalendar.DAY_OF_MONTH, 1);
                    } else {
                        date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(memberDetailsModel.getMember_dob() + " 00:00:00");
                        TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                        Locale loc = new Locale("en", "IN");
                        Calendar calendar = Calendar.getInstance(loc);
                        gc = (GregorianCalendar) calendar;
                        gc.setTimeZone(tz);
                        gc.setTime(date);
                    }
                }

                Map<String, Object> attributes = Utils.getMemberAttributeV4(
                        globalId,
                        memberDetailsModel.getMember_id(),
                        memberDetailsModel.getRelative_path(),
                        memberDetailsModel.getMember_name(),
                        memberDetailsModel.getRelationship_with_hoh(),
                        memberDetailsModel.getRelationship_with_hoh_other(),
                        memberDetailsModel.getMarital_status(),
                        memberDetailsModel.getMarital_status_other(),
                        (short) memberDetailsModel.getMember_spouse_count(),
                        memberDetailsModel.getMember_spouse_name(),
                        memberDetailsModel.getMember_contact_no(),
                        gc,
                        (short) memberDetailsModel.getAge(),
                        memberDetailsModel.getGender(),
                        (short) memberDetailsModel.getStaying_since_year(),
                        memberDetailsModel.getAadhar_no(),
                        memberDetailsModel.getPan_no(),
                        memberDetailsModel.getRation_card_colour(),
                        memberDetailsModel.getRation_card_no(),
                        memberDetailsModel.getFrom_state(),
                        memberDetailsModel.getFrom_state_other(),
                        memberDetailsModel.getMother_tongue(),
                        memberDetailsModel.getMother_tongue_other(),
                        memberDetailsModel.getReligion(),
                        memberDetailsModel.getReligion_other(),
                        memberDetailsModel.getEducation(),
                        memberDetailsModel.getEducation_other(),
                        memberDetailsModel.getOccupation(),
                        memberDetailsModel.getOccupation_other(),
                        memberDetailsModel.getPlace_of_work(),
                        memberDetailsModel.getType_of_work(),
                        memberDetailsModel.getType_of_work_other(),
                        memberDetailsModel.getMonthly_income(),
                        memberDetailsModel.getMode_of_transport(),
                        memberDetailsModel.getMode_of_transport_other(),
                        memberDetailsModel.getSchool_college_name_location(),
                        memberDetailsModel.getHandicap_or_critical_disease(),
                        memberDetailsModel.getStaying_with(),
                        memberDetailsModel.getVehicle_owned_driven(),
                        memberDetailsModel.getVehicle_owned_driven_other(),
                        memberDetailsModel.isUploaded(),
                        (short) memberDetailsModel.getDeath_certificate());

                if (memberDetailsModel.isUploaded()) {
                    attributes.put(Constants.objectid, objectId);
                    attributes.put("rel_globalid", structurePointGlobalIdStr.toUpperCase());
                    localSurveyDbViewModel.updateMediaInfo(objectId + "", memberDetailsModel.getGlobalId(), memberDetailsModel.getRelative_path());
                    attributes.put("activity_log","Mobile_Logged In "+username+"_UpdateNewMember (Edit Member)"+"_"+ Utils.getEpochDateStamp());
                    attriutesMemberUpdateArray.add(attributes);
                } else {
                    attributes.put("activity_log","Mobile_Logged In "+username+"_AddNewMember"+"_"+ Utils.getEpochDateStamp());
                    attriutesMemberArray.add(attributes);
                }
            } catch (Exception exception) {
                stopProcessWithError("Exception generateMemberData: " + exception.getMessage());
                return;
            }
        }

        Log.w("Upload", "Attributes::attriutesMemberUpdateArray " + new Gson().toJson(attriutesMemberUpdateArray));
        Log.w("Upload", "Attributes::attriutesMemberArray " + new Gson().toJson(attriutesMemberArray));

        new Handler().postDelayed(() -> {
            if (!attriutesMemberUpdateArray.isEmpty()) {
                App.getSharedPreferencesHandler().putString("activity_log_member","Mobile_Logged In "+username+"_UpdateNewMember (Edit Member)"+"_"+ Utils.getEpochDateStamp());
                uploadExistingMembersData(attriutesMemberUpdateArray, attriutesMemberArray);
            } else if (!attriutesMemberArray.isEmpty()) {
                App.getSharedPreferencesHandler().putString("activity_log_member","Mobile_Logged In "+username+"_AddNewMember"+"_"+ Utils.getEpochDateStamp());
                uploadNewMembersData(attriutesMemberArray);
            } else {
                uploadAttachments(Constants.member_infoLayer, null, "generateMembersData");
            }
        }, UPLOAD_INTERVAL_MIN);
    }

    private void uploadNewMembersData(ArrayList<Map<String, Object>> attriutesMemberAddArray) {

        ServiceFeatureTable serviceFeatureTable = memberInfoFT;

        saveLogs("uploadNewMembersData_" + listMembersIds.get(0).toString(), new Gson().toJson(attriutesMemberAddArray).toString());

        Log.d("New Upload", "New Upload :: uploadNewMembersData");

        serviceFeatureTable.addDoneLoadingListener(() -> {
            if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

                List<Feature> featureArrayList = new ArrayList<>();

                for (Map<String, Object> stringObjectMap : attriutesMemberAddArray) {
                    Feature feature = serviceFeatureTable.createFeature();
                    feature.getAttributes().putAll(stringObjectMap);
                    featureArrayList.add(feature);

                }
                Log.i("Update_newHoH_featureArrayList= ", " " + new Gson().toJson(featureArrayList.toString()));
                // add the new feature to the feature table and to server
                if (serviceFeatureTable.canAdd()) {
                    ListenableFuture<Void> addFeatureFuture = serviceFeatureTable.addFeaturesAsync(featureArrayList);
                    addFeatureFuture.addDoneListener(() -> {

                        try {
                            addFeatureFuture.get();
                            if (addFeatureFuture.isDone()) {
                                ListenableFuture<List<FeatureEditResult>> applyEdits = serviceFeatureTable.applyEditsAsync();
                                applyEdits.addDoneListener(() -> {
                                    try {
                                        List<FeatureEditResult> editResults = applyEdits.get();
                                        if (editResults != null && !editResults.isEmpty()) {
                                            if (!editResults.get(0).hasCompletedWithErrors()) {
                                                for (int i = 0; i < editResults.size(); i++) {

                                                    String globalId = editResults.get(i).getGlobalId().substring(1, editResults.get(i).getGlobalId().length() - 1);

                                                    localSurveyDbViewModel.updateMemberInfo("" + editResults.get(i).getObjectId(), globalId,
                                                            true, attriutesMemberAddArray.get(i).get("member_id").toString());

                                                    localSurveyDbViewModel.updateMediaInfo("" + editResults.get(i).getObjectId(),
                                                            editResults.get(i).getGlobalId().substring(1, editResults.get(i).getGlobalId().length() - 1),
                                                            attriutesMemberAddArray.get(i).get("relative_path").toString());
                                                }

                                                Utils.shortToast("Member Info Added Successfully.", activity);

                                                new Handler().postDelayed(() -> generateMedia(Constants.member_infoLayer), UPLOAD_INTERVAL_MIN);

                                            } else {
                                                updateSingleAttribute("Member Upload Completed With Error");
//                                                stopProcessWithError("Member Upload Completed With Error");
                                            }
                                        } else {
                                            updateSingleAttribute("Member Upload Completed With Error");
//                                            stopProcessWithError("Member Upload Completed With Error");
                                        }
                                    } catch (ExecutionException | InterruptedException e) {
                                        updateSingleAttribute("Exception:" + e.getMessage());
//                                        stopProcessWithError("Exception:" + e.getMessage());
                                    }
                                });
                            } else {
                                updateSingleAttribute("Member Not Added Try Again");
//                                stopProcessWithError("Member Not Added Try Again");
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            updateSingleAttribute("Exception:" + e.getMessage());
//                            stopProcessWithError("Exception:" + e.getMessage());
                        }
                    });
                } else {
                    updateSingleAttribute("Error while adding record. Please try again later.");
//                    stopProcessWithError("Error while adding record. Please try again later.");
                }
            }
        });
    }

    private void uploadExistingMembersData(ArrayList<Map<String, Object>> attriutesMembersUpdateArray, ArrayList<Map<String, Object>> attriutesMembersAddArray) {

        saveLogs("uploadExistingMembersData_" + listMembersIds.get(0).toString(), new Gson().toJson(attriutesMembersUpdateArray).toString());

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        List<UpdateFeatures.UpdateForm> array = new ArrayList<>();

        for (Map<String, Object> attributes : attriutesMembersUpdateArray) {
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attributes);
            array.add(updateForm);
        }

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initUpdateFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.MemberInfo_ENDPOINT,
                GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

        queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            if (updatedRecordResponse != null && updatedRecordResponse.getAddResults() != null
                    && !updatedRecordResponse.getAddResults().isEmpty() && updatedRecordResponse.getAddResults().get(0).getSuccess()) {

                Utils.shortToast("Member Info Updated Successfully.", activity);

                if (!attriutesMembersAddArray.isEmpty()) {
                    new Handler().postDelayed(() -> {
                        uploadNewMembersData(attriutesMembersAddArray);
                    }, UPLOAD_INTERVAL);
                } else {
                    new Handler().postDelayed(() -> {
                        generateMedia(Constants.member_infoLayer);
                    }, UPLOAD_INTERVAL_MIN);
                }
            } else {
                updateSingleAttribute("Member Not Uploaded Try Again");
//                stopProcessWithError("Member Not Uploaded Try Again");
            }
        });
    }

    ////////////// COMMON METHOD

    private void generateMedia(String layerType) {

        AppLog.d("generateMedia Uploading media Data::" + layerType);

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        try {
            ArrayList<Map<String, Object>> attriutesNewMediaArray = new ArrayList<>();
            ArrayList<Map<String, Object>> attriutesUpdateMediaArray = new ArrayList<>();

            final List<MediaInfoDataModel> mediaInfoDataModelArrayList;

            if (layerType.equalsIgnoreCase(Constants.unit_infoLayer)) {
                mediaIdArrayListToDelete.add(selectedUnitDataModel.getUnit_id());
                mediaInfoDataModelArrayList = localSurveyDbViewModel.getMediaInfoDataForUploadByTableName(selectedUnitDataModel.getUnit_id(), layerType);
                progressBar.setMessage("Uploading Unit Attachments");
                try {
                    progressBar.setProgress(r.nextInt(35 - 20) + 20);
                }catch (Exception ex){
                    ex.getMessage();
                }
            } else if (layerType.equalsIgnoreCase(Constants.hoh_infoLayer)) {
                new Handler().post(this::sendAadhaarVerificationDetails);
                mediaIdArrayListToDelete.add(listHohIds.get(0));
                mediaInfoDataModelArrayList = localSurveyDbViewModel.getMediaInfoDataForUploadByTableName(listHohIds.get(0), layerType);
                progressBar.setMessage("Uploading HOH Attachments");
                try {
                    progressBar.setProgress(r.nextInt(65 - 50) + 50);
                }catch (Exception ex){
                    ex.getMessage();
                }
            } else {
                List<MediaInfoDataModel> listMembersMedia = new ArrayList<>();
                for (String memberId : listMembersIds) {
                    mediaIdArrayListToDelete.add(memberId);
                    listMembersMedia.addAll(localSurveyDbViewModel.getMediaInfoDataForUploadByTableName(memberId, layerType));
                }
                mediaInfoDataModelArrayList = listMembersMedia;
                progressBar.setMessage("Uploading Members Attachments");
                try {
                    progressBar.setProgress(r.nextInt(95 - 80) + 80);
                }catch (Exception ex){
                    ex.getMessage();
                }
            }

            String url = "";

            for (MediaInfoDataModel mediaInfoDataModel : mediaInfoDataModelArrayList) {

                if (mediaInfoDataModel.getDocument_category().equalsIgnoreCase(Constants.UnitDistometerVideo)) {
                    url = getVideoPath(mediaInfoDataModel.getItem_url());
                } else {
                    url = mediaInfoDataModel.getItem_url();
                }

                if (mediaInfoDataModel.isLocal()) {
                    Map<String, Object> attributes = Utils.getMediaInfoAttribute(
                            mediaInfoDataModel.getContent_type(),
                            mediaInfoDataModel.getFilename(),
                            mediaInfoDataModel.getData_size(),
                            url,
                            mediaInfoDataModel.getFile_upload_checked(),
                            mediaInfoDataModel.getParent_table_name(),
                            mediaInfoDataModel.getParent_unique_id(),
                            "{" + mediaInfoDataModel.getRel_globalid() + "}", "",
                            mediaInfoDataModel.getGlobalId(), mediaInfoDataModel.getDocument_type(), mediaInfoDataModel.getDocument_category(),
                            mediaInfoDataModel.getName_on_document(), mediaInfoDataModel.getDocument_remarks());
                    attriutesNewMediaArray.add(attributes);
                } else {
                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("objectid", Long.parseLong(mediaInfoDataModel.getObejctId()));
                    attributes.put("document_remarks", mediaInfoDataModel.getDocument_remarks());
                    attriutesUpdateMediaArray.add(attributes);
                    updateMediaDetails(mediaInfoDataModel);
                }
            }

            Log.w("Upload", "Attributes::attriutesUpdateMediaArray " + layerType + " " + new Gson().toJson(attriutesUpdateMediaArray));
            Log.w("Upload", "Attributes::attriutesAddMediaArray " + layerType + " " + new Gson().toJson(attriutesNewMediaArray));

            new Handler().postDelayed(() -> {
                if (!attriutesUpdateMediaArray.isEmpty()) {
                    uploadUpdateMedia(layerType, mediaInfoDataModelArrayList, attriutesUpdateMediaArray, attriutesNewMediaArray);
                } else if (!attriutesNewMediaArray.isEmpty()) {
                    uploadAddMedia(layerType, mediaInfoDataModelArrayList, attriutesNewMediaArray);
                } else {
                    uploadAttachments(layerType, mediaInfoDataModelArrayList, "generateMedia");
                }
            }, UPLOAD_INTERVAL_MIN);
        } catch (Exception ex) {
            ex.printStackTrace();
            updateSingleAttribute("Exception in uploadMedia : " + ex.getMessage());
//            stopProcessWithError("Exception in uploadMedia : " + ex.getMessage());
        }
    }

    private void uploadAddMedia(String layerType, List<MediaInfoDataModel> mediaInfoDataModelArrayList,
                                ArrayList<Map<String, Object>> attriutesNewMediaArray) {

        saveLogs("uploadAddMedia_" + layerType, new Gson().toJson(attriutesNewMediaArray).toString());

        List<UpdateFeatures.UpdateForm> array = new ArrayList<>();
        for (Map<String, Object> attributes : attriutesNewMediaArray) {
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attributes);
            array.add(updateForm);
        }

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initAddFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.MediaInfo_ENDPOINT,
                GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

        queryResultRepoViewModel.getAddWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            if (updatedRecordResponse != null) {
                if (updatedRecordResponse.getAddResults() != null && !updatedRecordResponse.getAddResults().isEmpty()
                        && updatedRecordResponse.getAddResults().get(0).getSuccess()) {

                    for (int i = 0; i < updatedRecordResponse.getAddResults().size(); i++) {
                        mediaInfoDataModelArrayList.get(i).setObejctId(String.valueOf(updatedRecordResponse.getAddResults().get(i).getObjectId()));
                        mediaInfoDataModelArrayList.get(i).setGlobalId(String.valueOf(updatedRecordResponse.getAddResults().get(i).getGlobalId()));
                        localSurveyDbViewModel.updateMediaInfoObjectId(mediaInfoDataModelArrayList.get(i).getFilename(),
                                String.valueOf(updatedRecordResponse.getAddResults().get(i).getObjectId()),
                                String.valueOf(updatedRecordResponse.getAddResults().get(i).getGlobalId()));
                    }

                    new Handler().postDelayed(() -> uploadAttachments(layerType, mediaInfoDataModelArrayList, "uploadAddMedia"), UPLOAD_INTERVAL);

                } else {
                    updateSingleAttribute("Add Media Data Not Uploaded");
//                    stopProcessWithError("Add Media Data Not Uploaded");
                }
            } else {
                updateSingleAttribute("Add Media Data Not Uploaded");
//                stopProcessWithError("Add Media Data Not Uploaded");
            }
        });
    }

    private void uploadUpdateMedia(String layerType, List<MediaInfoDataModel> mediaInfoDataModelArrayList,
                                   ArrayList<Map<String, Object>> attriutesUpdateMediaArray,
                                   ArrayList<Map<String, Object>> attriutesNewMediaArray) {

        saveLogs("uploadUpdateMedia_" + layerType, new Gson().toJson(attriutesUpdateMediaArray).toString());

        List<UpdateFeatures.UpdateForm> array = new ArrayList<>();

        for (Map<String, Object> attributes : attriutesUpdateMediaArray) {
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attributes);
            array.add(updateForm);
        }

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initUpdateFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.MediaInfo_ENDPOINT,
                GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

        queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            if (updatedRecordResponse != null) {
                if (updatedRecordResponse.getAddResults() != null && !updatedRecordResponse.getAddResults().isEmpty() &&
                        updatedRecordResponse.getAddResults().get(0).getSuccess()) {

                    new Handler().postDelayed(() -> {
                        if (!attriutesNewMediaArray.isEmpty()) {
                            uploadAddMedia(layerType, mediaInfoDataModelArrayList, attriutesNewMediaArray);
                        } else {
                            uploadAttachments(layerType, mediaInfoDataModelArrayList, "uploadUpdateMedia");
                        }
                    }, UPLOAD_INTERVAL);
                } else {
                    updateSingleAttribute("Updated Media Data Not Uploaded. Kindly retry!!");
//                    stopProcessWithError("Updated Media Data Not Uploaded. Kindly retry!!");
                }
            } else {
                updateSingleAttribute("Updated Media Data Not Uploaded. Kindly retry!!");
//                stopProcessWithError("Updated Media Data Not Uploaded. Kindly retry!!");
            }
        });
    }

    private void uploadAttachments(String layerType, List<MediaInfoDataModel> mediaInfoDataModelArrayList, String from) {

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        // mapFailedUploadedUnits.clear();

        uploadParentPosition = -1;

        if (mediaInfoDataModelArrayList != null && !mediaInfoDataModelArrayList.isEmpty()) {
            saveLogs("uploadAttachments_" + layerType + "_" + from, new Gson().toJson(mediaInfoDataModelArrayList).toString());
            uploadParentMediaAttachments(layerType, mediaInfoDataModelArrayList);
        } else {
            saveLogs("uploadAttachments_" + layerType + "_" + from, "mediaInfoDataModelArrayList is blank / null");
            proceedAfterUploadDone(layerType, "No Attachments Available to upload. Check for Media Info for " + layerType, true, "");
        }
    }

    private void uploadParentMediaAttachments(String layerType, List<MediaInfoDataModel> mediaInfoDataModelArrayList) {

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        uploadParentPosition++;
        uploadFilePosition = 0;

        if (uploadParentPosition < mediaInfoDataModelArrayList.size()) {
            uploadMediaAttachments(layerType, mediaInfoDataModelArrayList);
        } else {
            proceedAfterUploadDone(layerType, "All Attachments have been uploaded for " + layerType, true, "");
        }
    }

    private void uploadMediaAttachments(String layerType, List<MediaInfoDataModel> mediaInfoDataModelArrayList) {

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        try {
            MediaInfoDataModel mediaInfoDataModel = mediaInfoDataModelArrayList.get(uploadParentPosition);

            // Added null and size check because of #1344
            if (mediaInfoDataModel.getUploadMediaList() != null && !mediaInfoDataModel.getUploadMediaList().isEmpty()) {

                if (uploadFilePosition < mediaInfoDataModel.getUploadMediaList().size()) {
                    String filePath = mediaInfoDataModel.getUploadMediaList().get(uploadFilePosition);
                    uploadFileToServer(layerType, mediaInfoDataModelArrayList, mediaInfoDataModel, filePath);
                } else {
                    uploadParentMediaAttachments(layerType, mediaInfoDataModelArrayList);
                }
            } else {
                saveLogs("uploadAttachments_" + layerType, "mediaInfoDataModel.getUploadMediaList().isEmpty()");
                uploadParentMediaAttachments(layerType, mediaInfoDataModelArrayList);
            }
        } catch (Exception e) {
            saveLogs("uploadAttachments_" + layerType, "mediaInfoDataModel.getUploadMediaList() Exception :: " + e.getMessage());
            updateSingleAttribute("Something went wrong while uploading attachments. Kindly retry!!");
//            stopProcessWithError("Something went wrong while uploading attachments. Kindly retry!!");
        }
    }

    public void uploadFileToServer(String layerType, List<MediaInfoDataModel> mediaInfoDataModelArrayList,
                                   MediaInfoDataModel mediaInfoDataModel, String filePath) {

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        File fileToUpload = new File(filePath);

        try {

            if (fileToUpload.exists()) {

                Log.w("Upload", "Upload : " + layerType + " >> uploadFileToServer :: Call for :: " + fileToUpload.getName());

                saveLogs("uploadAttachments_" + layerType, "Media File :: " + fileToUpload.getName());

                try {

                    CryptoUtilsTest.encryptFileinAES(fileToUpload, 2);

                    RequestBody requestBodyee = null;

                    try (InputStream in = new FileInputStream(filePath)) {
                        byte[] buf = new byte[in.available()];
                        while (in.read(buf) != -1)
                            ;  // Note: There's a semicolon here which might not be intended
                        requestBodyee = RequestBody.create(MediaType.parse("application/octet"), buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String filename = fileToUpload.getName().split("\\.")[0];
                    String fileNameExt = fileToUpload.getName().split("\\.")[1];

                    String offset = "0";
                    if (mediaInfoDataModel.getDocument_category().equalsIgnoreCase(Constants.UnitDistometerVideo)) {
                        offset = mediaInfoDataModel.isLocal() ? "0" : "1";
                    }

                    String relGlobalId = mediaInfoDataModel.getRel_globalid();

                    if (!relGlobalId.startsWith("{")) {
                        relGlobalId = "{" + relGlobalId + "}";
                    }

                    String remarks = mediaInfoDataModel.getDocument_remarks();
                    String documentCategory = mediaInfoDataModel.getDocument_category();
                    String documentType = mediaInfoDataModel.getDocument_type();
                    String objectId = mediaInfoDataModel.getObejctId();

                    String documentSource = "";

                    boolean isPdfCheckRequired = false;

                    if (layerType.equalsIgnoreCase(Constants.unit_infoLayer)) {

                        documentSource = "unit_info";

                        if (documentCategory.equalsIgnoreCase("Distometer")) {
                            documentSource = "unit_distometer";
                        } else if (documentCategory.equalsIgnoreCase("Unit_Layout")) {
                            documentSource = "unit_layout";
                            isPdfCheckRequired = true;
                        } else if (documentCategory.equalsIgnoreCase("unit_layout_rc")) {
                            documentSource = "unit_layout_rc";
                            isPdfCheckRequired = true;
                        } else if (documentCategory.equalsIgnoreCase("Notice_Pasted")) {
                            documentSource = "unit_notice";
                        } else if (documentCategory.equalsIgnoreCase("Annexure")) {
                            documentSource = "unit_annexure";
                        } else if (documentCategory.equalsIgnoreCase("Thumb_Impression")) {
                            documentSource = "unit_thumb";
                        }

                        // booth_signed_docs
                    } else if (layerType.equalsIgnoreCase(Constants.hoh_infoLayer)) {
                        documentSource = "hoh_info";
                    } else if (layerType.equalsIgnoreCase(Constants.member_infoLayer)) {
                        documentSource = "member_info";
                    }

                    if (isPdfCheckRequired && !Utils.isValidPdf(filePath)) {
                        String extraMessage = "Seems attached " + fileToUpload.getName() + " pdf file is Corrupted Or Tampered. Kindly retry with any other pdf file!!";
                        proceedAfterUploadDoneForError(layerType, "File " + fileToUpload.getName() + " is Corrupted Or Tampered.", false, extraMessage);
                        return;
                    }

                    JSONObject jObjRequest = new JSONObject();
                    jObjRequest.put("Offset", offset);
                    jObjRequest.put("documentSource", documentSource);
                    jObjRequest.put("documentCategory", documentCategory);
                    jObjRequest.put("documentType", documentType);
                    jObjRequest.put("relGlobalId", relGlobalId);
                    jObjRequest.put("remarks", remarks);
                    jObjRequest.put("filename", filename);
                    jObjRequest.put("objectId", objectId);
                    jObjRequest.put("fileNameExt", fileNameExt);
                    jObjRequest.put("filePath", filePath);
                    jObjRequest.put("fileSize", fileToUpload.length());

                    saveLogs("uploadAttachments_Upload_Request_" + layerType + "_" + filename, jObjRequest.toString());

                    AppLog.logCustomData(activity, filename, "Uploaded Attachment >> " + jObjRequest);

                    queryResultRepoViewModel.getUploadAttachmentsResult(activity, Constants.Upload_Video, "1", offset,
                            String.valueOf(fileToUpload.length()), userModel.getUser_name(), "." + fileNameExt,
                            documentSource, documentCategory, documentType, relGlobalId, remarks, filename, objectId, requestBodyee);

                    queryResultRepoViewModel.getUplaodMediaAttachmentMutableLiveData().observe(getActivity(), uploadVideoModel -> {

                        if (uploadVideoModel != null && uploadVideoModel.getStatus() != null && uploadVideoModel.getStatus().getStatus() == 1) {

                            new Handler().post(() -> Utils.shortToast("File" + " uploaded " + fileToUpload.getName(), activity));

                            saveLogs("uploadAttachments_Upload_Response_" + layerType + "_" + filename, new Gson().toJson(uploadVideoModel));

                            Log.w("Upload123", "uploadAttachments_Upload_Response_" + layerType + "_" + filename
                                    + "_" + new Gson().toJson(uploadVideoModel));

                            try {
                                CryptoUtilsTest.encryptFileinAES(fileToUpload, 1);
                            } catch (CryptoException e) {
                                throw new RuntimeException(e);
                            }

                            mediaInfoDataModel.setUploaded(true);
                            mediaInfoDataModel.setFile_upload_checked((short) 1);
                            mediaInfoDataModel.setServerFileDirectoryPath(uploadVideoModel.data.getFilePath());
//                            mediaInfoDataModel.setServerFileSize(uploadVideoModel.data.getFileSize()); suggested by Ajay Mehta
                            mediaInfoDataModel.setServerFileSize((int) fileToUpload.length());

                            // double actualFileSize = (double) mediaInfoDataModel.getData_size();
                            // double serverFileSize = (double) mediaInfoDataModel.getServerFileSize();

                            // double sizeMatchPercentage = (serverFileSize * 100) / actualFileSize;

                            // if (sizeMatchPercentage > 90) {

                            localSurveyDbViewModel.updateMediaIsUploadedInfo(mediaInfoDataModel.getObejctId(), true, mediaInfoDataModel.getMediaId());

                            if (mediaInfoDataModel.getDocument_category().equalsIgnoreCase(Constants.UnitDistometerVideo)) {
                                if (mediaInfoDataModel.isLocal()) {
                                    insertMediaDetails(mediaInfoDataModel, uploadVideoModel.data.getFileSize(), fileToUpload);
                                }
                            } else {
                                insertMediaDetails(mediaInfoDataModel, uploadVideoModel.data.getFileSize(), fileToUpload);
                            }

                            uploadFilePosition++;
                            Log.w("Upload", "Upload : " + layerType + " >> uploadFileToServer :: Call for :: Next");
                            uploadMediaAttachments(layerType, mediaInfoDataModelArrayList);
                          /*  } else {
                                saveLogs("uploadAttachments_" + layerType, "Server File Size and Actual File Size Percentage is too low for " + fileToUpload.getPath());
                                String extraMessage = "Layer Type :: " + layerType + " Server File Size and Actual File Size Percentage is too low for " + fileToUpload.getPath();
                                proceedAfterUploadDone(layerType, "File " + fileToUpload.getName() + " not uploaded.", false, extraMessage);
                            }*/
                        } else {
                            if (uploadVideoModel == null) {
                                saveLogs("uploadAttachments_" + layerType, "Retrieved null response for " + fileToUpload.getPath());
                                String extraMessage = "Layer Type :: " + layerType + " Retrieved null response for " + fileToUpload.getPath();
                                proceedAfterUploadDoneForError(layerType, "File " + fileToUpload.getName() + " not uploaded.", false, extraMessage);
                            } else if (uploadVideoModel.getStatus() == null) {
                                saveLogs("uploadAttachments_" + layerType, "Retrieved null status for " + fileToUpload.getPath());
                                String extraMessage = "Layer Type :: " + layerType + " Retrieved null status for " + fileToUpload.getPath();
                                proceedAfterUploadDoneForError(layerType, "File " + fileToUpload.getName() + " not uploaded.", false, extraMessage);
                            } else if (uploadVideoModel.getStatus().getStatus() != 1) {

                                String extraMessage;

                                if (uploadVideoModel.getStatus().getCode().equalsIgnoreCase("SERVER_ERROR")) {

                                    saveLogs("uploadAttachments_" + layerType, "SERVER ERROR is :: "
                                            + uploadVideoModel.getStatus().getStatus() + "/" + uploadVideoModel.getStatus().getMessage()
                                            + " for " + fileToUpload.getPath());

                                    extraMessage = "Layer Type :: " + layerType + " SERVER ERROR is :: " + uploadVideoModel.getStatus().getStatus()
                                            + "/" + uploadVideoModel.getStatus().getMessage() + " for " + fileToUpload.getPath();

                                } else {

                                    saveLogs("uploadAttachments_" + layerType, "Retrieved Status Code and Message is " +
                                            uploadVideoModel.getStatus().getStatus() + "/" + uploadVideoModel.getStatus().getMessage()
                                            + " for " + fileToUpload.getPath());

                                    extraMessage = "Layer Type :: " + layerType + " Retrieved Status Code and Message is " +
                                            uploadVideoModel.getStatus().getStatus() + "/" + uploadVideoModel.getStatus().getMessage()
                                            + " for " + fileToUpload.getPath();
                                }

                                proceedAfterUploadDoneForError(layerType, "File " + fileToUpload.getName() + " not uploaded.", false, extraMessage);
                            }

                            localSurveyDbViewModel.updateMediaIsUploadedInfo(mediaInfoDataModel.getObejctId(), false, mediaInfoDataModel.getMediaId());
                        }
                    });
                } catch (CryptoException e) {
                    localSurveyDbViewModel.updateMediaIsUploadedInfo(mediaInfoDataModel.getObejctId(), false, mediaInfoDataModel.getMediaId());
                    saveLogs("uploadAttachments_" + layerType, "CryptoException :: " + e.getMessage());
                    String extraMessage = "Layer Type :: " + layerType + " Crypto Exception is " + e.getMessage() + " for " + fileToUpload.getPath();
                    proceedAfterUploadDoneForError(layerType, "File " + fileToUpload.getName() + " not uploaded.", false, extraMessage);
                }
            } else {
                localSurveyDbViewModel.updateMediaIsUploadedInfo(mediaInfoDataModel.getObejctId(), false, mediaInfoDataModel.getMediaId());
                saveLogs("uploadAttachments_" + layerType, fileToUpload.getPath() + " File Not Exists");
                String extraMessage = "Layer Type :: " + layerType + " File Not Exists " + fileToUpload.getPath();
                proceedAfterUploadDoneForError(layerType, "File " + fileToUpload.getName() + " not uploaded.", false, extraMessage);
            }
        } catch (Exception ex) {
            localSurveyDbViewModel.updateMediaIsUploadedInfo(mediaInfoDataModel.getObejctId(), false, mediaInfoDataModel.getMediaId());
            String extraMessage = "Layer Type :: " + layerType + " Exception is " + ex.getMessage() + " for " + fileToUpload.getPath();
            proceedAfterUploadDoneForError(layerType, "File " + fileToUpload.getName() + " not uploaded.", false, extraMessage);
        }
    }

    public void displayUploadFailedMessageDialog(String subHeader, String message) {

        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.dialog_message, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView txtSubHeader = customLayout.findViewById(R.id.txtSubHeader);
        txtSubHeader.setText(subHeader);

        TextView txtMessage = customLayout.findViewById(R.id.txtMessage);
        txtMessage.setText(message);

        Button btnOk = customLayout.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void proceedAfterUploadDone(String layerType, String message, boolean isAllUploaded, String extraMessage) {
        try{
            if (isAllUploaded) {
                generateMediaDetails(layerType);
            } else {
                saveLogs("uploadAttachments_" + layerType, "Upload Failed Media Upload :: Is Single Media Upload? " + isSingleUploadCall);

//            stopProcessWithError(message);
                updateSingleAttribute(message);

                if (isSingleUploadCall) {
                    displayUploadFailedMessageDialog(message, extraMessage);
                } else {
                    listenUploadCounter.setValue(fileToUpload++);
                }
            }
        }catch(Exception ex){
            ex.getMessage();
        }
    }

    public void insertMediaDetails(MediaInfoDataModel mediaInfoDataModel, int fileSize, File file) {

        String filename = file.getName().split("\\.")[0];
        String fileNameExt = file.getName().split("\\.")[1];

        String relGlobalId = mediaInfoDataModel.getGlobalId();

        if (!relGlobalId.startsWith("{")) {
            relGlobalId = "{" + relGlobalId + "}";
        }

        MediaDetailsDataModel mediaDetailsDataModel = new MediaDetailsDataModel("", "",
                Utils.getContentType(file.getPath()), mediaInfoDataModel.getServerFileDirectoryPath(),
                filename, "." + fileNameExt, relGlobalId, fileSize, mediaInfoDataModel.getDocument_remarks(),
                selectedUnitDataModel.getUnit_id(), false, false);

        localSurveyDbViewModel.insertMediaDetailsPointData(mediaDetailsDataModel);
    }

    public void updateMediaDetails(MediaInfoDataModel mediaInfoDataModel) {
        localSurveyDbViewModel.updateMediaDetailsRemarksByRelGlobalId(mediaInfoDataModel.getGlobalId().toLowerCase(),
                mediaInfoDataModel.getDocument_remarks(), selectedUnitDataModel.getUnit_id(), false);
    }

    public void generateMediaDetails(String layerType) {

        Log.w("Upload", "Upload : " + layerType + " >> generateMediaDetails Call");

        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }

        try {

            // Get and Update Media Details to Update Remarks
            List<MediaDetailsDataModel> listPendingUpdate = localSurveyDbViewModel.getPendingMediaDetails(false, true,
                    selectedUnitDataModel.getUnit_id());
            ArrayList<Map<String, Object>> attributesUpdateMediaDetailsArray = new ArrayList<>();

            for (MediaDetailsDataModel mediaDetailsDataModel : listPendingUpdate) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("objectid", Long.parseLong(mediaDetailsDataModel.getObjectid()));
                attributes.put("remarks", mediaDetailsDataModel.getRemarks());
                attributesUpdateMediaDetailsArray.add(attributes);
            }

            saveLogs("generateMediaDetails_Update_" + layerType, new Gson().toJson(listPendingUpdate));

            // Get and Add New Media Details
            List<MediaDetailsDataModel> listPendingUpload = localSurveyDbViewModel.getPendingMediaDetails(false, false,
                    selectedUnitDataModel.getUnit_id());
            ArrayList<Map<String, Object>> attributesNewMediaDetailsArray = new ArrayList<>();

            for (MediaDetailsDataModel mediaDetailsDataModel : listPendingUpload) {

                String fileNameExt = mediaDetailsDataModel.getFile_ext().startsWith(".") ?
                        mediaDetailsDataModel.getFile_ext() : ("." + mediaDetailsDataModel.getFile_ext());

                Map<String, Object> attributes = Utils.getMediaDetailsAttribute(mediaDetailsDataModel.getContent_type(),
                        mediaDetailsDataModel.getFile_path(), mediaDetailsDataModel.getFile_name(),
                        fileNameExt, 1, mediaDetailsDataModel.getData_size(),
                        mediaDetailsDataModel.getRemarks(), mediaDetailsDataModel.getRel_globalid());
                attributesNewMediaDetailsArray.add(attributes);
            }

            saveLogs("generateMediaDetails_Add_" + layerType, new Gson().toJson(listPendingUpload));

            Log.w("Upload", "Attributes::attributesUpdateMediaDetailsArray " + layerType + " " + new Gson().toJson(attributesUpdateMediaDetailsArray));
            Log.w("Upload", "Attributes::attributesNewMediaDetailsArray " + layerType + " " + new Gson().toJson(attributesNewMediaDetailsArray));

            new Handler().postDelayed(() -> {
                if (!attributesUpdateMediaDetailsArray.isEmpty()) {
                    uploadUpdateMediaDetails(layerType, attributesUpdateMediaDetailsArray, attributesNewMediaDetailsArray, listPendingUpdate, listPendingUpload);
                } else if (!attributesNewMediaDetailsArray.isEmpty()) {
                    uploadAddMediaDetails(layerType, attributesNewMediaDetailsArray, listPendingUpload);
                } else {
                    proceedWithNextDataUpload(layerType);
                }
            }, UPLOAD_INTERVAL_MIN);
        } catch (Exception ex) {
            updateSingleAttribute("Exception in uploadMediaDetails : " + ex.getMessage());
//            stopProcessWithError("Exception in uploadMediaDetails : " + ex.getMessage());
        }
    }

    public void uploadUpdateMediaDetails(String layerType, ArrayList<Map<String, Object>> attriutesUpdateMediaArray,
                                         ArrayList<Map<String, Object>> attriutesNewMediaArray,
                                         List<MediaDetailsDataModel> listPendingUpdate,
                                         List<MediaDetailsDataModel> listPendingUpload) {

        saveLogs("uploadUpdateMediaDetails_" + layerType, new Gson().toJson(attriutesUpdateMediaArray).toString());

        List<UpdateFeatures.UpdateForm> array = new ArrayList<>();

        for (Map<String, Object> attributes : attriutesUpdateMediaArray) {
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attributes);
            array.add(updateForm);
        }

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initUpdateFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.MediaDetail_ENDPOINT,
                GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

        queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            if (updatedRecordResponse != null) {
                if (updatedRecordResponse.getAddResults() != null && !updatedRecordResponse.getAddResults().isEmpty() &&
                        updatedRecordResponse.getAddResults().get(0).getSuccess()) {

                    for (int i = 0; i < updatedRecordResponse.getAddResults().size(); i++) {
                        localSurveyDbViewModel.setMediaDetailsUploaded(true, true,
                                String.valueOf(updatedRecordResponse.getAddResults().get(i).getGlobalId()),
                                String.valueOf(updatedRecordResponse.getAddResults().get(i).getObjectId()),
                                listPendingUpdate.get(i).getFile_name());
                    }

                    Utils.shortToast("Media Details for " + layerType + " has been updated successfully.", activity);

                    new Handler().postDelayed(() -> {
                        if (!attriutesNewMediaArray.isEmpty()) {
                            uploadAddMediaDetails(layerType, attriutesNewMediaArray, listPendingUpload);
                        } else {
                            proceedWithNextDataUpload(layerType);
                        }
                    }, UPLOAD_INTERVAL);
                } else {
                    updateSingleAttribute("Updated Media Data Not Uploaded. Kindly retry!!");
//                    stopProcessWithError("Updated Media Data Not Uploaded. Kindly retry!!");
                }
            } else {
                updateSingleAttribute("Updated Media Data Not Uploaded. Kindly retry!!");
//                stopProcessWithError("Updated Media Data Not Uploaded. Kindly retry!!");
            }
        });
    }

    public void uploadAddMediaDetails(String layerType, ArrayList<Map<String, Object>> attriutesNewMediaArray, List<MediaDetailsDataModel> listPendingUpload) {

        saveLogs("uploadAddMediaDetails_" + layerType, new Gson().toJson(attriutesNewMediaArray).toString());

        List<UpdateFeatures.UpdateForm> array = new ArrayList<>();
        for (Map<String, Object> attributes : attriutesNewMediaArray) {
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attributes);
            array.add(updateForm);
        }

        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initAddFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.MediaDetail_ENDPOINT,
                GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

        queryResultRepoViewModel.getAddWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            if (updatedRecordResponse != null) {
                if (updatedRecordResponse.getAddResults() != null && !updatedRecordResponse.getAddResults().isEmpty()
                        && updatedRecordResponse.getAddResults().get(0).getSuccess()) {

                    for (int i = 0; i < updatedRecordResponse.getAddResults().size(); i++) {
                        localSurveyDbViewModel.setMediaDetailsUploaded(true, true,
                                String.valueOf(updatedRecordResponse.getAddResults().get(i).getGlobalId()),
                                String.valueOf(updatedRecordResponse.getAddResults().get(i).getObjectId()),
                                listPendingUpload.get(i).getFile_name());
                    }

                    Utils.shortToast("Media Details for " + layerType + " has been added successfully.", activity);

                    new Handler().postDelayed(() -> proceedWithNextDataUpload(layerType), UPLOAD_INTERVAL);

                } else {
                    updateSingleAttribute("New Upload :: Add Media Data Not Uploaded. Kindly retry!!");
//                    stopProcessWithError("New Upload :: Add Media Data Not Uploaded. Kindly retry!!");
                }
            } else {
                updateSingleAttribute("New Upload :: Add Media Data Not Uploaded. Kindly retry!!");
//                stopProcessWithError("New Upload :: Add Media Data Not Uploaded. Kindly retry!!");
            }
        });
    }

    public void sendAadhaarVerificationDetails() {

        try {

            JSONObject jObjAadhaarVerification = new JSONObject();

            AadhaarVerificationData aadhaarVerificationData = localSurveyDbViewModel.getAadhaarVerificationDetailsByHohId(selectedUnitDataModel.getUnit_unique_id(),
                    false);

            if (aadhaarVerificationData == null) {
                saveLogs("AadhaarVerificationData_DbData", "No Aadhaar Verification Data Available");
                return;
            }

            jObjAadhaarVerification.put("unitId", aadhaarVerificationData.getUnit_id());

            String hohId = aadhaarVerificationData.getHoh_id();

            if(!hohId.startsWith("{"))
                hohId = "{" + hohId + "}";

            jObjAadhaarVerification.put("hohId", hohId);
            jObjAadhaarVerification.put("hohAdhaarNo", aadhaarVerificationData.getHoh_adhaar_no());
            jObjAadhaarVerification.put("genOtpTransactionId", aadhaarVerificationData.getGen_otp_transaction_id());
            jObjAadhaarVerification.put("genOtpTimestamp", aadhaarVerificationData.getGen_otp_timestamp());
            jObjAadhaarVerification.put("genOtpRefId", aadhaarVerificationData.getGen_otp_ref_id());
            jObjAadhaarVerification.put("genOtpMessage", aadhaarVerificationData.getGen_otp_message());
            jObjAadhaarVerification.put("verOtpTransactionId", aadhaarVerificationData.getVer_otp_transaction_id());
            jObjAadhaarVerification.put("verOtpTimestamp", aadhaarVerificationData.getVer_otp_timestamp());
            jObjAadhaarVerification.put("verOtpRefId", aadhaarVerificationData.getVer_otp_ref_id());
            jObjAadhaarVerification.put("verOtpStatus", aadhaarVerificationData.getVer_otp_status());
            jObjAadhaarVerification.put("verOtpMessage", aadhaarVerificationData.getVer_otp_message());
            jObjAadhaarVerification.put("validateConfidence", aadhaarVerificationData.getValidate_confidence());
            jObjAadhaarVerification.put("validateStatus", aadhaarVerificationData.getValidate_status());
            jObjAadhaarVerification.put("validateDifferences", aadhaarVerificationData.getValidate_differences());
            jObjAadhaarVerification.put("adhaarVerifyBy", aadhaarVerificationData.getAdhaar_verify_by());

            saveLogs("AadhaarVerificationData_Request", jObjAadhaarVerification.toString());

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jObjAadhaarVerification.toString());

            Api_Interface apiInterface = RetrofitService.getDomainClient().create(Api_Interface.class);
            Call<JsonElement> call = apiInterface.saveUpdateAadhaarVerifyDetails(body);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                    Utils.dismissProgress();

                    try {

                        saveLogs("AadhaarVerificationData_Response", new Gson().toJson(response.body()));

                        if (response.code() == 200 && response.body() != null) {

                            JSONObject jObjRoot = new JSONObject(response.body().toString());
                            JSONObject jObjStatus = jObjRoot.getJSONObject("status");

                            if (jObjStatus.getString("status").equalsIgnoreCase("1")) {
                                localSurveyDbViewModel.updateAadhaarDetailsVerificationUploaded(selectedUnitDataModel.getUnit_unique_id(), true);
                            }
                        }
                    } catch (Exception e) {
                        AppLog.e(e.getMessage());
                        saveLogs("AadhaarVerificationData_Response_Ex", e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    call.cancel();
                    saveLogs("AadhaarVerificationData_Response_Ex", t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            saveLogs("AadhaarVerificationData_Response_Ex", e.getMessage());
        }
    }

    public void proceedWithNextDataUpload(String layerType) {

        if (layerType.equalsIgnoreCase(Constants.unit_infoLayer)) {
            Log.w("Upload", "Upload : Go for HOH Upload");
            new Handler().postDelayed(this::generateHOHData, UPLOAD_INTERVAL_MIN);
        } else if (layerType.equalsIgnoreCase(Constants.hoh_infoLayer)) {
            Log.w("Upload", "Upload : Go for Member Upload");
            new Handler().postDelayed(this::generateMembersData, UPLOAD_INTERVAL_MIN);
        } else if (layerType.equalsIgnoreCase(Constants.member_infoLayer)) {
            Log.w("Upload", "Upload : Set Delete Item Call");
            setDeleteItem();
        }
    }

    private String getVideoPath(String filePath) {
        int startIndex = filePath.indexOf("s_");
        String substring = "";
        if (startIndex != -1) { // Check if "s_" exists in the string
            // Find the ending index of "teri", starting from the index after "s_"
            int endIndex = filePath.indexOf("/unit_", startIndex + 2); // Adding 2 to startIndex to skip "s_" itself
            if (endIndex != -1) { // Check if "teri" exists in the string
                // Extract the substring between "s_" and "teri"
                substring = filePath.substring(startIndex, endIndex); // Adding 4 to endIndex to include "teri"
            }
        }
        return substring;
    }

    private void redirectToLogin() {
        Utils.shortToast("User details not found. Kindly login again.", activity);
        AppLog.logData(activity, "User details not found. Kindly login again.");
        activity.finishAffinity();
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }

    //////////Generate Zip File Code
    public void generateUploadDataForZip() {

        try {

            filePathArray.clear();
            JSONArray UploadDataArray = new JSONArray();

            int objectId = 0;
            String structurePointGlobalIdStr = "";
            Map<String, Object> attStructure = new HashMap<>();

            // Map<String, List<UnitInfoDataModel>> unitList = new HashMap<>();
            // Map<String, List<HohInfoDataModel>> holList = new HashMap<>();
            // Map<String, List<MemberInfoDataModel>> memberList = new HashMap<>();
            Map<String, String> structureUnitGlobalId = new HashMap<>();

            JSONObject dataObject = new JSONObject();
            localSurveyDbViewModel = ViewModelProviders.of(getActivity()).get(LocalSurveyDbViewModel.class);
            List<StructureInfoPointDataModel> structureInfoList = localSurveyDbViewModel.getStructureInfoPointDataZip(userModel.getUser_name());
            for (StructureInfoPointDataModel structurePointData : structureInfoList) {
                dataObject = new JSONObject();
                try {
                    objectId = (int) Double.parseDouble(structurePointData.getObejctId());
                } catch (Exception ignored) {
                    objectId = 0;
                }
                attStructure = Utils.getStructurePointAttributeV4(
                        structurePointData.getStructure_id(),
                        structurePointData.getGrid_number(),
                        structurePointData.getArea_name(),
                        structurePointData.getCluster_name(),
                        structurePointData.getPhase_name(),
                        structurePointData.getWork_area_name(),
                        structurePointData.getHut_number(),
                        structurePointData.getStructure_name(),
                        (short) structurePointData.getNo_of_floors(),
                        structurePointData.getAddress(),
                        structurePointData.getTenement_number(),
                        structurePointData.getStructure_status(),
                        structurePointData.getSurveyor_name());

                if (structurePointData.isUploaded() && objectId != 0) {
                    attStructure.put(Constants.objectid, objectId);
                    structurePointGlobalIdStr = "";
                    if (!structurePointData.getGlobalId().startsWith("{")) {
                        structurePointGlobalIdStr = "{" + structurePointData.getGlobalId() + "}";
                    } else {
                        structurePointGlobalIdStr = structurePointData.getGlobalId();
                    }
                    structureUnitGlobalId.put("" + objectId, structurePointGlobalIdStr);
                }
                dataObject.put("structure", new Gson().toJson(structurePointData));
                // unitInfoDataModelList = localSurveyDbViewModel.getUnitInfoData(structurePointData.getStructure_id());
                // structureUnitList.put(structurePointData.getStructure_id(), unitInfoDataModelList);
                // -----------------------------------------------------------
                // get unit data
                JSONArray unitArray = getUnitAttributeData(structurePointData.getStructure_id(), structurePointData.getObejctId(), structureUnitGlobalId);
                dataObject.put("unit", unitArray);
                // ------------------------------------------------------
                // get hoh data
                JSONArray hohArray = getHohAttributeData(unitArray);
                dataObject.put("hoh", hohArray);
                // -----------------------------------------------------
                // get member data
                JSONArray memberArray = getMemberAttributeData(hohArray);
                dataObject.put("member", memberArray);
                // ------------------------------------------------------

                UploadDataArray.put(dataObject);
            }

            // saveDataToFile(new Gson().toJson(structureInfoList).toString(),new Gson().toJson(structureUnitList).toString());
            saveDataToFile(UploadDataArray);
        } catch (Exception ex) {
            AppLog.e("generateUploadDataForZip:" + ex.getMessage());
            AppLog.logData(activity, "generateUploadDataForZip:" + ex.getMessage());
        }
    }

    public JSONArray getUnitAttributeData(String structureId, String objectid, Map<String, String> structureUnitGlobalId) {

        JSONArray unitArray = new JSONArray();

        try {

            hashMapGobalId = new HashMap<>();
            unitIdArrayList = new ArrayList<>();
            // memberIdArrayList = new ArrayList<>();
            // hashMapRelativePathObjectId = new ArrayList<>();

            // Map<String, List<UnitInfoDataModel>> unitAttributeData= new HashMap<>();
            short visit_count = 0, lockFlag = 0;
            // String unit_area_mtrs, loft_area_mtrs;

            List<UnitInfoDataModel> unitInfoDataModelList = localSurveyDbViewModel.getUnitInfoData(structureId);

            Map<String, Object> attributes;
            GregorianCalendar cal;

            for (UnitInfoDataModel unitInfoDataModel : unitInfoDataModelList) {

                try {

                    Object structurePointGlobalId;
                    String structurePointGlobalIdStr = structureUnitGlobalId.get(objectid).toUpperCase();

                    if (structurePointGlobalIdStr.startsWith("{"))
                        structurePointGlobalId = UUID.fromString(structurePointGlobalIdStr.substring(1, structurePointGlobalIdStr.length() - 1));
                    else structurePointGlobalId = UUID.fromString(structurePointGlobalIdStr);

                    int objectId = (int) Utils.doubleFormatter(unitInfoDataModel.getObejctId());

                    visit_count = (short) unitInfoDataModel.getVisit_count();
                    if (visit_count == 0) {
                        visit_count = 1;
                    }
                    if (visit_count >= 4) {
                        lockFlag = 1;
                    }
                    if (unitInfoDataModel.getUnit_status().equals(Constants.completed_statusLayer)) {
                        lockFlag = 1;
                    } else if (unitInfoDataModel.getUnit_status().equals(Constants.completed_dispute)) {
                        lockFlag = 1;
                    }
                    unitInfoDataModel.setVisit_count(lockFlag);

                    if (unitInfoDataModel.isYesNo() && !unitInfoDataModel.isMember_available()) {
                        try {
                            cal = Utils.getGregorianCalendarFromDate(unitInfoDataModel.getExistence_since());
                            // unit_area_mtrs = Double.toString(unitInfoDataModel.getUnit_area_sqft());
                            // loft_area_mtrs = Double.toString(unitInfoDataModel.getLoft_area_sqft());
                        } catch (Exception e) {
                            cal = null;
                        }
                        // all new values
                        String lfft = "YES";
                        if (unitInfoDataModel.getLoft_present()) {
                            lfft = "YES";
                        } else {
                            lfft = "NO";
                        }

                        GregorianCalendar gc = null;
                        try {
                            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(unitInfoDataModel.getExistence_since() + " 00:00:00");
                            TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                            Locale loc = new Locale("en", "IN");
                            Calendar calendar = Calendar.getInstance(loc);
                            gc = (GregorianCalendar) calendar;
                            gc.setTimeZone(tz);
                            gc.setTime(date);
                        } catch (Exception ex) {
                            ex.getCause();
                        }
//RS
                        attributes = Utils.getUnitInfoDetailsAttributeV4(structurePointGlobalId,
                                unitInfoDataModel.getUnit_id(),
                                unitInfoDataModel.getUnit_unique_id(),
                                unitInfoDataModel.getRelative_path(),
                                unitInfoDataModel.getTenement_number(),
                                unitInfoDataModel.getHut_number(),
                                unitInfoDataModel.getFloor(),
                                unitInfoDataModel.getUnit_no(),
                                unitInfoDataModel.getUnit_usage(),
                                gc,
                                cal,
                                unitInfoDataModel.getStructure_year(),
                                unitInfoDataModel.getNature_of_activity(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                (unitInfoDataModel.isElectric_bill_attached()) ? (short) 1 : (short) 0,
                                (short) 0,
                                (unitInfoDataModel.isProperty_tax_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isRent_agreement_attached()) ? (short) 1 : (short) 0,
                                (short) 0,
                                (unitInfoDataModel.isChain_document_attached()) ? (short) 1 : (short) 0,
                                (short) 0,
                                (unitInfoDataModel.isOthers_attachment_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isNa_tax_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isElectoral_roll_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isPhoto_pass_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isShare_certificate_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isSchool_college_certificate_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isCertificate_issued_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isRestro_hotel_license_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isFactory_act_license_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isFood_drug_license_attached()) ? (short) 1 : (short) 0,
                                "NO",
                                unitInfoDataModel.getUnit_status(),
                                unitInfoDataModel.getSurveyor_name(),
                                unitInfoDataModel.getRemarks(),
                                unitInfoDataModel.getRemarks_other(),
                                unitInfoDataModel.getMedia_captured_cnt(),
                                unitInfoDataModel.getMedia_uploaded_cnt(),
                                unitInfoDataModel.getSurveyor_desig(),
                                unitInfoDataModel.getDrp_officer_name(),
                                unitInfoDataModel.getDrp_officer_name_other(),
                                unitInfoDataModel.getDrp_officer_desig(),
                                unitInfoDataModel.getDrp_officer_desig_other(),
                                visit_count,
                                unitInfoDataModel.getArea_name(),
                                unitInfoDataModel.getWard_no(),
                                unitInfoDataModel.getSector_no(),
                                unitInfoDataModel.getZone_no(),
                                unitInfoDataModel.getNagar_name(),
                                unitInfoDataModel.getNagar_name_other(),
                                unitInfoDataModel.getSociety_name(),
                                unitInfoDataModel.getStreet_name(),
                                unitInfoDataModel.getLandmark_name(),
                                unitInfoDataModel.getRespondent_name(),
                                unitInfoDataModel.getRespondent_dob(),
                                unitInfoDataModel.getRespondent_age(),
                                unitInfoDataModel.getRespondent_hoh_contact(),
                                unitInfoDataModel.getRespondent_hoh_relationship(),
                                unitInfoDataModel.getRespondent_hoh_relationship_other(),
                                unitInfoDataModel.getTenement_document(),
                                unitInfoDataModel.getMashal_survey_number(),
                                unitInfoDataModel.getOwnership_status(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                lfft,
                                unitInfoDataModel.getLoft_area_sqft(),
                                unitInfoDataModel.getEmployees_count(),
                                unitInfoDataModel.getPincode(),
                                unitInfoDataModel.getRespondent_mobile(),
                                unitInfoDataModel.getGhumasta_area_sqft(),
                                new GregorianCalendar(),
                                // unitInfoDataModel.getForm_lock(),
                                lockFlag,
                                unitInfoDataModel.isUploaded(),
                                unitInfoDataModel.getRespondent_hoh_name(),
                                unitInfoDataModel.getSurvey_date(),
                                unitInfoDataModel.getSurvey_time(),
                                unitInfoDataModel.getType_of_other_structure(), "", unitInfoDataModel.getRespondent_non_available_remark(),
                                unitInfoDataModel.getSurvey_pavti_no(),
                                unitInfoDataModel.getExistence_since_year(),
                                unitInfoDataModel.getCountry_name(),
                                unitInfoDataModel.getState_name(),
                                unitInfoDataModel.getCity_name(),
                                unitInfoDataModel.getAccess_to_unit(),
                                unitInfoDataModel.getResidential_area_sqft(),
                                unitInfoDataModel.getCommercial_area_sqft(),
                                unitInfoDataModel.getMobile_no_for_otp(),
                                unitInfoDataModel.getOtp_sent(),
                                unitInfoDataModel.getOtp_received(),
                                unitInfoDataModel.getOtp_verified(),
                                unitInfoDataModel.getOtp_remarks(),
                                selectedUnitDataModel.getOtp_remarks_other(),
                                unitInfoDataModel.getThumb_remarks(),

                                selectedUnitDataModel.getStructure_type_religious(),
                                selectedUnitDataModel.getStructure_religious_other(),
                                selectedUnitDataModel.getStructure_type_amenities(),
                                selectedUnitDataModel.getStructure_amenities_other(),
                                selectedUnitDataModel.getName_of_structure(),
                                selectedUnitDataModel.getType_of_diety(),
                                selectedUnitDataModel.getType_of_diety_other(),
                                selectedUnitDataModel.getName_of_diety(),
                                selectedUnitDataModel.getCategory_of_faith(),
                                selectedUnitDataModel.getCategory_of_faith_other(),
                                selectedUnitDataModel.getSub_category_of_faith(),
                                selectedUnitDataModel.getReligion_of_structure_belongs(),
                                selectedUnitDataModel.getStructure_ownership_status(),
                                selectedUnitDataModel.getName_of_trust_or_owner(),
                                selectedUnitDataModel.getNature_of_structure(),
                                selectedUnitDataModel.getConstruction_material(),
                                selectedUnitDataModel.getDaily_visited_people_count(),
                                selectedUnitDataModel.getTenement_number_rel_amenities(),
                                selectedUnitDataModel.getTenement_doc_used(),
                                selectedUnitDataModel.getTenement_doc_other(),
                                selectedUnitDataModel.getIs_structure_registered(),
                                selectedUnitDataModel.getStructure_registered_with(),
                                selectedUnitDataModel.getOther_religious_authority(),
                                selectedUnitDataModel.getName_of_trustee(),
                                selectedUnitDataModel.getName_of_landowner(),
                                selectedUnitDataModel.getNoc_from_landlord_or_govt(),
                                selectedUnitDataModel.getApproval_from_govt(),
                                selectedUnitDataModel.getYearly_festival_conducted(),
                                selectedUnitDataModel.getSurvey_pavti_no_rel_amenities(),
                                selectedUnitDataModel.getMashal_rel_amenities(),
                                selectedUnitDataModel.getRa_total_no_of_floors(),
                                selectedUnitDataModel.getLatitude(),
                                selectedUnitDataModel.getLongitude(),
                                selectedUnitDataModel.getGenesys_device_name(),
                                selectedUnitDataModel.getPrimary_imei_no(),
                                selectedUnitDataModel.getSecond_imei_no(),
                                selectedUnitDataModel.getIs_drppl_officer_available(),
                                selectedUnitDataModel.getDrppl_officer_name()
                        );
                    } else if (!unitInfoDataModel.isMember_available()) {
                        // visit_count= (short) unitInfoDataModel.getVisit_count();
                        // Date dtt=getDate(Long.parseLong(unitInfoDataModel.getVisit_date()));
                        //RS
                        attributes = Utils.getUnitInfoDetailsAttributeV4(structurePointGlobalId,
                                unitInfoDataModel.getUnit_id(),
                                unitInfoDataModel.getUnit_unique_id(),
                                unitInfoDataModel.getRelative_path(),
                                unitInfoDataModel.getHut_number(),
                                unitInfoDataModel.getFloor(),
                                unitInfoDataModel.getUnit_no(),
                                unitInfoDataModel.getNature_of_activity(),
                                "NO",
                                unitInfoDataModel.getUnit_status(),
                                unitInfoDataModel.getSurveyor_name(),
                                unitInfoDataModel.getSurveyor_desig(),
                                unitInfoDataModel.getDrp_officer_name(),
                                unitInfoDataModel.getDrp_officer_name_other(),
                                unitInfoDataModel.getDrp_officer_desig(),
                                unitInfoDataModel.getDrp_officer_desig_other(),
                                unitInfoDataModel.getRemarks(),
                                unitInfoDataModel.getRemarks_other(),
                                visit_count,
                                unitInfoDataModel.getArea_name(),
                                unitInfoDataModel.getWard_no(),
                                unitInfoDataModel.getSector_no(),
                                unitInfoDataModel.getZone_no(),
                                unitInfoDataModel.getNagar_name(),
                                unitInfoDataModel.getNagar_name_other(),
                                unitInfoDataModel.getSociety_name(),
                                unitInfoDataModel.getStreet_name(),
                                unitInfoDataModel.getLandmark_name(),
                                unitInfoDataModel.getPincode(),
                                new GregorianCalendar(),
                                // unitInfoDataModel.getForm_lock(),
                                lockFlag,
                                unitInfoDataModel.getSurvey_date(),
                                unitInfoDataModel.getSurvey_time(),
                                unitInfoDataModel.isUploaded(),
                                unitInfoDataModel.getCountry_name(),
                                unitInfoDataModel.getState_name(),
                                unitInfoDataModel.getCity_name(),
                                unitInfoDataModel.getAccess_to_unit(),
                                unitInfoDataModel.getResidential_area_sqft(),
                                unitInfoDataModel.getCommercial_area_sqft(),
                                unitInfoDataModel.getMobile_no_for_otp(),
                                unitInfoDataModel.getOtp_sent(),
                                unitInfoDataModel.getOtp_received(),
                                unitInfoDataModel.getOtp_verified(),
                                unitInfoDataModel.getOtp_remarks(),
                                selectedUnitDataModel.getOtp_remarks_other(),
                                unitInfoDataModel.getThumb_remarks(),
                                selectedUnitDataModel.getLatitude(),
                                selectedUnitDataModel.getLongitude(),
                                selectedUnitDataModel.getGenesys_device_name(),
                                selectedUnitDataModel.getPrimary_imei_no(),
                                selectedUnitDataModel.getSecond_imei_no(),
                                selectedUnitDataModel.getIs_drppl_officer_available(),
                                selectedUnitDataModel.getDrppl_officer_name()
                        );
                    } else {
                        try {
                            cal = Utils.getGregorianCalendarFromDate(unitInfoDataModel.getExistence_since());
                            // unit_area_mtrs = Double.toString(unitInfoDataModel.getUnit_area_sqft());
                            // loft_area_mtrs = Double.toString(unitInfoDataModel.getLoft_area_sqft());
                            // visit_count= (short) unitInfoDataModel.getVisit_count();
                        } catch (Exception e) {
                            cal = null;
                        }
                        // all new values
                        String lfft = "YES";
                        if (unitInfoDataModel.getLoft_present()) {
                            lfft = "YES";
                        } else {
                            lfft = "NO";
                        }
                        GregorianCalendar gc = null;
                        try {
                            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(unitInfoDataModel.getExistence_since() + " 00:00:00");
                            TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                            Locale loc = new Locale("en", "IN");
                            Calendar calendar = Calendar.getInstance(loc);
                            gc = (GregorianCalendar) calendar;
                            gc.setTimeZone(tz);
                            gc.setTime(date);
                        } catch (Exception ex) {
                            ex.getCause();
                        }
//RS
                        attributes = Utils.getUnitInfoDetailsAttributeV4(structurePointGlobalId,
                                unitInfoDataModel.getUnit_id(),
                                unitInfoDataModel.getUnit_unique_id(),
                                unitInfoDataModel.getRelative_path(),
                                unitInfoDataModel.getTenement_number(),
                                unitInfoDataModel.getHut_number(),
                                unitInfoDataModel.getFloor(),
                                unitInfoDataModel.getUnit_no(),
                                unitInfoDataModel.getUnit_usage(),
                                gc,
                                cal,
                                unitInfoDataModel.getStructure_year(),
                                unitInfoDataModel.getNature_of_activity(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                (unitInfoDataModel.isElectric_bill_attached()) ? (short) 1 : (short) 0,
                                (short) 0,
                                (unitInfoDataModel.isProperty_tax_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isRent_agreement_attached()) ? (short) 1 : (short) 0,
                                (short) 0,
                                (unitInfoDataModel.isChain_document_attached()) ? (short) 1 : (short) 0,
                                (short) 0,
                                (unitInfoDataModel.isOthers_attachment_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isNa_tax_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isElectoral_roll_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isPhoto_pass_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isShare_certificate_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isSchool_college_certificate_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isCertificate_issued_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isRestro_hotel_license_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isFactory_act_license_attached()) ? (short) 1 : (short) 0,
                                (unitInfoDataModel.isFood_drug_license_attached()) ? (short) 1 : (short) 0,
                                "YES",
                                unitInfoDataModel.getUnit_status(),
                                unitInfoDataModel.getSurveyor_name(),
                                unitInfoDataModel.getRemarks(),
                                unitInfoDataModel.getRemarks_other(),
                                unitInfoDataModel.getMedia_captured_cnt(),
                                unitInfoDataModel.getMedia_uploaded_cnt(),
                                unitInfoDataModel.getSurveyor_desig(),
                                unitInfoDataModel.getDrp_officer_name(),
                                unitInfoDataModel.getDrp_officer_name_other(),
                                unitInfoDataModel.getDrp_officer_desig(),
                                unitInfoDataModel.getDrp_officer_desig_other(),
                                visit_count,
                                unitInfoDataModel.getArea_name(),
                                unitInfoDataModel.getWard_no(),
                                unitInfoDataModel.getSector_no(),
                                unitInfoDataModel.getZone_no(),
                                unitInfoDataModel.getNagar_name(),
                                unitInfoDataModel.getNagar_name_other(),
                                unitInfoDataModel.getSociety_name(),
                                unitInfoDataModel.getStreet_name(),
                                unitInfoDataModel.getLandmark_name(),
                                unitInfoDataModel.getRespondent_name(),
                                unitInfoDataModel.getRespondent_dob(),
                                unitInfoDataModel.getRespondent_age(),
                                unitInfoDataModel.getRespondent_hoh_contact(),
                                unitInfoDataModel.getRespondent_hoh_relationship(),
                                unitInfoDataModel.getRespondent_hoh_relationship_other(),
                                unitInfoDataModel.getTenement_document(),
                                unitInfoDataModel.getMashal_survey_number(),
                                unitInfoDataModel.getOwnership_status(),
                                unitInfoDataModel.getUnit_area_sqft(),
                                lfft,
                                unitInfoDataModel.getLoft_area_sqft(),
                                unitInfoDataModel.getEmployees_count(),
                                unitInfoDataModel.getPincode(),
                                unitInfoDataModel.getRespondent_mobile(),
                                unitInfoDataModel.getGhumasta_area_sqft(),
                                new GregorianCalendar(),
                                // unitInfoDataModel.getForm_lock(),
                                lockFlag,
                                unitInfoDataModel.isUploaded(),
                                unitInfoDataModel.getRespondent_hoh_name(),
                                unitInfoDataModel.getSurvey_date(),
                                unitInfoDataModel.getSurvey_time(),
                                unitInfoDataModel.getType_of_other_structure(),
                                unitInfoDataModel.getRespondent_remarks(),
                                unitInfoDataModel.getRespondent_non_available_remark(),
                                unitInfoDataModel.getSurvey_pavti_no(),
                                unitInfoDataModel.getExistence_since_year(),
                                unitInfoDataModel.getCountry_name(),
                                unitInfoDataModel.getState_name(),
                                unitInfoDataModel.getCity_name(),
                                unitInfoDataModel.getAccess_to_unit(),
                                unitInfoDataModel.getResidential_area_sqft(),
                                unitInfoDataModel.getCommercial_area_sqft(),
                                unitInfoDataModel.getMobile_no_for_otp(),
                                unitInfoDataModel.getOtp_sent(),
                                unitInfoDataModel.getOtp_received(),
                                unitInfoDataModel.getOtp_verified(),
                                unitInfoDataModel.getOtp_remarks(),
                                selectedUnitDataModel.getOtp_remarks_other(),
                                unitInfoDataModel.getThumb_remarks(),

                                selectedUnitDataModel.getStructure_type_religious(),
                                selectedUnitDataModel.getStructure_religious_other(),
                                selectedUnitDataModel.getStructure_type_amenities(),
                                selectedUnitDataModel.getStructure_amenities_other(),
                                selectedUnitDataModel.getName_of_structure(),
                                selectedUnitDataModel.getType_of_diety(),
                                selectedUnitDataModel.getType_of_diety_other(),
                                selectedUnitDataModel.getName_of_diety(),
                                selectedUnitDataModel.getCategory_of_faith(),
                                selectedUnitDataModel.getCategory_of_faith_other(),
                                selectedUnitDataModel.getSub_category_of_faith(),
                                selectedUnitDataModel.getReligion_of_structure_belongs(),
                                selectedUnitDataModel.getStructure_ownership_status(),
                                selectedUnitDataModel.getName_of_trust_or_owner(),
                                selectedUnitDataModel.getNature_of_structure(),
                                selectedUnitDataModel.getConstruction_material(),
                                selectedUnitDataModel.getDaily_visited_people_count(),
                                selectedUnitDataModel.getTenement_number_rel_amenities(),
                                selectedUnitDataModel.getTenement_doc_used(),
                                selectedUnitDataModel.getTenement_doc_other(),
                                selectedUnitDataModel.getIs_structure_registered(),
                                selectedUnitDataModel.getStructure_registered_with(),
                                selectedUnitDataModel.getOther_religious_authority(),
                                selectedUnitDataModel.getName_of_trustee(),
                                selectedUnitDataModel.getName_of_landowner(),
                                selectedUnitDataModel.getNoc_from_landlord_or_govt(),
                                selectedUnitDataModel.getApproval_from_govt(),
                                selectedUnitDataModel.getYearly_festival_conducted(),
                                selectedUnitDataModel.getSurvey_pavti_no_rel_amenities(),
                                selectedUnitDataModel.getMashal_rel_amenities(),
                                selectedUnitDataModel.getRa_total_no_of_floors(),
                                selectedUnitDataModel.getLatitude(),
                                selectedUnitDataModel.getLongitude(),
                                selectedUnitDataModel.getGenesys_device_name(),
                                selectedUnitDataModel.getPrimary_imei_no(),
                                selectedUnitDataModel.getSecond_imei_no(),
                                selectedUnitDataModel.getIs_drppl_officer_available(),
                                selectedUnitDataModel.getDrppl_officer_name()
                        );
                    }

                    if (unitInfoDataModel.isUploaded()) {

                        hashMapGobalId.put(unitInfoDataModel.getUnit_id(), "{" + unitInfoDataModel.getGlobalId() + "}");
                        if (!unitIdArrayList.contains(unitInfoDataModel.getUnit_id()))
                            unitIdArrayList.add(unitInfoDataModel.getUnit_id());

                        if (unitInfoDataModel.getUnit_status().equals(Constants.completed_statusLayer) || unitInfoDataModel.getUnit_status().equals(Constants.completed_dispute)) {
                            attributes.put("hoh_availability", Short.parseShort("" + unitInfoDataModel.getHoh_avaibility()));
                            attributes.put("annexure_remarks", unitInfoDataModel.getAnnexure_remarks());
                            attributes.put("annexure_uploaded", unitInfoDataModel.getAnnexure_uploaded());
                            GregorianCalendar gg = new GregorianCalendar();
                            // gg.add(Calendar.MINUTE,330);
                            attributes.put("annexure_upload_date", gg);
                            attributes.put("panchnama_form_remarks", unitInfoDataModel.getPanchnama_form_remarks());
                        }


                        attributes.put(Constants.objectid, objectId);
                        attributes.put("rel_globalid", structurePointGlobalIdStr.toUpperCase());
                        // if (cal != null) attributes.put("structure_since", cal.getTimeInMillis());
                        localSurveyDbViewModel.updateMediaInfo(objectId + "", unitInfoDataModel.getGlobalId(), unitInfoDataModel.getRelative_path());

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put(Constants.Relative_path, unitInfoDataModel.getRelative_path());
                        hashMap.put(Constants.objectid, "" + objectId);
                        hashMap.put(Constants.layerFor, Constants.unit_infoLayer);
                        // hashMapRelativePathObjectId.add(hashMap);
                        attributes.put("media", new Gson().toJson(getMediaAttributeData(unitInfoDataModel.getUnit_id())));
                        unitArray.put(attributes);
                    } else {
                        if (!unitIdArrayList.contains(unitInfoDataModel.getUnit_id()))
                            unitIdArrayList.add(unitInfoDataModel.getUnit_id());
                        if (unitInfoDataModel.getUnit_status().equals(Constants.completed_statusLayer) || unitInfoDataModel.getUnit_status().equals(Constants.completed_dispute)) {
                            attributes.put("hoh_availability", Short.parseShort("" + unitInfoDataModel.getHoh_avaibility()));
                            attributes.put("annexure_remarks", unitInfoDataModel.getAnnexure_remarks());
                            attributes.put("annexure_uploaded", unitInfoDataModel.getAnnexure_uploaded());

                            GregorianCalendar gg = new GregorianCalendar();

                            attributes.put("annexure_upload_date", gg);
                            attributes.put("panchnama_form_remarks", unitInfoDataModel.getPanchnama_form_remarks());
                        }

                        unitInfoDataModel.setNature_of_activity("add");
                        attributes.put("media", new Gson().toJson(getMediaAttributeData(unitInfoDataModel.getUnit_id())));
                        unitArray.put(attributes);
                    }
                } catch (Exception ignored) {
                    ignored.getCause();
                    AppLog.e("getUnitAttributeData ignored:" + ignored.getMessage());
                    AppLog.logData(activity, "getUnitAttributeData ignored:" + ignored.getMessage());
                }
            }
        } catch (Exception ex) {
            AppLog.e("getUnitAttributeData:" + ex.getMessage());
            AppLog.logData(activity, "getUnitAttributeData:" + ex.getMessage());
        }
        return unitArray;
    }

    public JSONArray getHohAttributeData(JSONArray unitArray) {
        JSONArray hohArray = new JSONArray();

        try {
            // ArrayList<Map<String, Object>> attriutesHohArray = new ArrayList();
            // ArrayList<Map<String, Object>> attriutesHohUpdateArray = new ArrayList();
            // Date  hoh_dob=null;
            GregorianCalendar hoh_dob = null;
            // short hoh_spouse_count = 0, hohage = 0, hohsince_year = 0;
            List<HohInfoDataModel> hohInfoDataModelList = new ArrayList<>();

            // String unitId="";
            for (String unitId : unitIdArrayList) {
                // for(int i=0;i<unitArray.length();i++){

                // unitId=unitArray.getJSONObject(i).getString("unit_id");
                List<HohInfoDataModel> hohInfoDataModel = localSurveyDbViewModel.getHohInfoData(unitId);
                Object globalId = "";
                String unitInfoGlobalIdStr = "";
                if (hashMapGobalId.containsKey(unitId)) {
                    unitInfoGlobalIdStr = hashMapGobalId.get(unitId);
                    globalId = UUID.fromString(unitInfoGlobalIdStr.substring(1, unitInfoGlobalIdStr.length() - 1));
                }
                for (HohInfoDataModel hohDetailsModel : hohInfoDataModel) {
                    int objectId = (int) Utils.doubleFormatter(hohDetailsModel.getObejctId());
                    try {
                        // hohage = (short) hohDetailsModel.getAge();
                        // hohsince_year = (short) hohDetailsModel.getStaying_since_year();
                        // hoh_dob = Utils.getGregorianCalendarFromDate("22/01/2024");
                        Log.i("Upload_ date epoch=", " " + hoh_dob);
                    } catch (Exception e) {
                        hoh_dob = null;
                        Log.e("Upload_JJJ=>", "initiateHohDataUpload conversion Error");
                    }
                    try {
                        Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(hohDetailsModel.getHoh_dob() + " 00:00:00");

                        TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                        Locale loc = new Locale("en", "IN");
                        Calendar calendar = Calendar.getInstance(loc);
                        GregorianCalendar gc = (GregorianCalendar) calendar;
                        gc.setTimeZone(tz);
                        gc.setTime(date);

                        Log.i("upload newDatee= ", " " + date);//  newDate=   Mon 20
                        Log.i("upload newDatee= ", " " + hohDetailsModel.getHoh_dob());  // 30/01/2024

                        Map<String, Object> attributes = Utils.getHohAttributeV4(
                                globalId,
                                hohDetailsModel.getHoh_id(),
                                hohDetailsModel.getRelative_path(),
                                hohDetailsModel.getHoh_name(),
                                hohDetailsModel.getMarital_status(),
                                hohDetailsModel.getMarital_status_other(),
                                (short) hohDetailsModel.getHoh_spouse_count(),
                                hohDetailsModel.getHoh_spouse_name(),
                                hohDetailsModel.getHoh_contact_no(),
                                gc,
                                (short) hohDetailsModel.getAge(),
                                hohDetailsModel.getGender(),
                                (short) hohDetailsModel.getStaying_since_year(),
                                hohDetailsModel.getAadhar_no(),
                                hohDetailsModel.getPan_no(),
                                hohDetailsModel.getRation_card_colour(),
                                hohDetailsModel.getRation_card_no(),
                                hohDetailsModel.getFrom_state(),
                                hohDetailsModel.getFrom_state_other(),
                                hohDetailsModel.getMother_tongue(),
                                hohDetailsModel.getMother_tongue_other(),
                                hohDetailsModel.getReligion(),
                                hohDetailsModel.getReligion_other(),
                                hohDetailsModel.getEducation(),
                                hohDetailsModel.getEducation_other(),
                                hohDetailsModel.getOccupation(),
                                hohDetailsModel.getOccupation_other(),
                                hohDetailsModel.getPlace_of_work(),
                                hohDetailsModel.getType_of_work(),
                                hohDetailsModel.getType_of_work_other(),
                                hohDetailsModel.getMonthly_income(),
                                hohDetailsModel.getMode_of_transport(),
                                hohDetailsModel.getMode_of_transport_other(),
                                hohDetailsModel.getSchool_college_name_location(),
                                hohDetailsModel.getHandicap_or_critical_disease(),
                                hohDetailsModel.getStaying_with(),
                                hohDetailsModel.getVehicle_owned_driven(),
                                hohDetailsModel.getVehicle_owned_driven_other(),
                                (short) hohDetailsModel.getDeath_certificate(),
                                (short) hohDetailsModel.getCount_of_other_members(),
                                hohDetailsModel.getAdhaar_verify_status(),
                                hohDetailsModel.getAdhaar_verify_remark(),
                                hohDetailsModel.getAdhaar_verify_date(),
                                hohDetailsModel.isUploaded());

                        if (hohDetailsModel.isUploaded()) {
                            // existing Hoh
                            hashMapGobalId.put(hohDetailsModel.getHoh_id(), "{" + hohDetailsModel.getGlobalId() + "}");

                            // hohIdArrayList.add(hohDetailsModel.getHoh_id());

                            attributes.put(Constants.objectid, objectId);
                            attributes.put("rel_globalid", unitInfoGlobalIdStr.toUpperCase());

                            localSurveyDbViewModel.updateMediaInfo(objectId + "", hohDetailsModel.getGlobalId(), hohDetailsModel.getRelative_path());

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(Constants.Relative_path, hohDetailsModel.getRelative_path());
                            hashMap.put(Constants.objectid, "" + objectId);
                            hashMap.put(Constants.layerFor, Constants.hoh_infoLayer);
                            // hashMapRelativePathObjectId.add(hashMap);
                            attributes.put("media", new Gson().toJson(getMediaAttributeData(hohDetailsModel.getHoh_id())));
                            hohArray.put(new Gson().toJson(attributes));
                        } else {
                            attributes.put("media", new Gson().toJson(getMediaAttributeData(hohDetailsModel.getHoh_id())));
                            hohArray.put(new Gson().toJson(attributes));
                        }
                    } catch (Exception e) {
                        Log.e("Upload_JJJErrorDate =>", "initiateHohDataUpload conversion Error" + e.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            AppLog.e("getHohAttributeData:" + ex.getMessage());
            AppLog.logData(activity, "getHohAttributeData:" + ex.getMessage());
        }

        return hohArray;
    }

    public JSONArray getMemberAttributeData(JSONArray hohArray) {

        JSONArray memberArray = new JSONArray();

        try {
            // GregorianCalendar member_dob = null;
            //short spouse_count = 0, age = 0, since_year = 0;
            // List<MemberInfoDataModel> memberInfoDataModelList = new ArrayList<>();
            String hohId = "";

            for (int i = 0; i < hohArray.length(); i++) {
                // for ( : hohIdArrayList) {
                hohId = new JSONObject(hohArray.get(i).toString()).getString("hoh_id");
                List<MemberInfoDataModel> memberInfoDataModels = localSurveyDbViewModel.getMemberInfoData(hohId);
                String unitInfoGlobalIdStr = "";
                Object globalId = "";
                if (hashMapGobalId.containsKey(hohId)) {
                    unitInfoGlobalIdStr = hashMapGobalId.get(hohId);
                    globalId = UUID.fromString(unitInfoGlobalIdStr.substring(1, unitInfoGlobalIdStr.length() - 1));
                }

                for (MemberInfoDataModel memberDetailsModel : memberInfoDataModels) {

                    int objectId = (int) Utils.doubleFormatter(memberDetailsModel.getObejctId());

                    try {
                        GregorianCalendar gc = null;
                        if (memberDetailsModel != null && memberDetailsModel.getMember_dob().length() > 0) {
                            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(memberDetailsModel.getMember_dob() + " 00:00:00");
                            TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                            Locale loc = new Locale("en", "IN");
                            Calendar calendar = Calendar.getInstance(loc);
                            gc = (GregorianCalendar) calendar;
                            gc.setTimeZone(tz);
                            gc.setTime(date);
                        }

                        Map<String, Object> attributes = Utils.getMemberAttributeV4(
                                globalId,
                                memberDetailsModel.getMember_id(),
                                memberDetailsModel.getRelative_path(),
                                memberDetailsModel.getMember_name(),
                                memberDetailsModel.getRelationship_with_hoh(),
                                memberDetailsModel.getRelationship_with_hoh_other(),
                                memberDetailsModel.getMarital_status(),
                                memberDetailsModel.getMarital_status_other(),
                                (short) memberDetailsModel.getMember_spouse_count(),
                                memberDetailsModel.getMember_spouse_name(),
                                memberDetailsModel.getMember_contact_no(),
                                gc,
                                (short) memberDetailsModel.getAge(),
                                memberDetailsModel.getGender(),
                                (short) memberDetailsModel.getStaying_since_year(),
                                memberDetailsModel.getAadhar_no(),
                                memberDetailsModel.getPan_no(),
                                memberDetailsModel.getRation_card_colour(),
                                memberDetailsModel.getRation_card_no(),
                                memberDetailsModel.getFrom_state(),
                                memberDetailsModel.getFrom_state_other(),
                                memberDetailsModel.getMother_tongue(),
                                memberDetailsModel.getMother_tongue_other(),
                                memberDetailsModel.getReligion(),
                                memberDetailsModel.getReligion_other(),
                                memberDetailsModel.getEducation(),
                                memberDetailsModel.getEducation_other(),
                                memberDetailsModel.getOccupation(),
                                memberDetailsModel.getOccupation_other(),
                                memberDetailsModel.getPlace_of_work(),
                                memberDetailsModel.getType_of_work(),
                                memberDetailsModel.getType_of_work_other(),
                                memberDetailsModel.getMonthly_income(),
                                memberDetailsModel.getMode_of_transport(),
                                memberDetailsModel.getMode_of_transport_other(),
                                memberDetailsModel.getSchool_college_name_location(),
                                memberDetailsModel.getHandicap_or_critical_disease(),
                                memberDetailsModel.getStaying_with(),
                                memberDetailsModel.getVehicle_owned_driven(),
                                memberDetailsModel.getVehicle_owned_driven_other(),
                                memberDetailsModel.isUploaded(),
                                (short) memberDetailsModel.getDeath_certificate());

                        if (memberDetailsModel.isUploaded()) {

                            hashMapGobalId.put(memberDetailsModel.getMember_id(), "{" + memberDetailsModel.getGlobalId() + "}");

                            // memberIdArrayList.add(memberDetailsModel.getMember_id());

                            attributes.put(Constants.objectid, objectId);
                            attributes.put("rel_globalid", unitInfoGlobalIdStr.toUpperCase());

                            localSurveyDbViewModel.updateMediaInfo(objectId + "", memberDetailsModel.getGlobalId(), memberDetailsModel.getRelative_path());

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(Constants.Relative_path, memberDetailsModel.getRelative_path());
                            hashMap.put(Constants.objectid, "" + objectId);
                            hashMap.put(Constants.layerFor, Constants.member_infoLayer);
                            // hashMapRelativePathObjectId.add(hashMap);
                            attributes.put("media", new Gson().toJson(getMediaAttributeData(memberDetailsModel.getMember_id())));
                            memberArray.put(new Gson().toJson(attributes));

                        } else {
                            attributes.put("media", new Gson().toJson(getMediaAttributeData(memberDetailsModel.getMember_id())));
                            memberArray.put(new Gson().toJson(attributes));
                            // memberInfoDataModelList.add(memberDetailsModel);
                        }
                    } catch (Exception exception) {
                        Log.e("Upload_JJJErrorDate =>", "initiateMemberDataUpload conversion Error" + exception.getMessage());
                    }
                }
                // }
            }
        } catch (Exception ex) {
            AppLog.e("getMemberttributeData:" + ex.getMessage());
            AppLog.logData(activity, "getMemberttributeData:" + ex.getMessage());
        }

        return memberArray;
    }

    public JSONArray getMediaAttributeData(String parentUniqueId) {

        JSONArray mediaArray = new JSONArray();

        try {

            AppLog.d("parentUniqueId :: " + parentUniqueId);
            // ArrayList<Map<String, Object>> attriutesMediaArray = new ArrayList();
            // MediaInfoDataModel mediaInfoDataModel1 = mediaInfoDataModels.get(seq);

            // mediaInfoDataModelArrayList = new ArrayList<>();
            List<MediaInfoDataModel> mediaInfoDataModelArrayList = localSurveyDbViewModel.getMediaInfoDataByParentUniqueId(parentUniqueId);

            // mediaInfoDataModelArrayList.add(mediaInfoDataModel1);

            for (MediaInfoDataModel mediaInfoDataModel : mediaInfoDataModelArrayList) {

                // String unitInfoGlobalIdStr = mediaInfoDataModel.getRel_globalid();
                // Object globalId = UUID.fromString(unitInfoGlobalIdStr);

                String url = "";
                if (mediaInfoDataModel.getDocument_category().equalsIgnoreCase(Constants.UnitDistometerVideo)) {
                    url = getVideoPath(mediaInfoDataModel.getItem_url());
                } else {
                    url = mediaInfoDataModel.getItem_url();
                }

                filePathArray.add(url);

                Map<String, Object> attributes = Utils.getMediaInfoAttribute(
                        mediaInfoDataModel.getContent_type(),
                        mediaInfoDataModel.getFilename(),
                        mediaInfoDataModel.getData_size(),
                        url,
                        mediaInfoDataModel.getFile_upload_checked(),
                        mediaInfoDataModel.getParent_table_name(),
                        mediaInfoDataModel.getParent_unique_id(),
                        "{" + mediaInfoDataModel.getRel_globalid() + "}", "",
                        mediaInfoDataModel.getGlobalId(), mediaInfoDataModel.getDocument_type(), mediaInfoDataModel.getDocument_category(),
                        mediaInfoDataModel.getName_on_document(), mediaInfoDataModel.getDocument_remarks());
                mediaArray.put(new Gson().toJson(attributes));
            }
        } catch (Exception ex) {
            AppLog.e("getMediaAttributeData:" + ex.getMessage());
            AppLog.logData(activity, "getMediaAttributeData:" + ex.getMessage());
        }

        return mediaArray;
    }

    // public void saveDataToFile(String structure,String unit){
    public void saveDataToFile(JSONArray obj) {

        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmmss");
            String formattedDate = df.format(c.getTime());
            String zipFileName = Environment.getExternalStorageDirectory() + "/DRP_DATA_" + formattedDate + ".zip";

            Utils.zipUpload(filePathArray, obj, zipFileName);

            Utils.shortToast("Upload Data Zip Generated Successfully", activity);
        } catch (Exception ex) {
            AppLog.e("saveDataToFile:" + ex.getMessage());
            AppLog.logData(activity, "saveDataToFile:" + ex.getMessage());
        }
    }

    private void setDeleteItem() {

        saveLogs("setDeleteItem_" + selectedUnitDataModel.getUnit_id(), "Deleting Attachments");

        progressBar.setMessage("Deleting Data");
        progressBar.setProgress(r.nextInt(99 - 95) + 95);

        if (deleteObjList != null) {
            deleteObjList.clear();
            deleteObjList = new ArrayList<>();
        }

        mapListMediaInfoToDelete = new LinkedHashMap<>();

        for (String parent_id : mediaIdArrayListToDelete) {

            // Retrieve deleted media based on isDeletedItem field
            List<MediaInfoDataModel> listDeletedItem = localSurveyDbViewModel.getDeleteItemObjList(parent_id, true, false);

            if (listDeletedItem != null && !listDeletedItem.isEmpty()) {
                deleteObjList.addAll(listDeletedItem);
            }
        }

        for (MediaInfoDataModel mediaInfoDataModel : deleteObjList) {

            if (!mediaInfoDataModel.getAttachmentItemLists().isEmpty()) {

                saveLogs("setDeleteItem_" + selectedUnitDataModel.getUnit_id(), new Gson().toJson(deleteObjList).toString());

                for (AttachmentItemList attachmentItemList : mediaInfoDataModel.getAttachmentItemLists()) {
                    if (attachmentItemList.isDeleted) {

                        MediaDetailsDataModel mediaDetailsDataModel = localSurveyDbViewModel.getMediaDetailsByNameAndRelGlobalId(
                                mediaInfoDataModel.getGlobalId().toLowerCase(), attachmentItemList.getFileName());

                        if (!mapListMediaInfoToDelete.containsKey(mediaInfoDataModel.getObejctId()))
                            mapListMediaInfoToDelete.put(mediaInfoDataModel.getObejctId(), new ArrayList<>());

                        mapListMediaInfoToDelete.get(mediaInfoDataModel.getObejctId()).add(mediaDetailsDataModel);
                    }
                }
            }
        }

        setDeleteMediaObjects();
    }

    private void setDeleteMediaObjects() {

        saveLogs("setDeleteMediaObjects_" + selectedUnitDataModel.getUnit_id(), "Deleting Whole Media Object");

        progressBar.setProgress(r.nextInt(99 - 95) + 95);

        List<MediaInfoDataModel> deleteMediaObjList = new ArrayList<>();

        for (String parent_id : mediaIdArrayListToDelete) {

            // Retrieve deleted media based on wholeObjectDeleted field
            List<MediaInfoDataModel> listDeletedItem = localSurveyDbViewModel.getDeleteItemMediaObjList(parent_id, true);

            if (listDeletedItem != null && !listDeletedItem.isEmpty()) {
                if (listDeletedItem.get(0).getAttachmentItemLists() != null && !listDeletedItem.get(0).getAttachmentItemLists().isEmpty()) {
                    deleteMediaObjList.addAll(listDeletedItem);
                }
            }
        }

        StringBuilder objectIds = new StringBuilder();

        if (!deleteMediaObjList.isEmpty()) {

            for (MediaInfoDataModel mediaInfoDataModel : deleteMediaObjList) {

                if (mediaInfoDataModel.getObejctId() != null && !mediaInfoDataModel.getObejctId().isEmpty()) {

                    List<MediaDetailsDataModel> listMediaDetailsToDelete =
                            localSurveyDbViewModel.getMediaDetailsByRelGlobalId(mediaInfoDataModel.getGlobalId().toLowerCase());

                    if (!mapListMediaInfoToDelete.containsKey(mediaInfoDataModel.getObejctId()))
                        mapListMediaInfoToDelete.put(mediaInfoDataModel.getObejctId(), new ArrayList<>());

                    mapListMediaInfoToDelete.get(mediaInfoDataModel.getObejctId()).addAll(listMediaDetailsToDelete);

                    if (objectIds.length() > 0)
                        objectIds.append(", ");

                    objectIds.append(mediaInfoDataModel.getObejctId());
                }

                saveLogs("setDeleteMediaObjects_" + selectedUnitDataModel.getUnit_id(), "Object Ids :: " + objectIds);
            }
        }

        if (mapListMediaInfoToDelete != null && !mapListMediaInfoToDelete.isEmpty()) {
            deleteMultipleAttachment(objectIds.toString());
        } else if (!objectIds.toString().isEmpty()) {
            deleteWholeMediaObject(objectIds.toString());
        } else {
            deleteLocalData();
        }

        // localSurveyDbViewModel.setDeleteItemObjValid(false, deleteObjList.get(0).getObejctId(), deleteObjList.get(0).getParent_unique_id());
    }

    public void deleteMultipleAttachment(String objectIds) {

        try {

            JSONArray jArrRequestBody = new JSONArray();

            for (String key : mapListMediaInfoToDelete.keySet()) {

                for (MediaDetailsDataModel mediaDetailsDataModel : mapListMediaInfoToDelete.get(key)) {
                    JSONObject jObjMediaInfo = new JSONObject();
                    jObjMediaInfo.put("mediaInfoObjectid", key);
                    jObjMediaInfo.put("mediaDetailsObjectid", mediaDetailsDataModel.getObjectid());
                    jArrRequestBody.put(jObjMediaInfo);
                }
            }

            saveLogs("deleteMultipleAttachment_Request", jArrRequestBody.toString());

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jArrRequestBody.toString());

            Api_Interface apiInterface = RetrofitService.getDomainClient().create(Api_Interface.class);
            Call<JsonElement> call = apiInterface.deleteMultipleAttachments(body);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                    Utils.dismissProgress();

                    try {

                        if (response.code() == 200 && response.body() != null) {

                            saveLogs("deleteMultipleAttachment_", new Gson().toJson(response.body()));

                            JSONObject jObjRoot = new JSONObject(response.body().toString());
                            JSONObject jObjStatus = jObjRoot.getJSONObject("status");
                            if (jObjStatus.getString("status").equalsIgnoreCase("1")) {
                                // Utils.showMessagePopup(jObjStatus.getString("message"), activity);
                                deleteMediaInfo(objectIds);
                            } else {
                                saveLogs("deleteMultipleAttachment_Response_Else", new Gson().toJson(response));
                                deleteMediaInfo(objectIds);
                            }
                        } else {
                            saveLogs("deleteMultipleAttachment_Response_Else1", new Gson().toJson(response));
                            deleteMediaInfo(objectIds);
                        }
                    } catch (Exception e) {
                        AppLog.e(e.getMessage());
                        saveLogs("deleteMultipleAttachment_Response_Else2", e.getMessage());
                        deleteMediaInfo(objectIds);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    call.cancel();
                    saveLogs("deleteMultipleAttachment_Response_Else3", t.getMessage());
                    deleteMediaInfo(objectIds);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            saveLogs("deleteMultipleAttachment_Response_Else4", e.getMessage());
            deleteMediaInfo(objectIds);
        }
    }

    public void deleteMediaInfo(String objectIds) {
        if (objectIds != null && !objectIds.isEmpty()) {
            deleteWholeMediaObject(objectIds);
        } else {
            deleteLocalData();
        }
    }

    private void deleteWholeMediaObject(String objectIds) {

        if (!objectIds.isEmpty()) {

            saveLogs("deleteWholeMediaObject_", objectIds);

            QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);

            queryResultRepoViewModel.initDeleteWholeMediaObjectFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.MediaInfo_ENDPOINT,
                    GetFormModel.getInstance().getQueryBuilderMediaObject("form-data", objectIds));

            queryResultRepoViewModel.getDeleteWholeMediaObjectAttachmentResultsMutableLiveData().observe(getActivity(), resultQueryModel -> {

                if (resultQueryModel != null && resultQueryModel.deleteResults != null && !resultQueryModel.deleteResults.isEmpty()) {

                    saveLogs("deleteWholeMediaObjectRes_" + selectedUnitDataModel.getUnit_id(), new Gson().toJson(resultQueryModel.deleteResults).toString());

                    // Check for Success in each item in deleteResults (If Required)
                    deleteLocalData();
                } else {
                    saveLogs("deleteWholeMediaObjectResElse_", "Something went wrong while delete attachments from sever. :: " + resultQueryModel);
                    // stopProcessWithError("Something went wrong while delete attachments from sever.");
                    deleteLocalData();
                }
            });
        } else {
            deleteLocalData();
        }
    }

    public void deleteLocalData() {
        Utils.dismissProgress();
        Utils.shortToast("Attachment upload completed", activity);
        deleteUnitFromLocalDB(false);
    }

    private void deleteUnitFromLocalDB(boolean isForceDelete) {

        mediaIdArrayListToDelete.clear();

        ArrayList<String> unitInfoIdData = new ArrayList<>();
        unitInfoIdData.add(selectedUnitDataModel.getUnit_id());
        deleteUnitInfo(unitInfoIdData, isForceDelete);

        saveLogs("deleteUnitFromLocalDB_" + selectedUnitDataModel.getUnit_id(), "Media Data Deleted from Local DB. Force Delete :: " + isForceDelete);

        if (isForceDelete) {
            uploadProcessCompleted(false, "delete");
        } else {
            uploadProcessCompleted(true, "done");
        }
    }

    private void uploadProcessCompleted(boolean isUploaded, String toastMsg) {

        saveLogs("uploadProcessCompleted_" + selectedUnitDataModel.getUnit_id(), "Is Uploaded :: " + isUploaded + "||" + "Message :: " + toastMsg);

        String message = "";

        binding.cvErrorView.setVisibility(View.VISIBLE);

        if (isUploaded && toastMsg.equalsIgnoreCase("done")) {

            message = "Form uploaded Successfully!";

            if (!isSingleUploadCall && filteredUnitDataToUpload != null) {
                if (filteredUnitDataToUpload.size() > 10) {
                    message = "Forms uploaded successfully! " + (fileToUpload + 1) + "/10";
                } else {
                    message = "Forms uploaded Successfully! " + (fileToUpload + 1) + "/" + filteredUnitDataToUpload.size();
                }
            }
        } else if (toastMsg.equalsIgnoreCase("delete")) {
            message = "Unit deleted successfully!!";
        }

        binding.txtErrorView.setText(message);

        new Handler().postDelayed(() -> binding.cvErrorView.setVisibility(View.GONE), 4000);

        if (progressBar != null && progressBar.isShowing())
            progressBar.dismiss();
    }

    private void deleteUnitInfo(List<String> unitInfoIdData, boolean isForceDelete) {

        List<String> mediaInfoRelativePath = new ArrayList<>();

        new Handler().postDelayed(() -> {

            if (!unitInfoIdData.isEmpty()) {

                mediaInfoRelativePath.addAll(unitInfoIdData);

                new Handler().postDelayed(() -> {

                    localSurveyDbViewModel.deleteBulkUnitInfoData(unitInfoIdData, activity);

                    List<String> hohInfoId = new ArrayList<>();
                    for (String unitInfoIdDatum : unitInfoIdData) {
                        hohInfoId.addAll(localSurveyDbViewModel.getHohInfoIdData(unitInfoIdDatum));
                    }

                    if (!hohInfoId.isEmpty()) {
                        mediaInfoRelativePath.addAll(hohInfoId);

                        new Handler().postDelayed(() -> {
                            localSurveyDbViewModel.deleteBulkHohInfoData(hohInfoId, activity);

                            List<String> memberInfoId = new ArrayList<>();

                            for (String hohInfo : hohInfoId) {
                                memberInfoId.addAll(localSurveyDbViewModel.getMemberInfoIdData(hohInfo));
                            }

                            if (!memberInfoId.isEmpty()) {
                                mediaInfoRelativePath.addAll(memberInfoId);

                                new Handler().postDelayed(() -> {
                                    localSurveyDbViewModel.deleteBulkMemberInfoData(memberInfoId, activity);
                                    deleteMediaInfo(mediaInfoRelativePath, true, isForceDelete);
                                }, 1000);
                            } else {
                                deleteMediaInfo(mediaInfoRelativePath, true, isForceDelete);
                            }
                        }, 1000);
                    } else {
                        deleteMediaInfo(mediaInfoRelativePath, true, isForceDelete);
                    }
                }, 1000);
            } else {
                Utils.dismissProgress();
            }
        }, 1000);
    }

    private void deleteMediaInfo(List<String> mediaInfoRelativePath, boolean isAfterUpload, boolean isForceDelete) {

        if (!mediaInfoRelativePath.isEmpty()) {

            new Handler().postDelayed(() -> {

                // TODO: Disabled Delete attachments temporary to keep physical files available in device
                // Utils.deleteDirectory(new File(activity.getExternalFilesDir("") + "/" + selectedUnitDataModel.getRelative_path()));
                localSurveyDbViewModel.deleteBulkMediaInfoData(mediaInfoRelativePath, activity);

                if (!isForceDelete) {
                    if (!isSingleUploadCall) {
                        listenUploadCounter.setValue(fileToUpload++);
                    } else if (isAfterUpload) {
                        Utils.shortToast("Data Upload Successfully", activity);
                        progressBar.setMessage("Completed Successfully");
                        progressBar.setProgress(100);
                        progressBar.dismiss();
                    }
                }
            }, UPLOAD_INTERVAL_MIN);
        } else if (isAfterUpload) {
            Utils.shortToast("Data Upload Successfully", activity);
            progressBar.setMessage("Completed Successfully");
            progressBar.setProgress(100);
            progressBar.dismiss();
        }
    }

    private void saveLogs(String fileName, String extraLog) {

        try {

            userModel = App.getInstance().getUserModel();

            if (userModel != null) {

                String logFileName = Utils.generateLogFilePath(fileName + "_" + Utils.getEpochDateStamp());

                String directoryName = selectedUnitDataModel.getUnit_unique_id().replaceAll("/", "_");
                directoryName = userModel.getUser_name() + "/" + directoryName;

                AppLog.retrieveAppLogs(activity, logFileName, directoryName, extraLog, false);
            } else {
                redirectToLogin();
            }
        } catch (Exception e) {
            AppLog.e(e.getMessage());
        }
    }

    public void stopProcessWithError(String message) {

        saveLogs("Exceptions", message);

        Utils.shortToast(message, activity);

        if (progressBar != null && progressBar.isShowing()) {
            progressBar.dismiss();
        }
    }

    /*public void generateODORequestToken() {

        try {

            JSONObject jObjRequest = new JSONObject();
            jObjRequest.put("jsonrpc", "2.0");

            JSONObject jObjParams = new JSONObject();
            jObjParams.put("username", "mohammad.abdul@igenesys.com");
            jObjParams.put("password", "9aa0ecb45a23b2f42fc582cffead122e195ee2f6a7e02b93e0879534b6987ce742840b48df37e5847037e6897c4a318fac3a3473b10dbacb3d89ad935b55c99b");

            jObjRequest.put("params", jObjParams);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jObjRequest.toString());

            Api_Interface apiInterfaceConfig = RetrofitService.getConfigClient().create(Api_Interface.class);
            apiInterfaceConfig.generateODORequestToken(Constants.ODO_TOKEN_GENERATE, Constants.ODO_SECRET_KEY, body).enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                    Utils.dismissProgress();

                    try {
                        if (response.code() == 200 && response.body() != null && !response.body().toString().isEmpty()) {
                            JSONObject jObjResponse = new JSONObject(response.body().toString());
                            JSONObject jObjData = new JSONObject(jObjResponse.getString("data"));
                            String accessToken = jObjData.getString("access_token");
                        }
                    } catch (Exception e) {
                        AppLog.e(e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    AppLog.e(t.getMessage());
                    call.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*public void addMediaInfoToFailedUploadList(MediaInfoDataModel mediaInfoDataModel, File fileToUpload) {

        try {

            if (mapFailedUploadedUnits == null)
                mapFailedUploadedUnits = new LinkedHashMap<>();

            // Added to UnitId to Hashmap for whom Media Upload Failed.
            if (!mapFailedUploadedUnits.containsKey(selectedUnitDataModel.getUnit_id()))
                mapFailedUploadedUnits.put(selectedUnitDataModel.getUnit_id(), new ArrayList<>());

            saveLogs("unitUploadFailed_Added_" + selectedUnitDataModel.getUnit_id(), new Gson().toJson(mediaInfoDataModel));

            Utils.shortToast("File " + fileToUpload.getName() + " not uploaded.", activity);

            localSurveyDbViewModel.updateMediaIsUploadedInfo(mediaInfoDataModel.getObejctId(), false, mediaInfoDataModel.getMediaId());

            mapFailedUploadedUnits.get(selectedUnitDataModel.getUnit_id()).add(mediaInfoDataModel);
        } catch (Exception e) {
            AppLog.e(e.getMessage());
        }
    }*/

    /*public ArrayList<MediaInfoDataModel> getFailedPendingUploadAttachments() {

        // Added check if any unit have pending uploaded data
        if (mapFailedUploadedUnits != null && !mapFailedUploadedUnits.isEmpty()
                && mapFailedUploadedUnits.containsKey(selectedUnitDataModel.getUnit_id())) {
            ArrayList<MediaInfoDataModel> listPendingUploadMedia = mapFailedUploadedUnits.get(selectedUnitDataModel.getUnit_id());
            saveLogs("unitUploadFailed_Received_" + selectedUnitDataModel.getUnit_id(), new Gson().toJson(listPendingUploadMedia));
            return listPendingUploadMedia;
        }

        saveLogs("unitUploadFailed_Received_" + selectedUnitDataModel.getUnit_id(), "No Media Found to retry Upload!!");

        return null;
    }*/

    //---------------------------------------


    private void saveParticularLogs(String fileName, String extraLog) {

        try {

            userModel = App.getInstance().getUserModel();

            if (userModel != null) {

                String logFileName = Utils.generateLogFilePath(fileName + "_" + Utils.getEpochDateStamp());

                String directoryName = selectedUnitDataModel.getUnit_unique_id().replaceAll("/", "_");
                directoryName = "extra_logs/"+userModel.getUser_name() + "/" + directoryName;

                AppLog.retrieveAppLogs(activity, logFileName, directoryName, extraLog, false);
            } else {
                redirectToLogin();
            }
        } catch (Exception e) {
            AppLog.e(e.getMessage());
        }
    }


    private void getStructureInfo(String uniqueId,StructureInfoPointDataModel structureInfoPointDataModel) {
        if (!Utils.isConnected(activity)) {
            stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
            return;
        }
        AppLog.e("getStructureInfoSurveyDetails ..... ");
        queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
//        Utils.updateProgressMsg("Getting structure details.Please wait..", activity);

        queryResultRepoViewModel.getQueryResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS,
                Constants.StructureInfo_ENDPOINT,
                GetFormModel.getInstance().getQueryBuilderForm("globalid = '" + uniqueId.toUpperCase() + "'", "*", true,
                        Constants.WorkArea_work_last_edited_date + " DESC", 1));

        queryResultRepoViewModel.getMutableLiveData().observe(getActivity(), resultQueryModel -> {
            if (resultQueryModel != null) {
                try {
                    if (resultQueryModel.getFeatures().size() > 0) {
                        Map<String, Object> map = (Map<String, Object>) resultQueryModel.getFeatures().get(0);
                        //Map<String, Object> mapGeometryValue = (Map<String, Object>) map.get("geometry");
                        // Map<String, Object> mapAttributeValue = (Map<String, Object>) map.get("attributes");
                        String geoStr = "";
                        if (map.get("geometry") != null)
                            geoStr = Utils.getGson().toJson((Map<String, Object>) map.get("geometry"));

                        // retrieveStructureDetails(mapAttributeValue, geoStr);
                        Log.d("API response", resultQueryModel.toString());
                        Map<String, Object> mapAttributeValue=(Map<String, Object>) map.get("attributes");
                        if(mapAttributeValue!=null){
                            String globalid = Utils.getString(mapAttributeValue.get(Constants.globalid));
                            if(globalid.equalsIgnoreCase(uniqueId)){
                                new Handler().postDelayed(() -> generateUnitData(structureInfoPointDataModel.getStructSampleGlobalid(),structureInfoPointDataModel,mapAttributeValue), UPLOAD_INTERVAL_MIN);
                            }else{
                                stopProcessWithError("Unit Not Uploaded ! Hut ID not matching with Unit data");
                            }
                        }

                    } else {
                        Utils.dismissProgress();
                        Utils.showMessagePopup("No record found for selected structure.", activity);
                        stopProcessWithError("Unit Not Uploaded Try Again");
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.e("Unable to get the structure details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.showMessagePopup("Unable to get the structure details.", activity);
                    stopProcessWithError("Unit Not Uploaded Try Again");
                }
            } else {
                Utils.dismissProgress();
                Utils.showMessagePopup("No record found for selected structure.", activity);
                stopProcessWithError("Unit Not Uploaded Try Again");
            }
        });
    }

    private void updateSingleAttribute(String message){
        try{
            if (!Utils.isConnected(activity)) {
                stopProcessWithError("No Internet Connectivity Found, Please Connect To Internet.");
                return;
            }

            List<UpdateFeatures.UpdateForm> array = new ArrayList<>();
            Map<String, Object> attributes = new HashMap<>();
            if(selectedUnitDataModel.getVisit_count()>=4){
                int objectId = (int) Utils.doubleFormatter(selectedUnitDataModel.getObejctId());
                attributes.put("objectid", objectId);
                attributes.put("visit_remarks ","VisitCount 4 but found Upload Error");
                Utils.shortToast("VisitCount 4 but found Upload Error", activity);
            }else{
                int objectId = (int) Utils.doubleFormatter(selectedUnitDataModel.getObejctId());
                attributes.put("objectid",objectId);
                attributes.put("visit_remarks","Form Unlocked due to Upload Error");
                attributes.put("form_lock",(short)0);
                Utils.shortToast("Form Unlocked due to Upload Error", activity);
            }
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attributes);
            array.add(updateForm);

            QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
            queryResultRepoViewModel.initUpdateFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.Unit_info_ENDPOINT,
                    GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

            queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

                if (updatedRecordResponse != null && updatedRecordResponse.getAddResults() != null
                        && !updatedRecordResponse.getAddResults().isEmpty() && updatedRecordResponse.getAddResults().get(0).getSuccess()) {
                    saveLogs("Exceptions", "Unit Not Uploaded Try Again");
                    Utils.shortToast(message, activity);
                    if (progressBar != null && progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                } else {
                    saveLogs("Exceptions", "Unit Not Uploaded Try Again");
                    Utils.shortToast(message, activity);
                    if (progressBar != null && progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }
            });

        }catch (Exception ex){
            saveLogs("Exceptions", message);
            Utils.shortToast(message, activity);
            if (progressBar != null && progressBar.isShowing()) {
                progressBar.dismiss();
            }
            ex.getMessage();
        }

    }

    public void proceedAfterUploadDoneForError(String layerType, String message, boolean isAllUploaded, String extraMessage) {
        try{
            if (!Utils.isConnected(activity)) {
                if (isAllUploaded) {
                    generateMediaDetails(layerType);
                } else {
                    saveLogs("uploadAttachments_" + layerType, "Upload Failed Media Upload :: Is Single Media Upload? " + isSingleUploadCall);
                    stopProcessWithError(message);
                    if (isSingleUploadCall) {
                        displayUploadFailedMessageDialog(message, extraMessage);
                    } else {
                        listenUploadCounter.setValue(fileToUpload++);
                    }
                }
                return;
            }

            List<UpdateFeatures.UpdateForm> array = new ArrayList<>();
            Map<String, Object> attributes = new HashMap<>();
            if(selectedUnitDataModel.getVisit_count()>=4){
                int objectId = (int) Utils.doubleFormatter(selectedUnitDataModel.getObejctId());
                attributes.put("objectid", objectId);
                attributes.put("visit_remarks ","VisitCount 4 but found Upload Error");
                Utils.shortToast("VisitCount 4 but found Upload Error", activity);
            }else{
                int objectId = (int) Utils.doubleFormatter(selectedUnitDataModel.getObejctId());
                attributes.put("objectid",objectId);
                attributes.put("visit_remarks","Form Unlocked due to Upload Error");
                attributes.put("form_lock",(short)0);
                Utils.shortToast("Form Unlocked due to Upload Error", activity);
            }
            UpdateFeatures.UpdateForm updateForm = new UpdateFeatures.UpdateForm();
            updateForm.setAttributes(attributes);
            array.add(updateForm);

            QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
            queryResultRepoViewModel.initUpdateFeatureResult(Constants.StructureInfo_FS_BASE_URL_ARC_GIS, Constants.Unit_info_ENDPOINT,
                    GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

            queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

                if (updatedRecordResponse != null && updatedRecordResponse.getAddResults() != null
                        && !updatedRecordResponse.getAddResults().isEmpty() && updatedRecordResponse.getAddResults().get(0).getSuccess()) {
                    if (isAllUploaded) {
                        generateMediaDetails(layerType);
                    } else {
                        saveLogs("uploadAttachments_" + layerType, "Upload Failed Media Upload :: Is Single Media Upload? " + isSingleUploadCall);
                        stopProcessWithError(message);
                        if (isSingleUploadCall) {
                            displayUploadFailedMessageDialog(message, extraMessage);
                        } else {
                            listenUploadCounter.setValue(fileToUpload++);
                        }
                    }
                } else {
                    if (isAllUploaded) {
                        generateMediaDetails(layerType);
                    } else {
                        saveLogs("uploadAttachments_" + layerType, "Upload Failed Media Upload :: Is Single Media Upload? " + isSingleUploadCall);
                        stopProcessWithError(message);
                        if (isSingleUploadCall) {
                            displayUploadFailedMessageDialog(message, extraMessage);
                        } else {
                            listenUploadCounter.setValue(fileToUpload++);
                        }
                    }
                }
            });

        }catch (Exception ex){
            if (isAllUploaded) {
                generateMediaDetails(layerType);
            } else {
                saveLogs("uploadAttachments_" + layerType, "Upload Failed Media Upload :: Is Single Media Upload? " + isSingleUploadCall);
                stopProcessWithError(message);
                if (isSingleUploadCall) {
                    displayUploadFailedMessageDialog(message, extraMessage);
                } else {
                    listenUploadCounter.setValue(fileToUpload++);
                }
            }
            ex.getMessage();
        }
    }
}