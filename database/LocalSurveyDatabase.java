package com.igenesys.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.igenesys.database.dabaseModel.Converters;
import com.igenesys.database.dabaseModel.newDbSModels.AadhaarVerificationData;
import com.igenesys.database.dabaseModel.newDbSModels.DownloadedWebMapModel;
import com.igenesys.database.dabaseModel.newDbSModels.HohInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaDetailsDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MediaInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.MemberInfoDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureInfoPointDataModel;
import com.igenesys.database.dabaseModel.newDbSModels.StructureUnitIdStatusDataTable;
import com.igenesys.database.dabaseModel.newDbSModels.UnitInfoDataModel;
import com.igenesys.utils.ArrayConverters;
import com.igenesys.utils.AttachmentListConverter;
import com.igenesys.utils.MediaConverter;

// version = 7)
@Database(entities = {StructureInfoPointDataModel.class, UnitInfoDataModel.class, HohInfoDataModel.class,
        MemberInfoDataModel.class, MediaInfoDataModel.class, MediaDetailsDataModel.class,
        DownloadedWebMapModel.class, StructureUnitIdStatusDataTable.class, AadhaarVerificationData.class},
        version = 4, exportSchema = false)

@TypeConverters({Converters.class, ArrayConverters.class, MediaConverter.class, AttachmentListConverter.class})
public abstract class LocalSurveyDatabase  extends RoomDatabase {

    // below line is to create instance
    // for our database class.
    private static LocalSurveyDatabase instance;
    // below line is to create a callback for our room database.
    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // this method is called when database is created
            // and below line is to populate our data.
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    // on below line we are getting instance for our database.
    public static synchronized LocalSurveyDatabase getInstance(Context context) {
        // below line is to check if
        // the instance is null or not.
        if (instance == null) {
            // if the instance is null we
            // are creating a new instance
            instance =
                    // for creating a instance for our database
                    // we are creating a database builder and passing
                    // our database class with our database name.
                    Room.databaseBuilder(context.getApplicationContext(), LocalSurveyDatabase.class, "gdc_survey_database")
                            // below line is use to add fall back to
                            // destructive migration to our database.
                            .fallbackToDestructiveMigration()
                            // below line is to add callback
                            // to our database.
                            .addCallback(roomCallback).allowMainThreadQueries()
                            // below line is to
                            // build our database.
                            .build();
        }
        // after creating an instance
        // we are returning our instance
        return instance;
    }

    // below line is to create
    // abstract variable for dao.
    public abstract LocalSurveyDao localSurveyDao();

    // we are creating an async task class to perform task in background.
    public static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        PopulateDbAsyncTask(LocalSurveyDatabase instance) {
            LocalSurveyDao LocalSurveyDao = instance.localSurveyDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
