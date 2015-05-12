package ph.com.gs3.formalistics.view.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SignatureView extends View {

	public static final String TAG = SignatureView.class.getSimpleName();

	private static final int BACKGROUND_COLOR = Color.WHITE;
	private static final int TOUCH_TOLERANCE = 4;
	private static final int STROKE_WIDTH = 2;

	private Path signPath;
	private Paint paint;
	private Bitmap bitmap;
	private Canvas signCanvas;
	private float currentX, currentY;

	private boolean isDragged = false;

	// ============================================================================
	// {{ Constructors
	public SignatureView(Context context) {
		super(context);
		initialize();
	}

	public SignatureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public SignatureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	// }}

	private void initialize() {
		setFocusable(true);
		signPath = new Path();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(~0x00FFFFFF);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(STROKE_WIDTH);
	}

	// =============================================================================
	// {{ Functional Methods

	/**
	 * Set the color of the signature.
	 * 
	 * @param color
	 *            the hex representation of the desired color, most likely an instance of
	 *            Color.*
	 */
	public void setSignColor(int color) {
		paint.setColor(color);
	}

	/**
	 * Set the color of the signature. For simpler option just us setSigColor(int color).
	 * 
	 * @param a
	 *            alpha value
	 * @param r
	 *            red value
	 * @param g
	 *            green value
	 * @param b
	 *            blue value\
	 */
	public void setSignColor(int a, int r, int g, int b) {
		paint.setARGB(a, r, g, b);
	}

	/**
	 * Clear the signature from the view.
	 */
	public void clearSignature() {
		if (signCanvas != null) {
			signCanvas.drawColor(BACKGROUND_COLOR);
			signPath.reset();
			invalidate();
		}
	}

	/**
	 * Get the bitmap backing the view.
	 */
	public Bitmap getImage() {
		return this.bitmap;
	}

	// }}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		int bitW = bitmap != null ? bitmap.getWidth() : 0;
		int bitH = bitmap != null ? bitmap.getWidth() : 0;

		// If the width and height of the bitmap are bigger than the
		// new defined size, then keep the excess bitmap and return
		// (Part of the backing bitmap will be clipped off, but it
		// will still exist)
		if (bitW >= w && bitH >= h) {
			return;
		}
		if (bitW < w) {
			bitW = w;
		}
		if (bitH < h) {
			bitH = h;
		}

		// Create a new bitmap and canvas for the new size
		Bitmap newBitmap = Bitmap.createBitmap(bitW, bitH, Bitmap.Config.ARGB_8888);
		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		if (bitmap != null) { // already have a bitmap
			newCanvas.drawBitmap(bitmap, 0, 0, null); // redraw it onto the new bitmap
		} else { // no path yet
			newCanvas.drawColor(BACKGROUND_COLOR);
		}
		// Replace the old bitmap and canvas with the new one
		bitmap = newBitmap;
		signCanvas = newCanvas;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.drawPath(signPath, paint);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchDown(x, y);
				break;
			case MotionEvent.ACTION_MOVE:
				touchMove(x, y);
				break;
			case MotionEvent.ACTION_UP:
				touchUp();
				break;
		}
		invalidate();
		return true;
	}

	/**
	 * ---------------------------------------------------------- Private methods
	 * ---------------------------------------------------------
	 */
	private void touchDown(float x, float y) {
		signPath.reset();
		signPath.moveTo(x, y);
		currentX = x;
		currentY = y;
		isDragged = false;
	}

	private void touchMove(float x, float y) {
		float dx = Math.abs(x - currentX);
		float dy = Math.abs(y - currentY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			signPath.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2);
			currentX = x;
			currentY = y;
			isDragged = true;
		}
	}

	private void touchUp() {
		if (isDragged) {
			signPath.lineTo(currentX, currentY);
		} else {
			signPath.lineTo(currentX + 2, currentY + 2);
		}
		signCanvas.drawPath(signPath, paint);
		signPath.reset();
	}
}
