package com.igenesys.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.igenesys.App;
import com.igenesys.R;
import com.igenesys.model.AutoCompleteModal;
import com.igenesys.model.EsriConfigModel;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    static int MY_PERMISSIONS_REQUEST_CODE = 2;
    static String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    private static ProgressDialog progressDialog;
    private static final int[] progressStatus = {0};
    private static androidx.appcompat.app.AlertDialog dialog = null;


    public static boolean storagePermission(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            // only for gingerbread and newer versions
            return true;
        } else {
            boolean permissioncheck1 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), PERMISSION_STORAGE[0]) == PackageManager.PERMISSION_GRANTED;
            boolean permissioncheck2 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), PERMISSION_STORAGE[1]) == PackageManager.PERMISSION_GRANTED;

            if (!permissioncheck1 && !permissioncheck2) {
                Utils.shortToast(activity.getResources().getString(R.string.readWritePermissionRequired), activity);
            }
            return permissioncheck1 && permissioncheck2;
        }

    }


    public static PictureMarkerSymbol setSymbols(Activity activity) {
        BitmapDrawable selectedDrawable;
        PictureMarkerSymbol selectedSymbol = null;
        try {
            selectedDrawable = (BitmapDrawable) ContextCompat.getDrawable(activity, R.drawable.ic_map_marker);
            selectedSymbol = PictureMarkerSymbol.createAsync(selectedDrawable).get();
            selectedSymbol.setOffsetY(10);
            selectedSymbol.loadAsync();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return selectedSymbol;
    }

    public static String reverseGeocode(Context context, Double lat, Double lng) {
        String strAddress = null;
        Geocoder gc = new Geocoder(context);
        try {
            if (Geocoder.isPresent() && lat != 0.0 && lng != 0.0) {
                List<Address> list = gc.getFromLocation(lat, lng, 1);
                Address address = list.get(0);
                strAddress = address.getAddressLine(0);

            } else {
                strAddress = "";
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        return strAddress;
    }

    public static boolean checkinterne(Activity context) {
        if ((isConnected(context))) {
            return true;
        } else {
            openalert(context, context.getResources().getString(R.string.notWorkingInternet));
            return false;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideSoftKeyboardd(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        //If no view currently has focus, create a new one, just so we can grab a window token from it
//        if (view == null) {
//            view = new View(activity);
//        }
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getCurrentDate(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy_HHmmss", Locale.ENGLISH);
        return formatter.format(date);
    }

    public static Date convertDateToDDMMYYYY(Date date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        return new SimpleDateFormat("dd/MM/yyyy").parse(formatter.format(date));
    }

    public static String convertDateToStringSurveyListFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy , HH:mm:ss", Locale.ENGLISH);
        return formatter.format(date);
    }

    public static String convertDateToStringDateOnlyFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        return formatter.format(date);
    }

    public static String convertDateToString(Object date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy , HH:mm:ss", Locale.ENGLISH);
            return formatter.format(date);
        } catch (Exception e) {
            return "n/a";
        }

    }

    public static String convertTimeToString(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
        return formatter.format(time);
    }

    public static String convertTimeToQueryString(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return formatter.format(time);
    }

    public static String convertDateToStringDate(Object date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            return formatter.format(date);
        } catch (Exception e) {
            return "N/A";
        }
    }

    public static Uri getCaptureImageOutputUri(Activity activity, String relative_path, String imgName) {
        Uri outputFileUri = null;
        if (getFile(activity, relative_path, imgName) != null) {
            outputFileUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider",
                    getFile(activity, relative_path, imgName));
        }
        return outputFileUri;
    }

    public static Uri getCaptureVideoOutputUri(Activity activity, String relative_path, String imgName) {
        Uri outputFileUri = null;
        if (getVideoFile(activity, relative_path, imgName) != null) {
            outputFileUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider",
                    getVideoFile(activity, relative_path, imgName));
        }
        return outputFileUri;
    }

    public static SimpleFillSymbol getSimpleFillLineSymbol(Activity activity) {
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ContextCompat.getColor(activity, R.color.blueColor), 4);
        return new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, ContextCompat.getColor(activity, R.color.tranparent), lineSymbol);
    }

    public static boolean isConnected(Activity context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    public static Geometry createBufferAroundBoundary(Geometry boundaryGeom, int distance) {
        LinearUnit linearUnit = new LinearUnit(LinearUnitId.METERS);
        try {
            return GeometryEngine.bufferGeodetic(boundaryGeom, distance, linearUnit,
                    0.0001, GeodeticCurveType.GEODESIC);
        } catch (Exception e) {
            return boundaryGeom;
        }
    }

    public static void openalert(Activity context, String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                context.getResources().getString(R.string.ok),
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.setCancelable(false);
        alert11.show();
    }

    public static boolean checkAutodateTimeValidation(Activity activity) {
        boolean isValid = false;
        try {
            if (Settings.Global.getInt(activity.getContentResolver(), Settings.Global.AUTO_TIME) == 0
                    || Settings.Global.getInt(activity.getContentResolver(), Settings.Global.AUTO_TIME_ZONE) == 0) {
                showAlertDialog(activity);
            } else {
                isValid = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }

    private static void showAlertDialog(Activity activity) {

        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(activity);

        builder1.setMessage(activity.getResources().getString(R.string.autoDateTimeNotEnabled));
        builder1.setCancelable(true);
        builder1.setTitle(activity.getResources().getString(R.string.alert));

        builder1.setPositiveButton(
                activity.getResources().getString(R.string.ok),
                (dialog, id) -> {
                    dialog.cancel();
                    activity.startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                });
        builder1.setNegativeButton(activity.getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
        alert11.setCancelable(false);
        alert11.setCanceledOnTouchOutside(false);
        if (!alert11.isShowing()) {
            alert11.show();
        }
    }

    public static double doubleFormatter(String value) {
        double doubleValue;
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            doubleValue = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            doubleValue = 0;
        }
        return doubleValue;
    }

    public static int integerFormatter(String value) {
        int intValue;
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            intValue = 0;
        }
        return intValue;
    }

    public static short shortFormatter(String value) {
        short intValue;
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            intValue = Short.parseShort(value);
        } catch (NumberFormatException ex) {
            intValue = 0;
        }
        return intValue;
    }

    public static void updateProgressMsg(String msg, Activity activity) {
        if (progressDialog == null) {
            showProgress(msg, activity);
        } else {
            if (progressDialog.isShowing())
                activity.runOnUiThread(() -> progressDialog.setMessage(msg));
            else showProgress(msg, activity);
        }
    }

    // Changed second paramater from Context to Activity
    public static void showProgress(String msg, Activity activity) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(Constants.appName);
            if (msg.equals(""))
                progressDialog.setMessage("Loading...");
            else progressDialog.setMessage(msg);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        if (!progressDialog.isShowing()) {
            try {
                progressDialog.show();
            } catch (Exception e) {
                try {
                    progressDialog.show();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /*
     * Shows popup with progress bar & displays the progress in percentage. Used in Upload Data screen located in side navigation > Upload data.
     *
     * progressPercent: accepts value to show on progress bar & -1 if popup needs to remove from screen
     * */

    public static void showProgressBarWithPercent(int progressPercent, Activity activity) {
        progressStatus[0] = progressPercent;
        if (progressStatus[0] == 0) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
            View customLayout = activity.getLayoutInflater().inflate(R.layout.progressbar_status, null);
            builder.setView(customLayout);
            dialog = builder.create();
            dialog.setCancelable(false);
            ProgressBar progressBar = customLayout.findViewById(R.id.upload_progressbar_pb);
            TextView progressStatusTV = customLayout.findViewById(R.id.progress_status_tv);
//        final int[] progressStatus = new int[1];
            Handler handler = new Handler();
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    while (progressStatus[0] < 100) {
//                        progressStatus[0] += 1;
                        // Update the progress bar and display the
                        //current value in the text view
                        handler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressStatus[0]);
                                progressStatusTV.setText(progressStatus[0] + "%");
                            }
                        });
                        try {
                            // Sleep for 200 milliseconds.
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t1.start();
            dialog.show();
        } else if (progressStatus[0] < 100) {
            progressStatus[0] += progressPercent;
        }

        if (progressPercent == -1) {
            dialog.dismiss();
        }

        if (progressStatus[0] >= 100) {
            try {
                progressStatus[0] = 100;
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
            progressStatus[0] = 0;
            dialog.dismiss();
        }
    }

    public static void updateProgressBarWithPercent(int progressPercent) {

    }

    public static void dismissProgress() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                try {
                    progressDialog.dismiss();
                    progressDialog = null;
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void setError(AutoCompleteTextView autoCompleteTextView, String msg, Activity activity) {
        autoCompleteTextView.requestFocus();
        autoCompleteTextView.setError(msg);
        Utils.shortToast(msg, activity);
    }

    public static void setError(EditText editText, String msg, Activity activity) {
        editText.requestFocus();
        editText.setError(msg);
        Utils.shortToast(msg, activity);
    }

    public static void setError(TextInputLayout editText, String msg, Activity activity) {
        editText.requestFocus();
        editText.setError(msg);
        Utils.shortToast(msg, activity);
    }

    public static void setError(TextView editText, String msg, Activity activity) {
        editText.requestFocus();
        editText.setError(msg);
        Utils.shortToast(msg, activity);
    }

    public static void setError(TextInputEditText editText, String msg, Activity activity) {
        editText.requestFocus();
        editText.setError(msg);
        Utils.shortToast(msg, activity);
    }

    public static void shortToast(String msg, Context context) {
        if (context == null)
            return;

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getUnitNumberPrefix(String unitSelected) {
        String[] unitDetails = unitSelected.trim().split(" ");
//        String uniPrefix = "";
        String uniPrefix = unitDetails[0];
//        for (String uni : unitDetails) {
//            uniPrefix = uniPrefix + uni.charAt(0);
//        }

        return uniPrefix + "-";
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .create();
    }

    public static String getString(Object objects) {
        if (objects != null) {
            return String.valueOf(objects);
        } else {
            return "";
        }
    }

    public static boolean getboolean(Object objects) {
        if (objects != null) {
            return getString(objects).equals("1");
        } else {
            return false;
        }
    }

    public static int getInteger(Object objects) {
        if (objects != null) {
            return integerFormatter(getString(objects));
        } else {
            return 0;
        }
    }

    public static short getShort(Object objects) {
        if (objects != null) {
            return shortFormatter(getString(objects));
        } else {
            return 0;
        }
    }

    public static void showMessagePopup(String message, Activity activity) {
        YesNoBottomSheet yesNoBottomSheet = YesNoBottomSheet.geInstance(activity, "", null, null, null);
        yesNoBottomSheet.setMsg(message);
        if (yesNoBottomSheet.getYesNoMessageTv() != null) {
            yesNoBottomSheet.getYesNoMessageTv().setText(message);
        }
        if (!yesNoBottomSheet.isAdded()) {
            yesNoBottomSheet.show(((AppCompatActivity) activity).getSupportFragmentManager(), "");
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidPancard(String str) {
        String s = "ABCDE1234F"; // get your editext value here
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

        Matcher matcher = pattern.matcher(str);
// Check if pattern matches

        return matcher.matches();
    }


    public static String getString(String text) {
        String newString = "";
        if (TextUtils.isEmpty(text) || text.equalsIgnoreCase("null")) {
            newString = "";
        } else {
            newString = text;
        }
        return newString;
    }

    public static boolean isValidPhoneNumber(CharSequence target) {
        String pattern = "[1-9][0-9]{9}";
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches());
//        return target.matche("[1-9][0-9]{9}");
    }

    public static String getSplittedString(String originalString, int length) {
        String requiredString;
        int requiredLength = length - 2;
        if (originalString != null) {
            if (originalString.trim().length() > (length - 1)) {
                requiredString = originalString.substring(0, requiredLength) + "..";
            } else {
                requiredString = originalString;
            }
        } else {
            requiredString = "";
        }
        return requiredString;
    }

    public static Map<String, Object> getStructurePointAttribute(String structure_id,
                                                                 String grid_number,
                                                                 String area_name,
                                                                 String cluster_name,
                                                                 String phase_name,
                                                                 String work_area_name,
                                                                 String hut_number,
                                                                 String structure_name,
                                                                 short no_of_floors,
                                                                 String address,
                                                                 String tenement_number,
                                                                 String structure_status,
                                                                 String surveyor_name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("structure_id", Utils.getSplittedString(structure_id, 50));
        attributes.put("grid_number", Utils.getSplittedString(grid_number, 50));
        attributes.put("area_name", Utils.getSplittedString(area_name, 50));
        attributes.put("cluster_name", Utils.getSplittedString(cluster_name, 50));
        attributes.put("phase_name", Utils.getSplittedString(phase_name, 50));
        attributes.put("work_area_name", Utils.getSplittedString(work_area_name, 50));
        attributes.put("hut_number", Utils.getSplittedString(hut_number, 50));
        attributes.put("structure_name", Utils.getSplittedString(structure_name, 100));
        attributes.put("no_of_floors", no_of_floors);
        attributes.put("address", Utils.getSplittedString(address, 250));
        attributes.put("tenement_number", Utils.getSplittedString(tenement_number, 25));
        attributes.put("structure_status", Utils.getSplittedString(structure_status, 35));
        attributes.put("surveyor_name", Utils.getSplittedString(surveyor_name, 100));

        return attributes;
    }

    public static Map<String, Object> getMediaInfoAttribute(String content_type,
                                                            String filename,
                                                            int data_size,
                                                            String item_url,
                                                            short file_upload_checked,
                                                            String parent_table_name,
                                                            String parent_unique_id,
                                                            Object rel_globalid, Object objectid, Object globalid,
                                                            String docType, String docCategory, String docName, String docRemarks) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("content_type", Utils.getSplittedString(content_type, 50));
        attributes.put("filename", Utils.getSplittedString(filename, 100));
        attributes.put("data_size", data_size);
        attributes.put("file_upload_checked", file_upload_checked);
        attributes.put("parent_table_name", parent_table_name);
        attributes.put("rel_globalid", rel_globalid);
        attributes.put("item_url", Utils.getSplittedString(item_url, 255));
        attributes.put("parent_unique_id", Utils.getSplittedString(parent_unique_id, 50));
        attributes.put("objectid", objectid);
        attributes.put("globalid", globalid);
        attributes.put("document_type", docType);
        attributes.put("document_category", docCategory);
        attributes.put("name_on_document", docName);
        attributes.put("document_remarks", docRemarks);

        return attributes;
    }

    public static Map<String, Object> getMediaDetailsAttribute(String content_type, String file_path, String file_name, String file_ext,
                                                               int file_order, long data_size, String remarks, String rel_globalid) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("content_type", Utils.getSplittedString(content_type, 50));
        attributes.put("file_path", file_path);
        attributes.put("file_name", Utils.getSplittedString(file_name, 100));
        attributes.put("file_ext", file_ext);
        attributes.put("file_order", file_order);
        attributes.put("data_size", data_size);
        attributes.put("remarks", remarks);
        attributes.put("rel_globalid", rel_globalid);

        // attributes.put("objectid", objectid);
        // attributes.put("globalid", globalid);
        // attributes.put("attachment_id", attachment_id);
        // attributes.put("created_by", created_by);
        // attributes.put("created_date", created_date);
        // attributes.put("edited_by", edited_by);
        // attributes.put("edited_date", edited_date);
        // attributes.put("created_user", created_user);
        // attributes.put("last_edited_user", last_edited_user);
        // attributes.put("last_edited_date", last_edited_date);

        return attributes;
    }

    public static Map<String, Object> getHohAttribute(
            Object rel_globalid,
            String hoh_id,
            String relative_path,
            String hoh_name,
            String marital_status,
            short hoh_spouse_count, String hoh_spouse_name,
            String hoh_contact_no, GregorianCalendar hoh_dob,
            short age,
            String gender, short staying_since_year,
            String aadhar_no, String pan_no,
            String ration_card_colour, String ration_card_no,
            String from_state, String mother_tongue,
            String religion, String education,
            String occupation, String place_of_work,
            String type_of_work, String monthly_income,
            String mode_of_transport, String school_college_name_location,
            String handicap_or_critical_disease, String staying_with,
            String vehicle_owned_driven, short death_certificate,
            short count_of_other_members, boolean isUploaded) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("rel_globalid", rel_globalid);
        attributes.put("hoh_id", Utils.getSplittedString(hoh_id, 25));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        attributes.put("hoh_name", Utils.getSplittedString(hoh_name, 102));
        attributes.put("hoh_contact_no", Utils.getSplittedString(hoh_contact_no, 15));
        attributes.put("hoh_spouse_name", Utils.getSplittedString(hoh_spouse_name, 50));
        attributes.put("age", age);
        if (gender.equalsIgnoreCase("Transgender")) {
            gender = "Trans";
        }
        attributes.put("gender", gender);
        attributes.put("marital_status", marital_status);
        if (from_state.equalsIgnoreCase("Dadra and Nagar Haveli and Daman and Diu")) {
            attributes.put("from_state", "Dadra Nagar Haveli and Daman Diu");
        } else {
            attributes.put("from_state", from_state);
        }
        attributes.put("mother_tongue", mother_tongue);
        attributes.put("religion", religion);
        attributes.put("education", education);
        attributes.put("occupation", occupation);
        attributes.put("place_of_work", place_of_work);
        attributes.put("type_of_work", type_of_work);
        attributes.put("aadhar_no", aadhar_no);
        attributes.put("pan_no", pan_no);
        if (isUploaded) {
            hoh_dob.add(Calendar.MINUTE, 330);
            attributes.put("hoh_dob", hoh_dob.getTimeInMillis());
        } else {
            hoh_dob.add(Calendar.MINUTE, 330);
            attributes.put("hoh_dob", hoh_dob);
        }
        attributes.put("staying_since_year", staying_since_year);
        attributes.put("ration_card_colour", ration_card_colour);
        attributes.put("ration_card_no", ration_card_no);
        attributes.put("monthly_income", monthly_income);
        attributes.put("mode_of_transport", mode_of_transport);
        attributes.put("hoh_spouse_count", hoh_spouse_count);
        attributes.put("school_college_name_location", school_college_name_location);
        attributes.put("handicap_or_critical_disease", handicap_or_critical_disease);
        attributes.put("staying_with", staying_with);
        attributes.put("vehicle_owned_driven", vehicle_owned_driven);
        attributes.put("count_of_other_members", count_of_other_members);
        attributes.put("death_certificate", death_certificate);

        return attributes;

    }

    public static Map<String, Object> getEditHohAttribute(
            Object rel_globalid,
            String hoh_id,
            String relative_path,
            String hoh_name,
            String marital_status,
            short hoh_spouse_count, String hoh_spouse_name,
            String hoh_contact_no,
            short age,
            String gender, short staying_since_year,
            String aadhar_no, String pan_no,
            String ration_card_colour, String ration_card_no,
            String from_state, String mother_tongue,
            String religion, String education,
            String occupation, String place_of_work,
            String type_of_work, String monthly_income,
            String mode_of_transport, String school_college_name_location,
            String handicap_or_critical_disease, String staying_with,
            String vehicle_owned_driven,
            short count_of_other_members) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("rel_globalid", rel_globalid);
        attributes.put("hoh_id", Utils.getSplittedString(hoh_id, 25));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        attributes.put("hoh_name", Utils.getSplittedString(hoh_name, 102));
        attributes.put("hoh_contact_no", Utils.getSplittedString(hoh_contact_no, 15));
        attributes.put("hoh_spouse_name", Utils.getSplittedString(hoh_spouse_name, 50));
        attributes.put("age", age);
        attributes.put("gender", gender);
        attributes.put("marital_status", marital_status);
        if (from_state.equalsIgnoreCase("Dadra and Nagar Haveli and Daman and Diu")) {
            attributes.put("from_state", "Dadra Nagar Haveli and Daman Diu");
        } else {
            attributes.put("from_state", from_state);
        }
        attributes.put("mother_tongue", mother_tongue);
        attributes.put("religion", religion);
        attributes.put("education", education);
        attributes.put("occupation", occupation);
        attributes.put("place_of_work", place_of_work);
        attributes.put("type_of_work", type_of_work);
        attributes.put("aadhar_no", aadhar_no);
        attributes.put("pan_no", pan_no);
        attributes.put("staying_since_year", staying_since_year);
        attributes.put("ration_card_colour", ration_card_colour);
        attributes.put("ration_card_no", ration_card_no);
        attributes.put("monthly_income", monthly_income);
        attributes.put("mode_of_transport", mode_of_transport);
        attributes.put("hoh_spouse_count", hoh_spouse_count);
        attributes.put("school_college_name_location", school_college_name_location);
        attributes.put("handicap_or_critical_disease", handicap_or_critical_disease);
        attributes.put("staying_with", staying_with);
        attributes.put("vehicle_owned_driven", vehicle_owned_driven);
        attributes.put("count_of_other_members", count_of_other_members);

        return attributes;

    }

    public static Map<String, Object> getMemberAttribute(
            Object rel_globalid, String member_id,
            String relative_path, String member_name,
            String relationship_with_hoh, String marital_status,
            short member_spouse_count, String member_spouse_name,
            String member_contact_no, GregorianCalendar member_dob,
            short age,
            String gender, short staying_since_year,
            String aadhar_no, String pan_no,
            String ration_card_colour, String ration_card_no,
            String from_state, String mother_tongue,
            String religion, String education,
            String occupation, String place_of_work,
            String type_of_work, String monthly_income,
            String mode_of_transport, String school_college_name_location,
            String handicap_or_critical_disease, String staying_with,
            String vehicle_owned_driven, boolean isUploaded, short death_certificate) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("rel_globalid", rel_globalid);
        attributes.put("member_id", Utils.getSplittedString(member_id, 25));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        attributes.put("member_name", Utils.getSplittedString(member_name, 35));
        attributes.put("age", age);
        if (gender.equalsIgnoreCase("Transgender")) {
            gender = "Trans";
        }
        attributes.put("gender", gender);
        attributes.put("marital_status", marital_status);
        if (from_state.equalsIgnoreCase("Dadra and Nagar Haveli and Daman and Diu")) {
            attributes.put("from_state", "Dadra Nagar Haveli and Daman Diu");
        } else {
            attributes.put("from_state", from_state);
        }
        attributes.put("mother_tongue", mother_tongue);
        attributes.put("religion", religion);
        attributes.put("education", education);
        attributes.put("occupation", occupation);
        attributes.put("place_of_work", place_of_work);
        attributes.put("type_of_work", type_of_work);
        attributes.put("relationship_with_hoh", relationship_with_hoh);
        attributes.put("aadhar_no", aadhar_no);
        attributes.put("pan_no", pan_no);
        attributes.put("member_spouse_count", member_spouse_count);
        attributes.put("member_spouse_name", member_spouse_name);

        if (isUploaded && member_dob != null) {
            member_dob.add(Calendar.MINUTE, 330);
            attributes.put("member_dob", member_dob.getTimeInMillis());
        } else if (member_dob != null) {
            member_dob.add(Calendar.MINUTE, 330);
            attributes.put("member_dob", member_dob);
        } else if (member_dob == null) {
            attributes.put("member_dob", null);
        }

        attributes.put("member_contact_no", member_contact_no);
        attributes.put("staying_since_year", staying_since_year);
        attributes.put("ration_card_colour", ration_card_colour);
        attributes.put("ration_card_no", ration_card_no);
        attributes.put("monthly_income", monthly_income);
        attributes.put("mode_of_transport", mode_of_transport);
        attributes.put("school_college_name_location", school_college_name_location);
        attributes.put("handicap_or_critical_disease", handicap_or_critical_disease);
        attributes.put("staying_with", staying_with);
        attributes.put("vehicle_owned_driven", vehicle_owned_driven);
//        attributes.put("share_certificate", death_certificate);
        return attributes;
    }

    public static Map<String, Object> getUnitInfoDetailsMemberNoAttribute(Object rel_globalid,
                                                                          String unit_id,
                                                                          String unit_unique_id,
                                                                          String relative_path,
                                                                          String tenement_number,
                                                                          String hut_number,
                                                                          String floor,
                                                                          String unit_no,
                                                                          String member_available,
                                                                          String unit_status,
                                                                          String surveyor_name,
                                                                          String remarks,
                                                                          String remarks_other,
                                                                          double area_sq_ft) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("unit_status", unit_status);
        attributes.put("surveyor_name", Utils.getSplittedString(surveyor_name, 247));

        attributes.put("member_available", member_available);
        attributes.put("remarks", Utils.getSplittedString(remarks, 247));
        attributes.put("remarks_other", Utils.getSplittedString(remarks_other, 150));

        attributes.put("rel_globalid", rel_globalid);
        attributes.put("unit_no", Utils.getSplittedString(unit_no, 10));
        attributes.put("hut_number", Utils.getSplittedString(hut_number, 25));
        attributes.put("unit_id", Utils.getSplittedString(unit_id, 50));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        attributes.put("tenement_number", Utils.getSplittedString(tenement_number, 25));
        attributes.put("nature_of_activity", " ");
        attributes.put("area_sq_ft", area_sq_ft);
        attributes.put("floor", floor);
        attributes.put("unit_unique_id", unit_unique_id);

        return attributes;
    }

    //add new values and agruments
    public static Map<String, Object> getUnitInfoDetailsAttribute(Object rel_globalid,
                                                                  String unit_id,
                                                                  String unit_unique_id,
                                                                  String relative_path,
                                                                  String tenement_number,
                                                                  String hut_number,
                                                                  String floor,
                                                                  String unit_no,
                                                                  String unit_usage,
                                                                  GregorianCalendar existence_since,
                                                                  GregorianCalendar structure_since,
                                                                  String structure_year,
                                                                  String nature_of_activity,
                                                                  Double residential_area,
                                                                  Double commercial_area,
                                                                  Double rc_residential_area,
                                                                  Double rc_commercial_area,
                                                                  Double other_area,
                                                                  Double area_sq_ft,
                                                                  short electric_bill,
                                                                  short ownership_proof,
                                                                  short property_tax,
                                                                  short rent_agreement,
                                                                  short gumasta,
                                                                  short chain_document,
                                                                  short financial_documents,
                                                                  short other_license,
                                                                  short na_tax,
                                                                  short electoral_roll,
                                                                  short photo_pass,
                                                                  short share_certificate,
                                                                  short school_college_certificate,
                                                                  short employer_certificate,
                                                                  short restaurant_hotel_license,
                                                                  short factory_act_license,
                                                                  short food_drug_license,
                                                                  String member_available,
                                                                  String unit_status,
                                                                  String surveyor_name,
                                                                  String remarks,
                                                                  String remarks_other,
                                                                  int media_captured_cnt,
                                                                  int media_uploaded_cnt,
                                                                  String surveyor_desig,
                                                                  String drp_officer_name,
                                                                  String drp_officer_name_other,
                                                                  String drp_officer_desig,
                                                                  String drp_officer_desig_other,
                                                                  short visit_count,
                                                                  String area_name,
                                                                  String ward_no,
                                                                  String sector_no,
                                                                  String zone_no,
                                                                  String nagar_name,
                                                                  String nagar_name_other,
                                                                  String society_name,
                                                                  String street_name,
                                                                  String landmark_name,
                                                                  String respondent_name,
                                                                  String respondent_dob,
                                                                  String respondent_age,
                                                                  String respondent_hoh_contact,
                                                                  String respondent_hoh_relationship,
                                                                  String respondent_hoh_relationship_other,
                                                                  String tenement_document,
                                                                  String mashal_survey_number,
                                                                  String ownership_status,
                                                                  Double unit_area_mtrs,
                                                                  String loft_present,
                                                                  Double loft_area_mtrs,
                                                                  String employees_count,
                                                                  String pincode,
                                                                  String contact,
                                                                  Double ghumasta_area_mtrs,
                                                                  GregorianCalendar visit_date,
                                                                  short form_lock,
                                                                  boolean isUploaded,
                                                                  String respondent_hoh_name,
                                                                  String survey_date,
                                                                  String survey_time,
                                                                  String type_of_other_structure,
                                                                  String respondent_remark, String member_non_available_remarks) throws ParseException {
        Map<String, Object> attributes = new HashMap<>();

        // attributes.put("structure_year", Utils.getSplittedString(structure_year, 247));
        attributes.put("unit_status", unit_status);
        attributes.put("media_captured_cnt", media_captured_cnt);
        attributes.put("media_uploaded_cnt", media_uploaded_cnt);
        attributes.put("surveyor_name", Utils.getSplittedString(surveyor_name, 247));

        attributes.put("member_available", member_available);
        attributes.put("remarks", Utils.getSplittedString(remarks, 247));
        attributes.put("remarks_other", Utils.getSplittedString(remarks_other, 150));
        attributes.put("member_non_available_remarks", Utils.getSplittedString(member_non_available_remarks, 247));

        attributes.put("rel_globalid", rel_globalid);
        attributes.put("structure_year", Utils.getSplittedString(structure_year, 250));
        attributes.put("nature_of_activity", "unit");
        attributes.put("unit_no", Utils.getSplittedString(unit_no, 10));
        attributes.put("hut_number", Utils.getSplittedString(hut_number, 25));
        attributes.put("unit_id", Utils.getSplittedString(unit_id, 50));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        attributes.put("tenement_number", Utils.getSplittedString(tenement_number, 25));
        attributes.put("unit_usage", unit_usage);
        attributes.put("floor", floor);

        /*
        new date changes
         */
        try {
            if (isUploaded) {
//                attributes.put("existence_since", null);
//                existence_since.add(Calendar.MINUTE,330);
                attributes.put("existence_since", existence_since.getTimeInMillis());
            } else {
//                existence_since.add(Calendar.MINUTE,330);
                attributes.put("existence_since", existence_since);
            }
        } catch (Exception ex) {
            attributes.put("existence_since", null);
            ex.getCause();
        }
//        attributes.put("existence_since", existence_since);

        attributes.put("structure_since", structure_since);

        attributes.put("residential_area", residential_area);
        attributes.put("commercial_area", commercial_area);
        attributes.put("rc_residential_area", rc_residential_area);
        attributes.put("rc_commercial_area", rc_commercial_area);
        attributes.put("other_area", other_area);
        attributes.put("area_sq_ft", area_sq_ft);

        attributes.put("electric_bill", electric_bill);
        attributes.put("ownership_proof", ownership_proof);
        attributes.put("property_tax", property_tax);
        attributes.put("rent_agreement", rent_agreement);
        attributes.put("gumasta", gumasta);
        attributes.put("chain_document", chain_document);
        attributes.put("financial_documents", financial_documents);
        attributes.put("other_license", other_license);
        attributes.put("na_tax", na_tax);
        attributes.put("electoral_roll", electoral_roll);
        attributes.put("photo_pass", photo_pass);
        attributes.put("share_certificate", share_certificate);
        attributes.put("school_college_certificate", school_college_certificate);
        attributes.put("employer_certificate", employer_certificate);
        attributes.put("restaurant_hotel_license", restaurant_hotel_license);
        attributes.put("food_drug_license", food_drug_license);
        attributes.put("factory_act_license", factory_act_license);
        attributes.put("surveyor_desig", surveyor_desig);
        attributes.put("drp_officer_name", drp_officer_name);
        attributes.put("drp_officer_name_other", drp_officer_name_other);
        attributes.put("drp_officer_desig", drp_officer_desig);
        attributes.put("drp_officer_desig_other", drp_officer_desig_other);
        attributes.put("visit_count", visit_count);
        attributes.put("area_name", area_name);//ERROR
        attributes.put("ward_no", ward_no);//ERROR
        attributes.put("sector_no", sector_no);//ERROR
        attributes.put("zone_no", zone_no);//ERROR
        attributes.put("nagar_name", nagar_name);
        attributes.put("nagar_name_other", nagar_name_other);
        attributes.put("society_name", society_name);
        attributes.put("street_name", street_name);
        attributes.put("landmark_name", landmark_name);
        attributes.put("respondent_name", respondent_name);
        attributes.put("respondent_dob", respondent_dob);//
        attributes.put("respondent_age", respondent_age);//
        if (Integer.parseInt(respondent_age) < 18) {
            attributes.put("respondent_remarks", respondent_remark);//
        }
        attributes.put("respondent_hoh_contact", respondent_hoh_contact);
        attributes.put("respondent_hoh_relationship", respondent_hoh_relationship);
        attributes.put("respondent_hoh_rel_other", respondent_hoh_relationship_other);
        attributes.put("tenement_document", tenement_document);//
        attributes.put("mashal_survey_number", mashal_survey_number);//
        attributes.put("ownership_status", ownership_status);

        attributes.put("loft_present", loft_present);//
        attributes.put("unit_area_sqft", unit_area_mtrs);//
        attributes.put("ghumasta_area_sqft", ghumasta_area_mtrs);
        attributes.put("loft_area_sqft", loft_area_mtrs);//
        if (employees_count != null && !employees_count.equalsIgnoreCase("")) {
            attributes.put("employees_count", Short.parseShort(employees_count));
        } else {
            attributes.put("employees_count", null);
        }
        attributes.put("unit_unique_id", unit_unique_id);
        attributes.put("nature_of_activity", "nature_of_activity");
        attributes.put("pincode", pincode);
        attributes.put("respondent_mobile", contact);
        if (isUploaded) {
//            visit_date.add(Calendar.MINUTE,330);
            attributes.put("visit_date", visit_date.getTimeInMillis());
        } else {
//            visit_date.add(Calendar.MINUTE,330);
            attributes.put("visit_date", visit_date);
        }
        attributes.put("form_lock", form_lock);
        attributes.put("respondent_hoh_name", respondent_hoh_name);

        if (!isUploaded) {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(survey_date + " 00:00 aa");
            TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
            Locale loc = new Locale("en", "IN");
            Calendar calendar = Calendar.getInstance(loc);
            GregorianCalendar gc = (GregorianCalendar) calendar;
            gc.setTimeZone(tz);
            gc.setTime(date);
//            gc.add(Calendar.MINUTE,330);
            attributes.put("survey_date", gc);
            attributes.put("survey_time", gc);
        }
        attributes.put("type_of_other_structure", type_of_other_structure);
        if (unit_status.equals(Constants.completed_statusLayer) || unit_status.equals(Constants.completed_dispute)) {
            if (isUploaded) {
                GregorianCalendar gg = new GregorianCalendar();
//                gg.add(Calendar.MINUTE,330);
                attributes.put("survey_end_date", "" + gg.getTimeInMillis());
            } else {
                GregorianCalendar gg = new GregorianCalendar();
//                gg.add(Calendar.MINUTE,330);
                attributes.put("survey_end_date", "" + gg.getTimeInMillis());
            }
        }

        return attributes;
    }

    public static long getEpochDateStamp() {
//        return new GregorianCalendar().getTimeInMillis();
        return System.currentTimeMillis();
    }

    public static GregorianCalendar getGregorianCalendarFromDate(String etExistenceSince) {
        GregorianCalendar cal = null;
        try {
            java.text.DateFormat df = new SimpleDateFormat(Constants.requiredDateFormat);
            Date date1 = df.parse(etExistenceSince);
            cal = new GregorianCalendar();
            cal.setTime(date1);
        } catch (Exception ignored) {
            cal = null;
        }

        return cal;
    }

    @SuppressLint("Range")
    public static String getFileName(Uri uri, Activity activity) {

        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        String name = "";
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = activity.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            name = myFile.getName();
        }

        return name;
    }

    public static boolean getFileSizeFromFile(Activity activity, File file) {

        if (file == null)
            return false;

        return checkFleSize(file.length(), activity);
    }

    public static boolean getFileSizeFromUri(Activity activity, Uri uri) {

        Cursor returnCursor = activity.getContentResolver().query(uri, new String[]{}, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        return checkFleSize(returnCursor.getFloat(sizeIndex), activity);

    }

    public static boolean checkFleSize(float size, Activity activity) {
        float fileSizeInKB = size / 1000;
        float fileSizeInMB = fileSizeInKB / 1024;

        if (fileSizeInMB > 10) {
            showMessagePopup("The file size is currently " + getStringSizeLengthFile(size) + ". Please note that files larger than 10MB are not permitted.", activity);
            return false;
        } else return true;
    }

    public static String getStringSizeLengthFile(float size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1000.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if (size < sizeMb)
            return df.format(size / sizeKb) + " Kb";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " Mb";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " Gb";

        return "";
    }

    public static File copyFile(Activity activity, Uri uri, String relative_path, String name) {
        File outPut = null;
        Uri returnUri = uri;
        Date date = new Date();
        CharSequence formateDate = DateFormat.format(Constants.requiredDateFormat, date);

        Cursor returnCursor = activity.getContentResolver().query(returnUri,
                new String[]{}, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        String mimeType = activity.getContentResolver().getType(uri);
        returnCursor.moveToFirst();

        String origialName = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        String fileName;
        try {
            fileName = name + "." + getFileExt(uri, activity);
        } catch (Exception e) {
            fileName = origialName;
        }

        if (!relative_path.equals("")) {
            //String filePath = "Attachments" + File.separator + formateDate;
            outPut = new File(activity.getExternalFilesDir(relative_path), fileName);
        } else {
            outPut = new File(activity.getExternalFilesDir("") + "/" + fileName);
        }

        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(outPut);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            outPut = null;
            AppLog.e("InputOutputStream: " + e.getMessage());
        }

        return outPut;
    }

    public static void deleteDirectory(File file) {
        try {
            if (file.isDirectory())
                for (File subFile : file.listFiles()) {
                    deleteDirectory(subFile);
                }
            if (!file.delete()) {
                AppLog.e("Failed to delete file: " + file.getPath());
            }
        } catch (Exception e) {

        }

    }

    public static File getFile(Activity activity, String relative_path, String imgName) {
        File path = null;
        File getImage = activity.getExternalCacheDir();
        if (getImage != null) {
            path = new File(activity.getExternalFilesDir(relative_path), imgName + ".png");
        }
        return path;
    }

    public static File getVideoFile(Activity activity, String relative_path, String videoName) {
        File path = null;
        File getImage = activity.getExternalCacheDir();
        if (getImage != null) {
            path = new File(activity.getExternalFilesDir(relative_path), videoName + ".mp4");
        }
        return path;
    }

    public static String getFileExt(Uri finalUri, Activity activity) {
        String extension;

//Check uri format to avoid null
        if (finalUri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(activity.getContentResolver().getType(finalUri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(finalUri.getPath())).toString());

        }

        return extension;
    }

    public static String getContentType(String imagePath) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(imagePath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }


    public static Map<String, Object> getUnitInfoDetailsAttribute(Object rel_globalid,
                                                                  String unit_id,
                                                                  String unit_unique_id,
                                                                  String relative_path,
                                                                  String hut_number,
                                                                  String floor,
                                                                  String unit_no,
                                                                  String nature_of_activity,
                                                                  String member_available,
                                                                  String unit_status,
                                                                  String surveyor_name,
                                                                  String surveyor_desig,
                                                                  String drp_officer_name,
                                                                  String drp_officer_name_other,
                                                                  String drp_officer_desig,
                                                                  String drp_officer_desig_other,
                                                                  String remarks,
                                                                  String remarks_other,
                                                                  short visit_count,
                                                                  String area_name,
                                                                  String ward_no,
                                                                  String sector_no,
                                                                  String zone_no,
                                                                  String nagar_name,
                                                                  String nagar_name_other,
                                                                  String society_name,
                                                                  String street_name,
                                                                  String landmark_name,
                                                                  String pincode,
                                                                  GregorianCalendar visit_date,
                                                                  short form_lock,
                                                                  String survey_date,
                                                                  String survey_time,
                                                                  boolean isUploaded
    ) throws ParseException {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("unit_status", unit_status);
        attributes.put("surveyor_name", Utils.getSplittedString(surveyor_name, 247));

        attributes.put("member_available", member_available);
        attributes.put("remarks", Utils.getSplittedString(remarks, 247));
        attributes.put("remarks_other", Utils.getSplittedString(remarks_other, 150));
        attributes.put("member_non_available_remarks", Utils.getSplittedString(remarks, 247));

        attributes.put("rel_globalid", rel_globalid);
        attributes.put("nature_of_activity", "unit");
        attributes.put("unit_no", Utils.getSplittedString(unit_no, 10));
        attributes.put("hut_number", Utils.getSplittedString(hut_number, 25));
        attributes.put("unit_id", Utils.getSplittedString(unit_id, 50));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        attributes.put("floor", floor);
        attributes.put("surveyor_desig", surveyor_desig);
        attributes.put("drp_officer_name", drp_officer_name);
        attributes.put("drp_officer_name_other", drp_officer_name_other);
        attributes.put("drp_officer_desig", drp_officer_desig);
        attributes.put("drp_officer_desig_other", drp_officer_desig_other);
        attributes.put("visit_count", visit_count);
        attributes.put("area_name", area_name);//ERROR
        attributes.put("ward_no", ward_no);//ERROR
        attributes.put("sector_no", sector_no);//ERROR
        attributes.put("zone_no", zone_no);//ERROR
        attributes.put("nagar_name", nagar_name);
        attributes.put("nagar_name_other", nagar_name_other);
        attributes.put("society_name", society_name);
        attributes.put("street_name", street_name);
        attributes.put("landmark_name", landmark_name);
        attributes.put("unit_unique_id", unit_unique_id);
        attributes.put("nature_of_activity", "nature_of_activity");
        attributes.put("pincode", pincode);
        if (isUploaded) {
//            visit_date.add(Calendar.MINUTE,330);
            attributes.put("visit_date", visit_date.getTimeInMillis());
        } else {
//            visit_date.add(Calendar.MINUTE,330);
            attributes.put("visit_date", visit_date);
        }
        attributes.put("form_lock", form_lock);
        attributes.put("area_sq_ft", 0.0);


        if (!isUploaded) {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(survey_date + " 00:00 aa");
            TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
            Locale loc = new Locale("en", "IN");
            Calendar calendar = Calendar.getInstance(loc);
            GregorianCalendar gc = (GregorianCalendar) calendar;
            gc.setTimeZone(tz);
            gc.setTime(date);
//            gc.add(Calendar.MINUTE,330);
            attributes.put("survey_date", gc);
            attributes.put("survey_time", gc);
        }
        if (unit_status.equals(Constants.completed_statusLayer) || unit_status.equals(Constants.completed_dispute)) {
            if (isUploaded) {
                GregorianCalendar gg = new GregorianCalendar();
//                gg.add(Calendar.MINUTE,330);
                attributes.put("survey_end_date", "" + gg.getTimeInMillis());
            } else {
                GregorianCalendar gg = new GregorianCalendar();
//                gg.add(Calendar.MINUTE,330);
                attributes.put("survey_end_date", "" + gg.getTimeInMillis());
            }
        }
        return attributes;
    }


    public static String formatToISO8601(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public static Date parseISO8601Date(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.parse(dateString);
    }

    public static Date parseStringToDate(String dateString) throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
    }

    public static Date convertExponentialToDate(String timestamp) {
        Double doubleTime = Double.parseDouble(timestamp);
        return new Date(doubleTime.longValue());
    }

    public static Date parseStringToDateOffline(String dateString) throws ParseException {
        return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(dateString);
    }

    //--------------V4 Code-------------------------------

    public static Map<String, Object> getStructurePointAttributeV4(String structure_id,
                                                                   String grid_number,
                                                                   String area_name,
                                                                   String cluster_name,
                                                                   String phase_name,
                                                                   String work_area_name,
                                                                   String hut_number,
                                                                   String structure_name,
                                                                   short no_of_floors,
                                                                   String address,
                                                                   String tenement_number,
                                                                   String structure_status,
                                                                   String surveyor_name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("structure_id", Utils.getSplittedString(structure_id, 50));
        //attributes.put("grid_number", Utils.getSplittedString(grid_number, 50));
        attributes.put("area_name", Utils.getSplittedString(area_name, 50));
        //attributes.put("cluster_name", Utils.getSplittedString(cluster_name, 50));
        //attributes.put("phase_name", Utils.getSplittedString(phase_name, 50));
        attributes.put("work_area_name", Utils.getSplittedString(work_area_name, 50));
        attributes.put("hut_id", Utils.getSplittedString(hut_number, 50));
        //attributes.put("structure_name", Utils.getSplittedString(structure_name, 100));
        attributes.put("no_of_floors", no_of_floors);
        //attributes.put("address", Utils.getSplittedString(address, 250));
        //attributes.put("tenement_number", Utils.getSplittedString(tenement_number, 25));
        attributes.put("structure_status", Utils.getSplittedString(structure_status, 35));
        attributes.put("surveyor_name", Utils.getSplittedString(surveyor_name, 100));

        return attributes;
    }


    public static Map<String, Object> getHohAttributeV4(
            Object rel_globalid,
            String hoh_id,
            String relative_path,
            String hoh_name,
            String marital_status,
            String marital_status_other,
            short hoh_spouse_count, String hoh_spouse_name,
            String hoh_contact_no, GregorianCalendar hoh_dob,
            short age,
            String gender, short staying_since_year,
            String aadhar_no, String pan_no,
            String ration_card_colour, String ration_card_no,
            String from_state, String from_state_other,
            String mother_tongue, String mother_tongue_other,
            String religion, String religion_other,
            String education, String education_other,
            String occupation, String occupation_other,
            String place_of_work,
            String type_of_work, String type_of_work_other,
            String monthly_income,
            String mode_of_transport, String mode_of_transport_other,
            String school_college_name_location,
            String handicap_or_critical_disease, String staying_with,
            String vehicle_owned_driven_type, String vehicle_owned_driven_other,
            short death_certificate, short count_of_other_members, String adhaar_verify_status,
            String adhaar_verify_remark, String adhaar_verify_date, boolean isUploaded) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("rel_globalid", rel_globalid);
        attributes.put("hoh_id", Utils.getSplittedString(hoh_id, 25));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        attributes.put("hoh_name", Utils.getSplittedString(hoh_name, 102));
        attributes.put("hoh_contact_no", Utils.getSplittedString(hoh_contact_no, 15));
        attributes.put("hoh_spouse_name", Utils.getSplittedString(hoh_spouse_name, 50));
        attributes.put("hoh_age", age);
        attributes.put("hoh_gender", gender);
        attributes.put("marital_status", marital_status);
        attributes.put("marital_status_other", marital_status_other);
        if (from_state.equalsIgnoreCase("Dadra and Nagar Haveli and Daman and Diu")) {
            attributes.put("from_state", "Dadra Nagar Haveli and Daman Diu");
        } else {
            attributes.put("from_state", from_state);
        }
        attributes.put("from_state_other", from_state_other);
        attributes.put("mother_tongue", mother_tongue);
        attributes.put("mother_tongue_other", mother_tongue_other);
        attributes.put("religion", religion);
        attributes.put("religion_other", religion_other);
        attributes.put("education", education);
        attributes.put("education_other", education_other);
        attributes.put("occupation", occupation);
        attributes.put("occupation_other", occupation_other);
        attributes.put("place_of_work", place_of_work);
        attributes.put("type_of_work", type_of_work);
        attributes.put("type_of_work_other", type_of_work_other);
        attributes.put("aadhar_no", aadhar_no);
        attributes.put("pan_no", pan_no);
        if (isUploaded) {
            hoh_dob.add(Calendar.MINUTE, 330);
            attributes.put("hoh_dob", hoh_dob.getTimeInMillis());
        } else {
            hoh_dob.add(Calendar.MINUTE, 330);
            attributes.put("hoh_dob", hoh_dob);
        }
        attributes.put("staying_since_year", staying_since_year);
        attributes.put("ration_card_colour", ration_card_colour);
        attributes.put("ration_card_no", ration_card_no);
        attributes.put("monthly_income", monthly_income);
        attributes.put("mode_of_transport", mode_of_transport);
        attributes.put("mode_of_transport_other", mode_of_transport_other);
        attributes.put("hoh_spouse_count", hoh_spouse_count);
        attributes.put("school_college_name_location", school_college_name_location);
        attributes.put("handicap_or_critical_disease", handicap_or_critical_disease);
        attributes.put("staying_with", staying_with);
        attributes.put("vehicle_owned_driven_type", vehicle_owned_driven_type);
        attributes.put("vehicle_owned_driven_other", vehicle_owned_driven_other);
        attributes.put("count_of_other_members", count_of_other_members);
        attributes.put("death_certificate", death_certificate);
        attributes.put("adhaar_verify_status", adhaar_verify_status);
        attributes.put("adhaar_verify_remark", adhaar_verify_remark);

        if (adhaar_verify_date != null && !adhaar_verify_date.isEmpty()) {
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(adhaar_verify_date);
                TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                Locale loc = new Locale("en", "IN");
                Calendar calendar = Calendar.getInstance(loc);
                GregorianCalendar gc = (GregorianCalendar) calendar;
                gc.setTimeZone(tz);
                gc.setTime(date);
                if (isUploaded) {
                    // gc.add(Calendar.MINUTE, 330);
                    attributes.put("adhaar_verify_date", gc.getTimeInMillis());
                } else {
                    attributes.put("adhaar_verify_date", gc);
                }
            } catch (Exception e) {
                TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
                Locale loc = new Locale("en", "IN");
                Calendar calendar = Calendar.getInstance(loc);
                GregorianCalendar gc = (GregorianCalendar) calendar;
                gc.setTimeZone(tz);
                gc.setTime(new Date());
                // attributes.put("adhaar_verify_date", gc.getTimeInMillis());
                if (isUploaded) {
                    // gc.add(Calendar.MINUTE, 330);
                    attributes.put("adhaar_verify_date", gc.getTimeInMillis());
                } else {
                    attributes.put("adhaar_verify_date", gc);
                }
            }
        } else {
            attributes.put("adhaar_verify_date", null);
        }

        return attributes;
    }

    public static Map<String, Object> getMemberAttributeV4(
            Object rel_globalid, String member_id,
            String relative_path, String member_name,
            String relationship_with_hoh, String relationship_with_hoh_other,
            String marital_status, String marital_status_other,
            short member_spouse_count, String member_spouse_name,
            String member_contact_no, GregorianCalendar member_dob,
            short age,
            String gender, short staying_since_year,
            String aadhar_no, String pan_no,
            String ration_card_colour, String ration_card_no,
            String from_state, String from_state_other,
            String mother_tongue, String mother_tongue_other,
            String religion, String religion_other,
            String education, String education_other,
            String occupation, String occupation_other, String place_of_work,
            String type_of_work, String type_of_work_other, String monthly_income,
            String mode_of_transport, String mode_of_transport_other,
            String school_college_name_location, String handicap_or_critical_disease, String staying_with,
            String vehicle_owned_driven_type, String vehicle_owned_driven_other,
            boolean isUploaded, short death_certificate) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("rel_globalid", rel_globalid);
        attributes.put("member_id", Utils.getSplittedString(member_id, 25));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        attributes.put("member_name", Utils.getSplittedString(member_name, 35));
        attributes.put("member_age", age);
        if (gender.equalsIgnoreCase("transgender")) {
            attributes.put("member_gender", "Trans");
        } else {
            attributes.put("member_gender", gender);
        }

        attributes.put("marital_status", marital_status);
        attributes.put("marital_status_other", marital_status_other);
        if (from_state.equalsIgnoreCase("Dadra and Nagar Haveli and Daman and Diu")) {
            attributes.put("from_state", "Dadra Nagar Haveli and Daman Diu");
        } else {
            attributes.put("from_state", from_state);
        }
        attributes.put("from_state_other", from_state_other);
        attributes.put("mother_tongue", mother_tongue);
        attributes.put("mother_tongue_other", mother_tongue_other);
        attributes.put("religion", religion);
        attributes.put("religion_other", religion_other);
        attributes.put("education", education);
        attributes.put("education_other", education_other);
        attributes.put("occupation", occupation);
        attributes.put("occupation_other", occupation_other);
        attributes.put("place_of_work", place_of_work);
        attributes.put("type_of_work", type_of_work);
        attributes.put("type_of_work_other", type_of_work_other);
        attributes.put("relationship_with_hoh", relationship_with_hoh);
        attributes.put("relationship_with_hoh_other", relationship_with_hoh_other);
        attributes.put("aadhar_no", aadhar_no);
        attributes.put("pan_no", pan_no);
        attributes.put("member_spouse_count", member_spouse_count);
        attributes.put("member_spouse_name", member_spouse_name);

        if (isUploaded && member_dob != null) {
            member_dob.add(Calendar.MINUTE, 330);
            attributes.put("member_dob", member_dob.getTimeInMillis());
        } else if (member_dob != null) {
            member_dob.add(Calendar.MINUTE, 330);
            attributes.put("member_dob", member_dob);
        } else if (member_dob == null) {
            attributes.put("member_dob", null);
        }

        attributes.put("member_contact_no", member_contact_no);
        attributes.put("staying_since_year", staying_since_year);
        attributes.put("ration_card_colour", ration_card_colour);
        attributes.put("ration_card_no", ration_card_no);
        attributes.put("monthly_income", monthly_income);
        attributes.put("mode_of_transport", mode_of_transport);
        attributes.put("mode_of_transport_other", mode_of_transport_other);
        attributes.put("school_college_name_location", school_college_name_location);
        attributes.put("handicap_or_critical_disease", handicap_or_critical_disease);
        attributes.put("staying_with", staying_with);
        attributes.put("vehicle_owned_driven_type", vehicle_owned_driven_type);
        attributes.put("vehicle_owned_driven_other", vehicle_owned_driven_other);
