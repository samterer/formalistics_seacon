package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.SessionMode;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.UsersAPI;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.presenter.fragment.view.LoginViewFragment;
import ph.com.gs3.formalistics.presenter.fragment.view.LoginViewFragment.LoginViewActionListener;
import ph.com.gs3.formalistics.presenter.fragment.view.SplashViewFragment;
import ph.com.gs3.formalistics.presenter.fragment.worker.LoginWorkerFragment;
import ph.com.gs3.formalistics.presenter.fragment.worker.LoginWorkerFragment.LoginWorkerEventListener;
import ph.com.gs3.formalistics.service.managers.SessionManager;

public class LoginActivity extends Activity implements LoginViewActionListener,
        LoginWorkerEventListener {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private static final String PREF_LAST_SERVER_USED = "last_server_used";

    private LoginViewFragment loginViewFragment;
    private SplashViewFragment splashViewFragment;

    private LoginWorkerFragment loginWorkerFragment;

    private enum LoginView {
        LOGIN, SPLASH
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeWorkerFragment();
        initializeViewFragments();

        if (savedInstanceState == null) {

            User activeUser = SessionManager.getApplicationInstance().getActiveUser();
            if (activeUser == null) {
                changeView(LoginView.LOGIN);
            } else {
                navigateToDocumentListActivity(activeUser);
            }

        }

    }

    private void initializeWorkerFragment() {

        FragmentManager fragmentMan = getFragmentManager();
        loginWorkerFragment = (LoginWorkerFragment) fragmentMan.findFragmentByTag(LoginWorkerFragment.TAG);

        if (loginWorkerFragment == null) {
            loginWorkerFragment = new LoginWorkerFragment();

            FragmentTransaction fragmentTransaction = fragmentMan.beginTransaction();
            fragmentTransaction.add(loginWorkerFragment, LoginWorkerFragment.TAG);
            fragmentTransaction.commit();
            fragmentMan.executePendingTransactions();
        }

    }

    private void initializeViewFragments() {
        FragmentManager fragmentMan = getFragmentManager();

        loginViewFragment = (LoginViewFragment) fragmentMan.findFragmentByTag(LoginViewFragment.TAG);
        splashViewFragment = (SplashViewFragment) fragmentMan.findFragmentByTag(SplashViewFragment.TAG);

        FragmentTransaction fragmentTransaction = null;

        if (loginViewFragment == null || splashViewFragment == null) {
            fragmentTransaction = fragmentMan.beginTransaction();

            loginViewFragment = new LoginViewFragment();
            splashViewFragment = new SplashViewFragment();

            fragmentTransaction.add(R.id.container, loginViewFragment, LoginViewFragment.TAG);
            fragmentTransaction.add(R.id.container, splashViewFragment, SplashViewFragment.TAG);

            fragmentTransaction.hide(loginViewFragment);
            fragmentTransaction.show(splashViewFragment);

            fragmentTransaction.commit();

        }

    }

    private void navigateToDocumentListActivity(User loggedInUser) {
        Intent intent = new Intent(LoginActivity.this, DocumentListActivity.class);
        intent.putExtra(DocumentListActivity.EXTRA_ACTIVE_USER, loggedInUser);
        startActivity(intent);
        finish();
    }

    private void changeView(LoginView loginView) {

        FragmentManager fragmentMan = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentMan.beginTransaction();

        if (loginView == LoginView.LOGIN) {
            FLLogger.d(TAG, "Changing view to login");
            fragmentTransaction.hide(splashViewFragment);
            fragmentTransaction.show(loginViewFragment);
        } else if (loginView == LoginView.SPLASH) {
            FLLogger.d(TAG, "Changing view to splash");
            fragmentTransaction.hide(loginViewFragment);
            fragmentTransaction.show(splashViewFragment);
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onViewReady() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String lastServerUsed = sp.getString(PREF_LAST_SERVER_USED, "");
        loginViewFragment.setServer(lastServerUsed);
    }

    @Override
    public void onLoginCommand(String server, String email, String password) {
        changeView(LoginView.SPLASH);
        loginWorkerFragment.login(server, email, password);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spEditor = sp.edit();

        spEditor.putString(PREF_LAST_SERVER_USED, server);
        spEditor.commit();

    }

    @Override
    public void onLoginSuccess(User loggedInUser) {
        navigateToDocumentListActivity(loggedInUser);
    }

    @Override
    public void onLoginFailed(UsersAPI.LoginException loginException) {

        if (SessionManager.getApplicationInstance().getSessionMode() == SessionMode.ONLINE) {
            changeView(LoginView.LOGIN);

            UsersAPI.LoginField[] affectedFields = loginException.getAffectedFields();

            if (affectedFields != null && affectedFields.length > 0) {
                for (UsersAPI.LoginField affectedField : affectedFields) {
                    loginViewFragment.setErrorMessage(loginException.getMessage(), affectedField);
                }
            } else {
                loginViewFragment.setErrorMessage(loginException.getMessage());
            }
        } else {

            User lastActiveUser = SessionManager.getApplicationInstance().getActiveUser();
            if (lastActiveUser != null) {
                //  Continue using the last active user in offline mode
                onLoginSuccess(lastActiveUser);
            } else {
                // Require login
                changeView(LoginView.LOGIN);
                loginViewFragment.setErrorMessage(loginException.getMessage());
            }
        }

    }

}
