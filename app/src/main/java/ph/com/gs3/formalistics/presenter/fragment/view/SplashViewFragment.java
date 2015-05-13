package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ph.com.gs3.formalistics.R;

public class SplashViewFragment extends Fragment {

	public static final String TAG = SplashViewFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_splash, container, false);
	}

}
