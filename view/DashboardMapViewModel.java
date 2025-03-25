package com.igenesys.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.igenesys.DashboardMapActivity;
import com.igenesys.R;
import com.igenesys.adapter.FeatureListAdapter;
import com.igenesys.database.dabaseModel.newDbSModels.DownloadedWebMapModel;

import com.igenesys.databinding.ActivityDashboardMapBinding;
import com.igenesys.fragment.ShowIdentifyItemBottomSheetFragment;
import com.igenesys.model.IdentifyItemListModel;
import com.igenesys.model.IndentifyFeatureListModel;
import com.igenesys.model.WorkAreaModel;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.AppPermissions;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;
//import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;
//import com.techaidsolution.gdc_app.App;
//import com.techaidsolution.gdc_app.R;
//import com.techaidsolution.gdc_app.database.dabaseModel.newDbSModels.DownloadedWebMapModel;
//import com.techaidsolution.gdc_app.databinding.ActivityDashboardMapBinding;
//import com.techaidsolution.gdc_app.model.IdentifyItemListModel;
//import com.techaidsolution.gdc_app.model.IndentifyFeatureListModel;
//import com.techaidsolution.gdc_app.model.WorkAreaModel;
//import com.techaidsolution.gdc_app.ui.map.FeatureListAdapter;
//import com.techaidsolution.gdc_app.ui.map.identify_item.ShowIdentifyItemBottomSheetFragment;
//import com.techaidsolution.gdc_app.utils.AppLog;
//import com.techaidsolution.gdc_app.utils.AppPermissions;
//import com.techaidsolution.gdc_app.utils.Constants;
//import com.techaidsolution.gdc_app.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class DashboardMapViewModel extends ActivityViewModel<DashboardMapActivity>
        implements FeatureListAdapter.OnSelectedItemClickListner {

    Activity activity;
    ActivityDashboardMapBinding binding;
    ArcGISMap map;
    MapView mapView;
    GraphicsOverlay selectedWorkAreaGrphicOverlay;
    WorkAreaModel selectedWorkArea;
    Geometry selectedWardAreaGeom;
    String structureInfo_FL_whereClause, selectedCluster, selectedWhereClause, selectedFilter;
    LocationDisplay locationDisplay;
    Point currentLocation;
    boolean identifyChoosed = false;
    HashMap<String, Feature> mapLayerFeature = new HashMap<>();
    HashMap<String, ArrayList<IdentifyItemListModel>> mapLayerAttributes = new HashMap<>();
    ArrayList<String> layerName = new ArrayList<>();
    int inProgressCount = 0, completeCount = 0, notStartedCount = 0, onHoldCount = 0, totalCount = 0;
    ArrayList<String> restrictedField;
    ArrayList<IndentifyFeatureListModel> indentifyFeatureListModelArrayList;
    FeatureLayer selectedFeatureLayer;
    String IntentComingFrom;
    DownloadedWebMapModel selectedDownloadedWebMapModel;
    String viewMode = "";
    boolean progressClicked = false, completedClicked = false, notStartedClicked = false, holdClicked = false;
    private ServiceFeatureTable structureInfo_FT;
    private FeatureLayer workArea_FL, structureInfo_FL;
    private double lng, lat, accu;

    public DashboardMapViewModel(DashboardMapActivity activity) {
        super(activity);
        this.activity = activity;
        this.binding = activity.getBinding();

        binding.appBarMain.cvTopHeader.setVisibility(View.GONE);
//      binding.appBarMain.imgvBackBtn.setVisibility(View.VISIBLE);
        binding.appBarMain.cvAddBtn.setVisibility(View.GONE);
        binding.appBarMain.cvSelectionBtn.setVisibility(View.GONE);

        mapView = binding.appBarMain.mapView;

        selectedWorkAreaGrphicOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(selectedWorkAreaGrphicOverlay);

        map = new ArcGISMap(Basemap.createTopographic());

        binding.appBarMain.txtSelectedWebMap.setText("Online Map");

        if (activity.getIntent().hasExtra(Constants.IntentComingFrom)) {
            IntentComingFrom = activity.getIntent().getStringExtra(Constants.IntentComingFrom);
            if (IntentComingFrom.equalsIgnoreCase("download")) {
                viewMode = "offline";
                initOfflineView();
            } else {
                viewMode = "online";
                initOnlineView();
            }
        } else {
            viewMode = "online";
            initOnlineView();
        }

        initListner();

        restrictedField = new ArrayList<>();
        restrictedField.add("created_user");
        restrictedField.add("globalid");
        restrictedField.add("last_edited_user");
        restrictedField.add("objectid");
    }

    private void initOfflineView() {
try {
    binding.appBarMain.filterLay.setVisibility(View.GONE);

    mapView.addMapScaleChangedListener(mapScaleChangedEvent -> {
//            int a = (int) Math.round(Math.log(591657550.500000 / (mapScaleChangedEvent.getSource().getMapScale() / 2)) / Math.log(2));
        int a = (int) (Math.log(591657550.500000 / (mapScaleChangedEvent.getSource().getMapScale() / 2)) / Math.log(2));

        binding.appBarMain.txtZoomLevel.setText("Zoom Level: " + a);
    });

    locationDisplay = mapView.getLocationDisplay();
    if (!locationDisplay.isStarted())
        locationDisplay.startAsync();

    showCurrentLocation();

    if (activity.getIntent().hasExtra(Constants.DownloadedWebMapModel)) {
        selectedDownloadedWebMapModel = (DownloadedWebMapModel) activity.getIntent().getSerializableExtra(Constants.DownloadedWebMapModel);
        displayOfflineMap(selectedDownloadedWebMapModel.getPathAddress());
        binding.appBarMain.txtSelectedClusterName.setText(selectedDownloadedWebMapModel.getWorkAreaName());
        binding.appBarMain.txtSelectedWebMap.setText("Offline Map");
        binding.commonHeader.txtPageHeader.setText("Preview Basemap - " + selectedDownloadedWebMapModel.getWorkAreaName());
        binding.appBarMain.txtSelectedWebMap.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.offlineBack));
        selectedWardAreaGeom = GeometryEngine.project(Geometry.fromJson(selectedDownloadedWebMapModel.getGeometry()), SpatialReference.create(Constants.SpatialReference));
    }
}catch(Exception ex){
    AppLog.e("Exception in download web map init:"+ex.getMessage());
    AppLog.logData(activity,ex.getMessage());
}
    }

    private void displayOfflineMap(String filePath) {
try{
        Utils.updateProgressMsg("", activity);
        // check if the offline map package file exists
        if (new File(filePath).exists()) {
            // load it as a MobileMapPackage
            MobileMapPackage mapPackage = new MobileMapPackage(filePath);
            mapPackage.loadAsync();
            mapPackage.addDoneLoadingListener(() -> {
                if (mapPackage.getLoadStatus() == LoadStatus.LOADED) {
                    map = mapPackage.getMaps().get(0);
                    mapView.setMap(map);
                    map.addDoneLoadingListener(() -> {
                        try {
                            if (map.getLoadStatus() == LoadStatus.LOADED) {

                                for (Layer operationalLayer : map.getOperationalLayers()) {

                                    if (operationalLayer.getName().equalsIgnoreCase(Constants.workAreaLayerName)) {
                                        workArea_FL = (FeatureLayer) operationalLayer;
                                    } else if (operationalLayer.getName().equalsIgnoreCase(Constants.structureInfoLayerName)) {
                                        structureInfo_FL = (FeatureLayer) operationalLayer;
                                    }
                                }

                                int a = (int) (Math.log(591657550.500000 / (mapView.getMapScale() / 2)) / Math.log(2));

                                binding.appBarMain.txtZoomLevel.setText("Zoom Level: " + a);

                                selectedWorkAreaGrphicOverlay.getGraphics().add(new Graphic(selectedWardAreaGeom, Utils.getSimpleFillLineSymbol(activity)));
                                mapView.setViewpointGeometryAsync(selectedWardAreaGeom, 20);
                                binding.appBarMain.ivCompass.bindTo(mapView, binding.appBarMain.ivCompass);

                                Utils.dismissProgress();

                            } else if (map.getLoadStatus() == LoadStatus.NOT_LOADED) {
                                Utils.dismissProgress();
                                Utils.showMessagePopup("Map not loaded.", activity);
                            } else if (map.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                                Utils.dismissProgress();
                                Utils.showMessagePopup("Failed to load the map. Error :" + map.getLoadError().getCause().getMessage(), activity);
                            } else if (map.getLoadStatus() == LoadStatus.LOADING) {
                                Utils.updateProgressMsg("Loading map.Please wait..", activity);
                                Utils.dismissProgress();
                            }
                        } catch (Exception e) {
                            AppLog.logData(activity,e.getMessage());
                            Utils.dismissProgress();
                            Utils.shortToast("Error in loading Structure Info Layer.", activity);
                        }
                    });
//                    Utils.shortToast("Loaded offline map. Map saved at: " + filePath, activity);
                } else if (mapPackage.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                    Utils.shortToast("Error loading map package: " + mapPackage.getLoadError().getCause(), activity);
                }
            });

        } else {
            Utils.shortToast("Webmap not exists", activity);
            activity.finish();
        }

}catch(Exception ex){
    AppLog.logData(activity,ex.getMessage());
    AppLog.e("Exception in display offline map:"+ex.getMessage());
}
    }

    private void initOnlineView() {
        binding.commonHeader.txtPageHeader.setText("Dashboard");

        binding.appBarMain.filterLay.setVisibility(View.VISIBLE);

        if (activity.getIntent().hasExtra(Constants.INTENT_SelectedWorkArea)) {
            selectedWorkArea = (WorkAreaModel) activity.getIntent().getSerializableExtra(Constants.INTENT_SelectedWorkArea);

            selectedWardAreaGeom = GeometryEngine.project(Geometry.fromJson(selectedWorkArea.getGeometry()), SpatialReference.create(Constants.SpatialReference));

            binding.appBarMain.txtSelectedClusterName.setText(selectedWorkArea.getWork_area_name());
        }

        if (activity.getIntent().hasExtra(Constants.selectedCluster)) {
            selectedCluster = activity.getIntent().getStringExtra(Constants.selectedCluster);
            structureInfo_FL_whereClause = "(cluster_name = '" + selectedCluster + "')";
        }
        if (activity.getIntent().hasExtra(Constants.INTENT_SelectedFilter)) {
            selectedFilter = activity.getIntent().getStringExtra(Constants.INTENT_SelectedFilter);
        }
        if (activity.getIntent().hasExtra(Constants.INTENT_SelectedWorkAreaNsCount)) {
            notStartedCount = activity.getIntent().getIntExtra(Constants.INTENT_SelectedWorkAreaNsCount, 0);
        }
        if (activity.getIntent().hasExtra(Constants.INTENT_SelectedWorkAreaIpCount)) {
            inProgressCount = activity.getIntent().getIntExtra(Constants.INTENT_SelectedWorkAreaIpCount, 0);
        }
        if (activity.getIntent().hasExtra(Constants.INTENT_SelectedWorkAreaC_Count)) {
            completeCount = activity.getIntent().getIntExtra(Constants.INTENT_SelectedWorkAreaC_Count, 0);
        }
        if (activity.getIntent().hasExtra(Constants.INTENT_SelectedWorkAreaOhCount)) {
            onHoldCount = activity.getIntent().getIntExtra(Constants.INTENT_SelectedWorkAreaOhCount, 0);
        }
        if (activity.getIntent().hasExtra(Constants.INTENT_SelectedWhereClause)) {
            selectedWhereClause = activity.getIntent().getStringExtra(Constants.INTENT_SelectedWhereClause);
            structureInfo_FL_whereClause = selectedWhereClause;
        }


        map.addDoneLoadingListener(() -> {
            try {
                if (map.getLoadStatus() == LoadStatus.LOADED) {
                    int a = (int) (Math.log(591657550.500000 / (mapView.getMapScale() / 2)) / Math.log(2));

                    binding.appBarMain.txtZoomLevel.setText("Zoom Level: " + a);

                    selectedWorkAreaGrphicOverlay.getGraphics().add(new Graphic(selectedWardAreaGeom, Utils.getSimpleFillLineSymbol(activity)));
                    mapView.setViewpointGeometryAsync(selectedWardAreaGeom, 20);
                    binding.appBarMain.ivCompass.bindTo(mapView, binding.appBarMain.ivCompass);

                } else if (map.getLoadStatus() == LoadStatus.NOT_LOADED) {
                    Utils.dismissProgress();
                    Utils.showMessagePopup("Map not loaded.", activity);
                } else if (map.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                    Utils.dismissProgress();
                    Utils.showMessagePopup("Failed to load the map. Error :" + map.getLoadError().getCause().getMessage(), activity);
                } else if (map.getLoadStatus() == LoadStatus.LOADING) {
                    Utils.updateProgressMsg("Loading map.Please wait..", activity);
                    Utils.dismissProgress();
                }
            } catch (Exception e) {
                AppLog.logData(activity,e.getMessage());
                Utils.dismissProgress();
                Utils.shortToast("Error in loading Structure Info Layer.", activity);
            }
        });
        mapView.setMap(map);

        structureInfo_FT = new ServiceFeatureTable(Constants.StructureInfo_MS_BASE_URL_ARC_GIS + Constants.StructureInfo_ENDPOINT);

        structureInfo_FT.loadAsync();

        structureInfo_FL = new FeatureLayer(structureInfo_FT);

        structureInfo_FL.loadAsync();

        structureInfo_FL.addDoneLoadingListener(() -> {
            try {
                if (structureInfo_FL.getLoadStatus() == LoadStatus.LOADED) {
                    if (selectedFilter.equalsIgnoreCase(Constants.In_Progress))
                        inProgressSelected();
                    else if (selectedFilter.equalsIgnoreCase(Constants.completed))
                        completedSelected();
                    else if (selectedFilter.equalsIgnoreCase(Constants.Not_Started))
                        notStartedSelected();
                    else if (selectedFilter.equalsIgnoreCase(Constants.On_Hold))
                        onHoldSelected();
                    else
                        structureInfo_FL.setDefinitionExpression(structureInfo_FL_whereClause);
                    mapView.getMap().getOperationalLayers().add(structureInfo_FL);
                    Utils.dismissProgress();
                } else if (structureInfo_FL.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                    Utils.dismissProgress();
                    Utils.showMessagePopup("Error in loading Structure Info Layer. Error: " + structureInfo_FL.getLoadError().getCause().getMessage(), activity);
                } else if (structureInfo_FL.getLoadStatus() == LoadStatus.NOT_LOADED) {
                    Utils.dismissProgress();
                    Utils.shortToast("Structure Info layer not loaded.", activity);
                } else if (map.getLoadStatus() == LoadStatus.LOADING) {
                    Utils.updateProgressMsg("Loading layer.Please wait..", activity);
                    Utils.dismissProgress();
                }
            } catch (Exception e) {
                AppLog.logData(activity,e.getMessage());
                Utils.shortToast("Error in loading Structure Info Layer.", activity);
            }
        });

        mapView.addMapScaleChangedListener(mapScaleChangedEvent -> {
//            int a = (int) Math.round(Math.log(591657550.500000 / (mapScaleChangedEvent.getSource().getMapScale() / 2)) / Math.log(2));
            int a = (int) (Math.log(591657550.500000 / (mapScaleChangedEvent.getSource().getMapScale() / 2)) / Math.log(2));

            binding.appBarMain.txtZoomLevel.setText("Zoom Level: " + a);
        });

        locationDisplay = mapView.getLocationDisplay();
        if (!locationDisplay.isStarted())
            locationDisplay.startAsync();

        showCurrentLocation();
    }

    private void setCardWidth() {
        binding.appBarMain.cvInProgress.setStrokeWidth(0);
        binding.appBarMain.cvCompleted.setStrokeWidth(0);
        binding.appBarMain.cvNotStarted.setStrokeWidth(0);
        binding.appBarMain.cvOnHold.setStrokeWidth(0);

        progressClicked = false;
        completedClicked = false;
        notStartedClicked = false;
        holdClicked = false;

    }

    private void notStartedSelected() {
        setCardWidth();
        binding.appBarMain.cvNotStarted.setStrokeWidth(2);

//            structureInfo_FL_whereClause = structureInfo_FL_whereClause.concat(" AND (structure_status = 'NotStarted')");
        structureInfo_FL.setDefinitionExpression(structureInfo_FL_whereClause.concat(" AND (structure_status = 'Not Started')"));
        structureInfo_FL.loadAsync();
    }

    private void inProgressSelected() {

        setCardWidth();
        binding.appBarMain.cvInProgress.setStrokeWidth(2);

//            structureInfo_FL_whereClause = structureInfo_FL_whereClause.concat(" AND (structure_status = 'InProgress')");
        structureInfo_FL.setDefinitionExpression(structureInfo_FL_whereClause.concat(" AND (structure_status = 'In Progress')"));
        structureInfo_FL.loadAsync();
    }

    private void completedSelected() {
        setCardWidth();
        binding.appBarMain.cvCompleted.setStrokeWidth(2);

//            structureInfo_FL_whereClause = structureInfo_FL_whereClause.concat(" AND (structure_status = 'Completed')");
        structureInfo_FL.setDefinitionExpression(structureInfo_FL_whereClause.concat(" AND (structure_status = 'Completed')"));

        structureInfo_FL.loadAsync();

    }

    private void onHoldSelected() {
        setCardWidth();
        binding.appBarMain.cvOnHold.setStrokeWidth(2);

//            structureInfo_FL_whereClause = structureInfo_FL_whereClause.concat(" AND (structure_status = 'Hold')");
        structureInfo_FL.setDefinitionExpression(structureInfo_FL_whereClause.concat(" AND (structure_status = 'On Hold')"));
        structureInfo_FL.loadAsync();
    }

    private void initListner() {
        binding.appBarMain.txtNotStarted.setText(notStartedCount + " " + binding.appBarMain.txtNotStarted.getText().toString());
        binding.appBarMain.txtInProgress.setText(inProgressCount + " " + binding.appBarMain.txtInProgress.getText().toString());
        binding.appBarMain.txtCompleted.setText(completeCount + " " + binding.appBarMain.txtCompleted.getText().toString());
        binding.appBarMain.txtOnHold.setText(onHoldCount + " " + binding.appBarMain.txtOnHold.getText().toString());

        binding.appBarMain.cvNotStarted.setOnClickListener(view -> {
            if (!notStartedClicked) {
                notStartedSelected();
                notStartedClicked = true;
            } else {
                setCardWidth();
                structureInfo_FL.setDefinitionExpression(structureInfo_FL_whereClause);
                notStartedClicked = false;
            }
        });

        binding.appBarMain.cvInProgress.setOnClickListener(view -> {
            if (!progressClicked) {
                inProgressSelected();
                progressClicked = true;
            } else {
                setCardWidth();
                structureInfo_FL.setDefinitionExpression(structureInfo_FL_whereClause);
                progressClicked = false;
            }
        });

        binding.appBarMain.cvCompleted.setOnClickListener(view -> {
            if (!completedClicked) {
                completedSelected();
                completedClicked = true;
            } else {
                setCardWidth();
                structureInfo_FL.setDefinitionExpression(structureInfo_FL_whereClause);
                completedClicked = false;
            }
        });

        binding.appBarMain.cvOnHold.setOnClickListener(view -> {
            if (!holdClicked) {
                onHoldSelected();
                holdClicked = true;
            } else {
                setCardWidth();
                structureInfo_FL.setDefinitionExpression(structureInfo_FL_whereClause);
                holdClicked = false;
            }
        });

        binding.commonHeader.imgBack.setOnClickListener(view -> activity.finish());

        binding.appBarMain.imgvIBtn.setOnClickListener(view -> {

            binding.appBarMain.page.setVisibility(View.GONE);
            if (!identifyChoosed) {
                identifyChoosed = true;
                binding.appBarMain.imgvIBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_i_img_white_svg));
                binding.appBarMain.imgvIBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.main_color));
            } else {
                clearSelection();
                identifyChoosed = false;
                binding.appBarMain.imgvIBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_i_img_svg));
                binding.appBarMain.imgvIBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            }
