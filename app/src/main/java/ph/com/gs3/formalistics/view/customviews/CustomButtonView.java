package ph.com.gs3.formalistics.view.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class CustomButtonView extends Button {
	
	public CustomButtonView(Context context){
		super(context);
	}
	
	public CustomButtonView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public CustomButtonView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	public void setPressed(boolean pressed){
		if(pressed && getParent() instanceof View &&((View) getParent()).isPressed()){
			return;
		}
		super.setPressed(pressed);
	}

}
