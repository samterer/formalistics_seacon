package ph.com.gs3.formalistics.model.values.business.document;

import java.util.List;

/**
 * Instead of ramming everything in the Document class (Form object and user object for
 * the author), specific purpose image_placeholder classes like this is created.
 * DocumentForDisplay contains only data needed for displaying documents. This drastically
 * decreases the memory overhead created when putting everything into one object.
 * 
 * @author Ervinne Sodusta
 * 
 */
public class DocumentForDisplay {

	private int id;
	private int webId;

	private String authorName;
	private String documentBody;

	private int formId;
	private String formName;

	private String status;

	private String dateCreated;

	private int commentCount;
	private int starMark;
	private List<DocumentAction> actions;

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

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getDocumentBody() {
		return documentBody;
	}

	public void setDocumentBody(String documentBody) {
		this.documentBody = documentBody;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getStarMark() {
		return starMark;
	}

	public void setStarMark(int starMark) {
		this.starMark = starMark;
	}

	public List<DocumentAction> getActions() {
		return actions;
	}

	public void setActions(List<DocumentAction> actions) {
		this.actions = actions;
	}

}
