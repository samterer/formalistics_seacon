package ph.com.gs3.formalistics.model.values.application;

import java.io.Serializable;

/**
 * Created by Ervinne on 4/22/2015.
 */
public class FileInfo implements Serializable {

    private int id;
    private String localPath;
    private int status; // enum FileStatus
    private String remoteURL;
    private int ownerId;

    private FieldOutgoingFileReference fieldOutgoingFileReference;

    //<editor-fold desc="Getters & Setters">
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemoteURL() {
        return remoteURL;
    }

    public void setRemoteURL(String remoteURL) {
        this.remoteURL = remoteURL;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public FieldOutgoingFileReference getFieldOutgoingFileReference() {
        return fieldOutgoingFileReference;
    }

    public void setFieldOutgoingFileReference(FieldOutgoingFileReference fieldOutgoingFileReference) {
        this.fieldOutgoingFileReference = fieldOutgoingFileReference;
    }

    //</editor-fold>
}
