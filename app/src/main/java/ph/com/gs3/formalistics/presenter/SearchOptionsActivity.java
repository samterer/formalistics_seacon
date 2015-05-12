package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ph.com.gs3.formalistics.R;

public class SearchOptionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DON'T CALL `setContentView`,
        // we are replacing that line with this code:
        ViewGroup wrapperView = setContentViewWithWrapper(R.layout.activity_search_options);

        // Now, because the wrapper view contains the entire screen (including the notification bar
        // which is above the ActionBar) I think you'll find it useful to know the exact Y where the
        // action bar is located.
        // You can use something like that:
        ViewGroup actionBar = (ViewGroup) ((LinearLayout) wrapperView.getChildAt(0)).getChildAt(0);
        int topOffset = actionBar.getTop();

        // Now, if you'll want to add a view:
        //  1. Create new view
        //  2. Set padding top - use "topOffset"
        //  3. Add the view to "wrapperView"
        //  4. The view should be set at front. if not - try calling to "bringToFront()"
    }

    private ViewGroup setContentViewWithWrapper(int resContent) {
        ViewGroup decorView = (ViewGroup) this.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);

        // Removing decorChild, we'll add it back soon
        decorView.removeAllViews();

        ViewGroup wrapperView = new FrameLayout(this);

        // You should set some ID, if you'll want to reference this wrapper in that manner later
        //
        // The ID, such as "R.id.ACTIVITY_LAYOUT_WRAPPER" can be set at a resource file, such as:
        //  <resources xmlns:android="http://schemas.android.com/apk/res/android">
        //      <item type="id" name="ACTIVITY_LAYOUT_WRAPPER"/>
        //  </resources>
        //
//        wrapperView.setId(R.id.ACTIVITY_LAYOUT_WRAPPER);

        // Now we are rebuilding the DecorView, but this time we
        // have our wrapper view to stand between the real content and the decor
        decorView.addView(wrapperView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        wrapperView.addView(decorChild, decorChild.getLayoutParams());
        LayoutInflater.from(this).inflate(R.layout.activity_search_options,
                (ViewGroup)((LinearLayout)wrapperView.getChildAt(0)).getChildAt(1), true);

        return wrapperView;
    }

}
