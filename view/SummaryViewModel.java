package com.igenesys.view;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ArcGISFeatureTable;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.StructureActivity;
import com.igenesys.SummaryActivity;
import com.igenesys.adapter.SummaryListAdapter;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaDetailsDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureUnitIdStatusDataTable;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.databinding.ActivitySummaryBinding;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.model.SummaryChildItemModel;
import com.igenesys.model.SummaryItemModel;
import com.igenesys.networks.GetFormModel;
import com.igenesys.networks.QueryResultRepoViewModel;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.Utils;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/*
 * This class is view model of login > select work area > choose point > say yes to popup > Summary screen.
 * */
public class SummaryViewModel extends ActivityViewModel<SummaryActivity> implements SummaryListAdapter.OnItemClickListner {
    ArrayList<SummaryChildItemModel> summaryChildItemModels;
    ArrayList<SummaryItemModel> summaryItemModels;
    SummaryListAdapter summaryListAdapter;
    Activity activity;
    ActivitySummaryBinding binding;
    String uniqueId, viewMode;
    LocalSurveyDbViewModel localSurveyDbViewModel;
    ArrayList<SummaryChildItemModel> summary;
    StructureInfoPointDataModel structureInfoPointDataMode;
    //UnitInfoDataModel unitInfoDataModel;
    ArrayList<UnitInfoDataModel> unitInfoDataModelArrayList;
    ArrayList<UnitInfoDataModel> unitInfoAddedPositionDataModelArrayList;
    boolean IS_EDITING;
    QueryResultRepoViewModel queryResultRepoViewModel;
    ArrayList<MediaInfoDataModel> mediaInfoDataModels;
    // int unitAttachmentQuery = 0;
    ArrayList<HohInfoDataModel> hohInfoDataModelArrayList;
    ArrayList<MemberInfoDataModel> memberInfoDataModelArrayList;
    HashMap<String, String> hohIdNameLst;

    int hohQueryCount = 0;
    ArrayList<Map<String, Object>> unitMapAttributesArrayList;
    ArrayList<Map<String, Object>> hohMapAttributesArrayList;
    ArrayList<Map<String, Object>> memberMapAttributesArrayList;
    HashMap<String, List<Attachment>> unitAttachmentHashmap;
    HashMap<String, List<Attachment>> hohAttachmentHashmap;
    HashMap<String, List<Attachment>> memberAttachmentHashmap;
    String viewModeSearchItem = "";
    int featureTableUnitPosition = 0;
    int featureTableHohPosition = 0;
    int featureTableMemberPosition = 0;
    String summaryStructUniqueId = "";
    ArrayList<String> relGlobalidForHoh;
    ArrayList<String> relGlobalidForMember;
    HashMap<String, UnitDetailsSummary> unitSummaryHashmap;
    HashMap<String, ArrayList<MediaInfoDataModel>> unitMediaInfoDataModel;
    HashMap<String, ArrayList<MediaInfoDataModel>> hohMediaInfoDataModel;
    HashMap<String, ArrayList<MediaInfoDataModel>> memberMediaInfoDataModel;
    HashMap<String, ArrayList<HohInfoDataModel>> hohListForUnitGlobalIdHashmap;
    HashMap<String, ArrayList<MemberInfoDataModel>> memberListForUnitGlobalIdHashmap;
    String unitStructureUniqueId = "";
    ArrayList<MediaInfoDataModel> mediaModelArrayList;
    LinkedHashMap<String, ArrayList<MediaDetailsDataModel>> mapMediaDetailsModelUnit;
    LinkedHashMap<String, ArrayList<MediaDetailsDataModel>> mapMediaDetailsModelHoh;
    LinkedHashMap<String, ArrayList<MediaDetailsDataModel>> mapMediaDetailsModelMember;
    ArrayList<Map<String, Object>> mediaAttributesArrayList;
    ArrayList<Map<String, Object>> listMediaDetailsAttributeUnit;
    ArrayList<Map<String, Object>> listMediaDetailsAttributeHOH;
    ArrayList<Map<String, Object>> listMediaDetailsAttributeMember;
    String s = "";

    ArrayList<String> filterUniqueIdList = new ArrayList<>();
    ArrayList<String> alreadyAddedUniqueId = new ArrayList<>();
    ArrayList<String> unitIdList = new ArrayList<>();
    /*
     * initializes binding object to access ui elements object. initializes localSurveyDbViewModel object to make db method call.
     * */

    public SummaryViewModel(SummaryActivity activity) {
        super(activity);
        this.activity = activity;
        binding = activity.getBinding();

        binding.commonHeader.txtPageHeader.setText("Summary");
        binding.commonHeader.imgBack.setOnClickListener(view -> finish());

        localSurveyDbViewModel = ViewModelProviders.of(getActivity()).get(LocalSurveyDbViewModel.class);
        initView();
    }

    /*
     * Takes pdf file path from sharedpreference & encrypts pdf file.
     * */

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
            AppLog.logData(activity, e.getMessage());
            throw new RuntimeException(e);
        }
        //EsriAuthUtil.init(activity);
//        initView();
    }

    private void initView() {
        filterUniqueIdList = App.getInstance().getListUniqueId();
        uniqueId = App.getSharedPreferencesHandler().getString(Constants.uniqueId);
        viewMode = App.getSharedPreferencesHandler().getString(Constants.viewMode);

        summaryChildItemModels = new ArrayList<>();
        summaryItemModels = new ArrayList<>();
        binding.rvSummary.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        summaryListAdapter = new SummaryListAdapter(activity, null, Constants.MemberPhotograph, this);
        binding.rvSummary.setAdapter(summaryListAdapter);

        binding.cvEdit.setOnClickListener(view -> {

            if (structureInfoPointDataMode != null && structureInfoPointDataMode.getHut_number() == null)
                return;

            ArrayList<String> dbLocalUniquIdList = new ArrayList<>(localSurveyDbViewModel.getLocalAddedUniqueIdList(structureInfoPointDataMode.getHut_number()));
            ArrayList<String> mainList = App.getInstance().getListUniqueId();
            ArrayList<String> testList = new ArrayList<>();
            if (mainList != null && dbLocalUniquIdList != null) {
                for (int i = 0; i < dbLocalUniquIdList.size(); i++) {
                    if (mainList.contains(dbLocalUniquIdList.get(i))) {
                        mainList.remove(dbLocalUniquIdList.get(i));
                    }
                }
            }
            if (mainList != null && mainList.size() > 0) {
                for (int i = 0; i < mainList.size(); i++) {
                    if (!mainList.get(i).equalsIgnoreCase("")) {
                        testList.add(mainList.get(i));
                    }
                }
            }
            if (testList != null && testList.size() > 0) {
                AppLog.d("Event: Add New Unit....");
                if (!structureInfoPointDataMode.getStructure_status().equals(Constants.completed) && !structureInfoPointDataMode.getStructure_status().equals(Constants.dispute)) {
                    activity.startActivity(new Intent(activity, StructureActivity.class)
                            .putExtra(Constants.IS_EDITING, IS_EDITING)
                            .putExtra(Constants.EDIT_TYPE, Constants.NewInfoData)
                            .putExtra("flow", true)
                            .putExtra(Constants.viewMode, viewMode)
                            .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataMode));
                    activity.finish();
                } else if (structureInfoPointDataMode.getStructure_status().equals(Constants.completed)) {
                    String structureErrorMsg = activity.getString(R.string.struct_lock_error_msg_unit);
                    Utils.shortToast(structureErrorMsg, activity);
                }
            } else {
                Utils.shortToast("You can-not create new unit as all units are already visited", activity);
            }
        });

        if (activity.getIntent().hasExtra(Constants.viewMode)) {
            viewModeSearchItem = activity.getIntent().getStringExtra(Constants.viewMode);
        }

        if (viewMode.equals(Constants.offline)) {
            viewMode = Constants.offline;
            setUpOfflineStructureDetails(uniqueId);
            IS_EDITING = true;
        } else if (viewModeSearchItem.equals("searchItem")) {
            viewMode = Constants.online;
            getStructureInfoSurveyDetails(uniqueId);
            IS_EDITING = true;
        } else if (App.getSharedPreferencesHandler().getBoolean(Constants.isMapLoadOffline)) {
            viewMode = Constants.offlineWebmap;
            IS_EDITING = true;
            getStructureInfoSurveyDetailsOffline(uniqueId);
        } else if (Utils.isConnected(activity)) {
            viewMode = Constants.online;
            getStructureInfoSurveyDetails(uniqueId);
            IS_EDITING = true;
        }
        //Utils.shortToast("viewMode: "+viewMode, activity);
    }

    private void getStructureInfoSurveyDetailsOffline(String uniqueId) {
        try {
            Utils.updateProgressMsg("Getting structure details.Please wait..", activity);

            summaryItemModels = new ArrayList<>();

            summaryListAdapter = new SummaryListAdapter(activity, null, Constants.MemberPhotograph, this);
            binding.rvSummary.setAdapter(summaryListAdapter);

            GeodatabaseFeatureTable structureFeatureTable = App.getInstance().getStructureGFT();
            if (structureFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                QueryParameters query = new QueryParameters();
                query.setWhereClause("globalid = '{" + uniqueId.toUpperCase() + "}'");
                query.getOrderByFields().add(new QueryParameters.OrderBy(Constants.WorkArea_work_last_edited_date, QueryParameters.SortOrder.DESCENDING));

                final ListenableFuture<FeatureQueryResult> future = structureFeatureTable.queryFeaturesAsync(query);
                future.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = future.get();
                        if (result.iterator().hasNext()) {
                            Feature feature = result.iterator().next();

                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            String geoStr = "";
                            if (feature.getGeometry() != null)
                                geoStr = feature.getGeometry().toJson();
                            retrieveStructureDetails(mapAttributeValue, geoStr);

                        } else {
                            Utils.dismissProgress();
                            Utils.showMessagePopup("No record found for selected structure.", activity);
                        }
                    } catch (Exception e) {
                        Utils.dismissProgress();
                        AppLog.logData(activity, e.getMessage());
                        AppLog.e("Unable to get the details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                        Utils.showMessagePopup("Unable to get the structure details.", activity);
                    }
                });
            } else if (structureFeatureTable.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                Utils.dismissProgress();
                AppLog.e("Unable to get the structure details.\n Error cause: " + structureFeatureTable.getLoadError().getCause() + "\nError message: " + structureFeatureTable.getLoadError().getMessage());
                Utils.showMessagePopup("Unable to get the structure details.", activity);
            }
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.logData(activity, ex.getMessage());
            AppLog.e("getStructureInfoSurveyDetailsOffline Unable to get the structure details.\n Error cause: " + ex.getMessage());
            Utils.showMessagePopup("getStructureInfoSurveyDetailsOffline Unable to get the structure details.", activity);
        }

    }


    private void getStructureInfoSurveyDetails(String uniqueId) {
        AppLog.e("getStructureInfoSurveyDetails ..... ");
        queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        if (!Utils.checkinterne(activity))
            return;
        Utils.updateProgressMsg("Getting structure details.Please wait..", activity);

        summaryItemModels = new ArrayList<>();

        summaryListAdapter = new SummaryListAdapter(activity, null, Constants.MemberPhotograph, this);
        binding.rvSummary.setAdapter(summaryListAdapter);


//temp testing code need to remove
        QueryParameters query = new QueryParameters();
        query.setWhereClause("globalid = '{" + uniqueId.toUpperCase() + "}'");

        //fetchData(App.getmInstance().getStructureFT(), query);


        queryResultRepoViewModel.getQueryResult(Constants.StructureInfo_MS_BASE_URL_ARC_GIS,
                Constants.StructureInfo_ENDPOINT,
                GetFormModel.getInstance().getQueryBuilderForm("globalid = '{" + uniqueId.toUpperCase() + "}'", "*", true,
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
                        retrieveStructureDetails((Map<String, Object>) map.get("attributes"), geoStr);

                    } else {
                        Utils.dismissProgress();
                        Utils.showMessagePopup("No record found for selected structure.", activity);
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.e("Unable to get the structure details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.showMessagePopup("Unable to get the structure details.", activity);
                }
            } else {
                Utils.dismissProgress();
                Utils.showMessagePopup("No record found for selected structure.", activity);
            }
        });

    }

    private void retrieveStructureDetails(Map<String, Object> mapAttributeValue, String geometry) {
        unitStructureUniqueId = "";
        try {
            if (mapAttributeValue != null) {
                String structUniqueId, globalid;

                if (Utils.isNullOrEmpty(Utils.getString(mapAttributeValue.get(Constants.StructureInfo_structure_id)))) {
                    structUniqueId = "S_" + Utils.getEpochDateStamp();
                } else
                    structUniqueId = Utils.getString(mapAttributeValue.get(Constants.StructureInfo_structure_id));

                unitStructureUniqueId = structUniqueId;

                AppLog.e("Summary structUniqueId : " + structUniqueId);
                globalid = Utils.getString(mapAttributeValue.get(Constants.globalid));

//                if (Utils.getString(mapAttributeValue.get(Constants.globalid)).startsWith("{"))
//                    globalid = structurePointGlobalIdStr.substring(1, structurePointGlobalIdStr.length() - 1);
//                else globalid = Utils.getString(mapAttributeValue.get(Constants.globalid));
                if(mapAttributeValue!=null && mapAttributeValue.containsKey("nagar_name")){
                    String workAreaName= App.getSharedPreferencesHandler().getString(Constants.workAreaNameN);
                    App.getSharedPreferencesHandler().putString(workAreaName,Utils.getString(mapAttributeValue.get("nagar_name")));
                }
                if(mapAttributeValue!=null && mapAttributeValue.containsKey("structure_type")){
                    App.getSharedPreferencesHandler().putString(Constants.structureTypeName,Utils.getString(mapAttributeValue.get("structure_type")));
                }
                summaryStructUniqueId = structUniqueId;
                structureInfoPointDataMode = new StructureInfoPointDataModel(
                        structUniqueId,
                        globalid,
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_structure_id)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_grid_number)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_area_name)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_cluster_name)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_phase_name)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_work_area_name)),
//                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_hut_number)),
                        Utils.getString(mapAttributeValue.get(Constants.UnitInfo_hut_number)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_structure_name)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_structure_usage)),
                        (int) Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.StructureInfo_no_of_floors))),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_address)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_tenement_number)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_source_of_possession)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_existance_since)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_structure_since)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_structure_status)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_surveyor_name)),
                        Utils.getString(mapAttributeValue.get(Constants.StructureInfo_remarks)),
                        geometry,
                        Utils.getString(mapAttributeValue.get(Constants.objectid)),
                        Utils.getString(mapAttributeValue.get(Constants.globalid)),
                        true,
                        new Date(),
                        new Date(),
                        Utils.getString(mapAttributeValue.get(Constants.Unit_zone_no)),
                        Utils.getString(mapAttributeValue.get(Constants.Unit_ward_no)),
                        Utils.getString(mapAttributeValue.get(Constants.Unit_sector_no)),
                        Utils.getString(mapAttributeValue.get(Constants.country_name)),
                        Utils.getString(mapAttributeValue.get(Constants.state_name)),
                        Utils.getString(mapAttributeValue.get(Constants.city_name))
                );


                ArrayList<SummaryChildItemModel> summaryChild = new ArrayList<>();
                summaryChild.add(new SummaryChildItemModel("Hut ID", structureInfoPointDataMode.getHut_number(), "", false, false));
//                summaryChild.add(new SummaryChildItemModel("Survey Unique ID No.", structureInfoPointDataMode.getStructure_id(), "", false, false));
//                summaryChild.add(new SummaryChildItemModel("Tenement No.", structureInfoPointDataMode.getTenement_number(), "", false, false));
//                summaryChild.add(new SummaryChildItemModel("Name of Nagar/Area", structureInfoPointDataMode.getArea_name(), "", false, false));
                summaryChild.add(new SummaryChildItemModel("Work Area", structureInfoPointDataMode.getWork_area_name(), "", false, false));
                //summaryChild.add(new SummaryChildItemModel("Grid Number", structureInfoPointDataMode.getGrid_number(), "", false, false));
