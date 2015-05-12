package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONParser {

	public static List<String> createStringListFromJSONArray(JSONArray raw) throws JSONException {

		List<String> list = new ArrayList<>();
		int itemCount = raw.length();

		for (int i = 0; i < itemCount; i++) {
			list.add(raw.getString(i));
		}

		return list;

	}

	public static JSONArray createJSONArrayFromStringList(List<String> list) {

		JSONArray json = new JSONArray();
		for (String item : list) {
			json.put(item);
		}

		return json;

	}

}
