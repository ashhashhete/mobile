package com.igenesys.view;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.gson.JsonElement;
import com.igenesys.App;
import com.igenesys.HohActivity;
import com.igenesys.MapActivity;
import com.igenesys.MemberActivity;
import com.igenesys.R;
import com.igenesys.adapter.AttachmentListAdapter;
import com.igenesys.adapter.HorizontalAdapter;
import com.igenesys.adapter.PostCompleteImageAdapter;
import com.igenesys.adapter.ViewAttachAdapter;
import com.igenesys.database.LocalSurveyDbViewModel;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.databinding.ActivityMemberBinding;
import com.igenesys.databinding.LayoutNewMemberDetailsBinding;
import com.igenesys.datamin.AuthBfdCap;
import com.igenesys.datamin.MorphoTabletFPSensorDevice;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.model.AttachmentListImageDetails;
import com.igenesys.model.AutoCompleteModal;
import com.igenesys.model.WorkAreaModel;
import com.igenesys.networks.Api_Interface;
import com.igenesys.networks.RetrofitService;
import com.igenesys.utils.AES;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.AppPermissions;
import com.igenesys.utils.Constants;
import com.igenesys.utils.CorrectImageRotation;
import com.igenesys.utils.CryptoUtilsTest;
import com.igenesys.utils.DeleteCallBack;
import com.igenesys.utils.FullScreenImage;
import com.igenesys.utils.GenericTextWatcher;
import com.igenesys.utils.MaskedEditText;
import com.igenesys.utils.Utils;
import com.igenesys.utils.YesNoBottomSheet;
import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import org.bouncycastle.crypto.CryptoException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberDetailsViewModel extends ActivityViewModel<MemberActivity> implements AuthBfdCap,AttachmentListAdapter.OnAttachmentItemClickListner, ViewAttachAdapter.OnViewClickListner, DeleteCallBack {

    String transactionID="";
    EditText etHohMobileForOTP;
    boolean imgCaptured=false;
    FrameLayout thumb_pic_items_fl;
    AutoCompleteTextView thumbRemark;
    Bitmap thumbPicBitmap=null;
    public static MorphoTabletFPSensorDevice fpSensorCap;
    Handler mHandler;
    private ImageView imgFP,thumbPic;
    public static byte[] isoTemplate = null;
    int cont=1;
    boolean verify=false;
    private boolean edd = false;
    private boolean isMemberValidated = false;
    private boolean member = false;
    private boolean isDocSelected = false;

    private boolean memberDob = false;

    private Activity memberActivity;

    private Calendar myCalendar;

    private int memberAge;
    private int docYear = 0, unitVisitCount = 0;
    private int globalAge=200;
    private int ageFlag=-1;
    private int year = 0;
    private ArrayList<AttachmentListImageDetails> userAttachmentList, resident_scAttachmentList, resident_ecbAttachmentList, resident_ppAttachmentList, resident_nataxAttachmentList, resident_ptprAttachmentList, resident_erAttachmentList;
    private AttachmentListAdapter resident_scAttachmentListAdapter, resident_ecbAttachmentListAdapter, resident_ppAttachmentListAdapter, resident_naTaxAttachmentListAdapter, resident_ptprAttachmentListAdapter, resident_ErAttachmentListAdapter, addImageAdapter;
    

    private List<MediaInfoDataModel> newMediaInfoDataModels;
    private HashMap<String, ArrayList<MemberInfoDataModel>> expandableListDetail;

    private AlertDialog dialogGlobal;
    private String buttonName = "", nagar_name = "";
    private LocalSurveyDbViewModel localSurveyDbViewModel;
    private String cc="";

    public static String AttName="";
    private String memberUniqueId = "";

    private String memberGender = "";
    private String handicapOrNot = "";
    private String stayingwith = "";
    private String member_relative_path = "";
    private  ArrayList<MemberInfoDataModel> existingMemberInfoDataModelAL;
    private ViewAttachAdapter viewAttachAdapter;
    private UnitInfoDataModel unitInfoDataModel;
    private HohInfoDataModel hohInfoDataModel;
    private ArrayList<MediaInfoDataModel> mediaInfoDataModels1;
    private File pdfPathFile = null;
    private int residentProofAttachmentCount = 0;
    private ArrayList<AttachmentListImageDetails> memberPhotographAttachmentList, memberAdhaarCardAttachmentList, memberPanCardAttachmentList;
    private List<String> getPreviousMediaFileName;

    private ArrayList<String> okFileExtensions = new ArrayList<>();
    private AttachmentListAdapter memberPhotographAttachmentListAdapter, memberAdhaarCardAttachmentListAdapter, memberPanCardAttachmentListAdapter;
    private String attachmentFor = "";
    private String target_relative_path = "", target_name = "";
    private File captureImagePath;
    private Uri imageUri;
    private final int selectCamera = 10, selectGallery = 20, selectPdf = 30;
    private int alpha = 0;
    private MediaInfoDataModel updObj;
    private String unitAlertStatus;
    private String workAreaName;
    private int deathCertificate;
    private String originalAadhaarNo = "";
    private ArrayList<MediaInfoDataModel> previousPanchnamaDocument,previousBoimetricDocument,previousAnnexureDocument, savedPanchnamaDocument,savedBoimetricDocument,savedAnnexureDocument ;
    boolean isPanchnamaExist=false;
    boolean isBiomatricExist=false;
    boolean isAnxDocumentDelete = false;
    int annexureFlag = 0;
    boolean isRelAmen=false;
    PostCompleteImageAdapter viewDeleteAdapterDelete=null;
    PostCompleteImageAdapter thumbAdapter=null;

private MemberInfoDataModel selectedmemberInfoDataModel;

/*
Rohit Panchnama
 */
private ImageView tempImageButton;
private FrameLayout tempImageLayout;
private String globalPanchnamaPath="";
private String globalAnnexureAPath="",globalAnnexureBPath="";

private int ann=0;
    RelativeLayout anexureOneLayout;
    RelativeLayout anexureTwoLayout;
    ImageView annexureOneCapturedDocument;
    ImageView annexureTwoCapturedDocument;
    private String member_rel_global_id = "";
    private LayoutNewMemberDetailsBinding memberBinding;
    private StructureInfoPointDataModel structureInfoPointDataModel;
    private Button activityCancelBtn;

    private boolean floorFlag=false;

//    private RecyclerView imageRecyclerDelete;

    /*
    * This constructor is used to initialize object of
    * 1. com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel
    * 2. com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel
    * 3. localSurveyDbViewModel: used to make db operations
    * 4. activityMemberBinding: used to access initialized UI components
    * 5. existingMemberInfoDataModelAL: consist of multiple members coming which exists in db & coming from previous hoh form
    * 6. memberActivity: represents this activity.
    * 7. memberUniqueId: holds primary key value of memberInfoDataTable
    * 8. member_relative_path: holds value of column memberInfoDataTable.relative_path
    * 9. member_rel_global_id: holds value of column memberInfoDataTable.rel_globalid
    * */

    public MemberDetailsViewModel(MemberActivity memberActivity) {
        super(memberActivity);
        Intent intent = memberActivity.getIntent();
        UnitInfoDataModel unitInfoDataModel=null;
        HohInfoDataModel hohInfoDataModel=null;
        String hohName="";
        if (intent.hasExtra(Constants.INTENT_DATA_UnitInfo)) {
            unitInfoDataModel = (UnitInfoDataModel) intent.getSerializableExtra(Constants.INTENT_DATA_UnitInfo);
        }else{
            Utils.shortToast("Unit Info Not Found.",activity);
        }


        if (intent.hasExtra(Constants.INTENT_DATA_HohInfo)) {
            hohInfoDataModel = (HohInfoDataModel) intent.getSerializableExtra(Constants.INTENT_DATA_HohInfo);
            hohName = hohInfoDataModel.getHoh_name();
        }else{
            Utils.shortToast("HOH Info Not Found.",activity);
        }
        if(intent.hasExtra(Constants.INTENT_DATA_MamberInfo)) {
            this.existingMemberInfoDataModelAL = (ArrayList<MemberInfoDataModel>) intent.getSerializableExtra(Constants.INTENT_DATA_MamberInfo);
        }

        ActivityMemberBinding activityMemberBinding = DataBindingUtil.setContentView(memberActivity, R.layout.activity_member);
        this.memberActivity = memberActivity;
        this.unitInfoDataModel = unitInfoDataModel;
        this.hohInfoDataModel = hohInfoDataModel;
        localSurveyDbViewModel = new LocalSurveyDbViewModel(memberActivity.getApplication());
        memberBinding = activityMemberBinding.layoutNewMemberDetails;
        this.activityCancelBtn = activityMemberBinding.btnCancel;
//        updateMemberListInUI(existingMemberInfoDataModelAL.get(0));
        activityMemberBinding.stepView.go(3, true);

//        if(intent.hasExtra(Constants.MEMBER_INFO_TO_RESTORE_FORM)) {
//            Bundle bundle = memberActivity.getIntent().getBundleExtra(Constants.MEMBER_INFO_TO_RESTORE_FORM);
//            if(bundle.containsKey(Constants.memberInfo_member_id)) {
//                applyBundleDataToForm(bundle);
//            }
//        }
        if(intent.hasExtra("isRelAmen") && intent.getBooleanExtra("isRelAmen",false)){
            isRelAmen=true;
            showUnitStatus();
            setUpAdapterNListeners();
            return;
        }
        aadharTextWatcher();
        memberBinding.etNameSpouse.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    memberBinding.etNameSpouse.setText(Utils.capitalizeEachWord(memberBinding.etNameSpouse.getText().toString()));
                }
            }
        });
        memberBinding.etFirstNameMember.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    memberBinding.etFirstNameMember.setText(Utils.capitalizeEachWord(memberBinding.etFirstNameMember.getText().toString()));
                }
            }
        });
        memberBinding.hohNameTv.setText(hohName);
/*
        if(existingMemberInfoDataModelAL!= null && existingMemberInfoDataModelAL != null && existingMemberInfoDataModelAL.size() > 0) {
//            memberBinding.txtSubmit.setText("Update");
            memberUniqueId = existingMemberInfoDataModelAL.get(0).getMember_id();
//            saveMemberFormToDb();
            updateMemberListInUI(null);
        } else {
//            memberBinding.txtSubmit.setText("Submit");
        }
*/
        AppLog.e("HutID:"+hohInfoDataModel.getHoh_id());
        syncMemberWithLiveData(hohInfoDataModel.getHoh_id());

        memberBinding.btnSubmit.setOnClickListener(view -> {
            if(memberValidation()) {
                memberBinding.etNameSpouse.setText(Utils.capitalizeEachWord(memberBinding.etNameSpouse.getText().toString()));
                memberBinding.etFirstNameMember.setText(Utils.capitalizeEachWord(memberBinding.etFirstNameMember.getText().toString()));
                if (!memberBinding.etAadhaarNumber.getText().toString().equals("") && !memberBinding.etAadhaarNumber.getText().toString().contains("x")){
                    MaskedEditText.setMaskedText(memberBinding.etAadhaarNumber,memberBinding.etAadhaarNumber.getText().toString());
                }
                if(memberBinding.txtSubmit.getText().equals("Update")) {
                    memberBinding.txtSubmit.setText("Submit");
                    //updateMemberFormToDb();
                    updateMemberToDb();
                } else if(memberBinding.txtSubmit.getText().equals("Submit")) {
                    Utils.shortToast("Success", memberActivity);
                    saveMemberFormToDb();
                }
                removeTagValue();
            }
        });
        memberBinding.btnCancel.setOnClickListener(view -> {
            clearAllFields();
            if(memberBinding.txtSubmit.getText().toString().equalsIgnoreCase("update")) {
                memberBinding.txtSubmit.setText("Submit");
            }
            memberUniqueId = "";
        });
        activityMemberBinding.btnNextMemberForm.setOnClickListener(view -> {
            showUnitStatus();
        });

//        activityMemberBinding.btnCancel.setOnClickListener(view -> {
//            showUnitStatus();
//            finish();
//        });

        setUpAdapterNListeners();
        memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
        member_relative_path = unitInfoDataModel.getRelative_path() + hohInfoDataModel.getHoh_id() + "/" + memberUniqueId + "/";
        member_rel_global_id = hohInfoDataModel.getGlobalId();