//                summaryChild.add(new SummaryChildItemModel("Address", structureInfoPointDataMode.getAddress(), "", false, false));
                summaryChild.add(new SummaryChildItemModel("No. of Floor", structureInfoPointDataMode.getNo_of_floors() + "", "", false, false));

                binding.txtTenantNumber.setText(structureInfoPointDataMode.getHut_number());

                setupStatus(structureInfoPointDataMode.getStructure_status());

                summaryItemModels.add(new SummaryItemModel("Structure Details", "", summaryChild, false, null, null, false));

                summaryListAdapter.setSummaryItemModels(summaryItemModels);

                mediaAttributesArrayList = new ArrayList<>();
                listMediaDetailsAttributeUnit = new ArrayList<>();
                listMediaDetailsAttributeHOH = new ArrayList<>();
                listMediaDetailsAttributeMember = new ArrayList<>();
                mediaModelArrayList = new ArrayList<>();
                mapMediaDetailsModelUnit = new LinkedHashMap<>();
                mapMediaDetailsModelHoh = new LinkedHashMap<>();
                mapMediaDetailsModelMember = new LinkedHashMap<>();
                unitMapAttributesArrayList = new ArrayList<>();
                hohMapAttributesArrayList = new ArrayList<>();
                memberMapAttributesArrayList = new ArrayList<>();
                unitInfoDataModelArrayList = new ArrayList<>();
                unitInfoAddedPositionDataModelArrayList = new ArrayList<>();
                unitAttachmentHashmap = new HashMap<>();
                hohAttachmentHashmap = new HashMap<>();
                memberAttachmentHashmap = new HashMap<>();

                if (viewMode.equals(Constants.online))
                    getUnitInfoSurveyDetailsOnline(structureInfoPointDataMode.getStructSampleGlobalid());
                else if (viewMode.equals(Constants.offlineWebmap))
                    getUnitInfoSurveyDetailsOffline(structureInfoPointDataMode.getStructSampleGlobalid());
            } else {
                Utils.dismissProgress();
                Utils.showMessagePopup("Unable to get the structure details.", activity);
            }
        } catch (Exception e) {
            Utils.dismissProgress();
            AppLog.logData(activity, e.getMessage());
            AppLog.e("Unable to get the structure details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.showMessagePopup("Unable to get the structure details.", activity);
        }
    }


    private void getMediaInfoOfflineMap(String uniqueId, UnitInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        Utils.updateProgressMsg("Getting Media Info records .Please wait..", activity);
        GeodatabaseFeatureTable geodatabaseFeatureTable = App.getInstance().getMediaGFT();
        if (geodatabaseFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();
            query.setWhereClause("rel_globalid = '{" + uniqueId.toUpperCase() + "}'");
            AppLog.e("unit rel_globalid : '" + uniqueId.toUpperCase() + "'");
            final ListenableFuture<FeatureQueryResult> future = geodatabaseFeatureTable.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        for (final Feature feature : result) {
                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            s = Utils.getString(mapAttributeValue.get("objectid"));
                            Utils.getString(mapAttributeValue.get("globalid"));
                            ArcGISFeature arcGISFeature = (ArcGISFeature) feature;
                            List<Attachment> attachmentsAsync;
                            try {
                                attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                            } catch (Exception e) {
                                attachmentsAsync = new ArrayList<>();
                            }
                            unitAttachmentHashmap.put("" + s, attachmentsAsync);
                            mediaAttributesArrayList.add(mapAttributeValue);
                        }
                        retrieveMediaDetails(mediaAttributesArrayList, "" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                    } else {
                        //this is for the first unit info
                        setUpSummaryItem(unitInfoDataModel.getUnit_unique_id(), unitInfoDataModel.getUnit_status(),
                                summary, true, null, null, false);
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.shortToast("Unable to get the unit details.", activity);
                }
            });
        } else if (geodatabaseFeatureTable.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + geodatabaseFeatureTable.getLoadError().getCause() + "\nError message: " + geodatabaseFeatureTable.getLoadError().getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getMediaInfoHohOffline(String uniqueId, HohInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        Utils.updateProgressMsg("Getting Media Info records .Please wait..", activity);
        GeodatabaseFeatureTable geodatabaseFeatureTable = App.getInstance().getMediaGFT();
        if (geodatabaseFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();
            query.setWhereClause("rel_globalid = '{" + uniqueId.toUpperCase() + "}'");
            AppLog.e("unit rel_globalid : '" + uniqueId.toUpperCase() + "'");
            final ListenableFuture<FeatureQueryResult> future = geodatabaseFeatureTable.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        for (final Feature feature : result) {
                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            s = Utils.getString(mapAttributeValue.get("objectid"));
                            Utils.getString(mapAttributeValue.get("globalid"));
                            ArcGISFeature arcGISFeature = (ArcGISFeature) feature;
                            List<Attachment> attachmentsAsync;
                            try {
                                attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                            } catch (Exception e) {
                                attachmentsAsync = new ArrayList<>();
                            }
                            hohAttachmentHashmap.put("" + s, attachmentsAsync);
                            mediaAttributesArrayList.add(mapAttributeValue);
                        }
                        retrieveHohMediaDetails(mediaAttributesArrayList, "" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                    } else {
                        getHohAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                        Utils.dismissProgress();
//                        Utils.shortToast("No unit details found for this structure.", activity);
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.shortToast("Unable to get the unit details.", activity);
                }
            });
        } else if (geodatabaseFeatureTable.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + geodatabaseFeatureTable.getLoadError().getCause() + "\nError message: " + geodatabaseFeatureTable.getLoadError().getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }


    private void getMediaInfoMemberOffline(String uniqueId, MemberInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        try {
            Utils.updateProgressMsg("Getting Media Info records .Please wait..", activity);
            GeodatabaseFeatureTable geodatabaseFeatureTable = App.getInstance().getMediaGFT();
            if (geodatabaseFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                QueryParameters query = new QueryParameters();
                query.setWhereClause("rel_globalid = '{" + uniqueId.toUpperCase() + "}'");
                AppLog.e("unit rel_globalid : '" + uniqueId.toUpperCase() + "'");
                final ListenableFuture<FeatureQueryResult> future = geodatabaseFeatureTable.queryFeaturesAsync(query);
                future.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = future.get();
                        if (result.iterator().hasNext()) {
                            for (final Feature feature : result) {
                                Map<String, Object> mapAttributeValue = feature.getAttributes();
                                s = Utils.getString(mapAttributeValue.get("objectid"));
                                Utils.getString(mapAttributeValue.get("globalid"));
                                ArcGISFeature arcGISFeature = (ArcGISFeature) feature;
                                List<Attachment> attachmentsAsync;
                                try {
                                    attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                                } catch (Exception e) {
                                    attachmentsAsync = new ArrayList<>();
                                }
                                memberAttachmentHashmap.put("" + s, attachmentsAsync);
                                mediaAttributesArrayList.add(mapAttributeValue);
                            }
                            retrieveMemberMediaDetails(mediaAttributesArrayList, "" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                        } else {
                            getMemberAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                            Utils.dismissProgress();
//                        Utils.shortToast("No unit details found for this structure.", activity);
                        }
                    } catch (Exception e) {
                        Utils.dismissProgress();
                        AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                        Utils.shortToast("Unable to get the unit details.", activity);
                    }
                });
            } else if (geodatabaseFeatureTable.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                Utils.dismissProgress();
                AppLog.e("Unable to get the unit details.\n Error cause: " + geodatabaseFeatureTable.getLoadError().getCause() + "\nError message: " + geodatabaseFeatureTable.getLoadError().getMessage());
                Utils.shortToast("Unable to get the unit details.", activity);
            }
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }


    private void getUnitInfoSurveyDetailsOffline(String uniqueId) {

        Utils.updateProgressMsg("Getting unit records .Please wait..", activity);

        GeodatabaseFeatureTable geodatabaseFeatureTable = App.getInstance().getUnitGFT();
        if (geodatabaseFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();
            query.setWhereClause("rel_globalid = '{" + uniqueId.toUpperCase() + "}'");
//            query.getOrderByFields().add(new QueryParameters.OrderBy("unit_no", QueryParameters.SortOrder.ASCENDING));

            final ListenableFuture<FeatureQueryResult> future = geodatabaseFeatureTable.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        for (final Feature feature : result) {
                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            ArcGISFeature arcGISFeature = (ArcGISFeature) feature;

                            List<Attachment> attachmentsAsync;
                            try {
                                attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                            } catch (Exception e) {
                                AppLog.logData(activity, e.getMessage());
                                attachmentsAsync = new ArrayList<>();
                            }

                            unitAttachmentHashmap.put("" + (int) Double.parseDouble(Utils.getString(mapAttributeValue.get(Constants.objectid))), attachmentsAsync);
                            unitMapAttributesArrayList.add(mapAttributeValue);
                        }
                        relGlobalidForHoh = new ArrayList<>();
                        relGlobalidForMember = new ArrayList<>();
                        unitSummaryHashmap = new HashMap<>();
                        retrieveUnitDetails(unitMapAttributesArrayList);
                    } else {
                        Utils.dismissProgress();
                        Utils.shortToast("No unit details found for this structure.", activity);
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.logData(activity, e.getMessage());
                    AppLog.e("getUnitInfoSurveyDetailsOfflineUnable to get the details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.shortToast("getUnitInfoSurveyDetailsOffline Unable to get the unit details.", activity);
                }
            });
        } else if (geodatabaseFeatureTable.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + geodatabaseFeatureTable.getLoadError().getCause() + "\nError message: " + geodatabaseFeatureTable.getLoadError().getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getUnitInfoSurveyDetailsOnline(String uniqueId) {

        if (!Utils.checkinterne(activity))
            return;

        Utils.updateProgressMsg("Getting unit records .Please wait..", activity);

        ServiceFeatureTable unitFT = App.getInstance().getUnitFT();
        if (unitFT.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();
            query.setWhereClause("rel_globalid = '" + uniqueId.toUpperCase() + "'");
            AppLog.e("unit rel_globalid : '" + uniqueId + "'");

//            query.getOrderByFields().add(new QueryParameters.OrderBy("unit_no", QueryParameters.SortOrder.ASCENDING));

            final ListenableFuture<FeatureQueryResult> future = unitFT.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        Map<String, Object> mapAttributeValue;
                        ArcGISFeature arcGISFeature;
                        List<Attachment> attachmentsAsync = new ArrayList<>();
                        for (final Feature feature : result) {
                            mapAttributeValue = feature.getAttributes();
                            arcGISFeature = (ArcGISFeature) feature;
                            try {
                                attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                            } catch (Exception e) {
                                AppLog.logData(activity, e.getMessage());
                                //attachmentsAsync = new ArrayList<>();
                            }

                            unitAttachmentHashmap.put("" + (int) Double.parseDouble(Utils.getString(mapAttributeValue.get(Constants.objectid))), attachmentsAsync);
                            unitMapAttributesArrayList.add(mapAttributeValue);
                        }

                        relGlobalidForHoh = new ArrayList<>();
                        relGlobalidForMember = new ArrayList<>();
                        unitSummaryHashmap = new HashMap<>();
                        retrieveUnitDetails(unitMapAttributesArrayList);
                    } else {
                        Utils.dismissProgress();
                        Utils.shortToast("No unit details found for this structure.", activity);
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.logData(activity, e.getMessage());
                    AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.shortToast("Unable to get the unit details.", activity);
                }
            });
        } else if (unitFT.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + unitFT.getLoadError().getCause() + "\nError message: " + unitFT.getLoadError().getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    /*
     * Removed memebr availability condition and fetch all fields from server
     * @auther : Jaid
     */
    private void retrieveUnitDetails(ArrayList<Map<String, Object>> unitMapAttributesArrayList) {
        try {
            String unitStatus;
            UnitInfoDataModel unitInfoDataModel;
            boolean memAvail = false;
            boolean loftStr = false;
            ArrayList<StructureUnitIdStatusDataTable> structureUnitIdStatusDataTable = new ArrayList<>();
            for (Map<String, Object> mapAttributeValue : unitMapAttributesArrayList) {
                if (mapAttributeValue != null) {
                    if (!Utils.isNullOrEmpty(Utils.getString(mapAttributeValue.get(Constants.unit_unique_id)))) {
                        alreadyAddedUniqueId.add(Utils.getString(mapAttributeValue.get(Constants.unit_unique_id)));
                        unitIdList.add(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_id)));
                        if (filterUniqueIdList.contains(Utils.getString(mapAttributeValue.get(Constants.unit_unique_id)))) {
                            filterUniqueIdList.remove(Utils.getString(mapAttributeValue.get(Constants.unit_unique_id)));
                        }
                    }
                    App.getInstance().setListUniqueId(filterUniqueIdList);


                    unitStatus = Constants.InProgress_statusLayer;

                    if (!Utils.isNullOrEmpty(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_status))))
                        unitStatus = Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_status));

                    if (!Utils.isNullOrEmpty(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_id))))
                        structureUnitIdStatusDataTable.add(new StructureUnitIdStatusDataTable(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_id)),
                                unitStructureUniqueId, unitStatus));

                    //can remove if condition , get ALL data
                    //if

                    boolean isYesNo = false;
                    if (mapAttributeValue.get(Constants.UnitInfo_member_available).equals("YES")) {
                        memAvail = true;
                        if (mapAttributeValue.get(Constants.Unit_loft_present) != null) {
                            if (mapAttributeValue.get(Constants.Unit_loft_present) != null && mapAttributeValue.get(Constants.Unit_loft_present).equals("YES") || mapAttributeValue.get(Constants.Unit_loft_present).equals("Yes")) {
                                loftStr = true;
                            } else {
                                loftStr = false;
                            }
                        } else {
                            loftStr = false;
                        }
                    } else {
                        memAvail = false;
                    }
                    if (!memAvail && Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_dob)) != null && !Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_dob)).equals("")) {
                        isYesNo = true;
                        if (mapAttributeValue.get(Constants.Unit_loft_present) != null) {
                            if (mapAttributeValue.get(Constants.Unit_loft_present) != null && mapAttributeValue.get(Constants.Unit_loft_present).equals("YES") || mapAttributeValue.get(Constants.Unit_loft_present).equals("Yes")) {
                                loftStr = true;
                            } else {
                                loftStr = false;
                            }
                        } else {
                            loftStr = false;
                        }
                    }

                    if (!memAvail && isYesNo) {
                        unitInfoDataModel = new UnitInfoDataModel(
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_id)),
                                Utils.getString(mapAttributeValue.get(Constants.unit_unique_id)),
                                Utils.getString(mapAttributeValue.get(Constants.globalid)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_relative_path)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_tenement_number)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_hut_number)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_floor)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_no)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_usage)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_structure_year)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_nature_of_activity)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_religious_area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                memAvail,
                                Utils.getString(mapAttributeValue.get(Constants.Unit_member_non_available_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_existance_since)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_structure_since)), false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_share_certificate)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_electric_bill)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_photo_pass)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_na_tax)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_property_tax)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_electoral_roll)).equals("1"),
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_school_college_certificate)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_employer_certificate)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_chain_document)).equals("1"),
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rent_agreement)).equals("1"),
                                false,
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_restaurant_hotel_license)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_food_drug_license)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_factory_act_license)).equals("1"),
                                false,
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_status)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_surveyor_name)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_surveyor_desig)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_name)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_name_other)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_desig)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_desig_other)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_remarks_other)),
                                Utils.getInteger(mapAttributeValue.get(Constants.UnitInfo_media_captured_cnt)),
                                Utils.getInteger(mapAttributeValue.get(Constants.UnitInfo_media_upload_cnt)),
                                Utils.getString(mapAttributeValue.get(Constants.objectid)),
                                Utils.getString(mapAttributeValue.get(Constants.globalid)).toUpperCase(),
                                new Date(),
                                new Date(),
                                Utils.getInteger(mapAttributeValue.get(Constants.Unit_visit_count)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_area_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_ward_no)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_sector_no)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_zone_no)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_nagar_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_nagar_name_other)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_society_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_street_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_landmark_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_dob)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_age)),
                                Utils.getString(mapAttributeValue.get(Constants.ResHohName)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_hoh_relationship)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_hoh_relationship_other)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_tenement_document)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_mashal_survey_number)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_ownership_status)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                loftStr,
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Loft_N_Area))),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_employees_count)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Gumasta_N_Area))), false,
                                Utils.getString(mapAttributeValue.get(Constants.Pincode)),
                                Utils.getString(mapAttributeValue.get(Constants.RespContact)),
                                Utils.getString(mapAttributeValue.get(Constants.VisitDate)),
                                Utils.getShort(mapAttributeValue.get(Constants.FormLock)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_hoh_contact)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_survey_date)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_survey_time)),
                                Utils.getString(mapAttributeValue.get(Constants.Type_of_other_structure)),
                                true, "",
                                Utils.getString(mapAttributeValue.get(Constants.Unit_pavti_num)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_existince_year_only)),
                                Utils.getString(mapAttributeValue.get(Constants.country_name)),
                                Utils.getString(mapAttributeValue.get(Constants.state_name)),
                                Utils.getString(mapAttributeValue.get(Constants.city_name)),
                                Utils.getString(mapAttributeValue.get(Constants.access_to_unit)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.RcResArea))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.RcCommArea))),
                                Utils.getString(mapAttributeValue.get(Constants.domain_thumb_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.domain_annexure_remarks)),

                                Utils.getString(mapAttributeValue.get(Constants.structure_type_religious)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_religious_other)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_type_amenities)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_amenities_other)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_structure)),
                                Utils.getString(mapAttributeValue.get(Constants.type_of_diety)),
                                Utils.getString(mapAttributeValue.get(Constants.type_of_diety_other)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_diety)),
                                Utils.getString(mapAttributeValue.get(Constants.category_of_faith)),
                                Utils.getString(mapAttributeValue.get(Constants.category_of_faith_other)),
                                Utils.getString(mapAttributeValue.get(Constants.sub_category_of_faith)),
                                Utils.getString(mapAttributeValue.get(Constants.religion_of_structure_belongs)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_ownership_status)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_trust_or_owner)),
                                Utils.getString(mapAttributeValue.get(Constants.nature_of_structure)),
                                Utils.getString(mapAttributeValue.get(Constants.construction_material)),
                                String.valueOf(Utils.getInteger(mapAttributeValue.get(Constants.daily_visited_people_count))),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_tenement_number)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_tenement_document)),
                                Utils.getString(mapAttributeValue.get(Constants.tenement_document_other)),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.is_structure_registered))),
                                Utils.getString(mapAttributeValue.get(Constants.structure_registered_with)),
                                Utils.getString(mapAttributeValue.get(Constants.other_religious_authority)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_trustee)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_landowner)),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.noc_from_landlord_or_govt))),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.approval_from_govt))),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.yearly_festival_conducted))),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_pavti_num)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_mashal_survey_number)),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.ra_total_no_of_floors_txt))),
                                Utils.getString(mapAttributeValue.get(Constants.panchnama_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.Gen_QC_REMARKS)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Latitude))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Longitude))),
                                Utils.getString(mapAttributeValue.get(Constants.Genesys_Display_Name)),
                                Utils.getString(mapAttributeValue.get(Constants.Primary_IMEI)),
                                Utils.getString(mapAttributeValue.get(Constants.Secondary_IMEI)),
                                Utils.getShort(mapAttributeValue.get(Constants.Is_DRPPL_Available)),
                                Utils.getString(mapAttributeValue.get(Constants.DRPPL_Officer_Name))
                        );
                    } else if (memAvail) {
                        unitInfoDataModel = new UnitInfoDataModel(
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_id)),
                                Utils.getString(mapAttributeValue.get(Constants.unit_unique_id)),
                                Utils.getString(mapAttributeValue.get(Constants.globalid)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_relative_path)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_tenement_number)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_hut_number)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_floor)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_no)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_usage)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_structure_year)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_nature_of_activity)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_religious_area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                memAvail,
                                Utils.getString(mapAttributeValue.get(Constants.Unit_member_non_available_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_existance_since)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_structure_since)), false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_share_certificate)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_electric_bill)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_photo_pass)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_na_tax)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_property_tax)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_electoral_roll)).equals("1"),
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_school_college_certificate)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_employer_certificate)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_chain_document)).equals("1"),
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rent_agreement)).equals("1"),
                                false,
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_restaurant_hotel_license)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_food_drug_license)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_factory_act_license)).equals("1"),
                                false,
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_status)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_surveyor_name)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_surveyor_desig)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_name)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_name_other)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_desig)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_desig_other)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_remarks_other)),
                                Utils.getInteger(mapAttributeValue.get(Constants.UnitInfo_media_captured_cnt)),
                                Utils.getInteger(mapAttributeValue.get(Constants.UnitInfo_media_upload_cnt)),
                                Utils.getString(mapAttributeValue.get(Constants.objectid)),
                                Utils.getString(mapAttributeValue.get(Constants.globalid)).toUpperCase(),
                                new Date(),
                                new Date(),
                                Utils.getInteger(mapAttributeValue.get(Constants.Unit_visit_count)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_area_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_ward_no)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_sector_no)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_zone_no)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_nagar_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_nagar_name_other)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_society_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_street_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_landmark_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_dob)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_age)),
                                Utils.getString(mapAttributeValue.get(Constants.ResHohName)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_hoh_relationship)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_hoh_relationship_other)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_tenement_document)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_mashal_survey_number)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_ownership_status)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                loftStr,
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Loft_N_Area))),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_employees_count)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Gumasta_N_Area))), false,
                                Utils.getString(mapAttributeValue.get(Constants.Pincode)),
                                Utils.getString(mapAttributeValue.get(Constants.RespContact)),
                                Utils.getString(mapAttributeValue.get(Constants.VisitDate)),
                                Utils.getShort(mapAttributeValue.get(Constants.FormLock)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_respondent_hoh_contact)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_survey_date)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_survey_time)),
                                Utils.getString(mapAttributeValue.get(Constants.Type_of_other_structure)),
                                false, "",
                                Utils.getString(mapAttributeValue.get(Constants.Unit_pavti_num)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_existince_year_only)),
                                Utils.getString(mapAttributeValue.get(Constants.country_name)),
                                Utils.getString(mapAttributeValue.get(Constants.state_name)),
                                Utils.getString(mapAttributeValue.get(Constants.city_name)),
                                Utils.getString(mapAttributeValue.get(Constants.access_to_unit)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.RcResArea))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.RcCommArea))),
                                Utils.getString(mapAttributeValue.get(Constants.domain_thumb_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.domain_annexure_remarks)),

                                Utils.getString(mapAttributeValue.get(Constants.structure_type_religious)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_religious_other)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_type_amenities)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_amenities_other)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_structure)),
                                Utils.getString(mapAttributeValue.get(Constants.type_of_diety)),
                                Utils.getString(mapAttributeValue.get(Constants.type_of_diety_other)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_diety)),
                                Utils.getString(mapAttributeValue.get(Constants.category_of_faith)),
                                Utils.getString(mapAttributeValue.get(Constants.category_of_faith_other)),
                                Utils.getString(mapAttributeValue.get(Constants.sub_category_of_faith)),
                                Utils.getString(mapAttributeValue.get(Constants.religion_of_structure_belongs)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_ownership_status)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_trust_or_owner)),
                                Utils.getString(mapAttributeValue.get(Constants.nature_of_structure)),
                                Utils.getString(mapAttributeValue.get(Constants.construction_material)),
                                String.valueOf(Utils.getInteger(mapAttributeValue.get(Constants.daily_visited_people_count))),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_tenement_number)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_tenement_document)),
                                Utils.getString(mapAttributeValue.get(Constants.tenement_document_other)),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.is_structure_registered))),
                                Utils.getString(mapAttributeValue.get(Constants.structure_registered_with)),
                                Utils.getString(mapAttributeValue.get(Constants.other_religious_authority)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_trustee)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_landowner)),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.noc_from_landlord_or_govt))),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.approval_from_govt))),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.yearly_festival_conducted))),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_pavti_num)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_mashal_survey_number)),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.ra_total_no_of_floors_txt))),
                                Utils.getString(mapAttributeValue.get(Constants.panchnama_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.Gen_QC_REMARKS)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Latitude))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Longitude))),
                                Utils.getString(mapAttributeValue.get(Constants.Genesys_Display_Name)),
                                Utils.getString(mapAttributeValue.get(Constants.Primary_IMEI)),
                                Utils.getString(mapAttributeValue.get(Constants.Secondary_IMEI)),
                                Utils.getShort(mapAttributeValue.get(Constants.Is_DRPPL_Available)),
                                Utils.getString(mapAttributeValue.get(Constants.DRPPL_Officer_Name))
                        );
                    } else {
                        unitInfoDataModel = new UnitInfoDataModel(
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_id)),
                                Utils.getString(mapAttributeValue.get(Constants.unit_unique_id)),
                                Utils.getString(mapAttributeValue.get(Constants.globalid)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_relative_path)),
                                "",
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_hut_number)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_floor)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_no)),
                                "",
                                "",
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_nature_of_activity)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_religious_area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Unit_N_Area))),
                                memAvail,
                                Utils.getString(mapAttributeValue.get(Constants.Unit_member_non_available_remarks)),
                                "",
                                "", false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_share_certificate)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_electric_bill)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_photo_pass)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_na_tax)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_property_tax)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_electoral_roll)).equals("1"),
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_school_college_certificate)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_employer_certificate)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_chain_document)).equals("1"),
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rent_agreement)).equals("1"),
                                false,
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_restaurant_hotel_license)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_food_drug_license)).equals("1"),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_factory_act_license)).equals("1"),
                                false,
                                false,
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_unit_status)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_surveyor_name)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_surveyor_desig)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_name)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_name_other)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_desig)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_drp_officer_desig_other)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_remarks_other)),
                                Utils.getInteger(mapAttributeValue.get(Constants.UnitInfo_media_captured_cnt)),
                                Utils.getInteger(mapAttributeValue.get(Constants.UnitInfo_media_upload_cnt)),
                                Utils.getString(mapAttributeValue.get(Constants.objectid)),
                                Utils.getString(mapAttributeValue.get(Constants.globalid)).toUpperCase(),
                                new Date(),
                                new Date(),
                                Utils.getInteger(mapAttributeValue.get(Constants.Unit_visit_count)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_area_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_ward_no)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_sector_no)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_zone_no)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_nagar_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_nagar_name_other)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_society_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_street_name)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_landmark_name)),
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                0.0,
                                loftStr,
                                0.0,
                                "",
                                0.0, false,
                                Utils.getString(mapAttributeValue.get(Constants.Pincode)),
                                "",
                                Utils.getString(mapAttributeValue.get(Constants.VisitDate)),
                                Utils.getShort(mapAttributeValue.get(Constants.FormLock)),
                                "",
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_survey_date)),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_survey_time)),
                                "",
                                false, "",
                                Utils.getString(mapAttributeValue.get(Constants.Unit_pavti_num)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_existince_year_only)),
                                Utils.getString(mapAttributeValue.get(Constants.country_name)),
                                Utils.getString(mapAttributeValue.get(Constants.state_name)),
                                Utils.getString(mapAttributeValue.get(Constants.city_name)),
                                Utils.getString(mapAttributeValue.get(Constants.access_to_unit)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.RcResArea))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.RcCommArea))),
                                Utils.getString(mapAttributeValue.get(Constants.domain_thumb_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.domain_annexure_remarks)),

                                Utils.getString(mapAttributeValue.get(Constants.structure_type_religious)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_religious_other)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_type_amenities)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_amenities_other)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_structure)),
                                Utils.getString(mapAttributeValue.get(Constants.type_of_diety)),
                                Utils.getString(mapAttributeValue.get(Constants.type_of_diety_other)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_diety)),
                                Utils.getString(mapAttributeValue.get(Constants.category_of_faith)),
                                Utils.getString(mapAttributeValue.get(Constants.category_of_faith_other)),
                                Utils.getString(mapAttributeValue.get(Constants.sub_category_of_faith)),
                                Utils.getString(mapAttributeValue.get(Constants.religion_of_structure_belongs)),
                                Utils.getString(mapAttributeValue.get(Constants.structure_ownership_status)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_trust_or_owner)),
                                Utils.getString(mapAttributeValue.get(Constants.nature_of_structure)),
                                Utils.getString(mapAttributeValue.get(Constants.construction_material)),
                                String.valueOf(Utils.getInteger(mapAttributeValue.get(Constants.daily_visited_people_count))),
                                Utils.getString(mapAttributeValue.get(Constants.UnitInfo_tenement_number)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_tenement_document)),
                                Utils.getString(mapAttributeValue.get(Constants.tenement_document_other)),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.is_structure_registered))),
                                Utils.getString(mapAttributeValue.get(Constants.structure_registered_with)),
                                Utils.getString(mapAttributeValue.get(Constants.other_religious_authority)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_trustee)),
                                Utils.getString(mapAttributeValue.get(Constants.name_of_landowner)),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.noc_from_landlord_or_govt))),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.approval_from_govt))),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.yearly_festival_conducted))),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_pavti_num)),
                                Utils.getString(mapAttributeValue.get(Constants.Unit_mashal_survey_number)),
                                String.valueOf(Utils.getShort(mapAttributeValue.get(Constants.ra_total_no_of_floors_txt))),
                                Utils.getString(mapAttributeValue.get(Constants.panchnama_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.Gen_QC_REMARKS)),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Latitude))),
                                Utils.doubleFormatter(Utils.getString(mapAttributeValue.get(Constants.Longitude))),
                                Utils.getString(mapAttributeValue.get(Constants.Genesys_Display_Name)),
                                Utils.getString(mapAttributeValue.get(Constants.Primary_IMEI)),
                                Utils.getString(mapAttributeValue.get(Constants.Secondary_IMEI)),
                                Utils.getShort(mapAttributeValue.get(Constants.Is_DRPPL_Available)),
                                Utils.getString(mapAttributeValue.get(Constants.DRPPL_Officer_Name))
                        );
                    }


