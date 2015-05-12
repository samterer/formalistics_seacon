package ph.com.gs3.formalistics.global.utilities;

public class TextHtmlParser {

	public static String stringToHTML(String rawString) {

		// SpannedString spannedString = new SpannedString(rawString);
		// return Html.toHtml(spannedString);

		String html = rawString;

		html = html.replaceAll("(\r\n|\n)", "<br>");

		return html;
	}

	public static String htmlToString(String rawHTML) {

		String parsedString = rawHTML;

		parsedString = parsedString.replace("<br></br>", "\n");
		parsedString = parsedString.replace("<br>", "\n");
		parsedString = parsedString.replace("<br/>", "\n");
		parsedString = parsedString.replace("<br />", "\n");

		// parsedString = parsedString.replace("\\", "\\\\");

		return parsedString;

	}

}
