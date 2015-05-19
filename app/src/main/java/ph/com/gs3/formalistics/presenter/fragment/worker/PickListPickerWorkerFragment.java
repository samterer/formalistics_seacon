package ph.com.gs3.formalistics.presenter.fragment.worker;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

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

    private Context context;

    private FormsDAO formsDAO;
    private DocumentsDAO documentsDAO;
    private DynamicFormFieldsDAO dynamicFormFieldsDAO;

    private User currentUser;
    private String fieldId;
    private PickListData searchAndResultData;
    private String parsedConditionString;

    public static PickListPickerWorkerFragment createInstance(Context context) {

        PickListPickerWorkerFragment instance = new PickListPickerWorkerFragment();

        instance.context = context;

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

        if (form == null) {
            Toast.makeText(context, "Search failed, missing form with id = " + formWebId + " please contact your administrator", Toast.LENGTH_LONG).show();
            return data;

        }

        try {
            documentsDAO.open();
            data = dynamicFormFieldsDAO.search(form, dataFieldIds, currentUser.getId(), conditions, parsedConditionString);
        } catch (JSONException | SQLiteException e) {
            e.printStackTrace();

            String expectedMessage = "no such column: ";
            String message = e.getMessage();
            if (e.getMessage().startsWith(expectedMessage)) {

                String missingField = e.getMessage().substring(expectedMessage.length());
                missingField = missingField.split(" ")[0];
                message = "Missing field: " + missingField + " from " + form.getName() + " , please contact your administrator";
            }

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } finally {
            documentsDAO.close();
        }

        return data;

    }

    private List<SearchCondition> createConditionsFromFilter(String filter, List<String> fieldIds) {

        List<SearchCondition> conditions = new ArrayList<>();

        for (String fieldId : fieldIds) {
//            conditions.add(new SearchCondition(fieldId, " LIKE ", "%" + filter + "%"));
            conditions.add(new SearchCondition(fieldId, " LIKE ", filter + "%"));
        }

        return conditions;

    }

    //<editor-fold desc="Getters & Setters">
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

    public String getParsedConditionString() {
        return parsedConditionString;
    }

    public void setParsedConditionString(String parsedConditionString) {
        this.parsedConditionString = parsedConditionString;
    }

//</editor-fold>

}
