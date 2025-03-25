package com.igenesys.networks;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.google.gson.Gson;
import com.igenesys.App;
import com.igenesys.model.AddRecordResponse;
import com.igenesys.model.AttachmentAddedResponse;
import com.igenesys.model.AttachmentQueryResult;
import com.igenesys.model.CombineMapQueryModel;
import com.igenesys.model.CountModel;
import com.igenesys.model.NagarListModel;
import com.igenesys.model.ResultQueryModel;
import com.igenesys.model.Root;
import com.igenesys.model.RootDeleteMediaObject;
import com.igenesys.model.Status;
import com.igenesys.model.UpdatedRecordResponse;
import com.igenesys.model.UploadAttachmentResult;
import com.igenesys.model.UploadVideoModel;
import com.igenesys.utils.AppLog;
import com.igenesys.utils.Constants;
import com.igenesys.utils.Utils;
//import com.techaidsolution.gdc_app.model.AddRecordResponse;
//import com.techaidsolution.gdc_app.model.AttachmentAddedResponse;
//import com.techaidsolution.gdc_app.model.AttachmentQueryResult;
//import com.techaidsolution.gdc_app.model.CombineMapQueryModel;
//import com.techaidsolution.gdc_app.model.CountModel;
//import com.techaidsolution.gdc_app.model.NagarListModel;
//import com.techaidsolution.gdc_app.model.ResultQueryModel;
//import com.techaidsolution.gdc_app.model.Root;
//import com.techaidsolution.gdc_app.model.UpdatedRecordResponse;
//import com.techaidsolution.gdc_app.utils.AppLog;
//import com.techaidsolution.gdc_app.utils.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.HttpException;

public class Api_Repository {

    private static Api_Repository Api_Repository;
    private final Api_Interface api_interface_userInfoFS, api_interface_WorkAreaFS, api_interface_WorkAreaMS, api_interface_StructureInfoMS, api_interface_AppVersionFS, api_interface_StructureInfoFS, api_interface_NagarList, api_interface_UploadVideo;

    private Api_Repository() {
        api_interface_userInfoFS = RetrofitService.createRetrofitArcGisUserInfoFS();
        api_interface_WorkAreaFS = RetrofitService.createRetrofitArcGisWorkAreaFS();
        api_interface_WorkAreaMS = RetrofitService.createRetrofitArcGisWorkAreaMS();
        api_interface_StructureInfoFS = RetrofitService.createRetrofitArcGisStructureInfoFS();
        api_interface_StructureInfoMS = RetrofitService.createRetrofitArcGisStructureInfoMS();
        api_interface_AppVersionFS = RetrofitService.createRetrofitArcGisAppVersionInfoFS();
        api_interface_NagarList = RetrofitService.createRetrofitArcGisNagarList();
        api_interface_UploadVideo = RetrofitService.createRetrofitArcGisUploadVideo();
    }

    public static Api_Repository getInstance() {
        if (Api_Repository == null) {
            Api_Repository = new Api_Repository();
        }
        return Api_Repository;
    }

