package ph.com.gs3.formalistics.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.List;

import ph.com.gs3.formalistics.global.interfaces.CallbackCommand;
import ph.com.gs3.formalistics.model.values.business.form.Form;

public class FormSelectionDialogFragment extends DialogFragment {

    public static final String TAG = FormSelectionDialogFragment.class.getSimpleName();

    public enum FormsAvailability {
        CURRENTLY_UPDATING, NO_FORMS_TO_SHOW, HAS_FORMS
    }

    private List<Form> formSelection;
    private FormsAvailability formsAvailability;

    private CallbackCommand<Form> formSelectedCallback;

    public FormSelectionDialogFragment() {
        formsAvailability = FormsAvailability.NO_FORMS_TO_SHOW;
    }

    public void setForms(List<Form> formSelection) {
        this.formSelection = formSelection;
    }

    public void setFormsAvailability(FormsAvailability formsAvailability) {
        this.formsAvailability = formsAvailability;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        switch (formsAvailability) {
            case HAS_FORMS:
                return createFormListDialog();
            case NO_FORMS_TO_SHOW:
                return createNoAvailableFormsDialog();
            case CURRENTLY_UPDATING:
                return createFormListUpdatingDialog();
            default:
                return null;
        }
    }

    private AlertDialog createNoAvailableFormsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Sorry").setMessage("No available forms yet.")
                .setNegativeButton("OK", null);

        return builder.create();

    }

    private AlertDialog createFormListDialog() {

        AlertDialog.Builder builder = createFormListDialogBuilder();

        return builder.create();

    }

    private AlertDialog createFormListUpdatingDialog() {

        AlertDialog.Builder builder;

        if (formSelection.size() >= 1) {
            builder = createFormListDialogBuilder();
        } else {
            builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Please wait...").setMessage("Forms are updating...")
                    .setNegativeButton("OK", null);
        }

        return builder.create();

    }

    private AlertDialog.Builder createFormListDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] formNames = new String[formSelection.size()];

        for (int i = 0; i < formSelection.size(); i++) {
            formNames[i] = formSelection.get(i).getName();
        }

        builder.setTitle("Create using which form?").setItems(formNames,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (formSelectedCallback != null) {
                            formSelectedCallback.execute(formSelection.get(which));
                        }

                    }
                });
        return builder;

    }

    public void setOnFormSelectedCallback(CallbackCommand<Form> formSelectedCallback) {
        this.formSelectedCallback = formSelectedCallback;
    }

}
