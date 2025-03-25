package com.igenesys.view;

import static android.app.Activity.RESULT_OK;
import static android.icu.lang.UCharacter.getAge;
import static android.widget.ArrayAdapter.createFromResource;

import static com.igenesys.utils.Constants.INTENT_DATA_MamberInfo;
import static com.igenesys.utils.Constants.INTENT_DATA_StructureInfo;
import static com.igenesys.utils.Constants.MEMBER_INFO_TO_RESTORE_FORM;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.igenesys.App;
import com.igenesys.HohActivity;
import com.igenesys.MapActivity;
import com.igenesys.MemberActivity;
import com.igenesys.R;
import com.igenesys.UnitActivity;
import com.igenesys.WorkAreaActivity;
import com.igenesys.adapter.AttachmentListAdapter;
import com.igenesys.adapter.HorizontalAdapter;
import com.igenesys.adapter.ViewAttachAdapter;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.AadhaarVerificationData;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaDetailsDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.databinding.ActivityHohBinding;
import com.igenesys.databinding.ActivityUnitBinding;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.model.AttachmentListImageDetails;
import com.igenesys.model.AutoCompleteModal;
import com.igenesys.model.WorkAreaModel;
import com.igenesys.networks.Api_Interface;
import com.igenesys.networks.RetrofitService;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.AppPermissions;
import com.igenesys.utils.Constants;
import com.igenesys.utils.CorrectImageRotation;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.GenericTextWatcher;
import com.igenesys.utils.MaskedEditText;
import com.igenesys.utils.Utils;
import com.igenesys.utils.YesNoBottomSheet;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import org.bouncycastle.crypto.CryptoException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HOHViewModel extends ActivityViewModel<HohActivity> implements
        AttachmentListAdapter.OnAttachmentItemClickListner, ViewAttachAdapter.OnViewClickListner {

    private final ActivityHohBinding binding;
    private LocalSurveyDbViewModel localSurveyDbViewModel;
    private UnitInfoDataModel previousUnitInfoPointDataModel;
    private final Activity activity;
    private Uri imageUri;
    private MediaInfoDataModel updObj;

    private boolean isAadhaarVerified = false;
    private AlertDialog aadhaarVerificationDialog = null;
    ArrayList<AutoCompleteModal> listAllAadhaarRemarks, listFailedAadhaarRemarks;
    private AadhaarVerificationData aadhaarVerificationData;
    private TextView btn_resend_otp, btn_generate_otp;
    private LinearLayout layout_otp_view;
    private MaterialCardView cv_verified_view;
    private String aadhaarVerifyDate = "";

    private String unit_unique_id = "", formattedDob = "", unit_rel_global_id = "", hoh_rel_global_id = "", originalAadhaarNo = "";

    private AttachmentListAdapter resident_scAttachmentListAdapter, resident_ecbAttachmentListAdapter, resident_ppAttachmentListAdapter, resident_naTaxAttachmentListAdapter, resident_ptprAttachmentListAdapter, resident_ErAttachmentListAdapter, addImageAdapter;
    private ArrayList<AttachmentListImageDetails> userAttachmentList, resident_scAttachmentList, resident_ecbAttachmentList, resident_ppAttachmentList, resident_nataxAttachmentList, resident_ptprAttachmentList, resident_erAttachmentList;

    private AlertDialog dialogGlobal;
    ArrayList<String> okFileExtensions = new ArrayList<>();
    private ArrayList<AttachmentListImageDetails> memberPhotographAttachmentList, memberAdhaarCardAttachmentList, memberPanCardAttachmentList;
    File pdfPathFile = null;
    private ViewAttachAdapter viewAttachAdapter;
    List<MediaInfoDataModel> newMediaInfoDataModels;
    private AttachmentListAdapter memberPhotographAttachmentListAdapter, memberAdhaarCardAttachmentListAdapter, memberPanCardAttachmentListAdapter;

    List<String> getPreviousMediaFileName;
    private String attachmentFor = "", buttonName = "";
    private File captureImagePath;
    private Calendar myCalendar;
    String target_relative_path = "", target_name = "";
    private final int selectCamera = 10, selectGallery = 20, selectPdf = 30;
    private boolean hoh = false;
    String cc = "";
    //    public static String FormPageViewModel.AttName = "";
    private boolean hohDob = false;
    private int alpha = 0;
    private boolean ishohUploaded = false;
    private HohInfoDataModel previousHohInfoDataModel = null, editableHohInfoDataModel, editableMemberHohInfoDataModel;
    private String hohMemberUniqueId = "", hoh_relative_path = "", hohMaritalStatus = "", hoh_rel_globalID = "", hoh_Unique_ID = "", unitUniqueId = "", hohUniqueId = "";
    private String unit_relative_path = "", member_relative_path = "", hoh_unique_id = "", hohMember_relative_path = "";
    private int residentProofAttachmentCount = 0, residentProofAdditionalAttachmentCount = 0, residentProofChainAttachmentCount = 0, licenseProofAttachmentListCount = 0, religiousAttachmentCount = 0, otherAttachmentCount = 0;

    private HohInfoDataModel hohInfoDataModel;
    private StructureInfoPointDataModel structureInfoPointDataModel;
    ArrayList<MediaInfoDataModel> mediaInfoDataModels1;
    private ArrayList<MemberInfoDataModel> memberInfoDataModelArrayList;
    private Bundle restoreMemberFormData = new Bundle();
    private String hohInfo_gender = "";

    private String sendAadhaarNo = "";

    private boolean isPrevHoh = false;

    private int n=-1;

    public HOHViewModel(HohActivity activity) {
        super(activity);
        this.activity = activity;
        binding = activity.getBinding();
        binding.stepView.go(2, true);
        init();
    }

    private void setAdapters() {
        try {
            aadharTextWatcher();
            binding.layoutNewHohDetails.autoCompResidingMember.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.noOfMembers)));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompResidingMember);

            ArrayList<AutoCompleteModal> listMaritalStatus = Utils.getDomianList(Constants.domain_marital_status);
            binding.layoutNewHohDetails.autoCompMaritialStatus.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listMaritalStatus));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompMaritialStatus, listMaritalStatus);

            binding.layoutNewHohDetails.autoCompMaritialStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int SELECTED_ITEM, long rowId) {
                    // int MARRIED_ITEM = 0;
                    // int UNMARRIED_ITEM = 1;
                    // int DIVORCE_ITEM = 2;
                    // int OTHER_ITEM = 3;
                    String s=binding.layoutNewHohDetails.autoCompMaritialStatus.getText().toString();

                    binding.layoutNewHohDetails.autoCompMaritialStatus.setTag("" + s);
                    binding.layoutNewHohDetails.autoCompMaritialStatus.setText(Utils.getTextByTag(Constants.domain_marital_status,"" + s), false);

                    binding.layoutNewHohDetails.etMaritalStatusOther.setText("");
                    binding.layoutNewHohDetails.etMaritalStatusOther.setVisibility(View.GONE);

                    if(s.equalsIgnoreCase(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etMaritalStatusOther.setText("");
                        binding.layoutNewHohDetails.etMaritalStatusOther.setVisibility(View.VISIBLE);
                        binding.layoutNewHohDetails.spouseLayout.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etNumberSpouse.setText("0");
                        binding.layoutNewHohDetails.etNameSpouse.setText("");
                    } else if (s.equalsIgnoreCase("Married") &&
                            binding.layoutNewHohDetails.genderRadioGroup.getCheckedRadioButtonId() ==
                                    binding.layoutNewHohDetails.radioGenderMale.getId()) {
                        binding.layoutNewHohDetails.spouseLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.VISIBLE);
                    }else if (s.equalsIgnoreCase("Married")){
                        binding.layoutNewHohDetails.spouseLayout.setVisibility(View.VISIBLE);
                    }else if (s.equalsIgnoreCase("Unmarried")){
                        binding.layoutNewHohDetails.etMaritalStatusOther.setText("");
                        binding.layoutNewHohDetails.etMaritalStatusOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.spouseLayout.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etNumberSpouse.setText("0");
                        binding.layoutNewHohDetails.etNameSpouse.setText("");
                    }else{
                        binding.layoutNewHohDetails.spouseLayout.setVisibility(View.VISIBLE);
                    }

//                    if (SELECTED_ITEM == UNMARRIED_ITEM) {
//                        binding.layoutNewHohDetails.spouseLayout.setVisibility(View.GONE);
//                        binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
//                    } else if(SELECTED_ITEM != MARRIED_ITEM) {
//                        binding.layoutNewHohDetails.spouseLayout.setVisibility(View.VISIBLE);
//                        binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.VISIBLE);//??
//                    }
//                    if(SELECTED_ITEM == MARRIED_ITEM) {
//                        if(binding.layoutNewHohDetails.genderRadioGroup.getCheckedRadioButtonId() ==
//                                binding.layoutNewHohDetails.radioGenderMale.getId()) {
//                            binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.VISIBLE);
//                            binding.layoutNewHohDetails.spouseLayout.setVisibility(View.VISIBLE);
//                        } else {
//                            binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
//                            binding.layoutNewHohDetails.spouseLayout.setVisibility(View.VISIBLE);
//                        }
//                    } else if(SELECTED_ITEM != UNMARRIED_ITEM) {
//                        binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
//                    }
                }
            });

            ArrayList<AutoCompleteModal> listReligions = Utils.getDomianList(Constants.domain_religion);
            binding.layoutNewHohDetails.autoCompReligion.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listReligions));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompReligion, listReligions);

            ArrayList<AutoCompleteModal> listWhichState = Utils.getDomianList(Constants.domain_state);
            binding.layoutNewHohDetails.autoCompWhichState.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listWhichState));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompWhichState, listWhichState);

            ArrayList<AutoCompleteModal> listMotherTongue = Utils.getDomianList(Constants.domain_mother_tongue);
            binding.layoutNewHohDetails.autoCompMotherTongue.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listMotherTongue));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompMotherTongue, listMotherTongue);

            ArrayList<AutoCompleteModal> listEducation = Utils.getDomianList(Constants.domain_educational_qualification);
            binding.layoutNewHohDetails.autoCompEducation.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listEducation));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompEducation, listEducation);

            ArrayList<AutoCompleteModal> listOccupation = Utils.getDomianList(Constants.domain_occupation);
            binding.layoutNewHohDetails.autoCompOccupation.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listOccupation));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompOccupation, listOccupation);

            ArrayList<AutoCompleteModal> listWorkType = Utils.getDomianList(Constants.domain_work_type);
            binding.layoutNewHohDetails.autoCompWorkType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listWorkType));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompWorkType, listWorkType);

            ArrayList<AutoCompleteModal> listTransport = Utils.getDomianList(Constants.domain_mode_of_transport);
            binding.layoutNewHohDetails.autoCompTransport.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listTransport));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompTransport, listTransport);

            ArrayList<AutoCompleteModal> listVehicleType = Utils.getDomianList(Constants.domain_vehicle_owned_driven_type);
            binding.layoutNewHohDetails.autoCompVehicleType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listVehicleType));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompVehicleType, listVehicleType);

            binding.layoutNewHohDetails.autoCompWorkPlace.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_place_of_work)));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompWorkPlace);

            binding.layoutNewHohDetails.autoCompRationColor.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_ration_card_color)));
            setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompRationColor);
            TimeZone timeZone = TimeZone.getTimeZone("IST");
            myCalendar = Calendar.getInstance(timeZone);
            DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                if (hohDob) {
                    hohDob = false;
                    binding.layoutNewHohDetails.etDob.setText("" + day + "/" + (month + 1) + "/" + year);
                    binding.layoutNewHohDetails.etCount.setText("" + getAge(year, (month + 1), day));
                    binding.layoutNewHohDetails.etDob.setError(null);
                }

                if (hoh) {
                    hoh = false;
                    binding.layoutNewHohDetails.etStatingYear.setText("" + year);
                    binding.layoutNewHohDetails.etStatingYear.setError(null);
                }
            };
            binding.layoutNewHohDetails.etDob.setOnClickListener(view -> {
                hohDob = true;
                DatePickerDialog dialog = new DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            });


            binding.layoutNewHohDetails.etStatingYear.setOnClickListener(view -> {
                hoh = true;
                DatePickerDialog dialog = new DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            });
        } catch (Exception ex) {
            AppLog.logData(activity,ex.getMessage());
            Utils.shortToast("Exception in setAdapters:" + ex.getMessage(), activity);
            AppLog.e("Exception in setAdapters:" + ex.getMessage());
        }
    }

    private void setFocusChange_OnTouch(AutoCompleteTextView autoCompleteTextView, ArrayList<AutoCompleteModal> listItems) {

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {

            try {

                // Report_150424_105609 & Report_150424_105628 & Report_160424_111535 - Resolved
                if (v.getWindowVisibility() != View.VISIBLE) {
                    return;
                }

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

                        if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompMaritialStatus.getId()) {
                            Toast.makeText(activity, "Select correct value from Marital Status List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Marital Status List");
                            binding.layoutNewHohDetails.etMaritalStatusOther.setText("");
                            binding.layoutNewHohDetails.etMaritalStatusOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompReligion.getId()) {
                            Toast.makeText(activity, "Select correct value from Religion List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Religion List");
                            binding.layoutNewHohDetails.etReligionOther.setText("");
                            binding.layoutNewHohDetails.etReligionOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompWhichState.getId()) {
                            Toast.makeText(activity, "Select correct value from State List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from State List");
                            binding.layoutNewHohDetails.etWhichStateOther.setText("");
                            binding.layoutNewHohDetails.etWhichStateOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompMotherTongue.getId()) {
                            Toast.makeText(activity, "Select correct value from Mother Tongue List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Mother Tongue List");
                            binding.layoutNewHohDetails.etMotherTongueOther.setText("");
                            binding.layoutNewHohDetails.etMotherTongueOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompEducation.getId()) {
                            Toast.makeText(activity, "Select correct value from Education List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Education List");
                            binding.layoutNewHohDetails.etEducationOther.setText("");
                            binding.layoutNewHohDetails.etEducationOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompOccupation.getId()) {
                            Toast.makeText(activity, "Select correct value from Occupation List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Occupation List");
                            binding.layoutNewHohDetails.etOccupationOther.setText("");
                            binding.layoutNewHohDetails.etOccupationOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompWorkType.getId()) {
                            Toast.makeText(activity, "Select correct value from Type Of Work List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Type Of Work List");
                            binding.layoutNewHohDetails.etTypeOfWorkOther.setText("");
                            binding.layoutNewHohDetails.etTypeOfWorkOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompTransport.getId()) {
                            Toast.makeText(activity, "Select correct value from Mode of Transport List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Mode of Transport List");
                            binding.layoutNewHohDetails.etModeOfTransportOther.setText("");
                            binding.layoutNewHohDetails.etModeOfTransportOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompVehicleType.getId()) {
                            Toast.makeText(activity, "Select correct value from Type Of Vehicle List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Type Of Vehicle List");
                            binding.layoutNewHohDetails.etVehicleTypeOther.setText("");
                            binding.layoutNewHohDetails.etVehicleTypeOther.setVisibility(View.GONE);
                        }
                    }
                }
            } catch (Exception e){
                AppLog.e(e.getMessage());
            }
        });

        autoCompleteTextView.setOnTouchListener((view, motionEvent) -> {
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

                AutoCompleteModal selected = (AutoCompleteModal) adapterView.getAdapter().getItem(i);

                if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompMaritialStatus.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etMaritalStatusOther.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewHohDetails.etMaritalStatusOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etMaritalStatusOther.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompReligion.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etReligionOther.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewHohDetails.etReligionOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etReligionOther.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompWhichState.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etWhichStateOther.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewHohDetails.etWhichStateOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etWhichStateOther.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompMotherTongue.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etMotherTongueOther.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewHohDetails.etMotherTongueOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etMotherTongueOther.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompEducation.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etEducationOther.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewHohDetails.etEducationOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etEducationOther.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompOccupation.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etOccupationOther.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewHohDetails.etOccupationOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etOccupationOther.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompWorkType.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etTypeOfWorkOther.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewHohDetails.etTypeOfWorkOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etTypeOfWorkOther.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompTransport.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etModeOfTransportOther.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewHohDetails.etModeOfTransportOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etModeOfTransportOther.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewHohDetails.autoCompVehicleType.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewHohDetails.etVehicleTypeOther.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewHohDetails.etVehicleTypeOther.setVisibility(View.GONE);
                        binding.layoutNewHohDetails.etVehicleTypeOther.setText("");
                    }
                }

                autoCompleteTextView.setTag(selected.code);

            }catch(Exception ex){
                AppLog.logData(activity,ex.getMessage());
                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setFocusChange_OnTouch(AutoCompleteTextView autoCompleteTextView) {

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {

            try {

                // Report_150424_105609 & Report_150424_105628 & Report_160424_111535 - Resolved
                if (v.getWindowVisibility() != View.VISIBLE) {
                    return;
                }

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
                AppLog.logData(activity, ex.getMessage());
                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
            }
        });
    }

    public void init() {
        try {
            if (activity.getIntent().hasExtra(Constants.INTENT_DATA_UnitInfo)) {
                previousUnitInfoPointDataModel = (UnitInfoDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_UnitInfo);
                App.getSharedPreferencesHandler().putString("inactive","true");
                App.getSharedPreferencesHandler().putString("inactiveId",previousUnitInfoPointDataModel.getUnit_id());
//            if (previousUnitInfoPointDataModel != null) Toast.makeText(activity, previousUnitInfoPointDataModel.getRelative_path(), Toast.LENGTH_SHORT).show();
            }
            if (activity.getIntent().hasExtra(Constants.INTENT_DATA_HohInfo)) {
                previousHohInfoDataModel = (HohInfoDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_HohInfo);
            }
            if(previousHohInfoDataModel!=null){
                binding.layoutNewHohDetails.uniqueIdDataCheckBox.setVisibility(View.GONE);
            }else if(App.getInstance().getAlreadyAddedUniqueId()==null){
                binding.layoutNewHohDetails.uniqueIdDataCheckBox.setVisibility(View.GONE);
            }else if(App.getInstance().getAlreadyAddedUniqueId().size()<=0){
                binding.layoutNewHohDetails.uniqueIdDataCheckBox.setVisibility(View.GONE);
            }else{
                binding.layoutNewHohDetails.uniqueIdDataCheckBox.setVisibility(View.VISIBLE);
            }

            if (activity.getIntent().hasExtra(INTENT_DATA_StructureInfo)) {
                structureInfoPointDataModel = (StructureInfoPointDataModel) activity.getIntent().getSerializableExtra(INTENT_DATA_StructureInfo);
            }
            if (activity.getIntent().hasExtra(INTENT_DATA_MamberInfo)) {
                memberInfoDataModelArrayList = (ArrayList<MemberInfoDataModel>) activity.getIntent().getSerializableExtra(INTENT_DATA_MamberInfo);
            }
//            if (activity.getIntent().hasExtra(MEMBER_INFO_TO_RESTORE_FORM)) {
//                restoreMemberFormData = (Bundle) activity.getIntent().getBundleExtra(MEMBER_INFO_TO_RESTORE_FORM);
//            }
            binding.layoutNewHohDetails.etNameSpouse.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        binding.layoutNewHohDetails.etNameSpouse.setText(Utils.capitalizeEachWord(binding.layoutNewHohDetails.etNameSpouse.getText().toString()));
                    }
                }
            });
            binding.layoutNewHohDetails.etNameHousehold.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        binding.layoutNewHohDetails.etNameHousehold.setText(Utils.capitalizeEachWord(binding.layoutNewHohDetails.etNameHousehold.getText().toString()));
                    }
                }
            });

            setDataIfPresent();
            setAdapters();
            if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase("Self")) {
                binding.layoutNewHohDetails.etNameHousehold.setText(previousUnitInfoPointDataModel.getRespondent_name());
                binding.layoutNewHohDetails.etDob.setText(previousUnitInfoPointDataModel.getRespondent_dob());
                binding.layoutNewHohDetails.etCount.setText(previousUnitInfoPointDataModel.getRespondent_age());
                binding.layoutNewHohDetails.etRespondentContact.setText(previousUnitInfoPointDataModel.getRespondent_mobile());
                disableHohFields();
            }
            addAttachments();
            localSurveyDbViewModel = ViewModelProviders.of(getActivity()).get(LocalSurveyDbViewModel.class);

            binding.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isValid()) {
                        try {
                            if (!binding.layoutNewHohDetails.etAadhaarNumber.getText().toString().equals("") && !binding.layoutNewHohDetails.etAadhaarNumber.getText().toString().contains("x")){
                                MaskedEditText.setMaskedText(binding.layoutNewHohDetails.etAadhaarNumber,binding.layoutNewHohDetails.etAadhaarNumber.getText().toString());
                            }
                            binding.layoutNewHohDetails.etNameSpouse.setText(Utils.capitalizeEachWord(binding.layoutNewHohDetails.etNameSpouse.getText().toString()));
                            binding.layoutNewHohDetails.etNameHousehold.setText(Utils.capitalizeEachWord(binding.layoutNewHohDetails.etNameHousehold.getText().toString()));

                            StructureInfoPointDataModel structureInfoPointDataModel = (StructureInfoPointDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);
                            memberInfoDataModelArrayList = (ArrayList<MemberInfoDataModel>) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_MamberInfo);
                            //hohInfoDataModelHashMap.put(unitUniqueId, expandableListTitle);
                            HohInfoDataModel hohInfoDataModel2 = getHohDataToLocal();
                            localSurveyDbViewModel.insertHohInfoPointData(hohInfoDataModel2, activity);
                            if (previousHohInfoDataModel == null)
                                previousHohInfoDataModel = hohInfoDataModel2;
                            AppLog.e("Hoh Unique ID:" + previousHohInfoDataModel.getHoh_id());

                            saveAadhaarVerificationDataToDB(hohInfoDataModel2.getGlobalId());

                            FormPageViewModel.isHohData = true;
                            activity.startActivity(new Intent(activity, MemberActivity.class)
                                    .putExtra(Constants.IS_EDITING, true)
                                    .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                    .putExtra("flow", true)
                                    .putExtra(Constants.viewMode, "viewMode")
                                    .putExtra(Constants.INTENT_DATA_HohInfo, previousHohInfoDataModel)
                                    .putExtra(INTENT_DATA_StructureInfo, structureInfoPointDataModel)
//                                    .putExtra(MEMBER_INFO_TO_RESTORE_FORM, restoreMemberFormData)
                                    .putExtra(Constants.INTENT_DATA_MamberInfo, memberInfoDataModelArrayList)//multiple members are allowed
                                    .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel)
                                    .putExtra("isRelAmen", false)
                            );
                        } catch (Exception e) {
                            AppLog.logData(activity,e.getMessage());
                            Utils.shortToast("Error_in_timstamp:" + e.getMessage(), activity);
                            Log.e("Error_in_timstamp= ", " " + e.getMessage());
                        }
                    }
                }
            });
        } catch (Exception ex) {
            AppLog.logData(activity,ex.getMessage());
            Utils.shortToast("Exception in Init:" + ex.getMessage(), activity);
            AppLog.e("Exception in Init:" + ex.getMessage());
        }

        binding.btnCancel.setOnClickListener(view -> closeFormPopup());

        binding.layoutNewHohDetails.radioGenderFemale.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                hohInfo_gender = binding.layoutNewHohDetails.radioGenderFemale.getText().toString();
                binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
            }
        });

        binding.layoutNewHohDetails.radioGenderMale.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                hohInfo_gender = binding.layoutNewHohDetails.radioGenderMale.getText().toString();
                binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
                if(binding.layoutNewHohDetails.autoCompMaritialStatus.getText().toString().equals("Married")) {
                    binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.spouseLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
                }
            }
        });

        binding.layoutNewHohDetails.radioGenderOther.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                // hohInfo_gender = binding.layoutNewHohDetails.radioGenderOther.getText().toString();
                hohInfo_gender = "Trans";
                binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
            }
        });

        listAllAadhaarRemarks = Utils.getDomianList(Constants.domain_aadhaar_remarks);
        listFailedAadhaarRemarks = new ArrayList<>();
        for (AutoCompleteModal model : listAllAadhaarRemarks) {
            if (!model.description.equalsIgnoreCase("Aadhar Card Verified"))
                listFailedAadhaarRemarks.add(model);
        }

        binding.layoutNewHohDetails.radioVerifyAadharYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    if (binding.layoutNewHohDetails.etNameHousehold.getText().toString().isEmpty()) {
                        isAadhaarVerified = false;
                        binding.layoutNewHohDetails.etNameHousehold.setError("Please Enter HOH Name.");
                        binding.layoutNewHohDetails.etNameHousehold.requestFocus();
                        binding.layoutNewHohDetails.radioVerifyAadharYes.setChecked(false);
                    } else if (binding.layoutNewHohDetails.etAadhaarNumber.getText().toString().isEmpty()) {
                        isAadhaarVerified = false;
                        binding.layoutNewHohDetails.etAadhaarNumber.setError("Please Enter Aadhaar Number.");
                        binding.layoutNewHohDetails.etAadhaarNumber.requestFocus();
                        binding.layoutNewHohDetails.radioVerifyAadharYes.setChecked(false);
                    } else if (binding.layoutNewHohDetails.etAadhaarNumber.getText().toString().length() < 12) {
                        binding.layoutNewHohDetails.etAadhaarNumber.setError(activity.getString(R.string.aadhar_validations));
                        binding.layoutNewHohDetails.etAadhaarNumber.requestFocus();
                        binding.layoutNewHohDetails.radioVerifyAadharYes.setChecked(false);
                    } else {
                        binding.layoutNewHohDetails.autoCompAadharRemarks.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listAllAadhaarRemarks));
                        setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompAadharRemarks, listAllAadhaarRemarks);
                        showVerifyAadhaarDialog();
                    }
                }
            }
        });

        binding.layoutNewHohDetails.radioVerifyAadharNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    isAadhaarVerified = false;
                    binding.layoutNewHohDetails.autoCompAadharRemarks.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listFailedAadhaarRemarks));
                    setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompAadharRemarks, listFailedAadhaarRemarks);
                }
            }
        });;

        binding.layoutNewHohDetails.uniqueIdDataCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.layoutNewHohDetails.autoCompUnitId.setText("");
                    binding.layoutNewHohDetails.autoCompUnitId.setTag("");

                    ArrayList<String> aOne=new ArrayList<>();
                    ArrayList<UnitInfoDataModel> bOne=new ArrayList<>();
                    String id=previousUnitInfoPointDataModel.getUnit_unique_id();
                    String[] tmp = id.split("/");
                    for(int i=0;i<App.getInstance().getAlreadyAddedUniqueId().size();i++){
                        if(App.getInstance().getAlreadyAddedUniqueId().get(i).contains(tmp[4])){
                            aOne.add(App.getInstance().getAlreadyAddedUniqueId().get(i));
                        }
                    }

                    for(int i=0;i<aOne.size();i++){
                        int k=-1;
                        for(int j=0;j<App.getInstance().getUniqueObjectList().size();j++){
                            if(aOne.get(i).equals(App.getInstance().getUniqueObjectList().get(j).getUnit_unique_id())){
                                bOne.add(App.getInstance().getUniqueObjectList().get(j));
                                k=-1;
                            }else{
                                k=1;
                            }
                        }
//                        if(k==1){
//                            bOne.add(new UnitInfoDataModel());
//                        }
                    }

                    binding.layoutNewHohDetails.uniqueIdList.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.autoCompUnitId.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, aOne));
                    setFocusChange_OnTouch(binding.layoutNewHohDetails.autoCompUnitId);
                    binding.layoutNewHohDetails.autoCompUnitId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            try {
                                ArrayList<UnitInfoDataModel> unitL=bOne;
                                ArrayList<HohInfoDataModel> hohL=new ArrayList<>();
                                if(unitL.get(position).getGlobalId().equals("") || (App.getInstance().getHohList().get(unitL.get(position).getUnit_id())!=null && App.getInstance().getHohList().get(unitL.get(position).getUnit_id()).size()>0)){
                                    if(App.getInstance().getHohList()!=null){
                                        hohL=App.getInstance().getHohList().get(unitL.get(position).getUnit_id());
                                    }else{
                                        clearHohFields();
                                        n=-1;
                                        showActionAlertDialogUpdateButton("DRP App", "OK", "Close", "HOH not available for the selected unit.", "");
                                    }
                                }else{
                                    if(App.getInstance().getHohList()!=null){
                                        hohL=App.getInstance().getHohList().get(unitL.get(position).getGlobalId().toUpperCase());
                                    }else{
                                        clearHohFields();
                                        n=-1;
                                        showActionAlertDialogUpdateButton("DRP App", "OK", "Close", "HOH not available for the selected unit.", "");
                                    }
                                }
                                if(hohL!=null && !hohL.isEmpty()){
                                    previousHohInfoDataModel=hohL.get(0);
                                    aadharTextWatcher();
                                    ishohUploaded = false;
                                    isPrevHoh = true;
                                    clearHohFields();
                                    try {
                                        binding.layoutNewHohDetails.etRespondentContact.setFocusable(true);
                                        binding.layoutNewHohDetails.etRespondentContact.setFocusableInTouchMode(true);
                                        binding.layoutNewHohDetails.etRespondentContact.setClickable(true);

                                        binding.layoutNewHohDetails.etNameHousehold.setFocusable(true);
                                        binding.layoutNewHohDetails.etNameHousehold.setFocusableInTouchMode(true);
                                        binding.layoutNewHohDetails.etNameHousehold.setClickable(true);

                                        binding.layoutNewHohDetails.etRespondentContact.setBackgroundResource(R.drawable.rounded_white_edittext);
                                        binding.layoutNewHohDetails.etDob.setClickable(true);
                                        binding.layoutNewHohDetails.etNameHousehold.setBackgroundResource(R.drawable.rounded_white_edittext);
                                        binding.layoutNewHohDetails.etDob.setBackgroundResource(R.drawable.rounded_white_edittext);

                                    } catch (Exception ex) {
                                        Utils.shortToast("Exception in disableHohFields:" + ex.getMessage(), activity);
                                        AppLog.e("Exception in disableHohFields:" + ex.getMessage());
                                    }

                                    Utils.showProgress("Please wait...", activity);

                                    new Handler().postDelayed(() -> {
                                        if(previousHohInfoDataModel!=null){
                                            n=1;
                                            setDataIfPresent();
                                        }else{
                                            n=-1;
                                            showActionAlertDialogUpdateButton("DRP App", "OK", "Close", "HOH not available for the selected unit.", "");
                                        }
                                        Utils.dismissProgress();
                                    }, 2000);
                                }else{
                                    clearHohFields();
                                    n=-1;
                                    showActionAlertDialogUpdateButton("DRP App", "OK", "Close", "HOH not available for the selected unit.", "");
                                }
                            }catch(Exception ex){
                                AppLog.logData(activity,ex.getMessage());
                            }
                        }
                    });
                }else{
                    clearHohFields();
                    isPrevHoh = false;
                    binding.layoutNewHohDetails.uniqueIdList.setVisibility(View.GONE);
                    if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase("Self")) {
                        binding.layoutNewHohDetails.etNameHousehold.setText(previousUnitInfoPointDataModel.getRespondent_name());
                        binding.layoutNewHohDetails.etDob.setText(previousUnitInfoPointDataModel.getRespondent_dob());
                        binding.layoutNewHohDetails.etCount.setText(previousUnitInfoPointDataModel.getRespondent_age());
                        binding.layoutNewHohDetails.etRespondentContact.setText(previousUnitInfoPointDataModel.getRespondent_mobile());
                        disableHohFields();
                    }
                }
            }
        });
    }

    public void showVerifyAadhaarDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_aadhaar_verification_dialog, null);
        builder.setView(customLayout);
        aadhaarVerificationDialog = builder.create();

        EditText edtAadhaarNumber = customLayout.findViewById(R.id.edtAadhaarNumber);
        String aadhaarNo = MaskedEditText.getOriginalText(binding.layoutNewHohDetails.etAadhaarNumber);

        if(aadhaarNo.isEmpty()) {
            aadhaarNo = binding.layoutNewHohDetails.etAadhaarNumber.getText().toString();
        }

        edtAadhaarNumber.setText(aadhaarNo);

        ImageView img_close = customLayout.findViewById(R.id.img_close);
        img_close.setOnClickListener(view -> {
            isAadhaarVerified = false;
            binding.layoutNewHohDetails.radioVerifyAadharNo.setChecked(true);
            aadhaarVerificationDialog.dismiss();
        });

        cv_verified_view = customLayout.findViewById(R.id.cv_verified_view);
        layout_otp_view = customLayout.findViewById(R.id.layout_otp_view);

        btn_generate_otp = customLayout.findViewById(R.id.btn_generate_otp);
        btn_generate_otp.setOnClickListener(view -> generateAadhaarOTP(edtAadhaarNumber.getText().toString()));

        EditText e1 = customLayout.findViewById(R.id.et_one);
        EditText e2 = customLayout.findViewById(R.id.et_two);
        EditText e3 = customLayout.findViewById(R.id.et_three);
        EditText e4 = customLayout.findViewById(R.id.et_four);
        EditText e5 = customLayout.findViewById(R.id.et_five);
        EditText e6 = customLayout.findViewById(R.id.et_six);

        e1.addTextChangedListener(new GenericTextWatcher(e2, e1));
        e2.addTextChangedListener(new GenericTextWatcher(e3, e1));
        e3.addTextChangedListener(new GenericTextWatcher(e4, e2));
        e4.addTextChangedListener(new GenericTextWatcher(e5, e3));
        e5.addTextChangedListener(new GenericTextWatcher(e6, e4));
        e6.addTextChangedListener(new GenericTextWatcher(e6, e5));

        TextView btn_verify = customLayout.findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(view -> {
            if (e1.getText().toString().trim().isEmpty() || e2.getText().toString().trim().isEmpty() ||
                    e3.getText().toString().trim().isEmpty() || e4.getText().toString().trim().isEmpty() ||
                    e5.getText().toString().trim().isEmpty() || e6.getText().toString().trim().isEmpty()) {
                Utils.shortToast("Enter Valid OTP", activity);
            } else {
                String otp = e1.getText().toString().trim() + e2.getText().toString().trim() +
                        e3.getText().toString().trim() + e4.getText().toString().trim() +
                        e5.getText().toString().trim() + e6.getText().toString().trim();
                verifyAadhaarOTP(otp);
            }
        });

        btn_resend_otp = customLayout.findViewById(R.id.btn_resend_otp);
        btn_resend_otp.setOnClickListener(view -> btn_generate_otp.performClick());

        aadhaarVerificationDialog.show();
    }

    public void startCountDown() {

        layout_otp_view.setVisibility(View.VISIBLE);
        btn_resend_otp.setEnabled(false);
        btn_resend_otp.setFocusable(false);

        try {

            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    long sec = (millisUntilFinished / 1000) % 60;
                    btn_resend_otp.setText(String.format("Resend OTP in %s",
                            new DecimalFormat("00").format(sec)));
                }

                // When the task is over it will print 00:00:00 there
                public void onFinish() {
                    btn_resend_otp.setText("RESEND OTP");
                    btn_resend_otp.setEnabled(true);
                    btn_resend_otp.setFocusable(true);
                }
            }.start();
        } catch (Exception ex) {
            AppLog.e("Exception in count down::" + ex.getMessage());
            AppLog.logData(activity, "Exception in count down::" + ex.getMessage());
        }
    }

    private void disableHohFields() {
        try {
            binding.layoutNewHohDetails.etRespondentContact.setFocusable(false);
            binding.layoutNewHohDetails.etRespondentContact.setBackgroundResource(R.drawable.rounded_blue_edittext);
            binding.layoutNewHohDetails.etNameHousehold.setFocusable(false);
//            binding.layoutNewHohDetails.etDob.setOnClickListener(null);
            binding.layoutNewHohDetails.etDob.setClickable(false);
            binding.layoutNewHohDetails.etNameHousehold.setBackgroundResource(R.drawable.rounded_blue_edittext);
            binding.layoutNewHohDetails.etDob.setBackgroundResource(R.drawable.rounded_blue_edittext);
        } catch (Exception ex) {
            Utils.shortToast("Exception in disableHohFields:" + ex.getMessage(), activity);
            AppLog.e("Exception in disableHohFields:" + ex.getMessage());
        }
    }

    private void setDataIfPresent() {
        try {
            if (previousHohInfoDataModel != null) {
                ishohUploaded = true;
                binding.layoutNewHohDetails.etNameHousehold.setText("" + previousHohInfoDataModel.getHoh_name());
                binding.layoutNewHohDetails.etRespondentContact.setText("" + previousHohInfoDataModel.getHoh_contact_no());
                binding.layoutNewHohDetails.etNumberSpouse.setText("" + previousHohInfoDataModel.getHoh_spouse_count());
                binding.layoutNewHohDetails.etNameSpouse.setText("" + previousHohInfoDataModel.getHoh_spouse_name());
                binding.layoutNewHohDetails.autoCompMaritialStatus.setTag("" + previousHohInfoDataModel.getMarital_status());
                binding.layoutNewHohDetails.autoCompMaritialStatus.setText(Utils.getTextByTag(Constants.domain_marital_status,"" + previousHohInfoDataModel.getMarital_status()), false);

                if(previousHohInfoDataModel.getMarital_status().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewHohDetails.etMaritalStatusOther.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.etMaritalStatusOther.setText(previousHohInfoDataModel.getMarital_status_other());
                } else {
                    binding.layoutNewHohDetails.etMaritalStatusOther.setText("");
                    binding.layoutNewHohDetails.etMaritalStatusOther.setVisibility(View.GONE);
                }

                if(previousHohInfoDataModel.getAdhaar_verify_status().equals("Yes")) {
                    binding.layoutNewHohDetails.radioVerifyAadharYes.setChecked(true);
                    isAadhaarVerified = true;
                } else {
                    binding.layoutNewHohDetails.radioVerifyAadharNo.setChecked(true);
                    isAadhaarVerified = false;
                }

                binding.layoutNewHohDetails.autoCompAadharRemarks.setText(previousHohInfoDataModel.getAdhaar_verify_remark());

                binding.layoutNewHohDetails.etDob.setText("" + previousHohInfoDataModel.getHoh_dob());
                try {
                    formattedDob = formattedDate(previousHohInfoDataModel.getHoh_dob());
                    if (!formattedDob.isEmpty())
                        binding.layoutNewHohDetails.etDob.setText("" + formattedDob);
                } catch (Exception e) {
                    AppLog.logData(activity,e.getMessage());
//                    Utils.shortToast("Error_in_timstamp:" + e.getMessage(), activity);
                    Log.e("Error_in_timstamp= ", " " + e.getMessage());
                }
                binding.layoutNewHohDetails.etCount.setText("" + previousHohInfoDataModel.getAge());
                binding.layoutNewHohDetails.etStatingYear.setText("" + previousHohInfoDataModel.getStaying_since_year());
                binding.layoutNewHohDetails.etPanNumber.setText("" + previousHohInfoDataModel.getPan_no());
                originalAadhaarNo = previousHohInfoDataModel.getAadhar_no();
//                sendAadhaarNo = previousHohInfoDataModel.getAadhar_no();
//                maskAadharCardNumber();
//                aadharTextWatcher();
                MaskedEditText.setMaskedText(binding.layoutNewHohDetails.etAadhaarNumber, previousHohInfoDataModel.getAadhar_no());
//                binding.layoutNewHohDetails.etAadhaarNumber.setText("" + previousHohInfoDataModel.getAadhar_no());

                binding.layoutNewHohDetails.etRationCardNumber.setText("" + previousHohInfoDataModel.getRation_card_no());
                binding.layoutNewHohDetails.autoCompRationColor.setTag("" + previousHohInfoDataModel.getRation_card_colour());
                binding.layoutNewHohDetails.autoCompRationColor.setText(Utils.getTextByTag(Constants.domain_ration_card_color,"" + previousHohInfoDataModel.getRation_card_colour()));

                binding.layoutNewHohDetails.autoCompReligion.setTag("" + previousHohInfoDataModel.getReligion());
                binding.layoutNewHohDetails.autoCompReligion.setText(Utils.getTextByTag(Constants.domain_religion,"" + previousHohInfoDataModel.getReligion()));

                if(previousHohInfoDataModel.getReligion().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewHohDetails.etReligionOther.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.etReligionOther.setText(previousHohInfoDataModel.getReligion_other());
                } else {
                    binding.layoutNewHohDetails.etReligionOther.setText("");
                    binding.layoutNewHohDetails.etReligionOther.setVisibility(View.GONE);
                }

                binding.layoutNewHohDetails.autoCompWhichState.setTag("" + previousHohInfoDataModel.getFrom_state());
                binding.layoutNewHohDetails.autoCompWhichState.setText(Utils.getTextByTag(Constants.domain_state,"" + previousHohInfoDataModel.getFrom_state()));

                if(previousHohInfoDataModel.getFrom_state().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewHohDetails.etWhichStateOther.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.etWhichStateOther.setText(previousHohInfoDataModel.getFrom_state_other());
                } else {
                    binding.layoutNewHohDetails.etWhichStateOther.setText("");
                    binding.layoutNewHohDetails.etWhichStateOther.setVisibility(View.GONE);
                }

                binding.layoutNewHohDetails.autoCompMotherTongue.setTag("" + previousHohInfoDataModel.getMother_tongue());
                binding.layoutNewHohDetails.autoCompMotherTongue.setText(Utils.getTextByTag(Constants.domain_mother_tongue,"" + previousHohInfoDataModel.getMother_tongue()));

                if(previousHohInfoDataModel.getMother_tongue().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewHohDetails.etMotherTongueOther.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.etMotherTongueOther.setText(previousHohInfoDataModel.getMother_tongue_other());
                } else {
                    binding.layoutNewHohDetails.etMotherTongueOther.setText("");
                    binding.layoutNewHohDetails.etMotherTongueOther.setVisibility(View.GONE);
                }

                binding.layoutNewHohDetails.autoCompVehicleType.setTag("" + previousHohInfoDataModel.getVehicle_owned_driven());
                binding.layoutNewHohDetails.autoCompVehicleType.setText(Utils.getTextByTag(Constants.hohInfo_vehicle_owned_driven_type,"" + previousHohInfoDataModel.getVehicle_owned_driven()));

                if(previousHohInfoDataModel.getVehicle_owned_driven().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewHohDetails.etVehicleTypeOther.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.etVehicleTypeOther.setText(previousHohInfoDataModel.getVehicle_owned_driven_other());
                } else {
                    binding.layoutNewHohDetails.etVehicleTypeOther.setText("");
                    binding.layoutNewHohDetails.etVehicleTypeOther.setVisibility(View.GONE);
                }

                binding.layoutNewHohDetails.autoCompEducation.setTag("" + previousHohInfoDataModel.getEducation());
                binding.layoutNewHohDetails.autoCompEducation.setText(Utils.getTextByTag(Constants.domain_educational_qualification,"" + previousHohInfoDataModel.getEducation()));

                if(previousHohInfoDataModel.getEducation().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewHohDetails.etEducationOther.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.etEducationOther.setText(previousHohInfoDataModel.getEducation_other());
                } else {
                    binding.layoutNewHohDetails.etEducationOther.setText("");
                    binding.layoutNewHohDetails.etEducationOther.setVisibility(View.GONE);
                }

                binding.layoutNewHohDetails.autoCompOccupation.setText("" + previousHohInfoDataModel.getOccupation());
                binding.layoutNewHohDetails.autoCompOccupation.setTag(Utils.getTextByTag(Constants.domain_occupation,"" + previousHohInfoDataModel.getOccupation()));

                if(previousHohInfoDataModel.getOccupation().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewHohDetails.etOccupationOther.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.etOccupationOther.setText(previousHohInfoDataModel.getOccupation_other());
                } else {
                    binding.layoutNewHohDetails.etOccupationOther.setText("");
                    binding.layoutNewHohDetails.etOccupationOther.setVisibility(View.GONE);
                }

                binding.layoutNewHohDetails.autoCompWorkType.setText("" + previousHohInfoDataModel.getType_of_work());
                binding.layoutNewHohDetails.autoCompWorkType.setTag(Utils.getTextByTag(Constants.domain_work_type,"" + previousHohInfoDataModel.getType_of_work()));

                if(previousHohInfoDataModel.getType_of_work().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewHohDetails.etTypeOfWorkOther.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.etTypeOfWorkOther.setText(previousHohInfoDataModel.getType_of_work_other());
                } else {
                    binding.layoutNewHohDetails.etTypeOfWorkOther.setText("");
                    binding.layoutNewHohDetails.etTypeOfWorkOther.setVisibility(View.GONE);
                }

                binding.layoutNewHohDetails.autoCompWorkPlace.setText("" + previousHohInfoDataModel.getPlace_of_work());
                binding.layoutNewHohDetails.autoCompWorkPlace.setTag(Utils.getTextByTag(Constants.domain_place_of_work,"" + previousHohInfoDataModel.getPlace_of_work()));
                binding.layoutNewHohDetails.etIncome.setText("" + previousHohInfoDataModel.getMonthly_income());
                binding.layoutNewHohDetails.autoCompTransport.setTag("" + previousHohInfoDataModel.getMode_of_transport());
                binding.layoutNewHohDetails.autoCompTransport.setText(Utils.getTextByTag(Constants.domain_mode_of_transport,"" + previousHohInfoDataModel.getMode_of_transport()));

                if(previousHohInfoDataModel.getMode_of_transport().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewHohDetails.etModeOfTransportOther.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.etModeOfTransportOther.setText(previousHohInfoDataModel.getMode_of_transport_other());
                } else {
                    binding.layoutNewHohDetails.etModeOfTransportOther.setText("");
                    binding.layoutNewHohDetails.etModeOfTransportOther.setVisibility(View.GONE);
                }

                // binding.layoutNewHohDetails.autoCompVehicleType.setTag("" + previousHohInfoDataModel.getVehicle_owned_driven());
                // binding.layoutNewHohDetails.autoCompVehicleType.setText(Utils.getTextByTag(Constants.domain_vehicle_owned_driven_type,"" + previousHohInfoDataModel.getVehicle_owned_driven()));
                // binding.layoutNewHohDetails.autoCompWorkPlace.setTag("" + previousHohInfoDataModel.getPlace_of_work());
                // binding.layoutNewHohDetails.autoCompWorkPlace.setText(Utils.getTextByTag(Constants.domain_place_of_work,"" + previousHohInfoDataModel.getPlace_of_work()));
                // binding.layoutNewHohDetails.autoCompResidingMember.setText("" + previousHohInfoDataModel.getCount_of_other_members());
                hohMaritalStatus = previousHohInfoDataModel.getMarital_status();
                if (previousHohInfoDataModel.getMarital_status().equalsIgnoreCase("Unmarried")) {
                    binding.layoutNewHohDetails.spouseLayout.setVisibility(View.GONE);
                    binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
                } else {
                    binding.layoutNewHohDetails.spouseLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.VISIBLE);
                }

                binding.layoutNewHohDetails.genderRadioGroup.clearCheck();
                if (previousHohInfoDataModel.getGender().equalsIgnoreCase(Constants.male)) {
                    binding.layoutNewHohDetails.radioGenderMale.setChecked(true);
                    hohInfo_gender=Constants.male;
                }
                else if (previousHohInfoDataModel.getGender().equalsIgnoreCase(Constants.female)) {
                    binding.layoutNewHohDetails.radioGenderFemale.setChecked(true);
                    hohInfo_gender=Constants.female;
                }
                else if (previousHohInfoDataModel.getGender().equalsIgnoreCase(Constants.transgender) || previousHohInfoDataModel.getGender().equalsIgnoreCase("trans")) {
                    binding.layoutNewHohDetails.radioGenderOther.setChecked(true);
                    hohInfo_gender="Trans";
                }

                if (previousHohInfoDataModel.getHandicap_or_critical_disease().equalsIgnoreCase(Constants.yes))
                    binding.layoutNewHohDetails.radioHandicappedYes.setChecked(true);
                else if (previousHohInfoDataModel.getHandicap_or_critical_disease().equalsIgnoreCase(Constants.no))
                    binding.layoutNewHohDetails.radioHandicappedNo.setChecked(true);

                if (previousHohInfoDataModel.getStaying_with().equalsIgnoreCase("Family"))
                    binding.layoutNewHohDetails.radioFamilyYes.setChecked(true);
                else if (previousHohInfoDataModel.getStaying_with().equalsIgnoreCase(activity.getString(R.string.individual)))
                    binding.layoutNewHohDetails.radioIndividualNo.setChecked(true);

                if (previousHohInfoDataModel.getDeath_certificate() == 1)
                    binding.layoutNewHohDetails.radioMemberAvailableYes.setChecked(true);
                else if (previousHohInfoDataModel.getDeath_certificate() == 0)
                    binding.layoutNewHohDetails.radioMemberAvailableNo.setChecked(true);

                if(!isPrevHoh){
                    hohUniqueId = previousHohInfoDataModel.getHoh_id();
                    hoh_unique_id = previousHohInfoDataModel.getHoh_id();
                    hoh_relative_path = previousHohInfoDataModel.getRelative_path();
                    hohMemberUniqueId = hohUniqueId;
                    hohMember_relative_path = hoh_relative_path;
                }else{
                    unit_relative_path = previousUnitInfoPointDataModel.getRelative_path();
                    hohUniqueId = "H_" + Utils.getEpochDateStamp();
                    hoh_relative_path = unit_relative_path + hohUniqueId + "/";
                    hohMemberUniqueId = hohUniqueId;
                    hohMember_relative_path = hoh_relative_path;
                    previousHohInfoDataModel.setHoh_id(hohUniqueId);
                    previousHohInfoDataModel.setRelative_path(hoh_relative_path);
                }

                AppLog.e("HOH UniqueId: " + hohMemberUniqueId);
                AppLog.e("HOH Relative_path: " + hohMember_relative_path);

                if(previousHohInfoDataModel.getMarital_status().equalsIgnoreCase("Married") &&
                previousHohInfoDataModel.getGender().equalsIgnoreCase("Male")) {
                    binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewHohDetails.NumSpouseLayout.setVisibility(View.GONE);
                }
                if(n==1){
                    n=-1;
                    setAdapters();
                }
            } else {
                Log.i("FormPAge resetUnitDetails=", "step 12");
                aadharTextWatcher();
                ishohUploaded = false;
                clearHohFields();
            }
        } catch (Exception ex) {
            if(n==1){
                n=-1;
                setAdapters();
            }
            AppLog.logData(activity,ex.getMessage());
            Utils.shortToast("Exception in setDataIfPresent:" + ex.getMessage(), activity);
            AppLog.e("Exception in setDataIfPresent:" + ex.getMessage());
        }
    }

    private void clearHohFields() {
        try {
            Log.e("JJJJJJJJJ", " Hoh Data NOT Present");
            Log.e("etNameHousehold= ", " 3");
            binding.layoutNewHohDetails.etNameHousehold.setText("");

            binding.layoutNewHohDetails.etNumberSpouse.setText("");

            binding.layoutNewHohDetails.autoCompMaritialStatus.setText("");
            binding.layoutNewHohDetails.autoCompMaritialStatus.setTag("");
            binding.layoutNewHohDetails.etMaritalStatusOther.setText("");
            binding.layoutNewHohDetails.etMaritalStatusOther.setVisibility(View.GONE);

            binding.layoutNewHohDetails.etNameSpouse.setText("");
            binding.layoutNewHohDetails.etRespondentContact.setText("");
            unit_relative_path = previousUnitInfoPointDataModel.getRelative_path();
//        unit_relative_path = "/" + structUniqueId + "/" + unitUniqueId + "/";
            if(!isPrevHoh){
                hohUniqueId = "H_" + Utils.getEpochDateStamp();
                hoh_relative_path = unit_relative_path + hohUniqueId + "/";
                hohMemberUniqueId = hohUniqueId;
                hohMember_relative_path = hoh_relative_path;
            }
            binding.layoutNewHohDetails.etCount.setText("");
            binding.layoutNewHohDetails.genderRadioGroup.clearCheck();

            binding.layoutNewHohDetails.autoCompReligion.setText("");
            binding.layoutNewHohDetails.autoCompReligion.setTag("");
            binding.layoutNewHohDetails.etReligionOther.setText("");
            binding.layoutNewHohDetails.etReligionOther.setVisibility(View.GONE);

            binding.layoutNewHohDetails.autoCompWhichState.setText("");
            binding.layoutNewHohDetails.autoCompWhichState.setTag("");
            binding.layoutNewHohDetails.etWhichStateOther.setText("");
            binding.layoutNewHohDetails.etWhichStateOther.setVisibility(View.GONE);

            binding.layoutNewHohDetails.autoCompMotherTongue.setText("");
            binding.layoutNewHohDetails.autoCompMotherTongue.setTag("");
            binding.layoutNewHohDetails.etMotherTongueOther.setText("");
            binding.layoutNewHohDetails.etMotherTongueOther.setVisibility(View.GONE);

            binding.layoutNewHohDetails.autoCompEducation.setText("");
            binding.layoutNewHohDetails.autoCompEducation.setTag("");
            binding.layoutNewHohDetails.etEducationOther.setText("");
            binding.layoutNewHohDetails.etEducationOther.setVisibility(View.GONE);

            binding.layoutNewHohDetails.autoCompOccupation.setText("");
            binding.layoutNewHohDetails.autoCompOccupation.setTag("");
            binding.layoutNewHohDetails.etOccupationOther.setText("");
            binding.layoutNewHohDetails.etOccupationOther.setVisibility(View.GONE);

            binding.layoutNewHohDetails.autoCompWorkType.setText("");
            binding.layoutNewHohDetails.autoCompWorkType.setTag("");
            binding.layoutNewHohDetails.etTypeOfWorkOther.setText("");
            binding.layoutNewHohDetails.etTypeOfWorkOther.setVisibility(View.GONE);

            binding.layoutNewHohDetails.autoCompTransport.setText("");
            binding.layoutNewHohDetails.autoCompTransport.setTag("");
            binding.layoutNewHohDetails.etModeOfTransportOther.setText("");
            binding.layoutNewHohDetails.etModeOfTransportOther.setVisibility(View.GONE);

            binding.layoutNewHohDetails.autoCompVehicleType.setText("");
            binding.layoutNewHohDetails.autoCompVehicleType.setTag("");
            binding.layoutNewHohDetails.etVehicleTypeOther.setText("");
            binding.layoutNewHohDetails.etVehicleTypeOther.setVisibility(View.GONE);

            binding.layoutNewHohDetails.autoCompWorkPlace.setText("");
            binding.layoutNewHohDetails.autoCompWorkPlace.setTag("");

            binding.layoutNewHohDetails.etStatingYear.setText("");
            binding.layoutNewHohDetails.etStatingYear.setTag("");

            binding.layoutNewHohDetails.etIncome.setText("");
            binding.layoutNewHohDetails.etIncome.setTag("");

            binding.layoutNewHohDetails.autoCompRationColor.setText("");
            binding.layoutNewHohDetails.autoCompRationColor.setTag("");

            binding.layoutNewHohDetails.etAadhaarNumber.setText("");
            binding.layoutNewHohDetails.etAadhaarNumber.setTag("");

            binding.layoutNewHohDetails.etPanNumber.setText("");
            binding.layoutNewHohDetails.etPanNumber.setTag("");

            binding.layoutNewHohDetails.etRationCardNumber.setText("");
            binding.layoutNewHohDetails.etRationCardNumber.setTag("");

            binding.layoutNewHohDetails.radioGroupMemberAvailable.clearCheck();
            binding.layoutNewHohDetails.radioGroupHandicapped.clearCheck();
            binding.layoutNewHohDetails.radioGroupStay.clearCheck();

            binding.layoutNewHohDetails.etDob.setText("");
            binding.layoutNewHohDetails.etDob.setTag("");
        } catch (Exception ex) {
            Utils.shortToast("Exception in clearHohFields:" + ex.getMessage(), activity);
            AppLog.e("Exception in clearHohFields:" + ex.getMessage());
        }
    }

    private HohInfoDataModel getHohDataToLocal() {

        try {

            originalAadhaarNo = MaskedEditText.getOriginalText(binding.layoutNewHohDetails.etAadhaarNumber);

            String objectId = "";
            String globalid = "";

            hoh_rel_globalID = UUID.randomUUID().toString();
            hoh_Unique_ID = hohUniqueId;

            boolean isUploaded = false;

            if (previousHohInfoDataModel != null && !isPrevHoh) {

                objectId = previousHohInfoDataModel.getObejctId();
                globalid = previousHohInfoDataModel.getGlobalId();

                if (previousHohInfoDataModel.getAdhaar_verify_date() != null &&
                        !previousHohInfoDataModel.getAdhaar_verify_date().isEmpty() && aadhaarVerifyDate.isEmpty()) {
                    if(previousHohInfoDataModel.isUploaded())
                        aadhaarVerifyDate = formattedDateForAadhaar(previousHohInfoDataModel.getAdhaar_verify_date());
                    else
                        aadhaarVerifyDate = previousHohInfoDataModel.getAdhaar_verify_date();
                }

                hoh_rel_globalID = previousHohInfoDataModel.getRel_globalid();
                hoh_Unique_ID = previousHohInfoDataModel.getHoh_id();

                if (previousHohInfoDataModel.getObejctId() != null && !previousHohInfoDataModel.getObejctId().isEmpty()) {
                    isUploaded = true;
                }
            }

            setNullTag();
            unitUniqueId = previousUnitInfoPointDataModel.getUnit_id();
            // change attachment here
            hohInfoDataModel = new HohInfoDataModel(hohUniqueId, unitUniqueId,
                    hoh_rel_globalID, hoh_relative_path,
                    binding.layoutNewHohDetails.etNameHousehold.getText().toString(),
                    binding.layoutNewHohDetails.autoCompMaritialStatus.getTag().toString(),
                    binding.layoutNewHohDetails.etMaritalStatusOther.getText().toString(),
                    Utils.integerFormatter(binding.layoutNewHohDetails.etNumberSpouse.getText().toString()),
                    binding.layoutNewHohDetails.etNameSpouse.getText().toString(),
                    binding.layoutNewHohDetails.etRespondentContact.getText().toString(),
                    binding.layoutNewHohDetails.etDob.getText().toString(),
                    Utils.integerFormatter(binding.layoutNewHohDetails.etCount.getText().toString()),
                    // getRadioButtonText(binding.layoutNewHohDetails.genderRadioGroup),
                    hohInfo_gender,
                    Utils.integerFormatter(binding.layoutNewHohDetails.etStatingYear.getText().toString()),
                    originalAadhaarNo,
                    // sendAadhaarNo,
                    binding.layoutNewHohDetails.etPanNumber.getText().toString(),
                    binding.layoutNewHohDetails.autoCompRationColor.getTag().toString(),
                    binding.layoutNewHohDetails.etRationCardNumber.getText().toString(),
                    binding.layoutNewHohDetails.autoCompReligion.getTag().toString(),
                    binding.layoutNewHohDetails.etReligionOther.getText().toString(),
                    binding.layoutNewHohDetails.autoCompWhichState.getTag().toString(),
                    binding.layoutNewHohDetails.etWhichStateOther.getText().toString(),
                    binding.layoutNewHohDetails.autoCompMotherTongue.getTag().toString(),
                    binding.layoutNewHohDetails.etMotherTongueOther.getText().toString(),
                    binding.layoutNewHohDetails.autoCompEducation.getTag().toString(),
                    binding.layoutNewHohDetails.etEducationOther.getText().toString(),
                    binding.layoutNewHohDetails.autoCompOccupation.getTag().toString(),
                    binding.layoutNewHohDetails.etOccupationOther.getText().toString(),
                    binding.layoutNewHohDetails.autoCompWorkPlace.getTag().toString(),
                    binding.layoutNewHohDetails.autoCompWorkType.getTag().toString(),
                    binding.layoutNewHohDetails.etTypeOfWorkOther.getText().toString(),
                    binding.layoutNewHohDetails.etIncome.getText().toString(),
                    binding.layoutNewHohDetails.autoCompTransport.getTag().toString(),
                    binding.layoutNewHohDetails.etModeOfTransportOther.getText().toString(),
                    binding.layoutNewHohDetails.etNameLocation.getText().toString(),
                    getRadioButtonText(binding.layoutNewHohDetails.radioGroupHandicapped),
                    getRadioButtonText(binding.layoutNewHohDetails.radioGroupStay),
                    binding.layoutNewHohDetails.autoCompVehicleType.getTag().toString(),
                    binding.layoutNewHohDetails.etVehicleTypeOther.getText().toString(),
                    0, objectId, globalid, getDeathCertificate(), isUploaded, new Date(), new Date(),
                    binding.layoutNewHohDetails.radioVerifyAadharYes.isChecked() ? "Yes" : "No",
                    binding.layoutNewHohDetails.autoCompAadharRemarks.getText().toString(),
                    aadhaarVerifyDate);

            // change attachment here
        } catch (Exception ex) {
            Utils.shortToast("Exception in getHohDataToLocal:" + ex.getMessage(), activity);
            AppLog.e("Exception in getHohDataToLocal:" + ex.getMessage());
        }

        try {
            HashMap<String, ArrayList<HohInfoDataModel>> unitI = App.getInstance().getHohList();
            if (unitI != null && !unitI.isEmpty()) {
                ArrayList<HohInfoDataModel> hh = new ArrayList<>();
                hh.add(hohInfoDataModel);
                unitI.put(previousUnitInfoPointDataModel.getUnit_id(), hh);
                App.getInstance().setHohList(unitI);
            } else {
                HashMap<String, ArrayList<HohInfoDataModel>> pp = new HashMap<>();
                ArrayList<HohInfoDataModel> hh = new ArrayList<>();
                hh.add(hohInfoDataModel);
                pp.put(previousUnitInfoPointDataModel.getUnit_id(), hh);
                App.getInstance().setHohList(pp);
            }
        } catch (Exception ex) {
            ex.getMessage();
        }

        return hohInfoDataModel;
    }

    private void addAttachments() {
        try {
            okFileExtensions = new ArrayList<>();
            userAttachmentList = new ArrayList<>();
            memberPhotographAttachmentList = new ArrayList<>();
            memberAdhaarCardAttachmentList = new ArrayList<>();
            memberPanCardAttachmentList = new ArrayList<>();
            getPreviousMediaFileName = new ArrayList<>();
            memberInfoDataModelArrayList = new ArrayList<>();
            okFileExtensions.add("jpg");
            okFileExtensions.add("png");
            okFileExtensions.add("jpeg");
            okFileExtensions.add("pdf");
            binding.layoutNewHohDetails.btnBrowseAadhaar.setOnClickListener(view -> {
                cc = "hoh";
                FormPageViewModel.AttName = "Aadhar Card";
                showDocumentPopup(5, 1, 1001);
            });

            binding.layoutNewHohDetails.btnBrowsePanCard.setOnClickListener(view -> {
                cc = "hoh";
                FormPageViewModel.AttName = "Pan Card";
                showDocumentPopup(5, 2, 1001);
            });

            binding.layoutNewHohDetails.btnBrowseRation.setOnClickListener(view -> {
                cc = "hoh";
                FormPageViewModel.AttName = "Ration Card";
                showDocumentPopup(5, 3, 1001);
            });

            binding.layoutNewHohDetails.btnBrowseDeathCertificate.setOnClickListener(view -> {
                cc = "hoh";
                FormPageViewModel.AttName = "Death Certificate";
                showDocumentPopup(5, 4, 1001);
            });

            binding.layoutNewHohDetails.btnBrowseIncome.setOnClickListener(view -> {
                cc = "hoh";
                FormPageViewModel.AttName = "Salary Proof";
                showDocumentPopup(5, 5, 1001);
            });

            binding.layoutNewHohDetails.btnBrowseHandicapped.setOnClickListener(view -> {
                cc = "hoh";
                FormPageViewModel.AttName = "Disease Proof";
                showDocumentPopup(5, 6, 1001);
            });
        } catch (Exception ex) {
            Utils.shortToast("Exception in addAttachments:" + ex.getMessage(), activity);
            AppLog.e("Exception in addAttachments:" + ex.getMessage());
        }
    }


    private String getRadioButtonText(RadioGroup radioGroup) {
        try {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            if (selectedId != -1) {
                RadioButton radioSexButton = (RadioButton) activity.findViewById(selectedId);
                return radioSexButton.getText().toString();
            } else return "";
        } catch (Exception ex) {
            Utils.shortToast("Exception in getRadioButtonText:" + ex.getMessage(), activity);
            AppLog.e("Exception in getRadioButtonText:" + ex.getMessage());
            return "";
        }
    }

    private boolean isValid() {
        try {
            List<MediaInfoDataModel> list1 = localSurveyDbViewModel.getMediaInfoDataByCatUnit("Disease Proof", hohUniqueId);
            List<MediaInfoDataModel> listDC = localSurveyDbViewModel.getMediaInfoDataByCatUnit("Death Certificate", hohUniqueId);
            List<MediaInfoDataModel> listAdhar = localSurveyDbViewModel.getMediaInfoDataByCatUnit("Aadhar Card", hohUniqueId);
            List<MediaInfoDataModel> listPan = localSurveyDbViewModel.getMediaInfoDataByCatUnit("Pan Card", hohUniqueId);
            if (binding.layoutNewHohDetails.etNameHousehold.getText().toString().equals("")) {
                binding.layoutNewHohDetails.etNameHousehold.setError(activity.getString(R.string.mandatory_field));
                binding.layoutNewHohDetails.etNameHousehold.requestFocus();
                return false;
            } else if (!Utils.isNullOrEmpty(binding.layoutNewHohDetails.etNameHousehold.getText().toString()) && !binding.layoutNewHohDetails.etNameHousehold.getText().toString().matches("^[a-zA-Z\\s]+$")) {
                Utils.setError(binding.layoutNewHohDetails.etNameHousehold, "Invalid Name Format - Field should only contain letters", activity);
                binding.layoutNewHohDetails.etNameHousehold.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompMaritialStatus.getText().toString().equals("")) {
                binding.layoutNewHohDetails.autoCompMaritialStatus.setError(activity.getString(R.string.mandatory_field));
                binding.layoutNewHohDetails.autoCompMaritialStatus.requestFocus();
                return false;
            // } else if (binding.layoutNewHohDetails.radioGenderMale.isChecked()  && binding.layoutNewHohDetails.autoCompMaritialStatus.getText().toString().equals("Married") &&
            //         binding.layoutNewHohDetails.etNumberSpouse.getText().toString().equals("")) {
            //     binding.layoutNewHohDetails.etNumberSpouse.setError(activity.getString(R.string.mandatory_field));
            //     binding.layoutNewHohDetails.etNumberSpouse.requestFocus();
            //     return false;
            } else if (binding.layoutNewHohDetails.autoCompMaritialStatus.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                    binding.layoutNewHohDetails.etMaritalStatusOther.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etMaritalStatusOther.setError("Please Enter Marital Status");
                binding.layoutNewHohDetails.etMaritalStatusOther.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompMaritialStatus.getText().toString().equalsIgnoreCase("Married") &&
                    binding.layoutNewHohDetails.etNameSpouse.getText().toString().equals("")) {
                binding.layoutNewHohDetails.etNameSpouse.setError(activity.getString(R.string.mandatory_field));
                binding.layoutNewHohDetails.etNameSpouse.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompMaritialStatus.getText().toString().equalsIgnoreCase("Married") &&
                    !binding.layoutNewHohDetails.etNameSpouse.getText().toString().matches("^[a-zA-Z\\s]*$")) {
                binding.layoutNewHohDetails.etNameSpouse.setError(activity.getString(R.string.remove_numbers));
                binding.layoutNewHohDetails.etNameSpouse.requestFocus();
                return false;
            } else if (!binding.layoutNewHohDetails.etPanNumber.getText().toString().equals("")
                    && !binding.layoutNewHohDetails.etPanNumber.getText().toString().matches("^[A-Z]{5}[0-9]{4}[A-Z]$")) {
                binding.layoutNewHohDetails.etPanNumber.setError(activity.getString(R.string.pan_validations));
                binding.layoutNewHohDetails.etPanNumber.requestFocus();
                return false;
            } else if (!binding.layoutNewHohDetails.etAadhaarNumber.getText().toString().equals("")
                    && binding.layoutNewHohDetails.etAadhaarNumber.getText().toString().length() < 12) {
                binding.layoutNewHohDetails.etAadhaarNumber.setError(activity.getString(R.string.aadhar_validations));
                binding.layoutNewHohDetails.etAadhaarNumber.requestFocus();
                return false;
            } else if(!binding.layoutNewHohDetails.etAadhaarNumber.getText().toString().equals("") && listAdhar.isEmpty()){// valid adhar number
                Toast.makeText(activity, activity.getString(R.string.adharhOption_validations), Toast.LENGTH_SHORT).show();
                return false;
            } else if(!binding.layoutNewHohDetails.etAadhaarNumber.getText().toString().equals("") && !listAdhar.isEmpty() &&
                    !binding.layoutNewHohDetails.radioVerifyAadharYes.isChecked() && !binding.layoutNewHohDetails.radioVerifyAadharNo.isChecked()){
                Toast.makeText(activity, "Please select Aadhaar Verification option", Toast.LENGTH_SHORT).show();
                return false;
            } else if(!binding.layoutNewHohDetails.etAadhaarNumber.getText().toString().equals("") && !listAdhar.isEmpty() &&
                    binding.layoutNewHohDetails.autoCompAadharRemarks.getText().toString().isEmpty()){
                binding.layoutNewHohDetails.autoCompAadharRemarks.setError("Please select Aadhaar Card Verification Remarks");
                binding.layoutNewHohDetails.etAadhaarNumber.requestFocus();
                return false;
            } else if (!binding.layoutNewHohDetails.etRationCardNumber.getText().toString().equals("")
                    && !binding.layoutNewHohDetails.etRationCardNumber.getText().toString().matches("^[a-zA-Z0-9]*$")) {
                binding.layoutNewHohDetails.etRationCardNumber.setError(activity.getString(R.string.rationCard_validations));
                binding.layoutNewHohDetails.etRationCardNumber.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.etRespondentContact.getText().toString().length() < 10) {
                binding.layoutNewHohDetails.etRespondentContact.setError("The Contact Number Should be 10 Digits Long");
                binding.layoutNewHohDetails.etRespondentContact.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.etDob.getText().toString().equals("")) {
                binding.layoutNewHohDetails.etDob.setError(activity.getString(R.string.mandatory_field));
                View targetView = binding.layoutNewHohDetails.etDob;
                targetView.getParent().requestChildFocus(targetView, targetView);
                return false;
            } else if (!binding.layoutNewHohDetails.radioGenderMale.isChecked() &&
                    !binding.layoutNewHohDetails.radioGenderFemale.isChecked() && !binding.layoutNewHohDetails.radioGenderOther.isChecked()) {
                Toast.makeText(activity, activity.getString(R.string.gender_validations), Toast.LENGTH_SHORT).show();
                View targetView = binding.layoutNewHohDetails.radioGenderMale;
                targetView.getParent().requestChildFocus(targetView, targetView);
                return false;
            } else if (binding.layoutNewHohDetails.etStatingYear.getText().toString().equals("")) {
                binding.layoutNewHohDetails.etStatingYear.setError(activity.getString(R.string.mandatory_field));
                View targetView = binding.layoutNewHohDetails.etStatingYear;
                targetView.getParent().requestChildFocus(targetView, targetView);
                return false;
            } else if (binding.layoutNewHohDetails.radioHandicappedYes.isChecked() && list1.size() <= 0) {
                Toast.makeText(activity, activity.getString(R.string.divyangOption_validations), Toast.LENGTH_SHORT).show();
                binding.layoutNewHohDetails.radioHandicappedYes.requestFocus();
                return false;
            // }else if (!binding.layoutNewHohDetails.radioFamilyYes.isChecked() && !binding.layoutNewHohDetails.radioIndividualNo.isChecked()) {
                // Toast.makeText(activity, activity.getString(R.string.stayOption_validations), Toast.LENGTH_SHORT).show();
            // } else if (binding.layoutNewHohDetails.autoCompResidingMember.getText().toString().equals("")) {
                // binding.layoutNewHohDetails.autoCompResidingMember.setError(activity.getString(R.string.no_of_member_validations));
                // return false;
            } else if(binding.layoutNewHohDetails.radioMemberAvailableYes.isChecked() && listDC.isEmpty()  ){
                // vidnyan check for death cerificate
                Toast.makeText(activity, activity.getString(R.string.deathOption_validations), Toast.LENGTH_SHORT).show();
                binding.layoutNewHohDetails.radioMemberAvailableYes.requestFocus();
                return false;
            } else if (!binding.layoutNewHohDetails.etPanNumber.getText().toString().equals("") && listPan.isEmpty() ){
                // vidnyan Pan Card attachment check
                Toast.makeText(activity, activity.getString(R.string.panOption_validations), Toast.LENGTH_SHORT).show();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompReligion.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                    binding.layoutNewHohDetails.etReligionOther.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etReligionOther.setError("Please Enter Religion");
                binding.layoutNewHohDetails.etReligionOther.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompWhichState.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                    binding.layoutNewHohDetails.etWhichStateOther.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etWhichStateOther.setError("Please Enter State Name");
                binding.layoutNewHohDetails.etWhichStateOther.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompMotherTongue.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                    binding.layoutNewHohDetails.etMotherTongueOther.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etMotherTongueOther.setError("Please Enter Mother Tongue");
                binding.layoutNewHohDetails.etMotherTongueOther.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompEducation.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                    binding.layoutNewHohDetails.etEducationOther.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etEducationOther.setError("Please Enter Education");
                binding.layoutNewHohDetails.etEducationOther.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompOccupation.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                    binding.layoutNewHohDetails.etOccupationOther.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etOccupationOther.setError("Please Enter Occupation");
                binding.layoutNewHohDetails.etOccupationOther.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompWorkType.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                    binding.layoutNewHohDetails.etTypeOfWorkOther.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etTypeOfWorkOther.setError("Please Enter Type Of Work");
                binding.layoutNewHohDetails.etTypeOfWorkOther.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompTransport.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                    binding.layoutNewHohDetails.etModeOfTransportOther.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etModeOfTransportOther.setError("Please Enter Mode Of Transport");
                binding.layoutNewHohDetails.etModeOfTransportOther.requestFocus();
                return false;
            } else if (binding.layoutNewHohDetails.autoCompVehicleType.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                    binding.layoutNewHohDetails.etVehicleTypeOther.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etVehicleTypeOther.setError("Please Enter Type Of Vehicle");
                binding.layoutNewHohDetails.etVehicleTypeOther.requestFocus();
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            Utils.shortToast("Exception in isValid:" + ex.getMessage(), activity);
            AppLog.e("Exception in isValid:" + ex.getMessage());
            return false;
        }
    }

    private int getDeathCertificate() {
        int i = 2;
        try {
            if (binding.layoutNewHohDetails.radioMemberAvailableYes.isChecked()) {
                i = 1;
            } else if (binding.layoutNewHohDetails.radioMemberAvailableNo.isChecked()) {
                i = 0;
            }
        } catch (Exception ex) {
            Utils.shortToast("Exception in getDeathCertificate:" + ex.getMessage(), activity);
            AppLog.e("Exception in getDeathCertificate:" + ex.getMessage());
        }
        return i;
    }

    private String formattedDate(String receivedDate) {
        String displayDate = "";
        try {
            long timestamp = extractTimestamp("" + receivedDate);

            TimeZone timeZone = TimeZone.getTimeZone("IST");
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(timestamp);

            displayDate = formatDate(calendar, "dd/MM/yyyy");

        } catch (Exception e) {
            AppLog.logData(activity,e.getMessage());
            // Utils.shortToast("Exception in formattedDate:"+e.getMessage(),activity);
            AppLog.e("Exception in formattedDate:" + e.getMessage());
        }
        return displayDate;
    }

    private String formattedDateForAadhaar(String receivedDate) {

        try {

            long timestamp = extractTimestamp(receivedDate);

            TimeZone timeZone = TimeZone.getTimeZone("IST");
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(timestamp);

            return formatDate(calendar, "dd/MM/yyyy hh:mm aa");

        } catch (Exception e) {
            AppLog.logData(activity, e.getMessage());
            // Utils.shortToast("Exception in formattedDate:"+e.getMessage(),activity);
            AppLog.e("Exception in formattedDate:" + e.getMessage());
            return receivedDate;
        }
    }

    public int getAge(int year, int month, int dayOfMonth) {
        return Period.between(LocalDate.of(year, month, dayOfMonth), LocalDate.now()).getYears();
    }

    private static long extractTimestamp(String inputString) {
        int startIndex = inputString.indexOf("time=") + 5;
        int endIndex = inputString.indexOf(",", startIndex);
        String timestampString = inputString.substring(startIndex, endIndex);
        return Long.parseLong(timestampString);
    }

    private static String formatDate(Calendar calendar, String parseFormat) {
        TimeZone timeZone = TimeZone.getTimeZone("IST");
        SimpleDateFormat sdf = new SimpleDateFormat(parseFormat);
        sdf.setTimeZone(timeZone);
        return sdf.format(calendar.getTime());
    }

    private void showDocumentPopup(int usageType, int btnNumber, int selectedYear) {
        try {

            resident_ecbAttachmentList = new ArrayList<>();
            String[] arr = new String[30];

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_document_layout, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            dialogGlobal = dialog;
            AutoCompleteTextView autoCompDocType = customLayout.findViewById(R.id.autoCompDocType);
            EditText et_dob = customLayout.findViewById(R.id.et_dob);
            LinearLayout layout_btn_add = customLayout.findViewById(R.id.btn_add);
            LinearLayout btn_add = customLayout.findViewById(R.id.btn_add);
            LinearLayout addErrorLayout = customLayout.findViewById(R.id.addErrorLayout);
            LinearLayout btn_Browse_Doc = customLayout.findViewById(R.id.btn_Browse_Doc);
            LinearLayout addLayout = customLayout.findViewById(R.id.addLayout);
            LinearLayout viewLayout = customLayout.findViewById(R.id.viewLayout);
            LinearLayout viewNewLayout = customLayout.findViewById(R.id.viewNewLayout);
            LinearLayout deleteLayout = customLayout.findViewById(R.id.deleteLayout);
            AppCompatButton btnCancel = customLayout.findViewById(R.id.btn_Cancel);
            AppCompatButton btn_edit = customLayout.findViewById(R.id.btn_edit);
            AppCompatButton btn_delete = customLayout.findViewById(R.id.btn_delete);
            EditText title = customLayout.findViewById(R.id.docTitle);
            TextView docTitle = customLayout.findViewById(R.id.docTitle);
            TextView addTab = customLayout.findViewById(R.id.addTab);
            TextView viewTab = customLayout.findViewById(R.id.viewTab);
            LinearLayout viewNoRecord = customLayout.findViewById(R.id.noRecordLyt);
//        ImageView img_close = customLayout.findViewById(R.id.img_close);
            TextView edtypeProof = customLayout.findViewById(R.id.edtypeProof);
            TextView docType = customLayout.findViewById(R.id.docType);
            TextView docName = customLayout.findViewById(R.id.docName);
            TextView docDate = customLayout.findViewById(R.id.docDate);
            TextView remarks = customLayout.findViewById(R.id.remarks);
            TextView addErrorTextView = customLayout.findViewById(R.id.addErrorTextView);

            ImageView img_close = customLayout.findViewById(R.id.img_close);
            EditText calDeleteView = customLayout.findViewById(R.id.calDeleteView);
            LinearLayout btnBrowseEdit = customLayout.findViewById(R.id.btnBrowseEdit);

            EditText catDoc = customLayout.findViewById(R.id.catDoc);
            EditText typeDoc = customLayout.findViewById(R.id.typeDoc);
            EditText nameDoc = customLayout.findViewById(R.id.nameDoc);
            EditText remarksDoc = customLayout.findViewById(R.id.remarksDoc);
            EditText et_doc_remarks = customLayout.findViewById(R.id.et_doc_remarks);

            if (usageType == 5) {
                if (btnNumber == 1 && selectedYear == 1001) {
                    buttonName = "Aadhar Card";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.hoh_member_adhar);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 2 && selectedYear == 1001) {
//                buttonName = "hoh_member_Pan_Card";
                    buttonName = "Pan Card";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.hoh_member_pan);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 3 && selectedYear == 1001) {
//                buttonName = "hoh_member_Ration_Card";
                    buttonName = Constants.rationLabel;
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.hoh_member_ration);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 4 && selectedYear == 1001) {
//                buttonName = "hoh_member_Death_Certificate";
                    buttonName = "Death Certificate";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.hoh_member_death_certificate);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 5 && selectedYear == 1001) {
//                buttonName = "hoh_member_Salary_Proof";
                    buttonName = "Salary Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.hoh_member_salary_proof);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 6 && selectedYear == 1001) {
                    buttonName = "Disease Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.hoh_member_deseas);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }

            title.setText(FormPageViewModel.AttName);

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
            imageRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            imageRecycler.setAdapter(new HorizontalAdapter(new int[]{R.drawable.img_placeholder, R.mipmap.ic_launcher, R.drawable.img_placeholder, R.mipmap.ic_launcher, R.drawable.img_placeholder}, newMediaInfoDataModels, 1, activity));
            imageRecyclerDelete.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

            RecyclerView addImageRecycler = customLayout.findViewById(R.id.addImageRecycler);
            addImageAdapter = new AttachmentListAdapter(activity, null, "", this);
            addImageRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            addImageRecycler.setAdapter(addImageAdapter);

            addTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewTab.setTextColor(activity.getResources().getColor(R.color.black));
                    addTab.setTextColor(activity.getResources().getColor(R.color.summaryEditBoarderColor));
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
                    addTab.setTextColor(activity.getResources().getColor(R.color.black));
                    viewTab.setTextColor(activity.getResources().getColor(R.color.summaryEditBoarderColor));
                    addLayout.setVisibility(View.GONE);
                    deleteLayout.setVisibility(View.GONE);
                    viewLayout.setVisibility(View.GONE);
                    viewNewLayout.setVisibility(View.VISIBLE);

                    imageRecyclerDelete.setVisibility(View.GONE);

                    if (cc.equals("hoh")) {
                        List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(buttonName, hohUniqueId, false);
                        newMediaInfoDataModels = getMediaInfoData;
                        if (newMediaInfoDataModels != null && newMediaInfoDataModels.size()>0){
                        new Handler().postDelayed(() -> {
                            viewRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            viewAttachAdapter = new ViewAttachAdapter(getMediaInfoData, 1, activity, hoh_relative_path, hohUniqueId, HOHViewModel.this, localSurveyDbViewModel);
                            viewRecycler.setAdapter(viewAttachAdapter);
                        }, 1000);
                        }else {
                            viewNoRecord.setVisibility(View.VISIBLE);
                            viewNewLayout.setVisibility(View.GONE);
                        }
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
                    } else if (binding.layoutNewHohDetails.etNameHousehold.getText().toString().equals("")) {
                        dialog.dismiss();
                        binding.layoutNewHohDetails.etNameHousehold.setError(activity.getString(R.string.mandatory_field));
                        binding.layoutNewHohDetails.etNameHousehold.requestFocus();
                    } else {
                        addErrorLayout.setVisibility(View.GONE);
                        if (cc.equals("hoh")) {
                            // showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), hoh_relative_path, hohUniqueId + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());

                            // #970 : Name HOH
                            if(binding.layoutNewHohDetails.etNameHousehold.getText().toString().isEmpty()) {

                                if(dialog != null && dialog.isShowing())
                                    dialog.dismiss();

                                binding.layoutNewHohDetails.etNameHousehold.setError(activity.getString(R.string.mandatory_field));
                                binding.layoutNewHohDetails.etNameHousehold.requestFocus();
                                return;
                            }

                            String fileName = Utils.getAttachmentFileName(binding.layoutNewHohDetails.etNameHousehold.getText().toString(),
                                    autoCompDocType.getText().toString());
                            showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), hoh_relative_path, fileName);
                        }
                    }
                }
            });

            btnBrowseEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cc.equals("hoh")) {
                        // showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), hoh_relative_path, hohUniqueId + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());

                        // #970 : Name HOH
                        if(binding.layoutNewHohDetails.etNameHousehold.getText().toString().isEmpty()) {

                            if(dialog != null && dialog.isShowing())
                                dialog.dismiss();

                            binding.layoutNewHohDetails.etNameHousehold.setError(activity.getString(R.string.mandatory_field));
                            binding.layoutNewHohDetails.etNameHousehold.requestFocus();
                            return;
                        }

                        String fileName = Utils.getAttachmentFileName(binding.layoutNewHohDetails.etNameHousehold.getText().toString(),
                                autoCompDocType.getText().toString());
                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), hoh_relative_path, fileName);
                    }
                }
            });


            et_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dialog = new DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMaxDate(new Date().getTime());
                    dialog.show();
                }
            });

            calDeleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dialog = new DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
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
                            if (cc.equals("hoh")) {
                                attach = getmediaInfoDataList(userAttachmentList, hoh_relative_path, Constants.hoh_infoLayer,
                                        hohUniqueId, hoh_rel_global_id);
                            }

                            if (attach.size() > 0) {

                                mediaInfoDataModels1 = new ArrayList<>();

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
                                localSurveyDbViewModel.insertAllMediaInfoPointData(mediaInfoData, activity);
                                userAttachmentList = new ArrayList<>();
                                dialog.dismiss();
                                Utils.dismissProgress();
                            }
