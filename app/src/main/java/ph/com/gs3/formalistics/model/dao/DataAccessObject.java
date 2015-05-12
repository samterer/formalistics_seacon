package ph.com.gs3.formalistics.model.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.utilities.Serializer;
import ph.com.gs3.formalistics.model.DatabaseHelperFactory;
import ph.com.gs3.formalistics.model.values.application.SearchCondition;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;

/**
 * Created by Ervinne on 4/7/2015.
 */
public abstract class DataAccessObject {

    protected Context context;

    // Database Fields
    protected SQLiteDatabase database;
    protected SQLiteOpenHelper databaseHelper;

    private boolean willUsePreOpenedDatabase;

    public DataAccessObject(Context context) {
        this.context = context;
        databaseHelper = DatabaseHelperFactory.getDatabaseHelper(context);
        willUsePreOpenedDatabase = false;
    }

    public DataAccessObject(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {
        this.context = context;
        this.database = preOpenedDatabaseWithTransaction;
        usePreOpenedDatabase(true);
    }

    public void usePreOpenedDatabase(boolean enable) {
        willUsePreOpenedDatabase = enable;
    }

    public void open() throws SQLException {
        if (!willUsePreOpenedDatabase) {
            database = databaseHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (!willUsePreOpenedDatabase) {
            databaseHelper.close();
        }
    }

    public String generateSelectClauseFromArray(String[] columns) {

        String selectClause = "";

        for (String column : columns) {
            selectClause += column + ", ";
        }

        selectClause = selectClause.substring(0, selectClause.length() - 2);

        return selectClause;

    }

    public String generateJoinClauseFromForms(List<Form> forms, String joinFromColumn, String joinToColumn) {

        String clause = "";

        for (Form form : forms) {
            String generatedFormTableName = form.getGeneratedFormTableName();
            clause += String.format(
                    "LEFT JOIN %s ON %s = %s.%s ",
                    generatedFormTableName,
                    joinFromColumn,
                    generatedFormTableName,
                    joinToColumn);
        }

        return clause;

    }

    public String generateWhereClauseFromConditions(List<SearchCondition> conditions) {
        return generateWhereClauseFromConditions(conditions, "AND");
    }

    public String generateWhereClauseFromConditions(List<SearchCondition> conditions, String logicalOperator) {

        List<String> conditionStringList = new ArrayList<>();
        String conditionString = "";

        for (SearchCondition searchCondition : conditions) {

            // Converted fields
            if ("TrackNo".equalsIgnoreCase(searchCondition.getFieldName())) {
                searchCondition.setFieldName("tracking_number");
            }

            conditionStringList.add(searchCondition.toString());
        }

        conditionString = "(" + Serializer.serializeList(conditionStringList, " " + logicalOperator + " ") + ")";

        return conditionString;

    }

    public List<SearchCondition> generateConditionsFromGenericStringFilter(String genericStringFilter, List<Form> forms) {

        List<SearchCondition> generatedConditions = new ArrayList<>();
        for (Form form : forms) {
            generatedConditions.addAll(
                    generateConditionsFromGenericStringFilterAndForm(genericStringFilter, form)
            );
        }

        generatedConditions.add(new SearchCondition("author.display_name", "LIKE", genericStringFilter));
        generatedConditions.add(new SearchCondition("d.tracking_number", "LIKE", genericStringFilter));
        generatedConditions.add(new SearchCondition("d.web_id", "LIKE", genericStringFilter));
        generatedConditions.add(new SearchCondition("d.status", "LIKE", genericStringFilter));

        return generatedConditions;

    }

    public List<SearchCondition> generateConditionsFromGenericStringFilterAndForm(String genericStringFilter, Form form) {

        List<FormFieldData> formFields = form.getActiveFields();
        List<SearchCondition> generatedConditions = new ArrayList<>();

        String generatedTableName = form.getGeneratedFormTableName();

        for (FormFieldData formField : formFields) {
            generatedConditions.add(
                    new SearchCondition(generatedTableName + "." + formField.getName(), "LIKE", genericStringFilter)
            );
        }

        return generatedConditions;

    }

    public static String escapeValue(Object rawValue) {

        if (rawValue == null) {
            return "null";
        } else if (isNumeric(rawValue)) {
            return rawValue.toString();
        } else {
            return "'" + rawValue.toString() + "'";
        }

    }

    public static boolean isNumeric(Object object) {
        return object.toString().matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static class DataAccessObjectException extends Exception {
        public DataAccessObjectException(String message) {
            super(message);
        }

        public DataAccessObjectException(Throwable t) {
            super(t);
        }

        public DataAccessObjectException(String message, Throwable t) {
            super(message, t);
        }
    }

}
