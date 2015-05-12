package ph.com.gs3.formalistics.model.dao.dummyimpl;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.StarMark;
import ph.com.gs3.formalistics.model.dao.DocumentsForDisplayDAO;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentAction;
import ph.com.gs3.formalistics.model.values.business.document.DocumentForDisplay;

public class DocumentsDummyDAO implements DocumentsForDisplayDAO {

	private List<DocumentForDisplay> dummyDocuments;

	private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

	private List<DocumentAction> dummyActions1;

	public DocumentsDummyDAO() {

		dummyDocuments = new ArrayList<>();

		dummyActions1 = new ArrayList<>();
		dummyActions1.add(new DocumentAction("Approve", "Approve"));
		dummyActions1.add(new DocumentAction("Reject", "Reject"));

		initializeDummyDocuments();

	}

	private void initializeDummyDocuments() {
		DocumentForDisplay document1 = new DocumentForDisplay();
		document1.setId(1);
		document1.setWebId(1);

		document1.setAuthorName("Coleen Garcia");
		document1.setDocumentBody("Applied for leave this April 13, 2015. Reason: " + LOREM_IPSUM);

		document1.setFormId(1);
		document1.setFormName("Leave Request Form");
		document1.setStatus("Awaiting Approval");

		document1.setDateCreated("April 7, 2015");

		document1.setStarMark(StarMark.STARRED);
		document1.setActions(dummyActions1);

		DocumentForDisplay document2 = new DocumentForDisplay();
		document2.setId(2);
		document2.setWebId(2);

		document2.setAuthorName("Robi Domingo");
		document2
		        .setDocumentBody("... is requesting for an expense reimbursement amounting P1,580.");

		document2.setFormId(2);
		document2.setFormName("Expense Reimbursement");
		document2.setStatus("Awaiting Approval");

		document2.setDateCreated("April 5, 2015");

		document2.setStarMark(StarMark.UNSTARRED);
		document2.setActions(dummyActions1);

		DocumentForDisplay document3 = new DocumentForDisplay();
		document3.setId(3);
		document3.setWebId(3);

		document3.setAuthorName("Kim Chui");
		document3.setDocumentBody("Applied for leave this April 12, 2015. Reason: " + LOREM_IPSUM);

		document3.setFormId(1);
		document3.setFormName("Leave Request Form");
		document3.setStatus("Awaiting Approval");

		document3.setDateCreated("April 3, 2015");

		document3.setStarMark(StarMark.UNSTARRED);
		document3.setActions(dummyActions1);

		dummyDocuments.add(document1);
		dummyDocuments.add(document2);
		dummyDocuments.add(document3);

	}

	@Override
	public List<DocumentForDisplay> getUserDocumentsForDisplay(User user) {
		return dummyDocuments;
	}

	@Override
	public List<DocumentForDisplay> getUserForApprovalDocumentsForDisplay(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DocumentForDisplay> getUserOwnedDocumentsForDisplay(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DocumentForDisplay> getUserStarredDocumentsForDisplay(User user) {
		// TODO Auto-generated method stub
		return null;
	}

}
