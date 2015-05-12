package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ph.com.gs3.formalistics.R;

/**
 * Created by Ervinne on 4/21/2015.
 */
public class TesterViewFragment extends Fragment {

    public static final String TAG = TesterViewFragment.class.getSimpleName();

    private EditText etFormula;
    private EditText etFormula2;
    private EditText etFormula3;
    private Button bTestFormula;
    private Button bTestFormula2;
    private Button bTestFormula3;

    private TesterViewFragmentActionListener actionListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        actionListener = (TesterViewFragmentActionListener) activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tester, container, false);

        etFormula = (EditText) rootView.findViewById(R.id.Tester_etFormula);
        etFormula2 = (EditText) rootView.findViewById(R.id.Tester_etFormula2);
        etFormula3 = (EditText) rootView.findViewById(R.id.Tester_etFormula3);

        bTestFormula = (Button) rootView.findViewById(R.id.Tester_bTestFormula);
        bTestFormula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onTestFormulaCommand(etFormula.getText().toString());
            }
        });

        bTestFormula2 = (Button) rootView.findViewById(R.id.Tester_bTestFormula2);
        bTestFormula2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onTestFormula2Command(etFormula2.getText().toString());
            }
        });

        bTestFormula3 = (Button) rootView.findViewById(R.id.Tester_bTestFormula3);
        bTestFormula3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionListener.onTestFormula3Command(etFormula3.getText().toString());
            }
        });

        // default formula:
        etFormula.setText("@Status == \"Some Status\" && @field_3 == \"string value\"");
//        etFormula2.setText("3-2+4*@field_4 + @GivenIf(@Status == \"test\", 5, 6)");
        etFormula2.setText("@StrConcat('test', @StrConcat('hello ', 'world, ', \"I'm \", 'Ervinne: ', @TrackingNumber))");
        etFormula3.setText("@Lookup('Shipping Line Maintenance','shippingline','number',@CRFrom)");

        return rootView;
    }

    public interface TesterViewFragmentActionListener {

        void onTestFormulaCommand(String formula);

        void onTestFormula2Command(String formula);

        void onTestFormula3Command(String formula);

    }

}
