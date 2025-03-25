package com.igenesys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.model.UserModel;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class StructureActivity extends AppCompatActivity {

    LocalSurveyDbViewModel localSurveyDbViewModel;
    HashMap<String, StructureInfoPointDataModel> structureInfoPointHashmap;
    StructureInfoPointDataModel previousStructureInfoPointDataModel;
    String work_area_name, statusAlert = "", viewMode, structUniqueId,hutId="", markerPointGeom, strutureAlertStatus, surveyor_name="", floor_no = "";
    double markerPointLat, markerPointLong;
    private ArrayList<String> unitStatusArray = new ArrayList<>();
    private HohInfoDataModel previousHohInfoDataModel = null;
    boolean flowFlag, flowFlagUnit, editMode;
    List<MediaInfoDataModel> deleteTotalMediaList = new ArrayList<>();
    MediaInfoDataModel model;
    EditText et_surveyUniqueIdNumber;
    TextView nextBtn;
    boolean IS_EDITING, unitInfoStatus = true;
    AutoCompleteTextView autoCompUnitFloorDetails;
    AppCompatButton btn_Cancel;
    LinearLayout btn_next;
    Activity activity;
    StructureInfoPointDataModel structureInfoPointDataModel;
    private UnitInfoDataModel previousUnitInfoPointDataModel = null;
    private ArrayList<MemberInfoDataModel> memberInfoDataModelArrayList;


    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_structure);

        initActivity();
    }


    private void initActivity() {
        try {
            userModel = App.getInstance().getUserModel();
            if(userModel.equals(null)){
                Utils.shortToast("User Details Not Found",activity);
            }
            surveyor_name=userModel.getUser_name();
            activity = StructureActivity.this;
            structureInfoPointHashmap = new HashMap<>();
//            localSurveyDbViewModel = ViewModelProviders.of(StructureActivity.this).get(LocalSurveyDbViewModel.class);
            localSurveyDbViewModel = new ViewModelProvider(StructureActivity.this).get(LocalSurveyDbViewModel.class);
            et_surveyUniqueIdNumber = (EditText) findViewById(R.id.et_surveyUniqueIdNumber);
            nextBtn = (TextView) findViewById(R.id.txt_next);
            autoCompUnitFloorDetails = (AutoCompleteTextView) findViewById(R.id.autoCompUnitFloorDetails);
            btn_Cancel = (AppCompatButton) findViewById(R.id.btn_Cancel);
            btn_next = (LinearLayout) findViewById(R.id.btn_next);


            if (activity.getIntent().hasExtra(Constants.WorkArea_work_area_name)) {
                work_area_name = activity.getIntent().getStringExtra(Constants.WorkArea_work_area_name);
                App.getSharedPreferencesHandler().putString(Constants.workAreaNameN,work_area_name);
            }

            if (activity.getIntent().hasExtra(Constants.EDIT_TYPE)) {
                autoCompUnitFloorDetails.setBackgroundResource(R.drawable.rounded_blue_edittext);
                if (activity.getIntent().hasExtra(Constants.UnitInfo_unit_status)){
                    unitInfoStatus = activity.getIntent().getBooleanExtra(Constants.UnitInfo_unit_status,true);
                }
                if (activity.getIntent().getStringExtra(Constants.EDIT_TYPE).toString().equals(Constants.EDIT_StructureInfo)) {
                    flowFlag = false;
                    nextBtn.setText("Finish");
                } else if (activity.getIntent().getStringExtra(Constants.EDIT_TYPE).toString().equals(Constants.NewInfoData)) {
                    flowFlag = true;
                    flowFlagUnit = false;

                }else if (activity.getIntent().getStringExtra(Constants.EDIT_TYPE).toString().equals(Constants.EDIT_UnitInfo)) {
                    flowFlag = true;
                    flowFlagUnit = false;
                }
            }
            if (activity.getIntent().hasExtra(Constants.viewMode)) {
                viewMode = activity.getIntent().getStringExtra(Constants.viewMode);
            }
            if (activity.getIntent().hasExtra(Constants.IS_EDITING)) {
                if (activity.getIntent().getBooleanExtra(Constants.IS_EDITING, false)) {
                    if (activity.getIntent().hasExtra(Constants.INTENT_DATA_StructureInfo)) {
                        previousStructureInfoPointDataModel = (StructureInfoPointDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);
                        if (previousStructureInfoPointDataModel != null) {
                            editMode = true;

                            structUniqueId = previousStructureInfoPointDataModel.getStructure_id();
                            hutId = previousStructureInfoPointDataModel.getHut_number();

                            if (!Utils.isNullOrEmpty(previousStructureInfoPointDataModel.getGeometry())) {
                                markerPointGeom = GeometryEngine.project(Geometry.fromJson(previousStructureInfoPointDataModel.getGeometry()), SpatialReference.create(Constants.SpatialReference)).toJson();
                                markerPointLat = Geometry.fromJson(markerPointGeom).getExtent().getCenter().getY();
                                markerPointLong = Geometry.fromJson(markerPointGeom).getExtent().getCenter().getX();
                            }

                            work_area_name = previousStructureInfoPointDataModel.getWork_area_name();
                            App.getSharedPreferencesHandler().putString(Constants.workAreaNameN,work_area_name);
//                            etAddress.setText(previousStructureInfoPointDataModel.getAddress());
//                            autoCompleteNoOfFloor.setText(previousStructureInfoPointDataModel.getNo_of_floors() + "", false);
//                            etTenementNumber.setText(previousStructureInfoPointDataModel.getTenement_number());
//                            etNameOfNagarArea.setText(previousStructureInfoPointDataModel.getArea_name());
//                            etPhaseClusterNameNo.setText(previousStructureInfoPointDataModel.getCluster_name());


                        }
                    }
                    if (activity.getIntent().hasExtra(Constants.INTENT_DATA_UnitInfo)) {
                        previousUnitInfoPointDataModel = (UnitInfoDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_UnitInfo);
                    }
                    if (activity.getIntent().hasExtra(Constants.INTENT_DATA_HohInfo)) {
                        previousHohInfoDataModel = (HohInfoDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_HohInfo);
                    }
                    if (activity.getIntent().hasExtra(Constants.INTENT_DATA_MamberInfo)) {
                        memberInfoDataModelArrayList = (ArrayList<MemberInfoDataModel>) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_MamberInfo);
                    }
                }
            }
            if (activity.getIntent().hasExtra(Constants.markerPointGeom)) {
                markerPointGeom = activity.getIntent().getStringExtra(Constants.markerPointGeom);
            }
            if (activity.getIntent().hasExtra(Constants.markerPointLat)) {
                markerPointLat = activity.getIntent().getDoubleExtra(Constants.markerPointLat, 0.0);
            }
            if (activity.getIntent().hasExtra(Constants.markerPointLong)) {
                markerPointLong = activity.getIntent().getDoubleExtra(Constants.markerPointLong, 0.0);
            }

            if (previousStructureInfoPointDataModel != null && previousStructureInfoPointDataModel.getHut_number() != null) {
                et_surveyUniqueIdNumber.setText(previousStructureInfoPointDataModel.getHut_number());
            }
            floor_no = String.valueOf(previousStructureInfoPointDataModel.getNo_of_floors());
            autoCompUnitFloorDetails.setText(floor_no);


            btn_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent(activity, MapActivity.class));
                        finish();
                    } catch (Exception ex) {
                        AppLog.logData(activity,"Exception in btn_Cancel setOnClickListener: " + ex.getMessage());
                        AppLog.e("Exception in btn_Cancel setOnClickListener: " + ex.getMessage());
                    }
                }
            });

            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (Utils.isNullOrEmpty(autoCompUnitFloorDetails.getText().toString())) {
                            Utils.setError(autoCompUnitFloorDetails, "Select no. of floor", activity);
                        } else {

                            if (flowFlag) {
                                if(previousStructureInfoPointDataModel!=null && previousStructureInfoPointDataModel.getStructure_status()!=null && !previousStructureInfoPointDataModel.getStructure_status().equals("")){
                                    saveStructureDataToLocal(previousStructureInfoPointDataModel.getStructure_status());
                                }else{
                                    saveStructureDataToLocal(Constants.InProgress);
                                }


                                if (previousHohInfoDataModel != null && (memberInfoDataModelArrayList == null || memberInfoDataModelArrayList.size() == 0)) {
                                    activity.startActivity(new Intent(activity, SurveyorActivity.class)
                                            .putExtra(Constants.IS_EDITING, true)
                                            .putExtra(Constants.EDIT_TYPE, Constants.EDIT_StructureInfo)
                                            .putExtra("flow", true)
                                            .putExtra(Constants.viewMode, viewMode)
                                            .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataModel)
                                            .putExtra(Constants.INTENT_DATA_HohInfo, previousHohInfoDataModel)
                                            .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel));
                                    activity.finish();
                                }else if(previousHohInfoDataModel != null && memberInfoDataModelArrayList != null && memberInfoDataModelArrayList.size() > 0) {
                                    activity.startActivity(new Intent(activity, SurveyorActivity.class)
                                            .putExtra(Constants.IS_EDITING, true)
                                            .putExtra(Constants.EDIT_TYPE, Constants.EDIT_StructureInfo)
                                            .putExtra("flow", true)
                                            .putExtra(Constants.viewMode, viewMode)
                                            .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataModel)
                                            .putExtra(Constants.INTENT_DATA_HohInfo, previousHohInfoDataModel)
                                            .putExtra(Constants.INTENT_DATA_MamberInfo, memberInfoDataModelArrayList)
                                            .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel));
                                    activity.finish();
                                }else {
                                    activity.startActivity(new Intent(activity, SurveyorActivity.class)
                                            .putExtra(Constants.IS_EDITING, true)
                                            .putExtra(Constants.EDIT_TYPE, Constants.EDIT_StructureInfo)
                                            .putExtra("flow", true)
                                            .putExtra(Constants.viewMode, viewMode)
                                            .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataModel)
                                            .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel));
                                    activity.finish();
                                }
                            }else {
                                if (previousStructureInfoPointDataModel.getStructure_status().equalsIgnoreCase(Constants.completed))
                                    Utils.shortToast("You can't change the status of already completed structure", activity);
                                 else changeStructureStatus();
                            }
                        }

                    } catch (Exception ex) {
                        AppLog.logData(activity,ex.getMessage());
                        AppLog.e("Exception in btn_next setOnClickListener: " + ex.getMessage());
                    }
                }
            });


        } catch (Exception ex) {
            AppLog.logData(activity,ex.getMessage());
            AppLog.e(ex.getMessage());
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setFocusChange_OnTouch(AutoCompleteTextView autoCompleteTextView) {

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteTextView.setError(null, null);
                autoCompleteTextView.showDropDown();
            }

        });

        autoCompleteTextView.setOnTouchListener((v, event) -> {
            autoCompleteTextView.showDropDown();
            autoCompleteTextView.setError(null, null);
            return false;
        });

        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            autoCompleteTextView.setError(null, null);
        });
    }