//                    if (Utils.getString(mapAttributeValue.get(Constants.UnitInfo_member_available)).equalsIgnoreCase("yes")) {

                    relGlobalidForHoh.add(Utils.getString(mapAttributeValue.get(Constants.globalid)).toUpperCase());
//                    }


                    unitInfoDataModelArrayList.add(unitInfoDataModel);
                }
            }

            App.getInstance().setUniqueId(unitIdList);
            try {
                ArrayList<String> arr1 = App.getInstance().getAlreadyAddedUniqueId();
                if (arr1 != null && arr1.size() > 0) {
                    for (int i = 0; i < alreadyAddedUniqueId.size(); i++) {
                        if (!arr1.contains(alreadyAddedUniqueId.get(i))) {
                            arr1.add(alreadyAddedUniqueId.get(i));
                        }
                    }
                    App.getInstance().setAlreadyAddedUniqueId(arr1);
                } else {
                    App.getInstance().setAlreadyAddedUniqueId(alreadyAddedUniqueId);
                }
            } catch (Exception ex) {
                ex.getMessage();
            }

            try {
                ArrayList<UnitInfoDataModel> unitI = App.getInstance().getUniqueObjectList();
                if (unitI != null && unitI.size() > 0) {

                    ArrayList<UnitInfoDataModel> unitNewList = new ArrayList<>();
                    HashMap<String, UnitInfoDataModel> unitHash = new HashMap<>();
                    for (int i = 0; i < unitInfoDataModelArrayList.size(); i++) {
                        unitHash.put(unitInfoDataModelArrayList.get(i).getUnit_id(), unitInfoDataModelArrayList.get(i));
                    }
                    for (int i = 0; i < unitI.size(); i++) {
                        if (!unitHash.containsKey(unitI.get(i).getUnit_id())) {
                            unitHash.put(unitI.get(i).getUnit_id(), unitI.get(i));
                        } else if (!unitHash.get(unitI.get(i)).getUnit_status().equals(unitI.get(i).getUnit_status())) {
//                            unitHash.remove(unitI.get(i).getUnit_id());
                            unitHash.put(unitI.get(i).getUnit_id(), unitI.get(i));
                        }
                    }
                    unitNewList = new ArrayList<>(unitHash.values());
                    App.getInstance().setUniqueObjectList(unitNewList);
                } else {
                    App.getInstance().setUniqueObjectList(unitInfoDataModelArrayList);
                }
            } catch (Exception ex) {
                ex.getMessage();
            }

            localSurveyDbViewModel.insertAllStructureUnitIdStatusData(structureUnitIdStatusDataTable, activity);

            featureTableUnitPosition = 0;
            unitInfoAddedPositionDataModelArrayList = new ArrayList<>();
            unitMediaInfoDataModel = new HashMap<>();
            hohMediaInfoDataModel = new HashMap<>();
            memberMediaInfoDataModel = new HashMap<>();
            if (unitInfoDataModelArrayList.size() > 0) {
                if (binding.txtProgress.getText().toString().equalsIgnoreCase(Constants.Not_Started)) {
                    binding.txtProgress.setText(Constants.InProgress);
                    binding.imgProgress.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.status_dark_blue));
                }
                Utils.updateProgressMsg("Getting additional details.Please wait..", activity);
                getUnitDetails(featureTableUnitPosition);
            } else {
                Utils.shortToast("No unit details found for this structure.", activity);
                Utils.dismissProgress();
            }

        } catch (Exception e) {
            Utils.dismissProgress();
            AppLog.logData(activity, e.getMessage());
            AppLog.e("retrieveUnitDetailsUnable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.shortToast("retrieveUnitDetails Unable to get the unit details.", activity);
        }

    }

    private void getUnitDetails(int featureTableUnitPosition) {
        try {

            if (unitInfoDataModelArrayList.size() > featureTableUnitPosition) {
                setupUnitDetails(unitInfoDataModelArrayList.get(featureTableUnitPosition));
            } else {
                if (!relGlobalidForHoh.isEmpty()) {
                    memberInfoDataModelArrayList = new ArrayList<>();
                    hohInfoDataModelArrayList = new ArrayList<>();
                    hohIdNameLst = new HashMap<>();

                    if (viewMode.equals(Constants.online))
                        getHohInfoSurveyDetailsOnline();
                    else if (viewMode.equals(Constants.offlineWebmap))
                        getHohInfoSurveyDetailsOffline();
//                getHohInfoSurveyDetailsOffline();

                } else {
                    summaryListAdapter.setSummaryItemModels(summaryItemModels);
                    Utils.dismissProgress();
                }
            }

        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("getUnitDetails Unable to get the unit details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void setupUnitDetails(UnitInfoDataModel unitInfoDataModel) {
        try {
            mediaInfoDataModels = new ArrayList<>();

            summary = new ArrayList<>();
//            summary.add(new SummaryChildItemModel("", "", unitInfoDataModel.getHut_number(), true, false));
//            summary.add(new SummaryChildItemModel("Unit Number", unitInfoDataModel.getUnit_no(), "", false, false));
            summary.add(new SummaryChildItemModel("Unique Survey ID", unitInfoDataModel.getUnit_unique_id(), "", false, false));
            if (unitInfoDataModel.isMember_available()) {
                summary.add(new SummaryChildItemModel("Member Availability", "Yes", "", false, false));
                summary.add(new SummaryChildItemModel("Unit Status", unitInfoDataModel.getUnit_status(), "", false, false));
//                summary.add(new SummaryChildItemModel("Total no. of Members", unitInfoDataModel.getNo_Of_Member(), "", false, false));
                summary.add(new SummaryChildItemModel("Structure Usage", unitInfoDataModel.getUnit_usage(), "", false, false));
                if ((!unitInfoDataModel.getRespondent_age().equals("") && Integer.parseInt(unitInfoDataModel.getRespondent_age()) > 18) && unitInfoDataModel.isMember_available()) {
                    summary.add(new SummaryChildItemModel("Area (Sq.ft)", "" + unitInfoDataModel.getArea_sq_ft(), "", false, false));

                    if (unitInfoDataModel.getExistence_since() != null) {
                        summary.add(new SummaryChildItemModel("Existence Since", formattedDateToYear(unitInfoDataModel.getExistence_since()) + "", "", false, false));
                    } else {
                        summary.add(new SummaryChildItemModel("Existence Since", "", "", false, false));
                    }

                    summary.add(new SummaryChildItemModel("Year of Structure", unitInfoDataModel.getStructure_year(), "", false, false));
                    summary.add(new SummaryChildItemModel("Remark", unitInfoDataModel.getRemarks(), unitInfoDataModel.getGen_qc_remarks(), false, false));
                } else if (!unitInfoDataModel.getRespondent_age().equals("") && Integer.parseInt(unitInfoDataModel.getRespondent_age()) < 18) {
                    summary.add(new SummaryChildItemModel("Remark", "Resident less than 18 years" + "", "", false, false));
                }
//                getUnitAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
//                getMediaInfoDetailsOnline(unitInfoDataModel.getGlobalId(),unitInfoDataModel, summary);

                if (viewMode.equals(Constants.online)) {
                    getMediaInfoDetailsOnline(unitInfoDataModel.getGlobalId(), unitInfoDataModel, summary);
                } else if (viewMode.equals(Constants.offlineWebmap)) {
                    getMediaInfoOfflineMap(unitInfoDataModel.getGlobalId(), unitInfoDataModel, summary);
                } else {
                    setUpSummaryItem(unitInfoDataModel.getUnit_unique_id(), unitInfoDataModel.getUnit_status(),
                            summary, true, null, null, false);
//                    featureTableUnitPosition++;
//                    unitAttachmentHashmap = new HashMap<>();
//                    mediaAttributesArrayList =new ArrayList<>();
//                    getUnitDetails(featureTableUnitPosition);
                }

            } else if (!unitInfoDataModel.isMember_available() && unitInfoDataModel.getRespondent_dob() != null && !unitInfoDataModel.getRespondent_dob().equals("")) {

                summary.add(new SummaryChildItemModel("Member Availability", "No", "", false, false));
                summary.add(new SummaryChildItemModel("Remark", unitInfoDataModel.getRemarks(), unitInfoDataModel.getGen_qc_remarks(), false, false));
                if (viewMode.equals(Constants.online)) {
                    getMediaInfoDetailsOnline(unitInfoDataModel.getGlobalId(), unitInfoDataModel, summary);
                } else if (viewMode.equals(Constants.offlineWebmap)) {
                    getMediaInfoOfflineMap(unitInfoDataModel.getGlobalId(), unitInfoDataModel, summary);
                }
//                else{
//                    setUpSummaryItem(unitInfoDataModel.getUnit_unique_id(), unitInfoDataModel.getUnit_status(),
//                            summary, true, null, null, false);
//                }

            } else {
                summary.add(new SummaryChildItemModel("Member Availability", "No", "", false, false));
                summary.add(new SummaryChildItemModel("Remark", unitInfoDataModel.getRemarks(), unitInfoDataModel.getGen_qc_remarks(), false, false));
                if (unitInfoDataModel.getRemarks().equalsIgnoreCase("Unit is locked and Notice pasted")) {
                    if (viewMode.equals(Constants.online)) {
                        getMediaInfoDetailsOnline(unitInfoDataModel.getGlobalId(), unitInfoDataModel, summary);
                    } else if (viewMode.equals(Constants.offlineWebmap)) {
                        getMediaInfoOfflineMap(unitInfoDataModel.getGlobalId(), unitInfoDataModel, summary);
                    }
                } else {
                    if (viewMode.equals(Constants.online)) {
                        getMediaInfoDetailsOnline(unitInfoDataModel.getGlobalId(), unitInfoDataModel, summary);
                    } else if (viewMode.equals(Constants.offlineWebmap)) {
                        getMediaInfoOfflineMap(unitInfoDataModel.getGlobalId(), unitInfoDataModel, summary);
                    }
//                    setUpSummaryItem(unitInfoDataModel.getUnit_unique_id(), unitInfoDataModel.getUnit_status(),
//                            summary, true, null, null, false);
                }
            }
        } catch (Exception e) {
            featureTableUnitPosition++;
            getUnitDetails(featureTableUnitPosition);
            AppLog.logData(activity, e.getMessage());
            AppLog.e("Unable to get the attachment details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
        }
    }

    private void getUnitAttachmentOffline(String objectId, UnitInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {

        try {
            ArrayList<MediaInfoDataModel> arrayList = new ArrayList<>();

//        if (unitAttachmentHashmap.containsKey(objectId)) {
            for (int i = 0; i < mediaModelArrayList.size(); i++) {
                mediaInfoDataModels = new ArrayList<>();
                ArrayList<MediaDetailsDataModel> listMediaDetails = mapMediaDetailsModelUnit.get(mediaModelArrayList.get(i).getGlobalId());
                if (listMediaDetails != null) {
                    if (!listMediaDetails.isEmpty()) {
                        for (MediaDetailsDataModel attachmentInfo : listMediaDetails) {
                            String weburl = Constants.WebFilePath + attachmentInfo.getFile_path() + attachmentInfo.getFile_name() + attachmentInfo.getFile_ext();
                            mediaInfoDataModels.add(new MediaInfoDataModel(attachmentInfo.getFile_name() + attachmentInfo.getFile_ext(), weburl));

                            arrayList.add(new MediaInfoDataModel(attachmentInfo.getContent_type(),
                                    attachmentInfo.getFile_name(), (int) attachmentInfo.getData_size(), "",
                                    weburl,
                                    (short) 0, (short) 0,
                                    Constants.unit_infoLayer, unitInfoDataModel.getUnit_id(),
                                    unitInfoDataModel.getRel_globalid().toUpperCase(),
                                    unitInfoDataModel.getRelative_path(),
                                    mediaModelArrayList.get(i).getObejctId(),
                                    // unitInfoDataModel.getGlobalId().toUpperCase(),
                                    mediaModelArrayList.get(i).getGlobalId().toUpperCase(),
                                    true, new Date(), new Date(), mediaModelArrayList.get(i).getDocument_category(),
                                    mediaModelArrayList.get(i).getDocument_type(), mediaModelArrayList.get(i).getDocument_remarks(), mediaModelArrayList.get(i).getName_on_document()));
                        }

                        if (!mediaInfoDataModels.isEmpty()) {
                            summary.add(new SummaryChildItemModel("Attachments", Utils.getGson().toJson(mediaInfoDataModels), "", false, true));
                        }

                        if (!arrayList.isEmpty()) {
                            unitMediaInfoDataModel.put(unitInfoDataModel.getGlobalId().toUpperCase(), arrayList);
                        }
                    } else if (mediaModelArrayList.get(i).getItem_url() != null && !mediaModelArrayList.get(i).getItem_url().equals("")) {

                        // TODO: Ajay :: Need to Confirm with Rohit Bhai
                        String weburl = Constants.Video_STAGE_URL + mediaModelArrayList.get(i).getItem_url() + "/" + mediaModelArrayList.get(i).getFilename();
                        mediaInfoDataModels.add(new MediaInfoDataModel(mediaModelArrayList.get(i).getFilename(), weburl));

                        arrayList.add(new MediaInfoDataModel(mediaModelArrayList.get(i).getContent_type(),
                                mediaModelArrayList.get(i).getFilename(), (int) mediaModelArrayList.get(i).getData_size(), "",
                                weburl,
                                (short) 0, (short) 0,
                                Constants.unit_infoLayer, unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid().toUpperCase(),
                                unitInfoDataModel.getRelative_path(),
                                mediaModelArrayList.get(i).getObejctId(),
                                // unitInfoDataModel.getGlobalId().toUpperCase(),
                                mediaModelArrayList.get(i).getGlobalId().toUpperCase(),
                                true, new Date(), new Date(), mediaModelArrayList.get(i).getDocument_category(),
                                mediaModelArrayList.get(i).getDocument_type(), mediaModelArrayList.get(i).getDocument_remarks(), mediaModelArrayList.get(i).getName_on_document()));

                        if (mediaInfoDataModels.size() > 0) {
                            summary.add(new SummaryChildItemModel("Attachments", Utils.getGson().toJson(mediaInfoDataModels), "", false, true));
                        }

                        if (arrayList.size() > 0) {
                            unitMediaInfoDataModel.put(unitInfoDataModel.getGlobalId().toUpperCase(), arrayList);
                        }
                    }
                } else {// vidnyan if no attachemnts found then need to add that id to unit summary hashmap

                }
            }

//        }
            //offline
            unitSummaryHashmap.put(unitInfoDataModel.getGlobalId().toUpperCase(), new UnitDetailsSummary(summary, unitInfoDataModel));

//        if(!unitInfoDataModel.isMember_available() && unitInfoDataModel.getRespondent_dob()!=null && !unitInfoDataModel.getRespondent_dob().equals("")){
//                    setUpSummaryItem(unitInfoDataModel.getUnit_no(), unitInfoDataModel.getUnit_status(),
//                summary, true, null, null, false);
//        }
//        setUpSummaryItem(unitInfoDataModel.getUnit_no(), unitInfoDataModel.getUnit_status(),
//                summary, true, null, null, false);

//        setUpSummaryItem(unitInfoDataModel.getUnit_no(), unitInfoDataModel.getUnit_status(),
//                summary, true, null, null, false);

            featureTableUnitPosition++;
            unitAttachmentHashmap = new HashMap<>();
            mediaAttributesArrayList = new ArrayList<>();
            getUnitDetails(featureTableUnitPosition);
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("getUnitAttachmentOffline unable  to get the hoh details.\n Error cause: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private void getHohInfoSurveyDetailsOffline() {
        try {
            String rel_globalid = "";

            for (String s : relGlobalidForHoh) {
                if (!Utils.isNullOrEmpty(rel_globalid))
                    rel_globalid = rel_globalid + " OR ";

                rel_globalid = rel_globalid + "rel_globalid = '{" + s.toUpperCase() + "}'";
            }

            GeodatabaseFeatureTable unitFT = App.getInstance().getHohGFT();
            if (unitFT.getLoadStatus() == LoadStatus.LOADED) {
                QueryParameters query = new QueryParameters();
                query.setWhereClause(rel_globalid);
                query.getOrderByFields().add(new QueryParameters.OrderBy("hoh_name", QueryParameters.SortOrder.ASCENDING));

                final ListenableFuture<FeatureQueryResult> future = unitFT.queryFeaturesAsync(query);
                future.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = future.get();
                        if (result.iterator().hasNext()) {
                            for (final Feature feature : result) {
                                Map<String, Object> mapAttributeValue = feature.getAttributes();
                                ArcGISFeature arcGISFeature = (ArcGISFeature) feature;

                                List<Attachment> attachmentsAsync;
                                try {
                                    attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                                } catch (Exception e) {
                                    attachmentsAsync = new ArrayList<>();
                                    AppLog.logData(activity, e.getMessage());
                                }
                                hohAttachmentHashmap.put("" + (int) Double.parseDouble(Utils.getString(mapAttributeValue.get(Constants.objectid))), attachmentsAsync);
                                hohMapAttributesArrayList.add(mapAttributeValue);
                            }
                            retrieveHohDetails(hohMapAttributesArrayList);
                        } else {

                            Utils.shortToast("No Hoh details record found.", activity);
                            setUpUnitSummaryItem();
                        }
                    } catch (Exception e) {
                        AppLog.logData(activity, e.getMessage());
                        Utils.shortToast("No hoh details record found.", activity);
                        setUpUnitSummaryItem();
                    }
                });
            } else if (unitFT.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                Utils.dismissProgress();
                AppLog.e("Unable to get the hoh details.\n Error cause: " + unitFT.getLoadError().getCause() + "\nError message: " + unitFT.getLoadError().getMessage());
                Utils.shortToast("Unable to get the hoh details.", activity);
                setUpUnitSummaryItem();
            }

        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private void getHohInfoSurveyDetailsOnline() {

        String rel_globalid = "";

        for (String s : relGlobalidForHoh) {
            if (!Utils.isNullOrEmpty(rel_globalid))
                rel_globalid = rel_globalid + " OR ";

            rel_globalid = rel_globalid + "rel_globalid = '{" + s.toUpperCase() + "}'";
        }

        ServiceFeatureTable unitFT = App.getInstance().getHohFT();
        if (unitFT.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();
            query.setWhereClause(rel_globalid);
            query.getOrderByFields().add(new QueryParameters.OrderBy("hoh_name", QueryParameters.SortOrder.ASCENDING));

            final ListenableFuture<FeatureQueryResult> future = unitFT.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        for (final Feature feature : result) {
                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            ArcGISFeature arcGISFeature = (ArcGISFeature) feature;

                            List<Attachment> attachmentsAsync;
                            try {
                                attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                            } catch (Exception e) {
                                attachmentsAsync = new ArrayList<>();
                                AppLog.logData(activity, e.getMessage());
                            }
                            hohAttachmentHashmap.put("" + (int) Double.parseDouble(Utils.getString(mapAttributeValue.get(Constants.objectid))), attachmentsAsync);
                            hohMapAttributesArrayList.add(mapAttributeValue);
                        }
                        retrieveHohDetails(hohMapAttributesArrayList);
                    } else {
                        Utils.shortToast("No HoH details record found.", activity);
                        setUpUnitSummaryItem();
                    }
                } catch (Exception e) {
                    AppLog.logData(activity, e.getMessage());
                    AppLog.e("JJJJJJJJ-Error No HoH= " + e.getMessage());
                    Utils.shortToast("No hoh details record found.", activity);
                    setUpUnitSummaryItem();
                }
            });
        } else if (unitFT.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the hoh details.\n Error cause: " + unitFT.getLoadError().getCause() + "\nError message: " + unitFT.getLoadError().getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
            setUpUnitSummaryItem();
        }
    }

    private void retrieveHohDetails(ArrayList<Map<String, Object>> hohMapAttributesArrayList) {
        try {
            hohListForUnitGlobalIdHashmap = new HashMap<>();
            ArrayList<HohInfoDataModel> hohInfoDataModels;

            for (Map<String, Object> mapAttributeValue : hohMapAttributesArrayList) {
                if (mapAttributeValue != null) {
                    hohInfoDataModels = new ArrayList<>();
//                    String genderFromBackend = Utils.getString(mapAttributeValue.get(Constants.hohInfo_gender));
                    String genderFromBackend = Utils.getString(mapAttributeValue.get(Constants.hoh_gender));
//                    String gender = genderFromBackend.equalsIgnoreCase("Trans") ? "Transgender" : genderFromBackend;
                    String gender = genderFromBackend.equalsIgnoreCase("Trans") ? "Transgender" : genderFromBackend;
                    String unitId = "";
                    if (!Utils.isNullOrEmpty(Utils.getString(mapAttributeValue.get(Constants.hohInfo_relative_path)))) {
                        String[] strings = Utils.getString(mapAttributeValue.get(Constants.hohInfo_relative_path)).split("/");
                        if (strings.length > 0)
                            unitId = strings[2];
                    }
                    HohInfoDataModel hohInfoDataModel = new HohInfoDataModel(
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_hoh_id)),
                            unitId,
                            Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)).toUpperCase(),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_relative_path)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_hoh_name)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_marital_status)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_marital_status_other)),
                            Utils.getInteger(mapAttributeValue.get(Constants.hoh_spouse_count)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_spouse_name)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_contact_number)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_dob)),
                            Utils.getInteger(mapAttributeValue.get(Constants.hoh_age)),
                            gender,
                            Utils.getInteger(mapAttributeValue.get(Constants.hohInfo_staying_since_year)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_aadhar_no)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_pan_no)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_ration_card_colour)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_ration_card_no)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_religion)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_religion_other)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_from_state)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_from_state_other)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_mother_tongue)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_mother_tongue_other)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_education)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_education_other)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_occupation)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_occupation_other)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_place_of_work)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_type_of_work)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_type_of_work_other)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_monthly_income)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_mode_of_transport)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_mode_of_transport_other)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_school_college_name_location)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_handicap)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_staying_with)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_vehicle_owned_driven_type)),
                            Utils.getString(mapAttributeValue.get(Constants.hohInfo_vehicle_owned_driven_other)),
                            Utils.getInteger(mapAttributeValue.get(Constants.hohInfo_member_count)),
                            Utils.getString(mapAttributeValue.get(Constants.objectid)),
                            Utils.getString(mapAttributeValue.get(Constants.globalid)).toUpperCase(),
                            Utils.getInteger(mapAttributeValue.get(Constants.death_certificate)),
                            true,
                            new Date(),
                            new Date(),
                            Utils.getString(mapAttributeValue.get(Constants.adhaar_verify_status)),
                            Utils.getString(mapAttributeValue.get(Constants.adhaar_verify_remark)),
                            Utils.getString(mapAttributeValue.get(Constants.adhaar_verify_date)));

                    relGlobalidForMember.add(Utils.getString(mapAttributeValue.get(Constants.globalid)).toUpperCase());

                    hohInfoDataModelArrayList.add(hohInfoDataModel);

                    if (hohListForUnitGlobalIdHashmap.containsKey(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)).toUpperCase()))
                        hohInfoDataModels.addAll(hohListForUnitGlobalIdHashmap.get(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)).toUpperCase()));

                    hohInfoDataModels.add(hohInfoDataModel);
                    hohListForUnitGlobalIdHashmap.put(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)).toUpperCase(), hohInfoDataModels);

                    hohIdNameLst.put(Utils.getString(mapAttributeValue.get(Constants.hohInfo_hoh_id)), Utils.getString(mapAttributeValue.get(Constants.hohInfo_hoh_name)));

                }
            }
            try {
                HashMap<String, ArrayList<HohInfoDataModel>> unitI = App.getInstance().getHohList();
                if (unitI != null && unitI.size() > 0) {
                    unitI.putAll(hohListForUnitGlobalIdHashmap);
                    App.getInstance().setHohList(unitI);
                } else {
                    App.getInstance().setHohList(hohListForUnitGlobalIdHashmap);
                }
            } catch (Exception ex) {
                ex.getMessage();
            }
            featureTableHohPosition = 0;
            if (hohInfoDataModelArrayList.size() > 0) {
                //Utils.updateProgressMsg("Getting hoh additional details.Please wait..", activity);
                getHohDetails(featureTableHohPosition);
            } else {
                setUpUnitSummaryItem();
                Utils.dismissProgress();
            }

        } catch (Exception e) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
        Utils.dismissProgress();
    }


    private void getHohDetails(int position) {
        try {
            if (hohInfoDataModelArrayList.size() > position) {
                if (viewMode.equals(Constants.online)) {
                    HohInfoDataModel hohInfoDataModel = hohInfoDataModelArrayList.get(position);
//            getHohAttachment("" + (int) Double.parseDouble(hohInfoDataModel.getObejctId()), hohInfoDataModel);
                    getMediaInfoHoh(hohInfoDataModel.getGlobalId(), hohInfoDataModel, summary);
                } else if (viewMode.equals(Constants.offlineWebmap)) {
                    HohInfoDataModel hohInfoDataModel = hohInfoDataModelArrayList.get(position);
                    getMediaInfoHohOffline(hohInfoDataModel.getGlobalId(), hohInfoDataModel, summary);
                }

            } else {
                if (relGlobalidForMember.size() > 0) {
                    if (viewMode.equals(Constants.online))
                        getMemberInfoSurveyDetailsOnline();
                    else if (viewMode.equals(Constants.offlineWebmap))
                        getMemberInfoSurveyDetailsOffline();
                } else {
                    setUpUnitSummaryItem();
                }
            }
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private void getMemberInfoSurveyDetailsOnline() {
        try {
            String rel_globalid = "";

            for (String s : relGlobalidForMember) {
                if (!Utils.isNullOrEmpty(rel_globalid))
                    rel_globalid = rel_globalid + " OR ";

                rel_globalid = rel_globalid + "rel_globalid = '{" + s.toUpperCase() + "}'";
            }

            ServiceFeatureTable unitFT = App.getInstance().getMemberFT();
            if (unitFT.getLoadStatus() == LoadStatus.LOADED) {
                QueryParameters query = new QueryParameters();
                query.setWhereClause(rel_globalid);
                query.getOrderByFields().add(new QueryParameters.OrderBy("member_name", QueryParameters.SortOrder.ASCENDING));

                final ListenableFuture<FeatureQueryResult> future = unitFT.queryFeaturesAsync(query);
                future.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = future.get();
                        if (result.iterator().hasNext()) {
                            for (final Feature feature : result) {
                                Map<String, Object> mapAttributeValue = feature.getAttributes();
                                ArcGISFeature arcGISFeature = (ArcGISFeature) feature;

                                List<Attachment> attachmentsAsync;
                                try {
                                    attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                                } catch (Exception e) {
                                    AppLog.logData(activity, e.getMessage());
                                    attachmentsAsync = new ArrayList<>();
                                }
                                memberAttachmentHashmap.put("" + (int) Double.parseDouble(Utils.getString(mapAttributeValue.get(Constants.objectid))), attachmentsAsync);
                                memberMapAttributesArrayList.add(mapAttributeValue);
                            }
                            retrieveMemberDetails(memberMapAttributesArrayList);
                        } else {
                            Utils.shortToast("No member details record found.", activity);
                            setUpUnitSummaryItem();
                        }
                    } catch (Exception e) {
                        AppLog.logData(activity, e.getMessage());
                        Utils.shortToast("No member details record found.", activity);
                        setUpUnitSummaryItem();
                    }
                });
            } else if (unitFT.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                Utils.dismissProgress();
                AppLog.e("Unable to get the member details.\n Error cause: " + unitFT.getLoadError().getCause() + "\nError message: " + unitFT.getLoadError().getMessage());
                Utils.shortToast("Unable to get the member details.", activity);
                setUpUnitSummaryItem();
            }
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private void getMemberInfoSurveyDetailsOffline() {
        try {
            String rel_globalid = "";

            for (String s : relGlobalidForMember) {
                if (!Utils.isNullOrEmpty(rel_globalid))
                    rel_globalid = rel_globalid + " OR ";

                rel_globalid = rel_globalid + "rel_globalid = '{" + s.toUpperCase() + "}'";
            }

            GeodatabaseFeatureTable unitFT = App.getInstance().getMemberGFT();
            if (unitFT.getLoadStatus() == LoadStatus.LOADED) {
                QueryParameters query = new QueryParameters();
                query.setWhereClause(rel_globalid);
                query.getOrderByFields().add(new QueryParameters.OrderBy("member_name", QueryParameters.SortOrder.ASCENDING));

                final ListenableFuture<FeatureQueryResult> future = unitFT.queryFeaturesAsync(query);
                future.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = future.get();
                        if (result.iterator().hasNext()) {
                            for (final Feature feature : result) {
                                Map<String, Object> mapAttributeValue = feature.getAttributes();
                                ArcGISFeature arcGISFeature = (ArcGISFeature) feature;

                                List<Attachment> attachmentsAsync;
                                try {
                                    attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                                } catch (Exception e) {
                                    AppLog.logData(activity, e.getMessage());
                                    attachmentsAsync = new ArrayList<>();
                                }
                                memberAttachmentHashmap.put("" + (int) Double.parseDouble(Utils.getString(mapAttributeValue.get(Constants.objectid))), attachmentsAsync);
                                memberMapAttributesArrayList.add(mapAttributeValue);
                            }
                            retrieveMemberDetails(memberMapAttributesArrayList);
                        } else {
                            Utils.shortToast("No member details record found.", activity);
                            setUpUnitSummaryItem();
                        }
                    } catch (Exception e) {
                        AppLog.logData(activity, e.getMessage());
                        Utils.shortToast("No member details record found.", activity);
                        setUpUnitSummaryItem();
                    }
                });
            } else if (unitFT.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                Utils.dismissProgress();
                AppLog.e("Unable to get the member details.\n Error cause: " + unitFT.getLoadError().getCause() + "\nError message: " + unitFT.getLoadError().getMessage());
                Utils.shortToast("Unable to get the member details.", activity);
                setUpUnitSummaryItem();
            }
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private void retrieveMemberDetails(ArrayList<Map<String, Object>> memberMapAttributesArrayList) {
        try {
            memberListForUnitGlobalIdHashmap = new HashMap<>();
            ArrayList<MemberInfoDataModel> memberInfoDataModels;

            for (Map<String, Object> mapAttributeValue : memberMapAttributesArrayList) {
                if (mapAttributeValue != null) {
                    memberInfoDataModels = new ArrayList<>();
//                    String genderFromBackend = Utils.getString(mapAttributeValue.get(Constants.memberInfo_gender));
                    String genderFromBackend = Utils.getString(mapAttributeValue.get(Constants.member_gender));
//                    String gender = genderFromBackend.equalsIgnoreCase("Trans") ? "Transgender" : genderFromBackend;
                    String gender = genderFromBackend.equalsIgnoreCase("Trans") ? "Transgender" : genderFromBackend;
                    String hohId = "";
                    if (!Utils.isNullOrEmpty(Utils.getString(mapAttributeValue.get(Constants.memberInfo_relative_path)))) {
                        String[] strings = Utils.getString(mapAttributeValue.get(Constants.memberInfo_relative_path)).split("/");
                        if (strings.length > 0)
                            hohId = strings[3];
                    }

                    String hohName = "";
                    if (hohIdNameLst != null && hohIdNameLst.containsKey(hohId)) {
                        hohName = hohIdNameLst.get(hohId);
                    }//mapAttributeValue.get(Constants.memberInfo_dob) ((Calendar)mapAttributeValue.get(Constants.memberInfo_dob)).get(Calendar.)

                    Calendar dobCalender = (Calendar) mapAttributeValue.get(Constants.memberInfo_dob);
                    String dobToStore = "";
                    if (dobCalender != null) {
                        int year = dobCalender.get(Calendar.YEAR);
                        int month = dobCalender.get(Calendar.MONTH) + 1; // Month starts from 0, add 1
                        int day = dobCalender.get(Calendar.DAY_OF_MONTH);
                        dobToStore = day + "/" + month + "/" + year;
                    }

                    MemberInfoDataModel memberInfoDataModel = new MemberInfoDataModel(
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_member_id)),
                            hohId,
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_rel_globalid)).toUpperCase(),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_relative_path)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_member_name)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_relationship)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_relationship_other)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_marital_status)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_marital_status_other)),
                            //Utils.getInteger(mapAttributeValue.get(Constants.memberInfo_spouse_count)),
                            Utils.getInteger(mapAttributeValue.get(Constants.member_spouse_count)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_spouse_name)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_contact_number)),
