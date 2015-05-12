package ph.com.gs3.formalistics.global.utilities.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Ervinne on 4/23/2015.
 */
public class ViewUtils {

    public static void changeTextViewsColorInsideView(View view, int color) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    // Recursively change text view color
                    changeTextViewsColorInsideView(viewGroup.getChildAt(i), color);
                }
            }
        }
    }

}
