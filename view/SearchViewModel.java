package com.igenesys.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.SearchActivity;
import com.igenesys.SummaryActivity;
import com.igenesys.adapter.SearchAdapter;
import com.igenesys.databinding.ActivitySearchBinding;
import com.igenesys.model.SearchChildItemModel;
import com.igenesys.model.SearchItemModel;
import com.igenesys.networks.GetFormModel;
import com.igenesys.networks.QueryResultRepoViewModel;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchViewModel extends ActivityViewModel<SearchActivity> implements SearchAdapter.OnItemClickListner {

    Activity activity;
    ActivitySearchBinding binding;
    SearchAdapter adapter;
    QueryResultRepoViewModel queryResultRepoViewModel;
    String gloablId = "";
    private ArrayList<SearchItemModel> searchItemModels;
    private ArrayList<SearchItemModel> searchItemModelsOffline;
    private boolean allowToLoad = true;

    private Map<String, Map<String, String>> searchedStructureXY = new HashMap<>();

    public SearchViewModel(SearchActivity activity, ActivitySearchBinding binding) {
        super(activity);

        this.activity = activity;
        this.binding = binding;

        binding.commonHeader.txtPageHeader.setText("Search");
        binding.commonHeader.imgBack.setOnClickListener(view -> finish());
        initView();

    }

    private void initView() {
        binding.searchbox.clearFocus();
        binding.searchbox.setCursorVisible(true);

        searchItemModels = new ArrayList<>();

        binding.searchlist.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new SearchAdapter(activity, searchItemModels, this);
        binding.searchlist.setAdapter(adapter);
        queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        binding.searchMainDash.setOnClickListener(view -> {
            if (Utils.checkinterne(activity)) {
                performSearch();
                Utils.hideSoftKeyboardd(activity, view);
            }
        });
        binding.searchbox.addTextChangedListener(new TextWatcher() {
            private String userText;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userText = charSequence.toString();
                if (charSequence.equals("")){
    //               adapter.setNewDataList(searchItemModels);
    //                searchDestination("");
                    ArrayList<SearchItemModel> emptyValues = new ArrayList<>();
    //                SearchAdapter searchAdapter = new SearchAdapter(activity, emptyValues, null);
                    adapter.setNewDataList(emptyValues);
                    binding.searchlist.setAdapter(adapter);
                    binding.searchlist.setVisibility(View.GONE);
                    binding.noSearchFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(userText.length() > 2) {
                    searchDestination(userText);
                } else if(userText.length() == 0) {
                    ArrayList<SearchItemModel> emptyValues = new ArrayList<>();
//                    SearchAdapter searchAdapter = new SearchAdapter(activity, emptyValues, null);
                    adapter.setNewDataList(emptyValues);
                    binding.searchlist.setAdapter(adapter);
                    binding.searchlist.setVisibility(View.GONE);
                    binding.noSearchFound.setVisibility(View.VISIBLE);
                }
                allowToLoad = true;
            }
        });

    }

    private void performSearch() {

        String obj = binding.searchbox.getText().toString();
        binding.searchbox.clearFocus();
        Utils.hideKeyboard(activity);
        if (Utils.isNullOrEmpty(obj)) {
            Toast.makeText(activity, activity.getResources().getString(R.string .enter_string_to_search), Toast.LENGTH_SHORT).show();
            return;
        }
        binding.searchView.setVisibility(View.VISIBLE);
        if (Utils.checkinterne(activity)) {
            if (obj.contains("DRP") && obj.length()>10) {
                adapter.setNewDataList(searchItemModels);
                searchDestination(obj);
            } else{
                Utils.hideKeyboard(activity);
                // Toast.makeText(activity, activity.getResources().getString(R.string.no_records_found), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utils.dismissProgress();
        }

    }

    private void hitApiCombineMapQuery(String s) {
        searchItemModels.clear();
        adapter.setDataList(searchItemModels);
//        String structureWhere = "hut_number LIKE '%" + s + "%' or tenement_number LIKE '%" + s + "%' ";
        String structureWhere = "hut_id LIKE '%" + s + "%'" + " AND work_area_name ='" + App.getSharedPreferencesHandler().getString(Constants.workAreaName, "") + "'";
//        String structureWhere = "hut_id LIKE '%" + s + "%'";
        String hohWhere = "hoh_name LIKE '%" + s + "%'";
        String memberWhere = "member_name LIKE '%" + s + "%'";



//        String structureWhere = String.format("ulbcode = '%s'", s);
//        String hohWhere = String.format("ulbcode = '%s'", s);
//        String memberWhere = String.format("distcode = '%s'", s);
//
        queryResultRepoViewModel.initCombineMapQueryResult(Constants.StructureInfo_ENDPOINT, Constants.Hoh_info_ENDPOINT, Constants.MemberInfo_ENDPOINT,
                GetFormModel.getInstance().getQueryBuilderForm(structureWhere, "*", true, false, false),
                GetFormModel.getInstance().getQueryBuilderForm(hohWhere, "*", false, false, false),
                GetFormModel.getInstance().getQueryBuilderForm(memberWhere, "*", false, false, false)
        );

        queryResultRepoViewModel.getCombineMapQueryModelMutableLiveData().observe(getActivity(), combineMapQueryModel -> {

            if (combineMapQueryModel != null && allowToLoad) {
                //ward boundary check once
                if (combineMapQueryModel.getResultQueryStructureModel() != null){
                    if (combineMapQueryModel.getResultQueryStructureModel().getFeatures().size() > 0) {
                        List<Geometry> geometries = new ArrayList<>();
                        String workAreaName = "";
                        try {
                            workAreaName = activity.getIntent().getExtras().getString(Constants.workAreaLayerName);
                        } catch (NullPointerException exception) {
                            AppLog.e(exception.getMessage());
                        }
                        allowToLoad = false;

                        for (int i = 0; i < combineMapQueryModel.getResultQueryStructureModel().getFeatures().size(); i++) {
                            Map<String, Object> mapValue = (Map<String, Object>) combineMapQueryModel.getResultQueryStructureModel().getFeatures().get(i);
                            Map<String, Object> mapAttributeValue = (Map<String, Object>) mapValue.get("attributes");
                            Map<String, Object> mapGeometryValue = (Map<String, Object>) mapValue.get("geometry");
                            /*
                            Kushal
                             */
                            try{
                                String globalIdStructure = mapAttributeValue.get("globalid").toString();
                                Map<String, String> xy = new HashMap<>();
                                String x = mapGeometryValue.get("x").toString();
                                String y = mapGeometryValue.get("y").toString();

                                xy.put("x",x);
                                xy.put("y",y);
                                searchedStructureXY.put(globalIdStructure, xy);
                            }catch (Exception exception){
                                exception.getMessage();
                            }

                            if (mapAttributeValue != null) {
                                if(mapGeometryValue != null) {
                                    Geometry geometry = GeometryEngine.project(Geometry.fromJson(Utils.getGson().toJson(mapGeometryValue)),
                                            SpatialReference.create(Constants.SpatialReference));
                                    geometries.add(geometry);
                                }
                                ArrayList<SearchChildItemModel> searchChildItemModel = new ArrayList<>();
                                SearchChildItemModel searchChildItemModelHutId = new SearchChildItemModel("Hut ID: ", Utils.getString(mapAttributeValue.get("hut_id")));
                                SearchChildItemModel searchChildItemModelWorkArea = new SearchChildItemModel("Work Area: ", Utils.getString(mapAttributeValue.get("work_area_name")));
                                if(searchChildItemModelHutId.getValue().contains(s) && workAreaName.length() > 0 && workAreaName.equals(searchChildItemModelWorkArea.getValue())) {
                                    binding.searchlist.setVisibility(View.VISIBLE);
                                    binding.noSearchFound.setVisibility(View.GONE);
                                    searchChildItemModel.add(searchChildItemModelHutId);
                                    searchChildItemModel.add(searchChildItemModelWorkArea);

                                    searchItemModels.add(new SearchItemModel(Utils.getString(mapAttributeValue.get("tenement_number")),
//                                            Utils.getString(mapAttributeValue.get("structure_id")),
                                            Utils.getString(mapAttributeValue.get("hut_id")),
                                            searchChildItemModel, true, false, false));

                                } else if(searchItemModels.size() == 0) {
                                    binding.searchlist.setVisibility(View.GONE);
                                    binding.noSearchFound.setVisibility(View.VISIBLE);
//                                    Utils.shortToast("No structure info found.", activity);
                                }
//                                searchChildItemModel.add(new SearchChildItemModel("Address: ", Utils.getString(mapAttributeValue.get("address"))));
//                                searchChildItemModel.add(new SearchChildItemModel("Record: ", "Structure Info"));

                            }
                        }
                        adapter.setDataList(searchItemModels);
//                        allowToLoad = true;
                    } else {
                        binding.searchlist.setVisibility(View.GONE);
                        binding.noSearchFound.setVisibility(View.VISIBLE);
                        Utils.shortToast("No structure info found.", activity);
                    }
                }else{
                    Log.d("noData","noData found");
                }

            }
        });
    }

    private void searchDestination(String obj) {
        searchItemModels.clear();
        searchedStructureXY.clear();
        hitApiCombineMapQuery(obj.toUpperCase());

    }

    @Override
    public void onCardClicked(SearchItemModel searchItemModel) {
//        Utils.shortToast("Selected successfully", activity);
        Utils.updateProgressMsg("Getting details. Please wait...", activity);
        String uniqueId = searchItemModel.getUniqueId();
//        if (uniqueId.contains("/")) {
//            String[] uniqueIdArray = uniqueId.split("/");
//            if (uniqueIdArray.length > 1) {
//                uniqueId = uniqueIdArray[1];
//            } else uniqueId = uniqueId.replace("/", "").trim();
//        }

        String endpoint = Constants.StructureInfo_ENDPOINT;
        String whereClause = "hut_id = '" + uniqueId + "'";

//        if (searchItemModel.isHohDetails) {
//            endpoint = Constants.Hoh_info_ENDPOINT;
//            whereClause = "hoh_id = '" + searchItemModel.getUniqueId() + "'";
//        } else if (searchItemModel.isMemberDetails) {
//            endpoint = Constants.MemberInfo_ENDPOINT;
//            whereClause = "member_id = '" + searchItemModel.getUniqueId() + "'";
//        }

        queryResultRepoViewModel.getQueryResult(Constants.StructureInfo_MS_BASE_URL_ARC_GIS,
                endpoint, GetFormModel.getInstance().getQueryBuilderForm(whereClause, "*", false, "", false, false));

        queryResultRepoViewModel.getMutableLiveData().observe(getActivity(), resultQueryModel -> {
            Utils.dismissProgress();
            if (resultQueryModel != null) {
                if (resultQueryModel.getFeatures().size() > 0) {
                    for (int i = 0; i < resultQueryModel.getFeatures().size(); i++) {
                        Map<String, Object> map = (Map<String, Object>) resultQueryModel.getFeatures().get(i);
                        Map<String, Object> mapAttributeValue = (Map<String, Object>) map.get("attributes");
                        Map<String, Object> mapGeometryValue = (Map<String, Object>) map.get("geometry");
                        if (mapAttributeValue != null) {

                            if (mapAttributeValue.get("globalid") != null) {
                                gloablId = Utils.getString(mapAttributeValue.get("globalid"));
                                setStructureUniqueId(mapAttributeValue);
                            }

                        }
                    }

                    if (!Utils.isNullOrEmpty(gloablId)) {
                        gloablId = gloablId.substring(1, gloablId.length() - 1);
                        App.getSharedPreferencesHandler().putString(Constants.uniqueId, gloablId);
                        App.getSharedPreferencesHandler().putString(Constants.viewMode, Constants.online);
//                        if(checkIfUserIsNear()){
                            activity.startActivity(new Intent(activity, SummaryActivity.class)
                                    .putExtra(Constants.viewMode, "searchItem")
                                    .putExtra(Constants.uniqueId, gloablId));
//                        }
                    }


                }
            } else Utils.shortToast("No structure found.", activity);

        });


//        Intent intent = new Intent();
//        intent.putExtra(Constants.INTENT_SelectedStructureId, uniqueId);
//        activity.setResult(RESULT_OK, intent);
//        finish();
    }

    /*
     * This method is invoked when user clicks on searched item in Search screen. To check if user current location is near with 50 meters from the
     * clicked structure location. If it is allow user to open the structure else show error.
     *
     * */
    private boolean checkIfUserIsNear() {
        boolean result = true;
        String globalIdWithCurlyBraces = "{"+gloablId+"}";
        Map<String, String> xy = searchedStructureXY.get(globalIdWithCurlyBraces);
        String x = xy.get("x").toString();
        String y = xy.get("y").toString();
        Geometry geometryFromGSON = Geometry.fromJson("{\"x\":"+x+",\"y\":"+y+"}");
        Geometry geometry = GeometryEngine.project(geometryFromGSON, SpatialReference.create(Constants.SpatialReference));

        double lat = activity.getIntent().getDoubleExtra("lat", 0);
        double lng = activity.getIntent().getDoubleExtra("lng", 0);
        Point currentLocation = ((Point) GeometryEngine.project(new Point(lng, lat, SpatialReferences.getWgs84()), SpatialReference.create(Constants.SpatialReference)));

        try {
            boolean isCurrentLocNearFea = GeometryEngine.contains(Utils.createBufferAroundBoundary(geometry, 50), currentLocation);
            if (isCurrentLocNearFea) {
                App.getSharedPreferencesHandler().putString(Constants.uniqueId, gloablId);
                App.getSharedPreferencesHandler().putString(Constants.viewMode, Constants.online);
                activity.startActivity(new Intent(activity, SummaryActivity.class)
                        .putExtra(Constants.uniqueId, gloablId));
                result = true;
            } else {
                binding.cvErrorView.setVisibility(View.VISIBLE);
                binding.txtErrorView.setText("Sorry, it seems that you are currently located more than 50 meters away from the selected structure point.");
                result = false;
                new Handler().postDelayed(() -> {
                    binding.cvErrorView.setVisibility(View.GONE);
                }, 5000);
            }
        } catch (Exception e) {
            Utils.shortToast("Facing an error please try again.", activity);
        }
        return result;
    }

    private void setStructureUniqueId(Map<String, Object> attr) {
        ArrayList<String> listUniqueId = new ArrayList<>();
        try {
            for (int i = 0; i < 30; i++) {

                String uniqueId;

                if (i < 10) {
                    uniqueId = Utils.getString(attr.get("unit_0" + i));
                } else {
                    uniqueId = Utils.getString(attr.get("unit_" + i));
                }

                // #1357
                if (Utils.isValidateUnitUniqueId(uniqueId))
                    listUniqueId.add(uniqueId);
            }
        } catch (Exception ex) {
            AppLog.e("Exception in setStructureUniqueId:" + ex.getMessage());
            AppLog.logData(activity, ex.getMessage());
        }

        App.getInstance().setListUniqueId(listUniqueId);
    }
}
