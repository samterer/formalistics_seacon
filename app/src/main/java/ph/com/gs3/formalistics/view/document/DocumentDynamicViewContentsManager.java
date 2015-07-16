package ph.com.gs3.formalistics.view.document;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.FormContentType;
import ph.com.gs3.formalistics.global.constants.FormContentTypeGroupings;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.model.values.business.form.FormViewContentData;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowObject;
import ph.com.gs3.formalistics.model.values.business.form.content.EmbeddedViewData;
import ph.com.gs3.formalistics.view.document.DocumentDynamicFieldsChangeDependencyMapper.FieldComputationRequestListener;
import ph.com.gs3.formalistics.view.document.contents.FField;
import ph.com.gs3.formalistics.view.document.contents.FView;
import ph.com.gs3.formalistics.view.document.contents.FViewCollection;
import ph.com.gs3.formalistics.view.document.contents.fields.FCheckBoxGroup;
import ph.com.gs3.formalistics.view.document.contents.fields.FCodeScannerField;
import ph.com.gs3.formalistics.view.document.contents.fields.FCodeScannerField.CodeScannerListener;
import ph.com.gs3.formalistics.view.document.contents.fields.FDateTimePicker;
import ph.com.gs3.formalistics.view.document.contents.fields.FDateTimePicker.DateTimePickerListener;
import ph.com.gs3.formalistics.view.document.contents.fields.FDateTimePicker.PickerType;
import ph.com.gs3.formalistics.view.document.contents.fields.FDropdown;
import ph.com.gs3.formalistics.view.document.contents.fields.FDynamicImage;
import ph.com.gs3.formalistics.view.document.contents.fields.FDynamicImage.DynamicImageFieldActionListener;
import ph.com.gs3.formalistics.view.document.contents.fields.FPickList;
import ph.com.gs3.formalistics.view.document.contents.fields.FPickList.PickListFieldListener;
import ph.com.gs3.formalistics.view.document.contents.fields.FRadioButtonGroup;
import ph.com.gs3.formalistics.view.document.contents.fields.FTextField;
import ph.com.gs3.formalistics.view.document.contents.views.FEmbeddedView;
import ph.com.gs3.formalistics.view.document.contents.views.FEmbeddedView.EmbeddedViewEventsListener;

public class DocumentDynamicViewContentsManager implements DocumentViewContentsManager {

    public static final String TAG = DocumentDynamicViewContentsManager.class.getSimpleName();

    private DocumentHeaderData documentHeaderData;
    private final FViewCollection documentViewContents;
    private final Context context;
    private final User currentUser;

    private final DocumentDynamicFieldsChangeDependencyMapper documentDynamicFieldsChangeDependencyMapper;

    // Field Listeners
    private PickListFieldListener pickListFieldListener;
    private DateTimePickerListener dateTimePickerListener;
    private CodeScannerListener codeScannerListener;
    private EmbeddedViewEventsListener embeddedViewEventsListener;
    private DynamicImageFieldActionListener dynamicImageFieldActionListener;

    public DocumentDynamicViewContentsManager(Context context, FieldComputationRequestListener fieldComputationRequestListener, User currentUser) {
        documentViewContents = new FViewCollection();
        this.documentDynamicFieldsChangeDependencyMapper = new DocumentDynamicFieldsChangeDependencyMapper(fieldComputationRequestListener);

        this.context = context;
        this.currentUser = currentUser;
    }

    @Override
    public View getCreatedDocumentViewContentsContainer() {
        return new DefaultDocumentFieldsContainer(context, this);
    }

