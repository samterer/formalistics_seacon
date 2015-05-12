package ph.com.gs3.formalistics.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.interfaces.CallbackCommand;

public class ListSelectionDialogFragment extends DialogFragment {

    public static final String TAG = ListSelectionDialogFragment.class.getSimpleName();

    private CallbackCommand<String> onItemSelectedCallback;
    public List<String> selection;

    private String header;
    private String emptyListMessage;

    public static ListSelectionDialogFragment createInstance(String header, String emptyListMessage) {

        ListSelectionDialogFragment instance = new ListSelectionDialogFragment();

        instance.selection = new ArrayList<>();

        instance.header = header;
        instance.emptyListMessage = emptyListMessage;

        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder;

        if (selection == null || selection.isEmpty()) {
            builder = createEmptyActionsDialog();
        } else {
            builder = createActionsDialog();
        }

        return builder.create();

    }

    private AlertDialog.Builder createEmptyActionsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Sorry").setMessage(emptyListMessage).setNegativeButton("OK", null);

        return builder;

    }

    private AlertDialog.Builder createActionsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] actionsString = selection.toArray(new String[selection.size()]);
        builder.setTitle(header).setItems(actionsString, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onItemSelectedCallback != null) {
                    onItemSelectedCallback.execute(selection.get(which));
                }
            }
        });

        return builder;

    }

    // ===================================================================================
    // {{ Getters & Setters

    public List<String> getSelection() {
        return selection;
    }

    public CallbackCommand<String> getOnItemSelectedCallback() {
        return onItemSelectedCallback;
    }

    public void setOnItemSelectedCallback(CallbackCommand<String> onItemSelectedCallback) {
        this.onItemSelectedCallback = onItemSelectedCallback;
    }

    public void setSelection(List<String> selection) {
        this.selection = selection;
    }
    // }}
}
