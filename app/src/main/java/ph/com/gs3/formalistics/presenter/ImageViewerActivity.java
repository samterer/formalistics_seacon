package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.dao.FilesDAO;
import ph.com.gs3.formalistics.presenter.fragment.view.ImageViewerViewFragment;

/**
 * Created by Ervinne on 4/22/2015.
 */
public class ImageViewerActivity extends Activity implements ImageViewerViewFragment.ImageViewerViewEventListener {

    public static final String TAG = ImageViewerActivity.class.getSimpleName();

    private ImageViewerViewFragment imageViewerViewFragment;

    public static final String EXTRA_IMAGE_LOCAL_PATH = "image_path";
    public static final String EXTRA_IMAGE_NEEDS_SCALING = "image_needs_scaling";

    private String imageLocalPath;
    private boolean imageNeedsScaling;

    private FilesDAO filesDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        filesDAO = new FilesDAO(this);

        initializeStateTransferredFields();

        imageViewerViewFragment = (ImageViewerViewFragment) getFragmentManager().findFragmentByTag(ImageViewerViewFragment.TAG);

        if (imageViewerViewFragment == null) {
            imageViewerViewFragment = new ImageViewerViewFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, imageViewerViewFragment, ImageViewerViewFragment.TAG)
                    .commit();
        }

    }

    private void initializeStateTransferredFields() {

        Bundle extras = getIntent().getExtras();
        imageLocalPath = extras.getString(EXTRA_IMAGE_LOCAL_PATH);
        imageNeedsScaling = extras.getBoolean(EXTRA_IMAGE_NEEDS_SCALING);

    }

    @Override
    public void onViewReady() {

        Bitmap bitmap = filesDAO.getBitmapFromPath(imageLocalPath, 1000);
        imageViewerViewFragment.setImageBitmap(bitmap);


    }

}