//        Utils.disableCopyPaste(memberBinding.etAadhaarNumber);
//        Utils.disableCopyPaste(memberBinding.etPanNumber);

        floorFlag=App.getSharedPreferencesHandler().getBoolean(Constants.floorFlag);
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

    /*
    * When user press any attach button out of 6 & clicks on browse button than this method is invoked
    * to show 3 options Camera, Image, PDF.
    *
    * */

    public void showAttachmentAlertDialogButtonClicked(String clickedFrom, String relative_path, String name) {
try {
    if (!Utils.checkAutodateTimeValidation(this.memberActivity)) {
        return;
    }

    attachmentFor = clickedFrom;
    target_relative_path = relative_path;
    target_name = name;


    // Create an alert builder
    AlertDialog.Builder builder = new AlertDialog.Builder(memberActivity);
    // set the custom layout
    final View customLayout = memberActivity.getLayoutInflater().inflate(R.layout.custom_alert_attachment_picker, null);
    builder.setView(customLayout);
    AlertDialog dialog = builder.create();
    TextView txt_Camera = customLayout.findViewById(R.id.txt_Camera);
    TextView txt_Gallery = customLayout.findViewById(R.id.txt_Gallery);
    TextView txt_ChooseFile = customLayout.findViewById(R.id.txt_ChooseFile);

    txt_Camera.setOnClickListener(view1 -> {
        if (AppPermissions.cameraPermission(memberActivity, true)) {
            imageUri = Utils.getCaptureImageOutputUri(memberActivity, target_relative_path, target_name);
            captureImagePath = Utils.getFile(memberActivity, target_relative_path, target_name);
            if (imageUri != null) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                memberActivity.startActivityForResult(cameraIntent, selectCamera);
                dialog.dismiss();
            } else Utils.shortToast("Unable to capture image.", memberActivity);
        }

    });

    txt_Gallery.setOnClickListener(view1 -> {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        memberActivity.startActivityForResult(Intent.createChooser(intent, "Select File"), selectGallery);

        dialog.dismiss();
    });

    txt_ChooseFile.setOnClickListener(view1 -> {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, true);
        memberActivity.startActivityForResult(Intent.createChooser(intent, "Select Pdf"), selectPdf);

        dialog.dismiss();
    });

    dialog.show();
}catch(Exception ex){
    AppLog.logData(activity,ex.getMessage());
    Utils.shortToast(ex.getMessage(),activity);
}
    }

    /*
    * When user presses any attach button out of 6 in member form than this method is invoked.
    * */

    private void showDocumentPopup(int usageType, int btnNumber, int selectedYear) {
        try{
            resident_ecbAttachmentList = new ArrayList<>();
            String[] arr = new String[30];

            AlertDialog.Builder builder = new AlertDialog.Builder(memberActivity);
            final View customLayout = memberActivity.getLayoutInflater().inflate(R.layout.custom_document_layout, null);
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

            if (usageType == 6) {
                if (btnNumber == 1 && selectedYear == 1001) {
                    buttonName = "hoh_member_Aadhar_Card";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = memberActivity.getResources().getStringArray(R.array.hoh_member_adhar);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 2 && selectedYear == 1001) {
                    buttonName = "hoh_member_Pan_Card";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = memberActivity.getResources().getStringArray(R.array.hoh_member_pan);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 3 && selectedYear == 1001) {
                    buttonName = "hoh_member_Ration_Card";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = memberActivity.getResources().getStringArray(R.array.hoh_member_ration);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 4 && selectedYear == 1001) {
                    buttonName = "hoh_member_Death_Certificate";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = memberActivity.getResources().getStringArray(R.array.hoh_member_death_certificate);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 5 && selectedYear == 1001) {
                    buttonName = "hoh_member_Salary_Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = memberActivity.getResources().getStringArray(R.array.hoh_member_salary_proof);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, newList));
                    setFocusChange_OnTouch(autoCompDocType);
                } else if (btnNumber == 6 && selectedYear == 1001) {
                    buttonName = "hoh_member_Disease_Proof";
                    List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByCatUnit(buttonName, memberUniqueId);
                    newMediaInfoDataModels = getMediaInfoData;
                    arr = memberActivity.getResources().getStringArray(R.array.hoh_member_deseas);
                    String newList[] = getUpdatedDocList(newMediaInfoDataModels, arr);
                    autoCompDocType.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, newList));
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
            imageRecycler.setLayoutManager(new LinearLayoutManager(memberActivity, LinearLayoutManager.HORIZONTAL, false));
            imageRecycler.setAdapter(new HorizontalAdapter(new int[]{R.drawable.img_placeholder, R.mipmap.ic_launcher, R.drawable.img_placeholder, R.mipmap.ic_launcher, R.drawable.img_placeholder}, newMediaInfoDataModels, 1, memberActivity));
            imageRecyclerDelete.setLayoutManager(new LinearLayoutManager(memberActivity, LinearLayoutManager.HORIZONTAL, false));

            RecyclerView addImageRecycler = customLayout.findViewById(R.id.addImageRecycler);
            addImageAdapter = new AttachmentListAdapter(memberActivity, null, "", this);
            addImageRecycler.setLayoutManager(new LinearLayoutManager(memberActivity, LinearLayoutManager.VERTICAL, false));
            addImageRecycler.setAdapter(addImageAdapter);

            addTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewTab.setTextColor(memberActivity.getResources().getColor(R.color.black));
                    addTab.setTextColor(memberActivity.getResources().getColor(R.color.summaryEditBoarderColor));
                    addLayout.setVisibility(View.VISIBLE);
                    viewLayout.setVisibility(View.GONE);
                    viewNewLayout.setVisibility(View.GONE);
                    viewNoRecord.setVisibility(View.GONE);
                    deleteLayout.setVisibility(View.GONE);
                    docTitle.setText(buttonName);
                    title.setText(FormPageViewModel.AttName);
                }
            });

            viewTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userAttachmentList = new ArrayList<>();
                    edtypeProof.setText(buttonName);
                    addTab.setTextColor(memberActivity.getResources().getColor(R.color.black));
                    viewTab.setTextColor(memberActivity.getResources().getColor(R.color.summaryEditBoarderColor));
                    addLayout.setVisibility(View.GONE);
                    deleteLayout.setVisibility(View.GONE);
                    viewLayout.setVisibility(View.GONE);
                    viewNewLayout.setVisibility(View.VISIBLE);
                    title.setText(FormPageViewModel.AttName);
                    imageRecyclerDelete.setVisibility(View.GONE);

                    if (cc.equals("member") && memberUniqueId != null && memberUniqueId.length() > 0) {
                        List<MediaInfoDataModel> getMediaInfoData = localSurveyDbViewModel.getMediaInfoDataByRemovedCatUnit(buttonName, memberUniqueId, false);
                        newMediaInfoDataModels = getMediaInfoData;
                        if (newMediaInfoDataModels != null && newMediaInfoDataModels.size()>0){
                        new Handler().postDelayed(() -> {
                            viewRecycler.setLayoutManager(new LinearLayoutManager(memberActivity, LinearLayoutManager.HORIZONTAL, false));
                            viewAttachAdapter = new ViewAttachAdapter(getMediaInfoData, 1, memberActivity, member_relative_path, memberUniqueId, MemberDetailsViewModel.this, localSurveyDbViewModel);
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
                    } else {
                        addErrorLayout.setVisibility(View.GONE);
                        if (cc.equals("member")) {

                            // #970 : Name Member
                            if(memberBinding.etFirstNameMember.getText().toString().isEmpty()) {
                                if(dialog != null & dialog.isShowing())
                                    dialog.dismiss();
                                memberBinding.etFirstNameMember.setError(activity.getString(R.string.mandatory_field));
                                memberBinding.etFirstNameMember.requestFocus();
                                return;
                            }

                            String fileName = Utils.getAttachmentFileName(memberBinding.etFirstNameMember.getText().toString(), autoCompDocType.getText().toString());
//                            showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), member_relative_path, memberUniqueId + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());
                            showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), member_relative_path, fileName);
                        }

                    }
                }
            });

            btnBrowseEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cc.equals("member")) {
//                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), member_relative_path, memberUniqueId + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());
                        showAttachmentAlertDialogButtonClicked(autoCompDocType.getText().toString(), member_relative_path, "member" + "_" + autoCompDocType.getText().toString() + "_" + Utils.getEpochDateStamp());
                    }
                }
            });


            et_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dialog = new DatePickerDialog(memberActivity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMaxDate(new Date().getTime());
                    dialog.show();
                }
            });

            calDeleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dialog = new DatePickerDialog(memberActivity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
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
                            if (cc.equals("member")) {
                                attach = getmediaInfoDataList(userAttachmentList, member_relative_path, Constants.member_infoLayer,
                                        memberUniqueId, member_rel_global_id);
                            }

                            mediaInfoDataModels1 = new ArrayList<>();
                            if (attach.size() > 0) {
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
                                localSurveyDbViewModel.insertAllMediaInfoPointData(mediaInfoData, memberActivity);
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
        }catch(Exception ex){
            AppLog.logData(activity,ex.getMessage());
            Utils.shortToast(ex.getMessage(),memberActivity);
        }
    }

    private ArrayList<MediaInfoDataModel> getmediaInfoDataList(ArrayList<AttachmentListImageDetails> attachmentImageDetailsList, String relative_path,
                                                           String infoLayer, String id, String globlId) {
        ArrayList<MediaInfoDataModel> mediaInfoDataModels1 = new ArrayList<>();
        MediaInfoDataModel mm = null;
        try{
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

            // Report_150424_165344, Report_150424_134355, Report_150424_121340,
            // Report_150424_112242, Report_160424_120846
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
    }catch(Exception ex){
            AppLog.logData(activity,ex.getMessage());
        Utils.shortToast(ex.getMessage(),activity);
    }
        return mediaInfoDataModels1;
    }

    /*
    * Member form show data when user presses any member from the member list. This method gets data from db & shows on the member UI.
    * */

    public void applyExistingMemberToUI() {
       try{
        selectedmemberInfoDataModel = localSurveyDbViewModel.getMemberInfoDataWithMemberId(memberUniqueId).get(0);
        int isDeathCertificateAvailable = selectedmemberInfoDataModel.getDeath_certificate();
            if(selectedmemberInfoDataModel.getHandicap_or_critical_disease().equalsIgnoreCase("Yes")) {
                memberBinding.radioGroupHandicapped.check(R.id.radio_handicapped_Yes);
            } else if(selectedmemberInfoDataModel.getHandicap_or_critical_disease().equalsIgnoreCase("No")) {
                memberBinding.radioGroupHandicapped.check(R.id.radio_handicapped_No);
            } else {
                memberBinding.radioGroupHandicapped.clearCheck();
            }
            String stayingwith =  selectedmemberInfoDataModel.getStaying_with();
            boolean isGenderMale = selectedmemberInfoDataModel.getGender().equalsIgnoreCase("male");
            boolean isGenderFemale = selectedmemberInfoDataModel.getGender().equalsIgnoreCase("female");
            boolean isOther = selectedmemberInfoDataModel.getGender().equalsIgnoreCase("transgender");
            try {
//                String formattedDate = formattedDate(selectedmemberInfoDataModel.getMember_dob());
                memberBinding.etDob.setText(selectedmemberInfoDataModel.getMember_dob());
            } catch (Exception e) {
                AppLog.logData(activity,e.getMessage());
                Log.e("Error_in_timstamp= ", " " + e.getMessage());
            }
            String age = "";

            if(selectedmemberInfoDataModel.getAge() != 0) {
                age = String.valueOf(selectedmemberInfoDataModel.getAge());
            }
            String numSpouse = "";
            if(selectedmemberInfoDataModel.getMember_spouse_count() != 0) {
                numSpouse = String.valueOf(selectedmemberInfoDataModel.getMember_spouse_count());
            }
            String staying = "";
            if(selectedmemberInfoDataModel.getStaying_since_year() != 0) {
                staying = String.valueOf(selectedmemberInfoDataModel.getStaying_since_year());
            }
            memberBinding.etFirstNameMember.setText(selectedmemberInfoDataModel.getMember_name());

            memberBinding.autoCompRelationWitHOH.setTag(selectedmemberInfoDataModel.getRelationship_with_hoh());
            memberBinding.autoCompRelationWitHOH.setText(Utils.getTextByTag(Constants.domain_relationship_with_hoh,selectedmemberInfoDataModel.getRelationship_with_hoh()));

            if(selectedmemberInfoDataModel.getRelationship_with_hoh().equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etRelationshipWithHohOther.setText(selectedmemberInfoDataModel.getRelationship_with_hoh_other());
                memberBinding.etRelationshipWithHohOther.setVisibility(View.VISIBLE);
            } else {
                memberBinding.etRelationshipWithHohOther.setText("");
                memberBinding.etRelationshipWithHohOther.setVisibility(View.GONE);
            }

            memberBinding.autoCompMaritialStatus.setTag(selectedmemberInfoDataModel.getMarital_status());
            memberBinding.autoCompMaritialStatus.setText(Utils.getTextByTag(Constants.domain_marital_status,selectedmemberInfoDataModel.getMarital_status()));

           if(selectedmemberInfoDataModel.getMarital_status().equalsIgnoreCase(Constants.dropdown_others)) {
               memberBinding.etMaritalStatusOther.setText(selectedmemberInfoDataModel.getMarital_status_other());
               memberBinding.etMaritalStatusOther.setVisibility(View.VISIBLE);
               memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
               memberBinding.etMemberNumberSpouse.setText("0");
               memberBinding.etNameSpouse.setText("");
               memberBinding.spouseLayout.setVisibility(View.GONE);
           } else {
               memberBinding.etMaritalStatusOther.setText("");
               memberBinding.etMaritalStatusOther.setVisibility(View.GONE);
           }

            memberBinding.etMemberNumberSpouse.setText(numSpouse);
            memberBinding.etNameSpouse.setText(selectedmemberInfoDataModel.getMember_spouse_name());
            memberBinding.etRespondentContact.setText(selectedmemberInfoDataModel.getMember_contact_no());
            memberBinding.etMemberAge.setText(age);
            memberBinding.etStayingYear.setText(staying);
//            memberBinding.etAadhaarNumber.setText(selectedmemberInfoDataModel.getAadhar_no());
            MaskedEditText.setMaskedText(memberBinding.etAadhaarNumber,selectedmemberInfoDataModel.getAadhar_no());
            memberBinding.etPanNumber.setText(selectedmemberInfoDataModel.getPan_no());
            memberBinding.autoCompRationColor.setTag(selectedmemberInfoDataModel.getRation_card_colour());
           memberBinding.autoCompRationColor.setText(Utils.getTextByTag(Constants.domain_ration_card_color,selectedmemberInfoDataModel.getRation_card_colour()));

           memberBinding.etRationCardNumber.setText(selectedmemberInfoDataModel.getRation_card_no());
           memberBinding.autoCompReligion.setTag(selectedmemberInfoDataModel.getReligion());
           memberBinding.autoCompReligion.setText(Utils.getTextByTag(Constants.domain_religion,selectedmemberInfoDataModel.getReligion()));

           if(selectedmemberInfoDataModel.getReligion().equalsIgnoreCase(Constants.dropdown_others)) {
               memberBinding.etReligionOther.setText(selectedmemberInfoDataModel.getReligion_other());
               memberBinding.etReligionOther.setVisibility(View.VISIBLE);
           } else {
               memberBinding.etReligionOther.setText("");
               memberBinding.etReligionOther.setVisibility(View.GONE);
           }

           memberBinding.autoCompWhichState.setTag(selectedmemberInfoDataModel.getFrom_state());
           memberBinding.autoCompWhichState.setText(Utils.getTextByTag(Constants.domain_state,selectedmemberInfoDataModel.getFrom_state()));

           if(selectedmemberInfoDataModel.getFrom_state().equalsIgnoreCase(Constants.dropdown_others)) {
               memberBinding.etWhichStateOther.setText(selectedmemberInfoDataModel.getFrom_state_other());
               memberBinding.etWhichStateOther.setVisibility(View.VISIBLE);
           } else {
               memberBinding.etWhichStateOther.setText("");
               memberBinding.etWhichStateOther.setVisibility(View.GONE);
           }

           memberBinding.autoCompMotherTongue.setTag(selectedmemberInfoDataModel.getMother_tongue());
           memberBinding.autoCompMotherTongue.setText(Utils.getTextByTag(Constants.domain_mother_tongue,selectedmemberInfoDataModel.getMother_tongue()));

           if(selectedmemberInfoDataModel.getMother_tongue().equalsIgnoreCase(Constants.dropdown_others)) {
               memberBinding.etMotherTongueOther.setText(selectedmemberInfoDataModel.getMother_tongue_other());
               memberBinding.etMotherTongueOther.setVisibility(View.VISIBLE);
           } else {
               memberBinding.etMotherTongueOther.setText("");
               memberBinding.etMotherTongueOther.setVisibility(View.GONE);
           }

           memberBinding.autoCompEducation.setTag(selectedmemberInfoDataModel.getEducation());
           memberBinding.autoCompEducation.setText(Utils.getTextByTag(Constants.domain_educational_qualification,selectedmemberInfoDataModel.getEducation()));

           if(selectedmemberInfoDataModel.getEducation().equalsIgnoreCase(Constants.dropdown_others)) {
               memberBinding.etEducationOther.setText(selectedmemberInfoDataModel.getEducation_other());
               memberBinding.etEducationOther.setVisibility(View.VISIBLE);
           } else {
               memberBinding.etEducationOther.setText("");
               memberBinding.etEducationOther.setVisibility(View.GONE);
           }

           memberBinding.autoCompOccupation.setTag(selectedmemberInfoDataModel.getOccupation());
           memberBinding.autoCompOccupation.setText(Utils.getTextByTag(Constants.domain_occupation,selectedmemberInfoDataModel.getOccupation()));

           if(selectedmemberInfoDataModel.getOccupation().equalsIgnoreCase(Constants.dropdown_others)) {
               memberBinding.etOccupationOther.setText(selectedmemberInfoDataModel.getOccupation_other());
               memberBinding.etOccupationOther.setVisibility(View.VISIBLE);
           } else {
               memberBinding.etOccupationOther.setText("");
               memberBinding.etOccupationOther.setVisibility(View.GONE);
           }

           memberBinding.autoCompWorkPlace.setTag(selectedmemberInfoDataModel.getPlace_of_work());
           memberBinding.autoCompWorkPlace.setText(Utils.getTextByTag(Constants.domain_place_of_work,selectedmemberInfoDataModel.getPlace_of_work()));

           memberBinding.autoCompWorkType.setTag(selectedmemberInfoDataModel.getType_of_work());
           memberBinding.autoCompWorkType.setText(Utils.getTextByTag(Constants.domain_work_type,selectedmemberInfoDataModel.getType_of_work()));

           if(selectedmemberInfoDataModel.getType_of_work().equalsIgnoreCase(Constants.dropdown_others)) {
               memberBinding.etTypeOfWorkOther.setText(selectedmemberInfoDataModel.getType_of_work_other());
               memberBinding.etTypeOfWorkOther.setVisibility(View.VISIBLE);
           } else {
               memberBinding.etTypeOfWorkOther.setText("");
               memberBinding.etTypeOfWorkOther.setVisibility(View.GONE);
           }

           memberBinding.etIncome.setText(selectedmemberInfoDataModel.getMonthly_income());

           memberBinding.autoCompTransport.setTag(selectedmemberInfoDataModel.getMode_of_transport());
           memberBinding.autoCompTransport.setText(Utils.getTextByTag(Constants.domain_mode_of_transport,selectedmemberInfoDataModel.getMode_of_transport()));
           if(selectedmemberInfoDataModel.getMode_of_transport().equalsIgnoreCase(Constants.dropdown_others)) {
               memberBinding.etModeOfTransportOther.setText(selectedmemberInfoDataModel.getMode_of_transport_other());
               memberBinding.etModeOfTransportOther.setVisibility(View.VISIBLE);
           } else {
               memberBinding.etModeOfTransportOther.setText("");
               memberBinding.etModeOfTransportOther.setVisibility(View.GONE);
           }

           memberBinding.etSchoolCollegeLocation.setText(selectedmemberInfoDataModel.getSchool_college_name_location());

           memberBinding.autoCompVehicleType.setTag(selectedmemberInfoDataModel.getVehicle_owned_driven());
           memberBinding.autoCompVehicleType.setText(Utils.getTextByTag(Constants.domain_vehicle_owned_driven_type,selectedmemberInfoDataModel.getVehicle_owned_driven()));
           if(selectedmemberInfoDataModel.getVehicle_owned_driven().equalsIgnoreCase(Constants.dropdown_others)) {
               memberBinding.etVehicleTypeOther.setText(selectedmemberInfoDataModel.getVehicle_owned_driven_other());
               memberBinding.etVehicleTypeOther.setVisibility(View.VISIBLE);
           } else {
               memberBinding.etVehicleTypeOther.setText("");
               memberBinding.etVehicleTypeOther.setVisibility(View.GONE);
           }

           if(stayingwith.equalsIgnoreCase("family")) {
                memberBinding.radioGroupStay.check(R.id.radio_family_Yes);
            } else if(stayingwith.equalsIgnoreCase("individual")) {
                memberBinding.radioGroupStay.check(R.id.radio_individual_No);
            } else {
                memberBinding.radioGroupStay.clearCheck();
            }
            if(selectedmemberInfoDataModel.getGender().equalsIgnoreCase("male")) {
                memberBinding.genderRadioGroup.check(R.id.radio_gender_Male);
                memberGender=Constants.male;
            } else if(selectedmemberInfoDataModel.getGender().equalsIgnoreCase("female")) {
                memberBinding.genderRadioGroup.check(R.id.radio_gender_Female);
                memberGender=Constants.female;
            } else if(selectedmemberInfoDataModel.getGender().equalsIgnoreCase("transgender")) {
                memberBinding.genderRadioGroup.check(R.id.radio_gender_Other);
                memberGender=Constants.transgender;
            } else {
                memberBinding.genderRadioGroup.clearCheck();
            }
            if(isDeathCertificateAvailable == 0) {
                memberBinding.radioGroupMemberAvailable.check(R.id.radio_memberAvailable_No);
            } else if(isDeathCertificateAvailable == 1) {
                memberBinding.radioGroupMemberAvailable.check(R.id.radio_memberAvailable_Yes);
            } else if(isDeathCertificateAvailable == 2) {
                memberBinding.radioGroupMemberAvailable.clearCheck();
            }

            if (selectedmemberInfoDataModel.getMarital_status().toString().equalsIgnoreCase("Unmarried")) {
                memberBinding.spouseLayout.setVisibility(View.GONE);
                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
            } else {
                memberBinding.spouseLayout.setVisibility(View.VISIBLE);
                memberBinding.memberNumSpouseLayout.setVisibility(View.VISIBLE);//TODO
            }

           if(selectedmemberInfoDataModel.getMarital_status().equalsIgnoreCase("Married") &&
                   selectedmemberInfoDataModel.getGender().equalsIgnoreCase("Male")) {
               memberBinding.memberNumSpouseLayout.setVisibility(View.VISIBLE);
           } else {
               memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
           }


       }catch(Exception ex){
           AppLog.logData(activity,"exception::"+ex.getMessage());
                Utils.shortToast(ex.getMessage(),activity);
       }

    }

    /*
    * When user presses cancel button besides submit, member form gets cleared. When user save/update any member from member form
    * this method gets invoked to clear member form.
    * */

    private void clearAllFields() {
        try {
            memberBinding.etFirstNameMember.setText("");
            memberBinding.autoCompRelationWitHOH.setText("");
            memberBinding.etRelationshipWithHohOther.setText("");
            memberBinding.autoCompMaritialStatus.setText("");
            memberBinding.etMaritalStatusOther.setText("");
            memberBinding.etMemberNumberSpouse.setText("");
            memberBinding.etNameSpouse.setText("");
            memberBinding.etRespondentContact.setText("");
            memberBinding.etDob.setText("");
            memberBinding.etMemberAge.setText("");
            memberBinding.etStayingYear.setText("");
            memberBinding.etAadhaarNumber.setText("");
            memberBinding.etPanNumber.setText("");
            memberBinding.autoCompRationColor.setText("");
            memberBinding.etRationCardNumber.setText("");
            memberBinding.autoCompReligion.setText("");
            memberBinding.etReligionOther.setText("");
            memberBinding.autoCompWhichState.setText("");
            memberBinding.etWhichStateOther.setText("");
            memberBinding.autoCompMotherTongue.setText("");
            memberBinding.etMotherTongueOther.setText("");
            memberBinding.autoCompEducation.setText("");
            memberBinding.etEducationOther.setText("");
            memberBinding.autoCompOccupation.setText("");
            memberBinding.etOccupationOther.setText("");
            memberBinding.autoCompWorkPlace.setText("");
            memberBinding.autoCompWorkType.setText("");
            memberBinding.etTypeOfWorkOther.setText("");
            memberBinding.etIncome.setText("");
            memberBinding.autoCompTransport.setText("");
            memberBinding.etModeOfTransportOther.setText("");
            memberBinding.etSchoolCollegeLocation.setText("");
            memberBinding.autoCompVehicleType.setText("");
            memberBinding.etVehicleTypeOther.setText("");
            memberBinding.radioGroupHandicapped.clearCheck();
            memberBinding.radioGroupStay.clearCheck();
            memberBinding.genderRadioGroup.clearCheck();
            memberBinding.radioGroupMemberAvailable.clearCheck();
            memberGender = "";
            handicapOrNot = "";
            stayingwith = "";
            removeTagValue();
        } catch (Exception ex) {
            AppLog.logData(activity, ex.getMessage());
            Utils.shortToast("clearAllFields:" + ex.getMessage(), activity);
        }
    }

    private void updateMemberListInUI(MemberInfoDataModel memberInfoDataModel) {
        try{
        Context context = memberBinding.btnCancel.getContext();
        LinearLayout listHohMemberLinearLayout = memberBinding.llMemberName;
        listHohMemberLinearLayout.removeAllViews();
//        List<MemberInfoDataModel> mediaInfoDataModels = localSurveyDbViewModel.getMemberInfoData(memberInfoDataModel.getMemberSampleGlobalid());
//        if(existingMemberInfoDataModelAL == null || existingMemberInfoDataModelAL.size() == 0) {
//            mediaInfoDataModels = localSurveyDbViewModel.getMemberInfoDataWithMemberId(memberInfoDataModel.getMember_id());
//        } else {
//            mediaInfoDataModels = existingMemberInfoDataModelAL;
//        }

        if(memberInfoDataModel == null) {
            existingMemberInfoDataModelAL.forEach(m -> {
                if(hohInfoDataModel != null && hohInfoDataModel.getGlobalId() != null && hohInfoDataModel.getRel_globalid() != null
                        && existingMemberInfoDataModelAL != null && existingMemberInfoDataModelAL.size() > 0) {
                    if (hohInfoDataModel.getGlobalId().equals(m.getRel_globalid())) {
                        LinearLayout linearLayout = new LinearLayout(context);
                        ImageView defaultMemberPic = new ImageView(context);
                        TextView memberName = new TextView(context);
                        Drawable drawable = context.getResources().getDrawable(R.drawable.icon_member_svg);

                        defaultMemberPic.setImageDrawable(drawable);
                        memberName.setText(m.getMember_name());//M_1707490271862
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.addView(defaultMemberPic);
                        linearLayout.addView(memberName);
                        linearLayout.setOnClickListener(view -> {
//                existingMemberInfoDataModelAL = m;
                            memberUniqueId = m.getMember_id();
                            memberBinding.txtSubmit.setText("Update");
                            applyExistingMemberToUI();
                            setUpAdapterNListeners();
                        });
                        listHohMemberLinearLayout.addView(linearLayout);
                    }
                }
            });
        }


        if(memberInfoDataModel != null) {
            List<MemberInfoDataModel> mediaInfoDataModels = localSurveyDbViewModel.getMemberInfoDataWithRelGlobalId(memberInfoDataModel.getRel_globalid());
            mediaInfoDataModels.forEach(member -> {
                LinearLayout linearLayout = new LinearLayout(context);
                ImageView defaultMemberPic = new ImageView(context);
                TextView memberName = new TextView(context);
                Drawable drawable = context.getResources().getDrawable(R.drawable.icon_member_svg);

                defaultMemberPic.setImageDrawable(drawable);
                memberName.setText(member.getMember_name());//M_1707490271862
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(defaultMemberPic);
                linearLayout.addView(memberName);
                linearLayout.setOnClickListener(view -> {
//                existingMemberInfoDataModelAL = m;
                    memberUniqueId = member.getMember_id();
                    memberBinding.txtSubmit.setText("Update");
                    applyExistingMemberToUI();
                    setUpAdapterNListeners();
                });
                listHohMemberLinearLayout.addView(linearLayout);
            });
        }
        }catch(Exception ex){
            AppLog.logData(activity,ex.getMessage());
            Utils.shortToast(ex.getMessage(),activity);
        }
    }

    /*
    * This method is used to save data from member form to db after basic & mandatory fields conditions are satisfied using memberValidation method.
    * memberInfoDataModel room entity object is initialized using UI binding object & global variable.
    * */

    public void saveMemberFormToDb() {
    try{
        String objectId = "";
        String globalid = "";
        MemberInfoDataModel memberInfoDataModel;
        String hohMember_relative_path = hohInfoDataModel.getRelative_path();
        String hoh_rel_globalID = hohInfoDataModel.getRel_globalid();
        boolean isUploaded = false;
        String hohUniqueId = hohInfoDataModel.getHoh_id();
        String unit_relative_path = unitInfoDataModel.getRelative_path();
        if(member_relative_path.equals("")) {
            member_relative_path = unit_relative_path + hohInfoDataModel.getHoh_id()  + "/" + memberUniqueId + "/";
        }



//        if (editableMemberInfoDataModel != null) {
            objectId = "";
            globalid = hohInfoDataModel.getGlobalId();
//            isUploaded = false;

//            objectId = editableMemberInfoDataModel.getObejctId();//??
//            globalid = editableMemberInfoDataModel.getGlobalId();//??
//            isUploaded = editableMemberInfoDataModel.isUploaded();//??
//        } else {
//            memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
//            member_relative_path = unit_relative_path + hohUniqueId + "/" + memberUniqueId + "/";
//            hohMemberUniqueId = memberUniqueId;
//            hohMember_relative_path = member_relative_path;
//        }
        //insert member in localDB //pick here
//        if(existingMemberInfoDataModelAL != null && existingMemberInfoDataModelAL.size() > 0) {

//            for (MemberInfoDataModel existingMemberInfoDataModel: existingMemberInfoDataModelAL) {
//                localSurveyDbViewModel.insertMemberInfoPointData(existingMemberInfoDataModel, memberActivity);
//                updateMemberListInUI(existingMemberInfoDataModel);
//                applyExistingMemberToUI();
//            }
//        } else {

        String relGlobalId = "";
        if(hohInfoDataModel != null && hohInfoDataModel.getGlobalId() != null && hohInfoDataModel.getRel_globalid() != null
                && existingMemberInfoDataModelAL != null && existingMemberInfoDataModelAL != null && existingMemberInfoDataModelAL.size() > 0) {
            relGlobalId = hohInfoDataModel.getGlobalId();
        } else {
            relGlobalId = hoh_rel_globalID;
        }
        if(memberUniqueId.equals("")) {
            memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
        }
        setNullTag();
            memberInfoDataModel = new MemberInfoDataModel(memberUniqueId, hohUniqueId,
                    relGlobalId,
                    member_relative_path,
                    memberBinding.etFirstNameMember.getText().toString(),
                    memberBinding.autoCompRelationWitHOH.getTag().toString(),
                    memberBinding.etRelationshipWithHohOther.getText().toString(),
                    memberBinding.autoCompMaritialStatus.getTag().toString(),
                    memberBinding.etMaritalStatusOther.getText().toString(),
                    Utils.integerFormatter(memberBinding.etMemberNumberSpouse.getText().toString()),
                    memberBinding.etNameSpouse.getText().toString(),
                    memberBinding.etRespondentContact.getText().toString(),
                    memberBinding.etDob.getText().toString(),
                    Utils.integerFormatter(memberBinding.etMemberAge.getText().toString()),
                    memberGender,
                    Utils.integerFormatter(memberBinding.etStayingYear.getText().toString()),
//                    memberBinding.etAadhaarNumber.getText().toString(),
                    MaskedEditText.getOriginalText(memberBinding.etAadhaarNumber),
                    memberBinding.etPanNumber.getText().toString(),
                    memberBinding.autoCompRationColor.getTag().toString(),
                    memberBinding.etRationCardNumber.getText().toString(),
                    memberBinding.autoCompReligion.getTag().toString(),
                    memberBinding.etReligionOther.getText().toString(),
                    memberBinding.autoCompWhichState.getTag().toString(),
                    memberBinding.etWhichStateOther.getText().toString(),
                    memberBinding.autoCompMotherTongue.getTag().toString(),
                    memberBinding.etMotherTongueOther.getText().toString(),
                    memberBinding.autoCompEducation.getTag().toString(),
                    memberBinding.etEducationOther.getText().toString(),
                    memberBinding.autoCompOccupation.getTag().toString(),
                    memberBinding.etOccupationOther.getText().toString(),
                    memberBinding.autoCompWorkPlace.getTag().toString(),
                    memberBinding.autoCompWorkType.getTag().toString(),
                    memberBinding.etTypeOfWorkOther.getText().toString(),
                    memberBinding.etIncome.getText().toString(),
                    memberBinding.autoCompTransport.getTag().toString(),
                    memberBinding.etModeOfTransportOther.getText().toString(),
                    memberBinding.etSchoolCollegeLocation.getText().toString(), handicapOrNot, stayingwith,
                    memberBinding.autoCompVehicleType.getTag().toString(),
                    memberBinding.etVehicleTypeOther.getText().toString(),
                    objectId, globalid, isUploaded, new Date(), new Date(), getDeathCertificate()
            );
            localSurveyDbViewModel.insertMemberInfoPointData(memberInfoDataModel, memberActivity);
            clearAllFields();
            memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
            member_relative_path = unitInfoDataModel.getRelative_path() + hohInfoDataModel.getHoh_id() + "/" + memberUniqueId + "/";
            member_rel_global_id = hohInfoDataModel.getGlobalId();
            //updateMemberListInUI(memberInfoDataModel);
            initMemberList(hohInfoDataModel.getHoh_id());
//        }
}catch(Exception ex){
        AppLog.logData(activity,ex.getMessage());
    Utils.shortToast(ex.getMessage(),activity);
}
    }
    //to remove tag values post saved into local DB by Komal Saini
    public void removeTagValue(){

        memberBinding.autoCompRelationWitHOH.setText("");
        memberBinding.autoCompRelationWitHOH.setTag("");
        memberBinding.etRelationshipWithHohOther.setText("");
        memberBinding.etRelationshipWithHohOther.setVisibility(View.GONE);

        memberBinding.autoCompMaritialStatus.setTag("");
        memberBinding.autoCompMaritialStatus.setText("");
        memberBinding.etMaritalStatusOther.setText("");
        memberBinding.etMaritalStatusOther.setVisibility(View.GONE);

        memberBinding.autoCompReligion.setTag("");
        memberBinding.autoCompReligion.setText("");
        memberBinding.etReligionOther.setText("");
        memberBinding.etReligionOther.setVisibility(View.GONE);

        memberBinding.etAadhaarNumber.setTag("");
        memberBinding.etAadhaarNumber.setText("");

        memberBinding.autoCompRationColor.setTag("");
        memberBinding.autoCompRationColor.setText("");

        memberBinding.autoCompWhichState.setTag("");
        memberBinding.autoCompWhichState.setText("");
        memberBinding.etWhichStateOther.setText("");
        memberBinding.etWhichStateOther.setVisibility(View.GONE);

        memberBinding.autoCompMotherTongue.setTag("");
        memberBinding.autoCompMotherTongue.setText("");
        memberBinding.etMotherTongueOther.setText("");
        memberBinding.etMotherTongueOther.setVisibility(View.GONE);

        memberBinding.autoCompEducation.setTag("");
        memberBinding.autoCompEducation.setText("");
        memberBinding.etEducationOther.setText("");
        memberBinding.etEducationOther.setVisibility(View.GONE);

        memberBinding.autoCompOccupation.setTag("");
        memberBinding.autoCompOccupation.setText("");
        memberBinding.etOccupationOther.setText("");
        memberBinding.etOccupationOther.setVisibility(View.GONE);

        memberBinding.autoCompWorkPlace.setTag("");
        memberBinding.autoCompWorkPlace.setText("");

        memberBinding.autoCompWorkType.setTag("");
        memberBinding.autoCompWorkType.setText("");
        memberBinding.etTypeOfWorkOther.setText("");
        memberBinding.etTypeOfWorkOther.setVisibility(View.GONE);

        memberBinding.autoCompTransport.setTag("");
        memberBinding.autoCompTransport.setText("");
        memberBinding.etModeOfTransportOther.setText("");
        memberBinding.etModeOfTransportOther.setVisibility(View.GONE);

        memberBinding.autoCompVehicleType.setTag("");
        memberBinding.autoCompVehicleType.setText("");
        memberBinding.etVehicleTypeOther.setText("");
        memberBinding.etVehicleTypeOther.setVisibility(View.GONE);
    }
    /*
    * If existing memberInfoDataModel don't have objectId column value in it this means that member is not uploaded to server.
    *
    *
    * */

    public void updateMemberToDb() {
        try{
            String objectId = "";
            String globalid = "";
            MemberInfoDataModel memberInfoDataModel;
            String hoh_rel_globalID = hohInfoDataModel.getRel_globalid();
            boolean isUploaded = true;
            String hohUniqueId = hohInfoDataModel.getHoh_id();
            String unit_relative_path = unitInfoDataModel.getRelative_path();
            member_relative_path = unit_relative_path + hohInfoDataModel.getHoh_id()  + "/" + memberUniqueId + "/";
            if (selectedmemberInfoDataModel != null && selectedmemberInfoDataModel.getObejctId() != null && !selectedmemberInfoDataModel.getObejctId().equals("")) {
                isUploaded = true;
            } else {
                isUploaded = false;
            }
            setNullTag();
            objectId = hohInfoDataModel.getObejctId();
            globalid = hohInfoDataModel.getGlobalId();

            String relGlobalId = "";

            memberInfoDataModel = new MemberInfoDataModel(
                    selectedmemberInfoDataModel.getMember_id(),
                    selectedmemberInfoDataModel.getMemberSampleGlobalid(),
                    selectedmemberInfoDataModel.getRel_globalid(),
                    selectedmemberInfoDataModel.getRelative_path(),
                    memberBinding.etFirstNameMember.getText().toString(),
                    memberBinding.autoCompRelationWitHOH.getTag().toString(),
                    memberBinding.etRelationshipWithHohOther.getText().toString(),
                    memberBinding.autoCompMaritialStatus.getTag().toString(),
                    memberBinding.etMaritalStatusOther.getText().toString(),
                    Utils.integerFormatter(memberBinding.etMemberNumberSpouse.getText().toString()),
                    memberBinding.etNameSpouse.getText().toString(),
                    memberBinding.etRespondentContact.getText().toString(),
                    memberBinding.etDob.getText().toString(),
                    Utils.integerFormatter(memberBinding.etMemberAge.getText().toString()),
                    memberGender,
                    Utils.integerFormatter(memberBinding.etStayingYear.getText().toString()),
//                    memberBinding.etAadhaarNumber.getText().toString(),
                    MaskedEditText.getOriginalText(memberBinding.etAadhaarNumber),
                    memberBinding.etPanNumber.getText().toString(),
                    memberBinding.autoCompRationColor.getTag().toString(),
                    memberBinding.etRationCardNumber.getText().toString(),
                    memberBinding.autoCompReligion.getTag().toString(),
                    memberBinding.etReligionOther.getText().toString(),
                    memberBinding.autoCompWhichState.getTag().toString(),
                    memberBinding.etWhichStateOther.getText().toString(),
                    memberBinding.autoCompMotherTongue.getTag().toString(),
                    memberBinding.etMotherTongueOther.getText().toString(),
                    memberBinding.autoCompEducation.getTag().toString(),
                    memberBinding.etEducationOther.getText().toString(),
                    memberBinding.autoCompOccupation.getTag().toString(),
                    memberBinding.etOccupationOther.getText().toString(),
                    memberBinding.autoCompWorkPlace.getTag().toString(),
                    memberBinding.autoCompWorkType.getTag().toString(),
                    memberBinding.etTypeOfWorkOther.getText().toString(),
                    memberBinding.etIncome.getText().toString(),
                    memberBinding.autoCompTransport.getTag().toString(),
                    memberBinding.etModeOfTransportOther.getText().toString(),
                    memberBinding.etSchoolCollegeLocation.getText().toString(),
                    handicapOrNot, stayingwith,
                    memberBinding.autoCompVehicleType.getTag().toString(),
                    memberBinding.etVehicleTypeOther.getText().toString(),
                    selectedmemberInfoDataModel.getObejctId(),
                    selectedmemberInfoDataModel.getGlobalId(),
                    isUploaded,
                    new Date(),
                    new Date(),
                    getDeathCertificate()
            );
            localSurveyDbViewModel.updateMember(memberInfoDataModel, memberActivity);
            clearAllFields();
            memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
            member_relative_path = unitInfoDataModel.getRelative_path() + hohInfoDataModel.getHoh_id() + "/" + memberUniqueId + "/";
            member_rel_global_id = hohInfoDataModel.getGlobalId();
           // updateMemberListInUI(memberInfoDataModel);
            initMemberList(hohInfoDataModel.getHoh_id());
        }catch(Exception ex){
            AppLog.e(""+ex.getMessage());
            Utils.shortToast("Update:"+ex.getMessage(),activity);
        }
    }

    public MemberInfoDataModel updateMemberFormToDb() {

        String objectId = "";
        String globalid = "";
        MemberInfoDataModel memberInfoDataModel;
        String hohMember_relative_path = hohInfoDataModel.getRelative_path();
        String hoh_rel_globalID = hohInfoDataModel.getRel_globalid();
        boolean isUploaded = false;
        String hohUniqueId = hohInfoDataModel.getHoh_id();
        objectId = hohInfoDataModel.getObejctId();
        globalid = hohInfoDataModel.getGlobalId();

        String relGlobalId = "";
        if(existingMemberInfoDataModelAL != null && existingMemberInfoDataModelAL.size() > 0) {
            relGlobalId = hohInfoDataModel.getGlobalId();
        } else {
            relGlobalId = hoh_rel_globalID;
        }

        memberInfoDataModel = new MemberInfoDataModel(memberUniqueId, hohUniqueId,
                relGlobalId,
                member_relative_path,
                memberBinding.etFirstNameMember.getText().toString(),
                memberBinding.autoCompRelationWitHOH.getTag().toString(),
                memberBinding.etRelationshipWithHohOther.getText().toString(),
                memberBinding.autoCompMaritialStatus.getTag().toString(),
                memberBinding.etMaritalStatusOther.getText().toString(),
                Utils.integerFormatter(memberBinding.etMemberNumberSpouse.getText().toString()),
                memberBinding.etNameSpouse.getText().toString(),
                memberBinding.etRespondentContact.getText().toString(),
                memberBinding.etDob.getText().toString(),
                Utils.integerFormatter(memberBinding.etMemberAge.getText().toString()),
                memberGender,
                Utils.integerFormatter(memberBinding.etStayingYear.getText().toString()),
//                memberBinding.etAadhaarNumber.getText().toString(),
                MaskedEditText.getOriginalText(memberBinding.etAadhaarNumber),
                memberBinding.etPanNumber.getText().toString(),
                memberBinding.autoCompRationColor.getTag().toString(),
                memberBinding.etRationCardNumber.getText().toString(),
                memberBinding.autoCompReligion.getTag().toString(),
                memberBinding.etReligionOther.getText().toString(),
                memberBinding.autoCompWhichState.getTag().toString(),
                memberBinding.etWhichStateOther.getText().toString(),
                memberBinding.autoCompMotherTongue.getTag().toString(),
                memberBinding.etMotherTongueOther.getText().toString(),
                memberBinding.autoCompEducation.getTag().toString(),
                memberBinding.etEducationOther.getText().toString(),
                memberBinding.autoCompOccupation.getTag().toString(),
                memberBinding.etOccupationOther.getText().toString(),
                memberBinding.autoCompWorkPlace.getTag().toString(),
                memberBinding.autoCompWorkType.getTag().toString(),
                memberBinding.etTypeOfWorkOther.getText().toString(),
                memberBinding.etIncome.getText().toString(),
                memberBinding.autoCompTransport.getTag().toString(),
                memberBinding.etModeOfTransportOther.getText().toString(),
                /*memberBinding.etSchoolCollegeLocation.getText().toString()*/"", handicapOrNot, stayingwith,
                memberBinding.autoCompVehicleType.getTag().toString(),
                memberBinding.etVehicleTypeOther.getText().toString(),
                objectId, globalid, isUploaded, new Date(), new Date(), getDeathCertificate()
        );
        localSurveyDbViewModel.insertMemberInfoPointData(memberInfoDataModel, memberActivity);
        clearAllFields();
        //updateMemberListInUI(memberInfoDataModel);
        initMemberList(hohInfoDataModel.getHoh_id());
        return memberInfoDataModel;
    }

    /*
    * When member form loads,  relationship with hoh, marital status, color of ration card, religion, originally from which state,
    * mother tongue, education level, occupation, type of work, mode of transport, vehicle type owned, place of work
    * form fields needs to get populated by list of string & user select click listeners need to get registered of Marital Status, date of birth, staying year, religion, from which state,
    *  mother tongue, education, occupation, work type
    * mode of transport, vehicle type, place of work, ration color, adhaar card attach, pan card attach, ration card attach, death certificate attach, salary proof attach,
    * handicap or not attach, radio buttons of 3 gender, handicap or not, staying with family, arrow key to show/hide member list, cancel button before finish.
    * Are located here.
    * */


    public void setUpAdapterNListeners() {
        try{
        okFileExtensions = new ArrayList<>();
        userAttachmentList = new ArrayList<>();
        memberPhotographAttachmentList = new ArrayList<>();
        memberAdhaarCardAttachmentList = new ArrayList<>();
        memberAdhaarCardAttachmentListAdapter = new AttachmentListAdapter(memberActivity, memberAdhaarCardAttachmentList,"member", this);
        memberPanCardAttachmentListAdapter = new AttachmentListAdapter(memberActivity, memberPanCardAttachmentList,"member", this);
        memberPanCardAttachmentList = new ArrayList<>();
        getPreviousMediaFileName = new ArrayList<>();
        okFileExtensions.add("jpg");
        okFileExtensions.add("png");
        okFileExtensions.add("jpeg");
        okFileExtensions.add("pdf");

        ArrayList<AutoCompleteModal> listMaritalStatus = Utils.getDomianList(Constants.domain_marital_status);
        memberBinding.autoCompMaritialStatus.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listMaritalStatus));
        setFocusChange_OnTouch(memberBinding.autoCompMaritialStatus, listMaritalStatus);

        memberBinding.autoCompMaritialStatus.setOnItemClickListener((parent, view, SELECTED_ITEM, rowId) -> {
            int MARRIED_ITEM = 0;
            int UNMARRIED_ITEM = 1;
            int DIVORCE_ITEM = 2;
            int OTHER_ITEM = 3;

            String s=memberBinding.autoCompMaritialStatus.getText().toString();
            memberBinding.autoCompMaritialStatus.setTag(s);
//            memberBinding.autoCompMaritialStatus.setText(Utils.getTextByTag(Constants.domain_marital_status,s));

            memberBinding.etMaritalStatusOther.setVisibility(View.GONE);
            memberBinding.etMaritalStatusOther.setText("");

            if(s.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etMaritalStatusOther.setText("");
                memberBinding.etMaritalStatusOther.setVisibility(View.VISIBLE);
                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
                memberBinding.etMemberNumberSpouse.setText("0");
                memberBinding.etNameSpouse.setText("");
                memberBinding.spouseLayout.setVisibility(View.GONE);
            } else if (s.equalsIgnoreCase("Married") &&
                    memberBinding.genderRadioGroup.getCheckedRadioButtonId() == memberBinding.radioGenderMale.getId()) {
                memberBinding.spouseLayout.setVisibility(View.VISIBLE);
                memberBinding.memberNumSpouseLayout.setVisibility(View.VISIBLE);
            }else if (s.equalsIgnoreCase("Married")){
                memberBinding.spouseLayout.setVisibility(View.VISIBLE);
            }else if (s.equalsIgnoreCase("Unmarried")){
                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
                memberBinding.etMemberNumberSpouse.setText("0");
                memberBinding.etNameSpouse.setText("");
                memberBinding.spouseLayout.setVisibility(View.GONE);
                memberBinding.etMaritalStatusOther.setText("");
                memberBinding.etMaritalStatusOther.setVisibility(View.GONE);
            }else{
                memberBinding.spouseLayout.setVisibility(View.VISIBLE);
            }

            if (s.equals(Constants.dropdown_others)) {
                memberBinding.etMaritalStatusOther.setVisibility(View.VISIBLE);
            } else {
                memberBinding.etMaritalStatusOther.setVisibility(View.GONE);
                memberBinding.etMaritalStatusOther.setText("");
            }

//            if (SELECTED_ITEM == UNMARRIED_ITEM) {
//                memberBinding.spouseLayout.setVisibility(View.GONE);
//                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
//            } else if(SELECTED_ITEM != MARRIED_ITEM) {
//                memberBinding.spouseLayout.setVisibility(View.VISIBLE);
//                memberBinding.memberNumSpouseLayout.setVisibility(View.VISIBLE);//??
//            }
//            if(SELECTED_ITEM == MARRIED_ITEM) {
//                if(memberBinding.genderRadioGroup.getCheckedRadioButtonId() == memberBinding.radioGenderMale.getId()) {
//                    memberBinding.memberNumSpouseLayout.setVisibility(View.VISIBLE);
//                    memberBinding.spouseLayout.setVisibility(View.VISIBLE);
//                } else {
//                    memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
//                    memberBinding.spouseLayout.setVisibility(View.VISIBLE);
//                }
//            } else if(SELECTED_ITEM != UNMARRIED_ITEM) {
//                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
//            }
        });

        ArrayList<AutoCompleteModal> listReligion = Utils.getDomianList(Constants.domain_religion);
        memberBinding.autoCompReligion.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listReligion));
        setFocusChange_OnTouch(memberBinding.autoCompReligion, listReligion);

        ArrayList<AutoCompleteModal> listWhichState = Utils.getDomianList(Constants.domain_state);
        memberBinding.autoCompWhichState.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listWhichState));
        setFocusChange_OnTouch(memberBinding.autoCompWhichState, listWhichState);

        ArrayList<AutoCompleteModal> listMotherTongue = Utils.getDomianList(Constants.domain_mother_tongue);
        memberBinding.autoCompMotherTongue.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listMotherTongue));
        setFocusChange_OnTouch(memberBinding.autoCompMotherTongue, listMotherTongue);

        ArrayList<AutoCompleteModal> listEducation = Utils.getDomianList(Constants.domain_educational_qualification);
        memberBinding.autoCompEducation.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listEducation));
        setFocusChange_OnTouch(memberBinding.autoCompEducation, listEducation);

        ArrayList<AutoCompleteModal> listOccupation = Utils.getDomianList(Constants.domain_occupation);
        memberBinding.autoCompOccupation.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listOccupation));
        setFocusChange_OnTouch(memberBinding.autoCompOccupation, listOccupation);

        ArrayList<AutoCompleteModal> listWorkType = Utils.getDomianList(Constants.domain_work_type);
        memberBinding.autoCompWorkType.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listWorkType));
        setFocusChange_OnTouch(memberBinding.autoCompWorkType, listWorkType);

        ArrayList<AutoCompleteModal> listTransport = Utils.getDomianList(Constants.domain_mode_of_transport);
        memberBinding.autoCompTransport.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listTransport));
        setFocusChange_OnTouch(memberBinding.autoCompTransport, listTransport);

        ArrayList<AutoCompleteModal> listVehicleType = Utils.getDomianList(Constants.domain_vehicle_owned_driven_type);
        memberBinding.autoCompVehicleType.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listVehicleType));
        setFocusChange_OnTouch(memberBinding.autoCompVehicleType, listVehicleType);

        memberBinding.autoCompWorkPlace.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, Utils.getDomianList(Constants.domain_place_of_work)));
        setFocusChange_OnTouch(memberBinding.autoCompWorkPlace);

        //memberBinding.autoCompSelectHOH.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, memberActivity.getResources().getStringArray(R.array.sample_hoh)));
        //setFocusChange_OnTouch(memberBinding.autoCompSelectHOH);
