package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.gs3.formalistics.model.values.business.Company;

public class CompanyJSONParser {

	public static Company createFromLoginJSON(JSONObject json, String server) throws JSONException {

		Company company = new Company();

		company.setWebId(json.getInt("id"));
		company.setName(json.getString("name"));
		company.setServer(server);

		return company;

	}

}
