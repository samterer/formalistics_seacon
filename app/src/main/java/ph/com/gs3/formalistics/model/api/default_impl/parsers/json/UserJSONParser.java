package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.gs3.formalistics.model.values.business.User;

public class UserJSONParser {

	public static final User createFromLoginJSON(JSONObject raw) throws JSONException {

		User user = new User();

		user.setWebId(raw.getInt("id"));
		user.setEmail(raw.getString("email"));
        user.setDisplayName(raw.getString("display_name"));
		user.setImageURL(raw.getString("image_url"));
		user.setUserLevelId(raw.getString("user_level_id"));
        user.setPositionId(raw.getInt("position"));
        // TODO: add info about user position name

		return user;

	}

	public static final User createAuthorFromDocumentJSON(JSONObject raw) throws JSONException {

		User user = new User();

		String webId = raw.getString("requestor_id");

		if (webId == null || webId.isEmpty() || "null".equals(webId)) {
			return null;
		}

		user.setWebId(raw.getInt("requestor_id"));
		user.setEmail(raw.getString("requestor_email"));
		user.setDisplayName(raw.getString("requestor_display_name"));
		user.setUserLevelId(raw.getString("requestor_user_level_id"));

		if (raw.has("requestor_image_url")) {
			user.setImageURL(raw.getString("requestor_image_url"));
		}

		return user;

	}

	public static final User createProcessorFromDocumentJSON(JSONObject raw) throws JSONException {

		User user = new User();

		String webId = raw.getString("processor_id");

		if (webId == null || webId.isEmpty() || "null".equals(webId)) {
			return null;
		}

		user.setWebId(raw.getInt("processor_id"));
		user.setEmail(raw.getString("processor_email"));
		user.setDisplayName(raw.getString("processor_display_name"));
		user.setUserLevelId(raw.getString("processor_user_level_id"));

		if (raw.has("processor_image_url")) {
			user.setImageURL(raw.getString("processor_image_url"));
		}

		return user;

	}

}
