package ph.com.gs3.formalistics.view.document;

import android.app.Activity;
import android.view.View;

import org.json.JSONObject;

import java.util.List;

import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;
import ph.com.gs3.formalistics.model.values.business.form.FormViewContentData;

public interface DocumentViewContentsManager {

    View getCreatedDocumentViewContentsContainer();

    /**
     * FIXME Make an abstract parent of document and outgoing action so that strategy
     * design pattern may be applied to them. (Requires refactoring of the document view
     * fragment and activity as well)
     */
    void createDocumentViewsFromData(
            List<FormViewContentData> formViewContentDataList,
            JSONObject fieldValues,
            DocumentHeaderData documentHeaderData,
            User currentUser);

    void setSpecialFieldsListener(Activity activity);

    View findFieldView(String fieldId);

    void setFieldValue(String fieldId, String value);

    JSONObject getFieldValues();

    List<String> validateFields();

    void notifyFieldsRequired(List<String> fieldsThatFailedValidation);

    // =====================================================
    // {{ Events

    void onLoad();

    // }}

}
