package ph.com.gs3.formalistics.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;

/**
 * Created by Ervinne on 4/26/2015.
 */
public class SeaconDatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = SeaconDatabaseHelper.class.getSimpleName();

    public final String DATABASE_PATH;
    public static final String DATABASE_NAME = "formalisticsseacon.db";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    private SQLiteDatabase currentlyOpenDatabase;

    public SeaconDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }

        createDatabase();
    }

    public Context getContext() {
        return context;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        super.getWritableDatabase();
        this.close();
        currentlyOpenDatabase = SQLiteDatabase.openDatabase(getDatabaseFullPath(), null, SQLiteDatabase.OPEN_READWRITE);
        return currentlyOpenDatabase;
    }

    @Override
    public void close() {
        if (currentlyOpenDatabase != null) {
            currentlyOpenDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        FLLogger.d(TAG, "onCreate");
        createDatabase();
//        db = SQLiteDatabase.openDatabase(getDatabaseFullPath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    private String getDatabaseFullPath() {
        return DATABASE_PATH + DATABASE_NAME;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDatabase() {

        if (!checkDatabase()) {
            this.getReadableDatabase();
            this.close();
            //Copy the database from assests
            copyDatabase();
            Log.e(TAG, "createDatabase database created");
        }

    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean checkDatabase() {

        File dbPath = new File(DATABASE_PATH);

        if (!dbPath.exists()) {
            dbPath.mkdirs();
        }

        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
        FLLogger.d(TAG, dbFile + " " + dbFile.exists());
        return dbFile.exists();
    }

    private void copyDatabase() {
        try {
            InputStream databaseAssetInputStream = context.getAssets().open(DATABASE_NAME);
            OutputStream databaseOutputStream = new FileOutputStream(getDatabaseFullPath());

            byte[] buffer = new byte[1024];
            int length;
            while ((length = databaseAssetInputStream.read(buffer)) > 0) {
                databaseOutputStream.write(buffer, 0, length);
            }

            databaseOutputStream.flush();
            databaseOutputStream.close();
            databaseAssetInputStream.close();
        } catch (IOException e) {
            String errorMessage = "Failed to start application, can't create database: " + e.getMessage();
            FLLogger.e(TAG, errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }


}
