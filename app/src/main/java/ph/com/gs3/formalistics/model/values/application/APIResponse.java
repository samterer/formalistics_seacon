package ph.com.gs3.formalistics.model.values.application;

import org.json.JSONException;
import org.json.JSONObject;

public class APIResponse {

    public static final String TAG = APIResponse.class.getSimpleName();

    public enum APIStatus {
        SUCCESS, ERROR
    }

    private APIStatus status;

    private String results;
    private String error;
    private String errorMessage;
    private String serverDate;

    public APIResponse(JSONObject rawResponse) throws InvalidResponseException {

        String currentKey = "status";    // first key

        try {
            String rawStatus = rawResponse.getString(currentKey);

            if ("SUCCESS".equals(rawStatus)) {
                status = APIStatus.SUCCESS;
            } else if ("ERROR".equals(rawStatus)) {
                status = APIStatus.ERROR;
            } else {
                throw new InvalidResponseException("Invalid status key, it must be SUCCESS or ERROR only.");
            }

            currentKey = "error";
            error = rawResponse.getString(currentKey) == "null" ? null : rawResponse
                    .getString(currentKey);

            currentKey = "error_message";
            errorMessage = rawResponse.getString(currentKey) == "null" ? null : rawResponse
                    .getString(currentKey);

            currentKey = "results";
            results = rawResponse.getString(currentKey) == "null" ? null : rawResponse.getString(currentKey);

            currentKey = "server_date";
            if (rawResponse.has(currentKey)) {
                serverDate = rawResponse.getString(currentKey);
            }

        } catch (JSONException e) {

            if (rawResponse.has(currentKey)) {
                try {
                    throw new InvalidResponseException(
                            "Response has incomplete keys, it must have a valid " + currentKey
                                    + " key. Its value is found to be: "
                                    + rawResponse.getString(currentKey), e);
                } catch (JSONException e1) {
                    throw new InvalidResponseException(
                            "Response has incomplete keys, it must have an valid " + currentKey
                                    + " key.", e1);
                }
            } else {
                throw new InvalidResponseException(
                        "Response has incomplete keys, it must have an existing " + currentKey
                                + " key.", e);
            }

        }

    }

    // ============================================================================
    // Exceptions

    public static final class ServerErrorException extends Exception {

        public ServerErrorException(String message) {
            super(message);
        }

    }

    public static final class InvalidResponseException extends Exception {

        private static final String DEFAULT_MESSAGE = "The server gave an invalid response.";

        private String rawResponseString;

        public InvalidResponseException() {
            super(DEFAULT_MESSAGE);
        }

        public InvalidResponseException(Exception e) {
            super(DEFAULT_MESSAGE, e);
        }

        public InvalidResponseException(String message) {
            super(message);
        }

        public InvalidResponseException(String message, Throwable throwable) {
            super(message, throwable);
        }

        public void setRawResponseString(String rawResponseString) {
            this.rawResponseString = rawResponseString;
        }

    }

    // ============================================================================
    // Getters & Setters

    public APIStatus getStatus() {
        return status;
    }

    /**
     * Same as using apiResponseObject.getStatus == APIStatus.SUCCESS
     *
     * @return
     */
    public boolean isOperationSuccessful() {
        return status == APIStatus.SUCCESS;
    }

    public String getResults() {
        return results;
    }

    public String getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getServerDate() {
        return serverDate;
    }

}
