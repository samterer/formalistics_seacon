package ph.com.gs3.formalistics.view.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.com.gs3.formalistics.global.utilities.Serializer;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.form.Formula;
import ph.com.gs3.formalistics.model.values.business.form.content.EmbeddedViewData;
import ph.com.gs3.formalistics.service.formula.FormulaLexer;
import ph.com.gs3.formalistics.view.document.contents.FField;
import ph.com.gs3.formalistics.view.document.contents.FField.AbstractFieldChangeListener;
import ph.com.gs3.formalistics.view.document.contents.FView;
import ph.com.gs3.formalistics.view.document.contents.FViewCollection;
import ph.com.gs3.formalistics.view.document.contents.views.FEmbeddedView;
import ph.com.gs3.formalistics.view.document.contents.views.FEmbeddedView.EmbeddedViewEventsListener;

/**
 * Created by Ervinne on 4/20/2015.
 */
public class DocumentDynamicFieldsChangeDependencyMapper {

    public static final String TAG = DocumentDynamicFieldsChangeDependencyMapper.class.getSimpleName();

    private Map<String, List<String>> fieldDependencyMap;
    private FViewCollection viewCollection;

    private final FieldComputationRequestListener fieldComputationRequestListener;

    public enum ChangeTriggerType {
        VALUE, VISIBILITY
    }

    public DocumentDynamicFieldsChangeDependencyMapper(FieldComputationRequestListener fieldComputationRequestListener) {
        this.fieldComputationRequestListener = fieldComputationRequestListener;
    }

    public void mapEmbeddedViewsChangeDependencies(FViewCollection viewCollection, final EmbeddedViewEventsListener embeddedViewEventsListener) {

        for (FView fieldView : viewCollection) {

            if (fieldView instanceof FEmbeddedView) {
                final FEmbeddedView embeddedView = (FEmbeddedView) fieldView;
                EmbeddedViewData embeddedViewData = embeddedView.getEmbeddedViewData();
                String changeTriggerFieldViewId = embeddedViewData
                        .getSearchCompareToThisDocumentFieldId();

                final FField changeTriggerFieldView = viewCollection.findFieldView(changeTriggerFieldViewId);

                if (changeTriggerFieldView != null) {
                    changeTriggerFieldView.addOnChangeListener(new AbstractFieldChangeListener() {

                        @Override
                        public void onChange(FField source, String newValue) {
                            FLLogger.d(TAG, changeTriggerFieldView.getFieldName() + ": " + newValue);
                            embeddedViewEventsListener.onSearchForEmbeddedViewRequested(newValue, embeddedView);
                        }
                    });
                } else {
                    FLLogger.e(TAG, "Unable to bind embedded view event to "
                            + changeTriggerFieldViewId + ", field not found.");
                }

            }

        }

    }

