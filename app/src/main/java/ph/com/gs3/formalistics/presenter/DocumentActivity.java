package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ActivityRequestCodes;
import ph.com.gs3.formalistics.global.constants.ActivityResultCodes;
import ph.com.gs3.formalistics.global.constants.DateTimePickerType;
import ph.com.gs3.formalistics.global.constants.DocumentType;
import ph.com.gs3.formalistics.global.interfaces.CallbackCommand;
import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.global.utilities.DocumentUtilities;
import ph.com.gs3.formalistics.global.utilities.Serializer;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.PicklistDataJSONParser;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.FilesDAO;
import ph.com.gs3.formalistics.model.values.application.FileInfo;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.Formula;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowAction;
import ph.com.gs3.formalistics.model.values.business.form.content.EmbeddedViewData;
import ph.com.gs3.formalistics.model.values.business.form.content.PickListData;
import ph.com.gs3.formalistics.presenter.fragment.view.DocumentViewFragment;
import ph.com.gs3.formalistics.presenter.fragment.view.DocumentViewFragment.DocumentViewActionListener;
import ph.com.gs3.formalistics.presenter.fragment.view.DocumentViewFragment.DocumentViewFragmentParameterBundle;
import ph.com.gs3.formalistics.presenter.fragment.worker.DocumentWorkerFragment;
import ph.com.gs3.formalistics.service.formula.FormulaEvaluator;
import ph.com.gs3.formalistics.service.formula.FormulaLexer;
import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.Token;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.function.LookupRequestListener;
import ph.com.gs3.formalistics.view.dialogs.ListSelectionDialogFragment;
import ph.com.gs3.formalistics.view.document.DocumentDynamicFieldsChangeDependencyMapper;
import ph.com.gs3.formalistics.view.document.DocumentDynamicViewContentsManager;
import ph.com.gs3.formalistics.view.document.DocumentViewContentsManager;
import ph.com.gs3.formalistics.view.document.contents.FField;
import ph.com.gs3.formalistics.view.document.contents.fields.FCodeScannerField;
import ph.com.gs3.formalistics.view.document.contents.fields.FDateTimePicker;
import ph.com.gs3.formalistics.view.document.contents.fields.FDynamicImage;
import ph.com.gs3.formalistics.view.document.contents.fields.FPickList;
import ph.com.gs3.formalistics.view.document.contents.views.FEmbeddedView;

