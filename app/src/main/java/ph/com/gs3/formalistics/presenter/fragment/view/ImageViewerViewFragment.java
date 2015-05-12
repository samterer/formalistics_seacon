package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ph.com.gs3.formalistics.R;

/**
 * Created by Ervinne on 4/22/2015.
 */
public class ImageViewerViewFragment extends Fragment {

    public static final String TAG = ImageViewerViewFragment.class.getSimpleName();

    private ImageView ivImage;

    private ImageViewerViewEventListener imageViewerViewEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        imageViewerViewEventListener = (ImageViewerViewEventListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        ivImage = (ImageView) rootView.findViewById(R.id.ImageViewer_ivImage);

        imageViewerViewEventListener.onViewReady();

        return rootView;
    }

    public void setImageBitmap(Bitmap bitmap) {
        ivImage.setImageBitmap(bitmap);
    }

    public interface ImageViewerViewEventListener {

        void onViewReady();

    }

}
