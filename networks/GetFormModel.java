package com.igenesys.networks;

import com.igenesys.App;

import java.util.HashMap;
import java.util.Map;

public class GetFormModel {
    private static GetFormModel getFormModel;

    public static GetFormModel getInstance() {
        if (getFormModel == null)
            getFormModel = new GetFormModel();

        return getFormModel;
    }

    public Map<String, Object> getQueryBuilderForm(String where, String outFields, boolean returnGeometry, String orderByField, int resultRecordCount) {

        Map<String, Object> map = new HashMap<>();
        map.put("where", where);
        map.put("outFields", outFields);
        map.put("orderByFields", orderByField);
        map.put("returnGeometry", returnGeometry);
        if (resultRecordCount != 0)
            map.put("resultRecordCount", resultRecordCount);
        map.put("f", "json");
        if(!where.equalsIgnoreCase("(appname = 'GDC')")){
            if(App.getSharedPreferencesHandler().getEsriToken()!=null) {
                map.put("token", App.getSharedPreferencesHandler().getEsriToken());
            }
        }

        return map;
    }

    public Map<String, Object> getQueryBuilderForm(String where, String outFields, boolean returnGeometry, String orderByField, boolean isOrderBy, boolean isDistinctValue) {

        Map<String, Object> map = new HashMap<>();
        map.put("where", where);
        map.put("outFields", outFields);
        map.put("returnGeometry", returnGeometry);
        map.put("returnDistinctValues", isDistinctValue);
        if (isOrderBy)
            map.put("orderByFields", orderByField);
        map.put("f", "json");

        if(App.getSharedPreferencesHandler().getEsriToken()!=null) {
            map.put("token", App.getSharedPreferencesHandler().getEsriToken());
        }

        return map;
    }

    public Map<String, Object> getQueryNagarList(String where, String outFields, boolean returnGeometry, boolean isDistinctValue) {

        Map<String, Object> map = new HashMap<>();
        map.put("where", where);
        map.put("outFields", outFields);
        map.put("returnGeometry", returnGeometry);
        map.put("returnDistinctValues", isDistinctValue);
        map.put("f", "json");
        if(App.getSharedPreferencesHandler().getEsriToken()!=null) {
            map.put("token", App.getSharedPreferencesHandler().getEsriToken());
        }

        return map;
    }

    public Map<String, Object> getQueryBuilderForm(String where, String outField, boolean isGeometry, boolean isOrderBy, boolean isDistinctValue) {

        Map<String, Object> map = new HashMap<>();
        map.put("f", "json");
        map.put("where", where);
        map.put("returnGeometry", isGeometry);

        if (isOrderBy)
            map.put("orderByFields", outField);

        map.put("returnDistinctValues", isDistinctValue);

        map.put("geometryType", "esriGeometryEnvelope");
        map.put("spatialRel", "esriSpatialRelIntersects");
        map.put("outFields", outField);
        if(App.getSharedPreferencesHandler().getEsriToken()!=null) {
            map.put("token", App.getSharedPreferencesHandler().getEsriToken());
        }

        return map;
    }

    public Map<String, Object> getUpdateFeatureBuilderForm(String features) {

        Map<String, Object> map = new HashMap<>();
        map.put("features", features);
        map.put("outFields", "*");
        map.put("rollbackOnFailure", false);
        map.put("f", "json");
        if(App.getSharedPreferencesHandler().getEsriToken()!=null) {
            map.put("token", App.getSharedPreferencesHandler().getEsriToken());
        }

        return map;
    }

    public Map<String, Object> getCountQueryBuilderForm(String where) {

        Map<String, Object> map = new HashMap<>();
        map.put("where", where);
        map.put("returnCountOnly", true);
        map.put("f", "json");
        if(App.getSharedPreferencesHandler().getEsriToken()!=null) {
            map.put("token", App.getSharedPreferencesHandler().getEsriToken());
        }

        return map;
    }

    public Map<String, Object> getQueryBuilderForm(String input, String ids) {

        Map<String, Object> map = new HashMap<>();
        map.put("Input", input);
        // map.put("attachmentIds", ids);
        map.put("f", "json");
        if(App.getSharedPreferencesHandler().getEsriToken()!=null) {
            map.put("token", App.getSharedPreferencesHandler().getEsriToken());
        }

        return map;
    }

    public Map<String, Object> getQueryBuilderMediaObject(String input, String ids) {

        Map<String, Object> map = new HashMap<>();
        map.put("Input", input);
        map.put("objectIds", ids);
        map.put("f", "json");
        if(App.getSharedPreferencesHandler().getEsriToken()!=null) {
            map.put("token", App.getSharedPreferencesHandler().getEsriToken());
        }

        return map;
    }
}