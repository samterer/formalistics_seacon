package ph.com.gs3.formalistics.global.utilities;

import java.util.ArrayList;
import java.util.List;

public class Serializer {

	public static final String serializeArray(String[] values) {

		if (values.length > 0) {
			StringBuilder sbSerializedValues = new StringBuilder();

			for (String value : values) {
				sbSerializedValues.append(value).append(",");
			}

			sbSerializedValues.deleteCharAt(sbSerializedValues.length() - 1);

			return sbSerializedValues.toString();
		} else {
			return "";
		}

	}

    public static final String serializeList(List values) {

        return serializeList(values, ",");

    }

	public static final String serializeList(List values, String divider) {
		if (values.size() > 0) {
			StringBuilder serializedValuesBuilder = new StringBuilder();

			for (Object value : values) {
				serializedValuesBuilder.append(value.toString()).append(divider);
			}

			String serializedValues = serializedValuesBuilder.substring(0,
			        serializedValuesBuilder.length() - divider.length());

			return serializedValues;
		} else {
			return "";
		}
	}

	public static final List<String> unserializeList(String serializedValues) {

		if (serializedValues == null || serializedValues.trim().isEmpty()) {
			return new ArrayList<String>();
		}

		String[] valuesArray = serializedValues.split(",");
		List<String> values = new ArrayList<>();

		for (String value : valuesArray) {
			values.add(value);
		}

		return values;

	}

}
