package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutViewFragment extends Fragment {

    private TextView tvVersionName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        tvVersionName = (TextView) rootView.findViewById(R.id.About_tvVersionName);

        tvVersionName.setText(FormalisticsApplication.versionSettings.versionName);

        return rootView;
    }
}
