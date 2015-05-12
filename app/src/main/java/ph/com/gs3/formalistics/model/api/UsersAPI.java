package ph.com.gs3.formalistics.model.api;

import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.business.User;

/**
 * Created by Ervinne on 4/7/2015.
 */
public interface UsersAPI {

    User login(String email, String password) throws LoginException;

    // <editor-fold desc="Constants & Exceptions" >

    enum LoginField {
        SERVER, EMAIL, PASSWORD
    }

    class LoginException extends Exception {

        public static final String SERVER_ERROR_USER_NOT_FOUND = "User Not Found";

        private LoginField[] affectedFields;
        private APIResponse response;

        public LoginException(APIResponse response) {
            super(response.getErrorMessage());
        }

        public LoginException(String message, Throwable t) {
            super(message, t);
        }

        public LoginException(String message, LoginField[] affectedFields) {
            super(message);
            setAffectedFields(affectedFields);
        }

        public LoginField[] getAffectedFields() {
            return this.affectedFields;
        }

        public void setAffectedFields(LoginField[] affectedFields) {
            this.affectedFields = affectedFields;
        }

        public APIResponse getAPIResponse() {
            return this.response;
        }

    }

    // </editor-fold>

}
