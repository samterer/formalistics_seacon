package ph.com.gs3.formalistics.model.dao;

import java.util.List;

import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentForDisplay;

public interface DocumentsForDisplayDAO {

	public List<DocumentForDisplay> getUserDocumentsForDisplay(User user);

	public List<DocumentForDisplay> getUserForApprovalDocumentsForDisplay(User user);

	public List<DocumentForDisplay> getUserOwnedDocumentsForDisplay(User user);

	public List<DocumentForDisplay> getUserStarredDocumentsForDisplay(User user);
}
