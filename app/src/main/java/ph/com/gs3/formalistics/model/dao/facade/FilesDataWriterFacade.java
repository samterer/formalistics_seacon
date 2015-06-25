package ph.com.gs3.formalistics.model.dao.facade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import ph.com.gs3.formalistics.global.constants.FileStatus;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.dao.DynamicFormFieldsDAO;
import ph.com.gs3.formalistics.model.dao.FieldOutgoingFileReferenceDAO;
import ph.com.gs3.formalistics.model.dao.FilesDAO;
import ph.com.gs3.formalistics.model.dao.UserDocumentsDAO;
import ph.com.gs3.formalistics.model.values.application.FieldOutgoingFileReference;
import ph.com.gs3.formalistics.model.values.application.FileInfo;

/**
 * Created by Ervinne on 4/22/2015.
 */
public class FilesDataWriterFacade {


    private FilesDAO filesDAO;
    private FieldOutgoingFileReferenceDAO fieldOutgoingFileReferenceDAO;

    public FilesDataWriterFacade(Context context) {
        initializeDAOs(context, null);
    }

    public FilesDataWriterFacade(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {
        initializeDAOs(context, preOpenedDatabaseWithTransaction);
    }

    public void initializeDAOs(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {

        if (preOpenedDatabaseWithTransaction != null) {
            filesDAO = new FilesDAO(context, preOpenedDatabaseWithTransaction);
            fieldOutgoingFileReferenceDAO = new FieldOutgoingFileReferenceDAO(context, preOpenedDatabaseWithTransaction);
        } else {
            filesDAO = new FilesDAO(context);
            fieldOutgoingFileReferenceDAO = new FieldOutgoingFileReferenceDAO(context);
        }

    }

    public FileInfo saveIncomingFileInfo(String remoteURL, int userId) {

        // check first if a file info for this already exists
        FileInfo existingFileInfo = filesDAO.findFileInfoForRemoteURL(remoteURL);
        FileInfo savedFileInfo = existingFileInfo; // if in case it's already existing, no need to save again

        if (existingFileInfo == null) {
            FileInfo fileInfo = new FileInfo();

            fileInfo.setLocalPath("");
            fileInfo.setRemoteURL(remoteURL);
            fileInfo.setStatus(FileStatus.INCOMING);
            fileInfo.setOwnerId(userId);

            savedFileInfo = filesDAO.insertFileInfo(fileInfo);
        }

        return savedFileInfo;

    }

    public FileInfo saveOutgoingFileInfo(FileInfo fileInfo) {

        fileInfo.setRemoteURL("");
        FieldOutgoingFileReference fieldOutgoingFileReference = fileInfo.getFieldOutgoingFileReference();
        FileInfo savedFileInfo = filesDAO.insertFileInfo(fileInfo);

        fieldOutgoingFileReference.setOutgoingFileId(savedFileInfo.getId());
        fieldOutgoingFileReferenceDAO.insertFieldOutgoingFileReference(fieldOutgoingFileReference);

        return savedFileInfo;

    }

}
