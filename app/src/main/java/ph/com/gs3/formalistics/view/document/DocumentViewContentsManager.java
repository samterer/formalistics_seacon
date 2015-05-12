package ph.com.gs3.formalistics.view.document;

import android.app.Activity;
import android.view.View;

import org.json.JSONObject;

import java.util.List;

import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;
import ph.com.gs3.formalistics.model.values.business.form.FormViewContentData;

public interface DocumentViewContentsManager {

    public View getCreatedDocumentViewContentsContainer();

    /**
     * FIXME Make an abstract parent of document and outgoing action so that strategy
     * design pattern may be applied to them. (Requires refactoring of the document view
     * fragment and activity as well)
     */
    public void createDocumentViewsFromData(
            List<FormViewContentData> formViewContentDataList,
            JSONObject fieldValues,
            DocumentHeaderData documentHeaderData,
            User currentUser);

    public void setSpecialFieldsListener(Activity activity);

    public View findFieldView(String fieldId);

    public void setFieldValue(String fieldId, String value);

    public JSONObject getFieldValues();

    public List<String> validateFields();

    public void notifyFieldsRequired(List<String> fieldsThatFailedValidation);

    // =====================================================
    // {{ Events

    public void onLoad();

    // }}

}