//                        setCounts(year);
                        }, 0);
                    }

                }
            });


            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            Utils.shortToast("Exception in showDocumentPopup:" + e.getMessage(), activity);
            AppLog.e("Exception in showDocumentPopup:" + e.getMessage());
        }
    }

    public void showAttachmentAlertDialogButtonClicked(String clickedFrom, String relative_path, String name) {

        try {
            if (!Utils.checkAutodateTimeValidation(this.activity)) {
                return;
            }

            attachmentFor = clickedFrom;
            target_relative_path = relative_path;
            target_name = name;

            // Create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            // set the custom layout
            final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_attachment_picker, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            TextView txt_Camera = customLayout.findViewById(R.id.txt_Camera);
            TextView txt_Gallery = customLayout.findViewById(R.id.txt_Gallery);
            TextView txt_ChooseFile = customLayout.findViewById(R.id.txt_ChooseFile);

            txt_Camera.setOnClickListener(view1 -> {
                if (AppPermissions.cameraPermission(activity, true)) {
                    imageUri = Utils.getCaptureImageOutputUri(activity, target_relative_path, target_name);
                    captureImagePath = Utils.getFile(activity, target_relative_path, target_name);
                    if (imageUri != null) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        activity.startActivityForResult(cameraIntent, selectCamera);
                        dialog.dismiss();
                    } else Utils.shortToast("Unable to capture image.", activity);
                }

            });

            txt_Gallery.setOnClickListener(view1 -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                activity.startActivityForResult(Intent.createChooser(intent, "Select File"), selectGallery);

                dialog.dismiss();
            });

            txt_ChooseFile.setOnClickListener(view1 -> {

                Intent intent = new Intent();

                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, true);
                activity.startActivityForResult(Intent.createChooser(intent, "Select Pdf"), selectPdf);

                dialog.dismiss();
            });

            dialog.show();
        } catch (Exception e) {
            Utils.shortToast("Exception in showAttachmentAlertDialogButtonClicked:" + e.getMessage(), activity);
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
            AppLog.logData(activity,e.getMessage());
            Utils.shortToast("Exception in getmediaInfoDataList:" + e.getMessage(), activity);
            AppLog.e("Exception in getmediaInfoDataList:" + e.getMessage());
        }
        return mediaInfoDataModels1;
    }

    private String[] getUpdatedDocList(List<MediaInfoDataModel> newMediaInfoDataModels, String[] arr) {
        ArrayList<String> tempList = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            tempList.add(arr[i]);
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


    @Override
    public void onAttachmentNameTextClicked(AttachmentListImageDetails attachmentListImageDetails) {
        try {
            String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;
            if (cc.equals("hoh")) {
                unit_unique_id = hohUniqueId;
                unitUniqueId = hohUniqueId;
            }
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

                    } else {
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
    public void onAttachmentCancelBtnClicked(String attachmentType, AttachmentListImageDetails attachmentListImageDetails) {
        try {
            Utils.deleteDirectory(new File(attachmentListImageDetails.getFilePath()));

            localSurveyDbViewModel.deleteMediaWithFileNameData(attachmentListImageDetails.getFileName(), activity);
            userAttachmentList.remove(attachmentListImageDetails);
            addImageAdapter.setAttachmentListImageDetails(userAttachmentList);

            switch (attachmentType) {
                case Constants.ShareCertificateLabel:
                    resident_scAttachmentList.remove(attachmentListImageDetails);
                    residentProofAttachmentCount--;
                    resident_scAttachmentListAdapter.setAttachmentListImageDetails(resident_scAttachmentList);
                    break;

                case Constants.NA_TaxLabel:
                    resident_nataxAttachmentList.remove(attachmentListImageDetails);
                    residentProofAttachmentCount--;
                    resident_naTaxAttachmentListAdapter.setAttachmentListImageDetails(resident_nataxAttachmentList);
                    break;
                case Constants.PropertyTaxPaymentReceiptLabel:
                    resident_ptprAttachmentList.remove(attachmentListImageDetails);
                    residentProofAttachmentCount--;
                    resident_ptprAttachmentListAdapter.setAttachmentListImageDetails(resident_ptprAttachmentList);
                    break;
                case Constants.MemberPhotograph:
                    memberPhotographAttachmentList.remove(attachmentListImageDetails);
                    memberPhotographAttachmentListAdapter.setAttachmentListImageDetails(memberPhotographAttachmentList);
                    break;
                case Constants.AadharCardAttachment:
                    memberAdhaarCardAttachmentList.remove(attachmentListImageDetails);
                    memberAdhaarCardAttachmentListAdapter.setAttachmentListImageDetails(memberAdhaarCardAttachmentList);
                    break;
                case Constants.PanCardttachment:
                    memberPanCardAttachmentList.remove(attachmentListImageDetails);
                    memberPanCardAttachmentListAdapter.setAttachmentListImageDetails(memberPanCardAttachmentList);
                    break;
            }
        } catch (Exception e) {
            Utils.shortToast("Exception in onAttachmentCancelBtnClicked:" + e.getMessage(), activity);
            AppLog.e("Exception in onAttachmentCancelBtnClicked:" + e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
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
                } else if (requestCode == selectGallery || requestCode == selectPdf) {
                    finalUri = data.getData();

                    if (okFileExtensions.contains(Utils.getFileExt(finalUri, activity))) {
                        isOkFileExtensions = true;
                        if (Utils.getFileSizeFromUri(activity, finalUri)) {
                            isOkFileSize = true;
                            finalFile = Utils.copyFile(activity, finalUri, target_relative_path, target_name);
                            finalFileName = finalFile.getName();
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
                        // if (!finalFileName.contains(".pdf"))
                        CryptoUtilsTest.encryptFileinAES(finalFile, 1);
                        switch (attachmentFor) {

                            case Constants.AadharCardAttachment:
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                            case Constants.PanCardttachment:
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                            case Constants.OthersDocument:
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                            case Constants.OwnerPhotoLable:
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                            case Constants.rationLabel:
                                residentProofAttachmentCount++;
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                            case "Aadhar Card":
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                            case "Pan Card":
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                            case "Salary Proof":
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                            case "Disease Proof":
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                            case "Death Certificate":
                                userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                                break;
                        }

                        if (addImageAdapter != null) {
                            addImageAdapter.setAttachmentListImageDetails(userAttachmentList);
                        }

                        if (alpha == 1 && updObj != null) {
                            String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;
                            if (cc.equals("hoh")) {
                                unit_unique_id = hohUniqueId;
                                unitUniqueId = hohUniqueId;
                            }
                            alpha = 0;
                            ArrayList<AttachmentListImageDetails> sample = userAttachmentList;
                            ArrayList<String> listImageDetails = new ArrayList<>();
                            ArrayList<String> add = new ArrayList<>();
                            if (updObj.getObejctId().equals("")) {
                                updObj = localSurveyDbViewModel.getByItemUrl(updObj.getParent_unique_id(),updObj.getFilename()).get(0);
                            } else {
                                updObj = localSurveyDbViewModel.getMediaInfoDataByObjId(updObj.getObejctId(),updObj.getParent_unique_id()).get(0);
                            }
                            // updObj=localSurveyDbViewModel.getMediaInfoDataByObjId(updObj.getObejctId(),updObj.getParent_unique_id()).get(0);
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
                            // setCounts();
                        }
                    } catch (CryptoException e) {
                        AppLog.logData(activity,e.getMessage());
                        Utils.shortToast("Error while encrypting the file.", activity);
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (Exception e) {
            AppLog.logData(activity,e.getMessage());
            Utils.shortToast("Exception in onActivityResult:" + e.getMessage(), activity);
            AppLog.e("Exception in onActivityResult:" + e.getMessage());
        }
    }

    @Override
    public void onAttachmentUpdateClicked(MediaInfoDataModel newMediaInfoDataModels) {
        try {
            if (newMediaInfoDataModels != null) {
                if (cc.equals("hoh")) {
                    localSurveyDbViewModel.setIsUploaded(hohUniqueId, newMediaInfoDataModels.getObejctId(), false);
                    localSurveyDbViewModel.setRemarksByMediaId(newMediaInfoDataModels.getMediaId(), newMediaInfoDataModels.getDocument_remarks());
                } else {
                    localSurveyDbViewModel.setIsUploaded(unitUniqueId, newMediaInfoDataModels.getObejctId(), false);
                }
                // localSurveyDbViewModel.setIsUploaded(unitUniqueId, newMediaInfoDataModels.getObejctId(), false);
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
        } catch (Exception e) {
            Utils.shortToast("Exception in onAttachmentUpdateClicked:" + e.getMessage(), activity);
            AppLog.e("Exception in onAttachmentUpdateClicked:" + e.getMessage());
        }
    }

    @Override
    public void onAttachmentDeletedClicked(List<MediaInfoDataModel> deleteTotalMediaList, int flag, int pos, List<AttachmentItemList> attItemLists, String itemUrl) {
        try {
            String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;
            if (cc.equals("hoh")) {
                unit_unique_id = hohUniqueId;
                unitUniqueId = hohUniqueId;
            }

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
            }
        } catch (Exception e) {
            AppLog.logData(activity,e.getMessage());
            // Utils.shortToast("Exception in onAttachmentDeleteClicked:"+e.getMessage(),activity);
            AppLog.e("Exception in onAttachmentDeleteClicked:" + e.getMessage());
        }
    }

    @Override
    public void onAttachmentBrowseClicked(MediaInfoDataModel mediaInfoDataModel, String documentType, String unitRelativePath, String name) {
        try {
            String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;
            if (cc.equals("hoh")) {
                unit_unique_id = hohUniqueId;
                unitUniqueId = hohUniqueId;
            }
            alpha = 1;
            updObj = mediaInfoDataModel;

            // #970 : Name HOH
            if(binding.layoutNewHohDetails.etNameHousehold.getText().toString().isEmpty()) {
                binding.layoutNewHohDetails.etNameHousehold.setError(activity.getString(R.string.mandatory_field));
                binding.layoutNewHohDetails.etNameHousehold.requestFocus();
                return;
            }

            name = Utils.getAttachmentFileName(binding.layoutNewHohDetails.etNameHousehold.getText().toString(), mediaInfoDataModel.getDocument_type());
            showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, name);
            // showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, "HOH_"+name);
        } catch (Exception e) {
            Utils.shortToast("Exception in onAttachmentBrowseClicked:" + e.getMessage(), activity);
            AppLog.e("Exception in onAttachmentBrowseClicked:" + e.getMessage());
        }
    }

    public void aadharTextWatcher() {
        binding.layoutNewHohDetails.etAadhaarNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    MaskedEditText.setMaskedText(binding.layoutNewHohDetails.etAadhaarNumber,binding.layoutNewHohDetails.etAadhaarNumber.getText().toString());
                }
                else{
                    binding.layoutNewHohDetails.etAadhaarNumber.setText(MaskedEditText.getOriginalText(binding.layoutNewHohDetails.etAadhaarNumber));

                }
            }
        });
//        try {
//            binding.layoutNewHohDetails.etAadhaarNumber.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////                    if(sendAadhaarNo.length()<12){
////                        try{
////                            sendAadhaarNo=sendAadhaarNo+charSequence.charAt(charSequence.length()-1);
////                        }catch (Exception ex){
////                            ex.getMessage();
////                            sendAadhaarNo="";
////                            originalAadhaarNo="";
////                        }
////                    }
//
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    originalAadhaarNo = s.toString();
//                    ArrayList<String> ch = new ArrayList<>();
//                    for (int i = 0; i < originalAadhaarNo.length(); i++) {
//
//                        if (i < 8) {
//                            ch.add("x");
//                        } else ch.add(String.valueOf(originalAadhaarNo.charAt(i)));
//
//                    }
//                    String listString = String.join("", ch);
//
//                    binding.layoutNewHohDetails.etAadhaarNumber.removeTextChangedListener(this); // Prevent infinite loop
//                    binding.layoutNewHohDetails.etAadhaarNumber.setText(listString);
//                    binding.layoutNewHohDetails.etAadhaarNumber.setSelection(ch.size());
//                    binding.layoutNewHohDetails.etAadhaarNumber.addTextChangedListener(this);
//                }
//            });
//        } catch (Exception e) {
//            Utils.shortToast("Exception in aadharTextWatcher:" + e.getMessage(), activity);
//            AppLog.e("Exception in aadharTextWatcher:" + e.getMessage());
//        }
    }

    @Override
    public boolean onBackKeyPress() {
        closeFormPopup();
        return true;
    }

    private void closeFormPopup() {
        YesNoBottomSheet.geInstance(activity, activity.getString(R.string.close_form_msg),
                activity.getResources().getString(R.string.yesBtn), activity.getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {

                    @Override
                    public void yesBtn() {
                        activity.finish();
                    }

                    @Override
                    public void noBtn() {

                    }
                }).show(((AppCompatActivity) activity).getSupportFragmentManager(), "");
    }

    private void maskAadharCardNumber(){
        ArrayList<String> ch = new ArrayList<>();
        for (int i = 0; i < originalAadhaarNo.length(); i++) {

            if (i < 8) {
                ch.add("x");
            } else ch.add(String.valueOf(originalAadhaarNo.charAt(i)));

        }
        String listString = String.join("", ch);

        binding.layoutNewHohDetails.etAadhaarNumber.setText(listString);
    }

    private void setNullTag() {

        if (binding.layoutNewHohDetails.autoCompMaritialStatus.getTag() == null) {
            binding.layoutNewHohDetails.autoCompMaritialStatus.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompRationColor.getTag() == null) {
            binding.layoutNewHohDetails.autoCompRationColor.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompReligion.getTag() == null) {
            binding.layoutNewHohDetails.autoCompReligion.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompWhichState.getTag() == null) {
            binding.layoutNewHohDetails.autoCompWhichState.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompMotherTongue.getTag() == null) {
            binding.layoutNewHohDetails.autoCompMotherTongue.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompEducation.getTag() == null) {
            binding.layoutNewHohDetails.autoCompEducation.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompOccupation.getTag() == null) {
            binding.layoutNewHohDetails.autoCompOccupation.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompWorkPlace.getTag() == null) {
            binding.layoutNewHohDetails.autoCompWorkPlace.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompWorkType.getTag() == null) {
            binding.layoutNewHohDetails.autoCompWorkType.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompTransport.getTag() == null) {
            binding.layoutNewHohDetails.autoCompTransport.setTag("");
        }

        if (binding.layoutNewHohDetails.autoCompVehicleType.getTag() == null) {
            binding.layoutNewHohDetails.autoCompVehicleType.setTag("");
        }
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
        btn_no.setVisibility(View.GONE);
        img_close.setVisibility(View.GONE);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);

        statusRadioGroup.setVisibility(View.GONE);

        if (Utils.isNullOrEmpty(yesBtn))
            btn_yes.setVisibility(View.GONE);

        btn_yes.setOnClickListener(view1 -> {
            binding.layoutNewHohDetails.autoCompUnitId.setText("");
            binding.layoutNewHohDetails.autoCompUnitId.setTag("");
            dialog.dismiss();
        });

        dialog.show();
    }

    public void generateAadhaarOTP(String aadhaarNo) {

        String hohName = binding.layoutNewHohDetails.etNameHousehold.getText().toString();

        try {

            aadhaarVerificationData = new AadhaarVerificationData();
            aadhaarVerificationData.setUnit_id(previousUnitInfoPointDataModel.getUnit_unique_id());
            aadhaarVerificationData.setHoh_adhaar_no(aadhaarNo);

            Utils.showProgress("Please wait while Sending OTP...", activity);

            JSONObject jObjRequest = new JSONObject();
            jObjRequest.put("aadhaar_number", aadhaarVerificationData.getHoh_adhaar_no());

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jObjRequest.toString());

            AppLog.logCustomData(activity, hohName + "_Generate_OTP_Request", jObjRequest.toString());

            Api_Interface apiInterfaceConfig = RetrofitService.getAadhaarVerificationClient().create(Api_Interface.class);
            Call<JsonElement> call = apiInterfaceConfig.generateAadhaarOTP(body);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                    Utils.dismissProgress();

                    try {

                        if (response.body() != null) {

                            AppLog.logCustomData(activity, hohName + "_Generate_OTP_Response", response.body().toString());

                            JSONObject jObjResponse = new JSONObject(response.body().toString());

                            if (response.code() == 200) {

                                JSONObject jObjContext = new JSONObject(jObjResponse.getString("context"));
                                aadhaarVerificationData.setGen_otp_transaction_id(jObjContext.getString("transaction_id"));
                                aadhaarVerificationData.setGen_otp_timestamp(jObjContext.getString("timestamp"));

                                if(jObjResponse.has("message")) {

                                    JSONObject jObjMessage = new JSONObject(jObjResponse.getString("message"));

                                    aadhaarVerificationData.setGen_otp_ref_id(jObjMessage.getString("ref_id"));
                                    aadhaarVerificationData.setGen_otp_message(jObjMessage.getString("message"));

                                    btn_generate_otp.setEnabled(false);
                                    btn_generate_otp.setFocusable(false);
                                    btn_generate_otp.setBackgroundResource(R.drawable.rounded_gray_edittext);

                                    startCountDown();

                                    Utils.shortToast(aadhaarVerificationData.getGen_otp_message(), activity);

                                } else if(jObjResponse.has("error")) {

                                    JSONObject jObjError = new JSONObject(jObjResponse.getString("error"));

                                    String code = jObjError.getString("code");
                                    String message = jObjError.getString("message");

                                    aadhaarVerificationData.setGen_otp_ref_id(code);
                                    aadhaarVerificationData.setGen_otp_message(message);

                                    String detailedMsg = "";

                                    if(code.equals("30001")) {
                                        detailedMsg = "Something went wrong while Generating OTP. Missing required parameter: aadhaar_number. Kindly Retry!!";
                                    } else if(code.equals("30002")) {
                                        detailedMsg = "Something went wrong while Generating OTP. Invalid Aadhaar Number Format. Kindly Retry!!";
                                    } else if(code.equals("30003")) {
                                        detailedMsg = "Something went wrong while Generating OTP. Invalid Aadhaar Card. Kindly Retry!!";
                                    } else if(code.equals("30004")) {
                                        detailedMsg = "Something went wrong while Generating OTP. Failed to generate OTP. Kindly Retry!!";
                                    } else {
                                        detailedMsg = "Something went wrong while Generating OTP. " + message + " Kindly Retry!!";
                                    }

                                    Utils.shortToast(detailedMsg, activity);

                                    AppLog.logCustomData(activity, hohName + "_Generate_OTP_Response", detailedMsg);
                                }
                            } else {

                                aadhaarVerificationData.setGen_otp_ref_id("-1");
                                aadhaarVerificationData.setGen_otp_message(jObjResponse.getString("message"));

                                String detailedMsg = "Something went wrong while Generating OTP. " + jObjResponse.getString("message") + " Kindly Retry!!";
                                Utils.shortToast(detailedMsg, activity);
                                AppLog.logCustomData(activity, hohName + "_Generate_OTP_Response", detailedMsg);
                            }
                        } else {
                            String detailedMsg = "Something went wrong while Generating OTP. Getting null Response. Kindly Retry!!";
                            Utils.shortToast(detailedMsg, activity);
                            aadhaarVerificationData.setGen_otp_ref_id("-1");
                            aadhaarVerificationData.setGen_otp_message(detailedMsg);
                            AppLog.logCustomData(activity, hohName + "_Generate_OTP_Response", detailedMsg);
                        }
                    } catch (Exception e) {
                        String detailedMsg = "Something went wrong while Generating OTP. " + e.getMessage() + " Kindly Retry!!";
                        AppLog.e(e.getMessage());
                        Utils.shortToast("Something went wrong while Generating OTP. " + e.getMessage() + " Kindly Retry!!", activity);
                        aadhaarVerificationData.setGen_otp_ref_id("-1");
                        aadhaarVerificationData.setGen_otp_message(detailedMsg);
                        AppLog.logCustomData(activity, hohName + "_Generate_OTP_Response", detailedMsg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    call.cancel();
                    String detailedMsg = "Something went wrong while Generating OTP. " + t.getMessage() + " Kindly Retry!!";
                    Utils.shortToast(detailedMsg, activity);
                    Utils.dismissProgress();
                    aadhaarVerificationData.setGen_otp_ref_id("-1");
                    aadhaarVerificationData.setGen_otp_message(detailedMsg);
                    AppLog.logCustomData(activity, hohName + "_Generate_OTP_Response", detailedMsg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            String detailedMsg = "Something went wrong while Generating OTP. " + e.getMessage() + " Kindly Retry!!";
            Utils.shortToast(detailedMsg, activity);
            Utils.dismissProgress();
            aadhaarVerificationData.setGen_otp_ref_id("-1");
            aadhaarVerificationData.setGen_otp_message(detailedMsg);
            AppLog.logCustomData(activity, hohName + "_Generate_OTP_Response", detailedMsg);
        }
    }

    public void verifyAadhaarOTP(String otp) {

        String hohName = binding.layoutNewHohDetails.etNameHousehold.getText().toString();

        try {

            Utils.showProgress("Please wait while Verifying OTP...", activity);

            JSONObject jObjRequest = new JSONObject();
            jObjRequest.put("ref_id", aadhaarVerificationData.getGen_otp_ref_id());
            jObjRequest.put("otp", otp);
            jObjRequest.put("name", binding.layoutNewHohDetails.etNameHousehold.getText().toString());

            JSONObject jObjValidate = new JSONObject();
            jObjValidate.put("type", 1);

            JSONObject jObjSettings = new JSONObject();
            jObjSettings.put("validate", jObjValidate);

            jObjRequest.put("settings", jObjSettings);
            jObjRequest.put("details", false);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jObjRequest.toString());

            AppLog.logCustomData(activity, hohName + "_Verify_OTP_Request", jObjRequest.toString());

            Api_Interface apiInterfaceConfig = RetrofitService.getAadhaarVerificationClient().create(Api_Interface.class);
            Call<JsonElement> call = apiInterfaceConfig.verifyAadhaarOTP(body);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                    Utils.dismissProgress();

                    try {

                        if (response.body() != null) {

                            AppLog.logCustomData(activity, hohName + "_Verify_OTP_Response", response.body().toString());

                            JSONObject jObjResponse = new JSONObject(response.body().toString());

                            if (response.code() == 200) {

                                JSONObject jObjContext = new JSONObject(jObjResponse.getString("context"));

                                aadhaarVerificationData.setVer_otp_transaction_id(jObjContext.getString("transaction_id"));
                                aadhaarVerificationData.setVer_otp_timestamp(jObjContext.getString("timestamp"));
                                aadhaarVerificationData.setVer_otp_ref_id(jObjContext.getString("ref_id"));

                                if(jObjResponse.has("message")) {

                                    JSONObject jObjMessage = new JSONObject(jObjResponse.getString("message"));
                                    aadhaarVerificationData.setVer_otp_status(jObjMessage.getString("status"));

                                    cv_verified_view.setVisibility(View.VISIBLE);

                                    JSONObject jObjValidate = new JSONObject(jObjMessage.getString("validate"));
                                    aadhaarVerificationData.setValidate_confidence(jObjValidate.getDouble("confidence"));
                                    aadhaarVerificationData.setValidate_status(jObjValidate.getString("status"));
                                    aadhaarVerificationData.setValidate_differences(jObjValidate.getString("differences"));

                                    isAadhaarVerified = true;

                                    try {
                                        binding.layoutNewHohDetails.autoCompAadharRemarks.setText(listAllAadhaarRemarks.get(0).toString());
                                    } catch (Exception e){
                                        binding.layoutNewHohDetails.autoCompAadharRemarks.setText("Aadhar Card Verified");
                                    }

                                    binding.layoutNewHohDetails.autoCompAadharRemarks.setEnabled(false);

                                    binding.layoutNewHohDetails.radioVerifyAadharYes.setChecked(true);
                                    binding.layoutNewHohDetails.radioVerifyAadharYes.setEnabled(false);
                                    binding.layoutNewHohDetails.radioVerifyAadharNo.setEnabled(false);

                                    aadhaarVerifyDate = Utils.getCurrentDate("dd/MM/yyyy hh:mm aa");

                                    new Handler().postDelayed(() -> {
                                        if (aadhaarVerificationDialog != null && aadhaarVerificationDialog.isShowing())
                                            aadhaarVerificationDialog.dismiss();
                                    }, 2000);

                                } else if(jObjResponse.has("error")) {

                                    isAadhaarVerified = false;

                                    binding.layoutNewHohDetails.radioVerifyAadharNo.setChecked(true);

                                    JSONObject jObjError = new JSONObject(jObjResponse.getString("error"));

                                    String code = jObjError.getString("code");
                                    aadhaarVerificationData.setVer_otp_ref_id(code);

                                    String message = jObjError.getString("message");
                                    aadhaarVerificationData.setVer_otp_message(jObjError.getString("message"));

                                    String detailedMsg = "";

                                    if(code.equals("40001")) {
                                        detailedMsg = "Something went wrong while Verifying OTP. Missing required parameter: ref_id. Kindly Retry!!";
                                    } else if(code.equals("40002")) {
                                        detailedMsg = "Something went wrong while Verifying OTP. Missing required parameter: otp. Kindly Retry!!";
                                    } else if(code.equals("40003")) {
                                        detailedMsg = "Something went wrong while Verifying OTP. Invalid or expired OTP. Kindly Retry!!";
                                    } else {
                                        detailedMsg = "Something went wrong while Verifying OTP. " + message + " Kindly Retry!!";
                                    }

                                    Utils.shortToast(detailedMsg, activity);

                                    AppLog.logCustomData(activity, hohName + "_Verify_OTP_Response", detailedMsg);
                                }
                            } else {

                                String detailedMsg = "Something went wrong while Verifying OTP. " + jObjResponse.getString("message") + " Kindly Retry!!";

                                Utils.shortToast(detailedMsg, activity);

                                isAadhaarVerified = false;
                                binding.layoutNewHohDetails.radioVerifyAadharNo.setChecked(true);
                                aadhaarVerificationData.setVer_otp_ref_id("-1");
                                aadhaarVerificationData.setVer_otp_message(jObjResponse.getString("message"));

                                AppLog.logCustomData(activity, hohName + "_Verify_OTP_Response", detailedMsg);
                            }
                        } else {

                            String detailedMsg = "Something went wrong while Verifying OTP. Getting null Response. Kindly Retry!!";
                            Utils.shortToast(detailedMsg, activity);

                            isAadhaarVerified = false;
                            binding.layoutNewHohDetails.radioVerifyAadharNo.setChecked(true);
                            aadhaarVerificationData.setVer_otp_ref_id("-1");
                            aadhaarVerificationData.setVer_otp_message(detailedMsg);

                            AppLog.logCustomData(activity, hohName + "_Verify_OTP_Response", detailedMsg);
                        }
                    } catch (Exception e) {

                        AppLog.e(e.getMessage());
                        String detailedMsg = "Something went wrong while Verifying OTP. " + e.getMessage() + " Kindly Retry!!";
                        Utils.shortToast(detailedMsg, activity);

                        isAadhaarVerified = false;
                        binding.layoutNewHohDetails.radioVerifyAadharNo.setChecked(true);
                        aadhaarVerificationData.setVer_otp_ref_id("-1");
                        aadhaarVerificationData.setVer_otp_message(detailedMsg);

                        AppLog.logCustomData(activity, hohName + "_Verify_OTP_Response", detailedMsg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                    call.cancel();

                    isAadhaarVerified = false;
                    binding.layoutNewHohDetails.radioVerifyAadharNo.setChecked(true);

                    Utils.dismissProgress();

                    String detailedMsg = "Something went wrong while Verifying OTP. " + t.getMessage() + " Kindly Retry!!";
                    Utils.shortToast(detailedMsg, activity);

                    aadhaarVerificationData.setVer_otp_ref_id("-1");
                    aadhaarVerificationData.setVer_otp_message(detailedMsg);

                    AppLog.logCustomData(activity, hohName + "_Verify_OTP_Response", detailedMsg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            String detailedMsg = "Something went wrong while Verifying OTP. " + e.getMessage() + " Kindly Retry!!";
            Utils.shortToast(detailedMsg, activity);
            isAadhaarVerified = false;
            binding.layoutNewHohDetails.radioVerifyAadharNo.setChecked(true);
            Utils.dismissProgress();
            aadhaarVerificationData.setVer_otp_ref_id("-1");
            aadhaarVerificationData.setVer_otp_message(detailedMsg);

            AppLog.logCustomData(activity, hohName + "_Verify_OTP_Response", detailedMsg);
        }
    }

    public void saveAadhaarVerificationDataToDB(String hohGlobalId) {
        if(aadhaarVerificationData != null && isAadhaarVerified) {
            aadhaarVerificationData.setHoh_id(hohGlobalId);
            aadhaarVerificationData.setAadhaarVerified(isAadhaarVerified);
            aadhaarVerificationData.setRemarks(binding.layoutNewHohDetails.autoCompAadharRemarks.getText().toString());
            aadhaarVerificationData.setUploaded(false);
            aadhaarVerificationData.setAdhaar_verify_by(App.getInstance().getUserModel().getUser_name());
            localSurveyDbViewModel.insertAadhaarVerificationDetail(aadhaarVerificationData);
        }
    }
}