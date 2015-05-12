package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.service.managers.ImageManager;

public class UserProfileViewFragment extends Fragment {

    public static final String TAG = UserProfileViewFragment.class.getSimpleName();

    private UserProfileViewActionListener actionListener;

    private ImageView ivAvatar;

    private TextView tvUserName;
    private TextView tvCompanyName;
    private TextView tvCompanyServer;

    private Button bLogout;

    private ImageManager imageManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        actionListener = (UserProfileViewActionListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        imageManager = ImageManager.getDefaultInstance(getActivity());

        ivAvatar = (ImageView) rootView.findViewById(R.id.UserProfile_ivAvatar);

        tvUserName = (TextView) rootView.findViewById(R.id.UserProfile_tvUserName);
        tvCompanyName = (TextView) rootView.findViewById(R.id.UserProfile_tvCompanyName);
        tvCompanyServer = (TextView) rootView.findViewById(R.id.UserProfile_tvCompanyServer);

        bLogout = (Button) rootView.findViewById(R.id.UserProfile_bLogout);
        bLogout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                actionListener.onLogoutCommand();
            }
        });

        actionListener.onViewReady();

        return rootView;
    }

    public void displayUser(User user) {

        imageManager.requestUserImage(user.getId(), ivAvatar);

        tvUserName.setText(user.getDisplayName());
        tvCompanyName.setText(user.getCompany().getName());
        tvCompanyServer.setText(user.getCompany().getServer());

    }

    public interface UserProfileViewActionListener {

        void onViewReady();

        void onLogoutCommand();

    }

}
