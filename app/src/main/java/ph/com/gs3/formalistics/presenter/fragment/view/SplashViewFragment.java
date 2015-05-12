package ph.com.gs3.formalistics.presenter.fragment.view;

import ph.com.gs3.formalistics.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashViewFragment extends Fragment {

	public static final String TAG = SplashViewFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_splash, container, false);
		return rootView;
	}

}
