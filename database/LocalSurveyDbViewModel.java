package com.igenesys.database;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.igenesys.database.dabaseModel.newDbSModels.AadhaarVerificationData;
import com.igenesys.database.dabaseModel.newDbSModels.DownloadedWebMapModel;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaDetailsDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StatusCount;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureUnitIdStatusDataTable;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.model.AttachmentItemList;
import com.igenesys.utils.AppLog;

import java.util.ArrayList;
import java.util.List;

public class LocalSurveyDbViewModel extends AndroidViewModel {

    private LocalSurveyDbRepository repository;

    public LocalSurveyDbViewModel(@NonNull Application application) {
        super(application);
        repository = new LocalSurveyDbRepository(application);
    }

    // below method is use to insert the data to our repository.
    public void insertStructureInfoPointData(StructureInfoPointDataModel model, Activity activity) {
        repository.insertStructureInfoPointData(model, activity);
    }

    // below method is use to insert the data to our repository.
    public void insertStructureUnitIdStatusData(StructureUnitIdStatusDataTable model, Activity activity) {
        repository.insertStructureUnitIdStatusData(model, activity);
    }

    public void updateStructureInfoPointData(StructureInfoPointDataModel model, Activity activity) {
        repository.updateStructureInfoPointData(model, activity);
    }

    public void deleteStructureInfoData(String model, Activity activity) {
        repository.deleteStructureInfoData(model, activity);
    }

    public void deleteUnitInfoData(String model, Activity activity) {
        repository.deleteUnitInfoData(model, activity);
    }

    public void deleteStructureUnitIdStatusData(String structure_id) {
        repository.deleteStructureUnitIdStatusData(structure_id);
    }

    public void deleteUnitDataById(String unit_id) {
        repository.deleteUnitDataById(unit_id);
    }

    public void deleteHohInfoData(String model, Activity activity) {
        repository.deleteHohInfoData(model, activity);
    }

    public void deleteMemberInfoData(String model, Activity activity) {
        repository.deleteMemberInfoData(model, activity);
    }

    public void deleteMediaInfoData(String model, Activity activity,String cat,String tableName) {
        repository.deleteMediaInfoData(model, activity,cat,tableName);
    }

    public void deleteMediaWithFileNameData(String model, Activity activity) {
        repository.deleteMediaWithFileNameData(model, activity);
    }

    public void deleteBulkStructureInfoData(List<String> model, Activity activity) {
        repository.deleteBulkStructureInfoData(model, activity);
    }

    public void deleteStructureUnitIdStatusDataTable(List<String> model, Activity activity) {
        repository.deleteStructureUnitIdStatusDataTable(model);
    }

    public void deleteBulkUnitInfoData(List<String> model, Activity activity) {
        repository.deleteBulkUnitInfoData(model, activity);
    }

    public void deleteBulkHohInfoData(List<String> model, Activity activity) {
        repository.deleteBulkHohInfoData(model, activity);
    }

    public void deleteBulkMemberInfoData(List<String> model, Activity activity) {
        repository.deleteBulkMemberInfoData(model, activity);
    }

    public void deleteBulkMediaInfoData(List<String> model, Activity activity) {
        repository.deleteBulkMediaInfoData(model, activity);
    }

    public void deleteWebMapInfoData(String webMapId, Activity activity) {
        repository.deleteWebMapInfoData(webMapId, activity);
    }

    public LiveData<List<StructureInfoPointDataModel>> getStructureInfoPointData(String surveyor_name) {
        return repository.getStructureInfoPointData(surveyor_name);
    }

    public LiveData<List<UnitInfoDataModel>> getUnitInfoUploadData(String surveyor_name) {
        return repository.getUnitInfoUploadData(surveyor_name);
    }

    public List<StructureInfoPointDataModel> getStructureInfoPointDataZip(String surveyor_name) {
        return repository.getStructureInfoPointDataZip(surveyor_name);
    }

    public LiveData<List<StructureInfoPointDataModel>> getStructureInfoPointDataWithId(String structure_id) {
        return repository.getStructureInfoPointDataWithId(structure_id);
    }

