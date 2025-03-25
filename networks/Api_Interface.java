package com.igenesys.networks;

import com.google.gson.JsonElement;
import com.igenesys.model.AddRecordResponse;
import com.igenesys.model.AfterLoginData;
import com.igenesys.model.AfterLogoutData;
import com.igenesys.model.AttachmentAddedResponse;
import com.igenesys.model.AttachmentQueryResult;
import com.igenesys.model.CountModel;
import com.igenesys.model.DeleteAttachmentModel;
import com.igenesys.model.DomainModel;
import com.igenesys.model.LoginModel;
import com.igenesys.model.NMDPLResponseList;
import com.igenesys.model.NagarListModel;
import com.igenesys.model.ResultQueryModel;
import com.igenesys.model.Root;
import com.igenesys.model.RootDeleteMediaObject;
import com.igenesys.model.UpdatedRecordResponse;
import com.igenesys.model.UploadAttachmentResult;
import com.igenesys.model.UploadVideoModel;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api_Interface {

    @FormUrlEncoded
    @POST("{path}/query")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<ResultQueryModel> getQueryResult(@Path("path") String path, @FieldMap Map<String, Object> map);

    @GET("{path}/{objectId}/attachments?f=pjson")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<AttachmentQueryResult> getAttachmentResult(@Path("path") String path,
                                                          @Path("objectId") String objectId);

    @GET("FC_NagarNames/FeatureServer/0/query?") // dev v4
//     @GET("FC_NagarNames/FeatureServer/1/query?") // stage v4
//     @GET("FC_NagarNames/FeatureServer/0/query?") // live v4
    Observable<NagarListModel> getNagarResult(@Query("where") String where, @Query("outFields") String outFields,
                                              @Query("returnGeometry") boolean returnGeometry, @Query("returnDistinctValues") boolean returnDistinctValues,
                                              @Query("f") String f);

    @GET("FC_NagarNames/FeatureServer/0/query")
    Call<NagarListModel> getData(@Query("where") String where, @Query("outFields") String outFields,
                                 @Query("returnGeometry") boolean returnGeometry, @Query("returnDistinctValues") boolean returnDistinctValues,
                                 @Query("f") String f);

    @FormUrlEncoded
    @POST("{path}/updateFeatures")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<UpdatedRecordResponse> updateFeature(@Path("path") String path, @FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("{path}/addFeatures")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<AddRecordResponse> addFeature(@Path("path") String path, @FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("{path}/query")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<CountModel> getQueryCountQueryResult(@Path("path") String path, @FieldMap Map<String, Object> map);

    @Multipart
    @POST("{path}/{objectId}/addAttachment")
    Observable<AttachmentAddedResponse> uploadAttachment(@Path("path") String path,
                                                         @Path("objectId") String objectId,
                                                         @Part("f") RequestBody f,
                                                         @Part MultipartBody.Part attachment,
                                                         @Query("token") String token);

    // @Multipart
    @POST("files")
    @Headers("Content-Type: application/octet-stream")
    Observable<UploadVideoModel> uploadVideo(@Header("fileName") String fileName,
                                             @Header("fileExt") String fileExt,
                                             @Header("partNumbers") String partNumbers,
                                             @Header("partNumber") String partNumber,
                                             @Header("fileSize") String fileSize,
                                             @Header("uploadOffset") String uploadOffset,
                                             @Header("uploadLength") String uploadLength,
                                             @Header("userName") String userName,
                                             @Header("filePath") String filePath,
                                             // @Part MultipartBody.Part file);
                                             @Body RequestBody body);

    // @Multipart
    @POST("attachments/uploadAttachment")
    @Headers({"Content-Type: application/octet-stream",
            "Access-Token: a4ae15a3e2a00fd725236484d6195482cfe221e94b7d8c305a3b7052edc4b6ff8955c91a026d2d543e4b00fcc605a2506513ce1bf2cefc7f6c15dd93f5ec7310"})
    Observable<UploadAttachmentResult> uploadAttachments(@Header("partNumbers") String partNumbers,
                                                         @Header("uploadOffset") String uploadOffset,
                                                         @Header("fileSize") String fileSize,
                                                         @Header("userName") String userName,
                                                         @Header("fileExt") String fileExt,
                                                         @Header("documentSource") String documentSource,
                                                         @Header("documentCategory") String documentCategory,
                                                         @Header("documentType") String documentType,
                                                         @Header("relGlobalId") String relGlobalId,
                                                         @Header("remarks") String remarks,
                                                         @Header("fileName") String fileName,
                                                         @Header("objectid") String objectId,
                                                         @Body RequestBody body);

    @FormUrlEncoded
    @POST("{path}/{objectId}/deleteAttachments")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<Root> deleteAttachment(@Path("path") String path,
                                      @Path("objectId") String objectId,
                                      @FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("{path}/deleteFeatures")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Observable<RootDeleteMediaObject> deleteWholeMediaObject(@Path("path") String path,
                                                             @FieldMap Map<String, Object> map);

    // @GET("getDomainInfo?domainName=marital_status&domainName=relationship_with_hoh&domainName=domain_religion&domainName=domain_service_status&domainName=respondent_remarks&domainName=domain_state&domainName=domain_property_type&domainName=domain_doc_type&domainName=member_not_available&domainName=surveyors_name&domainName=media_table&domainName=domain_occupancy_type&domainName=domain_yes_no&domainName=domain_gender&domainName=domain_occupation&domainName=place_of_work&domainName=domain_educational_qualification&domainName=domain_floor&domainName=domain_structure_year&domainName=client_qc_remarks&domainName=FC_NagarNames&domainName=client%20_qc_status&domainName=adani_qc_status")
    @GET("getDomainInfo")
    @Headers("Secret-Key: VMuD8Ej6gT9naycKsgC0eZJeqYXmSgaZ")
    Call<DomainModel> getDomainData();

    @GET("getWorkAreaList")
    Call<JsonElement> getWorkAreaData(@Query("username") String userName);

    @GET
    Call<ResponseBody> getEsriConfigInfo(@Url String url);

    @POST("authenticate")
    Call<LoginModel> validateUser(@Body RequestBody params);

    @POST("sendOTP")
    @Headers("Access-Token: a4ae15a3e2a00fd725236484d6195482cfe221e94b7d8c305a3b7052edc4b6ff8955c91a026d2d543e4b00fcc605a2506513ce1bf2cefc7f6c15dd93f5ec7310")
    Call<JsonElement> sendOTP(@Body RequestBody params);

    @POST("verifyOTP")
    @Headers("Access-Token: a4ae15a3e2a00fd725236484d6195482cfe221e94b7d8c305a3b7052edc4b6ff8955c91a026d2d543e4b00fcc605a2506513ce1bf2cefc7f6c15dd93f5ec7310")
    Call<JsonElement> verifyOTP(@Body RequestBody params);

    @FormUrlEncoded
    @POST("generateToken")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<JsonElement> generateESRIToken(@FieldMap Map<String, Object> map);

    @POST("attachments/deleteAttachment/{mediaInfoObjectId}/{mediaDetailsObjectId}")
    Call<DeleteAttachmentModel> deleteSingleMediaAttachment(@Path("mediaInfoObjectId") String mediaInfoObjectId,
                                                            @Path("mediaDetailsObjectId") String mediaDetailsObjectId);

    @POST("attachments/deleteMultipleAttachments")
    @Headers("Access-Token: a4ae15a3e2a00fd725236484d6195482cfe221e94b7d8c305a3b7052edc4b6ff8955c91a026d2d543e4b00fcc605a2506513ce1bf2cefc7f6c15dd93f5ec7310")
    Call<JsonElement> deleteMultipleAttachments(@Body RequestBody requestBody);

    @GET
    Call<JsonElement> getShortNameJson(@Url String url);

    @GET
    Call<JsonElement> getDrpOfficersDetails(@Url String url);

    @POST
    Call<JsonElement> generateODORequestToken(@Url String url, @Header("Secret-Key") String secretKey, @Body RequestBody requestBody);

    @POST("generate?code=3fuFdsH-RelbYREVAzmI88JTWKlDYczhAj0QVzWsiGd1AzFuIoGShg%3D%3D")
    @Headers("x-api-key: ss-h885524422116af6d5cbbe8aa210ac2q0de0826cad5bd798dbb65a0a6aqf35dk")
    Call<JsonElement> generateAadhaarOTP(@Body RequestBody body);

    @POST("verify?code=3fuFdsH-RelbYREVAzmI88JTWKlDYczhAj0QVzWsiGd1AzFuIoGShg%3D%3D")
    @Headers("x-api-key: ss-h885524422116af6d5cbbe8aa210ac2q0de0826cad5bd798dbb65a0a6aqf35dk")
    Call<JsonElement> verifyAadhaarOTP(@Body RequestBody body);

    @POST("aadhar-ops/saveUpdateAadharVerifyDetails")
    @Headers({"Content-Type: application/json",
            "Access-Token: a4ae15a3e2a00fd725236484d6195482cfe221e94b7d8c305a3b7052edc4b6ff8955c91a026d2d543e4b00fcc605a2506513ce1bf2cefc7f6c15dd93f5ec7310"})
    Call<JsonElement> saveUpdateAadhaarVerifyDetails(@Body RequestBody requestBody);

    // Call<WorkAreaDomainModel> getWorkAreaData(@Query("username")String userName);

    @POST("surveyor/loginTime")
    @Headers({"Content-Type: application/json",
            "Access-Token: a4ae15a3e2a00fd725236484d6195482cfe221e94b7d8c305a3b7052edc4b6ff8955c91a026d2d543e4b00fcc605a2506513ce1bf2cefc7f6c15dd93f5ec7310"})
    Call<AfterLoginData> validateAfterLoginUser(@Body RequestBody params);

    @POST("surveyor/logoutTime")
    @Headers({"Content-Type: application/json",
            "Access-Token: a4ae15a3e2a00fd725236484d6195482cfe221e94b7d8c305a3b7052edc4b6ff8955c91a026d2d543e4b00fcc605a2506513ce1bf2cefc7f6c15dd93f5ec7310"})
    Call<AfterLogoutData> validateAfterLogoutUser(@Body RequestBody params);

    @GET("getNMDPLOfficerList")
//    @Headers({"Content-Type: application/json",
//            "Access-Token: a4ae15a3e2a00fd725236484d6195482cfe221e94b7d8c305a3b7052edc4b6ff8955c91a026d2d543e4b00fcc605a2506513ce1bf2cefc7f6c15dd93f5ec7310"})
    @Headers("Secret-Key: VMuD8Ej6gT9naycKsgC0eZJeqYXmSgaZ")
    Call<NMDPLResponseList> getNMDPLOfficersList();
}