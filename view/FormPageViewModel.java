package com.igenesys.view;

import static android.app.Activity.RESULT_OK;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.igenesys.utils.Constants.INTENT_DATA_StructureInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.arthenica.mobileffmpeg.FFprobe;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.google.android.material.radiobutton.MaterialRadioButton;
//import com.iceteck.silicompressorr.SiliCompressor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.igenesys.App;
import com.igenesys.BuildConfig;
import com.igenesys.HohActivity;
import com.igenesys.LoginActivity;
import com.igenesys.MapActivity;
import com.igenesys.MemberActivity;
import com.igenesys.R;
import com.igenesys.UnitActivity;
import com.igenesys.adapter.AttachmentListAdapter;
import com.igenesys.adapter.CustomExpandableListAdapter;
import com.igenesys.adapter.HorizontalAdapter;
import com.igenesys.adapter.ViewAttachAdapter;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureUnitIdStatusDataTable;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.databinding.ActivityUnitBinding;
import com.igenesys.datamin.MorphoTabletFPSensorDevice;
import com.igenesys.model.AddHohInfoDetailsModel;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.model.AttachmentListImageDetails;
import com.igenesys.model.AutoCompleteModal;
import com.igenesys.model.MemberInfoDetailsModel;
import com.igenesys.model.SurveyorData;
import com.igenesys.model.UserModel;
import com.igenesys.networks.Api_Interface;
import com.igenesys.networks.QueryResultRepoViewModel;
import com.igenesys.networks.RetrofitService;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.AppPermissions;
import com.igenesys.utils.Constants;
import com.igenesys.utils.CorrectImageRotation;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.FullScreenImage;
import com.igenesys.utils.GenericTextWatcher;
import com.igenesys.utils.MaskedEditText;
import com.igenesys.utils.TelephonyInfo;
import com.igenesys.utils.Utils;
import com.igenesys.utils.YesNoBottomSheet;
import com.morpho.android.usb.USBManager;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import org.bouncycastle.crypto.CryptoException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FormPageViewModel extends ActivityViewModel<UnitActivity> implements AttachmentListAdapter.OnAttachmentItemClickListner, ViewAttachAdapter.OnViewClickListner {

    static int position = 0;
    private final ActivityUnitBinding binding;
    private final Activity activity;
    public static boolean isHohData = false;
    private final int selectCamera = 10, selectGallery = 20, selectPdf = 30, selectVideoCamera = 40, GALLERYVIDEO = 50;//
    private final String[] mimetypes = {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
            "text/plain", "application/pdf", "image/*"};
    private final String[] pincodes = {"400017", "400019", "400022"};
    private final boolean resident_proof = false;
    private final boolean additional_document = false;
    private final boolean chain_document_proof = false;
    private final boolean license_proof = false;
    ArrayList<HohInfoDataModel> hohInfoDataModels;
    String target_relative_path = "", target_name = "";
    ArrayList<MediaInfoDataModel> mediaInfoDataModels;
    UserModel userModel;
    ArrayList<MediaInfoDataModel> hohMediaInfoDataModel;
    ArrayList<MediaInfoDataModel> memberMediaInfoDataModel;
    ArrayList<MediaInfoDataModel> mediaInfoDataModels1;
    File pdfPathFile = null;
    String previousObjectIdUnit = "", previousGlobalIdUnit = "";
    boolean isUnitUploaded = false;
    ArrayList<String> okFileExtensions = new ArrayList<>();
    ArrayList<String> okVideoExtensions = new ArrayList<>();
    ArrayList<String> nagarNames = new ArrayList<>();
    List<String> getPreviousMediaFileName;
    private ExpandableListAdapter expandableListAdapter;
    private ArrayList<HohInfoDataModel> expandableListTitle, newExpandableListTitle;
    private ArrayList<MemberInfoDataModel> memberInfoDataModels;
    private HashMap<String, ArrayList<MediaInfoDataModel>> unitMemberMediaListDetail;
    private HashMap<String, ArrayList<MediaInfoDataModel>> unitMediaListDetail;
    private HashMap<String, ArrayList<MemberInfoDataModel>> expandableListDetail;
    // private HashMap<String, ArrayList<MemberInfoDetailsModel>> expandableListMemberInfoDetail;
    private CustomExpandableListAdapter customExpandableListAdapter;
    private Calendar myCalendar;
    private Uri imageUri;
    private Uri videoUri;
    private File captureImagePath;
    private File captureVideoPath;
    private double markerPointLat, markerPointLong;
    private String work_area_name, scImagePath, viewMode = "";
    private Bitmap scBitmap, ecbBitmap, ppBitmap, ntBitmap, ptprBitmap, erBitmap, pgBitmap, acBitmap, pcBitmap;
    private File scFile, ecbFile, ppFile, ntFile, ptprFile, erFile, pgFile, acFile, pcFile;
    private ServiceFeatureTable structureInfo_FT, unitInfoFt, hohInfoFt, memberInfoFt;
    private String markerPointGeom, unitStructureUsage = "", structurePointGlobalIdStr, unitInfoGlobalIdStr;
    private Object structurePointGlobalId, unitInfoGlobalId;
    private GregorianCalendar cal, member_dob_value;
    private int year = 0;
    private Object floorDetailsGlobalId;
    private ArrayList<AddHohInfoDetailsModel> hohDetailsModels;
    private ArrayList<String> unitStatusArray;
    // private ArrayList<MemberInfoDetailsModel> memberInfoDetailsModels;
    private String unitAlertStatus, strutureAlertStatus, statusAlert = "", isMemberAvailable = "", isUnit_RentOwner = "", N_reamrk = "";
    private short ownership_proof, financial_documents, electric_bill, property_tax, na_tax, rent_agreement, chain_document, other_license, gumasta, member_available;
    private short aadhar_card, pan_card, id_card, address_proof, photograph, electrol_roll, share_certificate, eductational_certificate, employer_certificate;
    private String attachmentFor = "";
    private AttachmentListAdapter resident_scAttachmentListAdapter, resident_ecbAttachmentListAdapter, resident_ppAttachmentListAdapter, resident_naTaxAttachmentListAdapter, resident_ptprAttachmentListAdapter, resident_ErAttachmentListAdapter, addImageAdapter;
    private ArrayList<AttachmentListImageDetails> userAttachmentList, resident_scAttachmentList, resident_ecbAttachmentList, resident_ppAttachmentList, resident_nataxAttachmentList, resident_ptprAttachmentList, resident_erAttachmentList;
    private AttachmentListAdapter additionalSccsasAttachmentAdapter, additionalCiesaAttachmentAdapter, additionalAttachment4AttachmentAdapter, additionalAttachment3AttachmentAdapter;
    private ArrayList<AttachmentListImageDetails> additionalSccsasAttachmentList, additionalCiesaAttachmentList, additionalAttachment3AttachmentList, additionalAttachment4AttachmentList;
    private AttachmentListAdapter chainPsipcAttachmentAdapter, chainRaAttachmentAdapter, chainAttachment3AttachmentAdapter, chainAttachment4AttachmentAdapter;
    private ArrayList<AttachmentListImageDetails> chainPsipcAttachmentList, chainRaAttachmentList, chainAttachment3AttachmentList, chainAttachment4AttachmentList;
    private AttachmentListAdapter licenseProofSeAttachmentListAdapter, licenseProofRhlAttachmentListAdapter, licenseProofFdlAttachmentListAdapter, licenseProofFalAttachmentListAdapter;
    private ArrayList<AttachmentListImageDetails> licenseProofSeAttachmentList, licenseProofRhlAttachmentList, licenseProofFdlAttachmentList, licenseProofFalAttachmentList;
    private AttachmentListAdapter religiousOtherA1AttachmentListAdapter, religiousOtherA2AttachmentListAdapter, religiousOtherA3AttachmentListAdapter, religiousOtherA4AttachmentListAdapter;
    private ArrayList<AttachmentListImageDetails> religiousOtherA1AttachmentList, religiousOtherA2AttachmentList, religiousOtherA3AttachmentList, religiousOtherA4AttachmentList;
    private int residentProofAttachmentCount = 0, residentProofAdditionalAttachmentCount = 0, residentProofChainAttachmentCount = 0, licenseProofAttachmentListCount = 0, religiousAttachmentCount = 0, otherAttachmentCount = 0;
    private ArrayList<AttachmentListImageDetails> memberPhotographAttachmentList, memberAdhaarCardAttachmentList, memberPanCardAttachmentList;
    private AttachmentListAdapter memberPhotographAttachmentListAdapter, memberAdhaarCardAttachmentListAdapter, memberPanCardAttachmentListAdapter;
    private String memberType = "", memberGender = "", memberRentOwnership = "", handicapOrNot = "", stayingwith = "";
    private StructureInfoPointDataModel structureInfoPointDataModel;
    private UnitInfoDataModel unitInfoDataModel, unitInfoDataModel2;
    private HohInfoDataModel hohInfoDataModel;
    private MemberInfoDataModel memberInfoDataModel;
    private String structUniqueId = "", unitUniqueId = "", hohUniqueId = "", hoh_unique_id = "", memberUniqueId = "", hohMemberUniqueId = "", surveyor_name;
    private boolean editMode = false;
    private boolean isMemberValidated = false;
    private String unit_relative_path = "", hoh_relative_path = "", member_relative_path = "", hohMember_relative_path = "", hoh_rel_globalID = "", hoh_Unique_ID = "";
    private String unit_unique_id = "", unit_rel_global_id = "", hoh_rel_global_id = "";
    private LocalSurveyDbViewModel localSurveyDbViewModel;
    private HashMap<String, StructureInfoPointDataModel> structureInfoPointHashmap;
    private HashMap<String, UnitInfoDataModel> unitInfoDataModelHashMap;
    private HashMap<String, ArrayList<HohInfoDataModel>> hohInfoDataModelHashMap;
    private HashMap<String, ArrayList<MemberInfoDataModel>> memberInfoDataModelHashMap;
    private boolean resident_proof_attached = false, share_certificate_attached = false, electric_bill_attached = false, photo_pass_attached = false, na_tax_attached = false, property_tax_attached = false, electoral_roll_attached = false;
    private boolean additional_document_attached = false, school_college_certificate_attached = false, certificate_issued_attached = false;
    private boolean chain_document_attached = false, purchase_structure_attached = false, rent_agreement_attached = false;
    private boolean license_proof_attached = false, shop_estalishment_attached = false, restro_hotel_license_attached = false, food_drug_license_attached = false, factory_act_license_attached = false;
    private boolean religious_attachment_attached = false, others_attachment_attached = false;
    private HohInfoDataModel selectedDropDownHoh;
    private StructureInfoPointDataModel previousStructureInfoPointDataModel;
    private SurveyorData surveyorData;
    private UnitInfoDataModel previousUnitInfoPointDataModel;
    private HohInfoDataModel previousHohInfoDataModel, editableHohInfoDataModel, editableMemberHohInfoDataModel;
    private MemberInfoDataModel previousMemberInfoDataModel, editableMemberInfoDataModel;

    private ArrayList<String> infoHOH = new ArrayList<>();
    private ArrayList<String> infoMember = new ArrayList<>();
    private ArrayList<String> recievedInfoMember = new ArrayList<>();

    private boolean edd = false;
    private boolean isDocSelected = false;
    private int docYear = 0, unitVisitCount = 0;

    private boolean resBoolean = true;
    private boolean commBoolean = false;
    private boolean rcBoolean = false;
    private boolean othersBoolean = false;
    private boolean industrialBoolean = false;
    private boolean hoh = false;
    private boolean member = false;

    private boolean hohDob = false;

    private boolean memberDob = false;

    private boolean issueDatePanHoh = false;
    private boolean issueDatePanMember = false;
    private boolean exsComm = false;
    private boolean exsRes = false;
    //addhere

    private String buttonName = "",panchnamaRemarks="", thumbRemark = "", annexureRemarks ;;

    private AlertDialog dialogGlobal;

    List<MediaInfoDataModel> newMediaInfoDataModels;
    private boolean ishohUploaded = false, is_loft = false, isMemeberUploaded = false;

    private MediaInfoDataModel updObj;
    private int alpha = 0;
    private String savedUnitNumber = "";

    private ViewAttachAdapter viewAttachAdapter;

    public static String AttName = "";
    public static String BtnName = "";

    private int globalAge = 200;
    private int ageFlag = -1;
    private int memberAge = 200;
    public static String hohDOB = "", hohName = "", hohContct = "";
    private String globalPanchnamaPath = "";
    // private String hohRelation = "";
    private ImageView tempImageButton;
    // private LinearLayout tempImageLayout;
    // private RelativeLayout tempImageLayout;
    private FrameLayout tempImageLayout;

    private String radioLoft = "";
    private boolean flowFlag = false;
    private boolean flowFlagUnit = false;
    boolean resYes = false;
    String isResident = "";
    String cc = "";
    UnitInfoDataModel unitInfoDataModelGlobal;
    private String surveyDate="";
    private String surveyTime="";

    private String dateCheck="";
    Date dateRangeOne=null;
    Date dateRangeTwo=null;
    Date dateRangeThree=null;
    Date dateRangeFour=null;
    String globalUnitVideoPath="", unitVideoFileName="", unitVideoFilePath="",globalUnitPdfPath="",unitPdfFileName="",pdfPathRes="",pdfPathComm="";

    String extSince = "";
    String existingVideoFile = "";
    List<MediaInfoDataModel> llvd;
    private List<MediaInfoDataModel> tempMediaObj=null;
    private int sss=0;
    private boolean indenpendentDocSet=false;
    private String nameTrustees="";
    private String nameBelong="";

    EditText etHohMobileForOTP;
    String transactionID="";
    boolean isPanchnamaExist=false;

    private View previousViewTrust;
    private View previousViewTrustee;
    private String trustNameString="";
    private String trusteeNameString="";
    private ArrayList<EditText> trustViewsList =new ArrayList<>();

    final int REQUEST_CODE = 101;
    private String pImei="";
    private String sImei="";
    private String deviceN="";
    private double latLoc=0.0,lonLoc=0.0;

    private int st=0;
    private int ft=0;
    private RadioButton previousSelectedRadio;

    private boolean surveyFlag=false;
    private String usageToiletMeterBox="";
    private boolean floorFlag= false;

    private String actualFloorNo="";

    public FormPageViewModel(UnitActivity activity) {
        super(activity);

        this.activity = activity;
        binding = activity.getBinding();
        binding.stepView.go(1, true);
        try {
            userModel = App.getInstance().getUserModel();
            isHohData = false;
            try {
                latLoc=0.0;
                latLoc=Double.parseDouble(App.getSharedPreferencesHandler().getString("lat"));
            }catch (Exception ex){
                latLoc=0.0;
                ex.getMessage();
            }
            try {
                lonLoc=0.0;
                lonLoc=Double.parseDouble(App.getSharedPreferencesHandler().getString("lon"));
            }catch (Exception ex){
                lonLoc=0.0;
                ex.getMessage();
            }
            if (userModel != null) {
                initView();
                setUpListner();
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

    private void setUpExpandAble() {
        mediaInfoDataModels = new ArrayList<>();
        hohDetailsModels = new ArrayList<>();

        hohInfoDataModels = new ArrayList<>();
        expandableListTitle = new ArrayList<>();
        memberInfoDataModels = new ArrayList<>();

        expandableListDetail = new HashMap<>();
        // expandableListMemberInfoDetail = new HashMap<>();

        unitMemberMediaListDetail = new HashMap<>();

        binding.layoutUnitDetailInfo.layoutAddMember.autoCompSelectHoh.setAdapter(null);

        binding.layoutUnitDetailInfo.layoutAddMember.autoCompSelectHoh.setText("", false);
        customExpandableListAdapter = new CustomExpandableListAdapter(activity, expandableListTitle, expandableListDetail);
        expandableListAdapter = customExpandableListAdapter;
        binding.layoutUnitDetailInfo.expandableListView.setAdapter(expandableListAdapter);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
//        getIMEIDeviceId(getActivity());
        deviceN=App.getSharedPreferencesHandler().getString("deviceId");
        pImei=App.getSharedPreferencesHandler().getString("pImei");
        sImei=App.getSharedPreferencesHandler().getString("sImei");
        if(deviceN==null || deviceN.equals("N/A")){
            deviceN="";
        }if(pImei==null || pImei.equals("N/A")){
            pImei="";
        }if(sImei==null || sImei.equals("N/A")){
            sImei="";
        }
        structureInfoPointHashmap = new HashMap<>();
        unitInfoDataModelHashMap = new HashMap<>();
        hohInfoDataModelHashMap = new HashMap<>();
        memberInfoDataModelHashMap = new HashMap<>();

        okFileExtensions = new ArrayList<>();
        okVideoExtensions = new ArrayList<>();
        okFileExtensions.add("jpg");
        okFileExtensions.add("png");
        okFileExtensions.add("jpeg");
        okFileExtensions.add("pdf");

        okVideoExtensions.add("mp4");
        okVideoExtensions.add("mkv");
        okVideoExtensions.add("mov");
        okVideoExtensions.add("wmv");
        okVideoExtensions.add("flv");

        editMode = false;
        setUpExpandAble();
        structUniqueId = "S_" + Utils.getEpochDateStamp();
        unitStatusArray = new ArrayList<>();
        AppLog.e("UniqueId: " + structUniqueId);
        AppLog.e("Relative_path: " + structUniqueId);

        surveyor_name = userModel.getUser_name();
        binding.layoutNewUnitDetails.autoHOHna.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    binding.layoutNewUnitDetails.autoHOHna.setText(Utils.capitalizeEachWord(binding.layoutNewUnitDetails.autoHOHna.getText().toString()));
                }
            }
        });
        binding.layoutNewUnitDetails.etRespondentName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    binding.layoutNewUnitDetails.etRespondentName.setText(Utils.capitalizeEachWord(binding.layoutNewUnitDetails.etRespondentName.getText().toString()));
                }
            }
        });
        binding.layoutNewUnitDetails.etHOHname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    binding.layoutNewUnitDetails.etHOHname.setText(Utils.capitalizeEachWord(binding.layoutNewUnitDetails.etHOHname.getText().toString()));
                }
            }
        });

        if (activity.getIntent().hasExtra(Constants.WorkArea_work_area_name)) {
            work_area_name = activity.getIntent().getStringExtra(Constants.WorkArea_work_area_name);
        }
        fillUnitUniqueId();
        if (activity.getIntent().hasExtra(Constants.EDIT_TYPE)) {
            if (activity.getIntent().getStringExtra(Constants.EDIT_TYPE).toString().equals(Constants.EDIT_StructureInfo)) {
                flowFlag = false;
            } else if (activity.getIntent().getStringExtra(Constants.EDIT_TYPE).toString().equals(Constants.NewInfoData)) {
                flowFlag = false;
            } else if (activity.getIntent().getStringExtra(Constants.EDIT_TYPE).toString().equals(Constants.EDIT_UnitInfo)) {
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

                        if (!Utils.isNullOrEmpty(previousStructureInfoPointDataModel.getGeometry())) {
                            markerPointGeom = GeometryEngine.project(Geometry.fromJson(previousStructureInfoPointDataModel.getGeometry()), SpatialReference.create(Constants.SpatialReference)).toJson();
                            markerPointLat = Geometry.fromJson(markerPointGeom).getExtent().getCenter().getY();
                            markerPointLong = Geometry.fromJson(markerPointGeom).getExtent().getCenter().getX();
                        }
                        Log.i("FormPAge initView=", "step 2");

                        work_area_name = previousStructureInfoPointDataModel.getWork_area_name();

                        if (activity.getIntent().hasExtra(Constants.INTENT_DATA_SurveyorInfo)) {
                            Log.i("FormPAge initView=", "step 3");

                            surveyorData = (SurveyorData) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_SurveyorInfo);
                        }
                        if (activity.getIntent().hasExtra(Constants.INTENT_DATA_UnitInfo)) {
                            Log.i("FormPAge initView=", "step 3");

                            previousUnitInfoPointDataModel = (UnitInfoDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_UnitInfo);
                            Log.i("FormPAge getObejctId=", previousUnitInfoPointDataModel.getObejctId());
                        }
                        if (activity.getIntent().hasExtra(Constants.INTENT_DATA_HohInfo)) {
                            previousHohInfoDataModel = (HohInfoDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_HohInfo);
                        }
                        if (activity.getIntent().hasExtra(Constants.INTENT_DATA_MamberInfo)) {
                            memberInfoDataModels = (ArrayList<MemberInfoDataModel>) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_MamberInfo);
                        }

                        /*
                        Rohit
                         */


                    }
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


        /*
        Crash Found
         */
//        if (markerPointLat != 0.0 && markerPointLong != 0.0 && Utils.isConnected(activity)) {
//            binding.layoutStructureInfo.etAddress.setText(Utils.reverseGeocode(activity, markerPointLat, markerPointLong));
//        }

        binding.commonHeader.txtPageHeader.setText("Survey Form ( " + structUniqueId + " )");

        binding.layoutUnitDetailInfo.llayoutUnitDetailRemarks.setVisibility(View.GONE);
        binding.layoutUnitDetailInfo.llayoutUnitDetail.setVisibility(View.VISIBLE);
        localSurveyDbViewModel = ViewModelProviders.of(getActivity()).get(LocalSurveyDbViewModel.class);

        resident_scAttachmentList = new ArrayList<>();
        resident_ecbAttachmentList = new ArrayList<>();
        userAttachmentList = new ArrayList<>();
        resident_ppAttachmentList = new ArrayList<>();
        resident_nataxAttachmentList = new ArrayList<>();
        resident_ptprAttachmentList = new ArrayList<>();
        resident_erAttachmentList = new ArrayList<>();

        additionalSccsasAttachmentList = new ArrayList<>();
        additionalCiesaAttachmentList = new ArrayList<>();
        additionalAttachment3AttachmentList = new ArrayList<>();
        additionalAttachment4AttachmentList = new ArrayList<>();

        chainPsipcAttachmentList = new ArrayList<>();
        chainRaAttachmentList = new ArrayList<>();
        chainAttachment3AttachmentList = new ArrayList<>();
        chainAttachment4AttachmentList = new ArrayList<>();

        licenseProofSeAttachmentList = new ArrayList<>();
        licenseProofRhlAttachmentList = new ArrayList<>();
        licenseProofFdlAttachmentList = new ArrayList<>();
        licenseProofFalAttachmentList = new ArrayList<>();

        religiousOtherA1AttachmentList = new ArrayList<>();
        religiousOtherA2AttachmentList = new ArrayList<>();
        religiousOtherA3AttachmentList = new ArrayList<>();
        religiousOtherA4AttachmentList = new ArrayList<>();

        memberPhotographAttachmentList = new ArrayList<>();
        memberAdhaarCardAttachmentList = new ArrayList<>();
        memberPanCardAttachmentList = new ArrayList<>();

        if (viewMode.equals(Constants.online)){
            setNagarList();
        }else{
            Gson gson = new Gson();
            String json = App.getSharedPreferencesHandler().getString("nagarSaveList", null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            nagarNames=gson.fromJson(json, type);
        }
        setAccessList();
setYearOfStructureList();



        if (previousUnitInfoPointDataModel != null && !previousUnitInfoPointDataModel.getTenement_number().equals("")) {
            binding.layoutNewUnitDetails.autoCompDocTenement.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_doc_type)));
            setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDocTenement);
        }

//        binding.layoutNewUnitDetails.autoCompNagar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
////                            binding.layoutNewUnitDetails.autoCompNagar.setText(parent.getAdapter().getItem(position).toString());
//                if (parent.getAdapter().getItem(position).toString().equals(Constants.dropdown_other)) {
//                    binding.layoutNewUnitDetails.othersLayout.setVisibility(View.VISIBLE);
//                    nagar_name = binding.layoutNewUnitDetails.etNameNagarOther.getText().toString();
//                } else {
//                    binding.layoutNewUnitDetails.othersLayout.setVisibility(View.GONE);
//                    nagar_name = parent.getAdapter().getItem(position).toString();
////                    binding.layoutNewUnitDetails.autoCompNagar.setText("" + nagar_name);
//                }
//            }
//        });

        if (previousUnitInfoPointDataModel != null && !previousUnitInfoPointDataModel.getTenement_number().equals("")) {
            binding.layoutNewUnitDetails.autoCompDocTenementComm.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_doc_type)));
            setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDocTenementComm);
        }


        resetUnitDetails();
        setupUnitDetailsListner();



        /*
        Rohit
         */
//issue here
        String workAreaName= App.getSharedPreferencesHandler().getString(Constants.workAreaNameN);
        String s=App.getSharedPreferencesHandler().getString(workAreaName,"");
        if(s!=null && !s.equals("") && !s.isEmpty()){
            binding.layoutNewUnitDetails.autoCompNagar.setText(s);
            binding.layoutNewUnitDetails.autoCompNagar.setFocusable(false);
            binding.layoutNewUnitDetails.autoCompNagar.setEnabled(false);
            binding.layoutNewUnitDetails.autoCompNagar.setBackgroundResource(R.drawable.rounded_blue_edittext);
        }else{
            binding.layoutNewUnitDetails.autoCompNagar.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, nagarNames));
            setFocusChange_OnTouchForNagarName(binding.layoutNewUnitDetails.autoCompNagar, nagarNames);
        }


//        binding.layoutNewUnitDetails.autoCompRespondentRelation.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.relaionshipWithHOH)));
        ArrayList<AutoCompleteModal> listRelationshipWithHOH = Utils.getDomianList(Constants.domain_respondent_relationship_with_hoh);
        binding.layoutNewUnitDetails.autoCompRespondentRelation.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listRelationshipWithHOH));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompRespondentRelation, listRelationshipWithHOH);

        binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listRelationshipWithHOH));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm, listRelationshipWithHOH);


        binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setTag(parent.getAdapter().getItem(position).toString());
//                binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setText(Utils.getTextByTag(Constants.domain_relationship_with_hoh,parent.getAdapter().getItem(position).toString()));
                if (parent.getAdapter().getItem(position).toString().equals("Rented Tenant") && (binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() || binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked())) {
                   indenpendentDocSet=true;
                    setOwnerRentDocs();
                } else {

                    if (parent.getAdapter().getItem(position).toString().equals(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText("");
                    }

                    indenpendentDocSet=false;
                    if(!binding.layoutUnitDetailInfo.etExistenceSince.getText().toString().equals("")){
                        if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else{
                     setupYearOfStructure();}
                    }
                }
            }
        });

        binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setTag(parent.getAdapter().getItem(position).toString());
//                binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,parent.getAdapter().getItem(position).toString()));
                if (parent.getAdapter().getItem(position).toString().equals("Rented Tenant") && (binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() || binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked())) {
                   indenpendentDocSet=true;
                    setOwnerRentDocs();
                } else {

                    if (parent.getAdapter().getItem(position).toString().equals(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setText("");
                    }

                    indenpendentDocSet=false;
                    if(!binding.layoutUnitDetailInfo.etExistenceSince.getText().toString().equals("")){
                        if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else{
                        setupYearOfStructure();}
                    }
                }
            }
        });





        binding.layoutNewUnitDetails.autoCompDiety.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_diety)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDiety);
        binding.layoutNewUnitDetails.autoCompDiety.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompDiety.setTag(parent.getAdapter().getItem(position).toString());
                if (parent.getAdapter().getItem(position).toString().equals("Others")) {
                    binding.layoutNewUnitDetails.etDietyOther.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.etDietyOther.setVisibility(View.GONE);
                }
            }
        });

        binding.layoutNewUnitDetails.autoCompFaith.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_faith)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompFaith);
        binding.layoutNewUnitDetails.autoCompFaith.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompFaith.setTag(parent.getAdapter().getItem(position).toString());
                if (parent.getAdapter().getItem(position).toString().equals("Others")) {
                    binding.layoutNewUnitDetails.etFaithOther.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.etFaithOther.setVisibility(View.GONE);
                }
            }
        });


        binding.layoutNewUnitDetails.autoCompStructureOwnership.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_ownership_rel_amenities)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompStructureOwnership);
        binding.layoutNewUnitDetails.autoCompStructureOwnership.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompStructureOwnership.setTag(parent.getAdapter().getItem(position).toString());
            }
        });


        binding.layoutNewUnitDetails.autoCompStructureNature.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_nature_of_structure)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompStructureNature);
        binding.layoutNewUnitDetails.autoCompStructureNature.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompStructureNature.setTag(parent.getAdapter().getItem(position).toString());
                if (parent.getAdapter().getItem(position).toString().equals("Others")) {
                    binding.layoutNewUnitDetails.etStructureNatureOther.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.etStructureNatureOther.setVisibility(View.GONE);
                }
            }
        });

        binding.layoutNewUnitDetails.autoCompTenementRelAmenities.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_rel_amenities)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompTenementRelAmenities);
        binding.layoutNewUnitDetails.autoCompTenementRelAmenities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompTenementRelAmenities.setTag(parent.getAdapter().getItem(position).toString());
                if (parent.getAdapter().getItem(position).toString().equals("Others")) {
                    binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.setVisibility(View.GONE);
                }
            }
        });

        binding.layoutNewUnitDetails.autoCompReligiousRegistered.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_yes_no)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompReligiousRegistered);
        binding.layoutNewUnitDetails.autoCompReligiousRegistered.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompReligiousRegistered.setTag(parent.getAdapter().getItem(position).toString());
                if (parent.getAdapter().getItem(position).toString().equalsIgnoreCase("Yes")) {
                    binding.layoutNewUnitDetails.registeredYesLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.registeredYesLayout.setVisibility(View.GONE);
                }
            }
        });

        binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_structure_registered_with)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes);
        binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.setTag(parent.getAdapter().getItem(position).toString());
                if (parent.getAdapter().getItem(position).toString().equals("Others")) {
                    binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.setVisibility(View.GONE);
                }
            }
        });


        binding.layoutNewUnitDetails.autoCompNocYesNo.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_yes_no)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompNocYesNo);
        binding.layoutNewUnitDetails.autoCompNocYesNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompNocYesNo.setTag(parent.getAdapter().getItem(position).toString());
            }
        });

        binding.layoutNewUnitDetails.autoCompApprovalYesNo.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_yes_no)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompApprovalYesNo);
        binding.layoutNewUnitDetails.autoCompApprovalYesNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompApprovalYesNo.setTag(parent.getAdapter().getItem(position).toString());
            }
        });


        binding.layoutNewUnitDetails.autoCompFestivalYesNo.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_yes_no)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompFestivalYesNo);
        binding.layoutNewUnitDetails.autoCompFestivalYesNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompFestivalYesNo.setTag(parent.getAdapter().getItem(position).toString());
            }
        });

        binding.layoutNewUnitDetails.autoCompNoFloor.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.ra_total_no_of_floors)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompNoFloor);
        binding.layoutNewUnitDetails.autoCompNoFloor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                binding.layoutNewUnitDetails.autoCompNoFloor.setTag(parent.getAdapter().getItem(position).toString());
            }
        });

        binding.layoutNewUnitDetails.imgFirstTrustee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(binding.layoutNewUnitDetails.etFirstTrustee.getText().toString().equals("")){
                        Utils.shortToast("Please add trustee name!",activity);
                    }else{
                        EditText ed=null;
                        if(previousViewTrustee!=null){
                            ed=previousViewTrustee.findViewById(R.id.trustNameEditTexts);
                        }
                        if(ed!=null && ed.getText().toString().trim().equals("")){
                            Utils.shortToast("Please add name to continue!",activity);
                        }
                        else{
                            LayoutInflater inflater = activity.getLayoutInflater();
                            LinearLayout root = (LinearLayout) activity.findViewById(R.id.trusteeRootLayout);
                            if(root.getChildCount()<24){
                                View view = inflater.inflate(R.layout.layout_add_dynamic_edit_del, root, false);
                                ImageView deleteView=view.findViewById(R.id.deleteView);
                                EditText trustNameEditTexts=view.findViewById(R.id.trustNameEditTexts);
                                deleteView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        root.removeView(view);
                                        root.invalidate();
                                    }
                                });
                                previousViewTrustee=view;
                                root.addView(view);
                            }else{
                                Utils.shortToast("You can add maximum 25 records!",activity);
                            }
                        }
                    }
                }catch(Exception ex){
                    ex.getMessage();
                }

            }
        });

        binding.layoutNewUnitDetails.imgFirstBelongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(binding.layoutNewUnitDetails.etFirstBelongs.getText().toString().equals("")){
                        Utils.shortToast("Please add name to continue!",activity);
                    }else{
                        EditText ed=null;
                        if(previousViewTrust!=null){
                            ed=previousViewTrust.findViewById(R.id.trustNameEditTexts);
                        }
                        if(ed!=null && ed.getText().toString().trim().equals("")){
                            Utils.shortToast("Please add name to continue!",activity);
                        }
                        else{
                            LayoutInflater inflater = activity.getLayoutInflater();
                            LinearLayout root = activity.findViewById(R.id.trustRootLayout);
                            if(root.getChildCount()<24){
                                View view = inflater.inflate(R.layout.layout_add_dynamic_edit_del, root, false);
                                ImageView deleteView=view.findViewById(R.id.deleteView);
                                EditText trustNameEditTexts=view.findViewById(R.id.trustNameEditTexts);
                                deleteView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        root.removeView(view);
                                        root.invalidate();
                                    }
                                });
                                previousViewTrust=view;
                                root.addView(view);
                            }else{
                                Utils.shortToast("You can add maximum 25 records!",activity);
                            }
                        }
                    }
                }catch(Exception ex){
                    ex.getMessage();
                }
            }
        });

    }

    private void setAccessList() {
//        binding.layoutNewUnitDetails.autoCompNagar.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, nagarNames));
//        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompNagar);

        binding.layoutNewUnitDetails.autoCompAccess.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_access_to_unit)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompAccess);
    }

    private void setYearOfStructureList(){
        ArrayList<AutoCompleteModal> list=Utils.getDomianList(Constants.domain_structure_year);
        binding.layoutNewUnitDetails.etYearOfStructure.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, list));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.etYearOfStructure);
        binding.layoutNewUnitDetails.etYearOfStructureComm.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, list));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.etYearOfStructureComm);
    }

    private String getRandom() {
        return String.format("%04d", new Random().nextInt(1000));
    }

    /*
    @author: Komal Saini
     */
    private void setNagarList() {
        Utils.updateProgressMsg("Loading..", activity);
        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(getActivity()).get(QueryResultRepoViewModel.class);
        queryResultRepoViewModel.initNagarResult("1=1", "nagarname", false, true, "json");

        queryResultRepoViewModel.getNagarMutableLiveData().observe(getActivity(), updatedRecordResponse -> {

            if (updatedRecordResponse != null) {
                Utils.dismissProgress();
//                nagarNames=new ArrayList<>();
                for (int i = 0; i < updatedRecordResponse.getFeatures().size(); i++) {
                    nagarNames.add(updatedRecordResponse.getFeatures().get(i).getAttributes().getNagarname());
                }
                nagarNames.sort(String::compareToIgnoreCase);
                Utils.dismissProgress();
            } else {
                Toast.makeText(activity, "No data found", Toast.LENGTH_SHORT).show();
                Utils.dismissProgress();
            }
        });
    }

    private void resetUnitDetails() {
        Log.i("FormPAge resetUnitDetails=", "step 1");

        getPreviousMediaFileName = new ArrayList<>();
        String id =App.getSharedPreferencesHandler().getString("unit_id");
        unitUniqueId = id;
//        unitUniqueId = "U_" + Utils.getEpochDateStamp();
        unit_unique_id = unitUniqueId;
        hohUniqueId = "H_" + Utils.getEpochDateStamp();
        hoh_relative_path = unit_relative_path + hohUniqueId + "/";
        resident_proof_attached = false;
        share_certificate_attached = false;
        electric_bill_attached = false;
        photo_pass_attached = false;
        na_tax_attached = false;
        property_tax_attached = false;
        electoral_roll_attached = false;

        additional_document_attached = false;
        school_college_certificate_attached = false;
        certificate_issued_attached = false;

        chain_document_attached = false;
        purchase_structure_attached = false;
        rent_agreement_attached = false;

        license_proof_attached = false;
        shop_estalishment_attached = false;
        restro_hotel_license_attached = false;
        food_drug_license_attached = false;
        factory_act_license_attached = false;

        religious_attachment_attached = false;
        others_attachment_attached = false;

        unitMediaListDetail = new HashMap<>();
        hohMediaInfoDataModel = new ArrayList<>();
        memberMediaInfoDataModel = new ArrayList<>();
        binding.layoutUnitDetailInfo.radioGroupMemberAvailable.clearCheck();
        binding.layoutUnitDetailInfo.radioGroupUnitStatusRentOwner.clearCheck();


//        binding.layoutNewUnitDetails.etUniqueNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    if (!savedUnitNumber.equals(""))
//                        binding.layoutNewUnitDetails.etUniqueNo.setText(savedUnitNumber);
//                } else {
//                    savedUnitNumber = binding.layoutNewUnitDetails.etUniqueNo.getText().toString();
//                    String s = "";
//                    for (int i = 0; i < savedUnitNumber.length(); i++) {
//                        s = s + "*";
//                    }
//                    binding.layoutNewUnitDetails.etUniqueNo.setText(s);
//                }
//            }
//        });


        try {
            binding.layoutNewUnitDetails.etTenementNo.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        binding.layoutNewUnitDetails.autoCompDocTenement.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_doc_type)));
                        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDocTenement);
                        binding.layoutNewUnitDetails.autoCompDocTenementComm.setBackgroundResource(R.drawable.rounded_white_edittext);
//                    binding.layoutNewUnitDetails.autoCompDocTenement.setText("");
                    } else {
                        binding.layoutNewUnitDetails.autoCompDocTenement.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                        binding.layoutNewUnitDetails.autoCompDocTenement.setText("");
                        binding.layoutNewUnitDetails.autoCompDocTenement.setTag("");
//                    binding.layoutNewUnitDetails.autoCompDocTenement.setSelection(0, binding.layoutNewUnitDetails.autoCompDocTenement.getText().length());
                        binding.layoutNewUnitDetails.autoCompDocTenement.requestFocus();
                        binding.layoutNewUnitDetails.autoCompDocTenement.dismissDropDown();
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }
            });


            binding.layoutNewUnitDetails.etTenementNoComm.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        binding.layoutNewUnitDetails.autoCompDocTenementComm.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_doc_type)));
                        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDocTenementComm);
                        binding.layoutNewUnitDetails.autoCompDocTenementComm.setBackgroundResource(R.drawable.rounded_white_edittext);
//                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setText("");
                    } else {
                        binding.layoutNewUnitDetails.autoCompDocTenementComm.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                        binding.layoutNewUnitDetails.autoCompDocTenementComm.setText("");
//                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setSelection(0, binding.layoutNewUnitDetails.autoCompDocTenement.getText().length());
                        binding.layoutNewUnitDetails.autoCompDocTenementComm.requestFocus();
                        binding.layoutNewUnitDetails.autoCompDocTenementComm.dismissDropDown();
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }
            });
        } catch (Exception ex) {
            AppLog.logData(activity,ex.getMessage());
            ex.getCause();
        }

        if (previousUnitInfoPointDataModel != null) {
            try {
                String F=getFloorNumber(previousUnitInfoPointDataModel.getUnit_unique_id());
                actualFloorNo=F;
                if(!F.equalsIgnoreCase("0")){
                    floorFlag=true;
                    App.getSharedPreferencesHandler().putBoolean(Constants.floorFlag,true);
                }else{
                    floorFlag=false;
                    App.getSharedPreferencesHandler().putBoolean(Constants.floorFlag,false);
                }

                if(previousUnitInfoPointDataModel.getUnit_status().equalsIgnoreCase(Constants.NotApplicable_statusLayer)){
                    surveyFlag=true;
                    binding.layoutNewUnitDetails.unitUniqueIdAttachLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.txtMemberAvailable.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.radioGroupMemberAvailable.setVisibility(View.GONE);
                    binding.txtNext.setText("Finish");
                    resYes = false;
                    isResident = "No";
                    binding.layoutNewUnitDetails.residentYesLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.detailsLayout.setVisibility(View.GONE);

                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("UnitType", unitUniqueId, false);
                    if(ll!=null && ll.size()>0){
                        binding.layoutNewUnitDetails.etAttacheUnitUnique.setText(ll.size() + " " + "out of 1 attached");
                    }
                }
            }catch (Exception ex){
                ex.getCause();
            }

            disableViews(binding.layoutNewUnitDetails.autoCompAccess);

//            List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(activity.getString(R.string.unit_distometer_video), unit_unique_id, false);
//            if(getMediaInfoData!=null && getMediaInfoData.size()>0){
//                globalUnitVideoPath=getMediaInfoData.get(0).getItem_url();
//            }
            thumbRemark = previousUnitInfoPointDataModel.getThumb_remarks();
            annexureRemarks = previousUnitInfoPointDataModel.getAnnexure_remarks();
            panchnamaRemarks = previousUnitInfoPointDataModel.getPanchnama_form_remarks();
            llvd = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.unit_distometer_video), previousUnitInfoPointDataModel.getUnit_id(), false);
            if (llvd != null && !llvd.isEmpty()) {

                if (!llvd.get(0).getFilename().contains("."))
                    llvd.get(0).setFilename(llvd.get(0).getFilename() + ".mp4");

                existingVideoFile = llvd.get(0).getFilename();
//                globalUnitVideoPath = llvd.get(0).getItem_url();
            }

            if (previousUnitInfoPointDataModel.getAccess_to_unit() != null && !previousUnitInfoPointDataModel.getAccess_to_unit().equalsIgnoreCase("")) {
                binding.layoutNewUnitDetails.autoCompAccess.setText(previousUnitInfoPointDataModel.getAccess_to_unit());
            }

            if(previousUnitInfoPointDataModel.getCountry_name()!=null && !previousUnitInfoPointDataModel.getCountry_name().equalsIgnoreCase("")){
                binding.layoutNewUnitDetails.etCountry.setText(previousUnitInfoPointDataModel.getCountry_name());
                binding.layoutNewUnitDetails.etState.setText(previousUnitInfoPointDataModel.getState_name());
                binding.layoutNewUnitDetails.etLandcity.setText(previousUnitInfoPointDataModel.getCity_name());
            }else if(previousStructureInfoPointDataModel.getCountry_name()!=null && !previousStructureInfoPointDataModel.getCountry_name().equalsIgnoreCase("")){
                binding.layoutNewUnitDetails.etCountry.setText(previousStructureInfoPointDataModel.getCountry_name());
                binding.layoutNewUnitDetails.etState.setText(previousStructureInfoPointDataModel.getState_name());
                binding.layoutNewUnitDetails.etLandcity.setText(previousStructureInfoPointDataModel.getCity_name());
            }else{
                binding.layoutNewUnitDetails.etCountry.setText("India");
                binding.layoutNewUnitDetails.etState.setText("Maharashtra");
                binding.layoutNewUnitDetails.etLandcity.setText("Mumbai");
            }

            unitStructureUsage = previousUnitInfoPointDataModel.getUnit_usage();

            if (!previousUnitInfoPointDataModel.isMember_available() && (previousUnitInfoPointDataModel.getRespondent_dob() == null || previousUnitInfoPointDataModel.getRespondent_dob().equals(""))) {
                /*
                Start of member not available
                 */

                if (Utils.isNullOrEmpty(previousUnitInfoPointDataModel.getUnit_id())) {
                    String idd =App.getSharedPreferencesHandler().getString("unit_id");
                    unitUniqueId = idd;
//                    unitUniqueId = "U_" + Utils.getEpochDateStamp();
                } else unitUniqueId = previousUnitInfoPointDataModel.getUnit_id();

                unit_unique_id = unitUniqueId;


                previousObjectIdUnit = previousUnitInfoPointDataModel.getObejctId();
                previousGlobalIdUnit = previousUnitInfoPointDataModel.getGlobalId();
                //isUnitUploaded = previousUnitInfoPointDataModel.isUploaded();
                if (previousUnitInfoPointDataModel.getObejctId() != null && !previousUnitInfoPointDataModel.getObejctId().equals("")) {
                    isUnitUploaded = true;
                } else {
                    isUnitUploaded = false;
                }


                unit_relative_path = "/" + structUniqueId + "/" + unitUniqueId + "/";
                unit_unique_id = previousUnitInfoPointDataModel.getUnit_id();
                unit_rel_global_id = previousUnitInfoPointDataModel.getRel_globalid();
                hoh_relative_path = unit_relative_path + hohUniqueId + "/";
                AppLog.e("UniqueId: " + unitUniqueId);
                AppLog.e("UniqueId==: " + previousUnitInfoPointDataModel.getHut_number());
                AppLog.e("Relative_path: " + unit_relative_path);

                binding.layoutNewUnitDetails.autoCompRemarks.setTag(previousUnitInfoPointDataModel.getRespondent_non_available_remark());
                binding.layoutNewUnitDetails.autoCompRemarks.setText(Utils.getTextByTag(Constants.domain_respondent_non_available,previousUnitInfoPointDataModel.getRespondent_non_available_remark()));

                try{
                    if(previousUnitInfoPointDataModel.getRespondent_non_available_remark().equalsIgnoreCase("Unit is locked and Notice pasted"))
                    {
                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Notice_Pasted", unitUniqueId, false);
                        if(ll!=null && ll.size()>=0){
                            binding.layoutNewUnitDetails.etAttacheNotice.setText(ll.size() + " " + "out of 1 attached");
                        }
                        binding.layoutNewUnitDetails.noticeLayout.setVisibility(View.VISIBLE);
                    }else{
                        binding.layoutNewUnitDetails.noticeLayout.setVisibility(View.GONE);
                    }
                }catch (Exception ex){
                    AppLog.logData(activity,ex.getMessage());
                    ex.getMessage();
                }

                binding.layoutNewUnitDetails.autoCompRemarks.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_respondent_non_available)));
                setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompRemarks);
                binding.layoutNewUnitDetails.autoCompRemarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                        //floor_no=binding.layoutStructureDetailsLayout.autoCompUnitFloorDetails.getText().toString();
                        N_reamrk = parent.getAdapter().getItem(position).toString();
                        try {
                            if(parent.getAdapter().getItem(position).toString().equalsIgnoreCase("Unit is locked and Notice pasted"))
                            {
                                binding.layoutNewUnitDetails.noticeLayout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.noticeLayout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.autoCompRemarks.setTag(parent.getAdapter().getItem(position).toString());
                            binding.layoutNewUnitDetails.autoCompRemarks.setText(Utils.getTextByTag(Constants.domain_respondent_non_available,parent.getAdapter().getItem(position).toString()));
                        }catch (Exception ex){
                            AppLog.logData(activity,ex.getMessage());
                            ex.getMessage();
                        }
//                        binding.layoutNewUnitDetails.autoCompRemarks.setText(N_reamrk);
                    }
                });
            /*
            Rohit
             */
                if (previousUnitInfoPointDataModel.getUnit_unique_id() != null) {
                    binding.layoutNewUnitDetails.etUniqueNo.setText(previousUnitInfoPointDataModel.getUnit_unique_id());
                    binding.layoutNewUnitDetails.etUniqueNo.setAdapter(null);
                    disableUnitIdSelection();
//                    String s = "";
//                    for (int i = 0; i < previousUnitInfoPointDataModel.getUnit_unique_id().length(); i++) {
//                        s = s + "*";
//                    }
//                    binding.layoutNewUnitDetails.etUniqueNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(previousUnitInfoPointDataModel.getUnit_unique_id().length())});
                    //binding.layoutNewUnitDetails.etConfUnitNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(previousUnitInfoPointDataModel.getUnit_unique_id().length())});
//                    binding.layoutNewUnitDetails.etUniqueNo.setText("" + s);
                   // binding.layoutNewUnitDetails.etConfUnitNumber.setText(previousUnitInfoPointDataModel.getUnit_unique_id());
//                    binding.layoutNewUnitDetails.etUniqueNo.setFocusable(false);
                   // binding.layoutNewUnitDetails.etConfUnitNumber.setFocusable(false);
                } else {
//                    binding.layoutNewUnitDetails.etUniqueNo.setText("");
                   // binding.layoutNewUnitDetails.etConfUnitNumber.setText("");
                }
                previousStructureInfoPointDataModel = (StructureInfoPointDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);
                previousUnitInfoPointDataModel = (UnitInfoDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_UnitInfo);




                try {
                    Date currentDate = new Date(previousUnitInfoPointDataModel.getLast_edited_date().toString());

                    // Format the date as "DD/MM/YYYY"
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(currentDate);

                    // Format the time as "HH:mm:a"
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:a", Locale.getDefault());
                    String formattedTime = timeFormat.format(currentDate);
//                    binding.layoutSurveyorDetailsLayout.etSurveyDate.setText(formattedDate);
//                    binding.layoutSurveyorDetailsLayout.etSurveyTime.setText(formattedTime);
                    surveyDate = previousUnitInfoPointDataModel.getSurvey_date().toString();
                    surveyTime = previousUnitInfoPointDataModel.getSurvey_time().toString();

                } catch (Exception e) {
                    AppLog.logData(activity,e.getMessage());
                    AppLog.e("Exception = " + e.getMessage());
                    Log.i("FormPAge resetUnitDetails=", "step 8" + e.getMessage());

                }

                if (previousUnitInfoPointDataModel.getTenement_number().equals("")) {
                    binding.layoutNewUnitDetails.autoCompDocTenement.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                    binding.layoutNewUnitDetails.autoCompDocTenement.setText("");
                    binding.layoutNewUnitDetails.autoCompDocTenement.setTag("");
                    binding.layoutNewUnitDetails.autoCompDocTenement.requestFocus();
                    binding.layoutNewUnitDetails.autoCompDocTenement.dismissDropDown();

                    binding.layoutNewUnitDetails.autoCompDocTenementComm.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setText("");
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setTag("");
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.requestFocus();
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.dismissDropDown();
                }
                binding.layoutNewUnitDetails.etCount.setText("" + previousUnitInfoPointDataModel.getVisit_count());
                binding.layoutNewUnitDetails.etAreaName.setText(previousUnitInfoPointDataModel.getArea_name());
                binding.layoutNewUnitDetails.etWardNo.setText(previousUnitInfoPointDataModel.getWard_no());
                binding.layoutNewUnitDetails.etSectorNo.setText(previousUnitInfoPointDataModel.getSector_no());
                binding.layoutNewUnitDetails.etZoneNo.setText(previousUnitInfoPointDataModel.getZone_no());
                binding.layoutNewUnitDetails.autoCompNagar.setText(previousUnitInfoPointDataModel.getNagar_name());
                binding.layoutNewUnitDetails.autoCompNagar.setFocusable(false);
                binding.layoutNewUnitDetails.autoCompNagar.setEnabled(false);
                binding.layoutNewUnitDetails.autoCompNagar.setBackgroundResource(R.drawable.rounded_blue_edittext);
                if (previousUnitInfoPointDataModel.getNagar_name().equals(Constants.dropdown_other)) {
                    binding.layoutNewUnitDetails.etNameNagarOther.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.etNameNagarOther.setText(previousUnitInfoPointDataModel.getNagar_name_other());
                }
                binding.layoutNewUnitDetails.etSocietyName.setText(previousUnitInfoPointDataModel.getSociety_name());
                binding.layoutNewUnitDetails.etStreetRoadName.setText(previousUnitInfoPointDataModel.getStreet_name());
                binding.layoutNewUnitDetails.etLandmark.setText(previousUnitInfoPointDataModel.getLandmark_name());
//                binding.layoutNewUnitDetails.autoCompRemarks.setText(previousUnitInfoPointDataModel.getRemarks());
                if (previousUnitInfoPointDataModel.getPincode() != null) {
                    binding.layoutNewUnitDetails.etPincode.setTag(previousUnitInfoPointDataModel.getPincode());
                    binding.layoutNewUnitDetails.etPincode.setText(Utils.getTextByTag(Constants.domain_pincodes,previousUnitInfoPointDataModel.getPincode()));

                }

                binding.layoutNewUnitDetails.radioGroupMemberAvailable.clearCheck();
                isResident = "No";
                resYes = false;
                try {
                    if(!previousUnitInfoPointDataModel.getUnit_status().equalsIgnoreCase(Constants.NotApplicable_statusLayer)){
                        binding.layoutNewUnitDetails.remarksLayout.setVisibility(View.VISIBLE);
                    }
                }catch (Exception ex){
                    binding.layoutNewUnitDetails.remarksLayout.setVisibility(View.VISIBLE);
                    ex.getMessage();
                }

                binding.layoutNewUnitDetails.radioMemberAvailableNo.setChecked(true);
                isMemberAvailable = binding.layoutNewUnitDetails.radioMemberAvailableNo.getText().toString();

                binding.layoutNewUnitDetails.etCount.setFocusable(false);
                binding.layoutNewUnitDetails.etCount.setEnabled(false);
                binding.layoutNewUnitDetails.etCount.setBackgroundResource(R.drawable.rounded_blue_edittext);

                binding.layoutNewUnitDetails.etAreaName.setFocusable(true);
                binding.layoutNewUnitDetails.etAreaName.setEnabled(true);
                binding.layoutNewUnitDetails.etAreaName.setBackgroundResource(R.drawable.rounded_white_edittext);

                binding.layoutNewUnitDetails.etWardNo.setFocusable(false);
                binding.layoutNewUnitDetails.etWardNo.setEnabled(false);
                binding.layoutNewUnitDetails.etWardNo.setBackgroundResource(R.drawable.rounded_blue_edittext);

                binding.layoutNewUnitDetails.etSectorNo.setFocusable(false);
                binding.layoutNewUnitDetails.etSectorNo.setEnabled(false);
                binding.layoutNewUnitDetails.etSectorNo.setBackgroundResource(R.drawable.rounded_blue_edittext);


                binding.layoutNewUnitDetails.etZoneNo.setFocusable(false);
                binding.layoutNewUnitDetails.etZoneNo.setEnabled(false);
                binding.layoutNewUnitDetails.etZoneNo.setBackgroundResource(R.drawable.rounded_blue_edittext);

            } else {
                if (previousUnitInfoPointDataModel.getTenement_number().equals("")) {
                    binding.layoutNewUnitDetails.autoCompDocTenement.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                }

                if (Utils.isNullOrEmpty(previousUnitInfoPointDataModel.getUnit_id())) {
                    String idd =App.getSharedPreferencesHandler().getString("unit_id");
                    unitUniqueId = idd;
//                    unitUniqueId = "U_" + Utils.getEpochDateStamp();
                } else unitUniqueId = previousUnitInfoPointDataModel.getUnit_id();

                unit_unique_id = unitUniqueId;


                previousObjectIdUnit = previousUnitInfoPointDataModel.getObejctId();
                previousGlobalIdUnit = previousUnitInfoPointDataModel.getGlobalId();
                //isUnitUploaded = previousUnitInfoPointDataModel.isUploaded();
                if (previousUnitInfoPointDataModel.getObejctId() != null && !previousUnitInfoPointDataModel.getObejctId().equals(""))
                    isUnitUploaded = true;
                else
                    isUnitUploaded = false;

                unit_relative_path = "/" + structUniqueId + "/" + unitUniqueId + "/";
                unit_unique_id = previousUnitInfoPointDataModel.getUnit_id();
                unit_rel_global_id = previousUnitInfoPointDataModel.getRel_globalid();
                hoh_relative_path = unit_relative_path + hohUniqueId + "/";
                AppLog.e("UniqueId: " + unitUniqueId);
                AppLog.e("UniqueId==: " + previousUnitInfoPointDataModel.getHut_number());
                AppLog.e("Relative_path: " + unit_relative_path);

                if (previousUnitInfoPointDataModel.getUnit_unique_id() != null) {
                    binding.layoutNewUnitDetails.etUniqueNo.setText(previousUnitInfoPointDataModel.getUnit_unique_id());
                    binding.layoutNewUnitDetails.etUniqueNo.setAdapter(null);
                    disableUnitIdSelection();
//                    String s = "";
//                    for (int i = 0; i < previousUnitInfoPointDataModel.getUnit_unique_id().length(); i++) {
//                        s = s + "*";
//                    }
//                    binding.layoutNewUnitDetails.etUniqueNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(previousUnitInfoPointDataModel.getUnit_unique_id().length())});
                    //binding.layoutNewUnitDetails.etConfUnitNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(previousUnitInfoPointDataModel.getUnit_unique_id().length())});
//                    binding.layoutNewUnitDetails.etUniqueNo.setText("" + s);
                   // binding.layoutNewUnitDetails.etConfUnitNumber.setText(previousUnitInfoPointDataModel.getUnit_unique_id());
//                    binding.layoutNewUnitDetails.etUniqueNo.setFocusable(false);
                    //binding.layoutNewUnitDetails.etConfUnitNumber.setFocusable(false);
                } else {
//                    binding.layoutNewUnitDetails.etUniqueNo.setText("");
                    //binding.layoutNewUnitDetails.etConfUnitNumber.setText("");
                }
                if ((previousUnitInfoPointDataModel.getRespondent_age() != "" && previousUnitInfoPointDataModel.getRespondent_age() != null) ) {
                    if (Integer.parseInt(previousUnitInfoPointDataModel.getRespondent_age()) < 18){
                    ageFlag = Integer.parseInt(previousUnitInfoPointDataModel.getRespondent_age());
                    binding.layoutNewUnitDetails.ageAboveLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.ageBelowLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.etHOHname.setText(previousUnitInfoPointDataModel.getRespondent_hoh_name());
                    binding.layoutNewUnitDetails.etContactHoh.setText(previousUnitInfoPointDataModel.getRespondent_hoh_contact());
                    binding.layoutNewUnitDetails.etContactNumber.setText(previousUnitInfoPointDataModel.getRespondent_hoh_contact());
                    binding.layoutNewUnitDetails.autoCompRespondentRelation.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                    binding.layoutNewUnitDetails.autoCompRespondentRelation.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                    if(previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRespondentRelation.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship_other());
                        binding.layoutNewUnitDetails.etRespondentRelation.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRespondentRelation.setText("");
                        binding.layoutNewUnitDetails.etRespondentRelation.setVisibility(View.GONE);
                    }

                    binding.layoutNewUnitDetails.autoCompRelationEmp.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                    binding.layoutNewUnitDetails.autoCompRelationEmp.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                    binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                    binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                    if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship_other());
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setText("");
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.GONE);
                    }

                    binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                    binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setText(Utils.getTextByTag(Constants.domain_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                    if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship_other());
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText("");
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.GONE);
                    }

                } else {
                    ageFlag = Integer.parseInt(previousUnitInfoPointDataModel.getRespondent_age());
                    binding.layoutNewUnitDetails.autoCompRespondentRelation.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                    binding.layoutNewUnitDetails.autoCompRespondentRelation.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                    if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRespondentRelation.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship_other());
                        binding.layoutNewUnitDetails.etRespondentRelation.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRespondentRelation.setText("");
                        binding.layoutNewUnitDetails.etRespondentRelation.setVisibility(View.GONE);
                    }

                    binding.layoutNewUnitDetails.autoCompRelationEmp.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                    binding.layoutNewUnitDetails.autoCompRelationEmp.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                    binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                    binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                    if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship_other());
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setText("");
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.GONE);
                    }

                    binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                    binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setText(Utils.getTextByTag(Constants.domain_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                    if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship_other());
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText("");
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.GONE);
                    }
                }}

                previousStructureInfoPointDataModel = (StructureInfoPointDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);
                previousUnitInfoPointDataModel = (UnitInfoDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_UnitInfo);

                Log.e("JJJJJJJJJJJJJ", " last Date=" + previousUnitInfoPointDataModel.getLast_edited_date());
                Log.e("JJJJJJJJJJJJJ", " last Date=" + previousUnitInfoPointDataModel.getDate());
                Log.e("JJJJJJJJJJJJJ", "  Date=" + previousUnitInfoPointDataModel.getSurvey_date());
                Log.e("JJJJJJJJJJJJJ", "  Time=" + previousUnitInfoPointDataModel.getSurvey_time());

                if (previousUnitInfoPointDataModel.getTenement_number().equals("")) {
                    binding.layoutNewUnitDetails.autoCompDocTenement.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                    binding.layoutNewUnitDetails.autoCompDocTenement.setText("");
                    binding.layoutNewUnitDetails.autoCompDocTenement.setTag("");
                    binding.layoutNewUnitDetails.autoCompDocTenement.requestFocus();
                    binding.layoutNewUnitDetails.autoCompDocTenement.dismissDropDown();

                    binding.layoutNewUnitDetails.autoCompDocTenementComm.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setText("");
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setTag("");
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.requestFocus();
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.dismissDropDown();
                }
                binding.layoutNewUnitDetails.etCount.setText("" + previousUnitInfoPointDataModel.getVisit_count());
                binding.layoutNewUnitDetails.etAreaName.setText(previousUnitInfoPointDataModel.getArea_name());
                binding.layoutNewUnitDetails.etWardNo.setText(previousUnitInfoPointDataModel.getWard_no());
                binding.layoutNewUnitDetails.etSectorNo.setText(previousUnitInfoPointDataModel.getSector_no());
                binding.layoutNewUnitDetails.etZoneNo.setText(previousUnitInfoPointDataModel.getZone_no());
                binding.layoutNewUnitDetails.autoCompNagar.setText(previousUnitInfoPointDataModel.getNagar_name());
                binding.layoutNewUnitDetails.autoCompNagar.setFocusable(false);
                binding.layoutNewUnitDetails.autoCompNagar.setEnabled(false);
                binding.layoutNewUnitDetails.autoCompNagar.setBackgroundResource(R.drawable.rounded_blue_edittext);
                if (previousUnitInfoPointDataModel.getNagar_name().equals(Constants.dropdown_other)) {
                    binding.layoutNewUnitDetails.etNameNagarOther.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.etNameNagarOther.setText(previousUnitInfoPointDataModel.getNagar_name_other());
                }
                binding.layoutNewUnitDetails.etSocietyName.setText(previousUnitInfoPointDataModel.getSociety_name());
                binding.layoutNewUnitDetails.etStreetRoadName.setText(previousUnitInfoPointDataModel.getStreet_name());
                binding.layoutNewUnitDetails.etLandmark.setText(previousUnitInfoPointDataModel.getLandmark_name());
                binding.layoutNewUnitDetails.autoCompRemarks.setTag(previousUnitInfoPointDataModel.getRespondent_non_available_remark());
                binding.layoutNewUnitDetails.autoCompRemarks.setText(Utils.getTextByTag(Constants.domain_respondent_non_available,previousUnitInfoPointDataModel.getRespondent_non_available_remark()));
                try{
                    if(previousUnitInfoPointDataModel.getRespondent_non_available_remark().equalsIgnoreCase("Unit is locked and Notice pasted"))
                    {
                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Notice_Pasted", unitUniqueId, false);
                        if(ll!=null && ll.size()>=0){
                            binding.layoutNewUnitDetails.etAttacheNotice.setText(ll.size() + " " + "out of 1 attached");
                        }
                        binding.layoutNewUnitDetails.noticeLayout.setVisibility(View.VISIBLE);
                    }else{
                        binding.layoutNewUnitDetails.noticeLayout.setVisibility(View.GONE);
                    }
                }catch (Exception ex){
                    AppLog.logData(activity,ex.getMessage());
                    ex.getMessage();
                }
                binding.layoutNewUnitDetails.etRespondentName.setText(previousUnitInfoPointDataModel.getRespondent_name());
                binding.layoutNewUnitDetails.etRespondentContact.setText(previousUnitInfoPointDataModel.getRespondent_hoh_contact());
                binding.layoutNewUnitDetails.etDobRespondent.setText(previousUnitInfoPointDataModel.getRespondent_dob());
                setAgeValidation();
                binding.layoutNewUnitDetails.etContactHoh.setText(previousUnitInfoPointDataModel.getRespondent_hoh_contact());
                binding.layoutNewUnitDetails.autoCompRespondentRelation.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                binding.layoutNewUnitDetails.autoCompRespondentRelation.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));
                // hohRelation = previousUnitInfoPointDataModel.getRespondent_hoh_relationship();

                if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewUnitDetails.etRespondentRelation.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship_other());
                    binding.layoutNewUnitDetails.etRespondentRelation.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.etRespondentRelation.setText("");
                    binding.layoutNewUnitDetails.etRespondentRelation.setVisibility(View.GONE);
                }

                binding.layoutNewUnitDetails.autoCompRelationEmp.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                binding.layoutNewUnitDetails.autoCompRelationEmp.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setTag(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setText(Utils.getTextByTag(Constants.domain_respondent_relationship_with_hoh,previousUnitInfoPointDataModel.getRespondent_hoh_relationship()));

                if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewUnitDetails.etRespondentRelationHoh.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship_other());
                    binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.etRespondentRelationHoh.setText("");
                    binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.GONE);
                }

                binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship());
                if (previousUnitInfoPointDataModel.getRespondent_hoh_relationship().equalsIgnoreCase(Constants.dropdown_others)) {
                    binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText(previousUnitInfoPointDataModel.getRespondent_hoh_relationship_other());
                    binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText("");
                    binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.GONE);
                }


                binding.layoutNewUnitDetails.etTenementNo.setText(previousUnitInfoPointDataModel.getTenement_number());
                binding.layoutNewUnitDetails.etTenementNoComm.setText(previousUnitInfoPointDataModel.getTenement_number());
                binding.layoutNewUnitDetails.autoCompDocTenement.setTag(previousUnitInfoPointDataModel.getTenement_document());
                binding.layoutNewUnitDetails.autoCompDocTenement.setText(Utils.getTextByTag(Constants.domain_tenement_doc_type,previousUnitInfoPointDataModel.getTenement_document()));

                binding.layoutNewUnitDetails.autoCompDocTenementComm.setTag(previousUnitInfoPointDataModel.getTenement_document());
                binding.layoutNewUnitDetails.autoCompDocTenementComm.setText(Utils.getTextByTag(Constants.domain_tenement_doc_type,previousUnitInfoPointDataModel.getTenement_document()));

                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_doc_type)));
                    setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDocTenementComm);
                    binding.layoutNewUnitDetails.autoCompDocTenement.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_doc_type)));
                    setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDocTenement);



                    try {
                        binding.layoutNewUnitDetails.etTenementRelAmenities.setText(previousUnitInfoPointDataModel.getTenement_number_rel_amenities());
                        binding.layoutNewUnitDetails.autoCompTenementRelAmenities.setTag(previousUnitInfoPointDataModel.getTenement_doc_used());
                        binding.layoutNewUnitDetails.autoCompTenementRelAmenities.setText(Utils.getTextByTag(Constants.domain_tenement_doc_type,previousUnitInfoPointDataModel.getTenement_doc_used()));
                        binding.layoutNewUnitDetails.autoCompTenementRelAmenities.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_doc_type)));
                        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompTenementRelAmenities);
                        binding.layoutNewUnitDetails.etMashalRelAmenities.setText(previousUnitInfoPointDataModel.getMashal_rel_amenities());
                        binding.layoutNewUnitDetails.etPavtiRelAmenities.setText(previousUnitInfoPointDataModel.getSurvey_pavti_no_rel_amenities());
                        disableViews(binding.layoutNewUnitDetails.etPavtiRelAmenities);
                    }catch (Exception ex){
                        ex.getCause();
                    }

                binding.layoutNewUnitDetails.etMashal.setText(previousUnitInfoPointDataModel.getMashal_survey_number());
                binding.layoutNewUnitDetails.etMashalComm.setText(previousUnitInfoPointDataModel.getMashal_survey_number());
//            binding.layoutNewUnitDetails.etUnitArea.setText(String.format("%.3f", previousUnitInfoPointDataModel.getUnit_area_sqft()));
//            binding.layoutNewUnitDetails.etUnitAreaComm.setText(String.format("%.3f", previousUnitInfoPointDataModel.getUnit_area_sqft()));
//            binding.layoutNewUnitDetails.etLoftArea.setText(String.format("%.3f", previousUnitInfoPointDataModel.getLoft_area_sqft()));
//            binding.layoutNewUnitDetails.etLoftAreaComm.setText(String.format("%.3f", previousUnitInfoPointDataModel.getLoft_area_sqft()));
                binding.layoutNewUnitDetails.etUnitArea.setText(String.format("" + previousUnitInfoPointDataModel.getUnit_area_sqft()));
                if (binding.layoutNewUnitDetails.etUnitArea.getText().toString().equals("0.0")) {
                    binding.layoutNewUnitDetails.etUnitArea.setText("");
                }
                binding.layoutNewUnitDetails.etUnitAreaComm.setText("" + previousUnitInfoPointDataModel.getUnit_area_sqft());
                if (previousUnitInfoPointDataModel.getUnit_usage().toString().equalsIgnoreCase("RC") || previousUnitInfoPointDataModel.getUnit_usage().toString().equalsIgnoreCase("Residential + Commercial")) {
                    binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.etRcResAreaLayout.setText("" + previousUnitInfoPointDataModel.getResidential_area_sqft());
                    binding.layoutNewUnitDetails.etRcCommAreaLayout.setText("" + previousUnitInfoPointDataModel.getCommercial_area_sqft());
                    binding.layoutNewUnitDetails.etUnitAreaComm.setText("" + Double.sum(previousUnitInfoPointDataModel.getResidential_area_sqft(),previousUnitInfoPointDataModel.getCommercial_area_sqft()));

//                    binding.layoutNewUnitDetails.etUnitAreaComm.setFocusable(false);
                    binding.layoutNewUnitDetails.etUnitAreaComm.setEnabled(false);
                    binding.layoutNewUnitDetails.etUnitAreaComm.setBackgroundResource(R.drawable.rounded_blue_edittext);
//                    binding.layoutNewUnitDetails.etUnitAreaComm.setText("" + previousUnitInfoPointDataModel.getUnit_area_sqft());
                }else{
                    binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.GONE);
                }
                if (previousUnitInfoPointDataModel.getUnit_usage().toString().equals(Constants.OthersCheckBox)) {
                    //Vidnyan changes here
                    if(Integer.parseInt(previousUnitInfoPointDataModel.getRespondent_age()) < 18){
                        binding.layoutNewUnitDetails.ltOtherUsageBelow.setVisibility(View.VISIBLE);
                        if (previousUnitInfoPointDataModel.getType_of_other_structure() != null)// vidnyan, setting value of Type_of_other_structure
                            binding.layoutNewUnitDetails.etOtherUsageBelow.setText("" + previousUnitInfoPointDataModel.getType_of_other_structure());
                        else
                            binding.layoutNewUnitDetails.etOtherUsageBelow.setText("");
                    }else{
                        binding.layoutNewUnitDetails.ltOtherUsage.setVisibility(View.VISIBLE);
                        if (previousUnitInfoPointDataModel.getType_of_other_structure() != null)// vidnyan, setting value of Type_of_other_structure
                            binding.layoutNewUnitDetails.etOtherUsage.setText("" + previousUnitInfoPointDataModel.getType_of_other_structure());
                        else
                            binding.layoutNewUnitDetails.etOtherUsage.setText("");
                    }



                } /*else {
                    binding.layoutNewUnitDetails.ltOtherUsage.setVisibility(View.GONE);
                }*/
                binding.layoutNewUnitDetails.etLoftArea.setText(String.format("" + previousUnitInfoPointDataModel.getLoft_area_sqft()));
                binding.layoutNewUnitDetails.etLoftAreaComm.setText(String.format("" + previousUnitInfoPointDataModel.getLoft_area_sqft()));

                binding.layoutNewUnitDetails.etYearOfStructure.setText(previousUnitInfoPointDataModel.getStructure_year());
                binding.layoutNewUnitDetails.etYearOfStructure.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                binding.layoutNewUnitDetails.etYearOfStructure.setCompoundDrawables(null, null, null, null);
                dateCheck=previousUnitInfoPointDataModel.getStructure_year();
                binding.layoutNewUnitDetails.etYearOfStructureComm.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                binding.layoutNewUnitDetails.etYearOfStructureComm.setCompoundDrawables(null, null, null, null);
                binding.layoutNewUnitDetails.etOwnerContact.setText(previousUnitInfoPointDataModel.getRespondent_hoh_contact());
                //Commercial - contact no of owner
//                binding.layoutNewUnitDetails.etUnitGomasta.setText(String.format("%.3f", previousUnitInfoPointDataModel.getGhumasta_area_sqft()));
                binding.layoutNewUnitDetails.etUnitGomasta.setText("" + previousUnitInfoPointDataModel.getGhumasta_area_sqft());
                if (binding.layoutNewUnitDetails.etUnitGomasta.getText().toString().equalsIgnoreCase("0.0")) {
                    binding.layoutNewUnitDetails.etUnitGomasta.setText("");
                }

                binding.layoutNewUnitDetails.etNoOfEmployee.setText(previousUnitInfoPointDataModel.getEmployees_count());
                if (previousUnitInfoPointDataModel.getPincode() != null) {
                    binding.layoutNewUnitDetails.etPincode.setTag(previousUnitInfoPointDataModel.getPincode());
                    binding.layoutNewUnitDetails.etPincode.setText(Utils.getTextByTag(Constants.domain_pincodes,previousUnitInfoPointDataModel.getPincode()));

                }

                binding.layoutNewUnitDetails.radioGroupMemberAvailable.clearCheck();

                if (!previousUnitInfoPointDataModel.isMember_available() && (previousUnitInfoPointDataModel.getRespondent_dob() == null || previousUnitInfoPointDataModel.getRespondent_dob().equals(""))) {
                    isResident = "No";
                    resYes = false;
                } else {
                    if (!previousUnitInfoPointDataModel.isMember_available()) {
                        isResident = "No";
                        resYes = false;

                        try {
                            if(!previousUnitInfoPointDataModel.getUnit_status().equalsIgnoreCase(Constants.NotApplicable_statusLayer)){
                                binding.layoutNewUnitDetails.remarksLayout.setVisibility(View.VISIBLE);
                            }
                        }catch (Exception ex){
                            binding.layoutNewUnitDetails.remarksLayout.setVisibility(View.VISIBLE);
                            ex.getMessage();
                        }
                        isMemberAvailable = binding.layoutNewUnitDetails.radioMemberAvailableNo.getText().toString();
                        binding.layoutNewUnitDetails.radioMemberAvailableNo.setChecked(true);
                        binding.layoutNewUnitDetails.residentYesLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.detailsLayout.setVisibility(View.GONE);
                    } else {
                        isResident = "Yes";
                        resYes = true;
                    }

                    if (resYes) {
                        binding.layoutNewUnitDetails.residentYesLayout.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.residentYesLayout.setVisibility(View.GONE);
                    }

                    if(previousUnitInfoPointDataModel.getSurvey_pavti_no()!=null && !previousUnitInfoPointDataModel.getSurvey_pavti_no().equals("")){
                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.setText(previousUnitInfoPointDataModel.getSurvey_pavti_no());
                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.setFocusable(false);
                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.setEnabled(false);
                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.setBackgroundResource(R.drawable.rounded_blue_edittext);
                    }else{
                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.setText("");
                    }
                    if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                        if(previousUnitInfoPointDataModel.getSurvey_pavti_no()!=null && !previousUnitInfoPointDataModel.getSurvey_pavti_no().equals("")){
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.setText(previousUnitInfoPointDataModel.getSurvey_pavti_no());
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.setFocusable(false);
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.setEnabled(false);
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.setBackgroundResource(R.drawable.rounded_blue_edittext);
                        }else{
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.setText("");
                        }
                    }else{
                        if(previousUnitInfoPointDataModel.getSurvey_pavti_no()!=null && !previousUnitInfoPointDataModel.getSurvey_pavti_no().equals("")){
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.setText(previousUnitInfoPointDataModel.getSurvey_pavti_no());
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.setFocusable(false);
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.setEnabled(false);
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.setBackgroundResource(R.drawable.rounded_blue_edittext);
                        }else{
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.setText("");
                        }
                    }

                    binding.layoutNewUnitDetails.radioUnitUsageReligious.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageAmenities.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageResidential.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageCommercial.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageRC.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageOthers.setClickable(false);
                    binding.layoutNewUnitDetails.etExistenceSince.setFocusable(true);
//                    if(previousUnitInfoPointDataModel.getExistence_since_year()!=null || !previousUnitInfoPointDataModel.getExistence_since_year().equalsIgnoreCase("")){
//                        binding.layoutNewUnitDetails.etExistenceSince.setText(previousUnitInfoPointDataModel.getExistence_since_year());
//                    }else{

                    if (previousUnitInfoPointDataModel.getUnit_usage().toString().equals(Constants.RcCheckBox)){
                        List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false,Constants.UnitDistometerPdfTypeRes);
                        List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false,Constants.UnitDistometerPdfTypeComm);

                            if((ll1!=null && ll1.size()>0) && (ll2!=null && ll2.size()>0)){
                                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("2 out of 2 attached");
                            }else if((ll1!=null && ll1.size()>0)){
                                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                            }else if((ll2!=null && ll2.size()>0)){
                                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                            }else{
                                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                            }
                        if(ll1!=null && ll1.size()>0){
                            List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(Constants.UnitDistometerPdfTypeRes, unit_unique_id, false);
                            if(getMediaInfoData!=null && getMediaInfoData.size()>0){
//                                pdfPathRes=getMediaInfoData.get(0).getItem_url();
                            }
                        }
                        if(ll2!=null && ll2.size()>0){
                            List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(Constants.UnitDistometerPdfTypeComm, unit_unique_id, false);
                            if(getMediaInfoData!=null && getMediaInfoData.size()>0){
//                                pdfPathComm=getMediaInfoData.get(0).getItem_url();
                            }
                        }
                    }else{
                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(Constants.UnitDistometerPdfType, unitUniqueId, false);
                        if(ll!=null && ll.size()>0){
                            binding.layoutNewUnitDetails.etPdfDistometer.setText(ll.size() + " " + "out of 1 attached");
                            binding.layoutNewUnitDetails.etPdfDistometerComm.setText(ll.size() + " " + "out of 1 attached");
                            List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(Constants.UnitDistometerPdfType, unit_unique_id, false);
                            if(getMediaInfoData!=null && getMediaInfoData.size()>0){
//                                globalUnitPdfPath=getMediaInfoData.get(0).getItem_url();
                            }
                        }else{
                            binding.layoutNewUnitDetails.etPdfDistometer.setText("0 out of 1 attached");
                            binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
                        }
                    }

                        if(previousUnitInfoPointDataModel.getExistence_since_year()!=null || !previousUnitInfoPointDataModel.getExistence_since_year().equalsIgnoreCase("")){
                            binding.layoutNewUnitDetails.etExistenceSince.setText(""+previousUnitInfoPointDataModel.getExistence_since_year());
                        }else{
                            binding.layoutNewUnitDetails.etExistenceSince.setText("");
                        }
                        try{
                            String s=formattedDateToYear(previousUnitInfoPointDataModel.getExistence_since());
                            if(s.equalsIgnoreCase("")){
//                                binding.layoutNewUnitDetails.etExistenceSince.setText(previousUnitInfoPointDataModel.getExistence_since());
                                extSince=previousUnitInfoPointDataModel.getExistence_since();
                            }else{
//                                binding.layoutNewUnitDetails.etExistenceSince.setText(s);
                                extSince=s;
                            }

                        }catch (Exception ex){
                            AppLog.logData(activity,ex.getMessage());
                            ex.getCause();
                        }
//                    }

//                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_doc_type)));
//                    setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDocTenementComm);
//                    binding.layoutNewUnitDetails.autoCompDocTenement.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_tenement_doc_type)));
//                    setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDocTenement);

                    binding.layoutNewUnitDetails.etExistenceSince.setFocusable(true);
                    binding.layoutNewUnitDetails.etExistenceSince.setEnabled(true);
                    if (binding.layoutNewUnitDetails.etExistenceSince.getText().toString().equalsIgnoreCase("0")) {
                        binding.layoutNewUnitDetails.etExistenceSince.setText("");
                        binding.layoutNewUnitDetails.etYearOfStructure.setText("");
                        binding.layoutNewUnitDetails.etYearOfStructure.setTag("");
                    }
                    //binding.layoutNewUnitDetails.etExistenceSince.setBackgroundResource(R.drawable.rounded_blue_edittext);

                    if(previousUnitInfoPointDataModel.getExistence_since_year()!=null || !previousUnitInfoPointDataModel.getExistence_since_year().equalsIgnoreCase("")){
                        binding.layoutNewUnitDetails.etExistenceSinceComm.setText(previousUnitInfoPointDataModel.getExistence_since_year());
                    }else{
                        binding.layoutNewUnitDetails.etExistenceSinceComm.setText("");
                    }
                    try{
                        String s=formattedDateToYear(previousUnitInfoPointDataModel.getExistence_since());
                        if(s.equalsIgnoreCase("")){
//                            binding.layoutNewUnitDetails.etExistenceSinceComm.setText(previousUnitInfoPointDataModel.getExistence_since());
                            extSince=previousUnitInfoPointDataModel.getExistence_since();
                        }else{
//                            binding.layoutNewUnitDetails.etExistenceSinceComm.setText(s);
                            extSince=s;
                        }

                    }catch (Exception ex){
                        AppLog.logData(activity,ex.getMessage());
                        ex.getCause();
                    }


                    binding.layoutNewUnitDetails.etExistenceSinceComm.setFocusable(true);
                    binding.layoutNewUnitDetails.etExistenceSinceComm.setEnabled(true);
                    binding.layoutNewUnitDetails.etExistenceSinceComm.setBackgroundResource(R.drawable.rounded_blue_edittext);
                    if (binding.layoutNewUnitDetails.etExistenceSinceComm.getText().toString().equalsIgnoreCase("0")) {
                        binding.layoutNewUnitDetails.etExistenceSinceComm.setText("");
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setText("");
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setTag("");
                    }
                    if (previousUnitInfoPointDataModel.getRespondent_age()!="" && Integer.parseInt(previousUnitInfoPointDataModel.getRespondent_age()) >= 18) {
                        binding.layoutNewUnitDetails.etExistenceSince.setFocusable(false);
                        binding.layoutNewUnitDetails.etExistenceSince.setEnabled(false);
                        binding.layoutNewUnitDetails.etExistenceSince.setBackgroundResource(R.drawable.rounded_blue_edittext);

                        binding.layoutNewUnitDetails.etExistenceSinceComm.setFocusable(false);
                        binding.layoutNewUnitDetails.etExistenceSinceComm.setEnabled(false);
                        binding.layoutNewUnitDetails.etExistenceSinceComm.setBackgroundResource(R.drawable.rounded_blue_edittext);
                    }
                    if (previousUnitInfoPointDataModel.getRespondent_age()!="" && Integer.parseInt(previousUnitInfoPointDataModel.getRespondent_age()) < 18) {
                        binding.layoutNewUnitDetails.etYearOfStructure.setFocusable(true);
                        binding.layoutNewUnitDetails.etYearOfStructure.setEnabled(true);
                        binding.layoutNewUnitDetails.etYearOfStructure.setBackgroundResource(R.drawable.rounded_edittext);
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setFocusable(true);
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setEnabled(true);
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setBackgroundResource(R.drawable.rounded_edittext);

                    }
                    if (previousUnitInfoPointDataModel.isMember_available()) {
                        binding.layoutNewUnitDetails.radioMemberAvailableYes.setChecked(true);
                        isMemberAvailable = binding.layoutNewUnitDetails.radioMemberAvailableYes.getText().toString();
                    } else {
                        binding.layoutNewUnitDetails.radioMemberAvailableNo.setChecked(true);
                        isMemberAvailable = binding.layoutNewUnitDetails.radioMemberAvailableNo.getText().toString();
                    }


                    binding.layoutNewUnitDetails.radioGroupUnitStatusRentOwner.clearCheck();
                    if (previousUnitInfoPointDataModel.isMember_available()) {
                        binding.layoutNewUnitDetails.detailsLayout.setVisibility(View.VISIBLE);
                    }

                    String ownership = previousUnitInfoPointDataModel.getOwnership_status();
                    if (previousUnitInfoPointDataModel.getOwnership_status() != null && previousUnitInfoPointDataModel.getOwnership_status().equals(binding.layoutNewUnitDetails.radioUnitStatusRent.getText().toString())) {
                        binding.layoutNewUnitDetails.radioUnitStatusRent.setChecked(true);
//                    binding.layoutNewUnitDetails.radioUnitStatusOwnership.setClickable(false);
//                    binding.layoutNewUnitDetails.radioUnitStatusRent.setClickable(false);
                        binding.layoutNewUnitDetails.radioUnitStatusRentComm.setChecked(true);
//                    binding.layoutNewUnitDetails.radioUnitStatusOwnershipComm.setClickable(false);
//                    binding.layoutNewUnitDetails.radioUnitStatusRentComm.setClickable(false);

                        if (unitStructureUsage.equals(Constants.ResidentialCheckBox)) {
                            isUnit_RentOwner = binding.layoutNewUnitDetails.radioUnitStatusRent.getText().toString();
                        } else {
                            isUnit_RentOwner = binding.layoutNewUnitDetails.radioUnitStatusRentComm.getText().toString();
                        }

                    } else {// (previousUnitInfoPointDataModel.getOwnership_status().equals(binding.layoutNewUnitDetails.radioUnitStatusOwnership.getText().toString())) {
                        if (unitStructureUsage.equals(Constants.OthersCheckBox) && previousUnitInfoPointDataModel.getOwnership_status() != null && previousUnitInfoPointDataModel.getOwnership_status().toString().equals("")) {

                        } else {
                            if (unitStructureUsage.equals(Constants.ResidentialCheckBox)) {
                                isUnit_RentOwner = binding.layoutNewUnitDetails.radioUnitStatusOwnership.getText().toString();
                            } else {
                                isUnit_RentOwner = binding.layoutNewUnitDetails.radioUnitStatusOwnershipComm.getText().toString();
                            }

                            binding.layoutNewUnitDetails.radioUnitStatusOwnership.setChecked(true);
                            binding.layoutNewUnitDetails.radioUnitStatusOwnershipComm.setChecked(true);
                        }

                    }
                    if (previousUnitInfoPointDataModel.getLoft_present() != null && previousUnitInfoPointDataModel.getLoft_present()) {
                        binding.layoutNewUnitDetails.radioLoftYes.setChecked(true);
                        binding.layoutNewUnitDetails.radioLoftYesComm.setChecked(true);
                        is_loft = true;
                        radioLoft = "true";

                        binding.layoutNewUnitDetails.etLoftArea.setFocusable(true);
                        binding.layoutNewUnitDetails.etLoftArea.setFocusableInTouchMode(true);
                        binding.layoutNewUnitDetails.etLoftArea.setEnabled(true);
                        binding.layoutNewUnitDetails.etLoftArea.setClickable(true);

                        binding.layoutNewUnitDetails.etLoftAreaComm.setFocusable(true);
                        binding.layoutNewUnitDetails.etLoftAreaComm.setFocusableInTouchMode(true);
                        binding.layoutNewUnitDetails.etLoftAreaComm.setEnabled(true);
                        binding.layoutNewUnitDetails.etLoftAreaComm.setClickable(true);
                    }
                    if (previousUnitInfoPointDataModel.getLoft_present() != null && !previousUnitInfoPointDataModel.getLoft_present()) {
                        binding.layoutNewUnitDetails.radioLoftNo.setChecked(true);
                        binding.layoutNewUnitDetails.radioLoftNoComm.setChecked(true);
                        is_loft = false;
                        radioLoft = "false";

                        binding.layoutNewUnitDetails.etLoftArea.setFocusable(false);
                        binding.layoutNewUnitDetails.etLoftArea.setEnabled(false);
                        binding.layoutNewUnitDetails.etLoftArea.setText("");


                        binding.layoutNewUnitDetails.etLoftAreaComm.setFocusable(false);
                        binding.layoutNewUnitDetails.etLoftAreaComm.setEnabled(false);
                        binding.layoutNewUnitDetails.etLoftAreaComm.setText("");

                    }

                    binding.layoutNewUnitDetails.etNoOfEmployee.setText(previousUnitInfoPointDataModel.getEmployees_count());//false


//                year = Integer.parseInt(previousUnitInfoPointDataModel.getStructure_year());
                    try {
                        if (previousUnitInfoPointDataModel.getExistence_since()!=null) {
                            if(!previousUnitInfoPointDataModel.getExistence_since().equals("")){
                                if(!previousUnitInfoPointDataModel.getExistence_since_year().equals("0")){
                                    year = Integer.parseInt(previousUnitInfoPointDataModel.getExistence_since_year());
                                }else{
                                    year = 0;
                                }
                            }else{
                                year = 0;
                            }

                        } else {
                            year = 0;
                        }
                    } catch (Exception e) {
                        AppLog.logData(activity,e.getMessage());
                        year=0;
                        e.getCause();
                    }

                    if((binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() || binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked()) && !binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("Rented Tenant")){
                        setOwnerRentDocs();
                    }else if((binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() || binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked()) && !binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("Rented Tenant")){
                        setOwnerRentDocs();
                    }else if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                        binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                        if(floorFlag){
                            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                        }else{
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                        }
                        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                    }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                        binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                        if(floorFlag){
                            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                        }else{
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                        }
                        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                    }else{
                        binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.GONE);
                        if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else{
                        setupYearOfStructure();}
                    }
                    //uncheckAllStructureUsageRadio();
                    unitStructureUsage = previousUnitInfoPointDataModel.getUnit_usage();
                    binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(true);
                    binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);

                    Log.e("JJJJJJJJJJJJJJ", " insertFetch =" + unitStructureUsage);
                    Log.i("FormPAge resetUnitDetails=", "step 10");

                    double area = 0.0, cArea = 0.0;
                    if (unitStructureUsage.equals(Constants.ReligiousCheckBox)) {
                        binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(true);
                        uncheckAllStructureUsageRadio();
                        unitStructureUsage = Constants.ReligiousCheckBox;
                        area = previousUnitInfoPointDataModel.getUnit_area_sqft();
                        resBoolean = false;
                        commBoolean = false;
                        rcBoolean = false;
                        othersBoolean = false;
                        industrialBoolean = false;
                        binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.religiousAmenitiesAboveLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.dietyLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.noPeopleVisitsLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.registeredReligiousLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.firstHideLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.loftLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);

                        binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(true);
                        binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);


                    }else if (unitStructureUsage.equals(Constants.AmenitiesCheckBox)) {
                        uncheckAllStructureUsageRadio();
                        unitStructureUsage = Constants.AmenitiesCheckBox;
                        binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(true);
                        area = previousUnitInfoPointDataModel.getUnit_area_sqft();
                        resBoolean = false;
                        commBoolean = false;
                        rcBoolean = false;
                        othersBoolean = false;
                        industrialBoolean = false;
                        binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.religiousAmenitiesAboveLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.dietyLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.noPeopleVisitsLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.registeredReligiousLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.firstHideLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.loftLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);

                        binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(true);
                        binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);
                    }
                    else if (unitStructureUsage.equals(Constants.ResidentialCheckBox)) {
                        uncheckAllStructureUsageRadio();
                        unitStructureUsage = Constants.ResidentialCheckBox;
                        binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(true);
                        setupResidentProof();
                        if (year > 1999) {
                            setupResidentProofAdditionalChainDocument();
                        }


                        area = previousUnitInfoPointDataModel.getResidential_area();
                        resBoolean = true;
                        commBoolean = false;
                        rcBoolean = false;
                        othersBoolean = false;
                        industrialBoolean = false;
                        binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.religiousAmenitiesAboveLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.firstHideLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.loftLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.VISIBLE);

                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                        if(floorFlag){
                            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 9 attached");
                            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 9 attached");
                        }else{
                            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 7 attached");
                            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 7 attached");
                            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 7 attached");
                        }



                        binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(true);
                        binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);
//
                    } else if (unitStructureUsage.equals(Constants.CommercialCheckBox)) {
                        uncheckAllStructureUsageRadio();
                        unitStructureUsage = Constants.CommercialCheckBox;
                        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(true);
                        setupResidentProof();
                        setupCommercialLicenceProof();
                        area = previousUnitInfoPointDataModel.getUnit_area_sqft();
                        resBoolean = false;
                        commBoolean = true;
                        rcBoolean = false;
                        othersBoolean = false;
                        industrialBoolean = false;
                        binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(true);
                        binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);

                    } else if (unitStructureUsage.equals(Constants.RcCheckBox)) {
                        uncheckAllStructureUsageRadio();
                        unitStructureUsage = Constants.RcCheckBox;
                        binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(true);
                        setupResidentProof();
                        setupCommercialLicenceProof();

                        area = previousUnitInfoPointDataModel.getUnit_area_sqft();
                        cArea = previousUnitInfoPointDataModel.getUnit_area_sqft();
                        resBoolean = false;
                        commBoolean = false;
                        rcBoolean = true;
                        othersBoolean = false;
                        industrialBoolean = false;
                        binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(true);
                        binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.VISIBLE);

                    } else if (unitStructureUsage.equals(Constants.IndustrialCheckBox)) {
                        uncheckAllStructureUsageRadio();
                        unitStructureUsage = Constants.IndustrialCheckBox;
                        binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(true);
                        setupResidentProof();
                        setupCommercialLicenceProof();

                        area = previousUnitInfoPointDataModel.getUnit_area_sqft();
                        cArea = previousUnitInfoPointDataModel.getUnit_area_sqft();
                        resBoolean = false;
                        commBoolean = false;
                        industrialBoolean = true;
                        rcBoolean = false;
                        othersBoolean = false;
                        industrialBoolean = false;
                        binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);

                    } else if (unitStructureUsage.equals(Constants.SpiritualCheckBox)) {
                        uncheckAllStructureUsageRadio();
                        setupReligiousOthersProof();
                        binding.layoutUnitDetailInfo.txtHeaderReligiousOtherProof.setText("Religious (Minimum 1)");
                        unitStructureUsage = Constants.SpiritualCheckBox;
                        binding.layoutUnitDetailInfo.radioUnitUsageReligious.setChecked(true);
                        area = previousUnitInfoPointDataModel.getReligious_area();
                    } else if (unitStructureUsage.equals(Constants.OthersCheckBox)) {
                        uncheckAllStructureUsageRadio();
                        setupReligiousOthersProof();
                        binding.layoutUnitDetailInfo.txtHeaderReligiousOtherProof.setText("Other (Minimum 1)");
                        unitStructureUsage = binding.layoutUnitDetailInfo.radioUnitUsageOthers.getText().toString();
                        binding.layoutUnitDetailInfo.radioUnitUsageOthers.setChecked(true);
                        area = previousUnitInfoPointDataModel.getOther_area();
                        resBoolean = false;
                        commBoolean = false;
                        rcBoolean = false;
                        industrialBoolean = false;
                        othersBoolean = true;
                        binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(true);
                        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);

                    }

                    binding.layoutNewUnitDetails.etRespondentName.setText(previousUnitInfoPointDataModel.getRespondent_name().toString());
                    binding.layoutNewUnitDetails.etRespondentContact.setText(previousUnitInfoPointDataModel.getRespondent_mobile().toString());

                    if (previousUnitInfoPointDataModel.getRespondent_hoh_contact() != null && !previousUnitInfoPointDataModel.getRespondent_hoh_contact().equals("")) {
                        binding.layoutNewUnitDetails.etHOHname.setText(previousUnitInfoPointDataModel.getRespondent_hoh_name().toString());
                        binding.layoutNewUnitDetails.autoHOHna.setText(previousUnitInfoPointDataModel.getRespondent_name().toString());
                        binding.layoutNewUnitDetails.etContactHoh.setText(previousUnitInfoPointDataModel.getRespondent_hoh_contact().toString());
                        binding.layoutNewUnitDetails.etOwnerContact.setText(previousUnitInfoPointDataModel.getRespondent_hoh_contact().toString());
                        binding.layoutNewUnitDetails.etOwnerContactA.setText(previousUnitInfoPointDataModel.getRespondent_hoh_contact().toString());
                    }
                    if (previousUnitInfoPointDataModel.getRespondent_age() != null && !previousUnitInfoPointDataModel.getRespondent_age().equals("")) {
                        binding.layoutNewUnitDetails.ageLayout.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.etShowAge.setText(previousUnitInfoPointDataModel.getRespondent_age());
                        int a = Integer.parseInt(previousUnitInfoPointDataModel.getRespondent_age());
                        ageFlag = a;
                        if (ageFlag >= 18) {
                            binding.layoutNewUnitDetails.usageLayout.setVisibility(View.VISIBLE);
                            if (unitStructureUsage.equals(Constants.ReligiousCheckBox)) {
                                binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.religiousAmenitiesAboveLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.dietyLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.noPeopleVisitsLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.registeredReligiousLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.firstHideLayout.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.loftLayout.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);

                                binding.layoutNewUnitDetails.hohDetails.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);

                                fillReligiousAminitiesDetaild(previousUnitInfoPointDataModel);

                            }else if (unitStructureUsage.equals(Constants.AmenitiesCheckBox)) {
                                binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.religiousAmenitiesAboveLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.dietyLayout.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.noPeopleVisitsLayout.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.registeredReligiousLayout.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.firstHideLayout.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.loftLayout.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);

                                binding.layoutNewUnitDetails.hohDetails.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);

                                fillReligiousAminitiesDetaild(previousUnitInfoPointDataModel);
                            }else if (unitStructureUsage.equals(Constants.ResidentialCheckBox)) {
                                binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.firstHideLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.loftLayout.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.VISIBLE);

                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                if(floorFlag){
                                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 9 attached");
                                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 9 attached");
                                }else{
                                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 7 attached");
                                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 7 attached");
                                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 7 attached");
                                }

                                binding.layoutNewUnitDetails.religiousAmenitiesAboveLayout.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.hohDetails.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                            } else {
                                binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            binding.layoutNewUnitDetails.usageLayout.setVisibility(View.VISIBLE);
                            if (unitStructureUsage.equals(Constants.ResidentialCheckBox)) {
                                binding.layoutNewUnitDetails.hohTitleRes.setText("Name of HOH");
                                binding.layoutNewUnitDetails.hohContactTitle.setText("Contact number of HOH");
                                binding.layoutNewUnitDetails.relHohBelow18TextView.setText("Relationship with HOH");
                            } else {
                                binding.layoutNewUnitDetails.hohTitleRes.setText("Name of Owner/Employer");
                                binding.layoutNewUnitDetails.hohContactTitle.setText("Contact Number of Owner/Employer");
                                binding.layoutNewUnitDetails.relHohBelow18TextView.setText("Relationship with Owner/Employer");
                            }
                        }
                    } else {
//                    binding.layoutNewUnitDetails.etShowAge.setText("44");
                    }

                    if (unitStructureUsage.equals(Constants.OthersCheckBox) && area == 0.0) {
                        binding.layoutNewUnitDetails.etUnitArea.setText("");
                        binding.layoutNewUnitDetails.etUnitAreaComm.setText("");
                    } else {
                        binding.layoutNewUnitDetails.etUnitArea.setText(area + "");
                        binding.layoutNewUnitDetails.etUnitAreaComm.setText(area + "");
                    }
                    if (binding.layoutNewUnitDetails.etUnitArea.getText().toString().equalsIgnoreCase("0.0") || binding.layoutNewUnitDetails.etUnitArea.getText().toString().equalsIgnoreCase("")) {
                        binding.layoutNewUnitDetails.etUnitArea.setText("");
                        //3-2-2024
                        binding.layoutNewUnitDetails.radioUnitStatusOwnership.setChecked(false);
                        binding.layoutNewUnitDetails.radioUnitStatusRent.setChecked(false);
                        binding.layoutNewUnitDetails.radioLoftNo.setChecked(false);
                        binding.layoutNewUnitDetails.radioLoftYes.setChecked(false);
                    }


//                if (cArea != 0.0) {
//                    binding.layoutNewUnitDetails.etUnitArea.setText("Residential " + activity.getResources().getString(R.string.area_sq_ft));
//                    binding.layoutUnitDetailInfo.layCommercialArea.setVisibility(View.VISIBLE);
//                    binding.layoutUnitDetailInfo.etAreaCommercialFt.setText(cArea + "");
//                } else {
//                    binding.layoutUnitDetailInfo.txtAreaSqFt.setText(activity.getResources().getString(R.string.area_sq_ft));
//                }
                }

//            if (getMediaInfoData.size() > 0) {
//                setupImages(getMediaInfoData);
//            }

                //
                binding.layoutNewUnitDetails.etCount.setFocusable(false);
                binding.layoutNewUnitDetails.etCount.setEnabled(false);
                binding.layoutNewUnitDetails.etCount.setBackgroundResource(R.drawable.rounded_blue_edittext);

                binding.layoutNewUnitDetails.etAreaName.setFocusable(true);
                binding.layoutNewUnitDetails.etAreaName.setEnabled(true);
                binding.layoutNewUnitDetails.etAreaName.setBackgroundResource(R.drawable.rounded_white_edittext);

                binding.layoutNewUnitDetails.etWardNo.setFocusable(false);
                binding.layoutNewUnitDetails.etWardNo.setEnabled(false);
                binding.layoutNewUnitDetails.etWardNo.setBackgroundResource(R.drawable.rounded_blue_edittext);

                binding.layoutNewUnitDetails.etSectorNo.setFocusable(false);
                binding.layoutNewUnitDetails.etSectorNo.setEnabled(false);
                binding.layoutNewUnitDetails.etSectorNo.setBackgroundResource(R.drawable.rounded_blue_edittext);


                binding.layoutNewUnitDetails.etZoneNo.setFocusable(false);
                binding.layoutNewUnitDetails.etZoneNo.setEnabled(false);
                binding.layoutNewUnitDetails.etZoneNo.setBackgroundResource(R.drawable.rounded_blue_edittext);
                //bind set hoh data
                Log.i("FormPAge resetUnitDetails=", "step 11");
            }
            if (previousUnitInfoPointDataModel != null) {
                binding.layoutNewUnitDetails.etUniqueNo.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                binding.layoutNewUnitDetails.etUniqueNo.setCompoundDrawables(null, null, null, null);
                disableUnitIdSelection();
                //binding.layoutNewUnitDetails.etConfUnitNumber.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
            }
            surveyDate = previousUnitInfoPointDataModel.getSurvey_date().toString();
            surveyTime = previousUnitInfoPointDataModel.getSurvey_time().toString();
        } else {

            previousStructureInfoPointDataModel = (StructureInfoPointDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);
            structUniqueId = previousStructureInfoPointDataModel.getStructure_id();
            surveyorData = (SurveyorData) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_SurveyorInfo);
            surveyDate = surveyorData.getDate();
            surveyTime = surveyorData.getTime();


            if(previousStructureInfoPointDataModel.getCountry_name()!=null && !previousStructureInfoPointDataModel.getCountry_name().equalsIgnoreCase("")){
                binding.layoutNewUnitDetails.etCountry.setText(previousStructureInfoPointDataModel.getCountry_name());
                binding.layoutNewUnitDetails.etState.setText(previousStructureInfoPointDataModel.getState_name());
                binding.layoutNewUnitDetails.etLandcity.setText(previousStructureInfoPointDataModel.getCity_name());
            }else{
                binding.layoutNewUnitDetails.etCountry.setText("India");
                binding.layoutNewUnitDetails.etState.setText("Maharashtra");
                binding.layoutNewUnitDetails.etLandcity.setText("Mumbai");
            }

            Log.i("FormPAge resetUnitDetails=", "step 15");
//            binding.layoutNewUnitDetails.usageLayout.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.etExistenceSince.setFocusable(true);
            //new record - change here= layoutNewUnitDetails
            isUnitUploaded = false;

            binding.layoutNewUnitDetails.autoCompDocTenement.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);

            binding.layoutNewUnitDetails.etCount.setFocusable(false);
            binding.layoutNewUnitDetails.etCount.setEnabled(false);
            binding.layoutNewUnitDetails.etCount.setBackgroundResource(R.drawable.rounded_blue_edittext);
            binding.layoutNewUnitDetails.etCount.setText("0");

            binding.layoutNewUnitDetails.etAreaName.setFocusable(true);
            binding.layoutNewUnitDetails.etAreaName.setEnabled(true);
            binding.layoutNewUnitDetails.etAreaName.setBackgroundResource(R.drawable.rounded_white_edittext);
            binding.layoutNewUnitDetails.etAreaName.setText("");

            binding.layoutNewUnitDetails.etWardNo.setFocusable(true);
            binding.layoutNewUnitDetails.etWardNo.setEnabled(true);
            binding.layoutNewUnitDetails.etWardNo.setBackgroundResource(R.drawable.rounded_white_edittext);
            binding.layoutNewUnitDetails.etWardNo.setText("");

            binding.layoutNewUnitDetails.etSectorNo.setFocusable(true);
            binding.layoutNewUnitDetails.etSectorNo.setEnabled(true);
            binding.layoutNewUnitDetails.etSectorNo.setBackgroundResource(R.drawable.rounded_white_edittext);
            binding.layoutNewUnitDetails.etSectorNo.setText("");

            binding.layoutNewUnitDetails.etZoneNo.setFocusable(true);
            binding.layoutNewUnitDetails.etZoneNo.setEnabled(true);
            binding.layoutNewUnitDetails.etZoneNo.setBackgroundResource(R.drawable.rounded_white_edittext);
            binding.layoutNewUnitDetails.etZoneNo.setText("");

            if (previousStructureInfoPointDataModel != null) {
                if (previousStructureInfoPointDataModel.getWard_no() != null) {
                    binding.layoutNewUnitDetails.etWardNo.setText(previousStructureInfoPointDataModel.getWard_no());
                    binding.layoutNewUnitDetails.etWardNo.setFocusable(false);
                    binding.layoutNewUnitDetails.etWardNo.setEnabled(false);
                    binding.layoutNewUnitDetails.etWardNo.setBackgroundResource(R.drawable.rounded_blue_edittext);
                }
                if (previousStructureInfoPointDataModel.getSector_no() != null) {
                    binding.layoutNewUnitDetails.etSectorNo.setText(previousStructureInfoPointDataModel.getSector_no());
                    binding.layoutNewUnitDetails.etSectorNo.setFocusable(false);
                    binding.layoutNewUnitDetails.etSectorNo.setEnabled(false);
                    binding.layoutNewUnitDetails.etSectorNo.setBackgroundResource(R.drawable.rounded_blue_edittext);
                }
                if (previousStructureInfoPointDataModel.getZone_no() != null) {
                    binding.layoutNewUnitDetails.etZoneNo.setText(previousStructureInfoPointDataModel.getZone_no());
                    binding.layoutNewUnitDetails.etZoneNo.setFocusable(false);
                    binding.layoutNewUnitDetails.etZoneNo.setEnabled(false);
                    binding.layoutNewUnitDetails.etZoneNo.setBackgroundResource(R.drawable.rounded_blue_edittext);
                }
            }

            unit_relative_path = "/" + structUniqueId + "/" + unitUniqueId + "/";
            hoh_relative_path = unit_relative_path + hohUniqueId + "/";

            AppLog.e("UniqueId: " + unitUniqueId);
            AppLog.e("Relative_path: " + unit_relative_path);

            binding.layoutNewUnitDetails.remarksLayout.setVisibility(View.GONE);
            binding.layoutNewUnitDetails.detailsLayout.setVisibility(View.VISIBLE);
            binding.layoutUnitDetailInfo.autoCompUnitFloorDetails.setText("",false);
            binding.layoutUnitDetailInfo.autoCompUnitFloorDetails.setTag("");


            binding.layoutUnitDetailInfo.etUnitNumber.setText("");
            binding.layoutUnitDetailInfo.radioMemberAvailableYes.setChecked(true);
            isMemberAvailable = binding.layoutUnitDetailInfo.radioMemberAvailableYes.getText().toString();
            binding.layoutUnitDetailInfo.radioMemberAvailableNo.setChecked(false);
            binding.layoutUnitDetailInfo.etRemarks.setText("");
//            binding.layoutUnitDetailInfo.radioUnitStatusRent.setChecked(false);
//            binding.layoutUnitDetailInfo.radioUnitStatusOwnership.setChecked(false);
            isUnit_RentOwner = "";
            binding.layoutUnitDetailInfo.autoCompNoOfMembers.setText("", false);
            uncheckAllStructureUsageRadio();
            binding.layoutUnitDetailInfo.etAreaSqFt.setText("");
            binding.layoutUnitDetailInfo.etExistenceSince.setText("");
            binding.layoutUnitDetailInfo.etYearOfStructure.setText("");
            binding.layoutUnitDetailInfo.etYearOfStructure.setTag("");
//            previousStructureInfoPointDataModel = (StructureInfoPointDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);
            if (previousStructureInfoPointDataModel.getHut_number() != null && previousStructureInfoPointDataModel.getHut_number().toString().contains("DRP")) {
                String[] aty = (previousStructureInfoPointDataModel.getHut_number().toString()).split("/");
                String asd = "";
                for (int i = 0; i < aty.length - 1; i++) {
                    asd = asd + "" + aty[i].toString() + "/";
                }
//                binding.layoutNewUnitDetails.inputEditOne.setPrefixText(asd);
                //binding.layoutNewUnitDetails.inputEditTwo.setPrefixText(asd);
//                binding.layoutNewUnitDetails.etUniqueNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
//                binding.layoutNewUnitDetails.etConfUnitNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            } else {
//                binding.layoutNewUnitDetails.etUniqueNo.setText("");
               // binding.layoutNewUnitDetails.etConfUnitNumber.setText("");
            }
        }

        ArrayList<AutoCompleteModal> listRelationshipWithHOH = Utils.getDomianList(Constants.domain_respondent_relationship_with_hoh);

        binding.layoutNewUnitDetails.autoCompRespondentRelation.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listRelationshipWithHOH));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompRespondentRelation, listRelationshipWithHOH);

        binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listRelationshipWithHOH));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompRespondentRelationHOH, listRelationshipWithHOH);

        binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listRelationshipWithHOH));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm, listRelationshipWithHOH);

        binding.layoutNewUnitDetails.autoCompRelationEmp.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, listRelationshipWithHOH));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompRelationEmp);

        binding.layoutNewUnitDetails.etPincode.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_pincodes)));
        setFocusChange_OnTouch(binding.layoutNewUnitDetails.etPincode);

        setAccessList();

        if (previousUnitInfoPointDataModel != null) {
            new Handler().postDelayed(() -> {
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    setCustomCount();
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    setCustomCount();
                }
            }, 1000);
        }
    }




    private void setUpPanCard(String text) {
        binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.txtHeader.setVisibility(View.GONE);
        binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText.setVisibility(View.VISIBLE);
        binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText.setText(text);
        binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText.setHint("Enter Pan Card Number");
        binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText.setAllCaps(true);
    }

    private void setUpAadharCard(String text) {
        binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.txtHeader.setVisibility(View.GONE);
        binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.etitText.setVisibility(View.VISIBLE);
        binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.etitText.setText(text);
        binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.etitText.setHint("Enter Aadhar Card Number");
        binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.etitText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});

        binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.etitText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void setHohMemberUniqueId() {
        if (binding.layoutUnitDetailInfo.layoutAddMember.radioMemberTypeHoh.isChecked()) {
            //check here
            //            hohUniqueId = "H_" + Utils.getEpochDateStamp();
//            hoh_relative_path = unit_relative_path + hohUniqueId + "/";
            hohMemberUniqueId = hohUniqueId;
            hohMember_relative_path = hoh_relative_path;
            AppLog.e("UniqueId: " + hohMemberUniqueId);
            AppLog.e("Relative_path: " + hohMember_relative_path);
        } else if (binding.layoutUnitDetailInfo.layoutAddMember.radioMemberTypeOther.isChecked()) {
            memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
            member_relative_path = unit_relative_path + hohUniqueId + "/" + memberUniqueId + "/";
            hohMemberUniqueId = memberUniqueId;
            hohMember_relative_path = member_relative_path;
            AppLog.e("UniqueId: " + hohMemberUniqueId);
            AppLog.e("Relative_path: " + hohMember_relative_path);
        }
    }

    private String getUnitNumber(String unitDetails) {
        String unitNumber = "";

        unitNumber = Utils.getUnitNumberPrefix(unitDetails) + "" + getRandom();

        return unitNumber;
    }

    private void setupUnitDetailsListner() {

        //UnitDetails
        binding.layoutUnitDetailInfo.autoCompUnitFloorDetails.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_floor)));//activity.getResources().getStringArray(R.array.unitDetails)

        setFocusChange_OnTouch(binding.layoutUnitDetailInfo.autoCompUnitFloorDetails);

        binding.layoutUnitDetailInfo.autoCompUnitFloorDetails.setOnItemClickListener((adapterView, view, i, l) -> {

            binding.layoutUnitDetailInfo.autoCompUnitFloorDetails.setError(null, null);

            binding.layoutUnitDetailInfo.etUnitNumber.setText(getUnitNumber(adapterView.getAdapter().getItem(i).toString()));
        });

        //isMemberAvailable
        binding.layoutUnitDetailInfo.radioMemberAvailableYes.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isMemberAvailable = binding.layoutUnitDetailInfo.radioMemberAvailableYes.getText().toString();
                binding.layoutUnitDetailInfo.llayoutUnitDetailRemarks.setVisibility(View.GONE);
                binding.layoutUnitDetailInfo.llayoutUnitDetail.setVisibility(View.VISIBLE);
            }
        });

        binding.layoutUnitDetailInfo.radioMemberAvailableNo.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                removeData();
                isMemberAvailable = binding.layoutUnitDetailInfo.radioMemberAvailableNo.getText().toString();
                binding.layoutUnitDetailInfo.llayoutUnitDetailRemarks.setVisibility(View.VISIBLE);
                binding.layoutUnitDetailInfo.llayoutUnitDetail.setVisibility(View.GONE);
            }
        });

        /*
        Rohit
         */
        binding.layoutNewUnitDetails.radioMemberAvailableYes.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.txtNext.setText("Next");
                resYes = true;
                isResident = "Yes";
                binding.layoutNewUnitDetails.residentYesLayout.setVisibility(View.VISIBLE);
                isMemberAvailable = binding.layoutNewUnitDetails.radioMemberAvailableYes.getText().toString();
                binding.layoutNewUnitDetails.remarksLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.detailsLayout.setVisibility(View.VISIBLE);
            }
        });


        binding.layoutNewUnitDetails.radioMemberAvailableNo.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.txtNext.setText("Finish");
                resYes = false;
                isResident = "No";
                binding.layoutNewUnitDetails.residentYesLayout.setVisibility(View.GONE);
                isMemberAvailable = binding.layoutNewUnitDetails.radioMemberAvailableNo.getText().toString();
                binding.layoutNewUnitDetails.remarksLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.detailsLayout.setVisibility(View.GONE);

                if(binding.layoutNewUnitDetails.autoCompRemarks.getTag()==null){
                    binding.layoutNewUnitDetails.autoCompRemarks.setTag("");
                }
                binding.layoutNewUnitDetails.autoCompRemarks.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_respondent_non_available)));
                setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompRemarks);
                binding.layoutNewUnitDetails.autoCompRemarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                        //floor_no=binding.layoutStructureDetailsLayout.autoCompUnitFloorDetails.getText().toString();
                        N_reamrk = parent.getAdapter().getItem(position).toString();
                        try {
                            if(parent.getAdapter().getItem(position).toString().equalsIgnoreCase("Unit is locked and Notice pasted"))
                            {
                                binding.layoutNewUnitDetails.noticeLayout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.noticeLayout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.autoCompRemarks.setTag(parent.getAdapter().getItem(position).toString());
                            binding.layoutNewUnitDetails.autoCompRemarks.setText(Utils.getTextByTag(Constants.domain_respondent_non_available,parent.getAdapter().getItem(position).toString()));
                        }catch (Exception ex){
                            AppLog.logData(activity,ex.getMessage());
                            ex.getMessage();
                        }
//                        binding.layoutNewUnitDetails.autoCompRemarks.setText(N_reamrk);
                    }
                });
            }
        });


        //unitStatusRent
        binding.layoutNewUnitDetails.radioUnitStatusRent.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isUnit_RentOwner = binding.layoutNewUnitDetails.radioUnitStatusRent.getText().toString();
                if(binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() && !binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("Rented Tenant")){
                    indenpendentDocSet=true;
                    setOwnerRentDocs();
                }else if(binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() && !binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("Rented Tenant")){
                    indenpendentDocSet=true;
                    setOwnerRentDocs();
                }else{
                    indenpendentDocSet=false;
                    if(!binding.layoutUnitDetailInfo.etExistenceSince.getText().toString().equals("")){
                        if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else{
                        setupYearOfStructure();}
                    }
                }
            }
        });

        binding.layoutNewUnitDetails.radioUnitStatusOwnership.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isUnit_RentOwner = binding.layoutNewUnitDetails.radioUnitStatusOwnership.getText().toString();
                indenpendentDocSet=false;
                if(!binding.layoutUnitDetailInfo.etExistenceSince.getText().toString().equals("")){
                    if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                        binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                        if(floorFlag){
                            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                        }else{
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                        }
                        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                    }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                        binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                        if(floorFlag){
                            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                        }else{
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                        }
                        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                    }else{
                    setupYearOfStructure();}
                }
            }
        });

        binding.layoutNewUnitDetails.radioUnitStatusRentComm.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isUnit_RentOwner = binding.layoutNewUnitDetails.radioUnitStatusRentComm.getText().toString();

                if(binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked() && !binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("Rented Tenant")){
                    indenpendentDocSet=true;
                    setOwnerRentDocs();
                }else if(binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked() && !binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("Rented Tenant")){
                    indenpendentDocSet=true;
                    setOwnerRentDocs();
                }else{
                    indenpendentDocSet=false;
                    if(!binding.layoutUnitDetailInfo.etExistenceSince.getText().toString().equals("")){
                        if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                            binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                            }else{
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                                binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                            }
                            binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                        }else{
                        setupYearOfStructure();}
                    }
                }
            }
        });

        binding.layoutNewUnitDetails.radioUnitStatusOwnershipComm.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                isUnit_RentOwner = binding.layoutNewUnitDetails.radioUnitStatusOwnershipComm.getText().toString();
                indenpendentDocSet=false;
                if(!binding.layoutUnitDetailInfo.etExistenceSince.getText().toString().equals("")){
                    if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                        binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                        if(floorFlag){
                            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                        }else{
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                        }
                        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                    }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                        binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                        if(floorFlag){
                            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                        }else{
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                            binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                        }
                        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
                    }else{
                    setupYearOfStructure();}
                }
            }
        });

        //no_of_member
        binding.layoutUnitDetailInfo.autoCompNoOfMembers.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.noOfMembers)));

        setFocusChange_OnTouch(binding.layoutUnitDetailInfo.autoCompNoOfMembers);



        /*
        Rohit
         */
        binding.layoutNewUnitDetails.radioUnitUsageReligious.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                unitStructureUsage = Constants.ReligiousCheckBox;
                resBoolean = false;
                commBoolean = false;
                rcBoolean = false;
                industrialBoolean = false;
                othersBoolean = false;
                if (ageFlag >= 18) {
                    binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.religiousAmenitiesAboveLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.dietyLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.noPeopleVisitsLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.registeredReligiousLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.firstHideLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.loftLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                } else {
                    binding.layoutNewUnitDetails.hohTitleRes.setText("Name of HOH");
                    binding.layoutNewUnitDetails.hohContactTitle.setText("Contact number of HOH");
                    binding.layoutNewUnitDetails.relHohBelow18TextView.setText("Relationship with HOH");
                    binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                    //vidnyan
                    binding.layoutNewUnitDetails.ltOtherUsageBelow.setVisibility(View.GONE);
                }
                binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(true);
                binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
                binding.layoutNewUnitDetails.etUnitAreaComm.setFocusable(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setEnabled(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setBackgroundResource(R.drawable.rounded_edittext);

                binding.layoutNewUnitDetails.autoCompStructure.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_structure_religious)));
                setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompStructure);
                binding.layoutNewUnitDetails.autoCompStructure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                        binding.layoutNewUnitDetails.autoCompStructure.setTag(parent.getAdapter().getItem(position).toString());
                        if (parent.getAdapter().getItem(position).toString().equals("Others")) {
                            binding.layoutNewUnitDetails.etStructureOther.setVisibility(View.VISIBLE);
                        } else {
                            binding.layoutNewUnitDetails.etStructureOther.setVisibility(View.GONE);
                        }
                    }
                });


//                uncheckAllStructureUsageRadio();
//                if (year > 2000) {
//                    setupResidentProofAdditionalChainDocument();
//                }
//                unitStructureUsage = Constants.ResidentialCheckBox;

            }
        });

        binding.layoutNewUnitDetails.radioUnitUsageAmenities.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                unitStructureUsage = Constants.AmenitiesCheckBox;
                resBoolean = false;
                commBoolean = false;
                rcBoolean = false;
                industrialBoolean = false;
                othersBoolean = false;
                if (ageFlag >= 18) {
                    binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.religiousAmenitiesAboveLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.dietyLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.noPeopleVisitsLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.registeredReligiousLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.firstHideLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.loftLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                } else {
                    binding.layoutNewUnitDetails.hohTitleRes.setText("Name of HOH");
                    binding.layoutNewUnitDetails.hohContactTitle.setText("Contact number of HOH");
                    binding.layoutNewUnitDetails.relHohBelow18TextView.setText("Relationship with HOH");
                    binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                    //vidnyan
                    binding.layoutNewUnitDetails.ltOtherUsageBelow.setVisibility(View.GONE);
                }
                binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(true);
                binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
                binding.layoutNewUnitDetails.etUnitAreaComm.setFocusable(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setEnabled(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setBackgroundResource(R.drawable.rounded_edittext);

                binding.layoutNewUnitDetails.autoCompStructure.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_structure_amenities)));
                setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompStructure);
                binding.layoutNewUnitDetails.autoCompStructure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                        binding.layoutNewUnitDetails.autoCompStructure.setTag(parent.getAdapter().getItem(position).toString());
                        if (parent.getAdapter().getItem(position).toString().equals("Others")) {
                            binding.layoutNewUnitDetails.etStructureOther.setVisibility(View.VISIBLE);
                        } else {
                            binding.layoutNewUnitDetails.etStructureOther.setVisibility(View.GONE);
                        }
                    }
                });


//                uncheckAllStructureUsageRadio();
//                if (year > 2000) {
//                    setupResidentProofAdditionalChainDocument();
//                }
//                unitStructureUsage = Constants.ResidentialCheckBox;

            }
        });



        binding.layoutNewUnitDetails.radioUnitUsageResidential.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if(st==1){
                    deleteAllMedia(previousSelectedRadio,binding.layoutNewUnitDetails.radioUnitUsageResidential);
                }
                if(st==0){
                    st=1;
                }
                previousSelectedRadio=binding.layoutNewUnitDetails.radioUnitUsageResidential;
                unitStructureUsage = Constants.ResidentialCheckBox;
                resBoolean = true;
                commBoolean = false;
                rcBoolean = false;
                industrialBoolean = false;
                othersBoolean = false;
                if (ageFlag >= 18) {
                    binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.religiousAmenitiesAboveLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.firstHideLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.loftLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.VISIBLE);

                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    if(floorFlag){
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 9 attached");
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 9 attached");
                    }else{
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 7 attached");
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 7 attached");
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 7 attached");
                    }
                } else {
                    binding.layoutNewUnitDetails.hohTitleRes.setText("Name of HOH");
                    binding.layoutNewUnitDetails.hohContactTitle.setText("Contact number of HOH");
                    binding.layoutNewUnitDetails.relHohBelow18TextView.setText("Relationship with HOH");
                    binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                    //vidnyan
                    binding.layoutNewUnitDetails.ltOtherUsageBelow.setVisibility(View.GONE);
                }
                binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(true);
                binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
                binding.layoutNewUnitDetails.etUnitAreaComm.setFocusable(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setEnabled(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setBackgroundResource(R.drawable.rounded_edittext);
//                uncheckAllStructureUsageRadio();
//                if (year > 2000) {
//                    setupResidentProofAdditionalChainDocument();
//                }
//                unitStructureUsage = Constants.ResidentialCheckBox;

            }
        });

        binding.layoutNewUnitDetails.radioUnitUsageOthers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if(st==1){
                    deleteAllMedia(previousSelectedRadio,binding.layoutNewUnitDetails.radioUnitUsageOthers);
                }
                if(st==0){
                    st=1;
                }
                previousSelectedRadio=binding.layoutNewUnitDetails.radioUnitUsageOthers;
                unitStructureUsage = Constants.OthersCheckBox;

                resBoolean = false;
                commBoolean = false;
                rcBoolean = false;
                industrialBoolean = false;
                othersBoolean = true;
                binding.layoutNewUnitDetails.noOfEmpText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                binding.layoutNewUnitDetails.areaUnitTextComm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                binding.layoutNewUnitDetails.loftTitleComm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                binding.layoutNewUnitDetails.areaLoftCommText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                binding.layoutNewUnitDetails.ExistSinceCommText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                binding.layoutNewUnitDetails.txtUnitStatusComm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                if (ageFlag >= 18) {
                    binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.hohTitleRes.setText("Name of Owner/Employer");
                    binding.layoutNewUnitDetails.hohContactTitle.setText("Contact Number of Owner/Employer");
                    binding.layoutNewUnitDetails.relHohBelow18TextView.setText("Relationship with Owner/Employer");
                    binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);


                    binding.layoutNewUnitDetails.ltOtherUsageBelow.setVisibility(View.VISIBLE);




                }
                binding.layoutNewUnitDetails.etOtherUsage.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.txtOtherUsage.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(true);
                binding.layoutNewUnitDetails.ltOtherUsage.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
                binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.etUnitAreaComm.setText("");
                binding.layoutNewUnitDetails.etUnitAreaComm.setFocusable(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setEnabled(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setBackgroundResource(R.drawable.rounded_edittext);
            } else {
                binding.layoutNewUnitDetails.ltOtherUsage.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.etOtherUsage.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.txtOtherUsage.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.noOfEmpText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_asterisk_svg, 0);
                binding.layoutNewUnitDetails.areaUnitTextComm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_asterisk_svg, 0);
                binding.layoutNewUnitDetails.loftTitleComm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_asterisk_svg, 0);
                binding.layoutNewUnitDetails.areaLoftCommText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_asterisk_svg, 0);
                binding.layoutNewUnitDetails.ExistSinceCommText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_asterisk_svg, 0);
                binding.layoutNewUnitDetails.txtUnitStatusComm.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_asterisk_svg, 0);

            }

        });

        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if(st==1){
                    deleteAllMedia(previousSelectedRadio,binding.layoutNewUnitDetails.radioUnitUsageCommercial);
                }
                if(st==0){
                    st=1;
                }
                previousSelectedRadio=binding.layoutNewUnitDetails.radioUnitUsageCommercial;
                unitStructureUsage = Constants.CommercialCheckBox;
                resBoolean = false;
                commBoolean = true;
                rcBoolean = false;
                industrialBoolean = false;
                othersBoolean = false;
                if (ageFlag >= 18) {
                    binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.hohTitleRes.setText("Name of Owner/Employer");
                    binding.layoutNewUnitDetails.hohContactTitle.setText("Contact Number of Owner/Employer");
                    binding.layoutNewUnitDetails.relHohBelow18TextView.setText("Relationship with Owner/Employer");
                    binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                    //vidnyan
                    binding.layoutNewUnitDetails.ltOtherUsageBelow.setVisibility(View.GONE);
                }
                binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(true);
                binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
                binding.layoutNewUnitDetails.etUnitAreaComm.setText("");
                binding.layoutNewUnitDetails.etUnitAreaComm.setFocusable(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setEnabled(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setBackgroundResource(R.drawable.rounded_edittext);
            }
        });

        binding.layoutNewUnitDetails.radioUnitUsageRC.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if(st==1){
                    deleteAllMedia(previousSelectedRadio,binding.layoutNewUnitDetails.radioUnitUsageRC);
                }
                if(st==0){
                    st=1;
                }
                previousSelectedRadio=binding.layoutNewUnitDetails.radioUnitUsageRC;
                if(pdfPathRes.equals("") && pdfPathComm.equals("")){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                }else if(pdfPathRes.equals("") && !pdfPathComm.equals("")){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
                }else if(!pdfPathRes.equals("") && pdfPathComm.equals("")){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
                }else if(!pdfPathRes.equals("") && !pdfPathComm.equals("")){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("2 out of 2 attached");
                }

                unitStructureUsage = Constants.RcCheckBox;

                resBoolean = false;
                commBoolean = false;
                rcBoolean = true;
                othersBoolean = false;
                industrialBoolean = false;
                if (ageFlag >= 18) {
                    binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.hohTitleRes.setText("Name of Owner/Employer");
                    binding.layoutNewUnitDetails.hohContactTitle.setText("Contact Number of Owner/Employer");
                    binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                    //vidnyan
                    binding.layoutNewUnitDetails.ltOtherUsageBelow.setVisibility(View.GONE);
                }
                binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(true);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);

                if(!binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString().equalsIgnoreCase("")
                        && !binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString().equalsIgnoreCase("")){
                    double a= Double.parseDouble(binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString());
                    double b= Double.parseDouble(binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString());
                    binding.layoutNewUnitDetails.etUnitAreaComm.setText(""+Double.sum(a,b));
                }
//                binding.layoutNewUnitDetails.etUnitAreaComm.setFocusable(false);
                binding.layoutNewUnitDetails.etUnitAreaComm.setEnabled(false);
                binding.layoutNewUnitDetails.etUnitAreaComm.setBackgroundResource(R.drawable.rounded_blue_edittext);
            }
        });

        binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if(st==1){
                    deleteAllMedia(previousSelectedRadio,binding.layoutNewUnitDetails.radioUnitUsageIndustrial);
                }
                if(st==0){
                    st=1;
                }
                previousSelectedRadio=binding.layoutNewUnitDetails.radioUnitUsageIndustrial;
                unitStructureUsage = Constants.IndustrialCheckBox;

                resBoolean = false;
                commBoolean = false;
                rcBoolean = false;
                industrialBoolean = true;
                othersBoolean = false;
                if (ageFlag >= 18) {
                    binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.hohTitleRes.setText("Name of Owner/Employer");
                    binding.layoutNewUnitDetails.hohContactTitle.setText("Contact Number of Owner/Employer");
                    binding.layoutNewUnitDetails.relHohBelow18TextView.setText("Relationship with Owner/Employer");
                    binding.layoutNewUnitDetails.commercialLayout.setVisibility(View.GONE);
                    //vidnyan
                    binding.layoutNewUnitDetails.ltOtherUsageBelow.setVisibility(View.GONE);
                }
                binding.layoutNewUnitDetails.rcAreaExtraLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.residentialLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageAmenities.setChecked(false);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setText("");
                binding.layoutNewUnitDetails.etUnitAreaComm.setFocusable(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setEnabled(true);
                binding.layoutNewUnitDetails.etUnitAreaComm.setBackgroundResource(R.drawable.rounded_edittext);
            }
        });


        binding.layoutNewUnitDetails.radioLoftNo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                is_loft = false;
                radioLoft = "false";
                binding.layoutNewUnitDetails.etLoftArea.setFocusable(false);
                binding.layoutNewUnitDetails.etLoftArea.setEnabled(false);
                binding.layoutNewUnitDetails.etLoftArea.setText("");
//                binding.layoutNewUnitDetails.etLoftArea.setBackgroundTintList(activity.getResources().getColorStateList(R.color.fixEditTextColor));
//                binding.layoutNewUnitDetails.etLoftArea.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_image));
                binding.layoutNewUnitDetails.etLoftArea.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.etLoftAreaNotEdit.setVisibility(View.VISIBLE);

                binding.layoutNewUnitDetails.etLoftAreaComm.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.etLoftAreaNotEditComm.setVisibility(View.VISIBLE);
            }
        });
        binding.layoutNewUnitDetails.radioLoftYes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                is_loft = true;
                radioLoft = "true";
                binding.layoutNewUnitDetails.etLoftArea.setFocusable(true);
                binding.layoutNewUnitDetails.etLoftArea.setFocusableInTouchMode(true);
                binding.layoutNewUnitDetails.etLoftArea.setEnabled(true);
                binding.layoutNewUnitDetails.etLoftArea.setClickable(true);
//                binding.layoutNewUnitDetails.etLoftArea.setBackgroundTintList(activity.getResources().getColorStateList(R.color.transparent));
//                binding.layoutNewUnitDetails.etLoftArea.setBackground(activity.getResources().getDrawable(R.drawable.rounded_edittext));
                binding.layoutNewUnitDetails.etLoftArea.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.etLoftAreaNotEdit.setVisibility(View.GONE);

                binding.layoutNewUnitDetails.etLoftAreaComm.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.etLoftAreaNotEditComm.setVisibility(View.GONE);
            }
        });


        binding.layoutNewUnitDetails.radioLoftNoComm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                is_loft = false;
                radioLoft = "false";
                binding.layoutNewUnitDetails.etLoftAreaComm.setFocusable(false);
                binding.layoutNewUnitDetails.etLoftAreaComm.setEnabled(false);
                binding.layoutNewUnitDetails.etLoftAreaComm.setText("");
                //binding.layoutNewUnitDetails.etLoftAreaComm.setBackgroundTintList(activity.getResources().getColorStateList(R.color.fixEditTextColor));

                binding.layoutNewUnitDetails.etLoftAreaComm.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.etLoftAreaNotEditComm.setVisibility(View.VISIBLE);

                binding.layoutNewUnitDetails.etLoftArea.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.etLoftAreaNotEdit.setVisibility(View.VISIBLE);
            }
        });
        binding.layoutNewUnitDetails.radioLoftYesComm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                is_loft = true;
                radioLoft = "true";
                binding.layoutNewUnitDetails.etLoftAreaComm.setFocusable(true);
                binding.layoutNewUnitDetails.etLoftAreaComm.setFocusableInTouchMode(true);
                binding.layoutNewUnitDetails.etLoftAreaComm.setEnabled(true);
                binding.layoutNewUnitDetails.etLoftAreaComm.setClickable(true);

//                binding.layoutNewUnitDetails.etLoftArea.setBackgroundTintList(activity.getResources().getColorStateList(R.color.transparent));
//                binding.layoutNewUnitDetails.etLoftArea.setBackground(activity.getResources().getDrawable(R.drawable.rounded_edittext));
                binding.layoutNewUnitDetails.etLoftAreaComm.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.etLoftAreaNotEditComm.setVisibility(View.GONE);

                binding.layoutNewUnitDetails.etLoftArea.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.etLoftAreaNotEdit.setVisibility(View.GONE);
            }
        });


        //Structure Usage (Unit Usage)
        binding.layoutUnitDetailInfo.radioUnitUsageResidential.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckAllStructureUsageRadio();
                setupResidentProof();
                if (year > 2000) {
                    setupResidentProofAdditionalChainDocument();
                }
                unitStructureUsage = Constants.ResidentialCheckBox;
                binding.layoutUnitDetailInfo.radioUnitUsageResidential.setChecked(true);
            }
        });

        binding.layoutUnitDetailInfo.radioUnitUsageCommercial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckAllStructureUsageRadio();
                setupResidentProof();
                setupCommercialLicenceProof();
                unitStructureUsage = Constants.CommercialCheckBox;
                binding.layoutUnitDetailInfo.radioUnitUsageCommercial.setChecked(true);
            }
        });

        binding.layoutUnitDetailInfo.radioUnitUsageRC.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckAllStructureUsageRadio();
                setupResidentProof();
                setupCommercialLicenceProof();
                binding.layoutUnitDetailInfo.layCommercialArea.setVisibility(View.VISIBLE);
                binding.layoutUnitDetailInfo.txtAreaSqFt.setText("Residential Area (Sq. m)");
                unitStructureUsage = Constants.RcCheckBox;
                binding.layoutUnitDetailInfo.radioUnitUsageRC.setChecked(true);
            }
        });

        binding.layoutUnitDetailInfo.radioUnitUsageReligious.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckAllStructureUsageRadio();
                setupReligiousOthersProof();
                binding.layoutUnitDetailInfo.txtHeaderReligiousOtherProof.setText("Religious (Minimum 1)");
                unitStructureUsage = Constants.SpiritualCheckBox;
                binding.layoutUnitDetailInfo.radioUnitUsageReligious.setChecked(true);
            }
        });

        binding.layoutUnitDetailInfo.radioUnitUsageOthers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                uncheckAllStructureUsageRadio();
                setupReligiousOthersProof();
                binding.layoutUnitDetailInfo.txtHeaderReligiousOtherProof.setText("Other (Minimum 1)");
                unitStructureUsage = binding.layoutUnitDetailInfo.radioUnitUsageOthers.getText().toString();
                binding.layoutUnitDetailInfo.radioUnitUsageOthers.setChecked(true);
                binding.layoutNewUnitDetails.etOtherUsage.setVisibility(View.VISIBLE);
            } else binding.layoutNewUnitDetails.etOtherUsage.setVisibility(View.GONE);

        });

        //ExistenceSince
        TimeZone timeZone = TimeZone.getTimeZone("IST");
        myCalendar = Calendar.getInstance(timeZone);

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            // if (exsComm) {
            //     exsComm = false;


            // }
            if (exsComm) {
//                binding.layoutNewUnitDetails.etExistenceSinceComm.setText("" + year);
                exsComm = false;
                /*
                new date changes
                 */
                extSince= "" + day + "/" + (month + 1) + "/" + year;
//                binding.layoutNewUnitDetails.etExistenceSinceComm.setText("" + day + "/" + (month + 1) + "/" + year);
//                binding.layoutNewUnitDetails.etExistenceSinceComm.setText("" + getAge(year, (month + 1), day));
                binding.layoutNewUnitDetails.etExistenceSinceComm.setText(""+year);
                binding.layoutNewUnitDetails.etExistenceSinceComm.setError(null);
            }
            if (exsRes) {
//                binding.layoutNewUnitDetails.etExistenceSince.setText("" + year);
                exsRes = false;
                /*
                new date changes
                 */
                extSince= "" + day + "/" + (month + 1) + "/" + year;
//                binding.layoutNewUnitDetails.etExistenceSince.setText("" + day + "/" + (month + 1) + "/" + year);
//                binding.layoutNewUnitDetails.etExistenceSince.setText("" + getAge(year, (month + 1), day));
                binding.layoutNewUnitDetails.etExistenceSince.setText(""+year);
                binding.layoutNewUnitDetails.etExistenceSince.setError(null);


            }


            if (edd) {
                if (isDocSelected) {
                    docYear = year;
                } else {
                    docYear = 0;
                }


                binding.layoutNewUnitDetails.ageLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.etShowAge.setText("" + getAge(year, (month + 1), day));
                binding.layoutNewUnitDetails.etDobRespondent.setText("" + day + "/" + (month + 1) + "/" + year);
                edd = false;
                globalAge = getAge(year, (month + 1), day);
                ageFlag = getAge(year, (month + 1), day);
                if (getAge(year, (month + 1), day) >= 18) {
                    binding.txtNext.setText("Next");
//                    binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
//                    binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.ageAboveLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.usageLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.ageBelowLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.hohDetails.setVisibility(View.GONE);

                    if (previousUnitInfoPointDataModel == null) {
                        binding.layoutNewUnitDetails.radioUnitUsageResidential.setClickable(true);
                        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setClickable(true);
                        binding.layoutNewUnitDetails.radioUnitUsageRC.setClickable(true);
                        binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setClickable(true);
                        binding.layoutNewUnitDetails.radioUnitUsageOthers.setClickable(true);
                    }else if(previousUnitInfoPointDataModel != null && unitStructureUsage.equalsIgnoreCase("others")){
                        //vidnyan
                        binding.layoutNewUnitDetails.ltOtherUsage.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.txtOtherUsage.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.etOtherUsage.setVisibility(View.VISIBLE);
                        if(!previousUnitInfoPointDataModel.getType_of_other_structure().isEmpty() && previousUnitInfoPointDataModel.getType_of_other_structure() != "" ){
                            binding.layoutNewUnitDetails.etOtherUsage.setText(previousUnitInfoPointDataModel.getType_of_other_structure().toString());
                        }else{
                            binding.layoutNewUnitDetails.etOtherUsage.setText("");
                        }


                    }
//                    binding.layoutNewUnitDetails.radioUnitUsageResidential.setClickable(true);
//                    binding.layoutNewUnitDetails.radioUnitUsageCommercial.setClickable(true);
//                    binding.layoutNewUnitDetails.radioUnitUsageRC.setClickable(true);
//                    binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setClickable(true);
//                    binding.layoutNewUnitDetails.radioUnitUsageOthers.setClickable(true);
                    if (previousUnitInfoPointDataModel == null) {
                        binding.layoutNewUnitDetails.etExistenceSince.setFocusable(true);
                        binding.layoutNewUnitDetails.etExistenceSince.setEnabled(true);
                        binding.layoutNewUnitDetails.etExistenceSince.setText("");
                        binding.layoutNewUnitDetails.etYearOfStructure.setText("");
                        binding.layoutNewUnitDetails.etYearOfStructure.setTag("");

                        //3-2-2024
//                    binding.layoutNewUnitDetails.etExistenceSince.setBackgroundResource(R.drawable.rounded_blue_edittext);
                        binding.layoutNewUnitDetails.etExistenceSinceComm.setFocusable(true);
                        binding.layoutNewUnitDetails.etExistenceSinceComm.setEnabled(true);
                        binding.layoutNewUnitDetails.etExistenceSinceComm.setText("");
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setText("");
                        binding.layoutNewUnitDetails.etYearOfStructureComm.setTag("");
                    }
//                    binding.layoutNewUnitDetails.etExistenceSinceComm.setBackgroundResource(R.drawable.rounded_blue_edittext);

                } else {
                    binding.txtNext.setText("Finish");
                    binding.layoutNewUnitDetails.ageAboveLayout.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.ageBelowLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.usageLayout.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.hohDetails.setVisibility(View.VISIBLE);
                    //Vidnyan
                    if(previousUnitInfoPointDataModel != null && unitStructureUsage.equalsIgnoreCase("others")){
                        //vidnyan
                        binding.layoutNewUnitDetails.ltOtherUsageBelow.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.txtOtherUsageBelow.setVisibility(View.VISIBLE);
                        binding.layoutNewUnitDetails.etOtherUsageBelow.setVisibility(View.VISIBLE);
                        if(!previousUnitInfoPointDataModel.getType_of_other_structure().isEmpty() && previousUnitInfoPointDataModel.getType_of_other_structure() != "" ){
                            binding.layoutNewUnitDetails.etOtherUsageBelow.setText(previousUnitInfoPointDataModel.getType_of_other_structure().toString());
                        }else{
                            binding.layoutNewUnitDetails.etOtherUsageBelow.setText("");
                        }


                    }

//                    binding.layoutNewUnitDetails.hohTitle.setVisibility(View.VISIBLE);
//                    binding.layoutNewUnitDetails.autoHOHna.setVisibility(View.VISIBLE);
//                    binding.layoutNewUnitDetails.contactHohTitle.setVisibility(View.VISIBLE);
//                    binding.layoutNewUnitDetails.contactHohLayout.setVisibility(View.VISIBLE);

//                    binding.layoutNewUnitDetails.ownerContactTitleComm.setVisibility(View.VISIBLE);
//                    binding.layoutNewUnitDetails.ownerContactTLayoutComm.setVisibility(View.VISIBLE);


                }
            } else {
                edd = false;
                updateDateLabel();
            }

        };

        binding.layoutUnitDetailInfo.etExistenceSince.setOnClickListener(view -> {

            if (!Utils.checkAutodateTimeValidation(this.activity)) {
                return;
            }

            DatePickerDialog dialog = new DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.show();
        });

        /*
        Rohit
         */

        binding.layoutNewUnitDetails.etExistenceSince.setOnClickListener(view -> {
            exsRes = true;
            if (!Utils.checkAutodateTimeValidation(this.activity)) {
                return;
            }

            DatePickerDialog dialog = new DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.show();
        });

        binding.layoutNewUnitDetails.etExistenceSinceComm.setOnClickListener(view -> {
            exsComm = true;
            if (!Utils.checkAutodateTimeValidation(this.activity)) {
                return;
            }

            DatePickerDialog dialog = new DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.show();
        });

        binding.layoutNewUnitDetails.etDobRespondent.setOnClickListener(view -> {

//            if (!Utils.checkAutodateTimeValidation(this.activity)) {
//                return;
//            }
            edd = true;
            DatePickerDialog dialog = new DatePickerDialog(activity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.show();
        });

        binding.layoutNewUnitDetails.layoutRelAmenAttachement.btnBrowseUniquePhoto.setOnClickListener(view -> {
            BtnName="";
            AttName = "Photo with Unique ID";
            showDocumentPopup(1, 400, 10000, "");
        });

        binding.layoutNewUnitDetails.layoutRelAmenAttachement.btnBrowseOthersDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Other Documents";
                showDocumentPopup(1, 300, 10000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRelAmenAttachement.btnBrowseRelAnem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    AttName = "documents_for_Religious_proof";
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    AttName = "documents_for_Amenities_proof";
                }
                BtnName="1650";
                showDocumentPopup(1650, 1650, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.btnBrowseTenement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    AttName = "Supporting documents showing address";
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    AttName = "Supporting documents showing address";
                }
                BtnName="1550";
                showDocumentPopup(1550, 1550, 2000, "");
//                if(binding.layoutNewUnitDetails.etTenementRelAmenities.getText().toString().trim().equals("")){
//                    Utils.shortToast("Please input tenement value first",activity);
//                }else{
//                    if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
//                        AttName = "Religious Document";
//                    }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
//                        AttName = "Amenities Document";
//                    }
//                    BtnName="1550";
//                    showDocumentPopup(1550, 1550, 2000, "");
//                }
            }
        });

        binding.layoutNewUnitDetails.btnBrowseReligiousProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    AttName = "Religious Document";
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    AttName = "Amenities Document";
                }
                BtnName="1555";
                showDocumentPopup(1555, 1555, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.btnBrowseNameTrusteeProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    AttName = "Religious Document";
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    AttName = "Amenities Document";
                }
                BtnName="1560";
                showDocumentPopup(1560, 1560, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.btnBrowseSurveyPavti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.layoutNewUnitDetails.etPavtiRelAmenities.getText().toString().equals("")){
                    Utils.shortToast("Please input survey pavti no. first",activity);
                }else{
                    BtnName="";
                    AttName = "Survey Pavti";
                    showDocumentPopup(1, 100, 10000, "");
                }
            }
        });

        binding.layoutNewUnitDetails.btnNocDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    AttName = "Religious Document";
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    AttName = "Amenities Document";
                }
                BtnName="1565";
                showDocumentPopup(1565, 1565, 2000, "");
            }
        });


        binding.layoutNewUnitDetails.btnApprovalDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    AttName = "Religious Document";
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    AttName = "Amenities Document";
                }
                BtnName="1570";
                showDocumentPopup(1570, 1570, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.btnFestivalDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    AttName = "Religious Document";
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    AttName = "Amenities Document";
                }
                BtnName="1575";
                showDocumentPopup(1575, 1575, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.btnBrowse2000BelowOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(1, 1, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRelAmenAttachement.btnBrowse2000BelowOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(1, 1, 1999, "");
            }
        });

//        binding.layoutNewUnitDetails.layoutRelAmenAttachement.btnBrowse2000BelowOne.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BtnName="";
//                AttName = "Proof of Structure";
//                showDocumentPopup(1, 1, 1999, "");
//            }
//        });


        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowse2000BelowOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(1, 1, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowse2000BelowTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of identity";
                showDocumentPopup(1, 2, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseRecent2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "One year Recent proof";
                showDocumentPopup(1, 3, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseTill2011One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(1, 1, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseTill2011Two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Address";
                showDocumentPopup(1, 2, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.btnBrowseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modif = "";
                if (binding.layoutNewUnitDetails.etUniqueNo.getText().toString().contains("DRP")) {
                    modif = binding.layoutNewUnitDetails.etUniqueNo.getText().toString();
                } else {
                    try {
                        modif = binding.layoutNewUnitDetails.inputEditOne.getPrefixText().toString() + "" + binding.layoutNewUnitDetails.etUniqueNo.getText().toString();
                    } catch (Exception ex) {
                        AppLog.logData(activity,ex.getMessage());
                        modif = "";
                        ex.getCause();
                    }
                }
                    String temfile = modif.toLowerCase();
                    unitVideoFileName = "unit_"+ temfile.replaceAll("/","-")+"_"+ Utils.getEpochDateStamp();
//                    unitVideoFileName = Constants.UnitDistometerVideoType+ Utils.getEpochDateStamp();
                    String str = (modif.toLowerCase()).replaceAll("/","-");
                    String id=previousStructureInfoPointDataModel.getGlobalId();
                    if(id.startsWith("{")){
                        id = previousStructureInfoPointDataModel.getGlobalId().substring( 1, previousStructureInfoPointDataModel.getGlobalId().length() - 1 );
                    }
                    unitVideoFilePath = "s_"+id.toString().toLowerCase()+"/u_"+str+"/distometer";
                    AttName = activity.getString(R.string.unit_distometer_video);
                BtnName="";
                    showDocumentPopup(0, 0, 0, Constants.video);

            }
        });

        binding.layoutNewUnitDetails.btnBrowseVideoComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modif = "";
                if (binding.layoutNewUnitDetails.etUniqueNo.getText().toString().contains("DRP")) {
                    modif = binding.layoutNewUnitDetails.etUniqueNo.getText().toString();
                } else {
                    try {
                        modif = binding.layoutNewUnitDetails.inputEditOne.getPrefixText().toString() + "" + binding.layoutNewUnitDetails.etUniqueNo.getText().toString();
                    } catch (Exception ex) {
                        AppLog.logData(activity,ex.getMessage());
                        modif = "";
                        ex.getCause();
                    }
                }
                String temfile = modif.toLowerCase();
                unitVideoFileName = "unit_"+ temfile.replaceAll("/","-")+"_"+ Utils.getEpochDateStamp();
                String str = (modif.toLowerCase()).replaceAll("/","-");
                String id=previousStructureInfoPointDataModel.getGlobalId();
                if(id.startsWith("{")){
                    id = previousStructureInfoPointDataModel.getGlobalId().substring( 1, previousStructureInfoPointDataModel.getGlobalId().length() - 1 );
                }
                unitVideoFilePath = "s_"+id.toString().toLowerCase()+"/u_"+str+"/distometer";
                AttName = activity.getString(R.string.unit_distometer_video);
                BtnName="";
                showDocumentPopup(0, 0, 0, Constants.video);


                if(!binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString().equalsIgnoreCase("")
                        && !binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString().equalsIgnoreCase("")){
                    double a= Double.parseDouble(binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString());
                    double b= Double.parseDouble(binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString());
                    binding.layoutNewUnitDetails.etUnitAreaComm.setText(""+Double.sum(a,b));
                }
            }
        });

        binding.layoutNewUnitDetails.btnBrowseNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Attachment of the notice on door";
                showDocumentPopup(900, 1, 900, "");
            }
        });

        binding.layoutNewUnitDetails.btnBrowseUnitUnique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "UnitPhoto";
                showDocumentPopup(9000, 1, 9000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseTill2011Three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Chain of Agreement";
                showDocumentPopup(1, 3, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseTill2011Four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of identity";
                showDocumentPopup(1, 4, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseTill2011Five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of payment receipt";
                showDocumentPopup(1, 5, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseRecent2011.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "One year recent proof";
                showDocumentPopup(1, 6, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseAfter2011One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(1, 1, 2011, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseAfter2011Two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Chain of Agreement";
                showDocumentPopup(1, 2, 2011, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseAfter2011Three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of any other document";
                showDocumentPopup(1, 3, 2011, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseSurveyPavti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Survey Pavti";
                showDocumentPopup(1, 100, 10000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseOthersDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Other Documents";
                showDocumentPopup(1, 300, 10000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutResAttachement.btnBrowseUniquePhoto.setOnClickListener(view -> {
            BtnName="";
            AttName = "Photo with Unique ID";
            showDocumentPopup(1, 400, 10000, "");
        });

        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseUniquePhoto.setOnClickListener(view -> {
            BtnName="";
            AttName = "Photo with Unique ID";
            showDocumentPopup(2, 400, 20000, "");
        });


        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowse2000BelowOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(2, 1, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowse2000BelowTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of License";
                showDocumentPopup(2, 2, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseRecentComm2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "One year Recent proof";
                showDocumentPopup(2, 3, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseTill2011One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(2, 1, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseTill2011Two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of License";
                showDocumentPopup(2, 2, 2000, "");
            }
        });

//        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseTill2011Three.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AttName = "Proof of Address";
//                showDocumentPopup(2, 3, 2000, "");
//            }
//        });


        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseTill2011Four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Chain of Agreement";
                showDocumentPopup(2, 4, 2000, "");
            }
        });


        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseTill2011Five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of identity";
                showDocumentPopup(2, 5, 2000, "");
            }
        });


        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseTill2011FF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of payment receipt from SRA";
                showDocumentPopup(2, 6, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseRecentComm2011.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "One year recent proof";
                showDocumentPopup(2, 7, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseAfter2011One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(2, 1, 2011, "");
            }
        });

        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseAfter2011Two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Chain of Agreement";
                showDocumentPopup(2, 2, 2011, "");
            }
        });


        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseAfter2011Three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of any other document";
                showDocumentPopup(2, 3, 2011, "");
            }
        });

        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseSurveyPavti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Survey Pavti";
                showDocumentPopup(2, 200, 20000, "");
            }
        });


        binding.layoutNewUnitDetails.layoutCommAttachement.btnBrowseOthersDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Other Documents";
                showDocumentPopup(1, 300, 10000, "");
            }
        });


        /*
        RC Usage
         */
        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseOthersDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Other Documents";
                showDocumentPopup(1, 300, 10000, "");
            }
        });


        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowse2000BelowOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(800, 1, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowse2000BelowTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of License";
                showDocumentPopup(800, 2, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseRecentComm2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "One year Recent proof";
                showDocumentPopup(800, 3, 1999, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseTill2011One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(800, 1, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseTill2011Two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of License";
                showDocumentPopup(800, 2, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseTill2011Three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Address";
                showDocumentPopup(800, 3, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseTill2011Four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Chain of Agreement";
                showDocumentPopup(800, 4, 2000, "");
            }
        });


        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseTill2011Five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of identity";
                showDocumentPopup(800, 5, 2000, "");
            }
        });


        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseTill2011FF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of payment receipt from SRA";
                showDocumentPopup(800, 6, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseRecentComm2011.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "One year recent proof";
                showDocumentPopup(800, 7, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseAfter2011One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Structure";
                showDocumentPopup(800, 1, 2011, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseAfter2011Two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Chain of Agreement";
                showDocumentPopup(800, 2, 2011, "");
            }
        });


        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseAfter2011Three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of any other document";
                showDocumentPopup(800, 3, 2011, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseSurveyPavti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Survey Pavti";
                showDocumentPopup(800, 200, 20000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutRcAttachement.btnBrowseUniquePhoto.setOnClickListener(view -> {
            BtnName="";
            AttName = "Photo with Unique ID";
            showDocumentPopup(800, 400, 20000, "");
        });

        binding.layoutNewUnitDetails.btnBrowsePdfDistometer.setOnClickListener(view -> {
            BtnName="";
            AttName = "Unit_Layout";
            showDocumentPopup(1100, 1100, 1100, "pdf");
        });

        binding.layoutNewUnitDetails.btnBrowsePdfDistometerComm.setOnClickListener(view -> {
            BtnName="";
            if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                AttName = "unit_layout_rc";
                showDocumentPopup(1100, 1100, 1100, "pdf");
            }else{
                AttName = "Unit_Layout";
                showDocumentPopup(1100, 1100, 1100, "pdf");
            }
        });

        binding.layoutNewUnitDetails.etRcResAreaLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString().equalsIgnoreCase("")
                    && !binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString().equalsIgnoreCase("")){
                        double a= Double.parseDouble(binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString());
                        double b= Double.parseDouble(binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString());
                        binding.layoutNewUnitDetails.etUnitAreaComm.setText(""+Double.sum(a,b));
                    }
                }
            }
        });

        binding.layoutNewUnitDetails.etRcCommAreaLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString().equalsIgnoreCase("")
                            && !binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString().equalsIgnoreCase("")){
                        double a= Double.parseDouble(binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString());
                        double b= Double.parseDouble(binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString());
                        binding.layoutNewUnitDetails.etUnitAreaComm.setText(""+Double.sum(a,b));
                    }
                }
            }
        });


        binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.btnBrowseTill2011Five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of identity";
                showDocumentPopup(1300, 1, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.btnBrowseTill2011Four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of Chain of Agreement";
                showDocumentPopup(1300, 2, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.btnBrowseAfter2011Three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Proof of any other document";
                showDocumentPopup(1300, 3, 2000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.btnBrowseSurveyPavti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Survey Pavti";
                showDocumentPopup(1, 100, 10000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.btnBrowseOthersDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnName="";
                AttName = "Other Documents";
                showDocumentPopup(1, 300, 10000, "");
            }
        });

        binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.btnBrowseUniquePhoto.setOnClickListener(view -> {
            BtnName="";
            AttName = "Photo with Unique ID";
            showDocumentPopup(1, 400, 10000, "");
        });

    }


    private void setupResidentProof() {
        residentProofAttachmentCount = 0;
        binding.layoutUnitDetailInfo.layResidentProof.setVisibility(View.VISIBLE);
        binding.layoutUnitDetailInfo.residentScAttachment.txtHeader.setText(Constants.ShareCertificateLabel);
        binding.layoutUnitDetailInfo.residentEcbAttachment.txtHeader.setText(Constants.ElectricityConnectionBillLabel);
        binding.layoutUnitDetailInfo.residentPhotoPassAttachment.txtHeader.setText(Constants.PhotoPassLabel);
        binding.layoutUnitDetailInfo.residentNaTaxAttachment.txtHeader.setText(Constants.NA_TaxLabel);
        binding.layoutUnitDetailInfo.residentPtprAttachment.txtHeader.setText(Constants.PropertyTaxPaymentReceiptLabel);
        binding.layoutUnitDetailInfo.residentErAttachment.txtHeader.setText(Constants.ElectoralRollLabel);

        resident_scAttachmentList = new ArrayList<>();
        resident_scAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.ShareCertificateLabel, this);
        binding.layoutUnitDetailInfo.residentScAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.residentScAttachment.rvImageListView.setAdapter(resident_scAttachmentListAdapter);

        resident_ecbAttachmentList = new ArrayList<>();
        resident_ecbAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.ElectricityConnectionBillLabel, this);
        binding.layoutUnitDetailInfo.residentEcbAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.residentEcbAttachment.rvImageListView.setAdapter(resident_ecbAttachmentListAdapter);

        resident_ppAttachmentList = new ArrayList<>();
        resident_ppAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.PhotoPassLabel, this);
        binding.layoutUnitDetailInfo.residentPhotoPassAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.residentPhotoPassAttachment.rvImageListView.setAdapter(resident_ppAttachmentListAdapter);

        resident_nataxAttachmentList = new ArrayList<>();
        resident_naTaxAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.NA_TaxLabel, this);
        binding.layoutUnitDetailInfo.residentNaTaxAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.residentNaTaxAttachment.rvImageListView.setAdapter(resident_naTaxAttachmentListAdapter);

        resident_ptprAttachmentList = new ArrayList<>();
        resident_ptprAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.PropertyTaxPaymentReceiptLabel, this);
        binding.layoutUnitDetailInfo.residentPtprAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.residentPtprAttachment.rvImageListView.setAdapter(resident_ptprAttachmentListAdapter);

        resident_erAttachmentList = new ArrayList<>();
        resident_ErAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.ElectoralRollLabel, this);
        binding.layoutUnitDetailInfo.residentErAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.residentErAttachment.rvImageListView.setAdapter(resident_ErAttachmentListAdapter);

    }

    private void setupResidentProofAdditionalChainDocument() {
        residentProofAdditionalAttachmentCount = 0;
        residentProofChainAttachmentCount = 0;
        binding.layoutUnitDetailInfo.layResidentProofAdditionalDocument.setVisibility(View.VISIBLE);
        binding.layoutUnitDetailInfo.layResidentProofChainDocument.setVisibility(View.VISIBLE);
        binding.layoutUnitDetailInfo.additionalSccsasAttachment.txtHeader.setText(Constants.additionalSccsasLabel);
        binding.layoutUnitDetailInfo.additionalCiesaAttachment.txtHeader.setText(Constants.additionalCiesaLabel);
        binding.layoutUnitDetailInfo.additionalAttachment3Attachment.txtHeader.setText(Constants.Attachment3Label);
        binding.layoutUnitDetailInfo.additionalAttachment4Attachment.txtHeader.setText(Constants.Attachment4Label);

        additionalSccsasAttachmentList = new ArrayList<>();
        additionalSccsasAttachmentAdapter = new AttachmentListAdapter(activity, null, Constants.additionalSccsasLabel, this);
        binding.layoutUnitDetailInfo.additionalSccsasAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.additionalSccsasAttachment.rvImageListView.setAdapter(additionalSccsasAttachmentAdapter);

        additionalCiesaAttachmentList = new ArrayList<>();
        additionalCiesaAttachmentAdapter = new AttachmentListAdapter(activity, null, Constants.additionalCiesaLabel, this);
        binding.layoutUnitDetailInfo.additionalCiesaAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.additionalCiesaAttachment.rvImageListView.setAdapter(additionalCiesaAttachmentAdapter);

        additionalAttachment3AttachmentList = new ArrayList<>();
        additionalAttachment3AttachmentAdapter = new AttachmentListAdapter(activity, null, Constants.additionalAttachment3Label, this);
        binding.layoutUnitDetailInfo.additionalAttachment3Attachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.additionalAttachment3Attachment.rvImageListView.setAdapter(additionalAttachment3AttachmentAdapter);

        additionalAttachment4AttachmentList = new ArrayList<>();
        additionalAttachment4AttachmentAdapter = new AttachmentListAdapter(activity, null, Constants.additionalAttachment4Label, this);
        binding.layoutUnitDetailInfo.additionalAttachment4Attachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.additionalAttachment4Attachment.rvImageListView.setAdapter(additionalAttachment4AttachmentAdapter);

        binding.layoutUnitDetailInfo.chainPsipcAttachment.txtHeader.setText(Constants.chainPsipcLabel);
        binding.layoutUnitDetailInfo.chainRaAttachment.txtHeader.setText(Constants.chainRaLabel);
        binding.layoutUnitDetailInfo.chainAttachment3Attachment.txtHeader.setText(Constants.Attachment3Label);
        binding.layoutUnitDetailInfo.chainAttachment4Attachment.txtHeader.setText(Constants.Attachment4Label);

        chainPsipcAttachmentList = new ArrayList<>();
        chainPsipcAttachmentAdapter = new AttachmentListAdapter(activity, null, Constants.chainPsipcLabel, this);
        binding.layoutUnitDetailInfo.chainPsipcAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.chainPsipcAttachment.rvImageListView.setAdapter(chainPsipcAttachmentAdapter);

        chainRaAttachmentList = new ArrayList<>();
        chainRaAttachmentAdapter = new AttachmentListAdapter(activity, null, Constants.chainRaLabel, this);
        binding.layoutUnitDetailInfo.chainRaAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.chainRaAttachment.rvImageListView.setAdapter(chainRaAttachmentAdapter);

        chainAttachment3AttachmentList = new ArrayList<>();
        chainAttachment3AttachmentAdapter = new AttachmentListAdapter(activity, null, Constants.chainAttachment3Label, this);
        binding.layoutUnitDetailInfo.chainAttachment3Attachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.chainAttachment3Attachment.rvImageListView.setAdapter(chainAttachment3AttachmentAdapter);

        chainAttachment4AttachmentList = new ArrayList<>();
        chainAttachment4AttachmentAdapter = new AttachmentListAdapter(activity, null, Constants.chainAttachment4Label, this);
        binding.layoutUnitDetailInfo.chainAttachment4Attachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.chainAttachment4Attachment.rvImageListView.setAdapter(chainAttachment4AttachmentAdapter);

    }

    private void setupCommercialLicenceProof() {

        licenseProofAttachmentListCount = 0;
        binding.layoutUnitDetailInfo.layCommercialLicenseProof.setVisibility(View.VISIBLE);
        binding.layoutUnitDetailInfo.licenseProofSeAttachment.txtHeader.setText(Constants.commercialShopEstLabel);
        binding.layoutUnitDetailInfo.licenseProofRhlAttachment.txtHeader.setText(Constants.commercialRestrHotelLabel);
        binding.layoutUnitDetailInfo.licenseProofFdlAttachment.txtHeader.setText(Constants.commercialFoodDrugLabel);
        binding.layoutUnitDetailInfo.licenseProofFalAttachment.txtHeader.setText(Constants.commercialFactoryActLabel);

        licenseProofSeAttachmentList = new ArrayList<>();
        licenseProofSeAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.commercialShopEstLabel, this);
        binding.layoutUnitDetailInfo.licenseProofSeAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.licenseProofSeAttachment.rvImageListView.setAdapter(licenseProofSeAttachmentListAdapter);

        licenseProofRhlAttachmentList = new ArrayList<>();
        licenseProofRhlAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.commercialRestrHotelLabel, this);
        binding.layoutUnitDetailInfo.licenseProofRhlAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.licenseProofRhlAttachment.rvImageListView.setAdapter(licenseProofRhlAttachmentListAdapter);

        licenseProofFdlAttachmentList = new ArrayList<>();
        licenseProofFdlAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.commercialFoodDrugLabel, this);
        binding.layoutUnitDetailInfo.licenseProofFdlAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.licenseProofFdlAttachment.rvImageListView.setAdapter(licenseProofFdlAttachmentListAdapter);

        licenseProofFalAttachmentList = new ArrayList<>();
        licenseProofFalAttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.commercialFactoryActLabel, this);
        binding.layoutUnitDetailInfo.licenseProofFalAttachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.licenseProofFalAttachment.rvImageListView.setAdapter(licenseProofFalAttachmentListAdapter);

    }

    private void setupReligiousOthersProof() {

        religiousAttachmentCount = 0;
        otherAttachmentCount = 0;
        binding.layoutUnitDetailInfo.layReligiousOther.setVisibility(View.VISIBLE);
        binding.layoutUnitDetailInfo.religiousOtherA1Attachment.txtHeader.setText(Constants.Attachment1Label);
        binding.layoutUnitDetailInfo.religiousOtherA2Attachment.txtHeader.setText(Constants.Attachment2Label);
        binding.layoutUnitDetailInfo.religiousOtherA3Attachment.txtHeader.setText(Constants.Attachment3Label);
        binding.layoutUnitDetailInfo.religiousOtherA4Attachment.txtHeader.setText(Constants.Attachment4Label);

        religiousOtherA1AttachmentList = new ArrayList<>();
        religiousOtherA1AttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.Attachment1Label, this);
        binding.layoutUnitDetailInfo.religiousOtherA1Attachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.religiousOtherA1Attachment.rvImageListView.setAdapter(religiousOtherA1AttachmentListAdapter);

        religiousOtherA2AttachmentList = new ArrayList<>();
        religiousOtherA2AttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.Attachment2Label, this);
        binding.layoutUnitDetailInfo.religiousOtherA2Attachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.religiousOtherA2Attachment.rvImageListView.setAdapter(religiousOtherA2AttachmentListAdapter);

        religiousOtherA3AttachmentList = new ArrayList<>();
        religiousOtherA3AttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.Attachment3Label, this);
        binding.layoutUnitDetailInfo.religiousOtherA3Attachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.religiousOtherA3Attachment.rvImageListView.setAdapter(religiousOtherA3AttachmentListAdapter);

        religiousOtherA4AttachmentList = new ArrayList<>();
        religiousOtherA4AttachmentListAdapter = new AttachmentListAdapter(activity, null, Constants.Attachment4Label, this);
        binding.layoutUnitDetailInfo.religiousOtherA4Attachment.rvImageListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.layoutUnitDetailInfo.religiousOtherA4Attachment.rvImageListView.setAdapter(religiousOtherA4AttachmentListAdapter);


    }

    private void hideAttachmentLay() {
        binding.layoutUnitDetailInfo.layResidentProof.setVisibility(View.GONE);
        binding.layoutUnitDetailInfo.layResidentProofAdditionalDocument.setVisibility(View.GONE);
        binding.layoutUnitDetailInfo.layResidentProofChainDocument.setVisibility(View.GONE);
        binding.layoutUnitDetailInfo.layCommercialLicenseProof.setVisibility(View.GONE);
        binding.layoutUnitDetailInfo.layReligiousOther.setVisibility(View.GONE);
    }

    private void setUpListner() {

        binding.commonHeader.imgBack.setOnClickListener(view -> {
            switch (position) {
                case 0: {
                    activity.onBackPressed();
                }
                case 1: {
                    binding.llayoutUnitDetailInfo.setVisibility(View.GONE);
                    binding.stepCardView.setVisibility(View.GONE);
                    binding.headerLayout.setVisibility(View.GONE);
                    position = 0;
                    binding.stepView.done(false);
                    binding.stepView.go(position, true);
                    binding.txtNext.setText("Next");
                    break;
                }
                case 2: {
                    binding.llayoutUnitDetailInfo.setVisibility(View.GONE);
                    position = 0; //position = 1;
                    binding.stepView.done(false);
                    binding.stepView.go(position, true);
                    binding.txtNext.setText("Next");
                    break;
                }
            }
        });


        binding.stepView.done(false);
        position = 0;
        binding.btnNext.setOnClickListener(view -> {
            floorFlag=App.getSharedPreferencesHandler().getBoolean(Constants.floorFlag);
            binding.layoutNewUnitDetails.autoHOHna.setText(Utils.capitalizeEachWord(binding.layoutNewUnitDetails.autoHOHna.getText().toString()));
            binding.layoutNewUnitDetails.etRespondentName.setText(Utils.capitalizeEachWord(binding.layoutNewUnitDetails.etRespondentName.getText().toString()));
            binding.layoutNewUnitDetails.etHOHname.setText(Utils.capitalizeEachWord(binding.layoutNewUnitDetails.etHOHname.getText().toString()));

//         if (position == 2) {
            if (previousStructureInfoPointDataModel.getStructure_status().equalsIgnoreCase(Constants.Not_Started)){
                localSurveyDbViewModel.updateStructureStatusDataTable(previousStructureInfoPointDataModel.getStructure_id(), Constants.InProgress);
            }
            boolean check = false;

            binding.txtNext.setText("Next");
            boolean mobileCheck = true;
            if (globalAge >= 18) {
                mobileCheck = false;
            }

            boolean isSoc=false;
            try {
                String structureTypeName= App.getSharedPreferencesHandler().getString(Constants.structureTypeName);
                if(structureTypeName!=null && !structureTypeName.equalsIgnoreCase("null") && (structureTypeName.contains("toilet") || structureTypeName.contains("Toilet")) ){
                    isSoc=true;
                }else if(structureTypeName!=null && !structureTypeName.equalsIgnoreCase("null") && (structureTypeName.contains("meter") || structureTypeName.contains("Meter")) ){
                    isSoc=false;
                }
            }catch (Exception ex){
                ex.getMessage();
            }

//                if(resYes){
//                    binding.layoutNewUnitDetails.residentYesLayout.setVisibility(View.VISIBLE);
//                }else{
//                    binding.layoutNewUnitDetails.residentYesLayout.setVisibility(View.GONE);
//                }
            List<MediaInfoDataModel> getMediaInfoDataRcRes=null;
            if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.RcCheckBox)){
                getMediaInfoDataRcRes = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit("unit_layout_rc", unit_unique_id, false);
            }

            List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit("Distometer", unit_unique_id, false);

            binding.layoutNewUnitDetails.inputEditOne.setError(null);
            binding.layoutNewUnitDetails.etUniqueNo.setError(null);
           // binding.layoutNewUnitDetails.inputEditTwo.setError(null);
            //binding.layoutNewUnitDetails.etConfUnitNumber.setError(null);
            //binding.layoutNewUnitDetails.etConfUnitNumber.setError(null);
            binding.layoutNewUnitDetails.etAreaName.setError(null);
            binding.layoutNewUnitDetails.etWardNo.setError(null);
            binding.layoutNewUnitDetails.etSectorNo.setError(null);
            binding.layoutNewUnitDetails.etZoneNo.setError(null);
            binding.layoutNewUnitDetails.autoCompNagar.setError(null);
            binding.layoutNewUnitDetails.etNameNagarOther.setError(null);
            binding.layoutNewUnitDetails.etSocietyName.setError(null);
            binding.layoutNewUnitDetails.etStreetRoadName.setError(null);
            binding.layoutNewUnitDetails.etLandmark.setError(null);
            binding.layoutNewUnitDetails.etPincode.setError(null);
            binding.layoutNewUnitDetails.txtMemberAvailable.setError(null);
            binding.layoutNewUnitDetails.autoCompRemarks.setError(null);
            binding.layoutNewUnitDetails.etRespondentName.setError(null);
            binding.layoutNewUnitDetails.etRespondentContact.setError(null);
            binding.layoutNewUnitDetails.etRespondentContact.setError(null);
            binding.layoutNewUnitDetails.etDobRespondent.setError(null);
            binding.layoutNewUnitDetails.usageTitle.setError(null);
            binding.layoutNewUnitDetails.etOtherUsage.setError(null);
            binding.layoutNewUnitDetails.etContactNumber.setError(null);
            binding.layoutNewUnitDetails.autoCompRespondentRelation.setError(null);
            binding.layoutNewUnitDetails.etRespondentRelation.setError(null);
            binding.layoutNewUnitDetails.etHOHname.setError(null);

            binding.layoutNewUnitDetails.autoCompNagar.setError(null);
            binding.layoutNewUnitDetails.etPincode.setError(null);
            binding.layoutNewUnitDetails.autoCompRemarks.setError(null);
            binding.layoutNewUnitDetails.etDobRespondent.setError(null);
            binding.layoutNewUnitDetails.usageTitle.setError(null);
            binding.layoutNewUnitDetails.autoCompRespondentRelation.setError(null);
            binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setError(null);
            binding.layoutNewUnitDetails.etRespondentRelationHoh.setError(null);
            binding.layoutNewUnitDetails.autoCompDocTenement.setError(null);
            binding.layoutNewUnitDetails.etLoftArea.setError(null);
            binding.layoutNewUnitDetails.txtMemberAvailable.setError(null);
            binding.layoutNewUnitDetails.txtUnitStatus.setError(null);
            binding.layoutNewUnitDetails.loftTitle.setError(null);

            binding.layoutNewUnitDetails.txtUnitStatusComm.setError(null);
            binding.layoutNewUnitDetails.loftTitleComm.setError(null);
            binding.layoutNewUnitDetails.etExistenceSinceComm.setError(null);


            binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setError(null);
            binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setError(null);
            binding.layoutNewUnitDetails.autoCompDocTenementComm.setError(null);
            binding.layoutNewUnitDetails.txtUnitStatusComm.setError(null);
            binding.layoutNewUnitDetails.etUnitAreaComm.setError(null);
            binding.layoutNewUnitDetails.loftTitleComm.setError(null);
            binding.layoutNewUnitDetails.etLoftAreaComm.setError(null);
            binding.layoutNewUnitDetails.etExistenceSinceComm.setError(null);
            binding.layoutNewUnitDetails.etNoOfEmployee.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoTwo.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheRecentComm2000.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavti.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttachePhoto.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoFour.setError(null);
//            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoFive.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoSix.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoSeven.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEE.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheRecentComm2011.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoNine.setError(null);
            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoTen.setError(null);


            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoTwo.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheRecentComm2000.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttachePhoto.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoFour.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoFive.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoSix.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoSeven.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEE.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheRecentComm2011.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoNine.setError(null);
            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoTen.setError(null);


            binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setError(null);
            binding.layoutNewUnitDetails.autoCompDocTenement.setError(null);
            binding.layoutNewUnitDetails.txtUnitStatus.setError(null);
            binding.layoutNewUnitDetails.etUnitArea.setError(null);
            binding.layoutNewUnitDetails.loftTitle.setError(null);
            binding.layoutNewUnitDetails.etLoftArea.setError(null);
            binding.layoutNewUnitDetails.etExistenceSince.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoTwo.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheRecent2000.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavti.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttachePhoto.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoFour.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoFive.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoSix.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoSeven.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheRecent2011.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoNine.setError(null);
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoTen.setError(null);


            binding.layoutNewUnitDetails.autoCompStructure.setError(null);
            binding.layoutNewUnitDetails.etStructureOther.setError(null);
            binding.layoutNewUnitDetails.etStructureName.setError(null);
            binding.layoutNewUnitDetails.autoCompDiety.setError(null);
            binding.layoutNewUnitDetails.etDietyOther.setError(null);
            binding.layoutNewUnitDetails.etDietyName.setError(null);
            binding.layoutNewUnitDetails.autoCompFaith.setError(null);
            binding.layoutNewUnitDetails.etFaithOther.setError(null);
            binding.layoutNewUnitDetails.etRelBelongsStrucutre.setError(null);
            binding.layoutNewUnitDetails.etFirstBelongs.setError(null);
            binding.layoutNewUnitDetails.autoCompStructureNature.setError(null);
            binding.layoutNewUnitDetails.etConstructionMaterial.setError(null);
            binding.layoutNewUnitDetails.autoCompTenementRelAmenities.setError(null);
            binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.setError(null);
            binding.layoutNewUnitDetails.autoCompReligiousRegistered.setError(null);
            binding.layoutNewUnitDetails.etReligiousRegisteredOthers.setError(null);
            binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.setError(null);
            binding.layoutNewUnitDetails.etReligioidProof.setError(null);
            binding.layoutNewUnitDetails.etFirstTrustee.setError(null);
            binding.layoutNewUnitDetails.etNameTrusteeProof.setError(null);
            binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.setError(null);
            binding.layoutNewUnitDetails.etNocDoc.setError(null);
            binding.layoutNewUnitDetails.etApprovalDoc.setError(null);
            binding.layoutNewUnitDetails.autoCompFestivalYesNo.setError(null);
            binding.layoutNewUnitDetails.etFestivalDoc.setError(null);
            binding.layoutNewUnitDetails.etSurveyPavti.setError(null);
            binding.layoutNewUnitDetails.autoCompNoFloor.setError(null);
            binding.layoutNewUnitDetails.etUnitArea.setError(null);
            binding.layoutNewUnitDetails.etPdfDistometer.setError(null);
            binding.layoutNewUnitDetails.etExistenceSince.setError(null);
            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheProofRelAnim.setError(null);
            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttachePhoto.setError(null);
            binding.layoutNewUnitDetails.autoCompStructure.setError(null);
            binding.layoutNewUnitDetails.etStructureOther.setError(null);
            binding.layoutNewUnitDetails.etStructureName.setError(null);
            binding.layoutNewUnitDetails.etFirstBelongs.setError(null);
            binding.layoutNewUnitDetails.autoCompStructureNature.setError(null);
            binding.layoutNewUnitDetails.etConstructionMaterial.setError(null);
            binding.layoutNewUnitDetails.autoCompTenementRelAmenities.setError(null);
            binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.setError(null);
            binding.layoutNewUnitDetails.etNocDoc.setError(null);
            binding.layoutNewUnitDetails.etApprovalDoc.setError(null);
            binding.layoutNewUnitDetails.autoCompFestivalYesNo.setError(null);
            binding.layoutNewUnitDetails.etFestivalDoc.setError(null);
            binding.layoutNewUnitDetails.etSurveyPavti.setError(null);
            binding.layoutNewUnitDetails.autoCompNoFloor.setError(null);
            binding.layoutNewUnitDetails.etUnitArea.setError(null);
            binding.layoutNewUnitDetails.etPdfDistometer.setError(null);
            binding.layoutNewUnitDetails.etExistenceSince.setError(null);
            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheProofRelAnim.setError(null);
            binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttachePhoto.setError(null);


            List<MediaInfoDataModel> abc = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Notice_Pasted", unitUniqueId, false);
            List<MediaInfoDataModel> bbb = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("UnitType", unitUniqueId, false);
            String rem=binding.layoutNewUnitDetails.autoCompRemarks.getText().toString();
           if (binding.layoutNewUnitDetails.etUniqueNo.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etUniqueNo, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etUniqueNo.requestFocus();
            }else if (binding.layoutNewUnitDetails.autoCompAccess.getText().toString().equals("")) {
               Utils.setError(binding.layoutNewUnitDetails.autoCompAccess, "Field is mandatory", activity);
               binding.layoutNewUnitDetails.autoCompAccess.requestFocus();
           }else if (binding.layoutNewUnitDetails.etAreaName.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etAreaName, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etAreaName.requestFocus();
            } else if (binding.layoutNewUnitDetails.etWardNo.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etWardNo, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etWardNo.requestFocus();
            } else if (binding.layoutNewUnitDetails.etSectorNo.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etSectorNo, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etSectorNo.requestFocus();
            } else if (binding.layoutNewUnitDetails.etZoneNo.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etZoneNo, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etZoneNo.requestFocus();
            } else if (binding.layoutNewUnitDetails.autoCompNagar.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.autoCompNagar, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.autoCompNagar.requestFocus();
            } else if (binding.layoutNewUnitDetails.etNameNagarOther.getVisibility() == View.VISIBLE
                   && binding.layoutNewUnitDetails.etNameNagarOther.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etNameNagarOther, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etNameNagarOther.requestFocus();
            } else if (!isSoc && binding.layoutNewUnitDetails.etSocietyName.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etSocietyName, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etSocietyName.requestFocus();
            }
//            else if (binding.layoutNewUnitDetails.etStreetRoadName.getText().toString().equals("")) {
//                Utils.setError(binding.layoutNewUnitDetails.etStreetRoadName, "Field is mandatory", activity);
//                binding.layoutNewUnitDetails.etStreetRoadName.requestFocus();
//            }
//            else if (binding.layoutNewUnitDetails.etLandmark.getText().toString().equals("")) {
//                Utils.setError(binding.layoutNewUnitDetails.etLandmark, "Field is mandatory", activity);
//                binding.layoutNewUnitDetails.etLandmark.requestFocus();
//            }
            else if (binding.layoutNewUnitDetails.etPincode.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etPincode, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etPincode.requestFocus();
            } else if (binding.layoutNewUnitDetails.etLandcity.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etLandcity, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etLandcity.requestFocus();
            } else if (binding.layoutNewUnitDetails.etState.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etState, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etState.requestFocus();
            } else if (binding.layoutNewUnitDetails.etCountry.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etCountry, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etCountry.requestFocus();
            } else if (isResident.toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.txtMemberAvailable, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.txtMemberAvailable.requestFocus();
                View targetView = binding.layoutNewUnitDetails.txtMemberAvailable;
                targetView.getParent().requestChildFocus(targetView, targetView);
            } else if ((!resYes) && !surveyFlag && binding.layoutNewUnitDetails.autoCompRemarks.getTag().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.autoCompRemarks, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.autoCompRemarks.requestFocus();
            }
            else if ((!resYes) && !surveyFlag && rem.equalsIgnoreCase("Unit is locked and Notice pasted") && abc!=null && abc.size() <= 0) {
                Utils.setError(binding.layoutNewUnitDetails.etAttacheNotice, "Minimum one attachment is mandatory", activity);
                return;
            }
            else if ((!resYes) && surveyFlag && bbb!=null && bbb.size() <= 0) {
                Utils.setError(binding.layoutNewUnitDetails.etAttacheUnitUnique, "Minimum one attachment is mandatory", activity);
                return;
            }
            else if ((resYes) && binding.layoutNewUnitDetails.etRespondentName.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etRespondentName, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etRespondentName.requestFocus();
            } else if ((resYes) && (binding.layoutNewUnitDetails.etRespondentContact.getVisibility() == View.VISIBLE || binding.layoutNewUnitDetails.etRespondentContact.getVisibility() == View.VISIBLE) && binding.layoutNewUnitDetails.etRespondentContact.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etRespondentContact, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etRespondentContact.requestFocus();
            } else if ((resYes) && (binding.layoutNewUnitDetails.etRespondentContact.getVisibility() == View.VISIBLE || binding.layoutNewUnitDetails.etRespondentContact.getVisibility() == View.VISIBLE) && binding.layoutNewUnitDetails.etRespondentContact.getText().toString().length() < 10) {
                Utils.setError(binding.layoutNewUnitDetails.etRespondentContact, "Please enter 10 digit contact number.", activity);
                binding.layoutNewUnitDetails.etRespondentContact.requestFocus();
            } else if ((resYes) && binding.layoutNewUnitDetails.etDobRespondent.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etDobRespondent, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etDobRespondent.requestFocus();
                View targetView = binding.layoutNewUnitDetails.etDobRespondent;
                targetView.getParent().requestChildFocus(targetView, targetView);
            } else if ((resYes) && unitStructureUsage.equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.usageTitle, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.usageTitle.requestFocus();
                View targetView = binding.layoutNewUnitDetails.usageTitle;
                targetView.getParent().requestChildFocus(targetView, targetView);
            } else if ((resYes) && ageFlag >= 18 && unitStructureUsage.equals(Constants.OthersCheckBox) && binding.layoutNewUnitDetails.etOtherUsage.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etOtherUsage, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etOtherUsage.requestFocus();
            }else if ((resYes) && ageFlag < 18 && unitStructureUsage.equals(Constants.OthersCheckBox) && binding.layoutNewUnitDetails.etOtherUsageBelow.getText().toString().equals("")) {
               Utils.setError(binding.layoutNewUnitDetails.etOtherUsageBelow, "Field is mandatory", activity); //vidnyan, added mandatory condition for age below 18
               binding.layoutNewUnitDetails.etOtherUsageBelow.requestFocus();
           }
            else if ((resYes) && ageFlag < 18 && mobileCheck && binding.layoutNewUnitDetails.etContactNumber.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etContactNumber, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etContactNumber.requestFocus();
            } else if ((resYes) && ageFlag < 18 && mobileCheck && binding.layoutNewUnitDetails.etContactNumber.getText().toString().length() < 10) {
                binding.layoutNewUnitDetails.etContactNumber.setError("Please enter 10 digit contact number.");
                binding.layoutNewUnitDetails.etContactNumber.requestFocus();
                return;
            } else if ((resYes) && ageFlag < 18 && mobileCheck && binding.layoutNewUnitDetails.autoCompRespondentRelation.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.autoCompRespondentRelation, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.autoCompRespondentRelation.requestFocus();
           } else if ((resYes) && ageFlag < 18 && mobileCheck &&
                   binding.layoutNewUnitDetails.autoCompRespondentRelation.getText().toString().equals(Constants.dropdown_others) &&
                   binding.layoutNewUnitDetails.etRespondentRelation.getVisibility() == View.VISIBLE &&
                   binding.layoutNewUnitDetails.etRespondentRelation.getText().toString().isEmpty()) {
               Utils.setError(binding.layoutNewUnitDetails.etRespondentRelation, "Please enter Relationship with HOH", activity);
               binding.layoutNewUnitDetails.etRespondentRelation.requestFocus();
            } else if ((resYes) && ageFlag < 18 && mobileCheck && binding.layoutNewUnitDetails.etHOHname.getText().toString().equals("")) {
                Utils.setError(binding.layoutNewUnitDetails.etHOHname, "Field is mandatory", activity);
                binding.layoutNewUnitDetails.etHOHname.requestFocus();
            }
            // Residential checks
            else if ((resYes) && unitStructureUsage.equals(Constants.ResidentialCheckBox)) {
               List<MediaInfoDataModel> dpdf = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(Constants.UnitDistometerPdfType, unitUniqueId, false);
                if ((resYes) && ageFlag >= 18 && unitStructureUsage.equals(Constants.ResidentialCheckBox)
                        && binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.autoCompRespondentRelationHOH, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.requestFocus();
                } else if ((resYes) && ageFlag >= 18 && unitStructureUsage.equals(Constants.ResidentialCheckBox)
                        && binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals(Constants.dropdown_others)
                        && binding.layoutNewUnitDetails.etRespondentRelationHoh.getVisibility() == View.VISIBLE
                        && binding.layoutNewUnitDetails.etRespondentRelationHoh.getText().toString().isEmpty()) {
                    Utils.setError(binding.layoutNewUnitDetails.etRespondentRelationHoh, "Please enter Relationship with HOH", activity);
                    binding.layoutNewUnitDetails.etRespondentRelationHoh.requestFocus();
                } else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && !binding.layoutNewUnitDetails.etTenementNo.getText().toString().equals("") && (binding.layoutNewUnitDetails.autoCompDocTenement.getText().toString().equals("") || binding.layoutNewUnitDetails.autoCompDocTenement.getText().toString().equals(""))) {
                    Utils.setError(binding.layoutNewUnitDetails.autoCompDocTenement, "Please select tenement document", activity);
                    binding.layoutNewUnitDetails.autoCompDocTenement.requestFocus();
                    binding.layoutNewUnitDetails.autoCompDocTenement.setTag("");
                } else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && isUnit_RentOwner.toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.txtUnitStatus, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.txtUnitStatus.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.txtUnitStatus;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                } else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && binding.layoutNewUnitDetails.etUnitArea.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etUnitArea, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.etUnitArea.requestFocus();
                }else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && previousUnitInfoPointDataModel==null && globalUnitVideoPath.equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etUnitArea, "Attachment is mandatory", activity);
                    binding.layoutNewUnitDetails.etUnitArea.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etUnitArea;
                    scrollToView(binding.layoutNewUnitDetails.scrollViewParent,targetView);
//                    targetView.getParent().requestChildFocus(targetView, targetView);
                }else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && previousUnitInfoPointDataModel!=null && (getMediaInfoData==null || getMediaInfoData.size()<=0)) {
                    Utils.setError(binding.layoutNewUnitDetails.etUnitArea, "Attachment is mandatory", activity);
                    binding.layoutNewUnitDetails.etUnitArea.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etUnitArea;
                    scrollToView(binding.layoutNewUnitDetails.scrollViewParent,targetView);
//                    targetView.getParent().requestChildFocus(targetView, targetView);
                }else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && dpdf.size() <= 0) {
                    Utils.setError(binding.layoutNewUnitDetails.etPdfDistometer, "Minimum one attachment is mandatory", activity);
                    binding.layoutNewUnitDetails.etPdfDistometer.requestFocus();
                    return;
                } else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && radioLoft.equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.loftTitle, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.loftTitle.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.loftTitle;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                } else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && radioLoft.equals("true") && binding.layoutNewUnitDetails.etLoftArea.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etLoftArea, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.etLoftArea.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.loftTitle;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                } else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && binding.layoutNewUnitDetails.etExistenceSince.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etExistenceSince, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.etExistenceSince.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etExistenceSince;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                }
                else if (!indenpendentDocSet && (resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && dateCheck.equalsIgnoreCase(Constants.dateTxtFirst)) {
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of identity", unitUniqueId, false);
                    List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    if(!floorFlag){
                        if (ll.size() <= 0) {
                            Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                    }
//                    else if (ll1.size() <= 0) {
//                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setError(null);
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoTwo, "Minimum one attachment is mandatory", activity);
//                        return;
//                    } else if (ll2.size() <= 0) {
//                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoTwo.setError(null);
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheRecent2000, "Minimum one attachment is mandatory", activity);
//                        return;
//                    }
//                    else
                        if (!binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheRecent2000.setError(null);
                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavti, "Minimum one attachment is mandatory", activity);
                        return;
                    }
//                    else
                        if (ll101.size() <= 0) {
                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavti.setError(null);
                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                        return;
                    }
//                    else if ((resYes) && (ageFlag > 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.getText().toString().equals("")) {
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum, "Field is mandatory", activity);
//                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.requestFocus();
//                        View targetView = binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum;
//                        targetView.getParent().requestChildFocus(targetView, targetView);
//                        return;
//                    }
                    else {
                        check = true;
                    }
                }
                else if (!indenpendentDocSet && (resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && dateCheck.equalsIgnoreCase(Constants.dateTxtSecond)) {
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Address", unitUniqueId, false);
                    List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Chain of Agreement", unitUniqueId, false);
                    List<MediaInfoDataModel> ll3 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of identity", unitUniqueId, false);
                    List<MediaInfoDataModel> ll4 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of payment receipt from SRA", unitUniqueId, false);
                    List<MediaInfoDataModel> ll5 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    if(!floorFlag){
                        if (ll.size() <= 0) {
                            Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                    }
//                    else if (ll1.size() <= 0) {
//                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setError(null);
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoFour, "Minimum one attachment is mandatory", activity);
//                        return;
//                    } else if (ll2.size() <= 0) {
//                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoFour.setError(null);
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoFive, "Minimum one attachment is mandatory", activity);
//                        return;
//                    } else if (ll3.size() <= 0) {
//                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoFive.setError(null);
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoSix, "Minimum one attachment is mandatory", activity);
//                        return;
//                    } else if (ll4.size() <= 0) {
//                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoSix.setError(null);
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoSeven, "Minimum one attachment is mandatory", activity);
//                        return;
//                    } else if (ll5.size() <= 0) {
//                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoSeven.setError(null);
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheRecent2011, "Minimum one attachment is mandatory", activity);
//                        return;
//                    }
//                    else
                        if (!binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheRecent2011.setError(null);
                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavti, "Minimum one attachment is mandatory", activity);
                        return;
                    }
//                    else
                        if (ll101.size() <= 0) {
                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavti.setError(null);
                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                        return;
                    }
//                    else if ((resYes) && (ageFlag > 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.getText().toString().equals("")) {
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum, "Field is mandatory", activity);
//                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.requestFocus();
//                        View targetView = binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum;
//                        targetView.getParent().requestChildFocus(targetView, targetView);
//                        return;
//                    }
                    else {
                        check = true;
                    }
                }
                else if (!indenpendentDocSet && (resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && (dateCheck.equalsIgnoreCase(Constants.dateTxtThird) || dateCheck.equalsIgnoreCase(Constants.dateTxtFour))) {
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_2_post_2011), unitUniqueId, false);
                    List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_3_post_2011), unitUniqueId, false);
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    if(!floorFlag){
                        if (ll.size() <= 0) {
                            Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                    }
//                    else if (ll1.size() <= 0) {
//                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setError(null);
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoNine, "Minimum one attachment is mandatory", activity);
//                        return;
//                    } else if (ll2.size() <= 0) {
//                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoNine.setError(null);
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoTen, "Minimum one attachment is mandatory", activity);
//                        return;
//                    }
//                    else
                        if (!binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.getText().toString().equals("") &&  ll100.size() <= 0) {
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoTen.setError(null);
                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavti, "Minimum one attachment is mandatory", activity);
                        return;
                    }
//                    else
                        if (ll101.size() <= 0) {
                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavti.setError(null);
                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                        return;
                    }
//                    else if ((resYes) && (ageFlag > 18) && unitStructureUsage.equals(Constants.ResidentialCheckBox) && binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.getText().toString().equals("")) {
//                        Utils.setError(binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum, "Field is mandatory", activity);
//                        binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.requestFocus();
//                        View targetView = binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum;
//                        targetView.getParent().requestChildFocus(targetView, targetView);
//                        return;
//                    }
                    else {
                        check = true;
                    }
                }
                else if (indenpendentDocSet && (resYes) && (ageFlag >= 18 ) && unitStructureUsage.equals(Constants.ResidentialCheckBox)){
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    if (!binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                        Utils.setError(binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etSurveyPavti, "Minimum one attachment is mandatory", activity);
                        return;
                    }
                    if (ll101.size() <= 0) {
                        binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etSurveyPavti.setError(null);
                        Utils.setError(binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                        return;
                    }
                    else {
                        check = true;
                    }
               }
                else {
                    check = true;
                }
            }// attachment check for Residential Pending
            //Commercial & rest checks
            else if ((resYes) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox))) {
               List<MediaInfoDataModel> dpdf = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(Constants.UnitDistometerPdfType, unitUniqueId, false);
                if ((resYes) && ageFlag >= 18
                        && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox))
                        && binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.requestFocus();
                } else if ((resYes) && ageFlag >= 18
                        && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox))
                        && binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getVisibility() == View.VISIBLE
                        && binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals(Constants.dropdown_others)
                        && binding.layoutNewUnitDetails.etRelationOwnerEmpComm.getText().toString().isEmpty()) {
                    Utils.setError(binding.layoutNewUnitDetails.etRelationOwnerEmpComm, "Please enter Relationship with Owner/Employer", activity);
                    binding.layoutNewUnitDetails.etRelationOwnerEmpComm.requestFocus();
//                    }else if ((resYes) && (ageFlag>18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && ((!binding.layoutNewUnitDetails.etTenementNo.getText().toString().equals("")) && (binding.layoutNewUnitDetails.autoCompDocTenementComm.getText().toString().equals("") || binding.layoutNewUnitDetails.autoCompDocTenementComm.getText().toString().equals("")))) {
                } else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && (!binding.layoutNewUnitDetails.etTenementNo.getText().toString().equals("") && (binding.layoutNewUnitDetails.autoCompDocTenement.getText().toString().equals("") || binding.layoutNewUnitDetails.autoCompDocTenement.getText().toString().equals("")))) {
                    Utils.setError(binding.layoutNewUnitDetails.autoCompDocTenementComm, "Please select tenement document", activity);
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.requestFocus();
                    binding.layoutNewUnitDetails.autoCompDocTenement.setTag("");
                } else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && isUnit_RentOwner.toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.txtUnitStatusComm, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.txtUnitStatusComm.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.txtUnitStatusComm;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                } else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.RcCheckBox) && binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etRcResAreaLayout, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.etRcResAreaLayout.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etRcResAreaLayout;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                }else if ((resYes) && (ageFlag >= 18) && unitStructureUsage.equals(Constants.RcCheckBox) && binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etRcCommAreaLayout, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.etRcCommAreaLayout.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etRcCommAreaLayout;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                }else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && binding.layoutNewUnitDetails.etUnitAreaComm.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etUnitAreaComm, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.etUnitAreaComm.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etUnitAreaComm;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                }else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && previousUnitInfoPointDataModel==null && globalUnitVideoPath.equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etUnitAreaComm, "Attachment is mandatory", activity);
                    binding.layoutNewUnitDetails.etUnitAreaComm.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etUnitAreaComm;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                }else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && previousUnitInfoPointDataModel!=null && (getMediaInfoData==null || getMediaInfoData.size()<=0)) {
                    Utils.setError(binding.layoutNewUnitDetails.etUnitAreaComm, "Attachment is mandatory", activity);
                    binding.layoutNewUnitDetails.etUnitAreaComm.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etUnitAreaComm;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                }else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox)) && dpdf.size() <= 0) {
                    Utils.setError(binding.layoutNewUnitDetails.etPdfDistometerComm, "Minimum one attachment is mandatory", activity);
                    binding.layoutNewUnitDetails.etPdfDistometerComm.requestFocus();
                    return;
                }else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.RcCheckBox)) && (getMediaInfoDataRcRes==null || getMediaInfoDataRcRes.size()<=1)) {
                    Utils.setError(binding.layoutNewUnitDetails.etPdfDistometerComm, "Minimum one attachment is mandatory for each type", activity);
                    binding.layoutNewUnitDetails.etPdfDistometerComm.requestFocus();
                    return;
                } else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && radioLoft.equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.loftTitleComm, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.loftTitleComm.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.loftTitleComm;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                } else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && radioLoft.equals("true") && binding.layoutNewUnitDetails.etLoftAreaComm.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etLoftAreaComm, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.etLoftAreaComm.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etLoftAreaComm;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                } else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && binding.layoutNewUnitDetails.etExistenceSinceComm.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etExistenceSinceComm, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.etExistenceSinceComm.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etExistenceSinceComm;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                } else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && binding.layoutNewUnitDetails.etNoOfEmployee.getText().toString().equals("")) {
                    Utils.setError(binding.layoutNewUnitDetails.etNoOfEmployee, "Field is mandatory", activity);
                    binding.layoutNewUnitDetails.etNoOfEmployee.requestFocus();
                    View targetView = binding.layoutNewUnitDetails.etNoOfEmployee;
                    targetView.getParent().requestChildFocus(targetView, targetView);
                }
                else if (!indenpendentDocSet && (resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && dateCheck.equalsIgnoreCase(Constants.dateTxtFirst)) {
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of License", unitUniqueId, false);
                    List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                        if(!floorFlag){
                            if (ll.size() <= 0) {
                                Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne, "Minimum one attachment is mandatory", activity);
                                return;
                            }
                        }

//                        else if (ll1.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoTwo, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll2.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoTwo.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheRecentComm2000, "Minimum one attachment is mandatory", activity);
//                            return;
//                        }
//                        else
                            if (!binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti, "Minimum one attachment is mandatory", activity);
                            return;
                        }  if (ll101.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                        else {
                            check = true;
                        }
                    }else{
                        if(!floorFlag){
                            if (ll.size() <= 0) {
                                Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne, "Minimum one attachment is mandatory", activity);
                                return;
                            }
                        }
//                        else if (ll1.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoTwo, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll2.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoTwo.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheRecentComm2000, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else
                            if (!binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavti, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                            if (ll101.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                        else {
                            check = true;
                        }
                    }

                }
                else if (!indenpendentDocSet && (resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && dateCheck.equalsIgnoreCase(Constants.dateTxtSecond)) {
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    List<MediaInfoDataModel> ll11 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of License", unitUniqueId, false);
                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Address", unitUniqueId, false);
                    List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Chain of Agreement", unitUniqueId, false);
                    List<MediaInfoDataModel> ll3 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of identity", unitUniqueId, false);
                    List<MediaInfoDataModel> ll4 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of payment receipt from SRA", unitUniqueId, false);
                    List<MediaInfoDataModel> ll5 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                        if(!floorFlag){
                            if (ll.size() <= 0) {
                                Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree, "Minimum one attachment is mandatory", activity);
                                return;
                            }
                        }

//                        else if (ll11.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoFour, "Minimum one attachment is mandatory", activity);
//                            return;
//                        }
//                        else if (ll1.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoFour.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoFive, "Minimum one attachment is mandatory", activity);
//                            return;
//                        }
//                        else if (ll2.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoFive.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoSix, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll3.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoSix.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoSeven, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll4.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoSeven.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEE, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll5.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEE.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheRecentComm2011, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else
                            if (!binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                            if (ll101.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                        else {
                            check = true;
                        }
                    }else{
                        if(!floorFlag){
                            if (ll.size() <= 0) {
                                Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree, "Minimum one attachment is mandatory", activity);
                                return;
                            }
                        }
//                        else if (ll11.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoFour, "Minimum one attachment is mandatory", activity);
//                            return;
//                        }
//                        else if (ll1.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoFour.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoFive, "Minimum one attachment is mandatory", activity);
//                            return;
//                        }
//                        else if (ll2.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoFour.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoSix, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll3.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoSix.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoSeven, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll4.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoSeven.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEE, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll5.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEE.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheRecentComm2011, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else
                            if (!binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                            if (ll101.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                        else {
                            check = true;
                        }
                    }
                }
                else if (!indenpendentDocSet && (resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox)) && (dateCheck.equalsIgnoreCase(Constants.dateTxtThird) || dateCheck.equalsIgnoreCase(Constants.dateTxtFour))) {
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_2_post_2011), unitUniqueId, false);
                    List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_3_post_2011), unitUniqueId, false);
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                        if(!floorFlag){
                            if (ll.size() <= 0) {
                                Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight, "Minimum one attachment is mandatory", activity);
                                return;
                            }
                        }
//                        else if (ll1.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoNine, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll2.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoNine.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoTen, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else
                            if (!binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                            if (ll101.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutRcAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                        else {
                            check = true;
                        }
                    }else{
                        if(!floorFlag){
                            if (ll.size() <= 0) {
                                Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight, "Minimum one attachment is mandatory", activity);
                                return;
                            }
                        }
//                        else if (ll1.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoNine, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else if (ll2.size() <= 0) {
//                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoNine.setError(null);
//                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoTen, "Minimum one attachment is mandatory", activity);
//                            return;
//                        } else
                            if (!binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                            if (ll101.size() <= 0) {
                            binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavti.setError(null);
                            Utils.setError(binding.layoutNewUnitDetails.layoutCommAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                            return;
                        }
                        else {
                            check = true;
                        }
                    }
                }
                else if (indenpendentDocSet && (resYes) && (ageFlag >= 18 ) && (unitStructureUsage.equals(Constants.IndustrialCheckBox) || unitStructureUsage.equals(Constants.CommercialCheckBox) || unitStructureUsage.equals(Constants.RcCheckBox))){
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    if (!binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etSurveyPavtiNum.getText().toString().equals("") && ll100.size() <= 0) {
                        Utils.setError(binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etSurveyPavti, "Minimum one attachment is mandatory", activity);
                        return;
                    }
                    if (ll101.size() <= 0) {
                        binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etSurveyPavti.setError(null);
                        Utils.setError(binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                        return;
                    }
                    else {
                        check = true;
                    }
                }
                else {
                    check = true;
                }
            }
            else if ((resYes) && (unitStructureUsage.equals(Constants.OthersCheckBox))) {
                check = true;
            }
           else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.ReligiousCheckBox))){
               List<MediaInfoDataModel> dpdf = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Distometer", unitUniqueId, false);
               List<MediaInfoDataModel> dpdf1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(Constants.UnitDistometerPdfType, unitUniqueId, false);
               List<MediaInfoDataModel> dpdf2 = localSurveyDbViewModel.getMediaInfoDataByCatTypeUnit(Constants.rel_cat, unit_unique_id, false,Constants.Proof_of_Religious_Structure_Registered);
               List<MediaInfoDataModel> dpdf3 = localSurveyDbViewModel.getMediaInfoDataByCatTypeUnit(Constants.rel_cat, unit_unique_id, false,Constants.NOC_document);
               List<MediaInfoDataModel> dpdf4 = localSurveyDbViewModel.getMediaInfoDataByCatTypeUnit(Constants.rel_cat, unit_unique_id, false,Constants.Police_or_traffic_or_any_authority_permission);
               List<MediaInfoDataModel> dpdf5 = localSurveyDbViewModel.getMediaInfoDataByCatTypeUnit(Constants.rel_cat, unit_unique_id, false,Constants.Govt_Approval_document);
               List<MediaInfoDataModel> dpdf6 = localSurveyDbViewModel.getMediaInfoDataByCatTypeUnit(Constants.rel_cat, unit_unique_id, false,Constants.Proof_showing_names_of_Trustee);
               List<MediaInfoDataModel> dpdf7 = localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.SurveyPavti, unit_unique_id);

               List<MediaInfoDataModel> dpdf8 = localSurveyDbViewModel.getMediaInfoDataByCatUnit("documents_for_Religious_proof", unit_unique_id);
               List<MediaInfoDataModel> dpdf9 = localSurveyDbViewModel.getMediaInfoDataByCatUnit("Owner Photo", unit_unique_id);
               if(binding.layoutNewUnitDetails.autoCompStructure.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompStructure, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompStructure.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompStructure;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompStructure.getText().toString().equalsIgnoreCase("others") && binding.layoutNewUnitDetails.etStructureOther.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etStructureOther, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etStructureOther.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etStructureOther;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.etStructureName.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etStructureName, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etStructureName.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etStructureName;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompDiety.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompDiety, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompDiety.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompDiety;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompDiety.getText().toString().equalsIgnoreCase("Others") && binding.layoutNewUnitDetails.etDietyOther.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etDietyOther, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etDietyOther.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etDietyOther;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.etDietyName.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etDietyName, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etDietyName.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etDietyName;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompFaith.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompFaith, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompFaith.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompFaith;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompFaith.getText().toString().equalsIgnoreCase("Others") && binding.layoutNewUnitDetails.etFaithOther.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etFaithOther, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etFaithOther.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etFaithOther;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.etRelBelongsStrucutre.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etRelBelongsStrucutre, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etRelBelongsStrucutre.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etRelBelongsStrucutre;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.etFirstBelongs.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etFirstBelongs, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etFirstBelongs.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etFirstBelongs;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompStructureNature.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompStructureNature, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompStructureNature.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompStructureNature;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.etConstructionMaterial.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etConstructionMaterial, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etConstructionMaterial.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etConstructionMaterial;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(!binding.layoutNewUnitDetails.etTenementRelAmenities.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompTenementRelAmenities.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompTenementRelAmenities, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompTenementRelAmenities.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompTenementRelAmenities;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompTenementRelAmenities.getText().toString().equalsIgnoreCase("Others") &&
                       binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompReligiousRegistered.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompReligiousRegistered, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompReligiousRegistered.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompReligiousRegistered;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompReligiousRegistered.getText().toString().equalsIgnoreCase("Others") &&
                       binding.layoutNewUnitDetails.etReligiousRegisteredOthers.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etReligiousRegisteredOthers, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etReligiousRegisteredOthers.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etReligiousRegisteredOthers;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompReligiousRegistered.getText().toString().equalsIgnoreCase("Yes")
                       && binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompReligiousRegistered.getText().toString().equalsIgnoreCase("Yes") && dpdf2.size()<=0){
                   Utils.setError(binding.layoutNewUnitDetails.etReligioidProof, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etReligioidProof.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etReligioidProof;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompReligiousRegistered.getText().toString().equalsIgnoreCase("Yes")
                       && binding.layoutNewUnitDetails.etFirstTrustee.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etFirstTrustee, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etFirstTrustee.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etFirstTrustee;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompReligiousRegistered.getText().toString().equalsIgnoreCase("Yes") && dpdf6.size()<=0){
                   Utils.setError(binding.layoutNewUnitDetails.etNameTrusteeProof, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etNameTrusteeProof.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etNameTrusteeProof;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.getText().toString().equalsIgnoreCase("Others") &&
                       binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompNocYesNo.getText().toString().equalsIgnoreCase("Yes") && dpdf3.size()<=0){
                   Utils.setError(binding.layoutNewUnitDetails.etNocDoc, "Document is mandatory", activity);
                   binding.layoutNewUnitDetails.etNocDoc.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etNocDoc;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompApprovalYesNo.getText().toString().equalsIgnoreCase("Yes")  && dpdf5.size()<=0){
                   Utils.setError(binding.layoutNewUnitDetails.etApprovalDoc, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etApprovalDoc.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etApprovalDoc;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompFestivalYesNo.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompFestivalYesNo, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompFestivalYesNo.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompFestivalYesNo;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompFestivalYesNo.getText().toString().equalsIgnoreCase("Yes")  && dpdf4.size()<=0){
                   Utils.setError(binding.layoutNewUnitDetails.etFestivalDoc, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etFestivalDoc.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etFestivalDoc;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(!binding.layoutNewUnitDetails.etPavtiRelAmenities.getText().toString().equals("")  && dpdf7.size()<=0){
                   Utils.setError(binding.layoutNewUnitDetails.etSurveyPavti, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etSurveyPavti.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etSurveyPavti;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompNoFloor.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompNoFloor, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompNoFloor.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompNoFloor;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if ((resYes) && (ageFlag >= 18) && binding.layoutNewUnitDetails.etUnitArea.getText().toString().equals("")) {
                   Utils.setError(binding.layoutNewUnitDetails.etUnitArea, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etUnitArea.requestFocus();
               }else if ((resYes) && (ageFlag >= 18) && dpdf.size() <= 0) {
                   Utils.setError(binding.layoutNewUnitDetails.etUnitArea, "Minimum one attachment is mandatory", activity);
                   binding.layoutNewUnitDetails.etUnitArea.requestFocus();
                   return;
               }else if ((resYes) && (ageFlag >= 18) && dpdf1.size() <= 0) {
                   Utils.setError(binding.layoutNewUnitDetails.etPdfDistometer, "Minimum one attachment is mandatory", activity);
                   binding.layoutNewUnitDetails.etPdfDistometer.requestFocus();
                   return;
               }else if (binding.layoutNewUnitDetails.etExistenceSince.getText().toString().equals("")) {
                   Utils.setError(binding.layoutNewUnitDetails.etExistenceSince, "Field is required", activity);
                   binding.layoutNewUnitDetails.etExistenceSince.requestFocus();
                   return;
               }else if ((resYes) && (ageFlag >= 18) && dpdf8.size() <= 0) {
                   Utils.setError(binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheProofRelAnim, "Minimum one attachment is mandatory", activity);
                   binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheProofRelAnim.requestFocus();
                   return;
               }
               else if ((resYes) && (ageFlag >= 18) && dpdf9.size() <= 0) {
                   Utils.setError(binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                   binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttachePhoto.requestFocus();
                   return;
               }
               else {
                   check = true;
               }
           }
           else if ((resYes) && (ageFlag >= 18) && (unitStructureUsage.equals(Constants.AmenitiesCheckBox))){
               List<MediaInfoDataModel> dpdf = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Distometer", unitUniqueId, false);
               List<MediaInfoDataModel> dpdf1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(Constants.UnitDistometerPdfType, unitUniqueId, false);
               List<MediaInfoDataModel> dpdf3 = localSurveyDbViewModel.getMediaInfoDataByCatTypeUnit(Constants.ame_cat, unit_unique_id, false,Constants.NOC_document);
               List<MediaInfoDataModel> dpdf4 = localSurveyDbViewModel.getMediaInfoDataByCatTypeUnit(Constants.ame_cat, unit_unique_id, false,Constants.Police_or_traffic_or_any_authority_permission);
               List<MediaInfoDataModel> dpdf5 = localSurveyDbViewModel.getMediaInfoDataByCatTypeUnit(Constants.ame_cat, unit_unique_id, false,Constants.Govt_Approval_document);
               List<MediaInfoDataModel> dpdf7 = localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.SurveyPavti, unit_unique_id);
               List<MediaInfoDataModel> dpdf8 = localSurveyDbViewModel.getMediaInfoDataByCatUnit("documents_for_Amenities_proof", unit_unique_id);
               List<MediaInfoDataModel> dpdf9 = localSurveyDbViewModel.getMediaInfoDataByCatUnit("Owner Photo", unit_unique_id);
               if(binding.layoutNewUnitDetails.autoCompStructure.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompStructure, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompStructure.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompStructure;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompStructure.getText().toString().equalsIgnoreCase("others") && binding.layoutNewUnitDetails.etStructureOther.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etStructureOther, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etStructureOther.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etStructureOther;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.etStructureName.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etStructureName, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etStructureName.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etStructureName;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.etFirstBelongs.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etFirstBelongs, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etFirstBelongs.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etFirstBelongs;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompStructureNature.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompStructureNature, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompStructureNature.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompStructureNature;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.etConstructionMaterial.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etConstructionMaterial, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etConstructionMaterial.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etConstructionMaterial;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(!binding.layoutNewUnitDetails.etTenementRelAmenities.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompTenementRelAmenities.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompTenementRelAmenities, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompTenementRelAmenities.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompTenementRelAmenities;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompTenementRelAmenities.getText().toString().equalsIgnoreCase("Others") &&
                       binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompNocYesNo.getText().toString().equalsIgnoreCase("Yes") && dpdf3.size()<=0){
                   Utils.setError(binding.layoutNewUnitDetails.etNocDoc, "Document is mandatory", activity);
                   binding.layoutNewUnitDetails.etNocDoc.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etNocDoc;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }
               else if(binding.layoutNewUnitDetails.autoCompApprovalYesNo.getText().toString().equalsIgnoreCase("Yes")  && dpdf5.size()<=0) {
                   Utils.setError(binding.layoutNewUnitDetails.etApprovalDoc, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etApprovalDoc.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etApprovalDoc;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }
               else if(binding.layoutNewUnitDetails.autoCompFestivalYesNo.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompFestivalYesNo, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompFestivalYesNo.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompFestivalYesNo;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompFestivalYesNo.getText().toString().equalsIgnoreCase("Yes")  && dpdf4.size()<=0){
                   Utils.setError(binding.layoutNewUnitDetails.etFestivalDoc, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etFestivalDoc.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etFestivalDoc;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(!binding.layoutNewUnitDetails.etPavtiRelAmenities.getText().toString().equals("")  && dpdf7.size()<=0){
                   Utils.setError(binding.layoutNewUnitDetails.etSurveyPavti, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etSurveyPavti.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.etSurveyPavti;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if(binding.layoutNewUnitDetails.autoCompNoFloor.getText().toString().equals("")){
                   Utils.setError(binding.layoutNewUnitDetails.autoCompNoFloor, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.autoCompNoFloor.requestFocus();
                   View targetView = binding.layoutNewUnitDetails.autoCompNoFloor;
                   targetView.getParent().requestChildFocus(targetView, targetView);
                   return;
               }else if ((resYes) && (ageFlag >= 18) && binding.layoutNewUnitDetails.etUnitArea.getText().toString().equals("")) {
                   Utils.setError(binding.layoutNewUnitDetails.etUnitArea, "Field is mandatory", activity);
                   binding.layoutNewUnitDetails.etUnitArea.requestFocus();
               }else if ((resYes) && (ageFlag >= 18) && dpdf.size() <= 0) {
                   Utils.setError(binding.layoutNewUnitDetails.etUnitArea, "Minimum one attachment is mandatory", activity);
                   binding.layoutNewUnitDetails.etUnitArea.requestFocus();
                   return;
               }else if ((resYes) && (ageFlag >= 18) && dpdf1.size() <= 0) {
                   Utils.setError(binding.layoutNewUnitDetails.etPdfDistometer, "Minimum one attachment is mandatory", activity);
                   binding.layoutNewUnitDetails.etPdfDistometer.requestFocus();
                   return;
               }else if (binding.layoutNewUnitDetails.etExistenceSince.getText().toString().equals("")) {
                   Utils.setError(binding.layoutNewUnitDetails.etExistenceSince, "Field is required", activity);
                   binding.layoutNewUnitDetails.etExistenceSince.requestFocus();
                   return;
               }else if ((resYes) && (ageFlag >= 18) && dpdf8.size() <= 0) {
                   Utils.setError(binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheProofRelAnim, "Minimum one attachment is mandatory", activity);
                   binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheProofRelAnim.requestFocus();
                   return;
               }
               else if ((resYes) && (ageFlag >= 18) && dpdf9.size() <= 0) {
                   Utils.setError(binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttachePhoto, "Minimum one attachment is mandatory", activity);
                   binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttachePhoto.requestFocus();
                   return;
               }
               else{
                   check = true;
               }
           }

            else if ((!resYes)) {
                check = true;
            }
            if(((ageFlag >= 18) && (unitStructureUsage.equals(Constants.ReligiousCheckBox))) || (((ageFlag >= 18) && (unitStructureUsage.equals(Constants.AmenitiesCheckBox))))){

            }else{
                if (!validateUsageFields(unitStructureUsage)) {
                    return;
                }
            }

            if(unitStructureUsage.equals(Constants.ReligiousCheckBox)) {
                if (ageFlag < 18) {
                    check = true;
                }
            }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                if(ageFlag<18){
                    check = true;
                }
            }

            if (check) {
                Utils.showProgress("Saving unit record.Please wait...", activity);
                UnitInfoDataModel unitInfoDataModel2 = getUnitInfoDataModel();
                unitInfoDataModelGlobal = unitInfoDataModel2;
                unitInfoDataModelHashMap.put(unitUniqueId, unitInfoDataModel2);
                localSurveyDbViewModel.insertStructureUnitIdStatusData(new StructureUnitIdStatusDataTable(unitUniqueId,
                        structUniqueId, unitAlertStatus), activity);
                new Handler().postDelayed(() -> {

                    localSurveyDbViewModel.insertUnitInfoPointData(unitInfoDataModelHashMap.get(unitUniqueId), activity);
                    position = 2;
                    if (ageFlag >= 18 && resYes) {
//                            binding.llayoutUnitDetailInfo.setVisibility(View.GONE);
//                            binding.llayoutNewUnitDetails.setVisibility(View.GONE);
                        binding.stepView.done(false);
                        binding.stepView.go(position, true);
                        binding.formScroll.scrollTo(0, 0);
                        position = 4;
                        Utils.dismissProgress();
                        previousUnitInfoPointDataModel = unitInfoDataModel2;
                        structureInfoPointDataModel = (StructureInfoPointDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);
                        List<HohInfoDataModel> previousHohInfoDataModelList = new ArrayList<>();
                        if (isHohData && previousHohInfoDataModel == null && localSurveyDbViewModel.getHohInfoData(previousUnitInfoPointDataModel.getUnit_id()) != null) {
                            previousHohInfoDataModelList = localSurveyDbViewModel.getHohInfoData(previousUnitInfoPointDataModel.getUnit_id());
                            if (previousHohInfoDataModelList.size() > 0)
                                previousHohInfoDataModel = previousHohInfoDataModelList.get(0);
                        }
                        // getUnitStatus();
                        if((unitStructureUsage.equals(Constants.ReligiousCheckBox)) || (unitStructureUsage.equals(Constants.AmenitiesCheckBox))){
                            activity.startActivity(new Intent(activity, MemberActivity.class)
                                            .putExtra(Constants.IS_EDITING, true)
                                            .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                            .putExtra("flow", true)
                                            .putExtra(Constants.viewMode, "viewMode")
                                            .putExtra(Constants.INTENT_DATA_HohInfo, new HohInfoDataModel())
                                            .putExtra(INTENT_DATA_StructureInfo, structureInfoPointDataModel)
                                            .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel)
                                            .putExtra("isRelAmen", true)
                            );
                        }else{
                            if (previousHohInfoDataModel != null && (memberInfoDataModels == null || memberInfoDataModels.size() == 0)) {
                                activity.startActivity(new Intent(activity, HohActivity.class)
                                        .putExtra(Constants.IS_EDITING, editMode)
                                        .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                        .putExtra("flow", true)
                                        .putExtra(Constants.viewMode, "viewMode")
                                        .putExtra(Constants.INTENT_DATA_HohInfo, previousHohInfoDataModel)
                                        .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataModel)
                                        .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel));

                            } else if (previousHohInfoDataModel != null && memberInfoDataModels != null && memberInfoDataModels.size() > 0) {
                                activity.startActivity(new Intent(activity, HohActivity.class)
                                        .putExtra(Constants.IS_EDITING, editMode)
                                        .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                        .putExtra("flow", true)
                                        .putExtra(Constants.viewMode, "viewMode")
                                        .putExtra(Constants.INTENT_DATA_HohInfo, previousHohInfoDataModel)
                                        .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataModel)
                                        .putExtra(Constants.INTENT_DATA_MamberInfo, memberInfoDataModels)
                                        .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel));

                            } else {
                                activity.startActivity(new Intent(activity, HohActivity.class)
                                        .putExtra(Constants.IS_EDITING, editMode)
                                        .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
                                        .putExtra("flow", true)
                                        .putExtra(Constants.viewMode, "viewMode")
                                        .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataModel)
                                        .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel));
                            }
                        }
                    } else {
                        Utils.dismissProgress();
                        if (previousUnitInfoPointDataModel == null) {
                            previousUnitInfoPointDataModel = unitInfoDataModel2;
                        }
                        activity.startActivity(new Intent(activity, MapActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                .putExtra(Constants.INTENT_SelectedWorkArea, work_area_name));
                        ;
                        activity.finish();
//
//                            if (activity.getIntent().hasExtra(Constants.INTENT_DATA_HohInfo) && previousHohInfoDataModel != null){
//                                activity.startActivity(new Intent(activity, HohActivity.class)
//                                        .putExtra(Constants.IS_EDITING, editMode)
//                                        .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
//                                        .putExtra("flow", true)
//                                        .putExtra(Constants.viewMode, "viewMode")
//                                        .putExtra(Constants.INTENT_DATA_HohInfo, previousHohInfoDataModel)
//                                        .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel));
//
//                            }else {
//                                activity.startActivity(new Intent(activity, HohActivity.class)
//                                        .putExtra(Constants.IS_EDITING, editMode)
//                                        .putExtra(Constants.EDIT_TYPE, Constants.EDIT_UnitInfo)
//                                        .putExtra("flow", true)
//                                        .putExtra(Constants.viewMode, "viewMode")
//                                        .putExtra(Constants.INTENT_DATA_UnitInfo, previousUnitInfoPointDataModel));
//                            }

                    }
                    if(previousStructureInfoPointDataModel.getStructure_status().equalsIgnoreCase(Constants.Not_Started)){
                        localSurveyDbViewModel.updateStructureStatusDataTable(previousStructureInfoPointDataModel.getHut_number(),Constants.InProgress_statusLayer);
                    }
                }, 2000);


            }

//            }
//            else if (position == 4) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_status_dialog, null);
//                builder.setView(customLayout);
//                AlertDialog dialog = builder.create();
//
//                RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);
//                MaterialRadioButton radio_inProg = customLayout.findViewById(R.id.radio_inProg);
//                MaterialRadioButton radio_hold = customLayout.findViewById(R.id.radio_hold);
//                MaterialRadioButton radio_complete = customLayout.findViewById(R.id.radio_complete);
//
//                radio_inProg.setOnCheckedChangeListener((compoundButton, b) -> {
//                    if (b) {
//                        statusAlert = Constants.InProgress_statusLayer;
//                    }
//                });
//
//                radio_hold.setOnCheckedChangeListener((compoundButton, b) -> {
//                    if (b) {
//                        statusAlert = Constants.OnHold_statusLayer;
//                    }
//                });
//
//                radio_complete.setOnCheckedChangeListener((compoundButton, b) -> {
//                    if (b) {
//                        statusAlert = Constants.completed_statusLayer;
//                    }
//                });
//
//
//                LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
//                LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);
//
//                btn_yes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        if(unitInfoDataModelGlobal!=null){
//                            if(statusAlert.equals("")){
//                                statusAlert=Constants.InProgress_statusLayer;
//                            }
//                            unitInfoDataModelGlobal.setUnit_status(statusAlert);
//                            unitInfoDataModelHashMap.put(unitUniqueId, unitInfoDataModelGlobal);
//                            localSurveyDbViewModel.insertUnitInfoPointData(unitInfoDataModelHashMap.get(unitUniqueId), activity);
//                            localSurveyDbViewModel.updateStructureUnitIdStatusDataTable(unitUniqueId,statusAlert);
//                        }
//                        Utils.showProgress("Please wait...",activity);
//                        new Handler().postDelayed(() -> {
//                            String sts=localSurveyDbViewModel.getUnitInfoStatus(unitUniqueId);
//                            Utils.dismissProgress();
//                            if(sts.equals(Constants.completed_statusLayer)){
//                                showOTPPopup();
//                            }else{
//                                showFormSubmitDialog();
//                            }
//                        }, 2000);
//                    }
//                });
//
//                btn_no.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
//            }
        });

        binding.btnCancel.setOnClickListener(view -> {
            closeFormPopup();
        });

        binding.layoutUnitDetailInfo.residentScAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.ShareCertificateLabel, unit_relative_path, unitUniqueId + "_" + Constants.share_certificate_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.residentEcbAttachment.btnBrowse.setOnClickListener(view -> {

            showAttachmentAlertDialogButtonClicked(Constants.ElectricityConnectionBillLabel, unit_relative_path, unitUniqueId + "_" + Constants.electric_bill_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.residentPhotoPassAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.PhotoPassLabel, unit_relative_path, unitUniqueId + "_" + Constants.photo_pass_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.residentNaTaxAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.NA_TaxLabel, unit_relative_path, unitUniqueId + "_" + Constants.na_tax_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.residentPtprAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.PropertyTaxPaymentReceiptLabel, unit_relative_path, unitUniqueId + "_" + Constants.property_tax_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.residentErAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.ElectoralRollLabel, unit_relative_path, unitUniqueId + "_" + Constants.electrol_roll_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.additionalSccsasAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.additionalSccsasLabel, unit_relative_path, unitUniqueId + "_" + Constants.eductational_certificate_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.additionalCiesaAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.additionalCiesaLabel, unit_relative_path, unitUniqueId + "_" + Constants.employer_certificate_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.additionalAttachment3Attachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.additionalAttachment3Label, unit_relative_path, unitUniqueId + "_" + Constants.additionalDocument_attachment3_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.additionalAttachment4Attachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.additionalAttachment4Label, unit_relative_path, unitUniqueId + "_" + Constants.additionalDocument_attachment4_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.chainPsipcAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.chainPsipcLabel, unit_relative_path, unitUniqueId + "_" + Constants.ownership_proof_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.chainRaAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.chainRaLabel, unit_relative_path, unitUniqueId + "_" + Constants.rent_agreement_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.chainAttachment3Attachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.chainAttachment3Label, unit_relative_path, unitUniqueId + "_" + Constants.chainDocument_attachment3_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.chainAttachment4Attachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.chainAttachment4Label, unit_relative_path, unitUniqueId + "_" + Constants.chainDocument_attachment4_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.licenseProofSeAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.commercialShopEstLabel, unit_relative_path, unitUniqueId + "_" + Constants.shop_establishment_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.licenseProofRhlAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.commercialRestrHotelLabel, unit_relative_path, unitUniqueId + "_" + Constants.restaurant_hotel_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.licenseProofFdlAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.commercialFoodDrugLabel, unit_relative_path, unitUniqueId + "_" + Constants.food_drug_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.licenseProofFalAttachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.commercialFactoryActLabel, unit_relative_path, unitUniqueId + "_" + Constants.factory_act_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.religiousOtherA1Attachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.Attachment1Label, unit_relative_path, unitUniqueId + "_" + Constants.attachment1_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.religiousOtherA2Attachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.Attachment2Label, unit_relative_path, unitUniqueId + "_" + Constants.attachment2_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.religiousOtherA3Attachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.Attachment3Label, unit_relative_path, unitUniqueId + "_" + Constants.attachment3_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.religiousOtherA4Attachment.btnBrowse.setOnClickListener(view -> {
            showAttachmentAlertDialogButtonClicked(Constants.Attachment4Label, unit_relative_path, unitUniqueId + "_" + Constants.attachment4_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.btnAddMember.setOnClickListener(view -> {

            if (!validateUnitInfo()) return;

            cal = Utils.getGregorianCalendarFromDate(binding.layoutUnitDetailInfo.etExistenceSince.getText().toString());

            unitAlertStatus = Constants.InProgress_statusLayer;

            UnitInfoDataModel unitInfoDataModel1 = getUnitInfoDataModel();

            unitInfoDataModelHashMap.put(unitUniqueId, unitInfoDataModel1);
            localSurveyDbViewModel.insertUnitInfoPointData(unitInfoDataModelHashMap.get(unitUniqueId), activity);

            localSurveyDbViewModel.insertStructureUnitIdStatusData(new StructureUnitIdStatusDataTable(unitUniqueId,
                    structUniqueId, unitAlertStatus), activity);

//          localSurveyDbViewModel.deleteMediaInfoData(unitUniqueId, activity);

            Utils.updateProgressMsg("Loading..", activity);
            new Handler().postDelayed(() -> {

                if (mediaInfoDataModels1.size() > 0) {
                    List<MediaInfoDataModel> mediaInfoData = mediaInfoDataModels1;
                    localSurveyDbViewModel.insertAllMediaInfoPointData(mediaInfoData, activity);
                }

                Utils.dismissProgress();
            }, 2000);

            //setUpPreviousMemberList(unitUniqueId);

            binding.layoutUnitDetailInfo.llayoutAddNewmember.setVisibility(View.VISIBLE);

            binding.layoutUnitDetailInfo.txtMemberDetails.setVisibility(View.VISIBLE);
            binding.layoutUnitDetailInfo.btnAddMember.setVisibility(View.GONE);

            binding.layoutUnitDetailInfo.layoutAddMember.etMemberName.requestFocus();
//            Utils.showKeyboard(activity);

            binding.layoutUnitDetailInfo.expandableListView.setOnGroupClickListener((ExpandableListView expandableListView, View view1, int i, long l) -> {

//                Utils.shortToast(expandableListTitle.get(i).getHoh_name(), activity);

                this.editableHohInfoDataModel = expandableListTitle.get(i);  //HohInfoDataModel
                showActionAlertDialogButtonClicked("Confirm the action", "Do you want to edit HOH '" + expandableListTitle.get(i).getHoh_name() + "'this record?",
                        "Yes", "No", false, false, false, false, true, false);

                return false;
            });

        });


        binding.layoutUnitDetailInfo.layoutAddMember.photographAttachment.btnBrowse.setOnClickListener(view -> {
            if (checkMemberAttachmentValid())
                showAttachmentAlertDialogButtonClicked(Constants.MemberPhotograph, hohMember_relative_path, hohMemberUniqueId + "_" + Constants.photograph_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.btnBrowse.setOnClickListener(view -> {
            if (checkMemberAttachmentValid())
                YesNoBottomSheet.geInstance(activity, "Capture Aadhar Card", "Front", "Back", new YesNoBottomSheet.YesNoButton() {
                    @Override

                    public void yesBtn() {
                        showAttachmentAlertDialogButtonClicked(Constants.AadharCardAttachment, hohMember_relative_path, hohMemberUniqueId + "_" + Constants.AadharCardF_ttachment + "_" + Utils.getEpochDateStamp());
                    }

                    @Override

                    public void noBtn() {
                        showAttachmentAlertDialogButtonClicked(Constants.AadharCardAttachment, hohMember_relative_path, hohMemberUniqueId + "_" + Constants.AadharCardB_Attachment + "_" + Utils.getEpochDateStamp());

                    }
                }).show(((AppCompatActivity) activity).getSupportFragmentManager(), "");
        });

        binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.btnBrowse.setOnClickListener(view -> {
            if (checkMemberAttachmentValid())
                showAttachmentAlertDialogButtonClicked(Constants.PanCardttachment, hohMember_relative_path, hohMemberUniqueId + "_" + Constants.pan_card_attachment_name + "_" + Utils.getEpochDateStamp());
        });

        binding.layoutUnitDetailInfo.layoutAddMember.btnAddMemberSubmit.setOnClickListener(view -> {
            if (validateMemberInfo()) addToList();
        });

        binding.layoutUnitDetailInfo.layoutAddMember.btnAddMemberReset.setOnClickListener(view -> {
            if (editableHohInfoDataModel != null || editableMemberInfoDataModel != null) {
                Utils.shortToast("It is not possible to reset while editing previous details.", activity);
                return;
            }
        });

    }


//    private void showDeleteRecord() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_alert_action_red, null);
//        builder.setView(customLayout);
//        AlertDialog dialog = builder.create();
//        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
//        btn_yes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//
//            }
//        });
//        dialog.show();
//    }

    private void showWithoutOTPDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_without_otp_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                showFormSubmitDialog();
                showPanchnamaDialog();
            }
        });

        dialog.show();
    }

    private void showFormSubmitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_form_submit_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Utils.shortToast("Member Not Available.",activity);
                activity.startActivity(new Intent(activity, MapActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .putExtra(Constants.INTENT_SelectedWorkArea, work_area_name));
                activity.finish();
            }
        });

        dialog.show();
    }

    private boolean checkMemberAttachmentValid() {
        if (Utils.isNullOrEmpty(memberType)) {
            Utils.shortToast("Select member type to attach.", activity);
            return false;
        } else if (binding.layoutUnitDetailInfo.layoutAddMember.radioMemberTypeOther.isChecked() && Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompSelectHoh.getText().toString())) {
            Utils.shortToast("Select hoh to attach.", activity);
            return false;
        } else return true;
    }

    public void showAttachmentAlertDialogButtonClicked(String clickedFrom, String relative_path, String name) {

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
        if (clickedFrom.equals(Constants.UnitDistometerVideoType))
            txt_ChooseFile.setVisibility(View.GONE);

        txt_Camera.setOnClickListener(view1 -> {
            if (AppPermissions.cameraPermission(activity, true)) {
                if (clickedFrom.equals(Constants.UnitDistometerVideoType)) {

                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    // Create a File reference for future access
                    if (existingVideoFile.equals("")) {
                        captureVideoPath = getPhotoFileUri(unitVideoFileName + ".mp4");
                    }else {
                        captureVideoPath = getPhotoFileUri(existingVideoFile);
                    }
                    // wrap File object into a content provider
                    // required for API >= 24
                    // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
                    videoUri=  FileProvider.getUriForFile(Objects.requireNonNull(activity.getApplicationContext()),
                            BuildConfig.APPLICATION_ID + ".provider", captureVideoPath);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    intent.putExtra(MediaStore.Video.Thumbnails.HEIGHT, 320);
                    intent.putExtra(MediaStore.Video.Thumbnails.WIDTH, 240);
                    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,104857600L);
                    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                    // So as long as the result is not null, it's safe to use the intent.
                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivityForResult(intent, selectVideoCamera);
                        dialog.dismiss();
                    }
                } else{
                    //                imageUri = Utils.getCaptureImageOutputUri(activity, target_relative_path, target_name);
//                captureImagePath = Utils.getFile(activity, target_relative_path, target_name);
                    captureImagePath = getPhotoUri(target_name+".jpg",target_relative_path);
                    imageUri = FileProvider.getUriForFile(Objects.requireNonNull(activity.getApplicationContext()),
                            BuildConfig.APPLICATION_ID + ".provider", captureImagePath);
                if (imageUri != null) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    activity.startActivityForResult(cameraIntent, selectCamera);
                    dialog.dismiss();
                } else Utils.shortToast("Unable to capture image.", activity);
            }
            }

        });

        txt_Gallery.setOnClickListener(view1 -> {
            if (clickedFrom.equals(Constants.UnitDistometerVideoType)) {
//                Intent galleryIntent =new  Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//                activity.startActivityForResult(galleryIntent, GALLERYVIDEO);
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                activity.startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERYVIDEO);

                dialog.dismiss();
                dialog.dismiss();
            }
            else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            activity.startActivityForResult(Intent.createChooser(intent, "Select File"), selectGallery);

            dialog.dismiss();
            }
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

    }


    private boolean validateUnitInfo() {

        boolean a, b, c, d, e, f;

        a = resident_scAttachmentList.size() > 0;
        b = resident_ecbAttachmentList.size() > 0;
        c = resident_ppAttachmentList.size() > 0;
        d = resident_nataxAttachmentList.size() > 0;
        e = resident_ptprAttachmentList.size() > 0;
        f = resident_erAttachmentList.size() > 0;

        int residentProofAttachmentTotalCount = (a ? 1 : 0) + (b ? 1 : 0) + (c ? 1 : 0) + (d ? 1 : 0) + (e ? 1 : 0) + (f ? 1 : 0);

        boolean g, h, i, j;

        g = additionalSccsasAttachmentList.size() > 0;
        h = additionalCiesaAttachmentList.size() > 0;
        i = additionalAttachment3AttachmentList.size() > 0;
        j = additionalAttachment4AttachmentList.size() > 0;

        int additionalDocumentProofAttachmentTotalCount = (g ? 1 : 0) + (h ? 1 : 0) + (i ? 1 : 0) + (j ? 1 : 0);

        boolean k, l, m, n;

        k = chainPsipcAttachmentList.size() > 0;
        l = chainRaAttachmentList.size() > 0;
        m = chainAttachment3AttachmentList.size() > 0;
        n = chainAttachment4AttachmentList.size() > 0;

        int chainDocumentProofAttachmentTotalCount = (k ? 1 : 0) + (l ? 1 : 0) + (m ? 1 : 0) + (n ? 1 : 0);

        boolean o, p, q, r;

        o = licenseProofSeAttachmentList.size() > 0;
        p = licenseProofRhlAttachmentList.size() > 0;
        q = licenseProofFdlAttachmentList.size() > 0;
        r = licenseProofFalAttachmentList.size() > 0;

        int licenceProofAttachmentTotalCount = (o ? 1 : 0) + (p ? 1 : 0) + (q ? 1 : 0) + (r ? 1 : 0);

        boolean s, t, u, v;

        s = religiousOtherA1AttachmentList.size() > 0;
        t = religiousOtherA2AttachmentList.size() > 0;
        u = religiousOtherA3AttachmentList.size() > 0;
        v = religiousOtherA4AttachmentList.size() > 0;

        int religiousOtherProofAttachmentTotalCount = (s ? 1 : 0) + (t ? 1 : 0) + (u ? 1 : 0) + (v ? 1 : 0);
//validations
        if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.autoCompUnitFloorDetails.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.autoCompUnitFloorDetails, "Select unit details", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.etUnitNumber.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.etUnitNumber, "Enter unit number", activity);
            return false;
        } else if (Utils.isNullOrEmpty(isMemberAvailable)) {
            Utils.shortToast("Is member available.", activity);
            binding.layoutNewUnitDetails.txtMemberAvailable.requestFocus();
            return false;
        } else if (binding.layoutNewUnitDetails.radioMemberAvailableYes.isChecked()) {
            if (Utils.isNullOrEmpty(isUnit_RentOwner)) {
                binding.layoutUnitDetailInfo.txtUnitUsage.requestFocus();
                Utils.shortToast("Select unit status.", activity);
                return false;
            } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.autoCompNoOfMembers.getText().toString())) {
                Utils.setError(binding.layoutUnitDetailInfo.autoCompNoOfMembers, "Select no. of members", activity);
                return false;
            } else if (Utils.isNullOrEmpty(unitStructureUsage)) {
                binding.layoutUnitDetailInfo.txtUnitUsage.requestFocus();
                Utils.shortToast("Select " + binding.layoutUnitDetailInfo.txtUnitUsage.getText().toString(), activity);
                return false;
            } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.etAreaSqFt.getText().toString())) {
                if (binding.layoutUnitDetailInfo.radioUnitUsageRC.isChecked())
                    Utils.setError(binding.layoutUnitDetailInfo.etAreaSqFt, "Enter residential area", activity);
                else
                    Utils.setError(binding.layoutUnitDetailInfo.etAreaSqFt, "Enter unit area", activity);
                return false;
            } else if (Utils.doubleFormatter(binding.layoutUnitDetailInfo.etAreaSqFt.getText().toString()) < 1) {
                if (binding.layoutUnitDetailInfo.radioUnitUsageRC.isChecked())
                    Utils.setError(binding.layoutUnitDetailInfo.etAreaSqFt, "Enter valid residential area", activity);
                else
                    Utils.setError(binding.layoutUnitDetailInfo.etAreaSqFt, "Enter valid unit area", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageRC.isChecked() && Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.etAreaCommercialFt.getText().toString())) {
                Utils.setError(binding.layoutUnitDetailInfo.etAreaCommercialFt, "Enter commercial area", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageRC.isChecked() && Utils.doubleFormatter(binding.layoutUnitDetailInfo.etAreaSqFt.getText().toString()) < 1) {
                Utils.setError(binding.layoutUnitDetailInfo.etAreaSqFt, "Enter valid commercial area", activity);
                return false;
            } else if (Utils.isNullOrEmpty(binding.layoutNewUnitDetails.etExistenceSince.getText().toString())) {
                Utils.setError(binding.layoutNewUnitDetails.etExistenceSince, "Select existence since", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageResidential.isChecked() && residentProofAttachmentTotalCount < 2) {
                binding.layoutUnitDetailInfo.layResidentProof.requestFocus();
                Utils.shortToast("Attach minimum 2 resident proof attachment.", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageResidential.isChecked() && year > 2000 && additionalDocumentProofAttachmentTotalCount < 2) {
                binding.layoutUnitDetailInfo.layResidentProofAdditionalDocument.requestFocus();
                Utils.shortToast("Attach minimum 2 additional document attachment.", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageResidential.isChecked() && year > 2000 && chainDocumentProofAttachmentTotalCount < 1) {
                binding.layoutUnitDetailInfo.layResidentProofChainDocument.requestFocus();
                Utils.shortToast("Attach minimum 1 chain document attachment.", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageCommercial.isChecked() && residentProofAttachmentTotalCount < 2) {
                binding.layoutUnitDetailInfo.layResidentProof.requestFocus();
                Utils.shortToast("Attach minimum 2 resident proof attachment.", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageCommercial.isChecked() && licenceProofAttachmentTotalCount < 1) {
                binding.layoutUnitDetailInfo.layCommercialLicenseProof.requestFocus();
                Utils.shortToast("Attach minimum 1 licence proof attachment.", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageRC.isChecked() && residentProofAttachmentTotalCount < 2) {
                binding.layoutUnitDetailInfo.layResidentProof.requestFocus();
                Utils.shortToast("Attach minimum 2 resident proof attachment.", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageRC.isChecked() && licenceProofAttachmentTotalCount < 1) {
                binding.layoutUnitDetailInfo.layCommercialLicenseProof.requestFocus();
                Utils.shortToast("Attach minimum 1 licence proof attachment.", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked() && religiousOtherProofAttachmentTotalCount < 1) {
                binding.layoutUnitDetailInfo.txtHeaderReligiousOtherProof.requestFocus();
                Utils.shortToast("Attach minimum 1 religious proof attachment.", activity);
                return false;
            } else if (binding.layoutUnitDetailInfo.radioUnitUsageOthers.isChecked() && religiousOtherProofAttachmentTotalCount < 1) {
                binding.layoutUnitDetailInfo.txtHeaderReligiousOtherProof.requestFocus();
                Utils.shortToast("Attach minimum 1 attachment.", activity);
                return false;
            } else {
                if (residentProofAttachmentCount > 1) resident_proof_attached = true;
                if (residentProofAdditionalAttachmentCount > 1) additional_document_attached = true;
                if (residentProofChainAttachmentCount > 0) chain_document_attached = true;
                if (licenseProofAttachmentListCount > 0) license_proof_attached = true;
                if (religiousAttachmentCount > 0) religious_attachment_attached = true;
                if (otherAttachmentCount > 0) others_attachment_attached = true;
                return true;
            }

        } else if (binding.layoutUnitDetailInfo.radioMemberAvailableNo.isChecked()) {
            if (Utils.isNullOrEmpty(binding.layoutNewUnitDetails.autoCompRemarks.getText().toString())) {
                Utils.setError(binding.layoutNewUnitDetails.autoCompRemarks, "Enter remark", activity);
                return false;
            } else return true;
        } else {
            return false;
        }

    }

    private boolean validateMemberInfo() {

        if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.etMemberName.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.etMemberName, "Enter name", activity);
            return false;
        } else if (Utils.isNullOrEmpty(memberType)) {
            Utils.shortToast("Select member type.", activity);
            return false;
        } else if (binding.layoutUnitDetailInfo.layoutAddMember.radioMemberTypeOther.isChecked() && Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompSelectHoh.getText().toString())) {

            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoCompSelectHoh, "Select HOH", activity);
            return false;
        } else if (binding.layoutUnitDetailInfo.layoutAddMember.radioMemberTypeOther.isChecked() && Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoRelationshipWithHoh.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoRelationshipWithHoh, "Select Relationship With OHh", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompMaritalStatus.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoCompMaritalStatus, "Select marital status", activity);
            return false;
        } else if (!binding.layoutUnitDetailInfo.layoutAddMember.autoCompMaritalStatus.getText().toString().equals("Unmarried") &&
                Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.etSpouseName.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.etSpouseName, "Enter spouse name", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.etContactNumber.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.etContactNumber, "Enter contact number", activity);
            return false;
        } else if (!Utils.isValidPhoneNumber(binding.layoutUnitDetailInfo.layoutAddMember.etContactNumber.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.etContactNumber, "Enter valid contact number", activity);
            return false;
        } else if (binding.layoutUnitDetailInfo.layoutAddMember.etContactNumber.getText().toString().trim().length() != 10) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.etContactNumber, "Enter valid number", activity);
            return false;
        } else if (memberPhotographAttachmentList.size() == 0) {
            Utils.shortToast("Attach " + memberType + " photograph.", activity);
            binding.layoutUnitDetailInfo.layoutAddMember.photographAttachment.btnBrowse.requestFocus();
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.etMemberAge.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.etMemberAge, "Enter age", activity);
            return false;
        } else if (Utils.isNullOrEmpty(memberGender)) {
            Utils.shortToast("Select gender", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.etitText.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.etitText, "Enter Aadhar card number", activity);
            return false;
        } else if (binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.etitText.getText().toString().trim().length() != 12) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.etitText, "Enter valid aadhar card number (12 digits)", activity);
            return false;
        } else if (memberAdhaarCardAttachmentList.size() == 0) {
            Utils.shortToast("Attach adhaar card.", activity);
            binding.layoutUnitDetailInfo.layoutAddMember.adhaarCardNumberAttachment.btnBrowse.requestFocus();
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText.getText().toString().trim())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText, "Enter PAN card number", activity);
            return false;
        } else if (binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText.getText().toString().trim().length() != 10) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText, "Enter valid PAN card (10 digits)", activity);
            return false;
        } else if (!Utils.isValidPancard(binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText.getText().toString().trim().toUpperCase())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.etitText, "Enter valid PAN card number (wrong format)", activity);
            return false;
        } else if (memberPanCardAttachmentList.size() == 0) {
            Utils.shortToast("Attach PAN card.", activity);
            binding.layoutUnitDetailInfo.layoutAddMember.panCardNumberAttachment.btnBrowse.requestFocus();
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompReligion.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoCompReligion, "Select religion", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompOriginallyWhichState.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoCompOriginallyWhichState, "Select state", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompMotherTongue.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoCompMotherTongue, "Select mother tongue", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompEducationLevel.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoCompEducationLevel, "Select education level", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompOccupation.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoCompOccupation, "Select occupation", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompTypeOfWork.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoCompTypeOfWork, "Select type of work", activity);
            return false;
        } else if (Utils.isNullOrEmpty(binding.layoutUnitDetailInfo.layoutAddMember.autoCompPlaceOfWork.getText().toString())) {
            Utils.setError(binding.layoutUnitDetailInfo.layoutAddMember.autoCompPlaceOfWork, "Select place of work", activity);
            return false;
        } else if (Utils.isNullOrEmpty(memberRentOwnership)) {
            Utils.shortToast("Select rent or ownership", activity);
            return false;
        } else {
            return true;
        }
    }

    private void addToList() {

//        if (binding.layoutUnitDetailInfo.layoutAddMember.radioMemberTypeHoh.isChecked()) {
//            HohInfoDataModel hohInfoDataModel1 = getHohDataToLocal();
//
//            if (expandableListTitle.contains(editableHohInfoDataModel))
//                expandableListTitle.set(expandableListTitle.indexOf(editableHohInfoDataModel), hohInfoDataModel1);
//            else expandableListTitle.add(hohInfoDataModel1);
//
//            hohInfoDataModelHashMap.put(unitUniqueId, expandableListTitle);
//
//            if (memberPhotographAttachmentList.size() > 0) {
//                ArrayList<MediaInfoDataModel> memberMedia = getmediaInfoDataList(memberPhotographAttachmentList, hohInfoDataModel1.getRelative_path(), Constants.hoh_infoLayer,
//                        hohInfoDataModel1.getHoh_id(), hohInfoDataModel1.getRel_globalid());
//                if (memberMedia.size() > 0)
//                    hohMediaInfoDataModel.addAll(memberMedia);
//            }
//            if (memberPanCardAttachmentList.size() > 0) {
//                ArrayList<MediaInfoDataModel> memberMedia = getmediaInfoDataList(memberPanCardAttachmentList, hohInfoDataModel1.getRelative_path(), Constants.hoh_infoLayer,
//                        hohInfoDataModel1.getHoh_id(), hohInfoDataModel1.getRel_globalid());
//                if (memberMedia.size() > 0)
//                    hohMediaInfoDataModel.addAll(memberMedia);
//            }
//            if (memberAdhaarCardAttachmentList.size() > 0) {
//                ArrayList<MediaInfoDataModel> memberMedia = getmediaInfoDataList(memberAdhaarCardAttachmentList, hohInfoDataModel1.getRelative_path(), Constants.hoh_infoLayer,
//                        hohInfoDataModel1.getHoh_id(), hohInfoDataModel1.getRel_globalid());
//                if (memberMedia.size() > 0)
//                    hohMediaInfoDataModel.addAll(memberMedia);
//            }
//
//            ArrayList<MediaInfoDataModel> hohMediaInfoData = hohMediaInfoDataModel;
//            Utils.updateProgressMsg("Saving HOH info.", activity);
//            localSurveyDbViewModel.insertHohInfoPointData(hohInfoDataModel1, activity);
//            new Handler().postDelayed(() -> {
//
//                if (hohMediaInfoData.size() > 0) {
//                    localSurveyDbViewModel.insertAllMediaInfoPointData(hohMediaInfoData, activity);
//                }
//
//                Utils.dismissProgress();
//            }, 1000);
//
//        } else {
//            boolean isHohChanged = false;
//            MemberInfoDataModel memberInfoDataModel1 = getMemberDataToLocal();
//            ArrayList<MemberInfoDataModel> nameList = new ArrayList<>();
//
//            AppLog.e("FormPage Member expandableListDetail1: " + Utils.getGson().toJson(expandableListDetail));
//            AppLog.e("FormPage Member memberInfoDataModel1: " + Utils.getGson().toJson(memberInfoDataModel1));
//            AppLog.e("FormPage Member editableMemberInfoDataModel: " + Utils.getGson().toJson(editableMemberInfoDataModel));
//            AppLog.e("FormPage Member selectedDropDownHoh: " + Utils.getGson().toJson(selectedDropDownHoh));
//
//            if (editableMemberInfoDataModel != null && !editableMemberInfoDataModel.getRel_globalid().equalsIgnoreCase(selectedDropDownHoh.getHoh_id())) {
//                if (expandableListDetail.containsKey(editableMemberHohInfoDataModel.getHoh_id())) {
//                    nameList = expandableListDetail.get(editableMemberHohInfoDataModel.getHoh_id());
//                    AppLog.e("FormPage Member nameList1: " + Utils.getGson().toJson(nameList));
//                    if (nameList != null && nameList.contains(editableMemberInfoDataModel)) {
//                        isHohChanged = true;
//                        nameList.remove(editableMemberInfoDataModel);
//                        expandableListDetail.put(editableMemberHohInfoDataModel.getHoh_id(), nameList);
//                        AppLog.e("FormPage expandableListDetail2: " + Utils.getGson().toJson(expandableListDetail));
//                    }
//                }
//            }
//
//            if (expandableListDetail.containsKey(selectedDropDownHoh.getHoh_id())) {
//                nameList = expandableListDetail.get(selectedDropDownHoh.getHoh_id());
//            }
//
//            AppLog.e("FormPage Member nameList2: " + Utils.getGson().toJson(nameList));
//
//            if (nameList != null) {
//                if (nameList.contains(editableMemberInfoDataModel))
//                    nameList.set(nameList.indexOf(editableMemberInfoDataModel), memberInfoDataModel1);
//                else {
//                    nameList.add(memberInfoDataModel1);
//                }
//            }
//
//            AppLog.e("FormPage Member nameList3: " + Utils.getGson().toJson(nameList));
//
//            memberInfoDataModels.add(memberInfoDataModel1);
//            expandableListDetail.put(selectedDropDownHoh.getHoh_id(), nameList);
//
//            AppLog.e("FormPage expandableListDetail3: " + Utils.getGson().toJson(expandableListDetail));
//
//            if (memberPhotographAttachmentList.size() > 0) {
//                ArrayList<MediaInfoDataModel> memberMedia = getmediaInfoDataList(memberPhotographAttachmentList, memberInfoDataModel1.getRelative_path(), Constants.member_infoLayer,
//                        memberInfoDataModel1.getMember_id(), memberInfoDataModel1.getRel_globalid());
//                if (memberMedia.size() > 0)
//                    memberMediaInfoDataModel.addAll(memberMedia);
//            }
//            if (memberPanCardAttachmentList.size() > 0) {
//                ArrayList<MediaInfoDataModel> memberMedia = getmediaInfoDataList(memberPanCardAttachmentList, memberInfoDataModel1.getRelative_path(), Constants.member_infoLayer,
//                        memberInfoDataModel1.getMember_id(), memberInfoDataModel1.getRel_globalid());
//                if (memberMedia.size() > 0)
//                    memberMediaInfoDataModel.addAll(memberMedia);
//            }
//            if (memberAdhaarCardAttachmentList.size() > 0) {
//
//                ArrayList<MediaInfoDataModel> memberMedia = getmediaInfoDataList(memberAdhaarCardAttachmentList, memberInfoDataModel1.getRelative_path(), Constants.member_infoLayer,
//                        memberInfoDataModel1.getMember_id(), memberInfoDataModel1.getRel_globalid());
//                if (memberMedia.size() > 0)
//                    memberMediaInfoDataModel.addAll(memberMedia);
//            }
//
//            ArrayList<MediaInfoDataModel> mediaInfoData = memberMediaInfoDataModel;
//
//            Utils.updateProgressMsg("Saving Member info.", activity);
//
//            localSurveyDbViewModel.insertMemberInfoPointData(memberInfoDataModel1, activity);
//            boolean finalIsHohChanged = isHohChanged;
//            new Handler().postDelayed(() -> {
//
//                if (mediaInfoData.size() > 0) {
//                    localSurveyDbViewModel.insertAllMediaInfoPointData(mediaInfoData, activity);
//                }
//
//                if (finalIsHohChanged) {
//                    localSurveyDbViewModel.updateMediaHohChangedInfo(hohMember_relative_path, memberUniqueId);
//                }
//
//                Utils.dismissProgress();
//            }, 1000);
//
//        }
//
//        //binding.layoutUnitDetailInfo.layoutAddMember.autoCompSelectHoh.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, expandableListTitle));
//
//        customExpandableListAdapter = new CustomExpandableListAdapter(activity, expandableListTitle, expandableListDetail);
//        expandableListAdapter = customExpandableListAdapter;
//        binding.layoutUnitDetailInfo.expandableListView.setAdapter(expandableListAdapter);
//
//        AppLog.e("FormPage Member expandableListTitle: " + Utils.getGson().toJson(expandableListTitle));
//        AppLog.e("FormPage Member expandableListDetail4: " + Utils.getGson().toJson(expandableListDetail));
//
//        for (int i = 0; i < expandableListTitle.size(); i++) {
//            binding.layoutUnitDetailInfo.expandableListView.expandGroup(i);
//        }
//
//        Utils.shortToast("Member added.", activity);
//        binding.layoutUnitDetailInfo.expandableLay.setVisibility(View.VISIBLE);
//
//        resetMemberDetails();
//        editableHohInfoDataModel = null;
//        editableMemberInfoDataModel = null;
//        editableMemberHohInfoDataModel = null;
////check here expand2 //remove expandable lsit
    }

    private void uncheckAllStructureUsageRadio() {
        hideAttachmentLay();
        binding.layoutUnitDetailInfo.layCommercialArea.setVisibility(View.GONE);
        binding.layoutUnitDetailInfo.txtAreaSqFt.setText(activity.getResources().getString(R.string.area_sq_ft));
        unitStructureUsage = "";
        binding.layoutNewUnitDetails.radioUnitUsageResidential.setChecked(false);
        binding.layoutNewUnitDetails.radioUnitUsageCommercial.setChecked(false);
        binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setChecked(false);
        binding.layoutNewUnitDetails.radioUnitUsageRC.setChecked(false);
        binding.layoutNewUnitDetails.radioUnitUsageReligious.setChecked(false);
        binding.layoutNewUnitDetails.radioUnitUsageOthers.setChecked(false);

        residentProofAttachmentCount = 0;
        residentProofAdditionalAttachmentCount = 0;
        residentProofChainAttachmentCount = 0;
        licenseProofAttachmentListCount = 0;
        religiousAttachmentCount = 0;
        otherAttachmentCount = 0;


    }

    public void showActionAlertDialogButtonClicked(String header, String mssage, String btnYes, String btnNo, boolean isRadiogroup, boolean isUnitStatus,
                                                   boolean isAddNewUnit, boolean isStructureStatus,
                                                   boolean isHohNameClick, boolean isMemberNameClick) {

        Boolean isAllUnitCompleted = true;
        statusAlert = "";
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

                if (isUnitStatus) {
                    unitAlertStatus = statusAlert;
                    if (unitStatusArray == null)
                        unitStatusArray = new ArrayList<>();
                    unitStatusArray.add(unitAlertStatus);
                } else if (isStructureStatus) {
                    strutureAlertStatus = statusAlert;
                }

            }

            if (isUnitStatus) {
                if (previousUnitInfoPointDataModel.getObejctId() != null && !previousUnitInfoPointDataModel.getObejctId().equals(""))
                    isUnitUploaded = true;
                else
                    isUnitUploaded = false;

                Utils.updateProgressMsg("Saving unit info.", activity);
                saveUnitDataToLocal();

            } else if (isAddNewUnit) {

                isMemberAvailable = "";
                unitStructureUsage = "";
                memberType = "";

                memberGender = "";
                memberRentOwnership = "";

                previousUnitInfoPointDataModel = null;
                previousObjectIdUnit = "";
                previousGlobalIdUnit = "";
                isUnitUploaded = false;

                resetUnitDetails();
                setUpExpandAble();
                binding.layoutUnitDetailInfo.expandableLay.setVisibility(View.GONE);
                binding.layoutUnitDetailInfo.llayoutAddNewmember.setVisibility(View.GONE);

                binding.layoutUnitDetailInfo.txtMemberDetails.setVisibility(View.INVISIBLE);
                binding.layoutUnitDetailInfo.btnAddMember.setVisibility(View.VISIBLE);

                binding.layoutUnitDetailInfo.llayoutUnitDetailRemarks.setVisibility(View.GONE);

            } else if (isStructureStatus) {

                Utils.updateProgressMsg("Saving structure info.", activity);
//                saveStructureDataToLocal(strutureAlertStatus);
                showActionAlertDialogButtonClicked("Confirm the action", "Do you want to save this record?",
                        "Yes", "No", false, false, false, false, false, false);
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

    // IMP
    private void saveUnitDataToLocal() {

        UnitInfoDataModel unitInfoDataModel1 = getUnitInfoDataModel();

        unitInfoDataModelHashMap.put(unitUniqueId, unitInfoDataModel1);

        localSurveyDbViewModel.insertStructureUnitIdStatusData(new StructureUnitIdStatusDataTable(unitUniqueId,
                structUniqueId, unitAlertStatus), activity);

//      localSurveyDbViewModel.deleteMediaInfoData(unitUniqueId, activity);

        new Handler().postDelayed(() -> {

            localSurveyDbViewModel.insertUnitInfoPointData(unitInfoDataModelHashMap.get(unitUniqueId), activity);

            if (mediaInfoDataModels1.size() > 0) {
                Utils.updateProgressMsg("Saving Unit Media info.", activity);

                List<MediaInfoDataModel> mediaInfoData = mediaInfoDataModels1;

                new Handler().postDelayed(() -> {
                    localSurveyDbViewModel.insertAllMediaInfoPointData(mediaInfoData, activity);

                    if (expandableListTitle.size() > 0) {
                        Utils.updateProgressMsg("Saving HOH info.", activity);

                        new Handler().postDelayed(() -> {
                            List<HohInfoDataModel> hohInfoDataModels1 = expandableListTitle;
                            localSurveyDbViewModel.insertAllHohInfoPointData(hohInfoDataModels1, activity);

                            Utils.updateProgressMsg("Saving HOH Media info.", activity);

                            List<MediaInfoDataModel> hohMediaInfoDataModel1 = hohMediaInfoDataModel;
//                            for (MediaInfoDataModel infoDataModel : hohMediaInfoDataModel1) {
//                                localSurveyDbViewModel.deleteMediaInfoData(infoDataModel.getParent_unique_id(), activity);
//                            }

                            new Handler().postDelayed(() -> {

                                localSurveyDbViewModel.insertAllMediaInfoPointData(hohMediaInfoDataModel1, activity);

                                if (memberInfoDataModels.size() > 0) {

                                    Utils.updateProgressMsg("Saving member info.", activity);
                                    new Handler().postDelayed(() -> {

                                        List<MemberInfoDataModel> memberInfoDataModels1 = memberInfoDataModels;
                                        localSurveyDbViewModel.insertAllMemberInfoPointData(memberInfoDataModels1, activity);

                                        Utils.updateProgressMsg("Saving member Media info.", activity);

                                        List<MediaInfoDataModel> memberMediaInfoDataModel1 = memberMediaInfoDataModel;
//                                        for (MediaInfoDataModel infoDataModel : memberMediaInfoDataModel1) {
//                                            localSurveyDbViewModel.deleteMediaInfoData(infoDataModel.getParent_unique_id(), activity);
//                                        }
                                        new Handler().postDelayed(() -> {
                                            localSurveyDbViewModel.insertAllMediaInfoPointData(memberMediaInfoDataModel1, activity);
                                            Utils.dismissProgress();
                                            Utils.shortToast("Details saved.", activity);
                                            showActionAlertDialogButtonClicked("Confirm the action", "Do you want to add another unit?", "Yes", "No", false, false, true, false, false, false);

                                        }, 5000);

                                    }, 2000);
                                } else {
                                    Utils.dismissProgress();
                                    Utils.shortToast("Details saved.", activity);
                                    showActionAlertDialogButtonClicked("Confirm the action", "Do you want to add another unit?", "Yes", "No", false, false, true, false, false, false);
                                }

                            }, 5000);

                        }, 2000);

                    } else Utils.dismissProgress();

                }, 2000);
            } else {
                Utils.dismissProgress();
                Utils.shortToast("Details saved.", activity);
                showActionAlertDialogButtonClicked("Confirm the action", "Do you want to add another unit?", "Yes", "No", false, false, true, false, false, false);
            }
        }, 2000);
    }

    /*
     * Adding new function to insert data in UnitiInfoData Table with limited fields
     * @author : Jaid
     */
    private UnitInfoDataModel insetUnitInfoDataModel() {

        double residential_area = 0.0, commercial_area = 0.0, rc_residential_area = 0.0, rc_commercial_area = 0.0, religious_area = 0.0, other_area = 0.0;
        if (binding.layoutUnitDetailInfo.radioUnitUsageResidential.isChecked())
            residential_area = Utils.doubleFormatter(binding.layoutUnitDetailInfo.etAreaSqFt.getText().toString());
        else if (binding.layoutUnitDetailInfo.radioUnitUsageCommercial.isChecked())
            commercial_area = Utils.doubleFormatter(binding.layoutUnitDetailInfo.etAreaSqFt.getText().toString());
        else if (binding.layoutUnitDetailInfo.radioUnitUsageRC.isChecked()) {
            rc_residential_area = Utils.doubleFormatter(binding.layoutUnitDetailInfo.etAreaSqFt.getText().toString());
            rc_commercial_area = Utils.doubleFormatter(binding.layoutUnitDetailInfo.etAreaCommercialFt.getText().toString());
        } else if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
            religious_area = Utils.doubleFormatter(binding.layoutUnitDetailInfo.etAreaSqFt.getText().toString());
        else if (binding.layoutUnitDetailInfo.radioUnitUsageOthers.isChecked())
            other_area = Utils.doubleFormatter(binding.layoutUnitDetailInfo.etAreaSqFt.getText().toString());

        //if (binding.layoutUnitDetailInfo.radioMemberAvailableYes.isChecked()) {
//        unitInfoDataModel2 = new UnitInfoDataModel(surveyor_name, binding.layoutSurveyorDetailsLayout.etSurveyorDesignation.getText().toString(), binding.layoutSurveyorDetailsLayout.autoCompNameDRP.getText().toString(), binding.layoutSurveyorDetailsLayout.etDrpDesignation.getText().toString(), new Date(), binding.layoutSurveyorDetailsLayout.etSurveyTime.getText().toString());
        //}
        return unitInfoDataModel2;

    }


    //Existing method
    private UnitInfoDataModel getUnitInfoDataModel() {
        double resRcArea=0.0,commRcArea=0.0;
        if (unitStructureUsage.equals(Constants.RcCheckBox)) {
            if(!binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString().equalsIgnoreCase("")){
                resRcArea= Double.valueOf(binding.layoutNewUnitDetails.etRcResAreaLayout.getText().toString());
            }
            if(!binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString().equalsIgnoreCase("")){
                commRcArea= Double.valueOf(binding.layoutNewUnitDetails.etRcCommAreaLayout.getText().toString());
            }
        }

        int visitCountCalculate = Utils.integerFormatter(binding.layoutNewUnitDetails.etCount.getText().toString());
        short lockFlag = 0;
        if (previousUnitInfoPointDataModel != null) {
//            lockFlag=previousUnitInfoPointDataModel.getForm_lock();
        }


        String unitUpdatedStatus = Constants.InProgress_statusLayer;
        String relationshipHoh = "";
        String relationshipHohOther = "";
        String tenementNo = "";
        String tenementDoc = "";
        String mashalNo = "";
        String unit_status = "";
        String unit_gomasta = "";
        String unitArea = "";
        String unitLoftArea = "";
        String unitExistance = "";
        String unitNoEmployee = "";
        String exisYear = "";
        String nagarName = "";
        String nagarNameOther = "";
        String otherTxt = "";
//        String extSince = "";
        String yearTemp = "";
        String pavtiNo = "";

        yearTemp= String.valueOf(year);

        if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
            unitArea = binding.layoutNewUnitDetails.etUnitArea.getText().toString();
            unitExistance=binding.layoutNewUnitDetails.etYearOfStructure.getText().toString();
        }
        if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
            unitArea = binding.layoutNewUnitDetails.etUnitArea.getText().toString();
            unitExistance=binding.layoutNewUnitDetails.etYearOfStructure.getText().toString();
        }

        if (binding.layoutNewUnitDetails.etShowAge != null && !binding.layoutNewUnitDetails.etShowAge.getText().toString().equals("")) {
            int ag = Integer.parseInt(binding.layoutNewUnitDetails.etShowAge.getText().toString());
            if (ag < 18) {
                unitUpdatedStatus = Constants.OnHold_statusLayer;
            }
        }

        nagarName = binding.layoutNewUnitDetails.autoCompNagar.getText().toString();

        if (nagarName.equals(Constants.dropdown_other)) {
            nagarNameOther = binding.layoutNewUnitDetails.etNameNagarOther.getText().toString();
        }

        if (unitStructureUsage.equals(Constants.ResidentialCheckBox)) {
            if (ageFlag >= 18) {
                relationshipHoh = binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getTag().toString();

                if(relationshipHoh.equalsIgnoreCase(Constants.dropdown_others)) {
                    relationshipHohOther = binding.layoutNewUnitDetails.etRespondentRelationHoh.getText().toString();
                }
            } else {
                relationshipHoh = binding.layoutNewUnitDetails.autoCompRespondentRelation.getTag().toString();

                if(relationshipHoh.equalsIgnoreCase(Constants.dropdown_others)) {
                    relationshipHohOther = binding.layoutNewUnitDetails.etRespondentRelation.getText().toString();
                }
            }
            tenementNo = binding.layoutNewUnitDetails.etTenementNo.getText().toString();
            if(tenementNo==""){
                tenementDoc="";
                binding.layoutNewUnitDetails.autoCompDocTenement.setTag("");
            }else if(binding.layoutNewUnitDetails.autoCompDocTenement.getText().toString()==""){
                    tenementDoc="";
                    binding.layoutNewUnitDetails.autoCompDocTenement.setTag("");
            }else {
//                if(binding.layoutNewUnitDetails.autoCompDocTenementComm.getTag()==null){
//                    tenementDoc="";
//                }else{
                binding.layoutNewUnitDetails.autoCompDocTenement.setTag(Utils.getTextByTag(Constants.domain_tenement_doc_type,binding.layoutNewUnitDetails.autoCompDocTenement.getText().toString()));
                    tenementDoc = binding.layoutNewUnitDetails.autoCompDocTenement.getTag().toString();
//                }
            }


            mashalNo = binding.layoutNewUnitDetails.etMashal.getText().toString();
            unit_status = isUnit_RentOwner;
            unit_gomasta = binding.layoutNewUnitDetails.etUnitGomasta.getText().toString();
            unitArea = binding.layoutNewUnitDetails.etUnitArea.getText().toString();
            unitLoftArea = binding.layoutNewUnitDetails.etLoftArea.getText().toString();
            if(binding.layoutNewUnitDetails.etYearOfStructure.getTag()!=null){
                unitExistance = binding.layoutNewUnitDetails.etYearOfStructure.getTag().toString();
            }else{
                unitExistance = "";
            }
            unitNoEmployee = "";
//            extSince = binding.layoutNewUnitDetails.etExistenceSince.getText().toString();
            pavtiNo = binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavtiNum.getText().toString();
        } else {
            if (binding.layoutNewUnitDetails.radioMemberAvailableYes.isChecked()) {
                // relationshipHoh=binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString();
                if (ageFlag >= 18) {
                    if(binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getTag()==null){
                        binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setTag("");
                    }
                    relationshipHoh = binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getTag().toString();

                    if(relationshipHoh.equalsIgnoreCase(Constants.dropdown_others)) {
                        relationshipHohOther = binding.layoutNewUnitDetails.etRelationOwnerEmpComm.getText().toString();
                    }
                } else {
                    if(binding.layoutNewUnitDetails.autoCompRespondentRelation.getTag()==null){
                        binding.layoutNewUnitDetails.autoCompRespondentRelation.setTag("");
                    }
                    relationshipHoh = binding.layoutNewUnitDetails.autoCompRespondentRelation.getTag().toString();

                    if(relationshipHoh.equalsIgnoreCase(Constants.dropdown_others)) {
                        relationshipHohOther = binding.layoutNewUnitDetails.etRespondentRelation.getText().toString();
                    }
                }

                tenementNo = binding.layoutNewUnitDetails.etTenementNoComm.getText().toString();
                if(tenementNo==""){
                    tenementDoc="";
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setTag("");
                }else if(binding.layoutNewUnitDetails.autoCompDocTenementComm.getText().toString()==""){
                    tenementDoc="";
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setTag("");
                }else {
//                    if(binding.layoutNewUnitDetails.autoCompDocTenementComm.getText().toString()==""){
//                        tenementDoc="";
//                    }else{
//                        if(binding.layoutNewUnitDetails.autoCompDocTenementComm.getTag()==null){
//                            tenementDoc="";
//                        }else{
                            binding.layoutNewUnitDetails.autoCompDocTenementComm.setTag(Utils.getTextByTag(Constants.domain_tenement_doc_type,binding.layoutNewUnitDetails.autoCompDocTenementComm.getText().toString()));
                            tenementDoc = binding.layoutNewUnitDetails.autoCompDocTenementComm.getTag().toString();
//                        }
//                    }

                }
                //tenementDoc = binding.layoutNewUnitDetails.autoCompDocTenementComm.getTag().toString();
                mashalNo = binding.layoutNewUnitDetails.etMashalComm.getText().toString();
                unit_status = isUnit_RentOwner;
                unit_gomasta = binding.layoutNewUnitDetails.etUnitGomasta.getText().toString();
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){

                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){

                }else{
                    unitArea = binding.layoutNewUnitDetails.etUnitAreaComm.getText().toString();
                    if(binding.layoutNewUnitDetails.etYearOfStructureComm.getTag()!=null){
                        unitExistance = binding.layoutNewUnitDetails.etYearOfStructureComm.getTag().toString();
                    }
                }
                unitLoftArea = binding.layoutNewUnitDetails.etLoftAreaComm.getText().toString();
                unitNoEmployee = binding.layoutNewUnitDetails.etNoOfEmployee.getText().toString();
                otherTxt = binding.layoutNewUnitDetails.etOtherUsage.getText().toString();
//                extSince = binding.layoutNewUnitDetails.etExistenceSinceComm.getText().toString();
                if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                    pavtiNo = binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.getText().toString();
                }else{
                    pavtiNo = binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.getText().toString();
                }
            } else if (!binding.layoutNewUnitDetails.radioMemberAvailableYes.isChecked() && previousUnitInfoPointDataModel != null && previousUnitInfoPointDataModel.isMember_available() && previousUnitInfoPointDataModel.getRespondent_dob() != null && !previousUnitInfoPointDataModel.getRespondent_dob().equals("")) {
                if (ageFlag >= 18) {
                    relationshipHoh = binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getTag().toString();

                    if(relationshipHoh.equalsIgnoreCase(Constants.dropdown_others)) {
                        relationshipHohOther = binding.layoutNewUnitDetails.etRelationOwnerEmpComm.getText().toString();
                    }
                } else {
                    relationshipHoh = binding.layoutNewUnitDetails.autoCompRespondentRelation.getTag().toString();

                    if(relationshipHoh.equalsIgnoreCase(Constants.dropdown_others)) {
                        relationshipHohOther = binding.layoutNewUnitDetails.etRespondentRelation.getText().toString();
                    }
                }
                tenementNo = binding.layoutNewUnitDetails.etTenementNoComm.getText().toString();
                if(tenementNo==""){
                    tenementDoc="";
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setTag("");
                }else if(binding.layoutNewUnitDetails.autoCompDocTenementComm.getText().toString()==""){
                    tenementDoc="";
                    binding.layoutNewUnitDetails.autoCompDocTenementComm.setTag("");
                }else {
//                    if(binding.layoutNewUnitDetails.autoCompDocTenementComm.getTag()==null){
//                        tenementDoc="";
//                    }else{
                        binding.layoutNewUnitDetails.autoCompDocTenementComm.setTag(Utils.getTextByTag(Constants.domain_tenement_doc_type,binding.layoutNewUnitDetails.autoCompDocTenementComm.getText().toString()));

                        tenementDoc = binding.layoutNewUnitDetails.autoCompDocTenementComm.getTag().toString();
//                    }
                }
//                tenementDoc = binding.layoutNewUnitDetails.autoCompDocTenementComm.getTag().toString();
                mashalNo = binding.layoutNewUnitDetails.etMashalComm.getText().toString();
                unit_status = isUnit_RentOwner;
                unit_gomasta = binding.layoutNewUnitDetails.etUnitGomasta.getText().toString();
                unitArea = binding.layoutNewUnitDetails.etUnitAreaComm.getText().toString();
                unitLoftArea = binding.layoutNewUnitDetails.etLoftAreaComm.getText().toString();
                if(binding.layoutNewUnitDetails.etYearOfStructureComm.getTag()!=null){
                    unitExistance = binding.layoutNewUnitDetails.etYearOfStructureComm.getTag().toString();
                }
                unitNoEmployee = binding.layoutNewUnitDetails.etNoOfEmployee.getText().toString();
                otherTxt = binding.layoutNewUnitDetails.etOtherUsage.getText().toString();
//                extSince = binding.layoutNewUnitDetails.etExistenceSinceComm.getText().toString();
                if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                    pavtiNo = binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavtiNum.getText().toString();
                }else{
                    pavtiNo = binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavtiNum.getText().toString();
                }

            }
        }
        unitVisitCount = Utils.integerFormatter(binding.layoutNewUnitDetails.etCount.getText().toString());


        if (previousUnitInfoPointDataModel != null && (previousUnitInfoPointDataModel.getObejctId() == null || previousUnitInfoPointDataModel.getObejctId().equals(""))) {

        } else {
            long day = 0;
            if (previousUnitInfoPointDataModel != null && previousUnitInfoPointDataModel.getVisit_date() != null && !previousUnitInfoPointDataModel.getVisit_date().equals("")) {

                String ss = formattedDate(previousUnitInfoPointDataModel.getVisit_date());
                DateTimeFormatter f = new DateTimeFormatterBuilder().parseCaseInsensitive()
                        .append(DateTimeFormatter.ofPattern("yyyy-MMM-dd")).toFormatter();
                LocalDate datetime = LocalDate.parse(ss, f);

                LocalDate localDate1 = datetime;
                LocalDate localDate2 = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                day = ChronoUnit.DAYS.between(localDate1, localDate2);
            }
            if (day > 0) {
                unitVisitCount = unitVisitCount + 1;
            }
            if (unitVisitCount == 4) {
                visitCountCalculate = 4;
            }else if (unitVisitCount > 4) {
                visitCountCalculate = unitVisitCount;
            } else {
                visitCountCalculate = unitVisitCount;
            }
        }


        mediaInfoDataModels1 = new ArrayList<>();
        double residential_area = 0.0, commercial_area = 0.0, rc_residential_area = 0.0, rc_commercial_area = 0.0, religious_area = 0.0, other_area = 0.0;
        if (binding.layoutNewUnitDetails.radioUnitUsageResidential.isChecked())
            residential_area = Utils.doubleFormatter(binding.layoutNewUnitDetails.etUnitArea.getText().toString());
        else if (binding.layoutNewUnitDetails.radioUnitUsageCommercial.isChecked())
            commercial_area = Utils.doubleFormatter(binding.layoutNewUnitDetails.etUnitAreaComm.getText().toString());
        else if (binding.layoutNewUnitDetails.radioUnitUsageRC.isChecked()) {
            rc_residential_area = Utils.doubleFormatter(binding.layoutNewUnitDetails.etUnitArea.getText().toString());
            rc_commercial_area = Utils.doubleFormatter(binding.layoutNewUnitDetails.etUnitAreaComm.getText().toString());
        } else if (binding.layoutNewUnitDetails.radioUnitUsageReligious.isChecked())
            religious_area = Utils.doubleFormatter(binding.layoutNewUnitDetails.etUnitAreaComm.getText().toString());
        else if (binding.layoutNewUnitDetails.radioUnitUsageOthers.isChecked())
            other_area = Utils.doubleFormatter(binding.layoutNewUnitDetails.etUnitAreaComm.getText().toString());
//insert
        String modif = binding.layoutNewUnitDetails.etUniqueNo.getText().toString();
        App.getSharedPreferencesHandler().putString("unit_unique_id",modif);
        calculateFloor(modif);
//        if (binding.layoutNewUnitDetails.etUniqueNo.getText().toString().contains("DRP")) {
//            modif = binding.layoutNewUnitDetails.etUniqueNo.getText().toString();
//        } else {
//            try {
//                modif = binding.layoutNewUnitDetails.inputEditOne.getPrefixText().toString() + "" + binding.layoutNewUnitDetails.etUniqueNo.getText().toString();
//            } catch (Exception ex) {
//                modif = "";
//                ex.getCause();
//            }
//        }

        // vidnyan , adding type of other usage text to system, if age below 18
        if(binding.layoutNewUnitDetails.radioUnitUsageOthers.isChecked() && ageFlag<18){
            otherTxt = binding.layoutNewUnitDetails.etOtherUsageBelow.getText().toString();

        }

        nameTrustees=binding.layoutNewUnitDetails.etFirstTrustee.getText().toString();
        try {
            LinearLayout root = (LinearLayout) activity.findViewById(R.id.trusteeRootLayout);
            if(root!=null && root.getChildCount()>0){
                for(int index = 0; index < ((ViewGroup) root).getChildCount(); index++) {
                    View nextChild = ((ViewGroup) root).getChildAt(index);
                    EditText ed=nextChild.findViewById(R.id.trustNameEditTexts);
                    if(ed!=null && !ed.getText().toString().trim().equals("")){
                        nameTrustees=nameTrustees+","+ed.getText().toString();
                    }
                }
            }
        }catch (Exception ex){
            ex.getMessage();
        }


        nameBelong=binding.layoutNewUnitDetails.etFirstBelongs.getText().toString();
        try {
            LinearLayout root = (LinearLayout) activity.findViewById(R.id.trustRootLayout);
            if(root!=null && root.getChildCount()>0){
                for(int index = 0; index < ((ViewGroup) root).getChildCount(); index++) {
                    View nextChild = ((ViewGroup) root).getChildAt(index);
                    EditText ed=nextChild.findViewById(R.id.trustNameEditTexts);
                    if(ed!=null && !ed.getText().toString().trim().equals("")){
                        nameBelong=nameBelong+","+ed.getText().toString();
                    }
                }
            }
        }catch (Exception ex){
            ex.getMessage();
        }
        /*
        Komal code commented as per discussion with @Komal
         */
//        if (!binding.layoutNewUnitDetails.etUnitArea.getText().toString().equals("")||!binding.layoutNewUnitDetails.etUnitArea.getText().toString().equals("0.0")) {
//            unitArea = binding.layoutNewUnitDetails.etUnitArea.getText().toString();
//        } else{
//            unitArea = binding.layoutNewUnitDetails.etUnitAreaComm.getText().toString();
//        }

//        if (!binding.layoutNewUnitDetails.etUnitArea.getText().toString().equals("") && !binding.layoutNewUnitDetails.etUnitArea.getText().toString().equals("0.0")) {
//            unitArea = binding.layoutNewUnitDetails.etUnitArea.getText().toString();
//        } else{
//            unitArea = binding.layoutNewUnitDetails.etUnitAreaComm.getText().toString();
//        }
        if (binding.layoutNewUnitDetails.radioMemberAvailableYes.isChecked()) {
            String rmk = "";
            String remarksChange = "";
            if (!binding.layoutNewUnitDetails.etShowAge.getText().equals("") && Integer.parseInt(binding.layoutNewUnitDetails.etShowAge.getText().toString()) < 18) {
                rmk = binding.layoutNewUnitDetails.etRemarksAge.getText().toString();
                remarksChange = binding.layoutNewUnitDetails.etRemarksAge.getText().toString();
            }else{
                if(binding.layoutNewUnitDetails.autoCompRemarks.getTag()==null){
                    binding.layoutNewUnitDetails.autoCompRemarks.setTag("");
                }
                remarksChange=binding.layoutNewUnitDetails.autoCompRemarks.getTag().toString();
            }
            if(binding.layoutNewUnitDetails.autoCompRemarks.getTag()==null){
                binding.layoutNewUnitDetails.autoCompRemarks.setTag("");
            }

            unitInfoDataModel = new UnitInfoDataModel(unitUniqueId, modif.toUpperCase(), structUniqueId,
//                    UUID.randomUUID().toString(), unit_relative_path,
                    previousStructureInfoPointDataModel.getGlobalId(), unit_relative_path,
                    tenementNo,
                    previousStructureInfoPointDataModel.getHut_number(),
                    actualFloorNo,
                    unitUniqueId,
                    unitStructureUsage, unitExistance + "", "test",
                    residential_area, commercial_area, rc_residential_area, rc_commercial_area,
                    religious_area, other_area,
                    Utils.doubleFormatter(unitArea),
                    binding.layoutNewUnitDetails.radioMemberAvailableYes.isChecked(),
//                    binding.layoutNewUnitDetails.autoCompRemarks.getText().toString(),
                    "",
//                    String.valueOf(year),
                    String.valueOf(extSince),
                    unitExistance,
                    resident_proof_attached, resident_scAttachmentList.size() > 0,
                    resident_ecbAttachmentList.size() > 0,
                    resident_ppAttachmentList.size() > 0,
                    resident_nataxAttachmentList.size() > 0,
                    resident_ptprAttachmentList.size() > 0,
                    resident_erAttachmentList.size() > 0,
                    additional_document_attached,
                    additionalSccsasAttachmentList.size() > 0,
                    additionalCiesaAttachmentList.size() > 0, chain_document_attached,
                    chainPsipcAttachmentList.size() > 0, chainRaAttachmentList.size() > 0,
                    license_proof_attached, licenseProofSeAttachmentList.size() > 0,
                    licenseProofRhlAttachmentList.size() > 0,
                    licenseProofFdlAttachmentList.size() > 0,
                    licenseProofFalAttachmentList.size() > 0, religious_attachment_attached,
                    others_attachment_attached, unitUpdatedStatus,
                    surveyorData.getSurName(),
                    surveyorData.getSurDeg(),
                    surveyorData.getDrpName(),
                    surveyorData.getDrpNameOther(),
                    surveyorData.getDrpDeg(),
                    surveyorData.getDrpDegOther(),
                    remarksChange, "",
                    residentProofAttachmentCount + residentProofAdditionalAttachmentCount + residentProofChainAttachmentCount + licenseProofAttachmentListCount + religiousAttachmentCount + otherAttachmentCount,
                    0,
                    previousObjectIdUnit, previousGlobalIdUnit, new Date(), new Date(),
                    visitCountCalculate,
                    binding.layoutNewUnitDetails.etAreaName.getText().toString(),
                    binding.layoutNewUnitDetails.etWardNo.getText().toString(),
                    binding.layoutNewUnitDetails.etSectorNo.getText().toString(),
                    binding.layoutNewUnitDetails.etZoneNo.getText().toString(),
                    nagarName, nagarNameOther,
                    binding.layoutNewUnitDetails.etSocietyName.getText().toString(),
                    binding.layoutNewUnitDetails.etStreetRoadName.getText().toString(),
                    binding.layoutNewUnitDetails.etLandmark.getText().toString(),
                    binding.layoutNewUnitDetails.etRespondentName.getText().toString(),
                    binding.layoutNewUnitDetails.etDobRespondent.getText().toString(),
                    binding.layoutNewUnitDetails.etShowAge.getText().toString(),
                    binding.layoutNewUnitDetails.etHOHname.getText().toString(),
                    relationshipHoh, relationshipHohOther,
                    tenementDoc,
                    mashalNo,
                    isUnit_RentOwner,
                    Utils.doubleFormatter(unitArea), is_loft,
                    Utils.doubleFormatter(unitLoftArea
                    ), unitNoEmployee, Utils.doubleFormatter(unit_gomasta), isUnitUploaded,
                    binding.layoutNewUnitDetails.etPincode.getTag().toString(),
                    binding.layoutNewUnitDetails.etRespondentContact.getText().toString(), "",
                    lockFlag, binding.layoutNewUnitDetails.etContactNumber.getText().toString(),
                    surveyDate,
                    surveyTime, otherTxt, false, rmk,pavtiNo,yearTemp,
                    binding.layoutNewUnitDetails.etCountry.getText().toString(),
                    binding.layoutNewUnitDetails.etState.getText().toString(),
                    binding.layoutNewUnitDetails.etLandcity.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompAccess.getText().toString(),resRcArea,commRcArea,thumbRemark,annexureRemarks,
                    binding.layoutNewUnitDetails.autoCompStructure.getText().toString(),
                    binding.layoutNewUnitDetails.etStructureOther.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompStructure.getText().toString(),
                    binding.layoutNewUnitDetails.etStructureOther.getText().toString(),
                    binding.layoutNewUnitDetails.etStructureName.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompDiety.getText().toString(),
                    binding.layoutNewUnitDetails.etDietyOther.getText().toString(),
                    binding.layoutNewUnitDetails.etDietyName.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompFaith.getText().toString(),
                    binding.layoutNewUnitDetails.etFaithOther.getText().toString(),
                    binding.layoutNewUnitDetails.etSubCategoryFaith.getText().toString(),
                    binding.layoutNewUnitDetails.etRelBelongsStrucutre.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompStructureOwnership.getText().toString(),
                    nameBelong,
                    binding.layoutNewUnitDetails.autoCompStructureNature.getText().toString(),
                    binding.layoutNewUnitDetails.etConstructionMaterial.getText().toString(),
                    binding.layoutNewUnitDetails.etNoOfPeoples.getText().toString(),
                    binding.layoutNewUnitDetails.etTenementRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompTenementRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompReligiousRegistered.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.getText().toString(),
                    binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.getText().toString(),
                    nameTrustees,
                    binding.layoutNewUnitDetails.etLandownerName.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompNocYesNo.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompApprovalYesNo.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompFestivalYesNo.getText().toString(),
                    binding.layoutNewUnitDetails.etPavtiRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.etMashalRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompNoFloor.getText().toString(),panchnamaRemarks,"",
                    latLoc,
                    lonLoc,
                    deviceN,pImei,sImei,(short) surveyorData.getIs_drppl_officer_available(),surveyorData.getDrppl_officer_name()
            );

            if (resident_proof_attached) {
                ArrayList<MediaInfoDataModel> resident_sc = getmediaInfoDataList(resident_scAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (resident_sc.size() > 0)
                    mediaInfoDataModels1.addAll(resident_sc);

                ArrayList<MediaInfoDataModel> resident_ecb = getmediaInfoDataList(resident_ecbAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (resident_ecb.size() > 0)
                    mediaInfoDataModels1.addAll(resident_ecb);

                ArrayList<MediaInfoDataModel> resident_pp = getmediaInfoDataList(resident_ppAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (resident_pp.size() > 0)
                    mediaInfoDataModels1.addAll(resident_pp);

                ArrayList<MediaInfoDataModel> resident_natax = getmediaInfoDataList(resident_nataxAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (resident_natax.size() > 0)
                    mediaInfoDataModels1.addAll(resident_natax);

                ArrayList<MediaInfoDataModel> resident_ptpr = getmediaInfoDataList(resident_ptprAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (resident_ptpr.size() > 0)
                    mediaInfoDataModels1.addAll(resident_ptpr);

                ArrayList<MediaInfoDataModel> resident_er = getmediaInfoDataList(resident_erAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (resident_er.size() > 0)
                    mediaInfoDataModels1.addAll(resident_er);
            }
            if (additional_document_attached) {

                ArrayList<MediaInfoDataModel> additional_Sccsas = getmediaInfoDataList(additionalSccsasAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (additional_Sccsas.size() > 0)
                    mediaInfoDataModels1.addAll(additional_Sccsas);

                ArrayList<MediaInfoDataModel> additional_Ciesa = getmediaInfoDataList(additionalCiesaAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (additional_Ciesa.size() > 0)
                    mediaInfoDataModels1.addAll(additional_Ciesa);

                ArrayList<MediaInfoDataModel> additional_Attachment3 = getmediaInfoDataList(additionalAttachment3AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (additional_Attachment3.size() > 0)
                    mediaInfoDataModels1.addAll(additional_Attachment3);

                ArrayList<MediaInfoDataModel> additional_Attachment4 = getmediaInfoDataList(additionalAttachment4AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (additional_Attachment4.size() > 0)
                    mediaInfoDataModels1.addAll(additional_Attachment4);
            }
            if (chain_document_attached) {
                ArrayList<MediaInfoDataModel> chain_Psipc = getmediaInfoDataList(chainPsipcAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (chain_Psipc.size() > 0)
                    mediaInfoDataModels1.addAll(chain_Psipc);

                ArrayList<MediaInfoDataModel> chain_Ra = getmediaInfoDataList(chainRaAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (chain_Ra.size() > 0)
                    mediaInfoDataModels1.addAll(chain_Ra);

                ArrayList<MediaInfoDataModel> chain_Attachment3 = getmediaInfoDataList(chainAttachment3AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (chain_Attachment3.size() > 0)
                    mediaInfoDataModels1.addAll(chain_Attachment3);

                ArrayList<MediaInfoDataModel> chain_Attachment4 = getmediaInfoDataList(chainAttachment4AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (chain_Attachment4.size() > 0)
                    mediaInfoDataModels1.addAll(chain_Attachment4);
            }
            if (license_proof_attached) {

                ArrayList<MediaInfoDataModel> licenseProof_Se = getmediaInfoDataList(licenseProofSeAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (licenseProof_Se.size() > 0)
                    mediaInfoDataModels1.addAll(licenseProof_Se);

                ArrayList<MediaInfoDataModel> licenseProof_Rhl = getmediaInfoDataList(licenseProofRhlAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (licenseProof_Rhl.size() > 0)
                    mediaInfoDataModels1.addAll(licenseProof_Rhl);

                ArrayList<MediaInfoDataModel> licenseProof_Fdl = getmediaInfoDataList(licenseProofFdlAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (licenseProof_Fdl.size() > 0)
                    mediaInfoDataModels1.addAll(licenseProof_Fdl);

                ArrayList<MediaInfoDataModel> licenseProof_Fal = getmediaInfoDataList(licenseProofFalAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (licenseProof_Fal.size() > 0)
                    mediaInfoDataModels1.addAll(licenseProof_Fal);
            }
            if (religious_attachment_attached) {

                ArrayList<MediaInfoDataModel> religious_OtherA1 = getmediaInfoDataList(religiousOtherA1AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (religious_OtherA1.size() > 0)
                    mediaInfoDataModels1.addAll(religious_OtherA1);

                ArrayList<MediaInfoDataModel> religious_OtherA2 = getmediaInfoDataList(religiousOtherA2AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (religious_OtherA2.size() > 0)
                    mediaInfoDataModels1.addAll(religious_OtherA2);

                ArrayList<MediaInfoDataModel> religious_OtherA3 = getmediaInfoDataList(religiousOtherA3AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (religious_OtherA3.size() > 0)
                    mediaInfoDataModels1.addAll(religious_OtherA3);

                ArrayList<MediaInfoDataModel> religious_OtherA4 = getmediaInfoDataList(religiousOtherA4AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (religious_OtherA4.size() > 0)
                    mediaInfoDataModels1.addAll(religious_OtherA4);
            }
            if (others_attachment_attached) {

                ArrayList<MediaInfoDataModel> religious_OtherA1 = getmediaInfoDataList(religiousOtherA1AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (religious_OtherA1.size() > 0)
                    mediaInfoDataModels1.addAll(religious_OtherA1);

                ArrayList<MediaInfoDataModel> religious_OtherA2 = getmediaInfoDataList(religiousOtherA2AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (religious_OtherA2.size() > 0)
                    mediaInfoDataModels1.addAll(religious_OtherA2);

                ArrayList<MediaInfoDataModel> religious_OtherA3 = getmediaInfoDataList(religiousOtherA3AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (religious_OtherA3.size() > 0)
                    mediaInfoDataModels1.addAll(religious_OtherA3);

                ArrayList<MediaInfoDataModel> religious_OtherA4 = getmediaInfoDataList(religiousOtherA4AttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                if (religious_OtherA4.size() > 0)
                    mediaInfoDataModels1.addAll(religious_OtherA4);
            }

            unitMediaListDetail.put(unitInfoDataModel.getUnit_id(), mediaInfoDataModels1);

        } else if ((previousUnitInfoPointDataModel != null && previousUnitInfoPointDataModel.isMember_available() && binding.layoutNewUnitDetails.radioMemberAvailableNo.isChecked()) || (previousUnitInfoPointDataModel!=null && previousUnitInfoPointDataModel.isYesNo())) {
            unitInfoDataModel = new UnitInfoDataModel(unitUniqueId, modif.toUpperCase(), structUniqueId,
//                    UUID.randomUUID().toString(), unit_relative_path,
                    previousStructureInfoPointDataModel.getGlobalId(), unit_relative_path,
                    tenementNo,
                    previousStructureInfoPointDataModel.getHut_number(),
                    actualFloorNo,
                    unitUniqueId,
                    unitStructureUsage, unitExistance + "", "test",
                    residential_area, commercial_area, rc_residential_area, rc_commercial_area,
                    religious_area, other_area,
                    Utils.doubleFormatter(unitArea),
                    binding.layoutNewUnitDetails.radioMemberAvailableYes.isChecked(),
                    binding.layoutNewUnitDetails.autoCompRemarks.getTag().toString(),
                    String.valueOf(extSince),
                    unitExistance,
                    resident_proof_attached, resident_scAttachmentList.size() > 0,
                    resident_ecbAttachmentList.size() > 0,
                    resident_ppAttachmentList.size() > 0,
                    resident_nataxAttachmentList.size() > 0,
                    resident_ptprAttachmentList.size() > 0,
                    resident_erAttachmentList.size() > 0,
                    additional_document_attached,
                    additionalSccsasAttachmentList.size() > 0,
                    additionalCiesaAttachmentList.size() > 0, chain_document_attached,
                    chainPsipcAttachmentList.size() > 0, chainRaAttachmentList.size() > 0,
                    license_proof_attached, licenseProofSeAttachmentList.size() > 0,
                    licenseProofRhlAttachmentList.size() > 0,
                    licenseProofFdlAttachmentList.size() > 0,
                    licenseProofFalAttachmentList.size() > 0, religious_attachment_attached,
                    others_attachment_attached, Constants.OnHold_statusLayer,
                    surveyorData.getSurName(),
                    surveyorData.getSurDeg(),
                    surveyorData.getDrpName(),
                    surveyorData.getDrpNameOther(),
                    surveyorData.getDrpDeg(),
                    surveyorData.getDrpDegOther(),
                    binding.layoutNewUnitDetails.autoCompRemarks.getTag().toString(), "",
                    residentProofAttachmentCount + residentProofAdditionalAttachmentCount + residentProofChainAttachmentCount + licenseProofAttachmentListCount + religiousAttachmentCount + otherAttachmentCount,
                    0,
                    previousObjectIdUnit, previousGlobalIdUnit, new Date(), new Date(),
                    visitCountCalculate,
                    binding.layoutNewUnitDetails.etAreaName.getText().toString(),
                    binding.layoutNewUnitDetails.etWardNo.getText().toString(),
                    binding.layoutNewUnitDetails.etSectorNo.getText().toString(),
                    binding.layoutNewUnitDetails.etZoneNo.getText().toString(),
                    nagarName, nagarNameOther,
                    binding.layoutNewUnitDetails.etSocietyName.getText().toString(),
                    binding.layoutNewUnitDetails.etStreetRoadName.getText().toString(),
                    binding.layoutNewUnitDetails.etLandmark.getText().toString(),
                    binding.layoutNewUnitDetails.etRespondentName.getText().toString(),
                    binding.layoutNewUnitDetails.etDobRespondent.getText().toString(),
                    binding.layoutNewUnitDetails.etShowAge.getText().toString(),
                    binding.layoutNewUnitDetails.etHOHname.getText().toString(),
                    relationshipHoh, relationshipHohOther,
                    tenementDoc,
                    mashalNo,
                    isUnit_RentOwner,
                    Utils.doubleFormatter(unitArea), is_loft,
                    Utils.doubleFormatter(unitLoftArea
                    ), unitNoEmployee, Utils.doubleFormatter(unit_gomasta), isUnitUploaded,
                    binding.layoutNewUnitDetails.etPincode.getTag().toString(),
                    binding.layoutNewUnitDetails.etRespondentContact.getText().toString(), "",
                    lockFlag, binding.layoutNewUnitDetails.etContactNumber.getText().toString(),
                    surveyDate,
                    surveyTime, otherTxt, true, "",pavtiNo,yearTemp,
                    binding.layoutNewUnitDetails.etCountry.getText().toString(),
                    binding.layoutNewUnitDetails.etState.getText().toString(),
                    binding.layoutNewUnitDetails.etLandcity.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompAccess.getText().toString(),resRcArea,commRcArea,thumbRemark,annexureRemarks,
                    binding.layoutNewUnitDetails.autoCompStructure.getText().toString(),
                    binding.layoutNewUnitDetails.etStructureOther.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompStructure.getText().toString(),
                    binding.layoutNewUnitDetails.etStructureOther.getText().toString(),
                    binding.layoutNewUnitDetails.etStructureName.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompDiety.getText().toString(),
                    binding.layoutNewUnitDetails.etDietyOther.getText().toString(),
                    binding.layoutNewUnitDetails.etDietyName.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompFaith.getText().toString(),
                    binding.layoutNewUnitDetails.etFaithOther.getText().toString(),
                    binding.layoutNewUnitDetails.etSubCategoryFaith.getText().toString(),
                    binding.layoutNewUnitDetails.etRelBelongsStrucutre.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompStructureOwnership.getText().toString(),
                    nameBelong,
                    binding.layoutNewUnitDetails.autoCompStructureNature.getText().toString(),
                    binding.layoutNewUnitDetails.etConstructionMaterial.getText().toString(),
                    binding.layoutNewUnitDetails.etNoOfPeoples.getText().toString(),
                    binding.layoutNewUnitDetails.etTenementRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompTenementRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompReligiousRegistered.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.getText().toString(),
                    binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.getText().toString(),
                    nameTrustees,
                    binding.layoutNewUnitDetails.etLandownerName.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompNocYesNo.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompApprovalYesNo.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompFestivalYesNo.getText().toString(),
                    binding.layoutNewUnitDetails.etPavtiRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.etMashalRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompNoFloor.getText().toString(),panchnamaRemarks,"",
                    latLoc,
                    lonLoc,
                    deviceN,pImei,sImei,(short) surveyorData.getIs_drppl_officer_available(),surveyorData.getDrppl_officer_name()

            );
        } else {
            String status="";
            String gen_qc_status="";
            String remarksI ="";
            String usage=unitStructureUsage;
            if(surveyFlag){
                status = Constants.NotApplicable_statusLayer;
                gen_qc_status = Constants.NotApplicable_statusLayer;
                remarksI=Constants.NotApplicable_statusLayer;
                usage=usageToiletMeterBox;
            }else{
                gen_qc_status ="";
                status=Constants.OnHold_statusLayer;
                remarksI = binding.layoutNewUnitDetails.autoCompRemarks.getTag().toString();
            }
            unitInfoDataModel = new UnitInfoDataModel(unitUniqueId, modif.toUpperCase(), structUniqueId,
//                    UUID.randomUUID().toString(), unit_relative_path,
                    previousStructureInfoPointDataModel.getGlobalId(), unit_relative_path,
                    "",
                    previousStructureInfoPointDataModel.getHut_number(),
                    actualFloorNo,
                    unitUniqueId,
                    usage, "", "test",
                    residential_area, commercial_area, rc_residential_area, rc_commercial_area,
                    religious_area, other_area,
                    Utils.doubleFormatter(unitArea),
                    binding.layoutNewUnitDetails.radioMemberAvailableYes.isChecked(),
                    remarksI,
                    "",
                    "",
                    resident_proof_attached, resident_scAttachmentList.size() > 0,
                    resident_ecbAttachmentList.size() > 0,
                    resident_ppAttachmentList.size() > 0,
                    resident_nataxAttachmentList.size() > 0,
                    resident_ptprAttachmentList.size() > 0,
                    resident_erAttachmentList.size() > 0,
                    additional_document_attached,
                    additionalSccsasAttachmentList.size() > 0,
                    additionalCiesaAttachmentList.size() > 0,
                    chain_document_attached,
                    chainPsipcAttachmentList.size() > 0,
                    chainRaAttachmentList.size() > 0,
                    license_proof_attached,
                    licenseProofSeAttachmentList.size() > 0,
                    licenseProofRhlAttachmentList.size() > 0,
                    licenseProofFdlAttachmentList.size() > 0,
                    licenseProofFalAttachmentList.size() > 0,
                    religious_attachment_attached,
                    others_attachment_attached,
                    status,
                    surveyorData.getSurName(),
                    surveyorData.getSurDeg(),
                    surveyorData.getDrpName(),
                    surveyorData.getDrpNameOther(),
                    surveyorData.getDrpDeg(),
                    surveyorData.getDrpDegOther(),
                    remarksI, "",
                    residentProofAttachmentCount + residentProofAdditionalAttachmentCount + residentProofChainAttachmentCount + licenseProofAttachmentListCount + religiousAttachmentCount + otherAttachmentCount,
                    0,
                    previousObjectIdUnit, previousGlobalIdUnit, new Date(), new Date(),
                    visitCountCalculate,
                    binding.layoutNewUnitDetails.etAreaName.getText().toString(),
                    binding.layoutNewUnitDetails.etWardNo.getText().toString(),
                    binding.layoutNewUnitDetails.etSectorNo.getText().toString(),
                    binding.layoutNewUnitDetails.etZoneNo.getText().toString(),
                    nagarName, nagarNameOther,
                    binding.layoutNewUnitDetails.etSocietyName.getText().toString(),
                    binding.layoutNewUnitDetails.etStreetRoadName.getText().toString(),
                    binding.layoutNewUnitDetails.etLandmark.getText().toString(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    Utils.doubleFormatter(unitArea), false,
                    0.0, "", 0.0, isUnitUploaded,
                    binding.layoutNewUnitDetails.etPincode.getTag().toString(),
                    "", "",
                    lockFlag, "",
                    surveyDate,
                    surveyTime, otherTxt, false, "",pavtiNo,yearTemp,
                    binding.layoutNewUnitDetails.etCountry.getText().toString(),
                    binding.layoutNewUnitDetails.etState.getText().toString(),
                    binding.layoutNewUnitDetails.etLandcity.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompAccess.getText().toString(),
                    resRcArea,commRcArea,thumbRemark,annexureRemarks,
                    binding.layoutNewUnitDetails.autoCompStructure.getText().toString(),
                    binding.layoutNewUnitDetails.etStructureOther.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompStructure.getText().toString(),
                    binding.layoutNewUnitDetails.etStructureOther.getText().toString(),
                    binding.layoutNewUnitDetails.etStructureName.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompDiety.getText().toString(),
                    binding.layoutNewUnitDetails.etDietyOther.getText().toString(),
                    binding.layoutNewUnitDetails.etDietyName.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompFaith.getText().toString(),
                    binding.layoutNewUnitDetails.etFaithOther.getText().toString(),
                    binding.layoutNewUnitDetails.etSubCategoryFaith.getText().toString(),
                    binding.layoutNewUnitDetails.etRelBelongsStrucutre.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompStructureOwnership.getText().toString(),
                    nameBelong,
                    binding.layoutNewUnitDetails.autoCompStructureNature.getText().toString(),
                    binding.layoutNewUnitDetails.etConstructionMaterial.getText().toString(),
                    binding.layoutNewUnitDetails.etNoOfPeoples.getText().toString(),
                    binding.layoutNewUnitDetails.etTenementRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompTenementRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompReligiousRegistered.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.getText().toString(),
                    binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.getText().toString(),
                    nameTrustees,
                    binding.layoutNewUnitDetails.etLandownerName.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompNocYesNo.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompApprovalYesNo.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompFestivalYesNo.getText().toString(),
                    binding.layoutNewUnitDetails.etPavtiRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.etMashalRelAmenities.getText().toString(),
                    binding.layoutNewUnitDetails.autoCompNoFloor.getText().toString(),panchnamaRemarks,gen_qc_status,
                    latLoc,
                    lonLoc,
                    deviceN,pImei,sImei,(short) surveyorData.getIs_drppl_officer_available(),surveyorData.getDrppl_officer_name()

            );

        }
        try {
            ArrayList<UnitInfoDataModel> unitI=App.getInstance().getUniqueObjectList();
            int n=-1;
            if(unitI!=null && unitI.size()>0){
                for(int i=0;i<unitI.size();i++){
                    if(unitI.get(i).getUnit_unique_id().equals(unitInfoDataModel.getUnit_unique_id())){
                        n=i;
                    }
                }
                if(n==-1){
                    unitI.add(unitInfoDataModel);
                    App.getInstance().setUniqueObjectList(unitI);
                }else{
                    unitI.remove(n);
                    unitI.add(unitInfoDataModel);
                    App.getInstance().setUniqueObjectList(unitI);
                }
            }else{
                ArrayList<UnitInfoDataModel> unitP=new ArrayList<>();
                unitP.add(unitInfoDataModel);
                App.getInstance().setUniqueObjectList(unitP);
            }
        }catch(Exception ex){
            ex.getMessage();
        }
        return unitInfoDataModel;
    }

    private ArrayList<MediaInfoDataModel> getmediaInfoDataList(ArrayList<AttachmentListImageDetails> attachmentImageDetailsList, String relative_path,
                                                               String infoLayer, String id, String globlId) {
        ArrayList<MediaInfoDataModel> mediaInfoDataModels1 = new ArrayList<>();
        MediaInfoDataModel mm = null;
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

//        for (AttachmentListImageDetails attachmentListImageDetails : attachmentImageDetailsList) {
//            if (!getPreviousMediaFileName.contains(attachmentListImageDetails.getFileName()))
//                if (attachmentListImageDetails.getFile().exists()) {
//                    String contentType = Utils.getContentType(attachmentListImageDetails.getFilePath());
//                    int dataSize = (int) attachmentListImageDetails.getFile().length();
//                    getPreviousMediaFileName.add(attachmentListImageDetails.getFileName());
//                    mediaInfoDataModels1.add(new MediaInfoDataModel(contentType,
//                            attachmentListImageDetails.getFileName(), dataSize, "",
//                            attachmentListImageDetails.getFilePath(),
//                            (short) 0, (short) 0,
//                            infoLayer, id, globlId, relative_path,
//                            "", "", false, new Date(), new Date()));
//                }
//        }

        return mediaInfoDataModels1;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", imageUri);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageUri = savedInstanceState.getParcelable("pic_uri");
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (activity!=null && activity.getIntent().hasExtra(Constants.INTENT_DATA_UnitInfo)) {
                UnitInfoDataModel pp = (UnitInfoDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_UnitInfo);
                if((previousUnitInfoPointDataModel.getObejctId() == null || previousUnitInfoPointDataModel.getObejctId().equals(""))){
                    binding.layoutNewUnitDetails.radioUnitUsageReligious.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageAmenities.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageResidential.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageCommercial.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageRC.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setClickable(false);
                    binding.layoutNewUnitDetails.radioUnitUsageOthers.setClickable(false);
                }else{
                    if(previousUnitInfoPointDataModel!=null && previousUnitInfoPointDataModel.getVisit_date() == null){
                        previousUnitInfoPointDataModel.setVisit_date(pp.getVisit_date());
                    }else if(previousUnitInfoPointDataModel!=null && previousUnitInfoPointDataModel.getVisit_date().equals("")){
                        previousUnitInfoPointDataModel.setVisit_date(pp.getVisit_date());
                    }
                }
            }
        }catch (Exception ex){
            ex.getCause();
        }
        if (pdfPathFile != null) {
            try {
                CryptoUtilsTest.encryptFileinAES(pdfPathFile, 1);
            } catch (CryptoException e) {
                AppLog.logData(activity,e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private void updateDateLabel() {
        String myFormat = Constants.requiredDateFormat;
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        binding.layoutUnitDetailInfo.etExistenceSince.setText(dateFormat.format(myCalendar.getTime()));

        /*
        Rohit
         */
//        binding.layoutNewUnitDetails.etExistenceSince.setText(dateFormat.format(myCalendar.getTime()));
//        binding.layoutNewUnitDetails.etExistenceSinceComm.setText(dateFormat.format(myCalendar.getTime()));

        /*
        date compare
         */

        try {
            String dtStart = "02/01/2000";
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                dateRangeOne = format.parse(dtStart);
                dtStart = "02/01/2011";
                format = new SimpleDateFormat("dd/MM/yyyy");
                dateRangeTwo = format.parse(dtStart);
                dtStart = "03/01/2011";
                format = new SimpleDateFormat("dd/MM/yyyy");
                dateRangeThree = format.parse(dtStart);
                dtStart = "16/11/2022";
                format = new SimpleDateFormat("dd/MM/yyyy");
                dateRangeFour = format.parse(dtStart);


            SimpleDateFormat dd = new SimpleDateFormat("dd/MM/yyyy");
            String date = dd.format(myCalendar.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date strDate = null;
            strDate = sdf.parse(date);

            if (strDate.before(dateRangeOne)) {
                dateCheck=Constants.dateTxtFirst;
            }else if ((strDate.equals(dateRangeOne) || strDate.after(dateRangeOne)) && strDate.before(dateRangeTwo)) {
                dateCheck=Constants.dateTxtSecond;
            }else if ((strDate.equals(dateRangeTwo) || strDate.after(dateRangeTwo)) && strDate.before(dateRangeFour)) {
                dateCheck=Constants.dateTxtThird;
            }else if (strDate.equals(dateRangeFour) || strDate.after(dateRangeFour)) {
                dateCheck=Constants.dateTxtFour;
            }
        } catch (ParseException e) {
            AppLog.logData(activity,e.getMessage());
            throw new RuntimeException(e);
        }


        String myYearFormat = "yyyy";
        SimpleDateFormat yearFormat = new SimpleDateFormat(myYearFormat, Locale.US);
        year = Integer.parseInt(yearFormat.format(myCalendar.getTime()));
        binding.layoutUnitDetailInfo.etExistenceSince.setError(null, null);
        /*
        Rohit
         */
        binding.layoutNewUnitDetails.etExistenceSince.setError(null, null);

        if((binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() || binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked()) && !binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("Rented Tenant")){
            setOwnerRentDocs();
        }else if((binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() || binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked()) && !binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("Rented Tenant")){
            setOwnerRentDocs();
        }else{
            if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                if(floorFlag){
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                }else{
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                }
                binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
            }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.VISIBLE);
                if(floorFlag){
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.VISIBLE);
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.VISIBLE);
                }else{
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.ttl.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.lyout.setVisibility(View.GONE);
                }
                binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
                binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
            }else{
            setupYearOfStructure();}
        }

    }

    private void setupYearOfStructure() {
        binding.layoutNewUnitDetails.layOwnerRent.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.VISIBLE);

        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
        if(floorFlag){
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 9 attached");
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 9 attached");
        }else{
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 7 attached");
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 7 attached");
            binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 7 attached");
        }

        if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
            binding.layoutNewUnitDetails.layRCProof.setVisibility(View.VISIBLE);
            List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
            if(floorFlag){
                binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setText(ll2.size() + " " + "out of 9 attached");
                binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll2.size() + " " + "out of 9 attached");
                binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll2.size() + " " + "out of 9 attached");
            }else{
                binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setText(ll2.size() + " " + "out of 7 attached");
                binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll2.size() + " " + "out of 7 attached");
                binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll2.size() + " " + "out of 7 attached");
            }

            binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.GONE);
        }else{
            binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.VISIBLE);
            List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
            if(floorFlag){
                binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setText(ll1.size() + " " + "out of 9 attached");
                binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll1.size() + " " + "out of 9 attached");
                binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll1.size() + " " + "out of 9 attached");
            }else{
                binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setText(ll1.size() + " " + "out of 7 attached");
                binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll1.size() + " " + "out of 7 attached");
                binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll1.size() + " " + "out of 7 attached");
            }
            binding.layoutNewUnitDetails.layRCProof.setVisibility(View.GONE);
        }


//        if (year < 2000) {
        if (dateCheck.equalsIgnoreCase(Constants.dateTxtFirst)) {
            residentProofAdditionalAttachmentCount = 0;
            residentProofChainAttachmentCount = 0;

            /*
            Rohit
             */
            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);

//            binding.layoutNewUnitDetails.etYearOfStructure.setText("before 01-01-2000");
//            binding.layoutNewUnitDetails.etYearOfStructureComm.setText("before 01-01-2000");
            binding.layoutNewUnitDetails.layoutResAttachement.below2000.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutResAttachement.till2011.setVisibility(View.GONE);
            binding.layoutNewUnitDetails.layoutResAttachement.after2011.setVisibility(View.GONE);
            binding.layoutNewUnitDetails.layoutResAttachement.photoExtraLayout.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutResAttachement.surveyPavtiExtraLayout.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutResAttachement.othersExtraLayout.setVisibility(View.VISIBLE);

            if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                binding.layoutNewUnitDetails.layoutRcAttachement.below2000.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.photoExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.surveyPavtiExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.othersExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.till2011.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layoutRcAttachement.after2011.setVisibility(View.GONE);

                binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layRCProof.setVisibility(View.VISIBLE);
                List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                if(floorFlag){
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setText(ll2.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll2.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll2.size() + " " + "out of 9 attached");
                }else{
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setText(ll2.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll2.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll2.size() + " " + "out of 7 attached");
                }
            }else{
                binding.layoutNewUnitDetails.layoutCommAttachement.below2000.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.photoExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.surveyPavtiExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.othersExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.till2011.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layoutCommAttachement.after2011.setVisibility(View.GONE);

                binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.VISIBLE);
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                if(floorFlag){
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setText(ll1.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll1.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll1.size() + " " + "out of 9 attached");
                }else{
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setText(ll1.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll1.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll1.size() + " " + "out of 7 attached");
                }
                binding.layoutNewUnitDetails.layRCProof.setVisibility(View.GONE);
            }


            binding.layoutUnitDetailInfo.etYearOfStructure.setText("before 01-01-2000");
            binding.layoutUnitDetailInfo.layResidentProofAdditionalDocument.setVisibility(View.GONE);
            binding.layoutUnitDetailInfo.layResidentProofChainDocument.setVisibility(View.GONE);


            setCounts(year);

        }
//        else if (year < 2011) {
        else if (dateCheck.equalsIgnoreCase(Constants.dateTxtSecond)) {
            if (binding.layoutUnitDetailInfo.radioUnitUsageResidential.isChecked())
                setupResidentProofAdditionalChainDocument();
            else {
                binding.layoutUnitDetailInfo.layResidentProofAdditionalDocument.setVisibility(View.GONE);
                binding.layoutUnitDetailInfo.layResidentProofChainDocument.setVisibility(View.GONE);
            }
            binding.layoutUnitDetailInfo.etYearOfStructure.setText(dateCheck);

            /*
            Rohit
             */
            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);

//            binding.layoutNewUnitDetails.etYearOfStructure.setText("01-01-2000 to 31-12-2010");
//            binding.layoutNewUnitDetails.etYearOfStructureComm.setText("01-01-2000 to 31-12-2010");
            binding.layoutNewUnitDetails.layoutResAttachement.below2000.setVisibility(View.GONE);
            binding.layoutNewUnitDetails.layoutResAttachement.after2011.setVisibility(View.GONE);
            binding.layoutNewUnitDetails.layoutResAttachement.till2011.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutResAttachement.photoExtraLayout.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutResAttachement.surveyPavtiExtraLayout.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutResAttachement.othersExtraLayout.setVisibility(View.VISIBLE);

            if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                binding.layoutNewUnitDetails.layoutRcAttachement.below2000.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layoutRcAttachement.after2011.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layoutRcAttachement.till2011.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.photoExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.surveyPavtiExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.othersExtraLayout.setVisibility(View.VISIBLE);

                binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layRCProof.setVisibility(View.VISIBLE);
                List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                if(floorFlag){
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setText(ll2.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll2.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll2.size() + " " + "out of 9 attached");
                }else{
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setText(ll2.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll2.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll2.size() + " " + "out of 7 attached");
                }
            }else{
                binding.layoutNewUnitDetails.layoutCommAttachement.below2000.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layoutCommAttachement.after2011.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layoutCommAttachement.till2011.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.photoExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.surveyPavtiExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.othersExtraLayout.setVisibility(View.VISIBLE);

                binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.VISIBLE);
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                if(floorFlag){
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setText(ll1.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll1.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll1.size() + " " + "out of 9 attached");
                }else{
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setText(ll1.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll1.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll1.size() + " " + "out of 7 attached");
                }
                binding.layoutNewUnitDetails.layRCProof.setVisibility(View.GONE);
            }

            setCounts(year);
        }

        else {
            /*
            Rohit
             */
            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);

//            binding.layoutNewUnitDetails.etYearOfStructure.setText("After 01-01-2011");
//            binding.layoutNewUnitDetails.etYearOfStructureComm.setText("After 01-01-2011");
            binding.layoutNewUnitDetails.layoutResAttachement.after2011.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutResAttachement.till2011.setVisibility(View.GONE);
            binding.layoutNewUnitDetails.layoutResAttachement.below2000.setVisibility(View.GONE);
            binding.layoutNewUnitDetails.layoutResAttachement.photoExtraLayout.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutResAttachement.surveyPavtiExtraLayout.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutResAttachement.othersExtraLayout.setVisibility(View.VISIBLE);

            if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                binding.layoutNewUnitDetails.layoutRcAttachement.after2011.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.photoExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.surveyPavtiExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.othersExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutRcAttachement.till2011.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layoutRcAttachement.below2000.setVisibility(View.GONE);

                binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layRCProof.setVisibility(View.VISIBLE);
                List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                if(floorFlag){
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setText(ll2.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll2.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll2.size() + " " + "out of 9 attached");
                }else{
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setText(ll2.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll2.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll2.size() + " " + "out of 7 attached");
                }
            }else{
                binding.layoutNewUnitDetails.layoutCommAttachement.after2011.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.photoExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.surveyPavtiExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.othersExtraLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.layoutCommAttachement.till2011.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layoutCommAttachement.below2000.setVisibility(View.GONE);

                binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.VISIBLE);
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                if(floorFlag){
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setText(ll1.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll1.size() + " " + "out of 9 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll1.size() + " " + "out of 9 attached");
                }else{
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setText(ll1.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll1.size() + " " + "out of 7 attached");
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll1.size() + " " + "out of 7 attached");
                }
                binding.layoutNewUnitDetails.layRCProof.setVisibility(View.GONE);
            }

            binding.layoutUnitDetailInfo.etYearOfStructure.setText(dateCheck);
            if (binding.layoutUnitDetailInfo.radioUnitUsageResidential.isChecked())
                setupResidentProofAdditionalChainDocument();
            else {
                binding.layoutUnitDetailInfo.layResidentProofAdditionalDocument.setVisibility(View.GONE);
                binding.layoutUnitDetailInfo.layResidentProofChainDocument.setVisibility(View.GONE);
            }

            setCounts(year);

        }
        if (binding.layoutNewUnitDetails.etExistenceSince.getText().toString().equalsIgnoreCase("")) {
            binding.layoutNewUnitDetails.etYearOfStructure.setText("");
        }
        if (binding.layoutNewUnitDetails.etExistenceSinceComm.getText().toString().equalsIgnoreCase("")) {
            binding.layoutNewUnitDetails.etYearOfStructureComm.setText("");
        }

        binding.layoutNewUnitDetails.etYearOfStructure.setTag(dateCheck);
        binding.layoutNewUnitDetails.etYearOfStructureComm.setTag(dateCheck);

        binding.layoutNewUnitDetails.etYearOfStructure.setText(Utils.getTextByTag(Constants.domain_structure_year,binding.layoutNewUnitDetails.etYearOfStructure.getText().toString()));
        binding.layoutNewUnitDetails.etYearOfStructureComm.setText(Utils.getTextByTag(Constants.domain_structure_year,binding.layoutNewUnitDetails.etYearOfStructureComm.getText().toString()));




    }

    private void setOwnerRentDocs(){
        binding.layoutNewUnitDetails.layOwnerRent.setVisibility(View.VISIBLE);
        if(floorFlag){
            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.ttl.setVisibility(View.VISIBLE);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.lyout.setVisibility(View.VISIBLE);
        }else{
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.ttl.setVisibility(View.GONE);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.lyout.setVisibility(View.GONE);
        }
        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.layRCProof.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.GONE);

        if (dateCheck!=null && !dateCheck.equalsIgnoreCase("")){
            binding.layoutNewUnitDetails.etYearOfStructure.setText(dateCheck);
            binding.layoutNewUnitDetails.etYearOfStructureComm.setText(dateCheck);
        }

        if (binding.layoutNewUnitDetails.etExistenceSince.getText().toString().equalsIgnoreCase("")) {
            binding.layoutNewUnitDetails.etYearOfStructure.setText("");
        }
        if (binding.layoutNewUnitDetails.etExistenceSinceComm.getText().toString().equalsIgnoreCase("")) {
            binding.layoutNewUnitDetails.etYearOfStructureComm.setText("");
        }

        binding.layoutNewUnitDetails.etYearOfStructure.setTag(dateCheck);
        binding.layoutNewUnitDetails.etYearOfStructureComm.setTag(dateCheck);
        binding.layoutNewUnitDetails.etYearOfStructure.setText(Utils.getTextByTag(Constants.domain_structure_year,binding.layoutNewUnitDetails.etYearOfStructure.getText().toString()));
        binding.layoutNewUnitDetails.etYearOfStructureComm.setText(Utils.getTextByTag(Constants.domain_structure_year,binding.layoutNewUnitDetails.etYearOfStructureComm.getText().toString()));
        setCounts(year);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setFocusChange_OnTouchForNagarName(AutoCompleteTextView autoCompleteTextView, ArrayList<String> listItems) {

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            try {
                if (hasFocus) {
                    autoCompleteTextView.setError(null, null);
                    autoCompleteTextView.showDropDown();
                } else {
                    if (!listItems.contains(autoCompleteTextView.getText().toString())) {
                        // Utils.setError(autoCompleteTextView, "Select correct value from Dropdown", activity);
                        Toast.makeText(activity, "Select correct value from Nagar List", Toast.LENGTH_SHORT).show();
                        autoCompleteTextView.setError("Select correct value from Nagar List");
                        autoCompleteTextView.setText("");
                        binding.layoutNewUnitDetails.etNameNagarOther.setText("");
                        binding.layoutNewUnitDetails.etNameNagarOther.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
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
                String selectedNagar = adapterView.getAdapter().getItem(i).toString();
                if (selectedNagar.equals(Constants.dropdown_other)) {
                    binding.layoutNewUnitDetails.etNameNagarOther.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNewUnitDetails.etNameNagarOther.setVisibility(View.GONE);
                    binding.layoutNewUnitDetails.etNameNagarOther.setText("");
                }

                autoCompleteTextView.setTag(selectedNagar);
            } catch (Exception ex) {
                AppLog.logData(activity, ex.getMessage());
                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
            }
        });
    }

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

                        if (autoCompleteTextView.getId() == binding.layoutNewUnitDetails.autoCompRespondentRelation.getId()) {
                            Toast.makeText(activity, "Select correct value from Relationship with HOH", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Relationship with HOH");
                            binding.layoutNewUnitDetails.etRespondentRelation.setText("");
                            binding.layoutNewUnitDetails.etRespondentRelation.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getId()) {
                            Toast.makeText(activity, "Select correct value from Relationship with HOH", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Relationship with HOH");
                            binding.layoutNewUnitDetails.etRespondentRelationHoh.setText("");
                            binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getId()) {
                            Toast.makeText(activity, "Select correct value from Relationship with Owner/Employer", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Relationship with Owner/Employer");
                            binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText("");
                            binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.GONE);
                        }
                    }
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
                AutoCompleteModal selected = (AutoCompleteModal) adapterView.getAdapter().getItem(i);

                if(autoCompleteTextView.getId() == binding.layoutNewUnitDetails.autoCompRespondentRelation.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRespondentRelation.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRespondentRelation.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etRespondentRelation.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etRespondentRelationHoh.setText("");
                    }
                } else if(autoCompleteTextView.getId() == binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setVisibility(View.GONE);
                        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText("");
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
            }else if (requestCode == selectVideoCamera) {
                 if (data != null) {
                    isOkFileExtensions = true;
                    finalFile = captureVideoPath;
                    Log.e("video url1:",finalUri+"new"+ finalFile);
                    finalUri = videoUri;

                    isOkFileSize = true;
                    finalFileName = finalFile.getName();
//                    if (FFprobe.getMediaInformation(finalFile.getPath()).getAllProperties().toString().contains("\"codec_name\":\"h264\"")){
//                            Toast.makeText(activity, "exist 264", Toast.LENGTH_SHORT).show();
//                    }else {
//                        Utils.convertH263ToH264(activity, finalFile.getPath(), finalFile.getParentFile() + "/new_" + finalFileName + ".mp4");
//                    }

                } else {
                    Utils.shortToast("Capture again.", activity);
                }
            }else if (requestCode == GALLERYVIDEO) {
//                if (data != null) {
//
                    finalUri = data.getData();
//                    isOkFileExtensions = true;
//                    isOkFileSize = true;
//                    String path = getMediaPath(finalUri);
//                    finalFile = saveVideoFile(path);
////                    finalFile = Utils.copyFile(activity, finalUri, target_relative_path, target_name);
//                    finalFileName = finalFile.getName();
//                    Log.e("video url1:",finalUri+"new"+ finalFile);
//                }
                if (okVideoExtensions.contains(Utils.getFileExt(finalUri, activity))) {
                    isOkFileExtensions = true;
                    if (getFileSizeFromUri(activity, finalUri)) {
                        isOkFileSize = true;
                        if (!existingVideoFile.equals("")){
                            String[] arrOfStr = existingVideoFile.split("\\.");
                            unitVideoFileName = arrOfStr[0];
                        }
                        finalFile = Utils.copyFile(activity, finalUri, unitVideoFilePath, unitVideoFileName);
                        finalFileName = finalFile.getName();
//                        if (FFprobe.getMediaInformation(finalFile.getPath()).getAllProperties().toString().contains("\"codec_name\":\"h264\"")){
////                            Toast.makeText(activity, "exist 264", Toast.LENGTH_SHORT).show();
//                        }else {
//                            Utils.convertH263ToH264(activity, finalFile.getPath(), finalFile.getParentFile() + "/new_" + unitVideoFileName + ".mp4");
//                        }
                        } else {
                        isOkFileSize = false;
                    }
                } else {
                    isOkFileSize = false;
                    isOkFileExtensions = false;
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
                if(requestCode == GALLERYVIDEO){
                    Utils.showMessagePopup("." + Utils.getFileExt(finalUri, activity) + " is an invalid attachment type. Accepted file types are mp4,wmv,flv,and mov.", activity);
                }else{
                    Utils.showMessagePopup("." + Utils.getFileExt(finalUri, activity) + " is an invalid attachment type. Accepted file types are jpg, png, jpeg, and pdf.", activity);
                }
                return;
            }

            if (!isOkFileSize) {
                return;
            }

            if (finalUri != null && finalFile != null) {
                try {
                    sss = 0;
                    // if (!finalFileName.contains(".pdf"))
                    CryptoUtilsTest.encryptFileinAES(finalFile, 1);
                    switch (attachmentFor) {
                        case Constants.DL:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.EBILL:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.PASSPORT:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.RATIONCARD:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.CHAIN:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.RENTAGREEMENT:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.AFFIDAVIT:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.VOTERID:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.ADHARCARD:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;






                        case Constants.survey_unique:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                            case Constants.Police_or_traffic_or_any_authority_permission:
                            // binding.layoutNewUnitDetails.etFestivalDoc.setText("1 out of 1 attached");
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Govt_Approval_document:
                            // binding.layoutNewUnitDetails.etApprovalDoc.setText("1 out of 1 attached");
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.NOC_document:
                            // binding.layoutNewUnitDetails.etNocDoc.setText("1 out of 1 attached");
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Proof_showing_names_of_Trustee:
                            // binding.layoutNewUnitDetails.etNameTrusteeProof.setText("1 out of 1 attached");
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Proof_of_Religious_Structure_Registered:
                            // binding.layoutNewUnitDetails.etReligioidProof.setText("1 out of 1 attached");
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Tenement_document:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.UnitDistometerPdfTypeRes:

                            pdfPathRes = finalFile.getPath();
                            List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false, Constants.UnitDistometerPdfTypeComm);
                            if ((ll1 != null && ll1.size() > 0) || (pdfPathComm != null && !pdfPathComm.equalsIgnoreCase(""))) {
                                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("2 out of 2 attached");
                            } else {
                                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                            }
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.UnitDistometerPdfTypeComm:
                            List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false, Constants.UnitDistometerPdfTypeRes);
                            pdfPathComm = finalFile.getPath();
                            if ((ll2 != null && ll2.size() > 0) || (pdfPathRes != null && !pdfPathRes.equalsIgnoreCase(""))) {
                                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("2 out of 2 attached");
                            } else {
                                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                            }
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.PermissionReceiptN:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.PropertyTaxPaymentReceiptLabelN:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.notN:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.tenAgreeOld:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.allOwn:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.drivingLic:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.affidevitVeri:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.hotelLicN:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.itrOr:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.esicOr:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.selfFamily:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.bankPassN:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.affidavit_N:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Gumasta_N_Area_N:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Elc_roll:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.NoticeLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.NoticeLabelSecond:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.LeaveLicenseOfficialLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.GovtAuthorityDeptLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.UnitDistometerVideoType:
//                            residentProofAttachmentCount++;
                            globalUnitVideoPath = finalFile.getPath();
                            // Utils.convertVideo(globalUnitVideoPath, globalUnitVideoPath);
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.BankStatementPostOffice:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.OnlyAadhar:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.elBillLF:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.gumasta_attachment_name:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.ElectricityBill:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.OwnerPhotoCopy:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.PermissionReceipt:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.ShareCert:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Voter2011:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.CertifiedHeadmaster:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.CertifiedEmployer:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.VoterOneYear:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.NoterizedSalePurchase:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.TenancyRent:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Payment40000:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.AadhaarOnly:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.ResLicense:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Payment60000:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.Payment60000Rc:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.ElcRoll:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.OwnerPhoto:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.VoteIdLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
//                        case Constants.PermissionLabel:
//                            residentProofAttachmentCount++;
//                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            break;
                        case Constants.AadhaarLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.EmployerLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.NotarizedLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.TenancyLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.PaymentReceiptLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.DrivingLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.PurchaseAgreementLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.PassportLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.GovernmentIDLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.VerifiedLetterLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.utilityLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.rationLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.telephoneLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.NotarizedRentLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.PropertyTaxPaidLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.BankPassbookLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.selfFamilyPensionLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.bankStatementLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.CreditCardStatementLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.DealCopyLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.InsuranceCopyLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.LeaveLicenseLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.GovtAuthorityLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SalariedBankAccountLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SalariedESICLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SalariedPrevious3Label:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SalariedPrevious6Label:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SalariedForm16Label:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SelfBankAccountLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SelfGSTLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SelfIncomeTaxLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SelfBalanceLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.SelfLastSixMonthLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.GumastaLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.HotelLicenseLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.RestaurantLicenseLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.ShareCertificateLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_scAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_scAttachmentListAdapter.setAttachmentListImageDetails(resident_scAttachmentList);
                            break;
                        case Constants.ElectricityConnectionBillLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.ElectricityConnectionBillLabelNew:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.BankAccNew:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.BnkPass:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.ElcNew:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.photoNew:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.photoCopyNew:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.permissionCopyNew:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.BMC_Pavti:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.Others_Type:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.Lease_Agree:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.Property_Tax_Receipt:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.Water_Bill:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                            break;
                        case Constants.PhotoPassLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ppAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ppAttachmentListAdapter.setAttachmentListImageDetails(resident_ppAttachmentList);
                            break;
                        case Constants.PhotoPassLabelN:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ppAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ppAttachmentListAdapter.setAttachmentListImageDetails(resident_ppAttachmentList);
                            break;
                        case Constants.NA_TaxLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_nataxAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_naTaxAttachmentListAdapter.setAttachmentListImageDetails(resident_nataxAttachmentList);
                            break;
                        case Constants.PropertyTaxPaymentReceiptLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ptprAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ptprAttachmentListAdapter.setAttachmentListImageDetails(resident_ptprAttachmentList);
                            break;
                        case Constants.ElectoralRollLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_erAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            resident_ErAttachmentListAdapter.setAttachmentListImageDetails(resident_erAttachmentList);
                            break;
                        case Constants.additionalSccsasLabel:
                            residentProofAdditionalAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            additionalSccsasAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            additionalSccsasAttachmentAdapter.setAttachmentListImageDetails(additionalSccsasAttachmentList);
                            break;
                        case Constants.additionalCiesaLabel:
                            residentProofAdditionalAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            additionalCiesaAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            additionalCiesaAttachmentAdapter.setAttachmentListImageDetails(additionalCiesaAttachmentList);
                            break;
                        case Constants.additionalAttachment3Label:
                            residentProofAdditionalAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            additionalAttachment3AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            additionalAttachment3AttachmentAdapter.setAttachmentListImageDetails(additionalAttachment3AttachmentList);
                            break;
                        case Constants.additionalAttachment4Label:
                            residentProofAdditionalAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            additionalAttachment4AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            additionalAttachment4AttachmentAdapter.setAttachmentListImageDetails(additionalAttachment4AttachmentList);
                            break;
                        case Constants.chainPsipcLabel:
                            residentProofChainAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            chainPsipcAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            chainPsipcAttachmentAdapter.setAttachmentListImageDetails(chainPsipcAttachmentList);
                            break;
                        case Constants.chainRaLabel:
                            residentProofChainAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            chainRaAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            chainRaAttachmentAdapter.setAttachmentListImageDetails(chainRaAttachmentList);
                            break;
                        case Constants.chainAttachment3Label:
                            residentProofChainAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            chainAttachment3AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            chainAttachment3AttachmentAdapter.setAttachmentListImageDetails(chainAttachment3AttachmentList);
                            break;
                        case Constants.chainAttachment4Label:
                            residentProofChainAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            chainAttachment4AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            chainAttachment4AttachmentAdapter.setAttachmentListImageDetails(chainAttachment4AttachmentList);
                            break;
                        case Constants.commercialShopEstLabel:
                            licenseProofAttachmentListCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            licenseProofSeAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            licenseProofSeAttachmentListAdapter.setAttachmentListImageDetails(licenseProofSeAttachmentList);
                            break;
                        case Constants.commercialRestrHotelLabel:
                            licenseProofAttachmentListCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            licenseProofRhlAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            licenseProofRhlAttachmentListAdapter.setAttachmentListImageDetails(licenseProofRhlAttachmentList);
                            break;
                        case Constants.commercialFoodDrugLabel:
                            licenseProofAttachmentListCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            licenseProofFdlAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            licenseProofFdlAttachmentListAdapter.setAttachmentListImageDetails(licenseProofFdlAttachmentList);
                            break;
                        case Constants.commercialFactoryActLabel:
                            licenseProofAttachmentListCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            licenseProofFalAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            licenseProofFalAttachmentListAdapter.setAttachmentListImageDetails(licenseProofFalAttachmentList);
                            break;
                        case Constants.Attachment1Label:
                            if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                                religiousAttachmentCount++;
                            else otherAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            religiousOtherA1AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            religiousOtherA1AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA1AttachmentList);

                            break;
                        case Constants.Attachment2Label:
                            if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                                religiousAttachmentCount++;
                            else otherAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            religiousOtherA2AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            religiousOtherA2AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA2AttachmentList);

                            break;
                        case Constants.Attachment3Label:
                            if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                                religiousAttachmentCount++;
                            else otherAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            religiousOtherA3AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            religiousOtherA3AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA3AttachmentList);

                            break;
                        case Constants.Attachment4Label:
                            if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                                religiousAttachmentCount++;
                            else otherAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            religiousOtherA4AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            religiousOtherA4AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA4AttachmentList);

                            break;
                        case Constants.MemberPhotograph:
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberPhotographAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberPhotographAttachmentListAdapter.setAttachmentListImageDetails(memberPhotographAttachmentList);
                            break;
                        case Constants.AadharCardAttachment:
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberAdhaarCardAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberAdhaarCardAttachmentListAdapter.setAttachmentListImageDetails(memberAdhaarCardAttachmentList);
                            break;
                        case Constants.PanCardttachment:
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberPanCardAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberPanCardAttachmentListAdapter.setAttachmentListImageDetails(memberPanCardAttachmentList);
                            break;
                        case Constants.SurveyPavti:
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberPanCardAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberPanCardAttachmentListAdapter.setAttachmentListImageDetails(memberPanCardAttachmentList);
                            break;
                        case Constants.OthersDocument:
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberPanCardAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
//                            memberPanCardAttachmentListAdapter.setAttachmentListImageDetails(memberPanCardAttachmentList);
                            break;
                        case Constants.OwnerPhotoLable:
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
                        case Constants.UnitDistometerPdfType:
                            sss = 1;
                            globalUnitPdfPath = finalFile.getPath();
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            binding.layoutNewUnitDetails.etPdfDistometer.setText("1 out of 1 attached");
                            binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 1 attached");
                            break;
                        case Constants.PanchnamaLable:
                            globalPanchnamaPath = finalFile.getPath();
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            File imgFile = new File(globalPanchnamaPath);
                            if (imgFile.exists()) {
                                try {
                                    CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                                    Bitmap myBitmap = BitmapFactory.decodeFile(globalPanchnamaPath);
                                    tempImageButton.setImageBitmap(myBitmap);
                                    tempImageButton.setVisibility(View.VISIBLE);
                                    tempImageLayout.setVisibility(View.VISIBLE);
                                } catch (CryptoException e) {
                                    AppLog.logData(activity, e.getMessage());
                                    throw new RuntimeException(e);
                                }


                            }
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
                        } else if (cc.equals("member")) {
                            unit_unique_id = memberUniqueId;
                            unitUniqueId = memberUniqueId;
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
//                        updObj=localSurveyDbViewModel.getMediaInfoDataByObjId(updObj.getObejctId(),updObj.getParent_unique_id()).get(0);
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
//                        setCounts();
                    }
                    setCounts(year);
                } catch (CryptoException e) {
                    AppLog.logData(activity, e.getMessage());
                    Utils.shortToast("Error while encrypting the file.", activity);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void setupImages(List<MediaInfoDataModel> mediaInfoDataModels) {

        for (MediaInfoDataModel mediaInfoDataModel : mediaInfoDataModels) {
            if (mediaInfoDataModel != null && mediaInfoDataModel.getFilename() != null) {
                getPreviousMediaFileName.add(mediaInfoDataModel.getFilename());
                File finalFile = new File(mediaInfoDataModel.getItem_url());
                String finalFileName = mediaInfoDataModel.getFilename();

                try {

                    if (finalFile.exists() || mediaInfoDataModel.getItem_url().startsWith("https:/")) {

                        if (finalFileName.contains(Constants.share_certificate_attachment_name)) {
                            residentProofAttachmentCount++;
                            resident_scAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            resident_scAttachmentListAdapter.setAttachmentListImageDetails(resident_scAttachmentList);
                        } else if (finalFileName.contains(Constants.electric_bill_attachment_name)) {
                            residentProofAttachmentCount++;
                            resident_ecbAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                        } else if (finalFileName.contains(Constants.photo_pass_attachment_name)) {
                            residentProofAttachmentCount++;
                            resident_ppAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            resident_ppAttachmentListAdapter.setAttachmentListImageDetails(resident_ppAttachmentList);
                        } else if (finalFileName.contains(Constants.na_tax_attachment_name)) {
                            residentProofAttachmentCount++;
                            resident_nataxAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            resident_naTaxAttachmentListAdapter.setAttachmentListImageDetails(resident_nataxAttachmentList);
                        } else if (finalFileName.contains(Constants.property_tax_attachment_name)) {
                            residentProofAttachmentCount++;
                            resident_ptprAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            resident_ptprAttachmentListAdapter.setAttachmentListImageDetails(resident_ptprAttachmentList);
                        } else if (finalFileName.contains(Constants.electrol_roll_attachment_name)) {
                            residentProofAttachmentCount++;
                            resident_erAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            resident_ErAttachmentListAdapter.setAttachmentListImageDetails(resident_erAttachmentList);
                        } else if (finalFileName.contains(Constants.eductational_certificate_attachment_name)) {
                            residentProofAdditionalAttachmentCount++;
                            additionalSccsasAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            additionalSccsasAttachmentAdapter.setAttachmentListImageDetails(additionalSccsasAttachmentList);
                        } else if (finalFileName.contains(Constants.employer_certificate_attachment_name)) {
                            residentProofAdditionalAttachmentCount++;
                            additionalCiesaAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            additionalCiesaAttachmentAdapter.setAttachmentListImageDetails(additionalCiesaAttachmentList);
                        } else if (finalFileName.contains(Constants.additionalDocument_attachment3_name)) {
                            residentProofAdditionalAttachmentCount++;
                            additionalAttachment3AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            additionalAttachment3AttachmentAdapter.setAttachmentListImageDetails(additionalAttachment3AttachmentList);
                        } else if (finalFileName.contains(Constants.additionalDocument_attachment4_name)) {
                            residentProofAdditionalAttachmentCount++;
                            additionalAttachment4AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            additionalAttachment4AttachmentAdapter.setAttachmentListImageDetails(additionalAttachment4AttachmentList);
                        } else if (finalFileName.contains(Constants.ownership_proof_attachment_name)) {
                            residentProofChainAttachmentCount++;
                            chainPsipcAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            chainPsipcAttachmentAdapter.setAttachmentListImageDetails(chainPsipcAttachmentList);
                        } else if (finalFileName.contains(Constants.rent_agreement_attachment_name)) {
                            residentProofChainAttachmentCount++;
                            chainRaAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            chainRaAttachmentAdapter.setAttachmentListImageDetails(chainRaAttachmentList);
                        } else if (finalFileName.contains(Constants.chainDocument_attachment3_name)) {
                            residentProofChainAttachmentCount++;
                            chainAttachment3AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            chainAttachment3AttachmentAdapter.setAttachmentListImageDetails(chainAttachment3AttachmentList);
                        } else if (finalFileName.contains(Constants.chainDocument_attachment4_name)) {
                            residentProofChainAttachmentCount++;
                            chainAttachment4AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            chainAttachment4AttachmentAdapter.setAttachmentListImageDetails(chainAttachment4AttachmentList);
                        } else if (finalFileName.contains(Constants.shop_establishment_attachment_name)) {
                            licenseProofAttachmentListCount++;
                            licenseProofSeAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            licenseProofSeAttachmentListAdapter.setAttachmentListImageDetails(licenseProofSeAttachmentList);
                        } else if (finalFileName.contains(Constants.restaurant_hotel_attachment_name)) {
                            licenseProofAttachmentListCount++;
                            licenseProofRhlAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            licenseProofRhlAttachmentListAdapter.setAttachmentListImageDetails(licenseProofRhlAttachmentList);
                        } else if (finalFileName.contains(Constants.food_drug_attachment_name)) {
                            licenseProofAttachmentListCount++;
                            licenseProofFdlAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            licenseProofFdlAttachmentListAdapter.setAttachmentListImageDetails(licenseProofFdlAttachmentList);
                        } else if (finalFileName.contains(Constants.factory_act_attachment_name)) {
                            licenseProofAttachmentListCount++;
                            licenseProofFalAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            licenseProofFalAttachmentListAdapter.setAttachmentListImageDetails(licenseProofFalAttachmentList);
                        } else if (finalFileName.contains(Constants.attachment1_attachment_name)) {
                            if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                                religiousAttachmentCount++;
                            else otherAttachmentCount++;
                            religiousOtherA1AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            religiousOtherA1AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA1AttachmentList);
                        } else if (finalFileName.contains(Constants.attachment2_attachment_name)) {
                            if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                                religiousAttachmentCount++;
                            else otherAttachmentCount++;
                            religiousOtherA2AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            religiousOtherA2AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA2AttachmentList);
                        } else if (finalFileName.contains(Constants.attachment3_attachment_name)) {
                            if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                                religiousAttachmentCount++;
                            else otherAttachmentCount++;
                            religiousOtherA3AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            religiousOtherA3AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA3AttachmentList);
                        } else if (finalFileName.contains(Constants.attachment4_attachment_name)) {
                            if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                                religiousAttachmentCount++;
                            else otherAttachmentCount++;
                            religiousOtherA4AttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            religiousOtherA4AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA4AttachmentList);
                        } else if (finalFileName.contains(Constants.photograph_attachment_name)) {
                            memberPhotographAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            memberPhotographAttachmentListAdapter.setAttachmentListImageDetails(memberPhotographAttachmentList);
                        } else if (finalFileName.contains("Aadhar")) {
                            memberAdhaarCardAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            memberAdhaarCardAttachmentListAdapter.setAttachmentListImageDetails(memberAdhaarCardAttachmentList);
                        } else if (finalFileName.contains(Constants.pan_card_attachment_name)) {
                            memberPanCardAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            memberPanCardAttachmentListAdapter.setAttachmentListImageDetails(memberPanCardAttachmentList);
                        }
                    }
                } catch (Exception e) {
                    AppLog.logData(activity,e.getMessage());
                }
            }
        }

    }

    @Override
    public void onAttachmentNameTextClicked(AttachmentListImageDetails attachmentListImageDetails) {
        try {
            String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;
            if (cc.equals("hoh")) {
                unit_unique_id = hohUniqueId;
                unitUniqueId = hohUniqueId;
            } else if (cc.equals("member")) {
                unit_unique_id = memberUniqueId;
                unitUniqueId = memberUniqueId;
            }
            if (attachmentListImageDetails.getFilePath().startsWith("https:/")) {
                Utils.shortToast("File not available for offline use.", activity);
            } else {
                File file = attachmentListImageDetails.getFile();
                if (file.exists()) {
                    CryptoUtilsTest.encryptFileinAES(attachmentListImageDetails.getFile(), 2);
                    if (attachmentListImageDetails.getFileName().contains(".pdf")) {
//                try {
//                    Utils.updateProgressMsg("Please wait..", activity);
//                    CryptoUtilsTest.encryptFileinAES(attachmentListImageDetails.getFile(), 2);
//                    pdfPathFile = attachmentListImageDetails.getFile();

//                    new Handler().postDelayed(() -> {

                        pdfPathFile = file;
//                Uri ppp = Uri.fromFile(file);
                        Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", attachmentListImageDetails.getFile());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        activity.startActivity(intent);


//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.setDataAndType(uri, activity.getContentResolver().getType(Uri.fromFile(attachmentListImageDetails.getFile())));
//                if (intent.resolveActivity(activity.getPackageManager()) == null) {
//                    Utils.shortToast("No app to open!", activity);
//                } else activity.startActivity(intent);

//                        Utils.dismissProgress();
//                    }, 3000);
//
//                } catch (CryptoException e) {
//                    Utils.shortToast("Unable to open!", activity);
//                    throw new RuntimeException(e);
//                }
                    }
                    else  if(attachmentListImageDetails.getFileName().contains(".mp4") || attachmentListImageDetails.getFileName().contains(".mov")
                    || attachmentListImageDetails.getFileName().contains(".wmv") || attachmentListImageDetails.getFileName().contains(".flv")){
//                    else  if(okVideoExtensions.contains(Utils.getFileExt(Uri.parse(attachmentListImageDetails.getFileName()), activity))){
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
       if (buttonName.equalsIgnoreCase(Constants.UnitDistometerVideo)){
           globalUnitVideoPath ="";
       }
       if (buttonName.equalsIgnoreCase(Constants.UnitDistometerPdfType)){
           globalUnitPdfPath ="";
           binding.layoutNewUnitDetails.etPdfDistometer.setText("0 out of 1 attached");
           binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
       }else if (buttonName.equalsIgnoreCase(Constants.UnitDistometerPdfTypeRes) || attachmentFor.equalsIgnoreCase("unit_layout_res")){
           pdfPathRes ="";
           if(pdfPathComm!=null && !pdfPathComm.equalsIgnoreCase("")){
               binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
           }else{
               binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
           }
       }else if (buttonName.equalsIgnoreCase(Constants.UnitDistometerPdfTypeComm) || attachmentFor.equalsIgnoreCase("unit_layout_comm")){
           pdfPathComm ="";
           if(pdfPathRes!=null && !pdfPathRes.equalsIgnoreCase("")){
               binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
           }else{
               binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
           }
       }
        switch (attachmentType) {
            case Constants.ShareCertificateLabel:
                resident_scAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_scAttachmentListAdapter.setAttachmentListImageDetails(resident_scAttachmentList);
                break;
            case Constants.ElectricityConnectionBillLabel:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;
            case Constants.ElectricityConnectionBillLabelNew:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;
                case Constants.BankAccNew:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;
                case Constants.BnkPass:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;case Constants.ElcNew:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;case Constants.photoNew:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;case Constants.photoCopyNew:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;case Constants.permissionCopyNew:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;
                case Constants.BMC_Pavti:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;
            case Constants.Water_Bill:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;
            case Constants.Property_Tax_Receipt:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;
            case Constants.Lease_Agree:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;
            case Constants.Others_Type:
                resident_ecbAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ecbAttachmentListAdapter.setAttachmentListImageDetails(resident_ecbAttachmentList);
                break;
            case Constants.PhotoPassLabel:
                resident_ppAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ppAttachmentListAdapter.setAttachmentListImageDetails(resident_ppAttachmentList);
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
            case Constants.ElectoralRollLabel:
                resident_erAttachmentList.remove(attachmentListImageDetails);
                residentProofAttachmentCount--;
                resident_ErAttachmentListAdapter.setAttachmentListImageDetails(resident_erAttachmentList);
                break;

            case Constants.additionalSccsasLabel:
                additionalSccsasAttachmentList.remove(attachmentListImageDetails);
                residentProofAdditionalAttachmentCount--;
                additionalSccsasAttachmentAdapter.setAttachmentListImageDetails(additionalSccsasAttachmentList);
                break;
            case Constants.additionalCiesaLabel:
                additionalCiesaAttachmentList.remove(attachmentListImageDetails);
                residentProofAdditionalAttachmentCount--;
                additionalCiesaAttachmentAdapter.setAttachmentListImageDetails(additionalCiesaAttachmentList);
                break;
            case Constants.additionalAttachment3Label:
                additionalAttachment3AttachmentList.remove(attachmentListImageDetails);
                residentProofAdditionalAttachmentCount--;
                additionalAttachment3AttachmentAdapter.setAttachmentListImageDetails(additionalAttachment3AttachmentList);
                break;
            case Constants.additionalAttachment4Label:
                additionalAttachment4AttachmentList.remove(attachmentListImageDetails);
                residentProofAdditionalAttachmentCount--;
                additionalAttachment4AttachmentAdapter.setAttachmentListImageDetails(additionalAttachment4AttachmentList);
                break;

            case Constants.chainPsipcLabel:
                chainPsipcAttachmentList.remove(attachmentListImageDetails);
                residentProofChainAttachmentCount--;
                chainPsipcAttachmentAdapter.setAttachmentListImageDetails(chainPsipcAttachmentList);
                break;
            case Constants.chainRaLabel:
                chainRaAttachmentList.remove(attachmentListImageDetails);
                residentProofChainAttachmentCount--;
                chainRaAttachmentAdapter.setAttachmentListImageDetails(chainRaAttachmentList);
                break;
            case Constants.chainAttachment3Label:
                chainAttachment3AttachmentList.remove(attachmentListImageDetails);
                residentProofChainAttachmentCount--;
                chainAttachment3AttachmentAdapter.setAttachmentListImageDetails(chainAttachment3AttachmentList);
                break;
            case Constants.chainAttachment4Label:
                chainAttachment4AttachmentList.remove(attachmentListImageDetails);
                residentProofChainAttachmentCount--;
                chainAttachment4AttachmentAdapter.setAttachmentListImageDetails(chainAttachment4AttachmentList);
                break;

            case Constants.commercialShopEstLabel:
                licenseProofSeAttachmentList.remove(attachmentListImageDetails);
                licenseProofAttachmentListCount--;
                licenseProofSeAttachmentListAdapter.setAttachmentListImageDetails(licenseProofSeAttachmentList);
                break;
            case Constants.commercialRestrHotelLabel:
                licenseProofRhlAttachmentList.remove(attachmentListImageDetails);
                licenseProofAttachmentListCount--;
                licenseProofRhlAttachmentListAdapter.setAttachmentListImageDetails(licenseProofRhlAttachmentList);
                break;
            case Constants.commercialFoodDrugLabel:
                licenseProofFdlAttachmentList.remove(attachmentListImageDetails);
                licenseProofAttachmentListCount--;
                licenseProofFdlAttachmentListAdapter.setAttachmentListImageDetails(licenseProofFdlAttachmentList);
                break;
            case Constants.commercialFactoryActLabel:
                licenseProofFalAttachmentList.remove(attachmentListImageDetails);
                licenseProofAttachmentListCount--;
                licenseProofFalAttachmentListAdapter.setAttachmentListImageDetails(licenseProofFalAttachmentList);
                break;

            case Constants.Attachment1Label:
                religiousOtherA1AttachmentList.remove(attachmentListImageDetails);
                religiousOtherA1AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA1AttachmentList);
                if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                    religiousAttachmentCount--;
                else otherAttachmentCount--;
                break;

            case Constants.Attachment2Label:
                religiousOtherA2AttachmentList.remove(attachmentListImageDetails);
                religiousOtherA2AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA2AttachmentList);
                if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                    religiousAttachmentCount--;
                else otherAttachmentCount--;
                break;

            case Constants.Attachment3Label:
                religiousOtherA3AttachmentList.remove(attachmentListImageDetails);
                religiousOtherA3AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA3AttachmentList);
                if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                    religiousAttachmentCount--;
                else otherAttachmentCount--;
                break;

            case Constants.Attachment4Label:
                religiousOtherA4AttachmentList.remove(attachmentListImageDetails);
                religiousOtherA4AttachmentListAdapter.setAttachmentListImageDetails(religiousOtherA4AttachmentList);
                if (binding.layoutUnitDetailInfo.radioUnitUsageReligious.isChecked())
                    religiousAttachmentCount--;
                else otherAttachmentCount--;
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
            case Constants.UnitDistometerVideoType:
                globalUnitVideoPath ="";
                break;
            case Constants.UnitDistometerPdfType:
                globalUnitPdfPath = "";
                binding.layoutNewUnitDetails.etPdfDistometer.setText("0 out of 1 attached");
                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
                break;
            case Constants.UnitDistometerPdfTypeRes:
                pdfPathRes = "";
                if (pdfPathComm != null && !pdfPathComm.equalsIgnoreCase("")) {
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                } else {
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                }
                break;
            case Constants.UnitDistometerPdfTypeComm:
                pdfPathComm = "";
                if (pdfPathRes != null && !pdfPathRes.equalsIgnoreCase("")) {
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                } else {
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                }
                break;
        }

    }


//    class WizardPagerAdapter extends PagerAdapter {
//
//        public Object instantiateItem(ViewGroup collection, int position) {
//
//            int resId = 0;
//            switch (position) {
//                case 0:
//                    resId = R.id.page_one;
//                    break;
//                case 1:
//                    resId = R.id.page_two;
//                    break;
//            }
//            return activity.findViewById(resId);
//        }
//
//        @Override
//        public int getCount() {
//            return 2;
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0 == arg1;
//        }
//
//        @Override public void destroyItem(ViewGroup container, int position, Object object) {
//            // No super
//        }
//    }


    public int getAge(int year, int month, int dayOfMonth) {
        return Period.between(
                LocalDate.of(year, month, dayOfMonth),
                LocalDate.now()
        ).getYears();
    }


    private void showDocumentPopup(int usageType, int btnNumber, int selectedYear, String documentView) {
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
        TextView videoExtText = customLayout.findViewById(R.id.videoExtText);
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
        RecyclerView imageRecycler = customLayout.findViewById(R.id.imageRecycler);
        RecyclerView viewRecycler = customLayout.findViewById(R.id.viewRecycler);
        RecyclerView imageRecyclerDelete = customLayout.findViewById(R.id.imageRecyclerDelete);
        TextView textOne = customLayout.findViewById(R.id.txtOne);
        TextView textTwo = customLayout.findViewById(R.id.txtTwo);
        ImageView  prev = customLayout.findViewById(R.id.imgPrev);
        ImageView next = customLayout.findViewById(R.id.imgNext);
        LinearLayout rotateLyt = customLayout.findViewById(R.id.rotatelyt);

        if (documentView.equals(Constants.video)) {
            imageRecycler.setVisibility(View.GONE);
            videoExtText.setText(activity.getString(R.string.video_ext_txt));
            imageRecyclerDelete.setVisibility(View.GONE);
            buttonName = activity.getString(R.string.unit_distometer_video);
            List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
            newMediaInfoDataModels = getMediaInfoData;
            arr = activity.getResources().getStringArray(R.array.unit_distometer_video);
            String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
            autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
            setFocusChange_OnTouch(autoCompDocType);
        }else {
            videoExtText.setText(activity.getString(R.string.img_ext_txt));
            imageRecycler.setVisibility(View.VISIBLE);
            imageRecyclerDelete.setVisibility(View.VISIBLE);
        }

        if (usageType == 1 || usageType == 4) {
            if (btnNumber == 1) {
                if (selectedYear == 1999) {
                    buttonName = "Proof of Structure";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    if(floorFlag){
                        arr = activity.getResources().getStringArray(R.array.proof_of_str);
                    }else{
                        arr = activity.getResources().getStringArray(R.array.res_proof1_before2000);
                    }

                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2000) {
                    buttonName = "Proof of Structure";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    if(floorFlag){
                        arr = activity.getResources().getStringArray(R.array.proof_of_str);
                    }else{
                        arr = activity.getResources().getStringArray(R.array.res_proof1_after2000);
                    }
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2011) {
                    buttonName = "Proof of Structure";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    if(floorFlag){
                        arr = activity.getResources().getStringArray(R.array.proof_of_str);
                    }else{
                        arr = activity.getResources().getStringArray(R.array.res_proof1_after2011);
                    }
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 2) {
                if (selectedYear == 1999) {
                    buttonName = "Proof of identity";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.res_proof2_before2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Residential Proof-2");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.res_proof2_before2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.res_proof2_before2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2000) {
                    buttonName = "Proof of Address";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.res_proof2_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Residential Proof-2");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.res_proof2_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.res_proof2_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2011) {
                    buttonName = "Proof of Chain of Agreement";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.res_proof2_after2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Residential Proof-2");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.res_proof2_after2011);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.res_proof2_after2011)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 3) {
                if (selectedYear == 1999) {
                    buttonName = "1 Year Recent Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.recent_year_res_doc_list);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2000) {
                    buttonName = "Proof of Chain of Agreement";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.res_proof3_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Residential Proof-3");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.res_proof3_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.res_proof3_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2011) {
                    buttonName = "Proof of any other document";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.res_proof3_after2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Residential Proof-3");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.res_proof3_after2011);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.res_proof3_after2011)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 4) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of identity";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.res_proof4_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Residential Proof-4");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.res_proof4_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.res_proof4_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 5) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of payment receipt from SRA";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.res_proof5_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Residential Proof-5");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.res_proof5_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.res_proof5_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 6) {
                if (selectedYear == 2000) {
                    buttonName = "1 Year Recent Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.recent_year_res_doc_list);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 100) {
                if (selectedYear == 10000) {
                    buttonName = "Survey Pavti";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.survey_pavti);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 300) {
                if (selectedYear == 10000) {
                    buttonName = "Any other document";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.other_document);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, arr));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 400) {
                if (selectedYear == 10000) {
                    buttonName = "Owner Photo";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.owner_photo);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }


        }

        else if (usageType == 2 || usageType == 3) {

            if (btnNumber == 1) {
                if (selectedYear == 1999) {
                    buttonName = "Proof of Structure";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    if(floorFlag){
                        arr = activity.getResources().getStringArray(R.array.proof_of_str);
                    }else{
                        arr = activity.getResources().getStringArray(R.array.comm_proof1_before2000);
                    }
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat(activity.getString(R.string.commercial_proof_1));
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof1_before2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof1_before2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2000) {
                    buttonName = "Proof of Structure";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    if(floorFlag){
                        arr = activity.getResources().getStringArray(R.array.proof_of_str);
                    }else{
                        arr = activity.getResources().getStringArray(R.array.comm_proof1_after2000);
                    }
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat(activity.getString(R.string.commercial_proof_1));
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof1_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof1_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2011) {
                    buttonName = "Proof of Structure";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    if(floorFlag){
                        arr = activity.getResources().getStringArray(R.array.proof_of_str);
                    }else{
                        arr = activity.getResources().getStringArray(R.array.comm_proof1_after2011);
                    }
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat(activity.getString(R.string.commercial_proof_1));
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof1_after2011);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof1_after2011)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 2) {
                if (selectedYear == 1999) {
                    buttonName = activity.getString(R.string.commercial_proof_1);
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.comm_proof2_before2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Commercial Proof-2");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof2_before2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof2_before2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2000) {
                    buttonName = activity.getString(R.string.commercial_proof_1);
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.comm_proof2_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Commercial Proof-2");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof2_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof2_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2011) {
                    buttonName = activity.getString(R.string.residential_proof_2_post_2011);
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.comm_proof2_after2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Commercial Proof-2");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof2_after2011);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof2_after2011)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 3) {
                if (selectedYear == 1999) {
                    buttonName = "1 Year Recent Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.recent_year_comm_doc_list);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2000) {
                    buttonName = "Proof of Address";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.comm_proof3_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Commercial Proof-3");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof3_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof3_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2011) {
                    buttonName = "Proof of any other document";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.comm_proof3_after2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Commercial Proof-3");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof3_after2011);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof3_after2011)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 4) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of Chain of Agreement";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.comm_proof4_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Commercial Proof-4");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof4_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof4_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 5) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of identity";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.comm_proof5_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Commercial Proof-5");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof5_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof5_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 6) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of payment receipt from SRA";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.comm_proof6_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Commercial Proof-6");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof6_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof6_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 7) {
                if (selectedYear == 2000) {
                    buttonName = "1 Year Recent Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.recent_year_comm_doc_list);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 200) {
                if (selectedYear == 20000) {
                    buttonName = "Survey Pavti";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.survey_pavti);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 400) {
                if (selectedYear == 20000) {
                    buttonName = "Owner Photo";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.owner_photo);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }

        }

        else if (usageType == 5) {
            if (btnNumber == 1 && selectedYear == 1001) {
                buttonName = "hoh_member_Aadhar_Card";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_adhar);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 2 && selectedYear == 1001) {
                buttonName = "hoh_member_Pan_Card";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_pan);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 3 && selectedYear == 1001) {
                buttonName = "hoh_member_Ration_Card";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_ration);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 4 && selectedYear == 1001) {
                buttonName = "hoh_member_Death_Certificate";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_death_certificate);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 5 && selectedYear == 1001) {
                buttonName = "hoh_member_Salary_Proof";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_salary_proof);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 6 && selectedYear == 1001) {
                buttonName = "hoh_member_Disease_Proof";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, hohUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_deseas);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            }
        }

        else if (usageType == 6) {
            if (btnNumber == 1 && selectedYear == 1001) {
                buttonName = "hoh_member_Aadhar_Card";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_adhar);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 2 && selectedYear == 1001) {
                buttonName = "hoh_member_Pan_Card";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_pan);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 3 && selectedYear == 1001) {
                buttonName = "hoh_member_Ration_Card";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_ration);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 4 && selectedYear == 1001) {
                buttonName = "hoh_member_Death_Certificate";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_death_certificate);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 5 && selectedYear == 1001) {
                buttonName = "hoh_member_Salary_Proof";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_salary_proof);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            } else if (btnNumber == 6 && selectedYear == 1001) {
                buttonName = "hoh_member_Disease_Proof";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.hoh_member_deseas);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            }
        }

        else if (usageType == 900) {
            if (btnNumber == 1 && selectedYear == 900) {
                buttonName = "Notice_Pasted";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.unit_notice_type);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            }
        }

        else if (usageType == 9000) {
            if (btnNumber == 1 && selectedYear == 9000) {
                buttonName = "UnitType";
                List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                newMediaInfoDataModels = getMediaInfoData;
                arr = activity.getResources().getStringArray(R.array.unit_unique_type);
                String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                setFocusChange_OnTouch(autoCompDocType);
            }
        }
        /*
        RC
         */
        else if (usageType == 800) {

            if (btnNumber == 1) {
                if (selectedYear == 1999) {
                    buttonName = "Proof of Structure";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    if(floorFlag){
                        arr = activity.getResources().getStringArray(R.array.proof_of_str);
                    }else{
                        arr = activity.getResources().getStringArray(R.array.rc_proof1_before2000);
                    }
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2000) {
                    buttonName = "Proof of Structure";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    if(floorFlag){
                        arr = activity.getResources().getStringArray(R.array.proof_of_str);
                    }else{
                        arr = activity.getResources().getStringArray(R.array.rc_proof1_before2011);
                    }

                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2011) {
                    buttonName = "Proof of Structure";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    if(floorFlag){
                        arr = activity.getResources().getStringArray(R.array.proof_of_str);
                    }else{
                        arr = activity.getResources().getStringArray(R.array.rc_proof1_after2011);
                    }
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 2) {
                if (selectedYear == 1999) {
                    buttonName = activity.getString(R.string.commercial_proof_1);
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof2_before2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2000) {
                    buttonName = activity.getString(R.string.commercial_proof_1);
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof2_before2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (selectedYear == 2011) {
                    buttonName = activity.getString(R.string.residential_proof_2_post_2011);
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof2_after2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 3) {
                if (selectedYear == 1999) {
                    buttonName = "1 Year Recent Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof3_before2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
                else if (selectedYear == 2000) {
                    buttonName = "Proof of Address";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof_address_before2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
                else if (selectedYear == 2011) {
                    buttonName = "Proof of any other document";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof3_after2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 4) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of Chain of Agreement";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.comm_proof4_after2000);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
//                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCat("Commercial Proof-4");
//                    newMediaInfoDataModels=getMediaInfoData;
//                    arr=activity.getResources().getStringArray(R.array.comm_proof4_after2000);
//                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, activity.getResources().getStringArray(R.array.comm_proof4_after2000)));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 5) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of identity";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof4_before2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 6) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of payment receipt from SRA";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof5_before2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 7) {
                if (selectedYear == 2000) {
                    buttonName = "1 Year Recent Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof6_before2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 200) {
                if (selectedYear == 20000) {
                    buttonName = "Survey Pavti";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.survey_pavti);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            } else if (btnNumber == 400) {
                if (selectedYear == 20000) {
                    buttonName = "Owner Photo";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.owner_photo);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }

        }
        else if(usageType==1100){
            if (selectedYear == 1100) {
                if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                    buttonName = "unit_layout_rc";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_disto_pdf);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }else{
                    buttonName = "Unit_Layout";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.disto_pdf);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
        }

        else if(usageType==1300){
            if (btnNumber == 1) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of identity";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof4_before2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
            else if (btnNumber == 2) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of Chain of Agreement";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rent_ownership);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }

            else if (btnNumber == 3) {
                if (selectedYear == 2000) {
                    buttonName = "Proof of any other document";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rc_proof3_after2011);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
        }

        else if(usageType==1650) {
            if (btnNumber == 1650) {
                if (selectedYear == 2000) {
                    buttonName = AttName;
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rel_amen_proofs);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
        }

        else if(usageType==1550) {
            if (btnNumber == 1550) {
                if (selectedYear == 2000) {
                    buttonName = AttName;
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.tenem_docs);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
        }

        else if(usageType==1555) {
            if (btnNumber == 1555) {
                if (selectedYear == 2000) {
                    buttonName = AttName;
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.rel_str_docs);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
        }

        else if(usageType==1560) {
            if (btnNumber == 1560) {
                if (selectedYear == 2000) {
                    buttonName = AttName;
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.trustee_docs);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
        }

        else if(usageType==1565) {
            if (btnNumber == 1565) {
                if (selectedYear == 2000) {
                    buttonName = AttName;
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.noc_docs);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
        }

        else if(usageType==1570) {
            if (btnNumber == 1570) {
                if (selectedYear == 2000) {
                    buttonName = AttName;
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.approval_docs);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
        }

        else if(usageType==1575) {
            if (btnNumber == 1575) {
                if (selectedYear == 2000) {
                    buttonName = AttName;
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, unit_unique_id);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = activity.getResources().getStringArray(R.array.festiv_docs);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                }
            }
        }


//        title.setText(buttonName);
        title.setText(AttName);
        String[] finalArr = arr;
        autoCompDocType.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
//                Utils.shortToast("Position:"+ finalArr[position].toString(), activity);
//                if(finalArr[position].toString().equals("Pan Card") || finalArr[position].toString().equals("Electricity Connection Bill")){
//                    et_dob.setVisibility(View.GONE);
//                }else{
//                    et_dob.setVisibility(View.VISIBLE);
//                }
            }
        });


        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sss==1){
                    globalUnitPdfPath ="";
                    binding.layoutNewUnitDetails.etPdfDistometer.setText("0 out of 1 attached");
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
                }
                userAttachmentList = new ArrayList<>();
                dialog.dismiss();
            }
        });


        imageRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        imageRecycler.setAdapter(new HorizontalAdapter(new int[]{R.drawable.img_placeholder, R.mipmap.ic_launcher, R.drawable.img_placeholder, R.mipmap.ic_launcher, R.drawable.img_placeholder}, newMediaInfoDataModels, 1, activity));
        imageRecyclerDelete.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView addImageRecycler = customLayout.findViewById(R.id.addImageRecycler);
        addImageAdapter = new AttachmentListAdapter(activity, null, "", this);
        addImageRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        addImageRecycler.setAdapter(addImageAdapter);

//        imageRecyclerDelete.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
//        imageRecyclerDelete.setAdapter(new HorizontalAdapter(new int[]{R.drawable.img_placeholder,R.mipmap.ic_launcher,R.drawable.img_placeholder,R.mipmap.ic_launcher,R.drawable.img_placeholder},newMediaInfoDataModels,2,activity));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int  imageCurrentPosition=  ((LinearLayoutManager)viewRecycler.getLayoutManager()  ).findFirstVisibleItemPosition();
//                if(imageCurrentPosition>0){
                    viewRecycler.smoothScrollToPosition(imageCurrentPosition+1);
//                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int  imageCurrentPosition=  ((LinearLayoutManager)viewRecycler.getLayoutManager()  ).findFirstVisibleItemPosition();
                if(imageCurrentPosition>0){
                    viewRecycler.smoothScrollToPosition(imageCurrentPosition-1);
                }
            }
        });
        textOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewRecycler.smoothScrollToPosition(0);
            }
        });
        textTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewRecycler.smoothScrollToPosition(1);
            }
        });
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
//                if(userAttachmentList!=null && userAttachmentList.size()==0){
//                    addImageRecycler.setVisibility(View.GONE);
//                }else{
//                    addImageRecycler.setVisibility(View.VISIBLE);
//                }
            }
        });

        viewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userAttachmentList!=null && userAttachmentList.size()>0){
                    Utils.shortToast("Please add selected attachment first!",activity);
                }else{
                    userAttachmentList = new ArrayList<>();
                    edtypeProof.setText(buttonName);
                    addTab.setTextColor(activity.getResources().getColor(R.color.black));
                    viewTab.setTextColor(activity.getResources().getColor(R.color.summaryEditBoarderColor));
                    addLayout.setVisibility(View.GONE);
                    deleteLayout.setVisibility(View.GONE);
//                viewLayout.setVisibility(View.VISIBLE);
//                imageRecycler.setVisibility(View.GONE);
                    viewLayout.setVisibility(View.GONE);
                    viewNewLayout.setVisibility(View.VISIBLE);

                    imageRecyclerDelete.setVisibility(View.GONE);

                    if (cc.equals("hoh")) {
                        List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(buttonName, hohUniqueId, false);
                        newMediaInfoDataModels = getMediaInfoData;
                        new Handler().postDelayed(() -> {
                            viewRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            viewAttachAdapter = new ViewAttachAdapter(getMediaInfoData, 1, activity, hoh_relative_path, hohUniqueId, FormPageViewModel.this, localSurveyDbViewModel);
                            viewRecycler.setAdapter(viewAttachAdapter);
                        }, 1000);
                    } else if (cc.equals("member")) {
                        List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(buttonName, memberUniqueId, false);
                        newMediaInfoDataModels = getMediaInfoData;
                        new Handler().postDelayed(() -> {
                            viewRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            viewAttachAdapter = new ViewAttachAdapter(getMediaInfoData, 1, activity, member_relative_path, memberUniqueId, FormPageViewModel.this, localSurveyDbViewModel);
                            viewRecycler.setAdapter(viewAttachAdapter);
                        }, 1000);
                    } else {
                        String typeName="";
                        if(BtnName.equals("1555")){
                            typeName=Constants.Proof_of_Religious_Structure_Registered;
                        }else if(BtnName.equals("1560")){
                            typeName=Constants.Proof_showing_names_of_Trustee;
                        }else if(BtnName.equals("1565")){
                            typeName=Constants.NOC_document;
                        }else if(BtnName.equals("1570")){
                            typeName=Constants.Govt_Approval_document;
                        }else if(BtnName.equals("1575")){
                            typeName=Constants.Police_or_traffic_or_any_authority_permission;
                        }else if(BtnName.equals("1575")){
                            typeName=Constants.Police_or_traffic_or_any_authority_permission;
                        }
//                        else if(BtnName.equals("1550")){
//                            typeName=Constants.Tenement_document;
//                        }
                        else{
                            typeName="";
                        }
                        List<MediaInfoDataModel> getMediaInfoData=null;
                        if(!typeName.equals("")){
                            getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatTypeUnit(buttonName, unit_unique_id, false,typeName);
                        }else{
                            getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(buttonName, unit_unique_id, false);
                        }
//                        List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(buttonName, unit_unique_id, false);
                        newMediaInfoDataModels = getMediaInfoData;
                        if (newMediaInfoDataModels != null && newMediaInfoDataModels.size()>0){
                            List<MediaInfoDataModel> finalGetMediaInfoData = getMediaInfoData;
                            new Handler().postDelayed(() -> {
                                viewRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                viewAttachAdapter = new ViewAttachAdapter(finalGetMediaInfoData, 1, activity, unit_relative_path, unitUniqueId, FormPageViewModel.this, localSurveyDbViewModel);
                                viewRecycler.setAdapter(viewAttachAdapter);
                            }, 1000);
                        }else {
                            viewNoRecord.setVisibility(View.VISIBLE);
                            viewNewLayout.setVisibility(View.GONE);
                        }
                        if (newMediaInfoDataModels.size()>1){
                            rotateLyt.setVisibility(View.VISIBLE);
                        }

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
                if(sss==1){
                    globalUnitPdfPath ="";
                    binding.layoutNewUnitDetails.etPdfDistometer.setText("0 out of 1 attached");
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
                }
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
                    if (cc.equals("hoh")) {
                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), hoh_relative_path, hohUniqueId + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());
                    } else if (cc.equals("member")) {
                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), member_relative_path, memberUniqueId + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());
                    } else {
                        if (autoCompDocType.getText().toString().equals(Constants.UnitDistometerVideoType)) {
//                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), unit_relative_path, "S_"+previousStructureInfoPointDataModel.getGlobalId()+"U_"+"" + "distometer_video/" );
                            if ( globalUnitVideoPath.equals("")) {
                                 showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), unit_relative_path, unitVideoFileName);
                            }else {
                                Utils.shortToast("Please delete existing video to add new one",activity);
                            }
                        }else if (autoCompDocType.getText().toString().equals(Constants.UnitDistometerPdfType)) {
                            if ( globalUnitPdfPath.equals("")) {
                                attachmentFor = autoCompDocType.getText().toString();
                                target_relative_path = unit_relative_path;
                                target_name = "Unit_Layout" + "_" + Utils.getEpochDateStamp();
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/pdf");
                                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, true);
                                activity.startActivityForResult(Intent.createChooser(intent, "Select Pdf"), selectPdf);
                            }else {
                                Utils.shortToast("Please delete existing pdf to add new one",activity);
                            }
                        }else if (autoCompDocType.getText().toString().equals(Constants.UnitDistometerPdfTypeRes)) {
                            if (pdfPathRes.equals("")) {
                                attachmentFor = autoCompDocType.getText().toString();
                                target_relative_path = unit_relative_path;
                                target_name = "unit_layout_res" + "_" + Utils.getEpochDateStamp();
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/pdf");
                                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, true);
                                activity.startActivityForResult(Intent.createChooser(intent, "Select Pdf"), selectPdf);
                            }else {
                                Utils.shortToast("Please delete existing pdf to add new one",activity);
                            }
                        }else if (autoCompDocType.getText().toString().equals(Constants.UnitDistometerPdfTypeComm)) {
                            if (pdfPathComm.equals("")) {
                                attachmentFor = autoCompDocType.getText().toString();
                                target_relative_path = unit_relative_path;
                                target_name = "unit_layout_comm" + "_" + Utils.getEpochDateStamp();
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/pdf");
                                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, true);
                                activity.startActivityForResult(Intent.createChooser(intent, "Select Pdf"), selectPdf);
                            }else {
                                Utils.shortToast("Please delete existing pdf to add new one",activity);
                            }
                        }else {
                            if(autoCompDocType.getText().toString().equalsIgnoreCase("Attachment of the notice on door")){
                                // #970
                                String fileName = Utils.getAttachmentFileName("Notice_Pasted");
                                showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), unit_relative_path, fileName);
                            }else{
                                // #970
//                                if(BtnName.equals("1550")){
//                                    String fileName = Utils.getAttachmentFileName("tenement_doc");
//                                    showAttachmentAlertDialogButtonClicked("Tenement_document", unit_relative_path, fileName);
//                                }else
                                    if(BtnName.equals("1555")){
                                    String fileName = Utils.getAttachmentFileName("proof_of_religious_structure");
                                    showAttachmentAlertDialogButtonClicked(Constants.Proof_of_Religious_Structure_Registered, unit_relative_path, fileName);
                                }else if(BtnName.equals("1560")){
                                    String fileName = Utils.getAttachmentFileName("proof_of_trustee");
                                    showAttachmentAlertDialogButtonClicked(Constants.Proof_showing_names_of_Trustee, unit_relative_path, fileName);
                                }else if(BtnName.equals("1565")){
                                    String fileName = Utils.getAttachmentFileName("noc_doc");
                                    showAttachmentAlertDialogButtonClicked(Constants.NOC_document, unit_relative_path, fileName);
                                }else if(BtnName.equals("1570")){
                                    String fileName = Utils.getAttachmentFileName("govt_approval_doc");
                                    showAttachmentAlertDialogButtonClicked(Constants.Govt_Approval_document, unit_relative_path, fileName);
                                }else if(BtnName.equals("1575")){
                                    String fileName = Utils.getAttachmentFileName("authority_permission");
                                    showAttachmentAlertDialogButtonClicked(Constants.Police_or_traffic_or_any_authority_permission, unit_relative_path, fileName);
                                }else{
                                    String fileName = Utils.getAttachmentFileName(autoCompDocType.getText().toString());
                                    showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), unit_relative_path, fileName);
                                }
                            }
                        }
                    }

                }
            }
        });

        btnBrowseEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cc.equals("hoh")) {
                    showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), hoh_relative_path, hohUniqueId + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());
                } else if (cc.equals("member")) {
                    showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), member_relative_path, memberUniqueId + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());
                } else {
                    if (autoCompDocType.getText().toString().equals(Constants.UnitDistometerVideo)) {
//                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), unit_relative_path, unit_relative_path + "distometer_video/" );
                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), unitVideoFilePath, unitVideoFileName);
                    } else {
                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), unit_relative_path, unitUniqueId + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());
                    }
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
                }
                else {
                    addErrorLayout.setVisibility(View.GONE);
                    new Handler().postDelayed(() -> {
                        ArrayList<MediaInfoDataModel> attach;
                        if (cc.equals("hoh")) {
                            attach = getmediaInfoDataList(userAttachmentList, hoh_relative_path, Constants.hoh_infoLayer,
                                    hohUniqueId, hoh_rel_global_id);
                        } else if (cc.equals("member")) {
                            attach = getmediaInfoDataList(userAttachmentList, unit_relative_path, Constants.member_infoLayer,
                                    memberUniqueId, member_relative_path);
                        }
                        else {
//                            attach = getmediaInfoDataList(userAttachmentList, unit_relative_path, Constants.unit_infoLayer,
//                                    unit_unique_id, unit_rel_global_id);
                            if (Objects.equals(buttonName, activity.getString(R.string.unit_distometer_video)) && llvd != null && !llvd.isEmpty()){
                                llvd.get(0).setFilename(userAttachmentList.get(0).getFileName());
                                llvd.get(0).setItem_url(userAttachmentList.get(0).getFilePath());
                                llvd.get(0).setData_size((int) userAttachmentList.get(0).getFile().length());
                                llvd.get(0).setDate(new Date());
                                llvd.get(0).setLastEditedDate(new Date());
                                llvd.get(0).setUploaded(false);
                                llvd.get(0).setHaveDelete(false);
                                if(llvd.get(0).getGlobalId() != null && !llvd.get(0).getGlobalId().isEmpty())
                                    llvd.get(0).setLocal(false);
                                else
                                    llvd.get(0).setLocal(true);

                                llvd.get(0).setSurveyorDoc(true);
                                attach = (ArrayList<MediaInfoDataModel>) llvd;
                            }else{
                                attach = getmediaInfoDataList(userAttachmentList, unit_relative_path, Constants.unit_infoLayer,
                                        unit_unique_id, unit_rel_global_id);
                                if (attach != null && attach.size() > 0) {
                                    attach.get(0).setSurveyorDoc(false);
                                }
                            }
                        }
//                        ArrayList<MediaInfoDataModel> attach = getmediaInfoDataList(userAttachmentList, unit_relative_path, Constants.unit_infoLayer,
//                                unit_unique_id, unit_rel_global_id);
                        if (attach != null && attach.size() > 0) {
                            mediaInfoDataModels1 = new ArrayList<>();
                            // AppLog.e("doc remark: "+et_doc_remarks.getText().toString());
                            attach.get(0).setDocument_remarks(et_doc_remarks.getText().toString());
                            attach.get(0).setDocument_category(buttonName);
                            if (autoCompDocType.getText().toString().equalsIgnoreCase("Attachment of the notice on door")) {
                                attach.get(0).setDocument_type("Notice_Pasted");
                            } else if (autoCompDocType.getText().toString().equalsIgnoreCase("Notice_Pasted")) {
                                attach.get(0).setDocument_type("Notice_Pasted");
                            } else if (autoCompDocType.getText().toString().equalsIgnoreCase("Unit_Layout")) {
                                sss = 0;
                                attach.get(0).setDocument_type("Unit_Layout");
                            }else if (autoCompDocType.getText().toString().equalsIgnoreCase("UnitPhoto")) {
                                attach.get(0).setDocument_type("UnitPhoto");
                            }
//                            else if(BtnName.equals("1550") && autoCompDocType.getText().toString().equals("Bank Account or Passbook details")) {
//                                attach.get(0).setDocument_type("Bank Account/Passbook details");
//                            }
//                            else if(BtnName.equals("1650") && autoCompDocType.getText().toString().equals("Electricity Connection Receipt or Electricity Bill")) {
//                                attach.get(0).setDocument_type("Electricity Connection Receipt/ Electricity Bill");
//                            }
//                            else if(BtnName.equals("1650") && autoCompDocType.getText().toString().equals("Survey Pavti or Photo Pass")) {
//                                attach.get(0).setDocument_type("Photo pass/ Survey Pavti");
//                            }
//                            else if(BtnName.equals("1650") && autoCompDocType.getText().toString().equals("Permission or Receipt for Non-Agriculture use")) {
//                                attach.get(0).setDocument_type("Permission/Receipt for Non-Agriculture use");
//                            }
//                            else if(BtnName.equals("1650") && autoCompDocType.getText().toString().equals("Photocopy of electoral roll book")) {
//                                attach.get(0).setDocument_type("Photocopy of electrol roll book");
//                            }
                            else {
                                if(AttName.equals("Survey Pavti")){
//                                    binding.layoutNewUnitDetails.etSurveyPavti.setText("1 out of 1 attached");
                                }
                                if(AttName.equals("Photo with Unique ID")){
                                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttachePhoto.setText("1 out of 1 attached");
                                }
                                if(AttName.equals("Other Documents")){
                                    binding.layoutNewUnitDetails.layoutRelAmenAttachement.etOthers.setText("1 out of 1 attached");
                                }
                                attach.get(0).setDocument_type(autoCompDocType.getText().toString());
                            }
                            attach.get(0).setUploadMediaCount(userAttachmentList.size());

                            attach.get(0).setLocal(true);
                            if (Objects.equals(buttonName, activity.getString(R.string.unit_distometer_video)) && llvd != null && llvd.size() >0){
                                if(attach.get(0).getGlobalId() != null && !attach.get(0).getGlobalId().isEmpty())
                                    attach.get(0).setLocal(false);
                                else
                                    attach.get(0).setLocal(true);
                            }

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
                            AppLog.e("doc edited.....");
                            userAttachmentList = new ArrayList<>();
                            dialog.dismiss();
                            Utils.dismissProgress();
                            setCounts(year);
                            if (autoCompDocType.getText().toString().equalsIgnoreCase("Attachment of the notice on door")) {
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Notice_Pasted", unitUniqueId, false);
                                if (ll != null && ll.size() >= 0) {
                                    binding.layoutNewUnitDetails.etAttacheNotice.setText(ll.size() + " " + "out of 1 attached");
                                }
                            } else if (autoCompDocType.getText().toString().equalsIgnoreCase("Notice_Pasted")) {
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Notice_Pasted", unitUniqueId, false);
                                if (ll != null && ll.size() >= 1) {
                                    binding.layoutNewUnitDetails.etAttacheNotice.setText(ll.size() + " " + "out of 1 attached");
                                }
                            }else if (autoCompDocType.getText().toString().equalsIgnoreCase("UnitPhoto")) {
                                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("UnitType", unitUniqueId, false);
                                if (ll != null && ll.size() >= 1) {
                                    binding.layoutNewUnitDetails.etAttacheUnitUnique.setText(ll.size() + " " + "out of 1 attached");
                                }

                            }
                        }
                    }, 0);
                }
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    setCustomCount();
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    setCustomCount();
                }
//                BtnName="";
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void setCustomCount() {
        try {

//            et_surveyPavti

            new Handler().postDelayed(() -> {
                String cat="";
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    cat = "Religious Document";
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    cat = "Amenities Document";
                }
                String typeName="";
                typeName=Constants.Proof_of_Religious_Structure_Registered;
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(cat, unitUniqueId, false,typeName);
                binding.layoutNewUnitDetails.etReligioidProof.setText(ll1.size()+" attached");

                List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(cat, unitUniqueId, false,Constants.Proof_showing_names_of_Trustee);
                binding.layoutNewUnitDetails.etNameTrusteeProof.setText(ll2.size()+" attached");

                List<MediaInfoDataModel> ll3 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(cat, unitUniqueId, false,Constants.NOC_document);
                binding.layoutNewUnitDetails.etNocDoc.setText(ll3.size()+" attached");

                List<MediaInfoDataModel> ll4 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(cat, unitUniqueId, false,Constants.Govt_Approval_document);
                binding.layoutNewUnitDetails.etApprovalDoc.setText(ll4.size()+" attached");

                List<MediaInfoDataModel> ll5 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType(cat, unitUniqueId, false,Constants.Police_or_traffic_or_any_authority_permission);
                binding.layoutNewUnitDetails.etFestivalDoc.setText(ll5.size()+" attached");

                List<MediaInfoDataModel> ll6 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Supporting documents showing address", unitUniqueId, false);
                binding.layoutNewUnitDetails.etTenementDocs.setText(ll6.size()+" attached");

                List<MediaInfoDataModel> ll7 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("Survey Pavti", unitUniqueId, false,"Survey Pavti");
                binding.layoutNewUnitDetails.etSurveyPavti.setText(ll7.size()+" attached");

                String abc="";
                if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
                    abc = "documents_for_Religious_proof";
                }else if(unitStructureUsage.equals(Constants.AmenitiesCheckBox)){
                    abc = "documents_for_Amenities_proof";
                }

                List<MediaInfoDataModel> ll8 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(abc, unitUniqueId, false);
                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttacheProofRelAnim.setText(ll8.size()+" attached");

                List<MediaInfoDataModel> ll9 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etAttachePhoto.setText(ll9.size()+" attached");

                List<MediaInfoDataModel> ll10 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
                binding.layoutNewUnitDetails.layoutRelAmenAttachement.etOthers.setText(ll10.size()+" attached");
            }, 2000);

        }catch (Exception ex){
            ex.getMessage();
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

    @Override
    public void onAttachmentUpdateClicked(MediaInfoDataModel newMediaInfoDataModels) {

        if (newMediaInfoDataModels != null) {
            if (cc.equals("hoh")) {
                localSurveyDbViewModel.setIsUploaded(hohUniqueId, newMediaInfoDataModels.getObejctId(), false);
            } else if (cc.equals("member")) {
                localSurveyDbViewModel.setIsUploaded(hohMemberUniqueId, newMediaInfoDataModels.getObejctId(), false);
            } else {
                localSurveyDbViewModel.setIsUploaded(unitUniqueId, newMediaInfoDataModels.getObejctId(), false);
                localSurveyDbViewModel.setRemarksByMediaId(newMediaInfoDataModels.getMediaId(), newMediaInfoDataModels.getDocument_remarks());
            }
//        localSurveyDbViewModel.setIsUploaded(unitUniqueId, newMediaInfoDataModels.getObejctId(), false);
            userAttachmentList = new ArrayList<>();
            if (dialogGlobal != null) {
                dialogGlobal.dismiss();
            }
            if(newMediaInfoDataModels.getDocument_category().equalsIgnoreCase("Notice_Pasted")){
                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Notice_Pasted", unitUniqueId, false);
                if(ll!=null && ll.size()>=0){
                    binding.layoutNewUnitDetails.etAttacheNotice.setText(ll.size() + " " + "out of 1 attached");
                }
            }
        } else {
            userAttachmentList = new ArrayList<>();
            if (dialogGlobal != null) {
                dialogGlobal.dismiss();
            }
        }
//        if(userAttachmentList.size()>0){
//            if(newMediaInfoDataModels!=null && newMediaInfoDataModels.getDocument_category()!=null){
//                new Handler().postDelayed(() -> {
//                    ArrayList<String> listImageDetails=new ArrayList<>();
//                    List<AttachmentItemList> ats=new ArrayList<>();
//                    for(int i=0;i<userAttachmentList.size();i++){
//
//                        if(newMediaInfoDataModels.getAttachmentItemLists()!=null){
//                            newMediaInfoDataModels.getAttachmentItemLists().add(new AttachmentItemList(0,userAttachmentList.get(i).getFileName(),userAttachmentList.get(i).getFilePath(),false,false));
//                        }else{
//                            ats.add(new AttachmentItemList(0,userAttachmentList.get(i).getFileName(),userAttachmentList.get(i).getFilePath(),false,false));
//                        }
//
//                        if(newMediaInfoDataModels.getUploadMediaList()!=null){
//                            newMediaInfoDataModels.getUploadMediaList().add(userAttachmentList.get(i).getFilePath());
//                        }else{
//                            listImageDetails.add(userAttachmentList.get(i).getFilePath());
//                        }
//
//                    }
//                    if(newMediaInfoDataModels.getAttachmentItemLists()==null){
//                        newMediaInfoDataModels.setAttachmentItemLists(ats);
//                    }
//                    if(newMediaInfoDataModels.getUploadMediaList()==null){
//                        newMediaInfoDataModels.setUploadMediaList(listImageDetails);
//                    }
//                    localSurveyDbViewModel.updateAttachUploadList(unitUniqueId,newMediaInfoDataModels.getAttachmentItemLists(),newMediaInfoDataModels.getObejctId(),newMediaInfoDataModels.getUploadMediaList());
//                    localSurveyDbViewModel.setMediaDeletedStatusByList(unitUniqueId,newMediaInfoDataModels.getAttachmentItemLists(),newMediaInfoDataModels.getObejctId());
//                    localSurveyDbViewModel.setIsUploaded(unitUniqueId,newMediaInfoDataModels.getObejctId(),false);
//                    userAttachmentList=new ArrayList<>();
//
//                    if(dialogGlobal!=null){
//                        dialogGlobal.dismiss();
//                    }
//                    Utils.dismissProgress();
//                }, 1000);
//
//
//            }else{
//                if(dialogGlobal!=null){
//                    dialogGlobal.dismiss();
//                }
//            }
//        }else{
//            if(dialogGlobal!=null){
//                dialogGlobal.dismiss();
//            }
//        }


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
        if (cc.equals("hoh")) {
            unit_unique_id = hohUniqueId;
            unitUniqueId = hohUniqueId;
        } else if (cc.equals("member")) {
            unit_unique_id = memberUniqueId;
            unitUniqueId = memberUniqueId;
        }

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
            if (deleteTotalMediaList.get(0).getDocument_category().equals(Constants.UnitDistometerVideo)){
                 globalUnitVideoPath="";
            }
            if (deleteTotalMediaList.get(0).getDocument_category().equals(Constants.UnitDistometerPdfType)){
                globalUnitPdfPath="";
                binding.layoutNewUnitDetails.etPdfDistometer.setText("0 out of 1 attached");
                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
            }

            if (deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfTypeRes)){
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false,Constants.UnitDistometerPdfTypeComm);
                pdfPathRes="";
                if((ll1!=null && ll1.size()>0) || (pdfPathComm!=null && !pdfPathComm.equalsIgnoreCase(""))){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                }else{
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                }
            }if (deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfTypeComm)){
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false,Constants.UnitDistometerPdfTypeRes);
                pdfPathComm="";
                if((ll1!=null && ll1.size()>0) || (pdfPathRes!=null && !pdfPathRes.equalsIgnoreCase(""))){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                }else{
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                }
            }
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
            setCounts(year);
            if (deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerVideo)){
                globalUnitVideoPath="";
            }
            if (deleteTotalMediaList.get(0).getDocument_category().equals(Constants.UnitDistometerPdfType)){
                globalUnitPdfPath="";
                binding.layoutNewUnitDetails.etPdfDistometer.setText("0 out of 1 attached");
                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
            }

            if (deleteTotalMediaList.get(0).getDocument_type().equalsIgnoreCase(Constants.UnitDistometerPdfTypeRes)){
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false,Constants.UnitDistometerPdfTypeComm);
                pdfPathRes="";
                if((ll1!=null && ll1.size()>0) || (pdfPathComm!=null && !pdfPathComm.equalsIgnoreCase(""))){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                }else{
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                }
            }if (deleteTotalMediaList.get(0).getDocument_type().equalsIgnoreCase(Constants.UnitDistometerPdfTypeComm)){
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false,Constants.UnitDistometerPdfTypeRes);
                pdfPathComm="";
                if((ll1!=null && ll1.size()>0) || (pdfPathRes!=null && !pdfPathRes.equalsIgnoreCase(""))){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                }else{
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                }
            }

            if(deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase("Notice_Pasted")){
                List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Notice_Pasted", unitUniqueId, false);
                if(ll!=null && ll.size()>=0){
                    binding.layoutNewUnitDetails.etAttacheNotice.setText(ll.size() + " " + "out of 1 attached");
                }
            }
        }


        if (deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerVideo)){
            globalUnitVideoPath="";
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                deleteTotalMediaList = localSurveyDbViewModel.getByItemUrl(deleteTotalMediaList.get(0).getParent_unique_id(), deleteTotalMediaList.get(0).getFilename());
//            } else {
//                deleteTotalMediaList = localSurveyDbViewModel.getMediaInfoDataByObjId(deleteTotalMediaList.get(0).getObejctId(), deleteTotalMediaList.get(0).getParent_unique_id());
//            }
//
//
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                localSurveyDbViewModel.setMediaDeletedListByUrl(unitUniqueId, deleteTotalMediaList.get(0).getFilename(), true);
//            } else {
//                localSurveyDbViewModel.setMediaDeletedStatus(unitUniqueId, deleteTotalMediaList.get(0).getObejctId(), true);
//            }
//
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                localSurveyDbViewModel.setDeleteItemObjValid(true, deleteTotalMediaList.get(0).getFilename(), unitUniqueId);
//            } else {
//                localSurveyDbViewModel.setDeleteItemObjValid(true, deleteTotalMediaList.get(0).getObejctId(), unitUniqueId);
//            }
//
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                localSurveyDbViewModel.setDeleteWholeFile(true, deleteTotalMediaList.get(0).getFilename(), unitUniqueId);
//            } else {
//                localSurveyDbViewModel.setDeleteWholeObject(true, deleteTotalMediaList.get(0).getObejctId(), unitUniqueId);
//            }
//
//            for (int i = 0; i < deleteTotalMediaList.get(0).getAttachmentItemLists().size(); i++) {
//                deleteTotalMediaList.get(0).getAttachmentItemLists().get(i).setIsDeleted(true);
//            }
//
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                localSurveyDbViewModel.uploadAttListByFileName(unitUniqueId, deleteTotalMediaList.get(0).getFilename(), deleteTotalMediaList.get(0).getAttachmentItemLists());
//            } else {
//                localSurveyDbViewModel.setMediaDeletedStatusByList(unitUniqueId, deleteTotalMediaList.get(0).getAttachmentItemLists(), deleteTotalMediaList.get(0).getObejctId());
//            }
//
//
//            if (dialogGlobal != null) {
//                dialogGlobal.dismiss();
//            }
            globalUnitVideoPath="";
        }
        else if (deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfType)
                || deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfTypeRes)
                || deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfTypeComm)){
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                deleteTotalMediaList = localSurveyDbViewModel.getByItemUrl(deleteTotalMediaList.get(0).getParent_unique_id(), deleteTotalMediaList.get(0).getFilename());
//            } else {
//                deleteTotalMediaList = localSurveyDbViewModel.getMediaInfoDataByObjId(deleteTotalMediaList.get(0).getObejctId(), deleteTotalMediaList.get(0).getParent_unique_id());
//            }
//
//
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                localSurveyDbViewModel.setMediaDeletedListByUrl(unitUniqueId, deleteTotalMediaList.get(0).getFilename(), true);
//            } else {
//                localSurveyDbViewModel.setMediaDeletedStatus(unitUniqueId, deleteTotalMediaList.get(0).getObejctId(), true);
//            }
//
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                localSurveyDbViewModel.setDeleteItemObjValid(true, deleteTotalMediaList.get(0).getFilename(), unitUniqueId);
//            } else {
//                localSurveyDbViewModel.setDeleteItemObjValid(true, deleteTotalMediaList.get(0).getObejctId(), unitUniqueId);
//            }
//
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                localSurveyDbViewModel.setDeleteWholeFile(true, deleteTotalMediaList.get(0).getFilename(), unitUniqueId);
//            } else {
//                localSurveyDbViewModel.setDeleteWholeObject(true, deleteTotalMediaList.get(0).getObejctId(), unitUniqueId);
//            }
//
//            for (int i = 0; i < deleteTotalMediaList.get(0).getAttachmentItemLists().size(); i++) {
//                deleteTotalMediaList.get(0).getAttachmentItemLists().get(i).setIsDeleted(true);
//            }
//
//            if (deleteTotalMediaList.get(0).getObejctId().equals("")) {
//                localSurveyDbViewModel.uploadAttListByFileName(unitUniqueId, deleteTotalMediaList.get(0).getFilename(), deleteTotalMediaList.get(0).getAttachmentItemLists());
//            } else {
//                localSurveyDbViewModel.setMediaDeletedStatusByList(unitUniqueId, deleteTotalMediaList.get(0).getAttachmentItemLists(), deleteTotalMediaList.get(0).getObejctId());
//            }
//
//
//            if (dialogGlobal != null) {
//                dialogGlobal.dismiss();
//            }

            if (deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfType)){
                globalUnitPdfPath="";
                binding.layoutNewUnitDetails.etPdfDistometer.setText("0 out of 1 attached");
                binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 1 attached");
            }else if (deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfTypeRes)){
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false,Constants.UnitDistometerPdfTypeComm);
                pdfPathRes="";
                if((ll1!=null && ll1.size()>0) || (pdfPathComm!=null && !pdfPathComm.equalsIgnoreCase(""))){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                }else{
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                }
            }else if (deleteTotalMediaList.get(0).getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfTypeComm)){
                List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCountType("unit_layout_rc", unitUniqueId, false,Constants.UnitDistometerPdfTypeRes);
                pdfPathComm="";
                if((ll1!=null && ll1.size()>0) || (pdfPathRes!=null && !pdfPathRes.equalsIgnoreCase(""))){
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("1 out of 2 attached");
                }else{
                    binding.layoutNewUnitDetails.etPdfDistometerComm.setText("0 out of 2 attached");
                }
            }
        }
    }

    @Override
    public void onAttachmentBrowseClicked(MediaInfoDataModel mediaInfoDataModel, String documentType, String unitRelativePath, String s) {
        String unit_unique_id = this.unit_unique_id, unitUniqueId = this.unitUniqueId;
        if (cc.equals("hoh")) {
            unit_unique_id = hohUniqueId;
            unitUniqueId = hohUniqueId;
        } else if (cc.equals("member")) {
            unit_unique_id = memberUniqueId;
            unitUniqueId = memberUniqueId;
        }

        if (mediaInfoDataModel.getDocument_category().equalsIgnoreCase(Constants.UnitDistometerVideo)){
            if (!globalUnitVideoPath.equals("")){
                Utils.shortToast("please delete existing video first to add new one",activity);
            }else {
                alpha = 1;
                updObj = mediaInfoDataModel;
                showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, s);
            }
        }else if (mediaInfoDataModel.getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfType)){
            if (!globalUnitPdfPath.equals("")){
                Utils.shortToast("please delete existing pdf first to add new one",activity);
            }else {
                alpha = 1;
                updObj = mediaInfoDataModel;
                showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, s);
            }
        }else if (mediaInfoDataModel.getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfTypeRes)){
            if (!pdfPathRes.equals("")){
                Utils.shortToast("please delete existing pdf first to add new one",activity);
            }else {
                alpha = 1;
                updObj = mediaInfoDataModel;
                showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, s);
            }
        }else if (mediaInfoDataModel.getDocument_category().equalsIgnoreCase(Constants.UnitDistometerPdfTypeComm)){
            if (!pdfPathComm.equals("")){
                Utils.shortToast("please delete existing pdf first to add new one",activity);
            }else {
                alpha = 1;
                updObj = mediaInfoDataModel;
                showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, s);
            }
        }else {
            alpha = 1;
            updObj = mediaInfoDataModel;
            showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, s);
        }
    }

    private void setCounts(int countYear) {
        if((binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() || binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked()) && !binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.getText().toString().equals("Rented Tenant")){
            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of identity", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttacheInfoSeven.setText(ll.size() + " " + "out of 1 attached");
            List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Chain of Agreement", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttacheInfoSix.setText(ll1.size() + " " + "out of 2 attached");
            List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of any other document", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttacheInfoTen.setText(ll2.size() + " " + "out of 29 attached");

            List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etSurveyPavti.setText(ll100.size() + " " + "out of 1 attached");
            List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttachePhoto.setText(ll101.size() + " " + "out of 1 attached");
            List<MediaInfoDataModel> ll104 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etOthers.setText(ll104.size() + " " + "attached");

        }else if((binding.layoutNewUnitDetails.radioUnitStatusRent.isChecked() || binding.layoutNewUnitDetails.radioUnitStatusRentComm.isChecked()) && !binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("") && binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.getText().toString().equals("Rented Tenant")){
            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of identity", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttacheInfoSeven.setText(ll.size() + " " + "out of 1 attached");
            List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Chain of Agreement", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttacheInfoSix.setText(ll1.size() + " " + "out of 2 attached");
            List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of any other document", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttacheInfoTen.setText(ll2.size() + " " + "out of 29 attached");

            List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etSurveyPavti.setText(ll100.size() + " " + "out of 1 attached");
            List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etAttachePhoto.setText(ll101.size() + " " + "out of 1 attached");
            List<MediaInfoDataModel> ll104 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
            binding.layoutNewUnitDetails.layoutOwnerRentedAttachement.etOthers.setText(ll104.size() + " " + "attached");

        }else{
            if (unitStructureUsage.equals(Constants.ResidentialCheckBox)) {
//            if (countYear < 2000) {
                if (dateCheck.equalsIgnoreCase(Constants.dateTxtFirst)) {
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    if(floorFlag){
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 9 attached");
                    }else{
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 7 attached");
                    }
                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of identity", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoTwo.setText(ll1.size() + " " + "out of 1 attached");
                    List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheRecent2011.setText(ll2.size() + " " + "out of 1 attached");
                }
//            else if (countYear < 2011) {
                else if (dateCheck.equalsIgnoreCase(Constants.dateTxtSecond)) {
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    if(floorFlag){
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 9 attached");
                    }else{
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 7 attached");
                    }

                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Address", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoFour.setText(ll1.size() + " " + "out of 3 attached");
                    List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Chain of Agreement", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoFive.setText(ll2.size() + " " + "out of 2 attached");
                    List<MediaInfoDataModel> ll3 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of identity", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoSix.setText(ll3.size() + " " + "out of 1 attached");
                    List<MediaInfoDataModel> ll4 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of payment receipt from SRA", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoSeven.setText(ll4.size() + " " + "out of 1 attached");
                    List<MediaInfoDataModel> ll5 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheRecent2011.setText(ll5.size() + " " + "out of 2 attached");

                } else {
                    List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                    if(floorFlag){
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 9 attached");
                    }else{
                        binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 5 attached");
                    }

                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_2_post_2011), unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoNine.setText(ll1.size() + " " + "out of 3 attached");
                    List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_3_post_2011), unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoTen.setText(ll2.size() + " " + "out of 29 attached");
                }

                List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                binding.layoutNewUnitDetails.layoutResAttachement.etSurveyPavti.setText(ll100.size() + " " + "out of 1 attached");
                List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                binding.layoutNewUnitDetails.layoutResAttachement.etAttachePhoto.setText(ll101.size() + " " + "out of 1 attached");
                List<MediaInfoDataModel> ll102 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of any other document", unitUniqueId, false);
                binding.layoutNewUnitDetails.layoutResAttachement.etAttacheInfoTen.setText(ll102.size() + " " + "out of 29 attached");
                List<MediaInfoDataModel> ll103 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                binding.layoutNewUnitDetails.layoutResAttachement.etAttacheRecent2000.setText(ll103.size() + " " + "out of 2 attached");

                List<MediaInfoDataModel> ll104 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
                binding.layoutNewUnitDetails.layoutResAttachement.etOthers.setText(ll104.size() + " " + "attached");
            } else {
                if (dateCheck.equalsIgnoreCase(Constants.dateTxtFirst)) {
                    if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 7 attached");
                        List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of License", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoTwo.setText(ll1.size() + " " + "out of 3 attached");
                        List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheRecentComm2000.setText(ll2.size() + " " + "out of 3 attached");
                        List<MediaInfoDataModel> ll104 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etOthers.setText(ll104.size() + " " + "attached");
                    }else{
                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoOne.setText(ll.size() + " " + "out of 5 attached");
                        List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of License", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoTwo.setText(ll1.size() + " " + "out of 3 attached");
                        List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheRecentComm2000.setText(ll2.size() + " " + "out of 2 attached");
                        List<MediaInfoDataModel> ll104 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etOthers.setText(ll104.size() + " " + "attached");
                    }
                }
                else if (dateCheck.equalsIgnoreCase(Constants.dateTxtSecond)) {
                    if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                        if(floorFlag){
                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 9 attached");
                        }else{
                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 7 attached");
                        }
                        List<MediaInfoDataModel> ll11 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of License", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoFour.setText(ll11.size() + " " + "out of 3 attached");
                        List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Address", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoFive.setText(ll1.size() + " " + "out of 3 attached");
                        List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Chain of Agreement", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoSix.setText(ll2.size() + " " + "out of 2 attached");
                        List<MediaInfoDataModel> ll3 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of identity", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoSeven.setText(ll3.size() + " " + "out of 1 attached");
                        List<MediaInfoDataModel> ll4 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of payment receipt from SRA", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEE.setText(ll4.size() + " " + "out of 1 attached");
                        List<MediaInfoDataModel> ll5 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheRecentComm2011.setText(ll5.size() + " " + "out of 3 attached");
                        List<MediaInfoDataModel> ll104 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etOthers.setText(ll104.size() + " " + "attached");
                    }else{
                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                        if(floorFlag){
                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 7 attached");
                        }else{
                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoThree.setText(ll.size() + " " + "out of 5 attached");
                        }

                        List<MediaInfoDataModel> ll11 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of License", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoFour.setText(ll11.size() + " " + "out of 3 attached");
//                    List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Address", unitUniqueId, false);
//                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoFive.setText(ll1.size() + " " + "out of 3 attached");
                        List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Chain of Agreement", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoSix.setText(ll2.size() + " " + "out of 2 attached");
                        List<MediaInfoDataModel> ll3 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of identity", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoSeven.setText(ll3.size() + " " + "out of 1 attached");
                        List<MediaInfoDataModel> ll4 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of payment receipt from SRA", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEE.setText(ll4.size() + " " + "out of 1 attached");
                        List<MediaInfoDataModel> ll5 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("1 Year Recent Proof", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheRecentComm2011.setText(ll5.size() + " " + "out of 2 attached");
                        List<MediaInfoDataModel> ll104 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etOthers.setText(ll104.size() + " " + "attached");
                    }


                } else {
                    if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                        if(floorFlag){
                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 9 attached");
                        }else{
                            binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 5 attached");
                        }
                        List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_2_post_2011), unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoNine.setText(ll1.size() + " " + "out of 4 attached");
                        List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_3_post_2011), unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etAttacheInfoTen.setText(ll2.size() + " " + "out of 29 attached");
                        List<MediaInfoDataModel> ll104 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutRcAttachement.etOthers.setText(ll104.size() + " " + "attached");
                    }else{
                        List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Proof of Structure", unitUniqueId, false);
                        if(floorFlag){
                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 9 attached");
                        }else{
                            binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoEight.setText(ll.size() + " " + "out of 3 attached");
                        }
                        List<MediaInfoDataModel> ll1 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_2_post_2011), unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoNine.setText(ll1.size() + " " + "out of 3 attached");
                        List<MediaInfoDataModel> ll2 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount(activity.getString(R.string.residential_proof_3_post_2011), unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etAttacheInfoTen.setText(ll2.size() + " " + "out of 19 attached");
                        List<MediaInfoDataModel> ll104 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Any other document", unitUniqueId, false);
                        binding.layoutNewUnitDetails.layoutCommAttachement.etOthers.setText(ll104.size() + " " + "attached");
                    }
                }
                if(unitStructureUsage.equalsIgnoreCase(Constants.RcCheckBox)){
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutRcAttachement.etSurveyPavti.setText(ll100.size() + " " + "out of 1 attached");
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutRcAttachement.etAttachePhoto.setText(ll101.size() + " " + "out of 1 attached");
                }else{
                    List<MediaInfoDataModel> ll100 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Survey Pavti", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutCommAttachement.etSurveyPavti.setText(ll100.size() + " " + "out of 1 attached");
                    List<MediaInfoDataModel> ll101 = localSurveyDbViewModel.getMediaInfoDataByCatUnitCount("Owner Photo", unitUniqueId, false);
                    binding.layoutNewUnitDetails.layoutCommAttachement.etAttachePhoto.setText(ll101.size() + " " + "out of 1 attached");
                }
            }
        }
    }


    private void showPanchnamaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.upload_panchnama_document, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        AutoCompleteTextView panchnamaRemarkDropdown = customLayout.findViewById(R.id.panchnama_remark_dropdown);
        ImageButton clickButton = customLayout.findViewById(R.id.clickButton);
        ImageView delImageView = customLayout.findViewById(R.id.delImageView);
        tempImageButton = customLayout.findViewById(R.id.panchnama_captured_document);
        tempImageLayout = customLayout.findViewById(R.id.tempImageLayout);
        EditText dcTitle = customLayout.findViewById(R.id.docTitle);
        LinearLayout addErrorLayout = customLayout.findViewById(R.id.addErrorLayout);
        TextView addErrorTextView = customLayout.findViewById(R.id.addErrorTextView);
        TextView uploadDocumentPanchnama = customLayout.findViewById(R.id.upload_document_panchnama);
        dcTitle.setText("Panchnama Form");
        panchnamaRemarkDropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_panchnama_remarks)));
        setFocusChange_OnTouch(panchnamaRemarkDropdown);

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (panchnamaRemarkDropdown.getText().toString().equals("")) {
                    addErrorLayout.setVisibility(View.VISIBLE);
                    addErrorTextView.setText("Please select punchnama remarks");
                } else {
                    addErrorLayout.setVisibility(View.GONE);
                    if (globalPanchnamaPath != null && !globalPanchnamaPath.equals("")) {
                        addErrorLayout.setVisibility(View.VISIBLE);
                        addErrorTextView.setText("Please remove existing punchnama to upload new one!");
                    } else {
                        addErrorLayout.setVisibility(View.GONE);
//                        showAttachmentAlertDialogButtonClicked("Panchnama Form", unit_relative_path, unitUniqueId + "_" + "Panchnama Form" + "_" + Utils.getEpochDateStamp());
                        showAttachmentAlertDialogButtonClicked("Panchnama Form", unit_relative_path, unitUniqueId + "_" + "Panchnama Form" + "_" + Utils.getEpochDateStamp());
                    }
                }
            }
        });

        tempImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalPanchnamaPath.contains(".pdf") && !globalPanchnamaPath.contains("http")) {
                    File pdfPathFile;
                    try {
                        pdfPathFile = new File(globalPanchnamaPath);
                        CryptoUtilsTest.encryptFileinAES(pdfPathFile, 2);
                    } catch (CryptoException e) {
                        AppLog.logData(activity,e.getMessage());
                        throw new RuntimeException(e);
                    }
                    Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", pdfPathFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(intent);
                } else if (globalPanchnamaPath.contains(".pdf") && globalPanchnamaPath.contains("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(globalPanchnamaPath), "application/pdf");
                    activity.startActivity(browserIntent);
                } else {
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    tempImageButton.buildDrawingCache();
                    Bitmap image = tempImageButton.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });

        delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete this attachment?",
                        "Delete", "Cancel", false, 1, position, globalPanchnamaPath);
            }
        });


        uploadDocumentPanchnama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (panchnamaRemarkDropdown.getText().toString().equals("")) {
                    addErrorLayout.setVisibility(View.VISIBLE);
                    addErrorTextView.setText("Please select punchnama remarks");
                } else if (globalPanchnamaPath == null || globalPanchnamaPath.equals("")) {
                    addErrorLayout.setVisibility(View.VISIBLE);
                    addErrorTextView.setText("Please upload punchnama image");
                } else {
                    addErrorLayout.setVisibility(View.GONE);
                    ArrayList<MediaInfoDataModel> attach = getmediaInfoDataList(userAttachmentList, unit_relative_path, Constants.unit_infoLayer,
                            unit_unique_id, unit_rel_global_id);
                    if (attach.size() > 0) {
                        mediaInfoDataModels1 = new ArrayList<>();
                        attach.get(0).setDocument_remarks(panchnamaRemarkDropdown.getTag().toString());
                        attach.get(0).setDocument_category("Panchnama Form");
                        attach.get(0).setDocument_type("Panchnama Form");
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
                        showFormSubmitDialog();
                    }
                }
            }
        });

        dialog.show();
    }


    public void showActionAlertDialogButtonClicked(String header, String mssage, String btnYes, String btnNo, boolean toUplaod, int flag, int pos, String itemUrl) {

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
        btn_yes.getBackground().setColorFilter(activity.getColor(R.color.lighter_red), PorterDuff.Mode.SRC_ATOP);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);

        statusRadioGroup.setVisibility(View.GONE);

        btn_yes.setOnClickListener(view1 -> {
            if (flag == 1) {
//                globalPanchnamaPath="";
//                tempImageLayout.setVisibility(View.GONE);

//                List<MediaInfoDataModel>  deleteTotalMediaList=localSurveyDbViewModel.getByItemUrl(unitUniqueId,globalPanchnamaPath);
//                localSurveyDbViewModel.updateByFileName(true,deleteTotalMediaList.get(0).getFilename(),unitUniqueId);
//
//
//
//                if(deleteTotalMediaList.get(0).getUploadMediaList()!=null){
//                    ArrayList<String>aff=new ArrayList<>();
//                    for(int i=0;i<deleteTotalMediaList.get(0).getUploadMediaList().size();i++){
//                        if(!deleteTotalMediaList.get(0).getUploadMediaList().get(i).toString().equals(itemUrl)){
//                            aff.add(deleteTotalMediaList.get(0).getUploadMediaList().get(i).toString());
//                        }
//                    }
//                    if(deleteTotalMediaList.get(0).getObejctId().equals("")){
//                        localSurveyDbViewModel.uploadListByFileName(unitUniqueId,deleteTotalMediaList.get(0).getFilename(),aff);
//                    }else{
//                        localSurveyDbViewModel.updateAttachUploadList(unitUniqueId,null,deleteTotalMediaList.get(0).getObejctId(),aff);
//                    }
//
//                }
//
//                for(int i=0;i<deleteTotalMediaList.get(0).getAttachmentItemLists().size();i++){
//                    if(deleteTotalMediaList.get(0).getAttachmentItemLists().get(i).getItem_url().equals(itemUrl)){
//                        deleteTotalMediaList.get(0).getAttachmentItemLists().get(i).setIsDeleted(true);
//                    }
//                }
//
//                if(deleteTotalMediaList.get(0).getObejctId().equals("")){
//                    localSurveyDbViewModel.uploadAttListByFileName(unitUniqueId,deleteTotalMediaList.get(0).getFilename(),deleteTotalMediaList.get(0).getAttachmentItemLists());
//                }else{
//                    localSurveyDbViewModel.setMediaDeletedStatusByList(unitUniqueId, deleteTotalMediaList.get(0).getAttachmentItemLists(),deleteTotalMediaList.get(0).getObejctId());
//                }

                globalPanchnamaPath = "";
                tempImageLayout.setVisibility(View.GONE);
            }
            dialog.dismiss();
        });

        btn_no.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        img_close.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    private Date getDate(long time) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currenTimeZone = new Date((long) 1379487711 * 1000);
        return currenTimeZone;
    }


    public static GregorianCalendar convertTimestampToGregorianCalendar(long timestamp) {
        // Create a Calendar instance and set its time using the provided timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        // Convert the Calendar instance to a GregorianCalendar instance
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(calendar.getTimeInMillis());

        return gregorianCalendar;
    }

    private String getDateInFormate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
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

        radio_inProg.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                unitAlertStatus = Constants.InProgress_statusLayer;
            }
        });

        radio_hold.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                unitAlertStatus = Constants.OnHold_statusLayer;
            }
        });

        radio_complete.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                unitAlertStatus = Constants.completed_statusLayer;
            }
        });


        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utils.showProgress("Please wait...", activity);
                new Handler().postDelayed(() -> {
//                    localSurveyDbViewModel.insertStructureUnitIdStatusData(new StructureUnitIdStatusDataTable(unitUniqueId,
//                            structUniqueId, unitAlertStatus), activity);
                    localSurveyDbViewModel.updateStructureStatusDataTable(structUniqueId, unitAlertStatus);
                    Utils.dismissProgress();
//                    activity.startActivity(new Intent(activity, MapPageActivity.class));
//                    activity.finish();
                }, 2000);
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static long extractTimestamp(String inputString) {
        int startIndex = inputString.indexOf("time=") + 5;
        int endIndex = inputString.indexOf(",", startIndex);
        String timestampString = inputString.substring(startIndex, endIndex);
        return Long.parseLong(timestampString);
    }

    private static String formatDate(Calendar calendar) {
        TimeZone timeZone = TimeZone.getTimeZone("IST");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd");
        sdf.setTimeZone(timeZone);
        return sdf.format(calendar.getTime());
    }

    private String formattedDate(String recivedDate) {
        String displayDate = "";
        try {
            long timestamp = extractTimestamp("" + recivedDate);

            TimeZone timeZone = TimeZone.getTimeZone("IST");
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(timestamp);

            displayDate = formatDate(calendar);

        } catch (Exception e) {
            AppLog.logData(activity,e.getMessage());
            Log.e("Error_in_timstamp= ", " " + e.getMessage());
        }
        return displayDate;
    }

    public boolean validateUsageFields(String usageType) {
        try {
            AppLog.e("validateUsageFields started....");
            if (usageType.equalsIgnoreCase(Constants.ResidentialCheckBox)) {
                AppLog.e("Residential Block");
                if (binding.layoutNewUnitDetails.etTenementNo.getText().toString().length() > 0 && binding.layoutNewUnitDetails.autoCompDocTenement.getText().toString().length() == 0) {
                    AppLog.e("Residential Block false");
                    Utils.setError(binding.layoutNewUnitDetails.autoCompDocTenement, "Field is mandatory", activity);
                    return false;
                } else {
                    AppLog.e("Residential Block true");
                    return true;
                }
            } else {
                if (binding.layoutNewUnitDetails.etTenementNoComm.getText().toString().length() > 0 && binding.layoutNewUnitDetails.autoCompDocTenementComm.getText().toString().length() == 0) {
                    AppLog.e("Commercial Block false");
                    Utils.setError(binding.layoutNewUnitDetails.autoCompDocTenementComm, "Field is mandatory", activity);
                    return false;
                } else {
                    AppLog.e("Commercial Block true");
                    return true;

                }
            }
        } catch (Exception ex) {
            AppLog.logData(activity,ex.getMessage());
            Utils.shortToast("Exception in validateUsageFields: " + ex.getMessage(), activity);
            AppLog.e("Exception in validateUsageFields: " + ex.getMessage());
        }
        return false;
    }


    public void setAgeValidation() {
        String[] arrDate = binding.layoutNewUnitDetails.etDobRespondent.getText().toString().split("/");
        if(arrDate!=null && arrDate.length>=3){
            if (getAge(Integer.parseInt(arrDate[2]), Integer.parseInt(arrDate[1]), Integer.parseInt(arrDate[0])) >= 18) {
                binding.txtNext.setText("Next");
                binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.layRCProof.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.ageAboveLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.usageLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.ageBelowLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.hohDetails.setVisibility(View.GONE);

                binding.layoutNewUnitDetails.radioUnitUsageResidential.setClickable(true);
                binding.layoutNewUnitDetails.radioUnitUsageCommercial.setClickable(true);
                binding.layoutNewUnitDetails.radioUnitUsageRC.setClickable(true);
                binding.layoutNewUnitDetails.radioUnitUsageIndustrial.setClickable(true);
                binding.layoutNewUnitDetails.radioUnitUsageOthers.setClickable(true);
                if (previousUnitInfoPointDataModel == null) {
                    binding.layoutNewUnitDetails.etExistenceSince.setFocusable(true);
                    binding.layoutNewUnitDetails.etExistenceSince.setEnabled(true);
                    binding.layoutNewUnitDetails.etExistenceSince.setText("");
                    binding.layoutNewUnitDetails.etYearOfStructure.setText("");
                    binding.layoutNewUnitDetails.etYearOfStructure.setTag("");
                    //3-2-2024
//                    binding.layoutNewUnitDetails.etExistenceSince.setBackgroundResource(R.drawable.rounded_blue_edittext);
                    binding.layoutNewUnitDetails.etExistenceSinceComm.setFocusable(true);
                    binding.layoutNewUnitDetails.etExistenceSinceComm.setEnabled(true);
                    binding.layoutNewUnitDetails.etExistenceSinceComm.setText("");
                    binding.layoutNewUnitDetails.etYearOfStructureComm.setText("");
                    binding.layoutNewUnitDetails.etYearOfStructureComm.setTag("");
                }
//                    binding.layoutNewUnitDetails.etExistenceSinceComm.setBackgroundResource(R.drawable.rounded_blue_edittext);
                if ((previousUnitInfoPointDataModel != null && previousUnitInfoPointDataModel.getRespondent_age()!="" && Integer.parseInt(previousUnitInfoPointDataModel.getRespondent_age()) < 18)) {
                    binding.layoutNewUnitDetails.etYearOfStructure.setFocusable(true);
                    binding.layoutNewUnitDetails.etYearOfStructure.setEnabled(true);
                    binding.layoutNewUnitDetails.etYearOfStructure.setBackgroundResource(R.drawable.rounded_edittext);
                    binding.layoutNewUnitDetails.etYearOfStructureComm.setFocusable(true);
                    binding.layoutNewUnitDetails.etYearOfStructureComm.setEnabled(true);
                    binding.layoutNewUnitDetails.etYearOfStructureComm.setBackgroundResource(R.drawable.rounded_edittext);

                }

            } else {
                binding.txtNext.setText("Finish");
                binding.layoutNewUnitDetails.ageAboveLayout.setVisibility(View.GONE);
                binding.layoutNewUnitDetails.ageBelowLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.usageLayout.setVisibility(View.VISIBLE);
                binding.layoutNewUnitDetails.hohDetails.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onBackKeyPress() {
        closeFormPopup();
        return true;
    }

    private void closeFormPopup() {
            YesNoBottomSheet.geInstance(activity, activity.getString(R.string.close_form_msg), activity.getResources().getString(R.string.yesBtn), activity.getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {

                @Override
                public void yesBtn() {
                    activity.finish();
                }

                @Override
                public void noBtn() {

                }
            }).show(((AppCompatActivity) activity).getSupportFragmentManager(), "");
    }

    private File saveVideoFile(String filePath) {
        if (filePath != null) {
            File videoFile = new File(filePath);
//            String videoFileName = System.currentTimeMillis() + "_" + videoFile.getName();
//            String folderName = Environment.DIRECTORY_MOVIES;
            String folderName1 = String.valueOf(activity.getFilesDir());
            String folderName = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+activity.getApplicationContext().getPackageName();

            File myDir = new File(folderName );
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            if (Build.VERSION.SDK_INT >= 30) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, unitVideoFileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "video/mp4");
//                values.put(MediaStore.Images.Media.RELATIVE_PATH, folderName);
                values.put(MediaStore.Images.Media.RELATIVE_PATH, folderName+"/"+unitVideoFilePath);
                values.put(MediaStore.Images.Media.IS_PENDING, 1);

                Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

                Uri fileUri = activity.getContentResolver().insert(collection, values);

                if (fileUri != null) {
                    try (FileOutputStream out = new FileOutputStream(activity.getContentResolver().openFileDescriptor(fileUri, "rw").getFileDescriptor())) {
                        try (FileInputStream inputStream = new FileInputStream(videoFile)) {
                            byte[] buf = new byte[4096];
                            int sz;
                            while ((sz = inputStream.read(buf)) > 0) {
                                out.write(buf, 0, sz);
                            }
                        }
                    } catch (IOException e) {
                        AppLog.logData(activity,e.getMessage());
                        e.printStackTrace();
                    }

                    values.clear();
                    values.put(MediaStore.Video.Media.IS_PENDING, 0);
                    activity.getContentResolver().update(
                            fileUri,
                            values,
                            null,
                            null
                    );

                    return new File(getMediaPath(fileUri));
                }
            } else {
                File downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File desFile = new File(downloadsPath, unitVideoFileName);

                if (desFile.exists())
                    desFile.delete();

                try {
                    desFile.createNewFile();
                } catch (IOException e) {
                    AppLog.logData(activity,e.getMessage());
                    e.printStackTrace();
                }

                return desFile;
            }
        }
        return null;
    }
    public String getMediaPath(Uri uri) {
        ContentResolver resolver = activity.getContentResolver();
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = resolver.query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                return cursor.getString(columnIndex);
            } else {
                return "";
            }
        } catch (Exception e) {
            AppLog.logData(activity,e.getMessage());
            try {
//                String filePath = activity.getApplicationInfo().dataDir + File.separator + System.currentTimeMillis();
                String filePath = unitVideoFilePath;
                File file = new File(filePath);

                InputStream inputStream = resolver.openInputStream(uri);
                if (inputStream != null) {
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buf = new byte[4096];
                        int len;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                    }
                    return file.getAbsolutePath();
                }
            } catch (Exception ex) {
                AppLog.logData(activity,ex.getMessage());
                ex.printStackTrace();
            }
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void fillUnitUniqueId(){
        try{
            previousStructureInfoPointDataModel = (StructureInfoPointDataModel) activity.getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);
            localSurveyDbViewModel = ViewModelProviders.of(getActivity()).get(LocalSurveyDbViewModel.class);
            ArrayList<String> dbLocalUniquIdList=new ArrayList<>(localSurveyDbViewModel.getLocalAddedUniqueIdList(previousStructureInfoPointDataModel.getHut_number()));
//            ArrayList<String> dbLocalUniquIdList = localSurveyDbViewModel.getLocalAddedUniqueIdList(previousStructureInfoPointDataModel.getHut_number());
            ArrayList<String> mainList=App.getInstance().getListUniqueId();
            for(int i=0;i<dbLocalUniquIdList.size();i++){
                    if(dbLocalUniquIdList.get(i)==null|| mainList.contains(dbLocalUniquIdList.get(i)) || dbLocalUniquIdList.get(i).toString().trim().length()<=0){
                        mainList.remove(dbLocalUniquIdList.get(i));
                    }
            }
            App.getInstance().setListUniqueId(mainList);
            try{
                ArrayList<String> arr=App.getInstance().getAlreadyAddedUniqueId();
                if(arr!=null && arr.size()>0){
                    for(int i=0;i<dbLocalUniquIdList.size();i++){
                        if(!arr.contains(dbLocalUniquIdList.get(i))){
                            arr.add(dbLocalUniquIdList.get(i));
                        }
                    }
                    App.getInstance().setAlreadyAddedUniqueId(arr);
                }else{
                    if(dbLocalUniquIdList!=null && dbLocalUniquIdList.size()>0){
                        App.getInstance().setAlreadyAddedUniqueId(dbLocalUniquIdList);
                    }
                }
            }catch(Exception ex){
                ex.getMessage();
            }

            binding.layoutNewUnitDetails.etUniqueNo.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, App.getInstance().getListUniqueId()));
            setFocusChange_OnTouch(binding.layoutNewUnitDetails.etUniqueNo);


            binding.layoutNewUnitDetails.etUniqueNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                    ArrayList<String> arr=App.getInstance().getListUniqueId();
                    String F=getFloorNumber(arr.get(position));
                    actualFloorNo=F;
                    if(!F.equalsIgnoreCase("0")){
                        floorFlag=true;
                        App.getSharedPreferencesHandler().putBoolean(Constants.floorFlag,true);
                    }else{
                        floorFlag=false;
                        App.getSharedPreferencesHandler().putBoolean(Constants.floorFlag,false);
                    }
                    String structureTypeName= App.getSharedPreferencesHandler().getString(Constants.structureTypeName);
                    if(structureTypeName!=null && !structureTypeName.equalsIgnoreCase("null") && (structureTypeName.contains("toilet") || structureTypeName.contains("Toilet")) ){
                        usageToiletMeterBox = "Toilet";
                        openalert(getActivity(),"Is unit is being used as a \"Toilet\" ?");
                    }else if(structureTypeName!=null && !structureTypeName.equalsIgnoreCase("null") && (structureTypeName.contains("meter") || structureTypeName.contains("Meter")) ){
                        usageToiletMeterBox = "Meter Box";
                        openalert(getActivity(),"Is unit is being used as a \"Meter Box\" ?");
                    }
                }
            });
        }catch(Exception ex){
            AppLog.e("Exception in fillUnitUniqueId:"+ex.getMessage());
            binding.layoutNewUnitDetails.etUniqueNo.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, App.getInstance().getListUniqueId()));
            setFocusChange_OnTouch(binding.layoutNewUnitDetails.etUniqueNo);
        }
    }
    private boolean getFileSizeFromUri(Activity activity, Uri uri) {

        Cursor returnCursor = activity.getContentResolver().query(uri, new String[]{}, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        return checkFleSize(returnCursor.getFloat(sizeIndex), activity);
    }

    private boolean checkFleSize(float size, Activity activity) {
        float fileSizeInKB = size / 1000;
        float fileSizeInMB = fileSizeInKB / 1024;

        if (fileSizeInMB > 100) {
            Utils.showMessagePopup("The file size is currently " + Utils.getStringSizeLengthFile(size) + ". Please note that files larger than 100MB are not permitted.", activity);
            return false;
        } else return true;
    }
    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_MOVIES), unitVideoFilePath);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(unitVideoFileName, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private String formattedDateToYear(String recivedDate) {
        String displayDate = "";
        try {
            long timestamp = extractTimestamp("" + recivedDate);

            TimeZone timeZone = TimeZone.getTimeZone("IST");
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(timestamp);
            year=calendar.get(Calendar.YEAR);
            displayDate = formatDateToYear(calendar);

        } catch (Exception e) {
            AppLog.logData(activity,e.getMessage());
//            Utils.shortToast("Exception in formattedDate:"+e.getMessage(),activity);
            AppLog.e("Exception in formattedDate:" + e.getMessage());
        }
        return displayDate;
    }

    private static String formatDateToYear(Calendar calendar) {
        TimeZone timeZone = TimeZone.getTimeZone("IST");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(timeZone);
        return sdf.format(calendar.getTime());
    }

    private void disableUnitIdSelection(){
//        binding.layoutNewUnitDetails.autoCompDocTenement.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
//        binding.layoutNewUnitDetails.autoCompDocTenement.setText("");
//        binding.layoutNewUnitDetails.etUniqueNo.requestFocus();
//        binding.layoutNewUnitDetails.etUniqueNo.dismissDropDown();
    }

    /**
     * Used to scroll to the given view.
     *
     * @param scrollViewParent Parent ScrollView
     * @param view View to which we need to scroll.
     */
    private void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    /**
     * Used to get deep child offset.
     * <p/>
     * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
     * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
     *
     * @param mainParent        Main Top parent.
     * @param parent            Parent.
     * @param child             Child.
     * @param accumulatedOffset Accumulated Offset.
     */
    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    public File getPhotoUri(String fileName,String filepath) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filepath);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            AppLog.d(fileName+ " failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void calculateFloor(String hutNumber) {

        String floor = "0";

        try {

            boolean isFound = false;

            String[] splittedData = hutNumber.split("/");

            for(int i = splittedData.length - 1 ; i > 0 ; i--) {
                if(splittedData[i].startsWith("F")) {
                    floor = splittedData[i].substring(1);
                    isFound = true;
                    break;
                }
            }

            if(!isFound) {
                floor = "0";
            }
        } catch (Exception ex) {
            floor = "0";
            ex.getMessage();
        }

        previousStructureInfoPointDataModel.setNo_of_floors(Integer.parseInt(floor));
    }

    private void calculateFloorOld(String str1) {

        String floor = "0";
        // get the DRP survey ID
        try {
            //Split the Survey ID by '/' and take the 5 value

            int totalParts = str1.split("/").length;

            if (totalParts > 5) {

                String strFloor = str1.split("/")[5];
                floor = strFloor.substring(1); //else take the Numeric Value after 'F'

                /*if(strFloor.equalsIgnoreCase("F1")) {
                    floor = "1";
                } else if(strFloor.equalsIgnoreCase("F2")) {
                    floor = "2";
                } else if(strFloor.equalsIgnoreCase("F3")) {
                    floor = "3";
                } else if(strFloor.equalsIgnoreCase("F4")) {
                    floor = "4";
                } else if(strFloor.equalsIgnoreCase("F5")) {
                    floor = "5";
                } else if(strFloor.equalsIgnoreCase("F6")) {
                    floor = "6";
                } else if(strFloor.equalsIgnoreCase("F7")) {
                    floor = "7";
                } else if(strFloor.equalsIgnoreCase("F8")) {
                    floor = "8";
                } else if(strFloor.equalsIgnoreCase("F9")) {
                    floor = "9";
                } else if(strFloor.equalsIgnoreCase("F10")) {
                    floor = "10";
                }*/

                previousStructureInfoPointDataModel.setNo_of_floors(Integer.parseInt(floor));
            } else {
                previousStructureInfoPointDataModel.setNo_of_floors(0);
            }
        } catch (Exception ex) {
            previousStructureInfoPointDataModel.setNo_of_floors(0);
            ex.getMessage();
        }
    }


    private void fillReligiousAminitiesDetaild(UnitInfoDataModel previousUnitInfoPointDataModel) {
    if(unitStructureUsage.equals(Constants.ReligiousCheckBox)){
        binding.layoutNewUnitDetails.dietyLayout.setVisibility(View.VISIBLE);
        binding.layoutNewUnitDetails.noPeopleVisitsLayout.setVisibility(View.VISIBLE);
        binding.layoutNewUnitDetails.registeredReligiousLayout.setVisibility(View.VISIBLE);

        binding.layoutNewUnitDetails.autoCompStructure.setText(previousUnitInfoPointDataModel.getStructure_type_religious());
        binding.layoutNewUnitDetails.etStructureOther.setText(previousUnitInfoPointDataModel.getStructure_religious_other());
//        binding.layoutNewUnitDetails.autoCompStructure.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_structure_religious)));
//        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompStructure);
    }else {
        binding.layoutNewUnitDetails.dietyLayout.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.noPeopleVisitsLayout.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.registeredReligiousLayout.setVisibility(View.GONE);

        binding.layoutNewUnitDetails.autoCompStructure.setText(previousUnitInfoPointDataModel.getStructure_type_amenities());
        binding.layoutNewUnitDetails.etStructureOther.setText(previousUnitInfoPointDataModel.getStructure_amenities_other());
//        binding.layoutNewUnitDetails.autoCompStructure.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_structure_amenities)));
//        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompStructure);
    }
        disableViews(binding.layoutNewUnitDetails.autoCompStructure);
        disableViews(binding.layoutNewUnitDetails.etStructureOther);
        if(binding.layoutNewUnitDetails.autoCompStructure.getText().toString().equalsIgnoreCase("Others")){
            binding.layoutNewUnitDetails.etStructureOther.setVisibility(View.VISIBLE);
        }

    binding.layoutNewUnitDetails.etStructureName.setText(previousUnitInfoPointDataModel.getName_of_structure());
        disableViews(binding.layoutNewUnitDetails.etStructureName);
        binding.layoutNewUnitDetails.autoCompDiety.setText(previousUnitInfoPointDataModel.getType_of_diety());
        disableViews(binding.layoutNewUnitDetails.autoCompDiety);
        if(binding.layoutNewUnitDetails.autoCompDiety.getText().toString().equalsIgnoreCase("Others")){
            binding.layoutNewUnitDetails.etDietyOther.setVisibility(View.VISIBLE);
        }

        binding.layoutNewUnitDetails.etDietyOther.setText(previousUnitInfoPointDataModel.getType_of_diety_other());
        disableViews(binding.layoutNewUnitDetails.etDietyOther);
        binding.layoutNewUnitDetails.etDietyName.setText(previousUnitInfoPointDataModel.getName_of_diety());
//        disableViews(binding.layoutNewUnitDetails.etDietyName);
        binding.layoutNewUnitDetails.autoCompFaith.setText(previousUnitInfoPointDataModel.getCategory_of_faith());
        disableViews(binding.layoutNewUnitDetails.autoCompFaith);
        if(binding.layoutNewUnitDetails.autoCompFaith.getText().toString().equalsIgnoreCase("Others")){
            binding.layoutNewUnitDetails.etFaithOther.setVisibility(View.VISIBLE);
        }
        binding.layoutNewUnitDetails.etFaithOther.setText(previousUnitInfoPointDataModel.getCategory_of_faith_other());
        disableViews(binding.layoutNewUnitDetails.etFaithOther);
        binding.layoutNewUnitDetails.etSubCategoryFaith.setText(previousUnitInfoPointDataModel.getSub_category_of_faith());
        disableViews(binding.layoutNewUnitDetails.etSubCategoryFaith);
        binding.layoutNewUnitDetails.etRelBelongsStrucutre.setText(previousUnitInfoPointDataModel.getReligion_of_structure_belongs());
        disableViews(binding.layoutNewUnitDetails.etRelBelongsStrucutre);
        binding.layoutNewUnitDetails.autoCompStructureOwnership.setText(previousUnitInfoPointDataModel.getStructure_ownership_status());
        disableViews(binding.layoutNewUnitDetails.autoCompStructureOwnership);

        binding.layoutNewUnitDetails.autoCompStructureNature.setText(previousUnitInfoPointDataModel.getNature_of_structure());
        disableViews(binding.layoutNewUnitDetails.autoCompStructureNature);
//        binding.layoutNewUnitDetails.etStructureNatureOther.setText("");
        binding.layoutNewUnitDetails.etConstructionMaterial.setText(previousUnitInfoPointDataModel.getConstruction_material());
        disableViews(binding.layoutNewUnitDetails.etConstructionMaterial);
        binding.layoutNewUnitDetails.etNoOfPeoples.setText(previousUnitInfoPointDataModel.getDaily_visited_people_count());
        disableViews(binding.layoutNewUnitDetails.etNoOfPeoples);
        binding.layoutNewUnitDetails.etTenementRelAmenities.setText(previousUnitInfoPointDataModel.getTenement_number_rel_amenities());
//        disableViews(binding.layoutNewUnitDetails.etTenementRelAmenities);
        binding.layoutNewUnitDetails.autoCompTenementRelAmenities.setText(previousUnitInfoPointDataModel.getTenement_doc_used());
//        disableViews(binding.layoutNewUnitDetails.autoCompTenementRelAmenities);
        if(binding.layoutNewUnitDetails.autoCompTenementRelAmenities.getText().toString().equalsIgnoreCase("Others")){
            binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.setVisibility(View.VISIBLE);
        }
        binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers.setText(previousUnitInfoPointDataModel.getTenement_doc_other());
//        disableViews(binding.layoutNewUnitDetails.etTenementRelAmenitiesOthers);
        if((previousUnitInfoPointDataModel.getIs_structure_registered()!=null && previousUnitInfoPointDataModel.getIs_structure_registered().equals("1"))||
                (previousUnitInfoPointDataModel.getIs_structure_registered()!=null && previousUnitInfoPointDataModel.getIs_structure_registered().equalsIgnoreCase("Yes"))){
            binding.layoutNewUnitDetails.autoCompReligiousRegistered.setText("Yes");
            binding.layoutNewUnitDetails.registeredYesLayout.setVisibility(View.VISIBLE);
        }else{
            binding.layoutNewUnitDetails.autoCompReligiousRegistered.setText("No");
            binding.layoutNewUnitDetails.registeredYesLayout.setVisibility(View.GONE);
        }
        disableViews(binding.layoutNewUnitDetails.autoCompReligiousRegistered);


        binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.setText(previousUnitInfoPointDataModel.getStructure_registered_with());
        disableViews(binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes);
        if(binding.layoutNewUnitDetails.autoCompReligiousRegisteredWithYes.getText().toString().equalsIgnoreCase("Others")){
            binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.setVisibility(View.VISIBLE);
        }
        binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers.setText(previousUnitInfoPointDataModel.getOther_religious_authority());
        disableViews(binding.layoutNewUnitDetails.etReligiousRegisteredWithYesOthers);

        String trusteeNames=previousUnitInfoPointDataModel.getName_of_trustee();
        binding.layoutNewUnitDetails.imgFirstTrustee.setVisibility(View.INVISIBLE);
        try {
            if(trusteeNames!=null && trusteeNames.length()>0){
                String arr[]=trusteeNames.split(",");
                if(arr.length>0){
                    binding.layoutNewUnitDetails.etFirstTrustee.setText(arr[0].toString());
                }
                if(arr.length>1){
                    for(int i=1;i<arr.length;i++){
                        LayoutInflater inflater = activity.getLayoutInflater();
                        LinearLayout root = (LinearLayout) activity.findViewById(R.id.trusteeRootLayout);
                        View view = inflater.inflate(R.layout.layout_add_dynamic_edit_del, root, false);
                        ImageView deleteView=view.findViewById(R.id.deleteView);
                        deleteView.setVisibility(View.INVISIBLE);
                        EditText trustNameEditTexts=view.findViewById(R.id.trustNameEditTexts);
                        trustNameEditTexts.setText(arr[i].toString());
                        trustNameEditTexts.setFocusable(false);
                        trustNameEditTexts.setEnabled(false);
                        trustNameEditTexts.setBackgroundResource(R.drawable.rounded_blue_edittext);

                        root.addView(view);
                    }
                }
            }
            binding.layoutNewUnitDetails.etFirstTrustee.setFocusable(false);
            binding.layoutNewUnitDetails.etFirstTrustee.setEnabled(false);
            binding.layoutNewUnitDetails.etFirstTrustee.setBackgroundResource(R.drawable.rounded_blue_edittext);
        }catch (Exception ex){
            ex.getMessage();
        }



        String belongNames=previousUnitInfoPointDataModel.getName_of_trust_or_owner();
        binding.layoutNewUnitDetails.imgFirstBelongs.setVisibility(View.INVISIBLE);
        try {
            if(belongNames!=null && belongNames.length()>0){
                String arr[]=belongNames.split(",");
                if(arr.length>0){
                    binding.layoutNewUnitDetails.etFirstBelongs.setText(arr[0].toString());
                }
                if(arr.length>1){
                    for(int i=1;i<arr.length;i++){
                        LayoutInflater inflater = activity.getLayoutInflater();
                        LinearLayout root = (LinearLayout) activity.findViewById(R.id.trustRootLayout);
                        View view = inflater.inflate(R.layout.layout_add_dynamic_edit_del, root, false);
                        ImageView deleteView=view.findViewById(R.id.deleteView);
                        deleteView.setVisibility(View.INVISIBLE);
                        EditText trustNameEditTexts=view.findViewById(R.id.trustNameEditTexts);
                        trustNameEditTexts.setText(arr[i].toString());
                        root.addView(view);
                    }
                }
            }
        }catch (Exception ex){
            ex.getMessage();
        }


        binding.layoutNewUnitDetails.etLandownerName.setText(previousUnitInfoPointDataModel.getName_of_landowner());
        disableViews(binding.layoutNewUnitDetails.etLandownerName);

        if((previousUnitInfoPointDataModel.getNoc_from_landlord_or_govt()!=null && previousUnitInfoPointDataModel.getNoc_from_landlord_or_govt().equals("1"))||
                (previousUnitInfoPointDataModel.getNoc_from_landlord_or_govt()!=null && previousUnitInfoPointDataModel.getNoc_from_landlord_or_govt().equalsIgnoreCase("Yes"))){
            binding.layoutNewUnitDetails.autoCompNocYesNo.setText("Yes");
        }else{
            binding.layoutNewUnitDetails.autoCompNocYesNo.setText("No");
        }
        disableViews(binding.layoutNewUnitDetails.autoCompNocYesNo);

        if((previousUnitInfoPointDataModel.getApproval_from_govt()!=null && previousUnitInfoPointDataModel.getApproval_from_govt().equals("1"))||
                (previousUnitInfoPointDataModel.getApproval_from_govt()!=null && previousUnitInfoPointDataModel.getApproval_from_govt().equalsIgnoreCase("Yes"))){
            binding.layoutNewUnitDetails.autoCompApprovalYesNo.setText("Yes");
        }else{
            binding.layoutNewUnitDetails.autoCompApprovalYesNo.setText("No");
        }
        disableViews(binding.layoutNewUnitDetails.autoCompApprovalYesNo);

        if((previousUnitInfoPointDataModel.getYearly_festival_conducted()!=null && previousUnitInfoPointDataModel.getYearly_festival_conducted().equals("1"))||
                (previousUnitInfoPointDataModel.getYearly_festival_conducted()!=null && previousUnitInfoPointDataModel.getYearly_festival_conducted().equalsIgnoreCase("Yes"))){
            binding.layoutNewUnitDetails.autoCompFestivalYesNo.setText("Yes");
        }else{
            binding.layoutNewUnitDetails.autoCompFestivalYesNo.setText("No");
        }
        disableViews(binding.layoutNewUnitDetails.autoCompFestivalYesNo);

        binding.layoutNewUnitDetails.etPavtiRelAmenities.setText(previousUnitInfoPointDataModel.getSurvey_pavti_no_rel_amenities());
        disableViews(binding.layoutNewUnitDetails.etPavtiRelAmenities);
        binding.layoutNewUnitDetails.etMashalRelAmenities.setText(previousUnitInfoPointDataModel.getMashal_rel_amenities());
//        disableViews(binding.layoutNewUnitDetails.etMashalRelAmenities);
        binding.layoutNewUnitDetails.autoCompNoFloor.setText(previousUnitInfoPointDataModel.getRa_total_no_of_floors());
        disableViews(binding.layoutNewUnitDetails.autoCompNoFloor);


        binding.layoutNewUnitDetails.radioMemberAvailableYes.setClickable(false);
        binding.layoutNewUnitDetails.radioMemberAvailableNo.setClickable(false);
        disableViews(binding.layoutNewUnitDetails.etRespondentName);
        disableViews(binding.layoutNewUnitDetails.etRespondentContact);
        disableViews(binding.layoutNewUnitDetails.etDobRespondent);

//        binding.layoutNewUnitDetails.autoCompDiety.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_diety)));
//        setFocusChange_OnTouch(binding.layoutNewUnitDetails.autoCompDiety);

    }

    private void disableViews(View view){
        view.setFocusable(false);
        view.setEnabled(false);
        view.setBackgroundResource(R.drawable.rounded_blue_edittext);
    }

    public void getIMEIDeviceId(Context context) {
        try {
            String deviceId="";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                deviceN = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
                    return;
                }
                final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                assert mTelephony != null;
                if (mTelephony.getDeviceId() != null)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
//                        deviceId = mTelephony.getImei();
                    }else {
                        deviceId = mTelephony.getDeviceId();
                        deviceN = mTelephony.getDeviceId();
                    }
                } else {
                    deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    deviceN = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            }
            Log.d("deviceId", deviceId);


            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(getActivity());
            String imeiSIM1 = telephonyInfo.getImeiSIM1();
            String imeiSIM2 = telephonyInfo.getImeiSIM2();
            pImei=imeiSIM1;
            sImei=imeiSIM2;
            deviceN=deviceId;
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // in the below line, we are calling on request permission result method.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            // in the below line, we are checking if permission is granted.
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if permissions are granted we are displaying below toast message.
                Toast.makeText(getActivity(), "Permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // in the below line, we are displaying toast message
                // if permissions are not granted.
                Toast.makeText(getActivity(), "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteAllMedia(RadioButton prev,RadioButton curr){
        try{
            if(prev!=null && curr!=null){
                if(prev != curr){
                    if(ft!=1){
                        showActionAlertDialogOkButton("DRP App Status", "Ok", "Cancel", "If you change usage, you might lost previous fill information of this form!",prev);
                    }else{
                        ft=0;
                    }
                }
            }
//            if(previousUnitInfoPointDataModel!=null && previousUnitInfoPointDataModel.getUnit_id()!=null){
//                localSurveyDbViewModel.deleteMediaInfoData(previousUnitInfoPointDataModel.getUnit_id(),activity,"Distometer","unit_info");
//                globalUnitVideoPath="";
//                globalUnitPdfPath="";
//                pdfPathRes="";
//                pdfPathComm="";
//                userAttachmentList=new ArrayList<>();
//            }
//            if(previousUnitInfoPointDataModel==null){
//                localSurveyDbViewModel.deleteMediaInfoData(unitUniqueId,activity,"Distometer","unit_info");
//                globalUnitVideoPath="";
//                globalUnitPdfPath="";
//                pdfPathRes="";
//                pdfPathComm="";
//                userAttachmentList=new ArrayList<>();
//            }
//            removeData();
        }catch (Exception ex){
            ex.getMessage();
        }
    }

    private void removeData(){
        binding.layoutNewUnitDetails.etRespondentRelationHoh.setText("");
        binding.layoutNewUnitDetails.etTenementNo.setText("");
        binding.layoutNewUnitDetails.etMashal.setText("");
        binding.layoutNewUnitDetails.etUnitArea.setText("");
        binding.layoutNewUnitDetails.etPdfDistometer.setText("0 out of 1 attached");
        binding.layoutNewUnitDetails.etLoftArea.setText("");
        binding.layoutNewUnitDetails.etExistenceSince.setText("");
        binding.layoutNewUnitDetails.etRelationOwnerEmpComm.setText("");
        binding.layoutNewUnitDetails.etTenementNoComm.setText("");
        binding.layoutNewUnitDetails.etMashalComm.setText("");
        binding.layoutNewUnitDetails.etRcResAreaLayout.setText("");
        binding.layoutNewUnitDetails.etRcCommAreaLayout.setText("");
        binding.layoutNewUnitDetails.etUnitAreaComm.setText("");
        binding.layoutNewUnitDetails.etUnitGomasta.setText("");
        binding.layoutNewUnitDetails.etLoftAreaComm.setText("");
        binding.layoutNewUnitDetails.etLoftAreaNotEditComm.setText("");
        binding.layoutNewUnitDetails.etExistenceSinceComm.setText("");
        binding.layoutNewUnitDetails.etNoOfEmployee.setText("");






        binding.layoutNewUnitDetails.autoCompRespondentRelationHOH.setText("");
        binding.layoutNewUnitDetails.autoCompDocTenement.setText("");
        binding.layoutNewUnitDetails.autoCompRelationOwnerEmpComm.setText("");
        binding.layoutNewUnitDetails.autoCompDocTenementComm.setText("");


        binding.layoutNewUnitDetails.radioGroupUnitStatusRentOwner.clearCheck();
        binding.layoutNewUnitDetails.radioGroupLoft.clearCheck();
        binding.layoutNewUnitDetails.radioGroupUnitStatusRentOwnerComm.clearCheck();
        binding.layoutNewUnitDetails.radioGroupLoftComm.clearCheck();


        binding.layoutNewUnitDetails.layCommercialProof.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.layRCProof.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.layResidentProof.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.layRelAmenitiesProof.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.layOwnerRent.setVisibility(View.GONE);
    }


    public void showActionAlertDialogOkButton(String header, String yesBtn, String noBtn, String message,RadioButton radioButton) {
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
        txt_mssage.setTextColor(activity.getResources().getColor(R.color.black));

        TextView txt_yes = customLayout.findViewById(R.id.txt_yes);
        TextView txt_no = customLayout.findViewById(R.id.txt_no);
        txt_no.setText(noBtn);

        txt_yes.setText(yesBtn);

        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);
        btn_no.setVisibility(View.VISIBLE);

        ImageView img_close = customLayout.findViewById(R.id.img_close);
        img_close.setVisibility(View.GONE);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);
        statusRadioGroup.setVisibility(View.GONE);

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(view1 -> {
            dialog.dismiss();
            try{
                if(previousUnitInfoPointDataModel!=null && previousUnitInfoPointDataModel.getUnit_id()!=null){
                    localSurveyDbViewModel.deleteMediaInfoData(previousUnitInfoPointDataModel.getUnit_id(),activity,"Distometer","unit_info");
                    globalUnitVideoPath="";
                    globalUnitPdfPath="";
                    pdfPathRes="";
                    pdfPathComm="";
                    userAttachmentList=new ArrayList<>();
                }
                if(previousUnitInfoPointDataModel==null){
                    localSurveyDbViewModel.deleteMediaInfoData(unitUniqueId,activity,"Distometer","unit_info");
                    globalUnitVideoPath="";
                    globalUnitPdfPath="";
                    pdfPathRes="";
                    pdfPathComm="";
                    userAttachmentList=new ArrayList<>();
                }
                removeData();
            }catch (Exception ex){
                ex.getMessage();
            }
        });

        btn_no.setOnClickListener(view1 -> {
            ft=1;
            radioButton.setChecked(true);
            dialog.dismiss();
        });

        dialog.show();
    }

    public void openalert(Activity context, String msg) {
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                context.getResources().getString(R.string.yesBtn),
                (dialog, id) -> alertOperationYes(dialog)
        );
        
        builder1.setNegativeButton(
                context.getResources().getString(R.string.noBtn),
                (dialog, id) -> alertOperationNo(dialog)

        );

        android.app.AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.setCancelable(false);
        alert11.show();
    }

    private void alertOperationYes(DialogInterface dialogInterface){
        surveyFlag=true;
        dialogInterface.cancel();
        binding.layoutNewUnitDetails.unitUniqueIdAttachLayout.setVisibility(View.VISIBLE);
        binding.layoutNewUnitDetails.txtMemberAvailable.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.radioGroupMemberAvailable.setVisibility(View.GONE);
        binding.txtNext.setText("Finish");
        resYes = false;
        isResident = "No";
        binding.layoutNewUnitDetails.residentYesLayout.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.detailsLayout.setVisibility(View.GONE);
    }

    private void alertOperationNo(DialogInterface dialogInterface){
        surveyFlag=false;
        dialogInterface.cancel();
        binding.layoutNewUnitDetails.unitUniqueIdAttachLayout.setVisibility(View.GONE);
        binding.layoutNewUnitDetails.txtMemberAvailable.setVisibility(View.VISIBLE);
        binding.layoutNewUnitDetails.radioGroupMemberAvailable.setVisibility(View.VISIBLE);
    }

    private String getFloorNumber(String hutNumber) {

        String floor = "0";

        try {

            boolean isFound = false;

            String[] splittedData = hutNumber.split("/");

            for(int i = splittedData.length - 1 ; i > 0 ; i--) {
                if(splittedData[i].startsWith("F")) {
                    floor = splittedData[i].substring(1);
                    isFound = true;
                    break;
                }else if(splittedData[i].startsWith("M")) {
                    floor = "M";
                    isFound = true;
                    break;
                }
            }

            if(!isFound) {
                floor = "0";
            }
        } catch (Exception ex) {
            floor = "0";
            ex.getMessage();
        }

        return floor;
    }

}
//6jan 8 Pm