//            String[] a = {
//                "Spouse",
//                "Child",
//                "Parent",
//                "Sibling",
//                "Relative",
//                "Friend",
//                "Tenant",
//                "Employee",
//                "Rented Tenant",
//                "Others",
//            };

        ArrayList<AutoCompleteModal> listRelationshipWithHOH = Utils.getDomianList(Constants.domain_relationship_with_hoh);
        memberBinding.autoCompRelationWitHOH.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, listRelationshipWithHOH));
        setFocusChange_OnTouch(memberBinding.autoCompRelationWitHOH, listRelationshipWithHOH);

        memberBinding.autoCompRationColor.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, Utils.getDomianList(Constants.domain_ration_card_color)));
        setFocusChange_OnTouch(memberBinding.autoCompRationColor);

        TimeZone timeZone = TimeZone.getTimeZone("IST");
        myCalendar = Calendar.getInstance(timeZone);

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);

            if (member) {
                member = false;
                memberBinding.etStayingYear.setText("" + year);
            }
            if (memberDob) {
                memberDob = false;
                memberBinding.etDob.setText("" + day + "/" + (month + 1) + "/" + year);
                memberBinding.etMemberAge.setText("" + getAge(year, (month + 1), day));
                memberAge = getAge(year, (month + 1), day);

            }

            if (edd) {
                if (isDocSelected) {
                    docYear = year;
                } else {
                    docYear = 0;
                }
                edd = false;
                globalAge=getAge(year, (month + 1), day);
                ageFlag=getAge(year, (month + 1), day);
            } else {
                edd = false;
//                updateDateLabel();
            }
        };

        memberBinding.etStayingYear.setOnClickListener(view -> {
            member = true;
            DatePickerDialog dialog = new DatePickerDialog(memberActivity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.show();
        });

        memberBinding.etDob.setOnClickListener(view -> {
            memberDob = true;
            DatePickerDialog dialog = new DatePickerDialog(memberActivity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.show();
        });

        memberBinding.btnBrowseAadhaar.setOnClickListener(view -> {
            if(member_relative_path.equals("")) {
                member_relative_path = unitInfoDataModel.getRelative_path() + hohInfoDataModel.getHoh_id()  + "/" + memberUniqueId + "/";
            }
            if(memberUniqueId.equals("")) {
                memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
            }
            cc="member";
            FormPageViewModel.AttName = "Aadhar Card";
            showDocumentPopup(6, 1, 1001);
        });

        memberBinding.btnBrowsePanCard.setOnClickListener(view -> {
            if(member_relative_path.equals("")) {
                member_relative_path = unitInfoDataModel.getRelative_path() + hohInfoDataModel.getHoh_id()  + "/" + memberUniqueId + "/";
            }
            if(memberUniqueId.equals("")) {
                memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
            }
            cc="member";
            FormPageViewModel.AttName = "Pan Card";
            showDocumentPopup(6, 2, 1001);
        });

        memberBinding.btnBrowseRation.setOnClickListener(view -> {
            if(member_relative_path.equals("")) {
                member_relative_path = unitInfoDataModel.getRelative_path() + hohInfoDataModel.getHoh_id()  + "/" + memberUniqueId + "/";
            }
            if(memberUniqueId.equals("")) {
                memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
            }
            cc="member";
            FormPageViewModel.AttName = "Ration Card";
            showDocumentPopup(6, 3, 1001);
        });

        memberBinding.btnBrowseIncome.setOnClickListener(view -> {
            if(member_relative_path.equals("")) {
                member_relative_path = unitInfoDataModel.getRelative_path() + hohInfoDataModel.getHoh_id()  + "/" + memberUniqueId + "/";
            }
            if(memberUniqueId.equals("")) {
                memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
            }
            cc="member";
            FormPageViewModel.AttName = "Salary Proof";
            showDocumentPopup(6, 5, 1001);
        });

        memberBinding.btnBrowseHandicapped.setOnClickListener(view -> {
            if(member_relative_path.equals("")) {
                member_relative_path = unitInfoDataModel.getRelative_path() + hohInfoDataModel.getHoh_id()  + "/" + memberUniqueId + "/";
            }
            if(memberUniqueId.equals("")) {
                memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
            }
            cc="member";
            FormPageViewModel.AttName = "Disease Proof";
            showDocumentPopup(6, 6, 1001);
        });
        memberBinding.btnBrowseDeathCertificate.setOnClickListener(view -> {
            if(member_relative_path.equals("")) {
                member_relative_path = unitInfoDataModel.getRelative_path() + hohInfoDataModel.getHoh_id()  + "/" + memberUniqueId + "/";
            }
            if(memberUniqueId.equals("")) {
                memberUniqueId = "M_" + Utils.getEpochDateStamp();//correct
            }
            cc="member";
            FormPageViewModel.AttName = "Death Certificate";
            showDocumentPopup(6, 4, 1001);
        });

        memberBinding.radioGenderMale.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                memberGender = memberBinding.radioGenderMale.getText().toString();
                if(memberBinding.autoCompMaritialStatus.getText().toString().equals("Married")) {
                    memberBinding.memberNumSpouseLayout.setVisibility(View.VISIBLE);
                    memberBinding.spouseLayout.setVisibility(View.VISIBLE);
                } else {
                    memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
                }
            }
        });

        memberBinding.radioGenderFemale.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                memberGender = memberBinding.radioGenderFemale.getText().toString();
                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
            }
        });

        memberBinding.radioGenderOther.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                memberGender = memberBinding.radioGenderOther.getText().toString();
//                memberGender = "Other";
                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
            }
        });

//        memberBinding.etStayingYear.setOnClickListener(view -> {
//            member = true;
//            DatePickerDialog dialog = new DatePickerDialog(memberActivity, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//            dialog.getDatePicker().setMaxDate(new Date().getTime());
//            dialog.show();
//        });
        memberBinding.radioHandicappedYes.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                handicapOrNot = memberBinding.radioHandicappedYes.getText().toString();
            }
        });

        memberBinding.radioHandicappedNo.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                handicapOrNot = memberBinding.radioHandicappedNo.getText().toString();
            }
        });

        memberBinding.radioFamilyYes.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                stayingwith = memberBinding.radioFamilyYes.getText().toString();
            }
        });

        memberBinding.radioIndividualNo.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                stayingwith = memberBinding.radioIndividualNo.getText().toString();
            }
        });

        memberBinding.radioMemberAvailableYes.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                deathCertificate = 1;
            }
        });
        memberBinding.radioMemberAvailableNo.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                deathCertificate = 0;
            }
        });

        memberBinding.upArrowIv.setOnClickListener(view -> {
            memberBinding.upArrowIv.setVisibility(View.GONE);
            memberBinding.downArrowIv.setVisibility(View.VISIBLE);
            memberBinding.llMemberName.setVisibility(View.GONE);
        });
        memberBinding.downArrowIv.setOnClickListener(view -> {
            memberBinding.downArrowIv.setVisibility(View.GONE);
            memberBinding.upArrowIv.setVisibility(View.VISIBLE);
            memberBinding.llMemberName.setVisibility(View.VISIBLE);
        });
        memberBinding.autoCompMaritialStatus.setSelection(0);

         activityCancelBtn.setOnClickListener(view -> {
             closeFormPopup();
         });

        }catch(Exception ex){
            AppLog.logData(activity,ex.getMessage());
            Utils.shortToast(ex.getMessage(),activity);
        }
    }

    private int getDeathCertificate() {
        int i = 2;
        if (memberBinding.radioMemberAvailableYes.isChecked())
            i = 1;
        else if (memberBinding.radioMemberAvailableNo.isChecked())
            i = 0;
        return i;
    }

    private void updateDateLabel() {
        try{
        String myFormat = Constants.requiredDateFormat;
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        memberBinding.etStayingYear.setText(dateFormat.format(myCalendar.getTime()));
        String myYearFormat = "yyyy";
        SimpleDateFormat yearFormat = new SimpleDateFormat(myYearFormat, Locale.US);
        year = Integer.parseInt(yearFormat.format(myCalendar.getTime()));
        memberBinding.etStayingYear.setError(null, null);
        /*
        Rohit
         */
        memberBinding.etStayingYear.setError(null, null);

//        setupYearOfStructure();
        }catch(Exception ex){
            AppLog.logData(activity,ex.getMessage());
            Utils.shortToast(ex.getMessage(),activity);
        }
    }

    public int getAge(int year, int month, int dayOfMonth) {
        return Period.between(
                LocalDate.of(year, month, dayOfMonth),
                LocalDate.now()
        ).getYears();
    }

    private void setFocusChange_OnTouchUnitStatusRemarks(AutoCompleteTextView autoCompleteTextView, EditText edtRemarksOther) {

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
                AutoCompleteModal selected = (AutoCompleteModal) adapterView.getAdapter().getItem(i);
                if (selected.code.equals(Constants.dropdown_others)) {
                    edtRemarksOther.setVisibility(View.VISIBLE);
                } else {
                    edtRemarksOther.setVisibility(View.GONE);
                    edtRemarksOther.setText("");
                }
                autoCompleteTextView.setTag(selected.code);
            }catch(Exception ex){
                AppLog.logData(activity,ex.getMessage());
                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
            }
        });
    }

    private void setFocusChange_OnTouchOTPRemarks(AutoCompleteTextView autoCompleteTextView, EditText etOtpReasonOther) {

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
                AutoCompleteModal selected = (AutoCompleteModal) adapterView.getAdapter().getItem(i);
                if (selected.code.equals(Constants.dropdown_others)) {
                    etOtpReasonOther.setVisibility(View.VISIBLE);
                } else {
                    etOtpReasonOther.setVisibility(View.GONE);
                    etOtpReasonOther.setText("");
                }
                autoCompleteTextView.setTag(selected.code);
            }catch(Exception ex){
                AppLog.logData(activity,ex.getMessage());
                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
            }
        });
    }

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

                        if(autoCompleteTextView.getId() == memberBinding.autoCompRelationWitHOH.getId()) {
                            Toast.makeText(activity, "Select correct value from Relationship with HOH/Owner/Employer List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Relationship with HOH/Owner/Employer List");
                            memberBinding.etRelationshipWithHohOther.setText("");
                            memberBinding.etRelationshipWithHohOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == memberBinding.autoCompMaritialStatus.getId()) {
                            Toast.makeText(activity, "Select correct value from Marital Status List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Marital Status List");
                            memberBinding.etMaritalStatusOther.setText("");
                            memberBinding.etMaritalStatusOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == memberBinding.autoCompReligion.getId()) {
                            Toast.makeText(activity, "Select correct value from Religion List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Religion List");
                            memberBinding.etReligionOther.setText("");
                            memberBinding.etReligionOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == memberBinding.autoCompWhichState.getId()) {
                            Toast.makeText(activity, "Select correct value from State List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from State List");
                            memberBinding.etWhichStateOther.setText("");
                            memberBinding.etWhichStateOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == memberBinding.autoCompMotherTongue.getId()) {
                            Toast.makeText(activity, "Select correct value from Mother Tongue List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Mother Tongue List");
                            memberBinding.etMotherTongueOther.setText("");
                            memberBinding.etMotherTongueOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == memberBinding.autoCompEducation.getId()) {
                            Toast.makeText(activity, "Select correct value from Education List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Education List");
                            memberBinding.etEducationOther.setText("");
                            memberBinding.etEducationOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == memberBinding.autoCompOccupation.getId()) {
                            Toast.makeText(activity, "Select correct value from Occupation List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Occupation List");
                            memberBinding.etOccupationOther.setText("");
                            memberBinding.etOccupationOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == memberBinding.autoCompWorkType.getId()) {
                            Toast.makeText(activity, "Select correct value from Type Of Work List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Type Of Work List");
                            memberBinding.etTypeOfWorkOther.setText("");
                            memberBinding.etTypeOfWorkOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == memberBinding.autoCompTransport.getId()) {
                            Toast.makeText(activity, "Select correct value from Mode of Transport List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Mode of Transport List");
                            memberBinding.etModeOfTransportOther.setText("");
                            memberBinding.etModeOfTransportOther.setVisibility(View.GONE);
                        } else if(autoCompleteTextView.getId() == memberBinding.autoCompVehicleType.getId()) {
                            Toast.makeText(activity, "Select correct value from Type Of Vehicle List", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Select correct value from Type Of Vehicle List");
                            memberBinding.etVehicleTypeOther.setText("");
                            memberBinding.etVehicleTypeOther.setVisibility(View.GONE);
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

                if(autoCompleteTextView.getId() == memberBinding.autoCompRelationWitHOH.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etRelationshipWithHohOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etRelationshipWithHohOther.setVisibility(View.GONE);
                        memberBinding.etRelationshipWithHohOther.setText("");
                    }
                }

                if(autoCompleteTextView.getId() == memberBinding.autoCompMaritialStatus.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etMaritalStatusOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etMaritalStatusOther.setVisibility(View.GONE);
                        memberBinding.etMaritalStatusOther.setText("");
                    }
                }

                if(autoCompleteTextView.getId() == memberBinding.autoCompReligion.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etReligionOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etReligionOther.setVisibility(View.GONE);
                        memberBinding.etReligionOther.setText("");
                    }
                }

                if(autoCompleteTextView.getId() == memberBinding.autoCompWhichState.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etWhichStateOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etWhichStateOther.setVisibility(View.GONE);
                        memberBinding.etWhichStateOther.setText("");
                    }
                }

                if(autoCompleteTextView.getId() == memberBinding.autoCompMotherTongue.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etMotherTongueOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etMotherTongueOther.setVisibility(View.GONE);
                        memberBinding.etMotherTongueOther.setText("");
                    }
                }

                if(autoCompleteTextView.getId() == memberBinding.autoCompEducation.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etEducationOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etEducationOther.setVisibility(View.GONE);
                        memberBinding.etEducationOther.setText("");
                    }
                }

                if(autoCompleteTextView.getId() == memberBinding.autoCompOccupation.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etOccupationOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etOccupationOther.setVisibility(View.GONE);
                        memberBinding.etOccupationOther.setText("");
                    }
                }

                if(autoCompleteTextView.getId() == memberBinding.autoCompWorkType.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etTypeOfWorkOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etTypeOfWorkOther.setVisibility(View.GONE);
                        memberBinding.etTypeOfWorkOther.setText("");
                    }
                }

                if(autoCompleteTextView.getId() == memberBinding.autoCompTransport.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etModeOfTransportOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etModeOfTransportOther.setVisibility(View.GONE);
                        memberBinding.etModeOfTransportOther.setText("");
                    }
                }

                if(autoCompleteTextView.getId() == memberBinding.autoCompVehicleType.getId()) {
                    if (selected.code.equals(Constants.dropdown_others)) {
                        memberBinding.etVehicleTypeOther.setVisibility(View.VISIBLE);
                    } else {
                        memberBinding.etVehicleTypeOther.setVisibility(View.GONE);
                        memberBinding.etVehicleTypeOther.setText("");
                    }
                }

                autoCompleteTextView.setTag(selected.code);
            }catch(Exception ex){
                AppLog.logData(activity,ex.getMessage());
                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
            }
        });
    }

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
            }catch(Exception ex){
                AppLog.logData(activity,ex.getMessage());
                autoCompleteTextView.setTag(adapterView.getAdapter().getItem(i));
            }
        });
    }

    public boolean memberValidation() {
        // Boolean memmobileCheck = false;
        // Boolean hasMemberName = false;
        List<MediaInfoDataModel> list1 = localSurveyDbViewModel.getMediaInfoDataByCatUnit("hoh_member_Disease_Proof", memberUniqueId);
        List<MediaInfoDataModel> list1Adhar = localSurveyDbViewModel.getMediaInfoDataByCatUnit("hoh_member_Aadhar_Card", memberUniqueId);
        List<MediaInfoDataModel> list1PAN = localSurveyDbViewModel.getMediaInfoDataByCatUnit("hoh_member_Pan_Card", memberUniqueId);
        //if (memberAge > 18) {
          //  memmobileCheck = false;
        //}
        if (memberBinding.autoCompRelationWitHOH.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.autoCompRelationWitHOH, "Relationship with HOH field is mandatory", memberActivity);
            isMemberValidated = false;
        } else if (memberBinding.autoCompRelationWitHOH.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etRelationshipWithHohOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etRelationshipWithHohOther, "Please enter Relationship with HOH", memberActivity);
            isMemberValidated = false;
        } else if(memberBinding.etFirstNameMember.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etFirstNameMember, "Please enter name.", memberActivity);
            isMemberValidated = false;
        } else if (!Utils.isNullOrEmpty(memberBinding.etFirstNameMember.getText().toString()) &&
                !memberBinding.etFirstNameMember.getText().toString().matches("^[a-zA-Z\\s]+$")) {
            Utils.setError(memberBinding.etFirstNameMember, "Invalid Name Format - Field should only contain letters", memberActivity);
            isMemberValidated = false;
        } else if (!Utils.isNullOrEmpty(memberBinding.etFirstNameMember.getText().toString()) &&
                !memberBinding.etFirstNameMember.getText().toString().matches("^[a-zA-Z\\s]+$")) {
            Utils.setError(memberBinding.etFirstNameMember, "Invalid Name Format - Field should only contain letters", memberActivity);
            isMemberValidated = false;
        } else if (!memberBinding.etNameSpouse.getText().toString().equals("") &&
                !memberBinding.etNameSpouse.getText().toString().matches("^[a-zA-Z\\s]+$")) {
            Utils.setError(memberBinding.etNameSpouse, "Invalid Name Format - Field should only contain letters", memberActivity);
            isMemberValidated = false;
        } else if (!memberBinding.etRespondentContact.getText().toString().equals("") &&
                memberBinding.etRespondentContact.getText().toString().length() < 10) {
            memberBinding.etRespondentContact.setError("Please enter 10 digit contact number.");
            memberBinding.etRespondentContact.requestFocus();
            isMemberValidated = false;
        } else if (!memberBinding.etAadhaarNumber.getText().toString().equals("") &&
                memberBinding.etAadhaarNumber.getText().toString().length() < 12) {
            memberBinding.etAadhaarNumber.setError("The Aadhar number should be 12 digits long");
            memberBinding.etAadhaarNumber.requestFocus();
            isMemberValidated = false;
        } else if (!memberBinding.etPanNumber.getText().toString().equals("") &&
                !memberBinding.etPanNumber.getText().toString().matches("^[A-Z]{5}[0-9]{4}[A-Z]$")) {
            memberBinding.etPanNumber.setError("Improper format of PAN number");
            memberBinding.etPanNumber.requestFocus();
            isMemberValidated = false;
        } else if (!memberBinding.etRationCardNumber.getText().toString().equals("") &&
                !memberBinding.etRationCardNumber.getText().toString().matches("^[a-zA-Z0-9]*$")) {
            memberBinding.etRationCardNumber.setError(activity.getString(R.string.rationCard_validations));
            memberBinding.etRationCardNumber.requestFocus();
            return false;
        } else if(!memberBinding.etRationCardNumber.getText().toString().equals("") &&
                !memberBinding.etRationCardNumber.getText().toString().matches("^[a-zA-Z0-9]*$")) {
            memberBinding.etRationCardNumber.setError(memberActivity.getString(R.string.rationCard_validations));
            memberBinding.etRationCardNumber.requestFocus();
            isMemberValidated = false;
        }else if(memberBinding.radioHandicappedYes.isChecked()  && list1.isEmpty()  ){ // vidnyan changes for divyang certificate
            //need to check for attached doc for same
            Toast.makeText(activity, activity.getString(R.string.divyangOption_validations), Toast.LENGTH_SHORT).show();
            isMemberValidated = false;
        }else if(!memberBinding.etAadhaarNumber.getText().toString().equals("") &&  list1Adhar.isEmpty()){
            //vidnyan, Adhar card attachment check hoh_member_Aadhar_Card
            Toast.makeText(activity, activity.getString(R.string.adharhOption_validations), Toast.LENGTH_SHORT).show();
            isMemberValidated = false;
        } else if(!memberBinding.etPanNumber.getText().toString().equals("") && list1PAN.isEmpty()){
            //vidnyan, PAN card attachment check  hoh_member_Pan_Card
            Toast.makeText(activity, activity.getString(R.string.panOption_validations), Toast.LENGTH_SHORT).show();
            isMemberValidated = false;
        } else if (memberBinding.autoCompMaritialStatus.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etMaritalStatusOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etMaritalStatusOther, "Please Enter Marital Status", memberActivity);
            isMemberValidated = false;
        } else if (memberBinding.autoCompReligion.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etReligionOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etReligionOther, "Please Enter Religion", memberActivity);
            isMemberValidated = false;
        } else if (memberBinding.autoCompWhichState.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etWhichStateOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etWhichStateOther, "Please Enter State Name", memberActivity);
            isMemberValidated = false;
        } else if (memberBinding.autoCompMotherTongue.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etMotherTongueOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etMotherTongueOther, "Please Enter Mother Tongue", memberActivity);
            isMemberValidated = false;
        } else if (memberBinding.autoCompEducation.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etEducationOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etEducationOther, "Please Enter Education", memberActivity);
            isMemberValidated = false;
        } else if (memberBinding.autoCompOccupation.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etOccupationOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etOccupationOther, "Please Enter Occupation", memberActivity);
            isMemberValidated = false;
        } else if (memberBinding.autoCompWorkType.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etTypeOfWorkOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etTypeOfWorkOther, "Please Enter Type Of Work", memberActivity);
            isMemberValidated = false;
        } else if (memberBinding.autoCompTransport.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etModeOfTransportOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etModeOfTransportOther, "Please Enter Mode Of Transport", memberActivity);
            isMemberValidated = false;
        } else if (memberBinding.autoCompVehicleType.getText().toString().equalsIgnoreCase(Constants.dropdown_others) &&
                memberBinding.etVehicleTypeOther.getText().toString().isEmpty()) {
            Utils.setError(memberBinding.etVehicleTypeOther, "Please Enter Type Of Vehicle", memberActivity);
            isMemberValidated = false;
        } else {
            //  if(hasMemberName) {
            isMemberValidated = true;
            //}
        }

        return isMemberValidated;
    }

    @Override
    public void onAttachmentNameTextClicked(AttachmentListImageDetails attachmentListImageDetails)  {
        try {
            String unit_unique_id = unitInfoDataModel.getUnit_unique_id(), unitUniqueId = unitInfoDataModel.getUnit_unique_id();
            if (cc.equals("member")) {
                if(selectedmemberInfoDataModel == null) {
                    unit_unique_id = memberUniqueId;
                    unitUniqueId = memberUniqueId;
                } else if(selectedmemberInfoDataModel != null) {
                    unit_unique_id = selectedmemberInfoDataModel.getMember_id();
                    unitUniqueId = selectedmemberInfoDataModel.getMember_id();
                }
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
        try{
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
            Utils.shortToast("Exception in onAttachmentCancelBtnClicked:"+e.getMessage(),activity);
            AppLog.e("Exception in onAttachmentCancelBtnClicked:"+e.getMessage());
        }
    }

    @Override
    public void onAttachmentUpdateClicked(MediaInfoDataModel newMediaInfoDataModels) {
        try{
            if (newMediaInfoDataModels != null) {
                if (cc.equals("member")) {
                    localSurveyDbViewModel.setIsUploaded(memberUniqueId, newMediaInfoDataModels.getObejctId(), false);
                    localSurveyDbViewModel.setRemarksByMediaId(newMediaInfoDataModels.getMediaId(), newMediaInfoDataModels.getDocument_remarks());
                } else {
                    localSurveyDbViewModel.setIsUploaded(unitInfoDataModel.getUnit_unique_id(), newMediaInfoDataModels.getObejctId(), false);
                }
//        localSurveyDbViewModel.setIsUploaded(unitUniqueId, newMediaInfoDataModels.getObejctId(), false);
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
            Utils.shortToast("Exception in onAttachmentUpdateClicked:"+e.getMessage(),activity);
            AppLog.e("Exception in onAttachmentUpdateClicked:"+e.getMessage());
        }
    }


    @Override
    public void onAttachmentDeletedClicked(List<MediaInfoDataModel> deleteTotalMediaList, int flag, int pos, List<AttachmentItemList> attachmentItemLists, String itemUrl) {
        try{
            String unit_unique_id = unitInfoDataModel.getUnit_unique_id(), unitUniqueId = unitInfoDataModel.getUnit_id();
            if (cc.equals("member")) {
                if(selectedmemberInfoDataModel == null) {
                    unit_unique_id = memberUniqueId;
                    unitUniqueId = memberUniqueId;
                } else if(selectedmemberInfoDataModel != null) {
                    unit_unique_id = selectedmemberInfoDataModel.getMember_id();
                    unitUniqueId = selectedmemberInfoDataModel.getMember_id();
                }
            }else if(cc.equals("")){
                unitUniqueId = unitInfoDataModel.getUnit_id();
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
                    localSurveyDbViewModel.updateByFileName(true, deleteTotalMediaList.get(0).getFilename(), deleteTotalMediaList.get(0).getParent_unique_id());
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
                        localSurveyDbViewModel.uploadListByFileName(deleteTotalMediaList.get(0).getParent_unique_id(), deleteTotalMediaList.get(0).getFilename(), aff);
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
                    localSurveyDbViewModel.uploadAttListByFileName(deleteTotalMediaList.get(0).getParent_unique_id(), deleteTotalMediaList.get(0).getFilename(), deleteTotalMediaList.get(0).getAttachmentItemLists());
                } else {
                    localSurveyDbViewModel.setMediaDeletedStatusByList(unitUniqueId, deleteTotalMediaList.get(0).getAttachmentItemLists(), deleteTotalMediaList.get(0).getObejctId());
                }


                List<MediaInfoDataModel> finalDeleteTotalMediaList = deleteTotalMediaList;
                String finalUnit_unique_id = unit_unique_id;
                String finalUnitUniqueId = unitUniqueId;
                new Handler().postDelayed(() -> {

                    try {

                        List<AttachmentItemList> btt = new ArrayList<>();
                        List<AttachmentItemList> att = new ArrayList<>();

                        if (!finalDeleteTotalMediaList.isEmpty()) {
                            if (finalDeleteTotalMediaList.get(0).getObejctId().equals("")) {

                                List<MediaInfoDataModel> listMediaModels = localSurveyDbViewModel.getByItemUrl(finalUnit_unique_id, finalDeleteTotalMediaList.get(0).getFilename());

                                if (listMediaModels != null && !listMediaModels.isEmpty()) {
                                    att = listMediaModels.get(0).getAttachmentItemLists();
                                }
                            } else {
                                att = localSurveyDbViewModel.getMediaInfoDataByObjId(finalDeleteTotalMediaList.get(0).getObejctId(), finalUnitUniqueId).get(0).getAttachmentItemLists();
                            }

                            for (int i = 0; i < att.size(); i++) {
                                if (!att.get(i).isDeleted) {
                                    btt.add(att.get(i));
                                }
                            }
                            if (viewAttachAdapter != null) {
                                viewAttachAdapter.setUpdatedImages(btt);
                            }
                            if(viewDeleteAdapterDelete!=null){
                                globalPanchnamaPath="";
                                ArrayList<MediaInfoDataModel> aa=(ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
                                ArrayList<String> itemList=new ArrayList<>();
                                if(aa.size()>0){
                                    for(int i=0;i<aa.get(0).getAttachmentItemLists().size();i++){
                                        if(!aa.get(0).getAttachmentItemLists().get(i).isDeleted){
                                            itemList.add(aa.get(0).getAttachmentItemLists().get(i).getItem_url());
                                        }
                                    }
                                }
                                viewDeleteAdapterDelete.setUpdatedList(itemList);
                            }
                        }
                    } catch (Exception e) {
                        AppLog.e(e.getMessage());
                    }
                }, 2000);
            } else if (flag == 2) {

                if (deleteTotalMediaList.get(0).getObejctId()==null || deleteTotalMediaList.get(0).getObejctId().equals("")) {
                    deleteTotalMediaList = localSurveyDbViewModel.getByItemUrl(deleteTotalMediaList.get(0).getParent_unique_id(), deleteTotalMediaList.get(0).getFilename());
                } else {
                    deleteTotalMediaList = localSurveyDbViewModel.getMediaInfoDataByObjId(deleteTotalMediaList.get(0).getObejctId(), deleteTotalMediaList.get(0).getParent_unique_id());
                }


                if (deleteTotalMediaList.get(0).getObejctId()==null || deleteTotalMediaList.get(0).getObejctId().equals("")) {
                    localSurveyDbViewModel.setMediaDeletedListByUrl(unitUniqueId, deleteTotalMediaList.get(0).getFilename(), true);
                } else {
                    localSurveyDbViewModel.setMediaDeletedStatus(unitUniqueId, deleteTotalMediaList.get(0).getObejctId(), true);
                }

                if (deleteTotalMediaList.get(0).getObejctId()==null || deleteTotalMediaList.get(0).getObejctId().equals("")) {
                    localSurveyDbViewModel.setDeleteItemObjValid(true, deleteTotalMediaList.get(0).getFilename(), unitUniqueId);
                } else {
                    localSurveyDbViewModel.setDeleteItemObjValid(true, deleteTotalMediaList.get(0).getObejctId(), unitUniqueId);
                }

                if (deleteTotalMediaList.get(0).getObejctId()==null || deleteTotalMediaList.get(0).getObejctId().equals("")) {
                    localSurveyDbViewModel.setDeleteWholeFile(true, deleteTotalMediaList.get(0).getFilename(), unitUniqueId);
                } else {
                    localSurveyDbViewModel.setDeleteWholeObject(true, deleteTotalMediaList.get(0).getObejctId(), unitUniqueId);
                }

                for (int i = 0; i < deleteTotalMediaList.get(0).getAttachmentItemLists().size(); i++) {
                    deleteTotalMediaList.get(0).getAttachmentItemLists().get(i).setIsDeleted(true);
                }

                if (deleteTotalMediaList.get(0).getObejctId()==null || deleteTotalMediaList.get(0).getObejctId().equals("")) {
                    localSurveyDbViewModel.uploadAttListByFileName(unitUniqueId, deleteTotalMediaList.get(0).getFilename(), deleteTotalMediaList.get(0).getAttachmentItemLists());
                } else {
                    localSurveyDbViewModel.setMediaDeletedStatusByList(unitUniqueId, deleteTotalMediaList.get(0).getAttachmentItemLists(), deleteTotalMediaList.get(0).getObejctId());
                }

                if(viewDeleteAdapterDelete!=null){
                    globalPanchnamaPath="";
                    tempImageLayout.setVisibility(View.GONE);
//                    ArrayList<MediaInfoDataModel> aa=(ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
//                    ArrayList<String> itemList=new ArrayList<>();
//                    if(aa.size()>0){
//                        for(int i=0;i<aa.get(0).getAttachmentItemLists().size();i++){
//                            if(!aa.get(0).getAttachmentItemLists().get(i).isDeleted){
//                                itemList.add(aa.get(0).getAttachmentItemLists().get(i).getItem_url());
//                            }
//                        }
//                    }
//                    viewDeleteAdapterDelete.setUpdatedList(itemList);
                }
                if (dialogGlobal != null) {
                    dialogGlobal.dismiss();
                }
            }
        } catch (Exception e) {
            Utils.shortToast("Exception in onAttachmentDeleteClicked:"+e.getMessage(),activity);
            AppLog.e("Exception in onAttachmentDeleteClicked:"+e.getMessage());
        }
    }



    @Override
    public void onAttachmentBrowseClicked(MediaInfoDataModel mediaInfoDataModel, String documentType, String unitRelativePath, String name) {
        try{
            String unit_unique_id = unitInfoDataModel.getUnit_unique_id(), unitUniqueId = unitInfoDataModel.getUnit_unique_id();
            if (cc.equals("member")) {
                if(selectedmemberInfoDataModel == null) {
                    unit_unique_id = memberUniqueId;
                    unitUniqueId = memberUniqueId;
                } else if(selectedmemberInfoDataModel != null) {
                    unit_unique_id = selectedmemberInfoDataModel.getMember_id();
                    unitUniqueId = selectedmemberInfoDataModel.getMember_id();
                }
            }
            alpha = 1;
            updObj = mediaInfoDataModel;

            if(memberBinding.etFirstNameMember.getText().toString().isEmpty()){
                return;
            }

            // #970 : Name Member
            if(memberBinding.etFirstNameMember.getText().toString().isEmpty()) {
                memberBinding.etFirstNameMember.setError(activity.getString(R.string.mandatory_field));
                memberBinding.etFirstNameMember.requestFocus();
                return;
            }
            name = Utils.getAttachmentFileName(memberBinding.etFirstNameMember.getText().toString(), mediaInfoDataModel.getDocument_type());
            showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, name);

            // showAttachmentAlertDialogButtonClicked(documentType, unitRelativePath, "member_"+name);

        } catch (Exception e) {
            Utils.shortToast("Exception in onAttachmentBrowseClicked:"+e.getMessage(),activity);
            AppLog.e("Exception in onAttachmentBrowseClicked:"+e.getMessage());
        }
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
                    if (Utils.getFileSizeFromFile(memberActivity, finalFile)) {
                        isOkFileSize = true;
                        finalFileName = Utils.getFileName(finalUri, memberActivity);
                    } else {
                        isOkFileSize = false;
                    }
                    Utils.deleteRecentClickedImage(activity);
                } else {
                    Utils.shortToast("Capture again.", memberActivity);
                }
            } else if (requestCode == selectGallery || requestCode == selectPdf) {
                finalUri = data.getData();

                if (okFileExtensions.contains(Utils.getFileExt(finalUri, memberActivity))) {
                    isOkFileExtensions = true;
                    if (Utils.getFileSizeFromUri(memberActivity, finalUri)) {
                        isOkFileSize = true;
                        finalFile = Utils.copyFile(memberActivity, finalUri, target_relative_path, target_name);
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
                Utils.showMessagePopup("." + Utils.getFileExt(finalUri, memberActivity) + " is an invalid attachment type. Accepted file types are jpg, png, jpeg, and pdf.", memberActivity);
                return;
            }

            if (!isOkFileSize) {
                return;
            }

            if (finalUri != null && finalFile != null) {
                try {
//                    if (!finalFileName.contains(".pdf"))
                    CryptoUtilsTest.encryptFileinAES(finalFile, 1);

                    if(userAttachmentList == null) {
                        return;
                    }

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
                        case Constants.rationLabel:
                            residentProofAttachmentCount++;
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            break;
                        case Constants.PanchnamaLable:
                            globalPanchnamaPath=finalFile.getPath();
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));

                            ArrayList<MediaInfoDataModel> aa=(ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
                            ArrayList<String> itemList=new ArrayList<>();
                            if(aa.size()>0){
                                for(int i=0;i<aa.get(0).getAttachmentItemLists().size();i++){
                                    if(!aa.get(0).getAttachmentItemLists().get(i).isDeleted){
                                        itemList.add(aa.get(0).getAttachmentItemLists().get(i).getItem_url());
                                    }
                                }
                                itemList.add(globalPanchnamaPath);
                            }else{
                                itemList.add(globalPanchnamaPath);
                            }
                            tempImageLayout.setVisibility(View.VISIBLE);
                            viewDeleteAdapterDelete.setUpdatedList(itemList);


                            File imgFile = new File(globalPanchnamaPath);
                            if(imgFile.exists()){
                                try {
                                    CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                                    Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
//                                    tempImageButton.setImageBitmap(bitmap);
//                                    tempImageButton.setVisibility(View.VISIBLE);
//                                    tempImageLayout.setVisibility(View.VISIBLE);
                                } catch (CryptoException e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }catch (Exception e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            };
                            break;
                        case Constants.AnnexureLableA:
                            imgFile=null;
                            if(ann==1){
                                globalAnnexureAPath=finalFile.getPath();
                            }else if(ann==2){
                                globalAnnexureBPath=finalFile.getPath();
                            }
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            if(ann==1){
                                imgFile = new File(globalAnnexureAPath);
                            }else if(ann==2){
                                imgFile = new File(globalAnnexureBPath);
                            }
                            if(imgFile.exists()){
                                try {
                                    Bitmap myBitmap=null;
                                    CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                                    if(ann==1){
//                                        myBitmap = BitmapFactory.decodeFile(globalAnnexureAPath);
                                        Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                                        anexureOneLayout.setVisibility(View.VISIBLE);
                                        annexureOneCapturedDocument.setImageBitmap(bitmap);
                                    }else if(ann==2){
                                        myBitmap = BitmapFactory.decodeFile(globalAnnexureBPath);
                                        anexureTwoLayout.setVisibility(View.VISIBLE);
                                        annexureTwoCapturedDocument.setImageBitmap(myBitmap);
                                    }

                                } catch (CryptoException e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }catch (Exception e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            };
                            break;
                        case Constants.AnnexureLableB:
                            imgFile=null;
                            if(ann==1){
                                globalAnnexureAPath=finalFile.getPath();
                            }else if(ann==2){
                                globalAnnexureBPath=finalFile.getPath();
                            }
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            if(ann==1){
                                imgFile = new File(globalAnnexureAPath);
                            }else if(ann==2){
                                imgFile = new File(globalAnnexureBPath);
                            }
                            if(imgFile.exists()){
                                try {
                                    Bitmap myBitmap=null;
                                    CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                                    if(ann==1){
                                        myBitmap = BitmapFactory.decodeFile(globalAnnexureAPath);
                                        anexureOneLayout.setVisibility(View.VISIBLE);
                                        annexureOneCapturedDocument.setImageBitmap(myBitmap);
                                    }else if(ann==2){
//                                        myBitmap = BitmapFactory.decodeFile(globalAnnexureBPath);
                                        Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                                        anexureTwoLayout.setVisibility(View.VISIBLE);
                                        annexureTwoCapturedDocument.setImageBitmap(bitmap);
                                    }

                                } catch (CryptoException e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }catch (Exception e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            };
                            break;


                            /*
                            namuna
                             */
                        case Constants.NamunaLableA:
                            imgFile=null;
                            if(ann==1){
                                globalAnnexureAPath=finalFile.getPath();
                            }else if(ann==2){
                                globalAnnexureBPath=finalFile.getPath();
                            }
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            if(ann==1){
                                imgFile = new File(globalAnnexureAPath);
                            }else if(ann==2){
                                imgFile = new File(globalAnnexureBPath);
                            }
                            if(imgFile.exists()){
                                try {
                                    Bitmap myBitmap=null;
                                    CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                                    if(ann==1){
//                                        myBitmap = BitmapFactory.decodeFile(globalAnnexureAPath);
                                        Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                                        anexureOneLayout.setVisibility(View.VISIBLE);
                                        annexureOneCapturedDocument.setImageBitmap(bitmap);
                                    }else if(ann==2){
                                        myBitmap = BitmapFactory.decodeFile(globalAnnexureBPath);
                                        anexureTwoLayout.setVisibility(View.VISIBLE);
                                        annexureTwoCapturedDocument.setImageBitmap(myBitmap);
                                    }

                                } catch (CryptoException e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }catch (Exception e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            };
                            break;
                        case Constants.NamunaLableB:
                            imgFile=null;
                            if(ann==1){
                                globalAnnexureAPath=finalFile.getPath();
                            }else if(ann==2){
                                globalAnnexureBPath=finalFile.getPath();
                            }
                            userAttachmentList.add(new AttachmentListImageDetails(finalFileName, finalFile.getPath(), "", finalFile));
                            if(ann==1){
                                imgFile = new File(globalAnnexureAPath);
                            }else if(ann==2){
                                imgFile = new File(globalAnnexureBPath);
                            }
                            if(imgFile.exists()){
                                try {
                                    Bitmap myBitmap=null;
                                    CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                                    if(ann==1){
                                        myBitmap = BitmapFactory.decodeFile(globalAnnexureAPath);
                                        anexureOneLayout.setVisibility(View.VISIBLE);
                                        annexureOneCapturedDocument.setImageBitmap(myBitmap);
                                    }else if(ann==2){
//                                        myBitmap = BitmapFactory.decodeFile(globalAnnexureBPath);
                                        Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                                        anexureTwoLayout.setVisibility(View.VISIBLE);
                                        annexureTwoCapturedDocument.setImageBitmap(bitmap);
                                    }

                                } catch (CryptoException e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }catch (Exception e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            };
                            break;
                    }
                    if (addImageAdapter != null) {
                        addImageAdapter.setAttachmentListImageDetails(userAttachmentList);
                    }


                    if (alpha == 1 && updObj != null) {
                        String unit_unique_id = this.unitInfoDataModel.getUnit_unique_id(), unitUniqueId = this.unitInfoDataModel.getUnit_unique_id();
                        /*if(cc.equals("hoh")){
                            unit_unique_id=hohUniqueId;
                            unitUniqueId=hohUniqueId;
                        }else */if(cc.equals("member")){
                            unit_unique_id=memberUniqueId;
                            unitUniqueId=memberUniqueId;
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
//                    setCounts(year);
                } catch (CryptoException e) {
                    AppLog.logData(activity,e.getMessage());
                    Utils.shortToast("Error while encrypting the file.", memberActivity);
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private void showUnitStatus() {

        if(memberActivity == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(memberActivity);
        final View customLayout = memberActivity.getLayoutInflater().inflate(R.layout.unit_status_remarks, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        structureInfoPointDataModel = (StructureInfoPointDataModel) memberActivity.getIntent().getSerializableExtra(Constants.INTENT_DATA_StructureInfo);

        // TextView textHeader = customLayout.findViewById(R.id.txt_header);
        // textHeader.setText("Structure Status");
        // TextView textMessage = customLayout.findViewById(R.id.txt_mssage);
        // textMessage.setText("Update the status of this structure");
        // RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);
        AutoCompleteTextView auto_comp_remarks = customLayout.findViewById(R.id.auto_comp_remarks_list);
        EditText et_remarks = customLayout.findViewById(R.id.etRemarks);
        MaterialRadioButton radio_inProg = customLayout.findViewById(R.id.radio_inProg);
        MaterialRadioButton radio_hold = customLayout.findViewById(R.id.radio_hold);
        MaterialRadioButton radio_complete = customLayout.findViewById(R.id.radio_complete);
        MaterialRadioButton radioDispute = customLayout.findViewById(R.id.radio_dispute);
        // ArrayAdapter<String> remarksValues = new ArrayAdapter<>(memberActivity, R.layout.list_item,
        // memberActivity.getResources().getStringArray(R.array.remarks_member));
        // auto_comp_remarks.setAdapter(remarksValues);
        setFocusChange_OnTouchUnitStatusRemarks(auto_comp_remarks, et_remarks);
        auto_comp_remarks.setSelection(0);
        auto_comp_remarks.setTag("");
        radio_inProg.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                auto_comp_remarks.setText("");
                auto_comp_remarks.setTag("");
                et_remarks.setText("");
                et_remarks.setTag("");
                et_remarks.setVisibility(View.GONE);
                unitAlertStatus = Constants.InProgress_statusLayer;
                auto_comp_remarks.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, Utils.getDomianList(Constants.domain_unit_status_in_progress_remarks))); //memberActivity.getResources().getStringArray(R.array.remarks_in_progress)
                setFocusChange_OnTouchUnitStatusRemarks(auto_comp_remarks, et_remarks);
            }
        });

        radio_hold.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                auto_comp_remarks.setText("");
                auto_comp_remarks.setTag("");
                et_remarks.setText("");
                et_remarks.setTag("");
                et_remarks.setVisibility(View.GONE);
                unitAlertStatus = Constants.OnHold_statusLayer;
                auto_comp_remarks.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, Utils.getDomianList(Constants.domain_unit_status_hold_remarks)));//memberActivity.getResources().getStringArray(R.array.remarks_on_hold)
                setFocusChange_OnTouchUnitStatusRemarks(auto_comp_remarks, et_remarks);
            }
        });

        radio_complete.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                auto_comp_remarks.setText("");
                auto_comp_remarks.setTag("");
                et_remarks.setText("");
                et_remarks.setTag("");
                et_remarks.setVisibility(View.GONE);
                unitAlertStatus = Constants.completed_statusLayer;
                auto_comp_remarks.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item,Utils.getDomianList(Constants.domain_unit_status_completed_remarks)));//memberActivity.getResources().getStringArray(R.array.remarks_completed)
                setFocusChange_OnTouchUnitStatusRemarks(auto_comp_remarks, et_remarks);
            }
        });

        radioDispute.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                auto_comp_remarks.setText("");
                auto_comp_remarks.setTag("");
                et_remarks.setText("");
                et_remarks.setTag("");
                et_remarks.setVisibility(View.GONE);
                unitAlertStatus = Constants.completed_dispute;
                auto_comp_remarks.setAdapter(new ArrayAdapter<>(memberActivity, R.layout.list_item, Utils.getDomianList(Constants.domain_unit_status_dispute_remarks)));//memberActivity.getResources().getStringArray(R.array.remarks_dispute)
                setFocusChange_OnTouchUnitStatusRemarks(auto_comp_remarks, et_remarks);
            }
        });

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Utils.showProgress("Please wait...",memberActivity);
                // new Handler().postDelayed(() -> {
                if (auto_comp_remarks.getTag() == null) {
                    auto_comp_remarks.setTag("");
                }

                String selectedRemark = auto_comp_remarks.getTag().toString();

                if (selectedRemark.isEmpty()) {
                    Utils.shortToast("Please select remarks", auto_comp_remarks.getContext());
                    return;
                }

                if(selectedRemark.equalsIgnoreCase(Constants.dropdown_others) && et_remarks.getText().toString().isEmpty()) {
                    et_remarks.setError("Please enter remarks");
                    et_remarks.requestFocus();
                    return;
                }

                // String selectedRemark = remarksIdx == -1 ? remarks[0] : remarksValues.getItem(remarksIdx);

                unitInfoDataModel.setUnit_status(unitAlertStatus);
                unitInfoDataModel.setRemarks(selectedRemark);
                unitInfoDataModel.setRemarks_other(et_remarks.getText().toString());

                // localSurveyDbViewModel.insertStructureUnitIdStatusData(new StructureUnitIdStatusDataTable(unitInfoDataModel.getUnit_id(),
                //  structureInfoPointDataModel.getStructure_id(), unitAlertStatus), memberActivity);
                localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                // localSurveyDbViewModel.updateStructureStatusDataTable(structUniqueId,unitAlertStatus);

                workAreaName = structureInfoPointDataModel.getWork_area_name();
                Utils.dismissProgress();
                dialog.dismiss();

                /* Rohit otp dialog */
                new Handler().postDelayed(() -> {
                    String sts = localSurveyDbViewModel.getUnitInfoStatus(unitInfoDataModel.getUnit_id());
                    Utils.dismissProgress();
                    if (sts.equals(Constants.completed_statusLayer) || sts.equals(Constants.completed_dispute)) {
                        showOTPPopup();
                    } else {
                        showFormSubmitDialog();
                    }
                }, 2000);
                // activity.startActivity(new Intent(activity, MapPageActivity.class));
                // activity.finish();
                // }, 2000);
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        if(isRelAmen){
            dialog.setCancelable(false);
        }
    }

    private void showFormSubmitDialog() {
        if(!activity.isFinishing()){
            AlertDialog.Builder builder = new AlertDialog.Builder(memberActivity);
            final View customLayout = memberActivity.getLayoutInflater().inflate(R.layout.custom_form_submit_dialog, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //Utils.shortToast("Member Not Available.",activity);
                    memberActivity.startActivity(new Intent(memberActivity, MapActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            .putExtra(Constants.INTENT_SelectedWorkArea, workAreaName));;
                    memberActivity.finish();
                }
            });

            dialog.show();
        }
    }

    public void aadharTextWatcher() {

        memberBinding.etAadhaarNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    MaskedEditText.setMaskedText(memberBinding.etAadhaarNumber,memberBinding.etAadhaarNumber.getText().toString());
                }
                else{
                    // Changes applied as said by Komal
                    memberBinding.etAadhaarNumber.setText(MaskedEditText.getOriginalText(memberBinding.etAadhaarNumber));
                }
            }
        });

