package com.igenesys.networks;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//import com.techaidsolution.gdc_app.model.AddRecordResponse;
//import com.techaidsolution.gdc_app.model.AttachmentAddedResponse;
//import com.techaidsolution.gdc_app.model.AttachmentQueryResult;
//import com.techaidsolution.gdc_app.model.CombineMapQueryModel;
//import com.techaidsolution.gdc_app.model.CountModel;
//import com.techaidsolution.gdc_app.model.NagarListModel;
//import com.techaidsolution.gdc_app.model.ResultQueryModel;
//import com.techaidsolution.gdc_app.model.Root;
//import com.techaidsolution.gdc_app.model.UpdatedRecordResponse;

import com.igenesys.model.AddRecordResponse;
import com.igenesys.model.AttachmentAddedResponse;
import com.igenesys.model.AttachmentQueryResult;
import com.igenesys.model.CombineMapQueryModel;
import com.igenesys.model.CountModel;
import com.igenesys.model.NagarListModel;
import com.igenesys.model.ResultQueryModel;
import com.igenesys.model.Root;
import com.igenesys.model.RootDeleteMediaObject;
import com.igenesys.model.UpdatedRecordResponse;
import com.igenesys.model.UploadAttachmentResult;
import com.igenesys.model.UploadVideoModel;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class QueryResultRepoViewModel extends ViewModel {
    private MutableLiveData<ResultQueryModel> mutableLiveData;
    private MutableLiveData<CountModel> countModelMutableLiveData;
    private MutableLiveData<AttachmentAddedResponse> uplaodAttachmentMutableLiveData;
    private MutableLiveData<UploadVideoModel> uplaodVideoMutableLiveData;
    private MutableLiveData<UploadAttachmentResult> uplaodMediaAttachmentMutableLiveData;
    private MutableLiveData<NagarListModel> nagarListModel;
    private MutableLiveData<AttachmentQueryResult> attachmentQueryResultMutableLiveData;

    private Api_Repository apiRepository;
    private MutableLiveData<UpdatedRecordResponse> updatedWorkAreaMutableLiveData;
    private MutableLiveData<AddRecordResponse> addStructurePointMutableLiveData;
    private MutableLiveData<Root> deleteAttachmentResultsMutableLiveData;
    private MutableLiveData<RootDeleteMediaObject> deleteWholeMediaObjectAttachmentResultsMutableLiveData;

    private MutableLiveData<CombineMapQueryModel> combineMapQueryModelMutableLiveData;

    public void getQueryResult(String queryFor, String path, Map<String, Object> map) {

        apiRepository = Api_Repository.getInstance();
        mutableLiveData = apiRepository.getQueryResult(queryFor, path, map);
    }

    public MutableLiveData<ResultQueryModel> getMutableLiveData() {
        return mutableLiveData;
    }

    public void getQueryCountResult(String queryFor, String path, Map<String, Object> map) {

        apiRepository = Api_Repository.getInstance();
        countModelMutableLiveData = apiRepository.getQueryCountResult(queryFor, path, map);
    }

    public void getUploadAttachmentResult(String queryFor, String path, String ojectId,
                                          RequestBody format, MultipartBody.Part attachment) {

        apiRepository = Api_Repository.getInstance();
        uplaodAttachmentMutableLiveData = apiRepository.getUploadAttachmentResult(queryFor, path, ojectId, format, attachment);
    }

    public void getUploadVideoResult(String queryFor, String fileName, String fileExt,String partNumbers, String partNumber,
                                     String fileSize, String uploadOffset, String uploadLength, String userName,
                                     String filePath, RequestBody file) {

        apiRepository = Api_Repository.getInstance();
        uplaodVideoMutableLiveData = apiRepository.getUploadVideoResult(queryFor, fileName, fileExt, partNumbers, partNumber,
                fileSize, uploadOffset, uploadLength, userName, filePath, file);
    }

    // New File Upload using Java Service
    public void getUploadAttachmentsResult(Activity activity, String queryFor, String partNumbers, String uploadOffset, String fileSize, String userName,
                                           String fileExt, String documentSource, String documentCategory, String documentType, String relGlobalId,
                                           String remarks, String fileName, String objectId, RequestBody file) {

        apiRepository = Api_Repository.getInstance();
        uplaodMediaAttachmentMutableLiveData = apiRepository.getUploadAttachmentsResult(activity, queryFor, partNumbers, uploadOffset, fileSize, userName, fileExt,
                documentSource, documentCategory, documentType, relGlobalId, remarks, fileName, objectId, file);
    }

    public void initCombineMapQueryResult(String structurePath,
                                          String hohPath,
                                          String memberPath,
                                          Map<String, Object> strcutureMap,
                                          Map<String, Object> hohMap,
                                          Map<String, Object> memeberMap
    ) {

        apiRepository = Api_Repository.getInstance();
        combineMapQueryModelMutableLiveData = apiRepository.getCombineMapQueryModel(structurePath, hohPath, memberPath,
                strcutureMap, hohMap, memeberMap);
    }

    public MutableLiveData<CombineMapQueryModel> getCombineMapQueryModelMutableLiveData() {
        return combineMapQueryModelMutableLiveData;
    }

    public MutableLiveData<AttachmentAddedResponse> getUplaodAttachmentMutableLiveData() {
        return uplaodAttachmentMutableLiveData;
    }

    public MutableLiveData<UploadAttachmentResult> getUplaodMediaAttachmentMutableLiveData() {
        return uplaodMediaAttachmentMutableLiveData;
    }

    public MutableLiveData<UploadVideoModel> getUplaodVideoMutableLiveData() {
        return uplaodVideoMutableLiveData;
    }

    public MutableLiveData<CountModel> getCountModelMutableLiveData() {
        return countModelMutableLiveData;
    }

    public void initUpdateFeatureResult(String queryFor, String path, Map<String, Object> map) {

        apiRepository = Api_Repository.getInstance();
        updatedWorkAreaMutableLiveData = apiRepository.getUpdateWorkAreaFeatureResult(queryFor, path, map);
    }

    public void initAddFeatureResult(String queryFor, String path, Map<String, Object> map) {

        apiRepository = Api_Repository.getInstance();
        addStructurePointMutableLiveData = apiRepository.getAddFeatureResult(queryFor, path, map);
    }

    public MutableLiveData<UpdatedRecordResponse> getUpdatedWorkAreaMutableLiveData() {
        return updatedWorkAreaMutableLiveData;
    }

    public MutableLiveData<AddRecordResponse> getAddWorkAreaMutableLiveData() {
        return addStructurePointMutableLiveData;
    }

    public void initNagarResult(String where,String outFields,boolean returnGeometry,boolean returnDistinctValues,String f) {
        apiRepository = Api_Repository.getInstance();
        nagarListModel = apiRepository.getNagarResult(where,outFields,returnGeometry,returnDistinctValues,f );
    }

    public MutableLiveData<NagarListModel> getNagarMutableLiveData() {
        return nagarListModel;
    }

    public void initDeleteFeatureResult(String queryFor, String path, String objectId, Map<String, Object> map) {
        apiRepository = Api_Repository.getInstance();
        deleteAttachmentResultsMutableLiveData = apiRepository.getRemoveFeatureResult(queryFor, path,objectId, map);
    }

    public MutableLiveData<Root> getDeleteAttachmentResultsMutableLiveData() {
        return deleteAttachmentResultsMutableLiveData;
    }

    public void initDeleteWholeMediaObjectFeatureResult(String queryFor, String path, Map<String, Object> map) {
        apiRepository = Api_Repository.getInstance();
        deleteWholeMediaObjectAttachmentResultsMutableLiveData = apiRepository.getRemoveWholeMediaObjectFeatureResult(queryFor, path, map);
    }

    public MutableLiveData<RootDeleteMediaObject> getDeleteWholeMediaObjectAttachmentResultsMutableLiveData() {
        return deleteWholeMediaObjectAttachmentResultsMutableLiveData;
    }
}