package com.emmanuelmess.API.twodmensions;

import android.graphics.RectF;
import android.view.MotionEvent;
/**
 * @author Emmanuel
 *         on 2014-12-17, at 08:48 AM, at 22:27.
 */
public class Contain {

	static RectF rf = new RectF();

	public static boolean isContainedInRectF(RectF rect, MotionEvent event) {
		if (event == null) throw new NullPointerException("Event CANNOT be null!!!");
		if (rect == null) throw new NullPointerException("Rectangle CANNOT be null!!!");

		return rect.contains(event.getX(), event.getY());
	}

	public static boolean isContainedInRectF(float[] rectangle, MotionEvent event) {
		if (event == null) throw new NullPointerException("Event CANNOT be null!!!");
		if (rectangle == null) throw new NullPointerException("Rectangle CANNOT be null!!!");

		rf.setEmpty();
		rf.set(rectangle[0], rectangle[1], rectangle[2], rectangle[3]);
		return rf.contains(event.getX(), event.getY());
	}

	public static boolean isContainedInRectF(float left, float top, float right, float down, MotionEvent event) {
		if (event == null) throw new NullPointerException("Event CANNOT be null!!!");

		rf.setEmpty();
		rf.set(left, top, right, down);
		return rf.contains(event.getX(), event.getY());
	}

}
