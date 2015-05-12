package ph.com.gs3.formalistics.model.dao.facade;

import android.content.Context;

import java.util.List;

import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DataAccessObject.DataAccessObjectException;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.WorkflowObjectsDAO;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowObject;

/**
 * Created by Ervinne on 4/18/2015.
 */
public class FormsDataReaderFacade {

    private final Context context;

    private final FormsDAO formsDAO;
    private final WorkflowObjectsDAO workflowObjectsDAO;

    public FormsDataReaderFacade(Context context) {
        this.context = context;

        formsDAO = new FormsDAO(context);
        workflowObjectsDAO = new WorkflowObjectsDAO(context);
    }

    public Form getForm(int formId) throws DataAccessObjectException {

        Form form = formsDAO.getForm(formId);
        if (form == null) {
            throw new DataAccessObjectException("Form not found");
        }
        List<WorkflowObject> workflowObjects = workflowObjectsDAO.getFormWorkflowObjects(form.getWorkflowId());

        form.setWorkflowObjects(workflowObjects);

        return form;

    }

    public Form getForm(int webId, int companyId) throws DataAccessObject.DataAccessObjectException {

        Form form = formsDAO.getForm(webId, companyId);
        if (form == null) {
            throw new DataAccessObjectException("Form not found");
        }
        List<WorkflowObject> workflowObjects = workflowObjectsDAO.getFormWorkflowObjects(form.getWorkflowId());

        form.setWorkflowObjects(workflowObjects);

        return form;

    }

}
