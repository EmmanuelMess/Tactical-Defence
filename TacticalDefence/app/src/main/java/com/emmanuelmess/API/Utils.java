package com.emmanuelmess.API;

import java.lang.reflect.Array;
/**
 * @author Emmanuel
 *         on 2015-07-25, at 17:04.
 */
public class Utils {

	public static <T> T[] merge(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public static double clamp(double min, double max, double val) {
		return Math.max(min, Math.min(max, val));
	}

}
