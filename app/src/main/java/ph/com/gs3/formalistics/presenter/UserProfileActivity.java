package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ActivityResultCodes;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.presenter.fragment.view.UserProfileViewFragment;
import ph.com.gs3.formalistics.presenter.fragment.view.UserProfileViewFragment.UserProfileViewActionListener;

public class UserProfileActivity extends Activity implements UserProfileViewActionListener {

    public static final String TAG = UserProfileActivity.class.getSimpleName();

    public static final String EXTRA_ACTIVE_USER = "active_user";
    public static final String EXTRA_LOGOUT_FLAG = "logout_flag";

    public static final int FLAG_NO_LOGOUT = 0;
    public static final int FLAG_DID_LOGOUT = 1;

    private User activeUser;
    private UserProfileViewFragment userProfileViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initializeStateTransferredFields();

        if (savedInstanceState == null) {
            userProfileViewFragment = new UserProfileViewFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, userProfileViewFragment, UserProfileViewFragment.TAG)
                    .commit();
        } else {
            userProfileViewFragment = (UserProfileViewFragment) getFragmentManager()
                    .findFragmentByTag(UserProfileViewFragment.TAG);
        }
    }

    private void initializeStateTransferredFields() {
        Bundle extras = getIntent().getExtras();
        RuntimeException noUserException = new RuntimeException("The current user must be passed when starting " + TAG);

        try {
            activeUser = (User) extras.getSerializable(EXTRA_ACTIVE_USER);
        } catch (Exception e) {
            noUserException.initCause(e);
            throw noUserException;
        }

        if (activeUser == null) {
            throw noUserException;
        }

    }

    @Override
    public void onViewReady() {
        userProfileViewFragment.displayUser(activeUser);
    }

    @Override
    public void onLogoutCommand() {
        Intent responseData = new Intent();
        responseData.putExtra(EXTRA_LOGOUT_FLAG, FLAG_DID_LOGOUT);
        setResult(ActivityResultCodes.LOGOUT_REQUESTED, responseData);
        finish();
    }

}
