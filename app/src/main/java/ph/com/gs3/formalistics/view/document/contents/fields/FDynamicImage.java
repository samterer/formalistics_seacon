package ph.com.gs3.formalistics.view.document.contents.fields;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.view.document.contents.FField;

/**
 * Created by Ervinne on 4/16/2015.
 */
public class FDynamicImage extends FField {

    private final LinearLayout llActionsContainer;
    private final TextView tvNotificationMessage;
    private final ImageView ivDynamicImage;
    private final Button bBrowseImage;
    private final Button bTakeNewimage;

    private String imageLocalPath;
    private String imageURL;

    private final DynamicImageFieldActionListener listener;

    public FDynamicImage(Context context, FormFieldData formFieldData, final DynamicImageFieldActionListener listener) {
        super(context, R.layout.field_dynamic_image, formFieldData);

        this.listener = listener;

        llActionsContainer = (LinearLayout) findViewById(R.id.FDynamicImage_llActionsContainer);

        tvNotificationMessage = (TextView) findViewById(R.id.FDynamicImage_tvNotificationMessage);
        ivDynamicImage = (ImageView) findViewById(R.id.FDynamicImage_ivDynamicImage);
        ivDynamicImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onOpenImageCommand(FDynamicImage.this);
            }
        });

        bBrowseImage = (Button) findViewById(R.id.FDynamicImage_bBrowseImage);
        bBrowseImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBrowseForImageCommand(FDynamicImage.this);
            }
        });

        bTakeNewimage = (Button) findViewById(R.id.FDynamicImage_bTakeNewImage);
        bTakeNewimage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTakeNewImageCommand(FDynamicImage.this);
            }
        });

        tvNotificationMessage.setVisibility(GONE);

    }

    @Override
    public void showError(String errorMessage) {
        tvNotificationMessage.setText(errorMessage);
        tvNotificationMessage.setTextColor(Color.RED);
        tvNotificationMessage.setVisibility(VISIBLE);
    }

    @Override
    public void setValue(String value) {
        imageURL = value;

        notifyValueChanged();
        listener.onFindImageOnLocalStorageCommand(this);
    }

    @Override
    public String getValue() {

        if (imageURL == null) {
            return getImageLocalPath();
        } else {
            return imageURL;
        }

    }

    @Override
    public void setEnabled(boolean enabled) {
        llActionsContainer.setVisibility(enabled ? VISIBLE : GONE);
//        ivDynamicImage.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return ivDynamicImage.isEnabled();
    }

    public String getImageLocalPath() {
        return imageLocalPath;
    }

    public void setImageLocalPath(String imageLocalPath) {
        this.imageLocalPath = imageLocalPath;
    }

    public void setBitmap(Bitmap bitmap) {
        ivDynamicImage.setImageBitmap(bitmap);
    }

    public interface DynamicImageFieldActionListener {

        void onBrowseForImageCommand(FDynamicImage source);

        void onTakeNewImageCommand(FDynamicImage source);

        Bitmap onFindImageOnLocalStorageCommand(FDynamicImage source);

        void onOpenImageCommand(FDynamicImage source);

    }

}
