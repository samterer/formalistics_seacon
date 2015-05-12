package ph.com.gs3.formalistics.global.utilities.view;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class FontUtil {

	public static final Map<String, Typeface> FONTS = new HashMap<>();

	public static Typeface getTypeface(Context context, String TypefaceName) {

		Typeface typface = FONTS.get(TypefaceName);

		if (typface == null) {
			typface = Typeface.createFromAsset(context.getAssets(), "fonts/"
					+ TypefaceName);
			FONTS.put(TypefaceName, typface);
		}

		return typface;
	}
	
	public static void setTypeface(TextView view, String typeface){
		
		view.setTypeface(getTypeface(view.getContext(), typeface));
		
	}

}
