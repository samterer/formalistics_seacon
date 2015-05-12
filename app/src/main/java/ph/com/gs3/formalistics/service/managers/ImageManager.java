package ph.com.gs3.formalistics.service.managers;

import android.content.Context;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.dao.UsersDAO;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.imagemanager.ImageLoader;

/**
 * @author Ervinne Sodusta
 */
public class ImageManager {

    public static final String TAG = ImageManager.class.getSimpleName();

    public static final String IMAGE_CACHE_PATH = ".formalistics/img_cache";

    public ImageLoader imageLoader;

    private Map<String, List<ImageView>> queuedImageViews;
    private Map<String, String> downloadedImages;

    private int imageNotAvailableStub = R.drawable.icons_user;

    private UsersDAO usersDAO;

    private static ImageManager defaultInstance;

    public static ImageManager getDefaultInstance(Context context) {

        if (defaultInstance == null) {
            defaultInstance = new ImageManager(context);
        }

        return defaultInstance;

    }

    public ImageManager(Context context) {

        imageLoader = new ImageLoader(context, IMAGE_CACHE_PATH);
        imageLoader.setImageNotAvailableResource(imageNotAvailableStub);

        queuedImageViews = new HashMap<String, List<ImageView>>();
        downloadedImages = new HashMap<String, String>();

        usersDAO = new UsersDAO(context);
    }

    public void requestUserImage(int userId, ImageView imageView) {
        // Get the user from the id provided
        User user = usersDAO.getUserWithId(userId);

        if (user == null) {
            FLLogger.w(TAG, "User with id " + userId + " is not found. Display/download of image failed.");
            return;
        }

        String userDbId = Integer.toString(user.getId());

        // Check if the image has be fetched already.
        if (downloadedImages.containsKey(userDbId)) {
            // The image is existing, simply load the image to the image view
            imageLoader.displayImage(downloadedImages.get(userDbId), imageView);
        } else {

            imageView.setImageResource(imageLoader.getImageNotAvailableResource());

            if (queuedImageViews.containsKey(userDbId)) {
                // Register the image view to an existing list
                queuedImageViews.get(userDbId).add(imageView);
            } else {
                // Create a new list for this user and request for the user's data
                List<ImageView> userImageViewQueue = new ArrayList<ImageView>();
                userImageViewQueue.add(imageView);
                queuedImageViews.put(userDbId, userImageViewQueue);

                // TODO: check if there is a need for re checking connection here

                downloadImage(user);

            }
        }
    }

    public void downloadImage(User user) {

        String userId = Integer.toString(user.getId());

        if (!queuedImageViews.containsKey(userId)) {
            // The image view list was suddenly lost, this may be caused
            // by changing activities etc., ignore and abort operation
            return;
        }

        // Load the image to each image views of the corresponding users
        List<ImageView> userImageViews = queuedImageViews.get(userId);

        if (user != null && user.getImageURL() != null && !user.getImageURL().isEmpty()
                && !"null".equals(user.getImageURL().toLowerCase(Locale.ENGLISH))) {

            for (ImageView currentUserImageView : userImageViews) {
                imageLoader.displayImage(user.getImageURL(), currentUserImageView);
            }

            // get the full image url from the user's account server and image url
            String server = user.getCompany().getServer();
            String imageURL = user.getImageURL();
            String fullImageURL = getFullURL(server, imageURL);

            // images that are already downloaded should have their
            // corresponding image views removed from the queue, a reference
            // to the image will now be stored in the "downloadedImages" map
            // and will be used by other image views instead of downloading
            // the image again.
            queuedImageViews.remove(user.getId());
            downloadedImages.put(userId, fullImageURL);

        } else {
            for (ImageView currentUserImageView : userImageViews) {
                currentUserImageView.setImageResource(imageLoader.getImageNotAvailableResource());
            }
        }

    }

    private String getFullURL(String server, String url) {

        if (url.indexOf("/") == 0) {
            url = server + url;
        } else {
            url = server + "/" + url;
        }

        return url;

    }

}
