package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ApplicationMode;
import ph.com.gs3.formalistics.model.api.UsersAPI;

public class LoginViewFragment extends Fragment {

    public static final String TAG = LoginViewFragment.class.getSimpleName();
    private LoginViewActionListener actionListener;

    private TextView tvMessage;

    private EditText etServer;
    private EditText etEmail;
    private EditText etPassword;

    private Button bLogin;

    private boolean isViewReady = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actionListener = (LoginViewActionListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        tvMessage = (TextView) rootView.findViewById(R.id.Login_tvMessage);
        tvMessage.setVisibility(View.GONE);

        etServer = (EditText) rootView.findViewById(R.id.Login_etServer);
        etEmail = (EditText) rootView.findViewById(R.id.Login_etEmail);
        etPassword = (EditText) rootView.findViewById(R.id.Login_etPassword);

        bLogin = (Button) rootView.findViewById(R.id.Login_bLogin);
        bLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String server = etServer.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                actionListener.onLoginCommand(server, email, password);
            }
        });

        if (FormalisticsApplication.APPLICATION_MODE == ApplicationMode.DEVELOPMENT) {
            etEmail.setText("gater@gmail.com");
//            etEmail.setText("charnie.capulong@gs3.com.ph");
//            etEmail.setText("formalisticsad@gmail.com");
//            etEmail.setText("cez.crisostomo@gs3.com.ph");
//            etEmail.setText("kei@test.com");
            etPassword.setText("password");
        }

        actionListener.onViewReady();
        isViewReady = true;

        return rootView;
    }

    public void setErrorMessage(String errorMessage, UsersAPI.LoginField loginField) {

        switch (loginField) {
            case SERVER:
                etServer.setError(Html.fromHtml("<font color='red'>" + errorMessage + "</font>"));
                break;
            case EMAIL:
                etEmail.setError(Html.fromHtml("<font color='red'>" + errorMessage + "</font>"));
                break;
            case PASSWORD:
                etPassword.setError(Html.fromHtml("<font color='red'>" + errorMessage + "</font>"));
                break;
            default:
                setErrorMessage(errorMessage);
        }

    }

    public void setServer(String server) {
        etServer.setText(server);
    }

    public void setErrorMessage(String errorMessage) {
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setTextColor(Color.RED);
        tvMessage.setText(errorMessage);
    }

    public boolean isViewReady() {
        return isViewReady;
    }

    public interface LoginViewActionListener {

        void onViewReady();

        void onLoginCommand(String server, String email, String password);

    }

}
