package ph.com.gs3.formalistics.model;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.model.values.application.VersionSettings;

/**
 * Created by Ervinne on 4/26/2015.
 */
public class DatabaseHelperFactory {

    private static SQLiteOpenHelper defaultDatabaseHelper;
    private static SQLiteOpenHelper seaconDatabaseHelper;

    public static SQLiteOpenHelper getDatabaseHelper(Context context) {

        VersionSettings versionSettings = FormalisticsApplication.versionSettings;

//        if (versionSettings.version == VersionSettings.AvailableVersion.SEACON) {
//            return getSeaconDatabaseHelper(context);
//        }
//
//        return getDefaultDatabaseHelper(context);

        if (versionSettings.version == VersionSettings.AvailableVersion.SEACON) {
            return new SeaconDatabaseHelper(context);
        }

        return new DefaultDatabaseHelper(context);

    }

    private static SQLiteOpenHelper getDefaultDatabaseHelper(Context context) {
        if (defaultDatabaseHelper == null) {
            defaultDatabaseHelper = new DefaultDatabaseHelper(context);
        }

        return defaultDatabaseHelper;
    }

    private static SQLiteOpenHelper getSeaconDatabaseHelper(Context context) {
        if (seaconDatabaseHelper == null) {
            seaconDatabaseHelper = new SeaconDatabaseHelper(context);
        }

        return seaconDatabaseHelper;
    }

}
