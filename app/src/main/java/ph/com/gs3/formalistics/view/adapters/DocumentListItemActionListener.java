package ph.com.gs3.formalistics.view.adapters;

import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;

public interface DocumentListItemActionListener {

    public void onOpenDocumentActionsCommand(DocumentSummary source);

    public void onToggleDocumentStarMarkCommand(DocumentSummary source);

    public void onOpenDocumentCommentsCommand(DocumentSummary source);

}