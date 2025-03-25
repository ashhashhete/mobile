package com.igenesys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igenesys.adapter.AttachmentListAdapter;
import com.igenesys.adapter.HorizontalAdapter;
import com.igenesys.adapter.SurveyorViewAttachAdapter;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.databinding.ActivitySurveyorBinding;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.model.AttachmentListImageDetails;
import com.igenesys.model.AutoCompleteModal;
import com.igenesys.model.SurveyorData;
import com.igenesys.model.UserModel;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.AppPermissions;
import com.igenesys.utils.Constants;
import com.igenesys.utils.CorrectImageRotation;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.FullScreenImage;
import com.igenesys.utils.SharedPreferencesHandler;
import com.igenesys.utils.Utils;
import com.igenesys.utils.YesNoBottomSheet;
import com.igenesys.view.FormPageViewModel;
import com.igenesys.view.HOHViewModel;
import com.igenesys.view.ShowFullScreenAttachment;

import org.bouncycastle.crypto.CryptoException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SurveyorActivity extends AppCompatActivity implements View.OnClickListener, SurveyorViewAttachAdapter.OnViewClickListner,AttachmentListAdapter.OnAttachmentItemClickListner {
    private ActivitySurveyorBinding binding;
    UnitInfoDataModel previousUnitInfoPointDataModel;
    SurveyorData surveyorData;
    UnitInfoDataModel unitInfoPointDataModel1;
    private HohInfoDataModel previousHohInfoDataModel = null;
    StructureInfoPointDataModel previousStructureInfoPointDataModel;
    private Calendar myCalendar;
    String structUniqueId;
    UserModel userModel;
    boolean editMode;
    Date currentDate1;
    private View view;
    private ArrayList<MemberInfoDataModel> memberInfoDataModelArrayList;

    private ArrayList<AutoCompleteModal> listDRPOfficersDetails;
    // private ArrayList<AutoCompleteModal> listDRPOfficersName, listDRPOfficersDesignations;

    short radioSelected=0;
    private String buttonName="";

    private ArrayList<AttachmentListImageDetails> userAttachmentList;
    private LocalSurveyDbViewModel localSurveyDbViewModel;
    private List<MediaInfoDataModel> newMediaInfoDataModels;

    private AttachmentListAdapter addImageAdapter;

    private SurveyorViewAttachAdapter viewAttachAdapter;

    List<String> getPreviousMediaFileName=new ArrayList<>();

    ArrayList<MediaInfoDataModel> mediaInfoDataModels1;

    String target_relative_path = "", target_name = "",attachmentFor = "";
    private final int selectCamera = 10, selectGallery = 20, selectPdf = 30;
    private File captureImagePath;
    private Uri imageUri;

    private String unit_relative_path = "";

    private String unit_unique_id = "", unit_rel_global_id = "", unitUniqueId = "";

    private Activity activity=SurveyorActivity.this;
    ArrayList<String> okFileExtensions = new ArrayList<>();
    private int alpha = 0;
    private MediaInfoDataModel updObj;
    private int sss=0;

    private AlertDialog dialogGlobal;
    private List<MediaInfoDataModel> tempMediaObj=null;
    File pdfPathFile = null;

    public static String attrName="";

    ArrayList<String> NMDPLOfficersList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurveyorBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(view);
        initActivity();
    }

    private void initActivity() {
        try {
            Gson gson = new Gson();
            String json = App.getSharedPreferencesHandler().getString("NMDPLOfficersSaveList", null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            NMDPLOfficersList=gson.fromJson(json, type);
            binding.etDrpplName.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, NMDPLOfficersList));
            setFocusChange_OnTouch_N(binding.etDrpplName);


            userModel = App.getInstance().getUserModel();
            localSurveyDbViewModel = ViewModelProviders.of(this).get(LocalSurveyDbViewModel.class);
            listDRPOfficersDetails = Utils.getDrpOfficerDetails();
            okFileExtensions = new ArrayList<>();
            okFileExtensions.add("jpg");
            okFileExtensions.add("png");
            okFileExtensions.add("jpeg");
            okFileExtensions.add("pdf");
            userAttachmentList = new ArrayList<>();
            // listDRPOfficersName = Utils.getDomianList(Constants.drp_officers);
            // listDRPOfficersDesignations = Utils.getDomianList(Constants.drp_officer_designations);
            disableFields();
            setDataIfPresent();
            bindUserDetails();
            // Vidnyan changes end

//            binding.etDrpDesignation.setText("");
//            Calendar calendar = Calendar.getInstance();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//            String currentDate = dateFormat.format(calendar.getTime());
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
            String currentDate = dateFormat.format(calendar.getTime());
            binding.etSurveyDate.setText(currentDate);
//            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String currentTime = timeFormat.format(calendar.getTime());
            binding.etSurveyTime.setText(currentTime);
            binding.layoutFormBottom.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!validateDrpOfficerName()) {
                        return;
                    }
                    List<MediaInfoDataModel> dpdf = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(Constants.Surveyor_Category, unitUniqueId, false,Constants.Drppl_Type);
                    if(!binding.radioLoftYes.isChecked() && !binding.radioLoftNo.isChecked()){
                        Toast.makeText(SurveyorActivity.this, "Please select DRPPL officer available or not", Toast.LENGTH_SHORT).show();
                    }else if(binding.etDrpplName.getText().toString().equals("")){
                        Toast.makeText(SurveyorActivity.this, "Please enter DRPPL Officer name", Toast.LENGTH_SHORT).show();
                    }else if(radioSelected==1 && dpdf.size()<=0){
                        Utils.setError(binding.etDrpplName, "Attachment is mandatory", activity);
                        binding.etDrpplName.requestFocus();
//                        View targetView = binding.etDrpplName;
//                        scrollToView(binding.layoutNewUnitDetails.scrollViewParent,targetView);
                    }else{
                        Utils.updateProgressMsg("Please wait...", SurveyorActivity.this);

                        updateDataIntoModel();

                        LocalSurveyDbViewModel localSurveyDbViewModel=ViewModelProviders.of(SurveyorActivity.this).get(LocalSurveyDbViewModel.class);
                        boolean crossFlag=false;
                        try {
                            if(previousUnitInfoPointDataModel==null){
                                App.getSharedPreferencesHandler().putString("unit_id",unitUniqueId);
                                String id=App.getSharedPreferencesHandler().getString("unit_unique_id");
                                if(localSurveyDbViewModel.getUnitByUniqueId(id)!=null){
                                    previousUnitInfoPointDataModel=localSurveyDbViewModel.getUnitByUniqueId(id).get(0);
                                    editMode=true;
                                    crossFlag=true;
                                }
                            }else{
                                App.getSharedPreferencesHandler().putString("unit_id",previousUnitInfoPointDataModel.getUnit_id());
                            }
                        }catch (Exception ex){
                            ex.getMessage();
                        }

                        boolean hohFlag=false;
                        try {
                            if(previousUnitInfoPointDataModel!=null && previousHohInfoDataModel==null && crossFlag==true){
                                if(localSurveyDbViewModel.getHohByUniqueId(previousUnitInfoPointDataModel.getUnit_id())!=null){
                                    previousHohInfoDataModel=localSurveyDbViewModel.getHohByUniqueId(previousUnitInfoPointDataModel.getUnit_id()).get(0);
                                    editMode=true;
                                    hohFlag=true;
                                }
                            }
                        }catch (Exception ex){
                            ex.getMessage();
                        }
                        boolean intentFlag=false;
                        if(!hohFlag){
                            intentFlag=getIntent().hasExtra(Constants.INTENT_DATA_HohInfo);
                        }else{
                            intentFlag=true;
                        }

                        boolean memberFlag=false;
                        try {
                            if(previousUnitInfoPointDataModel!=null && previousHohInfoDataModel!=null && crossFlag==true && hohFlag==true){
                                if(localSurveyDbViewModel.getMemberInfoData(previousHohInfoDataModel.getHoh_id())!=null){
                                    memberInfoDataModelArrayList=(ArrayList<MemberInfoDataModel>) localSurveyDbViewModel.getMemberInfoData(previousHohInfoDataModel.getHoh_id());
                                    editMode=true;
                                    memberFlag=true;
                                }
                            }
                        }catch (Exception ex){
                            ex.getMessage();
                        }

                        if (intentFlag && previousHohInfoDataModel != null &&
                                (memberInfoDataModelArrayList == null || memberInfoDataModelArrayList.size() == 0)) {
                            Utils.dismissProgress();
                            startActivity(new Intent(SurveyorActivity.this, UnitActivity.class)
                                    .putExtra(Constants.IS_EDITING, editMode)
                                    .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                    .putExtra("flow", true)
                                    .putExtra(Constants.viewMode, "viewMode")
                                    .putExtra(Constants.INTENT_DATA_HohInfo, previousHohInfoDataModel)
                                    .putExtra(Constants.INTENT_DATA_StructureInfo, previousStructureInfoPointDataModel)
                                    .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel)
                                    .putExtra(Constants.INTENT_DATA_SurveyorInfo, surveyorData));

                        }else if(previousHohInfoDataModel != null && memberInfoDataModelArrayList != null && memberInfoDataModelArrayList.size() > 0) {
                            Utils.dismissProgress();
                            startActivity(new Intent(SurveyorActivity.this, UnitActivity.class)
                                    .putExtra(Constants.IS_EDITING, editMode)
                                    .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                    .putExtra("flow", true)
                                    .putExtra(Constants.viewMode, "viewMode")
                                    .putExtra(Constants.INTENT_DATA_HohInfo, previousHohInfoDataModel)
                                    .putExtra(Constants.INTENT_DATA_StructureInfo, previousStructureInfoPointDataModel)
                                    .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel)
                                    .putExtra(Constants.INTENT_DATA_MamberInfo, memberInfoDataModelArrayList)
                                    .putExtra(Constants.INTENT_DATA_SurveyorInfo, surveyorData));
                        }else {
                            Utils.dismissProgress();
                            startActivity(new Intent(SurveyorActivity.this, UnitActivity.class)
                                    .putExtra(Constants.IS_EDITING, editMode)
                                    .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                    .putExtra("flow", true)
                                    .putExtra(Constants.viewMode, "viewMode")
                                    .putExtra(Constants.INTENT_DATA_StructureInfo, previousStructureInfoPointDataModel)
                                    .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel)
                                    .putExtra(Constants.INTENT_DATA_SurveyorInfo, surveyorData));
                        }
                    }
                }
            });

            binding.autoCompNameDRP.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, listDRPOfficersDetails));
            setFocusChange_OnTouch(binding.autoCompNameDRP, listDRPOfficersDetails);

            binding.autoCompDesDRP.setEnabled(false);
            // binding.etDrpOtherDesignation.setEnabled(false);

            // binding.autoCompDesDRP.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, listDRPOfficersDesignations));
            // setFocusChange_OnTouch(binding.autoCompDesDRP, listDRPOfficersDesignations);

            binding.etDrpOtherName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        binding.etDrpOtherName.setText(Utils.capitalizeEachWord(binding.etDrpOtherName.getText().toString()));
                    }
                }
            });

            binding.etDrpOtherDesignation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        binding.etDrpOtherDesignation.setText(Utils.capitalizeEachWord(binding.etDrpOtherDesignation.getText().toString()));
                    }
                }
            });

           /* binding.autoCompNameDRP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                   if (!hasFocus) {
                        binding.autoCompNameDRP.setText(Utils.capitalizeEachWord(binding.autoCompNameDRP.getText().toString()));
                    }
                }
            });*/
            binding.layoutFormBottom.btnCancel.setOnClickListener(view -> {
                YesNoBottomSheet.geInstance(SurveyorActivity.this, "Do you want to close the form?", getResources().getString(R.string.yesBtn), getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {
                    @Override

                    public void yesBtn() {
                        finish();
                    }

                    @Override
                    public void noBtn() {

                    }
                }).show(((AppCompatActivity) this).getSupportFragmentManager(), "");
            });
        } catch (Exception ex) {
            AppLog.logData(this,ex.getMessage());
            AppLog.e(ex.getMessage());
        }


        binding.radioLoftYes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                radioSelected=1;
                binding.nameTitleDrppl.setVisibility(View.VISIBLE);
                binding.drpplLayout.setVisibility(View.VISIBLE);
                binding.etDrpplName.setVisibility(View.VISIBLE);
                binding.btnAttachDrppl.setVisibility(View.VISIBLE);
            }
        });

        binding.radioLoftNo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                radioSelected=0;
                binding.nameTitleDrppl.setVisibility(View.VISIBLE);
                binding.drpplLayout.setVisibility(View.VISIBLE);
                binding.etDrpplName.setVisibility(View.VISIBLE);
                binding.btnAttachDrppl.setVisibility(View.GONE);
                try {
                    List<MediaInfoDataModel> dpdf=new ArrayList<>();
                    dpdf = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(Constants.Surveyor_Category, unitUniqueId, false,Constants.Drppl_Type);
                    if(dpdf!=null && dpdf.size()>0){
                        onAttachmentDeletedClicked(dpdf,2,0, null, "");
                    }
                }catch (Exception ex){
                    ex.getMessage();
                }
            }
        });

        binding.btnAttachDrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attrName=Constants.Drp_Type;
                showDocumentPopup(1);
            }
        });

        binding.btnAttachDrppl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attrName=Constants.Drppl_Type;
                showDocumentPopup(2);
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setFocusChange_OnTouch(AutoCompleteTextView autoCompleteTextView) {

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            try {
                if (hasFocus) {
                    autoCompleteTextView.setError(null, null);
                    autoCompleteTextView.showDropDown();
                }
            } catch (Exception e){
                AppLog.e(e.getMessage());
            }
        });

        autoCompleteTextView.setOnTouchListener((v, event) -> {
            try {
                autoCompleteTextView.showDropDown();
                autoCompleteTextView.setError(null, null);
            } catch (Exception e){
                AppLog.e(e.getMessage());
            }
            return false;
        });

        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            autoCompleteTextView.setError(null, null);
            try {
                if (adapterView.getAdapter().getItem(i) instanceof AutoCompleteModal) {
                    AutoCompleteModal selected = (AutoCompleteModal) adapterView.getAdapter().getItem(i);
                    autoCompleteTextView.setTag(selected.code);
                } else if (adapterView.getAdapter().getItem(i) instanceof String) {
                    autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
                }
            } catch (Exception ex) {
                AppLog.logData(this, ex.getMessage());
                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
            }
        });
    }

    public void bindUserDetails() {

        binding.etSurveyorName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        if (userModel != null) {

            //Vidnyan changes Start
            binding.etSurveyorName.setTag(userModel.getUser_name());

            if (userModel.getFirst_name() != null && !userModel.getFirst_name().isEmpty()) {
                if (userModel.getLast_name() != null && !userModel.getLast_name().isEmpty()) {
                    binding.etSurveyorName.setText(String.format("%s %s", userModel.getFirst_name(), userModel.getLast_name()));
                } else {
                    binding.etSurveyorName.setText(userModel.getFirst_name());
                }
            } else {
                binding.etSurveyorName.setText("Name Not Mentioned");
            }
        }

        binding.etSurveyorDesignation.setText("Field Surveyor");
    }

    @Override  // vidnyan chnages starts
    protected void onResume() {
        super.onResume();

        binding.autoCompNameDRP.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, listDRPOfficersDetails));
        setFocusChange_OnTouch(binding.autoCompNameDRP, listDRPOfficersDetails);

        if (pdfPathFile != null) {
            try {
                CryptoUtilsTest.encryptFileinAES(pdfPathFile, 1);
            } catch (CryptoException e) {
                AppLog.logData(activity,e.getMessage());
                throw new RuntimeException(e);
            }
        }

        // binding.autoCompDesDRP.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, listDRPOfficersDesignations));
        // setFocusChange_OnTouch(binding.autoCompDesDRP, listDRPOfficersDesignations);

    } // vidnyan changes ends

    @SuppressLint("ClickableViewAccessibility")
    private void setFocusChange_OnTouch(AutoCompleteTextView autoCompleteTextView, ArrayList<AutoCompleteModal> listItems) {

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            try {
                if (hasFocus) {
                    autoCompleteTextView.setError(null, null);
                    autoCompleteTextView.showDropDown();
                } else {

                    boolean isFound = false;

                    for (AutoCompleteModal autoComplete : listItems) {
                        if (autoComplete.description.equalsIgnoreCase(autoCompleteTextView.getText().toString())) {
                            isFound = true;
                            break;
                        }
                    }

                    if (!isFound) {

                        // If list doesn't contains inserted text item
                        autoCompleteTextView.setText("");

                        if (autoCompleteTextView.getId() == binding.autoCompNameDRP.getId()) {
                            Toast.makeText(SurveyorActivity.this, "Select correct value from DRP Surveyor Name", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from DRP Surveyor Name");
                            binding.etDrpOtherName.setText("");
                            binding.etDrpOtherName.setVisibility(View.GONE);
                            binding.autoCompDesDRP.setText("");
                            binding.etDrpOtherDesignation.setText("");
                            binding.etDrpOtherDesignation.setVisibility(View.GONE);
                        }/* else if(autoCompleteTextView.getId() == binding.autoCompDesDRP.getId()) {
                            Toast.makeText(SurveyorActivity.this, "Select correct value from DRP Surveyor Designation", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from DRP Surveyor Designation");
                            binding.etDrpOtherDesignation.setText("");
                            binding.etDrpOtherDesignation.setVisibility(View.GONE);
                        }*/
                    }
                }
            }catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });

        autoCompleteTextView.setOnTouchListener((v, event) -> {
            try {
                autoCompleteTextView.showDropDown();
                autoCompleteTextView.setError(null, null);
            }catch (Exception e) {
                AppLog.e(e.getMessage());
            }
            return false;
        });

        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            autoCompleteTextView.setError(null, null);
            try {
                AutoCompleteModal selected = (AutoCompleteModal) adapterView.getAdapter().getItem(i);

                if(autoCompleteTextView.getId() == binding.autoCompNameDRP.getId()) {
                    if (selected.description.toString().equals(Constants.dropdown_others)) {
                        binding.etDrpOtherName.setVisibility(View.VISIBLE);
                        binding.etDrpOtherName.setText("");
                        binding.etDrpOtherDesignation.setVisibility(View.VISIBLE);
                        binding.etDrpOtherDesignation.setText("");
                    } else {
                        binding.etDrpOtherName.setVisibility(View.GONE);
                        binding.etDrpOtherName.setText("");
                        binding.etDrpOtherDesignation.setVisibility(View.GONE);
                        binding.etDrpOtherDesignation.setText("");
                    }

                    binding.autoCompDesDRP.setText(selected.code.toString());
                }

               /* if(autoCompleteTextView.getId() == binding.autoCompDesDRP.getId()) {
                    if (selected.code.toString().equals(Constants.dropdown_others)) {
                        binding.etDrpOtherDesignation.setVisibility(View.VISIBLE);
                    } else {
                        binding.etDrpOtherDesignation.setVisibility(View.GONE);
                        binding.etDrpOtherDesignation.setText("");
                    }
                }*/

                autoCompleteTextView.setTag(selected.code);
            }catch(Exception ex){
                AppLog.logData(this,ex.getMessage());
                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
            }
        });
    }

    public boolean validateDrpOfficerName() {

        if(binding.autoCompNameDRP.getText().toString().isEmpty())
            return true;

        boolean isFound = false;

        for (AutoCompleteModal autoComplete : listDRPOfficersDetails) {
            if (autoComplete.description.equalsIgnoreCase(binding.autoCompNameDRP.getText().toString())) {
                isFound = true;
                break;
            }
        }

        if (!isFound) {

            // If list doesn't contains inserted text item
            binding.autoCompNameDRP.setText("");

            Toast.makeText(SurveyorActivity.this, "Select correct value from DRP Surveyor Name", Toast.LENGTH_SHORT).show();
            binding.autoCompNameDRP.setError("Select correct value from DRP Surveyor Name");
            binding.etDrpOtherName.setText("");
            binding.etDrpOtherName.setVisibility(View.GONE);
            binding.autoCompDesDRP.setText("");
            binding.etDrpOtherDesignation.setText("");
            binding.etDrpOtherDesignation.setVisibility(View.GONE);
        }

        return isFound;
    }

    private void disableFields() {
        binding.etSurveyorName.setFocusable(false);
        binding.etSurveyorName.setEnabled(false);
        binding.etSurveyorName.setBackgroundResource(R.drawable.rounded_blue_edittext);

        binding.etSurveyorDesignation.setFocusable(false);
        binding.etSurveyorDesignation.setEnabled(false);
        binding.etSurveyorDesignation.setBackgroundResource(R.drawable.rounded_blue_edittext);

        binding.etSurveyDate.setFocusable(false);
        binding.etSurveyDate.setEnabled(false);
        binding.etSurveyDate.setBackgroundResource(R.drawable.rounded_blue_edittext);

        binding.etSurveyTime.setFocusable(false);
        binding.etSurveyTime.setEnabled(false);
        binding.etSurveyTime.setBackgroundResource(R.drawable.rounded_blue_edittext);
    }

    private void setDataIfPresent() {
        if (getIntent().hasExtra(Constants.IS_EDITING)) {
            if (getIntent().getBooleanExtra(Constants.IS_EDITING, false)) {
                if (getIntent().hasExtra(Constants.INTENT_DATA_StructureInfo)) {
                    previousStructureInfoPointDataModel = (StructureInfoPointDataModel) getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);
                    if (previousStructureInfoPointDataModel != null) {

                        structUniqueId = previousStructureInfoPointDataModel.getStructure_id();

                    }
                    if (getIntent().hasExtra(Constants.INTENT_DATA_UnitInfo)) {
                        previousUnitInfoPointDataModel = (UnitInfoDataModel) getIntent().getSerializableExtra(Constants.INTENT_DATA_UnitInfo);
                    }
                    if (getIntent().hasExtra(Constants.INTENT_DATA_HohInfo)) {
                        previousHohInfoDataModel = (HohInfoDataModel) getIntent().getSerializableExtra(Constants.INTENT_DATA_HohInfo);
                    }
                    if (getIntent().hasExtra(Constants.INTENT_DATA_MamberInfo)) {
                        memberInfoDataModelArrayList = (ArrayList<MemberInfoDataModel>) getIntent().getSerializableExtra(Constants.INTENT_DATA_MamberInfo);
                    }
                }

                if (previousUnitInfoPointDataModel != null) {

                    unitUniqueId = previousUnitInfoPointDataModel.getUnit_id();
                    unit_unique_id = previousUnitInfoPointDataModel.getUnit_unique_id();
                    unit_relative_path = previousUnitInfoPointDataModel.getRelative_path();

                    binding.etDrpOtherDesignation.setFocusable(false);
                    binding.etDrpOtherDesignation.setEnabled(false);
                    binding.etDrpOtherDesignation.setBackgroundResource(R.drawable.rounded_blue_edittext);

                    binding.etDrpOtherName.setFocusable(false);
                    binding.etDrpOtherName.setEnabled(false);
                    binding.etDrpOtherName.setBackgroundResource(R.drawable.rounded_blue_edittext);

                    binding.autoCompNameDRP.setFocusable(false);
                    binding.autoCompNameDRP.setEnabled(false);
                    binding.autoCompNameDRP.setBackgroundResource(R.drawable.rounded_blue_edittext);


                    editMode = true;


                    binding.autoCompNameDRP.setText(previousUnitInfoPointDataModel.getDrp_officer_name());
                    binding.etDrpplName.setText(previousUnitInfoPointDataModel.getDrppl_officer_name());
                    short yn=previousUnitInfoPointDataModel.getIs_drppl_officer_available();
                    radioSelected=yn;
                    if(yn==0){
                        binding.radioLoftNo.setChecked(true);
                    }else if(yn==1){
                        binding.radioLoftYes.setChecked(true);
                        binding.btnAttachDrppl.setVisibility(View.VISIBLE);
                    }
                    if(previousUnitInfoPointDataModel.getGlobalId() != null && !previousUnitInfoPointDataModel.getGlobalId().isEmpty()){
                        if(previousUnitInfoPointDataModel.getDrppl_officer_name() != null && !previousUnitInfoPointDataModel.getDrppl_officer_name().isEmpty()){
                            binding.etDrpplName.setFocusable(false);
                            binding.etDrpplName.setEnabled(false);
                            binding.etDrpplName.setBackgroundResource(R.drawable.rounded_blue_edittext);
                            binding.radioLoftNo.setClickable(false);
                            binding.radioLoftYes.setClickable(false);
                        }else{
                            binding.radioLoftYes.setChecked(false);
                            binding.radioLoftNo.setChecked(true);
                            radioSelected=0;
                            binding.radioLoftNo.setClickable(false);
                            binding.radioLoftYes.setClickable(false);
                        }
                    }

                    bindUserDetails();




                    if(previousUnitInfoPointDataModel.getDrp_officer_name().equalsIgnoreCase(Constants.dropdown_others)) {
                        binding.etDrpOtherName.setText(previousUnitInfoPointDataModel.getDrp_officer_name_other());
                        binding.etDrpOtherName.setVisibility(View.VISIBLE);
                    }

                    binding.autoCompDesDRP.setText(previousUnitInfoPointDataModel.getDrp_officer_desig());

                    if(previousUnitInfoPointDataModel.getDrp_officer_desig().equalsIgnoreCase(Constants.dropdown_others)) {
                        binding.etDrpOtherDesignation.setText(previousUnitInfoPointDataModel.getDrp_officer_desig_other());
                        binding.etDrpOtherDesignation.setVisibility(View.VISIBLE);
                    }

                    try {
                        currentDate1 = new Date(previousUnitInfoPointDataModel.getLast_edited_date().toString());

                        // Format the date as "DD/MM/YYYY"
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String formattedDate = dateFormat.format(currentDate1);

                        // Format the time as "HH:mm:a"
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:a", Locale.getDefault());
                        String formattedTime = timeFormat.format(currentDate1);
                        binding.etSurveyDate.setText(formattedDate);
                        binding.etSurveyTime.setText(formattedTime);

                    } catch (Exception e) {
                        AppLog.logData(this,e.getMessage());
                        AppLog.e("Exception = " + e.getMessage());
                        Log.i("FormPAge resetUnitDetails=", "step 8" + e.getMessage());

                    }
                }else{
                    try {
                        if(previousUnitInfoPointDataModel==null){
                            unitUniqueId = "U_" + Utils.getEpochDateStamp();
                            unit_unique_id = unitUniqueId;
                            unit_relative_path = "/" + structUniqueId + "/" + unitUniqueId + "/";
                        }
                    }catch (Exception ex){
                        ex.getMessage();
                    }
                }
            }
        }
    }

    private void updateDataIntoModel() {

        if(binding.autoCompNameDRP.getText().toString().equalsIgnoreCase(Constants.dropdown_others)) {
            binding.etDrpOtherName.setText(Utils.capitalizeEachWord(binding.etDrpOtherName.getText().toString()));
        } else {
            binding.autoCompNameDRP.setText(Utils.capitalizeEachWord(binding.autoCompNameDRP.getText().toString()));
        }

        if(binding.autoCompDesDRP.getText().toString().equalsIgnoreCase(Constants.dropdown_others)) {
            binding.etDrpOtherDesignation.setText(Utils.capitalizeEachWord(binding.etDrpOtherDesignation.getText().toString()));
        } else {
            binding.autoCompDesDRP.setText(Utils.capitalizeEachWord(binding.autoCompDesDRP.getText().toString()));
        }

        String userName="";
        if(userModel!=null || userModel.getUser_name()!=null){
            userName=userModel.getUser_name();
        }
        surveyorData = new SurveyorData(
                (binding.etSurveyorName.getTag() != null ? binding.etSurveyorName.getTag().toString() : userName),
                binding.etSurveyorDesignation.getText().toString(),
                binding.autoCompNameDRP.getText().toString(),
                binding.etDrpOtherName.getText().toString(),
                binding.autoCompDesDRP.getText().toString(),
                binding.etDrpOtherDesignation.getText().toString(),
                binding.etSurveyDate.getText().toString(),
                "" + binding.etSurveyTime.getText().toString(),
                previousStructureInfoPointDataModel.getHut_number(),
                ""+previousStructureInfoPointDataModel.getNo_of_floors(),(short) radioSelected,binding.etDrpplName.getText().toString());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_surveyDate: {
                DatePickerDialog.OnDateSetListener date = (dview, year, month, day) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, day);

                    binding.etSurveyDate.setText("" + day + "/" + (month + 1) + "/" + year);

                };
                DatePickerDialog dialog = new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();

            }

            break;
        }
    }

    @Override
    public void onBackPressed() {
        closeFormPopup();
    }

    private void closeFormPopup() {
        YesNoBottomSheet.geInstance(this, getString(R.string.close_form_msg), getResources().getString(R.string.yesBtn), getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {

            @Override
            public void yesBtn() {
                finish();
            }

            @Override
            public void noBtn() {

            }
        }).show(((AppCompatActivity) this).getSupportFragmentManager(), "");
    }



    private void showDocumentPopup(int usageType) {
        try {
            String[] arr = new String[30];

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View customLayout = getLayoutInflater().inflate(R.layout.custom_document_layout, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            dialogGlobal = dialog;
            AutoCompleteTextView autoCompDocType = customLayout.findViewById(R.id.autoCompDocType);
            EditText et_dob = customLayout.findViewById(R.id.et_dob);
            LinearLayout btn_add = customLayout.findViewById(R.id.btn_add);
            LinearLayout addErrorLayout = customLayout.findViewById(R.id.addErrorLayout);
            LinearLayout btn_Browse_Doc = customLayout.findViewById(R.id.btn_Browse_Doc);
            LinearLayout addLayout = customLayout.findViewById(R.id.addLayout);
            LinearLayout viewLayout = customLayout.findViewById(R.id.viewLayout);
            LinearLayout viewNewLayout = customLayout.findViewById(R.id.viewNewLayout);
            LinearLayout deleteLayout = customLayout.findViewById(R.id.deleteLayout);
            AppCompatButton btnCancel = customLayout.findViewById(R.id.btn_Cancel);
            EditText title = customLayout.findViewById(R.id.docTitle);
            TextView docTitle = customLayout.findViewById(R.id.docTitle);
            docTitle.setText(Constants.Surveyor_Category);
            TextView addTab = customLayout.findViewById(R.id.addTab);
            TextView viewTab = customLayout.findViewById(R.id.viewTab);
            LinearLayout viewNoRecord = customLayout.findViewById(R.id.noRecordLyt);
            TextView edtypeProof = customLayout.findViewById(R.id.edtypeProof);
            TextView addErrorTextView = customLayout.findViewById(R.id.addErrorTextView);

            ImageView img_close = customLayout.findViewById(R.id.img_close);
            EditText calDeleteView = customLayout.findViewById(R.id.calDeleteView);
            LinearLayout btnBrowseEdit = customLayout.findViewById(R.id.btnBrowseEdit);
            EditText et_doc_remarks = customLayout.findViewById(R.id.et_doc_remarks);

            if (usageType == 1) {
                    buttonName = Constants.Surveyor_Category;
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unitUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = getResources().getStringArray(R.array.drp_type);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
            }else if (usageType == 2) {
                buttonName = Constants.Surveyor_Category;
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unitUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = getResources().getStringArray(R.array.drppl_type);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            }

            img_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userAttachmentList = new ArrayList<>();
                    dialog.dismiss();
                }
            });

            RecyclerView imageRecycler = customLayout.findViewById(R.id.imageRecycler);
            RecyclerView viewRecycler = customLayout.findViewById(R.id.viewRecycler);
            RecyclerView imageRecyclerDelete = customLayout.findViewById(R.id.imageRecyclerDelete);
            imageRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            imageRecycler.setAdapter(new HorizontalAdapter(new int[]{R.drawable.img_placeholder, R.mipmap.ic_launcher, R.drawable.img_placeholder, R.mipmap.ic_launcher, R.drawable.img_placeholder}, newMediaInfoDataModels, 1, this));
            imageRecyclerDelete.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            RecyclerView addImageRecycler = customLayout.findViewById(R.id.addImageRecycler);
            addImageAdapter = new AttachmentListAdapter(this, null, "", this);
            addImageRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            addImageRecycler.setAdapter(addImageAdapter);

            addTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewTab.setTextColor(getResources().getColor(R.color.black));
                    addTab.setTextColor(getResources().getColor(R.color.summaryEditBoarderColor));
                    addLayout.setVisibility(View.VISIBLE);
                    viewLayout.setVisibility(View.GONE);
                    viewNoRecord.setVisibility(View.GONE);
                    viewNewLayout.setVisibility(View.GONE);
                    deleteLayout.setVisibility(View.GONE);
                    docTitle.setText(buttonName);
                }
            });

            viewTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userAttachmentList = new ArrayList<>();
                    edtypeProof.setText(buttonName);
                    addTab.setTextColor(getResources().getColor(R.color.black));
                    viewTab.setTextColor(getResources().getColor(R.color.summaryEditBoarderColor));
                    addLayout.setVisibility(View.GONE);
                    deleteLayout.setVisibility(View.GONE);
                    viewLayout.setVisibility(View.GONE);
                    viewNewLayout.setVisibility(View.VISIBLE);
                    imageRecyclerDelete.setVisibility(View.GONE);

