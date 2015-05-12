package ph.com.gs3.formalistics.view.adapters;

import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;

public interface DocumentListItemActionListener {

    void onOpenDocumentActionsCommand(DocumentSummary source);

    void onToggleDocumentStarMarkCommand(DocumentSummary source);

    void onOpenDocumentCommentsCommand(DocumentSummary source);

}