//        memberBinding.etAadhaarNumber.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                originalAadhaarNo = s.toString();
//                ArrayList<String> ch = new ArrayList<>();
//                for (int i = 0; i < originalAadhaarNo.length(); i++) {
//
//                    if (i < 8) {
//                        ch.add("x");
//                    } else ch.add(String.valueOf(originalAadhaarNo.charAt(i)));
//
//                }
//                String listString = String.join("", ch);
//
//                memberBinding.etAadhaarNumber.removeTextChangedListener(this); // Prevent infinite loop
//                memberBinding.etAadhaarNumber.setText(listString);
//                memberBinding.etAadhaarNumber.setSelection(ch.size());
//                memberBinding.etAadhaarNumber.addTextChangedListener(this);
//            }
//        });
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

    private static long extractTimestamp(String inputString) {
        int startIndex = inputString.indexOf("time=") + 5;
        int endIndex = inputString.indexOf(",", startIndex);
        String timestampString = inputString.substring(startIndex, endIndex);
        return Long.parseLong(timestampString);
    }

    private static String formatDate(Calendar calendar) {
        TimeZone timeZone = TimeZone.getTimeZone("IST");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(timeZone);
        return sdf.format(calendar.getTime());
    }

    private void syncMemberWithLiveData(String hohId){
        try{
//            localSurveyDbViewModel.deleteMemberByHohId(hohId,memberActivity);
            localSurveyDbViewModel.insertAllMemberInfoPointData(existingMemberInfoDataModelAL,memberActivity);
            new Thread(){
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(3000);
                            memberActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initMemberList(hohId);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        AppLog.logData(activity,e.getMessage());
                        e.printStackTrace();
                    }
                };
            }.start();



        }catch(Exception ex){
            AppLog.e("Exception in syncMemberWithLiveData:"+ex.getMessage());
        }
    }

    private void initMemberList(String hohId){
        try{
            Context context = memberBinding.btnCancel.getContext();
            LinearLayout listHohMemberLinearLayout = memberBinding.llMemberName;
            listHohMemberLinearLayout.removeAllViews();
            List<MemberInfoDataModel> memberList=localSurveyDbViewModel.getMemberDetailsByHohId(hohId,memberActivity);
            memberList.forEach(member -> {
                LinearLayout linearLayout = new LinearLayout(context);
                ImageView defaultMemberPic = new ImageView(context);
                TextView memberName = new TextView(context);
                Drawable drawable = context.getResources().getDrawable(R.drawable.icon_member_svg);

                defaultMemberPic.setImageDrawable(drawable);
                memberName.setText(member.getMember_name());//M_1707490271862
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(defaultMemberPic);
                linearLayout.addView(memberName);
                linearLayout.setOnClickListener(view -> {
                    memberUniqueId = member.getMember_id();
                    member_relative_path = member.getRelative_path();
                    member_rel_global_id = member.getRel_globalid();
                    memberBinding.txtSubmit.setText("Update");
                    applyExistingMemberToUI();
                    setUpAdapterNListeners();
                });
                listHohMemberLinearLayout.addView(linearLayout);
            });
        }catch(Exception ex){
            AppLog.e("Exception in initMemberList:"+ex.getMessage());
        }
    }

    /*
    Rohit OTP
     */
    private void showOTPPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_input_mobile_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        etHohMobileForOTP = customLayout.findViewById(R.id.etHohMobileForOTP);
        if(isRelAmen){
            etHohMobileForOTP.setText(unitInfoDataModel.getRespondent_mobile());
        }else{
            etHohMobileForOTP.setText(hohInfoDataModel.getHoh_contact_no());
        }
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etHohMobileForOTP.getText().toString().trim().length()<10){
                    Utils.shortToast("Enter Valid 10 Digit Mobile Number",activity);
                }else{
                    dialog.dismiss();
                    showVerifyOTPPopup();
                   // resendOTP(dialog);
                    /*
                    Map<String, Object> jsonParams = new ArrayMap<>();
                    jsonParams.put("receiver", etHohMobileForOTP.getText().toString());
                    jsonParams.put("hohName", hohInfoDataModel.getHoh_name());
                    jsonParams.put("surveyId", unitInfoDataModel.getHut_number());
                    jsonParams.put("relationShipManagerNo", "52654");
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());


                    Utils.updateProgressMsg("Sending OTP, please wait..", activity);

                    try{
                        Api_Interface apiInterface = RetrofitService.getSMSClient().create(Api_Interface.class);
                        Call<JsonElement> call = apiInterface.sendOTP(body);
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                                Utils.dismissProgress();
                                Log.d("TAG",response.code()+"");
                                if(response.code()==200){

                                    JSONObject resObj=null;

                                    try {
                                        resObj = new JSONObject(response.body().toString());
                                        dialog.dismiss();
                                        showVerifyOTPPopup(resObj.getJSONObject("data").getString("transactionId"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }else{
                                    Utils.shortToast("Unable to Send OTP Try Again.",activity);
                                }

                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                call.cancel();
                            }
                        });

                    }catch(Exception ex){
                        AppLog.e(ex.getMessage());
                    }
*/
                }

            }
        });
        dialog.show();
    }

    private void showVerifyOTPPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_otp_input_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        TextView txtOtpSentMobileNumberMsg = customLayout.findViewById(R.id.txtOtpSentMobileNumberMsg);
        TextView continueWithoutOTP = customLayout.findViewById(R.id.continueWithoutOTP);
        TextView resendTime = customLayout.findViewById(R.id.resendTime);
        TextView resendOTP = customLayout.findViewById(R.id.resendOTP);
        LinearLayout layout_resend = customLayout.findViewById(R.id.layout_resend);
        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        EditText e1 = customLayout.findViewById(R.id.et_one);
        EditText e2 = customLayout.findViewById(R.id.et_two);
        EditText e3 = customLayout.findViewById(R.id.et_three);
        EditText e4 = customLayout.findViewById(R.id.et_four);
        EditText e5 = customLayout.findViewById(R.id.et_five);
        EditText e6 = customLayout.findViewById(R.id.et_six);
        txtOtpSentMobileNumberMsg.setText("We've sent OTP to your mobile number at *******"+etHohMobileForOTP.getText().toString().substring(8)+". Please enter 6 digit code receive");
        e1.addTextChangedListener(new GenericTextWatcher(e2, e1));
        e2.addTextChangedListener(new GenericTextWatcher(e3, e1));
        e3.addTextChangedListener(new GenericTextWatcher(e4, e2));
        e4.addTextChangedListener(new GenericTextWatcher(e5, e3));
        e5.addTextChangedListener(new GenericTextWatcher(e6, e4));
        e6.addTextChangedListener(new GenericTextWatcher(e6, e5));
        resendOTP();
        layout_resend.setVisibility(View.VISIBLE);
        resendOTP.setVisibility(View.GONE);
        startCountDown(resendTime,resendOTP,layout_resend);
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP();
            }
        });


        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(e1.getText().toString().trim().length()==0 ||
                        e2.getText().toString().trim().length()==0 ||
                        e3.getText().toString().trim().length()==0 ||
                        e4.getText().toString().trim().length()==0 ||
                        e5.getText().toString().trim().length()==0 ||
                        e6.getText().toString().trim().length()==0){
                    Utils.shortToast("Enter Valid OTP",activity);
                }else{
                    String otp=e1.getText().toString().trim()+e2.getText().toString().trim()+e3.getText().toString().trim()+e4.getText().toString().trim()+e5.getText().toString().trim()+e6.getText().toString().trim();
                    Map<String, Object> jsonParams = new ArrayMap<>();
                    jsonParams.put("sender", etHohMobileForOTP.getText().toString());
                    if(isRelAmen){
                        jsonParams.put("hohName", unitInfoDataModel.getRespondent_name());
                    }else{
                        jsonParams.put("hohName", hohInfoDataModel.getHoh_name());
                    }
                    jsonParams.put("surveyId", unitInfoDataModel.getHut_number());
                    jsonParams.put("relationShipManagerNo", "52654");
                    jsonParams.put("transactionId", transactionID);
                    jsonParams.put("otp", otp);
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());


                    Utils.updateProgressMsg("Verifying OTP, please wait..", activity);

                    try{
                        Api_Interface apiInterface = RetrofitService.getSMSClient().create(Api_Interface.class);
                        Call<JsonElement> call = apiInterface.verifyOTP(body);
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                                Utils.dismissProgress();
                                Log.d("TAG",response.code()+"");
                                if(response.code()==200){

                                    JSONObject resObj=null;

                                    try {
                                        resObj = new JSONObject(response.body().toString());
                                        if(resObj.getJSONObject("status").getString("message").equalsIgnoreCase("OTP is valid")){
                                            unitInfoDataModel.setMobile_no_for_otp(etHohMobileForOTP.getText().toString());
                                            unitInfoDataModel.setOtp_received(Integer.parseInt(otp));
                                            unitInfoDataModel.setOtp_sent(Integer.parseInt(otp));
                                            unitInfoDataModel.setOtp_verified((short)1);
                                            localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);

                                            dialog.dismiss();
                                            showPanchnamaDialog(isPanchnamaExist);
                                        }else{
                                            Utils.shortToast(resObj.getJSONObject("status").getString("message"),activity);
                                        }

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }else{
                                    Utils.shortToast("Try Again.",activity);
                                }

                            }

                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                call.cancel();
                            }
                        });

                    }catch(Exception ex){
                        AppLog.e(ex.getMessage());
                    }

                }

            }
        });

        continueWithoutOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
               showWithoutOTPDialog(false);
            }
        });

        dialog.show();
    }



    private void showWithoutOTPDialog(boolean isDone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.custom_without_otp_dialog, null);
        builder.setView(customLayout);
        AlertDialog dialog = builder.create();
        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        AutoCompleteTextView autoCompOtpReason=customLayout.findViewById(R.id.autoCompOtpReason);
        EditText etOtpReasonOther=customLayout.findViewById(R.id.etOtpReasonOther);
        autoCompOtpReason.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_otp_remarks)));
        setFocusChange_OnTouchOTPRemarks(autoCompOtpReason, etOtpReasonOther);

        if (isDone){
            autoCompOtpReason.setText(unitInfoDataModel.getOtp_remarks());
            autoCompOtpReason.setTag(unitInfoDataModel.getOtp_remarks());
            autoCompOtpReason.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_otp_remarks)));
        }

        btn_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(autoCompOtpReason.getTag()==null){
                    autoCompOtpReason.setTag("");
                }

                String selectedRemark = autoCompOtpReason.getTag().toString();

                if (selectedRemark.isEmpty()) {
                    Utils.shortToast("Please select Otp remarks", autoCompOtpReason.getContext());
                    return;
                }

                if(selectedRemark.equalsIgnoreCase(Constants.dropdown_others) && etOtpReasonOther.getText().toString().isEmpty()) {
                    etOtpReasonOther.setError("Please enter Otp remarks");
                    etOtpReasonOther.requestFocus();
                    return;
                }

                unitInfoDataModel.setOtp_remarks(selectedRemark);
                unitInfoDataModel.setOtp_remarks_other(etOtpReasonOther.getText().toString());
                localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);

                dialog.dismiss();

                showPanchnamaDialog(isPanchnamaExist);
            }
        });

        dialog.show();
    }

    private void showBiometricDialog(boolean isExt){
        try{
            isBiomatricExist=isExt;
             //sPref = getSharedPreferences("csp", Context.MODE_PRIVATE);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View customLayout = activity.getLayoutInflater().inflate(R.layout.thumb_impression_attachment, null);
            builder.setView(customLayout);
            AlertDialog dialogThumb = builder.create();
            RadioButton radioThumbHohAvailableYes=customLayout.findViewById(R.id.thumb_impression_hoh_yes_rb);
            RadioButton radioThumbHohAvailableNo=customLayout.findViewById(R.id.thumb_impression_hoh_no_rb);
            EditText thumbEt=customLayout.findViewById(R.id.thumb_et);
            thumbRemark=customLayout.findViewById(R.id.thumb_remarks_auto_comp);
            Button btnThumbImpressionAttach=customLayout.findViewById(R.id.thumb_impression_attach_btn);
            Button btnThumbContinue=customLayout.findViewById(R.id.thumb_continue_btn);
            thumbPic=customLayout.findViewById(R.id.thumb_pic_iv);
            ImageView delThumbPic=customLayout.findViewById(R.id.del_thumb_pic_iv);
            ImageView backThumbPic=customLayout.findViewById(R.id.backBtnThumbImpression);
            thumb_pic_items_fl=customLayout.findViewById(R.id.thumb_pic_items_fl);
            LinearLayout layThumb=customLayout.findViewById(R.id.layThumb);
            LinearLayout layRem=customLayout.findViewById(R.id.layRem);
            RecyclerView imageRecyclerDeleteThumb=customLayout.findViewById(R.id.imageRecyclerDeleteThumb);

            radioThumbHohAvailableYes.setChecked(false);
            layThumb.setVisibility(View.GONE);
            layRem.setVisibility(View.GONE);
            thumb_pic_items_fl.setVisibility(View.GONE);
            thumbRemark.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_thumb_remarks)));
            setFocusChange_OnTouch(thumbRemark);

            previousBoimetricDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.ThumbLable,unitInfoDataModel.getUnit_id());
            if (previousBoimetricDocument.size()>0) {
                isBiomatricExist = true;
                imgCaptured = true;

                ArrayList<String> arr=new ArrayList<>();
                for (int i=0;i<previousBoimetricDocument.get(0).getAttachmentItemLists().size();i++){
                    if(previousBoimetricDocument.get(0).getAttachmentItemLists().get(i).getItem_url().contains("http")){
                        arr.add(previousBoimetricDocument.get(0).getAttachmentItemLists().get(i).getItem_url());
                    }
                }
                if(arr.size()>0){
                    thumbAdapter=new PostCompleteImageAdapter(previousBoimetricDocument.get(0),arr,activity,MemberDetailsViewModel.this,MemberDetailsViewModel.this);
                    imageRecyclerDeleteThumb.setAdapter(thumbAdapter);
                    imageRecyclerDeleteThumb.setVisibility(View.VISIBLE);
                }
            }else{
                isBiomatricExist=false;
                imageRecyclerDeleteThumb.setVisibility(View.GONE);
            }
            if (isBiomatricExist){
                if (imgCaptured && previousBoimetricDocument.size()>0){
                    int index = previousBoimetricDocument.size()-1;
                    if (previousBoimetricDocument.get(index).getItem_url().contains("http")) {
//                        RequestOptions requestOptions = new RequestOptions();
//                        requestOptions.placeholder(R.drawable.img_place);
//                        requestOptions.error(R.drawable.img_place);
//
//                        GlideUrl glideUrl = new GlideUrl(previousBoimetricDocument.get(index).getItem_url() + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
//                                new LazyHeaders.Builder()
//                                        .addHeader("User-Agent", "drppl")
//                                        .build());
//                        Glide.with(activity).load(glideUrl).apply(requestOptions).into(thumbPic);
                    }else {
                        radioThumbHohAvailableYes.setChecked(true);
                        File imgFile = new File(previousBoimetricDocument.get(index).getItem_url());
                        CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                        Bitmap previewBitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                        imgCaptured = true;
                        thumbPicBitmap = previewBitmap;
                        thumbPic.setImageBitmap(previewBitmap);
                        thumb_pic_items_fl.setVisibility(View.VISIBLE);
                    }
//                    thumb_pic_items_fl.setVisibility(View.VISIBLE);
                    thumbRemark.setText(unitInfoDataModel.getThumb_remarks());
                    thumbRemark.setTag(unitInfoDataModel.getThumb_remarks());
                    thumbRemark.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_edittext));
                    thumbRemark.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.svg_keyboard_arrow_down_24, 0);
                    thumbRemark.setEnabled(true);
                    thumbRemark.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_thumb_remarks)));
                    setFocusChange_OnTouch(thumbRemark);
                    radioThumbHohAvailableYes.setChecked(true);
                    radioThumbHohAvailableNo.setChecked(false);
                    layThumb.setVisibility(View.VISIBLE);
                    layRem.setVisibility(View.VISIBLE);
                }
            }
            if (!imgCaptured && unitInfoDataModel.getThumb_remarks()!=null && unitInfoDataModel.getThumb_remarks().equalsIgnoreCase("Thumb Impression not provided")){
                radioThumbHohAvailableYes.setChecked(true);
                radioThumbHohAvailableNo.setChecked(false);
                thumbRemark.setText("Thumb Impression not provided");
                thumbRemark.setTag("Thumb Impression not provided");
                thumbRemark.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_thumb_remarks)));
                setFocusChange_OnTouch(thumbRemark);
                thumbRemark.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_edittext));
                thumbRemark.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.svg_keyboard_arrow_down_24, 0);
                thumbRemark.setEnabled(true);
                layThumb.setVisibility(View.VISIBLE);
                layRem.setVisibility(View.VISIBLE);
                thumb_pic_items_fl.setVisibility(View.GONE);
            }else if (!imgCaptured && unitInfoDataModel.getThumb_remarks()!=null && unitInfoDataModel.getThumb_remarks().equalsIgnoreCase("HOH not available")){
                radioThumbHohAvailableNo.setChecked(true);
                thumbRemark.setText("HOH not available");
                thumbRemark.setTag("HOH not available");
                thumbRemark.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                thumbRemark.setCompoundDrawables(null, null, null, null);
                thumbRemark.setEnabled(false);
                radioThumbHohAvailableYes.setChecked(false);
                thumb_pic_items_fl.setVisibility(View.GONE);
                layThumb.setVisibility(View.GONE);
                layRem.setVisibility(View.VISIBLE);
                thumb_pic_items_fl.setVisibility(View.GONE);
                thumbRemark.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_thumb_remarks)));
                setFocusChange_OnTouch(thumbRemark);
            }



            backThumbPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isBiomatricExist) {
                        if (imgFP != null)
                            imgFP.setImageDrawable(null);
                        if (thumbPic != null)
                            thumbPic.setImageDrawable(null);
                        isoTemplate = null;
                        thumb_pic_items_fl.setVisibility(View.GONE);
                        imgCaptured = false;
                    }
                    isPanchnamaExist = true;
                    dialogThumb.dismiss();
                    showPanchnamaDialog(isPanchnamaExist);
                }
            });

            radioThumbHohAvailableYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        thumbRemark.setText("");
                        thumbRemark.setTag("");
                        thumbRemark.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_edittext));
                        thumbRemark.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.svg_keyboard_arrow_down_24, 0);
                        thumbRemark.setEnabled(true);
                        radioThumbHohAvailableNo.setChecked(false);
                        layThumb.setVisibility(View.VISIBLE);
                        layRem.setVisibility(View.VISIBLE);
                        thumb_pic_items_fl.setVisibility(View.GONE);
                    }
                }
            });

            radioThumbHohAvailableNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        thumbRemark.setText("HOH not available");
                        thumbRemark.setTag("HOH not available");
                        thumbRemark.getBackground().setColorFilter(activity.getColor(R.color.fixEditTextColor), PorterDuff.Mode.SRC_ATOP);
                        thumbRemark.setCompoundDrawables(null, null, null, null);
                        thumbRemark.setEnabled(false);
                        radioThumbHohAvailableYes.setChecked(false);
                        thumb_pic_items_fl.setVisibility(View.GONE);
                        layThumb.setVisibility(View.GONE);
                        layRem.setVisibility(View.VISIBLE);
                        thumb_pic_items_fl.setVisibility(View.GONE);
                    }
                }
            });

            delThumbPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Bitmap image=  thumbPic.getDrawingCache();
