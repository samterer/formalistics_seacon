package ph.com.gs3.formalistics.model.values.application;

public class SearchCondition {

    private String fieldName;
    private String fieldValue;
    private String comparator;


    public SearchCondition(String fieldName, String comparator, String fieldValue) {
        this.fieldName = fieldName;
        this.comparator = comparator;
        this.fieldValue = fieldValue;
    }

    @Override
    public String toString() {
        String escapedFieldValue;

        if (fieldValue == null) {
            if ("=".equals(comparator)) {
                comparator = "IS";
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                comparator = "NOT";
            }
            escapedFieldValue = "NULL";
        } else {
            escapedFieldValue = SearchCondition.isNumeric(fieldValue) ? fieldValue : "'" + fieldValue + "'";
        }

        return fieldName + " " + comparator + " " + escapedFieldValue;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    // ========================================================================
    // {{ Getters & Setters

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    // }}

}
