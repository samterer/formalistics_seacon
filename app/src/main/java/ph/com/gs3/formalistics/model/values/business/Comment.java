package ph.com.gs3.formalistics.model.values.business;

public class Comment {
    private int id;
    private int webId;

    private int documentId;
    private int documentWebId;
    private int formWebId;
    private User author;

    private String text;
    private String dateCreated;

    private String pendingStubId;

    private boolean isCurrentlyBeingProcessed = false;
    private boolean isOutgoing = false;
    private boolean markedForDeletion = false;

    @Override
    public String toString() {
        return text;
    }

    // ===============================================================================
    //<editor-fold desc=" Getters & Setters">
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getDocumentWebId() {
        return documentWebId;
    }

    public void setDocumentWebId(int documentWebId) {
        this.documentWebId = documentWebId;
    }

    public int getFormWebId() {
        return formWebId;
    }

    public void setFormWebId(int formWebId) {
        this.formWebId = formWebId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isCurrentlyBeingProcessed() {
        return isCurrentlyBeingProcessed;
    }

    public void setCurrentlyBeingProcessed(boolean isCurrentlyBeingProcessed) {
        this.isCurrentlyBeingProcessed = isCurrentlyBeingProcessed;
    }

    public boolean isOutgoing() {
        return isOutgoing;
    }

    public void setOutgoing(boolean isOutgoing) {
        this.isOutgoing = isOutgoing;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
    }

    public String getPendingStubId() {
        return pendingStubId;
    }

    public void setPendingStubId(String pendingStubId) {
        this.pendingStubId = pendingStubId;
    }
    //</editor-fold>

}
