package ph.com.gs3.formalistics.model.values.application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ervinne on 5/5/2015.
 */
public class VersionSettings {

    public AvailableVersion version = AvailableVersion.DEFAULT;

    public boolean enableDocumentCreationGlobally = true;
    public List<Integer> enableDocumentCreationOnFormIdList = new ArrayList<>();
    public boolean showFilterByQRCodeButton = false;

    /**
     * If set to true, this will prevent other forms other that the forms specified in formIdListToSynchronize
     * to synchronize their documents.
     */
    public boolean partiallySynchronize = false;

    /**
     * Will be used if partiallySynchronize is set to true.
     * This will contain all the form id that will have their documents synchronized,
     * the rest of the forms will only synchronize their documents with a separate option on the
     * navigation drawer
     */
    public final List<Integer> formIdListToSynchronize = new ArrayList<>();

    public enum AvailableVersion {
        DEFAULT, SEACON, SMARTMATIC
    }

    public VersionSettings() {
    }

    public VersionSettings(AvailableVersion version) {
        this.version = version;
    }

}