//                    File thumbFile= saveBitmapImage(image);
//                    try {
//                        CryptoUtilsTest.encryptFileinAES(thumbFile, 2);
//                    } catch (CryptoException e) {
//                        throw new RuntimeException(e);
//                    }
                    if(thumbPic!=null){
                        thumbPic.setImageDrawable(null);
                    }
                    thumbPic=customLayout.findViewById(R.id.thumb_pic_iv);
                    isoTemplate=null;
                    thumb_pic_items_fl.setVisibility(View.GONE);
                    imgCaptured=false;
                    previousBoimetricDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.ThumbLable,unitInfoDataModel.getUnit_id());
                    if (previousBoimetricDocument!=null && previousBoimetricDocument.size()>0){
                       if(previousBoimetricDocument.get(0).getAttachmentItemLists()!=null && previousBoimetricDocument.get(0).getAttachmentItemLists().size()>0){
                           for(int i=0;i<previousBoimetricDocument.get(0).getAttachmentItemLists().size();i++){
                               if(!previousBoimetricDocument.get(0).getAttachmentItemLists().get(i).item_url.contains("http")){
                                   List<MediaInfoDataModel> pp = new ArrayList<>();
                                   pp.add(previousBoimetricDocument.get(0));
                                   if(previousBoimetricDocument.get(0).getAttachmentItemLists().size()==1){
                                       onAttachmentDeletedClicked(pp,2,0,null,"");
                                   }else{
                                       onAttachmentDeletedClicked(pp,1,0,null,previousBoimetricDocument.get(0).getAttachmentItemLists().get(i).item_url);
                                   }
                                   break;
                               }
                           }
//                        List<MediaInfoDataModel> pp = new ArrayList<>();
//                        pp.add(previousBoimetricDocument.get(0));
//                        onAttachmentDeletedClicked(pp,2,0,null,"");
//                        previousBoimetricDocument = new ArrayList<>();
//                        isBiomatricExist=false;
                       }
                    }
                    isBiomatricExist=false;
                }
            });

            thumbPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(activity, FullScreenImage.class);
                        thumbPic.buildDrawingCache();
                        Bitmap image=  thumbPic.getDrawingCache();
                        Bundle extras = new Bundle();
                        extras.putParcelable("imagebitmap", image);
                        if(previousBoimetricDocument!=null && previousBoimetricDocument.size()>0 && isBiomatricExist){
                            extras.putString("url",previousBoimetricDocument.get(previousBoimetricDocument.size()-1).getItem_url());
                        }
                        intent.putExtras(extras);
                        activity.startActivity(intent);
                }
            });

            btnThumbImpressionAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(thumbPic.getDrawable()!=null){
                            Utils.shortToast("Thumb Attachment Already Attached.Please remove existing attachment first.",activity);
                            return;
                        }
                        thumb_pic_items_fl.setVisibility(View.VISIBLE);
                        thumbPic.setVisibility(View.VISIBLE);
                        delThumbPic.setVisibility(View.VISIBLE);
                        thumbPicBitmap=null;
                        imgCaptured=false;
                        showCaptureThumbDialog();
                    }catch(Exception ex){
                        AppLog.logData(activity,ex.getMessage());
                        Utils.shortToast("Thumb scanner not found.",activity);
                    }
                }
            });

            btnThumbContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (previousBoimetricDocument.size()>0) {
                        int index = previousBoimetricDocument.size()-1;
                        previousBoimetricDocument.get(index).setDocument_remarks(thumbRemark.getText().toString());
                    }
                    System.out.println("text::"+thumbRemark.getText().toString());
                    System.out.println("imgCaptured::"+imgCaptured);

                    if(thumbRemark.getText().toString().trim().length()>0){
                        if(thumbRemark.getText().toString().equals("Thumb Impression provided")){
                            if(previousBoimetricDocument.size()<1 && imgCaptured==false){
                                Utils.shortToast("Please Provide Thumb Impression",activity);
                            }else {
                                try {
                                    dialogThumb.dismiss();
//                                    if(!isBiomatricExist){
                                        saveData(thumbPicBitmap, isoTemplate);
//                                    }else{
//                                        dialogThumb.dismiss();
//                                        showAnnexureDialog();
//                                    }
                                } catch (CryptoException e) {
                                    AppLog.logData(activity,e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            }

                        }else{
                            try {
                                dialogThumb.dismiss();
//                                if(!isBiomatricExist){
                                    saveData(thumbPicBitmap, isoTemplate);
//                                }else{
//                                    dialogThumb.dismiss();
//                                    showAnnexureDialog();
//                                }
                            } catch (CryptoException e) {
                                AppLog.logData(activity,e.getMessage());
                                throw new RuntimeException(e);
                            }
                        }


                    }else{
                        Utils.shortToast("Select Remark",activity);
                    }

                }
            });

            dialogThumb.setCancelable(false);
            dialogThumb.show();
        }catch(Exception e){
            AppLog.logData(activity,e.getMessage());
            AppLog.e(e.getMessage());
        }
    }

    private void showCaptureThumbDialog() {
        try {
            USBManager.getInstance().initialize(activity, "com.morpho.morphosample.USB_ACTION");
            // initFP();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            final View customLayout = activity.getLayoutInflater().inflate(R.layout.thumb_sensor_screen, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            imgFP = customLayout.findViewById(R.id.thumb_print_iv);
            dialog.setCancelable(true);
            dialog.show();
            initFP();
        } catch (Exception e) {
            AppLog.logData(activity, e.getMessage());
            AppLog.e(e.getMessage());
        }
    }

    private void capture () {
        //AppLog.logData(activity,"Capture function call");

        verify = false;
        try {
            fpSensorCap.startCapture(activity);
           // AppLog.logData(activity,"startCapture...");
        } catch (Exception e) {
            AppLog.logData(activity,"Exception in capture:"+e.getMessage());
            Log.e(this.getClass().toString(), "capture", e);
        }

    }

    public void initFP () {
        try {
           fpSensorCap = new MorphoTabletFPSensorDevice(this::updateImageView);
            android.util.Log.i("initFP", "Object Created");
           fpSensorCap.open(activity);
            android.util.Log.i("initFP", "Opened");
           fpSensorCap.setViewToUpdate(imgFP);
            capture();
        } catch (Exception ex) {
            AppLog.logData(activity, "Exception initFP:" + ex.getMessage());
            Utils.shortToast(ex.getMessage(), activity);
        }
    }

    @Override
    public void updateImageView ( final ImageView imgPreview,
                                  final Bitmap previewBitmap, final String message,
                                  final boolean flagComplete, final int captureError){
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
        thumb_pic_items_fl.setVisibility(View.VISIBLE);
        thumbPicBitmap=null;
        if (imgPreview != null) {
            imgCaptured=true;
            thumbPicBitmap=previewBitmap;
            imgPreview.setImageBitmap(previewBitmap);
            thumbPic.setImageBitmap(previewBitmap);
        }

        if (captureError == ErrorCodes.MORPHOERR_TIMEOUT) {
//                    tost = Toast.makeText(getApplicationContext(), "Capture Timeout", Toast.LENGTH_SHORT);
//                    tost.show();

            //        setButtonEnabled(true);
            Log.e(this.getClass().toString(), "Capture Timeout ErrorCodes = " + captureError);
            return;
        } else if (captureError == ErrorCodes.MORPHOERR_CMDE_ABORTED) {
            Log.e(this.getClass().toString(), "MORPHOERR_CMDE_ABORTED ErrorCodes = " + captureError);
            //          setButtonEnabled(true);
            return;
        }

        if (flagComplete && captureError == ErrorCodes.MORPHO_OK) {
            //            setButtonEnabled(true);
            //saveData(previewBitmap,fpSensorCap.templateBuffer);
            isoTemplate=fpSensorCap.templateBuffer;
            imgCaptured=true;
        }
//            }
//        });
    }

    /*
    Function to save to database. NOTE: The fingerprint has to be saved as a BLOB.
     */
    private void saveData(Bitmap thumbBitmap, byte[] _template) throws CryptoException {
       try{

           if(thumbPicBitmap==null){
//               MediaInfoDataModel thumbMediaInfo=new MediaInfoDataModel();
//               thumbMediaInfo.setDate(new java.util.Date());
//               thumbMediaInfo.setLastEditedDate(new java.util.Date());
//               thumbMediaInfo.setThumb_imp_content(null);
//               thumbMediaInfo.setDocument_remarks(thumbRemark.getText().toString());
//               thumbMediaInfo.setData_size((int) 0);
//               thumbMediaInfo.setDocument_category("Thumb_Impression");
//               thumbMediaInfo.setDocument_type("Thumb_Impression");
//               thumbMediaInfo.setFilename("");
//               thumbMediaInfo.setFile_upload_checked((short)0);
//               thumbMediaInfo.setRel_globalid(unitInfoDataModel.getRel_globalid());
//               thumbMediaInfo.setParent_unique_id(unitInfoDataModel.getUnit_unique_id());
//               thumbMediaInfo.setItem_url("");
//               thumbMediaInfo.setRelative_path(unitInfoDataModel.getRelative_path());
//               thumbMediaInfo.setParent_table_name("unit_info");
//               thumbMediaInfo.setLocal(true);
//               thumbMediaInfo.setUploadMediaCount(1);
//               ArrayList<String> uploadMediaList=new ArrayList<>();
//               uploadMediaList.add("");
//               thumbMediaInfo.setUploadMediaList(uploadMediaList);
//               List<AttachmentItemList> attachmentItemLists=new ArrayList<>();
//               //attachmentItemLists.add(new AttachmentItemList(0,thumbFile.getName(),thumbFile.getAbsolutePath(),false,false));
//               thumbMediaInfo.setAttachmentItemLists(attachmentItemLists);
//
//               List<MediaInfoDataModel> list=new ArrayList<MediaInfoDataModel>();
//               list.add(thumbMediaInfo);
//               localSurveyDbViewModel.insertAllMediaInfoPointData(list,activity);
               unitInfoDataModel.setThumb_remarks(thumbRemark.getText().toString());
               localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);

               ArrayList<MediaInfoDataModel> attach = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.ThumbLable,unitInfoDataModel.getUnit_id());

               if(attach != null && !attach.isEmpty()) {
                   localSurveyDbViewModel.setIsUploaded(attach.get(0).getParent_unique_id(), attach.get(0).getObejctId(), false);
                   localSurveyDbViewModel.setRemarksByMediaId(attach.get(0).getMediaId(), thumbRemark.getText().toString());
               }

               if(floorFlag){
                  showNamunaDialog();
               }else{
                   showAnnexureDialog();
               }
           }else{
               File thumbFile= saveBitmapImage(thumbBitmap);
               CryptoUtilsTest.encryptFileinAES(thumbFile, 1);
               if(thumbFile!=null){
                   List<MediaInfoDataModel> att=localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.ThumbLable,unitInfoDataModel.getUnit_id());
                   if(att.size()>0){
                       MediaInfoDataModel thumbMediaInfo= att.get(0);
                       thumbMediaInfo.setDate(new java.util.Date());
                       thumbMediaInfo.setLastEditedDate(new java.util.Date());
                       thumbMediaInfo.setThumb_imp_content(_template);
                       thumbMediaInfo.setDocument_remarks(thumbRemark.getText().toString());
                       thumbMediaInfo.setData_size((int) thumbFile.length());
                       thumbMediaInfo.setDocument_category("Thumb_Impression");
                       thumbMediaInfo.setDocument_type("Thumb_Impression");
                       thumbMediaInfo.setFilename(thumbFile.getName());
                       thumbMediaInfo.setFile_upload_checked((short)0);
                       thumbMediaInfo.setItem_url(thumbFile.getAbsolutePath());
                       if(thumbMediaInfo.isUploaded()){
                           thumbMediaInfo.setLocal(false);
                       }else{
                           thumbMediaInfo.setLocal(true);
                       }
                       thumbMediaInfo.setHaveDelete(false);
                       thumbMediaInfo.setUploaded(false);
                       thumbMediaInfo.setUploadMediaCount(2);

                       ArrayList<AttachmentItemList> attachmentItems = new ArrayList<>();
                       for (int i = 0; i < thumbMediaInfo.getAttachmentItemLists().size(); i++) {
                           attachmentItems.add(thumbMediaInfo.getAttachmentItemLists().get(i));
                       }

                           ArrayList<String> listImageDetails = new ArrayList<>();
                           listImageDetails.add(thumbFile.getAbsolutePath());
                           AttachmentItemList at = new AttachmentItemList(0,thumbFile.getName(),thumbFile.getAbsolutePath(),false,false);
                           attachmentItems.add(at);

                       thumbMediaInfo.setUploadMediaList(listImageDetails);
                       thumbMediaInfo.setAttachmentItemLists(attachmentItems);

                       List<MediaInfoDataModel> list=new ArrayList<MediaInfoDataModel>();
                       list.add(thumbMediaInfo);
                       localSurveyDbViewModel.insertAllMediaInfoPointData(list,activity);
                       unitInfoDataModel.setThumb_remarks(thumbRemark.getText().toString());
                       localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);

                       if(floorFlag){
                           showNamunaDialog();
                       }else{
                           showAnnexureDialog();
                       }

                   }else{
                       MediaInfoDataModel thumbMediaInfo=new MediaInfoDataModel();
                       thumbMediaInfo.setDate(new java.util.Date());
                       thumbMediaInfo.setLastEditedDate(new java.util.Date());
                       thumbMediaInfo.setThumb_imp_content(_template);
                       thumbMediaInfo.setDocument_remarks(thumbRemark.getText().toString());
                       thumbMediaInfo.setData_size((int) thumbFile.length());
                       thumbMediaInfo.setDocument_category("Thumb_Impression");
                       thumbMediaInfo.setDocument_type("Thumb_Impression");
                       thumbMediaInfo.setFilename(thumbFile.getName());
                       thumbMediaInfo.setFile_upload_checked((short)0);
                       thumbMediaInfo.setRel_globalid(unitInfoDataModel.getRel_globalid());
                       thumbMediaInfo.setParent_unique_id(unitInfoDataModel.getUnit_id());
                       thumbMediaInfo.setItem_url(thumbFile.getAbsolutePath());
                       thumbMediaInfo.setRelative_path(unitInfoDataModel.getRelative_path());
                       thumbMediaInfo.setParent_table_name("unit_info");
                       thumbMediaInfo.setLocal(true);
                       thumbMediaInfo.setUploadMediaCount(1);
                       ArrayList<String> uploadMediaList=new ArrayList<>();
                       uploadMediaList.add(thumbFile.getAbsolutePath());
                       thumbMediaInfo.setUploadMediaList(uploadMediaList);
                       List<AttachmentItemList> attachmentItemLists=new ArrayList<>();
                       attachmentItemLists.add(new AttachmentItemList(0,thumbFile.getName(),thumbFile.getAbsolutePath(),false,false));
                       thumbMediaInfo.setAttachmentItemLists(attachmentItemLists);

                       List<MediaInfoDataModel> list=new ArrayList<MediaInfoDataModel>();
                       list.add(thumbMediaInfo);
                       localSurveyDbViewModel.insertAllMediaInfoPointData(list,activity);
                       unitInfoDataModel.setThumb_remarks(thumbRemark.getText().toString());
                       localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                       if(floorFlag){
                           showNamunaDialog();
                       }else{
                           showAnnexureDialog();
                       }
                   }
               }else{
                   AppLog.d("Image File Not Created");
               }
           }
       }catch(Exception ex){
           AppLog.logData(activity,ex.getMessage());
         Utils.shortToast("Try again",activity);
       }


//        ContentValues values = new ContentValues();
//        values.put("id", cont);
//        values.put("lname", "test");
//        values.put("biodata", _template);

        //cont++;
    }

    private File saveBitmapImage(Bitmap image) {

        if(image == null) {
            AppLog.d("Image File Not Created");
            return null;
        }

        // #970
        String fileName = Utils.getAttachmentFileName("Thumb_Impression");
        File thumbFile = new File(activity.getExternalFilesDir("thumb"), fileName + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(thumbFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return thumbFile;
        } catch (FileNotFoundException e) {
           AppLog.d("File not found: " + e.getMessage());
        } catch (IOException e) {
            AppLog.d("Error accessing file: " + e.getMessage());
        }
        return null;
    }


    private void showPanchnamaDialog(boolean isPanchnama) {
        isPanchnamaExist=isPanchnama;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.upload_panchnama_document, null);
        builder.setView(customLayout);
        AlertDialog dialogPanchnama = builder.create();
        AutoCompleteTextView panchnamaRemarkDropdown=customLayout.findViewById(R.id.panchnama_remark_dropdown);
        ImageButton clickButton=customLayout.findViewById(R.id.clickButton);
        ImageView delImageView=customLayout.findViewById(R.id.delImageView);
        ImageView backBtnPanchnama=customLayout.findViewById(R.id.backBtnPanchnama);
        tempImageButton=customLayout.findViewById(R.id.panchnama_captured_document);
        tempImageLayout=customLayout.findViewById(R.id.tempImageLayout);
        RecyclerView imageRecyclerDelete=customLayout.findViewById(R.id.imageRecyclerDelete);
        EditText dcTitle=customLayout.findViewById(R.id.docTitle);
        LinearLayout addErrorLayout=customLayout.findViewById(R.id.addErrorLayout);
        TextView addErrorTextView=customLayout.findViewById(R.id.addErrorTextView);
        TextView uploadDocumentPanchnama=customLayout.findViewById(R.id.upload_document_panchnama);
        dcTitle.setText("Panchnama Form");
        panchnamaRemarkDropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_panchnama_remarks)));
        setFocusChange_OnTouch(panchnamaRemarkDropdown);

        viewDeleteAdapterDelete=new PostCompleteImageAdapter(null,new ArrayList<>(),activity,MemberDetailsViewModel.this,MemberDetailsViewModel.this);
        imageRecyclerDelete.setAdapter(viewDeleteAdapterDelete);

        previousPanchnamaDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
        if(previousPanchnamaDocument!=null && previousPanchnamaDocument.size()>0){
            isPanchnamaExist=true;
            if (isPanchnamaExist) {
                if (previousPanchnamaDocument != null && previousPanchnamaDocument.size() > 0) {
                    ArrayList<String> arr=new ArrayList<>();
                    for (int i=0;i<previousPanchnamaDocument.get(0).getAttachmentItemLists().size();i++){
                        arr.add(previousPanchnamaDocument.get(0).getAttachmentItemLists().get(i).getItem_url());
                        if(!previousPanchnamaDocument.get(0).getAttachmentItemLists().get(i).getItem_url().contains("http")){
                            globalPanchnamaPath=previousPanchnamaDocument.get(0).getAttachmentItemLists().get(i).getItem_url();
                        }
                    }
                    panchnamaRemarkDropdown.setText(unitInfoDataModel.getPanchnama_form_remarks());
                    panchnamaRemarkDropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_panchnama_remarks)));
                    setFocusChange_OnTouch(panchnamaRemarkDropdown);

                    tempImageLayout.setVisibility(View.VISIBLE);
                    viewDeleteAdapterDelete=new PostCompleteImageAdapter(previousPanchnamaDocument.get(0),arr,activity,MemberDetailsViewModel.this,MemberDetailsViewModel.this);
                    imageRecyclerDelete.setAdapter(viewDeleteAdapterDelete);
//                    int index = previousPanchnamaDocument.size()-1;
//                    globalPanchnamaPath = previousPanchnamaDocument.get(index).getItem_url();
//                    if (globalPanchnamaPath.contains("http")) {
//                        if (globalPanchnamaPath.contains(".pdf")){
//                            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file
//
//                            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
//                            Drawable res = activity.getResources().getDrawable(imageResource);
//                            tempImageButton.setImageDrawable(res);
//                        }else {
//                            RequestOptions requestOptions = new RequestOptions();
//                            requestOptions.placeholder(R.drawable.img_place);
//                            requestOptions.error(R.drawable.img_place);
//
//                            GlideUrl glideUrl = new GlideUrl(globalPanchnamaPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
//                                    new LazyHeaders.Builder()
//                                            .addHeader("User-Agent", "drppl")
//                                            .build());
//                            Glide.with(activity).load(glideUrl).apply(requestOptions).into(tempImageButton);
//                            //Glide.with(activity).setDefaultRequestOptions(requestOptions).load(globalPanchnamaPath +
//                                    //"?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(tempImageButton);
////                            Glide.with(activity).load(globalPanchnamaPath).into(tempImageButton);
//                        }
//                    }else {
//                        File imgFile = new File(globalPanchnamaPath);
//                        try {
//                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
//                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
//                            tempImageButton.setImageBitmap(bitmap);
//                        } catch (CryptoException e) {
//                            AppLog.logData(activity, e.getMessage());
//                            throw new RuntimeException(e);
//                        } catch (Exception e) {
//                            AppLog.logData(activity, e.getMessage());
//                            throw new RuntimeException(e);
//                        }
//                    }

