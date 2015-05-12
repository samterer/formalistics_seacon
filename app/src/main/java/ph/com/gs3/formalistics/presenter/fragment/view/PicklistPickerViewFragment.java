package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.business.form.content.PickListData;
import ph.com.gs3.formalistics.view.adapters.KeyValueGroupListViewAdapter;

public class PicklistPickerViewFragment extends Fragment {

    public static final String TAG = PicklistPickerViewFragment.class.getSimpleName();

    private EditText etFilter;
    private ListView lvPickListItems;

    private KeyValueGroupListViewAdapter picklistViewAdapter;

    private PicklistPickerActionListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (PicklistPickerActionListener) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(activity.getClass().getSimpleName()
                    + " must implement PicklistPickerActionListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_picklist_picker, container,
                false);

        picklistViewAdapter = new KeyValueGroupListViewAdapter(getActivity());

        etFilter = (EditText) rootView.findViewById(R.id.Picklist_etFilter);
        etFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                listener.onFilterRequested(s.toString());
            }
        });

        lvPickListItems = (ListView) rootView.findViewById(R.id.Picklist_lvPicklistItems);
        lvPickListItems.setAdapter(picklistViewAdapter);
        lvPickListItems.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemSelected((JSONObject) picklistViewAdapter.getItem(position));
            }

        });

        listener.onViewReady();

        return rootView;
    }

    public void setData(PickListData searchAndResultData, List<JSONObject> listData) {

        picklistViewAdapter.setData(searchAndResultData.getViewColumns(), listData);
        picklistViewAdapter.notifyDataSetChanged();

    }

    public interface PicklistPickerActionListener {

        void onViewReady();

        void onItemSelected(JSONObject selectedItemData);

        void onFilterRequested(String filter);

    }

}
