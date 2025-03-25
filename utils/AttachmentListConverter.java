package com.igenesys.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igenesys.model.AttachmentItemList;
//import com.techaidsolution.gdc_app.model.AttachmentItemList;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class AttachmentListConverter {

    private static Gson gson = new Gson();
    @TypeConverter
    public static List<AttachmentItemList> stringToListServer(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<AttachmentItemList>>() {}.getType();
        return gson.fromJson(data, listType);
    }
    @TypeConverter
    public static String listServerToString(List<AttachmentItemList> someObjects) {
        return gson.toJson(someObjects);
    }
}
