package ph.com.gs3.formalistics.model.dao.facade;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DynamicFormFieldsDAO;
import ph.com.gs3.formalistics.model.dao.FormTableReferenceDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.WorkflowObjectsDAO;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowObject;

public class FormsDataWriterFacade {

    protected final FormTableReferenceDAO formTableReferenceDAO;
    protected final DynamicFormFieldsDAO dynamicFormFieldsDAO;
    protected final FormsDAO formsDAO;
    protected final WorkflowObjectsDAO workflowObjectsDAO;

    public FormsDataWriterFacade(Context context) {
        formTableReferenceDAO = new FormTableReferenceDAO(context);
        dynamicFormFieldsDAO = new DynamicFormFieldsDAO(context);
        formsDAO = new FormsDAO(context);
        workflowObjectsDAO = new WorkflowObjectsDAO(context);
    }

    public Form saveForm(Form form) {
        Form updatedOrSavedForm = null;

        formsDAO.open();

        try {
            // Throws JSONException, InvalidFormFieldException
            Form oldForm = formsDAO.getForm(form.getWebId(), form.getCompany().getId());

            if (oldForm != null) { // This form is already existing, update it
                // Throws JSONException, InvalidFormFieldException
                updatedOrSavedForm = formsDAO.updateForm(form);
                dynamicFormFieldsDAO.updateFormTable(updatedOrSavedForm);

                // only save workflow objects if the workflow changed
                if (oldForm.getWorkflowId() != updatedOrSavedForm.getWorkflowId()) {
                    for (WorkflowObject workflowObject : form.getWorkflowObjects()) {
                        workflowObjectsDAO.insertWorkflowObject(workflowObject);
                    }
                }
            } else {
                int formDbId = formsDAO.insertForm(form);

                // Throws JSONException, InvalidFormFieldException
                updatedOrSavedForm = formsDAO.getForm(formDbId);
                dynamicFormFieldsDAO.createFormTable(updatedOrSavedForm);

                // Save a reference to the table
                try {
                    formTableReferenceDAO.open();
                    formTableReferenceDAO.saveTableName(formDbId,
                            updatedOrSavedForm.getGeneratedFormTableName());
                } catch (SQLiteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    formTableReferenceDAO.close();
                }

                // save the workflow of the table
                for (WorkflowObject workflowObject : form.getWorkflowObjects()) {
                    workflowObjectsDAO.insertWorkflowObject(workflowObject);
                }
            }
        } catch (DataAccessObject.DataAccessObjectException e) {
            e.printStackTrace();
        } finally {
            formsDAO.close();
        }
        return updatedOrSavedForm;

    }
}