//                        List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(buttonName, unitUniqueId, false);
                        List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(Constants.Surveyor_Category, unitUniqueId, false,attrName);
                        newMediaInfoDataModels = getMediaInfoData;
                        if (newMediaInfoDataModels != null && newMediaInfoDataModels.size()>0){
                            new Handler().postDelayed(() -> {
                                viewRecycler.setLayoutManager(new LinearLayoutManager(SurveyorActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                viewAttachAdapter = new SurveyorViewAttachAdapter(getMediaInfoData, 1, SurveyorActivity.this, unit_relative_path, unitUniqueId, SurveyorActivity.this, localSurveyDbViewModel);
                                viewRecycler.setAdapter(viewAttachAdapter);
                            }, 1000);
                        }else {
                            viewNoRecord.setVisibility(View.VISIBLE);
                            viewNewLayout.setVisibility(View.GONE);
                        }


                }
            });

            DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                et_dob.setText("" + day + "/" + (month + 1) + "/" + year);
            };

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userAttachmentList = new ArrayList<>();
                    dialog.dismiss();
                }
            });

            btn_Browse_Doc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (autoCompDocType.getText().toString().equals("")) {
                        addErrorLayout.setVisibility(View.VISIBLE);
                        addErrorTextView.setText("Please select document type");
                    } else {
                        addErrorLayout.setVisibility(View.GONE);
                        String name="";
                        if(usageType==1){
                            name="DRP_Officer_Photo";
                        }else if(usageType==2){
                            name="DRPPL_Officer_Photo";
                        }
                        if(userAttachmentList.size()==1){
                            Utils.shortToast("Please remove previous attached image first!", SurveyorActivity.this);
                        }else{
                            //String fileName = Utils.getAttachmentFileName(name,autoCompDocType.getText().toString());
                            String fileName = Utils.getAttachmentFileName(name);
                            showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), unit_relative_path, fileName);
                        }
                    }
                }
            });

            btnBrowseEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name="";
                    if(usageType==1){
                        name="DRP_Officer_Photo";
                    }else if(usageType==2){
                        name="DRPPL_Officer_Photo";
                    }