//            setUpIdentifyBottomSheet();
        });

        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(activity, mapView) {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                binding.appBarMain.page.setVisibility(View.GONE);

                clearSelection();
                if (identifyChoosed) {
                    Utils.updateProgressMsg("Please wait..", activity);
//                    if (viewMode.equalsIgnoreCase("online")) {
                    final ListenableFuture<List<IdentifyLayerResult>> identifyLayerResultsFuture = mMapView
                            .identifyLayersAsync(screenPoint, 20, false, 50);

                    identifyLayerResultsFuture.addDoneListener(() -> { //Point: [72.853239, 19.046540, 0.000000, NaN] SR: 4326
                        try {
                            List<IdentifyLayerResult> identifyLayerResults = identifyLayerResultsFuture.get();
                            handleIdentifyResults(identifyLayerResults, clickPoint);
                        } catch (InterruptedException | ExecutionException e1) {
                            Utils.dismissProgress();
                            AppLog.e( "Error identifying results: " + e1.getMessage());
                        }
                    });
//                    } else {
//                        Utils.dismissProgress();
//                        Utils.shortToast("Under development.", activity);
//                    }

                }
                return super.onSingleTapConfirmed(e);
            }
        });

        binding.appBarMain.ivCompass.setOnClickListener(v -> {
            mapView.setViewpointRotationAsync(0);
        });

        binding.appBarMain.imgvLocation.setOnClickListener(view -> {
            if (!AppPermissions.checkGpsOn(activity)) {
                Utils.shortToast("GPS is disabled in your device or it may not be on 'High Accuracy'. Please enable it.", activity);
                return;
            }

            if (!locationDisplay.isStarted())
                locationDisplay.startAsync();
            locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        });

        binding.appBarMain.imgvZoomToArea.setOnClickListener(v -> {
            if (selectedWardAreaGeom != null)
                mapView.setViewpointGeometryAsync(selectedWardAreaGeom, 20);
        });

        binding.appBarMain.forwardLay.setOnClickListener(view -> {
            int currentItem = binding.appBarMain.viewPager.getCurrentItem();
            AppLog.d("viewpager_inf"+ String.valueOf(currentItem));
            binding.appBarMain.viewPager.setCurrentItem(currentItem + 1);
        });

        binding.appBarMain.backwardLay.setOnClickListener(view -> {
            int currentItem = binding.appBarMain.viewPager.getCurrentItem();
            AppLog.d("viewpager_inf"+ String.valueOf(currentItem));
            binding.appBarMain.viewPager.setCurrentItem(currentItem - 1);
        });

        binding.appBarMain.viewPager.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    public void onPageScrollStateChanged(int i) {

                    }

                    public void onPageScrolled(int i, float f, int i2) {

                    }

                    public void onPageSelected(int i) {
                        try {
                            clearSelection();
                            if (indentifyFeatureListModelArrayList.get(i).getFeature() != null
                                    && indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry() != null) {
                                try {
                                    if (indentifyFeatureListModelArrayList.get(i).getLayerName().equals(Constants.structureInfoLayerNameProper))
                                        structureInfo_FL.selectFeature(indentifyFeatureListModelArrayList.get(i).getFeature());
                                    else if (indentifyFeatureListModelArrayList.get(i).getLayerName().equals(Constants.workAreaLayerNameProper))
                                        workArea_FL.selectFeature(indentifyFeatureListModelArrayList.get(i).getFeature());
                                    else if (indentifyFeatureListModelArrayList.get(i).getFeatureLayer() != null) {
                                        if (indentifyFeatureListModelArrayList.get(i).getFeatureLayer().getLoadStatus() == LoadStatus.LOADED)
                                            indentifyFeatureListModelArrayList.get(i).getFeatureLayer().selectFeature(indentifyFeatureListModelArrayList.get(i).getFeature());
                                    }

                                    addDot(i);
//                            Point pointGeoma = (Point) GeometryEngine.project(indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry(), SpatialReference.create(Constants.SpatialReference));

//                            mapView.setViewpointGeometryAsync(indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry(), 30);

                                    setViewPointGeom(indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry());
//                            setViewPoint(indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry());

                                } catch (Exception e) {
                                    AppLog.logData(activity,e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception ignored) {
                            AppLog.logData(activity,ignored.getMessage());
                        }
                    }
                });

    }

    private void setViewPointGeom(Geometry geometry) {
        if (geometry != null) {
            Geometry geometry1 = GeometryEngine.project(geometry, SpatialReference.create(Constants.SpatialReference));

            if (geometry1 != null) {
//                binding.appBarMain.mapView.setViewpointAsync(new Viewpoint(geometry1.getExtent()), 1);
                binding.appBarMain.mapView.setViewpointGeometryAsync(geometry1, 30);
            }

        }
    }

    private void handleIdentifyResults(List<IdentifyLayerResult> identifyLayerResults, Point clickPoint) {

        clearSelection();
        ArrayList<IdentifyItemListModel> identifyModels;
        indentifyFeatureListModelArrayList = new ArrayList<>();
        mapLayerAttributes = new HashMap<>();
        layerName = new ArrayList<>();
        mapLayerFeature = new HashMap<>();

        for (IdentifyLayerResult identifyLayerResult : identifyLayerResults) {
            identifyModels = new ArrayList<>();
            Feature selectedFeature = null;

            String headerName = "", header1 = "", header2 = "", header3 = "";
            headerName = identifyLayerResult.getLayerContent().getName();
            // for each result, get the GeoElements
            for (GeoElement identifiedElement : identifyLayerResult.getElements()) {
                identifyModels = new ArrayList<>();
                FeatureLayer featureLayer = null;
                if (identifiedElement instanceof Feature) {

                    Feature feature = (Feature) identifiedElement;
                    featureLayer = ((Feature) identifiedElement).getFeatureTable().getFeatureLayer();
                    selectedFeatureLayer = featureLayer;

                    if (identifyLayerResult.getLayerContent().getName().equals(Constants.workAreaLayerName)) {
                        headerName = Constants.workAreaLayerNameProper;
                    } else if (identifyLayerResult.getLayerContent().getName().equals(Constants.structureInfoLayerName)) {
                        headerName = Constants.structureInfoLayerNameProper;
                    } else {
                        headerName = identifyLayerResult.getLayerContent().getName().replaceAll("_", " ").trim();
                    }

                    layerName.add(headerName);

                    mapLayerFeature.put(headerName, feature);

                    if (identifiedElement.getGeometry() != null)
                        if (!identifiedElement.getGeometry().isEmpty()) {
                            selectedFeature = feature;

                            Map<String, Object> attr = identifiedElement.getAttributes();
                            if (attr != null) {
                                List<Field> fields = ((ArcGISFeature) identifiedElement).getFeatureTable().getFields();
                                Set<String> keys = attr.keySet();

                                if (identifyLayerResult.getLayerContent().getName().equals(Constants.workAreaLayerName)) {
                                    header1 = Utils.getString(attr.get("work_area_name"));
                                    header2 = Utils.getString(attr.get("work_area_status"));
                                    if (attr.get("last_edited_date") != null)
                                        if (attr.get("last_edited_date") instanceof GregorianCalendar) {
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy , HH:mm:ss", Locale.US);
                                            header3 = "Last updated: " + simpleDateFormat.format(((GregorianCalendar) attr.get("last_edited_date")).getTime());
                                        }
                                } else if (identifyLayerResult.getLayerContent().getName().equals(Constants.structureInfoLayerName)) {
                                    header1 = Utils.getString(attr.get("structure_id"));
                                    header2 = Utils.getString(attr.get("structure_status"));
                                    header3 = Utils.getString(attr.get("tenement_number"));
                                } else {
                                    if (attr.containsKey("name"))
                                        header1 = Utils.getString(attr.get("name"));
                                    if (attr.containsKey("ward"))
                                        header2 = Utils.getString(attr.get("ward"));
                                    if (attr.containsKey("ward_name"))
                                        header3 = Utils.getString(attr.get("ward_name"));
                                }
                                for (String key : keys) {
                                    if (!restrictedField.contains(key))
                                        for (Field field : fields) {
                                            if (field.getName().equals(key)) {
                                                Object value = attr.get(key);
                                                String fieldValue = field.getAlias().replaceAll("_", " ").trim();
                                                // append name value pairs to TextView
                                                if (value != null) {
                                                    if (!value.toString().trim().equals("")) {
                                                        if (value instanceof GregorianCalendar) {
                                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy , HH:mm:ss", Locale.US);
                                                            value = simpleDateFormat.format(((GregorianCalendar) value).getTime());
                                                        }
                                                        identifyModels.add(new IdentifyItemListModel(0, fieldValue, value.toString().trim()));
                                                    } else
                                                        identifyModels.add(new IdentifyItemListModel(0, fieldValue, ""));
                                                } else {
                                                    identifyModels.add(new IdentifyItemListModel(0, fieldValue, "N/A"));
                                                }
                                            }
                                        }
                                }
                            }
                        }
                }

                if (selectedFeature != null && identifyModels.size() > 0 && !Utils.isNullOrEmpty(header1))
                    indentifyFeatureListModelArrayList.add(
                            new IndentifyFeatureListModel(headerName, header1, header2, header3, selectedFeature, featureLayer, identifyModels));

            }

            mapLayerAttributes.put(headerName, identifyModels);

//            if (selectedFeature != null && identifyModels.size() > 0 && !Utils.isNullOrEmpty(header1))
//                indentifyFeatureListModelArrayList.add(
//                        new IndentifyFeatureListModel(headerName, header1, header2, header3, selectedFeature, identifyModels));
        }

        Utils.dismissProgress();
//        if (layerName.size() > 0)
//            showAlert(layerName);

        if (indentifyFeatureListModelArrayList.size() > 1)
            setupViewPager(indentifyFeatureListModelArrayList);
        else if (layerName.size() > 0)
            showAlert(layerName);
        else {
            Utils.shortToast("No data found", activity);
        }

    }

    private void showAlert(ArrayList<String> layerName) {
        if (layerName.size() == 1) {
            if (layerName.get(0).equals(Constants.workAreaLayerNameProper) && workArea_FL != null && workArea_FL.getLoadStatus() == LoadStatus.LOADED)
                workArea_FL.selectFeature(mapLayerFeature.get(layerName.get(0)));
            else if (layerName.get(0).equals(Constants.structureInfoLayerNameProper) && structureInfo_FL != null && structureInfo_FL.getLoadStatus() == LoadStatus.LOADED)
                structureInfo_FL.selectFeature(mapLayerFeature.get(layerName.get(0)));

            Feature feature = mapLayerFeature.get(layerName.get(0));
            selectedFeatureLayer.selectFeature(feature);
            setViewPointGeom(feature.getGeometry());
            setUpIdentifyBottomSheet(mapLayerAttributes.get(layerName.get(0)));
        }
//        else {
//            AlertDialog.Builder layerAlert = new AlertDialog.Builder(activity);
//            layerAlert.setTitle("Select Feature");
//            layerAlert.setItems(Utils.GetStringArray(layerName), (dialog, which) -> {
//                String name = layerName.get(which);
//                setUpIdentifyBottomSheet(mapLayerAttributes.get(name));
//
//                if (name.equals(Constants.structureInfoLayerName) && structureInfo_FL != null && structureInfo_FL.getLoadStatus() == LoadStatus.LOADED)
//                    structureInfo_FL.selectFeature(mapLayerFeature.get(name));
//
//            });
//            layerAlert.create().show();
//        }

    }

    private void setUpIdentifyBottomSheet(ArrayList<IdentifyItemListModel> identifyItemListModelList) {

        FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
        ShowIdentifyItemBottomSheetFragment showIdentifyItemBottomSheetFragment = ShowIdentifyItemBottomSheetFragment.geInstance(activity, identifyItemListModelList);
        showIdentifyItemBottomSheetFragment.show(fm, "");
    }

    private void showCurrentLocation() {
        locationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {

            // If LocationDisplay started OK, then continue.
            if (dataSourceStatusChangedEvent.isStarted())
                return;

            // No error is reported, then continue.
            if (dataSourceStatusChangedEvent.getError() == null)
                return;

            AppPermissions.checkPermission(activity);

        });

        if (!locationDisplay.isStarted())
            locationDisplay.startAsync();
        locationDisplay.addLocationChangedListener(locationChangedEvent -> {
            if (locationChangedEvent.getLocation().getPosition() != null) {
                lng = locationChangedEvent.getLocation().getPosition().getX();
                lat = locationChangedEvent.getLocation().getPosition().getY();
                currentLocation = ((Point) GeometryEngine.project(new Point(lng, lat, SpatialReferences.getWgs84()), SpatialReference.create(Constants.SpatialReference)));
                accu = locationChangedEvent.getLocation().getHorizontalAccuracy();
            }
        });
    }

    private void clearSelection() {
        if (workArea_FL != null)
            workArea_FL.clearSelection();

        if (structureInfo_FL != null)
            structureInfo_FL.clearSelection();

        if (selectedFeatureLayer != null)
            selectedFeatureLayer.clearSelection();

        if (indentifyFeatureListModelArrayList != null)
            for (IndentifyFeatureListModel indentifyFeatureListModel : indentifyFeatureListModelArrayList) {
                if (indentifyFeatureListModel.getFeatureLayer() != null) {
                    indentifyFeatureListModel.getFeatureLayer().clearSelection();
                }
            }


    }

    private void setupViewPager(ArrayList<IndentifyFeatureListModel> selectedFeatureLayer) {
        FeatureListAdapter adapter = new FeatureListAdapter(activity, selectedFeatureLayer, this::onItemClick,null);
        binding.appBarMain.viewPager.setAdapter(adapter);
        binding.appBarMain.viewPager.setCurrentItem(0);
        binding.appBarMain.page.setVisibility(View.VISIBLE);

        setViewPager(selectedFeatureLayer);
    }

    public void setViewPager(ArrayList<IndentifyFeatureListModel> selectedFeatureLayer) {

        clearSelection();

        if (selectedFeatureLayer.size() > 0) {
            addDot(0);
            if (selectedFeatureLayer.get(0).getFeature() != null
                    && selectedFeatureLayer.get(0).getFeature().getGeometry() != null) {
                try {
                    if (selectedFeatureLayer.get(0).getLayerName().equals(Constants.structureInfoLayerNameProper))
                        structureInfo_FL.selectFeature(selectedFeatureLayer.get(0).getFeature());
                    else if (selectedFeatureLayer.get(0).getLayerName().equals(Constants.workAreaLayerNameProper))
                        workArea_FL.selectFeature(selectedFeatureLayer.get(0).getFeature());
                    else if (selectedFeatureLayer.get(0).getFeatureLayer() != null) {
                        if (selectedFeatureLayer.get(0).getFeatureLayer().getLoadStatus() == LoadStatus.LOADED)
                            selectedFeatureLayer.get(0).getFeatureLayer().selectFeature(selectedFeatureLayer.get(0).getFeature());
                    }

//                    Point pointGeoma = (Point) GeometryEngine.project(selectedFeatureLayer.get(0).getFeature().getGeometry(), SpatialReference.create(Constants.SpatialReference));

//                  mapView.setViewpointGeometryAsync(selectedFeatureLayer.get(0).getFeature().getGeometry(), 30);

                    setViewPointGeom(selectedFeatureLayer.get(0).getFeature().getGeometry());

//                    setViewPoint(selectedFeatureLayer.get(0).getFeature().getGeometry());

//                    binding.appBarMain.mapView.setViewpointCenterAsync(pointGeoma);
                } catch (Exception e) {
                    AppLog.logData(activity,e.getMessage());
                    e.printStackTrace();
                }
            }
        } else AppLog.d("No records");

    }

    private void addDot(int page_position) {
        TextView[] dot = new TextView[indentifyFeatureListModelArrayList.size()];
        binding.appBarMain.layoutDot.removeAllViews();
        for (int i = 0; i < dot.length; i++) {
            dot[i] = new TextView(activity);
            dot[i].setText(Html.fromHtml("&#9673;"));
            dot[i].setTextSize(20);
            dot[i].setTextColor(activity.getResources().getColor(R.color.gray_text_color));
            binding.appBarMain.layoutDot.addView(dot[i]);
        }
        //active dot
        dot[page_position].setTextColor(activity.getResources().getColor(R.color.main_color));
    }

    @Override
    public void onItemClick(IndentifyFeatureListModel indentifyFeatureListModel) {
        if (Utils.isNullOrEmpty(indentifyFeatureListModel.getGlobalId())) {
            setUpIdentifyBottomSheet(indentifyFeatureListModel.getIdentifyItemListModels());
        }
    }
}