    @Override
    public void createDocumentViewsFromData(
            List<FormViewContentData> formViewContentDataList,
            JSONObject fieldValues,
            DocumentHeaderData documentHeaderData,
            User currentUser) {

        this.documentHeaderData = documentHeaderData;

        WorkflowObject currentWorkflowObject = documentHeaderData.getWorkflowObject();

        FLLogger.d(TAG, "Field values: " + fieldValues.toString());

        for (FormViewContentData formViewContentData : formViewContentDataList) {
            String formViewContentFieldName = formViewContentData.getName();

            // Get and assign the value of the field
            if (fieldValues.has(formViewContentFieldName) && FormContentTypeGroupings.isField(formViewContentData.getType())) {
                try {
                    String value = fieldValues.getString(formViewContentFieldName);
                    FLLogger.d(TAG, formViewContentFieldName + " = " + value);
                    ((FormFieldData) formViewContentData).setValue(value);
                } catch (JSONException e) {
                    FLLogger.d(
                            TAG,
                            "Unable to read value of field " + formViewContentFieldName + " "
                                    + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (formViewContentData instanceof FormFieldData) {
                // Generate the properties of the field from the document
                formViewContentData.setEnabled(currentWorkflowObject
                        .getFieldsEnabled()
                        .contains(formViewContentFieldName));
                formViewContentData.setHidden(currentWorkflowObject
                        .getFieldsHidden()
                        .contains(formViewContentFieldName));
                ((FormFieldData) formViewContentData).setRequired(currentWorkflowObject
                        .getFieldsRequired()
                        .contains(formViewContentFieldName));
            }

            FView fieldView = createFormContent(formViewContentData);

            if (fieldView != null) {

                fieldView.setEnabled(formViewContentData.isEnabled());
                fieldView.setVisible(!formViewContentData.isHidden());

                documentViewContents.add(fieldView);
            } else {
                FLLogger.d(
                        TAG,
                        "Unable to create field for " + formViewContentData.getName() + ", it's probably unsupported"
                );
            }
        }

        documentDynamicFieldsChangeDependencyMapper.mapEmbeddedViewsChangeDependencies(documentViewContents, embeddedViewEventsListener);
        documentDynamicFieldsChangeDependencyMapper.mapFieldsChangeDependencies(documentViewContents);

        // TODO: fix formulas on load

        // set field values, they are set here to trigger change listeners for formulas
//        Iterator<String> iterator = fieldValues.keys();
//        while (iterator.hasNext()) {
//            String key = iterator.next();
//            try {
//                String value = fieldValues.getString(key);
//                if (value == null || value == "") {
//                    setFieldValue(key, value);
//                }
//            } catch (JSONException e) {
//                FLLogger.e(
//                        TAG,
//                        "Unable to read value of field " + key + " "
//                                + e.getMessage());
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public List<String> validateFields() {
        List<String> fieldsRequired = documentHeaderData.getWorkflowObject().getFieldsRequired();
        List<String> fieldsThatFailedValidation = new ArrayList<>();

        JSONObject fieldValues = getFieldValues();

        boolean passed;

        for (String fieldRequired : fieldsRequired) {
            passed = false;
            try {
                String value = fieldValues.getString(fieldRequired);
                passed = value != null && !"".equals(value.trim());
            } catch (JSONException e) {
                FLLogger.w(TAG, "Failed to get value of " + fieldRequired + ": " + e.getMessage());
            }

            if (!passed) {
                fieldsThatFailedValidation.add(fieldRequired);
            }
        }

        return fieldsThatFailedValidation;

    }


    public void setFieldValue(String fieldId, String fieldValue) {
        FField field = documentViewContents.findFieldView(fieldId);

        if (field != null) {
            if (fieldValue != null && !"null".equals(fieldValue)) {
                field.setValue(fieldValue);
            }
        } else {
            FLLogger.e(TAG, "Failed updating field " + fieldId + ", field not found.");
        }

    }

    public JSONObject getFieldValues() {
        JSONObject fieldValues = new JSONObject();

        for (FView viewContent : documentViewContents) {
            if (viewContent instanceof FField) {
                FField fieldView = (FField) viewContent;
                try {
                    fieldValues.put(fieldView.getFieldName(), fieldView.getValue());
                } catch (JSONException e) {
                    FLLogger.e(TAG, "Failed to insert field value of " + fieldView.getFieldName()
                            + ", cause: " + e.getMessage());
                }
            }
        }

        return fieldValues;
    }

    public void notifyFieldsRequired(List<String> fieldsThatFailedValidation) {
        for (String fieldId : fieldsThatFailedValidation) {
            FLLogger.d(TAG, fieldId + " failed validation");

            FField field = documentViewContents.findFieldView(fieldId);

            if (field != null) {
                field.showError(FField.PROMPT_FIELD_REQUIRED);
            }

        }
    }

    /**
     * Initializes listeners using the activity, this may throw an illegal state exception
     * if the required listeners are not implemented by the activity
     *
     * @param activity The activity that implements PickListFieldListener, DateTimePickerListener,
     *                 CodeScannerListener, EmbeddedViewEventsListener and DynamicImageFieldActionListener
     */
    public void setSpecialFieldsListener(Activity activity) {

        String exceptionMessage = "The activity " + activity.getClass().getSimpleName()
                + " must implement ";

        try {
            this.pickListFieldListener = (PickListFieldListener) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(exceptionMessage + " PickListFieldListener");
        }

        try {
            this.dateTimePickerListener = (DateTimePickerListener) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(exceptionMessage + " DateTimePickerListener");
        }

        try {
            this.codeScannerListener = (CodeScannerListener) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(exceptionMessage + " BarcodeScannerListener");
        }

        try {
            this.embeddedViewEventsListener = (EmbeddedViewEventsListener) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(exceptionMessage + " EmbeddedViewEventsListener");
        }

        try {
            this.dynamicImageFieldActionListener = (DynamicImageFieldActionListener) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(exceptionMessage + " DynamicImageFieldActionListener");
        }
    }

    @Override
    public View findFieldView(String fieldId) {
        return documentViewContents.findFieldView(fieldId);
    }

    // =============================================================================
    // {{ Events

    @Override
    public void onLoad() {
        // TODO Add the dynamic events for creation here

    }

    // }}

    // =============================================================================
    // {{ Private Functional Methods

    private FView createFormContent(FormViewContentData formContentData) {

        FView view = null;

        if (FormContentTypeGroupings.isField(formContentData.getType())) {
            FormFieldData formField = (FormFieldData) formContentData;

            FField fieldView;

            switch (formContentData.getType()) {
                case TEXT_FIELD:
                    fieldView = new FTextField(context, formField);
                    break;
                case TEXT_AREA:
                    fieldView = new FTextField(context, formField, FTextField.TextFieldType.MULTI_LINE);
                    break;
                case CHECK_BOX_GROUP:
                case SELECT_MANY:
                    fieldView = new FCheckBoxGroup(context, formField, formField.getOptions());
                    break;
                case RADIO_BUTTON_GROUP:
                    fieldView = new FRadioButtonGroup(context, formField, formField.getOptions());
                    break;
                case DROPDOWN:
                    fieldView = new FDropdown(context, formField, formField.getOptions());
                    break;
                case PICK_LIST:

                    // Code level validation
                    if (pickListFieldListener == null) {
                        throw new IllegalStateException(
                                "Field builder cannot create picklists without a picklist listener");
                    }
                    fieldView = new FPickList(context, formField, pickListFieldListener);
                    break;
                case DATE_PICKER:
                    // Code level validation
                    if (dateTimePickerListener == null) {
                        throw new IllegalStateException(
                                "Field builder cannot create date pickers without a date time picker listener");
                    }

                    fieldView = new FDateTimePicker(context, formField, PickerType.DATE, dateTimePickerListener);
                    break;
                case DATE_TIME_PICKER:
                    // Code level validation
                    if (dateTimePickerListener == null) {
                        throw new IllegalStateException(
                                "Field builder cannot create date pickers without a date time picker listener");
                    }

                    fieldView = new FDateTimePicker(context, formField, PickerType.DATETIME, dateTimePickerListener);
                    break;
                case TIME_PICKER:
                    // code level validation
                    if (dateTimePickerListener == null) {
                        throw new IllegalStateException(
                                "Field builder cannot create date pickers without a date time picker listener");
                    }

                    fieldView = new FDateTimePicker(context, formField, PickerType.TIME, dateTimePickerListener);
                    break;
                case BARCODE_SCANNER:
                case QRCODE_SCANNER:
                    // code level validation
                    if (codeScannerListener == null) {
                        throw new IllegalStateException(
                                "Field builder cannot create barcode scanners without a listener");
                    }

                    FCodeScannerField.CodeType codeType =
                            formContentData.getType() == FormContentType.BARCODE_SCANNER ?
                                    FCodeScannerField.CodeType.BAR_CODE : FCodeScannerField.CodeType.QR_CODE;

                    fieldView = new FCodeScannerField(context, formField, codeType, codeScannerListener);
                    break;
                case DYNAMIC_IMAGE:
                    // code level validation
                    if (dynamicImageFieldActionListener == null) {
                        throw new IllegalStateException(
                                "Field builder cannot create barcode scanners without a listener");
                    }
                    fieldView = new FDynamicImage(context, formField, dynamicImageFieldActionListener);
                    break;
//				case SINGLE_ATTACHMENT:
//					String server = activeUser.getCompany().getServer();
//					formField.setValue(server + formField.getValue());
//					fieldView = new AttachmentField(context, formField);
//					break;
                default:
                    fieldView = null;

            }

            if (fieldView != null) {

//                FLLogger.d(TAG, "Setting " + formField.getLabel() + " = " + formField.getValue());

                fieldView.setLabel(formField.getLabel());
                fieldView.setValue(formField.getValue());
                fieldView.setEnabled(formField.isEnabled());

                view = fieldView;
            }

        } else {
            switch (formContentData.getType()) {
                case EMBEDDED_VIEW:
                    FEmbeddedView embeddedView = new FEmbeddedView(context, (EmbeddedViewData) formContentData);
                    embeddedView.setEventsListener(embeddedViewEventsListener);

                    view = embeddedView;
                    break;
                default:
                    view = null;
            }
        }

        if (view != null) {
            // Set properties
            int visibility = formContentData.isHidden() ? View.GONE : View.VISIBLE;
            view.setVisibility(visibility);

        }

        return view;
    }

    // }}

    // =============================================================================
    // {{ Getters & Setters

    public FViewCollection getDocumentViewContents() {
        return documentViewContents;
    }

    // }}

}
