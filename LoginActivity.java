package com.igenesys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

//import com.google.android.filament.BuildConfig;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.igenesys.databinding.ActivityLoginBinding;
import com.igenesys.model.AfterLoginData;
import com.igenesys.model.DomainModel;
import com.igenesys.model.LoginModel;
import com.igenesys.model.LoginUser;
import com.igenesys.model.NMDPLResponseList;
import com.igenesys.model.UserModel;
import com.igenesys.networks.Api_Interface;
import com.igenesys.networks.GetFormModel;
import com.igenesys.networks.QueryResultRepoViewModel;
import com.igenesys.networks.RetrofitService;
import com.igenesys.utils.AES;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;
import com.igenesys.view.LoginViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Activity activity;
    String currentAppVersionName;
    int currentAppVersionCode = 0, liveVersionCode = 0;
    boolean isAppActive = false, isAppLatest = false;
    String liveVersionName = "", liveApk_link = "", liveApk_description = "";
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    final int REQUEST_CODE = 101;
    String imei;

    //this is create method
    // this is new comment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        activity = LoginActivity.this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);

        binding.setLifecycleOwner(this);

        binding.setLoginViewModel(loginViewModel);

//        binding.txtAppVersion.setText("v " + BuildConfig.VERSION_NAME);
        // boolean b=App.getSharedPreferencesHandler().getBoolean("offlineCheck", false);
        // if(Utils.isConnected(activity) && !b){
        // getAppStatus();
        // }
        currentAppVersionName = BuildConfig.VERSION_NAME;
        currentAppVersionCode = BuildConfig.VERSION_CODE;
        try {
            int savedLive = App.getSharedPreferencesHandler().getIntString(Constants.liveVersionCode, liveVersionCode);
            if (currentAppVersionCode == savedLive)
                isAppLatest = true;

        } catch (Exception e) {
            AppLog.e(e.getMessage());
        }
        binding.usernameEt.setHint(getString(R.string.hint_username));
        binding.passwordEt.setHint(getString(R.string.hint_password));

        binding.usernameEt.setOnFocusChangeListener((v, v1) -> {
            if (binding.usernameHintHeader.getVisibility() == View.GONE) {
                binding.usernameHintHeader.setVisibility(View.VISIBLE);
                binding.usernameHintTv.setVisibility(View.VISIBLE);
                binding.usernameEt.setHint("");
            } else if (binding.usernameHintHeader.getVisibility() == View.VISIBLE && binding.usernameEt.getText().toString().length() == 0) {
                binding.usernameHintHeader.setVisibility(View.GONE);
                binding.usernameHintTv.setVisibility(View.GONE);
                binding.usernameEt.setHint(getString(R.string.hint_username));
            }
        });
        binding.passwordEt.setOnFocusChangeListener((v, v1) -> {
            if (binding.passHintHeader.getVisibility() == View.GONE) {
                binding.passHintHeader.setVisibility(View.VISIBLE);
                binding.passHintTv.setVisibility(View.VISIBLE);
                binding.passwordEt.setHint("");
            } else if (binding.passHintHeader.getVisibility() == View.VISIBLE && binding.passwordEt.getText().toString().isEmpty()) {
                binding.passHintTv.setVisibility(View.GONE);
                binding.passHintHeader.setVisibility(View.GONE);
                binding.passwordEt.setHint(getString(R.string.hint_password));
            }
        });

        // boolean isOffline = App.getSharedPreferencesHandler().getBoolean("offlineCheck", false);

         // Disable if want to bypass Esri Configuration
         // if (Utils.isConnected(activity) && !isOffline) {
         //     getDefaultConfig();
         // } else {
         //     bindConfigValues();
         // }

        // Enable if want to bypass Esri Configuration
        proceedToCheckLoginStatus();

        Utils.disableCopyPaste(binding.usernameEt);
        Utils.disableCopyPaste(binding.passwordEt);
    }

    /*public void getDefaultConfig() {

        Utils.updateProgressMsg("Initiating.., Please wait..", activity);

        Api_Interface apiInterfaceConfig = RetrofitService.getConfigClient().create(Api_Interface.class);
        apiInterfaceConfig.getEsriConfigInfo(Constants.ESRI_CONFIG_URL).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.code() == 200 && response.body() != null) {
                        String encryptedConfig = response.body().string();
                        App.getSharedPreferencesHandler().setEsriConfig(encryptedConfig);
                    }
                    bindConfigValues();
                } catch (Exception e) {
                    AppLog.e(e.getMessage());
                    bindConfigValues();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                call.cancel();
                bindConfigValues();
            }
        });
    }*/

    /*public void bindConfigValues() {

        boolean isAllValuesBound = Utils.bindConstantValuesFromConfig();

        if (isAllValuesBound) {
            proceedToCheckLoginStatus();
        } else {
            showActionAlertDialogOkButton("DRP App Status", "Ok", "", "Unable to retrieve configuration. Kindly Logout and Login again!!");
        }
    }*/

    public void proceedToCheckLoginStatus() {

        boolean isOffline = App.getSharedPreferencesHandler().getBoolean("offlineCheck", false);

        if (Utils.isConnected(activity) && !isOffline) {
            getAppStatus();
            // checkUserLoginStatus();
        } else {
            if (!isAppLatest) {
                showActionAlertDialogUpdateButton("DRP App Status", "", "Close", "This DRP application you are using is currently inactive.", "");
            } else if (App.getSharedPreferencesHandler().getBoolean(Constants.isAppLatest, true)) {
                if (App.getSharedPreferencesHandler().getBoolean(Constants.isAppActive, true)) {
                    checkUserLoginStatus();
                } else {
                    showActionAlertDialogUpdateButton("DRP App Status", "", "Close", "The DRP application is currently inactive.", "");
                }
            } else {
                showActionAlertDialogUpdateButton("DRP App Status", "", "Close", "The DRP application is currently not updated.", "");
            }
        }

        loginViewModel.getUser().observe(this, loginUser -> {

            if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrUserName())) {
                binding.usernameEt.setError("Enter an Username", null);
                binding.usernameEt.requestFocus();
            } else if (loginUser.isUserNameValid()) {
                binding.usernameEt.setError("Enter a Valid Username", null);
                binding.usernameEt.requestFocus();
            } else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getStrPassword())) {
                binding.passwordEt.setError("Enter a Password", null);
                binding.passwordEt.requestFocus();
            } else if (!loginUser.isPasswordLengthGreaterThan5()) {
                binding.passwordEt.setError("Enter at least 6 Digit password", null);
                binding.passwordEt.requestFocus();
            } else {
                try {
                    getIMEIDeviceId(this,loginUser.getStrUserName(),loginUser.getStrPassword());
//                    logInUser(loginUser.getStrUserName(), loginUser.getStrPassword());
                } catch (Exception e) {
                    AppLog.logData(activity, e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void checkUserLoginStatus() {
        Utils.updateProgressMsg("Checking previous login status.", activity);
        boolean isUserLogin = App.getSharedPreferencesHandler().getBoolean(Constants.loginStatus);
        if (isUserLogin) {
            if (!Utils.checkinterne(activity) || App.getSharedPreferencesHandler().getBoolean(Constants.isMapLoadOffline)){
            UserModel userModel = Utils.getGson().fromJson(App.getSharedPreferencesHandler().getString(Constants.User_Data), UserModel.class);
            AppLog.logFileName = Utils.getLogFilePath(userModel.getUser_name());
            App.getInstance().setUserModel(userModel);
            Utils.shortToast("Details fetched successfully.", activity);
            App.getSharedPreferencesHandler().putBoolean(Constants.loginStatus, true);
            new Handler().postDelayed(() -> {
                Utils.shortToast("Login Successful", activity);
                Utils.dismissProgress();
                startActivity(new Intent(activity, WorkAreaActivity.class));
                finish();
            }, 2000);
            }else{
                try {
                    getIMEIDeviceId(this,App.getSharedPreferencesHandler().getString("id"),App.getSharedPreferencesHandler().getString("pass"));
//                logInUser(App.getSharedPreferencesHandler().getString("id"),App.getSharedPreferencesHandler().getString("pass"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            Utils.dismissProgress();
        }
    }

    private void logInUser(String userName, String password) throws Exception {
        if (!Utils.checkinterne(activity))
            return;


        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("imeiNo", imei);
        jsonParams.put("username", userName);
        jsonParams.put("password", AES.encrypt(password));
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());


        Utils.updateProgressMsg("Authenticating, please wait..", activity);

        Api_Interface apiInterface = RetrofitService.getDomainClient().create(Api_Interface.class);
        Call<LoginModel> call = apiInterface.validateUser(body);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                Utils.dismissProgress();
                try {
                    Log.d("TAG", response.code() + "" + response.body());
                    if (response.code() == 200) {

                        LoginModel loginModel = response.body();
                        if(loginModel!=null && loginModel.status!=null && loginModel.status.statusCode==1){
                            UserModel userModel = new UserModel(
                                    Utils.getString(loginModel.data.getUser().firstName),
                                    Utils.getString(loginModel.data.getUser().lastName),
                                    Utils.getString(loginModel.data.getUser().userName),
                                    Utils.getString(loginModel.data.getUser().mobileNumber),
                                    Utils.getString(loginModel.data.getUser().email),
                                    loginModel.data.getUser().userId
                            );

                            FirebaseCrashlytics.getInstance().setUserId(loginModel.data.getUser().userName);

                            App.getSharedPreferencesHandler().putString(Constants.userId_cons,loginModel.data.getUser().userId);
                            App.getSharedPreferencesHandler().putString(Constants.username_cons,loginModel.data.getUser().userName);

                            // App.getInstance().setWorkAreaModels(workAreaList);
                            App.getSharedPreferencesHandler().putString("id",userName);
                            App.getSharedPreferencesHandler().putString("pass",password);
                            App.getSharedPreferencesHandler().putString(Constants.User_Data, Utils.getGson().toJson(userModel));
                            App.getInstance().setUserModel(userModel);
                            App.getSharedPreferencesHandler().putBoolean(Constants.loginStatus, true);
                            AfterLogInUser(App.getSharedPreferencesHandler().getString(Constants.userId_cons),App.getSharedPreferencesHandler().getString(Constants.username_cons));
                        }else if(loginModel!=null && loginModel.status!=null && loginModel.status.statusCode==0){
                            showActionAlertDialogUpdateButton("DRP App Status", "", "Close", loginModel.status.errorMessage, "");
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
            public void onFailure(Call<LoginModel> call, Throwable t) {
                call.cancel();
            }
        });

        /*
        Utils.updateProgressMsg("Authenticating, please wait..", activity);
        QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(this).get(QueryResultRepoViewModel.class);


        queryResultRepoViewModel.getQueryResult(Constants.UserInfo_FS_BASE_URL_ARC_GIS,
                Constants.USER_INFO_ENDPOINT,
                GetFormModel.getInstance().getQueryBuilderForm(String.format("user_name = '%s' and password = '%s'", userName, password), "*",
                        false, false, true));

        queryResultRepoViewModel.getMutableLiveData().observe(this, resultQueryModel -> {
            Utils.dismissProgress();
            if (resultQueryModel != null) {
                if (resultQueryModel.getFeatures().size() > 0) {
                    Map<String, Object> map = new HashMap<>();
                    Map<String, Object> mapAttributeValue = new HashMap<>();
                    try {
                        map = (Map<String, Object>) resultQueryModel.getFeatures().get(0);
                        mapAttributeValue = (Map<String, Object>) map.get("attributes");
                    } catch (Exception e) {
                        AppLog.e(e.getMessage());
                    }
                    if (mapAttributeValue != null) {
System.out.println("Attribute :: "+Utils.getGson().toJson(mapAttributeValue));
                        try {
                            UserModel userModel = new UserModel(
                            Utils.getString(mapAttributeValue.get(Constants.UserInfo_first_name)),
                                    Utils.getString(mapAttributeValue.get(Constants.UserInfo_last_name)),
                                    Utils.getString(mapAttributeValue.get(Constants.UserInfo_user_name)),
                                    Utils.getString(mapAttributeValue.get(Constants.UserInfo_mobile_number)),
                                    Utils.getString(mapAttributeValue.get(Constants.UserInfo_email)),
                                    mapAttributeValue.get(Constants.UserInfo_globalid)
                        );
                          //  userModel.setUser_name("suser1"); //remove this line , it is only for development
                        App.getSharedPreferencesHandler().putString(Constants.User_Data, Utils.getGson().toJson(userModel));
                        App.getInstance().setUserModel(userModel);
                        App.getSharedPreferencesHandler().putBoolean(Constants.loginStatus, true);
                        Utils.shortToast("Login Successful", activity);
                        startActivity(new Intent(activity, WorkAreaActivity.class));
                        finish();
                        } catch (Exception e) {
                            AppLog.e(e.getMessage());
                        }

                    } else
                        Utils.showMessagePopup("Invalid User", activity);
                } else
                    Utils.showMessagePopup("Invalid User", activity);
            } else
                Utils.showMessagePopup("Invalid User", activity);

        });

         */
    }

    private void getAppStatus() {

        Utils.updateProgressMsg("Checking for update. Please wait..", activity);

        try {
            QueryResultRepoViewModel queryResultRepoViewModel = new ViewModelProvider(this).get(QueryResultRepoViewModel.class);

            queryResultRepoViewModel.getQueryResult(Constants.AppVersion_FS_BASE_URL_ARC_GIS, Constants.AppVersion_ENDPOINT,
                    GetFormModel.getInstance().getQueryBuilderForm(
                            "(appname = 'GDC')", "*", false, "last_edited_date DESC", 1));

            App.getSharedPreferencesHandler().putBoolean(Constants.isAppActive, isAppActive);
            App.getSharedPreferencesHandler().putBoolean(Constants.isAppLatest, isAppLatest);

            queryResultRepoViewModel.getMutableLiveData().observe(this, resultQueryModel -> {
                if (resultQueryModel != null) {
                    if (resultQueryModel.getFeatures().size() > 0) {

                        Map<String, Object> map = (Map<String, Object>) resultQueryModel.getFeatures().get(0);
                        Map<String, Object> mapAttributeValue = (Map<String, Object>) map.get("attributes");

                        if (mapAttributeValue != null) {
                            String appActive = "" + (int) Utils.doubleFormatter(Utils.getString(mapAttributeValue.get("is_active")));
                            isAppActive = Utils.getString(appActive).equalsIgnoreCase("1");
                            liveVersionName = Utils.getString(mapAttributeValue.get("version_name"));
                            liveVersionCode = (int) Utils.doubleFormatter(Utils.getString(mapAttributeValue.get("version_code")));
                            liveApk_link = Utils.getString(mapAttributeValue.get("apk_link"));
                            liveApk_description = Utils.getString(mapAttributeValue.get("description"));
                            binding.txtAppVersion.setText(liveVersionName);
                        }

                        if (currentAppVersionCode == liveVersionCode)
                            isAppLatest = true;
                        else
                            isAppLatest = false;

                        App.getSharedPreferencesHandler().putString(Constants.liveVersionName, liveVersionName);
                        App.getSharedPreferencesHandler().putIntString(Constants.liveVersionCode, liveVersionCode);
                        App.getSharedPreferencesHandler().putString(Constants.liveApk_link, liveApk_link);
                        App.getSharedPreferencesHandler().putBoolean(Constants.isAppActive, isAppActive);
                        App.getSharedPreferencesHandler().putBoolean(Constants.isAppLatest, isAppLatest);

                        Utils.dismissProgress();

                        if (!isAppActive) {
                            showActionAlertDialogUpdateButton("DRP App Status", "", "Close", "The DRP application is currently inactive2.", "");
                        } else if (!isAppLatest) {
                            showActionAlertDialogUpdateButton("Update Required", "Update Now", "Close", liveApk_description, liveApk_link);
                        } else {
                            checkUserLoginStatus();
                        }
                    } else {
                        showActionAlertDialogUpdateButton("DRP App Status", "", "Close", "The DRP application is currently inactive3.", "");
                    }
                } else {
                    Utils.dismissProgress();
                    showActionAlertDialogUpdateButton("DRP App Status", "", "Close", "The DRP application is currently inactive4.", "");
                }
            });
        } catch (Exception e) {
            AppLog.e(e.getMessage());
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

    public void showActionAlertDialogOkButton(String header, String yesBtn, String noBtn, String message) {
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

        LinearLayout btn_no = customLayout.findViewById(R.id.btn_no);
        btn_no.setVisibility(View.GONE);

        ImageView img_close = customLayout.findViewById(R.id.img_close);
        img_close.setVisibility(View.GONE);

        RadioGroup statusRadioGroup = customLayout.findViewById(R.id.statusRadioGroup);
        statusRadioGroup.setVisibility(View.GONE);

        LinearLayout btn_yes = customLayout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    public void getIMEIDeviceId(Context context,String user,String pass) {
        try {
        String deviceId;
            App.getSharedPreferencesHandler().putString("pImei","");
            App.getSharedPreferencesHandler().putString("sImei","");
            App.getSharedPreferencesHandler().putString("deviceId","");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            try {
                App.getSharedPreferencesHandler().putString("deviceId",Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            }catch (Exception ex){
                ex.getMessage();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
                return;
            }
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = mTelephony.getImei();
                    try {
                        App.getSharedPreferencesHandler().putString("pImei",deviceId);
                    }catch (Exception ex){
                        ex.getMessage();
                    }

                    try {
                        App.getSharedPreferencesHandler().putString("deviceId",Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
                    }catch (Exception ex){
                        ex.getMessage();
                    }
                }else {
                    deviceId = mTelephony.getDeviceId();
                    try {
                        App.getSharedPreferencesHandler().putString("deviceId",deviceId);
                    }catch (Exception ex){
                        ex.getMessage();
                    }
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                try {
                    App.getSharedPreferencesHandler().putString("deviceId",deviceId);
                }catch (Exception ex){
                    ex.getMessage();
                }
            }
        }
        Log.d("deviceId", deviceId);
        imei=deviceId;
//            Toast.makeText(this, "ID= "+imei, Toast.LENGTH_SHORT).show();

            try {
                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String imeiNumber1 = tm.getDeviceId(0);
                App.getSharedPreferencesHandler().putString("pImei",imeiNumber1);
                String imeiNumber2 = tm.getDeviceId(1);
                App.getSharedPreferencesHandler().putString("sImei",imeiNumber2);
            }catch (Exception ex){
                ex.getMessage();
            }

            logInUser(user, pass);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // in the below line, we are displaying toast message
                // if permissions are not granted.
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void AfterLogInUser(String userId, String userName) throws Exception {
        if (!Utils.checkinterne(activity))
            return;
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("userName", userName);
        jsonParams.put("userId",userId);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());


        Utils.updateProgressMsg("Authenticating, please wait..", activity);

        Api_Interface apiInterface = RetrofitService.getAfterLogin().create(Api_Interface.class);
        Call<AfterLoginData> call = apiInterface.validateAfterLoginUser(body);
        call.enqueue(new Callback<AfterLoginData>() {
            @Override
            public void onResponse(Call<AfterLoginData> call, Response<AfterLoginData> response) {
                Utils.dismissProgress();
                try {
                    Log.d("TAG", response.code() + "" + response.body());
                    if (response.code() == 200) {

                        AfterLoginData loginModel = response.body();
                        if(loginModel!=null && loginModel.getStatus()!=null && loginModel.getStatus().getStatus()==1){
                            getNMDPLOfficersList();
//                            Utils.shortToast("Login Successful", activity);
//                            startActivity(new Intent(activity, WorkAreaActivity.class));
//                            finish();

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
            public void onFailure(Call<AfterLoginData> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void getNMDPLOfficersList() {
        try {
            Api_Interface apiInterface = RetrofitService.getDomainClient().create(Api_Interface.class);
            Call<NMDPLResponseList> call = apiInterface.getNMDPLOfficersList();
            call.enqueue(new Callback<NMDPLResponseList>() {
                @Override
                public void onResponse(Call<NMDPLResponseList> call, Response<NMDPLResponseList> response) {


                    Log.d("TAG", response.code() + "");
                    if (response.code() == 200) {
                        ArrayList<String> list=(ArrayList<String>) response.body().getData();
                        App.getSharedPreferencesHandler().putString("NMDPLOfficersSaveList", new Gson().toJson(list));
                        Utils.shortToast("Login Successful", activity);
                        startActivity(new Intent(activity, WorkAreaActivity.class));
                        finish();
                    } else {
                        Utils.shortToast("Domain Data Not Found, Try Again Opening the App.", activity);
                    }

                }

                @Override
                public void onFailure(Call<NMDPLResponseList> call, Throwable t) {
                    call.cancel();
                }
            });

        } catch (Exception ex) {
            AppLog.e(ex.getMessage());
        }
    }
}