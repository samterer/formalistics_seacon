package ph.com.gs3.formalistics.presenter.fragment.worker;

import android.app.Fragment;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.dao.DynamicFormFieldsDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.values.application.SearchCondition;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.content.PickListData;

public class PickListPickerWorkerFragment extends Fragment {

    public static final String TAG = PickListPickerWorkerFragment.class.getSimpleName();

    private FormsDAO formsDAO;
    private DocumentsDAO documentsDAO;
    private DynamicFormFieldsDAO dynamicFormFieldsDAO;

    private User currentUser;
    private String fieldId;
    private PickListData searchAndResultData;

    public static PickListPickerWorkerFragment createInstance(Context context) {

        PickListPickerWorkerFragment instance = new PickListPickerWorkerFragment();
        instance.formsDAO = new FormsDAO(context);
        instance.documentsDAO = new DocumentsDAO(context);
        instance.dynamicFormFieldsDAO = new DynamicFormFieldsDAO(context);
        return instance;

    }

    public List<JSONObject> getPicklistData(String filter) {

        int formWebId = searchAndResultData.getFormWebId();

        List<String> displayFieldIds = searchAndResultData.getViewColumnIdList();
        List<String> dataFieldIds = new ArrayList<>();

        // FIXME: create a converter utilities for all reserved fields that need conversion

        if ("TrackNo".equalsIgnoreCase(searchAndResultData.getResultFieldName())) {
            dataFieldIds.add("tracking_number");
        } else {
            dataFieldIds.add(searchAndResultData.getResultFieldName());
        }

        for (String dataFieldId : searchAndResultData.getViewColumnIdList()) {
            // Convert fields
            if ("TrackNo".equalsIgnoreCase(dataFieldId)) {
                dataFieldIds.add("tracking_number");
            } else {
                dataFieldIds.add(dataFieldId);
            }
        }

        Form form = null;

        try {
            form = formsDAO.getForm(formWebId, currentUser.getCompany().getId());
        } catch (DataAccessObject.DataAccessObjectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Create conditions from the filter
        List<SearchCondition> conditions = null;

        if (filter != null) {
            conditions = createConditionsFromFilter(filter, displayFieldIds);
        }

        List<JSONObject> data = new ArrayList<>();

        try {
            documentsDAO.open();
            data = dynamicFormFieldsDAO.search(form, dataFieldIds, currentUser.getId(), conditions);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            documentsDAO.close();
        }

        return data;

    }

    private List<SearchCondition> createConditionsFromFilter(String filter, List<String> fieldIds) {

        List<SearchCondition> conditions = new ArrayList<>();

        for (String fieldId : fieldIds) {
            conditions.add(new SearchCondition(fieldId, " LIKE ", filter + "%"));
        }

        return conditions;

    }

    // ========================================================================
    // {{ Getters & Setters

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public PickListData getSearchAndResultData() {
        return searchAndResultData;
    }

    public void setSearchAndResultData(PickListData searchAndResultData) {
        this.searchAndResultData = searchAndResultData;
    }

    // }}

}
