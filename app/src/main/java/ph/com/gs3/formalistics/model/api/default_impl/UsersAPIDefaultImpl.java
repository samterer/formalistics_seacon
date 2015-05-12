package ph.com.gs3.formalistics.model.api.default_impl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.API;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.UsersAPI;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.CompanyJSONParser;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.UserJSONParser;
import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.business.User;

/**
 * Created by Ervinne on 4/7/2015.
 */
public class UsersAPIDefaultImpl extends API implements UsersAPI {

    public static final String TAG = UsersAPIDefaultImpl.class.getSimpleName();

    public static final int LOGIN_CONNECTION_TIMEOUT = 10000;    // 10s
    public static final int LOGIN_READ_TIMEOUT = 6000;    // 6s

    public UsersAPIDefaultImpl(HttpCommunicator httpCommunicator, String server) {
        super(httpCommunicator, server);
    }

    @Override
    public User login(String email, String password) throws LoginException {
        String url = getServer() + "/API/login";
        Map<String, String> requestParams = new HashMap<>();

        requestParams.put("email", email);
        requestParams.put("password", password);

        // login should have shorter connection timeout than usual
        getCommunicator().setConnectionTimeout(LOGIN_CONNECTION_TIMEOUT);
        getCommunicator().setReadTimeout(LOGIN_READ_TIMEOUT);

        APIResponse response;
        User user;

        try {
            response = request(url, requestParams);
        } catch (HttpCommunicator.CommunicationException | APIResponse.InvalidResponseException e) {
            throw new LoginException(e.getMessage(), e);
        }

        if (response.isOperationSuccessful()) {
            String rawResults = response.getResults();

            JSONObject rawUserJSON;
            JSONObject rawCompanyJSON;

            try {
                rawUserJSON = new JSONObject(rawResults);
                rawCompanyJSON = rawUserJSON.getJSONObject("company");

                user = UserJSONParser.createFromLoginJSON(rawUserJSON);
                user.setPassword(password);    // TODO: add encryption here
                user.setCompany(CompanyJSONParser.createFromLoginJSON(rawCompanyJSON, getServer()));

            } catch (JSONException e) {
                FLLogger.e(TAG, "Failed to parse: " + rawResults + ". Cause: " + e.getMessage());
                throw new LoginException("Error parsing response from server", e);
            }

        } else {
            throw new LoginException(response);
        }

        return user;

    }

}
