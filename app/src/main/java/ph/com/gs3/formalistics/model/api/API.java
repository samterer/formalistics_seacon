package ph.com.gs3.formalistics.model.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Map;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.HttpCommunicator.CommunicationException;
import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.application.APIResponse.InvalidResponseException;

/**
 * Created by Ervinne on 4/7/2015.
 */
public abstract class API {

//    private static final String TAG = API.class.getSimpleName();

    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SERVER_TIME_FORMAT = "HH:mm:ss";

    private final HttpCommunicator communicator;
    private String server;
    private String lastSuccessfulRequestServerDate;

    public API(HttpCommunicator communicator) {
        this.communicator = communicator;
    }

    public API(HttpCommunicator communicator, String server) {
        this.communicator = communicator;
        setServer(server);
    }

    protected void commonValidation() throws CommunicationException {

        if (server == null) {
            throw new CommunicationException("Server not set");
        }

    }

    protected APIResponse request(String url, Map<String, String> requestParams)
            throws CommunicationException, InvalidResponseException {

        lastSuccessfulRequestServerDate = null;
        String rawResponseString = null;
        try {
            rawResponseString = communicator.getResponseString(url, requestParams, "POST");
        } catch (MalformedURLException e) {
            if (server == null) {
                throw new CommunicationException("Server cannot be null");
            } else {
                throw new CommunicationException(e);
            }
        }

        FLLogger.d("API.request", "response: " + rawResponseString);

        JSONObject responseJSON;
        try {
            responseJSON = new JSONObject(rawResponseString);
        } catch (JSONException e) {

            InvalidResponseException convertedException = new InvalidResponseException(e);
            convertedException.setRawResponseString(rawResponseString);

            throw convertedException;
        }

        // throws APIResponseException
        APIResponse apiResponse = new APIResponse(responseJSON);
        lastSuccessfulRequestServerDate = apiResponse.getServerDate();

        return apiResponse;
    }

    public String getLastSuccessfulRequestServerDate() {
        return lastSuccessfulRequestServerDate;
    }

    // <editor-fold desc="Getters & Setters">

    protected void setServer(String server) {

        if (server != null) {
            server = server.trim();
        } else {
            server = "";
        }

        // Make sure that the server contains http
        if (!server.contains("http://") && !server.isEmpty()) {
            this.server = "http://" + server;
        } else {
            this.server = server;
        }
    }

    public String getServer() {
        return server;
    }

    public HttpCommunicator getCommunicator() {
        return this.communicator;
    }

    // </editor-fold>

}
