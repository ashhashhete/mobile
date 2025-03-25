package com.igenesys.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.igenesys.R;
//import com.techaidsolution.gdc_app.R;

import java.util.ArrayList;
import java.util.List;

public class AppPermissions {

    static int MY_PERMISSIONS_REQUEST_CODE = 2;
    static String[] reqAllPermissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    static String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};
    static String[] reqStroagePermission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static String[] cameraPermissionArray = new String[]{Manifest.permission.CAMERA};

    public static boolean cameraPermission(Activity activity, boolean askPermission) {
        boolean permission = permissionGranted(activity, cameraPermissionArray, askPermission);
        if (askPermission) {
            if (!permission) {
//                Utils.shortToast(activity.getResources().getString(R.string.refused_camera_permission), activity);
            }
        }
        return permission;
    }

    private static boolean permissionGranted(Activity activity, String[] permissionArray, boolean askPermission) {

        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String permission : permissionArray) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (askPermission) {
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[0]), 1);
            }
        }

        return listPermissionsNeeded.isEmpty();//if empty permission granted else false
    }

    public static void checkPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context, Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.permissionRequiredToDoTask));
                builder.setTitle(context.getResources().getString(R.string.pleaseGrantPermission));
                builder.setPositiveButton(context.getResources().getString(R.string.ok), (dialogInterface, i) -> ActivityCompat.requestPermissions(
                        context,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                ));
                builder.setNeutralButton(context.getResources().getString(R.string.cancel), null);
                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(
                        context,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }
    }

    public static void displayLocationSettingsRequest(Activity context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        AppLog.d("All location settings are satisfied.");
//                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        AppLog.d("Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
//                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            int REQUEST_CHECK_SETTINGS = 1;
                            status.startResolutionForResult(context, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            AppLog.d("PendingIntent unable to execute request.");
//                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        AppLog.d("Location settings are inadequate, and cannot be fixed here. Dialog not created.");
//                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    public static boolean checkGpsOn(Activity context) {

        if (!Gpsenable(context)) {
            displayLocationSettingsRequest(context);
            return false;
        } else if (!gpsforapp(context)) {
            checkPermission(context);
            return false;
        } else {
            return true;
        }
    }

    public static Boolean Gpsenable(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && getLocationMode(activity) == 3;
    }

    public static int getLocationMode(Activity activity) {
        try {
            return Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean gpsforapp(Activity activity) {
        boolean permissioncheck1 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), reqPermissions[0]) == PackageManager.PERMISSION_GRANTED;
        boolean permissioncheck2 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), reqPermissions[1]) == PackageManager.PERMISSION_GRANTED;

        return permissioncheck1 && permissioncheck2;
    }

    public static void reqStroagePermission(Activity activity) {
        boolean permissioncheck1 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), reqStroagePermission[0]) == PackageManager.PERMISSION_GRANTED;
        boolean permissioncheck2 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), reqStroagePermission[1]) == PackageManager.PERMISSION_GRANTED;

        if (!permissioncheck1 || !permissioncheck2)
            askForStrgPermission(activity);
    }

    public static boolean checkAllPermission(Activity activity) {
        boolean permissioncheck1 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), reqAllPermissions[0]) == PackageManager.PERMISSION_GRANTED;
        boolean permissioncheck2 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), reqAllPermissions[1]) == PackageManager.PERMISSION_GRANTED;
        boolean permissioncheck3 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), reqAllPermissions[2]) == PackageManager.PERMISSION_GRANTED;

        return permissioncheck1 && permissioncheck2 && permissioncheck3;
    }

    public static void askForStrgPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                MY_PERMISSIONS_REQUEST_CODE
        );
    }

}
