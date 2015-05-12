package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.com.gs3.formalistics.model.values.business.form.content.EmbeddedViewData;
import ph.com.gs3.formalistics.model.values.business.view.ViewColumn;

public class EmbeddedViewDataJSONParser {

    public static final String TAG = EmbeddedViewDataJSONParser.class.getSimpleName();

    private static Map<String, String> fieldNameConversion = new HashMap<>();

    public static void lazyloadFieldNameConversion() {

        if (fieldNameConversion.isEmpty()) {
            fieldNameConversion.put("TrackNo", "tracking_number");
            fieldNameConversion.put("Id", "_id");
        }

    }

    public static EmbeddedViewData parseJSON(JSONObject raw) throws JSONException {

        lazyloadFieldNameConversion();

        EmbeddedViewData data = new EmbeddedViewData();

        String primaryDataString = raw.getString("primary_data");
        JSONObject primaryDataJSON = new JSONObject(primaryDataString);

        data.setName(raw.getString("id"));
        data.setSearchFormWebId(primaryDataJSON.getInt("source_form_id"));
        data.setSearchFieldId(primaryDataJSON.getString("primary_filter_search_source_field"));
        data.setSearchConditionalOperator(primaryDataJSON
                .getString("primary_filter_search_conditional_operator"));
        data.setSearchCompareToThisDocumentFieldId(primaryDataJSON
                .getString("primary_filter_search_current_field"));

        String columnListString = primaryDataJSON.getString("json_view_selected_columns");
        JSONArray columnListJSONArray = new JSONArray(columnListString);

        int columnCount = columnListJSONArray.length();

        List<ViewColumn> viewColumns = new ArrayList<>();

        for (int i = 0; i < columnCount; i++) {
            JSONObject columnJSON = columnListJSONArray.getJSONObject(i);

            String name = getConvertedFieldName(columnJSON.getString("FieldName"));
            String label = columnJSON.getString("FieldLabel");

            viewColumns.add(new ViewColumn(name, label));
        }

        data.setViewColumns(viewColumns);

        JSONObject eventsJSON = new JSONObject();
        eventsJSON = raw.getJSONObject("events");

        boolean enableEvents = eventsJSON.getBoolean("enable_event_action_privilages");

        if (enableEvents) {
            data.setEnableCreateDocumentAction(eventsJSON
                    .getBoolean("embed_action_click_create_request"));
            data.setCreateDocumentActionLabel(eventsJSON
                    .getString("embed_action_click_create_request_caption"));

        }

        if (raw.has("enable_embedPopupDataSending")
                && raw.getBoolean("enable_embedPopupDataSending")) {
            JSONArray dataSendingJSON = new JSONArray();
            dataSendingJSON = raw.getJSONArray("json_data_sending");

            boolean enableDataSending = raw.getBoolean("enable_embedPopupDataSending");

            data.setEnableDataSendingToNewDocuments(enableDataSending);
            if (enableDataSending) {

                int dataSendingCount = dataSendingJSON.length();

                for (int i = 0; i < dataSendingCount; i++) {
                    JSONObject dataSendingItem = dataSendingJSON.getJSONObject(i);
                    data.addDataSendingDataItem(
                            dataSendingItem.getString("epds_select_my_form_field_side"),
                            dataSendingItem.getString("epds_select_popup_form_field_side"));
                }
            }
        }

        return data;

    }

    private static String getConvertedFieldName(String originalFieldName) {

        if (fieldNameConversion.containsKey(originalFieldName)) {
            return fieldNameConversion.get(originalFieldName);
        }

        return originalFieldName;
    }

}
