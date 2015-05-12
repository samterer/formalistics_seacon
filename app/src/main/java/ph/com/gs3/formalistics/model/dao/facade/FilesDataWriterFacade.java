package ph.com.gs3.formalistics.model.dao.facade;

import android.content.Context;

import ph.com.gs3.formalistics.global.constants.FileStatus;
import ph.com.gs3.formalistics.model.dao.FieldOutgoingFileReferenceDAO;
import ph.com.gs3.formalistics.model.dao.FilesDAO;
import ph.com.gs3.formalistics.model.values.application.FieldOutgoingFileReference;
import ph.com.gs3.formalistics.model.values.application.FileInfo;

/**
 * Created by Ervinne on 4/22/2015.
 */
public class FilesDataWriterFacade {


    private final FilesDAO filesDAO;
    private final FieldOutgoingFileReferenceDAO fieldOutgoingFileReferenceDAO;

    public FilesDataWriterFacade(Context context) {
        filesDAO = new FilesDAO(context);
        fieldOutgoingFileReferenceDAO = new FieldOutgoingFileReferenceDAO(context);
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
