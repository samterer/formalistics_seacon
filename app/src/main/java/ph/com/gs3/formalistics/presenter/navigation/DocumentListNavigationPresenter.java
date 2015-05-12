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

    public void refreshCurrentView();

    public List<NavigationDrawerItem> getNavigationDrawerItems();

    public List<DocumentSummary> getDisplayableDocumentSummaries();

    public List<DisplayReadyAction> getDisplayableOutgoingActions();

}
