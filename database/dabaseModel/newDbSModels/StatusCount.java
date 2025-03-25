package com.igenesys.database.dabaseModel.newDbSModels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

public class StatusCount {
    @ColumnInfo(name = "countInProgress")
    int countInProgress;

    @ColumnInfo(name = "countNotStarted")
    int countNotStarted;

    @ColumnInfo(name = "countCompleted")
    int countCompleted;

    @ColumnInfo(name = "countonHold")
    int countonHold;

    public int getCountInProgress() {
        return countInProgress;
    }

    public void setCountInProgress(int countInProgress) {
        this.countInProgress = countInProgress;
    }

    public int getCountNotStarted() {
        return countNotStarted;
    }

    public void setCountNotStarted(int countNotStarted) {
        this.countNotStarted = countNotStarted;
    }

    public int getCountCompleted() {
        return countCompleted;
    }

    public void setCountCompleted(int countCompleted) {
        this.countCompleted = countCompleted;
    }

    public int getCountonHold() {
        return countonHold;
    }

    public void setCountonHold(int countonHold) {
        this.countonHold = countonHold;
    }
}
