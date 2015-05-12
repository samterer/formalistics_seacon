package ph.com.gs3.formalistics.view.document.contents.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.form.content.EmbeddedViewData;
import ph.com.gs3.formalistics.model.values.business.view.ViewColumn;
import ph.com.gs3.formalistics.view.document.contents.FView;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class FEmbeddedView extends FView {

    public static final String TAG = FEmbeddedView.class.getSimpleName();

    private final Button bCreateChildDocument;
    private final TableLayout tlEmbeddedViewDocuments;

    private EmbeddedViewEventsListener listener;
    private final EmbeddedViewData embeddedViewData;

    public FEmbeddedView(Context context, EmbeddedViewData embeddedViewData) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_embedded_view, this);

        this.embeddedViewData = embeddedViewData;
        tlEmbeddedViewDocuments = (TableLayout) findViewById(R.id.EmbeddedView_tlTable);

        int visibility = getEmbeddedViewData().isEnableCreateDocumentAction()
                ? View.VISIBLE : View.GONE;

        bCreateChildDocument = (Button) findViewById(R.id.EmbeddedView_bCreateDocument);
        bCreateChildDocument.setVisibility(visibility);
        bCreateChildDocument.setText(embeddedViewData.getCreateDocumentActionLabel());
        bCreateChildDocument.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (getEventsListener() != null) {
                    getEventsListener().onCreateChildDocument(
                            getEmbeddedViewData().getSearchFormWebId(),
                            getEmbeddedViewData().getDataSendingItems(),
                            FEmbeddedView.this);
                }
            }
        });

        clearView();

        setCreateChildDocumentActionLabel(embeddedViewData.getCreateDocumentActionLabel());

    }

    public void clearView() {
        tlEmbeddedViewDocuments.removeAllViews();
        tlEmbeddedViewDocuments.addView(createTableColumnHeaders());
    }

    public void setData(List<JSONObject> listData) {

        clearView();

        FLLogger.d(TAG, "list data size: " + listData.size());

        int dataSize = listData.size();

        for (int i = 0; i < dataSize; i++) {
            tlEmbeddedViewDocuments.addView(createRow(listData.get(i), i % 2 == 0));
        }
    }

    private TableRow createNewRow() {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        return row;
    }

    private TableRow createTableColumnHeaders() {

        TableRow row = createNewRow();

        List<ViewColumn> columns = getEmbeddedViewData().getViewColumns();
        for (ViewColumn viewColumn : columns) {
            TextView tvHeaderColumn = (TextView) inflate(getContext(), R.layout.view_table_textfield_header, null);
            tvHeaderColumn.setText(viewColumn.getLabel());

            row.addView(tvHeaderColumn);
        }

        return row;

    }

    private TableRow createRow(JSONObject rowData, boolean even) {

        TableRow row = createNewRow();
        row.setTag(rowData);

        List<ViewColumn> columns = getEmbeddedViewData().getViewColumns();
        for (ViewColumn column : columns) {

            String key = column.getName();

            TextView textView;

            if (even) {
                textView = (TextView) inflate(getContext(), R.layout.view_table_textfield_data_dark, null);
            } else {
                textView = (TextView) inflate(getContext(), R.layout.view_table_textfield_data_light, null);
            }

            try {
                textView.setText(rowData.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            row.addView(textView);
            row.setOnClickListener(onRowClickListener);

        }

        return row;

    }

    public EmbeddedViewData getEmbeddedViewData() {
        return embeddedViewData;
    }

    public EmbeddedViewEventsListener getEventsListener() {
        return listener;
    }

    public void setCreateChildDocumentActionLabel(String label) {
        bCreateChildDocument.setText(label);
    }

    public void setEventsListener(EmbeddedViewEventsListener listener) {
        this.listener = listener;
    }

    private final OnClickListener onRowClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            int formId = getEmbeddedViewData().getSearchFormWebId();
            int documentId = 0;
            int outgoingActionId = 0;

            if (v.getTag() != null) {

                JSONObject rowData = (JSONObject) v.getTag();

                FLLogger.d(TAG, rowData.toString());

                try {
                    documentId = rowData.getInt("Document_Id");
                    outgoingActionId = rowData.getInt("Outgoing_Action_Id");
                } catch (NumberFormatException | JSONException e) {
                    FLLogger.w(TAG, "Failed to convert document/outgoing action id: " + e.getMessage()
                            + ", a document failed to be opened because of this");
                }
            }

            if (documentId != 0) {
                getEventsListener().onOpenDocumentCommand(formId, documentId, FEmbeddedView.this);
            } else if (outgoingActionId != 0) {
                getEventsListener().onOpenOutgoingActionCommand(formId, outgoingActionId, FEmbeddedView.this);
            }
        }
    };

    public interface EmbeddedViewEventsListener {

        void onCreateChildDocument(int formWebId, List<EmbeddedViewData.EmbeddedViewDataSendingItem> dataSendingItems, FEmbeddedView source);

        void onSearchForEmbeddedViewRequested(String searchCompareToFieldValue, FEmbeddedView source);

        void onOpenDocumentCommand(int formWebId, int documentId, FEmbeddedView source);

        void onOpenOutgoingActionCommand(int formWebId, int outgoingActionId, FEmbeddedView source);

    }

}
