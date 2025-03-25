package com.igenesys.networks;

import com.igenesys.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static Retrofit retrofit = null;

    private final static OkHttpClient okHttpClientAttachment = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .addInterceptor(chain -> {
                Request originalRequest = chain.request();
                Request newRequest = originalRequest.newBuilder()
                        .header("User-Agent", "drppl")
                        .header("Strict-Transport-Security", "max-age=31536000")
                        .header("Content-Security-Policy", "default-src 'self'")
                        .addHeader("Access-Control-Allow-Origin", Constants.Base_URL)
                        .build();
                return chain.proceed(newRequest);
            }).build();

    private final static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(chain -> {
                Request originalRequest = chain.request();
                Request newRequest = originalRequest.newBuilder()
                        .header("User-Agent", "drppl")
                        .header("Strict-Transport-Security", "max-age=31536000")
                        .header("Content-Security-Policy", "default-src 'self'")
                        .addHeader("Access-Control-Allow-Origin", Constants.Base_URL)
                        .build();
                return chain.proceed(newRequest);
            }).build();

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.Nagar_Base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    @SuppressWarnings("unchecked")
    public static <S> S createRetrofitArcGisUserInfoFS() {
        return new Retrofit.Builder()
                .baseUrl(Constants.UserInfo_FS_BASE_URL_ARC_GIS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.client(addLogger(okHttpClient))
                .build().create((Class<S>) Api_Interface.class);
    }

    @SuppressWarnings("unchecked")
    public static <S> S createRetrofitArcGisWorkAreaFS() {
        return new Retrofit.Builder()
                .baseUrl(Constants.WorkArea_FS_BASE_URL_ARC_GIS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.client(addLogger(okHttpClient))
                .build().create((Class<S>) Api_Interface.class);
    }

    @SuppressWarnings("unchecked")
    public static <S> S createRetrofitArcGisWorkAreaMS() {
        return new Retrofit.Builder()
                .baseUrl(Constants.WorkArea_MS_BASE_URL_ARC_GIS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.client(addLogger(okHttpClient))
                .build().create((Class<S>) Api_Interface.class);
    }

    @SuppressWarnings("unchecked")
    public static <S> S createRetrofitArcGisStructureInfoMS() {
        return new Retrofit.Builder()
                .baseUrl(Constants.StructureInfo_MS_BASE_URL_ARC_GIS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.client(addLogger(okHttpClient))
                .build().create((Class<S>) Api_Interface.class);
    }

    @SuppressWarnings("unchecked")
    public static <S> S createRetrofitArcGisStructureInfoFS() {
        return new Retrofit.Builder()
                .baseUrl(Constants.StructureInfo_FS_BASE_URL_ARC_GIS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClientAttachment)
                .build().create((Class<S>) Api_Interface.class);
    }

    @SuppressWarnings("unchecked")
    public static <S> S createRetrofitArcGisAppVersionInfoFS() {
        return new Retrofit.Builder()
                .baseUrl(Constants.AppVersion_FS_BASE_URL_ARC_GIS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.client(addLogger(okHttpClient))
                .build().create((Class<S>) Api_Interface.class);
    }

    @SuppressWarnings("unchecked")
    public static <S> S createRetrofitArcGisNagarList() {
        return getClient().create((Class<S>) Api_Interface.class);
    }

    @SuppressWarnings("unchecked")
    public static <S> S createRetrofitArcGisUploadVideo() {
        return new Retrofit.Builder()
                .baseUrl(Constants.Upload_Video)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClientAttachment)
                .build().create((Class<S>) Api_Interface.class);
    }

    public static Retrofit getDomainClient() {
        return new Retrofit.Builder()
                .baseUrl(Constants.DOMAIN_DATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static Retrofit getConfigClient() {
        return new Retrofit.Builder()
                .baseUrl(Constants.CONFIG_DATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static Retrofit getSMSClient() {
        return new Retrofit.Builder()
                .baseUrl(Constants.SMS_SERVICE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static Retrofit getESRIClient() {
        return new Retrofit.Builder()
                .baseUrl(Constants.ESRI_TOKEN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static Retrofit getAadhaarVerificationClient() {
        return new Retrofit.Builder()
                .baseUrl(Constants.AADHAAR_VERIFICATION)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static Retrofit getAfterLogin() {
        return new Retrofit.Builder()
                .baseUrl(Constants.DOMAIN_DATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static Retrofit getAfterLogout() {
        return new Retrofit.Builder()
                .baseUrl(Constants.DOMAIN_DATA_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }
}