package ph.com.gs3.formalistics.service;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
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

import java.io.File;
import java.io.IOException;

import ph.com.gs3.formalistics.model.values.application.APIResponse;

/**
 * Created by Ervinne on 4/23/2015.
 */
public class FileUploader {

    public static final String TAG = FileUploader.class.getSimpleName();

    private final DefaultHttpClient httpClient;

    private final Context context;
    private final String server;

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
