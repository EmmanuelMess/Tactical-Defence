package com.emmanuelmess.API;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * @author Emmanuel 9/1/15, at 1:46
 */
@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class DeviceDimensions {

	private static final int CURRENT_API_VERSION = android.os.Build.VERSION.SDK_INT;
	private static Display display;
	private static Point size;

	/**
	 * @param context The class' context
	 */
	public DeviceDimensions(Context context) {
		if (context == null) throw new NullPointerException("Context CANNOT be null");
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		if (CURRENT_API_VERSION >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			size = new Point();
			if (!hasMenuBar(context)) display.getRealSize(size);
			else display.getSize(size);
		}
	}

	/**
	 * Use in onDraw() is not recommended due to Object initialization!
	 *
	 * @param context The class' context
	 * @return Device width
	 */
	public static int getWidth(Context context) {
		if (context == null) throw new NullPointerException("Context CANNOT be null");

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (CURRENT_API_VERSION < Build.VERSION_CODES.JELLY_BEAN_MR1)
			return display.getWidth();
		else {
			size = new Point();
			display.getRealSize(size);
			return size.x;
		}
	}

	/**
	 * Use in onDraw() is not recomended due to Object initialization!
	 *
	 * @param context the class' context
	 * @return Device height
	 */
	public static int getHeight(Context context) {
		if (context == null) throw new NullPointerException("Context CANNOT be null");

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (CURRENT_API_VERSION < Build.VERSION_CODES.JELLY_BEAN_MR1)
			return display.getWidth();
		else {
			size = new Point();
			display.getRealSize(size);
			return size.y;
		}
	}

	private static boolean hasMenuBar(Context context) {
		boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

		return (!hasMenuKey && !hasBackKey);
	}

	/**
	 * @return Device width
	 */
	public int getWidth() {
		if (CURRENT_API_VERSION < Build.VERSION_CODES.JELLY_BEAN_MR1)
			return display.getWidth();
		else return size.x;
	}

	/**
	 * @return Device height
	 */
	public int getHeight() {
		if (CURRENT_API_VERSION < Build.VERSION_CODES.JELLY_BEAN_MR1)
			return display.getHeight();
		else return size.y;
	}

}