//                    tempImageButton.setVisibility(View.VISIBLE);
//                    tempImageLayout.setVisibility(View.VISIBLE);
                } else {
                    panchnamaRemarkDropdown.setText("Panchnama not signed");
                    panchnamaRemarkDropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_panchnama_remarks)));
                    setFocusChange_OnTouch(panchnamaRemarkDropdown);
                }
            }
        }else{
            isPanchnamaExist=false;
        }

        backBtnPanchnama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = false;
                ArrayList<MediaInfoDataModel> aa = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
                if (!aa.isEmpty()) {
                    for (int i = 0; i < aa.get(0).getAttachmentItemLists().size(); i++) {
                        if (aa.get(0).getAttachmentItemLists().get(i).item_url.equals(globalPanchnamaPath)) {
                            b = true;
                        }
                    }
                }
                if ((!isPanchnamaExist) && !b) {
                    deleteMedia(globalPanchnamaPath, 1);
                    globalPanchnamaPath = "";
                    // tempImageButton.setImageBitmap(null);
                    // tempImageButton.setImageDrawable(null);
                    // tempImageButton.setImageResource(android.R.color.transparent);
                    // tempImageLayout.setVisibility(View.GONE);
                    // panchnamaRemarkDropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_panchnama_remarks)));
                    // setFocusChange_OnTouch(panchnamaRemarkDropdown);
                } else if (!globalPanchnamaPath.equals("") && !b) {
                    deleteMedia(globalPanchnamaPath, 1);
                    globalPanchnamaPath = "";
                }
                dialogPanchnama.dismiss();
                showWithoutOTPDialog(true);
            }
        });

        clickButton.setOnClickListener(v -> {
                // if(panchnamaRemarkDropdown.getText().toString().equals("")){
                //     addErrorLayout.setVisibility(View.VISIBLE);
                //     addErrorTextView.setText("Please select punchnama remarks");
                // }else{
                //     addErrorLayout.setVisibility(View.GONE);
                if(globalPanchnamaPath!=null && !globalPanchnamaPath.equals("")){
                    addErrorLayout.setVisibility(View.VISIBLE);
                    addErrorTextView.setText("Please remove existing punchnama to upload new one!");
                }else{
                    addErrorLayout.setVisibility(View.GONE);
                    // showAttachmentAlertDialogButtonClicked("Panchnama Form", unitInfoDataModel.getRelative_path(), unitInfoDataModel.getUnit_id() + "_" + "Panchnama Form" + "_" + Utils.getEpochDateStamp());
                    // #970
                    if(viewDeleteAdapterDelete==null){
                        viewDeleteAdapterDelete=new PostCompleteImageAdapter(null,new ArrayList<>(),activity,MemberDetailsViewModel.this,MemberDetailsViewModel.this);
                        imageRecyclerDelete.setAdapter(viewDeleteAdapterDelete);
                    }
                    String fileName = Utils.getAttachmentFileName("Panchnama");
                    showAttachmentAlertDialogButtonClicked("Panchnama Form", unitInfoDataModel.getRelative_path(), fileName);
                }
            // }
        });

        tempImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalPanchnamaPath.contains(".pdf") && !globalPanchnamaPath.contains("http")) {
                    File pdfPathFile;
                    try {
                        pdfPathFile=new File(globalPanchnamaPath);
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
                }else if (globalPanchnamaPath.contains(".pdf") && globalPanchnamaPath.contains("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(globalPanchnamaPath), "application/pdf");
                    activity.startActivity(browserIntent);
                }else{
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    tempImageButton.buildDrawingCache();
                    Bitmap image=  tempImageButton.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    extras.putString("url", globalPanchnamaPath);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });

        delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPanchnamaExist){
                    showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete this attachment?",
                            "Yes", "No", false,2,globalPanchnamaPath);
                }else{
                    showActionAlertDialogButtonClicked("Confirm the action", "Do you want to delete this attachment?",
                            "Yes", "No", false,1,globalPanchnamaPath);
                }
            }
        });


        uploadDocumentPanchnama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((panchnamaRemarkDropdown.getText().toString().equals("") || panchnamaRemarkDropdown.getText().toString().equalsIgnoreCase("Panchnama Signed")) || (!panchnamaRemarkDropdown.getText().toString().equalsIgnoreCase("Panchnama not signed") && (globalPanchnamaPath==null || globalPanchnamaPath.equals(""))) ){
                    if(!isPanchnamaExist && (globalPanchnamaPath==null || globalPanchnamaPath.equals(""))){
                        addErrorLayout.setVisibility(View.VISIBLE);
                        addErrorTextView.setText("Please upload punchnama document");
                    }else if(panchnamaRemarkDropdown.getText().toString().equals("")){
                        addErrorLayout.setVisibility(View.VISIBLE);
                        addErrorTextView.setText("Please select punchnama remarks");
                    }else{
                        addErrorLayout.setVisibility(View.GONE);
                        if((userAttachmentList!=null && !userAttachmentList.isEmpty()) || !isPanchnamaExist){
                            List<MediaInfoDataModel> ll =localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
                            if(!ll.isEmpty()){
                               alreadyObj(userAttachmentList.size(),panchnamaRemarkDropdown.getText().toString(),dialogPanchnama);
                            }else{
                                ArrayList<MediaInfoDataModel> attach = getmediaInfoDataList(userAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                                if (!attach.isEmpty()) {

                                    mediaInfoDataModels1 = new ArrayList<>();

                                    attach.get(0).setDocument_remarks(panchnamaRemarkDropdown.getText().toString());
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
                                    Utils.showProgress("Saving data...", memberActivity);
                                    unitInfoDataModel.setPanchnama_form_remarks(panchnamaRemarkDropdown.getText().toString());
                                    new Handler().postDelayed(() -> {
                                        localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                                        Utils.dismissProgress();
                                        dialogPanchnama.dismiss();
                                        showBiometricDialog(isBiomatricExist);
//                            showAnnexureDialog();
                                    }, 2000);
                                }
                            }
                        }
                        else{

                            ArrayList<MediaInfoDataModel> attach = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());

                            if(attach != null && !attach.isEmpty()) {
                                localSurveyDbViewModel.setIsUploaded(attach.get(0).getParent_unique_id(), attach.get(0).getObejctId(), false);
                                localSurveyDbViewModel.setRemarksByMediaId(attach.get(0).getMediaId(), panchnamaRemarkDropdown.getText().toString());
                            }

                            Utils.dismissProgress();
                            dialogPanchnama.dismiss();
                            showBiometricDialog(isBiomatricExist);
                        }
                    }
                }else{
                    addErrorLayout.setVisibility(View.GONE);
                    if(globalPanchnamaPath==null || globalPanchnamaPath.equals("")){
                        Utils.showProgress("Saving data...",memberActivity);
                        unitInfoDataModel.setPanchnama_form_remarks(panchnamaRemarkDropdown.getText().toString());

                        ArrayList<MediaInfoDataModel> attach = (ArrayList<MediaInfoDataModel>)
                                localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());

                        if(attach != null && !attach.isEmpty()) {
                            localSurveyDbViewModel.setIsUploaded(attach.get(0).getParent_unique_id(), attach.get(0).getObejctId(), false);
                            localSurveyDbViewModel.setRemarksByMediaId(attach.get(0).getMediaId(), panchnamaRemarkDropdown.getText().toString());
                        }

                        new Handler().postDelayed(() -> {
                            localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                            Utils.dismissProgress();
                            dialogPanchnama.dismiss();
                            showBiometricDialog(isBiomatricExist);
//                            showAnnexureDialog();
                        }, 2000);
                    }else{
                        if((userAttachmentList!=null && userAttachmentList.size()>0) || !isPanchnamaExist) {
                            List<MediaInfoDataModel> ll = localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
                            if (ll.size() > 0) {
                                alreadyObj(userAttachmentList.size(), panchnamaRemarkDropdown.getText().toString(), dialogPanchnama);
                            }else{
                                ArrayList<MediaInfoDataModel> attach = getmediaInfoDataList(userAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());

                                if (attach.size() > 0) {

                                    mediaInfoDataModels1 = new ArrayList<>();

                                    attach.get(0).setDocument_remarks(panchnamaRemarkDropdown.getText().toString());
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
                                    Utils.showProgress("Saving data...", memberActivity);
                                    unitInfoDataModel.setPanchnama_form_remarks(panchnamaRemarkDropdown.getText().toString());
                                    new Handler().postDelayed(() -> {
                                        localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                                        Utils.dismissProgress();
                                        dialogPanchnama.dismiss();
                                        showBiometricDialog(isBiomatricExist);
//                            showAnnexureDialog();
                                    }, 2000);
                                }else if(panchnamaRemarkDropdown.getText().toString().equalsIgnoreCase("Panchnama not signed")){
                                    Utils.showProgress("Saving data...",memberActivity);
                                    unitInfoDataModel.setPanchnama_form_remarks(panchnamaRemarkDropdown.getText().toString());
                                    new Handler().postDelayed(() -> {
                                        localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                                        Utils.dismissProgress();
                                        dialogPanchnama.dismiss();
                                        showBiometricDialog(isBiomatricExist);
//                            showAnnexureDialog();
                                    }, 2000);
                                }
                            }
                        }else{
                            ArrayList<MediaInfoDataModel> attach = getmediaInfoDataList(userAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                                    unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());

                            if (attach.size() > 0) {

                                mediaInfoDataModels1 = new ArrayList<>();

                                attach.get(0).setDocument_remarks(panchnamaRemarkDropdown.getText().toString());
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
                                Utils.showProgress("Saving data...", memberActivity);
                                unitInfoDataModel.setPanchnama_form_remarks(panchnamaRemarkDropdown.getText().toString());
                                new Handler().postDelayed(() -> {
                                    localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                                    Utils.dismissProgress();
                                    dialogPanchnama.dismiss();
                                    showBiometricDialog(isBiomatricExist);
//                            showAnnexureDialog();
                                }, 2000);
                            }else if(panchnamaRemarkDropdown.getText().toString().equalsIgnoreCase("Panchnama not signed")){
                                Utils.showProgress("Saving data...",memberActivity);
                                unitInfoDataModel.setPanchnama_form_remarks(panchnamaRemarkDropdown.getText().toString());
                                new Handler().postDelayed(() -> {
                                    localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                                    Utils.dismissProgress();
                                    dialogPanchnama.dismiss();
                                    showBiometricDialog(isBiomatricExist);
//                            showAnnexureDialog();
                                }, 2000);
                            }
                        }

                    }
                }
            }
        });

        dialogPanchnama.setCancelable(false);
        dialogPanchnama.show();
    }

    public void showActionAlertDialogButtonClicked(String header, String mssage, String btnYes, String btnNo, boolean toUplaod, int flag, String itemUrl) {

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
            deleteMedia(globalPanchnamaPath,1);
            globalPanchnamaPath="";
            tempImageButton.setImageBitmap(null);
            tempImageButton.setImageDrawable(null);
            tempImageButton.setImageResource(android.R.color.transparent);
            tempImageLayout.setVisibility(View.GONE);
            List<MediaInfoDataModel> pp = new ArrayList<>();
            previousPanchnamaDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
            if(previousPanchnamaDocument!=null && previousPanchnamaDocument.size()>0){
                pp.add(previousPanchnamaDocument.get(0));
                onAttachmentDeletedClicked(pp,2,0,null,"");
            }
            isPanchnamaExist=false;

//            if(flag==1 && !isPanchnamaExist){
//                deleteMedia(globalPanchnamaPath);
//                globalPanchnamaPath="";
//                tempImageButton.setImageBitmap(null);
//                tempImageButton.setImageDrawable(null);
//                tempImageButton.setImageResource(android.R.color.transparent);
//                tempImageLayout.setVisibility(View.GONE);
//            }else if(flag==2){
//                tempImageButton.setImageBitmap(null);
//                tempImageButton.setImageDrawable(null);
//                tempImageButton.setImageResource(android.R.color.transparent);
//                tempImageLayout.setVisibility(View.GONE);
//                globalPanchnamaPath="";
//                List<MediaInfoDataModel> pp = new ArrayList<>();
//                previousPanchnamaDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
//                pp.add(previousPanchnamaDocument.get(0));
//                onAttachmentDeletedClicked(pp,2,0,null,"");
//                isPanchnamaExist=false;
//            }
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


    public void showActionAlertDialogButtonClickedA(String header, String mssage, String btnYes, String btnNo, boolean toUplaod, int flag, String itemUrl) {

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
            annexureFlag++;
            isAnxDocumentDelete = true;
            if (flag == 1) {
                deleteMedia(globalAnnexureAPath, 2);
                annexureOneCapturedDocument.setImageBitmap(null);
                annexureOneCapturedDocument.setImageDrawable(null);
                annexureOneCapturedDocument.setImageResource(android.R.color.transparent);
                anexureOneLayout.setVisibility(View.GONE);
                List<MediaInfoDataModel> pp = new ArrayList<>();
                previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.AnnexureLable, unitInfoDataModel.getUnit_id());
                if (previousAnnexureDocument != null && !previousAnnexureDocument.isEmpty()) {
                    pp.add(previousAnnexureDocument.get(0));
                    if (annexureFlag == 1) {
                        onAttachmentDeletedClicked(pp, 1, 0, null, globalAnnexureAPath);
                    } else if (annexureFlag == 2) {
                        userAttachmentList.clear();
                        onAttachmentDeletedClicked(pp, 2, 0, null, "");
                    }
                }
                globalAnnexureAPath = "";
            }
            dialog.dismiss();
        });

        btn_no.setOnClickListener(view1 -> dialog.dismiss());

        img_close.setOnClickListener(view1 -> dialog.dismiss());

        dialog.show();
    }

    public void showActionAlertDialogButtonClickedB(String header, String mssage, String btnYes, String btnNo, boolean toUplaod, int flag, String itemUrl) {

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
            annexureFlag ++;
            isAnxDocumentDelete = true;
            if(flag==1){
                deleteMedia(globalAnnexureBPath,2);
                annexureTwoCapturedDocument.setImageBitmap(null);
                annexureTwoCapturedDocument.setImageDrawable(null);
                annexureTwoCapturedDocument.setImageResource(android.R.color.transparent);
                anexureTwoLayout.setVisibility(View.GONE);
                List<MediaInfoDataModel> pp = new ArrayList<>();
                previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.AnnexureLable, unitInfoDataModel.getUnit_id());
                if(previousAnnexureDocument!=null && previousAnnexureDocument.size()>0){
                    pp.add(previousAnnexureDocument.get(0));
                    if (annexureFlag == 1){
                        onAttachmentDeletedClicked(pp,1,0,null,globalAnnexureBPath);
                    }else if (annexureFlag == 2){
                        userAttachmentList.clear();
                        onAttachmentDeletedClicked(pp,2,0,null,"");
                    }
                }
                globalAnnexureBPath="";
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


    private void showAnnexureDialog() {
        cc = "unit";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.upload_annexure_document, null);
        builder.setView(customLayout);
        AlertDialog dialogAnnex = builder.create();
        RadioButton radioMemberAvailableYes=customLayout.findViewById(R.id.radio_memberAvailable_Yes);
        RadioButton radioMemberAvailableNo=customLayout.findViewById(R.id.radio_memberAvailable_No);
        AutoCompleteTextView annexure_remark_dropdown=customLayout.findViewById(R.id.annexure_remark_dropdown);
        LinearLayout radioYesLayout=customLayout.findViewById(R.id.radioYesLayout);
        LinearLayout radioNoLayout=customLayout.findViewById(R.id.radioNoLayout);
        TextView uploadDocumentAnnexure=customLayout.findViewById(R.id.upload_document_Annexure);

        ImageButton clickButtonA=customLayout.findViewById(R.id.clickButtonA);
        ImageButton clickButtonB=customLayout.findViewById(R.id.clickButtonB);
        annexureOneCapturedDocument=customLayout.findViewById(R.id.annexure_one_captured_document);
        annexureTwoCapturedDocument=customLayout.findViewById(R.id.annexure_two_captured_document);
        ImageView delOneImageView=customLayout.findViewById(R.id.delOneImageView);
        ImageView delTwoImageView=customLayout.findViewById(R.id.delTwoImageView);
        ImageView backAnnexDocument = customLayout.findViewById(R.id.backBtnAnnexDocuments);
        anexureOneLayout=customLayout.findViewById(R.id.anexureOneLayout);
        anexureTwoLayout=customLayout.findViewById(R.id.anexureTwoLayout);
        LinearLayout addErrorLayout=customLayout.findViewById(R.id.addErrorLayout);
        TextView addErrorTextView=customLayout.findViewById(R.id.addErrorTextView);
        RecyclerView imageRecyclerDeleteAnnexure=customLayout.findViewById(R.id.imageRecyclerDeleteAnnexure);


        if(floorFlag){
        EditText docTitle=customLayout.findViewById(R.id.docTitle);
        EditText secondHeading=customLayout.findViewById(R.id.secondHeading);
        TextView viewTab=customLayout.findViewById(R.id.viewTab);
        docTitle.setText("Namuna 1 Self Declaration");
            secondHeading.setText("Self-Attestation Declaration (Namuna 2)");
            viewTab.setText("Namuna Documents");
        }

        if(floorFlag){
            annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_namuna_remarks)));
        }else{
            if(unitInfoDataModel.getUnit_usage().equals("Religious") || unitInfoDataModel.getUnit_usage().equals("Amenities")){
                ArrayList<AutoCompleteModal> arr1=Utils.getDomianList(Constants.domain_annexure_remarks);
                ArrayList<AutoCompleteModal> brr=new ArrayList<>();
                for(int i=0;i<arr1.size();i++){
                    if(arr1.get(i).toString().contains("Annexures B") || arr1.get(i).toString().contains("Annexure B")){
                        brr.add(arr1.get(i));
                    }
                }
                annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, brr));
            }else{
                annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_annexure_remarks)));
            }
        }

        setFocusChange_OnTouch(annexure_remark_dropdown);
        // unitInfoDataModel.getAnnexure_remarks();
        previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.AnnexureLable, unitInfoDataModel.getUnit_id());
        if (previousAnnexureDocument.size()>0){

            ArrayList<String> arr=new ArrayList<>();
            for (int i=0;i<previousAnnexureDocument.get(0).getAttachmentItemLists().size();i++){
                if(previousAnnexureDocument.get(0).getAttachmentItemLists().get(i).getItem_url().contains("http")){
                    arr.add(previousAnnexureDocument.get(0).getAttachmentItemLists().get(i).getItem_url());
                }
            }
            if(arr.size()>0){
                thumbAdapter=new PostCompleteImageAdapter(previousAnnexureDocument.get(0),arr,activity,MemberDetailsViewModel.this,MemberDetailsViewModel.this);
                imageRecyclerDeleteAnnexure.setAdapter(thumbAdapter);
                imageRecyclerDeleteAnnexure.setVisibility(View.VISIBLE);
            }

            ArrayList<AttachmentItemList> userAttachmentListAnx = (ArrayList<AttachmentItemList>) previousAnnexureDocument.get(0).getAttachmentItemLists();
            if (userAttachmentListAnx.size()>=2){
                for (int i=0;i<userAttachmentListAnx.size();i++){
                    if (userAttachmentListAnx.get(i).fileName.contains("annexure_a") || userAttachmentListAnx.get(i).fileName.contains("Annexure A")){
                        if(!userAttachmentListAnx.get(i).isUpdated){
                            if(!userAttachmentListAnx.get(i).item_url.contains("http")){
                                globalAnnexureAPath = userAttachmentListAnx.get(i).item_url;
                            }

                        }
                    }else if (userAttachmentListAnx.get(i).fileName.contains("annexure_b")|| userAttachmentListAnx.get(i).fileName.contains("Annexure B")){
                        if(!userAttachmentListAnx.get(i).isUpdated){
                            if(!userAttachmentListAnx.get(i).item_url.contains("http")){
                                globalAnnexureBPath = userAttachmentListAnx.get(i).item_url;
                            }
                        }
                    }
//                    anexureOneLayout.setVisibility(View.VISIBLE);
//                    anexureTwoLayout.setVisibility(View.VISIBLE);

                    if (globalAnnexureAPath.contains("http")) {
                        if (globalAnnexureAPath.contains(".pdf")){
                            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

                            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
                            Drawable res = activity.getResources().getDrawable(imageResource);
                            annexureOneCapturedDocument.setImageDrawable(res);
                        }else {
                            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_place).error(R.drawable.img_place);

                            GlideUrl glideUrl = new GlideUrl(globalAnnexureAPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                                    new LazyHeaders.Builder().addHeader("User-Agent", "drppl").build());
                            Glide.with(activity).load(glideUrl).apply(requestOptions).into(annexureOneCapturedDocument);
                            // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(globalAnnexureAPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(annexureOneCapturedDocument);
//                            Glide.with(activity).load(globalPanchnamaPath).into(tempImageButton);
                        }
                    }else if(!globalAnnexureAPath.equals("")) {
                        File imgFile = new File(globalAnnexureAPath);
                        try {
                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                            anexureOneLayout.setVisibility(View.VISIBLE);
                            annexureOneCapturedDocument.setImageBitmap(bitmap);
                        } catch (CryptoException e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }

                    if (globalAnnexureBPath.contains("http")) {
                        if (globalAnnexureBPath.contains(".pdf")){
                            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

                            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
                            Drawable res = activity.getResources().getDrawable(imageResource);
                            annexureTwoCapturedDocument.setImageDrawable(res);
                        }else {
                            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_place).error(R.drawable.img_place);

                            GlideUrl glideUrl = new GlideUrl(globalAnnexureBPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                                    new LazyHeaders.Builder().addHeader("User-Agent", "drppl").build());
                            Glide.with(activity).load(glideUrl).apply(requestOptions).into(annexureTwoCapturedDocument);
                            // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(globalAnnexureBPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(annexureTwoCapturedDocument);
//                            Glide.with(activity).load(globalPanchnamaPath).into(tempImageButton);
                        }
                    }else if(!globalAnnexureBPath.equals("")) {
                        File imgFile = new File(globalAnnexureBPath);
                        try {
                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                            anexureTwoLayout.setVisibility(View.VISIBLE);
                            annexureTwoCapturedDocument.setImageBitmap(bitmap);
                        } catch (CryptoException e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            else if (userAttachmentListAnx.size()==1){
                if (userAttachmentListAnx.get(0).fileName.contains("annexure_a") || userAttachmentListAnx.get(0).fileName.contains("Annexure A")){
                    if(!userAttachmentListAnx.get(0).item_url.contains("http")){
                        globalAnnexureAPath = userAttachmentListAnx.get(0).item_url;
                    }
                    anexureOneLayout.setVisibility(View.VISIBLE);

                    if (globalAnnexureAPath.contains("http")) {
                        if (globalAnnexureAPath.contains(".pdf")){
                            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

                            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
                            Drawable res = activity.getResources().getDrawable(imageResource);
                            annexureOneCapturedDocument.setImageDrawable(res);
                        }else {
                            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_place).error(R.drawable.img_place);

                            GlideUrl glideUrl = new GlideUrl(globalAnnexureAPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                                    new LazyHeaders.Builder().addHeader("User-Agent", "drppl").build());
                            Glide.with(activity).load(glideUrl).apply(requestOptions).into(annexureOneCapturedDocument);
                            // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(globalAnnexureAPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(annexureOneCapturedDocument);
//                            Glide.with(activity).load(globalPanchnamaPath).into(tempImageButton);
                        }
                    }else if(!globalAnnexureAPath.equals("")) {
                        File imgFile = new File(globalAnnexureAPath);
                        try {
                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                            annexureOneCapturedDocument.setImageBitmap(bitmap);
                        } catch (CryptoException e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }else if (userAttachmentListAnx.get(0).fileName.contains("annexure_b")|| userAttachmentListAnx.get(0).fileName.contains("Annexure B")){
                    if(!userAttachmentListAnx.get(0).item_url.contains("http")){
                        globalAnnexureBPath = userAttachmentListAnx.get(0).item_url;
                    }

                    if (globalAnnexureBPath.contains("http")) {
                        if (globalAnnexureBPath.contains(".pdf")){
                            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

                            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
                            Drawable res = activity.getResources().getDrawable(imageResource);
                            annexureTwoCapturedDocument.setImageDrawable(res);
                        }else {
                            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_place).error(R.drawable.img_place);

                            GlideUrl glideUrl = new GlideUrl(globalAnnexureBPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                                    new LazyHeaders.Builder().addHeader("User-Agent", "drppl").build());
                            Glide.with(activity).load(glideUrl).apply(requestOptions).into(annexureTwoCapturedDocument);
                            // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(globalAnnexureBPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(annexureTwoCapturedDocument);
//                            Glide.with(activity).load(globalPanchnamaPath).into(tempImageButton);
                        }
                    }else if(!globalAnnexureBPath.equals("")) {
                        File imgFile = new File(globalAnnexureBPath);
                        try {
                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                            annexureTwoCapturedDocument.setImageBitmap(bitmap);
                        } catch (CryptoException e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }
                anexureTwoLayout.setVisibility(View.VISIBLE);
            }
            radioYesLayout.setVisibility(View.VISIBLE);
            radioNoLayout.setVisibility(View.GONE);
            annexure_remark_dropdown.setText(unitInfoDataModel.getAnnexure_remarks());

            if(floorFlag){
                annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_namuna_remarks)));
            }else{
                if(unitInfoDataModel.getUnit_usage().equals("Religious")  || unitInfoDataModel.getUnit_usage().equals("Amenities")){
                    ArrayList<AutoCompleteModal> arr1=Utils.getDomianList(Constants.domain_annexure_remarks);
                    ArrayList<AutoCompleteModal> brr=new ArrayList<>();
                    for(int i=0;i<arr1.size();i++){
                        if(arr1.get(i).toString().contains("Annexures B") || arr1.get(i).toString().contains("Annexure B")){
                            brr.add(arr1.get(i));
                        }
                    }
                    annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, brr));
                }else{
                    annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_annexure_remarks)));
                }
            }

            setFocusChange_OnTouch(annexure_remark_dropdown);
            radioMemberAvailableYes.setChecked(true);
            radioMemberAvailableNo.setChecked(false);
            uploadDocumentAnnexure.setVisibility(View.VISIBLE);
        }
        if(unitInfoDataModel.getAnnexure_remarks()!= null && unitInfoDataModel.getAnnexure_remarks().equalsIgnoreCase("HOH not available")) {
            radioMemberAvailableNo.setChecked(true);
            radioMemberAvailableYes.setChecked(false);
            radioYesLayout.setVisibility(View.GONE);
            radioNoLayout.setVisibility(View.VISIBLE);
            anexureOneLayout.setVisibility(View.GONE);
            anexureTwoLayout.setVisibility(View.GONE);
            uploadDocumentAnnexure.setVisibility(View.VISIBLE);
        }else if(unitInfoDataModel.getAnnexure_remarks()!= null){
            radioYesLayout.setVisibility(View.VISIBLE);
            radioNoLayout.setVisibility(View.GONE);
            annexure_remark_dropdown.setText(unitInfoDataModel.getAnnexure_remarks());

            if(floorFlag){
                annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_namuna_remarks)));
            }else{
                if(unitInfoDataModel.getUnit_usage().equals("Religious")  || unitInfoDataModel.getUnit_usage().equals("Amenities")){
                    ArrayList<AutoCompleteModal> arr1=Utils.getDomianList(Constants.domain_annexure_remarks);
                    ArrayList<AutoCompleteModal> brr=new ArrayList<>();
                    for(int i=0;i<arr1.size();i++){
                        if(arr1.get(i).toString().contains("Annexures B") || arr1.get(i).toString().contains("Annexure B")){
                            brr.add(arr1.get(i));
                        }
                    }
                    annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, brr));
                }else{
                    annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_annexure_remarks)));
                }
            }

            setFocusChange_OnTouch(annexure_remark_dropdown);
            radioMemberAvailableYes.setChecked(true);
            radioMemberAvailableNo.setChecked(false);
            uploadDocumentAnnexure.setVisibility(View.VISIBLE);
        }
        backAnnexDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (previousAnnexureDocument.isEmpty() || previousAnnexureDocument.size()==0) {
                        globalAnnexureAPath = "";
                        globalAnnexureBPath = "";
                        userAttachmentList.clear();
                    }
                    if (isAnxDocumentDelete && savedAnnexureDocument!=null && savedAnnexureDocument.size()>0) {
                        localSurveyDbViewModel.insertAllMediaInfoPointData(savedAnnexureDocument,activity);
                    }
                    isAnxDocumentDelete = false;
                    showBiometricDialog(isBiomatricExist);
                    dialogAnnex.dismiss();
                }catch (Exception ex){
                    ex.getMessage();
                }
            }
        });

        radioMemberAvailableYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioNoLayout.setVisibility(View.GONE);
                    radioYesLayout.setVisibility(View.VISIBLE);
                    uploadDocumentAnnexure.setVisibility(View.VISIBLE);
                }

            }
        });

        radioMemberAvailableNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    addErrorLayout.setVisibility(View.GONE);
                    anexureOneLayout.setVisibility(View.GONE);
                    anexureTwoLayout.setVisibility(View.GONE);
                    radioYesLayout.setVisibility(View.GONE);
                    uploadDocumentAnnexure.setVisibility(View.GONE);
                    radioNoLayout.setVisibility(View.VISIBLE);
                    uploadDocumentAnnexure.setVisibility(View.VISIBLE);
                    globalAnnexureAPath="";
                    globalAnnexureBPath="";
                }
            }
        });

        clickButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(globalAnnexureAPath!=null && !globalAnnexureAPath.equals("")){
                    addErrorLayout.setVisibility(View.VISIBLE);
                    if(floorFlag){
                        addErrorTextView.setText("Please remove exisiting namuna 1 to upload new one!");
                    }else{
                        addErrorTextView.setText("Please remove exisiting annexure A to upload new one!");
                    }
                }else{
                    addErrorLayout.setVisibility(View.GONE);
                    ann = 1;
//                    showAttachmentAlertDialogButtonClicked(Constants.AnnexureLableA, unitInfoDataModel.getRelative_path(), unitInfoDataModel.getUnit_id() + "_" + Constants.AnnexureLableA + "_" + Utils.getEpochDateStamp());
                    // #970
                    String fileName = Utils.getAttachmentFileName("Annexure_A");
                    showAttachmentAlertDialogButtonClicked(Constants.AnnexureLableA, unitInfoDataModel.getRelative_path(), fileName);
                }
            }
        });

        clickButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(globalAnnexureBPath!=null && !globalAnnexureBPath.equals("")){
                    addErrorLayout.setVisibility(View.VISIBLE);
                    if(floorFlag){
                        addErrorTextView.setText("Please remove exisiting namuna 2 to upload new one!");
                    }else{
                        addErrorTextView.setText("Please remove exisiting annexure B to upload new one!");
                    }

                }else{
                    addErrorLayout.setVisibility(View.GONE);
                    ann = 2;
//                    showAttachmentAlertDialogButtonClicked(Constants.AnnexureLableB, unitInfoDataModel.getRelative_path(), unitInfoDataModel.getUnit_id() + "_" + Constants.AnnexureLableB + "_" + Utils.getEpochDateStamp());
                    // #970
                    String fileName = Utils.getAttachmentFileName("Annexure_B");
                    showAttachmentAlertDialogButtonClicked(Constants.AnnexureLableB, unitInfoDataModel.getRelative_path(), fileName);
                }
            }
        });

        annexureOneCapturedDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalAnnexureAPath.contains(".pdf") && !globalAnnexureAPath.contains("http")) {
                    File pdfPathFile;
                    try {
                        pdfPathFile=new File(globalAnnexureAPath);
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
                }else if (globalAnnexureAPath.contains(".pdf") && globalAnnexureAPath.contains("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(globalAnnexureAPath), "application/pdf");
                    activity.startActivity(browserIntent);
                }else{
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    annexureOneCapturedDocument.buildDrawingCache();
                    Bitmap image=  annexureOneCapturedDocument.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    extras.putString("url", globalAnnexureAPath);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });

        annexureTwoCapturedDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalAnnexureBPath.contains(".pdf") && !globalAnnexureBPath.contains("http")) {
                    File pdfPathFile;
                    try {
                        pdfPathFile=new File(globalAnnexureBPath);
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
                }else if (globalAnnexureBPath.contains(".pdf") && globalAnnexureBPath.contains("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(globalAnnexureBPath), "application/pdf");
                    activity.startActivity(browserIntent);
                }else{
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    annexureTwoCapturedDocument.buildDrawingCache();
                    Bitmap image=  annexureTwoCapturedDocument.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    extras.putString("url", globalAnnexureBPath);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });

        delOneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionAlertDialogButtonClickedA("Confirm the action", "Do you want to delete this attachment?",
                        "Yes", "No", false,1,globalAnnexureAPath);
            }
        });
        delTwoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionAlertDialogButtonClickedB("Confirm the action", "Do you want to delete this attachment?",
                        "Yes", "No", false,1,globalAnnexureBPath);
            }
        });


        uploadDocumentAnnexure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addErrorLayout.setVisibility(View.GONE);
                if(radioMemberAvailableYes.isChecked()){
                    int a=-1;
                    if(annexure_remark_dropdown.getText().toString().equals("")){
                        addErrorLayout.setVisibility(View.VISIBLE);
                        if(floorFlag){
                            addErrorTextView.setText("Please select namuna documents detail from list");
                        }else{
                            addErrorTextView.setText("Please select annexure documents detail from list");
                        }

                        return;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Both annexures signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures A not signed")){
                        a=2;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures B not signed")){
                        a=3;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures A and B not signed")){
                        a=4;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexure A signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexure B signed")){
                        a=2;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures A signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures B signed")){
                        a=2;
                    }



                    //namuna
                    else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Both namuna signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 1 not signed")){
                        a=2;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 2 not signed")){
                        a=3;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 1 and 2 not signed")){
                        a=4;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 1 signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 2 signed")){
                        a=2;
                    }

                    else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna A not signed")){
                        a=2;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna B not signed")){
                        a=3;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna A and B not signed")){
                        a=4;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna A signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna B signed")){
                        a=2;
                    }


                    boolean b=false;
                    if(previousAnnexureDocument.size()<1){
                        b=true;
                    }else if(previousAnnexureDocument.get(0).getAttachmentItemLists()==null){
                        b=true;
                    }else if(previousAnnexureDocument.get(0).getAttachmentItemLists().size()<1){
                        b=true;
                    }
                    if(a==1){
                        boolean s=true;
                        if(unitInfoDataModel.getUnit_usage().equals("Religious")  || unitInfoDataModel.getUnit_usage().equals("Amenities")){
                            s=false;
                        }
                        if(s && b && (globalAnnexureAPath==null || globalAnnexureAPath.equals(""))){
                            addErrorLayout.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                addErrorTextView.setText("Please upload namuna 1 image");
                            }else{
                                addErrorTextView.setText("Please upload Annexure A image");
                            }
                            return;
                        }else if(!floorFlag && b && (globalAnnexureBPath==null || globalAnnexureBPath.equals(""))){
                            addErrorLayout.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                addErrorTextView.setText("Please upload namuna 2 image");
                            }else{
                                addErrorTextView.setText("Please upload Annexure B image");
                            }

                            return;
                        }

                    }else if(a==2){
                        if(!floorFlag && b && (globalAnnexureBPath==null || globalAnnexureBPath.equals(""))){
                            addErrorLayout.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                addErrorTextView.setText("Please upload namuna 2 image");
                            }else{
                                addErrorTextView.setText("Please upload Annexure B image");
                            }
                            return;
                        }

                    }else if(a==3){
                        if(!unitInfoDataModel.getUnit_usage().equals("Religious") && !unitInfoDataModel.getUnit_usage().equals("Amenities")){
                            if(b && (globalAnnexureAPath==null || globalAnnexureAPath.equals(""))){
                                addErrorLayout.setVisibility(View.VISIBLE);
                                if(floorFlag){
                                    addErrorTextView.setText("Please upload namuna 1 image");
                                }else{
                                    addErrorTextView.setText("Please upload Annexure A image");
                                }

                                return;
                            }
                        }

                    }else if(a==4){
                        Utils.showProgress("Saving data...",memberActivity);

                        ArrayList<MediaInfoDataModel> attach = (ArrayList<MediaInfoDataModel>)
                                localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.AnnexureLable,unitInfoDataModel.getUnit_id());

                        for(MediaInfoDataModel mediaInfoDataModel : attach){
                            localSurveyDbViewModel.setIsUploaded(mediaInfoDataModel.getParent_unique_id(), mediaInfoDataModel.getObejctId(), false);
                            localSurveyDbViewModel.setRemarksByMediaId(mediaInfoDataModel.getMediaId(), annexure_remark_dropdown.getText().toString());
                        }

                        unitInfoDataModel.setAnnexure_remarks(annexure_remark_dropdown.getText().toString());
                        unitInfoDataModel.setHoh_avaibility((short) 1);
                        unitInfoDataModel.setAnnexure_uploaded((short) 1);
                        new Handler().postDelayed(() -> {
                            localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
//                            Utils.dismissProgress();
//                            dialogAnnex.dismiss();
//                            showFormSubmitDialog();
                        }, 2000);
                    }
                    if(a!=-1){
                        Utils.showProgress("Saving data...",memberActivity);
                        addErrorLayout.setVisibility(View.GONE);
                        ArrayList<MediaInfoDataModel> attach = new ArrayList<>();
                        ArrayList<AttachmentItemList> attachmentItems = new ArrayList<>();
                        previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.AnnexureLable, unitInfoDataModel.getUnit_id());
                        if (previousAnnexureDocument.size()>0 && userAttachmentList.size()>0){
                            // previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.AnnexureLable, unitInfoDataModel.getUnit_id());
                            attach =previousAnnexureDocument;
                            if(attach.get(0).isUploaded()){
                                attach.get(0).setLocal(false);
                            }else{
                                attach.get(0).setLocal(true);
                            }
                            attach.get(0).setUploaded(false);
                            for (int i = 0; i < previousAnnexureDocument.get(0).getAttachmentItemLists().size(); i++) {
                                attachmentItems.add(previousAnnexureDocument.get(0).getAttachmentItemLists().get(i));
                            }
                        }else if (userAttachmentList.size()>0) {
                            attach = getmediaInfoDataList(userAttachmentList, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                                    unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid());
                            attach.get(0).setLocal(true);

                        }
                        if (attach.size() > 0) {

                            mediaInfoDataModels1 = new ArrayList<>();

                            attach.get(0).setDocument_remarks(annexure_remark_dropdown.getText().toString());
                            attach.get(0).setDocument_category(Constants.AnnexureLable);
                            attach.get(0).setDocument_type(Constants.AnnexureLable);
                            attach.get(0).setUploadMediaCount(userAttachmentList.size());
                            attach.get(0).setHaveDelete(attach.get(0).isHaveDelete());

                            ArrayList<String> listImageDetails = new ArrayList<>();
                            if (previousAnnexureDocument.size()>0 && userAttachmentList.size()>0){
                                if(previousAnnexureDocument.get(0).getAttachmentItemLists()!=null && previousAnnexureDocument.get(0).getAttachmentItemLists().size()>0){
                                    for (int i = 0; i < previousAnnexureDocument.get(0).getAttachmentItemLists().size(); i++) {
                                        if(!previousAnnexureDocument.get(0).getAttachmentItemLists().get(i).isDeleted && !previousAnnexureDocument.get(0).getAttachmentItemLists().get(i).isUpdated && !previousAnnexureDocument.get(0).getAttachmentItemLists().get(i).getItem_url().contains("http")){
                                            listImageDetails.add(previousAnnexureDocument.get(0).getAttachmentItemLists().get(i).getItem_url());
                                        }
                                    }
                                }
                            }

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
                            unitInfoDataModel.setHoh_avaibility((short) 1);
                            unitInfoDataModel.setAnnexure_uploaded((short) 1);
//                            unitInfoDataModel.setAnnexure_upload_date("");

                        }

                        ArrayList<MediaInfoDataModel> attachNew = (ArrayList<MediaInfoDataModel>)
                                localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.AnnexureLable,unitInfoDataModel.getUnit_id());

                        for(MediaInfoDataModel mediaInfoDataModel : attachNew){
                            localSurveyDbViewModel.setIsUploaded(mediaInfoDataModel.getParent_unique_id(), mediaInfoDataModel.getObejctId(), false);
                            localSurveyDbViewModel.setRemarksByMediaId(mediaInfoDataModel.getMediaId(), annexure_remark_dropdown.getText().toString());
                        }

                        isAnxDocumentDelete = false;
                        unitInfoDataModel.setAnnexure_remarks(annexure_remark_dropdown.getText().toString());
                        new Handler().postDelayed(() -> {
                            localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                            Utils.dismissProgress();
                            dialogAnnex.dismiss();
                            showFormSubmitDialog();
                        }, 2000);
                    }
                }else{
                    unitInfoDataModel.setAnnexure_remarks("HOH not available");
                    unitInfoDataModel.setHoh_avaibility((short) 0);
                    unitInfoDataModel.setAnnexure_uploaded((short) 0);
//                    unitInfoDataModel.setAnnexure_upload_date("");
                    new Handler().postDelayed(() -> {
                        localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                        Utils.dismissProgress();
                        dialogAnnex.dismiss();
                        showFormSubmitDialog();
                    }, 2000);
                }
            }
        });
        dialogAnnex.setCancelable(false);
        dialogAnnex.show();
    }

    private void deleteMedia(String globalPath,int pos){
        ArrayList<AttachmentListImageDetails> user=new ArrayList<>();
        for(int i=0;i<userAttachmentList.size();i++){
            if(!userAttachmentList.get(i).getFilePath().equals(globalPath)){
                user.add(userAttachmentList.get(i));
            }
        }
        userAttachmentList=user;
//            String unit_unique_id=unitInfoDataModel.getUnit_id();
//            localSurveyDbViewModel.updateByFileName(true, globalPath, unitInfoDataModel.getUnit_id());
//            List<AttachmentItemList> attachmentItemLists=new ArrayList<>();
//            ArrayList<String> uploadItemList =new ArrayList<>();
//            List<MediaInfoDataModel> mediaInfoDataModelList=localSurveyDbViewModel.getByItemUrl(unitInfoDataModel.getUnit_id(),globalPath);
//
//        if (mediaInfoDataModelList.get(0).getUploadMediaList() != null) {
//            ArrayList<String> aff = new ArrayList<>();
//            for (int i = 0; i < mediaInfoDataModelList.get(0).getUploadMediaList().size(); i++) {
//                if (!mediaInfoDataModelList.get(0).getUploadMediaList().get(i).toString().equals(globalPath)) {
//                    aff.add(mediaInfoDataModelList.get(0).getUploadMediaList().get(i).toString());
//                }
//            }
//            if (mediaInfoDataModelList.get(0).getObejctId().equals("")) {
//                localSurveyDbViewModel.uploadListByFileName(unit_unique_id, mediaInfoDataModelList.get(0).getFilename(), aff);
//            }
//
//        }
//
//        for (int i = 0; i < mediaInfoDataModelList.get(0).getAttachmentItemLists().size(); i++) {
//            if (mediaInfoDataModelList.get(0).getAttachmentItemLists().get(i).getItem_url().equals(globalPath)) {
//                mediaInfoDataModelList.get(0).getAttachmentItemLists().get(i).setIsDeleted(true);
//            }
//        }
//
//        if (mediaInfoDataModelList.get(0).getObejctId().equals("")) {
//            localSurveyDbViewModel.uploadAttListByFileName(unit_unique_id, mediaInfoDataModelList.get(0).getFilename(), mediaInfoDataModelList.get(0).getAttachmentItemLists());
//        }

        if(pos==1){
            ArrayList<MediaInfoDataModel> aa=(ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());
            ArrayList<String> itemList=new ArrayList<>();
            if(aa.size()>0){
                for(int i=0;i<aa.get(0).getAttachmentItemLists().size();i++){
                    if(!aa.get(0).getAttachmentItemLists().get(i).isDeleted){
                        itemList.add(aa.get(0).getAttachmentItemLists().get(i).getItem_url());
                    }
                }
                viewDeleteAdapterDelete.setUpdatedList(itemList);
            }else{
                viewDeleteAdapterDelete.setUpdatedList(itemList);
//                viewDeleteAdapterDelete=new PostCompleteImageAdapter(null,new ArrayList<>(),activity,MemberDetailsViewModel.this,MemberDetailsViewModel.this);
//                viewDeleteAdapterDelete.setUpdatedList(new ArrayList<>());
            }
        }
    }

    /*
    * Invoked whenever OS needs to kill the app process due to critical environment. OS allows us to save important data
    * before process death.
    * */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveFormDataInBundle(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            applyBundleDataToForm(savedInstanceState);
        }
    }
    /*
    * This method gets the member form data from savedInstanceState. In savedInstanceState the data is filled by
    * MemberDetailsViewModel.saveFormDataInBundle. It is used when user fills up member form & without clicking on submit
    * button user navigates to another screen. Here form is suppose to restore the form data when user comes back.
    * */

    private void applyBundleDataToForm(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            String member_id = savedInstanceState.getString(Constants.memberInfo_member_id);
            String relationWithHoh = savedInstanceState.getString(Constants.memberInfo_relationship);
            String relationship_with_hoh_other = savedInstanceState.getString(Constants.memberInfo_relationship_other);
            String member_name = savedInstanceState.getString(Constants.memberInfo_member_name);
            String marital_status = savedInstanceState.getString(Constants.memberInfo_marital_status);
            String marital_status_other = savedInstanceState.getString(Constants.memberInfo_marital_status_other);
            String spouse_count = savedInstanceState.getString(Constants.memberInfo_spouse_count);
            String spouse_name = savedInstanceState.getString(Constants.memberInfo_spouse_name);
            String dob = savedInstanceState.getString(Constants.memberInfo_dob);
            String age = savedInstanceState.getString(Constants.memberInfo_age);
            String gender = savedInstanceState.getString(Constants.memberInfo_gender);
            String mobNo = savedInstanceState.getString(Constants.memberInfo_contact_number);
            String staying_since_year = savedInstanceState.getString(Constants.memberInfo_staying_since_year);
            String aadharNo = savedInstanceState.getString(Constants.memberInfo_aadhar_no);
            String pan_no = savedInstanceState.getString(Constants.memberInfo_pan_no);
            String ration_card_no = savedInstanceState.getString(Constants.memberInfo_ration_card_no);
            String ration_card_colour = savedInstanceState.getString(Constants.memberInfo_ration_card_colour);
            short deathCertificate = savedInstanceState.getShort(Constants.death_certificate);
            String religion = savedInstanceState.getString(Constants.memberInfo_religion);
            String religion_other = savedInstanceState.getString(Constants.memberInfo_religion_other);
            String from_state = savedInstanceState.getString(Constants.memberInfo_from_state);
            String from_state_other = savedInstanceState.getString(Constants.memberInfo_from_state_other);
            String mother_tongue = savedInstanceState.getString(Constants.memberInfo_mother_tongue);
            String mother_tongue_other = savedInstanceState.getString(Constants.memberInfo_mother_tongue_other);
            String education = savedInstanceState.getString(Constants.memberInfo_education);
            String education_other = savedInstanceState.getString(Constants.memberInfo_education_other);
            String occupation = savedInstanceState.getString(Constants.memberInfo_occupation);
            String occupation_other = savedInstanceState.getString(Constants.memberInfo_occupation_other);
            String type_of_work = savedInstanceState.getString(Constants.memberInfo_type_of_work);
            String type_of_work_other = savedInstanceState.getString(Constants.memberInfo_type_of_work_other);
            String monthly_income = savedInstanceState.getString(Constants.memberInfo_monthly_income);
            String mode_of_transport = savedInstanceState.getString(Constants.memberInfo_mode_of_transport);
            String mode_of_transport_other = savedInstanceState.getString(Constants.memberInfo_mode_of_transport_other);
            String school_college_name_location = savedInstanceState.getString(Constants.memberInfo_school_college_name_location);
            String handicap = savedInstanceState.getString(Constants.memberInfo_handicap);
            String staying_with = savedInstanceState.getString(Constants.memberInfo_staying_with);
            String vehicle_owned_driven = savedInstanceState.getString(Constants.memberInfo_vehicle_owned_driven_type);
            String vehicle_owned_driven_other = savedInstanceState.getString(Constants.memberInfo_vehicle_owned_driven_other);
            String place_of_work = savedInstanceState.getString(Constants.memberInfo_place_of_work);

            if(marital_status.equals("Unmarried")) {
                memberBinding.spouseLayout.setVisibility(View.GONE);
                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
            } else if(marital_status.equals("Married") && gender.equals("Male")) {
                memberBinding.spouseLayout.setVisibility(View.VISIBLE);
                memberBinding.memberNumSpouseLayout.setVisibility(View.VISIBLE);
            } else if(marital_status.equals("Divorce")) {
                memberBinding.spouseLayout.setVisibility(View.VISIBLE);
                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
            } else {
                memberBinding.memberNumSpouseLayout.setVisibility(View.GONE);
                memberBinding.spouseLayout.setVisibility(View.VISIBLE);
            }

            memberUniqueId = member_id;

            memberBinding.autoCompRelationWitHOH.setText(relationWithHoh);
            if(relationWithHoh.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etRelationshipWithHohOther.setText(relationship_with_hoh_other);
                memberBinding.etRelationshipWithHohOther.setVisibility(View.VISIBLE);
            }

            memberBinding.etFirstNameMember.setText(member_name);

            memberBinding.autoCompMaritialStatus.setText(marital_status);
            if(marital_status.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etMaritalStatusOther.setText(marital_status_other);
                memberBinding.etMaritalStatusOther.setVisibility(View.VISIBLE);
            }

            memberBinding.etMemberNumberSpouse.setText(spouse_count);
            memberBinding.etNameSpouse.setText(spouse_name);
            memberBinding.etDob.setText(dob);
            memberBinding.etMemberAge.setText(age);
            switch (gender) {
                case "Male":
                    memberBinding.radioGenderMale.setChecked(true);
                    memberBinding.radioGenderFemale.setChecked(false);
                    memberBinding.radioGenderOther.setChecked(false);
                    break;
                case "Female":
                    memberBinding.radioGenderFemale.setChecked(true);
                    memberBinding.radioGenderMale.setChecked(false);
                    memberBinding.radioGenderOther.setChecked(false);
                    break;
                case "Transgender":
                    memberBinding.radioGenderFemale.setChecked(false);
                    memberBinding.radioGenderMale.setChecked(false);
                    memberBinding.radioGenderOther.setChecked(true);
                    break;
                default:
                    memberBinding.radioGenderFemale.setChecked(false);
                    memberBinding.radioGenderMale.setChecked(false);
                    memberBinding.radioGenderOther.setChecked(false);
            }
            memberBinding.etStayingYear.setText(staying_since_year);
//            memberBinding.etAadhaarNumber.setText(aadharNo);
            MaskedEditText.setMaskedText(memberBinding.etAadhaarNumber,aadharNo);
            memberBinding.etPanNumber.setText(pan_no);
            memberBinding.etRationCardNumber.setText(ration_card_no);
            memberBinding.autoCompRationColor.setTag(ration_card_colour);
            memberBinding.autoCompRationColor.setText(Utils.getTextByTag(Constants.domain_ration_card_color,ration_card_colour));

            switch (deathCertificate) {
                case 0:
                    memberBinding.radioMemberAvailableNo.setChecked(true);
                    memberBinding.radioMemberAvailableYes.setChecked(false);
                    break;
                case 1:
                    memberBinding.radioMemberAvailableNo.setChecked(false);
                    memberBinding.radioMemberAvailableYes.setChecked(true);
                    break;
                case 2:
                    memberBinding.radioMemberAvailableNo.setChecked(false);
                    memberBinding.radioMemberAvailableYes.setChecked(false);
                    break;
            }

            memberBinding.autoCompReligion.setTag(religion);
            memberBinding.autoCompReligion.setText(Utils.getTextByTag(Constants.domain_religion, religion));
            if(religion.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etReligionOther.setText(religion_other);
                memberBinding.etReligionOther.setVisibility(View.VISIBLE);
            }

            memberBinding.autoCompWhichState.setTag(from_state);
            memberBinding.autoCompWhichState.setText(Utils.getTextByTag(Constants.domain_state,from_state));
            if(from_state.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etWhichStateOther.setText(from_state_other);
                memberBinding.etWhichStateOther.setVisibility(View.VISIBLE);
            }

            memberBinding.autoCompMotherTongue.setTag(mother_tongue);
            memberBinding.autoCompMotherTongue.setText(Utils.getTextByTag(Constants.domain_mother_tongue,mother_tongue));
            if(mother_tongue.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etMotherTongueOther.setText(mother_tongue_other);
                memberBinding.etMotherTongueOther.setVisibility(View.VISIBLE);
            }

            memberBinding.autoCompEducation.setTag(education);
            memberBinding.autoCompEducation.setText(Utils.getTextByTag(Constants.domain_educational_qualification,education));
            if(education.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etEducationOther.setText(education_other);
                memberBinding.etEducationOther.setVisibility(View.VISIBLE);
            }

            memberBinding.autoCompOccupation.setTag(occupation);
            memberBinding.autoCompOccupation.setText(Utils.getTextByTag(Constants.domain_occupation,occupation));
            if(occupation.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etOccupationOther.setText(occupation_other);
                memberBinding.etOccupationOther.setVisibility(View.VISIBLE);
            }

            memberBinding.autoCompWorkType.setTag(type_of_work);
            memberBinding.autoCompWorkType.setText(Utils.getTextByTag(Constants.domain_work_type,type_of_work));
            if(type_of_work.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etTypeOfWorkOther.setText(type_of_work_other);
                memberBinding.etTypeOfWorkOther.setVisibility(View.VISIBLE);
            }

            memberBinding.etIncome.setText(monthly_income);
            memberBinding.etRespondentContact.setText(mobNo);

            memberBinding.autoCompTransport.setTag(mode_of_transport);
            memberBinding.autoCompTransport.setText(Utils.getTextByTag(Constants.domain_mode_of_transport,mode_of_transport));
            if(mode_of_transport.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etModeOfTransportOther.setText(mode_of_transport_other);
                memberBinding.etModeOfTransportOther.setVisibility(View.VISIBLE);
            }

            memberBinding.etSchoolCollegeLocation.setText(school_college_name_location);
            switch (handicap) {
                case "Yes":
                    memberBinding.radioHandicappedYes.setChecked(true);
                    memberBinding.radioHandicappedNo.setChecked(false);
                    break;
                case "No":
                    memberBinding.radioHandicappedYes.setChecked(false);
                    memberBinding.radioHandicappedNo.setChecked(true);
                    break;
                default:
                    memberBinding.radioHandicappedNo.setChecked(false);
                    memberBinding.radioHandicappedYes.setChecked(false);
            }
            switch (staying_with) {
                case "Family":
                    memberBinding.radioFamilyYes.setChecked(true);
                    memberBinding.radioIndividualNo.setChecked(false);
                    break;
                case "Individual":
                    memberBinding.radioIndividualNo.setChecked(true);
                    memberBinding.radioFamilyYes.setChecked(false);
                    break;
                default:
                    memberBinding.radioFamilyYes.setChecked(false);
                    memberBinding.radioIndividualNo.setChecked(false);
            }

            memberBinding.autoCompVehicleType.setTag(vehicle_owned_driven);
            memberBinding.autoCompVehicleType.setText(Utils.getTextByTag(Constants.domain_vehicle_owned_driven_type,vehicle_owned_driven));
            if(vehicle_owned_driven.equalsIgnoreCase(Constants.dropdown_others)) {
                memberBinding.etVehicleTypeOther.setText(vehicle_owned_driven_other);
                memberBinding.etVehicleTypeOther.setVisibility(View.VISIBLE);
            }

            memberBinding.autoCompWorkPlace.setText(place_of_work);
            memberBinding.autoCompWorkPlace.setText(Utils.getTextByTag(Constants.domain_place_of_work,place_of_work));

        }
    }

    /*
    * This method was created to save current form data inside outState object so when user navigates to other screen
    * without saving member form data using submit button it can restore it using outState object.
    *
    * Parameters:
    * outState: Bundle object to store form data inside it.
    * */
    private Bundle saveFormDataInBundle(Bundle outState) {
        setNullTag();
        outState.putString(Constants.memberInfo_member_id, memberUniqueId);
        outState.putString(Constants.memberInfo_relationship, memberBinding.autoCompRelationWitHOH.getText().toString());
        outState.putString(Constants.memberInfo_relationship_other, memberBinding.etRelationshipWithHohOther.getText().toString());
        outState.putString(Constants.memberInfo_member_name, memberBinding.etFirstNameMember.getText().toString());
        outState.putString(Constants.memberInfo_marital_status, memberBinding.autoCompMaritialStatus.getTag().toString());
        outState.putString(Constants.memberInfo_marital_status_other, memberBinding.etMaritalStatusOther.getText().toString());
        outState.putString(Constants.memberInfo_spouse_count, memberBinding.etMemberNumberSpouse.getText().toString());
        outState.putString(Constants.memberInfo_spouse_name, memberBinding.etNameSpouse.getText().toString());
        outState.putString(Constants.memberInfo_contact_number, memberBinding.etRespondentContact.getText().toString());
        outState.putString(Constants.memberInfo_dob, memberBinding.etDob.getText().toString());
        outState.putString(Constants.memberInfo_age, memberBinding.etMemberAge.getText().toString());
        outState.putString(Constants.memberInfo_gender, memberGender);
        outState.putString(Constants.memberInfo_staying_since_year, memberBinding.etStayingYear.getText().toString());
//        outState.putString(Constants.memberInfo_aadhar_no, memberBinding.etAadhaarNumber.getText().toString());
        outState.putString(Constants.memberInfo_aadhar_no, MaskedEditText.getOriginalText(memberBinding.etAadhaarNumber));
        outState.putString(Constants.memberInfo_pan_no, memberBinding.etPanNumber.getText().toString());
        outState.putString(Constants.memberInfo_ration_card_no, memberBinding.etRationCardNumber.getText().toString());
        outState.putString(Constants.memberInfo_ration_card_colour, memberBinding.autoCompRationColor.getTag().toString());
        outState.putShort(Constants.death_certificate, (short) deathCertificate);
        outState.putString(Constants.memberInfo_religion, memberBinding.autoCompReligion.getTag().toString());
        outState.putString(Constants.memberInfo_religion_other, memberBinding.etReligionOther.getText().toString());
        outState.putString(Constants.memberInfo_from_state, memberBinding.autoCompWhichState.getTag().toString());
        outState.putString(Constants.memberInfo_from_state_other, memberBinding.etWhichStateOther.getText().toString());
        outState.putString(Constants.memberInfo_mother_tongue, memberBinding.autoCompMotherTongue.getTag().toString());
        outState.putString(Constants.memberInfo_mother_tongue_other, memberBinding.etMotherTongueOther.getText().toString());
        outState.putString(Constants.memberInfo_education, memberBinding.autoCompEducation.getTag().toString());
        outState.putString(Constants.memberInfo_education_other, memberBinding.etEducationOther.getText().toString());
        outState.putString(Constants.memberInfo_occupation, memberBinding.autoCompOccupation.getTag().toString());
        outState.putString(Constants.memberInfo_occupation_other, memberBinding.etOccupationOther.getText().toString());
        outState.putString(Constants.memberInfo_type_of_work, memberBinding.autoCompWorkType.getTag().toString());
        outState.putString(Constants.memberInfo_type_of_work_other, memberBinding.etTypeOfWorkOther.getText().toString());
        outState.putString(Constants.memberInfo_monthly_income, memberBinding.etIncome.getText().toString());
        outState.putString(Constants.memberInfo_mode_of_transport, memberBinding.autoCompTransport.getTag().toString());
        outState.putString(Constants.memberInfo_mode_of_transport_other, memberBinding.etModeOfTransportOther.getText().toString());
        outState.putString(Constants.memberInfo_school_college_name_location, memberBinding.etSchoolCollegeLocation.getText().toString());
        outState.putString(Constants.memberInfo_handicap, handicapOrNot);
        outState.putString(Constants.memberInfo_staying_with, stayingwith);
        outState.putString(Constants.memberInfo_vehicle_owned_driven_type, memberBinding.autoCompVehicleType.getText().toString());
        outState.putString(Constants.memberInfo_vehicle_owned_driven_other, memberBinding.etVehicleTypeOther.getText().toString());
        outState.putString(Constants.memberInfo_place_of_work, memberBinding.autoCompWorkPlace.getText().toString());
        return outState;
    }

    /*
    * Invoked when back is pressed by user on member form & warns user that unsaved data will be lost.
    * */
    @Override
    public boolean onBackKeyPress() {
//        Bundle formDataToRestore = new Bundle();
//        saveFormDataInBundle(formDataToRestore);
//        memberActivity.startActivity(new Intent(memberActivity, HohActivity.class)
////                .putExtra(Constants.MEMBER_INFO_TO_RESTORE_FORM, formDataToRestore)
//                .putExtra(Constants.INTENT_DATA_HohInfo, hohInfoDataModel)
//                .putExtra(Constants.INTENT_DATA_StructureInfo, structureInfoPointDataModel)
//                .putExtra(Constants.INTENT_DATA_MamberInfo, existingMemberInfoDataModelAL)
//                .putExtra(Constants.INTENT_DATA_UnitInfo, unitInfoDataModel));
        closeFormPopup();
        return true;
    }

    /*
    * When user presses back button or cancel button which is stick to bottom screen this method get invoked.
    * */
    private void closeFormPopup() {
            YesNoBottomSheet.geInstance(memberActivity, memberActivity.getString(R.string.close_form_msg), memberActivity.getResources().getString(R.string.yesBtn), memberActivity.getResources().getString(R.string.noBtn), new YesNoBottomSheet.YesNoButton() {
                @Override
                public void yesBtn() {
//                    Bundle formDataToRestore = new Bundle();
//                    saveFormDataInBundle(formDataToRestore);
                    memberActivity.finish();
                }

                @Override
                public void noBtn() {

                }
            }).show(((AppCompatActivity) memberActivity).getSupportFragmentManager(), "");
    }

    private void setNullTag() {

        if (memberBinding.autoCompRelationWitHOH.getTag() == null) {
            memberBinding.autoCompRelationWitHOH.setTag("");
        }

        if (memberBinding.autoCompMaritialStatus.getTag() == null) {
            memberBinding.autoCompMaritialStatus.setTag("");
        }

        if (memberBinding.autoCompRationColor.getTag() == null) {
            memberBinding.autoCompRationColor.setTag("");
        }

        if (memberBinding.autoCompReligion.getTag() == null) {
            memberBinding.autoCompReligion.setTag("");
        }

        if (memberBinding.autoCompWhichState.getTag() == null) {
            memberBinding.autoCompWhichState.setTag("");
        }

        if (memberBinding.autoCompMotherTongue.getTag() == null) {
            memberBinding.autoCompMotherTongue.setTag("");
        }

        if (memberBinding.autoCompEducation.getTag() == null) {
            memberBinding.autoCompEducation.setTag("");
        }

        if (memberBinding.autoCompOccupation.getTag() == null) {
            memberBinding.autoCompOccupation.setTag("");
        }

        if (memberBinding.autoCompWorkPlace.getTag() == null) {
            memberBinding.autoCompWorkPlace.setTag("");
        }

        if (memberBinding.autoCompWorkType.getTag() == null) {
            memberBinding.autoCompWorkType.setTag("");
        }

        if (memberBinding.autoCompTransport.getTag() == null) {
            memberBinding.autoCompTransport.setTag("");
        }

        if (memberBinding.autoCompVehicleType.getTag() == null) {
            memberBinding.autoCompVehicleType.setTag("");
        }
    }

    public void startCountDown(TextView textView,TextView resendOTP, LinearLayout lay){

        try{

            new CountDownTimer(50000, 1000) {
                public void onTick(long millisUntilFinished) {
                    NumberFormat f = new DecimalFormat("00");
                    long sec = (millisUntilFinished / 1000) % 60;
                    textView.setText(f.format(sec));

                }
                // When the task is over it will print 00:00:00 there
                public void onFinish() {
                    textView.setText("00");
                    lay.setVisibility(View.GONE);
                    resendOTP.setVisibility(View.VISIBLE);
                }
            }.start();
        }catch(Exception ex){
            AppLog.e("Exception in count down::"+ex.getMessage());
            AppLog.logData(activity,"Exception in count down::"+ex.getMessage());
        }
    }

    public void resendOTP(){
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("receiver", etHohMobileForOTP.getText().toString());
        if(isRelAmen){
            jsonParams.put("hohName", unitInfoDataModel.getRespondent_name());
        }else{
            jsonParams.put("hohName", hohInfoDataModel.getHoh_name());
        }

        jsonParams.put("surveyId", unitInfoDataModel.getHut_number());
        jsonParams.put("relationShipManagerNo", "52654");
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());


        Utils.updateProgressMsg("Sending OTP, please wait..", activity);

        try{
            Api_Interface apiInterface = RetrofitService.getSMSClient().create(Api_Interface.class);
            Call<JsonElement> call = apiInterface.sendOTP(body);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                    Utils.dismissProgress();

                    if(response.code()==200 && response.body() != null){

                        try {

                            JSONObject resObj = new JSONObject(response.body().toString());

                            if(resObj.getJSONObject("status").getInt("status")==1 && resObj.getJSONObject("data")!=null ){
                                transactionID=resObj.getJSONObject("data").getString("transactionId");
                            }else{
                                Utils.shortToast(resObj.getJSONObject("status").getString("message"),activity);
                            }
                        } catch (JSONException e) {
                            //throw new RuntimeException(e);
                            Utils.shortToast("Unable to Send OTP Try Again.",activity);
                        }
                    }else{
                        Utils.shortToast("Unable to Send OTP Try Again.",activity);
                    }
//                    dialog.dismiss();
//                    showVerifyOTPPopup(transactionID);
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    call.cancel();
                }
            });

        }catch(Exception ex){
            AppLog.e(ex.getMessage());
        }
    }


    private void alreadyObj(int count, String remarks,AlertDialog dialogPanchnama){

        ArrayList<AttachmentItemList> attachmentItems = new ArrayList<>();
        ArrayList<MediaInfoDataModel> mm = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());

        ArrayList<MediaInfoDataModel> attach =(ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.PanchnamaLable, unitInfoDataModel.getUnit_id());

        if (attach != null && !attach.isEmpty()) {

            if(mm != null && !mm.isEmpty()) {
                for (int i = 0; i < mm.get(0).getAttachmentItemLists().size(); i++) {
                    attachmentItems.add(mm.get(0).getAttachmentItemLists().get(i));
                }
            }

            mediaInfoDataModels1 = new ArrayList<>();

            attach.get(0).setDocument_remarks(remarks);
            attach.get(0).setDocument_category("Panchnama Form");
            attach.get(0).setDocument_type("Panchnama Form");
            attach.get(0).setUploadMediaCount(count);
            attach.get(0).setLocal(false);
            attach.get(0).setHaveDelete(false);
            attach.get(0).setUploaded(false);

            ArrayList<String> listImageDetails = new ArrayList<>();
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

            unitInfoDataModel.setPanchnama_form_remarks(remarks);

            Utils.showProgress("Saving data...", memberActivity);

            new Handler().postDelayed(() -> {
                localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                Utils.dismissProgress();
                dialogPanchnama.dismiss();
                showBiometricDialog(isBiomatricExist);
            }, 2000);
        }
    }

    @Override
    public void onDelete(String itemUrl, MediaInfoDataModel model, int position) {
        deleteMedia(itemUrl,1);
        globalPanchnamaPath="";
//        if(itemUrl.contains("panchnama")){
//            List<MediaInfoDataModel> mm=new ArrayList<>();
//            mm.add(model);
//            onAttachmentDeletedClicked(mm,1,0,null,itemUrl);
//        }
    }


    private void showNamunaDialog() {
        cc = "unit";
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View customLayout = activity.getLayoutInflater().inflate(R.layout.upload_annexure_document, null);
        builder.setView(customLayout);
        AlertDialog dialogAnnex = builder.create();
        RadioButton radioMemberAvailableYes=customLayout.findViewById(R.id.radio_memberAvailable_Yes);
        RadioButton radioMemberAvailableNo=customLayout.findViewById(R.id.radio_memberAvailable_No);
        AutoCompleteTextView annexure_remark_dropdown=customLayout.findViewById(R.id.annexure_remark_dropdown);
        LinearLayout radioYesLayout=customLayout.findViewById(R.id.radioYesLayout);
        LinearLayout radioNoLayout=customLayout.findViewById(R.id.radioNoLayout);
        TextView uploadDocumentAnnexure=customLayout.findViewById(R.id.upload_document_Annexure);

        ImageButton clickButtonA=customLayout.findViewById(R.id.clickButtonA);
        ImageButton clickButtonB=customLayout.findViewById(R.id.clickButtonB);
        annexureOneCapturedDocument=customLayout.findViewById(R.id.annexure_one_captured_document);
        annexureTwoCapturedDocument=customLayout.findViewById(R.id.annexure_two_captured_document);
        ImageView delOneImageView=customLayout.findViewById(R.id.delOneImageView);
        ImageView delTwoImageView=customLayout.findViewById(R.id.delTwoImageView);
        ImageView backAnnexDocument = customLayout.findViewById(R.id.backBtnAnnexDocuments);
        anexureOneLayout=customLayout.findViewById(R.id.anexureOneLayout);
        anexureTwoLayout=customLayout.findViewById(R.id.anexureTwoLayout);
        LinearLayout addErrorLayout=customLayout.findViewById(R.id.addErrorLayout);
        TextView addErrorTextView=customLayout.findViewById(R.id.addErrorTextView);
        RecyclerView imageRecyclerDeleteAnnexure=customLayout.findViewById(R.id.imageRecyclerDeleteAnnexure);



            EditText docTitle=customLayout.findViewById(R.id.docTitle);
            EditText secondHeading=customLayout.findViewById(R.id.secondHeading);
            TextView viewTab=customLayout.findViewById(R.id.viewTab);
            docTitle.setText("Namuna 1 Self Declaration");
            secondHeading.setText("Self-Attestation Declaration (Namuna 2)");
            viewTab.setText("Namuna Documents");



            annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_namuna_remarks)));


        setFocusChange_OnTouch(annexure_remark_dropdown);
        // unitInfoDataModel.getAnnexure_remarks();
        previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.NamunaLable, unitInfoDataModel.getUnit_id());
        if (previousAnnexureDocument.size()>0){

            ArrayList<String> arr=new ArrayList<>();
            for(int i=0;i<previousAnnexureDocument.size();i++){
                for (int j=0;j<previousAnnexureDocument.get(i).getAttachmentItemLists().size();j++){
                    if(previousAnnexureDocument.get(i).getAttachmentItemLists().get(j).getItem_url().contains("http")){
                        arr.add(previousAnnexureDocument.get(i).getAttachmentItemLists().get(j).getItem_url());
                    }
                }
            }
//            for (int i=0;i<previousAnnexureDocument.get(0).getAttachmentItemLists().size();i++){
//                if(previousAnnexureDocument.get(0).getAttachmentItemLists().get(i).getItem_url().contains("http")){
//                    arr.add(previousAnnexureDocument.get(0).getAttachmentItemLists().get(i).getItem_url());
//                }
//            }
            if(arr.size()>0){
                thumbAdapter=new PostCompleteImageAdapter(previousAnnexureDocument.get(0),arr,activity,MemberDetailsViewModel.this,MemberDetailsViewModel.this);
                imageRecyclerDeleteAnnexure.setAdapter(thumbAdapter);
                imageRecyclerDeleteAnnexure.setVisibility(View.VISIBLE);
            }

            ArrayList<AttachmentItemList> userAttachmentListAnx=new ArrayList<>();
            for(int i=0;i<previousAnnexureDocument.size();i++){
                ArrayList<AttachmentItemList> uu=new ArrayList<>();
                uu = (ArrayList<AttachmentItemList>) previousAnnexureDocument.get(i).getAttachmentItemLists();
                userAttachmentListAnx.add(uu.get(0));
            }
//            ArrayList<AttachmentItemList> userAttachmentListAnx = (ArrayList<AttachmentItemList>) previousAnnexureDocument.get(0).getAttachmentItemLists();
            if (userAttachmentListAnx.size()>=2){
                for (int i=0;i<userAttachmentListAnx.size();i++){
                    if (userAttachmentListAnx.get(i).fileName.contains("namuna_1") || userAttachmentListAnx.get(i).fileName.contains("Namuna 1")){
                        if(!userAttachmentListAnx.get(i).isUpdated){
                            if(!userAttachmentListAnx.get(i).item_url.contains("http")){
                                globalAnnexureAPath = userAttachmentListAnx.get(i).item_url;
                            }

                        }
                    }else if (userAttachmentListAnx.get(i).fileName.contains("namuna_2")|| userAttachmentListAnx.get(i).fileName.contains("Namuna 2")){
                        if(!userAttachmentListAnx.get(i).isUpdated){
                            if(!userAttachmentListAnx.get(i).item_url.contains("http")){
                                globalAnnexureBPath = userAttachmentListAnx.get(i).item_url;
                            }
                        }
                    }
//                    anexureOneLayout.setVisibility(View.VISIBLE);
//                    anexureTwoLayout.setVisibility(View.VISIBLE);

                    if (globalAnnexureAPath.contains("http")) {
                        if (globalAnnexureAPath.contains(".pdf")){
                            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

                            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
                            Drawable res = activity.getResources().getDrawable(imageResource);
                            annexureOneCapturedDocument.setImageDrawable(res);
                        }else {
                            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_place).error(R.drawable.img_place);

                            GlideUrl glideUrl = new GlideUrl(globalAnnexureAPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                                    new LazyHeaders.Builder().addHeader("User-Agent", "drppl").build());
                            Glide.with(activity).load(glideUrl).apply(requestOptions).into(annexureOneCapturedDocument);
                            // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(globalAnnexureAPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(annexureOneCapturedDocument);
//                            Glide.with(activity).load(globalPanchnamaPath).into(tempImageButton);
                        }
                    }else if(!globalAnnexureAPath.equals("")) {
                        File imgFile = new File(globalAnnexureAPath);
                        try {
                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                            anexureOneLayout.setVisibility(View.VISIBLE);
                            annexureOneCapturedDocument.setImageBitmap(bitmap);
                        } catch (CryptoException e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }

                    if (globalAnnexureBPath.contains("http")) {
                        if (globalAnnexureBPath.contains(".pdf")){
                            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

                            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
                            Drawable res = activity.getResources().getDrawable(imageResource);
                            annexureTwoCapturedDocument.setImageDrawable(res);
                        }else {
                            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_place).error(R.drawable.img_place);

                            GlideUrl glideUrl = new GlideUrl(globalAnnexureBPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                                    new LazyHeaders.Builder().addHeader("User-Agent", "drppl").build());
                            Glide.with(activity).load(glideUrl).apply(requestOptions).into(annexureTwoCapturedDocument);
                            // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(globalAnnexureBPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(annexureTwoCapturedDocument);
//                            Glide.with(activity).load(globalPanchnamaPath).into(tempImageButton);
                        }
                    }else if(!globalAnnexureBPath.equals("")) {
                        File imgFile = new File(globalAnnexureBPath);
                        try {
                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                            anexureTwoLayout.setVisibility(View.VISIBLE);
                            annexureTwoCapturedDocument.setImageBitmap(bitmap);
                        } catch (CryptoException e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            else if (userAttachmentListAnx.size()==1){
                if (userAttachmentListAnx.get(0).fileName.contains("namuna_1") || userAttachmentListAnx.get(0).fileName.contains("Namuna 1")){
                    if(!userAttachmentListAnx.get(0).item_url.contains("http")){
                        globalAnnexureAPath = userAttachmentListAnx.get(0).item_url;
                    }
                    anexureOneLayout.setVisibility(View.VISIBLE);

                    if (globalAnnexureAPath.contains("http")) {
                        if (globalAnnexureAPath.contains(".pdf")){
                            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

                            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
                            Drawable res = activity.getResources().getDrawable(imageResource);
                            annexureOneCapturedDocument.setImageDrawable(res);
                        }else {
                            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_place).error(R.drawable.img_place);

                            GlideUrl glideUrl = new GlideUrl(globalAnnexureAPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                                    new LazyHeaders.Builder().addHeader("User-Agent", "drppl").build());
                            Glide.with(activity).load(glideUrl).apply(requestOptions).into(annexureOneCapturedDocument);
                            // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(globalAnnexureAPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(annexureOneCapturedDocument);
//                            Glide.with(activity).load(globalPanchnamaPath).into(tempImageButton);
                        }
                    }else if(!globalAnnexureAPath.equals("")) {
                        File imgFile = new File(globalAnnexureAPath);
                        try {
                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                            annexureOneCapturedDocument.setImageBitmap(bitmap);
                        } catch (CryptoException e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }else if (userAttachmentListAnx.get(0).fileName.contains("namuna_2")|| userAttachmentListAnx.get(0).fileName.contains("Namuna 2")){
                    if(!userAttachmentListAnx.get(0).item_url.contains("http")){
                        globalAnnexureBPath = userAttachmentListAnx.get(0).item_url;
                    }

                    if (globalAnnexureBPath.contains("http")) {
                        if (globalAnnexureBPath.contains(".pdf")){
                            String uri = "@drawable/icon_pdf";  // where myresource (without the extension) is the file

                            int imageResource = activity.getResources().getIdentifier(uri, null, activity.getPackageName());
                            Drawable res = activity.getResources().getDrawable(imageResource);
                            annexureTwoCapturedDocument.setImageDrawable(res);
                        }else {
                            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.img_place).error(R.drawable.img_place);

                            GlideUrl glideUrl = new GlideUrl(globalAnnexureBPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken(),
                                    new LazyHeaders.Builder().addHeader("User-Agent", "drppl").build());
                            Glide.with(activity).load(glideUrl).apply(requestOptions).into(annexureTwoCapturedDocument);
                            // Glide.with(activity).setDefaultRequestOptions(requestOptions).load(globalAnnexureBPath + "?token=" + App.getSharedPreferencesHandler().getEsriToken()).into(annexureTwoCapturedDocument);
//                            Glide.with(activity).load(globalPanchnamaPath).into(tempImageButton);
                        }
                    }else if(!globalAnnexureBPath.equals("")) {
                        File imgFile = new File(globalAnnexureBPath);
                        try {
                            CryptoUtilsTest.encryptFileinAES(imgFile, 2);
                            Bitmap bitmap = CorrectImageRotation.handleSamplingAndRotationBitmap(activity, Uri.fromFile(imgFile));
                            annexureTwoCapturedDocument.setImageBitmap(bitmap);
                        } catch (CryptoException e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        } catch (Exception e) {
                            AppLog.logData(activity, e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }
                anexureTwoLayout.setVisibility(View.VISIBLE);
            }
            radioYesLayout.setVisibility(View.VISIBLE);
            radioNoLayout.setVisibility(View.GONE);
            annexure_remark_dropdown.setText(unitInfoDataModel.getAnnexure_remarks());

            if(floorFlag){
                annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_namuna_remarks)));
            }

            setFocusChange_OnTouch(annexure_remark_dropdown);
            radioMemberAvailableYes.setChecked(true);
            radioMemberAvailableNo.setChecked(false);
            uploadDocumentAnnexure.setVisibility(View.VISIBLE);
        }
        if(unitInfoDataModel.getAnnexure_remarks()!= null && unitInfoDataModel.getAnnexure_remarks().equalsIgnoreCase("HOH not available")) {
            radioMemberAvailableNo.setChecked(true);
            radioMemberAvailableYes.setChecked(false);
            radioYesLayout.setVisibility(View.GONE);
            radioNoLayout.setVisibility(View.VISIBLE);
            anexureOneLayout.setVisibility(View.GONE);
            anexureTwoLayout.setVisibility(View.GONE);
            uploadDocumentAnnexure.setVisibility(View.VISIBLE);
        }else if(unitInfoDataModel.getAnnexure_remarks()!= null){
            radioYesLayout.setVisibility(View.VISIBLE);
            radioNoLayout.setVisibility(View.GONE);
            annexure_remark_dropdown.setText(unitInfoDataModel.getAnnexure_remarks());

            if(floorFlag){
                annexure_remark_dropdown.setAdapter(new ArrayAdapter<>(activity, R.layout.list_item, Utils.getDomianList(Constants.domain_namuna_remarks)));
            }

            setFocusChange_OnTouch(annexure_remark_dropdown);
            radioMemberAvailableYes.setChecked(true);
            radioMemberAvailableNo.setChecked(false);
            uploadDocumentAnnexure.setVisibility(View.VISIBLE);
        }
        backAnnexDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (previousAnnexureDocument.isEmpty() || previousAnnexureDocument.size()==0) {
                        globalAnnexureAPath = "";
                        globalAnnexureBPath = "";
                        userAttachmentList.clear();
                    }
                    if (isAnxDocumentDelete && savedAnnexureDocument!=null && savedAnnexureDocument.size()>0) {
                        localSurveyDbViewModel.insertAllMediaInfoPointData(savedAnnexureDocument,activity);
                    }
                    isAnxDocumentDelete = false;
                    showBiometricDialog(isBiomatricExist);
                    dialogAnnex.dismiss();
                }catch (Exception ex){
                    ex.getMessage();
                }
            }
        });

        radioMemberAvailableYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioNoLayout.setVisibility(View.GONE);
                    radioYesLayout.setVisibility(View.VISIBLE);
                    uploadDocumentAnnexure.setVisibility(View.VISIBLE);
                }

            }
        });

        radioMemberAvailableNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    addErrorLayout.setVisibility(View.GONE);
                    anexureOneLayout.setVisibility(View.GONE);
                    anexureTwoLayout.setVisibility(View.GONE);
                    radioYesLayout.setVisibility(View.GONE);
                    uploadDocumentAnnexure.setVisibility(View.GONE);
                    radioNoLayout.setVisibility(View.VISIBLE);
                    uploadDocumentAnnexure.setVisibility(View.VISIBLE);
                    globalAnnexureAPath="";
                    globalAnnexureBPath="";
                }
            }
        });

        clickButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(globalAnnexureAPath!=null && !globalAnnexureAPath.equals("")){
                    addErrorLayout.setVisibility(View.VISIBLE);
                    if(floorFlag){
                        addErrorTextView.setText("Please remove exisiting namuna 1 to upload new one!");
                    }else{
                        addErrorTextView.setText("Please remove exisiting annexure A to upload new one!");
                    }
                }else{
                    addErrorLayout.setVisibility(View.GONE);
                    ann = 1;
//                    showAttachmentAlertDialogButtonClicked(Constants.AnnexureLableA, unitInfoDataModel.getRelative_path(), unitInfoDataModel.getUnit_id() + "_" + Constants.AnnexureLableA + "_" + Utils.getEpochDateStamp());
                    // #970
                    String fileName = Utils.getAttachmentFileName("Namuna_1");
                    showAttachmentAlertDialogButtonClicked(Constants.NamunaLableA, unitInfoDataModel.getRelative_path(), fileName);
                }
            }
        });

        clickButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(globalAnnexureBPath!=null && !globalAnnexureBPath.equals("")){
                    addErrorLayout.setVisibility(View.VISIBLE);
                    if(floorFlag){
                        addErrorTextView.setText("Please remove exisiting namuna 2 to upload new one!");
                    }else{
                        addErrorTextView.setText("Please remove exisiting annexure B to upload new one!");
                    }

                }else{
                    addErrorLayout.setVisibility(View.GONE);
                    ann = 2;
//                    showAttachmentAlertDialogButtonClicked(Constants.AnnexureLableB, unitInfoDataModel.getRelative_path(), unitInfoDataModel.getUnit_id() + "_" + Constants.AnnexureLableB + "_" + Utils.getEpochDateStamp());
                    // #970
                    String fileName = Utils.getAttachmentFileName("Namuna_2");
                    showAttachmentAlertDialogButtonClicked(Constants.NamunaLableB, unitInfoDataModel.getRelative_path(), fileName);
                }
            }
        });

        annexureOneCapturedDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalAnnexureAPath.contains(".pdf") && !globalAnnexureAPath.contains("http")) {
                    File pdfPathFile;
                    try {
                        pdfPathFile=new File(globalAnnexureAPath);
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
                }else if (globalAnnexureAPath.contains(".pdf") && globalAnnexureAPath.contains("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(globalAnnexureAPath), "application/pdf");
                    activity.startActivity(browserIntent);
                }else{
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    annexureOneCapturedDocument.buildDrawingCache();
                    Bitmap image=  annexureOneCapturedDocument.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    extras.putString("url", globalAnnexureAPath);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });

        annexureTwoCapturedDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalAnnexureBPath.contains(".pdf") && !globalAnnexureBPath.contains("http")) {
                    File pdfPathFile;
                    try {
                        pdfPathFile=new File(globalAnnexureBPath);
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
                }else if (globalAnnexureBPath.contains(".pdf") && globalAnnexureBPath.contains("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setDataAndType(Uri.parse(globalAnnexureBPath), "application/pdf");
                    activity.startActivity(browserIntent);
                }else{
                    Intent intent = new Intent(activity, FullScreenImage.class);
                    annexureTwoCapturedDocument.buildDrawingCache();
                    Bitmap image=  annexureTwoCapturedDocument.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);
                    extras.putString("url", globalAnnexureBPath);
                    intent.putExtras(extras);
                    activity.startActivity(intent);
                }
            }
        });

        delOneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionAlertDialogButtonClickedANamuna("Confirm the action", "Do you want to delete this attachment?",
                        "Yes", "No", false,1,globalAnnexureAPath);
            }
        });
        delTwoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionAlertDialogButtonClickedBNamuna("Confirm the action", "Do you want to delete this attachment?",
                        "Yes", "No", false,1,globalAnnexureBPath);
            }
        });


        uploadDocumentAnnexure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addErrorLayout.setVisibility(View.GONE);
                if(radioMemberAvailableYes.isChecked()){
                    int a=-1;
                    if(annexure_remark_dropdown.getText().toString().equals("")){
                        addErrorLayout.setVisibility(View.VISIBLE);
                        if(floorFlag){
                            addErrorTextView.setText("Please select namuna documents detail from list");
                        }else{
                            addErrorTextView.setText("Please select annexure documents detail from list");
                        }

                        return;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Both annexures signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures A not signed")){
                        a=2;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures B not signed")){
                        a=3;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures A and B not signed")){
                        a=4;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexure A signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexure B signed")){
                        a=2;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures A signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Annexures B signed")){
                        a=2;
                    }



                    //namuna
                    else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("Both namuna signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 1 not signed")){
                        a=2;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 2 not signed")){
                        a=3;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 1 and 2 not signed")){
                        a=4;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 1 signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna 2 signed")){
                        a=2;
                    }

                    else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna A not signed")){
                        a=2;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna B not signed")){
                        a=3;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna A and B not signed")){
                        a=4;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna A signed")){
                        a=1;
                    }else if(annexure_remark_dropdown.getText().toString().equalsIgnoreCase("namuna B signed")){
                        a=2;
                    }


                    boolean b=false;
                    if(previousAnnexureDocument.size()<1){
                        b=true;
                    }else if(previousAnnexureDocument.get(0).getAttachmentItemLists()==null){
                        b=true;
                    }else if(previousAnnexureDocument.get(0).getAttachmentItemLists().size()<1){
                        b=true;
                    }
                    if(a==1){
                        boolean s=true;
                        if(unitInfoDataModel.getUnit_usage().equals("Religious")  || unitInfoDataModel.getUnit_usage().equals("Amenities")){
                            s=false;
                        }
                        if(s && b && (globalAnnexureAPath==null || globalAnnexureAPath.equals(""))){
                            addErrorLayout.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                addErrorTextView.setText("Please upload namuna 1 image");
                            }else{
                                addErrorTextView.setText("Please upload Annexure A image");
                            }
                            return;
                        }else if(!floorFlag && b && (globalAnnexureBPath==null || globalAnnexureBPath.equals(""))){
                            addErrorLayout.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                addErrorTextView.setText("Please upload namuna 2 image");
                            }else{
                                addErrorTextView.setText("Please upload Annexure B image");
                            }

                            return;
                        }

                    }else if(a==2){
                        if(!floorFlag && b && (globalAnnexureBPath==null || globalAnnexureBPath.equals(""))){
                            addErrorLayout.setVisibility(View.VISIBLE);
                            if(floorFlag){
                                addErrorTextView.setText("Please upload namuna 2 image");
                            }else{
                                addErrorTextView.setText("Please upload Annexure B image");
                            }
                            return;
                        }

                    }else if(a==3){
                        if(!unitInfoDataModel.getUnit_usage().equals("Religious") && !unitInfoDataModel.getUnit_usage().equals("Amenities")){
                            if(b && (globalAnnexureAPath==null || globalAnnexureAPath.equals(""))){
                                addErrorLayout.setVisibility(View.VISIBLE);
                                if(floorFlag){
                                    addErrorTextView.setText("Please upload namuna 1 image");
                                }else{
                                    addErrorTextView.setText("Please upload Annexure A image");
                                }

                                return;
                            }
                        }

                    }else if(a==4){
                        Utils.showProgress("Saving data...",memberActivity);

                        ArrayList<MediaInfoDataModel> attach = (ArrayList<MediaInfoDataModel>)
                                localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.NamunaLable,unitInfoDataModel.getUnit_id());

                        for(MediaInfoDataModel mediaInfoDataModel : attach){
                            localSurveyDbViewModel.setIsUploaded(mediaInfoDataModel.getParent_unique_id(), mediaInfoDataModel.getObejctId(), false);
                            localSurveyDbViewModel.setRemarksByMediaId(mediaInfoDataModel.getMediaId(), annexure_remark_dropdown.getText().toString());
                        }

                        unitInfoDataModel.setAnnexure_remarks(annexure_remark_dropdown.getText().toString());
                        unitInfoDataModel.setHoh_avaibility((short) 1);
                        unitInfoDataModel.setAnnexure_uploaded((short) 1);
                        new Handler().postDelayed(() -> {
                            localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
//                            Utils.dismissProgress();
//                            dialogAnnex.dismiss();
//                            showFormSubmitDialog();
                        }, 2000);
                    }
                    if(a!=-1){
                        Utils.showProgress("Saving data...",memberActivity);
                        addErrorLayout.setVisibility(View.GONE);
                        ArrayList<MediaInfoDataModel> attach = new ArrayList<>();
                        ArrayList<AttachmentItemList> attachmentItems = new ArrayList<>();
                        previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.NamunaLable, unitInfoDataModel.getUnit_id());
                        if (previousAnnexureDocument.size()>0 && userAttachmentList.size()>0){
                            // previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.AnnexureLable, unitInfoDataModel.getUnit_id());
                            attach =previousAnnexureDocument;
                            for(int i=0;i<attach.size();i++){
                                if(attach.get(i).isUploaded()){
                                    attach.get(i).setLocal(false);
                                }else{
                                    attach.get(i).setLocal(true);
                                }
                                attach.get(i).setUploaded(false);
                            }

                            for(int i=0;i<previousAnnexureDocument.size();i++){
                                for (int j = 0; j < previousAnnexureDocument.get(i).getAttachmentItemLists().size(); j++) {
                                    if(!attachmentItems.contains(previousAnnexureDocument.get(i).getAttachmentItemLists().get(j))){
                                        attachmentItems.add(previousAnnexureDocument.get(i).getAttachmentItemLists().get(j));
                                    }
                                }
                            }


                        }else if (userAttachmentList.size()>0) {
                            for(int i=0;i<userAttachmentList.size();i++){
                                MediaInfoDataModel data = new MediaInfoDataModel();
                                ArrayList<AttachmentListImageDetails> user=new ArrayList<>();
                                user.add(userAttachmentList.get(i));
                                data =getmediaInfoDataList(user, unitInfoDataModel.getRelative_path(), Constants.unit_infoLayer,
                                        unitInfoDataModel.getUnit_id(), unitInfoDataModel.getRel_globalid()).get(0);
                                attach.add(data);
                                attach.get(i).setLocal(true);
                            }


                        }
                        if (attach.size() > 0) {
                            for(int i=0;i<attach.size();i++){
                                mediaInfoDataModels1 = new ArrayList<>();

                                attach.get(i).setDocument_remarks(annexure_remark_dropdown.getText().toString());
                                attach.get(i).setDocument_category(Constants.NamunaLable);
                                if(attach.get(i).getFilename().contains("namuna_1")){
                                    attach.get(i).setDocument_type(Constants.NamunaLableA);
                                    attach.get(i).setUploadMediaCount(1);
                                }else if(attach.get(i).getFilename().contains("namuna_2")){
                                    attach.get(i).setDocument_type(Constants.NamunaLableB);
                                    attach.get(i).setUploadMediaCount(1);
                                }

                                attach.get(i).setHaveDelete(attach.get(i).isHaveDelete());

                                ArrayList<String> listImageDetails = new ArrayList<>();
                                if (previousAnnexureDocument.size()>0 && userAttachmentList.size()>0){
                                    for(int k=0;k<previousAnnexureDocument.size();k++){
                                        if(previousAnnexureDocument.get(k).getAttachmentItemLists()!=null && previousAnnexureDocument.get(k).getAttachmentItemLists().size()>0){
                                            for (int j = 0; j < previousAnnexureDocument.get(k).getAttachmentItemLists().size(); j++) {
                                                if(!previousAnnexureDocument.get(k).getAttachmentItemLists().get(j).isDeleted && !previousAnnexureDocument.get(k).getAttachmentItemLists().get(j).isUpdated && !previousAnnexureDocument.get(k).getAttachmentItemLists().get(j).getItem_url().contains("http")){
                                                    if(attach.get(i).getFilename().equalsIgnoreCase(previousAnnexureDocument.get(k).getAttachmentItemLists().get(j).getFileName())){
                                                        listImageDetails.add(previousAnnexureDocument.get(k).getAttachmentItemLists().get(j).getItem_url());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                for (int k = 0; k < userAttachmentList.size(); k++) {
                                    if(attach.get(i).getFilename().equalsIgnoreCase(userAttachmentList.get(k).getFileName())){
                                        listImageDetails.add(userAttachmentList.get(k).getFilePath());
                                        AttachmentItemList at = new AttachmentItemList(0, userAttachmentList.get(k).getFileName(), userAttachmentList.get(k).getFilePath(), false, false);
                                        attachmentItems.add(at);
                                        attach.get(i).setUploadMediaList(listImageDetails);
                                        attach.get(i).setAttachmentItemLists(attachmentItems);
                                    }
                                }


                                mediaInfoDataModels1.add(attach.get(i));
                                List<MediaInfoDataModel> mediaInfoData = mediaInfoDataModels1;
                                localSurveyDbViewModel.insertAllMediaInfoPointData(mediaInfoData, activity);
//                                userAttachmentList = new ArrayList<>();
                                unitInfoDataModel.setHoh_avaibility((short) 1);
                                unitInfoDataModel.setAnnexure_uploaded((short) 1);
//                            unitInfoDataModel.setAnnexure_upload_date("");
                                attachmentItems = new ArrayList<>();
                            }

                        }
                        userAttachmentList = new ArrayList<>();
                        ArrayList<MediaInfoDataModel> attachNew = (ArrayList<MediaInfoDataModel>)
                                localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.NamunaLable,unitInfoDataModel.getUnit_id());

                        for(MediaInfoDataModel mediaInfoDataModel : attachNew){
                            localSurveyDbViewModel.setIsUploaded(mediaInfoDataModel.getParent_unique_id(), mediaInfoDataModel.getObejctId(), false);
                            localSurveyDbViewModel.setRemarksByMediaId(mediaInfoDataModel.getMediaId(), annexure_remark_dropdown.getText().toString());
                        }

                        isAnxDocumentDelete = false;
                        unitInfoDataModel.setAnnexure_remarks(annexure_remark_dropdown.getText().toString());
                        new Handler().postDelayed(() -> {
                            localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                            Utils.dismissProgress();
                            dialogAnnex.dismiss();
                            showFormSubmitDialog();
                        }, 2000);
                    }
                }else{
                    unitInfoDataModel.setAnnexure_remarks("HOH not available");
                    unitInfoDataModel.setHoh_avaibility((short) 0);
                    unitInfoDataModel.setAnnexure_uploaded((short) 0);
//                    unitInfoDataModel.setAnnexure_upload_date("");
                    new Handler().postDelayed(() -> {
                        localSurveyDbViewModel.updateUnitInfoPointData(unitInfoDataModel, memberActivity);
                        Utils.dismissProgress();
                        dialogAnnex.dismiss();
                        showFormSubmitDialog();
                    }, 2000);
                }
            }
        });
        dialogAnnex.setCancelable(false);
        dialogAnnex.show();
    }


    public void showActionAlertDialogButtonClickedANamuna(String header, String mssage, String btnYes, String btnNo, boolean toUplaod, int flag, String itemUrl) {

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
            annexureFlag++;
            isAnxDocumentDelete = true;
            if (flag == 1) {
                deleteMedia(globalAnnexureAPath, 2);
                annexureOneCapturedDocument.setImageBitmap(null);
                annexureOneCapturedDocument.setImageDrawable(null);
                annexureOneCapturedDocument.setImageResource(android.R.color.transparent);
                anexureOneLayout.setVisibility(View.GONE);
                List<MediaInfoDataModel> pp = new ArrayList<>();
                previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.NamunaLable, unitInfoDataModel.getUnit_id());
                if (previousAnnexureDocument != null && !previousAnnexureDocument.isEmpty()) {
                    for(int i=0;i<previousAnnexureDocument.size();i++){
                        if(previousAnnexureDocument.get(i).getItem_url().equalsIgnoreCase(itemUrl)){
                            pp.add(previousAnnexureDocument.get(i));
                        }
                    }
                    if (annexureFlag == 1) {
                        onAttachmentDeletedClicked(pp, 1, 0, null, globalAnnexureAPath);
                    } else if (annexureFlag == 2) {
                        userAttachmentList.clear();
                        onAttachmentDeletedClicked(pp, 2, 0, null, "");
                    }
                }
                globalAnnexureAPath = "";
            }
            dialog.dismiss();
        });

        btn_no.setOnClickListener(view1 -> dialog.dismiss());

        img_close.setOnClickListener(view1 -> dialog.dismiss());

        dialog.show();
    }

    public void showActionAlertDialogButtonClickedBNamuna(String header, String mssage, String btnYes, String btnNo, boolean toUplaod, int flag, String itemUrl) {

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
            annexureFlag ++;
            isAnxDocumentDelete = true;
            if(flag==1){
                deleteMedia(globalAnnexureBPath,2);
                annexureTwoCapturedDocument.setImageBitmap(null);
                annexureTwoCapturedDocument.setImageDrawable(null);
                annexureTwoCapturedDocument.setImageResource(android.R.color.transparent);
                anexureTwoLayout.setVisibility(View.GONE);
                List<MediaInfoDataModel> pp = new ArrayList<>();
                previousAnnexureDocument = (ArrayList<MediaInfoDataModel>) localSurveyDbViewModel.getMediaInfoDataByCatUnit(Constants.NamunaLable, unitInfoDataModel.getUnit_id());
                if(previousAnnexureDocument!=null && previousAnnexureDocument.size()>0){
                    for(int i=0;i<previousAnnexureDocument.size();i++){
                        if(previousAnnexureDocument.get(i).getItem_url().equalsIgnoreCase(itemUrl)){
                            pp.add(previousAnnexureDocument.get(i));
                        }
                    }
                    if (annexureFlag == 1){
                        onAttachmentDeletedClicked(pp,1,0,null,globalAnnexureBPath);
                    }else if (annexureFlag == 2){
                        userAttachmentList.clear();
                        onAttachmentDeletedClicked(pp,2,0,null,"");
                    }
                }
                globalAnnexureBPath="";
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
}
