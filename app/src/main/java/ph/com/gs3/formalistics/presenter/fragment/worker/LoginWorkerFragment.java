package ph.com.gs3.formalistics.presenter.fragment.worker;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;

import ph.com.gs3.formalistics.model.api.UsersAPI;
import ph.com.gs3.formalistics.model.values.application.AsyncTaskResult;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.service.managers.SessionManager;

public class LoginWorkerFragment extends Fragment {

    public static final String TAG = LoginWorkerFragment.class.getSimpleName();

    private LoginWorkerEventListener eventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        eventListener = (LoginWorkerEventListener) activity;

    }

    public void login(String server, String email, String password) {
        LoginTask loginTask = new LoginTask();
        loginTask.execute(server, email, password, eventListener);
    }

    private static class LoginTask extends AsyncTask<Object, Void, AsyncTaskResult<User, UsersAPI.LoginException>> {

        private LoginWorkerEventListener eventListener;

        @Override
        protected AsyncTaskResult<User, UsersAPI.LoginException> doInBackground(Object... params) {

            String server = params[0].toString();
            String email = params[1].toString();
            String password = params[2].toString();

            eventListener = (LoginWorkerEventListener) params[3];

            AsyncTaskResult<User, UsersAPI.LoginException> result = new AsyncTaskResult<>();
            result.setOperationSuccessful(false);

            try {
                User loggedInUser = SessionManager.getApplicationInstance().login(server, email, password);
                result.setResult(loggedInUser);
                result.setOperationSuccessful(true);
            } catch (UsersAPI.LoginException e) {
                result.setException(e);
            }

            return result;
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<User, UsersAPI.LoginException> result) {
            super.onPostExecute(result);

            if (result.isOperationSuccessful()) {
                eventListener.onLoginSuccess(result.getResult());
            } else {
                eventListener.onLoginFailed(result.getException());
            }

        }

    }

    public static interface LoginWorkerEventListener {

        public void onLoginSuccess(User loggedInUser);

        public void onLoginFailed(UsersAPI.LoginException loginException);

    }

}
