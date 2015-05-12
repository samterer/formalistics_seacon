package ph.com.gs3.formalistics.view.document;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.view.document.contents.FViewCollection;
import ph.com.gs3.formalistics.view.document.contents.fields.FTextField;

public class DefaultDocumentFieldsContainer extends ScrollView {

    private LinearLayout llFieldsContainer;

    public DefaultDocumentFieldsContainer(Context context, DocumentDynamicViewContentsManager documentViewContentsManager) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.fragment_default_document_fields_container, this, true);

        llFieldsContainer = (LinearLayout) findViewById(R.id.Document_llFieldsContainer);

        FViewCollection documentViewContents = documentViewContentsManager.getDocumentViewContents();

        for (View documentViewContent : documentViewContents) {
            llFieldsContainer.addView(documentViewContent);

            // Trigger the onChange event of each view that does not automatically trigger
            // it once added to the view.
            if (documentViewContent instanceof FTextField) {
                ((FTextField) documentViewContent).notifyValueChanged();
            }

        }

    }
}
