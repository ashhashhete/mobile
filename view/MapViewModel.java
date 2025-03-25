package com.igenesys.view;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.esri.arcgisruntime.concurrent.Job;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
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
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.tasks.offlinemap.GenerateOfflineMapJob;
import com.esri.arcgisruntime.tasks.offlinemap.GenerateOfflineMapParameters;
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapTask;
import com.igenesys.App;
import com.igenesys.BuildConfig;
import com.igenesys.CrashReportActivity;
import com.igenesys.DashboardActivity;
import com.igenesys.DownloadActivity;
import com.igenesys.GridStatusActivity;
import com.igenesys.LoginActivity;
import com.igenesys.MapActivity;
import com.igenesys.R;
import com.igenesys.SearchActivity;
import com.igenesys.SummaryActivity;
import com.igenesys.UploadActivity;
import com.igenesys.adapter.FeatureListAdapter;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.DownloadedWebMapModel;
import com.igenesys.databinding.ActivityMapBinding;
import com.igenesys.fragment.ShowIdentifyItemBottomSheetFragment;
import com.igenesys.fragment.ShowTocBottomSheetFragment;
import com.igenesys.model.AfterLoginData;
import com.igenesys.model.AfterLogoutData;
import com.igenesys.model.IdentifyItemListModel;
import com.igenesys.model.IndentifyFeatureListModel;
import com.igenesys.model.TocLegendsListModel;
import com.igenesys.model.UpdateFeatureToLayer;
import com.igenesys.model.UpdateWorkAreaStatusModel;
import com.igenesys.model.UserModel;
import com.igenesys.model.WorkAreaModel;
import com.igenesys.networks.Api_Interface;
import com.igenesys.networks.GetFormModel;
import com.igenesys.networks.QueryResultRepoViewModel;
import com.igenesys.networks.RetrofitService;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.AppPermissions;
import com.igenesys.utils.Constants;
import com.igenesys.utils.EsriAuthUtil;
import com.igenesys.utils.Utils;
import com.igenesys.utils.YesNoBottomSheet;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends ActivityViewModel<MapActivity> implements FeatureListAdapter.OnSelectedItemClickListner {
    private LocationDisplay locationDisplay;
    private WorkAreaModel selectedWorkArea;
    private Geometry selectedWardAreaGeom;
    private GraphicsOverlay selectedWorkAreaGrphicOverlay, pointOverlayFacility;
    private Point currentLocation, markedPoint;
    private String workArea_FL_whereClause, structureInfo_FL_whereClause;
    private UserModel userModel;
    private HashMap<String, Feature> mapLayerFeature = new HashMap<>();
    private HashMap<String, ArrayList<IdentifyItemListModel>> mapLayerAttributes = new HashMap<>();
    private ArrayList<String> layerName = new ArrayList<>();
    private boolean identifyChoosed = false, addNewPoint = false, selectPoint = false;
    private ArrayList<String> restrictedField;
    private ArrayList<IndentifyFeatureListModel> indentifyFeatureListModelArrayList;
    private FeatureLayer selectedFeatureLayer;
    private ProgressDialog offlineProgressDialog;
    private GenerateOfflineMapJob job;
    private int zoomLevel = 0;
    private boolean loadOnline = false;
    private GeodatabaseFeatureTable geodatabaseworkArea_FT;
    private GeodatabaseFeatureTable geodatabasestructureInfo_FT;
    private double lng, lat, accu;
    private ServiceFeatureTable workArea_FT;
    private ServiceFeatureTable structureInfo_FT;
    private FeatureLayer workArea_FL, structureInfo_FL;
    private ActivityMapBinding binding;
    private MapActivity activity;
    private DrawerLayout drawerLayout;
    private MapView mapView;
    private ArcGISMap map;
    private List<IdentifyItemListModel> identifyItemListModelList;
    private List<TocLegendsListModel> tocLegendsListModels;
    private PictureMarkerSymbol mapMarker;
    private SimpleMarkerSymbol simpleMarkerSymbol;

    private LocalSurveyDbViewModel localSurveyDbViewModel;

    public MapViewModel(MapActivity activity) {
        super(activity);
        this.activity = activity;
        this.binding = activity.getBinding();

        drawerLayout = binding.drawerLayout;
        mapView = binding.appBarMain.mapView;

        try {
            userModel = App.getInstance().getUserModel();
            if (userModel != null) {
                initView();
                initListener();
            } else {
                Utils.shortToast("User details not found. Kindly login again.", activity);
                activity.finishAffinity();
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        } catch (Exception e) {
            Utils.shortToast("User details not found. Kindly login again.", activity);
            AppLog.logData(activity, "User details not found. Kindly login again." + e.getMessage());
            activity.finishAffinity();
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (SurveyListPageViewModel.loadMap) {
//                Utils.shortToast(SurveyListPageViewModel.loadMap+Constants.INTENT_LoadMap,activity);
                loadMap();
                SurveyListPageViewModel.loadMap = false;
            }
        } catch (Exception e) {
            AppLog.logData(activity, e.getMessage());
            Utils.shortToast(e.toString(), activity);
        }

        mapView.resume();
        addNewPoint = false;
        pointOverlayFacility.getGraphics().clear();
        binding.appBarMain.cvConfrmBtn.setVisibility(View.GONE);
        binding.appBarMain.imgvAddBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_add_svg));
        binding.appBarMain.imgvAddBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));

        if (selectedWardAreaGeom != null)
            mapView.setViewpointGeometryAsync(selectedWardAreaGeom, 40);

    }

    private void initView() {

        Utils.updateProgressMsg("Loading map. Please wait..", activity);

        offlineProgressDialog = new ProgressDialog(activity);
        offlineProgressDialog.setTitle("Downloading..");
        offlineProgressDialog.setMessage("\nPlease ensure a high-speed internet connection.\nIn case of failure, try downloading again.\nAfter working and uploading record, Please re-download offline map to see and work on updated records!");
        offlineProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        offlineProgressDialog.setIndeterminate(false);
        offlineProgressDialog.setProgress(0);
        offlineProgressDialog.setCancelable(false);
//        localSurveyDbViewModel = ViewModelProviders.of(getActivity()).get(LocalSurveyDbViewModel.class);
        localSurveyDbViewModel = new ViewModelProvider(getActivity()).get(LocalSurveyDbViewModel.class);

        mapMarker = Utils.setSymbols(activity);
        selectedWorkAreaGrphicOverlay = new GraphicsOverlay();
        try {
            mapView.getGraphicsOverlays().add(selectedWorkAreaGrphicOverlay);
        } catch (Exception exception) {
            AppLog.e(exception.getMessage());
        }

        pointOverlayFacility = new GraphicsOverlay();
        try {
            mapView.getGraphicsOverlays().add(pointOverlayFacility);
        } catch (Exception exception) {
            AppLog.e(exception.getMessage());
        }

        if (activity.getIntent().hasExtra(Constants.INTENT_SelectedWorkArea)) {
            selectedWorkArea = (WorkAreaModel) activity.getIntent().getSerializableExtra(Constants.INTENT_SelectedWorkArea);

            selectedWardAreaGeom = GeometryEngine.project(Geometry.fromJson(selectedWorkArea.getGeometry()), SpatialReference.create(Constants.SpatialReference));

            binding.customNavDra.tvName2.setText(userModel.getUser_name());
            binding.appBarMain.txtSelectedClusterName.setText(selectedWorkArea.getWork_area_name());

        } else {
            activity.finish();
            return;
        }

        if (selectedWorkArea == null)
            activity.finish();

        binding.appBarMain.page.setVisibility(View.GONE);

        if (Utils.isConnected(activity) && !App.getSharedPreferencesHandler().getBoolean(Constants.isMapLoadOffline)) {
            loadOnline = true;
            structureInfo_FL_whereClause = "work_area_name  = '" + selectedWorkArea.getWork_area_name() + "'";
            workArea_FL_whereClause = String.format("user_name = '%s' AND work_area_name = '%s' AND (work_area_status = '%s' OR work_area_status = '%s' OR work_area_status = '%s')",
                    userModel.getUser_name(), selectedWorkArea.getWork_area_name(), Constants.NotStarted_statusLayer, Constants.InProgress_statusLayer, Constants.OnHold_statusLayer);
            showOnline();
            binding.appBarMain.txtSelectedWebMap.setText("Online Map");
            binding.appBarMain.txtSelectedWebMap.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.main_color));
        } else {
            binding.appBarMain.txtSelectedWebMap.setText("Offline Map");
            binding.appBarMain.txtSelectedWebMap.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.offlineBack));
            loadOnline = false;
            showOffline();
        }
    }

    private void showOffline() {
        try {
            if (new File(selectedWorkArea.getWork_area_status()).exists()) {
                Utils.updateProgressMsg("Preparing offline map package. Please wait..", activity);
                MobileMapPackage mobileMapPackage = new MobileMapPackage(selectedWorkArea.getWork_area_status());

                mobileMapPackage.loadAsync();

                new Handler().postDelayed(() -> {
                    if (mobileMapPackage.getLoadStatus() == LoadStatus.NOT_LOADED) {
                        Utils.updateProgressMsg("Re trying to load offline map package. Please wait..", activity);
                        mobileMapPackage.retryLoadAsync();
                    }
                }, 5000);

                mobileMapPackage.addDoneLoadingListener(() -> {
                    if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADED) {
//                  binding.appBarMain.mapView.setMap(mobileMapPackage.getMaps().get(0));
                        Utils.updateProgressMsg("Loading offline map. Please wait..", activity);
                        map = mobileMapPackage.getMaps().get(0);
                        map.loadAsync();

                        map.addDoneLoadingListener(() -> {
                            if (map.getLoadStatus() == LoadStatus.LOADED) {
                                for (FeatureTable featureTable : map.getTables()) {

                                    AppLog.e("Map Tables: " + featureTable.getTableName());
                                    if (featureTable.getTableName().equalsIgnoreCase(Constants.hoh_infoLayer)) {
                                        GeodatabaseFeatureTable geodatabaseFeatureTable = (GeodatabaseFeatureTable) featureTable;
                                        geodatabaseFeatureTable.loadAsync();
                                        App.getInstance().setHohGFT(geodatabaseFeatureTable);
                                    } else if (featureTable.getTableName().equalsIgnoreCase(Constants.unit_infoLayer)) {
                                        GeodatabaseFeatureTable geodatabaseFeatureTable = (GeodatabaseFeatureTable) featureTable;
                                        geodatabaseFeatureTable.loadAsync();
                                        App.getInstance().setUnitGFT(geodatabaseFeatureTable);
                                    } else if (featureTable.getTableName().equalsIgnoreCase(Constants.member_infoLayer)) {
                                        GeodatabaseFeatureTable geodatabaseFeatureTable = (GeodatabaseFeatureTable) featureTable;
                                        geodatabaseFeatureTable.loadAsync();
                                        App.getInstance().setMemberGFT(geodatabaseFeatureTable);
                                    } else if (featureTable.getTableName().equalsIgnoreCase(Constants.media_infoLayer)) {
                                        GeodatabaseFeatureTable geodatabaseFeatureTable = (GeodatabaseFeatureTable) featureTable;
                                        geodatabaseFeatureTable.loadAsync();
                                        App.getInstance().setMediaGFT(geodatabaseFeatureTable);
                                    } else if (featureTable.getTableName().equalsIgnoreCase(Constants.media_detailLayer)) {
                                        GeodatabaseFeatureTable geodatabaseFeatureTable = (GeodatabaseFeatureTable) featureTable;
                                        geodatabaseFeatureTable.loadAsync();
                                        App.getInstance().setMediaDetailsGFT(geodatabaseFeatureTable);
                                    }
                                }

                                for (Layer operationalLayer : map.getOperationalLayers()) {
                                    if (App.getSharedPreferencesHandler().containKey(operationalLayer.getName())) {
                                        operationalLayer.setVisible(App.getSharedPreferencesHandler().getBoolean(operationalLayer.getName()));
                                    }
                                    AppLog.e("Map Layers: " + operationalLayer.getName());
                                    if (operationalLayer.getName().equalsIgnoreCase(Constants.workAreaLayerName)) {
                                        workArea_FL = (FeatureLayer) operationalLayer;
                                        geodatabaseworkArea_FT = (GeodatabaseFeatureTable) workArea_FL.getFeatureTable();
                                        geodatabaseworkArea_FT.loadAsync();
                                        App.getInstance().setWorkAreaGFt(geodatabaseworkArea_FT);
                                    } else if (operationalLayer.getName().equalsIgnoreCase(Constants.hutPolygonsLayerName)) {
                                        structureInfo_FL = (FeatureLayer) operationalLayer;
                                        geodatabasestructureInfo_FT = (GeodatabaseFeatureTable) structureInfo_FL.getFeatureTable();
                                        geodatabasestructureInfo_FT.loadAsync();
                                        App.getInstance().setStructureGFT(geodatabasestructureInfo_FT);
                                    }
                                }
                                mapLoading();
                            } else if (map.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                                String error = "Online map failed to load. Error: " + map.getLoadError().getCause();
                                Utils.showMessagePopup(error, activity);
                                Utils.dismissProgress();
                            } else if (map.getLoadStatus() == LoadStatus.NOT_LOADED) {
                                Utils.showMessagePopup("Online map not loaded.", activity);
                                Utils.dismissProgress();
                            } else if (map.getLoadStatus() == LoadStatus.LOADING) {
                                Utils.updateProgressMsg("Loading online map. Please wait..", activity);
                            }
                        });
                    } else if (mobileMapPackage.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                        String error = "Offline map package failed to load. Error: " + map.getLoadError().getCause();
                        Utils.showMessagePopup(error, activity);
                        Utils.dismissProgress();
                    } else if (mobileMapPackage.getLoadStatus() == LoadStatus.NOT_LOADED) {
                        Utils.showMessagePopup("Offline map package not loaded.", activity);
                        Utils.dismissProgress();
                    } else if (mobileMapPackage.getLoadStatus() == LoadStatus.LOADING) {
                        Utils.updateProgressMsg("Loading offline map package. Please wait..", activity);
                    }
                });
            } else {
                Utils.showMessagePopup("Offline map package not available.", activity);
            }
        } catch (Exception ex) {
            AppLog.e("Exception :" + ex.getMessage());
        }
    }

    private void showOnline() {

        try {

            if (!EsriAuthUtil.init(activity))
                Utils.shortToast("Authentication Error", activity);

            Portal portal = App.getInstance().getPortal();

            // Portal portal = new Portal(Constants.getPortalUrl, false);
            // portal.setCredential(new UserCredential(Constants.getPortalUserName, Constants.getPortalPassword));

            //------------------------
            // approch 1
            // com.esri.arcgisruntime.ArcGISRuntimeEnvironment.setApiKey("AAPK5e981df69ee8497294f8ba51d327311dpQEwqx5nIpg5XACoRMNLLzANUDTpRWMxjf8lYr8T3a6iveOezkF9G-WB_lYGDO5l");

            // approch 2----
            // DefaultAuthenticationChallengeHandler defaultAuthenticationChallengeHandler = new DefaultAuthenticationChallengeHandler(activity);
            // use the DefaultChallengeHandler to handle authentication challenges
            // AuthenticationManager.setAuthenticationChallengeHandler(defaultAuthenticationChallengeHandler);

            //-----------------------

            Utils.updateProgressMsg("Preparing online portal. Please wait..", activity);

            portal.loadAsync();
            portal.addDoneLoadingListener(() -> {

                if (portal.getLoadStatus() == LoadStatus.LOADED) {

                    PortalItem portalItem = new PortalItem(portal, Constants.getPortalItemId);
                    map = new ArcGISMap(portalItem);
                    map.loadAsync();
                    Utils.updateProgressMsg("Loading online map. Please wait..", activity);

                    //---------------------------

                    // OAuthConfiguration oAuthConfiguration = null;
                    // try {
                    //     oAuthConfiguration = new OAuthConfiguration(
                    //             Constants.getPortalUrl, Constants.clientId,
                    //             Constants.redirectUri + "://" + Constants.redirectHost
                    //     );
                    // } catch (MalformedURLException e) {
                    //     throw new RuntimeException(e);
                    // }

                    // setup AuthenticationManager to handle auth challenges
                    // DefaultAuthenticationChallengeHandler defaultAuthenticationChallengeHandler = new DefaultAuthenticationChallengeHandler(activity);

                    // use the DefaultChallengeHandler to handle authentication challenges
                    // AuthenticationManager.setAuthenticationChallengeHandler(defaultAuthenticationChallengeHandler);

                    // add an OAuth configuration
                    // NOTE: you must add the DefaultOAuthIntentReceiver Activity to the app's manifest to handle starting a browser
                    // AuthenticationManager.addOAuthConfiguration(oAuthConfiguration);
                    // ----------------------------

                    map.addDoneLoadingListener(() -> {
                        if (map.getLoadStatus() == LoadStatus.LOADED) {

                            App.getInstance().setHohFT(App.getInstance().getHohInfoFT_MS());
                            App.getInstance().setUnitFT(App.getInstance().getUnitInfoFT_FS());
                            App.getInstance().setMemberFT(App.getInstance().getMemberInfoFT_MS());
                            App.getInstance().setMediaFT(App.getInstance().getMediaInfoFT_FS());
                            App.getInstance().setMediaDetailsFT(App.getInstance().getMediaDetailsFT_FS());
                            App.getInstance().setWorkAreaGFt(App.getInstance().getWorkAreaGFt());

                            for (Layer operationalLayer : map.getOperationalLayers()) {
                                if (App.getSharedPreferencesHandler().containKey(operationalLayer.getName())) {
                                    operationalLayer.setVisible(App.getSharedPreferencesHandler().getBoolean(operationalLayer.getName()));
                                }
                                if (operationalLayer.getName().equalsIgnoreCase(Constants.workAreaLayerName)) {
                                    workArea_FL = (FeatureLayer) operationalLayer;
                                    workArea_FT = (ServiceFeatureTable) workArea_FL.getFeatureTable();
                                    workArea_FT.setDefinitionExpression(workArea_FL_whereClause);
                                    workArea_FT.loadAsync();
                                    App.getInstance().setWorkAreaFt(workArea_FT);
                                } else if (operationalLayer.getName().equalsIgnoreCase(Constants.hutPolygonsLayerName)) {
                                    structureInfo_FL = (FeatureLayer) operationalLayer;
                                    structureInfo_FT = (ServiceFeatureTable) structureInfo_FL.getFeatureTable();
                                    structureInfo_FT.setDefinitionExpression(structureInfo_FL_whereClause);
                                    structureInfo_FT.loadAsync();
                                    App.getInstance().setStructureFT(structureInfo_FT);
                                } else if (operationalLayer.getName().equalsIgnoreCase(Constants.hutPolygonLayerName)) {
                                    structureInfo_FL = (FeatureLayer) operationalLayer;
                                    structureInfo_FT = (ServiceFeatureTable) structureInfo_FL.getFeatureTable();
                                    structureInfo_FT.setDefinitionExpression(structureInfo_FL_whereClause);
                                    structureInfo_FT.loadAsync();
                                    App.getInstance().setStructureFT(structureInfo_FT);
                                }
                            }
                            mapLoading();
                        } else if (map.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                            //String error = "Online map failed to load. Error: " + map.getLoadError().getCause();
                            Utils.showMessagePopup("Online map failed to load. Error: " + map.getLoadError().getCause(), activity);
                            Utils.dismissProgress();
                        } else if (map.getLoadStatus() == LoadStatus.NOT_LOADED) {
                            Utils.showMessagePopup("Online map not loaded.", activity);
                            Utils.dismissProgress();
                        } else if (map.getLoadStatus() == LoadStatus.LOADING) {
                            Utils.updateProgressMsg("Loading online map. Please wait..", activity);
                        }
                    });
                } else if (portal.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                    // String error = "Online portal failed to load. Please try again.";
                    Utils.showMessagePopup("Online portal failed to load. Error: " + portal.getLoadError().getCause() + " Please try again. ", activity);
                    Utils.dismissProgress();
                } else if (map.getLoadStatus() == LoadStatus.NOT_LOADED) {
                    Utils.dismissProgress();
                    Utils.showMessagePopup("Online portal not loaded.", activity);
                } else if (map.getLoadStatus() == LoadStatus.LOADING) {
                    Utils.updateProgressMsg("Loading online portal. Please wait..", activity);
                }
            });
        } catch (Exception ex) {
            AppLog.e("Exception :" + ex.getMessage());
        }
    }

    private void mapLoading() {
        map.addDoneLoadingListener(() -> {
            try {
                if (map.getLoadStatus() == LoadStatus.LOADED) {
                    zoomLevel = (int) (Math.log(591657550.500000 / (mapView.getMapScale() / 2)) / Math.log(2));

                    binding.appBarMain.txtZoomLevel.setText("Zoom Level: " + zoomLevel);

                    if (selectedWardAreaGeom != null) {
                        selectedWorkAreaGrphicOverlay.getGraphics().clear();
                        selectedWorkAreaGrphicOverlay.getGraphics().add(new Graphic(selectedWardAreaGeom, Utils.getSimpleFillLineSymbol(activity)));
                    }
                    binding.appBarMain.ivCompass.bindTo(mapView, binding.appBarMain.ivCompass);
                    binding.appBarMain.mapView.setMap(map);
                    mapView.setViewpointGeometryAsync(selectedWardAreaGeom, 30);
                    Utils.dismissProgress();
                    // fetchData(App.getmInstance().getStructureFT());
                } else if (map.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                    // String error = "Online map failed to load. Error: " + map.getLoadError().getCause();
                    Utils.showMessagePopup("Online map failed to load. Error: " + map.getLoadError().getCause(), activity);
                    Utils.dismissProgress();
                } else if (map.getLoadStatus() == LoadStatus.NOT_LOADED) {
                    Utils.showMessagePopup("Online map not loaded.", activity);
                    Utils.dismissProgress();
                } else if (map.getLoadStatus() == LoadStatus.LOADING) {
                    Utils.updateProgressMsg("Loading online map. Please wait..", activity);
                }
            } catch (Exception e) {
                AppLog.logData(activity, e.getMessage());
                Utils.showMessagePopup("Error in loading map. Error: " + e.getCause(), activity);
                Utils.dismissProgress();
            }
        });

        mapView.addMapScaleChangedListener(mapScaleChangedEvent -> {
            zoomLevel = (int) (Math.log(591657550.500000 / (mapScaleChangedEvent.getSource().getMapScale() / 2)) / Math.log(2));
            binding.appBarMain.txtZoomLevel.setText(String.format("Zoom Level: %d", zoomLevel));
            AppLog.d("Map Scale:: " + mapView.getMapScale() + " Zoom::" + zoomLevel);
        });

        try {
            locationDisplay = mapView.getLocationDisplay();
            if (locationDisplay != null && !locationDisplay.isStarted())
                locationDisplay.startAsync();

            showCurrentLocation();
        }catch (Exception ex){
            ex.getMessage();
        }
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

        if (locationDisplay != null && !locationDisplay.isStarted())
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

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {

        restrictedField = new ArrayList<>();
        restrictedField.add("created_user");
        restrictedField.add("globalid");
        restrictedField.add("last_edited_user");
        restrictedField.add("objectid");
        restrictedField.add("st_area(shape)");
        restrictedField.add("st_length(shape)");
        restrictedField.add("Shape__Area");
        restrictedField.add("Shape__Length");

        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(activity, mapView) {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                try {

                    android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
                    Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                    binding.appBarMain.page.setVisibility(View.GONE);

                    // clickPoint != null added for Report_150424_115859
                    if (clickPoint != null && map.getLoadStatus().equals(LoadStatus.LOADED)) {

                        Point point1 = ((Point) GeometryEngine.project(clickPoint, SpatialReferences.getWgs84()));

                        if (point1 != null) {

                            Point newPoint = (Point) GeometryEngine.project(point1, SpatialReference.create(Constants.SpatialReference));

                            if (newPoint != null) {

                                if (currentLocation == null) {
                                    if (!AppPermissions.checkGpsOn(activity)) {
                                        Utils.shortToast("GPS is disabled in your device or it may not be on 'High Accuracy'. Please enable it.", activity);
                                    } else {
                                        Utils.shortToast("Please wait while we retrieve your current location.", activity);
                                        if (locationDisplay != null && !locationDisplay.isStarted())
                                            locationDisplay.startAsync();
                                    }

                                } else {
                                    boolean isPointInsideWa = GeometryEngine.contains(selectedWardAreaGeom, newPoint);
                                    boolean isCurrentLocNearClickPoint = GeometryEngine.contains(Utils.createBufferAroundBoundary(currentLocation, 50), newPoint);

                                    clearSelection();
                                    if (identifyChoosed) {
                                        Utils.updateProgressMsg("Please wait..", activity);
                                        final ListenableFuture<List<IdentifyLayerResult>> identifyLayerResultsFuture = mMapView
                                                .identifyLayersAsync(screenPoint, 20, false, 50);

                                        identifyLayerResultsFuture.addDoneListener(() -> { //Point: [72.853239, 19.046540, 0.000000, NaN] SR: 4326
                                            try {
                                                List<IdentifyLayerResult> identifyLayerResults = identifyLayerResultsFuture.get();
                                                handleIdentifyResults(identifyLayerResults, clickPoint);
                                            } catch (InterruptedException | ExecutionException |
                                                     CancellationException e1) {
                                                AppLog.logData(activity, e1.getMessage());
                                                Utils.dismissProgress();
                                                Log.e("error", "Error identifying results: " + e1.getMessage());
                                            }
                                        });
                                    }
                                    if (selectPoint) {
                                        Utils.updateProgressMsg("Please wait..", activity);
                                        final ListenableFuture<List<IdentifyLayerResult>> identifyLayerResultsFuture = mMapView
                                                .identifyLayersAsync(screenPoint, 20, false, 50);

                                        identifyLayerResultsFuture.addDoneListener(() -> { //Point: [72.853239, 19.046540, 0.000000, NaN] SR: 4326
                                            try {
                                                List<IdentifyLayerResult> identifyLayerResults = identifyLayerResultsFuture.get();
                                                handleIdentifySelectionResults(identifyLayerResults, clickPoint);
                                            } catch (InterruptedException | ExecutionException e1) {
                                                AppLog.logData(activity, e1.getMessage());
                                                Utils.dismissProgress();
                                                AppLog.e("Error identifying results: " + e1.getMessage());
                                            }
                                        });
                                    }
                                    if (addNewPoint) {
                                        pointOverlayFacility.getGraphics().clear();

                                        markedPoint = point1;
                                        binding.appBarMain.cvConfrmBtn.setVisibility(View.VISIBLE);
                                        pointOverlayFacility.getGraphics().add(new Graphic(point1, mapMarker));
                                    }
                                }
                            } else {
                                Utils.shortToast("Map not loaded. Please wait..", activity);
                            }
                        } else {
                            Utils.shortToast("Map not loaded. Please wait..", activity);
                        }
                    } else {
                        Utils.shortToast("Map not loaded. Please wait..", activity);
                    }
                } catch (Exception ex) {
                    AppLog.e(ex.getMessage());
                }

                return super.onSingleTapConfirmed(e);
            }
        });

        binding.customNavDra.imgCloseNavBtn.setOnClickListener(view -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            loadMap();
        });

        binding.customNavDra.sideNavDashboard.setOnClickListener(view -> {

            if (Utils.isConnected(activity)) {
                try {
                    activity.startActivity(new Intent(activity, DashboardActivity.class));
                } catch (Exception exception) {
                    AppLog.e(exception.getMessage());
                }
            } else Utils.shortToast("Unable to proceed. You're currently offline.", activity);
        });

        binding.appBarMain.autoSearchBox.setOnClickListener(view -> {
            if (Utils.isConnected(activity)) {
                Intent intentSearchScreen = new Intent(activity, SearchActivity.class);
                intentSearchScreen.putExtra(Constants.workAreaLayerName, selectedWorkArea.getWork_area_name());
                intentSearchScreen.putExtra("lat", lat);
                intentSearchScreen.putExtra("lng", lng);
                App.getSharedPreferencesHandler().putDoubString("lat",lat);
                App.getSharedPreferencesHandler().putDoubString("lon",lng);
                activity.startActivityForResult(intentSearchScreen, Constants.activityForSearch);
            } else Utils.shortToast("Unable to proceed. You're currently offline.", activity);
        });

        binding.customNavDra.sideNavGridStatus.setOnClickListener(view -> {
            if (Utils.isConnected(activity))
                activity.startActivityForResult(new Intent(activity, GridStatusActivity.class), Constants.activityForChangeClusterStatus);

            else Utils.shortToast("Unable to proceed. You're currently offline.", activity);

        });

        binding.customNavDra.sideNavUploadData.setOnClickListener(view -> {
            // previously SurveyLocalList
            try {
                activity.startActivity(new Intent(activity, UploadActivity.class));
                // activity.startActivity(new Intent(activity, VideoRecorderActivity.class));
            } catch (Exception exception) {
                AppLog.e(exception.getMessage());
            }
        });

        binding.customNavDra.sideNavDownloadOfflineBasemap.setOnClickListener(view -> {
            // Utils.shortToast("Under development.", activity);
            drawerLayout.closeDrawer(GravityCompat.START);
            if (selectedWardAreaGeom != null) {
                mapView.setViewpointGeometryAsync(selectedWardAreaGeom, 30);
                showActionAlertDialogButtonClicked("Download Offline Map", "Would you like to download the selected work area '" +
                        selectedWorkArea.getWork_area_name() + "' or view the already downloaded map list?", "Download", "View List");

            } else {
                Utils.shortToast("Error in downloading selected work area.", activity);
            }
            // downloadNewMap(selectedWorkArea.getWork_area_name());
        });

        binding.customNavDra.sideNavCrashReport.setOnClickListener(view -> {
            // Utils.shortToast("Under development.", activity);
            try {
                activity.startActivity(new Intent(activity, CrashReportActivity.class));
            } catch (Exception exception) {
                AppLog.e(exception.getMessage());
            }
        });

        binding.customNavDra.sideNavLogout.setOnClickListener(view -> logOut());

        binding.customNavDra.sideNavShareLog.setOnClickListener(view -> {

            try {

                String filePath = Utils.generateZipFile(activity);

                if (!filePath.isEmpty()) {
                    Toast.makeText(activity, "Zip file generated successfully", Toast.LENGTH_SHORT).show();
                    try {
                        File outputFile = new File(filePath);
                        // Uri uri = Uri.fromFile(outputFile);
                        Uri uri = FileProvider.getUriForFile(activity,
                                BuildConfig.APPLICATION_ID + ".provider", outputFile);
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("application/zip");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        share.setPackage("com.whatsapp");
                        activity.startActivity(share);
                    } catch (Exception ex) {
                        Toast.makeText(activity, "Zip file generated successfully, Whatsapp is not found on device.", Toast.LENGTH_SHORT).show();
                        AppLog.e("Whatsapp is not found on device");
                    }
                } else {
                    Toast.makeText(activity, "Zip not generated. Please Try Again!!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(activity, "Exception in share log: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                AppLog.e("Exception in share log:" + ex.getMessage());
            }
        });

        binding.appBarMain.ivCompass.setOnClickListener(v -> mapView.setViewpointRotationAsync(0));

        binding.appBarMain.imgvSideNav.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        binding.appBarMain.imgvSelectionBtn.setOnClickListener(view -> {
            clearSelection();
            binding.appBarMain.page.setVisibility(View.GONE);
            if (!selectPoint) {
                selectPoint = true;
                binding.appBarMain.imgvSelectionBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_selection_white_icon_svg));
                binding.appBarMain.imgvSelectionBtn.getBackground().setColorFilter(activity.getColor(R.color.main_color), PorterDuff.Mode.SRC_ATOP);
                addNewPoint = false;
                identifyChoosed = false;
                pointOverlayFacility.getGraphics().clear();
                binding.appBarMain.cvConfrmBtn.setVisibility(View.GONE);
                binding.appBarMain.imgvAddBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_add_svg));
                binding.appBarMain.imgvAddBtn.getBackground().setColorFilter(activity.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                binding.appBarMain.imgvIBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_i_img_svg));
                binding.appBarMain.imgvIBtn.getBackground().setColorFilter(activity.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            } else {
                clearSelection();
                selectPoint = false;
                binding.appBarMain.imgvSelectionBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_selection_icon_svg));
                binding.appBarMain.imgvSelectionBtn.getBackground().setColorFilter(activity.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        });

        binding.appBarMain.imgvIBtn.setOnClickListener(view -> {
            clearSelection();
            binding.appBarMain.page.setVisibility(View.GONE);
            if (!identifyChoosed) {
                identifyChoosed = true;
                binding.appBarMain.imgvIBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_i_img_white_svg));
//                binding.appBarMain.imgvIBtn.setBackground(getActivity().getResources().getColor(R.color.main_color));
                binding.appBarMain.imgvIBtn.getBackground().setColorFilter(activity.getColor(R.color.main_color), PorterDuff.Mode.SRC_ATOP);

                addNewPoint = false;
                selectPoint = false;
                pointOverlayFacility.getGraphics().clear();
                binding.appBarMain.cvConfrmBtn.setVisibility(View.GONE);
                binding.appBarMain.imgvAddBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_add_svg));
                binding.appBarMain.imgvAddBtn.getBackground().setColorFilter(activity.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                binding.appBarMain.imgvSelectionBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_selection_icon_svg));
                binding.appBarMain.imgvSelectionBtn.getBackground().setColorFilter(activity.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            } else {
                clearSelection();
                identifyChoosed = false;
                binding.appBarMain.imgvIBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_i_img_svg));
                binding.appBarMain.imgvIBtn.getBackground().setColorFilter(activity.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
            // setUpIdentifyBottomSheet();
        });

        binding.appBarMain.imgvAddBtn.setOnClickListener(view -> {
            clearSelection();
            binding.appBarMain.page.setVisibility(View.GONE);
            if (!addNewPoint) {
                addNewPoint = true;
                binding.appBarMain.imgvAddBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_add_white_svg));
                binding.appBarMain.imgvAddBtn.getBackground().setColorFilter(activity.getColor(R.color.main_color), PorterDuff.Mode.SRC_ATOP);
                identifyChoosed = false;
                selectPoint = false;
                binding.appBarMain.imgvIBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_i_img_svg));
                binding.appBarMain.imgvIBtn.getBackground().setColorFilter(activity.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                binding.appBarMain.imgvSelectionBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_selection_icon_svg));
                binding.appBarMain.imgvSelectionBtn.getBackground().setColorFilter(activity.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            } else {
                clearSelection();
                addNewPoint = false;
                pointOverlayFacility.getGraphics().clear();
                binding.appBarMain.cvConfrmBtn.setVisibility(View.GONE);
                binding.appBarMain.imgvAddBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_add_svg));
                binding.appBarMain.imgvAddBtn.getBackground().setColorFilter(activity.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
            // setUpIdentifyBottomSheet();
        });

        binding.appBarMain.cvConfrmBtn.setOnClickListener(view -> {

            try {
                if (currentLocation == null) {
                    if (!AppPermissions.checkGpsOn(activity)) {
                        Utils.shortToast("GPS is disabled in your device or it may not be on 'High Accuracy'. Please enable it.", activity);
                        return;
                    }

                    Utils.shortToast("Please wait while we retrieve your current location.", activity);

                    if (locationDisplay != null && !locationDisplay.isStarted())
                        locationDisplay.startAsync();

                    return;
                }

                // boolean isPointInsideWa = GeometryEngine.contains(selectedWardAreaGeom, markedPoint);
                // boolean isCurrentLocNearClickPoint = GeometryEngine.contains(Utils.createBufferAroundBoundary(currentLocation, 50), markedPoint);

                if (GeometryEngine.contains(selectedWardAreaGeom, markedPoint)) {
                    if (GeometryEngine.contains(Utils.createBufferAroundBoundary(currentLocation, 50), markedPoint)) {
                        YesNoBottomSheet.geInstance(activity, "Do you want to continue with the marked point?",
                                activity.getResources().getString(R.string.yesBtn), activity.getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {
                                    @Override
                                    public void yesBtn() {

                                        if (Utils.isConnected(activity) && loadOnline) {
                                            if (selectedWorkArea.getWork_area_status().equals(Constants.InProgress))
                                                navigateScreen();
                                            else updateWorkArea();
                                        } else {
                                            navigateScreen();
                                        }
                                    }

                                    @Override
                                    public void noBtn() {

                                    }
                                }).show(activity.getSupportFragmentManager(), "");
                    } else {
                        binding.appBarMain.cvErrorView.setVisibility(View.VISIBLE);
                        binding.appBarMain.txtErrorView.setText("The added point is beyond a 50-meter buffer from your current location.");
                        new Handler().postDelayed(() -> {
                            binding.appBarMain.cvErrorView.setVisibility(View.GONE);
                        }, 5000);
                    }
                } else {
                    binding.appBarMain.cvErrorView.setVisibility(View.VISIBLE);
                    binding.appBarMain.txtErrorView.setText("The point added is outside the boundaries of the assigned cluster.");
                    new Handler().postDelayed(() -> {
                        binding.appBarMain.cvErrorView.setVisibility(View.GONE);
                    }, 5000);
                }
            } catch (Exception ignored) {
                if (locationDisplay != null && !locationDisplay.isStarted())
                    locationDisplay.startAsync();
                AppLog.logData(activity, ignored.getMessage());
                Utils.shortToast("Facing an error please try again.", activity);
            }
        });

        binding.appBarMain.imgvLayers.setOnClickListener(view -> setUpTocLegendsBottomSheet());

        binding.appBarMain.imgvLocation.setOnClickListener(view -> {
            if (!AppPermissions.checkGpsOn(activity)) {
                Utils.shortToast("GPS is disabled in your device or it may not be on 'High Accuracy'. Please enable it.", activity);
                return;
            }

            if (locationDisplay != null) {
                locationDisplay.startAsync();
                locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
            }
        });

        binding.appBarMain.imgvZoomToArea.setOnClickListener(v -> {
            if (selectedWardAreaGeom != null)
                mapView.setViewpointGeometryAsync(selectedWardAreaGeom, 30);
        });

        binding.appBarMain.forwardLay.setOnClickListener(view -> {
            // int currentItem = binding.appBarMain.viewPager.getCurrentItem();
            // Log.d("viewpager_inf", String.valueOf(currentItem));
            binding.appBarMain.viewPager.setCurrentItem(binding.appBarMain.viewPager.getCurrentItem() + 1);
        });

        binding.appBarMain.backwardLay.setOnClickListener(view -> {
            // int currentItem = binding.appBarMain.viewPager.getCurrentItem();
            // Log.d("viewpager_inf", String.valueOf(currentItem));
            binding.appBarMain.viewPager.setCurrentItem(binding.appBarMain.viewPager.getCurrentItem() - 1);
        });

        binding.appBarMain.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {

            }

            public void onPageScrolled(int i, float f, int i2) {
                // try {
                //     clearSelection();
                //     if (indentifyFeatureListModelArrayList.get(i).getFeature() != null
                //             && indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry() != null) {
                //         try {
                //             if (indentifyFeatureListModelArrayList.get(i).getFeatureLayer() != null) {
                //                 if (indentifyFeatureListModelArrayList.get(i).getFeatureLayer().getLoadStatus() == LoadStatus.LOADED)
                //                     indentifyFeatureListModelArrayList.get(i).getFeatureLayer().clearSelection();
                //             }
                //         } catch (Exception e) {
                //             e.printStackTrace();
                //         }
                //     }
                // } catch (Exception ignored) {
                // }
            }

            public void onPageSelected(int i) {
                try {
                    clearSelection();
                    if (indentifyFeatureListModelArrayList.get(i).getFeature() != null
                            && indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry() != null) {
                        try {
                            if (indentifyFeatureListModelArrayList.get(i).getLayerName().equals(Constants.hutPolygonLayerNameProper))
                                structureInfo_FL.selectFeature(indentifyFeatureListModelArrayList.get(i).getFeature());
                            else if (indentifyFeatureListModelArrayList.get(i).getLayerName().equals(Constants.workAreaLayerNameProper))
                                workArea_FL.selectFeature(indentifyFeatureListModelArrayList.get(i).getFeature());
                            else if (indentifyFeatureListModelArrayList.get(i).getFeatureLayer() != null) {
                                if (indentifyFeatureListModelArrayList.get(i).getFeatureLayer().getLoadStatus() == LoadStatus.LOADED)
                                    indentifyFeatureListModelArrayList.get(i).getFeatureLayer().selectFeature(indentifyFeatureListModelArrayList.get(i).getFeature());
                            }

                            addDot(i);
                            // Point pointGeoma = (Point) GeometryEngine.project(indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry(), SpatialReference.create(Constants.SpatialReference));
                            // mapView.setViewpointGeometryAsync(indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry(), 30);

                            setViewPointGeom(indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry());
                            // setViewPoint(indentifyFeatureListModelArrayList.get(i).getFeature().getGeometry());

                        } catch (Exception e) {
                            // e.printStackTrace();
                            AppLog.e("Exception in onPageSelected: " + e.getMessage());
                        }
                    }
                } catch (Exception ignored) {
                    AppLog.e("Exception in onPageSelected ignored: " + ignored.getMessage());
                }
            }
        });
    }

    private void downloadNewMap(String workAreaName, String filePath) {

        try {

            job = null;

            offlineProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
                if (job != null) {
                    job.cancel();
                }
                //dismiss dialog
            });

            offlineProgressDialog.show();

            AppLog.e("Offline Path" + filePath);

            // specify the extent, min scale, and max scale as parameters
            double minScale = binding.appBarMain.mapView.getMapScale();
            double maxScale = binding.appBarMain.mapView.getMap().getMaxScale();
            // minScale must always be larger than maxScale
            if (minScale <= maxScale) {
                minScale = maxScale + 1;
            }

            GenerateOfflineMapParameters generateOfflineMapParameters = new GenerateOfflineMapParameters(
                    Utils.createBufferAroundBoundary(selectedWardAreaGeom, 100), minScale, maxScale);
            // set job to cancel on any errors
            generateOfflineMapParameters.setContinueOnErrors(false);

            // create an offline map offlineMapTask with the map
            OfflineMapTask offlineMapTask = new OfflineMapTask(binding.appBarMain.mapView.getMap());

            // create an offline map job with the download directory path and parameters and start the job
            job = offlineMapTask.generateOfflineMap(generateOfflineMapParameters, filePath);

            // replace the current map with the result offline map when the job finishes
            job.addJobDoneListener(() -> {
                AppLog.e("Download job Status : " + job.getStatus());
                if (job.getStatus() == Job.Status.SUCCEEDED) {
                    // GenerateOfflineMapResult result = job.getResult();
                    // binding.appBarMain.mapView.setMap(result.getOfflineMap());
                    // Utils.shortToast("Now displaying offline map.", activity);

                    DownloadedWebMapModel downloadedWebMapModel = new DownloadedWebMapModel(selectedWorkArea.getWork_area_name(), userModel.getUser_name(),
                            filePath, "" + zoomLevel, selectedWorkArea.getGeometry(), new Date(), new Date(),
                            selectedWorkArea.getStartDate(), selectedWorkArea.getEndDate());

                    localSurveyDbViewModel.insertDownloadWebmapInfoData(downloadedWebMapModel, activity);

                    Utils.shortToast("Downloading succeed.", activity);
                    showActionAlertDialogUpdateButtonOffline("DRP App Status", "OK", "", "After working and uploading record, Please re-download offline map to see and work on updated records!", "");
                } else if (job.getStatus() == Job.Status.CANCELING) {
                    Utils.shortToast("Downloading canceled.", activity);
                    try {
                        Utils.deleteDirectory(new File(filePath));
                    } catch (Exception exception) {
                        AppLog.e(exception.getMessage());
                    }
                } else {
                    String error = "There was an error while generating the offline map. Please try again.";
                    // if (job.getError().getCause() != null) {
                    //     error = "Error while generating the offline map. Please try again.";
                    // } else {
                    //     error = "Error while generating the offline map. Please try again.";
                    // }

                    Utils.showMessagePopup(error, activity);
                    AppLog.e("Download " + error);
                }

                offlineProgressDialog.dismiss();
            });

            // show the job's progress with the progress dialog
            job.addProgressChangedListener(() -> offlineProgressDialog.setProgress(job.getProgress()));

            // start the job
            job.start();
        } catch (Exception ex) {
            AppLog.e("Exception in downloadnewmap:" + ex.getMessage());
        }
    }

    public void showActionAlertDialogButtonClicked(String header, String mssage, String btnYes, String btnNo) {
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

            if (App.getSharedPreferencesHandler().getBoolean(Constants.isMapLoadOffline)) {
                Utils.shortToast("Unable to proceed Download. You're currently offline Mode.", activity);
                return;
            }

            if (Utils.isConnected(activity)) {

                String filePath = activity.getExternalFilesDir("OfflineMaps") + File.separator + selectedWorkArea.getWork_area_name();

                File myExternalOutputFile = new File(filePath);

                if (myExternalOutputFile.exists() && localSurveyDbViewModel.isWorkAreaDownloaded(selectedWorkArea.getWork_area_name())) {

                    YesNoBottomSheet.geInstance(activity, "The selected work area '" + selectedWorkArea.getWork_area_name() + "' " +
                                    "has already been downloaded. Do you want to re-download it again?",
                            activity.getResources().getString(R.string.yesBtn), activity.getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {
                                @Override
                                public void yesBtn() {
                                    try {
                                        Utils.updateProgressMsg("Deleting the previous download. Please wait...", activity);
                                        Utils.deleteDirectory(myExternalOutputFile);
                                        localSurveyDbViewModel.deleteWebMapInfoData(selectedWorkArea.getWork_area_name(), activity);
                                        new Handler().postDelayed(() -> {
                                            downloadNewMap(selectedWorkArea.getWork_area_name(), filePath);
                                            Utils.dismissProgress();
                                            dialog.dismiss();
                                        }, 2000);
                                    } catch (Exception ex) {
                                        AppLog.e("re-Download Map Exception:" + ex.getMessage());
                                    }
                                }

                                @Override
                                public void noBtn() {

                                }
                            }).show(activity.getSupportFragmentManager(), "");

                } else {
                    Utils.updateProgressMsg("Please wait...", activity);

                    Utils.deleteDirectory(myExternalOutputFile);
                    localSurveyDbViewModel.deleteWebMapInfoData(selectedWorkArea.getWork_area_name(), activity);

                    new Handler().postDelayed(() -> {
                        Utils.dismissProgress();
                        dialog.dismiss();
                        downloadNewMap(selectedWorkArea.getWork_area_name(), filePath);
                    }, 2000);
                    // downloadNewMap(selectedWorkArea.getWork_area_name(), filePath);
                    // dialog.dismiss();
                }
            } else {
                Utils.shortToast("Unable to proceed. You're currently offline.", activity);
            }
        });

        btn_no.setOnClickListener(view1 -> {
            // Utils.shortToast("Under development.", activity);
            // previously downloadofflinewebmap
            try {
                activity.startActivity(new Intent(activity, DownloadActivity.class));
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
            dialog.dismiss();
        });

        img_close.setOnClickListener(view1 -> dialog.dismiss());

        dialog.show();
    }

    private void setViewPointGeom(Geometry geometry) {
        if (geometry != null) {
            Geometry geometry1 = GeometryEngine.project(geometry, SpatialReference.create(Constants.SpatialReference));

            if (geometry1 != null) {
                // binding.appBarMain.mapView.setViewpointAsync(new Viewpoint(geometry1.getExtent()), 1);
                binding.appBarMain.mapView.setViewpointGeometryAsync(geometry1, 30);
            }
        }
    }

    private void setViewPoint(Geometry geometry) {
        binding.appBarMain.mapView.setViewpointAsync(new Viewpoint(geometry.getExtent()), 2);
    }

    private void updateWorkArea() {

        UpdateWorkAreaStatusModel updateWorkAreaStatusModel = new UpdateWorkAreaStatusModel();
        updateWorkAreaStatusModel.setWork_area_status(Constants.InProgress_statusLayer);
        updateWorkAreaStatusModel.setObjectid((int) Double.parseDouble(selectedWorkArea.getObjectid()));

        UpdateFeatureToLayer.UpdateForm updateForm = new UpdateFeatureToLayer.UpdateForm();
        updateForm.setAttributes(updateWorkAreaStatusModel);

        Utils.updateProgressMsg("Updating work area status.", activity);
        List<UpdateFeatureToLayer.UpdateForm> array = new ArrayList<>();
        try {

            array.add(updateForm);

            QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
            queryResultRepoViewModel.initUpdateFeatureResult(Constants.WorkArea_FS_BASE_URL_ARC_GIS, Constants.WorkArea_ENDPOINT,
                    GetFormModel.getInstance().getUpdateFeatureBuilderForm(Utils.getGson().toJson(array)));

            queryResultRepoViewModel.getUpdatedWorkAreaMutableLiveData().observe(activity, addedRecordResponse -> {

                Utils.dismissProgress();
                if (addedRecordResponse != null && !addedRecordResponse.getAddResults().isEmpty()) {
                    if (addedRecordResponse.getAddResults().get(0).getSuccess()) {
                        selectedWorkArea.setWork_area_status(Constants.InProgress_statusLayer);
                        Utils.shortToast("Work Area status updated successfully", activity);
                    } else {
                        Utils.shortToast("Work Area status not updated successfully", activity);
                    }
                    navigateScreen();

                } else {
                    Utils.shortToast("Work Area status not updated successfully", activity);
                }
            });
        } catch (Exception exception) {
            AppLog.e(exception.getMessage());
        }
    }

    private void navigateScreen() {
        App.getSharedPreferencesHandler().putString(Constants.structureInfoType, Constants.structureInfoAdd);
        /* activity.startActivityForResult(new Intent(activity, FormPageActivity.class)
                .putExtra(Constants.IS_EDITING, false)
                .putExtra(Constants.WorkArea_work_area_name, selectedWorkArea.getWork_area_name())
                .putExtra(Constants.markerPointGeom, markedPoint.toJson())
                .putExtra(Constants.markerPointLat, markedPoint.getY())
                .putExtra(Constants.markerPointLong, markedPoint.getX()), Constants.activityForMapResult
        ); */
    }

    private void logOut() {
        YesNoBottomSheet.geInstance(activity, activity.getResources().getString(R.string.ask_logout), activity.getResources().getString(R.string.yesBtn), activity.getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {
            @Override
            public void yesBtn() {
                try {
                    afterLogoutApi();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void noBtn() {

            }
        }).show(activity.getSupportFragmentManager(), "");
    }

    private void handleIdentifySelectionResults(List<IdentifyLayerResult> identifyLayerResults, Point clickPoint) {

        clearSelection();
        indentifyFeatureListModelArrayList = new ArrayList<>();
        mapLayerAttributes = new HashMap<>();
        layerName = new ArrayList<>();
        mapLayerFeature = new HashMap<>();
        Feature selectedFeature = null;
        FeatureLayer featureLayer = null;
        String headerName = "", header1 = "", header2 = "", header3 = "", globalId = "";
        for (IdentifyLayerResult identifyLayerResult : identifyLayerResults) {
            // Feature selectedFeature = null;

            headerName = identifyLayerResult.getLayerContent().getName();
            // for each result, get the GeoElements
            if (headerName.equals(Constants.hutPolygonsLayerName)) {
                headerName = Constants.hutPolygonLayerNameProper;
                for (GeoElement identifiedElement : identifyLayerResult.getElements()) {
                    //  FeatureLayer featureLayer = null;
                    if (identifiedElement instanceof Feature) {

                        // Feature feature = (Feature) identifiedElement;
                        featureLayer = ((Feature) identifiedElement).getFeatureTable().getFeatureLayer();
                        selectedFeatureLayer = featureLayer;
                        structureInfo_FL = featureLayer;

                        if (identifiedElement.getGeometry() != null)
                            if (!identifiedElement.getGeometry().isEmpty()) {
                                selectedFeature = (Feature) identifiedElement;//feature;

                                Map<String, Object> attr = identifiedElement.getAttributes();
                                if (attr != null) {
                                    if (identifyLayerResult.getLayerContent().getName().equals(Constants.hutPolygonsLayerName)) {
                                        header1 = Utils.getString(attr.get("structure_id"));
                                        header2 = Utils.getString(attr.get("structure_status"));
//                                        header3 = Utils.getString(attr.get("hut_number"));
                                        header3 = Utils.getString(attr.get("hut_id"));
                                        String workAreaName= App.getSharedPreferencesHandler().getString(Constants.workAreaNameN);
                                        App.getSharedPreferencesHandler().putString(workAreaName,Utils.getString(attr.get("nagar_name")));
                                        App.getSharedPreferencesHandler().putString(Constants.structureTypeName,Utils.getString(attr.get("structure_type")));
                                        App.getSharedPreferencesHandler().putBoolean(Constants.floorFlag,false);
                                        globalId = Utils.getString(attr.get(Constants.globalid));
                                        setStructureUniqueId(attr);
                                    }
                                }
                            }
                    }

                    if (selectedFeature != null && !Utils.isNullOrEmpty(globalId))
                        indentifyFeatureListModelArrayList.add(
                                new IndentifyFeatureListModel(headerName, header1, header2, header3, globalId, selectedFeature, featureLayer));

                }
            }

        }

        Utils.dismissProgress();

        if (indentifyFeatureListModelArrayList.size() > 1)
            setupViewPager(indentifyFeatureListModelArrayList);
        else if (indentifyFeatureListModelArrayList.size() == 1)
            showSelectionAlert(indentifyFeatureListModelArrayList.get(0));
        else {
            Utils.shortToast("No data found", activity);
        }
    }

    private void showSelectionAlert(IndentifyFeatureListModel selectedFeature) {

        if (selectedFeature.getFeature() != null) {
            Geometry ftrGeom = selectedFeature.getFeature().getGeometry();
            ftrGeom = GeometryEngine.project(ftrGeom, SpatialReference.create(Constants.SpatialReference));

            //Point: [72.854404, 19.047595, 0.000000, NaN] SR: 4326
            Geometry finalFtrGeom = ftrGeom;
            YesNoBottomSheet.geInstance(activity, "Are you at the right location and do you really want to continue with the selected point?",
                    activity.getResources().getString(R.string.yesBtn), activity.getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {
                        @Override
                        public void yesBtn() {
                            try {
                                if (currentLocation == null) {
                                    if (!AppPermissions.checkGpsOn(activity)) {
                                        Utils.shortToast("GPS is disabled in your device or it may not be on 'High Accuracy'. Please enable it.", activity);
                                        return;
                                    }

                                    Utils.shortToast("Please wait while we retrieve your current location.", activity);
                                    if (locationDisplay != null && !locationDisplay.isStarted())
                                        locationDisplay.startAsync();

                                    return;
                                }

                                boolean isCurrentLocNearFea = GeometryEngine.contains(Utils.createBufferAroundBoundary(finalFtrGeom, 30), currentLocation);
                                if (isCurrentLocNearFea) {
//                                 if (true) {
                                     App.getSharedPreferencesHandler().putDoubString("lat",lat);
                                     App.getSharedPreferencesHandler().putDoubString("lon",lng);
                                    App.getSharedPreferencesHandler().putString(Constants.uniqueId, selectedFeature.getGlobalId());
                                    App.getSharedPreferencesHandler().putString(Constants.viewMode, Constants.online);
                                    activity.startActivity(new Intent(activity, SummaryActivity.class)
                                            .putExtra(Constants.uniqueId, selectedFeature.getGlobalId()));
                                } else {
                                    binding.appBarMain.cvErrorView.setVisibility(View.VISIBLE);
                                    binding.appBarMain.txtErrorView.setText("Sorry, it seems that you are currently located more than 30 meters away from the selected structure point.");
                                    new Handler().postDelayed(() -> binding.appBarMain.cvErrorView.setVisibility(View.GONE), 5000);
                                }
                            } catch (Exception e) {
                                if (locationDisplay != null && !locationDisplay.isStarted())
                                    locationDisplay.startAsync();
                                AppLog.logData(activity, e.getMessage());
                                Utils.shortToast("Facing an error please try again.", activity);
                            }
                        }

                        @Override
                        public void noBtn() {
                            structureInfo_FL.clearSelection();
                        }
                    }).show(activity.getSupportFragmentManager(), "");
        } else {
            Utils.shortToast("Cannot proceed with selected structure point due to an error.", activity);
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
                    } else if (identifyLayerResult.getLayerContent().getName().equals(Constants.hutPolygonLayerName)) {
                        headerName = Constants.hutPolygonLayerNameProper;
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
                                } else if (identifyLayerResult.getLayerContent().getName().equals(Constants.hutPolygonsLayerName)) {
                                    header1 = Utils.getString(attr.get("hut_id"));
                                    header2 = Utils.getString(attr.get("structure_status"));
                                    header3 = Utils.getString(attr.get("hut_id"));//hut_number
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

                if (selectedFeature != null && !identifyModels.isEmpty() && !Utils.isNullOrEmpty(header1))
                    indentifyFeatureListModelArrayList.add(
                            new IndentifyFeatureListModel(headerName, header1, header2, header3, selectedFeature, featureLayer, identifyModels));
            }

            mapLayerAttributes.put(headerName, identifyModels);

            // if (selectedFeature != null && identifyModels.size() > 0 && !Utils.isNullOrEmpty(header1))
            //     indentifyFeatureListModelArrayList.add(
            //             new IndentifyFeatureListModel(headerName, header1, header2, header3, selectedFeature, identifyModels));
        }

        Utils.dismissProgress();
        // if (layerName.size() > 0)
        // showAlert(layerName);

        if (indentifyFeatureListModelArrayList.size() > 1)
            setupViewPager(indentifyFeatureListModelArrayList);
        else if (!layerName.isEmpty())
            showAlert(layerName);
        else
            Utils.shortToast("No data found", activity);
    }

    private void showAlert(ArrayList<String> layerName) {
        if (layerName.size() == 1) {
            if (layerName.get(0).equals(Constants.workAreaLayerNameProper) && workArea_FL != null && workArea_FL.getLoadStatus() == LoadStatus.LOADED)
                workArea_FL.selectFeature(mapLayerFeature.get(layerName.get(0)));
            else if (layerName.get(0).equals(Constants.hutPolygonLayerNameProper) && structureInfo_FL != null && structureInfo_FL.getLoadStatus() == LoadStatus.LOADED)
                structureInfo_FL.selectFeature(mapLayerFeature.get(layerName.get(0)));

            Feature feature = mapLayerFeature.get(layerName.get(0));
            selectedFeatureLayer.selectFeature(feature);
            setViewPointGeom(feature.getGeometry());
            setUpIdentifyBottomSheet(mapLayerAttributes.get(layerName.get(0)));
        }
    }

    private void setupViewPager(ArrayList<IndentifyFeatureListModel> selectedFeatureLayer) {
        FeatureListAdapter adapter = new FeatureListAdapter(activity, selectedFeatureLayer, this::onItemClick, identifyItemListModelList);
        binding.appBarMain.viewPager.setAdapter(adapter);
        binding.appBarMain.viewPager.setCurrentItem(0);
        binding.appBarMain.page.setVisibility(View.VISIBLE);
        setViewPager(selectedFeatureLayer);
    }

    public void setViewPager(ArrayList<IndentifyFeatureListModel> selectedFeatureLayer) {

        // Added null condition as NPE Observed
        if (workArea_FL != null)
            workArea_FL.clearSelection();
        if (structureInfo_FL != null)
            structureInfo_FL.clearSelection();

        if (!selectedFeatureLayer.isEmpty()) {
            addDot(0);
            if (selectedFeatureLayer.get(0).getFeature() != null
                    && selectedFeatureLayer.get(0).getFeature().getGeometry() != null) {
                try {
                    if (selectedFeatureLayer.get(0).getLayerName().equals(Constants.hutPolygonLayerNameProper))
                        structureInfo_FL.selectFeature(selectedFeatureLayer.get(0).getFeature());
                    else if (selectedFeatureLayer.get(0).getLayerName().equals(Constants.workAreaLayerNameProper))
                        workArea_FL.selectFeature(selectedFeatureLayer.get(0).getFeature());
                    else if (selectedFeatureLayer.get(0).getFeatureLayer() != null) {
                        if (selectedFeatureLayer.get(0).getFeatureLayer().getLoadStatus() == LoadStatus.LOADED)
                            selectedFeatureLayer.get(0).getFeatureLayer().selectFeature(selectedFeatureLayer.get(0).getFeature());
                    }

                    // Point pointGeoma = (Point) GeometryEngine.project(selectedFeatureLayer.get(0).getFeature().getGeometry(), SpatialReference.create(Constants.SpatialReference));
                    // mapView.setViewpointGeometryAsync(selectedFeatureLayer.get(0).getFeature().getGeometry(), 30);

                    setViewPointGeom(selectedFeatureLayer.get(0).getFeature().getGeometry());

                    // setViewPoint(selectedFeatureLayer.get(0).getFeature().getGeometry());
                    // binding.appBarMain.mapView.setViewpointCenterAsync(pointGeoma);
                } catch (Exception e) {
                    AppLog.e(e.getMessage());
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

    private void setUpTocLegendsBottomSheet() {

        if (mapView.getMap() != null) {
            FragmentManager fm = activity.getSupportFragmentManager();
            ShowTocBottomSheetFragment showLayerBottomSheetFragment = ShowTocBottomSheetFragment.geInstance(activity, mapView.getMap().getOperationalLayers(), "");
            showLayerBottomSheetFragment.show(fm, "");
        }
    }

    private void setUpIdentifyBottomSheet(ArrayList<IdentifyItemListModel> identifyItemListModelList) {
        FragmentManager fm = activity.getSupportFragmentManager();
        ShowIdentifyItemBottomSheetFragment showIdentifyItemBottomSheetFragment = ShowIdentifyItemBottomSheetFragment.geInstance(activity, identifyItemListModelList);
        showIdentifyItemBottomSheetFragment.show(fm, "");
    }

    private void clearSelection() {

        if (workArea_FL != null)
            workArea_FL.clearSelection();

        if (structureInfo_FL != null)
            structureInfo_FL.clearSelection();

        if (selectedFeatureLayer != null)
            selectedFeatureLayer.clearSelection();

        if (indentifyFeatureListModelArrayList != null) {
            for (IndentifyFeatureListModel indentifyFeatureListModel : indentifyFeatureListModelArrayList) {
                if (indentifyFeatureListModel.getFeatureLayer() != null) {
                    indentifyFeatureListModel.getFeatureLayer().clearSelection();
                }
            }
        }
    }

    @Override
    public void onPause() {
        mapView.pause();
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.activityForMapResult) {
                try {
                    addNewPoint = false;
                    pointOverlayFacility.getGraphics().clear();
                    binding.appBarMain.cvConfrmBtn.setVisibility(View.GONE);
                    binding.appBarMain.imgvAddBtn.setImageDrawable(activity.getDrawable(R.drawable.icon_add_svg));
                    binding.appBarMain.imgvAddBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                } catch (Exception e) {
                    AppLog.e(e.getMessage());
                }
            } else if (requestCode == Constants.activityForChangeClusterStatus) {
                if (data != null && data.hasExtra(Constants.INTENT_SelectedWorkArea)) {
                    WorkAreaModel selectedWork = (WorkAreaModel) data.getSerializableExtra(Constants.INTENT_SelectedWorkArea);
                    if (selectedWorkArea.getWork_area_name().equals(selectedWork.getWork_area_name())) {
                        selectedWorkArea.setWork_area_status(selectedWork.getWork_area_status());
                    }
                }
            } else if (requestCode == Constants.activityForSearch) {
                if (data != null && data.hasExtra(Constants.INTENT_SelectedStructureId)) {
                    getStructureLocation(data.getStringExtra(Constants.INTENT_SelectedStructureId));
                }
            }
        }
    }

    private void getStructureLocation(String stringExtra) {
        structureInfo_FT.addDoneLoadingListener(() -> {
            if (structureInfo_FT.getLoadStatus() == LoadStatus.LOADED) {
                Utils.updateProgressMsg("Getting structure location. Please wait..", activity);
                QueryParameters query = new QueryParameters();
                query.setWhereClause("structure_id = '" + stringExtra + "'");

                // structureInfo_FT.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);
                // List<String> outFields = new ArrayList<>();
                // outFields.add("*");
                final ListenableFuture<FeatureQueryResult> future = structureInfo_FT.queryFeaturesAsync(query, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                future.addDoneListener(() -> {
                    try {
                        Utils.dismissProgress();
                        FeatureQueryResult result = future.get();
                        Iterator<Feature> resultIterator = result.iterator();
                        if (result.iterator().hasNext()) {
                            Feature feature = resultIterator.next();
                            structureInfo_FL.selectFeature(feature);
                            binding.appBarMain.mapView.setViewpointGeometryAsync(feature.getGeometry(), 10);
                            binding.appBarMain.mapView.setViewpointScaleAsync(10000.0);

                            String globalId = Utils.getString(feature.getAttributes().get(Constants.globalid));

                            YesNoBottomSheet.geInstance(activity, "Do you want to continue with the selected point?",
                                    activity.getResources().getString(R.string.yesBtn), activity.getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {
                                        @Override

                                        public void yesBtn() {
                                            App.getSharedPreferencesHandler().putString(Constants.uniqueId, globalId);
                                            App.getSharedPreferencesHandler().putString(Constants.viewMode, Constants.online);
                                            activity.startActivity(new Intent(activity, SummaryActivity.class)
                                                    .putExtra(Constants.uniqueId, globalId));
                                        }

                                        @Override
                                        public void noBtn() {
                                            if (structureInfo_FL != null)
                                                structureInfo_FL.clearSelection();
                                        }
                                    }).show(activity.getSupportFragmentManager(), "");
                        } else Utils.shortToast("No structure point found.", activity);
                    } catch (Exception e) {
                        Utils.dismissProgress();
                        Utils.shortToast("Unable to get the details.", activity);
                        AppLog.e(e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        mapView.dispose();
        super.onDestroy();
    }

    @Override
    public void onItemClick(IndentifyFeatureListModel indentifyFeatureListModel) {
        if (Utils.isNullOrEmpty(indentifyFeatureListModel.getGlobalId())) {
            setUpIdentifyBottomSheet(indentifyFeatureListModel.getIdentifyItemListModels());
        } else {
            showSelectionAlert(indentifyFeatureListModel);
        }
    }

    private void loadMap() {
        if (Utils.isConnected(activity) && !App.getSharedPreferencesHandler().getBoolean(Constants.isMapLoadOffline)) {
            loadOnline = true;
            structureInfo_FL_whereClause = "work_area_name  = '" + selectedWorkArea.getWork_area_name() + "'";
            workArea_FL_whereClause = String.format("user_name = '%s' AND work_area_name = '%s' AND (work_area_status = '%s' OR work_area_status = '%s' OR work_area_status = '%s')",
                    userModel.getUser_name(), selectedWorkArea.getWork_area_name(), Constants.NotStarted_statusLayer, Constants.InProgress_statusLayer, Constants.OnHold_statusLayer);
            showOnline();
            binding.appBarMain.txtSelectedWebMap.setText("Online Map");
            binding.appBarMain.txtSelectedWebMap.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.main_color));
        } else {
            binding.appBarMain.txtSelectedWebMap.setText("Offline Map");
            binding.appBarMain.txtSelectedWebMap.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.offlineBack));
            loadOnline = false;
            showOffline();
        }
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
                    listUniqueId.add(uniqueId.trim());
            }
        } catch (Exception ex) {
            AppLog.e("Exception in setStructureUniqueId:" + ex.getMessage());
            AppLog.logData(activity, ex.getMessage());
        }

        App.getInstance().setListUniqueId(listUniqueId);
    }

    public void showActionAlertDialogUpdateButtonOffline(String header, String yesBtn, String noBtn, String message, String downloadLink) {
        // Create an alert builder
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_action, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        TextView txt_mssage = customLayout.findViewById(R.id.txt_mssage);

        txt_header.setText(header);
        txt_mssage.setText(message);

        TextView txt_yes = customLayout.findViewById(R.id.txt_yes);
        TextView txt_no = customLayout.findViewById(R.id.txt_no);

        txt_no.setText(noBtn);
        txt_yes.setText(yesBtn);

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);
        ImageView img_close = customLayout.findViewById(R.id.img_close);
        img_close.setVisibility(View.GONE);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);

        statusRadioGroup.setVisibility(View.GONE);

        if (Utils.isNullOrEmpty(yesBtn))
            btn_yes.setVisibility(View.GONE);

        if (Utils.isNullOrEmpty(noBtn))
            btn_no.setVisibility(View.GONE);

        btn_yes.setOnClickListener(view1 -> dialog.dismiss());

        btn_no.setOnClickListener(view1 -> {
            dialog.dismiss();
            activity.finish();
        });

        dialog.show();
    }

    private void fetchData(ServiceFeatureTable featureTable) {

        // create a query for the state that was entered
        QueryParameters query = new QueryParameters();
        query.setWhereClause("1=1");

        // search for the state feature in the feature table
        ListenableFuture<FeatureQueryResult> tableQueryResult = featureTable.queryFeaturesAsync(query);

        tableQueryResult.addDoneListener(() -> {
            try {
                // get the result from the query
                FeatureQueryResult result = tableQueryResult.get();
                // if a state feature was found
                if (result.iterator().hasNext()) {
                    // get state feature and zoom to it
                    Feature feature = result.iterator().next();
                    // Envelope envelope = feature.getGeometry().getExtent();
                    // mapView.setViewpointGeometryAsync(envelope, 200);
                    System.out.println("FETURE_ATTRIBUTES::" + feature.getAttributes().toString());
                }
            } catch (Exception e) {
                // on any error, display the stack trace
                e.printStackTrace();
            }
        });
    }

    private void afterLogoutApi() throws Exception {
        if (!Utils.checkinterne(activity))
            return;
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("userId",App.getSharedPreferencesHandler().getString(Constants.userId_cons));
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());


        Utils.updateProgressMsg("Authenticating, please wait..", activity);

        Api_Interface apiInterface = RetrofitService.getAfterLogout().create(Api_Interface.class);
        Call<AfterLogoutData> call = apiInterface.validateAfterLogoutUser(body);
        call.enqueue(new Callback<AfterLogoutData>() {
            @Override
            public void onResponse(Call<AfterLogoutData> call, Response<AfterLogoutData> response) {
                Utils.dismissProgress();
                try {
                    Log.d("TAG", response.code() + "" + response.body());
                    if (response.code() == 200) {

                        AfterLogoutData loginModel = response.body();
                        if(loginModel!=null && loginModel.getStatus()!=null && loginModel.getStatus().getStatus()==1){
                            App.getSharedPreferencesHandler().ClearPreferences();
                            App.getSharedPreferencesHandler().putBoolean(Constants.loginStatus, false);
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            activity.finishAffinity();
                        }else if(loginModel!=null && loginModel.getStatus()!=null && loginModel.getStatus().getStatus()==0){
                            showActionAlertDialogUpdateButton("DRP App Status", "", "Close", loginModel.getStatus().getMessage(), "");
                        }
                    } else {
                        Utils.showMessagePopup("Invalid User", activity);
                    }
                } catch (Exception e) {
                    AppLog.e(e.getMessage());
                    Utils.showMessagePopup("Invalid User.", activity);
                }
            }

            @Override
            public void onFailure(Call<AfterLogoutData> call, Throwable t) {
                call.cancel();
            }
        });
    }


    public void showActionAlertDialogUpdateButton(String header, String yesBtn, String noBtn, String message, String downloadLink) {
        // Create an alert builder
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_action, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        TextView txt_mssage = customLayout.findViewById(R.id.txt_mssage);

        txt_header.setText(header);
        txt_mssage.setText(message);

        TextView txt_yes = customLayout.findViewById(R.id.txt_yes);
        TextView txt_no = customLayout.findViewById(R.id.txt_no);

        txt_no.setText(noBtn);
        txt_yes.setText(yesBtn);

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);
        ImageView img_close = customLayout.findViewById(R.id.img_close);
        img_close.setVisibility(View.GONE);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);

        statusRadioGroup.setVisibility(View.GONE);

        if (Utils.isNullOrEmpty(yesBtn))
            btn_yes.setVisibility(View.GONE);

        btn_yes.setOnClickListener(view1 -> {
            if (Utils.isConnected(activity)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink));
                dialog.dismiss();
                try {
                    activity.startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    AppLog.e(e.getMessage());
                }
                activity.finish();
            } else Utils.shortToast("Unable to proceed. You're currently offline.", activity);

        });
        btn_no.setOnClickListener(view1 -> {
            dialog.dismiss();
            activity.finish();

        });

        dialog.show();
    }
}