package ph.com.gs3.formalistics.global.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormContentTypeGroupings {

    public static final FormContentType[] FIELDS = {
            FormContentType.TEXT_FIELD,
            FormContentType.TEXT_AREA,
            FormContentType.DROPDOWN,
            FormContentType.CHECK_BOX_GROUP,
            FormContentType.SELECT_MANY,
            FormContentType.RADIO_BUTTON_GROUP,
            FormContentType.DATE_PICKER,
            FormContentType.TIME_PICKER,
            FormContentType.DATE_TIME_PICKER,
            FormContentType.PICK_LIST,
            FormContentType.BARCODE_SCANNER,
            FormContentType.QRCODE_SCANNER,
            FormContentType.SINGLE_ATTACHMENT,
            FormContentType.DYNAMIC_IMAGE
    };

    public static final FormContentType[] VIEW = {FormContentType.EMBEDDED_VIEW};

    public static final boolean isField(FormContentType formContentType) {

        List<FormContentType> formContentTypes = new ArrayList<>(Arrays.asList(FIELDS));
        return formContentTypes.contains(formContentType);

    }

    public static final boolean isView(FormContentType formContentType) {

        List<FormContentType> formContentTypes = new ArrayList<>(Arrays.asList(VIEW));
        return formContentTypes.contains(formContentType);

    }

}