    public LiveData<List<UnitInfoDataModel>> getUnitInfoPointData(String uniqueId) {
        return repository.getUnitInfoPointData(uniqueId);
    }

    public List<UnitInfoDataModel> getUnitInfoData(String uniqueId) {
        return repository.getUnitInfoData(uniqueId);
    }

    public List<DownloadedWebMapModel> getDownloadedMapInfoData(String userName) {
        return repository.getDownloadedMapInfoData(userName);
    }

    public List<HohInfoDataModel> getHohInfoData(String uniqueId) {
        return repository.getHohInfoData(uniqueId);
    }

    public List<String> getStructureUnitIdStatusListData(String uniqueId) {
        return repository.getStructureUnitIdStatusListData(uniqueId);
    }

    public List<String> getUnitInfoIdData(String uniqueId) {
        return repository.getUnitInfoIdData(uniqueId);
    }

    public List<String> getHohInfoIdData(String uniqueId) {
        return repository.getHohInfoIdData(uniqueId);
    }

    public List<String> getMemberInfoIdData(String uniqueId) {
        return repository.getMemberInfoIdData(uniqueId);
    }

    public List<MemberInfoDataModel> getMemberInfoData(String uniqueId) {
        return repository.getMemberInfoData(uniqueId);
    }
    public List<MemberInfoDataModel> getMemberInfoDataWithRelGlobalId(String relGlobalId) {
        return repository.getMemberInfoDataWithRelGlobalId(relGlobalId);
    }
    public List<MemberInfoDataModel> getMemberInfoDataWithMemberId(String uniqueId) {
        return repository.getMemberInfoDataWithMemberId(uniqueId);
    }

    public StructureInfoPointDataModel getStructureInfoPointDataModel(String uniqueId) {
        return repository.getStructureInfoPointDataModel(uniqueId);
    }

    public List<StructureInfoPointDataModel> getStructureInfoPointDataModelAll() {
        return repository.getStructureInfoPointDataModelAll();
    }