private void addFloor(){
    autoCompUnitFloorDetails.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_floor)));
    setFocusChange_OnTouch(autoCompUnitFloorDetails);
    autoCompUnitFloorDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                floor_no = String.valueOf(autoCompUnitFloorDetails.getText());
            } catch (Exception ex) {
                AppLog.e("Exception in autoCompUnitFloorDetails ItemClickListner: " + ex.getMessage());
            }
        }
    });
}

    private void saveStructureDataToLocal(String strutureStatus) {
        try {
            strutureAlertStatus = strutureStatus;
            String objectid = "", rel_gloablId = UUID.randomUUID().toString(), gloablId = "";

            if (previousStructureInfoPointDataModel != null && !Utils.isNullOrEmpty(previousStructureInfoPointDataModel.getObejctId())) {
                objectid = (int) Utils.doubleFormatter(previousStructureInfoPointDataModel.getObejctId()) + "";
                rel_gloablId = previousStructureInfoPointDataModel.getGlobalId();
                gloablId = previousStructureInfoPointDataModel.getGlobalId();
            }

            if (!localSurveyDbViewModel.containsPrimaryKey(structUniqueId)) {

                structureInfoPointDataModel = new StructureInfoPointDataModel(structUniqueId,
                        rel_gloablId,
                        structUniqueId,
                        "GN-" + getRandom(),
                        "",//etNameOfNagarArea.getText().toString(),
                        work_area_name,
                        "",//etPhaseClusterNameNo.getText().toString(),
                        work_area_name,
                        previousStructureInfoPointDataModel.getHut_number(),//etSurveyUniqueIdNumber.getText().toString(),
                        "SN-" + getRandom(),
                        "",
                        Utils.integerFormatter(floor_no),
                        previousStructureInfoPointDataModel.getAddress(),//etAddress.getText().toString(),
                        "",//etTenementNumber.getText().toString(),
                        "", "",
                        "", strutureStatus,
                        surveyor_name, previousStructureInfoPointDataModel.getRemarks(), markerPointGeom, objectid,
                        gloablId, !Utils.isNullOrEmpty(objectid), new Date(),
                        previousStructureInfoPointDataModel.getWard_no(),previousStructureInfoPointDataModel.getSector_no(),previousStructureInfoPointDataModel.getZone_no(), new Date()
                        ,previousStructureInfoPointDataModel.getCountry_name()
                        ,previousStructureInfoPointDataModel.getState_name()
                        ,previousStructureInfoPointDataModel.getCity_name());

                structureInfoPointHashmap.put(structUniqueId, structureInfoPointDataModel);

                new Handler().postDelayed(() -> {
                    localSurveyDbViewModel.insertStructureInfoPointData(structureInfoPointHashmap.get(structUniqueId), activity);
                    Utils.dismissProgress();
                }, 2000);

                if (!activity.getIntent().hasExtra("flow") && !activity.getIntent().getBooleanExtra("flow", false) == true) {
                    if (!flowFlag) {
                        new Handler().postDelayed(() -> {
                            Utils.dismissProgress();
                            //showActionAlertDialogButtonClicked("Structure Status", "Update the status of this structure.", "Save", "Cancel", true, false, false, true, false, false);
                            changeStructureStatus();

                        }, 1000);
                    }
                }


            } else {

                previousStructureInfoPointDataModel = localSurveyDbViewModel.getStructureInfoPointDataModel(structUniqueId);
                structureInfoPointDataModel = new StructureInfoPointDataModel(structUniqueId,
                        rel_gloablId,
                        structUniqueId,
                        previousStructureInfoPointDataModel.getGrid_number(),
                        "",//etNameOfNagarArea.getText().toString(),
                        work_area_name,
                        "",//etPhaseClusterNameNo.getText().toString(),
                        work_area_name,
                        previousStructureInfoPointDataModel.getHut_number(),
                        previousStructureInfoPointDataModel.getStructure_name(),
                        "",
                        Utils.integerFormatter(floor_no),
                        "",//etAddress.getText().toString(),
                        "",//etTenementNumber.getText().toString(),
                        "", "",
                        "", strutureStatus,
                        surveyor_name, "", markerPointGeom, objectid,
                        gloablId, previousStructureInfoPointDataModel.isUploaded(), previousStructureInfoPointDataModel.getDate(),
                        previousStructureInfoPointDataModel.getWard_no(),
                        previousStructureInfoPointDataModel.getSector_no(),
                        previousStructureInfoPointDataModel.getZone_no(),
                        new Date()
                        ,previousStructureInfoPointDataModel.getCountry_name()
                        ,previousStructureInfoPointDataModel.getState_name()
                        ,previousStructureInfoPointDataModel.getCity_name());

                structureInfoPointHashmap.put(structUniqueId, structureInfoPointDataModel);

                new Handler().postDelayed(() -> {
                    localSurveyDbViewModel.updateStructureInfoPointData(structureInfoPointHashmap.get(structUniqueId), activity);
                    Utils.dismissProgress();
                }, 2000);


                if (!activity.getIntent().hasExtra("flow") && !activity.getIntent().getBooleanExtra("flow", false) == true) {
                    if (!flowFlag) {
                        new Handler().postDelayed(() -> {
                            Utils.dismissProgress();

                            showActionAlertDialogButtonClicked("Structure Status", "Update the status of this structure.", "Save", "Cancel", true, false, false, true, false, false);
                            changeStructureStatus();
//                        activity.startActivity(new Intent(activity, MapPageActivity.class));
//                        activity.finish();
                        }, 1000);
                    }
                }
            }
        } catch (Exception ex) {
            AppLog.logData(activity,ex.getMessage());
            AppLog.e("Exception in AddStructureDetail: " + ex.getMessage());
        }
    }

    private String getRandom() {
        return String.format("%04d", new Random().nextInt(1000));
    }

    private void changeStructureStatus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_status_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        TextView textHeader = customLayout.findViewById(R.id.txt_header);
        textHeader.setText("Structure Status");
        TextView textMessage = customLayout.findViewById(R.id.txt_mssage);
        textMessage.setText("Update the status of this structure");
        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);
        MaterialRadioButton radio_inProg = customLayout.findViewById(R.id.radio_inProg);
        MaterialRadioButton radio_hold = customLayout.findViewById(R.id.radio_hold);
        MaterialRadioButton radio_complete = customLayout.findViewById(R.id.radio_complete);
        AutoCompleteTextView remark_complete = customLayout.findViewById(R.id.auto_comp_remarks);
        remark_complete.setVisibility(View.GONE);

        radio_inProg.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                strutureAlertStatus = Constants.InProgress_statusLayer;
            }
        });

        radio_hold.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                strutureAlertStatus = Constants.OnHold_statusLayer;
            }
        });

        radio_complete.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                strutureAlertStatus = Constants.completed_statusLayer;
            }
        });

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(v -> {
            if (!unitInfoStatus && strutureAlertStatus.equalsIgnoreCase(Constants.completed)) {
                Utils.shortToast("Please complete all unit under structure to make it complete.", activity);
            } else {
                dialog.dismiss();
                Utils.showProgress("Please wait...", activity);
                new Handler().postDelayed(() -> {
                    // localSurveyDbViewModel.insertStructureUnitIdStatusData(new StructureUnitIdStatusDataTable(unitUniqueId, structUniqueId, unitAlertStatus), activity);
                    localSurveyDbViewModel.updateStructureStatusDataTable(hutId, strutureAlertStatus);
                    Utils.dismissProgress();
                    activity.startActivity(new Intent(activity, MapActivity.class));
                    activity.finish();
                }, 2000);
            }
        });

        btn_no.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void showActionAlertDialogButtonClicked(String header, String mssage, String btnYes, String btnNo, boolean isRadiogroup, boolean isUnitStatus,
                                                   boolean isAddNewUnit, boolean isStructureStatus,
                                                   boolean isHohNameClick, boolean isMemberNameClick) {

        Boolean isAllUnitCompleted = true;
        statusAlert = "";
        // Create an alert builder
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        // set the custom layout
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_action, null);
        builder.setView(customLayout);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
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
        MaterialRadioButton radio_inProg = customLayout.findViewById(R.id.radio_inProg);
        MaterialRadioButton radio_hold = customLayout.findViewById(R.id.radio_hold);
        MaterialRadioButton radio_complete = customLayout.findViewById(R.id.radio_complete);

        if (isRadiogroup) {
            statusRadioGroup.setVisibility(View.VISIBLE);
        } else {
            statusRadioGroup.setVisibility(View.GONE);
        }

        radio_inProg.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                statusAlert = Constants.InProgress_statusLayer;
            }
        });

        radio_hold.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                statusAlert = Constants.OnHold_statusLayer;
            }
        });

        if (isStructureStatus) {
            unitStatusArray = new ArrayList<>();
            unitStatusArray.addAll(localSurveyDbViewModel.getStructureUnitIdStatusListData(structUniqueId));

            for (String s : unitStatusArray) {
                if (s.equals(Constants.InProgress_statusLayer) || s.equals(Constants.OnHold_statusLayer)) {
                    isAllUnitCompleted = false;
                    break;
                }
            }

        }

        if (isAllUnitCompleted)
            radio_complete.setVisibility(View.VISIBLE);
        else radio_complete.setVisibility(View.GONE);

        radio_complete.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                statusAlert = Constants.completed_statusLayer;
            }
        });

        btn_yes.setOnClickListener(view1 -> {

            if (isRadiogroup) {
                if (Utils.isNullOrEmpty(statusAlert)) {
                    Utils.shortToast("Select the status.", activity);
                    return;
                }

            }
            if (isStructureStatus) {
                strutureAlertStatus = statusAlert;
                Utils.updateProgressMsg("Saving structure info.", activity);
                saveStructureDataToLocal(strutureAlertStatus);
//                showActionAlertDialogButtonClicked("Confirm the action", "Do you want to save this record?",
//                        "Yes", "No", false, false, false, false, false, false);
            } else {
                Utils.shortToast("Record saved.", activity);
                activity.finish();
            }

            dialog.dismiss();
        });

        btn_no.setOnClickListener(view1 -> {
            if (isAddNewUnit) {

                showActionAlertDialogButtonClicked("Structure Status", "Update the status of this structure.", "Save", "Cancel", true, false, false, true, false, false);
            }
            dialog.dismiss();
        });

        img_close.setOnClickListener(view1 -> {

            dialog.dismiss();
        });

        dialog.show();
    }


}