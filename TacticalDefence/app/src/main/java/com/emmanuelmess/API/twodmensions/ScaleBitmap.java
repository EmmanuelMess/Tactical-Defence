package com.emmanuelmess.API.twodmensions;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ScaleBitmap {

	public static Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {
		Matrix matrix = new Matrix();

		if (newWidth < 0 || newHeight < 0)
			throw new IllegalArgumentException((newHeight < 0? "new height":"new width") + "is < 0");
		int width = bm.getWidth(), height = bm.getHeight();
		float scaleWidth = newWidth/(float) width;
		float scaleHeight = newHeight/(float) height;
		// resize the bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// recreate the new Bitmap
		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	}
}
