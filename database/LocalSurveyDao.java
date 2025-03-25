package com.igenesys.database;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@androidx.room.Dao
public interface LocalSurveyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStructureInfoPointData(StructureInfoPointDataModel model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStructureUnitIdStatusData(StructureUnitIdStatusDataTable model);

    @Update
    void updateStructureInfoPointData(StructureInfoPointDataModel model);

    //@author : Jaid
    @Update
    void updateUnitInfoPointData(UnitInfoDataModel model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUnitInfoPointData(UnitInfoDataModel model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDownloadWebmapInfoData(DownloadedWebMapModel downloadedWebMapModel);

    @Query("UPDATE downloadedWebMapInfoDataTable SET updatedOn = :date WHERE workAreaName = :id")
    void updateDownloadWebmapInfo(String id, Date date);

    @Query("UPDATE structureUnitIdStatusDataTable SET unitStatus = :unitStatus WHERE unitUniqueId = :unitUniqueId")
    void updateStructureUnitIdStatusDataTable(String unitUniqueId, String unitStatus);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllHohInfoPointData(List<HohInfoDataModel> model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllStructureUnitIdStatusData(List<StructureUnitIdStatusDataTable> model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMediaInfoPointData(List<MediaInfoDataModel> model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMediaDetailsPointData(List<MediaDetailsDataModel> model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMediaDetailsPointData(MediaDetailsDataModel media);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHohInfoPointData(HohInfoDataModel model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMemberInfoPointData(MemberInfoDataModel model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAadhaarVerificationDetail(AadhaarVerificationData model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMemberInfoPointData(List<MemberInfoDataModel> model);

    @Query("SELECT * FROM structureInfoPointDataTable WHERE surveyor_name = :surveyor_name ORDER BY lastEditedDate DESC")
    LiveData<List<StructureInfoPointDataModel>> getStructureInfoPointData(String surveyor_name);

    @Query("SELECT * FROM unitInfoDataTable WHERE surveyor_name = :surveyor_name ORDER BY lastEditedDate DESC")
    LiveData<List<UnitInfoDataModel>> getUnitInfoUploadData(String surveyor_name);

    @Query("SELECT * FROM structureInfoPointDataTable WHERE surveyor_name = :surveyor_name ORDER BY lastEditedDate DESC")
    List<StructureInfoPointDataModel> getStructureInfoPointDataZip(String surveyor_name);

    @Query("SELECT * FROM structureInfoPointDataTable WHERE structure_id = :structure_id ORDER BY date DESC")
    LiveData<List<StructureInfoPointDataModel>> getStructureInfoPointDataWithId(String structure_id);

    @Query("SELECT * FROM unitInfoDataTable WHERE unitSampleGlobalid = :structureUniqueId ORDER BY date ASC")
    LiveData<List<UnitInfoDataModel>> getUnitInfoPointData(String structureUniqueId);

    @Query("SELECT * FROM downloadedWebMapInfoDataTable WHERE userName = :userName ORDER BY createdOn DESC")
    List<DownloadedWebMapModel> getDownloadedMapInfoData(String userName);

    @Query("SELECT * FROM unitInfoDataTable WHERE unitSampleGlobalid = :unitUniqueId ORDER BY date ASC")
    List<UnitInfoDataModel> getUnitInfoData(String unitUniqueId);

    @Query("SELECT * FROM structureUnitIdStatusDataTable WHERE structure_id = :structure_id")
    List<StructureUnitIdStatusDataTable> getStructureUnitIdStatusData(String structure_id);

    @Query("SELECT unit_id FROM unitInfoDataTable WHERE unitSampleGlobalid = :unitUniqueId ORDER BY date ASC")
    List<String> getUnitInfoIdData(String unitUniqueId);

    @Query("SELECT hoh_id FROM hohInfoDataTable WHERE hohSampleGlobalid = :uniqueId ORDER BY date ASC")
    List<String> getHohInfoIdData(String uniqueId);

    @Query("SELECT member_id FROM memberInfoDataTable WHERE memberSampleGlobalid = :uniqueId ORDER BY date ASC")
    List<String> getMemberInfoIdData(String uniqueId);

    @Query("SELECT * FROM hohInfoDataTable WHERE hohSampleGlobalid = :unitUniqueId ORDER BY hoh_name ASC")
    List<HohInfoDataModel> getHohInfoPointData(String unitUniqueId);

    @Query("SELECT unit_status FROM unitInfoDataTable WHERE unit_id = :unitSampleGlobalid ORDER BY unit_id ASC")
    String getUnitInfoStatus(String unitSampleGlobalid);

    @Query("SELECT unitStatus FROM structureUnitIdStatusDataTable WHERE structure_id = :structure_id ORDER BY unitUniqueId ASC")
    List<String> getStructureUnitIdStatusListData(String structure_id);

    @Query("SELECT * FROM memberInfoDataTable WHERE memberSampleGlobalid = :memberUniqueId ORDER BY member_name ASC")
    List<MemberInfoDataModel> getMemberInfoPointData(String memberUniqueId);

    @Query("SELECT * FROM memberInfoDataTable WHERE member_id = :memberUniqueId ORDER BY member_name ASC")
    List<MemberInfoDataModel> getMemberInfoPointDataWithMemberId(String memberUniqueId);

    @Query("SELECT * FROM structureInfoPointDataTable WHERE structure_id = :structure_id ORDER BY date DESC")
    StructureInfoPointDataModel getStructureInfoPointDataModel(String structure_id);

    @Query("SELECT * FROM structureInfoPointDataTable ORDER BY date DESC")
    List<StructureInfoPointDataModel> getStructureInfoPointDataModelAll();

    @Query("SELECT * FROM mediaInfoDDataTable WHERE relative_path = :relativePath ORDER BY date ASC")
    List<MediaInfoDataModel> getMediaInfoData(String relativePath);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE document_category = :cat")
    List<MediaInfoDataModel> getMediaInfoDataByCat(String cat);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE parent_unique_id = :parentUniqueId")
    List<MediaInfoDataModel> getMediaInfoDataByParentUniqueId(String parentUniqueId);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE document_category = :cat and parent_unique_id = :unit and wholeObjectDeleted= :flag")
    List<MediaInfoDataModel> getMediaInfoDataByCatUnit(boolean flag,String cat,String unit);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE document_category = :cat and relative_path = :relative")
    List<MediaInfoDataModel> getMediaFileByCatRelative(String cat,String relative);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE relative_path = :relativePath and isUploaded = :isNotUpload and isHaveDelete = :isNotUpload ORDER BY date ASC")
    List<MediaInfoDataModel> getMediaInfoDataForUpload(String relativePath, boolean isNotUpload);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE parent_unique_id = :parent_unique_id and parent_table_name= :parent_table_name and isUploaded = :isNotUpload and isHaveDelete = :isNotUpload ORDER BY isLocal DESC")
    List<MediaInfoDataModel> getMediaInfoDataForUploadByTableName(String parent_unique_id,String parent_table_name,boolean isNotUpload);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE parent_unique_id = :parent_unique_id and parent_table_name= :parent_table_name and isUploaded = :isNotUpload and isHaveDelete = :isNotUpload ORDER BY isLocal DESC")
    List<MediaInfoDataModel> getMediaInfoDataForUploadByTableNameAll(String parent_unique_id,String parent_table_name,boolean isNotUpload);

    @Query("DELETE from structureInfoPointDataTable where structure_id in (:structure_id)")
    // S_1693123790015/U_1693123790107/
    void deleteBulkStructureInfoData(List<String> structure_id);

    @Query("DELETE from structureUnitIdStatusDataTable where structure_id in (:structure_id)")
    // S_1693123790015/U_1693123790107/
    void deleteStructureUnitIdStatusDataTable(List<String> structure_id);

    @Query("DELETE FROM structureInfoPointDataTable WHERE structure_id = :structure_id")
    void deleteStructureInfoData(String structure_id);

    @Query("DELETE FROM downloadedWebMapInfoDataTable WHERE workAreaName = :webMapId")
    void deleteWebMapInfoData(String webMapId);

    @Query("DELETE from unitInfoDataTable where unit_id in (:unitSampleGlobalid)")
    void deleteBulkUnitInfoData(List<String> unitSampleGlobalid);

    @Query("DELETE FROM unitInfoDataTable WHERE unitSampleGlobalid = :unitSampleGlobalid")
    void deleteUnitInfoData(String unitSampleGlobalid);

    @Query("DELETE FROM structureUnitIdStatusDataTable WHERE structure_id = :structure_id")
    void deleteStructureUnitIdStatusData(String structure_id);

    @Query("DELETE FROM unitInfoDataTable WHERE unit_id = :unit_id")
    void deleteUnitDataById(String unit_id);

    @Query("DELETE from hohInfoDataTable where hoh_id in (:hohSampleGlobalid)")
    void deleteBulkHohInfoData(List<String> hohSampleGlobalid);

    @Query("DELETE FROM hohInfoDataTable WHERE hohSampleGlobalid = :hohSampleGlobalid")
    void deleteHohInfoData(String hohSampleGlobalid);

    @Query("DELETE from memberInfoDataTable where member_id in (:memberSampleGlobalid)")
    void deleteBulkMemberInfoData(List<String> memberSampleGlobalid);

    @Query("DELETE from mediaInfoDDataTable where parent_unique_id in (:mediaRelativePathList)")
    void deleteBulkMediaInfoData(List<String> mediaRelativePathList);

    @Query("DELETE FROM memberInfoDataTable WHERE memberSampleGlobalid = :memberSampleGlobalid")
    void deleteMemberInfoData(String memberSampleGlobalid);

//    @Query("DELETE FROM mediaInfoDDataTable WHERE parent_unique_id = :parent_unique_id and parent_table_name!= :tableName and document_category!= :cat")
    @Query("DELETE FROM mediaInfoDDataTable WHERE parent_unique_id = :parent_unique_id and isSurveyorDoc = :isDoc")
    void deleteMediaInfoData(String parent_unique_id,boolean isDoc);

    @Query("DELETE FROM mediaInfoDDataTable WHERE filename = :filename")
    void deleteMediaWithFileNameData(String filename);

    @Query("SELECT * FROM hohinfodatatable WHERE hohSampleGlobalid = :unitUniqueId ORDER BY hoh_name ASC")
    LiveData<List<HohInfoDataModel>> getHohInfoPointLiveData(String unitUniqueId);

    @Query("SELECT * FROM memberInfoDataTable WHERE rel_globalid = :memberUniqueId ORDER BY member_name ASC")
    LiveData<List<MemberInfoDataModel>> getMemberInfoPointLiveData(String memberUniqueId);
    @Query("SELECT * FROM memberInfoDataTable WHERE rel_globalid = :memberUniqueId ORDER BY member_name ASC")
    List<MemberInfoDataModel> getMemberInfoRelGlobalId(String memberUniqueId);

    // @Query("UPDATE mediaInfoDDataTable SET obejctId=:a1 , rel_globalid=:globalId WHERE relative_path = :relative_path")
    // void updateMediaInfo(String a1, String globalId, String relative_path);

    @Query("UPDATE mediaInfoDDataTable SET rel_globalid=:globalId WHERE relative_path = :relative_path")
    void updateMediaInfo(String globalId, String relative_path);

    @Query("UPDATE mediaInfoDDataTable SET obejctId=:infoObjID , isUploaded=:isUploaded , file_upload_checked=:val  WHERE mediaId = :id")
    void updateMediaIsUploadedInfo(String infoObjID,boolean isUploaded, short val, int id);

    @Query("UPDATE mediaInfoDDataTable SET obejctId=:obejctId,globalId = :globalId WHERE filename =:fileName")
    void updateMediaInfoObjectId(String fileName,String obejctId,String globalId);

    @Query("UPDATE mediaInfoDDataTable SET isUploaded=:isUploaded , relative_path=:relative_path , file_upload_checked=:val  WHERE parent_unique_id = :parent_unique_id")
    void updateMediaHohChangedInfo(boolean isUploaded, String relative_path, short val, String parent_unique_id);

    @Query("UPDATE structureInfoPointDataTable SET obejctId=:a1 , globalId=:globalId ,isUploaded=:isUploaded, lastEditedDate = :date WHERE structure_id = :id")
    void updateStructureInfo(String a1, String globalId, boolean isUploaded, String id, Date date);

    @Query("UPDATE unitInfoDataTable SET obejctId=:a1 , globalId=:globalId ,isUploaded=:isUploaded, lastEditedDate = :date WHERE unit_id = :id")
    void updateUnitInfo(String a1, String globalId, boolean isUploaded, String id, Date date);

    @Query("UPDATE hohInfoDataTable SET obejctId=:a1 , globalId=:globalId ,isUploaded=:isUploaded, lastEditedDate = :date WHERE hoh_id = :id")
    void updateHOHInfo(String a1, String globalId, boolean isUploaded, String id, Date date);

    @Query("UPDATE memberInfoDataTable SET obejctId=:a1 , globalId=:a2 ,isUploaded=:a3, lastEditedDate = :a5 WHERE member_id = :a4")
    void updateMemberInfo(String a1, String a2, boolean a3, String a4, Date a5);

    @Query("SELECT COUNT(CASE WHEN  structure_status = :a1 THEN 1 END) AS countInProgress," +
            "COUNT(CASE WHEN  structure_status = :a2 THEN 1 END) AS countNotStarted," +
            "COUNT(CASE WHEN  structure_status = :a3 THEN 1 END) AS countCompleted," +
            "COUNT(CASE WHEN  structure_status = :a4 THEN 1 END) AS countonHold from structureInfoPointDataTable")
    StatusCount getStructureStatusCounts(String a1, String a2, String a3, String a4);

    @Query("SELECT COUNT(CASE WHEN  unit_status = :a1 THEN 1 END) AS countInProgress," +
            "COUNT(CASE WHEN  unit_status = :a2 THEN 1 END) AS countNotStarted," +
            "COUNT(CASE WHEN  unit_status = :a3 THEN 1 END) AS countCompleted," +
            "COUNT(CASE WHEN  unit_status = :a4 THEN 1 END) AS countonHold from unitInfoDataTable")
    StatusCount getUnitStatusCounts(String a1, String a2, String a3, String a4);

    @Query("SELECT structure_status FROM structureInfoPointDataTable WHERE  work_area_name = :a2 AND surveyor_name = :a1  ORDER BY date ASC")
    String getWorkAreaStatus(String a2, String a1);

    @Query("SELECT * FROM structureInfoPointDataTable WHERE surveyUniqueIdNumber = :a1")
    int isStructInfoAvailable(String a1);

    @Query("SELECT count(*)!=0 FROM structureInfoPointDataTable WHERE surveyUniqueIdNumber = :a1 ")
    boolean containsPrimaryKey(String a1);

    @Query("SELECT count(*)!=0 FROM downloadedWebMapInfoDataTable WHERE workAreaName = :a1 ")
    boolean isWorkAreaDownloaded(String a1);

    // @Query("UPDATE mediaInfoDDataTable SET deleteTotalMediaList=:deleteTotalMediaList,isHaveDelete=:flag WHERE obejctId = :objId and parent_unique_id = :unitId and item_url = :url")
    @Query("UPDATE mediaInfoDDataTable SET isHaveDelete=:flag,deleteTotalMediaList=:deleteTotalMediaList WHERE item_url = :url and parent_unique_id = :unitId")
    void setMediaDeletedList(List<MediaInfoDataModel> deleteTotalMediaList,boolean flag,String url,String unitId);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE isHaveDelete = :isDelete")
    List<MediaInfoDataModel> getDeleteAttchList(boolean isDelete);

    // @Query("UPDATE mediaInfoDDataTable SET isHaveDelete=:isDelete WHERE mediaId = :mediaId and obejctId = :objectId")
    // void updateDeleteAttchList(String objectId,boolean isDelete,int mediaId);

    @Query("DELETE FROM mediaInfoDDataTable WHERE obejctId = :objectId")
    void updateDeleteAttchList(String objectId);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE document_category = :cat and parent_unique_id = :unit and isHaveDelete =:flag")
    List<MediaInfoDataModel> getMediaInfoDataByCatUnit(String cat,String unit,boolean flag);

    @Query("UPDATE mediaInfoDDataTable SET isHaveDelete=:flag WHERE obejctId = :objId and parent_unique_id = :unitId")
    void setMediaDeletedList(String unitId,String objId,boolean flag);

    @Query("UPDATE mediaInfoDDataTable SET isHaveDelete=:flag WHERE item_url = :url and parent_unique_id = :unitId")
    void setMediaDeletedStatusByItemUrl(String unitId,String url,boolean flag);

    @Query("UPDATE mediaInfoDDataTable SET totalMediaList=:updateMediaInfo WHERE parent_unique_id = :relativePath and document_category = :docCat and document_type = :docType and mediaId = :mediaId")
    void updateMediaInfoList(int mediaId, List<MediaInfoDataModel> updateMediaInfo, String relativePath, String docCat,String docType);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE obejctId = :objId and parent_unique_id = :unit")
    List<MediaInfoDataModel> getMediaInfoDataByObjId(String objId,String unit);

    @Query("UPDATE mediaInfoDDataTable SET attachmentItemLists = :attachmentItemLists WHERE obejctId = :objId and parent_unique_id = :unitId")
    void setMediaDeletedStatusByList(String unitId, List<AttachmentItemList> attachmentItemLists, String objId);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE isDeletedItem = :isDelete")
    List<MediaInfoDataModel> getDeleteItemObjList(boolean isDelete);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE parent_unique_id = :parent_id and isDeletedItem = :isDelete and wholeObjectDeleted = :isWholeObjectDeleted")
    List<MediaInfoDataModel> getDeleteItemObjList(String parent_id, boolean isDelete, boolean isWholeObjectDeleted);

    @Query("UPDATE mediaInfoDDataTable SET isDeletedItem = :isDelete WHERE obejctId = :objId and parent_unique_id = :parentId")
    void setDeleteItemObjValid(boolean isDelete,String objId,String parentId);

    @Query("UPDATE mediaInfoDDataTable SET uploadMediaList =:uploadMediaList WHERE obejctId = :objId and parent_unique_id = :unitId")
    void updateAttachUploadList(String unitId,String objId, ArrayList<String> uploadMediaList);

    @Query("UPDATE mediaInfoDDataTable SET isUploaded =:isUploaded WHERE obejctId = :objId and parent_unique_id = :unitId")
    void setIsUploaded(String unitId,String objId,boolean isUploaded);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE filename = :url and parent_unique_id = :unitId")
    List<MediaInfoDataModel> getByItemUrl(String unitId,String url);

    @Query("UPDATE mediaInfoDDataTable SET uploadMediaList =:uploadMediaList WHERE filename = :filename and parent_unique_id = :unitId")
    void uploadListByFileName(String unitId, String filename, ArrayList<String> uploadMediaList);

    @Query("UPDATE mediaInfoDDataTable SET attachmentItemLists =:attachmentItemLists WHERE filename = :filename and parent_unique_id = :unitId")
    void uploadAttListByFileName(String unitId, String filename, List<AttachmentItemList> attachmentItemLists);

    @Query("UPDATE mediaInfoDDataTable SET isDeletedItem = :isDelete WHERE filename = :filename and parent_unique_id = :parentId")
    void updateByFileName(boolean isDelete,String filename,String parentId);

    @Query("UPDATE mediaInfoDDataTable SET isHaveDelete=:flag WHERE filename = :filename and parent_unique_id = :unitId")
    void setMediaDeletedListByUrl(String unitId,String filename,boolean flag);

    @Query("UPDATE mediaInfoDDataTable SET isDeletedItem = :isDelete WHERE filename = :fileName and parent_unique_id = :parentId")
    void setDeleteItemByFileName(boolean isDelete,String fileName,String parentId);

    @Query("SELECT * FROM unitInfoDataTable WHERE unitSampleGlobalid = :unitUniqueId")
    List<UnitInfoDataModel> getUnitInfoDataByNonDate(String unitUniqueId);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE document_category = :cat and parent_unique_id = :unit and isHaveDelete =:flag")
    List<MediaInfoDataModel> getMediaInfoDataByCatUnitCount(String cat,String unit,boolean flag);

    @Query("UPDATE structureInfoPointDataTable SET structure_status = :unitStatus WHERE hut_number = :unitUniqueId")
    void updateStructureStatusDataTable(String unitUniqueId, String unitStatus);

    @Query("UPDATE mediaInfoDDataTable SET wholeObjectDeleted = :isDelete WHERE obejctId = :objId and parent_unique_id = :parentId")
    void setDeleteWholeObject(boolean isDelete,String objId,String parentId);

    @Query("UPDATE mediaInfoDDataTable SET wholeObjectDeleted = :isDelete WHERE filename = :objId and parent_unique_id = :parentId")
    void setDeleteWholeFile(boolean isDelete,String objId,String parentId);

    @Query("UPDATE mediaInfoDDataTable SET document_remarks =:remarks WHERE parent_unique_id = :unitId")
    void setRemarks(String unitId,String remarks);

    @Query("UPDATE mediaInfoDDataTable SET document_remarks =:remarks WHERE mediaId = :mediaId")
    void setRemarksByMediaId(int mediaId,String remarks);

    @Update
    void updateMember(MemberInfoDataModel model);

    @Query("DELETE FROM memberInfoDataTable WHERE memberSampleGlobalid = :hohId")
    void deleteMemberByHohId(String hohId);

    @Query("SELECT * FROM memberInfoDataTable WHERE memberSampleGlobalid = :hohId")
    List<MemberInfoDataModel> getMemberDetailsByHohId(String hohId);

    @Query("SELECT distinct(trim(unit_unique_id)) as unit_unique_id FROM unitInfoDataTable WHERE hut_number = :hutId")
    List<String> getLocalAddedUniqueIdList(String hutId);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE document_category = :cat  and document_type = :type and parent_unique_id = :unit and isHaveDelete =:flag")
    List<MediaInfoDataModel> getMediaInfoDataByCatUnitCountType(String cat,String unit,boolean flag,String type);

    @Query("SELECT work_area_name FROM structureInfoPointDataTable WHERE structure_id = :unitSampleGlobalid")
    String getWorkAreaNameByStructureId(String unitSampleGlobalid);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE parent_unique_id = :parent_id and wholeObjectDeleted = :isWholeObjectDelete")
    List<MediaInfoDataModel> getDeleteItemMediaObjList(String parent_id, boolean isWholeObjectDelete);

    @Query("SELECT * FROM unitInfoDataTable WHERE hut_number = :hutId")
    List<UnitInfoDataModel> getAllUnitsByHut(String hutId);

    @Query("SELECT * FROM mediaInfoDDataTable WHERE document_category = :cat and document_type = :type and parent_unique_id = :unit and isHaveDelete =:flag")
    List<MediaInfoDataModel> getMediaInfoDataByCatTypeUnit(String cat,String unit,boolean flag,String type);

    @Query("SELECT * FROM unitInfoDataTable WHERE unit_unique_id = :id")
    List<UnitInfoDataModel> getUnitByUniqueId(String id);

    @Query("SELECT * FROM hohInfoDataTable WHERE hohSampleGlobalid = :id")
    List<HohInfoDataModel> getHohByUniqueId(String id);

    @Query("UPDATE mediaDetailsDataTable SET objectid=:obejctId,globalId = :globalId WHERE file_name =:fileName")
    void updateMediaDetailsObjectId(String fileName,String obejctId,String globalId);

    @Query("UPDATE mediaDetailsDataTable SET isUploaded=:isUploaded,isFileUploaded = :isFileUploaded,globalid =:globalid, objectid=:objectid WHERE file_name =:file_name")
    void setMediaDetailsUploaded(boolean isUploaded,boolean isFileUploaded,String globalid,String objectid, String file_name);

    @Query("SELECT * FROM mediaDetailsDataTable WHERE rel_globalid =:rel_globalid")
    List<MediaDetailsDataModel> getMediaDetailsByRelGlobalId(String rel_globalid);

    @Query("SELECT * FROM mediaDetailsDataTable WHERE rel_globalid = :rel_globalid and file_name = :fileName")
    MediaDetailsDataModel getMediaDetailsByNameAndRelGlobalId(String rel_globalid, String fileName);

    @Query("DELETE FROM mediaDetailsDataTable WHERE rel_globalid = :rel_globalid")
    void deleteMediaDetailsByRelGlobalId(String rel_globalid);

    @Query("SELECT * FROM mediaDetailsDataTable WHERE globalid =:globalid")
    List<MediaDetailsDataModel> getMediaDetailsByGlobalId(String globalid);

    @Query("SELECT * FROM mediaDetailsDataTable")
    List<MediaDetailsDataModel> geAllMediaDetails();

    @Query("UPDATE mediaDetailsDataTable SET remarks=:remarks,isUploaded=:isUploaded,unitId=:unitId WHERE rel_globalid =:rel_globalid")
    void updateMediaDetailsRemarksByRelGlobalId(String rel_globalid,String remarks, String unitId, boolean isUploaded);

    @Query("SELECT * FROM mediaDetailsDataTable WHERE isUploaded =:isUploaded and isFileUploaded=:isFileUploaded and unitId=:unitId")
    List<MediaDetailsDataModel> getPendingMediaDetails(boolean isUploaded, boolean isFileUploaded, String unitId);

    @Query("SELECT * FROM aadhaarVerificationData WHERE unit_id =:unitId and isUploaded=:isUploaded")
    AadhaarVerificationData getAadhaarVerificationDetailsByHohId(String unitId, boolean isUploaded);

    @Query("UPDATE aadhaarVerificationData SET isUploaded=:isUploaded WHERE unit_id =:unit_id")
    void updateAadhaarDetailsVerificationUploaded(String unit_id, boolean isUploaded);

    @Query("UPDATE aadhaarVerificationData SET hoh_id=:hoh_id WHERE unit_id =:unit_id")
    void updateHohIdForAadhaarVerification(String hoh_id, String unit_id);
}