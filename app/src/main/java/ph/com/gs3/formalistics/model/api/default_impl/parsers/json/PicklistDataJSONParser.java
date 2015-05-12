package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.values.business.form.content.PickListData;
import ph.com.gs3.formalistics.model.values.business.view.ViewColumn;

public class PicklistDataJSONParser {

	public static PickListData parseJSON(JSONObject json) throws JSONException {

		PickListData data = new PickListData();

		JSONObject picklistData = json.getJSONObject("picklist_data");
        JSONArray columns = new JSONArray(picklistData.getString("display_columns"));

		List<ViewColumn> viewColumns = new ArrayList<>();

		int columnCount = columns.length();
		for (int i = 0; i < columnCount; i++) {
			JSONObject columnJSON = new JSONObject(columns.getString(i));

			String label = columnJSON.getString("FieldLabel");
			String name = columnJSON.getString("FieldName");

			viewColumns.add(new ViewColumn(name, label));

		}

		data.setViewColumns(viewColumns);
		data.setFormWebId(picklistData.getInt("form_id"));
		data.setFormName(picklistData.getString("formname"));
		data.setCondition(picklistData.getString("condition"));
		data.setResultFieldName(picklistData.getString("return_field_name"));

		return data;

	}
}