//        attributes.put("share_certificate", death_certificate);
        return attributes;
    }

    public static Map<String, Object> getUnitInfoDetailsAttributeV4(Object rel_globalid,
                                                                    String unit_id,
                                                                    String unit_unique_id,
                                                                    String relative_path,
                                                                    String tenement_number,
                                                                    String hut_number,
                                                                    String floor,
                                                                    String unit_no,
                                                                    String unit_usage,
                                                                    GregorianCalendar existence_since,
                                                                    GregorianCalendar structure_since,
                                                                    String structure_year,
                                                                    String nature_of_activity,
                                                                    Double residential_area,
                                                                    Double commercial_area,
                                                                    Double rc_residential_area,
                                                                    Double rc_commercial_area,
                                                                    Double other_area,
                                                                    Double area_sq_ft,
                                                                    short electric_bill,
                                                                    short ownership_proof,
                                                                    short property_tax,
                                                                    short rent_agreement,
                                                                    short gumasta,
                                                                    short chain_document,
                                                                    short financial_documents,
                                                                    short other_license,
                                                                    short na_tax,
                                                                    short electoral_roll,
                                                                    short photo_pass,
                                                                    short share_certificate,
                                                                    short school_college_certificate,
                                                                    short employer_certificate,
                                                                    short restaurant_hotel_license,
                                                                    short factory_act_license,
                                                                    short food_drug_license,
                                                                    String member_available,
                                                                    String unit_status,
                                                                    String surveyor_name,
                                                                    String remarks,
                                                                    String remarks_other,
                                                                    int media_captured_cnt,
                                                                    int media_uploaded_cnt,
                                                                    String surveyor_desig,
                                                                    String drp_officer_name,
                                                                    String drp_officer_name_other,
                                                                    String drp_officer_desig,
                                                                    String drp_officer_desig_other,
                                                                    short visit_count,
                                                                    String area_name,
                                                                    String ward_no,
                                                                    String sector_no,
                                                                    String zone_no,
                                                                    String nagar_name,
                                                                    String nagar_name_other,
                                                                    String society_name,
                                                                    String street_name,
                                                                    String landmark_name,
                                                                    String respondent_name,
                                                                    String respondent_dob,
                                                                    String respondent_age,
                                                                    String respondent_hoh_contact,
                                                                    String respondent_hoh_relationship,
                                                                    String respondent_hoh_relationship_other,
                                                                    String tenement_document,
                                                                    String mashal_survey_number,
                                                                    String ownership_status,
                                                                    Double unit_area_mtrs,
                                                                    String loft_present,
                                                                    Double loft_area_mtrs,
                                                                    String employees_count,
                                                                    String pincode,
                                                                    String contact,
                                                                    Double ghumasta_area_mtrs,
                                                                    GregorianCalendar visit_date,
                                                                    short form_lock,
                                                                    boolean isUploaded,
                                                                    String respondent_hoh_name,
                                                                    String survey_date,
                                                                    String survey_time,
                                                                    String type_of_other_structure,
                                                                    String respondent_remark, String member_non_available_remarks,
                                                                    String pavti_no, String year,
                                                                    String country,
                                                                    String state,
                                                                    String city,
                                                                    String accessUnit,
                                                                    double rcResArea,
                                                                    double rcCommArea,
                                                                    String mobile_no_for_otp,
                                                                    int otp_sent,
                                                                    int otp_received,
                                                                    short otp_verified,
                                                                    String otp_remarks, String otp_remarks_other,
                                                                    String thumb_remarks,

                                                                    String structure_type_religious,
                                                                    String structure_religious_other,
                                                                    String structure_type_amenities,
                                                                    String structure_amenities_other,
                                                                    String name_of_structure,
                                                                    String type_of_diety,
                                                                    String type_of_diety_other,
                                                                    String name_of_diety,
                                                                    String category_of_faith,
                                                                    String category_of_faith_other,
                                                                    String sub_category_of_faith,
                                                                    String religion_of_structure_belongs,
                                                                    String structure_ownership_status,
                                                                    String name_of_trust_or_owner,
                                                                    String nature_of_structure,
                                                                    String construction_material,
                                                                    String daily_visited_people_count,
                                                                    String tenement_number_rel_amenities,
                                                                    String tenement_doc_used,
                                                                    String tenement_doc_other,
                                                                    String is_structure_registered,
                                                                    String structure_registered_with,
                                                                    String other_religious_authority,
                                                                    String name_of_trustee,
                                                                    String name_of_landowner,
                                                                    String noc_from_landlord_or_govt,
                                                                    String approval_from_govt,
                                                                    String yearly_festival_conducted,
                                                                    String survey_pavti_no_rel_amenities,
                                                                    String mashal_rel_amenities,
                                                                    String ra_total_no_of_floors,double latitude,double longitude,String genesys_device_name,String primary_imei_no,String second_imei_no,short is_drppl_officer_available,String drppl_officer_name

    ) throws ParseException {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("mobile_no_for_otp", mobile_no_for_otp);
        attributes.put("otp_sent", otp_sent);
        attributes.put("otp_received", otp_received);
        attributes.put("otp_verified", otp_verified);
        attributes.put("otp_remarks", otp_remarks);
        attributes.put("otp_remarks_other", otp_remarks_other);

        attributes.put("thumb_remarks", thumb_remarks);

        // attributes.put("structure_year", Utils.getSplittedString(structure_year, 247));
        attributes.put("unit_status", unit_status);
        attributes.put("media_captured_cnt", media_captured_cnt);
        attributes.put("media_uploaded_cnt", media_uploaded_cnt);
        attributes.put("surveyor_name", Utils.getSplittedString(surveyor_name, 247));

        attributes.put("respondent_available", member_available);
        attributes.put("remarks", Utils.getSplittedString(remarks, 247));
        attributes.put("remarks_other", Utils.getSplittedString(remarks_other, 150));
        attributes.put("respondent_non_available_remark", Utils.getSplittedString(member_non_available_remarks, 247));

        attributes.put("rel_globalid", rel_globalid);
        attributes.put("structure_year", Utils.getSplittedString(structure_year, 250));
        // attributes.put("nature_of_activity", "unit");
        //  attributes.put("unit_no", Utils.getSplittedString(unit_no, 10));
        attributes.put("hut_id", Utils.getSplittedString(hut_number, 25));
        attributes.put("unit_id", Utils.getSplittedString(unit_id, 50));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        if (unit_usage.equals(Constants.ReligiousCheckBox)) {

        } else if (unit_usage.equals(Constants.AmenitiesCheckBox)) {

        } else {
            attributes.put("tenement_number", Utils.getSplittedString(tenement_number, 25));
            attributes.put("tenement_document", tenement_document);//
            attributes.put("mashal_survey_number", mashal_survey_number);//
            try {
                if (pavti_no != null) {
                    attributes.put("survey_pavti_no", "" + pavti_no);
                }
            } catch (Exception ex) {
                ex.getCause();
            }
        }
        attributes.put("unit_usage", unit_usage);
        attributes.put("floor", floor);


        /*
        new date changes
         */
        try {
            if (isUploaded) {
//                attributes.put("existence_since", null);
//                existence_since.add(Calendar.MINUTE,330);
                attributes.put("existence_since", existence_since.getTimeInMillis());
            } else {
//                existence_since.add(Calendar.MINUTE,330);
                attributes.put("existence_since", existence_since);
            }
        } catch (Exception ex) {
            attributes.put("existence_since", null);
            ex.getCause();
        }
        attributes.put("unit_area_sqft", area_sq_ft);
        attributes.put("surveyor_desig", surveyor_desig);
        attributes.put("drp_officer_name", drp_officer_name);
        attributes.put("drp_officer_name_other", drp_officer_name_other);
        attributes.put("drp_officer_desig", drp_officer_desig);
        attributes.put("drp_officer_desig_other", drp_officer_desig_other);
        attributes.put("visit_count", visit_count);
        attributes.put("area_name", area_name);//ERROR
        attributes.put("ward_no", ward_no);//ERROR
        attributes.put("sector_no", sector_no);//ERROR
        attributes.put("zone_no", zone_no);//ERROR
        attributes.put("nagar_name", nagar_name);
        attributes.put("nagar_name_other", nagar_name_other);
        attributes.put("society_name", society_name);
        attributes.put("street_name", street_name);
        attributes.put("landmark_name", landmark_name);
        attributes.put("respondent_name", respondent_name);
        attributes.put("respondent_dob", respondent_dob);//
        attributes.put("respondent_age", respondent_age);//
        if (Integer.parseInt(respondent_age) < 18) {
            attributes.put("respondent_remarks", respondent_remark);//
        }
        attributes.put("respondent_hoh_contact", respondent_hoh_contact);
        attributes.put("respondent_hoh_relationship", respondent_hoh_relationship);
        attributes.put("respondent_hoh_rel_other", respondent_hoh_relationship_other);

        attributes.put("ownership_status", ownership_status);

        attributes.put("loft_present", loft_present);//
//        attributes.put("unit_area_sqft",unit_area_mtrs);//
        attributes.put("ghumasta_area_sqft", ghumasta_area_mtrs);
        attributes.put("loft_area_sqft", loft_area_mtrs);//
        if (employees_count != null && !employees_count.equalsIgnoreCase("")) {
            attributes.put("employees_count", Short.parseShort(employees_count));
        } else {
            attributes.put("employees_count", null);
        }
        attributes.put("unit_unique_id", unit_unique_id);
        //attributes.put("nature_of_activity", "nature_of_activity");
        attributes.put("pincode", pincode);
        attributes.put("respondent_mobile", contact);
        if (isUploaded) {
//            visit_date.add(Calendar.MINUTE,330);
            attributes.put("visit_date", visit_date.getTimeInMillis());
        } else {
//            visit_date.add(Calendar.MINUTE,330);
            attributes.put("visit_date", visit_date);
        }
        attributes.put("form_lock", form_lock);
        attributes.put("respondent_hoh_name", respondent_hoh_name);

        if (!isUploaded) {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(survey_date + " 00:00 aa");
            TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
            Locale loc = new Locale("en", "IN");
            Calendar calendar = Calendar.getInstance(loc);
            GregorianCalendar gc = (GregorianCalendar) calendar;
            gc.setTimeZone(tz);
            gc.setTime(date);
//            gc.add(Calendar.MINUTE,330);
            attributes.put("survey_date", gc);
        }
        attributes.put("type_of_other_structure", type_of_other_structure);
        if (unit_status.equals(Constants.completed_statusLayer) || unit_status.equals(Constants.completed_dispute)) {
            if (isUploaded) {
                GregorianCalendar gg = new GregorianCalendar();
//                gg.add(Calendar.MINUTE,330);
                attributes.put("survey_end_date", "" + gg.getTimeInMillis());
            } else {
                GregorianCalendar gg = new GregorianCalendar();
//                gg.add(Calendar.MINUTE,330);
                attributes.put("survey_end_date", "" + gg.getTimeInMillis());
            }
        }


        try {
            if (year != null) {
                if (!year.equalsIgnoreCase("")) {
                    attributes.put("existence_since_year", Short.parseShort(year));
                } else {
                    attributes.put("existence_since_year", +Short.parseShort("0"));
                }
            }
        } catch (Exception ex) {
            ex.getCause();
        }

        attributes.put("country_name", country);
        attributes.put("state_name", state);
        attributes.put("city_name", city);
        attributes.put("access_to_unit", accessUnit);
        attributes.put(Constants.RcResArea, rcResArea);
        attributes.put(Constants.RcCommArea, rcCommArea);

        //Religious & Amenities
        if (unit_usage.equalsIgnoreCase(Constants.ReligiousCheckBox) || unit_usage.equalsIgnoreCase(Constants.AmenitiesCheckBox)) {
            try {
                if (unit_usage.equalsIgnoreCase(Constants.ReligiousCheckBox)) {
                    attributes.put(Constants.structure_type_religious, structure_type_religious);
                    attributes.put(Constants.structure_religious_other, structure_religious_other);
                }

                if (unit_usage.equalsIgnoreCase(Constants.AmenitiesCheckBox)) {
                    attributes.put(Constants.structure_type_amenities, structure_type_amenities);
                    attributes.put(Constants.structure_amenities_other, structure_amenities_other);
                }


//                if(unit_usage.equalsIgnoreCase(Constants.ReligiousCheckBox)){
                if (type_of_diety != null) {
                    attributes.put(Constants.type_of_diety, type_of_diety);
                }
                if (type_of_diety_other != null) {
                    attributes.put(Constants.type_of_diety_other, type_of_diety_other);
                }
                if (name_of_diety != null) {
                    attributes.put(Constants.name_of_diety, name_of_diety);
                }
                if (category_of_faith != null) {
                    attributes.put(Constants.category_of_faith, category_of_faith);
                }
                if (category_of_faith_other != null) {
                    attributes.put(Constants.category_of_faith_other, category_of_faith_other);
                }
                if (sub_category_of_faith != null) {
                    attributes.put(Constants.sub_category_of_faith, sub_category_of_faith);
                }
                if (religion_of_structure_belongs != null) {
                    attributes.put(Constants.religion_of_structure_belongs, religion_of_structure_belongs);
                }


                try {
                    if (daily_visited_people_count != null && !daily_visited_people_count.equals("")) {
                        attributes.put(Constants.daily_visited_people_count, Integer.parseInt(daily_visited_people_count));
                    } else {
                        attributes.put(Constants.daily_visited_people_count, 0);
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }

                try {
                    if (is_structure_registered != null && !is_structure_registered.equals("") && is_structure_registered.equalsIgnoreCase("yes")) {
                        attributes.put(Constants.is_structure_registered, (short) 1);
                    } else {
                        attributes.put(Constants.is_structure_registered, (short) 0);
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }

                if (structure_registered_with != null) {
                    attributes.put(Constants.structure_registered_with, structure_registered_with);
                }
                if (other_religious_authority != null) {
                    attributes.put(Constants.other_religious_authority, other_religious_authority);
                }
                if (name_of_trustee != null) {
                    attributes.put(Constants.name_of_trustee, name_of_trustee);
                }

//                }

                if (name_of_structure != null) {
                    attributes.put(Constants.name_of_structure, name_of_structure);
                }
                if (structure_ownership_status != null) {
                    attributes.put(Constants.structure_ownership_status, structure_ownership_status);
                }
                if (name_of_trust_or_owner != null) {
                    attributes.put(Constants.name_of_trust_or_owner, name_of_trust_or_owner);
                }
                if (nature_of_structure != null) {
                    attributes.put(Constants.nature_of_structure, nature_of_structure);
                }
                if (construction_material != null) {
                    attributes.put(Constants.construction_material, construction_material);
                }
                if (name_of_landowner != null) {
                    attributes.put(Constants.name_of_landowner, name_of_landowner);
                }

                try {
                    if (noc_from_landlord_or_govt != null && !noc_from_landlord_or_govt.equals("") && noc_from_landlord_or_govt.equalsIgnoreCase("yes")) {
                        attributes.put(Constants.noc_from_landlord_or_govt, (short) 1);
                    } else {
                        attributes.put(Constants.noc_from_landlord_or_govt, (short) 0);
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }

                try {
                    if (approval_from_govt != null && !approval_from_govt.equals("") && approval_from_govt.equalsIgnoreCase("yes")) {
                        attributes.put(Constants.approval_from_govt, (short) 1);
                    } else {
                        attributes.put(Constants.approval_from_govt, (short) 0);
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }

                try {
                    if (yearly_festival_conducted != null && !yearly_festival_conducted.equals("") && yearly_festival_conducted.equalsIgnoreCase("yes")) {
                        attributes.put(Constants.yearly_festival_conducted, (short) 1);
                    } else {
                        attributes.put(Constants.yearly_festival_conducted, (short) 0);
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }

                try {
                    if (ra_total_no_of_floors != null && !ra_total_no_of_floors.equals("")) {
                        attributes.put(Constants.ra_total_no_of_floors_txt, Short.valueOf(ra_total_no_of_floors));
                    } else {
                        attributes.put(Constants.ra_total_no_of_floors_txt, (short) 0);
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }

                if (tenement_number_rel_amenities != null) {
                    attributes.put("tenement_number", tenement_number_rel_amenities);
                }
                if (tenement_doc_used != null) {
                    attributes.put("tenement_document", tenement_doc_used);//
                }
                if (mashal_rel_amenities != null) {
                    attributes.put("mashal_survey_number", mashal_rel_amenities);//
                }
                if (tenement_doc_other != null) {
                    attributes.put(Constants.tenement_document_other, tenement_doc_other);
                }

                try {
                    if (survey_pavti_no_rel_amenities != null) {
                        attributes.put("survey_pavti_no", "" + survey_pavti_no_rel_amenities);
                    }
                } catch (Exception ex) {
                    ex.getCause();
                }
            } catch (Exception ex) {
                ex.getMessage();
            }
        }

        attributes.put("latitude",latitude);
        attributes.put("longitude",longitude);
        attributes.put("genesys_device_name",genesys_device_name);
        attributes.put("primary_imei_no",primary_imei_no);
        attributes.put("second_imei_no",second_imei_no);
        attributes.put("is_drppl_officer_available",(short)is_drppl_officer_available);
        attributes.put("drppl_officer_name",drppl_officer_name);

        return attributes;
    }


    public static Map<String, Object> getUnitInfoDetailsAttributeV4(Object rel_globalid,
                                                                    String unit_id,
                                                                    String unit_unique_id,
                                                                    String relative_path,
                                                                    String hut_number,
                                                                    String floor,
                                                                    String unit_no,
                                                                    String nature_of_activity,
                                                                    String member_available,
                                                                    String unit_status,
                                                                    String surveyor_name,
                                                                    String surveyor_desig,
                                                                    String drp_officer_name,
                                                                    String drp_officer_name_other,
                                                                    String drp_officer_desig,
                                                                    String drp_officer_desig_other,
                                                                    String remarks,
                                                                    String remarks_other,
                                                                    short visit_count,
                                                                    String area_name,
                                                                    String ward_no,
                                                                    String sector_no,
                                                                    String zone_no,
                                                                    String nagar_name,
                                                                    String nagar_name_other,
                                                                    String society_name,
                                                                    String street_name,
                                                                    String landmark_name,
                                                                    String pincode,
                                                                    GregorianCalendar visit_date,
                                                                    short form_lock,
                                                                    String survey_date,
                                                                    String survey_time,
                                                                    boolean isUploaded, String country,
                                                                    String state,
                                                                    String city,
                                                                    String accessUnit,
                                                                    double rcResArea,
                                                                    double rcCommArea,
                                                                    String mobile_no_for_otp,
                                                                    int otp_sent,
                                                                    int otp_received,
                                                                    short otp_verified,
                                                                    String otp_remarks, String otp_remarks_other,
                                                                    String thumb_remarks,double latitude,double longitude,String genesys_device_name,String primary_imei_no,String second_imei_no,short is_drppl_officer_available,String drppl_officer_name
    ) throws ParseException {
        Map<String, Object> attributes = new HashMap<>();

        if(unit_status.equalsIgnoreCase(Constants.NotApplicable_statusLayer)){
            attributes.put("gen_qc_status",Constants.NotApplicable_statusLayer);
        }

        attributes.put("mobile_no_for_otp", mobile_no_for_otp);
        attributes.put("otp_sent", otp_sent);
        attributes.put("otp_received", otp_received);
        attributes.put("otp_verified", otp_verified);
        attributes.put("otp_remarks", otp_remarks);
        attributes.put("otp_remarks_other", otp_remarks_other);

        attributes.put("thumb_remarks", thumb_remarks);

        attributes.put("unit_status", unit_status);
        attributes.put("surveyor_name", Utils.getSplittedString(surveyor_name, 247));

        attributes.put("respondent_available", member_available);
        attributes.put("remarks", Utils.getSplittedString(remarks, 247));
        attributes.put("remarks_other", Utils.getSplittedString(remarks_other, 150));
        attributes.put("respondent_non_available_remark", Utils.getSplittedString(remarks, 247));

        attributes.put("rel_globalid", rel_globalid);
        // attributes.put("nature_of_activity", "unit");
        //attributes.put("unit_no", Utils.getSplittedString(unit_no, 10));
        attributes.put("hut_id", Utils.getSplittedString(hut_number, 25));
        attributes.put("unit_id", Utils.getSplittedString(unit_id, 50));
        attributes.put("relative_path", Utils.getSplittedString(relative_path, 150));
        attributes.put("floor", floor);
        attributes.put("surveyor_desig", surveyor_desig);
        attributes.put("drp_officer_name", drp_officer_name);
        attributes.put("drp_officer_name_other", drp_officer_name_other);
        attributes.put("drp_officer_desig", drp_officer_desig);
        attributes.put("drp_officer_desig_other", drp_officer_desig_other);
        attributes.put("visit_count", visit_count);
        attributes.put("area_name", area_name);//ERROR
        attributes.put("ward_no", ward_no);//ERROR
        attributes.put("sector_no", sector_no);//ERROR
        attributes.put("zone_no", zone_no);//ERROR
        attributes.put("nagar_name", nagar_name);
        attributes.put("nagar_name_other", nagar_name_other);
        attributes.put("society_name", society_name);
        attributes.put("street_name", street_name);
        attributes.put("landmark_name", landmark_name);
        attributes.put("unit_unique_id", unit_unique_id);
        //attributes.put("nature_of_activity", "nature_of_activity");
        attributes.put("pincode", pincode);
        if (isUploaded) {
//            visit_date.add(Calendar.MINUTE,330);
            attributes.put("visit_date", visit_date.getTimeInMillis());
        } else {
//            visit_date.add(Calendar.MINUTE,330);
            attributes.put("visit_date", visit_date);
        }
        attributes.put("form_lock", form_lock);
        attributes.put("unit_area_sqft", 0.0);


        if (!isUploaded) {
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(survey_date + " 00:00 aa");
            TimeZone tz = TimeZone.getTimeZone("GMT-5:30");
            Locale loc = new Locale("en", "IN");
            Calendar calendar = Calendar.getInstance(loc);
            GregorianCalendar gc = (GregorianCalendar) calendar;
            gc.setTimeZone(tz);
            gc.setTime(date);
//            gc.add(Calendar.MINUTE,330);
            attributes.put("survey_date", gc);
            //attributes.put("survey_time", gc);
        }
        if (unit_status.equals(Constants.completed_statusLayer) || unit_status.equals(Constants.completed_dispute)) {
            if (isUploaded) {
                GregorianCalendar gg = new GregorianCalendar();
//                gg.add(Calendar.MINUTE,330);
                attributes.put("survey_end_date", "" + gg.getTimeInMillis());
            } else {
                GregorianCalendar gg = new GregorianCalendar();
//                gg.add(Calendar.MINUTE,330);
                attributes.put("survey_end_date", "" + gg.getTimeInMillis());
            }
        }

        attributes.put("country_name", country);
        attributes.put("state_name", state);
        attributes.put("city_name", city);
        attributes.put("access_to_unit", accessUnit);
        attributes.put(Constants.RcResArea, rcResArea);
        attributes.put(Constants.RcCommArea, rcCommArea);

        attributes.put("latitude",latitude);
        attributes.put("longitude",longitude);
        attributes.put("genesys_device_name",genesys_device_name);
        attributes.put("primary_imei_no",primary_imei_no);
        attributes.put("second_imei_no",second_imei_no);
        attributes.put("is_drppl_officer_available",(short)is_drppl_officer_available);
        attributes.put("drppl_officer_name",drppl_officer_name);

        return attributes;
    }


    //-----------------------------------------------------


    public static String getLogFilePath(String userName) {
        String fileName = "NA";
        try {
            String[] days = new String[]{"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            fileName = userName + "_" + days[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ".log";
        } catch (Exception ex) {

        }
        return fileName;
    }

    public static String generateLogFilePath(String filePrefix) {
        String fileName = "NA";
        try {
            fileName = getCurrentDate("yyyyMMddHHmmssSSS") + "_" + filePrefix + ".log";
        } catch (Exception ex) {

        }
        return fileName;
    }


    public static ArrayList<AutoCompleteModal> getDomianList(String domainName) {
        ArrayList<AutoCompleteModal> list = new ArrayList<>();
        try {
            for (int i = 0; i < App.getInstance().getDomainModel().data.size(); i++) {
                if (App.getInstance().getDomainModel().data.get(i).domainName.equalsIgnoreCase(domainName)) {
                    list.add(new AutoCompleteModal(App.getInstance().getDomainModel().data.get(i).code, App.getInstance().getDomainModel().data.get(i).description));
                }
            }

            list.sort((s1, s2) -> s1.description.compareToIgnoreCase(s2.description));

        } catch (Exception ex) {
            AppLog.e("Exception in getDomianList::" + ex.getMessage());
        }
        return list;
    }

    public static String getTextByTag(String domainName, String tagValue) {
        ArrayList<AutoCompleteModal> list = new ArrayList<>();
        try {
            for (int i = 0; i < App.getInstance().getDomainModel().data.size(); i++) {
                if (App.getInstance().getDomainModel().data.get(i).domainName.equalsIgnoreCase(domainName) && App.getInstance().getDomainModel().data.get(i).code.equalsIgnoreCase(tagValue)) {
                    return App.getInstance().getDomainModel().data.get(i).description;
                }
            }

        } catch (Exception ex) {
            AppLog.e("Exception in getDomianList::" + ex.getMessage());
        }
        return "";
    }

//    public static void convertH263ToH264(Activity activity, String inputPath, String outputPath) {
//        updateProgressMsg("Loading....", activity);
//        Thread backgroundThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // Perform background task here
////                doBackgroundTask();
//                //        try {
//                String[] cmd = new String[]{"-y", "-i", inputPath, "-c:v", "libx264", outputPath};
//                int  rc = FFmpeg.execute(cmd);
//
//                // Check if FFmpeg command was successful
//                if (rc == Config.RETURN_CODE_SUCCESS) {
//                    // Conversion successful
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
////                            Toast.makeText(activity, "Conversion complete", Toast.LENGTH_SHORT).show();
//                            Log.d("FFmpeg", "Conversion successful!"+inputPath+" output is"+outputPath.toString());
//
//                            renameFile(activity, outputPath, inputPath);
//                        }
//                    });
//
////            Toast.makeText(this, "Conversion complete"+rename(new File(outputPath),new File(inputPath))+outputPath.toString(), Toast.LENGTH_SHORT).show();
//
//                } else {
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            // Conversion failed
//                            dismissProgress();
//                            Log.e("FFmpeg", "Conversion failed with rc=" + rc);
//                            Toast.makeText(activity, "Conversion failed", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//
//                }
//            }
//
//        });
//        backgroundThread.start();
//
//
//    }
//
//
//
//    public static void renameFile(Activity activity, String currentFilePath, String newFileName) {
//        File currentFile = new File(currentFilePath);
//
//        // Check if the file exists
//        if (currentFile.exists()) {
//            // Get the parent directory
//            File parentDir = currentFile.getParentFile();
//            if (parentDir != null) {
//                // Create a new File object with the new file name in the same directory
//                File f = new File(newFileName);
//                File newFile = new File(parentDir, f.getName());
//
//                // Rename the file
//                if (currentFile.renameTo(newFile)) {
//                    // File renamed successfully
//                    Log.d("File", "File renamed successfully.");
////                    Toast.makeText(activity, "File renamed successfully", Toast.LENGTH_SHORT).show();
//                    dismissProgress();
//                } else {
//                    dismissProgress();
//                    // Failed to rename the file
//                    Log.e("File", "Failed to rename the file.");
//                    Toast.makeText(activity, "Failed to rename the file", Toast.LENGTH_SHORT).show();
//
//                }
//            } else {
//                dismissProgress();
//                // Failed to get parent directory
//                Toast.makeText(activity, "Failed to get parent directory", Toast.LENGTH_SHORT).show();
//
//                Log.e("File", "Failed to get parent directory.");
//            }
//        } else {
//            dismissProgress();
//            // File does not exist
//            Toast.makeText(activity, "File does not exist.", Toast.LENGTH_SHORT).show();
//
//            Log.e("File", "File does not exist.");
//        }
//    }

    public static String generateZipFile(Activity activity) {
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmmss");
            String formattedDate = df.format(c.getTime());
            List filePathArray = new ArrayList();
            String zipFileName = Environment.getExternalStorageDirectory() + "/DRP_LOG_" + formattedDate + ".zip";
            File directoryPath = new File(activity.getExternalFilesDir("logs").toString());
            int i = 0;
            for (File file : directoryPath.listFiles()) {
                filePathArray.add(file.getAbsolutePath().toString());
                i++;
            }
            if (zip(filePathArray, zipFileName)) {
                return zipFileName;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean zip(List _files, String zipFileName) {
        try {
            int BUFFER = 1024;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.size(); i++) {
                Log.v("Compress", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i).toString());
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files.get(i).toString().substring(_files.get(i).toString().lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean zipUpload(List _files, JSONArray obj, String zipFileName) {
        try {
            int BUFFER = 1024;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];


            ZipEntry e = new ZipEntry("DRP_DATA.json");
            out.putNextEntry(e);

            byte[] data1 = obj.toString().getBytes();
            out.write(data1, 0, data.length);
            out.closeEntry();

            for (int i = 0; i < _files.size(); i++) {
                Log.v("Compress", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i).toString());
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files.get(i).toString().substring(_files.get(i).toString().lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }

                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void deleteRecentClickedImage(Context context) {
        try {
            // Define the content resolver
            ContentResolver contentResolver = context.getContentResolver();

            // Define the URI for querying the MediaStore for images
            Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            // Define the projection (columns to fetch)
            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};

            // Define the sort order to get the most recent image first
            String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT 1";

            // Query the MediaStore for the most recent image
            Cursor cursor = contentResolver.query(imagesUri, projection, null, null, sortOrder);

            if (cursor != null && cursor.moveToFirst()) {
                // Get the image ID and path
                long imageId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                // Delete the image
                Uri imageUri = Uri.withAppendedPath(imagesUri, Long.toString(imageId));
                int rowsDeleted = contentResolver.delete(imageUri, null, null);

                if (rowsDeleted > 0) {
                    // Image deleted successfully
                    // You may also want to delete the image file from storage using the imagePath
                    // However, since we're not directly dealing with the file here, we can't delete it directly
                } else {
                    // Failed to delete image
                }
            } else {
                // No image found
            }

            // Close the cursor
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ex) {
            ex.getCause();
        }
    }

    public static void disableCopyPaste(EditText editText) {

        editText.setTextIsSelectable(false);
        editText.setLongClickable(false);
        editText.setOnLongClickListener(view -> true);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        };

        editText.setCustomInsertionActionModeCallback(actionModeCallback);
        editText.setCustomSelectionActionModeCallback(actionModeCallback);
    }

    public static String capitalizeWords(String input) {
        // split the input string into an array of words
        String[] words = input.split("\\s");

        // StringBuilder to store the result
        StringBuilder result = new StringBuilder();

        // iterate through each word
        for (String word : words) {
            // capitalize the first letter, append the rest of the word, and add a space
            result.append(Character.toTitleCase(word.charAt(0)))
                    .append(word.substring(1)).append(" ");
        }

        // convert StringBuilder to String and trim leading/trailing spaces
        return result.toString().trim();
    }

    public static String capitalizeEachWord(String text) {

        String originalString = text;
        String[] words = originalString.split("\\s");
        StringBuilder capitalizedString = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalizedString.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1)).append(" ");
            }
        }

        return capitalizedString.toString().trim();
    }

    public static String getAttachmentFileName(String documentType) {
        return getAttachmentFileName("", documentType);
    }

    public static String getAttachmentFileName(String name, String documentType) {

        String finalName = "";

        if (!name.isEmpty()) {
            finalName += name.replaceAll(" ", "_");
            finalName += "_";
        }

        if (!documentType.isEmpty()) {
            finalName += getShortName(documentType);
            // finalName += documentType.replaceAll(" ", "_");
        }

        finalName = finalName + "_" + getEpochDateStamp();

        return finalName;
    }

    public static String getShortName(String docName) {

        try {

            String shortNameJson = App.getSharedPreferencesHandler().getShortNameJson();

            if (shortNameJson != null && !shortNameJson.isEmpty()) {

                JSONObject jObjShortName = new JSONObject(shortNameJson);

                if (jObjShortName.has(docName)) {
                    return jObjShortName.getString(docName);
                }
            }
        } catch (Exception ex) {
            AppLog.e("Exception in getShortName::" + ex.getMessage());
        }

        return docName.replaceAll(" ", "_").toLowerCase();
    }

    public static boolean isValidWorkAreaDate(Date startStrDate, Date endStrDate) {

        try {

            Date startDate = convertDateToDDMMYYYY(startStrDate);
            Date endDate = convertDateToDDMMYYYY(endStrDate);
            Date currentDate = convertDateToDDMMYYYY(new Date());

            if ((currentDate.equals(startDate) || currentDate.after(startDate))
                    && (currentDate.equals(endDate) || currentDate.before(endDate))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e(e.getMessage());
        }

        return false;
    }

    public static boolean isDateBetweenDateRange(Date startDate, Date endDate, Date currentDate) {
        return currentDate.after(startDate) && currentDate.before(endDate);
    }

    public static ArrayList<AutoCompleteModal> getDrpOfficerDetails() {
        ArrayList<AutoCompleteModal> list = new ArrayList<>();
        try {
            String drpOfficerDetailsString = App.getSharedPreferencesHandler().getDRPOfficerDetails();
            if (drpOfficerDetailsString != null && !drpOfficerDetailsString.isEmpty()) {
                JSONObject jObjOfficerDetails = new JSONObject(drpOfficerDetailsString);
                JSONArray jArrDrpOfficers = jObjOfficerDetails.getJSONArray("drp_officers");
                for (int i = 0; i < jArrDrpOfficers.length(); i++) {
                    list.add(new AutoCompleteModal(jArrDrpOfficers.getJSONObject(i).getString("designation"),
                            jArrDrpOfficers.getJSONObject(i).getString("name")));
                }
            }
        } catch (Exception ex) {
            AppLog.e("Exception in get DRP officer Name::" + ex.getMessage());
        }
        return list;
    }

    public static String formattedDateToYear(Activity activity, String receivedDate) {
        String displayDate = "";
        try {
            long timestamp = extractTimestamp("" + receivedDate);
            TimeZone timeZone = TimeZone.getTimeZone("IST");
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTimeInMillis(timestamp);
            displayDate = String.valueOf(calendar.get(Calendar.YEAR));

        } catch (Exception e) {
            AppLog.logData(activity, e.getMessage());
            // Utils.shortToast("Exception in formattedDate:"+e.getMessage(),activity);
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

//    public static ArrayList<AutoCompleteModal> getDRPOfficerDesignationByName(String name) {
//        ArrayList<AutoCompleteModal> list = new ArrayList<>();
//        try {
//            String drpOfficerDesignationString = App.getSharedPreferencesHandler().getDRPOfficersDesignationJson();
//            if (drpOfficerDesignationString != null && !drpOfficerDesignationString.isEmpty()) {
//                JSONObject drpofficerJson = new JSONObject(drpOfficerDesignationString);
//                JSONArray drpOfficerArray=drpofficerJson.getJSONArray("drp_officers");
//                for (int i=0;i<drpOfficerArray.length();i++) {
//                    if(drpOfficerArray.getJSONObject(i).getString("name")==name || drpOfficerArray.getJSONObject(i).getString("name").equalsIgnoreCase(name)) {
//                        list.add(new AutoCompleteModal(drpOfficerArray.getJSONObject(i).getString("designation"), drpOfficerArray.getJSONObject(i).getString("designation")));
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            AppLog.e("Exception in get DRP officer Designation::" + ex.getMessage());
//        }
//        return list;
//    }

    public static boolean bindConstantValuesFromConfig() {

        EsriConfigModel esriConfigModel = App.getSharedPreferencesHandler().getEsriConfig();

        if (esriConfigModel == null) {
            return false;
        }

        Constants.ESRI_TOKEN_URL = esriConfigModel.ESRI_TOKEN_URL;
        Constants.ESRI_API_KEY = esriConfigModel.ESRI_API_KEY;
        // Constants.ExpiryTokenTime = esriConfigModel.ExpiryTokenTime;
        Constants.clientId = esriConfigModel.clientId;
        Constants.redirectHost = esriConfigModel.redirectHost;
        Constants.redirectUri = esriConfigModel.redirectUri;
        Constants.getPortalUrl = esriConfigModel.getPortalUrl;
        Constants.getPortalItemId = esriConfigModel.getPortalItemId;
        Constants.getPortalUserName = esriConfigModel.getPortalUserName;
        Constants.getPortalPassword = esriConfigModel.getPortalPassword;
        Constants.dir_distometer_video = esriConfigModel.dir_distometer_video;
        Constants.Base_URL = esriConfigModel.Base_URL;
        Constants.UserInfo_FS_BASE_URL_ARC_GIS = esriConfigModel.UserInfo_FS_BASE_URL_ARC_GIS;
        Constants.AppVersion_FS_BASE_URL_ARC_GIS = esriConfigModel.AppVersion_FS_BASE_URL_ARC_GIS;
        Constants.WorkArea_FS_BASE_URL_ARC_GIS = esriConfigModel.WorkArea_FS_BASE_URL_ARC_GIS;
        Constants.WorkArea_MS_BASE_URL_ARC_GIS = esriConfigModel.WorkArea_MS_BASE_URL_ARC_GIS;
        Constants.StructureInfo_FS_BASE_URL_ARC_GIS = esriConfigModel.StructureInfo_FS_BASE_URL_ARC_GIS;
        Constants.StructureInfo_MS_BASE_URL_ARC_GIS = esriConfigModel.StructureInfo_MS_BASE_URL_ARC_GIS;
        Constants.Nagar_Base_url = esriConfigModel.Nagar_Base_url;
        Constants.Upload_Video_Old = esriConfigModel.Upload_Video_Old;
        Constants.Upload_Video = esriConfigModel.Upload_Video;
        Constants.WebFilePath = esriConfigModel.WebFilePath;
        Constants.Video_STAGE_URL = esriConfigModel.Video_STAGE_URL;
        Constants.DOMAIN_DATA_URL = esriConfigModel.DOMAIN_DATA_URL;
        Constants.CONFIG_DATA_URL = esriConfigModel.CONFIG_DATA_URL;
        Constants.SMS_SERVICE_URL = esriConfigModel.SMS_SERVICE_URL;
        Constants.ATTACHMENTS_SHORTNAMES = esriConfigModel.ATTACHMENTS_SHORTNAMES;
        Constants.DRP_OFFICERS_DESIGNATION = esriConfigModel.DRP_OFFICERS_DESIGNATION;
        Constants.ODO_TOKEN_GENERATE = esriConfigModel.ODO_TOKEN_GENERATE;
        Constants.ODO_SECRET_KEY = esriConfigModel.ODO_SECRET_KEY;
        Constants.AppVersion_ENDPOINT = esriConfigModel.AppVersion_ENDPOINT;
        Constants.USER_INFO_ENDPOINT = esriConfigModel.USER_INFO_ENDPOINT;
        Constants.WorkArea_ENDPOINT = esriConfigModel.WorkArea_ENDPOINT;
        Constants.StructureInfo_ENDPOINT = esriConfigModel.StructureInfo_ENDPOINT;
        Constants.Unit_info_ENDPOINT = esriConfigModel.Unit_info_ENDPOINT;
        Constants.Hoh_info_ENDPOINT = esriConfigModel.Hoh_info_ENDPOINT;
        Constants.MemberInfo_ENDPOINT = esriConfigModel.MemberInfo_ENDPOINT;
        Constants.MediaInfo_ENDPOINT = esriConfigModel.MediaInfo_ENDPOINT;
        Constants.MediaDetail_ENDPOINT = esriConfigModel.MediaDetail_ENDPOINT;

        return true;
    }

    public static boolean isValidPdf(String filePath) {
        boolean isValidPdf1 = isValidPdfCheck1(filePath);
        boolean isValidPdf2 = isValidPdfCheck2(filePath);
        boolean isValidPdf3 = isValidPdfCheck3(filePath, false, 1);
        boolean isValidPdf4 = isValidPdfCheck3(filePath, false, 2);
        return isValidPdf1 && isValidPdf2 && isValidPdf3 && isValidPdf4;
    }

    private static boolean isValidPdfCheck1(String filePath) {
        try {
            // TODO: Ajay >> Remove the library post Deployment
            PDDocument.load(new File(filePath));
            return true;
        } catch (Exception ex) {
            AppLog.e(filePath + " >> Pdf Is Corrupted");
            return false;
        }
    }

    private static boolean isValidPdfCheck2(String filePath) {
        File file = new File(filePath);
        try {
            Scanner input = new Scanner(new FileReader(file));
            while (input.hasNextLine()) {
                final String checkline = input.nextLine();
                if (checkline.contains("%PDF-")) {
                    // a match!
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            AppLog.e(filePath + " >> Pdf Is Corrupted");
            return false;
        }

        AppLog.e(filePath + " >> Pdf Is Corrupted");
        return false;
    }

    private static boolean isValidPdfCheck3(String filePath, boolean includeVersion, int checkType) {

        try {

            File file = new File(filePath);

            int offsetStart = includeVersion ? 8 : 5, offsetEnd = 8;
            byte[] bytes = new byte[offsetStart + offsetEnd];
            try (InputStream is = Files.newInputStream(file.toPath())) {
                is.read(bytes, 0, offsetStart); // %PDF-
                is.skip(file.length() - bytes.length); // Skip bytes
                is.read(bytes, offsetStart, offsetEnd); // %%EOF,SP?,CR?,LF?
            }

            return isValidPdfBytesCheck(bytes, checkType);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isValidPdfBytesCheck(byte[] data, int checkType) {
        if (checkType == 1) {
            if (data == null || data.length < 5) return false;
            // %PDF-
            if (data[0] == 0x25 && data[1] == 0x50 && data[2] == 0x44 && data[3] == 0x46 && data[4] == 0x2D) {
                int offset = data.length - 8, count = 0; // check last 8 bytes for %%EOF with optional white-space
                boolean hasSpace = false, hasCr = false, hasLf = false;
                while (offset < data.length) {
                    if (count == 0 && data[offset] == 0x25) count++;
                    if (count == 1 && data[offset] == 0x25) count++;
                    if (count == 2 && data[offset] == 0x45) count++;
                    if (count == 3 && data[offset] == 0x4F) count++;
                    if (count == 4 && data[offset] == 0x46) count++;
                    // Optional flags for meta info
                    if (count == 5 && data[offset] == 0x20) hasSpace = true;
                    if (count == 5 && data[offset] == 0x0D) hasCr = true;
                    if (count == 5 && data[offset] == 0x0A) hasLf = true;
                    offset++;
                }
                if (count == 5) {
                    String version = data.length > 13 ? String.format("%s%s%s", (char) data[5], (char) data[6], (char) data[7]) : "?";
                    System.out.printf("Version : %s | Space : %b | CR : %b | LF : %b%n", version, hasSpace, hasCr, hasLf);
                    return true;
                }
            }
            return false;
        } else {
            String s = new String(data);
            String d = s.substring(data.length - 7, data.length - 1);
            if (data != null && data.length > 4 &&
                    data[0] == 0x25 && // %
                    data[1] == 0x50 && // P
                    data[2] == 0x44 && // D
                    data[3] == 0x46 && // F
                    data[4] == 0x2D) { // -

                if (d.contains("%%EOF")) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isValidateUnitUniqueId(String unitUniqueId) {
        return !unitUniqueId.trim().isEmpty() && !unitUniqueId.trim().equalsIgnoreCase("<Null>") &&
                !unitUniqueId.trim().equalsIgnoreCase("null") && unitUniqueId.trim().length() > 15;
    }

    public static void convertVideo(String inputFilePath, String outputFilePath) {

        /*File source = new File(inputFilePath);
        File target = new File(outputFilePath);
        MultimediaObject multimediaObject = new MultimediaObject(source);

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("aac");
        audio.setBitRate(64000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        VideoAttributes video = new VideoAttributes();
        video.setCodec("h264");
        video.setX264Profile(X264_PROFILE.BASELINE);
        video.setBitRate(160000);
        video.setFrameRate(15);
        video.setSize(new VideoSize(400, 300));

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);

        try {
            Encoder encoder = new Encoder();
            encoder.encode(multimediaObject, target, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}