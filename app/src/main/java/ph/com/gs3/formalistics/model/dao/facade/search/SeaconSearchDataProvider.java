package ph.com.gs3.formalistics.model.dao.facade.search;

import java.util.EnumSet;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;

/**
 * Created by Ervinne on 5/11/2015.
 */
public class SeaconSearchDataProvider implements SearchDataProvider {
    @Override
    public List<DocumentSummary> searchDocumentSummaries(EnumSet<DocumentSearchType> searchTypeSet, String searchFilter, int fromIndex, int fetchCount) {
        return null;
    }
}