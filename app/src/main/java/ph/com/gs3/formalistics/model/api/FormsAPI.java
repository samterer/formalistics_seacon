package ph.com.gs3.formalistics.model.api;

import java.util.List;

import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.application.UnparseableObject;
import ph.com.gs3.formalistics.model.values.business.form.Form;

/**
 * Created by Ervinne on 4/11/2015.
 */
public interface FormsAPI {

    public List<Form> getForms(String fromDate) throws
            APIResponse.InvalidResponseException,
            HttpCommunicator.CommunicationException;

    public List<UnparseableObject> getUnparseableForms();

}