//                        String fileName = Utils.getAttachmentFileName(name,autoCompDocType.getText().toString());
                        String fileName = Utils.getAttachmentFileName(name);
                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), unit_relative_path, fileName);
                }
            });


            et_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dialog = new DatePickerDialog(SurveyorActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMaxDate(new Date().getTime());
                    dialog.show();
                }
            });

            calDeleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dialog = new DatePickerDialog(SurveyorActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMaxDate(new Date().getTime());
                    dialog.show();
                }
            });

            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (autoCompDocType.getText().toString().equals("")) {
                        addErrorLayout.setVisibility(View.VISIBLE);
                        addErrorTextView.setText("Please select document type");
                    } else if (userAttachmentList.size() < 1) {
                        addErrorLayout.setVisibility(View.VISIBLE);
                        addErrorTextView.setText("Please attach a file");
                    } else {
                        addErrorLayout.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            ArrayList<MediaInfoDataModel> attach = new ArrayList<>();
                                attach = getmediaInfoDataList(userAttachmentList, unit_relative_path, Constants.unit_infoLayer,
                                        unitUniqueId, "");


                            if (attach.size() > 0) {

                                mediaInfoDataModels1 = new ArrayList<>();

                                attach.get(0).setSurveyorDoc(true);
                                attach.get(0).setDocument_remarks(et_doc_remarks.getText().toString());
                                attach.get(0).setDocument_category(buttonName);
                                attach.get(0).setDocument_type(autoCompDocType.getText().toString());
                                attach.get(0).setUploadMediaCount(userAttachmentList.size());
                                attach.get(0).setLocal(true);
                                attach.get(0).setHaveDelete(false);

                                ArrayList<String> listImageDetails = new ArrayList<>();
                                ArrayList<AttachmentItemList> attachmentItems = new ArrayList<>();
                                for (int i = 0; i < userAttachmentList.size(); i++) {
                                    listImageDetails.add(userAttachmentList.get(i).getFilePath());
                                    AttachmentItemList at = new AttachmentItemList(0, userAttachmentList.get(i).getFileName(), userAttachmentList.get(i).getFilePath(), false, false);
                                    attachmentItems.add(at);
                                }
                                attach.get(0).setUploadMediaList(listImageDetails);
                                attach.get(0).setAttachmentItemLists(attachmentItems);
                                mediaInfoDataModels1.addAll(attach);
                                List<MediaInfoDataModel> mediaInfoData = mediaInfoDataModels1;
                                localSurveyDbViewModel.insertAllMediaInfoPointData(mediaInfoData, SurveyorActivity.this);
                                userAttachmentList = new ArrayList<>();
                                dialog.dismiss();
                                Utils.dismissProgress();
                            }
                        }, 0);
                    }

                }
            });


            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            Utils.shortToast("Exception in showDocumentPopup:" + e.getMessage(), SurveyorActivity.this);
            AppLog.e("Exception in showDocumentPopup:" + e.getMessage());
        }
    }


    private String[] getUpdatedDocList(List<MediaInfoDataModel> newMediaInfoDataModels, String[] arr) {
        ArrayList<String> tempList = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].equalsIgnoreCase("Attachment of the notice on door")){
                tempList.add("Notice_Pasted");
            }else{
                tempList.add(arr[i]);
            }
        }
        for (int i = 0; i < newMediaInfoDataModels.size(); i++) {
            if (tempList.contains(newMediaInfoDataModels.get(i).getDocument_type())) {
                tempList.remove(newMediaInfoDataModels.get(i).getDocument_type());
            }
        }
        String[] brr = new String[tempList.size()];
        for (int i = 0; i < tempList.size(); i++) {
            brr[i] = tempList.get(i).toString();
        }

        return brr;
    }


    public void showAttachmentAlertDialogButtonClicked(String clickedFrom, String relative_path, String name) {

        try {
            if (!Utils.checkAutodateTimeValidation(this)) {
                return;
            }

            attachmentFor = clickedFrom;
            target_relative_path = relative_path;
            target_name = name;

            // Create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // set the custom layout
            final View customLayout = getLayoutInflater().inflate(R.layout.custom_alert_attachment_picker, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            TextView txt_Camera = customLayout.findViewById(R.id.txt_Camera);
            TextView txt_Gallery = customLayout.findViewById(R.id.txt_Gallery);
            TextView txt_ChooseFile = customLayout.findViewById(R.id.txt_ChooseFile);

            txt_Camera.setOnClickListener(view1 -> {
                if (AppPermissions.cameraPermission(this, true)) {
                    imageUri = Utils.getCaptureImageOutputUri(this, target_relative_path, target_name);
                    captureImagePath = Utils.getFile(this, target_relative_path, target_name);
                    if (imageUri != null) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(cameraIntent, selectCamera);
                        dialog.dismiss();
                    } else Utils.shortToast("Unable to capture image.", this);
                }

            });

            txt_Gallery.setOnClickListener(view1 -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), selectGallery);

                dialog.dismiss();
            });

            txt_ChooseFile.setOnClickListener(view1 -> {

                Intent intent = new Intent();

                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, true);
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), selectPdf);

                dialog.dismiss();
            });

            dialog.show();
        } catch (Exception e) {
            Utils.shortToast("Exception in showAttachmentAlertDialogButtonClicked:" + e.getMessage(), this);
            AppLog.e("Exception in showAttachmentAlertDialogButtonClicked:" + e.getMessage());
        }
    }


    private ArrayList<MediaInfoDataModel> getmediaInfoDataList(ArrayList<AttachmentListImageDetails> attachmentImageDetailsList, String relative_path,
                                                               String infoLayer, String id, String globlId) {
        ArrayList<MediaInfoDataModel> mediaInfoDataModels1 = new ArrayList<>();
        MediaInfoDataModel mm = null;
        try {
            for (AttachmentListImageDetails attachmentListImageDetails : attachmentImageDetailsList) {
                if (!getPreviousMediaFileName.contains(attachmentListImageDetails.getFileName()))
                    if (attachmentListImageDetails.getFile().exists()) {
                        String contentType = Utils.getContentType(attachmentListImageDetails.getFilePath());
                        int dataSize = (int) attachmentListImageDetails.getFile().length();
                        getPreviousMediaFileName.add(attachmentListImageDetails.getFileName());
                        mm = new MediaInfoDataModel(contentType,
                                attachmentListImageDetails.getFileName(), dataSize, "",
                                attachmentListImageDetails.getFilePath(),
                                (short) 0, (short) 0,
                                infoLayer, id, globlId, relative_path,
                                "", "", false, new Date(), new Date());

                    }
            }

            if(mm != null)
                mediaInfoDataModels1.add(mm);
        } catch (Exception e) {
            AppLog.logData(this,e.getMessage());
            Utils.shortToast("Exception in getmediaInfoDataList:" + e.getMessage(), this);
            AppLog.e("Exception in getmediaInfoDataList:" + e.getMessage());
        }
        return mediaInfoDataModels1;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Boolean isOkFileExtensions = true;
            Boolean isOkFileSize = true;
            Uri finalUri = null;
            File finalFile = null;
            String finalFileName = "";

            if (requestCode == selectCamera) {
                if (imageUri != null) {
                    finalUri = imageUri;
                    finalFile = captureImagePath;
                    if (Utils.getFileSizeFromFile(activity, finalFile)) {
                        isOkFileSize = true;
                        finalFileName = Utils.getFileName(finalUri, activity);
                    } else {
                        isOkFileSize = false;
                    }
                    Utils.deleteRecentClickedImage(activity);
                } else {
                    Utils.shortToast("Capture again.", activity);
                }
            } else if (requestCode == selectGallery || requestCode == selectPdf ) {
                finalUri = data.getData();

                if (okFileExtensions.contains(Utils.getFileExt(finalUri, activity))) {
                    isOkFileExtensions = true;
                    if (Utils.getFileSizeFromUri(activity, finalUri)) {
                        isOkFileSize = true;
                        finalFile = Utils.copyFile(activity, finalUri, target_relative_path, target_name);
                        finalFileName = finalFile.getName();

                        if (finalFileName.endsWith("pdf")) {
                            if (!Utils.isValidPdf(finalFile.getPath())) {
                                Utils.shortToast("Seems attached pdf file is Corrupted Or Tampered. Kindly retry again or attach any valid pdf file!!", activity);
                                return;
                            }
                        }
                    } else {
                        isOkFileSize = false;
                    }
                } else {
                    isOkFileSize = false;
                    isOkFileExtensions = false;
                }
            }

            if (!isOkFileExtensions) {
                    Utils.showMessagePopup("." + Utils.getFileExt(finalUri, activity) + " is an invalid attachment type. Accepted file types are jpg, png, jpeg, and pdf.", activity);
                return;
            }

            if (!isOkFileSize) {
                return;
            }

            if (finalUri != null && finalFile != null) {
                try {
                    sss = 0;
                    CryptoUtilsTest.encryptFileinAES(finalFile, 1);
                    switch (attachmentFor) {
                        case Constants.Drp_Type:
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Drppl_Type:
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                    }
                    if (addImageAdapter != null) {
                        addImageAdapter.setAttachmentListImageDetails(userAttachmentList);
                    }


                    if (alpha == 1 && updObj != null) {
                        String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;
                        alpha = 0;
                        ArrayList<AttachmentListImageDetails> sample = userAttachmentList;
                        ArrayList<String> listImageDetails = new ArrayList<>();
                        ArrayList<String> add = new ArrayList<>();
                        if (updObj.getObejctId().equals("")) {
                            updObj = localSurveyDbViewModel.getByItemUrl(updObj.getParent_unique_id(),updObj.getFilename()).get(0);
                        } else {
                            updObj = localSurveyDbViewModel.getMediaInfoDataByObjId(updObj.getObejctId(),updObj.getParent_unique_id()).get(0);
                        }
                        ArrayList<String> list = updObj.getUploadMediaList();
                        for (int i = 0; i < sample.size(); i++) {
                            add.add(sample.get(i).getFilePath().toString());
                            if (updObj.getUploadMediaList() != null) {
                                if (!list.contains(sample.get(i).getFilePath())) {
                                    updObj.getUploadMediaList().add(sample.get(i).getFilePath());
                                }
                            } else {
                                if (!listImageDetails.contains(sample.get(i).getFilePath())) {
                                    listImageDetails.add(sample.get(i).getFilePath());
                                }
                            }
                        }
                        if (updObj.getUploadMediaList() == null) {
                            updObj.setUploadMediaList(listImageDetails);
                        }
                        if (updObj.getObejctId().equals("")) {
                            localSurveyDbViewModel.uploadListByFileName(unit_unique_id, updObj.getFilename(), updObj.getUploadMediaList());
                        } else {
                            localSurveyDbViewModel.updateAttachUploadList(unitUniqueId, updObj.getAttachmentItemLists(), updObj.getObejctId(), updObj.getUploadMediaList());
                        }


                        if (viewAttachAdapter != null && updObj != null) {
                            updObj.setTempMediaList(add);

                            List<AttachmentItemList> ll = new ArrayList<>();
                            if (updObj.getObejctId().equals("")) {
                                ll = localSurveyDbViewModel.getByItemUrl(unit_unique_id, updObj.getFilename()).get(0).getAttachmentItemLists();
                            } else {
                                ll = localSurveyDbViewModel.getMediaInfoDataByObjId(updObj.getObejctId(), unitUniqueId).get(0).getAttachmentItemLists();
                            }

//

                            if (updObj.getTempMediaList().size() == 1) {
                                ll.add(new AttachmentItemList(0, updObj.getTempMediaList().get(0).toString()
                                        , updObj.getTempMediaList().get(0).toString(), false, false));
                            } else if (updObj.getTempMediaList().size() > 1) {
                                ll.add(new AttachmentItemList(0, updObj.getTempMediaList().get(updObj.getTempMediaList().size() - 1).toString()
                                        , updObj.getTempMediaList().get(updObj.getTempMediaList().size() - 1).toString(), false, false));
                            }

                            if (updObj.getObejctId().equals("")) {
                                localSurveyDbViewModel.uploadAttListByFileName(unit_unique_id, updObj.getFilename(), ll);
                            } else {
                                localSurveyDbViewModel.setMediaDeletedStatusByList(unitUniqueId, ll, updObj.getObejctId());
                            }


                            final List<AttachmentItemList>[] att = new List[]{new ArrayList<>()};
                            String finalUnit_unique_id = unit_unique_id;
                            String finalUnitUniqueId = unitUniqueId;
                            new Handler().postDelayed(() -> {
                                List<AttachmentItemList> btt = new ArrayList<>();
                                if (updObj.getObejctId().equals("")) {
                                    att[0] = localSurveyDbViewModel.getByItemUrl(finalUnit_unique_id, updObj.getFilename()).get(0).getAttachmentItemLists();
                                } else {
                                    att[0] = localSurveyDbViewModel.getMediaInfoDataByObjId(updObj.getObejctId(), finalUnitUniqueId).get(0).getAttachmentItemLists();
                                }


                                for (int i = 0; i < att[0].size(); i++) {
                                    if (!att[0].get(i).isDeleted) {
                                        btt.add(att[0].get(i));
                                    }
                                }
                                viewAttachAdapter.setUpdatedList(btt);
                            }, 2000);
                        }
                    } else {
                    }
//                    setCounts(year);
                } catch (CryptoException e) {
                    AppLog.logData(activity, e.getMessage());
                    Utils.shortToast("Error while encrypting the file.", activity);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onAttachmentUpdateClicked(MediaInfoDataModel newMediaInfoDataModels) {
        if (newMediaInfoDataModels != null) {
            localSurveyDbViewModel.setIsUploaded(unitUniqueId, newMediaInfoDataModels.getObejctId(), false);
            localSurveyDbViewModel.setRemarksByMediaId(newMediaInfoDataModels.getMediaId(), newMediaInfoDataModels.getDocument_remarks());
            userAttachmentList = new ArrayList<>();
            if (dialogGlobal != null) {
                dialogGlobal.dismiss();
            }
        } else {
            userAttachmentList = new ArrayList<>();
            if (dialogGlobal != null) {
                dialogGlobal.dismiss();
            }
        }

    }

    @Override
    public void onAttachmentDeletedClicked(List<MediaInfoDataModel> deleteTotalMediaList, int flag, int pos, List<AttachmentItemList> attItemLists, String itemUrl) {
        ArrayList<AttachmentListImageDetails> userAttList=new ArrayList<>();
        boolean s=false;
        int a=-1;
        for(int i=0;i<userAttachmentList.size();i++){
            if(userAttachmentList.get(i).getFilePath().equals(itemUrl)){
                s=true;
                a=i;
            }
        }
        for(int i=0;i<userAttachmentList.size();i++){
            if(i!=a){
                userAttList.add(userAttachmentList.get(i));
            }
        }
        userAttachmentList=userAttList;

        String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;

        if(deleteTotalMediaList == null || deleteTotalMediaList.isEmpty())
            return;

        tempMediaObj=deleteTotalMediaList;


        if (flag == 1) {
            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
                deleteTotalMediaList = localSurveyDbViewModel.getByItemUrl(deleteTotalMediaList.get(0).getParent_unique_id(), deleteTotalMediaList.get(0).getFilename());
            } else {
                deleteTotalMediaList = localSurveyDbViewModel.getMediaInfoDataByObjId(deleteTotalMediaList.get(0).getObejctId(), deleteTotalMediaList.get(0).getParent_unique_id());
            }

            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
                localSurveyDbViewModel.updateByFileName(true, deleteTotalMediaList.get(0).getFilename(), unit_unique_id);
            } else {
                localSurveyDbViewModel.setDeleteItemObjValid(true, deleteTotalMediaList.get(0).getObejctId(), unitUniqueId);
            }

            if (deleteTotalMediaList.get(0).getUploadMediaList() != null) {
                ArrayList<String> aff = new ArrayList<>();
                for (int i = 0; i < deleteTotalMediaList.get(0).getUploadMediaList().size(); i++) {
                    if (!deleteTotalMediaList.get(0).getUploadMediaList().get(i).toString().equals(itemUrl)) {
                        aff.add(deleteTotalMediaList.get(0).getUploadMediaList().get(i).toString());
                    }
                }
                if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
                    localSurveyDbViewModel.uploadListByFileName(unit_unique_id, deleteTotalMediaList.get(0).getFilename(), aff);
                } else {
                    localSurveyDbViewModel.updateAttachUploadList(unitUniqueId, null, deleteTotalMediaList.get(0).getObejctId(), aff);
                }

            }

            for (int i = 0; i < deleteTotalMediaList.get(0).getAttachmentItemLists().size(); i++) {
                if (deleteTotalMediaList.get(0).getAttachmentItemLists().get(i).getItem_url().equals(itemUrl)) {
                    deleteTotalMediaList.get(0).getAttachmentItemLists().get(i).setIsDeleted(true);
                }
            }

            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
                localSurveyDbViewModel.uploadAttListByFileName(unit_unique_id, deleteTotalMediaList.get(0).getFilename(), deleteTotalMediaList.get(0).getAttachmentItemLists());
            } else {
                localSurveyDbViewModel.setMediaDeletedStatusByList(unitUniqueId, deleteTotalMediaList.get(0).getAttachmentItemLists(), deleteTotalMediaList.get(0).getObejctId());
            }


            List<MediaInfoDataModel> finalDeleteTotalMediaList = deleteTotalMediaList;
            String finalUnit_unique_id = unit_unique_id;
            String finalUnitUniqueId = unitUniqueId;
            new Handler().postDelayed(() -> {
                List<AttachmentItemList> btt = new ArrayList<>();
                List<AttachmentItemList> att = new ArrayList<>();
                if (finalDeleteTotalMediaList.get(0).getObejctId().equals("")) {
                    att = localSurveyDbViewModel.getByItemUrl(finalUnit_unique_id, finalDeleteTotalMediaList.get(0).getFilename()).get(0).getAttachmentItemLists();
                } else {
                    att = localSurveyDbViewModel.getMediaInfoDataByObjId(finalDeleteTotalMediaList.get(0).getObejctId(), finalUnitUniqueId).get(0).getAttachmentItemLists();
                }

                for (int i = 0; i < att.size(); i++) {
                    if (!att.get(i).isDeleted) {
                        btt.add(att.get(i));
                    }
                }
                viewAttachAdapter.setUpdatedImages(btt);
            }, 2000);
        } else if (flag == 2) {

            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
                deleteTotalMediaList = localSurveyDbViewModel.getByItemUrl(deleteTotalMediaList.get(0).getParent_unique_id(), deleteTotalMediaList.get(0).getFilename());
            } else {
                deleteTotalMediaList = localSurveyDbViewModel.getMediaInfoDataByObjId(deleteTotalMediaList.get(0).getObejctId(), deleteTotalMediaList.get(0).getParent_unique_id());
            }


            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
                localSurveyDbViewModel.setMediaDeletedListByUrl(unitUniqueId, deleteTotalMediaList.get(0).getFilename(), true);
            } else {
                localSurveyDbViewModel.setMediaDeletedStatus(unitUniqueId, deleteTotalMediaList.get(0).getObejctId(), true);
            }

            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
                localSurveyDbViewModel.setDeleteItemObjValid(true, deleteTotalMediaList.get(0).getFilename(), unitUniqueId);
            } else {
                localSurveyDbViewModel.setDeleteItemObjValid(true, deleteTotalMediaList.get(0).getObejctId(), unitUniqueId);
            }

            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
                localSurveyDbViewModel.setDeleteWholeFile(true, deleteTotalMediaList.get(0).getFilename(), unitUniqueId);
            } else {
                localSurveyDbViewModel.setDeleteWholeObject(true, deleteTotalMediaList.get(0).getObejctId(), unitUniqueId);
            }

            for (int i = 0; i < deleteTotalMediaList.get(0).getAttachmentItemLists().size(); i++) {
                deleteTotalMediaList.get(0).getAttachmentItemLists().get(i).setIsDeleted(true);
            }

            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
                localSurveyDbViewModel.uploadAttListByFileName(unitUniqueId, deleteTotalMediaList.get(0).getFilename(), deleteTotalMediaList.get(0).getAttachmentItemLists());
            } else {
                localSurveyDbViewModel.setMediaDeletedStatusByList(unitUniqueId, deleteTotalMediaList.get(0).getAttachmentItemLists(), deleteTotalMediaList.get(0).getObejctId());
            }


            if (dialogGlobal != null) {
                dialogGlobal.dismiss();
            }