    public List<MediaInfoDataModel> getMediaInfoData(String relativePath) {
        return repository.getMediaInfoData(relativePath);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCat(String cat) {
        return repository.getMediaInfoDataByCat(cat);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByParentUniqueId(String parentUniqueId) {
        return repository.getMediaInfoDataByParentUniqueId(parentUniqueId);
    }


    public List<MediaInfoDataModel> getMediaInfoDataByCatUnit(String cat,String unit) {
        return repository.getMediaInfoDataByCatUnit(cat,unit);
    }

    public List<MediaInfoDataModel> getMediaFileByCatRelative(String cat,String relative) {
        return repository.getMediaFileByCatRelative(cat,relative);
    }

    public List<MediaInfoDataModel> getMediaInfoDataForUpload(String relativePath) {
        return repository.getMediaInfoDataForUpload(relativePath);
    }

    public List<MediaInfoDataModel> getMediaInfoDataForUploadByTableName(String parent_unique_id,String parent_table_name) {
        return repository.getMediaInfoDataForUploadByTableName(parent_unique_id,parent_table_name);
    }

    public List<MediaInfoDataModel> getMediaInfoDataForUploadByTableNameAll(String parent_unique_id,String parent_table_name) {
        return repository.getMediaInfoDataForUploadByTableNameAll(parent_unique_id,parent_table_name);
    }

    //Jaid : below method is use to update data in unit repository
    public void updateUnitInfoPointData(UnitInfoDataModel model, Activity activity) {
        repository.updateUnitInfoPointData(model, activity);
    }
    // below method is use to insert the data to our repository.
    public void insertUnitInfoPointData(UnitInfoDataModel model, Activity activity) {
        repository.insertUnitInfoPointData(model, activity);
    }

    public void insertDownloadWebmapInfoData(DownloadedWebMapModel model, Activity activity) {
        repository.insertDownloadWebmapInfoData(model, activity);
    }

    public void updateMediaInfo(String obejctId, String globalId, String relativePath) {
        repository.updateMediaInfo(obejctId, globalId, relativePath);
    }

    public void updateMediaIsUploadedInfo(String infoObjID,boolean isUploaded, int id) {
        repository.updateMediaIsUploadedInfo(infoObjID,isUploaded, id);
    }

    public void updateMediaInfoObjectId(String fileName,String obejctId,String globalId) {
        repository.updateMediaInfoObjectId(fileName,obejctId,globalId);
    }

    public void setMediaDeletedList(String unitId,List<MediaInfoDataModel> deleteTotalMediaList,String objId,boolean flag,String url) {
        repository.setMediaDeletedList(unitId,deleteTotalMediaList,objId,flag,url);
    }

    public void updateMediaHohChangedInfo(String relative_path, String parent_unique_id) {
        repository.updateMediaHohChangedInfo(relative_path, parent_unique_id);
    }

    public void updateStructureInfo(String objectid, String globalId, boolean isUploaded, String id) {
        repository.updateStructureInfo(objectid, globalId, isUploaded, id);
    }

    public void updateUnitInfo(String objectid, String globalId, boolean isUploaded, String id) {
        repository.updateUnitInfo(objectid, globalId, isUploaded, id);
    }

    public void updateHOHInfo(String objectid, String globalId, boolean isUploaded, String id) {
        repository.updateHOHInfo(objectid, globalId, isUploaded, id);
    }

    public void updateMemberInfo(String objectid, String globalId, boolean isUploaded, String id) {
        repository.updateMemberInfo(objectid, globalId, isUploaded, id);
    }

    public void insertAllHohInfoPointData(List<HohInfoDataModel> model, Activity activity) {
        repository.insertAllHohInfoPointData(model, activity);
    }

    public void insertAllStructureUnitIdStatusData(List<StructureUnitIdStatusDataTable> model, Activity activity) {
        repository.insertAllStructureUnitIdStatusData(model, activity);
    }

    public void insertAllMediaInfoPointData(List<MediaInfoDataModel> model, Activity activity) {
        repository.insertAllMediaInfoPointData(model, activity);
    }

    public void insertAllMemberInfoPointData(List<MemberInfoDataModel> model, Activity activity) {
        repository.insertAllMemberInfoPointData(model, activity);
    }

    public void insertHohInfoPointData(HohInfoDataModel model, Activity activity) {
        repository.insertHohInfoPointData(model, activity);
    }

    public void insertMemberInfoPointData(MemberInfoDataModel model, Activity activity) {
        repository.insertMemberInfoPointData(model, activity);
    }

    public void deleteMemberByHohId(String hohId, Activity activity) {
        repository.deleteMemberByHohId(hohId, activity);
    }

    public List<MemberInfoDataModel> getMemberDetailsByHohId(String hohId, Activity activity) {
       return  repository.getMemberDetailsByHohId(hohId, activity);
    }


    public void updateMember(MemberInfoDataModel model, Activity activity) {
        repository.updateMember(model, activity);
    }
    public String getWorkAreaStatus(String workAreaName, String surveyor_name) {
        return repository.getWorkAreaStatus(workAreaName, surveyor_name);
    }

    public boolean containsPrimaryKey(String vc) {
        return repository.containsPrimaryKey(vc);
    }

    public boolean isWorkAreaDownloaded(String vc) {
        return repository.isWorkAreaDownloaded(vc);
    }

    public StatusCount getStructureStatusCounts() {
        return repository.getStructureStatusCounts();
    }

    public StatusCount getUnitStatusCounts() {
        return repository.getUnitStatusCounts();
    }

    public List<MediaInfoDataModel> getDeleteAttchList(boolean isDelete) {
        return repository.getDeleteAttchList(isDelete);
    }

    public void updateDeleteAttchList(String objectId,int mediaId,boolean isDelete) {
        repository.updateDeleteAttchList(objectId,isDelete,mediaId);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByRemovedCatUnit(String cat,String unit,boolean flag) {
        return repository.getMediaInfoDataByCatUnit(cat,unit,flag);
    }

    public void setMediaDeletedStatus(String unitId,String objId,boolean flag) {
        repository.setMediaDeletedStatus(unitId,objId,flag);
    }

    public void setMediaDeletedStatusByItemUrl(String unitId,String url,boolean flag) {
        repository.setMediaDeletedStatusByItemUrl(unitId, url, flag);

    }

    public void setMediaDeletedStatusByList(String unitId, List<AttachmentItemList> attachmentItemLists, String objId){
        repository.setMediaDeletedStatusByList(unitId,attachmentItemLists,objId);

    }

    public void updateMediaInfoList(int mediaId, List<MediaInfoDataModel> updateMediaInfo, String relativePath, String docCat,String docType) {
        repository.updateMediaInfoList(mediaId,updateMediaInfo,relativePath,docCat, docType);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByObjId(String objId,String unit) {
        return repository.getMediaInfoDataByObjId(objId,unit);
    }

    public List<MediaInfoDataModel> getDeleteItemObjList(boolean isDelete) {
        return repository.getDeleteItemObjList(isDelete);
    }

    public List<MediaInfoDataModel> getDeleteItemObjList(String parent_id,boolean isDelete, boolean isWholeObjectDeleted) {
        return repository.getDeleteItemObjList(parent_id,isDelete, isWholeObjectDeleted);
    }

    public void setDeleteItemObjValid(boolean isDelete,String objId,String parentId){
        repository.setDeleteItemObjValid(isDelete,objId,parentId);
    }

    public void updateAttachUploadList(String unitId, List<AttachmentItemList> attachmentItemLists, String objId, ArrayList<String> uploadMediaList){
        repository.updateAttachUploadList(unitId, attachmentItemLists, objId, uploadMediaList);
    }
    public void setIsUploaded(String unitId,String objId,boolean isUploaded){
        repository.setIsUploaded(unitId,objId,isUploaded);
    }

    public List<MediaInfoDataModel> getByItemUrl(String unitId,String url){
        return repository.getByItemUrl(unitId,url);
    }

    public void uploadListByFileName(String unitId, String filename, ArrayList<String> uploadMediaList){
        repository.uploadListByFileName(unitId, filename, uploadMediaList);
    }

    public void uploadAttListByFileName(String unitId, String filename, List<AttachmentItemList> uploadMediaList){
        repository.uploadAttListByFileName(unitId, filename, uploadMediaList);
    }

    public void updateByFileName(boolean isDelete,String objId,String parentId){
        repository.updateByFileName(isDelete,objId,parentId);
    }

    public void setMediaDeletedListByUrl(String unitId,String objId,boolean flag) {
        repository.setMediaDeletedListByUrl(unitId,objId,flag);
    }

    public void setDeleteItemByFileName(boolean isDelete,String objId,String parentId){
        repository.setDeleteItemByFileName(isDelete,objId,parentId);
    }

    public String getUnitInfoStatus(String uniqueId) {
        return repository.getUnitInfoStatus(uniqueId);
    }

    public List<UnitInfoDataModel> getUnitInfoDataByNonDate(String uniqueId) {
        return repository.getUnitInfoDataByNonDate(uniqueId);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCatUnitCount(String cat,String unit,boolean flag) {
        return repository.getMediaInfoDataByCatUnitCount(cat,unit,flag);
    }

    public void updateStructureUnitIdStatusDataTable(String unitId, String status) {
        repository.updateStructureUnitIdStatusDataTable(unitId, status);
    }

    public void updateStructureStatusDataTable(String unitId, String status) {
        repository.updateStructureStatusDataTable(unitId, status);
    }

    public void setDeleteWholeObject(boolean isDelete,String objId,String parentId){
        repository.setDeleteWholeObject(isDelete,objId,parentId);
    }

    public void setDeleteWholeFile(boolean isDelete,String objId,String parentId){
        repository.setDeleteWholeFile(isDelete,objId,parentId);
    }

    public void setRemarks(String unitId,String remarks){
        repository.setRemarks(unitId,remarks);
    }

    public void setRemarksByMediaId(int mediaId,String remarks){
        repository.setRemarksByMediaId(mediaId,remarks);
    }

    public List<String> getLocalAddedUniqueIdList(String hutId){
        return repository.getLocalAddedUniqueIdList(hutId);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCatUnitCountType(String cat,String unit,boolean flag,String type) {
        return repository.getMediaInfoDataByCatUnitCountType(cat,unit,flag,type);
    }

    public String getWorkAreaNameByStructureId(String unitSampleGlobalid) {
        return repository.getWorkAreaNameByStructureId(unitSampleGlobalid);
    }

    public List<MediaInfoDataModel> getDeleteItemMediaObjList(String parent_id, boolean isWholeObjDelete) {
        return repository.getDeleteItemMediaObjList(parent_id, isWholeObjDelete);
    }

    public List<UnitInfoDataModel> getAllUnitsByHut(String hutId){
        return repository.getAllUnitsByHut(hutId);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCatTypeUnit(String cat,String unit,boolean flag,String type) {
        return repository.getMediaInfoDataByCatTypeUnit(cat,unit,flag,type);
    }

    public List<UnitInfoDataModel> getUnitByUniqueId(String id) {
        return repository.getUnitByUniqueId(id);
    }

    public List<HohInfoDataModel> getHohByUniqueId(String id) {
        return repository.getHohByUniqueId(id);
    }

    public void insertAllMediaDetailsPointData(List<MediaDetailsDataModel> model) {
        repository.insertAllMediaDetailsPointData(model);
    }

    public void insertMediaDetailsPointData(MediaDetailsDataModel model) {
        repository.insertMediaDetailsPointData(model);
    }

    public void updateMediaDetailsObjectId(String fileName,String obejctId,String globalId) {
        repository.updateMediaDetailsObjectId(fileName,obejctId,globalId);
    }

    public void setMediaDetailsUploaded(boolean isUploaded,boolean isFileUploaded,String globalid,String objectid, String file_name) {
        repository.setMediaDetailsUploaded(isUploaded,isFileUploaded,globalid,objectid, file_name);
    }

    public List<MediaDetailsDataModel> getMediaDetailsByRelGlobalId(String rel_globalid) {
        return repository.getMediaDetailsByRelGlobalId(rel_globalid);
    }

    public MediaDetailsDataModel getMediaDetailsByNameAndRelGlobalId(String rel_globalid, String fileName) {
        return repository.getMediaDetailsByNameAndRelGlobalId(rel_globalid, fileName);
    }

    public void deleteMediaDetailsByRelGlobalId(String rel_globalid) {
        repository.deleteMediaDetailsByRelGlobalId(rel_globalid);
    }

    public List<MediaDetailsDataModel> getMediaDetailsByGlobalId(String globalid) {
        return repository.getMediaDetailsByGlobalId(globalid);
    }

    public List<MediaDetailsDataModel> geAllMediaDetails() {
        return repository.geAllMediaDetails();
    }

    public void updateMediaDetailsRemarksByRelGlobalId(String rel_globalid,String remarks, String unitId, boolean isUploaded) {
        repository.updateMediaDetailsRemarksByRelGlobalId(rel_globalid,remarks,unitId,isUploaded);
    }

    public List<MediaDetailsDataModel> getPendingMediaDetails(boolean isUploaded, boolean isFileUploaded, String unitId) {
        return repository.getPendingMediaDetails(isUploaded, isFileUploaded, unitId);
    }

    public void insertAadhaarVerificationDetail(AadhaarVerificationData aadhaarVerificationData) {
        repository.insertAadhaarVerificationDetail(aadhaarVerificationData);
    }

    public AadhaarVerificationData getAadhaarVerificationDetailsByHohId(String unitId, boolean isUploaded) {
        return repository.getAadhaarVerificationDetailsByHohId(unitId, isUploaded);
    }

    public void updateAadhaarDetailsVerificationUploaded(String unit_id, boolean isUploaded) {
        repository.updateAadhaarDetailsVerificationUploaded(unit_id, isUploaded);
    }

    public void updateHohIdForAadhaarVerification(String hoh_id, String unit_id) {
        repository.updateHohIdForAadhaarVerification(hoh_id, unit_id);
    }
}