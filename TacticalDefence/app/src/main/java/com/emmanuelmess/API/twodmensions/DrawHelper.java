package com.emmanuelmess.API.twodmensions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import com.emmanuelmess.tacticaldefence.R;
import com.emmanuelmess.tacticaldefence.TacticalDefence;


/**
 * @author Emmanuel
 *         on 2015-02-05, at 12:33 AM, at 22:27.
 */
public class DrawHelper implements Parcelable {

	public static final int NO_ANGLE = 0;
	public static final int RECT_ANGLE = 90;
	public static final int BLUE = 0;
	public static final Parcelable.Creator<DrawHelper> CREATOR = new Parcelable.Creator<DrawHelper>() {
		@Override
		public DrawHelper createFromParcel(Parcel source) {
			return new DrawHelper(source);  //using parcelable constructor
		}

		@Override
		public DrawHelper[] newArray(int size) {
			return new DrawHelper[size];
		}
	};
	private Paint paint;
	private int angle = 0;

	public DrawHelper() {
		paint = new Paint();
	}

	//parcel part
	public DrawHelper(Parcel in) {
		angle = in.readInt();

	}

	public static void drawBitmap(Bitmap b, RectF r, Canvas canvas) {
		canvas.drawBitmap(b, r.centerX() - b.getWidth()/2f, r.centerY() - b.getHeight()/2f, null);
	}

	/**
	 * Repaces the color blue with red
	 *
	 * @param b Bitmap to be used
	 */
	public static Bitmap replaceColorBlueRed(Context context, Bitmap b) {
		Bitmap bClone = b.copy(b.getConfig(), true);
		int[] allPixels = new int[b.getHeight()*b.getWidth()];

		b.getPixels(allPixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());

		for (int i = 0; i < allPixels.length; i++)
			if ((allPixels[i] & 0xFF) > 50)//workaround because of bugs...
				allPixels[i] = context.getResources().getColor(R.color.ai_red);

		bClone.setPixels(allPixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());

		return bClone;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public void drawHealthBar(float x, float y, int length, int width, int percentageFilled, int colourD, int colour, Canvas canvas) {
		float pixelsFilled = (length)*percentageFilled/100;

		if (pixelsFilled < 0) pixelsFilled = 0;

		switch (angle) {
			case 0:
				paint.setColor(colourD);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawRect(x, y, x + length, y + width, paint);

				paint.setColor(colour);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawRect(x, y, x + pixelsFilled, y + width, paint);

				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawRect(x, y, x + length, y + width, paint);
				break;
			case 90:
				paint.setColor(colourD);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawRect(x, y, x + width, y + length, paint);

				paint.setColor(colour);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawRect(x, y, x + width, y + pixelsFilled, paint);

				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawRect(x, y, x + width, y + length, paint);
				break;
			default:
				throw new IllegalArgumentException("Illegal angle given, impossible to draw. Use the constants given in DrawHelper!");
		}
	}

	public void drawHealthBar(float x, float y, int length, int percentageFilled, Canvas canvas) {
		drawHealthBar(x, y, length, 5, percentageFilled, Color.RED, Color.GREEN, canvas);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(angle);
	}

}
class Utils {

	private static float HEIGHT_CONSTANT = TacticalDefence.DD.getHeight()/280;// TODO: 2015-11-02 check
	private static float WIDTH_CONSTANT = TacticalDefence.DD.getWidth()/480;

	public static RectF convert(RectF r) {
		return new RectF(r.left*WIDTH_CONSTANT, r.top*HEIGHT_CONSTANT, r.right*WIDTH_CONSTANT, r.bottom*HEIGHT_CONSTANT);
	}

}