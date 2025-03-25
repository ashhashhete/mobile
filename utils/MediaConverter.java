package com.igenesys.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
//import com.techaidsolution.gdc_app.database.dabaseModel.newDbSModels.MediaInfoDataModel;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class MediaConverter {

    private static Gson gson = new Gson();
    @TypeConverter
    public static List<MediaInfoDataModel> stringToListServer(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<MediaInfoDataModel>>() {}.getType();
        return gson.fromJson(data, listType);
    }
    @TypeConverter
    public static String listServerToString(List<MediaInfoDataModel> someObjects) {
        return gson.toJson(someObjects);
    }
}
