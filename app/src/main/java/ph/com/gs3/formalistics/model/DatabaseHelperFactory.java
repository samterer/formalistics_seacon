package ph.com.gs3.formalistics.model;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.model.values.application.VersionSettings;

/**
 * Created by Ervinne on 4/26/2015.
 */
public class DatabaseHelperFactory {

    public static SQLiteOpenHelper getDatabaseHelper(Context context) {

        VersionSettings versionSettings = FormalisticsApplication.versionSettings;

        if (versionSettings.version == VersionSettings.AvailableVersion.SEACON) {
            return new SeaconDatabaseHelper(context);
        }

        return new DefaultDatabaseHelper(context);

    }

}
