package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ActivityRequestCodes;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.form.content.PickListData;
import ph.com.gs3.formalistics.presenter.fragment.view.PicklistPickerViewFragment;
import ph.com.gs3.formalistics.presenter.fragment.view.PicklistPickerViewFragment.PicklistPickerActionListener;
import ph.com.gs3.formalistics.presenter.fragment.worker.PickListPickerWorkerFragment;

public class PicklistPickerActivity extends Activity implements PicklistPickerActionListener {

    public static final String TAG = PicklistPickerActivity.class.getSimpleName();

    public static final String EXTRA_ACTIVE_USER = "current_user";
    public static final String EXTRA_PICKLIST_SEARCH_AND_RESULT_DATA = "search_and_result_data";
    public static final String EXTRA_FIELD_ID = "field_id";
    public static final String EXTRA_PICKED_RESULT = "picked_result";
    public static final String EXTRA_PARSED_CONDITION_STRING = "parsed_condition_string";

    private PickListPickerWorkerFragment workerFragment;
    private PicklistPickerViewFragment viewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picklist_picker);

        initializeFragements(savedInstanceState);
        initializeData();
    }

    private void initializeData() {

        Bundle extras = getIntent().getExtras();

        User user = (User) extras.getSerializable(EXTRA_ACTIVE_USER);
        String fieldId = extras.getString(EXTRA_FIELD_ID);
        PickListData searchAndResultData = (PickListData) extras.getSerializable(EXTRA_PICKLIST_SEARCH_AND_RESULT_DATA);
        String parsedConditionString = extras.getString(EXTRA_PARSED_CONDITION_STRING);

        workerFragment.setCurrentUser(user);
        workerFragment.setFieldId(fieldId);
        workerFragment.setSearchAndResultData(searchAndResultData);
        workerFragment.setParsedConditionString(parsedConditionString);
    }

    private void initializeFragements(Bundle savedInstanceState) {
        workerFragment = (PickListPickerWorkerFragment) getFragmentManager().findFragmentByTag(
                PickListPickerWorkerFragment.TAG);

        if (workerFragment == null) {
            workerFragment = PickListPickerWorkerFragment.createInstance(this);
            getFragmentManager().beginTransaction().add(workerFragment,
                    PickListPickerWorkerFragment.TAG);
        }

        if (savedInstanceState == null) {
            viewFragment = new PicklistPickerViewFragment();
            getFragmentManager().beginTransaction().add(
                    R.id.container,
                    viewFragment,
                    PicklistPickerViewFragment.TAG).commit();
        } else {
            viewFragment = (PicklistPickerViewFragment) getFragmentManager().findFragmentByTag(PicklistPickerViewFragment.TAG);
        }
    }

    private void refreshViewData(String filter) {
        List<JSONObject> data = workerFragment.getPicklistData(filter);
        viewFragment.setData(workerFragment.getSearchAndResultData(), data);
    }

    private void finishWithResults(JSONObject selectedData) {

        String fieldId = workerFragment.getFieldId();
        PickListData searchAndResultData = workerFragment.getSearchAndResultData();


        String resultFieldName = searchAndResultData.getResultFieldName();

        if ("TrackNo".equalsIgnoreCase(resultFieldName)) {
            resultFieldName = "tracking_number";
        }

        try {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_FIELD_ID, fieldId);
            resultIntent.putExtra(EXTRA_PICKED_RESULT, selectedData.getString(resultFieldName));
            setResult(ActivityRequestCodes.PICK_LIST, resultIntent);
            finish();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // ====================================================================
    // {{ View Implementation Methods

    @Override
    public void onViewReady() {
        refreshViewData(null);
    }

    @Override
    public void onFilterRequested(String filter) {
        refreshViewData(filter);
    }

    @Override
    public void onItemSelected(JSONObject selectedItemData) {
        finishWithResults(selectedItemData);
    }

    // }}

}
