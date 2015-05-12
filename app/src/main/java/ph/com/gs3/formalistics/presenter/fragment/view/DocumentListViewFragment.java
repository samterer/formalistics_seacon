package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;
import ph.com.gs3.formalistics.model.values.business.view.DisplayReadyAction;
import ph.com.gs3.formalistics.view.adapters.DisplayReadActionListViewAdapter;
import ph.com.gs3.formalistics.view.adapters.DocumentListItemActionListener;
import ph.com.gs3.formalistics.view.adapters.DocumentListViewAdapter;
import ph.com.gs3.formalistics.view.utilities.InfiniteScrollListener;

public class DocumentListViewFragment extends Fragment {

    public static final String TAG = DocumentListViewFragment.class.getSimpleName();
    public static final String PROMPT_NO_CONNECTION = "No connection to server";
    public static final String PROMPT_HAS_CONNECTION = "Connected to server";
    public static final String PROMPT_CONNECTING = "Connecting to %s";

    public static final String COLOR_ERROR = "#dbdbdb";
    public static final String COLOR_PROGRESS = "#ff9000";
    public static final String COLOR_INFO = "#00d505";

    public static final int DOCUMENT_FETCH_COUNT = 20;

    private DocumentListViewFragmentActionListener actionListener;

    private DocumentListViewAdapter documentListViewAdapter;
    private DisplayReadActionListViewAdapter displayReadyActionListViewAdapter;

    private SwipeRefreshLayout documentSwipeRefreshLayout;
    private ListView lvDocuments;

    private TextView tvCenterMessageNotification;
    private TextView tvFooterMessageNotification;
    private Button bRetryConnection;
    private Button bFilterByQRCode;

    private ProgressBar pbCenterMessageProgressBar;
    private ProgressBar pbRetryingConnection;

    private LinearLayout llFooterMessageContainer;
    private LinearLayout llCenterMessageContainer;

    private User activeUser;

    private ViewingMode viewingMode;

    public enum ViewingMode {
        EXISTING_DOCUMENTS, OUTGOING_ACTIONS
    }

    public static DocumentListViewFragment createInstance(User activeUser) {
        DocumentListViewFragment instance = new DocumentListViewFragment();
        instance.activeUser = activeUser;
        return instance;
    }

    //<editor-fold desc="Fragment Life Cycle">
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        actionListener = (DocumentListViewFragmentActionListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_document_list, container, false);

        documentListViewAdapter = new DocumentListViewAdapter(getActivity(), activeUser);
        documentListViewAdapter.setDocumentListItemActionListener((DocumentListItemActionListener) getActivity());

        displayReadyActionListViewAdapter = new DisplayReadActionListViewAdapter(getActivity());

        documentSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.DocumentList_srlSwipeLayout);
        documentSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (actionListener != null) {
                    actionListener.onRefreshCommand();
                } else {
                    FLLogger.w(TAG, "No action listener assigned to handle refresh command.");
                }
            }
        });

        lvDocuments = (ListView) rootView.findViewById(R.id.DocumentList_lvSelectableItems);
        lvDocuments.setAdapter(documentListViewAdapter);
        lvDocuments.setOnScrollListener(infiniteScrollListener);
        lvDocuments.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (viewingMode == ViewingMode.EXISTING_DOCUMENTS) {
                    actionListener.onOpenDocumentCommand((DocumentSummary) documentListViewAdapter.getItem(position));
                } else if (viewingMode == ViewingMode.OUTGOING_ACTIONS) {
                    actionListener.onOpenOutgoingDocumentsComment((DisplayReadyAction) displayReadyActionListViewAdapter.getItem(position));
                }

            }
        });

        // Center Notification Views
        llCenterMessageContainer = (LinearLayout) rootView.findViewById(R.id.DocumentList_llCenterMessageContainer);
        tvCenterMessageNotification = (TextView) rootView.findViewById(R.id.DocumentList_tvCenterMessageText);
        pbCenterMessageProgressBar = (ProgressBar) rootView.findViewById(R.id.DocumentList_pbCenterMessageProgressBar);

        // Bottom Notification Views
        llFooterMessageContainer = (LinearLayout) rootView.findViewById(R.id.DocumentList_llFooterMessageContainer);
        llFooterMessageContainer.setVisibility(View.GONE);

        tvFooterMessageNotification = (TextView) rootView.findViewById(R.id.DocumentList_tvFooterMessage);
        pbRetryingConnection = (ProgressBar) rootView.findViewById(R.id.DocumentList_pbRetryingConnection);

        bRetryConnection = (Button) rootView.findViewById(R.id.DocumentList_bRetry);
        bRetryConnection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionListener.onRetryConnectionCommand();
            }

        });

        if (FormalisticsApplication.versionSettings.showFilterByQRCodeButton) {
            bFilterByQRCode = (Button) rootView.findViewById(R.id.DocumentList_bFilterByQRCode);
            bFilterByQRCode.setVisibility(View.VISIBLE);
            bFilterByQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionListener.onFilterByQRCodeCommand();
                }
            });
        }

        actionListener.onViewReady();
        return rootView;
    }
    //</editor-fold>

    //<editor-fold desc="Functional View Methods">
    public void setViewDocuments(List<DocumentSummary> documentSummaryList) {
        viewingMode = ViewingMode.EXISTING_DOCUMENTS;
        lvDocuments.setAdapter(documentListViewAdapter);
        documentListViewAdapter.setDisplayItems(documentSummaryList);
    }

    public void addViewDocuments(List<DocumentSummary> documentSummaryList) {
        FLLogger.i(TAG, "addViewDocuments");
        viewingMode = ViewingMode.EXISTING_DOCUMENTS;
        documentListViewAdapter.addDisplayItems(documentSummaryList);
    }

    public void setOutgoingViewDocuments(List<DisplayReadyAction> displayReadyActions) {
        viewingMode = ViewingMode.OUTGOING_ACTIONS;
        lvDocuments.setAdapter(displayReadyActionListViewAdapter);
        displayReadyActionListViewAdapter.setDisplayItems(displayReadyActions);
    }

    public void hideCenterMessage() {
        llCenterMessageContainer.setVisibility(View.GONE);
        pbCenterMessageProgressBar.setVisibility(View.GONE);
    }

    public void showCenterMessage(String message, boolean showProgress) {

        llCenterMessageContainer.setVisibility(View.VISIBLE);
        tvCenterMessageNotification.setText(message);
        if (showProgress) {
            pbCenterMessageProgressBar.setVisibility(View.VISIBLE);
        }

    }

    public boolean isCenterMessageShowing() {
        return llCenterMessageContainer.getVisibility() == View.VISIBLE;
    }

    public void notifyOnlineMode() {

        tvFooterMessageNotification.setText(PROMPT_HAS_CONNECTION);
        bRetryConnection.setVisibility(View.INVISIBLE);
        pbRetryingConnection.setVisibility(View.GONE);

        llFooterMessageContainer.setVisibility(View.VISIBLE);
        llFooterMessageContainer.setBackgroundColor(Color.parseColor(COLOR_INFO));
        llFooterMessageContainer.postDelayed(new Runnable() {

            @Override
            public void run() {
                llFooterMessageContainer.setVisibility(View.GONE);
            }

        }, 5000);
    }

    public void notifyConnectingToServer(String server) {
        bRetryConnection.setVisibility(View.GONE);
        pbRetryingConnection.setVisibility(View.VISIBLE);
        tvFooterMessageNotification.setText(String.format(PROMPT_CONNECTING, server));
        llFooterMessageContainer.setBackgroundColor(Color.parseColor(COLOR_PROGRESS));
    }

    public void notifyOfflineMode() {

        tvFooterMessageNotification.setText(PROMPT_NO_CONNECTION);
        bRetryConnection.setVisibility(View.VISIBLE);
        llFooterMessageContainer.setVisibility(View.VISIBLE);
        llFooterMessageContainer.setBackgroundColor(Color.parseColor(COLOR_ERROR));
        pbRetryingConnection.setVisibility(View.GONE);

    }

    public void startSwipeRefresh() {
        documentSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                documentSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    public void stopSwipeRefresh() {
        documentSwipeRefreshLayout.setRefreshing(false);
    }
    //</editor-fold>

    //<editor-fold desc="Functional Methods">
    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public int getViewItemCount() {
        if (viewingMode == ViewingMode.EXISTING_DOCUMENTS) {
            return documentListViewAdapter.getCount();
        } else {
            return displayReadyActionListViewAdapter.getCount();
        }
    }
    //</editor-fold>

    public interface DocumentListViewFragmentActionListener {

        void onViewReady();

        void onRefreshCommand();

        void onRetryConnectionCommand();

        void onFilterByQRCodeCommand();

        void onLoadMore(int startIndex, int fetchCount);

        void onOpenDocumentCommand(DocumentSummary documentForDisplay);

        void onOpenOutgoingDocumentsComment(DisplayReadyAction displayReadyAction);

    }

    //<editor-fold desc="Anonymous Implementation Classes">
    private InfiniteScrollListener infiniteScrollListener = new InfiniteScrollListener(DOCUMENT_FETCH_COUNT) {
        @Override
        public void loadMore(int page, int totalItemsCount) {
            actionListener.onLoadMore(page, totalItemsCount);
        }
    };
    //</editor-fold>

}
