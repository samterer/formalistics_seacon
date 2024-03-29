package ph.com.gs3.formalistics.global.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Serializer {

	public static String serializeArray(String[] values) {

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

    public static String serializeList(List values) {

        return serializeList(values, ",");

    }

	public static String serializeList(List values, String divider) {
		if (values.size() > 0) {
			StringBuilder serializedValuesBuilder = new StringBuilder();

			for (Object value : values) {
				serializedValuesBuilder.append(value.toString()).append(divider);
			}

			return serializedValuesBuilder.substring(0,
			        serializedValuesBuilder.length() - divider.length());
		} else {
			return "";
		}
	}

	public static List<String> unserializeList(String serializedValues) {

		if (serializedValues == null || serializedValues.trim().isEmpty()) {
			return new ArrayList<>();
		}

		String[] valuesArray = serializedValues.split(",");
		List<String> values = new ArrayList<>();

		Collections.addAll(values, valuesArray);

		return values;

	}

}
