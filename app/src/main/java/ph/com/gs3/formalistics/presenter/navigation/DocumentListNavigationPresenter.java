package ph.com.gs3.formalistics.presenter.navigation;

import java.util.List;

import ph.com.gs3.formalistics.model.values.application.NavigationDrawerItem;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;
import ph.com.gs3.formalistics.model.values.business.view.DisplayReadyAction;
import ph.com.gs3.formalistics.presenter.fragment.view.NavigationDrawerFragment;

/**
 * Created by Ervinne on 4/24/2015.
 */
public interface DocumentListNavigationPresenter extends NavigationDrawerFragment.NavigationDrawerActionListener {

    void refreshCurrentView();

    List<NavigationDrawerItem> getNavigationDrawerItems();

    List<DocumentSummary> getDisplayableDocumentSummaries();

    List<DisplayReadyAction> getDisplayableOutgoingActions();

}
