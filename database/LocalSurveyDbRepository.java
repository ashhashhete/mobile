package com.igenesys.database;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

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
import com.igenesys.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LocalSurveyDbRepository {

    private static LocalSurveyDbRepository instance;
    private final LocalSurveyDao dao;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    public LocalSurveyDbRepository(Application application) {
        LocalSurveyDatabase database = LocalSurveyDatabase.getInstance(application);
        dao = database.localSurveyDao();
    }

    public static LocalSurveyDbRepository getInstance(Application application) {
        if (instance == null) {
            instance = new LocalSurveyDbRepository(application);
        }
        return instance;
    }

    public LiveData<List<StructureInfoPointDataModel>> getStructureInfoPointData(String surveyor_name) {
        return dao.getStructureInfoPointData(surveyor_name);
    }

    public LiveData<List<UnitInfoDataModel>> getUnitInfoUploadData(String surveyor_name) {
        return dao.getUnitInfoUploadData(surveyor_name);
    }



    public List<StructureInfoPointDataModel> getStructureInfoPointDataZip(String surveyor_name) {
        return dao.getStructureInfoPointDataZip(surveyor_name);
    }

    public LiveData<List<StructureInfoPointDataModel>> getStructureInfoPointDataWithId(String structure_id) {
        return dao.getStructureInfoPointDataWithId(structure_id);
    }

    public LiveData<List<UnitInfoDataModel>> getUnitInfoPointData(String uniqueId) {
        return dao.getUnitInfoPointData(uniqueId);
    }

    public List<MemberInfoDataModel> getMemberInfoData(String uniqueId) {
        return dao.getMemberInfoPointData(uniqueId);
    }
    public List<MemberInfoDataModel> getMemberInfoDataWithRelGlobalId(String relGlobalId) {
        return dao.getMemberInfoRelGlobalId(relGlobalId);
    }

    public List<MemberInfoDataModel> getMemberInfoDataWithMemberId(String uniqueId) {
        return dao.getMemberInfoPointDataWithMemberId(uniqueId);
    }

    public StructureInfoPointDataModel getStructureInfoPointDataModel(String uniqueId) {
        return dao.getStructureInfoPointDataModel(uniqueId);
    }

    public List<StructureInfoPointDataModel> getStructureInfoPointDataModelAll() {
        return dao.getStructureInfoPointDataModelAll();
    }

    public List<MediaInfoDataModel> getMediaInfoData(String relativePath) {
        return dao.getMediaInfoData(relativePath);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCat(String cat) {
        return dao.getMediaInfoDataByCat(cat);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByParentUniqueId(String parentUniqueId) {
        return dao.getMediaInfoDataByParentUniqueId(parentUniqueId);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCatUnit(String cat,String unit) {
        return dao.getMediaInfoDataByCatUnit(false,cat,unit);
    }

    public List<MediaInfoDataModel> getMediaFileByCatRelative(String cat,String relative) {
        return dao.getMediaFileByCatRelative(cat,relative);
    }

    public List<MediaInfoDataModel> getMediaInfoDataForUpload(String relativePath) {
        return dao.getMediaInfoDataForUpload(relativePath, false);
    }

    public List<MediaInfoDataModel> getMediaInfoDataForUploadByTableName(String parent_unique_id,String parent_table_name) {
        return dao.getMediaInfoDataForUploadByTableName(parent_unique_id, parent_table_name,false);
    }

    public List<MediaInfoDataModel> getMediaInfoDataForUploadByTableNameAll(String parent_unique_id,String parent_table_name) {
        return dao.getMediaInfoDataForUploadByTableNameAll(parent_unique_id, parent_table_name,false);
    }

    public List<UnitInfoDataModel> getUnitInfoData(String uniqueId) {
        return dao.getUnitInfoData(uniqueId);
    }

    public List<DownloadedWebMapModel> getDownloadedMapInfoData(String userName) {
        return dao.getDownloadedMapInfoData(userName);
    }

    public List<HohInfoDataModel> getHohInfoData(String uniqueId) {
        return dao.getHohInfoPointData(uniqueId);
    }

    public List<String> getStructureUnitIdStatusListData(String uniqueId) {
        return dao.getStructureUnitIdStatusListData(uniqueId);
    }

    public List<String> getUnitInfoIdData(String uniqueId) {
        return dao.getUnitInfoIdData(uniqueId);
    }

    public List<String> getHohInfoIdData(String uniqueId) {
        return dao.getHohInfoIdData(uniqueId);
    }

    public List<String> getMemberInfoIdData(String uniqueId) {
        return dao.getMemberInfoIdData(uniqueId);
    }

    public List<MediaInfoDataModel> getDeleteAttchList(boolean isDelete) {
        return dao.getDeleteAttchList(isDelete);
    }

    public void updateDeleteAttchList(String objId,boolean isDelete,int mediaId) {
        mExecutor.execute(() -> {
            try {
                dao.updateDeleteAttchList(objId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateMediaInfo(String obejctId, String globalId, String relativePath) {

        mExecutor.execute(() -> {
            try {
//                dao.updateMediaInfo(obejctId, globalId, relativePath);
                dao.updateMediaInfo(globalId, relativePath);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateMediaIsUploadedInfo(String infoObjID,boolean isUploaded, int id) {

        mExecutor.execute(() -> {
            try {
                dao.updateMediaIsUploadedInfo(infoObjID,isUploaded, (short) 1, id);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateMediaInfoObjectId(String fileName,String obejctId,String globalId) {

        mExecutor.execute(() -> {
            try {
                dao.updateMediaInfoObjectId(fileName,obejctId,globalId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setMediaDeletedList(String unitId,List<MediaInfoDataModel> deleteTotalMediaList,String objId,boolean flag,String url) {

        mExecutor.execute(() -> {
            try {
                dao.setMediaDeletedList(deleteTotalMediaList,flag,url,unitId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setMediaDeletedStatus(String unitId,String objId,boolean flag) {

        mExecutor.execute(() -> {
            try {
                dao.setMediaDeletedList(unitId,objId,flag);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setMediaDeletedStatusByItemUrl(String unitId,String url,boolean flag) {

        mExecutor.execute(() -> {
            try {
                dao.setMediaDeletedStatusByItemUrl(unitId,url,flag);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setMediaDeletedStatusByList(String unitId, List<AttachmentItemList> attachmentItemLists, String objId){

        mExecutor.execute(() -> {
            try {
                dao.setMediaDeletedStatusByList(unitId,attachmentItemLists,objId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateMediaHohChangedInfo(String relative_path, String parent_unique_id) {

        mExecutor.execute(() -> {
            try {
                dao.updateMediaHohChangedInfo(false, relative_path, (short) 0, parent_unique_id);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateStructureInfo(String objectid, String globalId, boolean isUploaded, String id) {

        mExecutor.execute(() -> {
            try {
                dao.updateStructureInfo(objectid, globalId, isUploaded, id, new Date());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateUnitInfo(String objectid, String globalId, boolean isUploaded, String id) {

        mExecutor.execute(() -> {
            try {
                dao.updateUnitInfo(objectid, globalId, isUploaded, id, new Date());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateHOHInfo(String objectid, String globalId, boolean isUploaded, String id) {

        mExecutor.execute(() -> {
            try {
                dao.updateHOHInfo(objectid, globalId, isUploaded, id, new Date());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateMemberInfo(String objectid, String globalId, boolean isUploaded, String id) {

        mExecutor.execute(() -> {
            try {
                dao.updateMemberInfo(objectid, globalId, isUploaded, id, new Date());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void deleteBulkStructureInfoData(final List<String> model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteBulkStructureInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Structure info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Structure info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteStructureUnitIdStatusDataTable(final List<String> model) {

        mExecutor.execute(() -> {
            try {
                dao.deleteStructureUnitIdStatusDataTable(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Structure info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Structure info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteBulkUnitInfoData(final List<String> model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteBulkUnitInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteBulkHohInfoData(final List<String> model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteBulkHohInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "HOH info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "HOH info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteBulkMemberInfoData(final List<String> model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteBulkMemberInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Member info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Member info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteBulkMediaInfoData(final List<String> model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteBulkMediaInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Media info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Media info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteStructureInfoData(final String model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteStructureInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Structure info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Structure info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteWebMapInfoData(final String webMapId, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteWebMapInfoData(webMapId);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Offline webmap deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Offline webmap not deleted.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void deleteUnitInfoData(final String model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteUnitInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteStructureUnitIdStatusData(final String structure_id) {

        mExecutor.execute(() -> {
            try {
                dao.deleteStructureUnitIdStatusData(structure_id);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteUnitDataById(final String unit_id) {

        mExecutor.execute(() -> {
            try {
                dao.deleteUnitDataById(unit_id);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteHohInfoData(final String model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteHohInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "HOH info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "HOH info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteMemberInfoData(final String model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteMemberInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Member info deleted.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Member info not deleted.", Toast.LENGTH_SHORT).show());
            }

        });
    }

    public void deleteMediaInfoData(final String model, Activity activity,String cat,String tableName) {

        mExecutor.execute(() -> {
            try {
                dao.deleteMediaInfoData(model,false);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }

        });
    }

    public void deleteMediaWithFileNameData(final String model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.deleteMediaWithFileNameData(model);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }

        });
    }

    public void insertStructureInfoPointData(final StructureInfoPointDataModel model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertStructureInfoPointData(model);
                activity.runOnUiThread(() -> Toast.makeText(activity, "Structure info Saved", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void insertStructureUnitIdStatusData(final StructureUnitIdStatusDataTable model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertStructureUnitIdStatusData(model);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void updateStructureInfoPointData(final StructureInfoPointDataModel model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.updateStructureInfoPointData(model);
                activity.runOnUiThread(() -> Toast.makeText(activity, "Structure info updated", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void insertUnitInfoPointData(UnitInfoDataModel model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertUnitInfoPointData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info Saved", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void insertDownloadWebmapInfoData(DownloadedWebMapModel model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertDownloadWebmapInfoData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit info Saved", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
////                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public StatusCount getStructureStatusCounts() {
        return dao.getStructureStatusCounts(Constants.InProgress_statusLayer,
                Constants.NotStarted_statusLayer,
                Constants.completed_statusLayer,
                Constants.OnHold_statusLayer);

    }

    public StatusCount getUnitStatusCounts() {
        return dao.getUnitStatusCounts(Constants.InProgress_statusLayer,
                //Constants.NotStarted_statusLayer,
                Constants.completed_dispute,
                Constants.completed_statusLayer,
                Constants.OnHold_statusLayer);

    }

    public void insertAllHohInfoPointData(List<HohInfoDataModel> model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertAllHohInfoPointData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "HOH info Saved", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void insertAllStructureUnitIdStatusData(List<StructureUnitIdStatusDataTable> model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertAllStructureUnitIdStatusData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "HOH info Saved", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void insertAllMediaInfoPointData(List<MediaInfoDataModel> model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertAllMediaInfoPointData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Media info Saved", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void insertAllMemberInfoPointData(List<MemberInfoDataModel> model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertAllMemberInfoPointData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Member info Saved", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void insertHohInfoPointData(HohInfoDataModel model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertHohInfoPointData(model);
//                Utils.showMessagePopup(activity.getResources().getString(R.string.dataSaved), activity);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
////                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void insertMemberInfoPointData(MemberInfoDataModel model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.insertMemberInfoPointData(model);
//                Utils.showMessagePopup(activity.getResources().getString(R.string.dataSaved), activity);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
////                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void updateMember(MemberInfoDataModel model, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.updateMember(model);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
           }
        });
    }


    public List<MemberInfoDataModel> getMemberDetailsByHohId(String hohId, Activity activity) {
        return dao.getMemberDetailsByHohId(hohId);
    }

    public void deleteMemberByHohId(String hohId, Activity activity) {
        mExecutor.execute(() -> {
            try {
                dao.deleteMemberByHohId(hohId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }


    public boolean containsPrimaryKey(String structureId) {
        int count = 0;
        try {
            return dao.containsPrimaryKey(structureId);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isWorkAreaDownloaded(String workAreaName) {
        int count = 0;
        try {
            return dao.isWorkAreaDownloaded(workAreaName);
        } catch (Exception e) {
            return false;
        }
    }

    public String getWorkAreaStatus(String workAreaName, String surveyor_name) {
        return dao.getWorkAreaStatus(workAreaName, surveyor_name);
    }
    //@author : Jaid
    public void updateUnitInfoPointData(UnitInfoDataModel model, Activity activity) {

        mExecutor.execute(() -> {
            try {
                dao.updateUnitInfoPointData(model);
                activity.runOnUiThread(() -> Toast.makeText(activity, "Unit Surveyor info updated", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCatUnit(String cat,String unit,boolean flag) {
        return dao.getMediaInfoDataByCatUnit(cat,unit,flag);
    }


    public void updateMediaInfoList(int mediaId, List<MediaInfoDataModel> updateMediaInfo, String relativePath, String docCat,String docType) {

        mExecutor.execute(() -> {
            try {
                dao.updateMediaInfoList(mediaId,updateMediaInfo,relativePath,docCat, docType);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public List<MediaInfoDataModel> getMediaInfoDataByObjId(String objId,String unit) {
        return dao.getMediaInfoDataByObjId(objId,unit);
    }

    public List<MediaInfoDataModel> getDeleteItemObjList(boolean isDelete) {
        return dao.getDeleteItemObjList(isDelete);
    }

    public List<MediaInfoDataModel> getDeleteItemObjList(String parent_id,boolean isDelete, boolean isWholeObjectDeleted) {
        return dao.getDeleteItemObjList(parent_id,isDelete, isWholeObjectDeleted);
    }

    public void setDeleteItemObjValid(boolean isDelete,String objId,String parentId){
        mExecutor.execute(() -> {
            try {
                dao.setDeleteItemObjValid(isDelete,objId,parentId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }
    public void updateAttachUploadList(String unitId, List<AttachmentItemList> attachmentItemLists, String objId, ArrayList<String> uploadMediaList){
        mExecutor.execute(() -> {
            try {
                dao.updateAttachUploadList(unitId, objId, uploadMediaList);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }
    public void setIsUploaded(String unitId,String objId,boolean isUploaded){
        mExecutor.execute(() -> {
            try {
                dao.setIsUploaded(unitId,objId,isUploaded);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public List<MediaInfoDataModel> getByItemUrl(String unitId, String url) {
        return dao.getByItemUrl(unitId, url);
    }

    public void uploadListByFileName(String unitId, String filename, ArrayList<String> uploadMediaList){
        mExecutor.execute(() -> {
            try {
                dao.uploadListByFileName(unitId,filename,uploadMediaList);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void uploadAttListByFileName(String unitId, String filename, List<AttachmentItemList> uploadMediaList){
        mExecutor.execute(() -> {
            try {
                dao.uploadAttListByFileName(unitId,filename,uploadMediaList);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateByFileName(boolean isDelete,String objId,String parentId){
        mExecutor.execute(() -> {
            try {
                dao.updateByFileName(isDelete,objId,parentId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setMediaDeletedListByUrl(String unitId,String objId,boolean flag) {

        mExecutor.execute(() -> {
            try {
                dao.setMediaDeletedListByUrl(unitId,objId,flag);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setDeleteItemByFileName(boolean isDelete,String objId,String parentId){
        mExecutor.execute(() -> {
            try {
                dao.setDeleteItemByFileName(isDelete,objId,parentId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public String getUnitInfoStatus(String uniqueId) {
        return dao.getUnitInfoStatus(uniqueId);
    }

    public List<UnitInfoDataModel> getUnitInfoDataByNonDate(String uniqueId) {
        return dao.getUnitInfoDataByNonDate(uniqueId);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCatUnitCount(String cat,String unit,boolean flag) {
        return dao.getMediaInfoDataByCatUnitCount(cat,unit,flag);
    }

    public void updateStructureUnitIdStatusDataTable(String unitId,String status) {

        mExecutor.execute(() -> {
            try {
                dao.updateStructureUnitIdStatusDataTable(unitId,status);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void updateStructureStatusDataTable(String unitId,String status) {

        mExecutor.execute(() -> {
            try {
                dao.updateStructureStatusDataTable(unitId,status);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setDeleteWholeObject(boolean isDelete,String objId,String parentId){
        mExecutor.execute(() -> {
            try {
                dao.setDeleteWholeObject(isDelete,objId,parentId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setDeleteWholeFile(boolean isDelete,String objId,String parentId){
        mExecutor.execute(() -> {
            try {
                dao.setDeleteWholeFile(isDelete,objId,parentId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setRemarks(String unitId,String remarks){
        mExecutor.execute(() -> {
            try {
                dao.setRemarks(unitId,remarks);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setRemarksByMediaId(int mediaId,String remarks){
        mExecutor.execute(() -> {
            try {
                dao.setRemarksByMediaId(mediaId,remarks);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public List<String> getLocalAddedUniqueIdList(String hutId){
        return dao.getLocalAddedUniqueIdList(hutId);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCatUnitCountType(String cat,String unit,boolean flag,String type) {
        return dao.getMediaInfoDataByCatUnitCountType(cat,unit,flag,type);
    }

    public String getWorkAreaNameByStructureId(String unitSampleGlobalid) {
        return dao.getWorkAreaNameByStructureId(unitSampleGlobalid);
    }

    public List<MediaInfoDataModel> getDeleteItemMediaObjList(String parent_id,boolean isWholeObjDelete) {
        return dao.getDeleteItemMediaObjList(parent_id, isWholeObjDelete);
    }

    public List<UnitInfoDataModel> getAllUnitsByHut(String hutId){
        return dao.getAllUnitsByHut(hutId);
    }

    public List<MediaInfoDataModel> getMediaInfoDataByCatTypeUnit(String cat,String unit,boolean flag,String type) {
        return dao.getMediaInfoDataByCatTypeUnit(cat,unit,flag,type);
    }

    public List<UnitInfoDataModel> getUnitByUniqueId(String id) {
        return dao.getUnitByUniqueId(id);
    }

    public List<HohInfoDataModel> getHohByUniqueId(String id) {
        return dao.getHohByUniqueId(id);
    }

    public void insertAllMediaDetailsPointData(List<MediaDetailsDataModel> model) {
        mExecutor.execute(() -> {
            try {
                dao.insertAllMediaDetailsPointData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Media info Saved", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void insertMediaDetailsPointData(MediaDetailsDataModel model) {
        mExecutor.execute(() -> {
            try {
                dao.insertMediaDetailsPointData(model);
//                activity.runOnUiThread(() -> Toast.makeText(activity, "Media info Saved", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                AppLog.e(e.getMessage());
//                Utils.showMessagePopup(activity.getResources().getString(R.string.errorInDeletingTheData), activity);
            }
        });
    }

    public void updateMediaDetailsObjectId(String fileName,String obejctId,String globalId) {

        mExecutor.execute(() -> {
            try {
                dao.updateMediaDetailsObjectId(fileName,obejctId,globalId);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public void setMediaDetailsUploaded(boolean isUploaded,boolean isFileUploaded,String globalid,String objectid, String file_name) {

        mExecutor.execute(() -> {
            try {
                dao.setMediaDetailsUploaded(isUploaded,isFileUploaded,globalid,objectid,file_name);
            } catch (Exception e) {
                AppLog.e(e.getMessage());
            }
        });
    }

    public List<MediaDetailsDataModel> getMediaDetailsByRelGlobalId(String rel_globalid) {
        return dao.getMediaDetailsByRelGlobalId(rel_globalid);
    }

    public MediaDetailsDataModel getMediaDetailsByNameAndRelGlobalId(String rel_globalid, String fileName) {
        return dao.getMediaDetailsByNameAndRelGlobalId(rel_globalid, fileName);
    }

    public void deleteMediaDetailsByRelGlobalId(String rel_globalid) {
        dao.deleteMediaDetailsByRelGlobalId(rel_globalid);
    }

    public List<MediaDetailsDataModel> getMediaDetailsByGlobalId(String globalid) {
        return dao.getMediaDetailsByGlobalId(globalid);
    }

    public List<MediaDetailsDataModel> geAllMediaDetails() {
        return dao.geAllMediaDetails();
    }

    public void updateMediaDetailsRemarksByRelGlobalId(String rel_globalid,String remarks, String unitId, boolean isUploaded) {
        dao.updateMediaDetailsRemarksByRelGlobalId(rel_globalid, remarks, unitId, isUploaded);
    }

    public List<MediaDetailsDataModel> getPendingMediaDetails(boolean isUploaded, boolean isFileUploaded, String unitId) {
        return dao.getPendingMediaDetails(isUploaded, isFileUploaded, unitId);
    }

    public void insertAadhaarVerificationDetail(AadhaarVerificationData aadhaarVerificationData) {
        dao.insertAadhaarVerificationDetail(aadhaarVerificationData);
    }

    public AadhaarVerificationData getAadhaarVerificationDetailsByHohId(String unitId, boolean isUploaded) {
        return dao.getAadhaarVerificationDetailsByHohId(unitId, isUploaded);
    }

    public void updateAadhaarDetailsVerificationUploaded(String unit_id, boolean isUploaded) {
        dao.updateAadhaarDetailsVerificationUploaded(unit_id, isUploaded);
    }

    public void updateHohIdForAadhaarVerification(String hoh_id, String unit_id) {
        dao.updateHohIdForAadhaarVerification(hoh_id, unit_id);
    }
}