    public void mapFieldsChangeDependencies(FViewCollection viewCollection) {
        fieldDependencyMap = new HashMap<>();
        this.viewCollection = viewCollection;

        FormulaLexer lexer = new FormulaLexer();

        for (FView view : viewCollection) {
            if (!(view instanceof FField)) {
                continue;
            }

            final FField field = (FField) view;

            Formula valueFormula = field.getFormFieldData().getValueFormula();
            Formula visibilityFormula = field.getFormFieldData().getVisibilityFormula();

            FLLogger.d(TAG, field.getFieldName());

            if (valueFormula != null) {
                FLLogger.d(TAG, field.getFieldName() + "'s " + valueFormula.getFormulaType().name() + " formula = " + valueFormula.getRule());

                if (valueFormula.getFormulaType() == Formula.FormulaType.STATIC) {
                    // just set this formula as the value of the field
                    if (!"".equals(valueFormula.getRule().trim())) {
                        field.setValue(valueFormula.getRule());
                    }
                } else if (valueFormula.getFormulaType() == Formula.FormulaType.COMPUTED) {

                    List<String> fieldNames = lexer.lexVariables(valueFormula.getRule());

                    // map field dependencies
                    for (String fieldName : fieldNames) {
                        if (!fieldDependencyMap.containsKey(fieldName)) {
                            fieldDependencyMap.put(fieldName, new ArrayList<String>());
                            FField fieldDependentUpon = viewCollection.findFieldView(fieldName);
                            if (fieldDependentUpon != null) {
                                fieldDependentUpon.addOnChangeListener(new AbstractFieldChangeListener() {
                                    @Override
                                    public void onChange(FField source, String newValue) {
                                        triggerOnChangeForDependentFields(source, newValue, null, ChangeTriggerType.VALUE);
                                    }
                                });
                            }
                        }

                        fieldDependencyMap.get(fieldName).add(field.getFieldName());
                    }

                    FLLogger.d(TAG, "The field " + field.getFieldName() + " is dependent to " + Serializer.serializeList(fieldNames));

                }
            }

            if (visibilityFormula != null) {

                List<String> fieldNames = lexer.lexVariables(visibilityFormula.getRule());

                // map field dependencies
                for (String fieldName : fieldNames) {

                    boolean fieldFound = false;

                    if (!fieldDependencyMap.containsKey(fieldName)) {
                        fieldDependencyMap.put(fieldName, new ArrayList<String>());
                        FField fieldDependentUpon = viewCollection.findFieldView(fieldName);
                        if (fieldDependentUpon != null) {
                            fieldFound = true;
                            fieldDependentUpon.addOnChangeListener(new AbstractFieldChangeListener() {
                                @Override
                                public void onChange(FField source, String newValue) {
                                    triggerOnChangeForDependentFields(source, newValue, null, ChangeTriggerType.VISIBILITY);
                                }
                            });
                        }
                    }

                    if (!fieldFound) {
                        FLLogger.w(TAG,
                                String.format("Failed to bind visibility formula dependency on %s. Field not found.", fieldName));
                    }

                    fieldDependencyMap.get(fieldName).add(field.getFieldName());
                }

                FLLogger.d(TAG, "The field " + field.getFieldName() + "'s visbility is dependent to " + Serializer.serializeList(fieldNames));
            }

        }

        // Compute values of each field with formula:
        for (FView view : viewCollection) {
            if (!(view instanceof FField)) {
                continue;
            }

            final FField field = (FField) view;

            Formula valueFormula = field.getFormFieldData().getValueFormula();
            Formula visiblityFormula = field.getFormFieldData().getVisibilityFormula();

            FLLogger.d(TAG, field.getFieldName());

            if (valueFormula != null) {
                requestFieldComputation(field, ChangeTriggerType.VALUE);
            }

            if (visiblityFormula != null) {
                requestFieldComputation(field, ChangeTriggerType.VISIBILITY);
            }

        }
    }

    private synchronized void triggerOnChangeForDependentFields(FField source, String newValue, List<String> alreadyTriggeredFields, ChangeTriggerType changeTriggerType) {

        FLLogger.d(TAG, source.getOldValue() + " != " + newValue);

        // if there was really a change
//        if (source.getOldValue() != newValue) {

        List<String> triggeredFields = new ArrayList<>();

        if (alreadyTriggeredFields != null) {
            triggeredFields.addAll(alreadyTriggeredFields);
        }

        List<String> dependentFields = fieldDependencyMap.get(source.getFieldName());
        if (dependentFields != null) {
            for (String dependentField : dependentFields) {
                // Prevent circular onChange trigger
                if (!triggeredFields.contains(dependentField)) {
                    FLLogger.d(TAG, dependentField + "'s value will be changed");

                    FField fieldToRecompute = viewCollection.findFieldView(dependentField);

                    if (fieldToRecompute != null) {
                        triggeredFields.add(dependentField);
                        String computedValue = requestFieldComputation(fieldToRecompute, changeTriggerType);
                        // set value and notify listeners about change if the field is not yet triggered,
                        // use recursion to manually trigger change
                        if (!triggeredFields.contains(fieldToRecompute.getFieldName())) {
                            triggerOnChangeForDependentFields(fieldToRecompute, computedValue, triggeredFields, changeTriggerType);
                        }
                    }

                } else {
                    FLLogger.d(TAG, "List of triggered fields cleared");
                    triggeredFields.clear();
                }
            }
        }
//        } else {
//            FLLogger.d(TAG, "No change detected");
//        }
    }

    private synchronized String requestFieldComputation(FField fieldToRecompute, ChangeTriggerType changeTriggerType) {
        String computedValue = fieldComputationRequestListener.onRecomputeRequested(fieldToRecompute, changeTriggerType);
        FLLogger.d(TAG, "Computed value: " + computedValue);

        if (changeTriggerType == ChangeTriggerType.VALUE) {
            // fieldToRecompute.setValue(value, notifyListeners);
            fieldToRecompute.setValue(computedValue, false);
        } else {
            Boolean computedBoolean = Boolean.parseBoolean(computedValue);
            fieldToRecompute.setVisible(computedBoolean);
            FLLogger.d(TAG, "Visibility of " + fieldToRecompute.getFieldName() + ", visible:" + computedBoolean);
        }

        FLLogger.d(TAG, "Will recompute value of field " + fieldToRecompute.getFieldName());

        return computedValue;

    }

    public interface FieldComputationRequestListener {
        String onRecomputeRequested(FField fieldToRecompute, ChangeTriggerType changeTriggerType);
    }

}