//                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_dob)),
                            dobToStore,
                            Utils.getInteger(mapAttributeValue.get(Constants.member_age)),
                            gender,
                            Utils.getInteger(mapAttributeValue.get(Constants.memberInfo_staying_since_year)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_aadhar_no)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_pan_no)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_ration_card_colour)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_ration_card_no)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_religion)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_religion_other)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_from_state)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_from_state_other)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_mother_tongue)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_mother_tongue_other)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_education)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_education_other)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_occupation)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_occupation_other)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_place_of_work)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_type_of_work)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_type_of_work_other)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_monthly_income)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_mode_of_transport)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_mode_of_transport_other)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_school_college_name_location)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_handicap)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_staying_with)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_vehicle_owned_driven_type)),
                            Utils.getString(mapAttributeValue.get(Constants.memberInfo_vehicle_owned_driven_other)),
                            Utils.getString(mapAttributeValue.get(Constants.objectid)),
                            Utils.getString(mapAttributeValue.get(Constants.globalid)).toUpperCase(),
                            true,
                            new Date(),
                            new Date(), Utils.getInteger(mapAttributeValue.get(Constants.UnitInfo_share_certificate) == null ? 2 : mapAttributeValue.get(Constants.UnitInfo_share_certificate)));

                    memberInfoDataModelArrayList.add(memberInfoDataModel);

                    if (memberListForUnitGlobalIdHashmap.containsKey(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)).toUpperCase()))
                        memberInfoDataModels.addAll(memberListForUnitGlobalIdHashmap.get(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)).toUpperCase()));

                    memberInfoDataModels.add(memberInfoDataModel);
                    memberListForUnitGlobalIdHashmap.put(Utils.getString(mapAttributeValue.get(Constants.UnitInfo_rel_globalid)).toUpperCase(), memberInfoDataModels);
                }
            }

            featureTableMemberPosition = 0;
            if (memberInfoDataModelArrayList.size() > 0) {
                Utils.updateProgressMsg("Getting member additional details.Please wait..", activity);
                getMemberDetails(featureTableMemberPosition);
            } else {
                setUpUnitSummaryItem();
                Utils.dismissProgress();
            }

        } catch (Exception e) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getMemberDetails(int featureTableMemberPosition) {
        try {
            if (viewMode.equals(Constants.online)) {
                if (memberInfoDataModelArrayList.size() > featureTableMemberPosition) {
                    MemberInfoDataModel memberInfoDataModel = memberInfoDataModelArrayList.get(featureTableMemberPosition);
                    // getMemberAttachment("" + (int) Double.parseDouble(memberInfoDataModel.getObejctId()), memberInfoDataModel);
                    getMediaInfoMember(memberInfoDataModel.getGlobalId(), memberInfoDataModel, summary);
                } else {
                    setUpUnitSummaryItem();
                }
            } else if (viewMode.equals(Constants.offlineWebmap)) {
                if (memberInfoDataModelArrayList.size() > featureTableMemberPosition) {
                    MemberInfoDataModel memberInfoDataModel = memberInfoDataModelArrayList.get(featureTableMemberPosition);
                    getMediaInfoMemberOffline(memberInfoDataModel.getGlobalId(), memberInfoDataModel, summary);
                } else {
                    setUpUnitSummaryItem();
                }
            }
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private void setUpSummaryItem(String header, String unitStatus, ArrayList<SummaryChildItemModel> summaryChildItemModels, boolean isUnitDetails,
                                  ArrayList<HohInfoDataModel> hohInfoDataModelArrayList, HashMap<String, ArrayList<MemberInfoDataModel>> memberInfoList,
                                  boolean isMemberDetails) {
        try {
            summaryItemModels.add(new SummaryItemModel(header, unitStatus,
                    summaryChildItemModels, isUnitDetails, hohInfoDataModelArrayList, memberInfoList, isMemberDetails));
            if (header.equalsIgnoreCase("DRP/S0/HE/Z00/224"))
                Toast.makeText(activity, "" + hohInfoDataModelArrayList.size(), Toast.LENGTH_SHORT).show();
            unitInfoAddedPositionDataModelArrayList.add(unitInfoDataModelArrayList.get(featureTableUnitPosition));

            featureTableUnitPosition++;
            getUnitDetails(featureTableUnitPosition);
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }

    }

    private void setUpUnitSummaryItem() {
        try {

            HashMap<String, ArrayList<MemberInfoDataModel>> memberInfoList = new HashMap<>();

            for (String s : relGlobalidForHoh) {
                memberInfoList = new HashMap<>();
                if (unitSummaryHashmap != null && unitSummaryHashmap.containsKey(s)) {
                    if (unitSummaryHashmap.get(s) != null)
                        if (unitSummaryHashmap.get(s).getUnitInfoDataModel() != null) {
                            UnitInfoDataModel unitInfoDataModel = unitSummaryHashmap.get(s).getUnitInfoDataModel();

                            unitInfoAddedPositionDataModelArrayList.add(unitInfoDataModel);

                            ArrayList<HohInfoDataModel> hohInfoDataModelArrayList;
                            if (hohListForUnitGlobalIdHashmap != null && hohListForUnitGlobalIdHashmap.containsKey(s)) {
                                hohInfoDataModelArrayList = hohListForUnitGlobalIdHashmap.get(s);
                                if (hohInfoDataModelArrayList != null) {
                                    for (HohInfoDataModel hohInfoDataModel : hohInfoDataModelArrayList) {
                                        ArrayList<MemberInfoDataModel> memberInfoDataModels = new ArrayList<>();
                                        if (memberListForUnitGlobalIdHashmap != null && memberListForUnitGlobalIdHashmap.containsKey(hohInfoDataModel.getGlobalId().toUpperCase())) {
                                            memberInfoDataModels.addAll(memberListForUnitGlobalIdHashmap.get(hohInfoDataModel.getGlobalId().toUpperCase()));
                                            memberInfoList.put(hohInfoDataModel.getHoh_id(), memberInfoDataModels);
                                        }
                                    }
                                } else {
                                    hohInfoDataModelArrayList = null;
                                    memberInfoList = null;
                                }
                            } else {
                                hohInfoDataModelArrayList = null;
                                memberInfoList = null;
                            }

                            summaryItemModels.add(new SummaryItemModel(unitInfoDataModel.getUnit_unique_id(), unitInfoDataModel.getUnit_status(),
                                    unitSummaryHashmap.get(s).getUnitSummaryHashmap(), true,
                                    hohInfoDataModelArrayList, memberInfoList, hohInfoDataModelArrayList != null));
                        }
                }/*else{ // vidnyan code if no attachement

                int positionOfS = unitInfoAddedPositionDataModelArrayList.indexOf(s);
                summaryItemModels.add(new SummaryItemModel(unitInfoAddedPositionDataModelArrayList.get(positionOfS).getUnit_unique_id(), unitInfoAddedPositionDataModelArrayList.get(positionOfS).getUnit_status(),
                        unitSummaryHashmap.get(s).getUnitSummaryHashmap(), true,
                        hohInfoDataModelArrayList, memberInfoList, hohInfoDataModelArrayList != null));
            }*/
            }

            summaryListAdapter.setSummaryItemModels(summaryItemModels);
            Utils.dismissProgress();
        } catch (Exception ex) {
            AppLog.logData(activity, ex.getMessage());
            Utils.dismissProgress();
        }
    }

    private void setupStatus(String structStatus) {
        try {
            String status = "N/A";
            int imgInt = R.color.main_color;

            if (structStatus.equals(Constants.InProgress_statusLayer)) {
                status = Constants.In_Progress;
//            imgInt = R.color.inProgressBoarderColor;
                imgInt = R.color.status_dark_blue;
            } else if (structStatus.equals(Constants.OnHold_statusLayer)) {
                status = Constants.On_Hold;
                imgInt = R.color.onHoldBoarderColor;
            } else if (structStatus.equals(Constants.NotStarted_statusLayer)) {
                status = Constants.Not_Started;
                imgInt = R.color.notStartedBoarderColor;
            } else if (structStatus.equals(Constants.completed_statusLayer)) {
                status = Constants.completed;
                imgInt = R.color.completeBoarderColor;
            } else if (structStatus.equals(Constants.completed_dispute)) {
                status = Constants.dispute;
                imgInt = R.color.lighter_red;
            }
            AppLog.e("Status::" + status);
            binding.txtProgress.setText(status);
            binding.imgProgress.setBackgroundTintList(ContextCompat.getColorStateList(activity, imgInt));
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private void setUpOfflineStructureDetails(String uniqueId) {
        try {
            unitInfoDataModelArrayList = new ArrayList<>();
            unitInfoAddedPositionDataModelArrayList = new ArrayList<>();
            summaryItemModels = new ArrayList<>();

            summaryListAdapter = new SummaryListAdapter(activity, null, Constants.MemberPhotograph, this);
            binding.rvSummary.setAdapter(summaryListAdapter);

            localSurveyDbViewModel.getStructureInfoPointDataWithId(uniqueId).observe(getActivity(), structureInfoPointDataModelList -> {
                // Update the cached copy of the words in the adapter.

                if (structureInfoPointDataModelList != null && !structureInfoPointDataModelList.isEmpty()) {

                    ArrayList<SummaryChildItemModel> summaryChild = new ArrayList<>();
                    summaryChild.add(new SummaryChildItemModel("Hut ID", structureInfoPointDataModelList.get(0).getHut_number(), "", false, false));
                    // summaryChild.add(new SummaryChildItemModel("Survey Unique ID No.", structureInfoPointDataModelList.get(0).getSurveyUniqueIdNumber(), "", false, false));
                    // summaryChild.add(new SummaryChildItemModel("Tenement No.", structureInfoPointDataModelList.get(0).getTenement_number(), "", false, false));
                    // summaryChild.add(new SummaryChildItemModel("Name of Nagar/Area", structureInfoPointDataModelList.get(0).getArea_name(), "", false, false));
                    summaryChild.add(new SummaryChildItemModel("Work Area", structureInfoPointDataModelList.get(0).getCluster_name(), "", false, false));
                    // summaryChild.add(new SummaryChildItemModel("Grid Number", structureInfoPointDataModelList.get(0).getGrid_number(), "", false, false));
                    // summaryChild.add(new SummaryChildItemModel("Address", structureInfoPointDataModelList.get(0).getAddress(), "", false, false));
                    summaryChild.add(new SummaryChildItemModel("No. of Floor", structureInfoPointDataModelList.get(0).getNo_of_floors() + "", "", false, false));

                    structureInfoPointDataMode = structureInfoPointDataModelList.get(0);
                    binding.txtTenantNumber.setText(structureInfoPointDataModelList.get(0).getHut_number());
                    // binding.cvEdit.setOnClickListener(view -> {
                    //     activity.startActivity(new Intent(activity, StructureActivity.class)
                    //             .putExtra(Constants.IS_EDITING, true)
                    //             .putExtra(Constants.EDIT_TYPE, Constants.EDIT_StructureInfo)
                    //             .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataModelList.get(0)));
                    //     finish();
                    // });
                    setupStatus(structureInfoPointDataModelList.get(0).getStructure_status());

                    summaryItemModels.add(new SummaryItemModel("Structure Details", "", summaryChild, false, null, null, false));

                    localSurveyDbViewModel.getUnitInfoPointData(structureInfoPointDataModelList.get(0).getStructure_id()).observe(getActivity(), unitInfoDataModelList -> {

                        for (UnitInfoDataModel unitInfoDataModel : unitInfoDataModelList) {
                            summary = new ArrayList<>();
                            unitInfoDataModelArrayList.add(unitInfoDataModel);
                            ArrayList<HohInfoDataModel> hohInfoDataModelArrayList = new ArrayList<>();
                            HashMap<String, ArrayList<MemberInfoDataModel>> memberInfoList = new HashMap<>();

                            summary.add(new SummaryChildItemModel("", "", unitInfoDataModel.getHut_number(), true, false));
//                        summary.add(new SummaryChildItemModel("Unit Number", unitInfoDataModel.getUnit_unique_id(), "", false, false));
                            summary.add(new SummaryChildItemModel("Unique Survey ID", unitInfoDataModel.getUnit_unique_id(), "", false, false));
                            if (unitInfoDataModel.isMember_available()) {

                                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoData(unitInfoDataModel.getRelative_path());

                                hohInfoDataModelArrayList.addAll(localSurveyDbViewModel.getHohInfoData(unitInfoDataModel.getUnit_id()));

                                summary.add(new SummaryChildItemModel("Member Availability", "Yes", "", false, false));
                                summary.add(new SummaryChildItemModel("Unit Status", unitInfoDataModel.getUnit_status(), "", false, false));
//                            summary.add(new SummaryChildItemModel("Total no. of Members", unitInfoDataModel.getNo_Of_Member(), "", false, false));
                                summary.add(new SummaryChildItemModel("Structure Usage", unitInfoDataModel.getUnit_usage(), "", false, false));
                                if ((Integer.parseInt(unitInfoDataModel.getRespondent_age()) > 18 && unitInfoDataModel.isMember_available())) {
                                    summary.add(new SummaryChildItemModel("Area (Sq.ft)", String.format("%.3f", unitInfoDataModel.getArea_sq_ft()), "", false, false));
                                    summary.add(new SummaryChildItemModel("Existence Since", unitInfoDataModel.getStructure_since() + "", "", false, false));
                                    if (unitInfoDataModel.getExistence_since() != null) {
                                        summary.add(new SummaryChildItemModel("Year of Structure", formattedDateToYear(unitInfoDataModel.getExistence_since()), "", false, false));
                                    } else {
                                        summary.add(new SummaryChildItemModel("Year of Structure", "", "", false, false));
                                    }

                                    summary.add(new SummaryChildItemModel("Remark", unitInfoDataModel.getRemarks(), unitInfoDataModel.getGen_qc_remarks(), false, false));
                                } else if (Integer.parseInt(unitInfoDataModel.getRespondent_age()) < 18) {
                                    summary.add(new SummaryChildItemModel("Remark", "Resident less than 18 years" + "", "", false, false));
                                }
                                if (!unitInfoDataModel.isMember_available()) {
                                    summary.add(new SummaryChildItemModel("Remark", unitInfoDataModel.getRespondent_non_available_remark() + "", "", false, false));
                                }
                                if (getMediaInfoData.size() > 0) {

                                    ArrayList<MediaInfoDataModel> mediaInfoDataModels = new ArrayList<>();

                                    StringBuilder imageName = new StringBuilder();

                                    for (MediaInfoDataModel getMediaInfoDatum : getMediaInfoData) {
                                    /*
                                new code for summary attachments
                                 */
                                        List<AttachmentItemList> attachmentItemLists = getMediaInfoDatum.getAttachmentItemLists();
                                        for (AttachmentItemList att : attachmentItemLists) {
                                            if (!att.isDeleted) {
                                                mediaInfoDataModels.add(new MediaInfoDataModel(att.getFileName(),
                                                        att.getItem_url()));
                                                File f = new File(att.getFileName());
                                                imageName.append(f.getName()).append("\n");
                                            }
                                        }

//                                    mediaInfoDataModels.add(new MediaInfoDataModel(getMediaInfoDatum.getFilename(),
//                                            getMediaInfoDatum.getItem_url()));
//                                    File f = new File(getMediaInfoDatum.getFilename());
//                                    imageName.append(f.getName()).append("\n");
                                    }

                                    if (mediaInfoDataModels.size() > 0) {
                                        summary.add(new SummaryChildItemModel("Attachments", Utils.getGson().toJson(mediaInfoDataModels), "", false, true));
                                    }

                                }

                            } else {
                                summary.add(new SummaryChildItemModel("Member Availability", "No", "", false, false));
                                summary.add(new SummaryChildItemModel("Remark", unitInfoDataModel.getRemarks(), unitInfoDataModel.getGen_qc_remarks(), false, false));
                            }
                            ArrayList<HohInfoDataModel> hh = new ArrayList<>();
                            for (HohInfoDataModel hohInfoDataModel : hohInfoDataModelArrayList) {
                                hh.add(hohInfoDataModel);
                                ArrayList<MemberInfoDataModel> memberInfoDataModels = new ArrayList<>();
                                if (localSurveyDbViewModel.getMemberInfoData(hohInfoDataModel.getHoh_id()).size() > 0) {
                                    memberInfoDataModels.addAll(localSurveyDbViewModel.getMemberInfoData(hohInfoDataModel.getHoh_id()));
                                    memberInfoList.put(hohInfoDataModel.getHoh_id(), memberInfoDataModels);
                                }
                            }
                            try {
                                HashMap<String, ArrayList<HohInfoDataModel>> unitI = App.getInstance().getHohList();
                                if (unitI != null && unitI.size() > 0) {
                                    if (unitInfoDataModel.getGlobalId().equals("")) {
                                        unitI.put(unitInfoDataModel.getUnit_id(), hh);
                                    } else {
                                        unitI.put(unitInfoDataModel.getRel_globalid(), hh);
                                    }
                                    App.getInstance().setHohList(unitI);
                                } else {
                                    HashMap<String, ArrayList<HohInfoDataModel>> pp = new HashMap<String, ArrayList<HohInfoDataModel>>();
                                    if (unitInfoDataModel.getGlobalId().equals("")) {
                                        pp.put(unitInfoDataModel.getUnit_id(), hh);
                                    } else {
                                        pp.put(unitInfoDataModel.getRel_globalid(), hh);
                                    }
                                    App.getInstance().setHohList(pp);
                                }
                            } catch (Exception ex) {
                                ex.getMessage();
                            }

                            summaryItemModels.add(new SummaryItemModel(unitInfoDataModel.getUnit_unique_id(), unitInfoDataModel.getUnit_status(), summary, true, hohInfoDataModelArrayList, memberInfoList, hohInfoDataModelArrayList.size() > 0));

                            unitInfoAddedPositionDataModelArrayList.add(unitInfoDataModel);
                        }

                        summaryListAdapter.setSummaryItemModels(summaryItemModels);


                    });
                }
            });
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.logData(activity, ex.getMessage());
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    // IMP - StructureEditBtnClick
    @Override
    public void onStructureEditBtnClicked() {
        if(structureInfoPointDataMode != null &&
                structureInfoPointDataMode.getStructure_status() != null &&
                !structureInfoPointDataMode.getStructure_status().isEmpty()) {
            if (!structureInfoPointDataMode.getStructure_status().equals(Constants.completed) &&
                    !structureInfoPointDataMode.getStructure_status().equals(Constants.dispute)) {
                boolean unitStatus = true;
                ArrayList<String> unitStatusList = new ArrayList<>();
                for (UnitInfoDataModel i : unitInfoAddedPositionDataModelArrayList) {
                    unitStatusList.add(i.getUnit_status());
                }
                if (unitStatusList.contains(Constants.InProgress) || unitStatusList.contains(Constants.OnHold)) {
                    unitStatus = false;
                }
                if (App.getInstance().getUserModel() != null && App.getInstance().getUserModel().getUser_name() != null &&
                        !App.getInstance().getUserModel().getUser_name().equals("")) {
                    structureInfoPointDataMode.setSurveyor_name(App.getInstance().getUserModel().getUser_name());
                }
                localSurveyDbViewModel.insertStructureInfoPointData(structureInfoPointDataMode, activity);
                activity.startActivity(new Intent(activity, StructureActivity.class)
                        .putExtra(Constants.IS_EDITING, IS_EDITING)
                        .putExtra(Constants.EDIT_TYPE, Constants.EDIT_StructureInfo)
                        .putExtra(Constants.UnitInfo_unit_status, unitStatus)
                        .putExtra(Constants.viewMode, viewMode)
                        .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataMode));
                activity.finish();
            } else {
                String structureErrorMsg = activity.getString(R.string.struct_lock_error_msg);
                Utils.shortToast(structureErrorMsg, activity);
            }
        } else {
            Utils.shortToast("Something went wrong while editing the structure. Please try again!!", activity);
        }
    }

    @Override
    public void onUnitEditBtnClicked(int position) {
        try {
            String str = "";
            if (App.getSharedPreferencesHandler().containKey("local") && App.getSharedPreferencesHandler().getString("local").equals("local")) {
                str = "local";
            }

            List<UnitInfoDataModel> un = localSurveyDbViewModel.getUnitByUniqueId(unitInfoAddedPositionDataModelArrayList.get(position).getUnit_unique_id());
            if (un != null && un.size() > 0 && un.get(0).isUploaded() && str.equals("")) {
                Utils.shortToast("Please goto upload screen to view and edit the respective record!", activity);
            } else {
                App.getSharedPreferencesHandler().putString("local", "test");
                Log.i("Debug onUnitEditBtnClicked=", "step 1");
                if (unitInfoAddedPositionDataModelArrayList.size() >= position) {

                    UnitInfoDataModel unitInfoDataModel1 = unitInfoAddedPositionDataModelArrayList.get(position);
                    List<String> mediaInfoDataParentUniqueId = new ArrayList<>();
                    List<MediaInfoDataModel> mediaInfoDataModels1 = new ArrayList<>();

                    ArrayList<HohInfoDataModel> hohInfoDataModelArrayList1 = new ArrayList<>();
                    ArrayList<MemberInfoDataModel> memberInfoDataModels = new ArrayList<>();
//                if(unitInfoDataModel1.getForm_lock()==0 && !unitInfoDataModel1.getUnit_status().equals(Constants.completed_statusLayer)&& !unitInfoDataModel1.getUnit_status().equals(Constants.completed_dispute)){
                    if (unitInfoDataModel1.getForm_lock() == 0) {
                        if (!viewMode.equals(Constants.offline)) {
                            if (unitMediaInfoDataModel != null) {
                                mediaInfoDataModels1 = unitMediaInfoDataModel.get(unitInfoAddedPositionDataModelArrayList.get(position).getGlobalId().toUpperCase());
                                mediaInfoDataParentUniqueId.add(unitInfoDataModel1.getUnit_id());
                            }

                            if (hohListForUnitGlobalIdHashmap != null && hohListForUnitGlobalIdHashmap.size() > 0) {
                                hohInfoDataModelArrayList1 = hohListForUnitGlobalIdHashmap.get(unitInfoDataModel1.getGlobalId().toUpperCase());

//                    mediaInfoDataParentUniqueId.add(unitInfoDataModel1.getUnit_id());

                                if (hohInfoDataModelArrayList1 != null) {
                                    for (HohInfoDataModel hohInfoDataModel : hohInfoDataModelArrayList1) {
                                        if (memberListForUnitGlobalIdHashmap != null && memberListForUnitGlobalIdHashmap.containsKey(hohInfoDataModel.getGlobalId().toUpperCase())) {
                                            memberInfoDataModels.addAll(memberListForUnitGlobalIdHashmap.get(hohInfoDataModel.getGlobalId().toUpperCase()));
                                        }

                                        if (hohMediaInfoDataModel != null && hohMediaInfoDataModel.containsKey(hohInfoDataModel.getGlobalId().toUpperCase())) {
                                            if (mediaInfoDataModels1 == null) {
                                                mediaInfoDataModels1 = new ArrayList<>();
                                            }
                                            mediaInfoDataModels1.addAll(hohMediaInfoDataModel.get(hohInfoDataModel.getGlobalId().toUpperCase()));
                                        }

                                        mediaInfoDataParentUniqueId.add(hohInfoDataModel.getHoh_id());
                                    }

                                    if (memberInfoDataModels != null) {
                                        for (MemberInfoDataModel memberInfoDataModel : memberInfoDataModels) {

                                            if (memberMediaInfoDataModel != null && memberMediaInfoDataModel.containsKey(memberInfoDataModel.getGlobalId().toUpperCase())) {
                                                mediaInfoDataModels1.addAll(memberMediaInfoDataModel.get(memberInfoDataModel.getGlobalId().toUpperCase()));
                                            }

                                            mediaInfoDataParentUniqueId.add(memberInfoDataModel.getMember_id());
                                        }
                                    }

                                    localSurveyDbViewModel.insertAllHohInfoPointData(hohInfoDataModelArrayList1, activity);
                                    localSurveyDbViewModel.insertAllMemberInfoPointData(memberInfoDataModels, activity);
                                }
                            }


                            if (mediaInfoDataModels1 != null && mediaInfoDataModels1.size() > 0) {
                                localSurveyDbViewModel.deleteBulkMediaInfoData(mediaInfoDataParentUniqueId, activity);
//                    localSurveyDbViewModel.insertAllMediaInfoPointData(mediaInfoDataModels1, activity);

                                ArrayList<String> objList = new ArrayList<>();
                                List<MediaInfoDataModel> mediaParentList = new ArrayList<>();

                                for (int i = 0; i < mediaInfoDataModels1.size(); i++) {
                                    if (!objList.contains(mediaInfoDataModels1.get(i).getObejctId())) {
                                        mediaParentList.add(mediaInfoDataModels1.get(i));
                                        objList.add(mediaInfoDataModels1.get(i).getObejctId());
                                    }
                                }
                                for (int i = 0; i < mediaParentList.size(); i++) {
                                    //List<MediaInfoDataModel> list = new ArrayList<>();
                                    List<AttachmentItemList> attachmentItemLists = new ArrayList<>();
                                    for (int j = 0; j < mediaInfoDataModels1.size(); j++) {
                                        if (mediaParentList.get(i).getObejctId().equals(mediaInfoDataModels1.get(j).getObejctId())) {
                                            // list.add(mediaInfoDataModels1.get(j));
                                            attachmentItemLists.add(new AttachmentItemList(mediaInfoDataModels1.get(j).getMediaId(),
                                                    mediaInfoDataModels1.get(j).getFilename(), mediaInfoDataModels1.get(j).getItem_url(), false, false));
                                        }
                                    }
                                    // mediaParentList.get(i).setMediaList(list);
                                    mediaParentList.get(i).setAttachmentItemLists(attachmentItemLists);
                                }

                                localSurveyDbViewModel.insertAllMediaInfoPointData(mediaParentList, activity);

                                ArrayList<MediaDetailsDataModel> listMediaDetails = new ArrayList<>();

                                for (MediaInfoDataModel mediaInfo : mediaParentList) {
                                    localSurveyDbViewModel.deleteMediaDetailsByRelGlobalId(mediaInfo.getGlobalId().toLowerCase());
                                }

                                for (MediaInfoDataModel mediaInfo : mediaParentList) {
                                    if (mapMediaDetailsModelUnit.get(mediaInfo.getGlobalId().toLowerCase()) != null) {
                                        listMediaDetails.addAll(Objects.requireNonNull(mapMediaDetailsModelUnit.get(mediaInfo.getGlobalId().toLowerCase())));
                                    }

                                    if (mapMediaDetailsModelHoh.get(mediaInfo.getGlobalId().toLowerCase()) != null) {
                                        listMediaDetails.addAll(Objects.requireNonNull(mapMediaDetailsModelHoh.get(mediaInfo.getGlobalId().toLowerCase())));
                                    }

                                    if (mapMediaDetailsModelMember.get(mediaInfo.getGlobalId().toLowerCase()) != null) {
                                        listMediaDetails.addAll(Objects.requireNonNull(mapMediaDetailsModelMember.get(mediaInfo.getGlobalId().toLowerCase())));
                                    }
                                }

                                localSurveyDbViewModel.insertAllMediaDetailsPointData(listMediaDetails);
                            }

                            localSurveyDbViewModel.insertStructureInfoPointData(structureInfoPointDataMode, activity);
                            localSurveyDbViewModel.insertUnitInfoPointData(unitInfoDataModel1, activity);

                        }
//                    if ((hohInfoDataModelArrayList1 != null && hohInfoDataModelArrayList1.size()<1) && (viewMode.equals(Constants.offline) || viewMode.equals(Constants.offlineWebmap)) ){
                        if (hohInfoDataModelArrayList1 != null) {
                            hohInfoDataModelArrayList1.addAll(localSurveyDbViewModel.getHohInfoData(unitInfoAddedPositionDataModelArrayList.get(position).getUnit_id()));
                        }
//                    }

                        if (hohInfoDataModelArrayList1 != null && hohInfoDataModelArrayList1.size() > 0 && memberInfoDataModelArrayList != null && memberInfoDataModelArrayList.size() > 0) {
                            activity.startActivity(new Intent(activity, StructureActivity.class)
                                    .putExtra(Constants.IS_EDITING, IS_EDITING)
                                    .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                    .putExtra(Constants.viewMode, viewMode)
                                    .putExtra(Constants.INTENT_DATA_UnitInfo, unitInfoAddedPositionDataModelArrayList.get(position))
                                    .putExtra(Constants.INTENT_DATA_HohInfo, hohInfoDataModelArrayList1.get(0))
                                    .putExtra(Constants.INTENT_DATA_MamberInfo, memberInfoDataModelArrayList)//multiple members are allowed
                                    .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataMode));
                            activity.finish();
                        } else if (hohInfoDataModelArrayList1 != null && hohInfoDataModelArrayList1.size() > 0) {
                            activity.startActivity(new Intent(activity, StructureActivity.class)
                                    .putExtra(Constants.IS_EDITING, IS_EDITING)
                                    .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                    .putExtra(Constants.viewMode, viewMode)
                                    .putExtra(Constants.INTENT_DATA_UnitInfo, unitInfoAddedPositionDataModelArrayList.get(position))
                                    .putExtra(Constants.INTENT_DATA_HohInfo, hohInfoDataModelArrayList1.get(0))
                                    .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataMode));
                            activity.finish();
                        } else {
                            activity.startActivity(new Intent(activity, StructureActivity.class)
                                    .putExtra(Constants.IS_EDITING, IS_EDITING)
                                    .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                    .putExtra(Constants.viewMode, viewMode)
                                    .putExtra(Constants.INTENT_DATA_UnitInfo, unitInfoAddedPositionDataModelArrayList.get(position))
                                    .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataMode));
                            activity.finish();

                        }
                    } else {
                        if (unitInfoDataModel1.getUnit_status().equals(Constants.completed_statusLayer)) {
                            Utils.shortToast("Form Lock! You can-not edit completed unit", activity);
                            showActionAlertFormLocked();
                        } else if (unitInfoDataModel1.getUnit_status().equals(Constants.completed_dispute)) {
                            Utils.shortToast("Form Lock! You can-not edit disputed unit", activity);
                            showActionAlertFormLocked();
                        } else {
                            Utils.shortToast("Form Locked", activity);
                            showActionAlertFormLocked();
                        }
                    }
                }
                Log.i("Debug onUnitEditBtnClicked=", "step 8");
            }
        } catch (Exception e) {
            AppLog.logData(activity, e.getMessage());
            AppLog.e("Error onUnitEditBtnClicked= " + e.getMessage());
        }
    }

    class UnitDetailsSummary {
        ArrayList<SummaryChildItemModel> unitSummaryHashmap;
        UnitInfoDataModel unitInfoDataModel;

        public UnitDetailsSummary(ArrayList<SummaryChildItemModel> unitSummaryHashmap, UnitInfoDataModel unitInfoDataModel) {
            this.unitSummaryHashmap = unitSummaryHashmap;
            this.unitInfoDataModel = unitInfoDataModel;
        }

        public ArrayList<SummaryChildItemModel> getUnitSummaryHashmap() {
            return unitSummaryHashmap;
        }

        public void setUnitSummaryHashmap(ArrayList<SummaryChildItemModel> unitSummaryHashmap) {
            this.unitSummaryHashmap = unitSummaryHashmap;
        }

        public UnitInfoDataModel getUnitInfoDataModel() {
            return unitInfoDataModel;
        }

        public void setUnitInfoDataModel(UnitInfoDataModel unitInfoDataModel) {
            this.unitInfoDataModel = unitInfoDataModel;
        }
    }

    private void getMediaInfoDetailsOnline(String uniqueId, UnitInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        if (!Utils.checkinterne(activity))
            return;
        Utils.updateProgressMsg("Getting Media Info records .Please wait..", activity);
        ServiceFeatureTable unitFT = App.getInstance().getMediaFT();
        if (unitFT.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();
            query.setWhereClause("rel_globalid = '{" + uniqueId.toUpperCase() + "}'");
            AppLog.e("unit rel_globalid : '" + uniqueId.toUpperCase() + "'");
            final ListenableFuture<FeatureQueryResult> future = unitFT.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        for (final Feature feature : result) {
                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            s = Utils.getString(mapAttributeValue.get("objectid"));
                            Utils.getString(mapAttributeValue.get("globalid"));
                            ArcGISFeature arcGISFeature = (ArcGISFeature) feature;
                            List<Attachment> attachmentsAsync;
                            try {
                                attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                            } catch (Exception e) {
                                AppLog.logData(activity, e.getMessage());
                                attachmentsAsync = new ArrayList<>();
                            }
                            unitAttachmentHashmap.put("" + s, attachmentsAsync);
                            mediaAttributesArrayList.add(mapAttributeValue);
                        }
                        retrieveMediaDetails(mediaAttributesArrayList, "" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                    } else if (unitInfoDataModel.getUnit_usage().equalsIgnoreCase("others") && unitInfoDataModel.isMember_available()) { // vidnyan  code here
                        unitSummaryHashmap.put(unitInfoDataModel.getGlobalId().toUpperCase(), new UnitDetailsSummary(summary, unitInfoDataModel));
                        featureTableUnitPosition++;
                        unitAttachmentHashmap = new HashMap<>();
                        mediaAttributesArrayList = new ArrayList<>();
                        getUnitDetails(featureTableUnitPosition);
                    } else {

                       /* featureTableUnitPosition++;
                        unitAttachmentHashmap = new HashMap<>();
                        mediaAttributesArrayList = new ArrayList<>();
                        getUnitDetails(featureTableUnitPosition);  //need to add attachment condition below
                            if(unitInfoDataModel.getUnit_usage().equalsIgnoreCase("others") && unitInfoDataModel.isMember_available()) {
                                setUpUnitSummaryItem();
                            } else {*/
                        setUpSummaryItem(unitInfoDataModel.getUnit_unique_id(), unitInfoDataModel.getUnit_status(),
                                summary, true, null, null, false);
                        //}

                        /*getUnitAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                        Utils.dismissProgress();
                        Utils.shortToast("No unit details found for this structure.", activity);*/
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.shortToast("Unable to get the unit details.", activity);
                }
            });
        } else if (unitFT.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + unitFT.getLoadError().getCause() + "\nError message: " + unitFT.getLoadError().getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }


    private void retrieveMediaDetails(ArrayList<Map<String, Object>> mediaAttributesArrayList, String s, UnitInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        try {
            mediaModelArrayList = new ArrayList<>();
            for (Map<String, Object> mapAttributeValue : mediaAttributesArrayList) {
                if (mapAttributeValue != null) {
                    MediaInfoDataModel mediaInfoDataModel;
                    if ((Utils.getString(mapAttributeValue.get(Constants.document_category).toString()).equalsIgnoreCase(Constants.UnitDistometerVideo))) {
                        mediaInfoDataModel = new MediaInfoDataModel(
                                Utils.getString(mapAttributeValue.get(Constants.objectid)),
                                Utils.getString(mapAttributeValue.get(Constants.filename)),
                                Utils.getString(mapAttributeValue.get(Constants.date_on_document)),
                                Utils.getString(mapAttributeValue.get(Constants.rel_globalid)),
                                Utils.getString(""),
                                Utils.getString(mapAttributeValue.get(Constants.globalid)),
                                Utils.getString(mapAttributeValue.get(Constants.document_category)),
                                Utils.getString(mapAttributeValue.get(Constants.document_type)),
                                Utils.getString(mapAttributeValue.get(Constants.document_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.name_on_document)),
                                Utils.getString(unitInfoDataModel.getGlobalId()),
                                Utils.getString(mapAttributeValue.get(Constants.item_url))
                        );
                    } else {
                        mediaInfoDataModel = new MediaInfoDataModel(
                                Utils.getString(mapAttributeValue.get(Constants.objectid)),
                                Utils.getString(mapAttributeValue.get(Constants.filename)),
                                Utils.getString(mapAttributeValue.get(Constants.date_on_document)),
                                Utils.getString(mapAttributeValue.get(Constants.rel_globalid)),
                                Utils.getString(""),
                                Utils.getString(mapAttributeValue.get(Constants.globalid)),
                                Utils.getString(mapAttributeValue.get(Constants.document_category)),
                                Utils.getString(mapAttributeValue.get(Constants.document_type)),
                                Utils.getString(mapAttributeValue.get(Constants.document_remarks)),
                                Utils.getString(mapAttributeValue.get(Constants.name_on_document)),
                                Utils.getString(unitInfoDataModel.getGlobalId())
                        );
                    }

                    mediaModelArrayList.add(mediaInfoDataModel);
                }
            }
            mediaAttributesArrayList = new ArrayList<>();
            getMediaDetailsByMediaInfo(mediaModelArrayList, unitInfoDataModel, summary);

        } catch (Exception e) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getMediaDetailsByMediaInfo(ArrayList<MediaInfoDataModel> mediaModelArrayList, UnitInfoDataModel unitInfoDataModel,
                                            ArrayList<SummaryChildItemModel> summary) {

        if (viewMode.equals(Constants.online)) {
            if (!Utils.checkinterne(activity))
                return;
        }

        Utils.updateProgressMsg("Getting Media Info records .Please wait..", activity);

        ArcGISFeatureTable arcGidFeatureTable;

        if (viewMode.equals(Constants.online)) {
            arcGidFeatureTable = App.getInstance().getMediaDetailsFT();
        } else {
            arcGidFeatureTable = App.getInstance().getMediaDetailsGFT();
        }

        // mapMediaDetailsModel = new LinkedHashMap<>();
        listMediaDetailsAttributeUnit = new ArrayList<>();

        if (arcGidFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();

            StringBuilder whereClause = new StringBuilder();

            for (MediaInfoDataModel mediaInfoDataModel : mediaModelArrayList) {

                if (whereClause.length() > 0)
                    whereClause.append(" OR ");

                whereClause.append("rel_globalid=").append("'{" + mediaInfoDataModel.getGlobalId().toUpperCase() + "}'");
            }

            query.setWhereClause(whereClause.toString());

            final ListenableFuture<FeatureQueryResult> future = arcGidFeatureTable.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        for (final Feature feature : result) {
                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            int objectid = Utils.getInteger(mapAttributeValue.get("objectid"));
                            String globalid = Utils.getString(mapAttributeValue.get("globalid"));
                            String content_type = Utils.getString(mapAttributeValue.get("content_type"));
                            String file_path = Utils.getString(mapAttributeValue.get("file_path"));
                            String file_name = Utils.getString(mapAttributeValue.get("file_name"));
                            String file_ext = Utils.getString(mapAttributeValue.get("file_ext"));
                            String rel_globalid = Utils.getString(mapAttributeValue.get("rel_globalid"));
                            long size = Utils.getInteger(mapAttributeValue.get("data_size"));
                            listMediaDetailsAttributeUnit.add(mapAttributeValue);
                        }

                        getMediaDetailsAttributed(unitInfoDataModel);

                        // retrieveMediaDetails(mediaAttributesArrayList,"" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                    } else if (unitInfoDataModel.getUnit_usage().equalsIgnoreCase("others") && unitInfoDataModel.isMember_available()) { // vidnyan  code here
                        unitSummaryHashmap.put(unitInfoDataModel.getGlobalId().toUpperCase(), new UnitDetailsSummary(summary, unitInfoDataModel));
                        featureTableUnitPosition++;
                        unitAttachmentHashmap = new HashMap<>();
                        // mapUnitAttachmentsOnline = new LinkedHashMap<>();
                        //mediaAttributesArrayList = new ArrayList<>();
                        getUnitDetails(featureTableUnitPosition);
                    } else {

                       /* featureTableUnitPosition++;
                        unitAttachmentHashmap = new HashMap<>();
                        mediaAttributesArrayList = new ArrayList<>();
                        getUnitDetails(featureTableUnitPosition);  //need to add attachment condition below
                            if(unitInfoDataModel.getUnit_usage().equalsIgnoreCase("others") && unitInfoDataModel.isMember_available()) {
                                setUpUnitSummaryItem();
                            } else {*/
                        setUpSummaryItem(unitInfoDataModel.getUnit_unique_id(), unitInfoDataModel.getUnit_status(),
                                summary, true, null, null, false);
                        //}

                        /*getUnitAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                        Utils.dismissProgress();
                        Utils.shortToast("No unit details found for this structure.", activity);*/
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.shortToast("Unable to get the unit details.", activity);
                }
            });
        } else if (arcGidFeatureTable.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " +
                    arcGidFeatureTable.getLoadError().getCause() + "\nError message: " + arcGidFeatureTable.getLoadError().getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getMediaDetailsAttributedHOH(HohInfoDataModel unitInfoDataModel) {

        try {

            for (Map<String, Object> mapAttributeValue : listMediaDetailsAttributeHOH) {

                if (mapAttributeValue != null) {

                    MediaDetailsDataModel mediaDetailsDataModel = new MediaDetailsDataModel(
                            Utils.getString(mapAttributeValue.get(Constants.objectid)),
                            Utils.getString(mapAttributeValue.get(Constants.globalid)),
                            Utils.getString(mapAttributeValue.get(Constants.content_type)),
                            Utils.getString(mapAttributeValue.get(Constants.file_path)),
                            Utils.getString(mapAttributeValue.get(Constants.file_name)),
                            Utils.getString(mapAttributeValue.get(Constants.file_ext)),
                            Utils.getString(mapAttributeValue.get(Constants.rel_globalid)),
                            Utils.getInteger(mapAttributeValue.get(Constants.data_size)),
                            Utils.getString(mapAttributeValue.get(Constants.remarks)), "",true, true);

                    if (!mapMediaDetailsModelHoh.containsKey(mediaDetailsDataModel.getRel_globalid()))
                        mapMediaDetailsModelHoh.put(mediaDetailsDataModel.getRel_globalid(), new ArrayList<>());

                    mapMediaDetailsModelHoh.get(mediaDetailsDataModel.getRel_globalid()).add(mediaDetailsDataModel);
                }
            }

            getHohAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getMediaDetailsAttributed(UnitInfoDataModel unitInfoDataModel) {

        try {

            for (Map<String, Object> mapAttributeValue : listMediaDetailsAttributeUnit) {

                if (mapAttributeValue != null) {

                    MediaDetailsDataModel mediaDetailsDataModel = new MediaDetailsDataModel(
                            Utils.getString(mapAttributeValue.get(Constants.objectid)),
                            Utils.getString(mapAttributeValue.get(Constants.globalid)),
                            Utils.getString(mapAttributeValue.get(Constants.content_type)),
                            Utils.getString(mapAttributeValue.get(Constants.file_path)),
                            Utils.getString(mapAttributeValue.get(Constants.file_name)),
                            Utils.getString(mapAttributeValue.get(Constants.file_ext)),
                            Utils.getString(mapAttributeValue.get(Constants.rel_globalid)),
                            Utils.getInteger(mapAttributeValue.get(Constants.data_size)),
                            Utils.getString(mapAttributeValue.get(Constants.remarks)), "",true, true);

                    if (!mapMediaDetailsModelUnit.containsKey(mediaDetailsDataModel.getRel_globalid()))
                        mapMediaDetailsModelUnit.put(mediaDetailsDataModel.getRel_globalid(), new ArrayList<>());

                    mapMediaDetailsModelUnit.get(mediaDetailsDataModel.getRel_globalid()).add(mediaDetailsDataModel);
                }
            }

            if (!unitInfoDataModel.isMember_available() && unitInfoDataModel.getRespondent_dob() != null && !unitInfoDataModel.getRespondent_dob().equals("")) {
                saveAttachmentForYesNo(unitInfoDataModel, summary);
            } else {
                getUnitAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getMediaInfoHoh(String uniqueId, HohInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        if (!Utils.checkinterne(activity))
            return;
        Utils.updateProgressMsg("Getting Media Info records .Please wait..", activity);
        ServiceFeatureTable unitFT = App.getInstance().getMediaFT();
        if (unitFT.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();
            query.setWhereClause("rel_globalid = '{" + uniqueId.toUpperCase() + "}'");
            AppLog.e("unit rel_globalid : '" + uniqueId.toUpperCase() + "'");
            final ListenableFuture<FeatureQueryResult> future = unitFT.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        for (final Feature feature : result) {
                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            s = Utils.getString(mapAttributeValue.get("objectid"));
                            Utils.getString(mapAttributeValue.get("globalid"));
                            ArcGISFeature arcGISFeature = (ArcGISFeature) feature;
                            List<Attachment> attachmentsAsync;
                            try {
                                attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                            } catch (Exception e) {
                                AppLog.logData(activity, e.getMessage());
                                attachmentsAsync = new ArrayList<>();
                            }
                            hohAttachmentHashmap.put("" + s, attachmentsAsync);
                            mediaAttributesArrayList.add(mapAttributeValue);
                        }
                        retrieveHohMediaDetails(mediaAttributesArrayList, "" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                    } else {
                        getHohAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                        Utils.dismissProgress();
//                        Utils.shortToast("No unit details found for this structure.", activity);
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.shortToast("Unable to get the unit details.", activity);
                }
            });
        } else if (unitFT.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + unitFT.getLoadError().getCause() + "\nError message: " + unitFT.getLoadError().getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }


    private void retrieveHohMediaDetails(ArrayList<Map<String, Object>> mediaAttributesArrayList, String s, HohInfoDataModel unitInfoDataModel,
                                         ArrayList<SummaryChildItemModel> summary) {
        try {
            mediaModelArrayList = new ArrayList<>();
            for (Map<String, Object> mapAttributeValue : mediaAttributesArrayList) {
                if (mapAttributeValue != null) {
                    MediaInfoDataModel mediaInfoDataModel;
                    mediaInfoDataModel = new MediaInfoDataModel(
                            Utils.getString(mapAttributeValue.get(Constants.objectid)),
                            Utils.getString(mapAttributeValue.get(Constants.filename)),
                            Utils.getString(mapAttributeValue.get(Constants.date_on_document)),
                            Utils.getString(mapAttributeValue.get(Constants.rel_globalid)),
                            Utils.getString(""),
                            Utils.getString(mapAttributeValue.get(Constants.globalid)),
                            Utils.getString(mapAttributeValue.get(Constants.document_category)),
                            Utils.getString(mapAttributeValue.get(Constants.document_type)),
                            Utils.getString(mapAttributeValue.get(Constants.document_remarks)),
                            Utils.getString(mapAttributeValue.get(Constants.name_on_document)),
                            Utils.getString(unitInfoDataModel.getGlobalId())
                    );
                    mediaModelArrayList.add(mediaInfoDataModel);
                }
            }
            mediaAttributesArrayList = new ArrayList<>();
            getMediaDetailsByMediaInfoHOH(mediaModelArrayList, unitInfoDataModel, summary);

        } catch (Exception e) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    public void getMediaDetailsByMediaInfoHOH(ArrayList<MediaInfoDataModel> mediaModelArrayList, HohInfoDataModel unitInfoDataModel,
                                              ArrayList<SummaryChildItemModel> summary) {

        if (viewMode.equals(Constants.online)) {
            if (!Utils.checkinterne(activity))
                return;
        }

        Utils.updateProgressMsg("Getting Media Info records .Please wait..", activity);

        ArcGISFeatureTable arcGidFeatureTable;

        if (viewMode.equals(Constants.online)) {
            arcGidFeatureTable = App.getInstance().getMediaDetailsFT();
        } else {
            arcGidFeatureTable = App.getInstance().getMediaDetailsGFT();
        }

        // mapMediaDetailsModel = new LinkedHashMap<>();
        listMediaDetailsAttributeHOH = new ArrayList<>();

        if (arcGidFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();

            StringBuilder whereClause = new StringBuilder();

            for (MediaInfoDataModel mediaInfoDataModel : mediaModelArrayList) {

                if (whereClause.length() > 0)
                    whereClause.append(" OR ");

                whereClause.append("rel_globalid=").append("'{" + mediaInfoDataModel.getGlobalId().toUpperCase() + "}'");
            }

            query.setWhereClause(whereClause.toString());

            final ListenableFuture<FeatureQueryResult> future = arcGidFeatureTable.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        for (final Feature feature : result) {
                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            int objectid = Utils.getInteger(mapAttributeValue.get("objectid"));
                            String globalid = Utils.getString(mapAttributeValue.get("globalid"));
                            String content_type = Utils.getString(mapAttributeValue.get("content_type"));
                            String file_path = Utils.getString(mapAttributeValue.get("file_path"));
                            String file_name = Utils.getString(mapAttributeValue.get("file_name"));
                            String file_ext = Utils.getString(mapAttributeValue.get("file_ext"));
                            String rel_globalid = Utils.getString(mapAttributeValue.get("rel_globalid"));
                            long size = Utils.getInteger(mapAttributeValue.get("data_size"));
                            listMediaDetailsAttributeHOH.add(mapAttributeValue);
                        }

                        getMediaDetailsAttributedHOH(unitInfoDataModel);

                        // retrieveMediaDetails(mediaAttributesArrayList,"" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                    } else {
                        getHohAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                        Utils.dismissProgress();
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.shortToast("Unable to get the unit details.", activity);
                }
            });
        } else if (arcGidFeatureTable.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " +
                    arcGidFeatureTable.getLoadError().getCause() + "\nError message: " + arcGidFeatureTable.getLoadError().getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getHohAttachmentOffline(String objectId, HohInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        try {
            ArrayList<MediaInfoDataModel> arrayList = new ArrayList<>();
            for (int i = 0; i < mediaModelArrayList.size(); i++) {
                mediaInfoDataModels = new ArrayList<>();
                ArrayList<MediaDetailsDataModel> listMediaDetails = mapMediaDetailsModelHoh.get(mediaModelArrayList.get(i).getGlobalId());
                if (listMediaDetails != null) {
                    if (!listMediaDetails.isEmpty()) {
                        for (MediaDetailsDataModel attachmentInfo : listMediaDetails) {
                            String weburl = Constants.WebFilePath + attachmentInfo.getFile_path() + attachmentInfo.getFile_name() + attachmentInfo.getFile_ext();
                            mediaInfoDataModels.add(new MediaInfoDataModel(attachmentInfo.getFile_name() + attachmentInfo.getFile_ext(), weburl));

                            arrayList.add(new MediaInfoDataModel(attachmentInfo.getContent_type(),
                                    attachmentInfo.getFile_name(), (int) attachmentInfo.getData_size(), "",
                                    weburl,
                                    (short) 0, (short) 0,
                                    Constants.hoh_infoLayer, unitInfoDataModel.getHoh_id(), unitInfoDataModel.getRel_globalid().toUpperCase(),
                                    unitInfoDataModel.getRelative_path(),
                                    mediaModelArrayList.get(i).getObejctId(),
                                    // unitInfoDataModel.getGlobalId().toUpperCase(),
                                    mediaModelArrayList.get(i).getGlobalId().toUpperCase(),
                                    true, new Date(), new Date(), mediaModelArrayList.get(i).getDocument_category(),
                                    mediaModelArrayList.get(i).getDocument_type(), mediaModelArrayList.get(i).getDocument_remarks(), mediaModelArrayList.get(i).getName_on_document()));

                        }

                        if (mediaInfoDataModels.size() > 0) {
//                        summary.add(new SummaryChildItemModel("Attachments", Utils.getGson().toJson(mediaInfoDataModels), "", false, true));
                        }

                        if (arrayList.size() > 0) {
                            hohMediaInfoDataModel.put(unitInfoDataModel.getGlobalId().toUpperCase(), arrayList);
                        }
                    }
                }
            }

//        unitSummaryHashmap.put(unitInfoDataModel.getGlobalId().toUpperCase(), new UnitDetailsSummary(summary, unitInfoDataModel));

            featureTableHohPosition++;
            hohAttachmentHashmap = new HashMap<>();
            mediaAttributesArrayList = new ArrayList<>();
            getHohDetails(featureTableHohPosition);
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private GregorianCalendar convertToGregorianCalendar(Object dateAttribute) {
        // Assuming dateAttribute is a string representing a date in a specific format
        String dateString = (String) dateAttribute;

        // You might need to change the date format based on the actual format of your date attribute
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = dateFormat.parse(dateString);
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(date);
            return gregorianCalendar;
        } catch (ParseException e) {
            AppLog.logData(activity, e.getMessage());
            e.printStackTrace();
            return null; // Handle the exception according to your needs
        }
    }


    private void getMediaInfoMember(String uniqueId, MemberInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        try {
            if (!Utils.checkinterne(activity))
                return;
            Utils.updateProgressMsg("Getting Media Info records .Please wait..", activity);
            ServiceFeatureTable unitFT = App.getInstance().getMediaFT();
            if (unitFT.getLoadStatus() == LoadStatus.LOADED) {
                QueryParameters query = new QueryParameters();
                query.setWhereClause("rel_globalid = '{" + uniqueId.toUpperCase() + "}'");
                AppLog.e("unit rel_globalid : '" + uniqueId.toUpperCase() + "'");
                final ListenableFuture<FeatureQueryResult> future = unitFT.queryFeaturesAsync(query);
                future.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = future.get();
                        if (result.iterator().hasNext()) {
                            for (final Feature feature : result) {
                                Map<String, Object> mapAttributeValue = feature.getAttributes();
                                s = Utils.getString(mapAttributeValue.get("objectid"));
                                Utils.getString(mapAttributeValue.get("globalid"));
                                ArcGISFeature arcGISFeature = (ArcGISFeature) feature;
                                List<Attachment> attachmentsAsync;
                                try {
                                    attachmentsAsync = arcGISFeature.fetchAttachmentsAsync().get();
                                } catch (Exception e) {
                                    AppLog.logData(activity, e.getMessage());
                                    attachmentsAsync = new ArrayList<>();
                                }
                                memberAttachmentHashmap.put("" + s, attachmentsAsync);
                                mediaAttributesArrayList.add(mapAttributeValue);
                            }
                            retrieveMemberMediaDetails(mediaAttributesArrayList, "" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                        } else {
                            getMemberAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                            Utils.dismissProgress();
//                        Utils.shortToast("No unit details found for this structure.", activity);
                        }
                    } catch (Exception e) {
                        Utils.dismissProgress();
                        AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                        Utils.shortToast("Unable to get the unit details.", activity);
                    }
                });
            } else if (unitFT.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                Utils.dismissProgress();
                AppLog.e("Unable to get the unit details.\n Error cause: " + unitFT.getLoadError().getCause() + "\nError message: " + unitFT.getLoadError().getMessage());
                Utils.shortToast("Unable to get the unit details.", activity);
            }
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }


    private void retrieveMemberMediaDetails(ArrayList<Map<String, Object>> mediaAttributesArrayList, String s, MemberInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        try {
            mediaModelArrayList = new ArrayList<>();
            for (Map<String, Object> mapAttributeValue : mediaAttributesArrayList) {
                if (mapAttributeValue != null) {
                    MediaInfoDataModel mediaInfoDataModel;
                    mediaInfoDataModel = new MediaInfoDataModel(
                            Utils.getString(mapAttributeValue.get(Constants.objectid)),
                            Utils.getString(mapAttributeValue.get(Constants.filename)),
                            Utils.getString(mapAttributeValue.get(Constants.date_on_document)),
                            Utils.getString(mapAttributeValue.get(Constants.rel_globalid)),
                            Utils.getString(""),
                            Utils.getString(mapAttributeValue.get(Constants.globalid)),
                            Utils.getString(mapAttributeValue.get(Constants.document_category)),
                            Utils.getString(mapAttributeValue.get(Constants.document_type)),
                            Utils.getString(mapAttributeValue.get(Constants.document_remarks)),
                            Utils.getString(mapAttributeValue.get(Constants.name_on_document)),
                            Utils.getString(unitInfoDataModel.getMember_id())
                    );
                    mediaModelArrayList.add(mediaInfoDataModel);
                }
            }
            mediaAttributesArrayList = new ArrayList<>();
            getMediaDetailsByMediaInfoMember(mediaModelArrayList, unitInfoDataModel, summary);

        } catch (Exception e) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    public void getMediaDetailsByMediaInfoMember(ArrayList<MediaInfoDataModel> mediaModelArrayList, MemberInfoDataModel unitInfoDataModel,
                                                 ArrayList<SummaryChildItemModel> summary) {

        if (viewMode.equals(Constants.online)) {
            if (!Utils.checkinterne(activity))
                return;
        }

        Utils.updateProgressMsg("Getting Media Info records .Please wait..", activity);

        ArcGISFeatureTable arcGidFeatureTable;

        if (viewMode.equals(Constants.online)) {
            arcGidFeatureTable = App.getInstance().getMediaDetailsFT();
        } else {
            arcGidFeatureTable = App.getInstance().getMediaDetailsGFT();
        }

        // mapMediaDetailsModel = new LinkedHashMap<>();
        listMediaDetailsAttributeMember = new ArrayList<>();

        if (arcGidFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
            QueryParameters query = new QueryParameters();

            StringBuilder whereClause = new StringBuilder();

            for (MediaInfoDataModel mediaInfoDataModel : mediaModelArrayList) {

                if (whereClause.length() > 0)
                    whereClause.append(" OR ");

                whereClause.append("rel_globalid=").append("'{" + mediaInfoDataModel.getGlobalId().toUpperCase() + "}'");
            }

            query.setWhereClause(whereClause.toString());

            final ListenableFuture<FeatureQueryResult> future = arcGidFeatureTable.queryFeaturesAsync(query);
            future.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = future.get();
                    if (result.iterator().hasNext()) {
                        for (final Feature feature : result) {
                            Map<String, Object> mapAttributeValue = feature.getAttributes();
                            int objectid = Utils.getInteger(mapAttributeValue.get("objectid"));
                            String globalid = Utils.getString(mapAttributeValue.get("globalid"));
                            String content_type = Utils.getString(mapAttributeValue.get("content_type"));
                            String file_path = Utils.getString(mapAttributeValue.get("file_path"));
                            String file_name = Utils.getString(mapAttributeValue.get("file_name"));
                            String file_ext = Utils.getString(mapAttributeValue.get("file_ext"));
                            String rel_globalid = Utils.getString(mapAttributeValue.get("rel_globalid"));
                            long size = Utils.getInteger(mapAttributeValue.get("data_size"));
                            listMediaDetailsAttributeMember.add(mapAttributeValue);
                        }

                        getMediaDetailsAttributedMember(unitInfoDataModel);

                        // retrieveMediaDetails(mediaAttributesArrayList,"" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                    } else {
                        getMemberAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);
                        Utils.dismissProgress();
                    }
                } catch (Exception e) {
                    Utils.dismissProgress();
                    AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
                    Utils.shortToast("Unable to get the unit details.", activity);
                }
            });
        } else if (arcGidFeatureTable.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " +
                    arcGidFeatureTable.getLoadError().getCause() + "\nError message: " + arcGidFeatureTable.getLoadError().getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getMediaDetailsAttributedMember(MemberInfoDataModel unitInfoDataModel) {

        try {

            for (Map<String, Object> mapAttributeValue : listMediaDetailsAttributeMember) {

                if (mapAttributeValue != null) {

                    MediaDetailsDataModel mediaDetailsDataModel = new MediaDetailsDataModel(
                            Utils.getString(mapAttributeValue.get(Constants.objectid)),
                            Utils.getString(mapAttributeValue.get(Constants.globalid)),
                            Utils.getString(mapAttributeValue.get(Constants.content_type)),
                            Utils.getString(mapAttributeValue.get(Constants.file_path)),
                            Utils.getString(mapAttributeValue.get(Constants.file_name)),
                            Utils.getString(mapAttributeValue.get(Constants.file_ext)),
                            Utils.getString(mapAttributeValue.get(Constants.rel_globalid)),
                            Utils.getInteger(mapAttributeValue.get(Constants.data_size)),
                            Utils.getString(mapAttributeValue.get(Constants.remarks)), "",true, true);

                    if (!mapMediaDetailsModelMember.containsKey(mediaDetailsDataModel.getRel_globalid()))
                        mapMediaDetailsModelMember.put(mediaDetailsDataModel.getRel_globalid(), new ArrayList<>());

                    mapMediaDetailsModelMember.get(mediaDetailsDataModel.getRel_globalid()).add(mediaDetailsDataModel);
                }
            }

            getMemberAttachmentOffline("" + (int) Double.parseDouble(unitInfoDataModel.getObejctId()), unitInfoDataModel, summary);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.dismissProgress();
            AppLog.e("Unable to get the unit details.\n Error cause: " + e.getCause() + "\nError message: " + e.getMessage());
            Utils.shortToast("Unable to get the unit details.", activity);
        }
    }

    private void getMemberAttachmentOffline(String objectId, MemberInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        try {
            ArrayList<MediaInfoDataModel> arrayList = new ArrayList<>();
            for (int i = 0; i < mediaModelArrayList.size(); i++) {
                mediaInfoDataModels = new ArrayList<>();
                ArrayList<MediaDetailsDataModel> listMediaDetails = mapMediaDetailsModelMember.get(mediaModelArrayList.get(i).getGlobalId());
                if (listMediaDetails != null) {
                    if (!listMediaDetails.isEmpty()) {
                        for (MediaDetailsDataModel attachmentInfo : listMediaDetails) {
                            String weburl = Constants.WebFilePath + attachmentInfo.getFile_path() + attachmentInfo.getFile_name() + attachmentInfo.getFile_ext();
                            mediaInfoDataModels.add(new MediaInfoDataModel(attachmentInfo.getFile_name() + attachmentInfo.getFile_ext(), weburl));

                            arrayList.add(new MediaInfoDataModel(attachmentInfo.getContent_type(),
                                    attachmentInfo.getFile_name(), (int) attachmentInfo.getData_size(), "",
                                    weburl,
                                    (short) 0, (short) 0,
                                    Constants.member_infoLayer, unitInfoDataModel.getMember_id(), mediaModelArrayList.get(i).getRel_globalid().toUpperCase(),
                                    unitInfoDataModel.getRelative_path(),
                                    mediaModelArrayList.get(i).getObejctId(), mediaModelArrayList.get(i).getGlobalId().toUpperCase(), true, new Date(), new Date(), mediaModelArrayList.get(i).getDocument_category(),
                                    mediaModelArrayList.get(i).getDocument_type(), mediaModelArrayList.get(i).getDocument_remarks(), mediaModelArrayList.get(i).getName_on_document()));

                        }

                        if (mediaInfoDataModels.size() > 0) {
//                        summary.add(new SummaryChildItemModel("Attachments", Utils.getGson().toJson(mediaInfoDataModels), "", false, true));
                        }

                        if (arrayList.size() > 0) {
                            memberMediaInfoDataModel.put(unitInfoDataModel.getGlobalId().toUpperCase(), arrayList);
                        }
                    }
                }
            }

//        unitSummaryHashmap.put(unitInfoDataModel.getGlobalId().toUpperCase(), new UnitDetailsSummary(summary, unitInfoDataModel));

//        featureTableHohPosition++;
            featureTableMemberPosition++;
            memberAttachmentHashmap = new HashMap<>();
            mediaAttributesArrayList = new ArrayList<>();
//        getMemberDetails(featureTableHohPosition);
            getMemberDetails(featureTableMemberPosition);
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private void saveAttachmentForYesNo(UnitInfoDataModel unitInfoDataModel, ArrayList<SummaryChildItemModel> summary) {
        try {
            ArrayList<MediaInfoDataModel> arrayList = new ArrayList<>();
            for (int i = 0; i < mediaModelArrayList.size(); i++) {
                mediaInfoDataModels = new ArrayList<>();
                ArrayList<MediaDetailsDataModel> listMediaDetails = mapMediaDetailsModelUnit.get(mediaModelArrayList.get(i).getGlobalId());
                // List<Attachment> attachments = unitAttachmentHashmap.get(mediaModelArrayList.get(i).getObejctId());
                if (listMediaDetails != null) {
                    if (listMediaDetails.size() > 0) {
                        for (MediaDetailsDataModel attachmentInfo : listMediaDetails) {
                            String weburl = Constants.WebFilePath + attachmentInfo.getFile_path() + attachmentInfo.getFile_name() + attachmentInfo.getFile_ext();
                            mediaInfoDataModels.add(new MediaInfoDataModel(attachmentInfo.getFile_name() + attachmentInfo.getFile_ext(), weburl));

                            arrayList.add(new MediaInfoDataModel(attachmentInfo.getContent_type(),
                                    attachmentInfo.getFile_name(), (int) attachmentInfo.getData_size(), "",
                                    weburl,
                                    (short) 0, (short) 0,
                                    Constants.unit_infoLayer, unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid().toUpperCase(),
                                    unitInfoDataModel.getRelative_path(),
                                    mediaModelArrayList.get(i).getObejctId(),
                                    // unitInfoDataModel.getGlobalId().toUpperCase(),
                                    mediaModelArrayList.get(i).getGlobalId().toUpperCase(),
                                    true, new Date(), new Date(), mediaModelArrayList.get(i).getDocument_category(),
                                    mediaModelArrayList.get(i).getDocument_type(), mediaModelArrayList.get(i).getDocument_remarks(),
                                    mediaModelArrayList.get(i).getName_on_document()));
                        }

                        if (unitInfoDataModel.getRespondent_non_available_remark() != null &&
                                unitInfoDataModel.getRespondent_non_available_remark().equalsIgnoreCase("Unit is locked and Notice pasted")) {
                            if (!mediaInfoDataModels.isEmpty()) {
                                if (mediaInfoDataModels.get(0).getFilename().contains("Notice_Pasted")) {
                                    summary.add(new SummaryChildItemModel("Attachments", Utils.getGson().toJson(mediaInfoDataModels), "", false, true));
                                }
                            }
                        }

                        if (!arrayList.isEmpty()) {
                            unitMediaInfoDataModel.put(unitInfoDataModel.getGlobalId().toUpperCase(), arrayList);
                        }
                    } else if (mediaModelArrayList.get(i).getItem_url() != null && !mediaModelArrayList.get(i).getItem_url().equals("")) {
//                      String weburl=Constants.Video_STAGE_URL+mediaModelArrayList.get(i).getItem_url()+"/"+mediaModelArrayList.get(i).getFilename();
//                      mediaInfoDataModels.add(new MediaInfoDataModel(mediaModelArrayList.get(i).getFilename(), weburl));

                        // TODO: Ajay :: Need to Confirm with Rohit Bhai
                        String weburl = Constants.Video_STAGE_URL + mediaModelArrayList.get(i).getItem_url() + "/" + mediaModelArrayList.get(i).getFilename();
                        mediaInfoDataModels.add(new MediaInfoDataModel(mediaModelArrayList.get(i).getFilename(), weburl));

                        arrayList.add(new MediaInfoDataModel(mediaModelArrayList.get(i).getContent_type(),
                                mediaModelArrayList.get(i).getFilename(), (int) mediaModelArrayList.get(i).getData_size(), "",
                                weburl,
                                (short) 0, (short) 0,
                                Constants.unit_infoLayer, unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid().toUpperCase(),
                                unitInfoDataModel.getRelative_path(),
                                mediaModelArrayList.get(i).getObejctId(), unitInfoDataModel.getGlobalId().toUpperCase(), true, new Date(), new Date(), mediaModelArrayList.get(i).getDocument_category(),
                                mediaModelArrayList.get(i).getDocument_type(), mediaModelArrayList.get(i).getDocument_remarks(), mediaModelArrayList.get(i).getName_on_document()));

                        if (unitInfoDataModel.getRespondent_non_available_remark() != null && unitInfoDataModel.getRespondent_non_available_remark().equalsIgnoreCase("Unit is locked and Notice pasted")) {
                            if (mediaInfoDataModels.size() > 0) {
                                if (mediaInfoDataModels.get(0).getFilename().contains("Notice_Pasted")) {
                                    summary.add(new SummaryChildItemModel("Attachments", Utils.getGson().toJson(mediaInfoDataModels), "", false, true));
                                }
                            }
                        }

                        if (arrayList.size() > 0) {
                            unitMediaInfoDataModel.put(unitInfoDataModel.getGlobalId().toUpperCase(), arrayList);
                        }
                    }
                }
            }
            unitAttachmentHashmap = new HashMap<>();
            mediaAttributesArrayList = new ArrayList<>();
            setUpSummaryItem(unitInfoDataModel.getUnit_unique_id(), unitInfoDataModel.getUnit_status(),
                    summary, true, null, null, false);
        } catch (Exception ex) {
            Utils.dismissProgress();
            AppLog.e("hoh Details Unable to get the hoh details.\n Error cause: " + ex.getCause() + "\nError message: " + ex.getMessage());
            Utils.shortToast("Unable to get the hoh details.", activity);
        }
    }

    private String formattedDateToYear(String recivedDate) {
        String displayDate = "";
        try {
            long timestamp = extractTimestamp("" + recivedDate);

            TimeZone timeZone = TimeZone.getTimeZone("IST");
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(timestamp);
            displayDate = String.valueOf(calendar.get(Calendar.YEAR));

        } catch (Exception e) {
            AppLog.logData(activity, e.getMessage());
//            Utils.shortToast("Exception in formattedDate:"+e.getMessage(),activity);
            AppLog.e("Exception in formattedDate:" + e.getMessage());
        }
        return "" + displayDate;
    }

    private static long extractTimestamp(String inputString) {
        int startIndex = inputString.indexOf("time=") + 5;
        int endIndex = inputString.indexOf(",", startIndex);
        String timestampString = inputString.substring(startIndex, endIndex);
        return Long.parseLong(timestampString);
    }

    public void showActionAlertFormLocked() {
        // Create an alert builder
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.form_locked_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        Button btn = customLayout.findViewById(R.id.continueBtn);

        btn.setOnClickListener(view1 -> {
            dialog.dismiss();

        });
        dialog.show();
    }


}