public class DocumentActivity extends Activity implements DocumentViewActionListener,
        FPickList.PickListFieldListener,
        FDateTimePicker.DateTimePickerListener,
        FCodeScannerField.CodeScannerListener,
        FEmbeddedView.EmbeddedViewEventsListener,
        FDynamicImage.DynamicImageFieldActionListener,
        DocumentDynamicFieldsChangeDependencyMapper.FieldComputationRequestListener {

    //<editor-fold desc="Constants">
    public static final String TAG = DocumentActivity.class.getSimpleName();

    public static final String EXTRA_ACTIVE_USER = "active_user";
    public static final String EXTRA_DOCUMENT_TYPE = "document_type";
    public static final String EXTRA_FORM_ID = "form_id";
    public static final String EXTRA_DOCUMENT_OR_OUTGOING_ACTION_ID = "document_id";

    public static final String EXTRA_PARENT_DOCUMENT_ID = "parent_document_id";

    public static final String EXTRA_ISSUED_ACTION = "issued_action";
    public static final String EXTRA_DATA_SENDING_DATA = "data_sending_data";
    //</editor-fold>

    private FEmbeddedView embeddedViewAwaitingChildDocumentSubmit;
    private FDynamicImage activeDynamicImageField;
    private String activeBarcodeScannerFieldId;

    private JSONObject dataSendingData;
    private String lastDocumentData;

    private DocumentViewContentsManager documentViewContentsManager;

    private DocumentWorkerFragment documentWorkerFragment;
    private DocumentViewFragment documentViewFragment;

    private FormulaLexer formulaLexer;

    //<editor-fold desc="Activity Life Cycle and Activity Action Listeners">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_document);

        initializeWorkerFragment();

        User activeUser = documentWorkerFragment.getActiveUser();
        Form form = documentWorkerFragment.getCurrentForm();

        setTitle(form.getName());

        documentViewContentsManager = new DocumentDynamicViewContentsManager(this, this, activeUser);
        formulaLexer = new FormulaLexer();

        if (savedInstanceState == null) {
            documentViewFragment = DocumentViewFragment.createInstance(documentViewContentsManager);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, documentViewFragment, DocumentViewFragment.TAG)
                    .commit();
        } else {
            documentViewFragment = (DocumentViewFragment) getFragmentManager().findFragmentByTag(DocumentViewFragment.TAG);
            documentViewFragment.setDocumentViewContentsManager(documentViewContentsManager);

            lastDocumentData = savedInstanceState.getString("document_field_data");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        JSONObject currentDocumentData = documentViewContentsManager.getFieldValues();
        outState.putString("document_field_data", currentDocumentData.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        DocumentType documentType = documentWorkerFragment.getDocumentType();

        switch (documentType) {
            case NEW_DOCUMENT:
            case CHILD_DOCUMENT:
                getMenuInflater().inflate(R.menu.document, menu);
                break;
            case EXISTING_DOCUMENT:
                User activeUser = documentWorkerFragment.getActiveUser();
                Document currentDocument = documentWorkerFragment.getCurrentDocument();

                if (DocumentUtilities.isProcessor(currentDocument, activeUser)) {
                    getMenuInflater().inflate(R.menu.document_for_approval, menu);
                } else {
                    FLLogger.d(TAG, "not displaying for approval");
                }
                break;
            default:
                break;
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_submit_action:
                boolean areFieldsValid = validateFields();
                if (areFieldsValid) {
                    showActionsDialog();
                } else {
                    FLLogger.d(TAG, "Not showing actions dialog");
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ActivityResultCodes.DOCUMENT_CHILD_SUBMITTED) {
            if (embeddedViewAwaitingChildDocumentSubmit != null) {
                try {
                    refreshEmbeddedView(embeddedViewAwaitingChildDocumentSubmit);
                } catch (JSONException e) {
                    FLLogger.e(TAG, "Failed to refresh embedded view: " + e.getMessage());
                    Toast.makeText(DocumentActivity.this, "Failed to refresh embedded view: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                embeddedViewAwaitingChildDocumentSubmit = null;
            }
        }

        switch (requestCode) {
            case ActivityRequestCodes.PICK_DATE:
            case ActivityRequestCodes.PICK_TIME:
            case ActivityRequestCodes.PICK_DATE_TIME: {
                if (data != null && data.hasExtra(DateTimePickerActivity.EXTRA_RESULT_SELECTED_DATE)) {
                    String fieldId = data.getStringExtra(DateTimePickerActivity.EXTRA_DATE_TIME_PICKER_FIELD_ID);
                    Date updatedDate = (Date) data.getSerializableExtra(DateTimePickerActivity.EXTRA_RESULT_SELECTED_DATE);
                    updateDateTimePickerView(requestCode, fieldId, updatedDate);
                }
            }
            break;
            case IntentIntegrator.REQUEST_CODE: { // Bar code scanner
                if (data != null) {
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (scanResult == null) {
                        return;
                    }
                    final String result = scanResult.getContents();
                    if (result != null) {
                        Handler handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                documentViewContentsManager.setFieldValue(activeBarcodeScannerFieldId, result);
                            }
                        });

                    }
                }
            }
            break;
            case ActivityRequestCodes.PICK_LIST: {

                if (data != null && data.hasExtra(PicklistPickerActivity.EXTRA_PICKED_RESULT)) {
                    String fieldId = data.getStringExtra(PicklistPickerActivity.EXTRA_FIELD_ID);
                    String result = data.getStringExtra(PicklistPickerActivity.EXTRA_PICKED_RESULT);
                    documentViewContentsManager.setFieldValue(fieldId, result);
                }

            }
            break;
            case ActivityRequestCodes.CAMERA_REQUEST: {

                FLLogger.d(TAG, " ActivityRequestCodes.CAMERA_REQUEST");

                FLLogger.d(TAG, "image local path: " + activeDynamicImageField.getImageLocalPath());
                Bitmap selectedImage = documentWorkerFragment.getBitmapFromPath(activeDynamicImageField.getImageLocalPath());

                if (selectedImage != null) {
                    File movedFile = documentWorkerFragment.moveFileToInternalStorage(new File(activeDynamicImageField.getImageLocalPath()));
                    activeDynamicImageField.setImageLocalPath(movedFile.getPath());
                    activeDynamicImageField.setBitmap(selectedImage);
                    documentWorkerFragment.saveOutgoingFileFromDynamicImageField(activeDynamicImageField);
                } else {
                    Toast.makeText(DocumentActivity.this, "Failed to save image on local storage.", Toast.LENGTH_LONG).show();
                }

            }
            break;
            case ActivityRequestCodes.IMAGE_PICKER_REQUEST: {
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = documentWorkerFragment.getRealPathFromURI(selectedImageUri);
                    Bitmap selectedImage = documentWorkerFragment.getBitmapFromPath(selectedImagePath);

                    String imagePath = documentWorkerFragment.getRealPathFromURI(selectedImageUri);
                    activeDynamicImageField.setImageLocalPath(imagePath);
                    activeDynamicImageField.setBitmap(selectedImage);
                    documentWorkerFragment.saveOutgoingFileFromDynamicImageField(activeDynamicImageField);
                }
            }
            break;
        }

    }

    @Override
    public void onBackPressed() {

        // Block if there are child documents that will be orphaned
        DocumentType documentType = documentWorkerFragment.getDocumentType();

        if (documentType != DocumentType.CHILD_DOCUMENT) {
            boolean hasNewChildOutgoingActions = documentWorkerFragment.hasNewChildOutgoingActions();
            if (hasNewChildOutgoingActions) {
                showClearChildOutgoingActionsOnExitDialog();
                // Prevent default
                return;
            }
        }

        addFinishResults();
        super.onBackPressed();

    }
    //</editor-fold>

    //<editor-fold desc="Initialization Methods">
    private void initializeWorkerFragment() {

        Bundle extras = getIntent().getExtras();

        User activeUser = (User) extras.getSerializable(EXTRA_ACTIVE_USER);
        DocumentType documentType = (DocumentType) extras.get(EXTRA_DOCUMENT_TYPE);
        int formId = extras.getInt(EXTRA_FORM_ID);
        int documentId = extras.getInt(EXTRA_DOCUMENT_OR_OUTGOING_ACTION_ID);
        int parentDocumentId = -1;

        if (extras.containsKey(EXTRA_PARENT_DOCUMENT_ID)) {
            parentDocumentId = extras.getInt(EXTRA_PARENT_DOCUMENT_ID);
        }

        if (extras.containsKey(EXTRA_DATA_SENDING_DATA)) {
            String dataSendingDataString = extras.getString(EXTRA_DATA_SENDING_DATA);
            try {
                dataSendingData = new JSONObject(dataSendingDataString);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        FragmentManager fragmentMan = getFragmentManager();

        documentWorkerFragment = (DocumentWorkerFragment) fragmentMan.findFragmentByTag(DocumentWorkerFragment.TAG);

        if (documentWorkerFragment == null) {
            // Initialize worker fragment
            documentWorkerFragment = DocumentWorkerFragment.createInstance(this, activeUser);
            fragmentMan.beginTransaction().add(documentWorkerFragment, DocumentWorkerFragment.TAG).commit();
        }

        try {
            documentWorkerFragment.initialize(documentType, formId, documentId, parentDocumentId);
        } catch (DocumentWorkerFragment.InvalidFormException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    //</editor-fold>

    //<editor-fold desc="View Functional Methods">
    private void refreshEmbeddedView(FEmbeddedView embeddedView) throws JSONException {
        EmbeddedViewData embeddedViewData = embeddedView.getEmbeddedViewData();
        String compareToFieldId = embeddedViewData.getSearchCompareToThisDocumentFieldId();
        JSONObject fieldValues = documentViewContentsManager.getFieldValues();

        // throws JSONException
        String compareToFieldValue = fieldValues.getString(compareToFieldId);
        onSearchForEmbeddedViewRequested(compareToFieldValue, embeddedView);

    }

    private void updateDateTimePickerView(int sourceRequestCode, String fieldId, Date updatedDate) {

        if (updatedDate == null) {
            FLLogger.w(TAG, "No date selected");
        } else {
            documentViewContentsManager.setFieldValue(fieldId, DateUtilities.SERVER_DATE_TIME_FORMAT.format(updatedDate));
//            SimpleDateFormat dateFormat;
//
//            switch (sourceRequestCode) {
//                case ActivityRequestCodes.PICK_DATE:
//                    dateFormat = DateUtilities.DEFAULT_DISPLAY_DATE_ONLY_FORMAT;
//                    break;
//                case ActivityRequestCodes.PICK_TIME:
//                    dateFormat = DateUtilities.DEFAULT_DISPLAY_TIME_ONLY_FORMAT;
//                    break;
//                case ActivityRequestCodes.PICK_DATE_TIME:
//                    dateFormat = DateUtilities.DEFAULT_DISPLAY_DATE_TIME_FORMAT;
//                    break;
//                default: {
//                    FLLogger.w(TAG, "Invalid request code, the update was ignored");
//                    return;
//                }
//            }
//
//            String displayDate = dateFormat.format(updatedDate);
//            documentViewContentsManager.setFieldValue(fieldId, displayDate);

        }

    }
    //</editor-fold>

    //<editor-fold desc="Functional Methods">
    private boolean validateFields() {

        boolean areFieldsValid = false;

        List<String> fieldsThatFailedValidation = documentViewContentsManager.validateFields();
        areFieldsValid = fieldsThatFailedValidation.size() <= 0;

        if (!areFieldsValid) {
            documentViewContentsManager.notifyFieldsRequired(fieldsThatFailedValidation);

            String message = String.format("The field(s) %s failed validation.", Serializer.serializeList(fieldsThatFailedValidation));
            Toast.makeText(DocumentActivity.this, message, Toast.LENGTH_LONG).show();
        }

        return areFieldsValid;

    }

    private void addFinishResults() {
        Document document = documentWorkerFragment.getCurrentDocument();
        DocumentType documentType = documentWorkerFragment.getDocumentType();
        int originalStarMark = documentWorkerFragment.getOriginalStarMark();

        if (documentType == DocumentType.EXISTING_DOCUMENT && originalStarMark != document.getStarMark()) {
            setResult(ActivityResultCodes.DOCUMENT_STAR_MARKED);
        } else if (documentType == DocumentType.CHILD_DOCUMENT) {
            setResult(ActivityResultCodes.DOCUMENT_CHILD_SUBMITTED);
        }
    }

    public void submitDocumentAction(String action) {

        // Extract data from document view
        // Document updatedDocument = documentViewFragment.updateDocumentValuesFromView();
        JSONObject updatedFieldValues = documentViewContentsManager.getFieldValues();
        documentWorkerFragment.saveDocumentAction(updatedFieldValues, action);

        int resultCode = ActivityResultCodes.DOCUMENT_ACTION_SUBMITTED;
        if (documentWorkerFragment.getDocumentType() != DocumentType.CHILD_DOCUMENT) {
            documentWorkerFragment.updateChildOutgoingActionsForSubmition();
        }

        if (documentWorkerFragment.getDocumentType() == DocumentType.CHILD_DOCUMENT) {
            resultCode = ActivityResultCodes.DOCUMENT_CHILD_SUBMITTED;
        }

        Toast.makeText(
                DocumentActivity.this,
                "Your document is being processed. You may continue processing/viewing other documents while its sending.",
                Toast.LENGTH_LONG).show();

        setResult(resultCode);
        finish();

    }
    //</editor-fold>

    //<editor-fold desc="Dialogs">
    private void showActionsDialog() {

        String dialogHeader = "Actions";
        String dialogEmptyListMessage = "No are actions available for this document";

        ListSelectionDialogFragment dialog = ListSelectionDialogFragment.createInstance(dialogHeader, dialogEmptyListMessage);
        List<WorkflowAction> actions = documentWorkerFragment
                .getDocumentHeaderData()
                .getWorkflowObject()
                .getWorkflowActions();

        List<String> actionStringList = new ArrayList<>();

        for (WorkflowAction action : actions) {
            actionStringList.add(action.getLabel());
        }

        dialog.setSelection(actionStringList);
        dialog.show(getFragmentManager(), ListSelectionDialogFragment.TAG);
        dialog.setOnItemSelectedCallback(new CallbackCommand<String>() {

            @Override
            public void execute(String result) {
                submitDocumentAction(result);
            }
        });
    }

    private void showClearChildOutgoingActionsOnExitDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        dialogBuilder.setTitle("Exit document?");
        dialogBuilder
                .setMessage("You submitted new documents with this document as their parent, exiting now will delete all those documents. Are you sure you want to exit?");
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                documentWorkerFragment.clearOrphanedOutgoingActions();
                addFinishResults();
                finish();
            }

        });
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.show();
    }

    private void showDynamicImageOptionsDialog() {

        String dialogHeader = "Image";
        String dialogEmptyListMessage = "";
        List<String> selection = new ArrayList<>();

        selection.add("Take a Picture");
        selection.add("Browse");

        ListSelectionDialogFragment dialog = ListSelectionDialogFragment.createInstance(dialogHeader, dialogEmptyListMessage);

        dialog.setSelection(selection);
        dialog.show(getFragmentManager(), ListSelectionDialogFragment.TAG);
        dialog.setOnItemSelectedCallback(new CallbackCommand<String>() {

            @Override
            public void execute(String result) {

                if ("Take a Picture".equals(result)) {
                    Intent cameraIntent = new Intent(
                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, ActivityRequestCodes.CAMERA_REQUEST);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                            ActivityRequestCodes.PICK_IMAGE);
                }

            }
        });

    }
    //</editor-fold>

    //<editor-fold desc="View Implementation Methods">
    @Override
    public void onFailedOpeningDocumentData(String message) {
        FLLogger.d(TAG, message);
        finish();
    }

    @Override
    public void onViewReady() {

        FLLogger.d(TAG, "onViewReady");

        DocumentViewFragmentParameterBundle bundle = new DocumentViewFragmentParameterBundle();

        bundle.activeUser = documentWorkerFragment.getActiveUser();
        bundle.form = documentWorkerFragment.getCurrentForm();
        bundle.documentType = documentWorkerFragment.getDocumentType();
        bundle.document = documentWorkerFragment.getCurrentDocument();
        bundle.outgoingAction = documentWorkerFragment.getCurrentOutgoingAction();

        // If there is already data updated by the user
        if (lastDocumentData != null) {
            FLLogger.d(TAG, "Setting last document data: " + lastDocumentData);
            bundle.document.setFieldValuesJSONString(lastDocumentData);
        }

        try {
            documentViewFragment.initializeDynamicViews(bundle);
        } catch (SQLiteException e) {
            String expectedMessage = "no such column: ";
            String message = e.getMessage();
            if (e.getMessage().startsWith(expectedMessage)) {

                String missingField = e.getMessage().substring(expectedMessage.length());
                missingField = missingField.split(" ")[0];
                message = "Missing field: " + missingField + " from " + bundle.form.getName() + " , please contact your administrator";
            }

            Toast.makeText(DocumentActivity.this, message, Toast.LENGTH_LONG).show();
            finish();
        }

        // Initialize values from data sent to this document
        if (dataSendingData != null) {
            Iterator keys = dataSendingData.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                try {
                    documentViewContentsManager.setFieldValue(key, dataSendingData.getString(key));
                } catch (JSONException e) {
                    Toast.makeText(DocumentActivity.this, "Failed to assign data to " + key + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                    FLLogger.e(TAG, "Failed to assign data to " + key + ": " + e.getMessage());
                }
            }
        }

    }

    //</editor-fold>

    //<editor-fold desc="Other Field Views Implementation Methods">
    @Override
    public void onOpenPicklistCommand(FPickList source) {

        PickListData data;

        try {
            JSONObject pickListDataJSON = new JSONObject(source.getFormFieldData().getRawJSONString());
            data = PicklistDataJSONParser.parseJSON(pickListDataJSON);
        } catch (JSONException e) {
            // TODO: add an error report here that can be sent to the administrator of the
            // formalistics installation

            FLLogger.e(TAG, e.getMessage());
            Toast.makeText(
                    DocumentActivity.this,
                    "Failed to open picklist, its search data might be corrupted or has an incorrect entry. Contact your administrator.",
                    Toast.LENGTH_LONG).show();
            return;
        }


        String rawFormulaConditionString = data.getCondition();
        String parsedConditionString = null;

        FLLogger.d(TAG, "rawFormulaConditionString: " + rawFormulaConditionString);

        if (rawFormulaConditionString != null && !"".equals(rawFormulaConditionString.trim())) {

            try {
                LinkedList<Token> tokenizedFormula = formulaLexer.lex(rawFormulaConditionString);

                DocumentHeaderData documentHeaderData = documentWorkerFragment.getDocumentHeaderData();
                JSONObject fieldValues = documentViewContentsManager.getFieldValues();

                FormulaEvaluator formulaEvaluator = new FormulaEvaluator(documentHeaderData, fieldValues, lookupRequestListener);
                ExpressionNode topExpressionNode = formulaEvaluator.evaluateForCondition(tokenizedFormula);

                parsedConditionString = topExpressionNode.getValue().toString();
            } catch (ParserException e) {
                FLLogger.e(TAG, "Failed to parse formula " + rawFormulaConditionString + ": " + e.getMessage());
                Toast.makeText(this, "Failed to parse formula " + rawFormulaConditionString + ", please contact your administrator", Toast.LENGTH_LONG).show();
            }

        }

        Intent intent = new Intent(DocumentActivity.this, PicklistPickerActivity.class);
        intent.putExtra(PicklistPickerActivity.EXTRA_FIELD_ID, source.getFieldName());
        intent.putExtra(PicklistPickerActivity.EXTRA_ACTIVE_USER, documentWorkerFragment.getActiveUser());
        intent.putExtra(PicklistPickerActivity.EXTRA_PICKLIST_SEARCH_AND_RESULT_DATA, data);
        intent.putExtra(PicklistPickerActivity.EXTRA_PARSED_CONDITION_STRING, parsedConditionString);
        startActivityForResult(intent, ActivityRequestCodes.PICK_LIST);

    }

    @Override
    public void onOpenPickerViewCommand(FDateTimePicker source, FDateTimePicker.PickerType pickerType, String currentFieldValue) {

        DateTimePickerType viewPickerType = null;
        int requestCode = 0;

        switch (pickerType) {
            case DATE: {
                viewPickerType = DateTimePickerType.DATE_ONLY;
                requestCode = ActivityRequestCodes.PICK_DATE;
            }
            break;
            case TIME: {
                viewPickerType = DateTimePickerType.TIME_ONLY;
                requestCode = ActivityRequestCodes.PICK_TIME;
            }
            break;
            case DATETIME: {
                viewPickerType = DateTimePickerType.DATE_TIME;
                requestCode = ActivityRequestCodes.PICK_DATE_TIME;
            }
            break;
        }

        if (viewPickerType != null && requestCode != 0) {

            Intent dateTimePicker = new Intent(DocumentActivity.this, DateTimePickerActivity.class);

            dateTimePicker.putExtra(DateTimePickerActivity.EXTRA_DATE_TIME_PICKER_FIELD_ID, source.getFieldName());
            dateTimePicker.putExtra(DateTimePickerActivity.EXTRA_DATE_TIME_PICKER_TYPE, viewPickerType);
            dateTimePicker.putExtra(DateTimePickerActivity.EXTRA_PRE_SELECTED_DATE, currentFieldValue);

            startActivityForResult(dateTimePicker, requestCode);
        }

    }

    @Override
    public void onScanCodeCommand(FCodeScannerField source) {
        activeBarcodeScannerFieldId = source.getFieldName();

        IntentIntegrator integrator = new IntentIntegrator(DocumentActivity.this);
        integrator.initiateScan();

    }

    //</editor-fold>

    //<editor-fold desc="Field Views - EmbeddedViewEventsListener implementation methods">
    @Override
    public void onCreateChildDocument(int formWebId, List<EmbeddedViewData.EmbeddedViewDataSendingItem> dataSendingItems, FEmbeddedView source) {

        User activeUser = documentWorkerFragment.getActiveUser();

        Form childDocumentForm = documentWorkerFragment.findForm(formWebId, activeUser.getCompany().getId());
        Document currentDocument = documentWorkerFragment.getCurrentDocument();

        Intent documentActivity = new Intent(DocumentActivity.this, DocumentActivity.class);

        documentActivity.putExtra(DocumentActivity.EXTRA_ACTIVE_USER, activeUser);
        documentActivity.putExtra(DocumentActivity.EXTRA_DOCUMENT_TYPE, DocumentType.CHILD_DOCUMENT);
        documentActivity.putExtra(DocumentActivity.EXTRA_FORM_ID, childDocumentForm.getId());
        documentActivity.putExtra(DocumentActivity.EXTRA_DOCUMENT_OR_OUTGOING_ACTION_ID, 0);
        documentActivity.putExtra(DocumentActivity.EXTRA_PARENT_DOCUMENT_ID, currentDocument.getId());

        if (dataSendingItems != null) {
            FLLogger.d(TAG, "data sending enabled");

            JSONObject dataSendingData = null;

            try {
                dataSendingData = new JSONObject();
                JSONObject documentFieldValues = documentViewContentsManager.getFieldValues();
                for (EmbeddedViewData.EmbeddedViewDataSendingItem dataSendingItem : dataSendingItems) {
                    String destination = dataSendingItem.getDestinationField();
                    String sourceFieldId = dataSendingItem.getSourceField();

                    if (documentFieldValues.has(sourceFieldId)) {
                        String value = documentFieldValues.getString(sourceFieldId);
                        dataSendingData.put(destination, value);
                    }

                }

                documentActivity.putExtra(DocumentActivity.EXTRA_DATA_SENDING_DATA, dataSendingData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        embeddedViewAwaitingChildDocumentSubmit = source;

        startActivityForResult(documentActivity, ActivityRequestCodes.OPEN_DOCUMENT);

    }

    @Override
    public void onSearchForEmbeddedViewRequested(String searchCompareToFieldValue, FEmbeddedView source) {

        EmbeddedViewData embeddedViewData = source.getEmbeddedViewData();
        try {
            List<JSONObject> listData = documentWorkerFragment
                    .searchDataForEmbeddedView(searchCompareToFieldValue, embeddedViewData);
            source.setData(listData);
        } catch (DataAccessObject.DataAccessObjectException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(
                    DocumentActivity.this,
                    "Failed to search data for embedded view " + embeddedViewData.getName() + ": " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }


    }

    @Override
    public void onOpenDocumentCommand(int formWebId, int documentId, FEmbeddedView source) {

        User activeUser = documentWorkerFragment.getActiveUser();
        Form form = documentWorkerFragment.findForm(formWebId, activeUser.getCompany().getId());

        Intent documentActivity = new Intent(DocumentActivity.this, DocumentActivity.class);

        documentActivity.putExtra(DocumentActivity.EXTRA_ACTIVE_USER, activeUser);
        documentActivity.putExtra(DocumentActivity.EXTRA_DOCUMENT_TYPE, DocumentType.EXISTING_DOCUMENT);
        documentActivity.putExtra(DocumentActivity.EXTRA_FORM_ID, form.getId());
        documentActivity.putExtra(DocumentActivity.EXTRA_DOCUMENT_OR_OUTGOING_ACTION_ID, documentId);

        embeddedViewAwaitingChildDocumentSubmit = source;

        startActivityForResult(documentActivity, ActivityRequestCodes.OPEN_DOCUMENT);

    }

    @Override
    public void onOpenOutgoingActionCommand(int formWebId, int outgoingActionId, FEmbeddedView source) {

        User activeUser = documentWorkerFragment.getActiveUser();
        Form form = documentWorkerFragment.findForm(formWebId, activeUser.getCompany().getId());

        Intent documentActivity = new Intent(DocumentActivity.this, DocumentActivity.class);

        documentActivity.putExtra(DocumentActivity.EXTRA_ACTIVE_USER, activeUser);
        documentActivity.putExtra(DocumentActivity.EXTRA_DOCUMENT_TYPE, DocumentType.OUTGOING_DOCUMENT);
        documentActivity.putExtra(DocumentActivity.EXTRA_FORM_ID, form.getId());
        documentActivity.putExtra(DocumentActivity.EXTRA_DOCUMENT_OR_OUTGOING_ACTION_ID, outgoingActionId);

        embeddedViewAwaitingChildDocumentSubmit = source;

        startActivityForResult(documentActivity, ActivityRequestCodes.OPEN_DOCUMENT);

    }

    //</editor-fold>

    //<editor-fold desc="Field Views - DynamicImageFieldActionListener implementation methods">
    @Override
    public void onBrowseForImageCommand(FDynamicImage source) {
        activeDynamicImageField = source;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ActivityRequestCodes.IMAGE_PICKER_REQUEST);
    }

    @Override
    public void onTakeNewImageCommand(FDynamicImage source) {
        activeDynamicImageField = source;

        String imageName = DateUtilities.getCurrentTimeStamp() + ".bmp";
        File imageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), imageName);

        activeDynamicImageField.setImageLocalPath(imageFile.getAbsolutePath());

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        startActivityForResult(cameraIntent, ActivityRequestCodes.CAMERA_REQUEST);
    }

    @Override
    public Bitmap onFindImageOnLocalStorageCommand(FDynamicImage source) {

        Bitmap bitmap = documentWorkerFragment.getBitmapFromDynamicImageField(source);
        if (bitmap != null) {
            source.setBitmap(bitmap);
        }

        return bitmap;
    }

    @Override
    public void onOpenImageCommand(FDynamicImage source) {

        String localFileLocation = null;
        boolean imageNeedsScaling = false;

        if (source.getValue() != null && !"".equals(source.getValue())) {
            FilesDAO filesDAO = new FilesDAO(DocumentActivity.this);

            try {
                new URL(source.getValue());
                FileInfo fileInfo = filesDAO.findFileInfoForRemoteURL(source.getValue());
                localFileLocation = fileInfo.getLocalPath();
            } catch (MalformedURLException e) {
                localFileLocation = source.getValue();
            }

        } else if (source.getImageLocalPath() != null && !"".equals(source.getImageLocalPath())) {
            localFileLocation = source.getImageLocalPath();
            imageNeedsScaling = true;
        }

        Intent intent = new Intent(DocumentActivity.this, ImageViewerActivity.class);
        intent.putExtra(ImageViewerActivity.EXTRA_IMAGE_LOCAL_PATH, localFileLocation);
        intent.putExtra(ImageViewerActivity.EXTRA_IMAGE_NEEDS_SCALING, imageNeedsScaling);
        startActivity(intent);

    }


    //</editor-fold>

    //<editor-fold desc="FieldComputationRequestListener Implementation Methods & formula related objects">
    @Override
    public String onRecomputeRequested(FField fieldToRecompute) {

        String computedValue = null;

        Formula formula = fieldToRecompute.getFormFieldData().getValueFormula();
        if (formula != null && formula.getFormulaType() == Formula.FormulaType.COMPUTED) {
            String message = null;
            try {
                LinkedList<Token> tokenizedFormula = formulaLexer.lex(formula.getRule());

                DocumentHeaderData documentHeaderData = documentWorkerFragment.getDocumentHeaderData();
                JSONObject fieldValues = documentViewContentsManager.getFieldValues();

                FormulaEvaluator formulaEvaluator = new FormulaEvaluator(documentHeaderData, fieldValues, lookupRequestListener);
                ExpressionNode topExpressionNode = formulaEvaluator.evaluate(tokenizedFormula);

                computedValue = topExpressionNode.getValue().toString();
                if (computedValue == null) {
                    computedValue = "";
                }
                FLLogger.d(TAG, "Formula evaluated: " + formula.getRule() + " = " + computedValue);
            } catch (ParserException e) {
                e.printStackTrace();
                message = e.getMessage();
            }

            if (message != null) {
                FLLogger.d(TAG, message);
            }
        }

        return computedValue;
    }

    private final LookupRequestListener lookupRequestListener = new LookupRequestListener() {
        @Override
        public String onLookupCommand(String formName, String returnFieldName, String compareToOtherFormFieldName, String compareToThisFormFieldValue) {

            String lookedUpValue = "";

            if (compareToThisFormFieldValue != null && !"".equals(compareToThisFormFieldValue)) {
                try {
                    lookedUpValue = documentWorkerFragment.lookupData(formName, returnFieldName, compareToOtherFormFieldName, compareToThisFormFieldValue);
                } catch (DocumentWorkerFragment.LookupFailedException e) {
                    Toast.makeText(DocumentActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            return lookedUpValue;
        }
    };
    //</editor-fold>

}
