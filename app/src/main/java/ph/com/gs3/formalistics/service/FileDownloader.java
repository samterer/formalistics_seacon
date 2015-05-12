package ph.com.gs3.formalistics.service;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ervinne on 4/22/2015.
 */
public class FileDownloader {

    public static int CONNECTION_TIMEOUT = 10000; // 10s
    public static int READ_TIMEOUT = 60000; // 1min

    private String fileDestinationLocation;

    public FileDownloader(Context context) {
        this.fileDestinationLocation = context.getFilesDir().toString();
    }

    public File download(String url) throws DownloadException {
        File file = new File(fileDestinationLocation + "/" + getFileNameFromURL(url));

        InputStream is = null;
        OutputStream os = null;

        // the image is not yet downloaded, get it from web
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();

            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            conn.setInstanceFollowRedirects(true);
            is = conn.getInputStream();
            os = new FileOutputStream(file);

            copyStream(is, os);

            conn.disconnect();

            return file;

        } catch (IOException e) {
            throw new DownloadException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    is = null;
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    os = null;
                }
            }

        }
    }

    private void copyStream(InputStream is, OutputStream os) {

        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }

    }

    private String getFileNameFromURL(String url) {

        String[] splittedURL = url.split("/");
        return splittedURL[splittedURL.length - 1];

    }

    public static class DownloadException extends Exception {

        public DownloadException(String detailMessage) {
            super(detailMessage);
        }

        public DownloadException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public DownloadException(Throwable throwable) {
            super(throwable);
        }
    }

}
