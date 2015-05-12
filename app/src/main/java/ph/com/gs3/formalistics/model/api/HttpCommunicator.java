package ph.com.gs3.formalistics.model.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.global.constants.ApplicationMode;

public class HttpCommunicator {

    public static final String TAG = HttpCommunicator.class.getSimpleName();

    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';

    public static final int STATUS_INVALID_URL = -2;
    public static final int STATUS_DISCONNECTED = -1;
    public static final int STATUS_ERROR_ON_CONNECT = 0;
    public static final int STATUS_CONNECTED = 1;

    /**
     * This is how long (in milliseconds) will the application wait until the it is able
     * to connect to the server being checked.
     */
    // @formatter:off
	public static final int CHECK_CONNECTION_TIMEOUT = 5000;
	public static final int CHECK_CONNECTION_READ_TIMEOUT = 5000;
	// @formatter:on

    /**
     * This is how long (in milliseconds) will the application wait until it is able to
     * complete a request to the server.
     */
    // public static final int DEFAULT_CONNECTION_TIMEOUT = 20000; // 20s
    // @formatter:off
	public static final int DEFAULT_CONNECTION_TIMEOUT 
		= FormalisticsApplication.APPLICATION_MODE == ApplicationMode.DEVELOPMENT
			? 5000 : 15000;	// 5s or 15s
	public static final int DEFAULT_READ_TIMEOUT 
		= FormalisticsApplication.APPLICATION_MODE == ApplicationMode.DEVELOPMENT
			? 15000 : 30000; // 10s or 25s
	// @formatter:on

    // Default Configuration:
    // The application may read http request without limit but connection is limited by
    // the default connection timeout
    private int readTimeout = DEFAULT_READ_TIMEOUT;
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    /**
     * Maintains session in cookie in the whole application
     */
    private static final CookieManager cookieManager = new CookieManager();

    /**
     * Checks connection to the specified URL.
     *
     * @param urlString the url to test connection to.
     * @return an integer constant that can either be Communicator.STATUS_INVALID_URL,
     * Communicator.STATUS_DISCONNECTED, Communicator.STATUS_ERROR_ON_CONNECT, or
     * Communicator.STATUS_CONNECTED.
     */
    public int testConnection(String urlString) {

        if (urlString == null || urlString.isEmpty()) {
            throw new IllegalStateException("Tried to test connection with an empty server.");
        }

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return STATUS_INVALID_URL;
        }

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(CHECK_CONNECTION_READ_TIMEOUT);
            conn.setConnectTimeout(CHECK_CONNECTION_TIMEOUT);
            conn.connect();

            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                return STATUS_CONNECTED;
            } else {
                return STATUS_ERROR_ON_CONNECT;
            }

        } catch (IOException e) {
            return STATUS_DISCONNECTED;
        }
    }

    /**
     * Gets the response from the url specified, the user may specify parameters and the
     * request method. Additionally, the user may set the readTimeout and/or
     * connectionTimout before calling this method.
     * <p/>
     * <pre>
     *    {@code
     * 		communicatorInstance.readTimeout = 1000	//	milliseconds
     * 		communicatorInstance.connectionTimeout = 3000	//	milliseconds
     * 		String response = communicatorInstance.getResponseString("http://my.api.com", null, "GET");
     * 	}
     * </pre>
     *
     * @param urlString     the URL of the request to be made.
     * @param parameters    a Map<String, String> object representation of the parameters
     * @param requestMethod the request method to use, (GET, POST, PUT, DELETE)
     * @return a string containing the response from the request
     * @throws java.io.IOException
     */
    public String getResponseString(
            String urlString, Map<String, String> parameters, String requestMethod)
            throws CommunicationException, MalformedURLException {

        CookieHandler.setDefault(HttpCommunicator.cookieManager);

        //  throws MalformedURLException
        URL url = new URL(urlString);

        HttpURLConnection conn = null;

        try {

            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new CommunicationException("Failed to open connection to URL "
                        + urlString + ". Unexpected IOException: " + e.getMessage(), e);
            }

            if (readTimeout > 0) {
                conn.setReadTimeout(readTimeout);
            }

            if (connectionTimeout > 0) {
                conn.setConnectTimeout(connectionTimeout);
            }

            try {
                conn.setRequestMethod(requestMethod);
            } catch (ProtocolException e) {
                throw new CommunicationException("Failed to set request method to "
                        + requestMethod, e);
            }

            conn.setDoInput(true);

            if (parameters != null) {

                // Send the parameters out
                PrintWriter out;
                try {
                    out = new PrintWriter(conn.getOutputStream());
                    out.print(createQueryString(parameters));
                    out.close();
                } catch (SocketTimeoutException e) {
                    throw new CommunicationException(urlString
                            + " did not respond after the connection time limit ("
                            + conn.getConnectTimeout() + "ms) .", e);
                } catch (IOException e) {

                    // check if the server is simply unreachable
                    if (e.getMessage().contains("EHOSTUNREACH")) {
                        throw new CommunicationException("Host " + urlString
                                + " is currently unreachable, please contact your administrator.",
                                e);
                    } else {
                        throw new CommunicationException(
                                "Failed to get output stream from existing connection to URL "
                                        + urlString + ". Unexpected IOException: " + e.getMessage(),
                                e);
                    }
                }

            }

            // Handle issues
            int statusCode;
            try {
                statusCode = conn.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    throw new CommunicationException(urlString + " not found");
                } else if (statusCode != HttpURLConnection.HTTP_OK) {
                    // throw new RDAOCommunicationException(
                    // "Server status returned is not 200 (OK), contact your administrator");
                    throw new CommunicationException("Server status: " + statusCode + ", expecting 200");
                }
            } catch (IOException e) {
                String message;

                if (e.getMessage() != null) {
                    message = "Unable to retrieve status code of the response. " + e.getMessage();
                } else {
                    message = "Unable to retrieve status code of the response.";
                }

                throw new CommunicationException(message, e);
            }

//            FLLogger.d(TAG, "response length: " + conn.getContentLength());

            // Convert the response to string and then return it
            try {
                return parseInputStream(conn.getInputStream());
            } catch (UnsupportedEncodingException e) {
                throw new CommunicationException(
                        "This device does not support UTF-8 encoding.", e);
            } catch (IOException e) {
                throw new CommunicationException(
                        "Unexpected IOException when trying to read server response, exception: "
                                + e.getMessage(), e);
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    /**
     * Converts a given map (key value pair) of strings to a query string ready to be sent
     * to the server as request parameters.
     *
     * @param parameters the Map of values to become parameters
     * @return a string representation of the parameters
     */
    public static String createQueryString(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                try {
                    parametersAsQueryString.append(parameterName).append(PARAMETER_EQUALS_CHAR)
                            .append(URLEncoder.encode(parameters.get(parameterName), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("UTF-8 not supported by this device.");
                }

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }

    /**
     * Reads a given InputStream object and returns a string from it.
     *
     * @param stream the input stream to convert
     * @return The string format of the given input stream
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    public static String parseInputStream(InputStream stream) throws IOException {

        StringBuilder sbuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sbuilder.append(line);
            }

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sbuilder.toString();

    }

    // ===========================================================================
    // Getters & Setters

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public static class CommunicationException extends Exception {

        public CommunicationException(String message) {
            super(message);
        }

        public CommunicationException(Throwable t) {
            super(t);
        }

        public CommunicationException(String message, Throwable t) {
            super(message, t);
        }

    }

}
