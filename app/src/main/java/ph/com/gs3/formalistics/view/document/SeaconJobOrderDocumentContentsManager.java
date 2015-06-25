package ph.com.gs3.formalistics.view.document;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.json.JSONObject;

import java.util.List;

import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;
import ph.com.gs3.formalistics.model.values.business.form.FormViewContentData;

/**
 * Created by Ervinne Sodusta on 6/25/2015.
 */
public class SeaconJobOrderDocumentContentsManager extends DocumentDynamicViewContentsManager {

    public SeaconJobOrderDocumentContentsManager(Context context, DocumentDynamicFieldsChangeDependencyMapper.FieldComputationRequestListener fieldComputationRequestListener, User currentUser) {
        super(context, fieldComputationRequestListener, currentUser);
    }
}
