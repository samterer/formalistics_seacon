package ph.com.gs3.formalistics.model.dao.facade.search;

import android.content.Context;

import org.json.JSONException;

import java.util.EnumSet;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;
import ph.com.gs3.formalistics.model.values.business.form.Form;

/**
 * Created by Ervinne on 5/11/2015.
 */
public class DefaultSearchDataProvider implements SearchDataProvider {

    private final User activeUser;

    private final FormsDAO formsDAO;
    private final DocumentsDAO documentsDAO;

    public DefaultSearchDataProvider(Context context, User activeUser) {
        this.activeUser = activeUser;
        formsDAO = new FormsDAO(context);
        documentsDAO = new DocumentsDAO(context);
    }

    @Override
    public List<DocumentSummary> searchDocumentSummaries(EnumSet<DocumentSearchType> searchTypeSet, String searchFilter, int fromIndex, int fetchCount) {

        // FIXME: throw exceptions for caught exceptions here

        try {
            if (searchTypeSet.contains(DocumentSearchType.DEFAULT_STARRED)) {
                return documentsDAO.getStarredDocumentSummaries(activeUser.getId(), fromIndex, fetchCount);
            } else if (searchTypeSet.contains(DocumentSearchType.DEFAULT_INBOX)) {
                if (searchFilter == null || "".equals(searchFilter)) {
                    return documentsDAO.getUserDocumentSummaries(activeUser.getId(), fromIndex, fetchCount);
                } else {
                    try {
                        List<Form> forms = formsDAO.getCompanyForms(activeUser.getCompany().getId());
                        return documentsDAO.searchForUserDocumentSummaries(activeUser, forms, searchFilter);
                    } catch (DataAccessObject.DataAccessObjectException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid search type set " + searchTypeSet);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