//            setCounts(year);
        }
    }

    @Override
    public void onAttachmentBrowseClicked(MediaInfoDataModel mediaInfoDataModel, String documentType, String unitRelativePath, String s) {
        String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;
        List<MediaInfoDataModel> dpdf=new ArrayList<>();
        if(attrName.equals(Constants.Drp_Type)){
            dpdf = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(Constants.Surveyor_Category, unitUniqueId, false,Constants.Drp_Type);
        }else if(attrName.equals(Constants.Drppl_Type)){
            dpdf = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(Constants.Surveyor_Category, unitUniqueId, false,Constants.Drppl_Type);
        }
        if(dpdf.size()>=1 && previousUnitInfoPointDataModel!=null && previousUnitInfoPointDataModel.getGlobalId() != null && !previousUnitInfoPointDataModel.getGlobalId().isEmpty()){
            Utils.shortToast("You cannot add more images!", SurveyorActivity.this);
        }else if(dpdf.size()>=1 && previousUnitInfoPointDataModel!=null && previousUnitInfoPointDataModel.getGlobalId().equals("")){
            Utils.shortToast("Please remove previous attached image first!", SurveyorActivity.this);
        }else if(dpdf.size()>=1){
            Utils.shortToast("Please remove previous attached image first!", SurveyorActivity.this);
        }else{
            alpha = 1;
            updObj = mediaInfoDataModel;
            showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, s);
        }
    }

    @Override
    public void onAttachmentNameTextClicked(AttachmentListImageDetails attachmentListImageDetails) {
        try {
            String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;
            if (attachmentListImageDetails.getFilePath().startsWith("https:/")) {
                Utils.shortToast("File not available for offline use.", activity);
            } else {
                File file = attachmentListImageDetails.getFile();
                if (file.exists()) {
                    CryptoUtilsTest.encryptFileinAES(attachmentListImageDetails.getFile(), 2);
                    if (attachmentListImageDetails.getFileName().contains(".pdf")) {
                        pdfPathFile = file;
                        Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", attachmentListImageDetails.getFile());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        activity.startActivity(intent);
                    }
                    else  if(attachmentListImageDetails.getFileName().contains(".mp4") || attachmentListImageDetails.getFileName().contains(".mov")
                            || attachmentListImageDetails.getFileName().contains(".wmv") || attachmentListImageDetails.getFileName().contains(".flv")){
                        Intent intent = new Intent(activity, FullScreenImage.class);
                        Bundle extras = new Bundle();
                        extras.putString("video", attachmentListImageDetails.getFilePath());
                        intent.putExtras(extras);
                        activity.startActivity(intent);
                    }

                    else {
                        try {
                            CryptoUtilsTest.encryptFileinAES(attachmentListImageDetails.getFile(), 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(attachmentListImageDetails.getFile()));
                            if (bitmap != null) {
                                FragmentManager fm = ((AppCompatActivity) activity).getSupportFragmentManager();
                                ShowFullScreenAttachment showAttachmentBottomSheetFragment = ShowFullScreenAttachment.newInstance(attachmentListImageDetails.getFile(),
                                        bitmap, null, attachmentListImageDetails.getFileName());
                                showAttachmentBottomSheetFragment.show(fm, "");
                            }
                        } catch (CryptoException e) {
                            AppLog.logData(activity,e.getMessage());
                            Utils.shortToast("Unable to open!", activity);
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

        } catch (IOException e) {
            AppLog.logData(activity,e.getMessage());
            throw new RuntimeException(e);
        } catch (CryptoException e) {
            AppLog.logData(activity,e.getMessage());
            Utils.shortToast("Unable to open!", activity);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onAttachmentCancelBtnClicked(String attachmentType, AttachmentListImageDetails
            attachmentListImageDetails) {
        Utils.deleteDirectory(new File(attachmentListImageDetails.getFilePath()));
        localSurveyDbViewModel.deleteMediaWithFileNameData(attachmentListImageDetails.getFileName(), activity);
        userAttachmentList.remove(attachmentListImageDetails);
        addImageAdapter.setAttachmentListImageDetails(userAttachmentList);

    }
    @SuppressLint("ClickableViewAccessibility")
    private void setFocusChange_OnTouch_N(AutoCompleteTextView autoCompleteTextView) {

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            try {
                if (hasFocus) {
                    autoCompleteTextView.setError(null, null);
                    autoCompleteTextView.showDropDown();
                }
            } catch (Exception e){
                Log.d("",e.getMessage());
            }
        });

        autoCompleteTextView.setOnTouchListener((v, event) -> {
            try {
                autoCompleteTextView.showDropDown();
                autoCompleteTextView.setError(null, null);
            } catch (Exception e){
                Log.d("",e.getMessage());
            }
            return false;
        });

//        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
//            autoCompleteTextView.setError(null, null);
//            try {
//                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
//            } catch (Exception ex) {
//                Log.d("",ex.getMessage());
//                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
//            }
//        });
    }
}