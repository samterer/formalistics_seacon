package ph.com.gs3.formalistics.model.dao.facade.search;

import android.content.Context;

import org.json.JSONException;

import java.util.EnumSet;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;

/**
 * Created by Ervinne on 5/11/2015.
 */
public class DefaultSearchDataProvider implements SearchDataProvider {

    private User activeUser;

    private DocumentsDAO documentsDAO;

    public DefaultSearchDataProvider(Context context, User activeUser) {
        this.activeUser = activeUser;
        documentsDAO = new DocumentsDAO(context);
    }

    @Override
    public List<DocumentSummary> searchDocumentSummaries(EnumSet<DocumentSearchType> searchTypeSet, String searchFilter, int fromIndex, int fetchCount) {

        try {
            if (searchTypeSet.contains(DocumentSearchType.DEFAULT_INBOX)) {
                return documentsDAO.getUserDocumentSummaries(activeUser.getId(), fromIndex, fetchCount);
            } else if (searchTypeSet.contains(DocumentSearchType.DEFAULT_STARRED)) {
                return documentsDAO.getStarredDocumentSummaries(activeUser.getId(), fromIndex, fetchCount);
            } else {
                throw new IllegalArgumentException("Invalid search type set " + searchTypeSet);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
