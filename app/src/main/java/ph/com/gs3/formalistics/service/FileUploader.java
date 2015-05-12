package ph.com.gs3.formalistics.service;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import ph.com.gs3.formalistics.model.values.application.APIResponse;

/**
 * Created by Ervinne on 4/23/2015.
 */
public class FileUploader {

    public static final String TAG = FileUploader.class.getSimpleName();

    private DefaultHttpClient httpClient;

    private Context context;
    private String server;

    public FileUploader(Context context, String server) {
        this.context = context;
        this.server = server;

        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        httpClient = new DefaultHttpClient(params);
    }

    /**
     * Uploads a file to the server and returns its resulting url in String format
     *
     * @param fileToUpload the file to upload
     * @return the url of the file in the server
     * @throws UploadException
     */
    public String upload(File fileToUpload) throws UploadException {

        String urlString = server + "/file/upload";

        try {
            HttpPost httpPost = new HttpPost(urlString);

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart("target_directory", new StringBody("formImage"));
            multipartEntity.addPart("upload_permanent", new StringBody("true"));
            multipartEntity.addPart("file", new FileBody(fileToUpload));
            httpPost.setEntity(multipartEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity r_entity = httpResponse.getEntity();

            String responseString = EntityUtils.toString(r_entity);

            JSONObject responseJSON = new JSONObject(responseString);
            APIResponse apiResponse = new APIResponse(responseJSON);

            if (apiResponse.isOperationSuccessful()) {
                JSONObject responseResults = new JSONObject(apiResponse.getResults());
                return server + "/" + responseResults.getString("full_file_path");
            } else {
                throw new UploadException(apiResponse.getErrorMessage());
            }

        } catch (IOException | JSONException | APIResponse.InvalidResponseException e) {
            throw new UploadException(e);
        }

    }

    private class FileUploadResponseHandler implements ResponseHandler<Object> {

        @Override
        public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

            HttpEntity r_entity = response.getEntity();
            String responseString = EntityUtils.toString(r_entity);
            Log.d("UPLOAD", responseString);

            return null;
        }

    }

    public void uploadOldStyle(File fileToUpload) {

        String urlString = server + "/file/upload";

        String fileName = fileToUpload.getName();

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(fileToUpload);
            URL url = new URL(urlString);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                    + fileName + "\"" + lineEnd);

            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i(TAG, "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

            if (serverResponseCode == 200) {
                Log.i(TAG, parseInputStream(conn.getInputStream()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Reads a given InputStream object and returns a string from it.
     *
     * @param stream the input stream to convert
     * @return The string format of the given input stream
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    public static String parseInputStream(InputStream stream) throws IOException,
            UnsupportedEncodingException {

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

    public static class UploadException extends Exception {
        public UploadException(String detailMessage) {
            super(detailMessage);
        }

        public UploadException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public UploadException(Throwable throwable) {
            super(throwable);
        }
    }

}