    public MutableLiveData<ResultQueryModel> getQueryResult(String queryFor, String path, Map<String, Object> map) {
        final MutableLiveData<ResultQueryModel> listMutableLiveData = new MutableLiveData<>();

        Api_Interface api_interface = getApi_interface(queryFor);

        if (api_interface != null)
            api_interface.getQueryResult(path, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResultQueryModel>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull ResultQueryModel resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }

    private Api_Interface getApi_interface(String queryFor) {
        Api_Interface api_interface = null;

        if (queryFor.equals(Constants.UserInfo_FS_BASE_URL_ARC_GIS)) {
            api_interface = api_interface_userInfoFS;
        } else if (queryFor.equals(Constants.WorkArea_MS_BASE_URL_ARC_GIS)) {
            api_interface = api_interface_WorkAreaMS;
        } else if (queryFor.equals(Constants.WorkArea_FS_BASE_URL_ARC_GIS)) {
            api_interface = api_interface_WorkAreaFS;
        } else if (queryFor.equals(Constants.StructureInfo_MS_BASE_URL_ARC_GIS)) {
            api_interface = api_interface_StructureInfoMS;
        } else if (queryFor.equals(Constants.StructureInfo_FS_BASE_URL_ARC_GIS)) {
            api_interface = api_interface_StructureInfoFS;
        } else if (queryFor.equals(Constants.AppVersion_FS_BASE_URL_ARC_GIS)) {
            api_interface = api_interface_AppVersionFS;
        } else if (queryFor.equals(Constants.Upload_Video)) {
            api_interface = api_interface_UploadVideo;
        } else if (queryFor.equals("")) {
            api_interface = api_interface_NagarList;
        }

        return api_interface;
    }

    public MutableLiveData<CountModel> getQueryCountResult(String queryFor, String path, Map<String, Object> map) {
        final MutableLiveData<CountModel> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface(queryFor);
        if (api_interface != null)
            api_interface.getQueryCountQueryResult(path, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<CountModel>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull CountModel resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }

    public MutableLiveData<AttachmentAddedResponse> getUploadAttachmentResult(String queryFor, String path, String ojectId,
                                                                              RequestBody format, MultipartBody.Part attachment) {
        final MutableLiveData<AttachmentAddedResponse> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface(queryFor);

        if (api_interface != null)
            api_interface.uploadAttachment(path, ojectId, format, attachment, App.getSharedPreferencesHandler().getEsriToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AttachmentAddedResponse>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull AttachmentAddedResponse resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            Log.v("ApiRepository", "OnError >> " + e.getMessage());
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }

    public MutableLiveData<UploadVideoModel> getUploadVideoResult(String queryFor, String fileName, String fileExt, String partNumbers,
                                                                  String partNumber, String fileSize, String uploadOffset, String uploadLength,
                                                                  String userName, String filePath, RequestBody attachment) {
        final MutableLiveData<UploadVideoModel> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface(queryFor);
        if (api_interface != null)
            api_interface.uploadVideo(fileName, fileExt, partNumbers, partNumber, fileSize, uploadOffset, uploadLength, userName, filePath, attachment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<UploadVideoModel>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull UploadVideoModel resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }

    // New File Upload using Java Service
    public MutableLiveData<UploadAttachmentResult> getUploadAttachmentsResult(Activity activity, String queryFor, String partNumbers, String uploadOffset, String fileSize,
                                                                              String userName, String fileExt, String documentSource, String documentCategory,
                                                                              String documentType, String relGlobalId, String remarks, String fileName,
                                                                              String objectId, RequestBody attachment) {

        final MutableLiveData<UploadAttachmentResult> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface(queryFor);

        if (api_interface != null) {

            AppLog.logCustomData(activity, fileName, "Uploaded Attachment Started");

            api_interface.uploadAttachments(partNumbers, uploadOffset, fileSize, userName, fileExt, documentSource, documentCategory,
                            documentType, relGlobalId, remarks, fileName, objectId, attachment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<UploadAttachmentResult>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull UploadAttachmentResult resultQueryModel) {
                            AppLog.logCustomData(activity, fileName, "Uploaded Attachment Success " + new Gson().toJson(resultQueryModel));
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {

                            try {

                                String errorMessage = "Uploaded Attachment Failed. Server Error " + e.getMessage() + " // " + getErrorMsg(e);

                                AppLog.logCustomData(activity, fileName, errorMessage);

                                Status status = new Status();
                                status.setStatus(-1);
                                status.setCode("SERVER_ERROR");
                                status.setMessage(errorMessage);

                                UploadAttachmentResult result = new UploadAttachmentResult();
                                result.setStatus(status);
                                result.setData(null);

                                listMutableLiveData.setValue(result);
                            } catch (Exception ex) {
                                String errorMessage = "Uploaded Attachment Failed. Server Error Exception " + ex.getMessage();
                                AppLog.logCustomData(activity, fileName, errorMessage);
                                listMutableLiveData.setValue(null);
                            }
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        } else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }

    public MutableLiveData<AttachmentQueryResult> getAttachmentResult(String queryFor, String path, String ojectId) {
        final MutableLiveData<AttachmentQueryResult> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface(queryFor);
        if (api_interface != null)
            api_interface.getAttachmentResult(path, ojectId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AttachmentQueryResult>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull AttachmentQueryResult resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }

    public MutableLiveData<NagarListModel> getNagarResult(String where, String outFields, boolean returnGeometry, boolean returnDistinctValues, String f) {
        final MutableLiveData<NagarListModel> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface("");
        if (api_interface != null)
            api_interface.getNagarResult(where, outFields, returnGeometry, returnDistinctValues, f)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<NagarListModel>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(NagarListModel nagarListModel) {
                            listMutableLiveData.setValue(nagarListModel);

                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }

    public MutableLiveData<CombineMapQueryModel> getCombineMapQueryModel(String structurePath,
                                                                         String hohPath,
                                                                         String memberPath,
                                                                         Map<String, Object> strcutureMap,
                                                                         Map<String, Object> hohMap,
                                                                         Map<String, Object> memeberMap) {
        final MutableLiveData<CombineMapQueryModel> listMutableLiveData = new MutableLiveData<>();

        Observable<ResultQueryModel> resultQueryStructureModelObservable = api_interface_StructureInfoMS.getQueryResult(structurePath, strcutureMap);
        Observable<ResultQueryModel> resultQueryHohModelObservable = api_interface_StructureInfoMS.getQueryResult(hohPath, hohMap);
        Observable<ResultQueryModel> resultQueryMemberModelObservable = api_interface_StructureInfoMS.getQueryResult(memberPath, memeberMap);

        Observable<CombineMapQueryModel> combined = Observable.zip(resultQueryStructureModelObservable,
                resultQueryHohModelObservable,
                resultQueryMemberModelObservable,
                CombineMapQueryModel::new);

        combined.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CombineMapQueryModel>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull CombineMapQueryModel combineMapQueryModel) {
                        listMutableLiveData.setValue(combineMapQueryModel);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        listMutableLiveData.setValue(null);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        return listMutableLiveData;
    }

    public MutableLiveData<UpdatedRecordResponse> getUpdateWorkAreaFeatureResult(String queryFor, String path, Map<String, Object> map) {
        final MutableLiveData<UpdatedRecordResponse> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface(queryFor);
        if (path != null)
            api_interface.updateFeature(path, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<UpdatedRecordResponse>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull UpdatedRecordResponse resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);

                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        return listMutableLiveData;
    }

    public MutableLiveData<AddRecordResponse> getAddFeatureResult(String queryFor, String path, Map<String, Object> map) {
        final MutableLiveData<AddRecordResponse> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface(queryFor);
        if (api_interface != null)
            api_interface.addFeature(path, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AddRecordResponse>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull AddRecordResponse resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }


    public MutableLiveData<AddRecordResponse> getAddStructureInfoFeatureResult(String path, Map<String, Object> map) {
        final MutableLiveData<AddRecordResponse> listMutableLiveData = new MutableLiveData<>();
        if (path != null)
            api_interface_WorkAreaFS.addFeature(path.replace(Constants.StructureInfo_MS_BASE_URL_ARC_GIS, ""), map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AddRecordResponse>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull AddRecordResponse resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        return listMutableLiveData;
    }

    public MutableLiveData<Boolean> getFeatureAddResult(ServiceFeatureTable serviceFeatureTable,
                                                        Map<String, Object> attributes,
                                                        String geometry) {
        MutableLiveData<Boolean> booleanMutableLiveData = new MutableLiveData<>();

        serviceFeatureTable.addDoneLoadingListener(() -> {
            if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                Feature feature = serviceFeatureTable.createFeature(attributes, Geometry.fromJson(geometry));
                // add the new feature to the feature table and to server
                serviceFeatureTable.addFeatureAsync(feature).addDoneListener(() -> {
                    // apply the changes to the server
                    final ListenableFuture<List<FeatureEditResult>> editResult = serviceFeatureTable.applyEditsAsync();
                    editResult.addDoneListener(() -> {
                        try {
                            List<FeatureEditResult> editResults = editResult.get();
                            // check if the server edit was successful
                            if (editResults != null && !editResults.isEmpty()) {
                                if (!editResults.get(0).hasCompletedWithErrors()) {
                                    editResults.get(0).getObjectId();
                                    editResults.get(0).getGlobalId();
                                    booleanMutableLiveData.setValue(true);
                                } else {
                                    booleanMutableLiveData.setValue(null);

                                    throw editResults.get(0).getError();
                                }
                            } else
                                booleanMutableLiveData.setValue(null);
                        } catch (InterruptedException | ExecutionException e) {
                            booleanMutableLiveData.setValue(null);
                        }
                    });
                });
            }
        });
        return booleanMutableLiveData;
    }

    private String getErrorMsg(Throwable e) {
        String result = "", errorRespose = "", code = "";
        if (e instanceof HttpException) {
            ResponseBody body = Objects.requireNonNull(((HttpException) e).response()).errorBody();
            try {
                String msg = "";
                result = Objects.requireNonNull(body).string();
                JSONObject jsonObjectResponse = new JSONObject(result);
                AppLog.e("Error: " + result);
                if (jsonObjectResponse.has("responseMessage"))
                    msg = jsonObjectResponse.getString("responseMessage");
                else if (jsonObjectResponse.has("errorMessage"))
                    msg = jsonObjectResponse.getString("errorMessage");
                else if (jsonObjectResponse.has("error"))
                    msg = jsonObjectResponse.getString("error");

                if (jsonObjectResponse.has("responseCode"))
                    code = String.valueOf(jsonObjectResponse.getInt("responseCode"));
                else if (jsonObjectResponse.has("errorCode"))
                    code = String.valueOf(jsonObjectResponse.getInt("errorCode"));
                else code = e.getMessage();

                errorRespose = msg + " (" + code + ")";
            } catch (IOException | JSONException ioException) {
                ioException.printStackTrace();
                errorRespose = e.getMessage();
            }
        } else if (e instanceof SocketTimeoutException) {
            errorRespose = "Server Time out. Please try again later.";
        } else {
            errorRespose = e.getMessage();
        }

        return errorRespose + " :: " + result;
    }


    public MutableLiveData<Root> getRemoveFeatureResult(String queryFor, String path, String objectId, Map<String, Object> map) {
        final MutableLiveData<Root> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface(queryFor);
        if (api_interface != null)
            api_interface.deleteAttachment(path, objectId, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Root>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull Root resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }

    public MutableLiveData<RootDeleteMediaObject> getRemoveWholeMediaObjectFeatureResult(String queryFor, String path, Map<String, Object> map) {
        final MutableLiveData<RootDeleteMediaObject> listMutableLiveData = new MutableLiveData<>();
        Api_Interface api_interface = getApi_interface(queryFor);
        if (api_interface != null)
            api_interface.deleteWholeMediaObject(path, map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RootDeleteMediaObject>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull RootDeleteMediaObject resultQueryModel) {
                            listMutableLiveData.setValue(resultQueryModel);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            AppLog.e(getErrorMsg(e));
                            listMutableLiveData.setValue(null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        else listMutableLiveData.setValue(null);

        return listMutableLiveData;
    }

}