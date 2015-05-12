package ph.com.gs3.formalistics.view.document.contents;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by Ervinne on 4/13/2015.
 */
public abstract class FView extends LinearLayout {

    protected FView(Context context) {
        super(context);
    }

    public void setVisible(boolean visible) {
        this.setVisibility(visible ? VISIBLE : GONE);
    }

}
