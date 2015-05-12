package ph.com.gs3.formalistics.service.synchronizers;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.FileStatus;
import ph.com.gs3.formalistics.global.constants.LoggingType;
import ph.com.gs3.formalistics.model.dao.DataAccessObject.DataAccessObjectException;
import ph.com.gs3.formalistics.model.dao.FilesDAO;
import ph.com.gs3.formalistics.model.values.application.FileInfo;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.service.FileDownloader;
import ph.com.gs3.formalistics.service.FileDownloader.DownloadException;
import ph.com.gs3.formalistics.service.FileUploader;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationFailedException;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationPrematureException;

/**
 * Created by Ervinne on 4/22/2015.
 */
public class FilesSynchronizer extends AbstractSynchronizer {

    public static final String TAG = FilesSynchronizer.class.getSimpleName();
    public static LoggingType LOGGING_TYPE;

    private User activeUser;
    private FilesDAO filesDAO;

    private FileUploader fileUploader;
    private FileDownloader fileDownloader;

    public FilesSynchronizer(Context context, User activeUser) {
        super(TAG, LOGGING_TYPE);

        this.activeUser = activeUser;

        filesDAO = new FilesDAO(context);

        fileUploader = new FileUploader(context, activeUser.getCompany().getServer());
        fileDownloader = new FileDownloader(context);
    }

    public void synchronize() throws SynchronizationFailedException, SynchronizationPrematureException {

        uploadOutgoingFiles();
        downloadIncomingFiles();

    }

    public void uploadOutgoingFiles() {
        List<FileInfo> filesToUpload = filesDAO.getAllOutgoingFileInfo(activeUser.getId());

        for (FileInfo fileInfo : filesToUpload) {
            log("Uploading " + fileInfo.getLocalPath());
            File file = new File(fileInfo.getLocalPath());

            try {
                String fileURL = fileUploader.upload(file);
                fileInfo.setRemoteURL(fileURL);
                fileInfo.setStatus(FileStatus.LOCALLY_AVAILABLE);
                filesDAO.updateFileInfo(fileInfo.getId(), fileInfo);
            } catch (FileUploader.UploadException e) {
                Log.e(TAG, "Failed to upload " + fileInfo.getFieldOutgoingFileReference() + ": " + e.getMessage());
                e.printStackTrace();
            } catch (DataAccessObjectException e) {
                Log.e(TAG, "Failed to save URL of file " + fileInfo.getFieldOutgoingFileReference() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }


    }

    public void downloadIncomingFiles() {
        List<FileInfo> filesToDownload = filesDAO.getAllIncomingFileInfo(activeUser.getId());

        for (FileInfo fileInfo : filesToDownload) {
            try {
                log("Downloading " + fileInfo.getRemoteURL());
                File downloadedFile = fileDownloader.download(fileInfo.getRemoteURL());
                fileInfo.setLocalPath(downloadedFile.getAbsolutePath());
                fileInfo.setStatus(FileStatus.LOCALLY_AVAILABLE);
                // Save the newly updated file info
                filesDAO.updateFileInfo(fileInfo.getId(), fileInfo);

            } catch (DownloadException | DataAccessObjectException e) {
                e.printStackTrace();
            }
        }
    }
}
