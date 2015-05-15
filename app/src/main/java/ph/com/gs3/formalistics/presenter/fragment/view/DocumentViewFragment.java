package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.DocumentType;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;
import ph.com.gs3.formalistics.model.values.business.document.OutgoingAction;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.view.document.DocumentViewContentsManager;

/**
 * Created by Ervinne on 4/12/2015.
 */
public class DocumentViewFragment extends Fragment {

    public static final String TAG = DocumentViewFragment.class.getSimpleName();

    //<editor-fold desc="View Fields">
    private LinearLayout llActionsContainer;
    private LinearLayout llDynamicViewsContainer;
    private LinearLayout llStarBusyPrompt;
    private LinearLayout llMessageContainer;
    private LinearLayout llOutgoingActionLinkContainer;

    private TextView tvTrackingNumber;
    private TextView tvStatus;

    private TextView tvMessage;
    private ProgressBar pbMessageProgress;
    //</editor-fold>
    private DocumentViewActionListener listener;
    private DocumentViewContentsManager documentViewContentsManager;

    public static DocumentViewFragment createInstance(DocumentViewContentsManager documentViewContentsManager) {
        DocumentViewFragment instance = new DocumentViewFragment();
        instance.documentViewContentsManager = documentViewContentsManager;
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (DocumentViewActionListener) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(activity.getClass().getSimpleName()
                    + " does not implement DocumentViewActionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_document, container, false);

        initializeDefaultViews(rootView);

        listener.onViewReady();

        return rootView;
    }

    private void initializeDefaultViews(View rootView) {

        llDynamicViewsContainer = (LinearLayout) rootView.findViewById(R.id.Document_llFieldsContainer);
        llMessageContainer = (LinearLayout) rootView.findViewById(R.id.Document_llMessageContainer);
        llOutgoingActionLinkContainer = (LinearLayout) rootView.findViewById(R.id.Document_llOutgoingActionLinkContainer);

        tvMessage = (TextView) rootView.findViewById(R.id.Document_tvMessage);
        pbMessageProgress = (ProgressBar) rootView.findViewById(R.id.Document_pbMessageProgress);

        tvTrackingNumber = (TextView) rootView.findViewById(R.id.Document_tvTrackingNumber);
        tvStatus = (TextView) rootView.findViewById(R.id.Document_tvStatus);

    }

    public void initializeDynamicViews(DocumentViewFragmentParameterBundle bundle) {

        DocumentHeaderData documentHeaderData = null;

        JSONObject fieldValues = new JSONObject();
        if (bundle.documentType == DocumentType.EXISTING_DOCUMENT) {
            if (bundle.document != null && bundle.document.getFieldValuesJSONString() != null) {
                try {
                    fieldValues = new JSONObject(bundle.document.getFieldValuesJSONString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFailedOpeningDocumentData("The document data is not a valid JSON object");
                }
            }
        } else {
            if (bundle.outgoingAction != null && bundle.outgoingAction.getDocumentFieldUpdates() != null) {
                fieldValues = bundle.outgoingAction.getDocumentFieldUpdates();
            }
        }

        switch (bundle.documentType) {
            case NEW_DOCUMENT:
            case CHILD_DOCUMENT:
                // New document, get the field properties from the form
                documentHeaderData = DocumentHeaderData.createFromForm(bundle.form);
                break;
            case EXISTING_DOCUMENT:
                // Existing document on the workflow, get the field properties from the
                // document
                documentHeaderData = DocumentHeaderData.createFromDocument(bundle.document, bundle.form);

                break;
            case OUTGOING_DOCUMENT:
                documentHeaderData = DocumentHeaderData.createFromOutgoingAction(bundle.outgoingAction, bundle.form);
                break;
        }

        tvTrackingNumber.setText(documentHeaderData.getTrackingNumber());
        tvStatus.setText(documentHeaderData.getWorkflowObject().getStatus());

        documentViewContentsManager.setSpecialFieldsListener(getActivity());
        documentViewContentsManager.createDocumentViewsFromData(
                bundle.form.getActiveContents(), fieldValues, documentHeaderData, bundle.activeUser
        );

        llDynamicViewsContainer.addView(documentViewContentsManager.getCreatedDocumentViewContentsContainer());

    }

    public void setDocumentViewContentsManager(DocumentViewContentsManager documentViewContentsManager) {
        this.documentViewContentsManager = documentViewContentsManager;
    }

    public interface DocumentViewActionListener {
        void onFailedOpeningDocumentData(String message);

        void onViewReady();
    }

    public static class DocumentViewFragmentParameterBundle {

        public User activeUser;
        public Form form;
        public Document document;
        public OutgoingAction outgoingAction;
        public DocumentType documentType;

    }

}
