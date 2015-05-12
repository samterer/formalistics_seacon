package ph.com.gs3.formalistics.global.utilities;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.gs3.formalistics.global.constants.ProcessorType;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;

/**
 * Created by Ervinne on 4/18/2015.
 */
public class DocumentUtilities {

    public static final String TAG = DocumentUtilities.class.getSimpleName();

    public static boolean isProcessor(
            int processorType,
            String processor,
            int documentAuthorId,
            String documentJSONValuesString,
            User user) {

        boolean isProcessor = false;

        switch (processorType) {
            case ProcessorType.DEPARTMENT_POSITION:
                // TODO: unsupported
                isProcessor = false;
                break;
            case ProcessorType.COMPANY_POSITION:
                try {
                    if (user == null) {
                        FLLogger.e(TAG, "user is null");
                    }

                    if (processor == null) {
                        FLLogger.e(TAG, "processor is null");
                    }

                    isProcessor = Integer.parseInt(processor) == user.getPositionId();
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Failed to determine the processor of the document, " +
                            "the processor field is not a valid integer.");
                }
                break;
            case ProcessorType.PERSON:
                try {
                    isProcessor = Integer.parseInt(processor) == user.getWebId();
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Failed to determine the processor of the document, " +
                            "the processor field is not a valid integer. ");
                }
                break;
            case ProcessorType.AUTHOR:
                isProcessor = documentAuthorId == user.getId();
                break;
            case ProcessorType.FIELD:
                try {
                    JSONObject fieldValues = new JSONObject(documentJSONValuesString);
                    int processorId = fieldValues.getInt(processor);
                    isProcessor = processorId == user.getWebId();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w(TAG, "Failed to determine the processor of the document. " +
                            "The application encountered a problem while parsing the field values: " + e.getMessage());
                }
                break;
        }

        return isProcessor;

    }

    public static boolean isProcessor(Document document, User user) {

        return isProcessor(
                document.getProcessorType(),
                document.getProcessor(),
                document.getAuthorId(),
                document.getFieldValuesJSONString(),
                user
        );

    }

    public static boolean isProcessor(DocumentSummary documentSummary, User user) {

        return isProcessor(
                documentSummary.getProcessorType(),
                documentSummary.getProcessor(),
                documentSummary.getAuthorId(),
                documentSummary.getFieldValuesJSON().toString(),
                user
        );

    }

}
