package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.FileStatus;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.tables.FilesTable;
import ph.com.gs3.formalistics.model.values.application.FileInfo;

/**
 * Created by Ervinne on 4/16/2015.
 */
public class FilesDAO extends DataAccessObject {

    public static final String TAG = FilesDAO.class.getSimpleName();

    public FilesDAO(Context context) {
        super(context);
    }

    public FilesDAO(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {
        super(context, preOpenedDatabaseWithTransaction);
    }

    public File saveBitmap(Bitmap bitmap, String filename) {
        String fullFileNameAndPath = context.getFilesDir() + "/" + filename;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fullFileNameAndPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return new File(fullFileNameAndPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String[] image = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri,
                image, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);
        cursor.close();

        return imagePath;
    }

    public File moveFileToInternal(File originalFile, Context context) {

        String outputFilePath = null;

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = context.getFilesDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            outputFilePath = dir.getAbsolutePath() + File.separator + originalFile.getName();

            in = new FileInputStream(originalFile);
            out = new FileOutputStream(outputFilePath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            originalFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(outputFilePath);

    }

    public Bitmap getBitmapWithFileRemoteURL(String url) {

        FileInfo fileInfo = findFileInfoForRemoteURL(url);
        Bitmap bitmap = null;

        if (fileInfo != null && fileInfo.getStatus() == FileStatus.LOCALLY_AVAILABLE) {
            bitmap = getBitmapFromPath(fileInfo.getLocalPath());
        }

        return bitmap;

    }

    public Bitmap getBitmapFromPath(String filePath) {

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError e) {
            int requiredSize = 100;

            try {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(filePath), null, o);

                int width = o.outWidth, height = o.outHeight;
                int scale = 1;
                while (true) {
                    if (width / 2 < requiredSize || height / 2 < requiredSize) {
                        break;
                    }
                    width /= 2;
                    height /= 2;
                    scale *= 2;
                }

                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                return BitmapFactory.decodeStream(new FileInputStream(filePath), null, o2);
            } catch (IOException ioe) {
                FLLogger.e(TAG, ioe.getMessage());
                ioe.printStackTrace();
                return null;
            }
        }

    }

    //<editor-fold desc="FileInfo Insert & Update Methods">
    public FileInfo insertFileInfo(FileInfo fileInfo) {

        ContentValues contentValues = createCVFromFileInfo(fileInfo);

        try {
            open();
            int insertId = (int) database.insert(FilesTable.NAME, null, contentValues);
            fileInfo.setId(insertId);
            return fileInfo;
        } finally {
            close();
        }

    }

    public FileInfo updateFileInfo(int fileInfoId, FileInfo fileInfo) throws DataAccessObjectException {

        ContentValues contentValues = createCVFromFileInfo(fileInfo);

        String whereClause = FilesTable.COL_ID + "=?";
        String[] whereArgs = {Integer.toString(fileInfoId)};

        try {
            open();
            int affectedRows = database.update(FilesTable.NAME, contentValues, whereClause, whereArgs);
            if (affectedRows > 1) {
                FLLogger.w(TAG, "Warning: more than one records are updated when updating file info " + fileInfoId);
            }

            if (affectedRows < 1) {
                throw new DataAccessObjectException("Failed to update file info " + fileInfoId);
            }

            return getFileInfo(whereClause, whereArgs);

        } finally {
            close();
        }

    }
    //</editor-fold>

    //<editor-fold desc="FileInfo Query Methods">
    public FileInfo findFileInfoForRemoteURL(String remoteURL) {

        String whereClause = FilesTable.COL_REMOTE_URL + "=?";
        String[] whereArgs = new String[]{remoteURL};
        return getFileInfo(whereClause, whereArgs);
    }

    private FileInfo getFileInfo(String whereClause, String[] whereArgs) {

        try {
            open();
            Cursor cursor = database.query(
                    FilesTable.NAME,
                    FilesTable.COLUMN_COLLECTION,
                    whereClause,
                    whereArgs,
                    null, null, null);
            cursor.moveToFirst();

            FileInfo fileInfo = null;
            if (!cursor.isAfterLast()) {
                fileInfo = cursorToFileInfo(cursor);
            }

            cursor.close();

            return fileInfo;

        } finally {
            close();
        }

    }
    //</editor-fold>

    //<editor-fold desc="Multiple FileInfo Query Methods">
    public List<FileInfo> getAllIncomingFileInfo(int userId) {

        String whereClause = FilesTable.COL_STATUS + "=? AND " + FilesTable.COL_OWNER_ID + "=?";
        String[] whereArgs = new String[]{Integer.toString(FileStatus.INCOMING), Integer.toString(userId)};

        return getFileInfos(whereClause, whereArgs);

    }

    public List<FileInfo> getAllOutgoingFileInfo(int userId) {

        String whereClause = FilesTable.COL_STATUS + "=? AND " + FilesTable.COL_OWNER_ID + "=?";
        String[] whereArgs = new String[]{Integer.toString(FileStatus.OUTGOING), Integer.toString(userId)};

        return getFileInfos(whereClause, whereArgs);

    }

    private List<FileInfo> getFileInfos(String whereClause, String[] whereArgs) {

        try {
            open();
            Cursor cursor = database.query(
                    FilesTable.NAME,
                    FilesTable.COLUMN_COLLECTION,
                    whereClause,
                    whereArgs,
                    null, null, null);
            cursor.moveToFirst();

            List<FileInfo> fileInfoList = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                fileInfoList.add(cursorToFileInfo(cursor));
                cursor.moveToNext();
            }

            cursor.close();

            return fileInfoList;

        } finally {
            close();
        }

    }
    //</editor-fold>

    //<editor-fold desc="Conversion Methods">
    private ContentValues createCVFromFileInfo(FileInfo fileInfo) {

        ContentValues cv = new ContentValues();

        cv.put(FilesTable.COL_LOCAL_PATH, fileInfo.getLocalPath());
        cv.put(FilesTable.COL_STATUS, fileInfo.getStatus());
        cv.put(FilesTable.COL_REMOTE_URL, fileInfo.getRemoteURL());
        cv.put(FilesTable.COL_OWNER_ID, fileInfo.getOwnerId());

        return cv;

    }

    private FileInfo cursorToFileInfo(Cursor cursor) {

        // @formatter:off
        int idIndex         = cursor.getColumnIndexOrThrow(FilesTable.COL_ID);
        int localPathIndex  = cursor.getColumnIndexOrThrow(FilesTable.COL_LOCAL_PATH);
        int statusIndex     = cursor.getColumnIndexOrThrow(FilesTable.COL_STATUS);
        int remoteURLIndex  = cursor.getColumnIndexOrThrow(FilesTable.COL_REMOTE_URL);
        int ownerIdIndex    = cursor.getColumnIndexOrThrow(FilesTable.COL_OWNER_ID);

        int id              = cursor.getInt(idIndex);
        String localPath    = cursor.getString(localPathIndex);
        int status          = cursor.getInt(statusIndex);
        String remoteURL    = cursor.getString(remoteURLIndex);
        int ownerId         = cursor.getInt(ownerIdIndex);
        // @formatter:on

        FileInfo fileInfo = new FileInfo();

        fileInfo.setId(id);
        fileInfo.setLocalPath(localPath);
        fileInfo.setStatus(status);
        fileInfo.setRemoteURL(remoteURL);
        fileInfo.setOwnerId(ownerId);

        return fileInfo;

    }
    //</editor-fold>

}
