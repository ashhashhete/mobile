package com.igenesys.database.dabaseModel.newDbSModels;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "structureUnitIdStatusDataTable")
public class StructureUnitIdStatusDataTable implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    // variable for our id.
    private String unitUniqueId;

    String structure_id, unitStatus;

    public StructureUnitIdStatusDataTable(@NonNull String unitUniqueId, String structure_id, String unitStatus) {
        this.unitUniqueId = unitUniqueId;
        this.structure_id = structure_id;
        this.unitStatus = unitStatus;
    }

    @NonNull
    public String getUnitUniqueId() {
        return unitUniqueId;
    }

    public void setUnitUniqueId(@NonNull String unitUniqueId) {
        this.unitUniqueId = unitUniqueId;
    }

    public String getStructure_id() {
        return structure_id;
    }

    public void setStructure_id(String structure_id) {
        this.structure_id = structure_id;
    }

    public String getUnitStatus() {
        return unitStatus;
    }

    public void setUnitStatus(String unitStatus) {
        this.unitStatus = unitStatus;
    }
}
